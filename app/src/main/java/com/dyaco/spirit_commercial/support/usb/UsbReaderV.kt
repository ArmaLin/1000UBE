package com.dyaco.spirit_commercial.support.usb

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Build
import android.util.Log
import android.webkit.MimeTypeMap
import kotlinx.coroutines.*
import java.io.*
import java.nio.file.Files

/**
 * UsbReaderV
 * 不使用 libaums，改用「系統掛載點 + File IO」來讀 USB 檔案，
 * 但提供跟 UsbReaderKt 幾乎相同的 API 與 callback 行為。
 * 支援：
 * - USB 插拔偵測（ACTION_USB_DEVICE_ATTACHED / DETACHED）
 * - 自動流程 autoFindFile()
 * - 傳統流程 findFile()
 * - 進度回報 onProgress()
 * - JSON / APK / MP4 / IMAGE / BIN 等不同型態
 *
 */
class UsbReaderV(private val context: Context) {

    companion object {
        private const val TAG = "USB_UPDATE_V"
        private const val ACTION_USB_DEVICE_ATTACHED =
            "android.hardware.usb.action.USB_DEVICE_ATTACHED"
        private const val ACTION_USB_DEVICE_DETACHED =
            "android.hardware.usb.action.USB_DEVICE_DETACHED"
    }

    private val usbManager =
        context.getSystemService(Context.USB_SERVICE) as UsbManager? // 這邊只用來拿 UsbDevice 名稱用，實際讀檔不靠它

    private val filter = IntentFilter().apply {
        addAction(ACTION_USB_DEVICE_ATTACHED)
        addAction(ACTION_USB_DEVICE_DETACHED)
    }

    private var broadcastReceiver: BroadcastReceiver? = createBroadcastReceiver()
    private var listener: UsbReaderListener? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    // 狀態
    private var isFinding = false
    private var isCancelled = false

    private var fileSaveDir: File = context.cacheDir
    private val attachedDevices = mutableSetOf<String>() // 存 mountPath 或 deviceName

    // 自動流程參數
    private var autoFileName: String? = null
    private var autoFileType: FileType? = null
    private var autoFileKind: FileKind? = null

    // 影片驗證狀態（同 UsbReaderKt）
    private var lastVideoValidationErrors: IntArray = intArrayOf(0, 0, 0, 0)

    // 紀錄最近一次偵測到的 USB 掛載點（用來給 onDeviceDetached call back 使用）
    private var lastMountedPath: String? = null

    // --------------------------------------------------------
// Listener / 廣播註冊
// --------------------------------------------------------
    fun setListener(l: UsbReaderListener?) {
        listener = l
        isCancelled = false
        attachedDevices.clear()
        lastMountedPath = null

        if (listener != null && broadcastReceiver != null) {
            context.registerReceiver(broadcastReceiver, filter)
            Log.d(TAG, "已註冊廣播接收器")
            scanExistingDevices()
        } else {
            Log.d(TAG, "註冊廣播接收器失敗")
        }
    }

    /**
     * 掃描「目前已經掛載」的 USB 裝置，並 callback 一次 onDeviceAttached
     */
    private fun scanExistingDevices() {
        scope.launch(Dispatchers.IO) {
            val mount = UsbMountedStorageReader.findUsbMountPoint(context)
            if (mount != null) {
                val name = mount.absolutePath
                if (attachedDevices.add(name)) {
                    lastMountedPath = name
                    Log.d(TAG, "scanExistingDevices: 模擬 onDeviceAttached → $name")
                    withContext(Dispatchers.Main) {
                        listener?.onDeviceAttached(name)
                    }
                }
            } else {
                Log.d(TAG, "scanExistingDevices: 目前沒有已掛載的 USB")
            }
        }
    }

