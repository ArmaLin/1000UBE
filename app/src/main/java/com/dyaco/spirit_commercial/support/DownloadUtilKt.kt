package com.dyaco.spirit_commercial.support

import android.content.Context
import android.net.NetworkCapabilities
import android.os.Handler
import android.os.Looper
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.SocketException
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * DownloadUtilKt 用於下載檔案、存檔並回報進度，並提供停止下載的方法。
 *
 * 修改重點：
 * 1. 在 companion object 的 downloadFile() 方法同時傳入 context
 * 2. 透過 context 取得 externalCacheDir 作為更新檔案儲存路徑
 * 3. 透過建構子將 fileExtension 與 updateFilePath 傳入實例，並建立下載檔案位置
 */
class DownloadUtilKt private constructor(fileExtension: String, private val updateFilePath: String) {

    companion object {
        private const val TAG = "UPDATE_APP"
        // 預設副檔名
        private const val DEFAULT_FILE_EXTENSION = "apk"
        // 暫存檔前置字串
        private const val TEMP_FILE_PREFIX = "Spirit_"
        // 檔案後置格式 (例如 .apk )
        private const val TEMP_FILE_SUFFIX_FORMAT = ".%s"

        /**
         * 由 url 自動判斷副檔名後，由 DownloadUtilKt 進行下載。
         *
         * 傳入參數包括：
         * @param context 取得下載檔案儲存路徑
         * @param url 下載的網址
         * @param listener 下載狀態回呼
         */
        @JvmStatic
        fun downloadFile(context: Context, url: String, listener: DownloadListener): DownloadUtilKt {
            val extension = extractFileExtension(url)
            Log.d(TAG, "APP 副檔名: $extension")
            // 透過 context 取得 externalCacheDir，若為 null 則使用 cacheDir
            val path = context.externalCacheDir?.absolutePath ?: context.cacheDir.absolutePath
            val util = DownloadUtilKt(extension, path)
            util.downloadFile(url, listener)
            return util
        }

        private fun extractFileExtension(url: String): String {
            val cleanUrl = url.substringBefore("?")
            val dotIndex = cleanUrl.lastIndexOf('.')
            return if (dotIndex != -1 && dotIndex < cleanUrl.length - 1) {
                cleanUrl.substring(dotIndex + 1)
            } else {
                DEFAULT_FILE_EXTENSION
            }
        }
    }

    // OkHttpClient 物件
    private val client: OkHttpClient by lazy { OkHttpClient.Builder().build() }

    // 保留 OkHttp Call 以供取消下載
    private var mCall: Call? = null

    // 使用 SupervisorJob 與私有 scope 集中管理 Coroutine
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    // 依據傳入的 updateFilePath 初始化暫存檔案，檔案路徑放在 updateFilePath 指定的資料夾下
    private val mFile: File = run {
        val dir = File(updateFilePath)
        dir.mkdirs()
        File.createTempFile(
            TEMP_FILE_PREFIX,
            String.format(Locale.getDefault(), TEMP_FILE_SUFFIX_FORMAT, fileExtension),
            dir
        )
    }

    interface DownloadListener {
        fun onStart()
        fun onProgress(progress: Int, current: Long, total: Long)
        fun onFinish(filePath: String)
        fun onFailure(reason: String)
    }

