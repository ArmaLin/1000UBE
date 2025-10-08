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

import java.util.List;

public class EgymIntervalViewBySet extends View {

    // 進度設定：進度範圍 0 ~ maxProgress（maxProgress 為 durationTimesList 的 size）
    private int progress = 0;
    private int maxProgress = 0;

    // 使用 List<Integer> 決定各格的權重
    private List<Integer> durationTimesList;

    // 儲存各格的寬度（依比例計算），避免 onDraw 時反覆建立陣列
    private float[] mBarWidths;

    // 畫筆設定
    private final Paint paintBackground;
    private final Paint paintProgress;

    // 預先分配 RectF 物件，避免 onDraw 時重複 new
    private final RectF segmentRect = new RectF();
    private final RectF progressRect = new RectF();

    private final Context mContext;

    // 預設圓角半徑 12dp
    private final float cornerRadius;

    // 預先配置用於左、右圓角的 Path 物件
    private final Path mLeftPath = new Path();
    private final Path mRightPath = new Path();

    // 預先建立左右圓角用的 radii 陣列，避免 onDraw 時產生新物件
    private final float[] mLeftRadii;
    private final float[] mRightRadii;

    public EgymIntervalViewBySet(Context context) {
        this(context, null);
    }

    public EgymIntervalViewBySet(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        cornerRadius = dpToPx(12);

        // 初始化背景 Paint
        paintBackground = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintBackground.setColor(ContextCompat.getColor(mContext, R.color.color33ADB8C2)); // 淺灰色（約20%透明度）
        paintBackground.setStyle(Paint.Style.FILL);

        // 初始化進度 Paint
        paintProgress = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintProgress.setColor(Color.WHITE);
        paintProgress.setStyle(Paint.Style.FILL);

        // 預先建立左右圓角用的 radii 陣列
        mLeftRadii = new float[]{cornerRadius, cornerRadius, 0, 0, 0, 0, cornerRadius, cornerRadius};
        mRightRadii = new float[]{0, 0, cornerRadius, cornerRadius, cornerRadius, cornerRadius, 0, 0};
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.TRANSPARENT);

        float totalWidth = getWidth();
        float height = getHeight();

        if (durationTimesList == null || durationTimesList.isEmpty()) {
            return;
        }

        int segmentCount = durationTimesList.size();
        maxProgress = segmentCount;

        // 設定每格最小寬度：1dp
        float minSegmentWidth = dpToPx(1);
        // 預設間隔：6dp，但允許縮小到最小 1dp
        float desiredGapWidth = dpToPx(6);
        float minGapWidth = dpToPx(1);
        float gapWidth = desiredGapWidth;

        // 如果總寬度不足以滿足所有段至少 minSegmentWidth 時，
        // 則直接每格平分（gap 設為 0）
        if (totalWidth < segmentCount * minSegmentWidth) {
            gapWidth = 0;
            mBarWidths = new float[segmentCount];
            for (int i = 0; i < segmentCount; i++) {
                mBarWidths[i] = totalWidth / segmentCount;
            }
        } else {
            // 調整 gapWidth，若總寬度不足以同時容納所有段的最小寬度及預設間隔
            float totalMinNeeded = segmentCount * minSegmentWidth + (segmentCount - 1) * desiredGapWidth;
            if (totalWidth < totalMinNeeded) {
                gapWidth = (totalWidth - segmentCount * minSegmentWidth) / (segmentCount - 1);
                if (gapWidth < minGapWidth) {
                    gapWidth = minGapWidth;
                }
            }
            float availableWidth = totalWidth - (segmentCount - 1) * gapWidth;

            // 計算所有權重總和
            float totalWeight = 0;
            for (int i = 0; i < segmentCount; i++) {
                totalWeight += durationTimesList.get(i);
            }

            // 以迭代方式分配各格寬度，確保每格不低於 minSegmentWidth，
            // 同時盡量依比例分配剩餘寬度
            mBarWidths = new float[segmentCount];
            boolean[] fixed = new boolean[segmentCount];
            for (int i = 0; i < segmentCount; i++) {
                fixed[i] = false;
                mBarWidths[i] = 0;
            }
            float remainingWidth = availableWidth;
            float remainingWeight = totalWeight;

            boolean needIteration = true;
            while (needIteration) {
                needIteration = false;
                for (int i = 0; i < segmentCount; i++) {
                    if (!fixed[i]) {
                        float proposed = (durationTimesList.get(i) / remainingWeight) * remainingWidth;
                        if (proposed < minSegmentWidth) {
                            mBarWidths[i] = minSegmentWidth;
                            fixed[i] = true;
                            needIteration = true;
                            remainingWidth -= minSegmentWidth;
                            remainingWeight -= durationTimesList.get(i);
                        }
                    }
                }
            }
            // 將剩餘寬度依比例分配給尚未固定的段
            for (int i = 0; i < segmentCount; i++) {
                if (!fixed[i]) {
                    mBarWidths[i] = (durationTimesList.get(i) / remainingWeight) * remainingWidth;
                }
            }
        }

