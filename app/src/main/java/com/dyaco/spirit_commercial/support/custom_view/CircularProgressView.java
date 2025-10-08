package com.dyaco.spirit_commercial.support.custom_view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.dyaco.spirit_commercial.R;

public class CircularProgressView extends View {
    
    private Paint backgroundPaint;
    private Paint progressPaint;
    private RectF rectF;
    private float progress = 50; // Current progress (0 to 100)
    private float strokeWidth = 50f;
    private int backgroundColor = 0xFFE0E0E0; // Gray background
    private int progressColor = 0xFFFF4081; // Pink progress color

    public CircularProgressView(Context context) {
        super(context);
        init(null);
    }

    public CircularProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CircularProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CircularProgressViewGG);
            progress = a.getFloat(R.styleable.CircularProgressViewGG_progressSS, progress);
            strokeWidth = a.getFloat(R.styleable.CircularProgressViewGG_strokeWidthSS, strokeWidth);
            backgroundColor = a.getColor(R.styleable.CircularProgressViewGG_backgroundColorSS, backgroundColor);
            progressColor = a.getColor(R.styleable.CircularProgressViewGG_progressColorSS, progressColor);
            a.recycle();
        }

        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(backgroundColor);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(strokeWidth);

        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setColor(progressColor);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(strokeWidth);
        
        rectF = new RectF();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int padding = (int) (strokeWidth / 2);
        rectF.set(padding, padding, w - padding, h - padding);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw background circle
        canvas.drawArc(rectF, 0, 360, false, backgroundPaint);

        // Draw progress arc
        float angle = (progress / 100) * 360;
        canvas.drawArc(rectF, -90, angle, false, progressPaint);
    }

    public void setProgress(float progress) {
        this.progress = progress;
        invalidate(); // Redraw the view
    }

    public float getProgress() {
        return progress;
    }
}
