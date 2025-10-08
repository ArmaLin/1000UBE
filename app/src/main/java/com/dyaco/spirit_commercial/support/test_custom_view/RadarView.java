package com.dyaco.spirit_commercial.support.test_custom_view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class RadarView extends View {
    private Path path;
    private Paint circlePaint;
    private Paint linePaint;
    private Paint textPaint;
    private int numRings = 5; // Number of rings in the radar
    private float radius; // Radius of the radar
    private float centerX, centerY; // Center coordinates

    public RadarView(Context context) {
        super(context);
        init();
    }

    public RadarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RadarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(Color.LTGRAY);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(2);

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(Color.BLACK);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(1);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(12);

        path = new Path();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = w / 2f;
        centerY = h / 2f;
        radius = Math.min(centerX, centerY) - 20; // Adjust padding
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw rings
        for (int i = 1; i <= numRings; i++) {
            float ringRadius = (i / (float) numRings) * radius;
            canvas.drawCircle(centerX, centerY, ringRadius, circlePaint);
        }

        // Draw lines (for example, 8 lines for a simple radar)
        for (int i = 0; i < 8; i++) {
            float angle = (float) (i * 45) * (float) Math.PI / 180;
            float startX = centerX + radius * (float) Math.cos(angle);
            float startY = centerY + radius * (float) Math.sin(angle);

            canvas.drawLine(centerX, centerY, startX, startY, linePaint);
        }

        // Draw radar scan (optional, for visual effect)
        path.moveTo(centerX, centerY);
        path.lineTo(centerX + radius, centerY);
        canvas.drawPath(path, linePaint);
    }
}