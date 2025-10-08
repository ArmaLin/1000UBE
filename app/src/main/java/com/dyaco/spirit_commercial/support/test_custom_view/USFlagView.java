package com.dyaco.spirit_commercial.support.test_custom_view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class USFlagView extends View {

    private Paint redPaint;
    private Paint whitePaint;
    private Paint bluePaint;
    private Paint starPaint;

    private RectF unionRect; // The blue rectangle
    private RectF[] stripeRects; // The red and white stripes

    private float starSize;
    private float stripeHeight;

    public USFlagView(Context context) {
        super(context);
        init();
    }

    public USFlagView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public USFlagView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        redPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        redPaint.setColor(Color.rgb(179, 25, 66)); // Official "Old Glory Red"
        redPaint.setStyle(Paint.Style.FILL);

        whitePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        whitePaint.setColor(Color.WHITE);
        whitePaint.setStyle(Paint.Style.FILL);

        bluePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bluePaint.setColor(Color.rgb(10, 49, 97));  // Official "Old Glory Blue"
        bluePaint.setStyle(Paint.Style.FILL);

        starPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        starPaint.setColor(Color.WHITE);
        starPaint.setStyle(Paint.Style.FILL);


        stripeRects = new RectF[13]; // 13 stripes
        for (int i = 0; i < 13; i++) {
            stripeRects[i] = new RectF();
        }
        unionRect = new RectF();

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // Official proportions:  https://www.law.cornell.edu/uscode/text/4/1
        float flagWidth = w;
        float flagHeight = h;

        // Ensure correct aspect ratio (1.9:1), adjust width or height as needed
        if (flagWidth / flagHeight > 1.9f) {
             flagWidth = flagHeight * 1.9f;
        } else {
            flagHeight = flagWidth / 1.9f;
        }


        // Calculate stripe height (Flag Height / 13)
        stripeHeight = flagHeight / 13f;

        // Set stripe rectangles
        for (int i = 0; i < 13; i++) {
            stripeRects[i].set(0, i * stripeHeight, flagWidth, (i + 1) * stripeHeight);
        }


        // Calculate Union (blue area) dimensions.  Official specs:
        // A.  height = 7/13 of flag height (7 stripes high)
        // B.  width = 2/5 of flag width (0.4 * flag width)
        float unionHeight = 7f * stripeHeight;
        float unionWidth = 0.4f * flagWidth;
        unionRect.set(0, 0, unionWidth, unionHeight);

        // Calculate star size and spacing.  Official spec:
        // star diameter (K) = 0.0616 * flag height  (when flag height = 1.0)
        // We also need to calculate the spacing between stars (E, F, G, H).
        starSize = 0.0616f * flagHeight;
        float starSpacingHoriz = 0.063f * flagWidth; // E and G
        float starSpacingVert = 0.054f * flagHeight; // F and H

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw stripes
        for (int i = 0; i < 13; i++) {
            canvas.drawRect(stripeRects[i], (i % 2 == 0) ? redPaint : whitePaint);
        }

        // Draw union
        canvas.drawRect(unionRect, bluePaint);

        // Draw stars.  This is the tricky part - nested loops to place the stars.
        float starXOffset = unionRect.width() * 0.063f; // Initial horizontal offset
        float starYOffset = unionRect.height() * 0.054f; // Initial vertical offset

        // Calculate horizontal (E, G) and vertical (F, H) spacing
        float starSpacingHoriz = unionRect.width() * 0.063f;
        float starSpacingVert = unionRect.height() * 0.054f;

       for (int row = 0; row < 9; row++) {
            for (int col = 0; col < ((row % 2 == 0) ? 6 : 5); col++) { // 6 stars on even rows, 5 on odd rows
                 float x = starXOffset + col * (2 * starSpacingHoriz) + (row % 2 == 0 ? 0 : starSpacingHoriz);
                 float y = starYOffset + row * starSpacingVert;

                drawStar(canvas, x, y, starSize / 2f);  // Pass star radius
            }
        }
    }

    // Helper function to draw a single 5-pointed star
    private void drawStar(Canvas canvas, float x, float y, float radius) {
        float innerRadius = radius * 0.382f; // Approximate inner radius for a 5-pointed star
        float startAngle = (float) (-Math.PI / 2); // Start at the top point, facing up

        float[] points = new float[20]; // 10 points (x, y pairs)

        for (int i = 0; i < 5; i++) {
            // Outer point
            float outerAngle = startAngle + (i * 2 * (float) Math.PI) / 5;
            points[i * 4] = x + radius * (float) Math.cos(outerAngle);
            points[i * 4 + 1] = y + radius * (float) Math.sin(outerAngle);

            // Inner point
            float innerAngle = outerAngle + (float) Math.PI / 5;
            points[i * 4 + 2] = x + innerRadius * (float) Math.cos(innerAngle);
            points[i * 4 + 3] = y + innerRadius * (float) Math.sin(innerAngle);
        }
        canvas.drawVertices(Canvas.VertexMode.TRIANGLE_FAN, 20, points, 0, null, 0, null, 0, null, 0, 0, starPaint);
    }


    // Optional:  Add a method to set a custom aspect ratio, if needed
    public void setAspectRatio(float aspectRatio) {
         //  You would need to recalculate dimensions in onSizeChanged based on this ratio.
         //  For simplicity, I'm sticking to the official 1.9:1 ratio.
        invalidate(); // Trigger a redraw
    }
}