package com.dyaco.spirit_commercial.maintenance_mode

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

/**
 * 只支援 FAT32
 * UsbReaderKt 用於 USB 裝置的讀取、複製與封包處理
 */
class UsbReaderKt(private val context: Context) {

    companion object {
        private const val TAG = "USB_UPDATE"
        private const val ACTION_USB_PERMISSION = "corestar.usb.permission"
        private const val ACTION_USB_DEVICE_ATTACHED = "android.hardware.usb.action.USB_DEVICE_ATTACHED"
        private const val ACTION_USB_DEVICE_DETACHED = "android.hardware.usb.action.USB_DEVICE_DETACHED"
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

    // 正在尋找/複製檔案中
    private var isFinding = false
    // 使用者拔除時要取消複製
    private var isCancelled = false

    private var fileSaveDir: File = context.cacheDir
    private val attachedDevices = mutableSetOf<String>()

    // 自動流程參數
    private var autoFileName: String? = null
    private var autoFileType: FileType? = null
    private var autoFileKind: FileKind? = null

    // 暫存影片驗證錯誤狀態
    private var lastVideoValidationErrors: IntArray = intArrayOf(0, 0, 0, 0)

    /**
     * 註冊 listener 並啟動動態廣播，註冊完成後掃描已插裝置
     */
    fun setListener(l: UsbReaderListener?) {
        listener = l
        isCancelled = false
        attachedDevices.clear()
        if (listener != null && broadcastReceiver != null) {
            context.registerReceiver(broadcastReceiver, filter)
            Log.d(TAG, "已註冊廣播接收器")
            scanExistingDevices()
        } else {
            Log.d(TAG, "註冊廣播接收器失敗")
        }
    }

    /**
     * 掃描已插上的 USB 裝置，並對尚未觸發過的裝置呼 onDeviceAttached
     */
    private fun scanExistingDevices() {
        getUsbDevice().forEach { csUsb ->
            val name = csUsb.name
            if (attachedDevices.add(name)) {
                Log.d(TAG, "scanExistingDevices: 模擬 onDeviceAttached → $name")
                scope.launch { listener?.onDeviceAttached(name) }
            }
        }
    }

    /**
     * 自動流程：取得裝置、申請權限、搜尋檔案
     */
    fun autoFindFile(fileName: String, type: FileType, kind: FileKind) {
        Log.d(TAG, "啟動自動尋找流程 → 檔名：$fileName，類型：$type，種類：$kind")
        autoFileName = fileName
        autoFileType = type
        autoFileKind = kind
        getUsbDevice().firstOrNull()?.let { requestPermission(it) }
            ?: run {
                Log.d(TAG, "無 USB 裝置")
                scope.launch { listener?.onError(UsbError.NO_USB_DEVICE) }
            }
    }

    /**
     * 傳統流程：由外部呼叫搜尋檔案
     */
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
            val data = if (type in setOf(FileType.APK, FileType.MP4, FileType.IMAGE, FileType.JSON))
                copyResult as? String else null
            val raw = if (type == FileType.BIN) copyResult as? ByteArray else null
            Log.d(TAG, "尋找結果 → 狀態：$status，data：$data，raw 長度：${raw?.size ?: 0}")
            listener?.onFindFile(fileName, status, type, data, raw, kind)
        }
    }

    private suspend fun findFileInBackground(
        device: CSUsbDevice, fileName: String, type: FileType, kind: FileKind
    ): Pair<FileStatus, Any?> = withContext(Dispatchers.IO) {
        Log.d(TAG, "背景尋找檔案：$fileName")
        var fileStatus = FileStatus.FILE_NOT_FOUND
        var copyResult: Any? = null

        var attempts = 0
        var initialized = false

        // 加上 IOException 處理
        while (attempts < 3) {
            try {
                device.device.init()
                initialized = true
                break
            } catch (e: UnitAttention) {
                attempts++
                Log.w(TAG, "UnitAttention，重試 init 第 $attempts 次")
                delay(200)
            } catch (e: IOException) {
                Log.e(TAG, "初始化時 IOException：${e.localizedMessage}", e)
                withContext(Dispatchers.Main) { listener?.onError(UsbError.BACKGROUND_SEARCH_ERROR) }
                return@withContext Pair(FileStatus.FILE_NOT_FOUND, null)
            }
        }

        if (!initialized) {
            Log.e(TAG, "初始化 UnitAttention 重試失敗")
            withContext(Dispatchers.Main) { listener?.onError(UsbError.BACKGROUND_SEARCH_ERROR) }
            return@withContext Pair(FileStatus.FILE_NOT_FOUND, null)
        }

        try {
            val partition = device.device.partitions.firstOrNull()
                ?: return@withContext Pair(FileStatus.FILE_NOT_FOUND, null)
            val fs = partition.fileSystem
            val root = fs.rootDirectory
            val usbFile = root.listFiles().firstOrNull {
                !it.isDirectory && it.name.equals(fileName, ignoreCase = true)
            }

            if (usbFile == null) {
                Log.d(TAG, "找不到檔案：$fileName")
            } else {
                Log.d(TAG, "找到檔案：${usbFile.name}，開始複製")
                fileStatus = FileStatus.FILE_FOUND

                if (type == FileType.MP4 && !checkVideo(usbFile)) {
                    Log.e(TAG, "影片檢查失敗 → ${usbFile.name}")
                    withContext(Dispatchers.Main) { listener?.onError(UsbError.VIDEO_VALIDATION_FAILED) }
                    return@withContext Pair(FileStatus.FILE_NOT_FOUND, null)
                }

                val target = File(fileSaveDir, usbFile.name)
                copyResult = copyAndReadFile(fs, usbFile, target, fs.chunkSize, type)

                if (copyResult == null) {
                    fileStatus = FileStatus.WRITE_FAIL
                }
            }
        } catch (e: Exception) {
            if (!isCancelled) {
                Log.e(TAG, "背景尋找檔案錯誤：${e.localizedMessage}", e)
                withContext(Dispatchers.Main) { listener?.onError(UsbError.BACKGROUND_SEARCH_ERROR) }
            }
        } finally {
            try {
                device.device.close()
            } catch (_: Exception) {}
            Log.d(TAG, "搜尋結束，已關閉裝置：${device.name}")
        }

        Log.d(TAG, "背景尋找結束 → 狀態：$fileStatus")
        Pair(fileStatus, copyResult)
    }


    private fun copyAndReadFile(
        fileSystem: FileSystem, fromFile: UsbFile, toFile: File,
        chunkSize: Int, type: FileType
    ): Any? {
        Log.d(TAG, "開始複製檔案：${fromFile.name}")
        val totalSize = fromFile.length
        val content = ByteArray(totalSize.toInt())
        val buffer = ByteArray(chunkSize.coerceAtMost(16 * 1024)) // 最多16KB
        var current = 0

        try {
            BufferedInputStream(
                UsbFileStreamFactory.createBufferedInputStream(fromFile, fileSystem)
            ).use { inputStream ->
                BufferedOutputStream(Files.newOutputStream(toFile.toPath()), buffer.size).use { outputStream ->
                    while (!isCancelled) {
                        val bytesRead = inputStream.read(buffer)
                        if (bytesRead <= 0) break
                        outputStream.write(buffer, 0, bytesRead)
                        System.arraycopy(buffer, 0, content, current, bytesRead)
                        current += bytesRead
                        scope.launch {
                            listener?.onProgress(current.toLong(), totalSize)
                        }
                    }
                }
            }
            if (isCancelled) {
                Log.d(TAG, "檔案下載被取消")
                return null
            }
            val result = when (type) {
                FileType.JSON -> String(content)
                FileType.APK, FileType.MP4, FileType.IMAGE -> toFile.absolutePath
                FileType.BIN -> content
            }
            Log.d(TAG, "複製完成 → 結果：$result")
            return result
        } catch (e: IOException) {
            if (!isCancelled) {
                Log.e(TAG, "複製時IOException：${e.localizedMessage}", e)
                scope.launch { listener?.onError(UsbError.COPY_FILE_ERROR) }
            } else {
                Log.d(TAG, "複製中使用者取消，不回報錯誤")
            }
            return null
        } catch (e: Exception) {
            Log.e(TAG, "複製其他錯誤：${e.localizedMessage}", e)
            scope.launch { listener?.onError(UsbError.COPY_FILE_ERROR) }
            return null
        }
    }


    private fun checkVideo(
        usbFile: UsbFile,
        maxSizeMb: Float = 120f,
        expectedMime: String = "video/mp4"
    ): Boolean {
        val mime = getMimeType(usbFile.absolutePath)
        val fileSizeMb = byte2Mb(usbFile.length)
        Log.e(TAG, "檢查影片 → ${usbFile.name}，檔案大小：${usbFile.length}，約 ${fileSizeMb}MB，是否符合 $expectedMime：${mime == expectedMime}")
        val errors = IntArray(4).apply {
            this[0] = if (mime == expectedMime) 1 else 2
            this[1] = 0
            this[2] = if (fileSizeMb <= maxSizeMb) 1 else 2
            this[3] = 1
        }
        lastVideoValidationErrors = errors
        return !errors.contains(2)
    }

    fun getVideoValidationErrors(): IntArray = lastVideoValidationErrors

    fun requestPermission(csUsb: CSUsbDevice?) {
        csUsb?.let { device ->
            Log.d(TAG, "申請權限 → 裝置：${device.name}")
            if (usbManager.hasPermission(device.device.usbDevice)) {
                device.isPermissionGranted = true
                if (autoFileName != null && autoFileType != null && autoFileKind != null) {
                    findFile(device, autoFileName, autoFileType, autoFileKind)
                    autoFileName = null; autoFileType = null; autoFileKind = null
                } else {
                    Unit
                }
            } else if (usbDevicesMap.containsKey(device.name)) {
                val intent = PendingIntent.getBroadcast(
                    context, 0, Intent(ACTION_USB_PERMISSION),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                usbManager.requestPermission(device.device.usbDevice, intent)
                Log.d(TAG, "已向系統申請權限：${device.name}")
            } else {
                Log.w(TAG, "裝置不存在或已拔除：${device.name}")
                scope.launch { listener?.onError(UsbError.PERMISSION_FAILED) }
            }
        } ?: scope.launch { listener?.onError(UsbError.PARAMETER_ERROR) }
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
                    synchronized(this) {
                        val device: UsbDevice? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                            intent.getParcelableExtra("device", UsbDevice::class.java)
                        else
                            @Suppress("DEPRECATION") intent.getParcelableExtra("device")
                        val granted = intent.getBooleanExtra("permission", false)
                        device?.deviceName?.let { name ->
                            usbDevicesMap[name]?.let { csUsb ->
                                csUsb.isPermissionGranted = granted
                                Log.d(TAG, "權限回傳 → 裝置：$name，是否授權：$granted")
                                if (!granted) {
                                    scope.launch { listener?.onError(UsbError.PERMISSION_FAILED) }
                                } else if (autoFileName != null && autoFileType != null && autoFileKind != null) {
                                    findFile(csUsb, autoFileName, autoFileType, autoFileKind)
                                    autoFileName = null; autoFileType = null; autoFileKind = null
                                } else {
                                    Unit
                                }
                            }
                        }
                    }
                }
                ACTION_USB_DEVICE_ATTACHED -> {
                    val dev: UsbDevice? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                        intent.getParcelableExtra(UsbManager.EXTRA_DEVICE, UsbDevice::class.java)
                    else
                        @Suppress("DEPRECATION") intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
                    dev?.deviceName?.let { name ->
                        if (attachedDevices.add(name)) {
                            Log.d(TAG, "裝置插入：$name，開始自動取得 USB 裝置")
                            getUsbDevice().firstOrNull()?.let { requestPermission(it) }
                                ?: Log.d(TAG, "自動流程：未找到 USB 裝置")
                            scope.launch { listener?.onDeviceAttached(name) }
                        } else {
                            Log.d(TAG, "裝置插入，但已處理過 → $name")
                        }
                    }
                }
                ACTION_USB_DEVICE_DETACHED -> {
                    val dev: UsbDevice? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                        intent.getParcelableExtra(UsbManager.EXTRA_DEVICE, UsbDevice::class.java)
                    else
                        @Suppress("DEPRECATION") intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
                    dev?.deviceName?.let { name ->
                        // 使用者拔除 → 取消複製、關閉底層，並只印停止訊息
                        isCancelled = true
                        usbDevicesMap[name]?.device?.let { massDev ->
                            try {
                                massDev.close()
                            } catch (e: Exception) {
                                Log.e(TAG, "關閉裝置時出錯：${e.localizedMessage}", e)
                            }
                        }
                        Log.d(TAG, "檔案下載停止（USB 已拔除）")
                        attachedDevices.remove(name)
                        scope.launch { listener?.onDeviceDetached(name) }
                    }
                }
            }
        }
    }

    fun closeUsb() {
        try {
            // 取消所有Coroutine
            scope.cancel()

            // 關閉所有裝置
            usbDevicesMap.values.forEach {
                try {
                    it.device.close()
                } catch (e: Exception) {
                    Log.e(TAG, "關閉裝置錯誤：${e.localizedMessage}", e)
                }
            }
            usbDevicesMap.clear()

            // 註銷廣播接收器
            broadcastReceiver?.let {
                try {
                    context.unregisterReceiver(it)
                } catch (_: Exception) {}
            }
            broadcastReceiver = null

            listener = null
            Log.d(TAG, "所有 USB 資源已釋放")
        } catch (e: Exception) {
            Log.e(TAG, "關閉 USB 時發生錯誤：${e.localizedMessage}", e)
        }
    }


    data class CSUsbDevice(val device: UsbMassStorageDevice) {
        var isPermissionGranted = false
        val name: String = device.usbDevice.deviceName
    }

    interface UsbReaderListener {
        fun onFindFile(file: String, status: FileStatus, type: FileType, data: String?, raw: ByteArray?, kind: FileKind)
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
        return ext.takeIf { it.isNotEmpty() }?.let { MimeTypeMap.getSingleton().getMimeTypeFromExtension(it) }
    }

    private fun byte2Mb(size: Long): Float = size / (1024f * 1024f)
}
