package com.dyaco.spirit_commercial.support.test_custom_view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResizableTriangleView extends View {
    private static final Logger log = LoggerFactory.getLogger(ResizableTriangleView.class);
    private Paint trianglePaint;
    private Path trianglePath;

    private PointF topVertex = new PointF(150, 50); // Initial vertex positions
    private PointF bottomLeftVertex = new PointF(50, 250);
    private PointF bottomRightVertex = new PointF(250, 250);

    private PointF touchStart = new PointF();
    private int movingVertexIndex = -1; // -1: not moving, 0: top, 1: bottom-left, 2: bottom-right

    public ResizableTriangleView(Context context) {
        super(context);
        init();
        Log.d(TAG, "ResizableTriangleView: 1");
    }

    public ResizableTriangleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        Log.d(TAG, "ResizableTriangleView: 2");
    }

    public ResizableTriangleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        Log.d(TAG, "ResizableTriangleView: 3");
    }

    private void init() {
        trianglePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        trianglePaint.setColor(Color.BLUE);
        trianglePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        trianglePaint.setStrokeWidth(5f);

        trianglePath = new Path();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Log.d(TAG, "onFinishInflate: ");
    }
    private static final String TAG = "ResizableTriangleView";

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(TAG, "onDraw: ");
        // Update triangle path
        trianglePath.reset();
        trianglePath.moveTo(topVertex.x, topVertex.y);
        trianglePath.lineTo(bottomLeftVertex.x, bottomLeftVertex.y);
        trianglePath.lineTo(bottomRightVertex.x, bottomRightVertex.y);
        trianglePath.close();

        canvas.drawPath(trianglePath, trianglePaint);
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d(TAG, "onMeasure: ");
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Log.d(TAG, "onLayout: ");
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d(TAG, "onSizeChanged: ");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchStart.set(event.getX(), event.getY());
                movingVertexIndex = getTouchedVertexIndex(touchStart);
                Log.d(TAG, "ACTION_DOWN: ");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "ACTION_MOVE: ");
                if (movingVertexIndex != -1) {
                    moveVertex(movingVertexIndex, event.getX(), event.getY());
                    invalidate(); // Redraw the view
                }
                break;

            case MotionEvent.ACTION_UP:
                Log.d(TAG, "ACTION_UP: ");
                movingVertexIndex = -1; // Stop moving the vertex
                break;
        }
        return true;
    }

    private int getTouchedVertexIndex(PointF touchPoint) {
        if (distance(touchPoint, topVertex) <= 50) {
            return 0; // Top vertex
        } else if (distance(touchPoint, bottomLeftVertex) <= 50) {
            return 1; // Bottom-left vertex
        } else if (distance(touchPoint, bottomRightVertex) <= 50) {
            return 2; // Bottom-right vertex
        }
        return -1; // No vertex touched
    }

    private void moveVertex(int index, float x, float y) {
        switch (index) {
            case 0:
                topVertex.set(x, y);
                break;
            case 1:
                bottomLeftVertex.set(x, y);
                break;
            case 2:
                bottomRightVertex.set(x, y);
                break;
        }
    }

    private float distance(PointF p1, PointF p2) {
        return (float) Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
    }



}