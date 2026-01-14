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
import android.webkit.MimeTypeMap
import com.github.mjdev.libaums.UsbMassStorageDevice
import com.github.mjdev.libaums.driver.scsi.commands.sense.UnitAttention
import com.github.mjdev.libaums.fs.FileSystem
import com.github.mjdev.libaums.fs.UsbFile
import com.github.mjdev.libaums.fs.UsbFileStreamFactory
import kotlinx.coroutines.*
import timber.log.Timber
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
            Timber.tag(TAG).d("已註冊廣播接收器")
            scanExistingDevices()
        } else {
            Timber.tag(TAG).d("註冊廣播接收器失敗")
        }
    }

    /**
     * 掃描已插上的 USB 裝置，並對尚未觸發過的裝置呼 onDeviceAttached
     */
    private fun scanExistingDevices() {
        getUsbDevice().forEach { csUsb ->
            val name = csUsb.name
            if (attachedDevices.add(name)) {
                Timber.tag(TAG).d("scanExistingDevices: 模擬 onDeviceAttached → $name")
                scope.launch { listener?.onDeviceAttached(name) }
            }
        }
    }

    /**
     * 自動流程：取得裝置、申請權限、搜尋檔案
     */
    fun autoFindFile(fileName: String, type: FileType, kind: FileKind) {
        Timber.tag(TAG).d( "啟動自動尋找流程 → 檔名：$fileName，類型：$type，種類：$kind")
        autoFileName = fileName
        autoFileType = type
        autoFileKind = kind
        getUsbDevice().firstOrNull()?.let { requestPermission(it) }
            ?: run {
                Timber.tag(TAG).d( "無 USB 裝置")
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
            Timber.tag(TAG).d( "參數有誤")
            scope.launch { listener?.onError(UsbError.PARAMETER_ERROR) }
            return
        }
        if (isFinding) {
            Timber.tag(TAG).d( "已有尋找作業進行中")
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
            Timber.tag(TAG).d( "尋找結果 → 狀態：$status，data：$data，raw 長度：${raw?.size ?: 0}")
            listener?.onFindFile(fileName, status, type, data, raw, kind)
        }
    }

    private suspend fun findFileInBackground(
        device: CSUsbDevice, fileName: String, type: FileType, kind: FileKind
    ): Pair<FileStatus, Any?> = withContext(Dispatchers.IO) {
        Timber.tag(TAG).d( "背景尋找檔案：$fileName")
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
            } catch (_: UnitAttention) {
                attempts++
                Timber.tag(TAG).w("UnitAttention，重試 init 第 $attempts 次")
                delay(200)
            } catch (e: IOException) {
                Timber.tag(TAG).e(e, "初始化時 IOException：${e.localizedMessage}")
                withContext(Dispatchers.Main) { listener?.onError(UsbError.BACKGROUND_SEARCH_ERROR) }
                return@withContext Pair(FileStatus.FILE_NOT_FOUND, null)
            }
        }

        if (!initialized) {
            Timber.tag(TAG).e("初始化 UnitAttention 重試失敗")
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
                Timber.tag(TAG).d("找不到檔案：$fileName")
            } else {
                Timber.tag(TAG).d("找到檔案：${usbFile.name}，開始複製")
                fileStatus = FileStatus.FILE_FOUND

                if (type == FileType.MP4 && !checkVideo(usbFile)) {
                    Timber.tag(TAG).e("影片檢查失敗 → ${usbFile.name}")
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
                Timber.tag(TAG).e(e, "背景尋找檔案錯誤：${e.localizedMessage}")
                withContext(Dispatchers.Main) { listener?.onError(UsbError.BACKGROUND_SEARCH_ERROR) }
            }
        } finally {
            try {
                device.device.close()
            } catch (_: Exception) {}
            Timber.tag(TAG).d("搜尋結束，已關閉裝置：${device.name}")
        }

        Timber.tag(TAG).d("背景尋找結束 → 狀態：$fileStatus")
        Pair(fileStatus, copyResult)
    }


    private fun copyAndReadFile(
        fileSystem: FileSystem, fromFile: UsbFile, toFile: File,
        chunkSize: Int, type: FileType
    ): Any? {
        Timber.tag(TAG).d("開始串流複製檔案：${fromFile.name} 至 ${toFile.absolutePath}")
        val totalSize = fromFile.length
        // 使用一個固定大小的緩衝區，例如 16KB，避免一次載入整個檔案
        val buffer = ByteArray(chunkSize.coerceAtMost(16 * 1024))
        var currentBytesCopied = 0L // 使用 Long 來避免大檔案時的溢位

        try {
            // 建立從 USB 檔案讀取的輸入流
            BufferedInputStream(
                UsbFileStreamFactory.createBufferedInputStream(fromFile, fileSystem)
            ).use { inputStream ->
                // 建立寫入到 app 快取目錄的輸出流
                BufferedOutputStream(Files.newOutputStream(toFile.toPath()), buffer.size).use { outputStream ->
                    while (!isCancelled) {
                        val bytesRead = inputStream.read(buffer)
                        if (bytesRead <= 0) break // 讀取完畢

                        // 將讀取到的緩衝區內容直接寫入輸出流
                        outputStream.write(buffer, 0, bytesRead)

                        currentBytesCopied += bytesRead

                        // 更新進度
                        val finalCurrentBytes = currentBytesCopied
                        scope.launch {
                            // 這裡的 onProgress 參數 current 應該是 Long 型別以支援大檔案
                            listener?.onProgress(finalCurrentBytes, totalSize)
                        }
                    }
                    // 確保所有緩衝區的資料都寫入檔案
                    outputStream.flush()
                }
            }

            if (isCancelled) {
                Timber.tag(TAG).d("檔案複製被使用者取消")
                // 如果取消了，刪除不完整的檔案
                if (toFile.exists()) {
                    toFile.delete()
                }
                return null
            }

            Timber.tag(TAG).d("檔案複製完成，總大小: $currentBytesCopied bytes")

            // 檔案成功複製到 toFile 後，再根據類型決定回傳內容
            val result = when (type) {
                // 對於需要內容的類型，從已儲存的 toFile 讀取
                FileType.JSON -> toFile.readText(Charsets.UTF_8)
                FileType.BIN -> toFile.readBytes()
                // 對於只需要路徑的類型，直接回傳路徑
                FileType.APK, FileType.MP4, FileType.IMAGE -> toFile.absolutePath
            }
            Timber.tag(TAG).d("複製完成 → 回傳結果類型：${result?.javaClass?.simpleName}")
            return result

        } catch (e: IOException) {
            if (!isCancelled) {
                Timber.tag(TAG).e(e, "複製檔案時發生 IOException：${e.localizedMessage}")
                scope.launch { listener?.onError(UsbError.COPY_FILE_ERROR) }
            } else {
                Timber.tag(TAG).d("複製過程中斷 (使用者取消)，不回報錯誤")
            }
            // 發生錯誤時刪除不完整的檔案
            if (toFile.exists()) {
                toFile.delete()
            }
            return null
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "複製檔案時發生其他錯誤：${e.localizedMessage}")
            scope.launch { listener?.onError(UsbError.COPY_FILE_ERROR) }
            // 發生錯誤時刪除不完整的檔案
            if (toFile.exists()) {
                toFile.delete()
            }
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
        Timber.tag(TAG)
            .e("檢查影片 → ${usbFile.name}，檔案大小：${usbFile.length}，約 ${fileSizeMb}MB，是否符合 $expectedMime：${mime == expectedMime}")
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
            Timber.tag(TAG).d("申請權限 → 裝置：${device.name}")
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
                Timber.tag(TAG).d("已向系統申請權限：${device.name}")
            } else {
                Timber.tag(TAG).w("裝置不存在或已拔除：${device.name}")
                scope.launch { listener?.onError(UsbError.PERMISSION_FAILED) }
            }
        } ?: scope.launch { listener?.onError(UsbError.PARAMETER_ERROR) }
    }

    fun getUsbDevice(): List<CSUsbDevice> {
        usbDevicesMap.clear()
        UsbMassStorageDevice.getMassStorageDevices(context).forEach { dev ->
            Timber.tag(TAG).d("加入裝置：${dev.usbDevice.deviceName}")
            usbDevicesMap[dev.usbDevice.deviceName] = CSUsbDevice(dev)
        }
        Timber.tag(TAG).d("找到 ${usbDevicesMap.size} 個 USB 裝置")
        return usbDevicesMap.values.toList()
    }

    private fun createBroadcastReceiver(): BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(ctx: Context?, intent: Intent?) {
            intent ?: return
            Timber.tag(TAG).d("接收到廣播：${intent.action}")
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
                                Timber.tag(TAG).d("權限回傳 → 裝置：$name，是否授權：$granted")
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
                            Timber.tag(TAG).d("裝置插入：$name，開始自動取得 USB 裝置")
                            getUsbDevice().firstOrNull()?.let { requestPermission(it) }
                                ?: Timber.tag(TAG).d("自動流程：未找到 USB 裝置")
                            scope.launch { listener?.onDeviceAttached(name) }
                        } else {
                            Timber.tag(TAG).d("裝置插入，但已處理過 → $name")
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
                                Timber.tag(TAG).e(e, "關閉裝置時出錯：${e.localizedMessage}")
                            }
                        }
                        Timber.tag(TAG).d("檔案下載停止（USB 已拔除）")
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
                    Timber.tag(TAG).e(e, "關閉裝置錯誤：${e.localizedMessage}")
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
            Timber.tag(TAG).d("所有 USB 資源已釋放")
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "關閉 USB 時發生錯誤：${e.localizedMessage}")
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