    // --------------------------------------------------------
// 自動流程：取得裝置、搜尋檔案
// --------------------------------------------------------
    fun autoFindFile(fileName: String, type: FileType, kind: FileKind) {
        Log.d(TAG, "啟動自動尋找流程 → 檔名：$fileName，類型：$type，種類：$kind")
        autoFileName = fileName
        autoFileType = type
        autoFileKind = kind

// 直接啟動尋找（不需要 USB permission）
        getUsbDevice().firstOrNull()?.let { device ->
            findFile(device, fileName, type, kind)
        } ?: run {
            Log.d(TAG, "autoFindFile: 無 USB 掛載點")
            scope.launch { listener?.onError(UsbError.NO_USB_DEVICE) }
        }
    }

    // --------------------------------------------------------
// 傳統流程：由外部呼叫搜尋檔案
// --------------------------------------------------------
    fun findFile(
        device: CSUsbDevice?, fileName: String?, type: FileType?, kind: FileKind?
    ) {
        if (fileName == null || type == null || kind == null) {
            Log.d(TAG, "參數有誤")
            scope.launch { listener?.onError(UsbError.PARAMETER_ERROR) }
            return
        }

// device 在本實作中其實不太重要，只是保留 API 兼容
        val realDevice = device ?: getUsbDevice().firstOrNull()
        if (realDevice == null) {
            Log.d(TAG, "findFile: 找不到 USB 掛載點")
            scope.launch { listener?.onError(UsbError.NO_USB_DEVICE) }
            return
        }

        if (isFinding) {
            Log.d(TAG, "已有尋找作業進行中")
            return
        }

        isFinding = true
        isCancelled = false

        scope.launch {
            val (status, copyResult) = findFileInBackground(realDevice, fileName, type, kind)
            isFinding = false

            val data = if (type in setOf(FileType.APK, FileType.MP4, FileType.IMAGE, FileType.JSON))
                copyResult as? String else null
            val raw = if (type == FileType.BIN) copyResult as? ByteArray else null

            Log.d(
                TAG,
                "尋找結果 → 狀態：$status，data=$data，raw 長度=${raw?.size ?: 0}"
            )
            listener?.onFindFile(fileName, status, type, data, raw, kind)
        }
    }

    // --------------------------------------------------------
// 背景尋找 + 複製
// --------------------------------------------------------
    private suspend fun findFileInBackground(
        device: CSUsbDevice,
        fileName: String,
        type: FileType,
        kind: FileKind
    ): Pair<FileStatus, Any?> = withContext(Dispatchers.IO) {
        Log.d(TAG, "背景尋找檔案：$fileName，mountRoot=${device.mountRoot}")

        var fileStatus = FileStatus.FILE_NOT_FOUND
        var copyResult: Any? = null

        try {
            val mountRoot = device.mountRoot
            if (!mountRoot.exists() || !mountRoot.isDirectory || !mountRoot.canRead()) {
                Log.e(TAG, "背景尋找：掛載點不可用：${mountRoot.absolutePath}")
                return@withContext Pair(FileStatus.FILE_NOT_FOUND, null)
            }

            val targetFile =
                UsbMountedStorageReader.findFileOnUsbRecursive(mountRoot, fileName, maxDepth = 6)

            if (targetFile == null) {
                Log.d(TAG, "找不到檔案：$fileName（mount=${mountRoot.absolutePath}）")
            } else {
                Log.d(TAG, "找到檔案：${targetFile.absolutePath}，開始複製")
                fileStatus = FileStatus.FILE_FOUND

                if (type == FileType.MP4 && !checkVideo(targetFile)) {
                    Log.e(TAG, "影片檢查失敗 → ${targetFile.name}")
                    withContext(Dispatchers.Main) {
                        listener?.onError(UsbError.VIDEO_VALIDATION_FAILED)
                    }
                    return@withContext Pair(FileStatus.FILE_NOT_FOUND, null)
                }

                val target = File(fileSaveDir, targetFile.name)
                copyResult = copyAndReadFile(targetFile, target, type)

                if (copyResult == null) {
                    fileStatus = FileStatus.WRITE_FAIL
                }
            }
        } catch (e: Exception) {
            if (!isCancelled) {
                Log.e(TAG, "背景尋找檔案錯誤：${e.localizedMessage}", e)
                withContext(Dispatchers.Main) {
                    listener?.onError(UsbError.BACKGROUND_SEARCH_ERROR)
                }
            }
        }

        Log.d(TAG, "背景尋找結束 → 狀態：$fileStatus")
        Pair(fileStatus, copyResult)
    }

