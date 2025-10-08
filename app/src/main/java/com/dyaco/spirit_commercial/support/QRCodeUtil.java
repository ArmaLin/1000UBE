package com.dyaco.spirit_commercial.support;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.dyaco.spirit_commercial.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.HashMap;
import java.util.Map;

public class QRCodeUtil {
    private static final String TAG = "QRCodeUtil";

    // 原本的 logo 版本方法 (不變)
    public static Bitmap generateQRCodeWithLogo(Context context, String content, int logoResId) {
        int defaultSize = 600;
        return generateQRCodeWithLogo(context, content, defaultSize, logoResId);
    }

    public static Bitmap generateQRCodeWithText(Context context, String content, String logoResId) {
        int defaultSize = 600;
        return generateQRCodeWithCenterText(context, content, defaultSize, logoResId);
    }

    /**
     * 透過 resource id 取得 Bitmap（支援向量圖）
     */
    private static Bitmap getBitmapFromDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (drawable == null) {
            Log.e(TAG, "getBitmapFromDrawable: 無法取得 Drawable, resId = " + drawableId);
            return null;
        }
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        if (width <= 0 || height <= 0) {
            width = 100;
            height = 100;
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 判斷在未縮放的 QR Code 模組座標中，該點是否屬於 Finder Pattern 區域（左上、右上、左下）。
     * Finder Pattern 標準大小為 7 x 7 模組。
     */
    private static boolean inFinderPattern(int x, int y, int matrixSize) {
        // 左上
        if (x < 7 && y < 7) return true;
        // 右上
        if (x >= matrixSize - 7 && y < 7) return true;
        // 左下
        return x < 7 && y >= matrixSize - 7;
    }

    /**
     * 產生帶有 Logo 的 QR Code，同時將左上、右上、左下三個 Finder Pattern 模組顏色改成
     * 從 R.color.color1396ef 取得的自訂顏色。
     *
     * @param context   用來取得資源
     * @param content   QR Code 內容
     * @param size      輸出 QR Code 邊長（像素）
     * @param logoResId Logo 資源 id（例如 R.drawable.your_logo）
     * @return 生成完成的 QR Code Bitmap
     */
    public static Bitmap generateQRCodeWithLogo(Context context, String content, int size, int logoResId) {
        Log.d(TAG, "開始生成 QR Code, content: " + content + ", 輸出尺寸: " + size);
        try {
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.MARGIN, 2); // 留少量白框

            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix unscaledMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 0, 0, hints);
            int unscaledWidth = unscaledMatrix.getWidth();
            int multiple = size / unscaledWidth;
            int outputSize = unscaledWidth * multiple;
            Log.d(TAG, "未縮放矩陣尺寸: " + unscaledWidth + "，縮放倍率: " + multiple + ", 輸出圖尺寸: " + outputSize);

            // 使用像素陣列一次性填入所有像素，取代逐一 drawRect()
            int[] pixels = new int[outputSize * outputSize];
            int customColor = ContextCompat.getColor(context, R.color.color1396ef);
            for (int y = 0; y < unscaledWidth; y++) {
                for (int x = 0; x < unscaledWidth; x++) {
                    boolean module = unscaledMatrix.get(x, y);
                    int color = module ? Color.BLACK : Color.WHITE;
                    if (module && inFinderPattern(x, y, unscaledWidth)) {
                        color = customColor;
                    }
                    for (int dy = 0; dy < multiple; dy++) {
                        for (int dx = 0; dx < multiple; dx++) {
                            int posX = x * multiple + dx;
                            int posY = y * multiple + dy;
                            pixels[posY * outputSize + posX] = color;
                        }
                    }
                }
            }

            Bitmap qrBitmap = Bitmap.createBitmap(outputSize, outputSize, Bitmap.Config.ARGB_8888);
            qrBitmap.setPixels(pixels, 0, outputSize, 0, 0, outputSize, outputSize);
            Log.d(TAG, "QR Code 模組繪製完成");

            // 加入 Logo 圖片
            Bitmap logoBitmap = getBitmapFromDrawable(context, logoResId);
            if (logoBitmap != null) {
                Canvas canvas = new Canvas(qrBitmap);
                int logoSize = outputSize / 5; // 預設 Logo 大小為 QR Code 的 20%
                Bitmap scaledLogo = Bitmap.createScaledBitmap(logoBitmap, logoSize, logoSize, false);
                Paint paintRect = new Paint();
                paintRect.setStyle(Paint.Style.FILL);
                paintRect.setColor(Color.WHITE);
                int logoLeft = (outputSize - logoSize) / 2;
                int logoTop = (outputSize - logoSize) / 2;
                canvas.drawRect(logoLeft, logoTop, logoLeft + logoSize, logoTop + logoSize, paintRect);
                canvas.drawBitmap(scaledLogo, logoLeft, logoTop, null);
                Log.d(TAG, "Logo 繪製完成");
            } else {
                Log.e(TAG, "無法取得 Logo Bitmap, resId: " + logoResId);
            }

            Log.d(TAG, "QR Code with logo 生成完成");
            return qrBitmap;
        } catch (WriterException e) {
            Log.e(TAG, "生成 QR Code 時發生錯誤", e);
            return null;
        }
    }

    /**
     * 新增一個方法，傳入 centerText 字串，在 QR Code 中間顯示文字，而非 Logo 圖檔
     *
     * @param context    用來取得資源
     * @param content    QR Code 內容
     * @param size       輸出 QR Code 邊長（像素）
     * @param centerText 要顯示在 QR Code 中間的文字
     * @return 生成完成的 QR Code Bitmap
     */
    public static Bitmap generateQRCodeWithCenterText(Context context, String content, int size, String centerText) {
        Log.d(TAG, "開始生成 QR Code with center text, content: " + content + ", 輸出尺寸: " + size + ", centerText: " + centerText);
        try {
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.MARGIN, 2); // 留少量白框

            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix unscaledMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 0, 0, hints);
            int unscaledWidth = unscaledMatrix.getWidth();
            int multiple = size / unscaledWidth;
            int outputSize = unscaledWidth * multiple;
            Log.d(TAG, "未縮放矩陣尺寸: " + unscaledWidth + "，縮放倍率: " + multiple + ", 輸出圖尺寸: " + outputSize);

            int[] pixels = new int[outputSize * outputSize];
            int customColor = ContextCompat.getColor(context, R.color.color1396ef);
            for (int y = 0; y < unscaledWidth; y++) {
                for (int x = 0; x < unscaledWidth; x++) {
                    boolean module = unscaledMatrix.get(x, y);
                    int color = module ? Color.BLACK : Color.WHITE;
                    if (module && inFinderPattern(x, y, unscaledWidth)) {
                        color = customColor;
                    }
                    for (int dy = 0; dy < multiple; dy++) {
                        for (int dx = 0; dx < multiple; dx++) {
                            int posX = x * multiple + dx;
                            int posY = y * multiple + dy;
                            pixels[posY * outputSize + posX] = color;
                        }
                    }
                }
            }

            Bitmap qrBitmap = Bitmap.createBitmap(outputSize, outputSize, Bitmap.Config.ARGB_8888);
            qrBitmap.setPixels(pixels, 0, outputSize, 0, 0, outputSize, outputSize);
            Log.d(TAG, "QR Code 模組繪製完成");

            // 設定文字樣式並在中間加入文字
            Canvas canvas = new Canvas(qrBitmap);
            Paint textPaint = new Paint();
            textPaint.setColor(Color.BLACK);
            textPaint.setTextAlign(Paint.Align.CENTER);
            textPaint.setAntiAlias(true);
            textPaint.setTextSize(size / 15f);

            float textWidth = textPaint.measureText(centerText);
            Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
            float textHeight = fontMetrics.descent - fontMetrics.ascent;
            float paddingX = textWidth * 0.2f;
            float paddingY = textHeight * 0.2f;
            float boxWidth = textWidth + 2 * paddingX;
            float boxHeight = textHeight + 2 * paddingY;
            float boxLeft = (outputSize - boxWidth) / 2;
            float boxTop = (outputSize - boxHeight) / 2;

            Paint rectPaint = new Paint();
            rectPaint.setStyle(Paint.Style.FILL);
            rectPaint.setColor(Color.WHITE);
            canvas.drawRect(boxLeft, boxTop, boxLeft + boxWidth, boxTop + boxHeight, rectPaint);

            float textBaseline = boxTop + paddingY - fontMetrics.ascent;
            canvas.drawText(centerText, outputSize / 2f, textBaseline, textPaint);

            Log.d(TAG, "QR Code with center text 生成完成");
            return qrBitmap;
        } catch (WriterException e) {
            Log.e(TAG, "生成 QR Code with center text 時發生錯誤", e);
            return null;
        }
    }
}
