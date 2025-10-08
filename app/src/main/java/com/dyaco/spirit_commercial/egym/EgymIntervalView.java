package com.dyaco.spirit_commercial.egym;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.dyaco.spirit_commercial.R;

import java.util.Arrays;

public class EgymIntervalView extends View {
    private static final int MAX_INDEX = 20; // 總索引範圍 0 ~ 19
    private int[] segmentIndexes = new int[]{0}; // 存放 segment 的索引位置（自動從 0 開始）
    private int progress = 0; // 進度條範圍 0 ~ 20
    private Paint paintBackground; // 背景 Paint
    private Paint paintProgress; // 進度 Paint
    private final int gapWidthPx; // 間隔寬度（硬編碼為 6dp）

    // ✅ **預先分配 RectF，減少 `onDraw()` 內存分配**
    private final RectF segmentRect = new RectF();
    private final RectF progressRect = new RectF();
    private final Context mContext;

    public EgymIntervalView(Context context) {
        super(context);
        gapWidthPx = dpToPx(6);
        mContext = context;
        init();
    }

    public EgymIntervalView(Context context, AttributeSet attrs) {
        super(context, attrs);
        gapWidthPx = dpToPx(6);
        mContext = context;
        init();
    }

    private void init() {
        // 初始化背景 Paint
        paintBackground = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintBackground.setColor(ContextCompat.getColor(mContext, R.color.color33ADB8C2)); // 淺灰色，透明度 20%
        paintBackground.setStyle(Paint.Style.FILL);

        // 初始化進度 Paint
        paintProgress = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintProgress.setColor(Color.WHITE);
        paintProgress.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float totalWidth = getWidth();
        float height = getHeight();
        float unitWidth = (totalWidth - ((segmentIndexes.length - 1) * gapWidthPx)) / MAX_INDEX;
        float cornerRadius = dpToPx(12); // ✅ 設定圓角半徑為 12dp

        float currentX = 0;
        int remainingProgress = progress;

        for (int i = 0; i < segmentIndexes.length; i++) {
            int startIndex = segmentIndexes[i];
            int endIndex = (i < segmentIndexes.length - 1) ? segmentIndexes[i + 1] : MAX_INDEX;

            float segmentWidth = (endIndex - startIndex) * unitWidth;

            boolean isFirstSegment = (i == 0);
            boolean isLastSegment = (i == segmentIndexes.length - 1 || endIndex == MAX_INDEX);

            // ✅ **繪製背景**
            segmentRect.set(currentX, 0, currentX + segmentWidth, height);

            if (isFirstSegment && isLastSegment) {
                // ✅ **只有 1 個 segment，兩邊都圓角**
                canvas.drawRoundRect(segmentRect, cornerRadius, cornerRadius, paintBackground);
            } else if (isFirstSegment) {
                // ✅ **第一個 segment（左邊圓角，右邊直角）**
                canvas.drawPath(createLeftRoundRectPath(segmentRect, cornerRadius), paintBackground);
            } else if (isLastSegment) {
                // ✅ **最後一個 segment（左邊直角，右邊圓角）**
                canvas.drawPath(createRightRoundRectPath(segmentRect, cornerRadius), paintBackground);
            } else {
                // ✅ **中間 segment（直角）**
                canvas.drawRect(segmentRect, paintBackground);
            }


            // ✅ **繪製進度條**
            if (remainingProgress > 0) {
                float progressEndX = Math.min(currentX + (remainingProgress * unitWidth), currentX + segmentWidth);
                progressRect.set(currentX, 0, progressEndX, height);

                boolean isFinalFullProgress = (progress == MAX_INDEX); // ✅ **確保滿格才變圓角**

                if (isFirstSegment && isLastSegment) {
                    // ✅ **只有 1 個 segment，進度條跟隨圓角**
                    canvas.drawRoundRect(progressRect, cornerRadius, cornerRadius, paintProgress);
                } else if (isFirstSegment) {
                    // ✅ **第一個 segment（左邊圓角）**
                    canvas.drawPath(createLeftRoundRectPath(progressRect, cornerRadius), paintProgress);
                } else if (isLastSegment && isFinalFullProgress) {  // ✅ **確保進度條全滿才圓角**
                    progressRect.right = totalWidth; // **確保畫面填滿**
                    canvas.drawPath(createRightRoundRectPath(progressRect, cornerRadius), paintProgress);
                } else {
                    // ✅ **中間 segment（直角）**
                    canvas.drawRect(progressRect, paintProgress);
                }

                int consumedProgress = Math.round((progressEndX - currentX) / unitWidth);
                remainingProgress = Math.max(0, remainingProgress - consumedProgress);
            }


            currentX += segmentWidth;

            // ✅ **透明間隔**
            if (i < segmentIndexes.length - 1) {
                currentX += gapWidthPx;
            }
        }
    }


    // ✅ **左邊圓角（右邊直角）**
    private Path createLeftRoundRectPath(RectF rect, float radius) {
        Path path = new Path();
        path.addRoundRect(rect, new float[]{radius, radius, 0, 0, 0, 0, radius, radius}, Path.Direction.CW);
        return path;
    }

    // ✅ **右邊圓角（左邊直角）**
    private Path createRightRoundRectPath(RectF rect, float radius) {
        Path path = new Path();
        path.addRoundRect(rect, new float[]{0, 0, radius, radius, radius, radius, 0, 0}, Path.Direction.CW);
        return path;
    }


    //new int[]{4,20} 會有兩個 Segment, 20 有沒有設定沒影響
//    public void setSegmentLengths(int[] indexes) {
//        Arrays.sort(indexes); // 確保索引排序
//        // **自動補上 0 作為起點**
//        int[] newIndexes = new int[indexes.length + 1];
//        newIndexes[0] = 0;
//        System.arraycopy(indexes, 0, newIndexes, 1, indexes.length);
//
//        this.segmentIndexes = newIndexes;
//        Log.d("EGEGEGEGEGE", "setSegmentLengths: " + Arrays.toString(segmentIndexes));
//        invalidate();
//    }

    public void setSegmentLengths(int[] indexes) {

        Arrays.sort(indexes); // 確保索引排序


        // 如果數組長度超過 20，則僅保留前 20 個最小值
        int validLength = Math.min(indexes.length, 20);
        int[] trimmedIndexes = Arrays.copyOf(indexes, validLength);

        // **自動補上 0 作為起點**
        int[] newIndexes = new int[validLength + 1];
        newIndexes[0] = 0;
        System.arraycopy(trimmedIndexes, 0, newIndexes, 1, validLength);

        this.segmentIndexes = newIndexes;
      //  Log.d("EGEGEGEGEGE", "setSegmentLengths: " + Arrays.toString(segmentIndexes));
        invalidate();
    }



    // ✅ **用 int 設定進度，範圍 0 ~ 20**
    public void setProgress(int progress) {
        this.progress = Math.max(0, Math.min(MAX_INDEX, progress)) + 1;
        invalidate();
    }

    // 將 dp 轉換為 px
    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
}