    private fun copyAndReadFile(
        fromFile: File,
        toFile: File,
        type: FileType
    ): Any? {
        Log.d(TAG, "開始複製檔案：${fromFile.absolutePath}")
        val totalSize = fromFile.length()
        if (totalSize <= 0L) {
            Log.w(TAG, "來源檔案大小為 0：${fromFile.absolutePath}")
        }

        val content =
            if (type == FileType.BIN || type == FileType.JSON) ByteArray(totalSize.toInt())
            else null

        val buffer = ByteArray(16 * 1024)
        var current = 0L

        try {
            BufferedInputStream(FileInputStream(fromFile)).use { inputStream ->
// 若要複製到 cache（APK/MP4/IMAGE/JSON 都會複製一份）
                BufferedOutputStream(
                    Files.newOutputStream(toFile.toPath()),
                    buffer.size
                ).use { outputStream ->
                    while (!isCancelled) {
                        val bytesRead = inputStream.read(buffer)
                        if (bytesRead <= 0) break

// 寫到目標檔案
                        outputStream.write(buffer, 0, bytesRead)

// 若需要 ByteArray，順便存進 content
                        if (content != null) {
                            System.arraycopy(
                                buffer,
                                0,
                                content,
                                current.toInt(),
                                bytesRead
                            )
                        }

                        current += bytesRead

// 回報進度
                        scope.launch {
                            listener?.onProgress(current, totalSize)
                        }
                    }
                }
            }

            if (isCancelled) {
                Log.d(TAG, "檔案下載被取消")
                return null
            }

            val result: Any? = when (type) {
                FileType.JSON -> {
                    if (content != null) {
                        String(content)
                    } else {
// 保底：若 content 為 null，就直接從目標檔案讀
                        toFile.readText()
                    }
                }

                FileType.APK, FileType.MP4, FileType.IMAGE -> toFile.absolutePath
                FileType.BIN -> content
            }

            Log.d(TAG, "複製完成 → 結果：$result")
            return result
        } catch (e: IOException) {
            if (!isCancelled) {
                Log.e(TAG, "複製時 IOException：${e.localizedMessage}", e)
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

    // --------------------------------------------------------
// 影片檢查：改用 File
// --------------------------------------------------------
    private fun checkVideo(
        file: File,
        maxSizeMb: Float = 120f,
        expectedMime: String = "video/mp4"
    ): Boolean {
        val mime = getMimeType(file.absolutePath)
        val fileSizeMb = byte2Mb(file.length())
        Log.e(
            TAG,
            "檢查影片 → ${file.name}，檔案大小：${file.length()}，約 ${fileSizeMb}MB，是否符合 $expectedMime：${mime == expectedMime}"
        )

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

    // --------------------------------------------------------
// USB Device 抽象（其實就是掛載點）
// --------------------------------------------------------
    data class CSUsbDevice(val mountRoot: File) {
        val name: String = mountRoot.absolutePath
    }

    /**
     * 取得目前的 USB「裝置」列表
     * 這裡其實只有一個：目前偵測到的掛載點
     */
    fun getUsbDevice(): List<CSUsbDevice> {
        val mount = UsbMountedStorageReader.findUsbMountPoint(context) ?: run {
            Log.d(TAG, "getUsbDevice: 找不到任何掛載點")
            return emptyList()
        }
        Log.d(TAG, "getUsbDevice: 掛載點=${mount.absolutePath}")
        return listOf(CSUsbDevice(mount))
    }

    // --------------------------------------------------------
// 廣播處理：USB 插拔
// --------------------------------------------------------
    private fun createBroadcastReceiver(): BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(ctx: Context?, intent: Intent?) {
            intent ?: return
            Log.d(TAG, "接收到廣播：${intent.action}")

            when (intent.action) {
                ACTION_USB_DEVICE_ATTACHED -> {
                    scope.launch(Dispatchers.IO) {
                        val maxWaitMs = 10_000L     // 最多等 10 秒
                        val intervalMs = 500L       // 每 0.5 秒查一次
                        val start = System.currentTimeMillis()
                        var mount: File? = null

                        while (System.currentTimeMillis() - start < maxWaitMs) {
                            mount = UsbMountedStorageReader.findUsbMountPoint(context)
                            if (mount != null) {
                                Log.d(
                                    TAG,
                                    "ACTION_USB_DEVICE_ATTACHED: 在等待中找到掛載點=${mount.absolutePath}"
                                )
                                break
                            }
                            Log.d(TAG, "ACTION_USB_DEVICE_ATTACHED: 尚未掛載完成，繼續等 ${intervalMs}ms")
                            delay(intervalMs)
                        }

                        if (mount != null) {
                            val name = mount!!.absolutePath
                            lastMountedPath = name
                            if (attachedDevices.add(name)) {
                                Log.d(TAG, "裝置插入並偵測到掛載點：$name")

                                // 有自動流程就直接跑
                                val autoName = autoFileName
                                val autoType = autoFileType
                                val autoKind = autoFileKind
                                if (autoName != null && autoType != null && autoKind != null) {
                                    findFile(
                                        CSUsbDevice(mount!!),
                                        autoName,
                                        autoType,
                                        autoKind
                                    )
                                    autoFileName = null
                                    autoFileType = null
                                    autoFileKind = null
                                }

                                withContext(Dispatchers.Main) {
                                    listener?.onDeviceAttached(name)
                                }
                            } else {
                                Log.d(TAG, "裝置插入，但相同掛載點已處理過 → $name")
                            }
                        } else {
                            Log.w(
                                TAG,
                                "ACTION_USB_DEVICE_ATTACHED: 在 $maxWaitMs ms 內都沒偵測到掛載點（可能系統沒掛，或 FS 不支援）"
                            )
                        }
                    }
                }


                ACTION_USB_DEVICE_DETACHED -> {
// 這邊只用來通知「USB 拔除」，掛載點實際上可能已經被卸載
                    isCancelled = true

                    val dev: UsbDevice? =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            intent.getParcelableExtra(
                                UsbManager.EXTRA_DEVICE,
                                UsbDevice::class.java
                            )
                        } else {
                            @Suppress("DEPRECATION")
                            intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
                        }

                    val name = lastMountedPath ?: dev?.deviceName ?: "UNKNOWN"
                    Log.d(TAG, "USB 已拔除：$name，停止任何下載作業")
                    attachedDevices.remove(name)
                    scope.launch { listener?.onDeviceDetached(name) }
                }
            }
        }
    }

    // --------------------------------------------------------
// 關閉 / 清理
// --------------------------------------------------------
    fun closeUsb() {
        try {
            scope.cancel()
            broadcastReceiver?.let {
                try {
                    context.unregisterReceiver(it)
                } catch (_: Exception) {
                }
            }
            broadcastReceiver = null
            listener = null
            attachedDevices.clear()
            Log.d(TAG, "UsbReaderV 所有資源已釋放")
        } catch (e: Exception) {
            Log.e(TAG, "關閉 UsbReaderV 時發生錯誤：${e.localizedMessage}", e)
        }
    }

    // --------------------------------------------------------
// Public interface / enums（跟 UsbReaderKt 對齊）
// --------------------------------------------------------
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

    enum class FileStatus {
        FILE_NOT_FOUND,
        FILE_FOUND,
        WRITE_FAIL
    }

    enum class FileType {
        MP4,
        IMAGE,
        JSON,
        APK,
        BIN
    }

    enum class FileKind {
        SUB_MCU,
        LWR,
        NORMAL
    }

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

    // --------------------------------------------------------
// 小工具
// --------------------------------------------------------
    private fun getMimeType(url: String): String? {
        val ext = MimeTypeMap.getFileExtensionFromUrl(url)
        return ext.takeIf { it.isNotEmpty() }
            ?.let { MimeTypeMap.getSingleton().getMimeTypeFromExtension(it) }
    }

    private fun byte2Mb(size: Long): Float = size / (1024f * 1024f)
}