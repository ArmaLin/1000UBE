package com.dyaco.spirit_commercial.egym

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.dyaco.spirit_commercial.R

class StackedBarChartView3 @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val barPaintOutcome = Paint().apply {
        style = Paint.Style.FILL
    }
    private val barPaintTarget = Paint().apply {
        style = Paint.Style.FILL
        // 預設 target 顏色（若未設定漸層，則以此為單色）
        color = ContextCompat.getColor(context, R.color.white_15)
    }
    private val gridPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.white_15)
        strokeWidth = 2f
        style = Paint.Style.STROKE
    }
    private val textPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.colorADB8C2_65)
        textSize = dpToPx(14f)
        isAntiAlias = true
    }
    private val xLabelPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.colorADB8C2_65)
        textSize = dpToPx(14f)
        isAntiAlias = true
    }
    private val linePaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.white_15)
        strokeWidth = 2f
        style = Paint.Style.STROKE
    }
    private val leftLinePaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.white_15)
        strokeWidth = 2f
        style = Paint.Style.STROKE
    }

    // 資料預設值
    private var outcomeData: DoubleArray =
        doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
    private var targetData: DoubleArray =
        doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
    private var xLabels: Array<String> = arrayOf("0:00", "4:00", "8:00", "12:00", "16:00", "20:00", "24:00")
    private var yLabels: Array<String> = emptyArray()

    private var maxDataValue = 20 // 🔥 Y 軸最大值
    private var xLabelMarginBottom = dpToPx(28f)
    private var yLabelMarginRight = dpToPx(40f)
    private var chartTopMargin = dpToPx(8f)

    // 原本用於單色設定的欄位（保留參考用）
    private var outcomeTopColor = Color.parseColor("#E61396EF")
    private var outcomeBottomColor = Color.parseColor("#331396EF")

    // 用來儲存 outcome 與 target 的漸層色
    private var outcomeGradientStartColor = 0
    private var outcomeGradientEndColor = 0
    private var targetGradientStartColor = 0
    private var targetGradientEndColor = 0

    // 是否使用漸層設定
    private var useGradientOutcome = false
    private var useGradientTarget = false

    // 分別控制 target 與 outcome 的動畫進度（0~1）
    private var targetAnimationProgress = 0f
    private var outcomeAnimationProgress = 0f

    // 控制水平（從左到右）動畫的進度（0~1），僅用於 outcome bar
    private var horizontalAnimationProgress = 0f

    // 判斷是否有設定 targetData
    private var hasTargetData = false

    init {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.StackedBarChartView)
            xLabelMarginBottom = typedArray.getDimension(R.styleable.StackedBarChartView_xLabelMarginBottom, dpToPx(28f))
            yLabelMarginRight = typedArray.getDimension(R.styleable.StackedBarChartView_yLabelMarginRight, dpToPx(40f))
            chartTopMargin = typedArray.getDimension(R.styleable.StackedBarChartView_chartTopMargin, dpToPx(8f))
            typedArray.recycle()
        }
        updateYLabels()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        // 啟動水平動畫，只用於 outcome bar 的左往右效果
        startHorizontalAnimation()
        // 啟動 target 的垂直動畫（由下往上）
        startTargetAnimation()
        // 若有設定 targetData，延遲 500 毫秒後再啟動 outcome 的動畫
        if (hasTargetData) {
            postDelayed({ startOutcomeAnimation() }, 500L)
        } else {
            startOutcomeAnimation()
        }
    }

    /**
     * 啟動水平動畫：從左邊逐漸顯示 outcome bar
     */
    private fun startHorizontalAnimation() {
        val horizontalAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 1500L // 可依需求調整
            addUpdateListener { valueAnimator ->
                horizontalAnimationProgress = valueAnimator.animatedValue as Float
                invalidate()
            }
        }
        horizontalAnimator.start()
    }

    /**
     * 啟動 target 的 bar chart 動畫，讓圖表從下往上伸展
     */
    private fun startTargetAnimation() {
        val animatorAccelerate = ValueAnimator.ofFloat(0f, 0.9f).apply {
            duration = 1000L
            interpolator = AccelerateInterpolator()
            addUpdateListener { valueAnimator ->
                targetAnimationProgress = valueAnimator.animatedValue as Float
                invalidate()
            }
        }
        val animatorOvershoot = ValueAnimator.ofFloat(0.9f, 1f).apply {
            duration = 500L
            interpolator = OvershootInterpolator(3.0f)
            addUpdateListener { valueAnimator ->
                targetAnimationProgress = valueAnimator.animatedValue as Float
                invalidate()
            }
        }
        AnimatorSet().apply {
            playSequentially(animatorAccelerate, animatorOvershoot)
            start()
        }
    }

    /**
     * 啟動 outcome 的 bar chart 動畫，讓圖表從下往上伸展
     */
    private fun startOutcomeAnimation() {
        val animatorAccelerate = ValueAnimator.ofFloat(0f, 0.9f).apply {
            duration = 1000L
            interpolator = AccelerateInterpolator()
            addUpdateListener { valueAnimator ->
                outcomeAnimationProgress = valueAnimator.animatedValue as Float
                invalidate()
            }
        }
        val animatorOvershoot = ValueAnimator.ofFloat(0.9f, 1f).apply {
            duration = 500L
            interpolator = OvershootInterpolator(3.0f)
            addUpdateListener { valueAnimator ->
                outcomeAnimationProgress = valueAnimator.animatedValue as Float
                invalidate()
            }
        }
        AnimatorSet().apply {
            playSequentially(animatorAccelerate, animatorOvershoot)
            start()
        }
    }

    /**
     * 根據 maxDataValue 動態設定 Y 軸標籤
     */
    private fun updateYLabels() {
        val steps = 5
        yLabels = Array(steps + 1) { "" }
        val interval = maxDataValue.toDouble() / steps
        for (i in 0 until steps) {
            yLabels[i] = Math.round(i * interval).toString()
        }
        yLabels[steps] = maxDataValue.toString()
    }

    fun setMaxDataValue(maxValue: Int) {
        maxDataValue = maxValue
        updateYLabels()
        invalidate()
    }

    fun setAxisLabels(xLabels: Array<String>) {
        this.xLabels = xLabels
        invalidate()
    }

    fun setOutcomeData(outcomeData: DoubleArray) {
        this.outcomeData = outcomeData
        invalidate()
    }

    fun setTargetData(targetData: DoubleArray) {
        this.targetData = targetData
        hasTargetData = true
        invalidate()
    }

    /**
     * 設定 outcome 與 target 的漸層色
     * 方法一：指定 outcome 上下漸層色，target 則以單一色（自動轉成漸層）
     */
    fun setBarColors(barOutcomeTopColor: Int, barOutcomeBottomColor: Int, barTargetColor: Int) {
        outcomeGradientStartColor = barOutcomeTopColor
        outcomeGradientEndColor = barOutcomeBottomColor
        useGradientOutcome = true

        targetGradientEndColor = barTargetColor
        targetGradientStartColor = lightenColor(barTargetColor, 0.2f)
        useGradientTarget = true
        invalidate()
    }

    /**
     * 設定 outcome 與 target 的顏色（自動產生漸層效果）
     * 以傳入的顏色產生較亮的起始色
     */
    fun setBarColors(barOutcomeColor: Int, barTargetColor: Int) {
        outcomeGradientEndColor = barOutcomeColor
        outcomeGradientStartColor = lightenColor(barOutcomeColor, 0.2f)
        useGradientOutcome = true

        targetGradientEndColor = barTargetColor
        targetGradientStartColor = lightenColor(barTargetColor, 0.2f)
        useGradientTarget = true
        invalidate()
    }

    /**
     * 輔助方法：產生較亮的顏色，factor 為提升亮度比例（0~1）
     */
    private fun lightenColor(color: Int, factor: Float): Int {
        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)
        val newR = r + ((255 - r) * factor).toInt()
        val newG = g + ((255 - g) * factor).toInt()
        val newB = b + ((255 - b) * factor).toInt()
        return Color.rgb(newR, newG, newB)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width
        val height = height
        val chartWidth = width - yLabelMarginRight.toInt()
        val chartHeight = height - xLabelMarginBottom.toInt() - chartTopMargin.toInt()

        drawGridLines(canvas, 0, chartTopMargin.toInt(), chartWidth, chartHeight)
        drawVerticalLines(canvas, 0, chartTopMargin.toInt(), chartWidth, chartHeight)
        drawBars(canvas, 0, chartTopMargin.toInt(), chartWidth, chartHeight)
        drawYLabels(canvas, chartWidth, chartHeight)
        drawXLabels(canvas, 0, chartHeight, chartWidth, height)

        if (isDataZero()) {
            drawNoDataSquare(canvas)
        }
    }

    /**
     * 判斷 outcomeData 與 targetData 是否皆為 0
     */
    private fun isDataZero(): Boolean {
        if (outcomeData.any { it != 0.0 }) return false
        if (targetData.any { it != 0.0 }) return false
        return true
    }

    /**
     * 當資料皆為 0 時，在 View 中間繪製一個正方形，
     * 背景色來自資源 R.color.color1c242a，內部文字顯示 "No Data"
     */
    private fun drawNoDataSquare(canvas: Canvas) {
        val width = width
        val height = height
        val squareSize = dpToPx(180f).toInt()
        val left = (width - squareSize) / 2
        val top = (height - squareSize) / 2
        val right = left + squareSize
        val bottom = top + squareSize

        val squarePaint = Paint().apply {
            style = Paint.Style.FILL
            color = context.getColor(R.color.color1c242a)
        }
        canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), squarePaint)

        val text = context.getString(R.string.NO_DATA)
        val noDataTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = context.getColor(R.color.color8192a2)
            textSize = 24f
            typeface = ResourcesCompat.getFont(context, R.font.inter_regular)
        }
        val textWidth = noDataTextPaint.measureText(text)
        val fm = noDataTextPaint.fontMetrics
        val x = left + (squareSize - textWidth) / 2
        val y = top + squareSize / 2f - ((fm.descent + fm.ascent) / 2)
        canvas.drawText(text, x, y, noDataTextPaint)
    }

    private fun drawXLabels(canvas: Canvas, paddingLeft: Int, chartHeight: Int, chartWidth: Int, totalHeight: Int) {
        val labelCount = xLabels.size
        val labelSpacing = chartWidth / (labelCount - 1)
        for (i in 0 until labelCount) {
            val x = paddingLeft + i * labelSpacing + dpToPx(12f)
            canvas.drawText(
                xLabels[i],
                (x - xLabelPaint.measureText(xLabels[i]) / 2),
                (totalHeight - dpToPx(2f)),
                xLabelPaint
            )
        }
    }

    private fun drawYLabels(canvas: Canvas, chartWidth: Int, chartHeight: Int) {
        val yCount = yLabels.size
        for (i in 0 until yCount) {
            val y = chartHeight - ((i.toDouble() / (yCount - 1)) * chartHeight) + chartTopMargin
            val textHeight = textPaint.descent() - textPaint.ascent()
            // 將右側數字的 y 座標下移 4dp
            canvas.drawText(
                yLabels[i],
                (chartWidth + dpToPx(6f)),
                (y + textHeight / 4 + dpToPx(2f)).toFloat(),
                textPaint
            )
        }
    }

    private fun drawGridLines(canvas: Canvas, paddingLeft: Int, paddingTop: Int, chartWidth: Int, chartHeight: Int) {
        val yCount = yLabels.size
        val offset = dpToPx(12f)
        for (i in 0 until yCount) {
            val y = paddingTop + chartHeight - ((i.toDouble() / (yCount - 1)) * chartHeight)
            canvas.drawLine(
                paddingLeft.toFloat(),
                y.toFloat(),
                (paddingLeft + chartWidth - offset),
                y.toFloat(),
                gridPaint
            )
        }
    }

    private fun drawVerticalLines(canvas: Canvas, paddingLeft: Int, paddingTop: Int, chartWidth: Int, chartHeight: Int) {
        val labelCount = xLabels.size
        val labelSpacing = chartWidth / (labelCount - 1)
        for (i in 0 until labelCount - 1) {
            val x = paddingLeft + i * labelSpacing
            canvas.drawLine(
                x.toFloat(),
                paddingTop.toFloat(),
                x.toFloat(),
                (paddingTop + chartHeight).toFloat(),
                linePaint
            )
        }
    }

    /**
     * 將 target 與 outcome 的 bar 分開繪製：
     * 1. target bar：僅使用垂直動畫（由下往上）
     * 2. outcome bar：保留垂直與水平動畫效果
     */
    private fun drawBars(canvas: Canvas, paddingLeft: Int, paddingTop: Int, chartWidth: Int, chartHeight: Int) {
        val barCount = minOf(outcomeData.size, targetData.size)
        if (barCount == 0) return

        val barWidth = chartWidth / barCount

        // 計算所有 bar 的最小 top 座標，作為漸層 shader 的起始點
        var minBarTop = (paddingTop + chartHeight).toDouble()
        for (i in 0 until barCount) {
            val finalTargetHeight = (targetData[i] / maxDataValue) * chartHeight
            val finalTargetTop = paddingTop + chartHeight - finalTargetHeight
            val finalOutcomeHeight = (outcomeData[i] / maxDataValue) * chartHeight
            val finalOutcomeTop = paddingTop + chartHeight - finalOutcomeHeight
            if (targetData[i] > 0) {
                minBarTop = minBarTop.coerceAtMost(finalTargetTop)
            } else if (outcomeData[i] > 0) {
                minBarTop = minBarTop.coerceAtMost(finalOutcomeTop)
            }
        }
        if (minBarTop >= (paddingTop + chartHeight)) {
            minBarTop = paddingTop.toDouble()
        }

        // 設定 target 與 outcome 統一的漸層 shader
        if (useGradientTarget) {
            val targetGradient = LinearGradient(
                0f,
                minBarTop.toFloat(),
                0f,
                (paddingTop + chartHeight).toFloat(),
                targetGradientStartColor,
                targetGradientEndColor,
                Shader.TileMode.CLAMP
            )
            barPaintTarget.shader = targetGradient
        } else {
            barPaintTarget.shader = null
        }

        if (useGradientOutcome) {
            val outcomeGradient = LinearGradient(
                0f,
                minBarTop.toFloat(),
                0f,
                (paddingTop + chartHeight).toFloat(),
                outcomeGradientStartColor,
                outcomeGradientEndColor,
                Shader.TileMode.CLAMP
            )
            barPaintOutcome.shader = outcomeGradient
        } else {
            barPaintOutcome.shader = null
        }

        // 先繪製 target bar，不受水平動畫影響，僅使用垂直動畫（由下往上）
        for (i in 0 until barCount) {
            val left = paddingLeft + i * barWidth
            val right = left + barWidth

            val animatedTargetHeight = (targetData[i] / maxDataValue) * chartHeight * targetAnimationProgress
            val animatedTargetTop = paddingTop + chartHeight - animatedTargetHeight
            canvas.drawRect(
                left.toFloat(),
                animatedTargetTop.toFloat(),
                right.toFloat(),
                (paddingTop + chartHeight).toFloat(),
                barPaintTarget
            )
        }

        // 繪製 outcome bar，保留水平動畫效果：利用 clipRect 限制 outcome bar 從左側逐漸展現
        canvas.save()
        val clipRight = paddingLeft.toFloat() + chartWidth * horizontalAnimationProgress
        canvas.clipRect(
            paddingLeft.toFloat(),
            paddingTop.toFloat(),
            clipRight,
            (paddingTop + chartHeight).toFloat()
        )
        for (i in 0 until barCount) {
            val left = paddingLeft + i * barWidth
            val right = left + barWidth

            val animatedOutcomeHeight = (outcomeData[i] / maxDataValue) * chartHeight * outcomeAnimationProgress
            val animatedOutcomeTop = paddingTop + chartHeight - animatedOutcomeHeight
            canvas.drawRect(
                left.toFloat(),
                animatedOutcomeTop.toFloat(),
                right.toFloat(),
                (paddingTop + chartHeight).toFloat(),
                barPaintOutcome
            )
        }
        canvas.restore()
    }

    private fun dpToPx(dp: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            resources.displayMetrics
        )
    }
}
