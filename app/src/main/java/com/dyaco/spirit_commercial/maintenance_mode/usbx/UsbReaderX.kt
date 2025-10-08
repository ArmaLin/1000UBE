package com.dyaco.spirit_commercial.maintenance_mode.usbx

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Build
import android.util.ArrayMap
import android.util.Log
import android.webkit.MimeTypeMap
import com.github.mjdev.libaums.UsbMassStorageDevice
import com.github.mjdev.libaums.driver.scsi.commands.sense.UnitAttention
import com.github.mjdev.libaums.fs.FileSystem
import com.github.mjdev.libaums.fs.UsbFile
import com.github.mjdev.libaums.fs.UsbFileStreamFactory
import com.github.mjdev.libaums.partition.Partition
import kotlinx.coroutines.*
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.IOException
import java.nio.file.Files

class UsbReaderX(private val context: Context) {

    companion object {
        private const val TAG = "USB_UPDATE"
        private const val ACTION_USB_PERMISSION = "corestar.usb.permission"
        private const val ACTION_USB_DEVICE_ATTACHED = "android.hardware.usb.action.USB_DEVICE_ATTACHED"
        private const val ACTION_USB_DEVICE_DETACHED = "android.hardware.usb.action.USB_DEVICE_DETACHED"

        private const val FS_TAG = "UsbFsDetector"
        private const val MOUNT_POINT = "/mnt/usb_detect"
        private const val EXFAT_MOUNT_TOOL = "/data/local/tmp/mount.exfat-fuse"
        private const val MAX_FIND_ATTEMPTS = 3
        private const val FIND_RETRY_DELAY_MS = 200L
    }

    private val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
    private val filter = IntentFilter().apply {
        addAction(ACTION_USB_PERMISSION)
        addAction(ACTION_USB_DEVICE_ATTACHED)
        addAction(ACTION_USB_DEVICE_DETACHED)
    }
    private var broadcastReceiver: BroadcastReceiver? = createBroadcastReceiver()
    private val usbDevicesMap = ArrayMap<String, CSUsbDevice>()
    private var listener: UsbReaderListener? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val detectionScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var isFinding = false
    private var isCancelled = false
    private var fileSaveDir: File = context.cacheDir

    private val attachedDevices = mutableSetOf<String>()

    private var lastFileName: String? = null
    private var lastFileType: FileType? = null
    private var lastFileKind: FileKind? = null

    private var autoFileName: String? = null
    private var autoFileType: FileType? = null
    private var autoFileKind: FileKind? = null

    private var lastVideoValidationErrors: IntArray = intArrayOf(0, 0, 0, 0)

    private enum class FsType { FAT32, EXFAT, UNKNOWN, NONE }

    fun setListener(l: UsbReaderListener?) {
        listener = l
        isCancelled = false
        isFinding = false
        attachedDevices.clear()
        if (listener != null && broadcastReceiver != null) {
            context.registerReceiver(broadcastReceiver, filter)
            Log.d(TAG, "已註冊廣播接收器")
            scanExistingDevices()
        } else {
            Log.d(TAG, "註冊廣播接收器失敗")
        }
    }

    private fun scanExistingDevices() {
        getUsbDevice().forEach { csUsb ->
            Log.d(TAG, "scanExistingDevices: 模擬 onDeviceAttached → ${csUsb.name}")
            scope.launch { listener?.onDeviceAttached(csUsb.name) }
        }
    }

    fun autoFindFile(fileName: String, type: FileType, kind: FileKind) {
        Log.d(TAG, "啟動自動尋找流程 → 檔名：$fileName，類型：$type，種類：$kind")
        lastFileName = fileName
        lastFileType = type
        lastFileKind = kind

        autoFileName = fileName
        autoFileType = type
        autoFileKind = kind

        getUsbDevice().firstOrNull()?.let { requestPermission(it) }
            ?: run {
                Log.d(TAG, "無 USB 裝置")
                scope.launch { listener?.onError(UsbError.NO_USB_DEVICE) }
            }
    }

