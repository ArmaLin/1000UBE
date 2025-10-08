package com.dyaco.spirit_commercial.support.test_custom_view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class PieChartView extends View {

    private float[] values; // Values for each slice
    private int[] colors; // Colors for each slice
    private Paint paint;
    private RectF rectF;

    public PieChartView(Context context) {
        super(context);
        init();
    }

    public PieChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PieChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rectF = new RectF();

        float[] values = {45, 50, 5}; // Example values
        int[] colors = {Color.RED, Color.GREEN, Color.BLUE}; // Example colors
        setData(values, colors);
    }

    // Set the values and colors for the pie chart
    public void setData(float[] values, int[] colors) {
        this.values = values;
        this.colors = colors;
        invalidate(); // Force a redraw
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int radius = Math.min(centerX, centerY) - 20; // Adjust padding

        rectF.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);

        float startAngle = 0;
        for (int i = 0; i < values.length; i++) {
            paint.setColor(colors[i]);
            float sweepAngle = (values[i] / sumValues(values)) * 360;
            canvas.drawArc(rectF, startAngle, sweepAngle, true, paint);
            startAngle += sweepAngle;
        }
    }

    private float sumValues(float[] values) {
        float sum = 0;
        for (float value : values) {
            sum += value;
        }
        return sum;
    }
}