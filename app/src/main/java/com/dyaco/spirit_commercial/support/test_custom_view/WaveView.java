package com.dyaco.spirit_commercial.support.test_custom_view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class WaveView extends View {

    private Paint wavePaint;
    private Path wavePath;
    private Paint linePaint; // Paint for the grid lines

    private int waveColor = Color.BLUE;
    private float amplitude = 100f; // Wave height
    private float wavelength = 500f; // Distance between peaks
    private float waveShift = 0f; // For animation

    public WaveView(Context context) {
        super(context);
        init();
    }

    public WaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        wavePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        wavePaint.setColor(waveColor);
        wavePaint.setAntiAlias(true);
        wavePaint.setStyle(Paint.Style.FILL);

        wavePath = new Path();

        // Initialize linePaint for the grid
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(Color.RED);
        linePaint.setStrokeWidth(2f); // Set line thickness
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();


        // 2. Draw the wave
        drawWave(canvas, width, height);

        // 1. Draw the grid lines
        drawGridLines(canvas, width, height);
    }

    private void drawWave(Canvas canvas, int width, int height) {
        wavePath.reset();
        wavePath.moveTo(0, height / 2f);

        for (float x = 0; x <= width; x += wavelength / 10) {
            float y = (float) (amplitude * Math.sin(2 * Math.PI * (x + waveShift) / wavelength) + height / 2f);
            wavePath.lineTo(x, y);
        }

        wavePath.lineTo(width, height);
        wavePath.lineTo(0, height);
        wavePath.close();

        canvas.drawPath(wavePath, wavePaint);
    }

    private void drawGridLines(Canvas canvas, int width, int height) {
        // Draw vertical lines
        for (int i = 1; i <= 5; i++) {
            canvas.drawLine(i * width / 6f, 0, i * width / 6f, height, linePaint);
        }

        // Draw horizontal lines
        for (int i = 1; i <= 5; i++) {
            canvas.drawLine(0, i * height / 6f, width, i * height / 6f, linePaint);
        }
    }

    // Method to update wave shift for animation
    public void updateWaveShift(float shift) {
        waveShift += shift;
        invalidate();
    }
}