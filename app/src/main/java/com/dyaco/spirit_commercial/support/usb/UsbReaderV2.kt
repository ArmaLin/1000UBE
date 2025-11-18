package com.dyaco.spirit_commercial.maintenance_mode.usbx

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbManager
import android.util.Log
import com.dyaco.spirit_commercial.support.usb.UsbMountedStorageReader
import kotlinx.coroutines.*
import java.io.File
import java.io.IOException

/**
 * UsbReaderV：以「掛載點 + File API」為核心的 USB 檔案讀取器。
 *
 * 特色：
 * - 不依賴 libaums，不需要 USB 權限
 * - 只處理系統已掛載的 USB 儲存（支援 exFAT、FAT、NTFS等，只要系統有掛）
 * - callback 收斂成三種事件：
 *   - DeviceState：裝置插拔 / 初始狀態
 *   - FileResult：單次檔案尋找結果（成功 / 找不到 / 失敗）
 *   - Progress：複製進度（可選）
 *
 * 使用方式：
 *   val reader = UsbReaderV(context)
 *   reader.setListener(
 *       onDeviceStateChanged = { ... },
 *       onFileResult = { ... }
 *   )
 *   reader.autoFindFile("welcome.png", FileType.IMAGE, FileKind.NORMAL)
 */
class UsbReaderV2(
    private val context: Context
) {

    companion object {
        private const val TAG = "USB_UPDATE_V"
        private const val ACTION_USB_DEVICE_ATTACHED = UsbManager.ACTION_USB_DEVICE_ATTACHED
        private const val ACTION_USB_DEVICE_DETACHED = UsbManager.ACTION_USB_DEVICE_DETACHED
    }

    // ---------------------- 型別宣告 ----------------------

    /**
     * 一次檔案請求資訊
     */
    data class FileRequest(
        val fileName: String,
        val type: FileType,
        val kind: FileKind
    )

    /**
     * 裝置狀態
     */
    sealed class DeviceState {
        /**
         * 已經偵測到掛載點（包含啟動時的 scanExistingMount）
         */
        data class Attached(val mountPath: String) : DeviceState()

        /**
         * 裝置拔除
         */
        data class Detached(val lastMountPath: String?) : DeviceState()
    }

    /**
     * 進度資訊（目前主要給 UI 想顯示 ProgressBar 的時候用）
     */
    data class Progress(
        val fileName: String?,
        val current: Long,
        val total: Long
    )

    /**
     * 檔案結果（統一由 onFileResult 收）
     */
    sealed class FileResult {

        abstract val request: FileRequest
        abstract val mountPath: String

        /**
         * 成功找到檔案
         *
         * dataPath：APK/MP4/IMAGE 類型時 → 複製到 cache 後的路徑
         * rawBytes：BIN 類型時 → 讀出的 ByteArray
         * jsonText：JSON 類型時 → 讀出的字串
         */
        data class Success(
            override val request: FileRequest,
            override val mountPath: String,
            val dataPath: String?,
            val rawBytes: ByteArray?,
            val jsonText: String?
        ) : FileResult()

        /**
         * 找不到檔案（mount 有，但名字對不到）
         */
        data class NotFound(
            override val request: FileRequest,
            override val mountPath: String
        ) : FileResult()

        /**
         * 發生錯誤（IO / 其他）
         */
        data class Failed(
            override val request: FileRequest,
            override val mountPath: String,
            val error: UsbError,
            val message: String? = null,
            val throwable: Throwable? = null
        ) : FileResult()
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

    /**
     * 代表一個 USB 掛載點（根目錄）
     */
    data class CSUsbDevice(val root: File) {
        val name: String = root.absolutePath
    }

    /**
     * Java 用的傳統 Listener（所有事件集中在三個 callback）
     */
    interface Listener {
        fun onDeviceStateChanged(state: DeviceState)
        fun onFileResult(result: FileResult)
        fun onProgress(progress: Progress)
    }

    // ---------------------- 狀態 & 資源 ----------------------

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    // Java / Kotlin 共用 Listener
    private var listener: Listener? = null

    // Kotlin DSL listener（優先於 listener）
    private var onDeviceStateChangedBlock: ((DeviceState) -> Unit)? = null
    private var onFileResultBlock: ((FileResult) -> Unit)? = null
    private var onProgressBlock: ((Progress) -> Unit)? = null

    private var broadcastReceiver: BroadcastReceiver? = null

    // 自動流程目前的請求
    private var currentRequest: FileRequest? = null

    private var isFinding = false
    private var isCancelled = false

    private var lastMountedPath: String? = null
    private val attachedDevices = mutableSetOf<String>()

    private var fileSaveDir: File = context.cacheDir

    // ---------------------- Listener 設定 ----------------------

    /**
     * Java 用：設定 / 清除 Listener。
     * 傳入 null 即停止監聽並釋放資源。
     */
    fun setListener(listener: Listener?) {
        this.listener = listener
        this.onDeviceStateChangedBlock = null
        this.onFileResultBlock = null
        this.onProgressBlock = null

        setupOrTeardownReceiver(listener != null)
    }

    /**
     * Kotlin 用：DSL 版 Listener。
     *
     * 範例：
     *   usbReader.setListener(
     *       onDeviceStateChanged = { state -> ... },
     *       onFileResult = { result -> ... }
     *   )
     */
    @JvmOverloads
    fun setListener(
        onDeviceStateChanged: (DeviceState) -> Unit,
        onFileResult: (FileResult) -> Unit,
        onProgress: (Progress) -> Unit = {}
    ) {
        this.listener = null
        this.onDeviceStateChangedBlock = onDeviceStateChanged
        this.onFileResultBlock = onFileResult
        this.onProgressBlock = onProgress

        setupOrTeardownReceiver(true)
    }

    /**
     * 根據是否需要 listener 來註冊 / 解除廣播接收器
     */
    private fun setupOrTeardownReceiver(needReceiver: Boolean) {
        isCancelled = false
        if (needReceiver) {
            if (broadcastReceiver == null) {
                broadcastReceiver = createBroadcastReceiver()
                val filter = IntentFilter().apply {
                    addAction(ACTION_USB_DEVICE_ATTACHED)
                    addAction(ACTION_USB_DEVICE_DETACHED)
                }
                context.registerReceiver(broadcastReceiver, filter)
                Log.d(TAG, "已註冊廣播接收器")
                scanExistingMount()
            }
        } else {
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

    // ---------------------- 對外 API ----------------------

    /**
     * 自動流程：
     * - 記住這個 FileRequest
     * - 若當下已偵測到掛載點 → 直接找檔
     * - 否則等待 ACTION_USB_DEVICE_ATTACHED，掛載完成後自動找檔
     */
    fun autoFindFile(fileName: String, type: FileType, kind: FileKind) {
        val request = FileRequest(fileName, type, kind)
        currentRequest = request
        Log.d(TAG, "啟動自動尋找流程 → request=$request")

        scope.launch(Dispatchers.IO) {
            val mount = UsbMountedStorageReader.findUsbMountPoint(context)
            if (mount != null) {
                lastMountedPath = mount.absolutePath
                if (attachedDevices.add(mount.absolutePath)) {
                    emitDeviceState(DeviceState.Attached(mount.absolutePath))
                }
                findFileInternal(mount, request)
            } else {
                Log.d(TAG, "autoFindFile(): 目前沒有掛載點，等待 ACTION_USB_DEVICE_ATTACHED")
            }
        }
    }

    /**
     * 傳統流程：外部手動指定掛載點與請求。
     */
    fun findFile(
        device: CSUsbDevice?,
        fileName: String?,
        type: FileType?,
        kind: FileKind?
    ) {
        if (device == null || fileName == null || type == null || kind == null) {
            Log.d(TAG, "findFile() 參數有誤")
            emitFileResult(
                FileResult.Failed(
                    FileRequest(fileName ?: "null", type ?: FileType.BIN, kind ?: FileKind.NORMAL),
                    mountPath = device?.name ?: "",
                    error = UsbError.PARAMETER_ERROR,
                    message = "參數為 null"
                )
            )
            return
        }
        val request = FileRequest(fileName, type, kind)
        currentRequest = request
        scope.launch(Dispatchers.IO) {
            findFileInternal(device.root, request)
        }
    }

    /**
     * 取得目前偵測到的 USB root 列表（通常是 0 或 1 個）
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
     * 釋放所有資源：取消 coroutine、解除廣播、清除 listener。
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
            onDeviceStateChangedBlock = null
            onFileResultBlock = null
            onProgressBlock = null

            attachedDevices.clear()
            lastMountedPath = null
            currentRequest = null

            Log.d(TAG, "UsbReaderV 所有資源已釋放")
        } catch (e: Exception) {
            Log.e(TAG, "關閉 UsbReaderV 時發生錯誤：${e.localizedMessage}", e)
        }
    }

    // ---------------------- 內部：掃描 / 尋檔 ----------------------

    /**
     * 啟動時掃描是否已有掛載的 USB。
     */
    private fun scanExistingMount() {
        scope.launch(Dispatchers.IO) {
            val mount = UsbMountedStorageReader.findUsbMountPoint(context)
            if (mount != null) {
                val name = mount.absolutePath
                lastMountedPath = name
                if (attachedDevices.add(name)) {
                    Log.d(TAG, "scanExistingMount: 模擬 Attached → $name")
                    emitDeviceState(DeviceState.Attached(name))
                }
            } else {
                Log.d(TAG, "scanExistingMount: 目前沒有 USB 掛載點")
            }
        }
    }

    /**
     * 實際尋檔 + 複製 / 讀取
     */
    private suspend fun findFileInternal(
        mountRoot: File,
        request: FileRequest
    ) {
        if (isFinding) {
            Log.d(TAG, "findFileInternal: 已有尋找作業進行中，略過新的 request=$request")
            return
        }
        isFinding = true
        isCancelled = false

        val mountPath = mountRoot.absolutePath
        Log.d(TAG, "背景尋找檔案：${request.fileName}，mountRoot=$mountPath")

        try {
            val target = UsbMountedStorageReader.findFileOnUsbRecursive(
                mountRoot,
                request.fileName,
                maxDepth = 6
            )

            if (target == null) {
                Log.d(TAG, "找不到檔案：${request.fileName}（mount=$mountPath）")
                emitFileResult(FileResult.NotFound(request, mountPath))
                return
            }

            Log.d(TAG, "找到檔案：${target.absolutePath}，開始複製 / 讀取")
            emitProgress(Progress(request.fileName, 0L, target.length()))

            when (request.type) {
                FileType.JSON -> {
                    val bytes = UsbMountedStorageReader.readFileAsBytes(target)
                    val text = String(bytes)
                    emitFileResult(
                        FileResult.Success(
                            request = request,
                            mountPath = mountPath,
                            dataPath = null,
                            rawBytes = null,
                            jsonText = text
                        )
                    )
                }

                FileType.APK, FileType.MP4, FileType.IMAGE -> {
                    val path = UsbMountedStorageReader.copyFileToCache(context, target)
                    emitFileResult(
                        FileResult.Success(
                            request = request,
                            mountPath = mountPath,
                            dataPath = path,
                            rawBytes = null,
                            jsonText = null
                        )
                    )
                }

                FileType.BIN -> {
                    val bytes = UsbMountedStorageReader.readFileAsBytes(target)
                    emitFileResult(
                        FileResult.Success(
                            request = request,
                            mountPath = mountPath,
                            dataPath = null,
                            rawBytes = bytes,
                            jsonText = null
                        )
                    )
                }
            }

            emitProgress(Progress(request.fileName, target.length(), target.length()))
        } catch (e: IOException) {
            if (!isCancelled) {
                Log.e(TAG, "findFileInternal IOException：${e.localizedMessage}", e)
                emitFileResult(
                    FileResult.Failed(
                        request = request,
                        mountPath = mountPath,
                        error = UsbError.COPY_FILE_ERROR,
                        message = e.localizedMessage,
                        throwable = e
                    )
                )
            }
        } catch (e: Exception) {
            if (!isCancelled) {
                Log.e(TAG, "findFileInternal 其他錯誤：${e.localizedMessage}", e)
                emitFileResult(
                    FileResult.Failed(
                        request = request,
                        mountPath = mountPath,
                        error = UsbError.BACKGROUND_SEARCH_ERROR,
                        message = e.localizedMessage,
                        throwable = e
                    )
                )
            }
        } finally {
            isFinding = false
        }
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

    private fun handleUsbAttached() {
        scope.launch(Dispatchers.IO) {
            val maxWaitMs = 10_000L
            val intervalMs = 500L
            val start = System.currentTimeMillis()
            var mount: File? = null

            while (System.currentTimeMillis() - start < maxWaitMs) {
                mount = UsbMountedStorageReader.findUsbMountPoint(context)
                if (mount != null) {
                    Log.d(TAG, "ATTACHED: 等待中找到掛載點=${mount.absolutePath}")
                    break
                }
                Log.d(TAG, "ATTACHED: 尚未掛載完成，等 ${intervalMs}ms")
                delay(intervalMs)
            }

            if (mount != null) {
                val name = mount!!.absolutePath
                lastMountedPath = name
                if (attachedDevices.add(name)) {
                    emitDeviceState(DeviceState.Attached(name))

                    // 若有 auto request，這邊直接觸發尋檔
                    val req = currentRequest
                    if (req != null) {
                        findFileInternal(mount!!, req)
                        // 不清掉 req，讓呼叫端可以重複同個 request 時再用
                    }
                } else {
                    Log.d(TAG, "ATTACHED: 相同掛載點已處理過 → $name")
                }
            } else {
                Log.w(TAG, "ATTACHED: 在 $maxWaitMs ms 內沒偵測到掛載點")
            }
        }
    }

    private fun handleUsbDetached() {
        val name = lastMountedPath
        isCancelled = true
        lastMountedPath = null
        attachedDevices.clear()
        Log.d(TAG, "USB 已拔除：$name，停止任何下載作業")
        emitDeviceState(DeviceState.Detached(name))
    }

    // ---------------------- emit 工具 ----------------------

    private fun emitDeviceState(state: DeviceState) {
        scope.launch(Dispatchers.Main.immediate) {
            onDeviceStateChangedBlock?.invoke(state)
            listener?.onDeviceStateChanged(state)
        }
    }

    private fun emitFileResult(result: FileResult) {
        scope.launch(Dispatchers.Main.immediate) {
            onFileResultBlock?.invoke(result)
            listener?.onFileResult(result)
        }
    }

    private fun emitProgress(progress: Progress) {
        scope.launch(Dispatchers.Main.immediate) {
            onProgressBlock?.invoke(progress)
            listener?.onProgress(progress)
        }
    }
}