    /**
     * 利用 Coroutine 進行檔案下載：
     * 1. 開始下載前先自動刪除舊檔案並建立新檔案
     * 2. 以 suspendCancellableCoroutine 包裝 OkHttp 的非同步請求，支援取消
     * 3. 於 IO 執行下載並寫入檔案，同時計算進度與回報
     * 4. 所有 callback 均切換回主執行緒
     *
     * 此方法加上 @JvmSynthetic 以隱藏給 Java 呼叫，避免與 companion object 的 downloadFile() 衝突。
     */
    @JvmSynthetic
    fun downloadFile(url: String, downloadListener: DownloadListener) {
        // 開始下載前先自動刪除舊檔案（若有），並新建檔案
        if (mFile.exists()) {
            Log.d(TAG, "刪除檔案 成功")
            mFile.delete()
        }
        try {
            mFile.parentFile?.mkdirs()
            if (!mFile.createNewFile()) {
                runOnUiThread { downloadListener.onFailure("File creation failed: ${mFile.absolutePath}") }
                return
            }
        } catch (e: Exception) {
            runOnUiThread { downloadListener.onFailure("File creation failed: ${e.message}") }
            return
        }

        scope.launch {
            try {
                // 切換主執行緒呼叫 onStart
                withContext(Dispatchers.Main) { downloadListener.onStart() }
                val response = downloadResponse(url)
                if (response == null || !response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        downloadListener.onFailure("Download request failed: ${response?.code}")
                    }
                    return@launch
                }
                val body = response.body
                val totalLength = body.contentLength()
                var currentLength = 0L

                // 設定進度節流條件
                var lastProgress = -1
                var lastUpdateTime = System.currentTimeMillis()

                body.byteStream().use { input ->
                    FileOutputStream(mFile).use { output ->
                        val buffer = ByteArray(4096)
                        var len: Int
                        while (input.read(buffer).also { len = it } != -1) {
                            // 檢查協程取消
                            if (!isActive) throw CancellationException("Download cancelled")
                            output.write(buffer, 0, len)
                            currentLength += len
                            val progress = (100 * currentLength / totalLength).toInt()
                            // 每 100ms 或進度提升超過 1% 時更新一次
                            val now = System.currentTimeMillis()
                            if (progress != lastProgress && now - lastUpdateTime > 100) {
                                lastProgress = progress
                                lastUpdateTime = now
                                withContext(Dispatchers.Main) {
                                    downloadListener.onProgress(progress, currentLength, totalLength)
                                }
                            }
                        }
                    }
                }
                withContext(Dispatchers.Main) { downloadListener.onFinish(mFile.absolutePath) }
            } catch (e: Exception) {
                Log.d(TAG, "Exception: ${e.message}, $e")
                if (e is SocketException || e is CancellationException) {
                    //執行STOP 會跑 SocketException
                    Log.d(TAG, "取消操作，不回報 onFailure")
                    return@launch
                }
                val msg = e.message ?: "Unknown error"
                withContext(NonCancellable + Dispatchers.Main) { //NonCancellable 取消了也會執行 onFailure
                    downloadListener.onFailure("Download failed: $msg")
                }
            }

                //NonCancellable 取消了也會執行 onFailure
//            } catch (e: Exception) {
//                Log.d(TAG, "Exception: ${e.message}, $e")
//                if (e is CancellationException) {
//                    Log.d(TAG, "取消操作，不回報 onFailure")
//                    return@launch
//                }
//                val msg = e.message ?: "Unknown error"
//                withContext(NonCancellable + Dispatchers.Main) {
//                    downloadListener.onFailure("Download failed: $msg")
//                }
//            }

            //原始寫法
//            } catch (e: Exception) {
//                Log.d(TAG, "Exception: " + e.message +","+ e)
//                if (e is CancellationException) {
//                    Log.d(TAG, "取消操作，不回報 onFailure")
//                    return@launch
//                }
//                val msg = e.message ?: "Unknown error"
//                withContext(Dispatchers.Main) { downloadListener.onFailure("Download failed: $msg") }
//            }
        }
    }

    /**
     * 使用 suspendCancellableCoroutine 包裝 OkHttp 的非同步請求，支援 Coroutine 取消
     */
    private suspend fun downloadResponse(url: String): Response? =
        suspendCancellableCoroutine { cont ->
            val request = Request.Builder().url(url).build()
            val call = client.newCall(request)
            mCall = call
            cont.invokeOnCancellation { call.cancel() }
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    if (cont.isActive) cont.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (cont.isActive) cont.resume(response)
                }
            })
        }

    /**
     * 停止下載作業：取消 OkHttp Call 並取消所有由該 scope 啟動的子 Coroutine
     */
    fun stop() {
        mCall?.cancel()
        job.cancelChildren()
    }

    /**
     * 遞迴刪除指定資料夾下所有檔案與子目錄
     */
    fun deleteFile() {
        val dir = File(updateFilePath)
        recursionDeleteFile(dir)
        Log.d(TAG, "檔案已刪除")
    }

    // 將遞迴刪除方法簡化：先刪除子目錄再刪除自身，依據檔案或資料夾給出不同 log 訊息
    private fun recursionDeleteFile(file: File) {
        if (file.isDirectory) {
            file.listFiles()?.forEach { recursionDeleteFile(it) }
        }
        if (!file.delete()) {
            Log.w(
                TAG,
                if (file.isFile)
                    "刪除檔案失敗: ${file.absolutePath}"
                else
                    "刪除目錄失敗: ${file.absolutePath}"
            )
        }
    }

    /**
     * 根據網路頻寬、總位元組與目前下載的位元組數，
     * 回傳預估剩餘下載時間（分鐘），格式化至 1 位小數。
     */
    fun getDownloadTime(
        networkCapabilities: NetworkCapabilities,
        total: Long,
        current: Long
    ): String {
        val sizeMb = (total - current) / 1024.0 / 1024.0
        val downSpeedMb = networkCapabilities.linkDownstreamBandwidthKbps.toDouble() / 8 / 1024
        val minutes = (sizeMb / downSpeedMb) / 60
        return String.format(Locale.getDefault(), "%.1f", minutes)
    }

    /**
     * 將指定動作切換至主執行緒
     */
    private fun runOnUiThread(action: () -> Unit) {
        Handler(Looper.getMainLooper()).post { action() }
    }
}