        // 開始依序繪製每個段的背景與進度
        float currentX = 0;
        float remainingProgress = progress; // 每個段視為 1 單位進度
        for (int i = 0; i < segmentCount; i++) {
            float segmentWidth = mBarWidths[i];
            // 設定當前段的矩形範圍
            segmentRect.set(currentX, 0, currentX + segmentWidth, height);
            boolean isFirstSegment = (i == 0);
            boolean isLastSegment = (i == segmentCount - 1);

            // 繪製背景
            if (isFirstSegment && isLastSegment) {
                canvas.drawRoundRect(segmentRect, cornerRadius, cornerRadius, paintBackground);
            } else if (isFirstSegment) {
                mLeftPath.reset();
                mLeftPath.addRoundRect(segmentRect, mLeftRadii, Path.Direction.CW);
                canvas.drawPath(mLeftPath, paintBackground);
            } else if (isLastSegment) {
                mRightPath.reset();
                mRightPath.addRoundRect(segmentRect, mRightRadii, Path.Direction.CW);
                canvas.drawPath(mRightPath, paintBackground);
            } else {
                canvas.drawRect(segmentRect, paintBackground);
            }

            // 繪製進度（每格視為 1 單位進度）
            if (remainingProgress > 0) {
                float segmentProgress = Math.min(remainingProgress, 1f);
                float progressEndX = currentX + segmentProgress * segmentWidth;
                progressRect.set(currentX, 0, progressEndX, height);

                if (isFirstSegment && isLastSegment) {
                    canvas.drawRoundRect(progressRect, cornerRadius, cornerRadius, paintProgress);
                } else if (isFirstSegment) {
                    mLeftPath.reset();
                    mLeftPath.addRoundRect(progressRect, mLeftRadii, Path.Direction.CW);
                    canvas.drawPath(mLeftPath, paintProgress);
                } else if (isLastSegment && progressEndX >= currentX + segmentWidth) {
                    mRightPath.reset();
                    mRightPath.addRoundRect(progressRect, mRightRadii, Path.Direction.CW);
                    canvas.drawPath(mRightPath, paintProgress);
                } else {
                    canvas.drawRect(progressRect, paintProgress);
                }
                remainingProgress -= segmentProgress;
            }

            currentX += segmentWidth;
            if (i < segmentCount - 1) {
                currentX += gapWidth;
            }
        }
    }

    // dp 轉換為 px（整數與浮點數版本）
    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    private float dpToPx(float dp) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    /**
     * 設定 durationTimesList：
     * 根據 List 的 size 決定格子數量，並依各值等比例分配每格寬度，
     * 同時將 maxProgress 設為 List 的 size（每格 1 單位進度）
     */
    public void setDurationTimesList(List<Integer> list) {
        this.durationTimesList = list;
        if (list != null) {
            maxProgress = list.size();
        } else {
            maxProgress = 0;
        }
        invalidate();
    }

    /**
     * 設定進度，範圍 0 ~ maxProgress（即 List 的 size）
     */
    public void setProgress(int progress) {
        if (durationTimesList != null && !durationTimesList.isEmpty()) {
            this.progress = Math.max(0, Math.min(maxProgress, progress));
        } else {
            this.progress = progress;
        }
        invalidate();
    }
}
