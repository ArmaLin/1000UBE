package com.dyaco.spirit_commercial.egym;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.dyaco.spirit_commercial.R;

public class EgymBarChartView extends View {

    private Paint speedPaint;        // 藍色柱狀圖 Paint (Speed)
    private Paint inclinePaint;      // 紫色柱狀圖 Paint (Incline)
    private Paint heartRatePaint;    // 橘色柱狀圖 Paint (Heart Rate)
    private Paint backgroundPaint;   // 深灰色背景 Paint
    private Paint linePaint;         // 分隔線 Paint
    private Paint textPaint;         // 顯示文字 Paint

    private float[] speedValues = {};       // 藍色部分的高度比例
    private float[] inclineValues = {};     // 紫色部分的高度比例
    private float[] heartRateValues = {};   // 橘色部分的高度比例
    private float[] barWidths = {};         // 每個柱狀圖的寬度比例
    private float[] adjustedBarWidths;      // 計算後的柱狀圖寬度

    private float[][] chartValues; // 儲存所有資料陣列

    private float lineWidth = 0f;    // 分隔線的寬度

    // 漸層色設定與開關
    private int speedGradientStartColor, speedGradientEndColor;
    private boolean useGradientSpeed = false;
    private int inclineGradientStartColor, inclineGradientEndColor;
    private boolean useGradientIncline = false;
    private int heartRateGradientStartColor, heartRateGradientEndColor;
    private boolean useGradientHeartRate = false;

    // 動畫進度 (0~1)
    private float animationProgress = 0f;
    private ValueAnimator barAnimator;

    // 以下為預先快取用變數，避免在 onDraw 或 layout 中重複建立物件
    private String noChartText;
    private float density;
    private static final float EPSILON = 0.5f;

    // 快取各系列漸層 LinearGradient 及其相關參數
    private LinearGradient cachedSpeedGradient = null;
    private float cachedSpeedMinTop = -1;
    private int cachedSpeedStartColor = -1;
    private int cachedSpeedEndColor = -1;

    private LinearGradient cachedInclineGradient = null;
    private float cachedInclineMinTop = -1;
    private int cachedInclineStartColor = -1;
    private int cachedInclineEndColor = -1;

    private LinearGradient cachedHeartRateGradient = null;
    private float cachedHeartRateMinTop = -1;
    private int cachedHeartRateStartColor = -1;
    private int cachedHeartRateEndColor = -1;

    // 快取 View 高度，當高度改變時則重建所有漸層
    private int cachedHeight = -1;