    fun findFile(
        device: CSUsbDevice?, fileName: String?, type: FileType?, kind: FileKind?
    ) {
        if (device == null || fileName == null || type == null || kind == null) {
            Log.d(TAG, "參數有誤")
            scope.launch { listener?.onError(UsbError.PARAMETER_ERROR) }
            return
        }
        if (isFinding) {
            Log.d(TAG, "已有尋找作業進行中")
            return
        }
        isFinding = true
        isCancelled = false

        scope.launch {
            val (status, copyResult) = findFileInBackground(device, fileName, type, kind)
            isFinding = false

            val data: String? =
                if (type == FileType.APK || type == FileType.MP4 ||
                    type == FileType.IMAGE || type == FileType.JSON
                ) {
                    copyResult as? String
                } else {
                    null
                }

            val raw: ByteArray? =
                if (type == FileType.BIN) {
                    copyResult as? ByteArray
                } else {
                    null
                }

            Log.d(TAG, "尋找結果 → 狀態：$status，data：$data，raw 長度：${raw?.size ?: 0}")
            listener?.onFindFile(fileName, status, type, data, raw, kind)

            if (status == FileStatus.FILE_FOUND) {
                lastFileName = null
                lastFileType = null
                lastFileKind = null
            }
        }
    }

//    2025-04-29 10:58:29.658 USB_UPDATE              接收到廣播：android.hardware.usb.action.USB_DEVICE_ATTACHED
//    2025-04-29 10:58:29.658 USB_UPDATE              裝置插入：/dev/bus/usb/002/032，開始檔案系統偵測
//    2025-04-29 10:58:31.995 USB_UPDATE              當前 USB[/dev/bus/usb/002/032] 檔案系統：NONE
//    2025-04-29 10:58:31.995 USB_UPDATE              檔案系統偵測失敗：NONE
    // TODO:  可能有 IoException的問題
    private suspend fun findFileInBackground(
        csUsb: CSUsbDevice, fileName: String, type: FileType, kind: FileKind
    ): Pair<FileStatus, Any?> = withContext(Dispatchers.IO) {
        Log.d(TAG, "背景尋找檔案：$fileName")
        var attempts = 0
        while (true) {
            try {
                csUsb.device.init()
                break
            } catch (e: UnitAttention) {
                attempts++
                if (attempts >= 3) {
                    Log.e(TAG, "初始化重試失敗：$attempts 次", e)
                    withContext(Dispatchers.Main) { listener?.onError(UsbError.BACKGROUND_SEARCH_ERROR) }
                    return@withContext Pair(FileStatus.FILE_NOT_FOUND, null)
                }
                Log.w(TAG, "UnitAttention，重試 init 第 $attempts 次")
                delay(200)
            }
        }

        try {
            val partition = csUsb.device.partitions.firstOrNull()
                ?: return@withContext Pair(FileStatus.FILE_NOT_FOUND, null)
            val fs = partition.fileSystem
            val root = fs.rootDirectory
            val usbFile = root.listFiles().firstOrNull {
                !it.isDirectory && it.name.equals(fileName, ignoreCase = true)
            }
            if (usbFile == null) {
                Log.d(TAG, "找不到檔案：$fileName")
                return@withContext Pair(FileStatus.FILE_NOT_FOUND, null)
            }

            Log.d(TAG, "找到檔案：${usbFile.name}，開始複製")
            if (type == FileType.MP4 && !checkVideo(usbFile)) {
                Log.e(TAG, "影片檢查失敗 → ${usbFile.name}")
                withContext(Dispatchers.Main) { listener?.onError(UsbError.VIDEO_VALIDATION_FAILED) }
                return@withContext Pair(FileStatus.FILE_NOT_FOUND, null)
            }

            val target = File(fileSaveDir, usbFile.name)
            val result = copyAndReadFile(fs, usbFile, target, fs.chunkSize, type)
            val status = if (result != null) FileStatus.FILE_FOUND else FileStatus.WRITE_FAIL
            return@withContext Pair(status, result)
        } catch (e: Exception) {
            if (!isCancelled) {
                Log.e(TAG, "背景尋找檔案錯誤：${e.localizedMessage}", e)
                withContext(Dispatchers.Main) { listener?.onError(UsbError.BACKGROUND_SEARCH_ERROR) }
            }
            return@withContext Pair(FileStatus.FILE_NOT_FOUND, null)
        } finally {
            try { csUsb.device.close() } catch (_: Exception) {}
            Log.d(TAG, "搜尋結束，已關閉裝置：${csUsb.name}")
        }
    }

