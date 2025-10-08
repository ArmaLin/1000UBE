package com.dyaco.spirit_commercial.support

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import kotlin.math.roundToInt

/**
 * SplashImageProcessor 負責讀取指定路徑的圖片，加上透明遮罩後存檔至 splashImagePathJPG，
 * 並提供成功與失敗的 callback（均於 UI thread 執行），讓 Java 與 Kotlin 均可呼叫使用。
 *
 * @param context             用來顯示 Toast 或存取其他資源的 Context。
 * @param splashImagePathJPG  處理完圖片儲存的完整路徑。
 */
class SplashImageProcessor(
    private val context: Context,
    private val splashImagePathJPG: String
) {
    /**
     * Callback 介面，供呼叫端實作成功與失敗後的處理。
     */
    interface OnProcessCallback {
        fun onSuccess()
        fun onFailure(errorMessage: String)
    }

    /**
     * 處理圖片：讀取 tempPath 指定的圖片，疊加指定透明度的黑色遮罩，
     * 並存檔到 splashImagePathJPG。
     *
     * 此方法在背景執行緒執行圖片處理，並確保 callback 皆在 UI thread 執行。
     *
     * @param tempPath 原始圖片檔案路徑。
     * @param newAlpha 透明度百分比 (0~100)。
     * @param callback 處理完成的 callback，包含成功與失敗結果（可為 null）。
     */
    @JvmOverloads
    fun processImage(tempPath: String, newAlpha: Int, callback: OnProcessCallback? = null) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // 檢查來源圖片檔案是否存在
                val file = File(tempPath)
                if (!file.exists()) {
                    throw FileNotFoundException("File not found: $tempPath")
                }
                // 解碼圖片
                val bmOptions = BitmapFactory.Options().apply {
                    inPreferredConfig = Bitmap.Config.ARGB_8888
                }
                var bitmap = BitmapFactory.decodeFile(file.absolutePath, bmOptions)
                    ?: throw IllegalStateException("Failed to decode image")
                // 確保 Bitmap 為 mutable
                if (!bitmap.isMutable) {
                    bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
                }
                // 處理圖片：加上透明遮罩並儲存
                addAlphaAndSave(bitmap, newAlpha)
                // 切換至 UI thread，進行成功 callback 與
                withContext(Dispatchers.Main) {
                    callback?.onSuccess()
                }
            } catch (e: Exception) {
                // 切換至 UI thread，進行失敗 callback
                withContext(Dispatchers.Main) {
                    val errorMsg = "Image processing failed: ${e.message}"
                    callback?.onFailure(errorMsg)
                }
            }
        }
    }

    /**
     * 在原始圖片上疊加一層黑色透明遮罩，並將處理後的圖片存檔到 splashImagePathJPG。
     *
     * @param originalBitmap 原始 Bitmap 物件。
     * @param newAlpha       透明度百分比 (0~100)。
     */
    private fun addAlphaAndSave(originalBitmap: Bitmap, newAlpha: Int) {
        val width = originalBitmap.width
        val height = originalBitmap.height
        val newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(newBitmap)
        // 繪製原始圖片
        canvas.drawBitmap(originalBitmap, 0f, 0f, null)
        // 計算透明度數值 (0 ~ 255)
        val alphaValue = (newAlpha / 100f * 255).roundToInt()
        val paint = Paint().apply {
            style = Paint.Style.FILL
            color = Color.BLACK
            alpha = alphaValue
        }
        // 畫上全尺寸黑色透明遮罩
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        // 儲存處理後的圖片（JPEG 格式，品質 90）
        try {
            FileOutputStream(splashImagePathJPG).use { fos ->
                newBitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos)
            }
        } catch (e: Exception) {
            throw RuntimeException("Unable to save image to $splashImagePathJPG", e)
        } finally {
            if (!newBitmap.isRecycled) {
                newBitmap.recycle()
            }
        }
    }
}
