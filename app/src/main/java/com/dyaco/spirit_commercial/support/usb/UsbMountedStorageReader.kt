package com.dyaco.spirit_commercial.support.usb

import android.content.Context
import android.os.Build
import android.util.Log
import java.io.File
import java.io.IOException


object UsbMountedStorageReader {

    private const val TAG = "USB_MOUNT_READER"

    /**
     * 尋找 USB 掛載點
     * 可能出現在：
     * /storage/XXXX-XXXX
     * /mnt/media_rw/XXXX-XXXX
     */
    @JvmStatic
    fun findUsbMountPoint(): File? {
        val candidatePaths = arrayListOf<File>()

        // 尋找 /storage 下的 removable volume
        val storage = File("/storage")
        storage.listFiles()?.forEach { f ->
            val name = f.name
            if (f.isDirectory &&
                f.canRead() &&
                name.matches(Regex("[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}"))
            ) {
                candidatePaths.add(f)
            }
        }

        val mediaRw = File("/mnt/media_rw")
        mediaRw.listFiles()?.forEach { f ->
            val name = f.name
            if (f.isDirectory &&
                f.canRead() &&
                name.matches(Regex("[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}"))
            ) {
                candidatePaths.add(f)
            }
        }

        if (candidatePaths.isEmpty()) {
            Log.w(TAG, "找不到 USB 掛載點")
            return null
        }

        val sorted = candidatePaths.sortedBy { it.absolutePath.startsWith("/mnt/media_rw") }
        val mount = sorted.firstOrNull()

        Log.d(TAG, "找到 USB 掛載點：${mount?.absolutePath}")
        return mount
    }

    /**
     * 從 USB 掛載目錄找檔案
     */
    @JvmStatic
    fun findFileOnUsb(mountPoint: File, fileName: String): File? {
        val files = mountPoint.listFiles() ?: return null
        return files.firstOrNull { f ->
            f.isFile && f.name.equals(fileName, ignoreCase = true)
        }
    }

    /**
     * 複製檔案到 app cacheDir，並回傳路徑（給 APK, MP4, Image 使用）
     */
    @JvmStatic
    @Throws(IOException::class)
    fun copyFileToCache(context: Context, src: File): String {
        val target = File(context.cacheDir, src.name)
        src.inputStream().use { input ->
            target.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return target.absolutePath
    }

    /**
     * 複製檔案並回傳 ByteArray（給 BIN）
     */
    @JvmStatic
    @Throws(IOException::class)
    fun readFileAsBytes(src: File): ByteArray {
        return src.readBytes()
    }

    /**
     * 一次做完：尋找 + 讀取
     * Java 直接呼叫：
     *
     * UsbMountedStorageReader.readUsbFile(context, "update.bin", true)
     *
     * @param returnBytes = true → 回傳 ByteArray
     * @param returnBytes = false → 回傳複製後的 filePath
     */
    @JvmStatic
    @JvmOverloads
    fun readUsbFile(
        context: Context,
        fileName: String,
        returnBytes: Boolean = false
    ): Any? {
        val mount = findUsbMountPoint() ?: return null
        val target = findFileOnUsb(mount, fileName) ?: return null

        return try {
            if (returnBytes) {
                readFileAsBytes(target)
            } else {
                copyFileToCache(context, target)
            }
        } catch (e: Exception) {
            Log.e(TAG, "讀取 USB 發生錯誤：${e.localizedMessage}", e)
            null
        }
    }
}