    private fun copyAndReadFile(
        fileSystem: FileSystem, fromFile: UsbFile, toFile: File,
        chunkSize: Int, type: FileType
    ): Any? {
        Log.d(TAG, "開始複製檔案：${fromFile.name}")
        val totalSize = fromFile.length
        val content = ByteArray(totalSize.toInt())
        val buffer = ByteArray(chunkSize.coerceAtMost(16 * 1024))
        var current = 0

        try {
            BufferedInputStream(UsbFileStreamFactory.createBufferedInputStream(fromFile, fileSystem))
                .use { ins ->
                    BufferedOutputStream(Files.newOutputStream(toFile.toPath()), buffer.size)
                        .use { outs ->
                            while (true) {
                                if (isCancelled) {
                                    Log.d(TAG, "檔案下載停止（使用者拔除 USB）")
                                    return null
                                }
                                val read = ins.read(buffer)
                                if (read <= 0) break
                                outs.write(buffer, 0, read)
                                System.arraycopy(buffer, 0, content, current, read)
                                current += read
                                scope.launch { listener?.onProgress(current.toLong(), totalSize) }
                            }
                        }
                }

            Log.d(TAG, "複製完成 → 長度：${content.size}")
            return when (type) {
                FileType.JSON -> String(content)
                FileType.APK, FileType.MP4, FileType.IMAGE -> toFile.absolutePath
                FileType.BIN -> content
            }
        } catch (e: Exception) {
            if (isCancelled) {
                Log.d(TAG, "複製中止（使用者拔除 USB）")
            } else {
                Log.e(TAG, "複製檔案錯誤：${e.localizedMessage}", e)
                scope.launch { listener?.onError(UsbError.COPY_FILE_ERROR) }
            }
            return null
        }
    }

    private fun checkVideo(
        usbFile: UsbFile,
        maxSizeMb: Float = 120f,
        expectedMime: String = "video/mp4"
    ): Boolean {
        val mime = getMimeType(usbFile.name)
        val fileSizeMb = byte2Mb(usbFile.length)
        Log.e(TAG, "檢查影片 → ${usbFile.name}，大小：${fileSizeMb}MB，Mime：$mime")
        val errors = intArrayOf(
            if (mime == expectedMime) 1 else 2,
            0,
            if (fileSizeMb <= maxSizeMb) 1 else 2,
            1
        )
        lastVideoValidationErrors = errors
        return !errors.contains(2)
    }

    fun getVideoValidationErrors(): IntArray = lastVideoValidationErrors

