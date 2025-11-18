package com.dyaco.spirit_commercial.support.usb

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbManager
import android.util.Log
import kotlinx.coroutines.*
import java.io.File
import java.io.IOException

/**
 * UsbReaderV：以「掛載點 + File API」為核心的 USB 檔案讀取器。
 *
 * 特性：
 * - 不使用 libaums，不需要 USB 權限
 * - 以 StorageManager / /proc/mounts / 目錄掃描 找到掛載點（支援 exFAT 等）
 * - API 風格模仿 UsbReaderKt：setListener / autoFindFile / findFile / closeUsb
 *
 * 注意：
 * - 只處理「系統已掛載」的外接儲存，不處理 MTP/PTP。
 */
class UsbReaderV(
    private val context: Context
) {

    companion object {
        private const val TAG = "USB_UPDATE_V"
        private const val ACTION_USB_DEVICE_ATTACHED = UsbManager.ACTION_USB_DEVICE_ATTACHED
        private const val ACTION_USB_DEVICE_DETACHED = UsbManager.ACTION_USB_DEVICE_DETACHED
    }

    // ---------------------- 狀態 & 設定 ----------------------

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private var listener: UsbReaderListener? = null
    private var broadcastReceiver: BroadcastReceiver? = null

    // 自動流程參數
    private var autoFileName: String? = null
    private var autoFileType: FileType? = null
    private var autoFileKind: FileKind? = null

    // 執行狀態
    private var isFinding = false
    private var isCancelled = false

    // 最近一次偵測到的掛載點
    private var lastMountedPath: String? = null
    private val attachedDevices = mutableSetOf<String>()

    // 檔案儲存資料夾（預設 cacheDir）
    private var fileSaveDir: File = context.cacheDir

    // ---------------------- 對外介面 ----------------------

    /**
     * 註冊 listener 並啟用 USB 插拔監聽。
     * 傳入 null 則停止監聽並釋放資源。
     */
    fun setListener(l: UsbReaderListener?) {
        listener = l
        isCancelled = false

        if (l != null) {
            if (broadcastReceiver == null) {
                broadcastReceiver = createBroadcastReceiver()
                val filter = IntentFilter().apply {
                    addAction(ACTION_USB_DEVICE_ATTACHED)
                    addAction(ACTION_USB_DEVICE_DETACHED)
                }
                context.registerReceiver(broadcastReceiver, filter)
                Log.d(TAG, "已註冊廣播接收器")

                // 啟動時掃描一次既有掛載點
                scanExistingMount()
            }
        } else {
            // 停止監聽
            broadcastReceiver?.let {
                try {
                    context.unregisterReceiver(it)
                } catch (_: Exception) {
                }
            }
            broadcastReceiver = null
            Log.d(TAG, "已取消廣播接收器註冊")
        }
    }

    /**
     * 自動流程：
     * - 先找目前已掛載的 USB（或之後插進來）
     * - 找到後自動尋檔 + 複製 / 讀取
     */
    fun autoFindFile(fileName: String, type: FileType, kind: FileKind) {
        Log.d(TAG, "啟動自動尋找流程 → 檔名：$fileName，類型：$type，種類：$kind")
        autoFileName = fileName
        autoFileType = type
        autoFileKind = kind

        // 若一開始就有掛載點，直接開始找檔案
        scope.launch(Dispatchers.IO) {
            val mount = UsbMountedStorageReader.findUsbMountPoint(context)
            if (mount != null) {
                lastMountedPath = mount.absolutePath
                if (attachedDevices.add(mount.absolutePath)) {
                    withContext(Dispatchers.Main) {
                        listener?.onDeviceAttached(mount.absolutePath)
                    }
                }
                findFile(CSUsbDevice(mount), fileName, type, kind)
            } else {
                Log.d(TAG, "autoFindFile(): 目前沒有掛載點，等待 ACTION_USB_DEVICE_ATTACHED 觸發")
            }
        }
    }

    /**
     * 傳統流程：外部手動指定 device（掛載點）與檔案資訊。
     */
    fun findFile(
        device: CSUsbDevice?,
        fileName: String?,
        type: FileType?,
        kind: FileKind?
    ) {
        if (device == null || fileName == null || type == null || kind == null) {
            Log.d(TAG, "findFile() 參數有誤")
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
            val (status, copyResult) = findFileInBackground(device.root, fileName, type, kind)
            isFinding = false

            val data = if (type in setOf(FileType.APK, FileType.MP4, FileType.IMAGE, FileType.JSON))
                copyResult as? String else null
            val raw = if (type == FileType.BIN) copyResult as? ByteArray else null

            Log.d(TAG, "尋找結果 → 狀態：$status，data：$data，raw 長度：${raw?.size ?: 0}")
            listener?.onFindFile(fileName, status, type, data, raw, kind)
        }
    }

    /**
     * 取得目前偵測到的 USB root 列表（實際上大多只有 0 或 1 個）。
     */
    fun getUsbDevice(): List<CSUsbDevice> {
        val list = mutableListOf<CSUsbDevice>()
        lastMountedPath?.let { path ->
            val f = File(path)
            if (f.exists() && f.isDirectory && f.canRead()) {
                list.add(CSUsbDevice(f))
            }
        }
        Log.d(TAG, "getUsbDevice: 目前有 ${list.size} 個掛載點")
        return list
    }

    /**
     * 關閉所有資源：取消 coroutine、解除廣播、清除 listener。
     */
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
            lastMountedPath = null

            Log.d(TAG, "UsbReaderV 所有資源已釋放")
        } catch (e: Exception) {
            Log.e(TAG, "關閉 UsbReaderV 時發生錯誤：${e.localizedMessage}", e)
        }
    }

    // ---------------------- 內部實作：掃描 / 尋檔 ----------------------

    /**
     * 啟動時掃描目前是否已經有掛載的 USB。
     */
    private fun scanExistingMount() {
        scope.launch(Dispatchers.IO) {
            val mount = UsbMountedStorageReader.findUsbMountPoint(context)
            if (mount != null) {
                val name = mount.absolutePath
                lastMountedPath = name
                if (attachedDevices.add(name)) {
                    Log.d(TAG, "scanExistingMount: 模擬 onDeviceAttached → $name")
                    withContext(Dispatchers.Main) {
                        listener?.onDeviceAttached(name)
                    }
                }
            } else {
                Log.d(TAG, "scanExistingMount: 目前沒有 USB 掛載點")
            }
        }
    }

    /**
     * 背景遞迴尋檔 + 複製 / 讀取內容。
     */
    private suspend fun findFileInBackground(
        mountRoot: File,
        fileName: String,
        type: FileType,
        kind: FileKind
    ): Pair<FileStatus, Any?> = withContext(Dispatchers.IO) {

        Log.d(TAG, "背景尋找檔案：$fileName，mountRoot=${mountRoot.absolutePath}")
        var fileStatus = FileStatus.FILE_NOT_FOUND
        var copyResult: Any? = null

        try {
            val target = UsbMountedStorageReader.findFileOnUsbRecursive(mountRoot, fileName, 6)
            if (target == null) {
                Log.d(TAG, "找不到檔案：$fileName（mount=${mountRoot.absolutePath}）")
            } else {
                Log.d(TAG, "找到檔案：${target.absolutePath}，開始複製")
                fileStatus = FileStatus.FILE_FOUND

                // ※ 這裡如果要做影片驗證，可再加一層 checkVideo(target)

                copyResult = when (type) {
                    FileType.JSON -> {
                        val bytes = UsbMountedStorageReader.readFileAsBytes(target)
                        String(bytes)
                    }
                    FileType.APK, FileType.MP4, FileType.IMAGE -> {
                        UsbMountedStorageReader.copyFileToCache(context, target)
                    }
                    FileType.BIN -> {
                        UsbMountedStorageReader.readFileAsBytes(target)
                    }
                }

                if (copyResult == null) {
                    fileStatus = FileStatus.WRITE_FAIL
                }
            }
        } catch (e: IOException) {
            if (!isCancelled) {
                Log.e(TAG, "背景尋找檔案時 IOException：${e.localizedMessage}", e)
                withContext(Dispatchers.Main) {
                    listener?.onError(UsbError.COPY_FILE_ERROR)
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

    // ---------------------- 廣播處理 ----------------------

    private fun createBroadcastReceiver(): BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(ctx: Context?, intent: Intent?) {
            intent ?: return
            Log.d(TAG, "接收到廣播：${intent.action}")
            when (intent.action) {
                ACTION_USB_DEVICE_ATTACHED -> handleUsbAttached()
                ACTION_USB_DEVICE_DETACHED -> handleUsbDetached()
            }
        }
    }

    /**
     * USB 插入廣播：輪詢等待掛載完成。
     */
    private fun handleUsbAttached() {
        scope.launch(Dispatchers.IO) {
            val maxWaitMs = 10_000L
            val intervalMs = 500L
            val start = System.currentTimeMillis()
            var mount: File? = null

            while (System.currentTimeMillis() - start < maxWaitMs) {
                mount = UsbMountedStorageReader.findUsbMountPoint(context)
                if (mount != null) {
                    Log.d(TAG, "ACTION_USB_DEVICE_ATTACHED: 在等待中找到掛載點=${mount.absolutePath}")
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

                    // 若有設定 auto 流程，這邊直接觸發
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
                Log.w(TAG, "ACTION_USB_DEVICE_ATTACHED: 在 $maxWaitMs ms 內都沒偵測到掛載點（可能系統未掛載或檔案系統不支援）")
            }
        }
    }

    /**
     * USB 拔除廣播：標記取消、清掉狀態。
     */
    private fun handleUsbDetached() {
        val name = lastMountedPath ?: run {
            Log.d(TAG, "USB_DEVICE_DETACHED: 沒有 lastMountedPath 可用，略過")
            return
        }
        isCancelled = true
        attachedDevices.remove(name)
        lastMountedPath = null

        Log.d(TAG, "USB 已拔除：$name，停止任何下載作業")
        scope.launch {
            listener?.onDeviceDetached(name)
        }
    }

    // ---------------------- 型別宣告 ----------------------

    /**
     * 代表一個 USB 掛載點（根目錄）
     */
    data class CSUsbDevice(val root: File) {
        val name: String = root.absolutePath
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
        COPY_FILE_ERROR,
        BACKGROUND_SEARCH_ERROR,
        UNKNOWN_ERROR
    }
}
