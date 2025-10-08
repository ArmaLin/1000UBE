package com.dyaco.spirit_commercial.support.test_custom_view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CircleGridView extends View {

    private int numRows = 10;
    private int numCols = 10;
    private float circleRadius = 50f;
    private float circleSpacing = 20f;
    private Paint defaultCirclePaint;
    private Paint connectedCirclePaint;
    private Paint linePaint;

    private List<PointF> circleCenters = new ArrayList<>();
    private Set<Integer> connectedCircleIndices = new HashSet<>(); // Track connected circles
    private List<PointF> connectedPoints = new ArrayList<>();

    public CircleGridView(Context context) {
        super(context);
        init();
    }

    public CircleGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        defaultCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        defaultCirclePaint.setColor(Color.BLUE);
        defaultCirclePaint.setStyle(Paint.Style.FILL);

        connectedCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        connectedCirclePaint.setColor(Color.RED); // Color of connected circles
        connectedCirclePaint.setStyle(Paint.Style.FILL);

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(Color.RED);
        linePaint.setStrokeWidth(8f);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        calculateCircleCenters();
    }

    private void calculateCircleCenters() {
        circleCenters.clear();
        float startX = (getWidth() - (numCols * (circleRadius * 2 + circleSpacing) - circleSpacing)) / 2f;
        float startY = (getHeight() - (numRows * (circleRadius * 2 + circleSpacing) - circleSpacing)) / 2f;

        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                float centerX = startX + col * (circleRadius * 2 + circleSpacing) + circleRadius;
                float centerY = startY + row * (circleRadius * 2 + circleSpacing) + circleRadius;
                circleCenters.add(new PointF(centerX, centerY));
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawCircles(canvas);
        drawConnections(canvas);
    }

    private void drawCircles(Canvas canvas) {
        for (int i = 0; i < circleCenters.size(); i++) {
            PointF center = circleCenters.get(i);
            // Use connectedCirclePaint if the circle is connected
            if (connectedCircleIndices.contains(i)) {
                canvas.drawCircle(center.x, center.y, circleRadius, connectedCirclePaint);
            } else {
                canvas.drawCircle(center.x, center.y, circleRadius, defaultCirclePaint);
            }
        }
    }

    private void drawConnections(Canvas canvas) {
        for (int i = 1; i < connectedPoints.size(); i++) {
            PointF startPoint = connectedPoints.get(i - 1);
            PointF endPoint = connectedPoints.get(i);
            canvas.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y, linePaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                connectedCircleIndices.clear();
                connectedPoints.clear();
                // Fall through to ACTION_MOVE
            case MotionEvent.ACTION_MOVE:
                PointF touchPoint = new PointF(event.getX(), event.getY());
                int connectedCircleIndex = getTouchedCircleIndex(touchPoint);
                if (connectedCircleIndex != -1) {
                    connectedCircleIndices.add(connectedCircleIndex);
                    connectedPoints.add(circleCenters.get(connectedCircleIndex));
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                // Handle end of touch (if needed)
                break;
        }
        return true;
    }

    private int getTouchedCircleIndex(PointF point) {
        for (int i = 0; i < circleCenters.size(); i++) {
            PointF center = circleCenters.get(i);
            float distance = (float) Math.sqrt(Math.pow(point.x - center.x, 2) +
                    Math.pow(point.y - center.y, 2));
            if (distance <= circleRadius) {
                return i;
            }
        }
        return -1; // No circle touched
    }
}