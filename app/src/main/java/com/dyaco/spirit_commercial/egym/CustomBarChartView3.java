package com.dyaco.spirit_commercial.egym;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.dyaco.spirit_commercial.R;

public class CustomBarChartView3 extends View {
    private Context context;
    private Paint speedPaint;
    private Paint inclinePaint;
    private Paint heartRatePaint;
    private Paint backgroundPaint;
    private Paint linePaint; // 黑線畫筆

    private float[] speedValues = {}; // 藍色部分高度
    private float[] inclineValues = {}; // 紫色部分高度
    private float[] heartRateValues = {}; // 橘色部分高度

    private float[] barWidths = {}; // 每個 bar 的寬度比例（可動態設定）

    private float lineWidth = 4f; // 黑色分隔線的寬度

    public CustomBarChartView3(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public CustomBarChartView3(Context context) {
        super(context);
        this.context = context;
        init();
    }

    private void init() {
        // 初始化顏色
        speedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        speedPaint.setColor(ContextCompat.getColor(context, R.color.color_level_speed_bar_run)); // 藍色 (Speed)

//        speedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        // 初始設為漸變色的 Shader，稍後在 onSizeChanged 更新
//        LinearGradient speedGradient = new LinearGradient(
//                0, 0, 0, getHeight(),
//                new int[]{Color.parseColor("#E61396EF"), Color.parseColor("#4D1396EF")},
//                null,
//                Shader.TileMode.CLAMP
//        );
//        speedPaint.setShader(speedGradient);


        inclinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        inclinePaint.setColor(ContextCompat.getColor(context, R.color.color_incline_bar_run)); // 紫色 (Incline)


//        inclinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        LinearGradient inclineGradient = new LinearGradient(
//                0, 0, 0, getHeight(),
//                new int[]{Color.parseColor("#E6CD5BFF"), Color.parseColor("#4DCD5BFF")},
//                null,
//                Shader.TileMode.CLAMP
//        );
//        inclinePaint.setShader(inclineGradient);


        heartRatePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        heartRatePaint.setColor(ContextCompat.getColor(context, R.color.colorFF715e_60)); // 橘色 (Heart Rate)

        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(ContextCompat.getColor(context, R.color.color252e37)); // 設定為深灰色背景 (#252e37)

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(Color.BLACK); // 黑色分隔線
        linePaint.setStrokeWidth(lineWidth); // 分隔線寬度
    }



//    @Override
//    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        super.onSizeChanged(w, h, oldw, oldh);
//
//        // 在尺寸改變時更新 Shader
//        LinearGradient speedGradient = new LinearGradient(
//                0, 0, 0, h, // 從上到下的漸變方向
//                new int[]{Color.parseColor("#E61396EF"), Color.parseColor("#4D1396EF")},
//                null,
//                Shader.TileMode.CLAMP
//        );
//        speedPaint.setShader(speedGradient);
//
//
//        LinearGradient inclineGradient = new LinearGradient(
//                0, 0, 0, getHeight(),
//                new int[]{Color.parseColor("#E6CD5BFF"), Color.parseColor("#4DCD5BFF")},
//                null,
//                Shader.TileMode.CLAMP
//        );
//        inclinePaint.setShader(inclineGradient);
//
//    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 獲取畫布寬高
        int width = getWidth();
        int height = getHeight();

        // 畫背景，顏色 #252e37
        canvas.drawRect(0, 0, width, height, backgroundPaint);

        // 確保所有 bar 和間距之和覆蓋整個寬度
        float totalWidth = width;
        int barCount = speedValues.length;

        // 確保 barWidths 的長度與 barCount 一致
        if (barWidths.length != barCount) {
            throw new IllegalArgumentException("barWidths array length must match the number of bars.");
        }

        // 計算每個 bar 寬度的絕對值（根據畫布寬度）
        float[] adjustedBarWidths = new float[barCount];
        float totalRelativeWidth = 0f;
        for (float relativeWidth : barWidths) {
            totalRelativeWidth += relativeWidth;
        }
        for (int i = 0; i < barCount; i++) {
            adjustedBarWidths[i] = (barWidths[i] / totalRelativeWidth) * (totalWidth - (lineWidth * (barCount - 1)));
        }

        // 開始畫每個柱狀圖
        float startX = 0; // 將柱狀圖起始位置設為 0，貼緊左側
        for (int i = 0; i < barCount; i++) {
            float barWidth = adjustedBarWidths[i];
            float left = startX;
            float right = left + barWidth;

            float totalHeight = height; // 柱狀圖的總高度 (占畫布的 100%)
            float bottom = height;     // 底部緊貼畫布的最下方

            // Speed (藍色部分)
            float speedHeight = totalHeight * speedValues[i];
            float speedTop = bottom - speedHeight;
            canvas.drawRect(left, speedTop, right, bottom, speedPaint);

            // Incline (紫色部分) 從 X 軸開始繪製
            float inclineHeight = totalHeight * inclineValues[i];
            float inclineTop = bottom - inclineHeight;
            canvas.drawRect(left, inclineTop, right, bottom, inclinePaint);

            // Heart Rate (橘色部分) 從 X 軸開始繪製
            float heartRateHeight = totalHeight * heartRateValues[i];
            float heartRateTop = bottom - heartRateHeight;
            canvas.drawRect(left, heartRateTop, right, bottom, heartRatePaint);

            // 畫黑線作為分隔線
            if (i < barCount - 1) { // 最後一個柱子後面不畫線
                float lineX = right + lineWidth / 2;
                canvas.drawLine(lineX, 0, lineX, height, linePaint); // 黑線延伸到最上方和最下方
            }

            // 更新起始 X 座標，為下一個柱狀圖留出位置（包括分隔線寬度）
            startX = right + lineWidth;
        }
    }

    public void setValues(float[] speedValues, float[] inclineValues, float[] heartRateValues, float[] barWidths) {
        this.speedValues = speedValues;
        this.inclineValues = inclineValues;
        this.heartRateValues = heartRateValues;
        this.barWidths = barWidths;
        invalidate(); // 重新繪製
    }
}
