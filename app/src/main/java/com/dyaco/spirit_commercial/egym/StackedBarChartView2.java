package com.dyaco.spirit_commercial.egym;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.dyaco.spirit_commercial.R;

public class StackedBarChartView2 extends View {
    private Paint barPaintOutcome, barPaintTarget, gridPaint, textPaint, xLabelPaint, linePaint, leftLinePaint;
    private double[] outcomeData = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private double[] targetData = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private String[] xLabels = {"0:00", "4:00", "8:00", "12:00", "16:00", "20:00", "24:00"};
    private String[] yLabels;

    private int maxDataValue = 20; // 🔥 Y 軸最大值
    private double xLabelMarginBottom, yLabelMarginRight, chartTopMargin;
    // 原本用於單色設定的欄位，保留參考用
    private int outcomeTopColor = Color.parseColor("#E61396EF");
    private int outcomeBottomColor = Color.parseColor("#331396EF");
    private Context mContext;

    // 新增：用來儲存 outcome 與 target 的漸層色
    private int outcomeGradientStartColor, outcomeGradientEndColor;
    private int targetGradientStartColor, targetGradientEndColor;
    // 是否使用漸層設定
    private boolean useGradientOutcome = false;
    private boolean useGradientTarget = false;

    public StackedBarChartView2(Context context) {
        super(context);
        mContext = context;
        init(context);
    }

