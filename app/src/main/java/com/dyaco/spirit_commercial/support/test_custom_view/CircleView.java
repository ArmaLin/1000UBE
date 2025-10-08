package com.dyaco.spirit_commercial.support.test_custom_view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class CircleView extends View {

    private Paint circlePaint;
    private int circleColor = Color.YELLOW; // 設定圓形顏色
    private int circleRadius = 200; // 設定圓形半徑

    public CircleView(Context context) {
        super(context);
        init();
    }

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(circleColor);
        circlePaint.setStyle(Paint.Style.FILL); // 設定填充模式
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 計算圓心座標
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        // 繪製圓形
        canvas.drawCircle(centerX, centerY, circleRadius, circlePaint);
    }

    // 設定圓形顏色
    public void setCircleColor(int color) {
        circleColor = color;
        circlePaint.setColor(circleColor);
        invalidate(); // 重新繪製
    }

    // 設定圓形半徑
    public void setCircleRadius(int radius) {
        circleRadius = radius;
        invalidate(); // 重新繪製
    }
}