    fun requestPermission(csUsb: CSUsbDevice?) {
        if (csUsb == null) {
            scope.launch { listener?.onError(UsbError.PARAMETER_ERROR) }
            return
        }
        Log.d(TAG, "申請權限 → 裝置：${csUsb.name}")
        if (usbManager.hasPermission(csUsb.device.usbDevice)) {
            csUsb.isPermissionGranted = true
            if (autoFileName != null && autoFileType != null && autoFileKind != null) {
                findFile(csUsb, autoFileName, autoFileType, autoFileKind)
            }
        } else if (usbDevicesMap.containsKey(csUsb.name)) {
            val pi = PendingIntent.getBroadcast(
                context, 0,
                Intent(ACTION_USB_PERMISSION),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            usbManager.requestPermission(csUsb.device.usbDevice, pi)
        } else {
            scope.launch { listener?.onError(UsbError.PERMISSION_FAILED) }
        }
    }

    fun getUsbDevice(): List<CSUsbDevice> {
        usbDevicesMap.clear()
        UsbMassStorageDevice.getMassStorageDevices(context).forEach { dev ->
            Log.d(TAG, "加入裝置：${dev.usbDevice.deviceName}")
            usbDevicesMap[dev.usbDevice.deviceName] = CSUsbDevice(dev)
        }
        Log.d(TAG, "找到 ${usbDevicesMap.size} 個 USB 裝置")
        return usbDevicesMap.values.toList()
    }

    private fun createBroadcastReceiver(): BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(ctx: Context?, intent: Intent?) {
            intent ?: return
            Log.d(TAG, "接收到廣播：${intent.action}")
            when (intent.action) {
                ACTION_USB_PERMISSION -> {
                    @Suppress("DEPRECATION")
                    val device: UsbDevice? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                        intent.getParcelableExtra("device", UsbDevice::class.java)
                    else
                        intent.getParcelableExtra("device")
                    val granted = intent.getBooleanExtra("permission", false)
                    device?.deviceName?.let { name ->
                        usbDevicesMap[name]?.let { cs ->
                            cs.isPermissionGranted = granted
                            Log.d(TAG, "權限回傳 → 裝置：$name，授權：$granted")
                            if (granted && autoFileName != null) {
                                findFile(cs, autoFileName, autoFileType, autoFileKind)
                            }
                        }
                    }
                }

                ACTION_USB_DEVICE_ATTACHED -> {
                    @Suppress("DEPRECATION")
                    val dev: UsbDevice? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                        intent.getParcelableExtra(UsbManager.EXTRA_DEVICE, UsbDevice::class.java)
                    else
                        intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
                    val name = dev?.deviceName
                    if (name != null && attachedDevices.add(name)) {
                        Log.e(TAG, "裝置插入：$name，開始檔案系統偵測")
                        detectUsbFsAsync({ fsType ->
                            Log.e(TAG, "當前 USB[$name] 檔案系統：$fsType")
                            if (fsType == FsType.FAT32 || fsType == FsType.EXFAT) {
                                if (lastFileName != null && !isFinding) {
                                    autoFileName = lastFileName
                                    autoFileType = lastFileType
                                    autoFileKind = lastFileKind
                                }
                                getUsbDevice().firstOrNull()?.let { requestPermission(it) }
                                scope.launch { listener?.onDeviceAttached(name) }
                            } else {
                                Log.e(TAG, "檔案系統偵測失敗：$fsType")
                            }
                        })
                    } else {
                        Log.d(TAG, "裝置插入，但已處理過 → $name")
                    }
                }

                ACTION_USB_DEVICE_DETACHED -> {
                    @Suppress("DEPRECATION")
                    val dev: UsbDevice? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                        intent.getParcelableExtra(UsbManager.EXTRA_DEVICE, UsbDevice::class.java)
                    else
                        intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
                    val name = dev?.deviceName
                    if (name != null) {
                        isCancelled = true
                        isFinding = false
                        try {
                            usbDevicesMap[name]?.device?.close()
                        } catch (e: UninitializedPropertyAccessException) {
                            Log.w(TAG, "close before init(): ${e.message}")
                        }
                        Log.d(TAG, "檔案下載停止（USB 已拔除）")
                        attachedDevices.remove(name)
                        scope.launch { listener?.onDeviceDetached(name) }
                    }
                }
            }
        }
    }

