package com.dyaco.spirit_commercial.support.custom_view.gaugeseekbar

import android.graphics.*

class TrackDrawable(position: PointF,
                    private val radiusPx: Float,
                    private val margin: Float,
                    private val gradientArray: IntArray,
                    private val startAngle: Float,
                    private val trackWidthPx: Float,
                    private val strokeColor: Int,
                    gradientPositionsArray: FloatArray? = null) : DrawableEntity(position) {

    private val gradientPositionsArray: FloatArray = gradientPositionsArray ?: FloatArray(gradientArray.size) { it.toFloat() / gradientArray.size }

    init {
        if (gradientArray.size != this.gradientPositionsArray.size)
            throw IllegalStateException("gradientArray and gradientPositionsArray sizes must be equal.")
    }

    private val progressPaint = Paint().apply {
        strokeWidth = trackWidthPx
        isAntiAlias = true
//        strokeCap = Paint.Cap.ROUND
        style = Paint.Style.STROKE

        if (gradientArray.size > 1) {
            val shader = createSweepGradient()
            setShader(shader)
        } else {
            color = gradientArray[0]
        }
    }

    private val strokePaint1 = Paint().apply {
        strokeWidth = 2f
        color =  strokeColor
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
        style = Paint.Style.STROKE
    }

    private fun createSweepGradient(): SweepGradient {
        val shader = SweepGradient(centerPosition.x, centerPosition.y, gradientArray, getGradientPositions())
        val gradientRotationMatrix = Matrix()
        gradientRotationMatrix.preRotate(90f + startAngle - 5, centerPosition.x, centerPosition.y)
        shader.setLocalMatrix(gradientRotationMatrix)
        return shader
    }

    private fun getGradientPositions(): FloatArray {
        val normalizedStartAngle = startAngle / 360f
        val normalizedAvailableSpace = 1f - 2 * normalizedStartAngle

        return FloatArray(gradientArray.size) {
            normalizedStartAngle + normalizedAvailableSpace * gradientPositionsArray[it]
        }
    }

    override fun draw(canvas: Canvas) {
        val angle = (360 - (startAngle * 2))
        val rect = RectF(centerPosition.x - radiusPx + margin,
                centerPosition.y - radiusPx + margin,
                centerPosition.x + radiusPx - margin,
                centerPosition.y + radiusPx - margin)
        canvas.drawArc(rect,
                90f + startAngle,
                angle,
                false,
                progressPaint)



        //兩條邊線
        val k = (trackWidthPx / 2)

        val rect2 = RectF(centerPosition.x - radiusPx + margin - k,
            centerPosition.y - radiusPx + margin - k,
            centerPosition.x + radiusPx - margin + k,
            centerPosition.y + radiusPx - margin + k)
        canvas.drawArc(rect2,
            startAngle,
            angle,
            false,
            strokePaint1)

        val rect3 = RectF(centerPosition.x - radiusPx + margin + k,
            centerPosition.y - radiusPx + margin + k,
            centerPosition.x + radiusPx - margin - k,
            centerPosition.y + radiusPx - margin - k)
        canvas.drawArc(rect3,
            startAngle,
            angle,
            false,
            strokePaint1)
    }

    override fun setAlpha(alpha: Int) {}

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

    override fun setColorFilter(colorFilter: ColorFilter?) {}


    /**
     * drawArc
     * 第一個參數 oval：定義承載圓弧形狀的矩形。通過設置該矩形可以指定圓弧的位置和大小。
     * 第二個參數 startAngle: 設置圓弧是從哪個角度順時針繪畫的。
     * 第三個參數 sweepAngle: 設置圓弧順時針掃過的角度。
     * 第四個參數 useCenter: 繪製的時候是否使用圓心，我們繪製圓弧的時候設置為false,如果設置為true, 並且當前畫筆的描邊屬性設置為Paint.Style.FILL的時候，畫出的就是扇形。
     * 第五個參數 paint: 指定繪製的畫筆。
     */
}