    public EgymBarChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
        speedValues = new float[0];
        inclineValues = new float[0];
        heartRateValues = new float[0];
        barWidths = new float[0];
        chartValues = new float[0][0];
    }

    public EgymBarChartView(Context context) {
        super(context);
        init(context, null);
        speedValues = new float[0];
        inclineValues = new float[0];
        heartRateValues = new float[0];
        barWidths = new float[0];
        chartValues = new float[0][0];
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = (attrs == null) ? null : getContext().obtainStyledAttributes(attrs, R.styleable.EgymBarChartView);
        if (typedArray != null) {
            lineWidth = typedArray.getDimension(R.styleable.EgymBarChartView_lineWidth, lineWidth);
            typedArray.recycle();
        }

        // 預先快取 dp、字串等資料
        density = getResources().getDisplayMetrics().density;
        noChartText = getContext().getString(R.string.no_chart_available);

        // 初始化 Paint 物件
        speedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        speedPaint.setColor(ContextCompat.getColor(context, R.color.color_level_speed_bar_run));

        inclinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        inclinePaint.setColor(ContextCompat.getColor(context, R.color.color_incline_bar_run));

        heartRatePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        heartRatePaint.setColor(ContextCompat.getColor(context, R.color.colorFF715e_60));

        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(ContextCompat.getColor(context, R.color.color252e37));

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(Color.TRANSPARENT);
    //    linePaint.setColor(ContextCompat.getColor(context, R.color.color1396ef));
        linePaint.setStrokeWidth(lineWidth);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(ContextCompat.getColor(context, R.color.color5a7085));
        textPaint.setTextSize(18f);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTypeface(ResourcesCompat.getFont(context, R.font.inter_regular));
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startBarAnimation();
    }

    /**
     * 啟動動畫，讓圖表從下往上伸展
     */
    private void startBarAnimation() {
        if (barAnimator != null && barAnimator.isRunning()) {
            barAnimator.cancel();
        }
        animationProgress = 0f;
        barAnimator = ValueAnimator.ofFloat(0f, 1f);
        barAnimator.setDuration(500);
        barAnimator.addUpdateListener(animation -> {
            animationProgress = (float) animation.getAnimatedValue();
            invalidate();
        });
        barAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        // 使用快取字串，避免每次呼叫 getString 分配新物件
        if (chartValues == null || barWidths == null || speedValues == null ||
                inclineValues == null || heartRateValues == null) {
            showNoData(canvas, width, height);
            return;
        }

        int barCount = speedValues.length;
        if (barWidths.length != barCount) {
            Log.d("EGYM", "barWidths 陣列的長度必須與柱狀圖的數量一致。");
            showNoData(canvas, width, height);
            return;
        }

        // 若所有資料皆為 0，則顯示 no data
        boolean allZero = true;
        for (float[] values : chartValues) {
            for (float value : values) {
                if (value != 0) {
                    allZero = false;
                    break;
                }
            }
            if (!allZero) break;
        }
        if (allZero) {
            showNoData(canvas, width, height);
            return;
        }

        // 若 View 高度改變，清除快取的漸層
        if (height != cachedHeight) {
            cachedSpeedGradient = null;
            cachedInclineGradient = null;
            cachedHeartRateGradient = null;
            cachedHeight = height;
        }

        float totalRelativeWidth = 0f;
        for (float relativeWidth : barWidths) {
            totalRelativeWidth += relativeWidth;
        }

        // 預設分隔線寬度為 4px
        final float defaultLineWidth = 4f;
        lineWidth = defaultLineWidth;

        // 修改點一：將每個柱狀圖的最小寬度從 1dp 改為 2dp，
        // 並且在 candidate 計算時，若 candidate 小於預設 4px，確保分隔線最低為 2px。
        if (barCount > 1) {
            float minWeight = Float.MAX_VALUE;
            for (float weight : barWidths) {
                if (weight > 0 && weight < minWeight) {
                    minWeight = weight;
                }
            }
            if (minWeight != Float.MAX_VALUE) {
                float minBarWidthPx = density * 4; // 原先 density * 1，現改為 2dp
                float candidate = (width - ((minBarWidthPx * totalRelativeWidth) / minWeight)) / (barCount - 1);
                if (candidate < defaultLineWidth) {
                    lineWidth = Math.max(candidate, 2f); // 原先最低 1px，現改為 2px
                }
            }
        }

        if (adjustedBarWidths == null || adjustedBarWidths.length != barCount) {
            adjustedBarWidths = new float[barCount];
        }
        for (int i = 0; i < barCount; i++) {
            adjustedBarWidths[i] = (barWidths[i] / totalRelativeWidth) * (width - (lineWidth * (barCount - 1)));
        }

        float totalHeight = height;
        float minSpeedTop = height;
        float minInclineTop = height;
        float minHeartRateTop = height;
        for (int i = 0; i < barCount; i++) {
            float speedHeight = totalHeight * speedValues[i];
            float speedTop = height - speedHeight;
            if (speedValues[i] > 0) {
                minSpeedTop = Math.min(minSpeedTop, speedTop);
            }
            float inclineHeight = totalHeight * inclineValues[i];
            float inclineTop = height - inclineHeight;
            if (inclineValues[i] > 0) {
                minInclineTop = Math.min(minInclineTop, inclineTop);
            }
            float heartRateHeight = totalHeight * heartRateValues[i];
            float heartRateTop = height - heartRateHeight;
            if (heartRateValues[i] > 0) {
                minHeartRateTop = Math.min(minHeartRateTop, heartRateTop);
            }
        }
        if (minSpeedTop == height) minSpeedTop = 0;
        if (minInclineTop == height) minInclineTop = 0;
        if (minHeartRateTop == height) minHeartRateTop = 0;

        // 設定 Speed 漸層 (快取重用)
        if (useGradientSpeed) {
            if (cachedSpeedGradient == null ||
                    Math.abs(cachedSpeedMinTop - minSpeedTop) > EPSILON ||
                    cachedSpeedStartColor != speedGradientStartColor ||
                    cachedSpeedEndColor != speedGradientEndColor) {
                cachedSpeedGradient = new LinearGradient(
                        0, minSpeedTop,
                        0, height,
                        speedGradientStartColor,
                        speedGradientEndColor,
                        Shader.TileMode.CLAMP);
                cachedSpeedMinTop = minSpeedTop;
                cachedSpeedStartColor = speedGradientStartColor;
                cachedSpeedEndColor = speedGradientEndColor;
            }
            speedPaint.setShader(cachedSpeedGradient);
        } else {
            speedPaint.setShader(null);
        }

        // 設定 Incline 漸層 (快取重用)
        if (useGradientIncline) {
            if (cachedInclineGradient == null ||
                    Math.abs(cachedInclineMinTop - minInclineTop) > EPSILON ||
                    cachedInclineStartColor != inclineGradientStartColor ||
                    cachedInclineEndColor != inclineGradientEndColor) {
                cachedInclineGradient = new LinearGradient(
                        0, minInclineTop,
                        0, height,
                        inclineGradientStartColor,
                        inclineGradientEndColor,
                        Shader.TileMode.CLAMP);
                cachedInclineMinTop = minInclineTop;
                cachedInclineStartColor = inclineGradientStartColor;
                cachedInclineEndColor = inclineGradientEndColor;
            }
            inclinePaint.setShader(cachedInclineGradient);
        } else {
            inclinePaint.setShader(null);
        }

        // 設定 HeartRate 漸層 (快取重用)
        if (useGradientHeartRate) {
            if (cachedHeartRateGradient == null ||
                    Math.abs(cachedHeartRateMinTop - minHeartRateTop) > EPSILON ||
                    cachedHeartRateStartColor != heartRateGradientStartColor ||
                    cachedHeartRateEndColor != heartRateGradientEndColor) {
                cachedHeartRateGradient = new LinearGradient(
                        0, minHeartRateTop,
                        0, height,
                        heartRateGradientStartColor,
                        heartRateGradientEndColor,
                        Shader.TileMode.CLAMP);
                cachedHeartRateMinTop = minHeartRateTop;
                cachedHeartRateStartColor = heartRateGradientStartColor;
                cachedHeartRateEndColor = heartRateGradientEndColor;
            }
            heartRatePaint.setShader(cachedHeartRateGradient);
        } else {
            heartRatePaint.setShader(null);
        }

        float startX = 0;
        for (int i = 0; i < barCount; i++) {
            float barWidth = adjustedBarWidths[i];
            float left = startX;
            float right = left + barWidth;
            float bottom = height;

            canvas.drawRect(left, 0, right, height, backgroundPaint);

            float heartRateHeight = totalHeight * heartRateValues[i] * animationProgress;
            float heartRateTop = bottom - heartRateHeight;
            canvas.drawRect(left, heartRateTop, right, bottom, heartRatePaint);

            float speedHeight = totalHeight * speedValues[i] * animationProgress;
            float speedTop = bottom - speedHeight;
            canvas.drawRect(left, speedTop, right, bottom, speedPaint);

            float inclineHeight = totalHeight * inclineValues[i] * animationProgress;
            float inclineTop = bottom - inclineHeight;
            canvas.drawRect(left, inclineTop, right, bottom, inclinePaint);

            if (i < barCount - 1) {
                float lineX = right + lineWidth / 2;
                canvas.drawLine(lineX, 0, lineX, height, linePaint);
            }
            startX = right + lineWidth;
        }
    }

    private void showNoData(Canvas canvas, int width, int height) {
        canvas.drawRect(0, 0, width, height, backgroundPaint);
        canvas.drawText(noChartText,
                width / 2f,
                height / 2f - ((textPaint.descent() + textPaint.ascent()) / 2),
                textPaint);
    }

    /**
     * 更新柱狀圖資料並重啟動畫
     *
     * @param speedValues     藍色部分高度陣列
     * @param inclineValues   紫色部分高度陣列
     * @param heartRateValues 橘色部分高度陣列
     * @param barWidths       每個柱狀圖寬度比例陣列
     */
    public void setValues(float[] speedValues, float[] inclineValues, float[] heartRateValues, float[] barWidths) {
        this.speedValues = speedValues;
        this.inclineValues = inclineValues;
        this.heartRateValues = heartRateValues;
        this.barWidths = barWidths;
        this.chartValues = new float[][] { speedValues, inclineValues, heartRateValues };

        if (adjustedBarWidths == null || adjustedBarWidths.length != barWidths.length) {
            adjustedBarWidths = new float[barWidths.length];
        }
        animationProgress = 0f;
        startBarAnimation();
    }

    // 以下設定各系列的漸層色

    // Speed 系列
    public void setSpeedBarColors(int speedColor) {
        this.speedGradientEndColor = speedColor;
        this.speedGradientStartColor = lightenColor(speedColor, 0.2f);
        this.useGradientSpeed = true;
        invalidate();
    }

    public void setSpeedBarColors(int topColor, int bottomColor) {
        this.speedGradientStartColor = topColor;
        this.speedGradientEndColor = bottomColor;
        this.useGradientSpeed = true;
        invalidate();
    }

    // Incline 系列
    public void setInclineBarColors(int inclineColor) {
        this.inclineGradientEndColor = inclineColor;
        this.inclineGradientStartColor = lightenColor(inclineColor, 0.2f);
        this.useGradientIncline = true;
        invalidate();
    }

    public void setInclineBarColors(int topColor, int bottomColor) {
        this.inclineGradientStartColor = topColor;
        this.inclineGradientEndColor = bottomColor;
        this.useGradientIncline = true;
        invalidate();
    }

    // Heart Rate 系列
    public void setHeartRateBarColors(int heartRateColor) {
        this.heartRateGradientEndColor = heartRateColor;
        this.heartRateGradientStartColor = lightenColor(heartRateColor, 0.2f);
        this.useGradientHeartRate = true;
        invalidate();
    }

    public void setHeartRateBarColors(int topColor, int bottomColor) {
        this.heartRateGradientStartColor = topColor;
        this.heartRateGradientEndColor = bottomColor;
        this.useGradientHeartRate = true;
        invalidate();
    }

    /**
     * 輔助方法：依照 factor 調整顏色亮度 (0~1)
     */
    private int lightenColor(int color, float factor) {
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        r += (int) ((255 - r) * factor);
        g += (int) ((255 - g) * factor);
        b += (int) ((255 - b) * factor);
        return Color.rgb(r, g, b);
    }
}