    private fun detectUsbFsAsync(callback: (FsType) -> Unit, delayMs: Long = 1500L) {
        detectionScope.launch {
            Log.d(FS_TAG, "delay ${delayMs}ms 後開始偵測 USB 檔案系統")
            delay(delayMs)
            val type = detectUsbFsSync()
            withContext(Dispatchers.Main) {
                Log.d(FS_TAG, "偵測結束，結果：$type")
                callback(type)
            }
        }
    }

    private fun detectUsbFsSync(): FsType {
        Log.d(FS_TAG, "開始同步偵測 USB 檔案系統")
        val devicePath = findUsbBlockDevice() ?: run {
            Log.d(FS_TAG, "找不到 USB 裝置")
            return FsType.NONE
        }
        Log.d(FS_TAG, "找到裝置: $devicePath")

        val fs = probeFsByBlkid(devicePath)
        Log.d(FS_TAG, "blkid 回傳: `$fs`")
        if (fs == "vfat" || fs == "fat32") {
            Log.d(FS_TAG, "判斷為 FAT32")
            return FsType.FAT32
        } else if (fs == "exfat") {
            Log.d(FS_TAG, "判斷為 exFAT")
            return FsType.EXFAT
        }

        cleanupMountPoint()
        if (mountFs(devicePath, useExfatTool = false)) {
            Log.d(FS_TAG, "掛載 FAT32 成功")
            umountCleanup()
            return FsType.FAT32
        } else {
            Log.d(FS_TAG, "掛載 FAT32 失敗")
        }

        cleanupMountPoint()
        if (mountFs(devicePath, useExfatTool = true)) {
            Log.d(FS_TAG, "掛載 exFAT 成功")
            umountCleanup()
            return FsType.EXFAT
        } else {
            Log.d(FS_TAG, "掛載 exFAT 失敗")
        }

        umountCleanup()
        Log.d(FS_TAG, "所有方式都失敗，回傳 UNKNOWN")
        return FsType.UNKNOWN
    }

    private fun findUsbBlockDevice(): String? {
        for (i in 1..MAX_FIND_ATTEMPTS) {
            try {
                Log.d(FS_TAG, "第${i}次嘗試 find /dev/block")
                val lines = Runtime.getRuntime()
                    .exec(arrayOf("su","-c","find /dev/block -type b"))
                    .inputStream.bufferedReader().readLines()
                val dev = lines.firstOrNull { it.matches(Regex("^/dev/block/sd[a-z][0-9]*$")) }
                Log.d(FS_TAG, "find 結果: $dev")
                if (dev != null) return dev
            } catch (e: IOException) {
                Log.e(FS_TAG, "第${i}次嘗試失敗", e)
            }
            Thread.sleep(FIND_RETRY_DELAY_MS)
        }
        Log.e(FS_TAG, "超過 $MAX_FIND_ATTEMPTS 次仍找不到裝置")
        return null
    }

    private fun probeFsByBlkid(device: String): String {
        Log.d(FS_TAG, "執行 blkid -o value -s TYPE $device")
        return try {
            val out = Runtime.getRuntime()
                .exec(arrayOf("su","-c","blkid -o value -s TYPE $device"))
                .inputStream.bufferedReader().readText().trim()
            Log.d(FS_TAG, "blkid stdout: `$out`")
            out
        } catch (e: Exception) {
            Log.e(FS_TAG, "blkid 探測失敗", e)
            ""
        }
    }

    private fun cleanupMountPoint() {
        Log.d(FS_TAG, "cleanupMountPoint")
        execCmd("su","-c","umount $MOUNT_POINT")
        Thread.sleep(100)
        execCmd("su","-c","rmdir $MOUNT_POINT")
        execCmd("su","-c","mkdir -p $MOUNT_POINT")
    }

