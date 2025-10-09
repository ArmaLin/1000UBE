package com.dyaco.spirit_commercial.support.custom_view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.core.content.ContextCompat;

import com.dyaco.spirit_commercial.R;

public class CustomProgressView extends View {
    // 畫筆
    private Paint mPaint;
    // 每個圈圈的半徑（單位：像素）
    private final int mCircleRadius = 4;
    // 三個圈圈間的間距
    private int mCircleSpacing = 4;
    // 動畫進度參數，介於 0~1 之間
    private float mAnimationProgress = 0;
    // 動畫物件
    private ValueAnimator mAnimator;
    private final Context mContext;

    // 建構子
    public CustomProgressView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public CustomProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public CustomProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    // 初始化設定
    private void init() {
        // 關閉硬體加速，以確保陰影效果可以正常顯示
        setLayerType(LAYER_TYPE_SOFTWARE, null);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(ContextCompat.getColor(mContext, R.color.colorADB8C2));
        // 調整陰影參數：陰影半徑 16, 水平偏移 0, 垂直偏移 8, 陰影顏色使用深灰
        mPaint.setShadowLayer(8, 0, 8, Color.DKGRAY);

        // 建立一個從 0 到 1 的循環動畫，週期設定為 1000 毫秒
        mAnimator = ValueAnimator.ofFloat(0, 1);
        mAnimator.setDuration(1000);
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.addUpdateListener(animation -> {
            mAnimationProgress = (float) animation.getAnimatedValue();
            invalidate(); // 更新畫面
        });
       // mAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        // 計算三個圈圈所佔的總寬度
        int totalWidth = mCircleRadius * 2 * 3 + mCircleSpacing * 2;
        // 設定起始 X 座標，讓圈圈置中
        int startX = (width - totalWidth) / 2 + mCircleRadius;
        // 以畫面中線作為基準 Y 座標
        int baseY = height / 2;

        // 繪製三個圈圈，每個圈圈都有不同的延遲效果
        for (int i = 0; i < 3; i++) {
            // 設定各圈圈的相位偏移，這邊設定間隔為 0.2
            float phase = (mAnimationProgress + i * 0.2f) % 1;
            // 用正弦函數計算 Y 軸的位移，振幅為 30 像素
            float offsetY = (float)(Math.sin(phase * 2 * Math.PI) * 10);
            // 計算圈圈的中心座標
            float cx = startX + i * (mCircleRadius * 2 + mCircleSpacing);
            float cy = baseY - offsetY;
            // 繪製圈圈，陰影效果會自動套用
            canvas.drawCircle(cx, cy, mCircleRadius, mPaint);
        }
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        // 確保動畫在 View 可見時運行
        if (mAnimator != null && !mAnimator.isRunning()) {
            mAnimator.start();
        }
    }

    // 當 View 從視窗分離時會被呼叫 (這是最關鍵的修改)
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        // 如果動畫存在，則停止並清理它
        if (mAnimator != null) {
            // 停止動畫
            mAnimator.cancel();
            // 移除所有更新監聽器，徹底斷開引用鏈
            mAnimator.removeAllUpdateListeners();
        }
    }
}