    public StackedBarChartView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(context);
    }

    private void init(Context context) {
        barPaintOutcome = new Paint();
        barPaintOutcome.setStyle(Paint.Style.FILL);

        barPaintTarget = new Paint();
        barPaintTarget.setStyle(Paint.Style.FILL);
        // 預設 target 顏色（若未設定漸層，則以此為單色）
        barPaintTarget.setColor(ContextCompat.getColor(context, R.color.white_15));

        gridPaint = new Paint();
        gridPaint.setColor(ContextCompat.getColor(context, R.color.white_15));
        gridPaint.setStrokeWidth(2f);
        gridPaint.setStyle(Paint.Style.STROKE);

        textPaint = new Paint();
        textPaint.setColor(ContextCompat.getColor(context, R.color.colorADB8C2_65));
        textPaint.setTextSize(dpToPx(14));
        textPaint.setAntiAlias(true);

        xLabelPaint = new Paint();
        xLabelPaint.setColor(ContextCompat.getColor(context, R.color.colorADB8C2_65));
        xLabelPaint.setTextSize(dpToPx(14));
        xLabelPaint.setAntiAlias(true);

        linePaint = new Paint();
        linePaint.setColor(ContextCompat.getColor(context, R.color.white_15));
        linePaint.setStrokeWidth(2f);
        linePaint.setStyle(Paint.Style.STROKE);

        leftLinePaint = new Paint();
        leftLinePaint.setColor(ContextCompat.getColor(context, R.color.white_15));
        leftLinePaint.setStrokeWidth(2f);
        leftLinePaint.setStyle(Paint.Style.STROKE);

        xLabelMarginBottom = dpToPx(28);
        yLabelMarginRight = dpToPx(40);
        chartTopMargin = dpToPx(8);

        updateYLabels(); // 🔥 初始化 yLabels
    }

    /**
     * 🔥 **根據 maxDataValue 動態設定 Y 軸標籤**
     */
    private void updateYLabels() {
        int steps = 5;
        yLabels = new String[steps + 1];

        // 🔥 修正間隔計算，確保不會丟失 maxDataValue
        double interval = (double) maxDataValue / steps;

        for (int i = 0; i < steps; i++) {
            yLabels[i] = String.valueOf((int) Math.round(i * interval)); // 確保每個標籤對齊
        }
        yLabels[steps] = String.valueOf(maxDataValue); // 🔥 確保最後一個值為 maxDataValue
    }

    public void setMaxDataValue(int maxValue) {
        this.maxDataValue = maxValue;
        updateYLabels();
        invalidate();
    }

    public void setAxisLabels(String[] xLabels) {
        this.xLabels = xLabels;
        invalidate();
    }

    public void setOutcomeData(double[] outcomeData) {
        this.outcomeData = outcomeData;
        invalidate();
    }

    public void setTargetData(double[] targetData) {
        this.targetData = targetData;
        invalidate();
    }

    /**
     * 設定 outcome 與 target 的漸層色
     * 方法一：指定 outcome 上下漸層色，target 則以單一色（自動轉成漸層）
     */
    public void setBarColors(int barOutcomeTopColor, int barOutcomeBottomColor, int barTargetColor) {
        // 對 outcome 使用指定的上、下色
        this.outcomeGradientStartColor = barOutcomeTopColor;
        this.outcomeGradientEndColor = barOutcomeBottomColor;
        this.useGradientOutcome = true;
        // 對 target，如果只提供單一色，則自動產生一個較亮的版本當作起始色
        this.targetGradientEndColor = barTargetColor;
        this.targetGradientStartColor = lightenColor(barTargetColor, 0.2f);
        this.useGradientTarget = true;
        invalidate();
    }

    /**
     * 設定 outcome 與 target 的顏色（改成自動產生漸層效果）
     * 此方法會以傳入的顏色做漸層，產生較亮的起始色
     */
    public void setBarColors(int barOutcomeColor, int barTargetColor) {
        this.outcomeGradientEndColor = barOutcomeColor;
        this.outcomeGradientStartColor = lightenColor(barOutcomeColor, 0.2f);
        this.useGradientOutcome = true;

        this.targetGradientEndColor = barTargetColor;
        this.targetGradientStartColor = lightenColor(barTargetColor, 0.2f);
        this.useGradientTarget = true;
        invalidate();
    }

    /**
     * 輔助方法：產生較亮的顏色，factor 為提升亮度比例（0~1之間）
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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int chartWidth = width - (int) yLabelMarginRight;
        int chartHeight = height - (int) xLabelMarginBottom - (int) chartTopMargin;

        drawGridLines(canvas, 0, (int) chartTopMargin, chartWidth, chartHeight);
        drawVerticalLines(canvas, 0, (int) chartTopMargin, chartWidth, chartHeight);
        drawBars(canvas, 0, (int) chartTopMargin, chartWidth, chartHeight);
        drawYLabels(canvas, chartWidth, chartHeight);
        drawXLabels(canvas, 0, chartHeight, chartWidth, height);

        // 若 outcomeData 與 targetData 全部為 0，則顯示 "No Data" 的正方形
        if (isDataZero()) {
            drawNoDataSquare(canvas);
        }
    }

    /**
     * 判斷 outcomeData 與 targetData 是否全部為 0
     */
    private boolean isDataZero() {
        for (double d : outcomeData) {
            if (d != 0.0) {
                return false;
            }
        }
        for (double d : targetData) {
            if (d != 0.0) {
                return false;
            }
        }
        return true;
    }

    /**
     * 當資料皆為 0 時，在 View 中間繪製一個正方形，
     * 背景色由資源 R.color.color1c242a 定義，內部文字顯示 "No Data"
     */
    private void drawNoDataSquare(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        int squareSize = (int) dpToPx(180);
        int left = (width - squareSize) / 2;
        int top = (height - squareSize) / 2;
        int right = left + squareSize;
        int bottom = top + squareSize;

        // 繪製正方形背景
        Paint squarePaint = new Paint();
        squarePaint.setStyle(Paint.Style.FILL);
        squarePaint.setColor(mContext.getColor(R.color.color1c242a));
        canvas.drawRect(left, top, right, bottom, squarePaint);

        // 設定 "No Data" 文字屬性
        String text = mContext.getString(R.string.NO_DATA);
        Paint noDataTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        noDataTextPaint.setColor(mContext.getColor(R.color.color8192a2));
        noDataTextPaint.setTextSize(24);
        noDataTextPaint.setTypeface(ResourcesCompat.getFont(mContext, R.font.inter_regular));

        // 計算文字寬度與垂直居中位置
        float textWidth = noDataTextPaint.measureText(text);
        Paint.FontMetrics fm = noDataTextPaint.getFontMetrics();
        float x = left + (squareSize - textWidth) / 2;
        float y = top + (squareSize / 2f) - ((fm.descent + fm.ascent) / 2);
        canvas.drawText(text, x, y, noDataTextPaint);
    }

    private void drawXLabels(Canvas canvas, int paddingLeft, int chartHeight, int chartWidth, int totalHeight) {
        int labelCount = xLabels.length;
        int labelSpacing = chartWidth / (labelCount - 1);
        for (int i = 0; i < labelCount; i++) {
            double x = paddingLeft + i * labelSpacing + dpToPx(12);
            canvas.drawText(xLabels[i], (float) (x - xLabelPaint.measureText(xLabels[i]) / 2),
                    totalHeight - dpToPx(2), xLabelPaint);
        }
    }

    private void drawYLabels(Canvas canvas, int chartWidth, int chartHeight) {
        int yCount = yLabels.length;
        for (int i = 0; i < yCount; i++) {
            double y = chartHeight - ((double) i / (yCount - 1) * chartHeight) + chartTopMargin;
            float textHeight = textPaint.descent() - textPaint.ascent();
            canvas.drawText(yLabels[i], chartWidth + dpToPx(10), (float) (y + textHeight / 4), textPaint);
        }
    }

    private void drawGridLines(Canvas canvas, int paddingLeft, int paddingTop, int chartWidth, int chartHeight) {
        int yCount = yLabels.length;
        for (int i = 0; i < yCount; i++) {
            double y = paddingTop + chartHeight - ((double) i / (yCount - 1) * chartHeight);
            canvas.drawLine(paddingLeft, (float) y, paddingLeft + chartWidth, (float) y, gridPaint);
        }
    }

    private void drawVerticalLines(Canvas canvas, int paddingLeft, int paddingTop, int chartWidth, int chartHeight) {
        int labelCount = xLabels.length;
        int labelSpacing = chartWidth / (labelCount - 1);
        for (int i = 0; i < labelCount - 1; i++) {
            double x = paddingLeft + i * labelSpacing;
            canvas.drawLine((float) x, paddingTop, (float) x, paddingTop + chartHeight, linePaint);
        }
    }

    private void drawBars(Canvas canvas, int paddingLeft, int paddingTop, int chartWidth, int chartHeight) {
        int barCount = outcomeData.length;
        int barWidth = chartWidth / barCount;

        // 計算所有 bar 中最高的 bar 的 top 座標
        double minBarTop = paddingTop + chartHeight; // 初始值設為圖表底部
        for (int i = 0; i < barCount; i++) {
            double targetHeight = (targetData[i] / maxDataValue) * chartHeight;
            double targetTop = paddingTop + chartHeight - targetHeight;
            double outcomeHeight = (outcomeData[i] / maxDataValue) * chartHeight;
            double outcomeTop = paddingTop + chartHeight - outcomeHeight;
            // 依據資料，若 outcome 有資料則取 outcomeTop，否則若 target 有資料則取 targetTop
            if (outcomeData[i] > 0) {
                minBarTop = Math.min(minBarTop, outcomeTop);
            } else if (targetData[i] > 0) {
                minBarTop = Math.min(minBarTop, targetTop);
            }
        }
        // 若沒有任何 bar 有資料，則退回使用原始 paddingTop
        if (minBarTop >= paddingTop + chartHeight) {
            minBarTop = paddingTop;
        }

        // 使用統一的漸層 shader，以最高 bar 的 top 為頂端
        if (useGradientTarget) {
            LinearGradient targetGradient = new LinearGradient(
                    0, (float) minBarTop,
                    0, paddingTop + chartHeight,
                    targetGradientStartColor,
                    targetGradientEndColor,
                    Shader.TileMode.CLAMP);
            barPaintTarget.setShader(targetGradient);
        } else {
            barPaintTarget.setShader(null);
        }

        if (useGradientOutcome) {
            LinearGradient outcomeGradient = new LinearGradient(
                    0, (float) minBarTop,
                    0, paddingTop + chartHeight,
                    outcomeGradientStartColor,
                    outcomeGradientEndColor,
                    Shader.TileMode.CLAMP);
            barPaintOutcome.setShader(outcomeGradient);
        } else {
            barPaintOutcome.setShader(null);
        }

        // 繪製每個 bar
        for (int i = 0; i < barCount; i++) {
            int left = paddingLeft + i * barWidth;
            int right = left + barWidth;

            double targetHeight = (targetData[i] / maxDataValue) * chartHeight;
            double targetTop = paddingTop + chartHeight - targetHeight;
            double outcomeHeight = (outcomeData[i] / maxDataValue) * chartHeight;
            double outcomeTop = paddingTop + chartHeight - outcomeHeight;

            canvas.drawRect(left, (float) targetTop, right, paddingTop + chartHeight, barPaintTarget);
            canvas.drawRect(left, (float) outcomeTop, right, paddingTop + chartHeight, barPaintOutcome);
        }
    }



    private float dpToPx(double dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) dp, getResources().getDisplayMetrics());
    }
}