    private fun mountFs(device: String, useExfatTool: Boolean): Boolean {
        unmountDevice(device)
        val cmd = if (!useExfatTool) {
            "mount -t vfat -o ro $device $MOUNT_POINT"
        } else {
            "$EXFAT_MOUNT_TOOL -o ro,nonempty $device $MOUNT_POINT"
        }
        Log.d(FS_TAG, "執行 $cmd")
        val code = execCmd("su","-c",cmd)
        Log.d(FS_TAG, "掛載 exit code=$code")
        return code == 0
    }

    private fun unmountDevice(device: String) {
        try {
            Log.d(FS_TAG, "解除原掛載")
            Runtime.getRuntime().exec(arrayOf("su","-c","mount"))
                .inputStream.bufferedReader().readLines()
                .filter { it.startsWith("$device on ") }
                .forEach {
                    val m = it.split(" ")[2]
                    Log.d(FS_TAG, "umount $m")
                    execCmd("su","-c","umount $m")
                    Thread.sleep(100)
                }
        } catch (e: Exception) {
            Log.e(FS_TAG, "解除掛載失敗", e)
        }
    }

    private fun umountCleanup() {
        Log.d(FS_TAG, "umountCleanup")
        execCmd("su","-c","umount $MOUNT_POINT")
        Thread.sleep(100)
    }

    private fun execCmd(vararg cmd: String): Int {
        val full = cmd.joinToString(" ")
        return try {
            Log.d(FS_TAG, "執行: $full")
            val p = Runtime.getRuntime().exec(cmd)
            p.inputStream.bufferedReader().readText().also { if (it.isNotBlank()) Log.d(FS_TAG, it) }
            p.errorStream.bufferedReader().readText().also { if (it.isNotBlank()) Log.e(FS_TAG, it) }
            val code = p.waitFor()
            Log.d(FS_TAG, "exit code=$code")
            code
        } catch (e: Exception) {
            Log.e(FS_TAG, "execCmd 失敗: $full", e)
            -1
        }
    }

    fun closeUsb() {
        try {
            usbDevicesMap.values.forEach {
                try {
                    it.device.close()
                } catch (e: UninitializedPropertyAccessException) {
                    Log.w(TAG, "close before init(): ${e.message}")
                }
            }
            usbDevicesMap.clear()
            listener = null
            broadcastReceiver?.let { context.unregisterReceiver(it) }
            broadcastReceiver = null
            scope.cancel()
            Log.d(TAG, "所有 USB 資源已釋放")
        } catch (e: Exception) {
            Log.e(TAG, "關閉 USB 錯誤：${e.localizedMessage}", e)
        }
    }

    data class CSUsbDevice(val device: UsbMassStorageDevice) {
        var isPermissionGranted = false
        val name: String = device.usbDevice.deviceName
    }

    interface UsbReaderListener {
        fun onFindFile(
            file: String,
            status: FileStatus,
            type: FileType,
            data: String?,
            raw: ByteArray?,
            kind: FileKind
        )
        fun onDeviceAttached(name: String)
        fun onDeviceDetached(name: String)
        fun onProgress(current: Long, total: Long)
        fun onError(error: UsbError)
    }

    enum class FileStatus { FILE_NOT_FOUND, FILE_FOUND, WRITE_FAIL }
    enum class FileType { MP4, IMAGE, JSON, APK, BIN }
    enum class FileKind { SUB_MCU, LWR, NORMAL }
    enum class UsbError {
        NO_USB_DEVICE,
        PARAMETER_ERROR,
        DEVICE_NO_PARTITION,
        PERMISSION_FAILED,
        COPY_FILE_ERROR,
        BACKGROUND_SEARCH_ERROR,
        VIDEO_VALIDATION_FAILED,
        UNKNOWN_ERROR
    }

    private fun getMimeType(url: String): String? {
        val ext = MimeTypeMap.getFileExtensionFromUrl(url)
        return ext.takeIf { it.isNotEmpty() }
            ?.let { MimeTypeMap.getSingleton().getMimeTypeFromExtension(it) }
    }

    private fun byte2Mb(size: Long): Float = size / (1024f * 1024f)
}
