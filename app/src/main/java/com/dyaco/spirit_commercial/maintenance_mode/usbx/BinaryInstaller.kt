package com.dyaco.spirit_commercial.maintenance_mode.usbx

import android.content.Context
import android.util.Log
import kotlinx.coroutines.*
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream

/**
 * 安裝 Exfat-Fuse 的二進位檔
 */
object BinaryInstaller {

    private const val TAG = "USB_UPDATE"
    private const val ASSET_NAME = "mount.exfat-fuse"
    private const val TARGET_PATH = "/data/local/tmp/$ASSET_NAME"

    @JvmStatic
    fun installBinaryIfNeeded(
        context: Context,
        callback: InstallCallback
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val targetFile = File(TARGET_PATH)
                if (targetFile.exists()) {
                    Timber.tag(TAG).d("Binary 已存在，跳過複製")
                    withContext(Dispatchers.Main) {
                        callback.onInstallResult(true)
                    }
                    return@launch
                }

                Timber.tag(TAG).d("Binary 不存在，開始複製")
                val tempFile = File.createTempFile("tmp_", null, context.cacheDir)

                context.assets.open(ASSET_NAME).use { input ->
                    FileOutputStream(tempFile).use { output ->
                        input.copyTo(output)
                    }
                }

                val copyCmd = "cp ${tempFile.absolutePath} $TARGET_PATH"
                val chmodCmd = "chmod 755 $TARGET_PATH"

                Runtime.getRuntime().exec(arrayOf("su", "-c", copyCmd)).waitFor()
                Runtime.getRuntime().exec(arrayOf("su", "-c", chmodCmd)).waitFor()

                Timber.tag(TAG).d("Binary 安裝完成")
                withContext(Dispatchers.Main) {
                    callback.onInstallResult(true)
                }

            } catch (e: Exception) {
                Timber.tag(TAG).e(e, "Binary 安裝失敗：${e.message}")
                withContext(Dispatchers.Main) {
                    callback.onInstallResult(false)
                }
            }
        }
    }

    interface InstallCallback {
        fun onInstallResult(success: Boolean)
    }
}
