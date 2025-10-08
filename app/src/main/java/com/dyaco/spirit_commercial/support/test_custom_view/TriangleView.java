package com.dyaco.spirit_commercial.support.test_custom_view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class TriangleView extends View {

    private Paint paint;
    private Path path;
    private int circleColor = Color.GREEN; // 設定圓形顏色

    public TriangleView(Context context) {
        super(context);
        init();
    }

    public TriangleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TriangleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(circleColor);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(5);

        path = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        // Define the triangle points (adjust coordinates as needed)
//        int x1 = getWidth() / 2;
//        int y1 = 0; // Top point
//        int x2 = 0;
//        int y2 = getHeight(); // Bottom left point
//        int x3 = getWidth();
//        int y3 = getHeight(); // Bottom right point
//
//        // Create the triangle path
//        path.moveTo(x1, y1);
//        path.lineTo(x2, y2);
//        path.lineTo(x3, y3);
//        path.close();

        // Calculate side length based on view size
        int sideLength = Math.min(getWidth(), getHeight());

        // Calculate points for an equilateral triangle
        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;
        float height = (float) (sideLength * Math.sqrt(3) / 2);
        float topY = centerY - height;

        // Create the triangle path
        path.moveTo(centerX, topY);
        path.lineTo(centerX - (sideLength / 2f), centerY);
        path.lineTo(centerX + (sideLength / 2f), centerY);
        path.close();

        // Draw the triangle
        canvas.drawPath(path, paint);
    }
}