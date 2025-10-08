package com.dyaco.spirit_commercial.support.custom_view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.dyaco.spirit_commercial.R
import kotlin.math.roundToInt

class ProfileChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    // 資料：level、incline 最多兩組
    private var levelData: IntArray = IntArray(0)
    private var inclineData: IntArray? = null

    // 屬性：bar 間距、最大值
    private var barSpacing: Float = dpToPx(2f)
    private var maxValue: Int = 100

    // Paint：綠／淺藍／深藍
    private val paintLevel   = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paintIncline = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paintOverlap = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        attrs?.let {
            val a = context.obtainStyledAttributes(
                it, R.styleable.ProfileChartView, 0, 0
            )
            barSpacing = a.getDimension(
                R.styleable.ProfileChartView_barSpacingCustom,
                barSpacing
            )
            maxValue = a.getInteger(
                R.styleable.ProfileChartView_maxValueCustom,
                maxValue
            )
            paintLevel.color = a.getColor(
                R.styleable.ProfileChartView_levelColorCustom,
                ContextCompat.getColor(context, android.R.color.holo_green_light)
            )
            paintIncline.color = a.getColor(
                R.styleable.ProfileChartView_inclineColorCustom,
                ContextCompat.getColor(context, android.R.color.holo_blue_light)
            )
            paintOverlap.color = a.getColor(
                R.styleable.ProfileChartView_overlapColorCustom,
                ContextCompat.getColor(context, android.R.color.holo_blue_dark)
            )
            a.recycle()
        }
    }

    /** 最少一組 level，第二組 incline 預設可不帶 */
    @JvmOverloads
    fun setData(levelData: IntArray, inclineData: IntArray? = null) {
        this.levelData = levelData
        this.inclineData = inclineData
        requestLayout()
        invalidate()
    }

    /** 設定最大值 */
    fun setMaxValue(maxValue: Int) {
        this.maxValue = maxValue
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // 直接撐滿父容器：寬度用父給值，高度同樣用父給值
        val w = MeasureSpec.getSize(widthMeasureSpec)
        val h = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(w, h)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (levelData.isEmpty()) return

        val chartHeight = height - paddingTop - paddingBottom
        val count = levelData.size.coerceAtLeast(inclineData?.size ?: 0)
        if (count <= 0) return

        // 計算動態 barWidth：(可用寬度 - 總間距) / count
        val totalSpacing   = (count - 1) * barSpacing
        val availableWidth = width - paddingLeft - paddingRight - totalSpacing
        val barWidthDynamic = availableWidth / count

        val bottom = height - paddingBottom.toFloat()

        for (i in 0 until count) {
            val x = paddingLeft + i * (barWidthDynamic + barSpacing)

            val level   = levelData.getOrNull(i) ?: 0
            val incline = inclineData?.getOrNull(i) ?: 0

            val levelH   = (level.toFloat()   / maxValue * chartHeight).coerceAtMost(chartHeight.toFloat())
            val inclineH = (incline.toFloat() / maxValue * chartHeight).coerceAtMost(chartHeight.toFloat())

            // 1️⃣ 畫 incline（淺藍）
            canvas.drawRect(
                x, bottom - inclineH,
                x + barWidthDynamic, bottom,
                paintIncline
            )

            // 2️⃣ 重疊部分（深藍）
            val overlapH = minOf(levelH, inclineH)
            if (overlapH > 0f) {
                canvas.drawRect(
                    x, bottom - overlapH,
                    x + barWidthDynamic, bottom,
                    paintOverlap
                )
            }

            // 3️⃣ level 超出 incline 部分（綠）
            if (levelH > inclineH) {
                canvas.drawRect(
                    x, bottom - levelH,
                    x + barWidthDynamic, bottom - inclineH,
                    paintLevel
                )
            }
        }
    }

    private fun dpToPx(dp: Float): Float =
        dp * resources.displayMetrics.density


    companion object {
        /**
         * 將 “7#11#12#…” 這種格式轉成 IntArray，Java 可直接呼叫：
         * int[] arr = CustomBarChartView.parseDataString("7#11#…");
         */
        @JvmStatic
        fun parseDataString(dataStr: String): IntArray {
            return dataStr
                .split("#")
                .mapNotNull { it.toIntOrNull() }
                .toIntArray()
        }

        /**
         * 將 “7#11#12#…” 這種原始字串
         * 先轉成數字，除以 2，四捨五入後回傳 IntArray
         */
        @JvmStatic
        fun parseInclineDataString(dataStr: String): IntArray {
            return dataStr
                .split("#")
                .mapNotNull { it.toDoubleOrNull() }
                .map { (it / 2).roundToInt() }
                .toIntArray()
        }
    }
}
