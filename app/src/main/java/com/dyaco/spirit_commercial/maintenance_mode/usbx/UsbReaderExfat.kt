package com.dyaco.spirit_commercial.maintenance_mode.usbx

import android.util.Log
import kotlinx.coroutines.*
import java.io.File

class UsbReaderExfat {

    interface MountCallback {
        @JvmSuppressWildcards
        fun onMountSuccess(mountPath: String, matchedFiles: List<File>)
        fun onMountFailed(errorMessage: String)
    }

    companion object {
        private const val TAG = "UsbReaderExfat"
        private const val MOUNT_POINT = "/mnt/usb"
        private const val MOUNT_TOOL = "/data/local/tmp/mount.exfat-fuse"
        private const val TIMEOUT_MS = 10000L
        private const val INTERVAL_MS = 1000L

        @JvmStatic
        fun waitAndMountUsb(
            nameContains: String,
            fileExtension: String,
            callback: MountCallback
        ) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    Runtime.getRuntime().exec(arrayOf("su", "-c", "mkdir -p $MOUNT_POINT")).waitFor()
                    delay(1000)

                    val startTime = System.currentTimeMillis()
                    var devicePath: String? = null

                    while (System.currentTimeMillis() - startTime < TIMEOUT_MS) {
                        val findCmd = "find /dev/block -type b | grep -E '/sd[a-z][0-9]*$'"
                        val process = Runtime.getRuntime().exec(arrayOf("su", "-c", findCmd))
                        val results = process.inputStream.bufferedReader().readLines()
                        if (results.isNotEmpty()) {
                            devicePath = results.first()
                            break
                        }
                        delay(INTERVAL_MS)
                    }

                    if (devicePath == null) {
                        withContext(Dispatchers.Main) {
                            callback.onMountFailed("未在 ${TIMEOUT_MS / 1000} 秒內找到 USB 裝置")
                        }
                        return@launch
                    }

                    val mountCmd = "$MOUNT_TOOL -o nonempty $devicePath $MOUNT_POINT"
                    Log.d(TAG, "掛載裝置：$mountCmd")

                    val mountProcess = Runtime.getRuntime().exec(arrayOf("su", "-c", mountCmd))
                    val errorOutput = mountProcess.errorStream.bufferedReader().readText()
                    val resultCode = mountProcess.waitFor()

                    if (resultCode != 0) {
                        withContext(Dispatchers.Main) {
                            callback.onMountFailed("掛載失敗：$devicePath\n代碼=$resultCode\n錯誤=$errorOutput")
                        }
                        return@launch
                    }

                    Log.d(TAG, "掛載成功，開始過濾檔案：關鍵字=$nameContains 副檔名=$fileExtension")

                    val matchedFiles = File(MOUNT_POINT).walkTopDown()
                        .filter { it.isFile && it.name.contains(nameContains, ignoreCase = true) && it.name.endsWith(fileExtension, ignoreCase = true) }
                        .toList()

                    withContext(Dispatchers.Main) {
                        callback.onMountSuccess(MOUNT_POINT, matchedFiles)
                    }

                } catch (e: Exception) {
                    Log.e(TAG, "掛載異常：${e.message}", e)
                    withContext(Dispatchers.Main) {
                        callback.onMountFailed("異常：${e.message}")
                    }
                }
            }
        }
    }
}
