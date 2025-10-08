package com.dyaco.spirit_commercial.support.custom_view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class CustomFlagView extends View {

    private Paint paint;
    private static final int NUM_STRIPES = 13;
    private static final int NUM_ROWS = 9;
    private static final int NUM_COLUMNS = 11;
    private static final int NUM_STARS = 50;

    public CustomFlagView(Context context) {
        super(context);
        init();
    }

    public CustomFlagView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Get View dimensions
        int width = getWidth();
        int height = getHeight();

        // Draw the stripes
        drawStripes(canvas, width, height);

        // Draw the blue rectangle
        float unionHeight = height * 7 / 13f;
        float unionWidth = width * 2 / 5f;
        drawUnion(canvas, unionWidth, unionHeight);

        // Draw the stars
        drawStars(canvas, unionWidth, unionHeight);
    }

    private void drawStripes(Canvas canvas, int width, int height) {
        float stripeHeight = height / (float) NUM_STRIPES;

        for (int i = 0; i < NUM_STRIPES; i++) {
            if (i % 2 == 0) {
                paint.setColor(Color.RED);
            } else {
                paint.setColor(Color.WHITE);
            }
            canvas.drawRect(0, i * stripeHeight, width, (i + 1) * stripeHeight, paint);
        }
    }

    private void drawUnion(Canvas canvas, float unionWidth, float unionHeight) {
        paint.setColor(Color.BLUE);
        canvas.drawRect(0, 0, unionWidth, unionHeight, paint);
    }

    private void drawStars(Canvas canvas, float unionWidth, float unionHeight) {
        paint.setColor(Color.WHITE);
        float offsetX = unionWidth / NUM_COLUMNS;
        float offsetY = unionHeight / NUM_ROWS;
        float starRadius = offsetX / 3f;  // Adjust the star size

        for (int row = 0; row < NUM_ROWS; row++) {
            for (int col = 0; col < NUM_COLUMNS; col++) {
                if (shouldDrawStar(row, col)) {
                    float cx = (col + 0.5f) * offsetX;
                    float cy = (row + 0.5f) * offsetY;
                    drawStar(canvas, cx, cy, starRadius);
                }
            }
        }
    }

    private boolean shouldDrawStar(int row, int col) {
        // Only draw stars in staggered pattern (5 stars in even rows, 6 in odd)
        return (row % 2 == 0 && col % 2 == 0) || (row % 2 == 1 && col % 2 == 1);
    }

    private void drawStar(Canvas canvas, float cx, float cy, float radius) {
        Path path = new Path();
        double angle = Math.PI / 5; // 36 degrees

        for (int i = 0; i < 10; i++) {
            double r = (i % 2 == 0) ? radius : radius / 2;
            float x = (float) (cx + r * Math.sin(i * angle));
            float y = (float) (cy - r * Math.cos(i * angle));
            if (i == 0) {
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }
        }

        path.close();
        canvas.drawPath(path, paint);
    }
}
