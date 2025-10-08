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
import java.util.Locale

class StackedBarChartView @JvmOverloads constructor(
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

    // 資料來源改為 List<StackedBarBean>
    private var targetDataList: List<StackedBarBean> = emptyList()
    private var outcomeDataList: List<StackedBarBean> = emptyList()

    // X 軸標籤將根據 targetDataList 自動產生，格式為 mm:ss，總數固定 5 個
    private var xLabels: Array<String> = emptyArray()
    private var yLabels: Array<String> = emptyArray()

    private var maxDataValue = 20 // Y 軸最大值
    private var xLabelMarginBottom = dpToPx(28f)
    private var yLabelMarginRight = dpToPx(40f)
    private var chartTopMargin = dpToPx(8f)

    // 儲存漸層色
    private var outcomeGradientStartColor = 0
    private var outcomeGradientEndColor = 0
    private var targetGradientStartColor = 0
    private var targetGradientEndColor = 0

    // 是否使用漸層
    private var useGradientOutcome = false
    private var useGradientTarget = false

    // 動畫進度 (0~1)
    private var targetAnimationProgress = 0f
    private var outcomeAnimationProgress = 0f
    private var horizontalAnimationProgress = 0f  // 僅用於 outcome bar 的水平動畫

    // 判斷是否有 target 資料
    private var hasTargetData = false

    init {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.StackedBarChartView)
            xLabelMarginBottom = typedArray.getDimension(
                R.styleable.StackedBarChartView_xLabelMarginBottom,
                dpToPx(28f)
            )
            yLabelMarginRight = typedArray.getDimension(
                R.styleable.StackedBarChartView_yLabelMarginRight,
                dpToPx(40f)
            )
            chartTopMargin = typedArray.getDimension(
                R.styleable.StackedBarChartView_chartTopMargin,
                dpToPx(8f)
            )
            typedArray.recycle()
        }
        updateYLabels()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        // 啟動水平動畫（僅用於 outcome bar 從左到右呈現）
        startHorizontalAnimation()
        // 啟動 target 的垂直動畫（由下往上）
        startTargetAnimation()
        // 若有 target 資料，延遲 500 毫秒後再啟動 outcome 的動畫
        if (hasTargetData) {
            postDelayed({ startOutcomeAnimation() }, 500L)
        } else {
            startOutcomeAnimation()
        }
    }

    /**
     * 啟動水平動畫：從左逐漸呈現 outcome bar
     */
    private fun startHorizontalAnimation() {
        val horizontalAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 1500L
            addUpdateListener { animator ->
                horizontalAnimationProgress = animator.animatedValue as Float
                invalidate()
            }
        }
        horizontalAnimator.start()
    }

    /**
     * 啟動 target bar 的垂直動畫（由下往上）
     */
    private fun startTargetAnimation() {
        val animatorAccelerate = ValueAnimator.ofFloat(0f, 0.9f).apply {
            duration = 1000L
            interpolator = AccelerateInterpolator()
            addUpdateListener { animator ->
                targetAnimationProgress = animator.animatedValue as Float
                invalidate()
            }
        }
        val animatorOvershoot = ValueAnimator.ofFloat(0.9f, 1f).apply {
            duration = 500L
            interpolator = OvershootInterpolator(3.0f)
            addUpdateListener { animator ->
                targetAnimationProgress = animator.animatedValue as Float
                invalidate()
            }
        }
        AnimatorSet().apply {
            playSequentially(animatorAccelerate, animatorOvershoot)
            start()
        }
    }

    /**
     * 啟動 outcome bar 的垂直動畫（由下往上）
     */
    private fun startOutcomeAnimation() {
        val animatorAccelerate = ValueAnimator.ofFloat(0f, 0.9f).apply {
            duration = 1000L
            interpolator = AccelerateInterpolator()
            addUpdateListener { animator ->
                outcomeAnimationProgress = animator.animatedValue as Float
                invalidate()
            }
        }
        val animatorOvershoot = ValueAnimator.ofFloat(0.9f, 1f).apply {
            duration = 500L
            interpolator = OvershootInterpolator(3.0f)
            addUpdateListener { animator ->
                outcomeAnimationProgress = animator.animatedValue as Float
                invalidate()
            }
        }
        AnimatorSet().apply {
            playSequentially(animatorAccelerate, animatorOvershoot)
            start()
        }
    }

    /**
     * 根據 maxDataValue 動態設定 Y 軸標籤（保持原來的 5 等分，共 6 條線）
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

    /**
     * 自動根據 targetDataList 中每筆資料的 secTime 計算累積秒數，
     * 再依據總秒數平均取 5 個標籤，第一個一定顯示 "0:00"，最後一個為總時間，
     * 中間以 mm:ss 格式呈現。
     */
    private fun updateAxisLabelsFromData() {
        if (targetDataList.isEmpty()) {
            xLabels = emptyArray()
            return
        }
        val totalSecTime = targetDataList.sumOf { it.secTime }
        val labelCount = 5
        val labels = mutableListOf<String>()
        for (i in 0 until labelCount) {
            // 依比例計算標籤時間（浮點數計算）
            val t = totalSecTime.toFloat() * i / (labelCount - 1)
            val minutes = (t.toInt()) / 60
            val seconds = (t.toInt()) % 60
            labels.add(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds))
        }
        xLabels = labels.toTypedArray()
        invalidate()
    }

    /**
     * 設定 target 資料：以 List<StackedBarBean> 傳入，
     * 其中 bean.value 決定 bar 高度，bean.secTime 決定 bar 寬度比例分配，
     * 並根據 target 資料自動更新 X 軸標籤。
     */
    fun setTargetData(dataList: List<StackedBarBean>) {
        this.targetDataList = dataList
        hasTargetData = true
        updateAxisLabelsFromData()
        invalidate()
    }

    /**
     * 設定 outcome 資料，做法與 target 資料相同，
     * 若資料數量不足則補 0
     */
    fun setOutcomeData(dataList: List<StackedBarBean>) {
        this.outcomeDataList = dataList
        invalidate()
    }

    /**
     * 設定 outcome 與 target 的漸層色
     * 方法一：指定 outcome 上下漸層色，target 則直接使用單一色，不做輕化
     */
    fun setBarColors(barOutcomeTopColor: Int, barOutcomeBottomColor: Int, barTargetColor: Int) {
        outcomeGradientStartColor = barOutcomeTopColor
        outcomeGradientEndColor = barOutcomeBottomColor
        useGradientOutcome = true

        targetGradientEndColor = barTargetColor
        targetGradientStartColor = barTargetColor
        useGradientTarget = false
        invalidate()
    }

    /**
     * 設定 outcome 與 target 的顏色（不做輕化處理）
     */
    fun setBarColors(barOutcomeColor: Int, barTargetColor: Int) {
        outcomeGradientEndColor = barOutcomeColor
        outcomeGradientStartColor = barOutcomeColor
        useGradientOutcome = true

        targetGradientEndColor = barTargetColor
        targetGradientStartColor = barTargetColor
        useGradientTarget = true
        invalidate()
    }

    /**
     * 輔助方法：根據 factor 提升顏色亮度（factor 範圍 0~1）
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
     * 判斷 targetDataList 與 outcomeDataList 是否皆為 0
     */
    private fun isDataZero(): Boolean {
        if (targetDataList.any { it.value != 0.0 }) return false
        if (outcomeDataList.any { it.value != 0.0 }) return false
        return true
    }

    /**
     * 當資料皆為 0 時，在 View 中間繪製正方形，
     * 背景色來自資源 R.color.color1c242a，
     * 內部文字顯示 "No Data"
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

    /**
     * 繪製 X 軸標籤：
     * 依據 targetDataList 總秒數線性映射，平均取 5 個標籤，
     * 位置 = paddingLeft + (labelTime/totalSecTime)*chartWidth
     */
    private fun drawXLabels(canvas: Canvas, paddingLeft: Int, chartHeight: Int, chartWidth: Int, totalHeight: Int) {
        // 如果沒有 xLabels 則不繪製
        if (xLabels.isEmpty()) return
        // 取得 targetDataList 的總秒數
        val totalSecTime = targetDataList.sumOf { it.secTime }
        // 取得標籤數量
        val labelCount = xLabels.size
        // 將迴圈改為迭代到 labelCount - 1，這樣最後一個標籤不會被繪製
        for (i in 0 until labelCount - 1) {
            // 計算當前標籤對應的累積秒數比例
            val labelTime = totalSecTime.toFloat() * i / (labelCount - 1)
            // 計算 x 座標：根據累積秒數比例映射到 chartWidth
            var xPos = paddingLeft + (labelTime / totalSecTime.toFloat()) * chartWidth
            if (i == 0) {
                // 若為第一個標籤，調整 xPos 讓標籤中心不會靠太邊，預留半個文字寬度
                xPos = paddingLeft + xLabelPaint.measureText(xLabels[i]) / 2
            }
            // 將標籤文字置中繪製
            canvas.drawText(
                xLabels[i],
                xPos - xLabelPaint.measureText(xLabels[i]) / 2,
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
        if (labelCount < 2) return
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
     * 繪製 bar chart：
     * 1. 根據 targetDataList 的 size 決定 x 軸比例，
     *    每個 bar 的寬度依據 bean.secTime 佔總 secTime 的比例分配。
     * 2. 每個 bar 的高度依據 bean.value 與動畫進度呈現。
     */
    private fun drawBars(canvas: Canvas, paddingLeft: Int, paddingTop: Int, chartWidth: Int, chartHeight: Int) {
        val barCount = targetDataList.size
        if (barCount == 0) return

        // 定義與 grid 相同的 offset
        val offset = dpToPx(12f)
        // 使用有效寬度，使 bars 不超過 x 軸的白線
        val effectiveChartWidth = chartWidth - offset

        // 根據 target 資料的 secTime 計算總秒數
        val totalSecTime = targetDataList.sumOf { it.secTime }
        var currentX = paddingLeft.toFloat()

        // 計算 target 與 outcome 的最小 top 值，作為各自漸層 shader 的起始點
        var minTargetBarTop = (paddingTop + chartHeight).toDouble()
        var minOutcomeBarTop = (paddingTop + chartHeight).toDouble()
        for (i in 0 until barCount) {
            val targetBean = targetDataList[i]
            val finalTargetHeight = (targetBean.value / maxDataValue) * chartHeight
            val finalTargetTop = paddingTop + chartHeight - finalTargetHeight
            if (targetBean.value > 0) {
                minTargetBarTop = minTargetBarTop.coerceAtMost(finalTargetTop)
            }
            val outcomeValue = if (i < outcomeDataList.size) outcomeDataList[i].value else 0.0
            val finalOutcomeHeight = (outcomeValue / maxDataValue) * chartHeight
            val finalOutcomeTop = paddingTop + chartHeight - finalOutcomeHeight
            if (outcomeValue > 0) {
                minOutcomeBarTop = minOutcomeBarTop.coerceAtMost(finalOutcomeTop)
            }
        }
        if (minTargetBarTop >= (paddingTop + chartHeight)) {
            minTargetBarTop = paddingTop.toDouble()
        }
        if (minOutcomeBarTop >= (paddingTop + chartHeight)) {
            minOutcomeBarTop = paddingTop.toDouble()
        }

        // 設定 target 的漸層 shader
        if (useGradientTarget) {
            val targetGradient = LinearGradient(
                0f,
                minTargetBarTop.toFloat(),
                0f,
                (paddingTop + chartHeight).toFloat(),
                targetGradientStartColor,
                targetGradientEndColor,
                Shader.TileMode.CLAMP
            )
            barPaintTarget.shader = targetGradient
        } else {
            barPaintTarget.shader = null
            barPaintTarget.color = targetGradientStartColor
        }

        // 設定 outcome 的漸層 shader
        if (useGradientOutcome) {
            val outcomeGradient = LinearGradient(
                0f,
                minOutcomeBarTop.toFloat(),
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

        // 繪製 target bar（不受水平動畫影響）
        for (i in 0 until barCount) {
            val bean = targetDataList[i]
            val barWidth = (bean.secTime.toFloat() / totalSecTime.toFloat()) * effectiveChartWidth
            val left = currentX
            val right = left + barWidth

            val animatedTargetHeight = (bean.value / maxDataValue) * chartHeight * targetAnimationProgress
            val animatedTargetTop = paddingTop + chartHeight - animatedTargetHeight

            canvas.drawRect(
                left,
                animatedTargetTop.toFloat(),
                right,
                (paddingTop + chartHeight).toFloat(),
                barPaintTarget
            )
            currentX += barWidth
        }

        // 繪製 outcome bar，保留水平動畫效果（利用 clipRect 從左側逐漸呈現）
        currentX = paddingLeft.toFloat()
        canvas.save()
        val clipRight = paddingLeft.toFloat() + effectiveChartWidth * horizontalAnimationProgress
        canvas.clipRect(
            paddingLeft.toFloat(),
            paddingTop.toFloat(),
            clipRight,
            (paddingTop + chartHeight).toFloat()
        )
        for (i in 0 until barCount) {
            val bean = targetDataList[i]
            val barWidth = (bean.secTime.toFloat() / totalSecTime.toFloat()) * effectiveChartWidth
            val left = currentX
            val right = left + barWidth

            val outcomeValue = if (i < outcomeDataList.size) outcomeDataList[i].value else 0.0
            val animatedOutcomeHeight = (outcomeValue / maxDataValue) * chartHeight * outcomeAnimationProgress
            val animatedOutcomeTop = paddingTop + chartHeight - animatedOutcomeHeight

            canvas.drawRect(
                left,
                animatedOutcomeTop.toFloat(),
                right,
                (paddingTop + chartHeight).toFloat(),
                barPaintOutcome
            )
            currentX += barWidth
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
