package com.dyaco.spirit_commercial.support

import android.content.Context
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.security.MessageDigest

/**
 * MD5 工具
 * - 取得目前 App 自己的 APK MD5
 * - 取得任意檔案（例如下載好的 update.apk）的 MD5
 *
 * ⚠ 這些方法都會做檔案 IO，不要在主執行緒呼叫
 */
object AppMd5Utils {

    /**
     * 取得「目前安裝的這個 App 自己」的 APK MD5
     * 主要給 debug / 自檢用
     */
    @JvmStatic
    fun getCurrentApkMd5(context: Context): String? {
        // /data/app/.../base.apk，App 自己可以讀，不需要 root
        val apkPath = context.applicationInfo.sourceDir
        return getFileMd5(apkPath)
    }

    /**
     * 依檔案路徑計算 MD5
     *
     * @param filePath 檔案完整路徑（例如：/sdcard/Download/update.apk）
     * @return 成功：32 字元 MD5（小寫）；失敗：null
     */
    @JvmStatic
    fun getFileMd5(filePath: String?): String? {
        if (filePath.isNullOrBlank()) return null
        return calculateFileMd5(File(filePath))
    }

    /**
     * 依 File 物件計算 MD5
     *
     * @param file 要計算 MD5 的檔案
     * @return 成功：32 字元 MD5（小寫）；失敗：null
     */
    @JvmStatic
    fun calculateFileMd5(file: File): String? {
        if (!file.exists() || !file.isFile) return null

        var fis: FileInputStream? = null
        return try {
            val digest = MessageDigest.getInstance("MD5")
            fis = FileInputStream(file)

            // 一次讀 8KB，避免吃太多記憶體
            val buffer = ByteArray(8 * 1024)
            var read: Int
            while (fis.read(buffer).also { read = it } != -1) {
                digest.update(buffer, 0, read)
            }

            val md5Bytes = digest.digest()
            buildString(md5Bytes.size * 2) {
                for (b in md5Bytes) {
                    append(String.format("%02x", b))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            try {
                fis?.close()
            } catch (_: IOException) {
            }
        }
    }
}
