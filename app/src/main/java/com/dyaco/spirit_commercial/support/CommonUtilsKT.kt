package com.dyaco.spirit_commercial.support

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.net.HttpURLConnection
import java.net.URL

object CommonUtilsKT {

    // 圖片下載邏輯（Flow）
    private fun downloadImageFlow(urlString: String): Flow<ByteArray> = flow {
        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection
        connection.doInput = true
        connection.connect()
        val bytes = connection.inputStream.use { it.readBytes() }
        emit(bytes)
    }.flowOn(Dispatchers.IO)

    /**
     * Java-friendly callback 封裝：固定回調在 MainThread
     */
    @JvmStatic
    fun downloadImage(
        urlString: String,
        callback: DownloadCallback
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            downloadImageFlow(urlString)
                .catch { error ->
                    withContext(Dispatchers.Main) {
                        callback.onError(error)
                    }
                }
                .collect { bytes ->
                    withContext(Dispatchers.Main) {
                        callback.onSuccess(bytes)
                    }
                }
        }
    }

    @JvmStatic
    fun wrapByteArray(bytes: ByteArray): Array<ByteArray> = arrayOf(bytes)

    interface DownloadCallback {
        fun onSuccess(data: ByteArray)
        fun onError(error: Throwable)
    }
}
