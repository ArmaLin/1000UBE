package com.dyaco.spirit_commercial.support

import android.os.Environment
import android.util.Log
import kotlinx.coroutines.*
import java.io.File
import java.io.IOException
import java.util.regex.Pattern

class UsbFileCopierKt {

    interface CopyCallback {
        fun onFileCopied(fileName: String)
        fun onCompleted(successCount: Int, failCount: Int, durationMs: Long)
        fun onError(errorMessage: String)
    }

    companion object {
        private const val TAG = "UsbFileCopier"
        private const val MOUNT_POINT = "/mnt/usb"
        private val DEST_DIR = "${Environment.getExternalStorageDirectory().absolutePath}/CoreStar/Dyaco/Spirit"
        private val FILESYSTEMS = arrayOf("vfat", "exfat", "ntfs", "ext4")

        @JvmStatic
        fun copyRetailVideosFromUsb(callback: CopyCallback) {
            CoroutineScope(Dispatchers.IO).launch {
                val startTime = System.currentTimeMillis()
                var copied = 0
                var failed = 0

                try {
                    val usbBlock = findUsbDevice()
                    if (usbBlock == null) {
                        withContext(Dispatchers.Main) {
                            callback.onError("USB device not found.")
                        }
                        return@launch
                    }

                    Log.d(TAG, "Found USB device: $usbBlock")
                    val mounted = tryMountWithSupportedFs(usbBlock)
                    if (!mounted) {
                        withContext(Dispatchers.Main) {
                            callback.onError("Failed to mount USB device.")
                        }
                        return@launch
                    }

                    val destFolder = File(DEST_DIR)
                    if (!destFolder.exists()) destFolder.mkdirs()

                    val pattern = Pattern.compile(".*retail_\\d{2}\\.mp4$", Pattern.CASE_INSENSITIVE)
                    val matchedFiles = mutableListOf<String>()

                    val process = Runtime.getRuntime().exec(arrayOf("su", "-c", "find $MOUNT_POINT -type f"))
                    process.inputStream.bufferedReader().useLines { lines ->
                        lines.forEach { line ->
                            val fileName = File(line).name
                            if (pattern.matcher(fileName).matches()
                                && !line.contains("/.Trashes/")
                                && !fileName.startsWith("._")
                            ) {
                                matchedFiles.add(line)
                                Log.d(TAG, "Matched file: $line")
                            } else {
                                Log.d(TAG, "Skipped: $line")
                            }
                        }
                    }
                    process.waitFor()

                    Log.d(TAG, "Total matched files: ${matchedFiles.size}")

                    matchedFiles.forEach { src ->
                        val fileName = File(src).name
                        val dst = "$DEST_DIR/$fileName"

                        try {
                            val cmd = "cp \"$src\" \"$dst\""
                            val copyProcess = Runtime.getRuntime().exec(arrayOf("su", "-c", cmd))
                            val result = copyProcess.waitFor()
                            if (result == 0) {
                                copied++
                                withContext(Dispatchers.Main) {
                                    callback.onFileCopied(fileName)
                                }
                            } else {
                                throw IOException("cp failed, code=$result")
                            }
                        } catch (e: Exception) {
                            failed++
                            Log.e(TAG, "Failed to copy: $fileName → ${e.message}")
                        }
                    }

                    val duration = System.currentTimeMillis() - startTime
                    Log.i(TAG, "Copy finished. Success: $copied, Failed: $failed, Duration: ${duration}ms")

                    withContext(Dispatchers.Main) {
                        callback.onCompleted(copied, failed, duration)
                    }

                } catch (e: Exception) {
                    Log.e(TAG, "Error during copy process: ${e.message}", e)
                    withContext(Dispatchers.Main) {
                        callback.onError("Unexpected error: ${e.message}")
                    }
                }
            }
        }

        private fun tryMountWithSupportedFs(blockDevice: String): Boolean {
            for (fs in FILESYSTEMS) {
                val cmd = "mkdir -p $MOUNT_POINT && mount -o rw -t $fs $blockDevice $MOUNT_POINT"
                val process = Runtime.getRuntime().exec(arrayOf("su", "-c", cmd))
                val result = process.waitFor()
                if (result == 0) {
                    Log.d(TAG, "Mounted with fs: $fs")
                    return true
                } else {
                    Log.w(TAG, "Mount failed for fs: $fs")
                }
            }
            return false
        }

        private fun findUsbDevice(): String? {
            val blockDir = File("/dev/block")
            val blocks = blockDir.listFiles() ?: return null

            for (file in blocks) {
                val name = file.name
                if (name.startsWith("sd")) {
                    Log.d(TAG, "Found block: ${file.absolutePath}")
                }
                if (name.matches(Regex("^sd[a-z][0-9]*$"))) {
                    Log.d(TAG, "Confirmed USB block: $name")
                    return file.absolutePath
                }
            }

            return null
        }
    }
}
