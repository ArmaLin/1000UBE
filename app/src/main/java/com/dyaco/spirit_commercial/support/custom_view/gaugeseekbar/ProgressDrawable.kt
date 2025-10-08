package com.dyaco.spirit_commercial.support.custom_view.gaugeseekbar

import android.graphics.*
import kotlin.math.asin

class ProgressDrawable(
    position: PointF,
    private var progress: Float,
    private val radiusPx: Float,
    private val margin: Float,
    private val gradientArray: IntArray,
    private val startAngle: Float,
    private val trackWidthPx: Float,
    private val strokeColor: Int,
    private var viewType: Int,
    gradientPositionsArray: FloatArray? = null
) : DrawableEntity(position) {

    private val gradientPositionsArray: FloatArray =
        if (gradientPositionsArray != null) {
            getGradientPositions(gradientPositionsArray)
        } else getGradientPositions(
            FloatArray(gradientArray.size) {
                it.toFloat() / (gradientArray.size - 1)
            })

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
        color = strokeColor
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
        style = Paint.Style.STROKE
    }

    private val strokePaintLine = Paint().apply {
        strokeWidth = 8f
        color = Color.WHITE
        isAntiAlias = true
        //strokeCap = Paint.Cap.ROUND
        style = Paint.Style.STROKE
    }

    private fun createSweepGradient(): SweepGradient {
        val shader =
            SweepGradient(centerPosition.x, centerPosition.y, gradientArray, gradientPositionsArray)
        val gradientRotationMatrix = Matrix()
        //code need to account for path width
        val angularMargin = Math.toDegrees(2 * asin((trackWidthPx / radiusPx).toDouble())).toFloat()
        gradientRotationMatrix.preRotate(
            90f + startAngle - angularMargin,
            centerPosition.x,
            centerPosition.y
        )
        shader.setLocalMatrix(gradientRotationMatrix)
        return shader
    }

    private fun getGradientPositions(gradientPositions: FloatArray): FloatArray {
        val normalizedStartAngle = startAngle / 360f
        val normalizedAvailableSpace = 1f - 2 * normalizedStartAngle

        return FloatArray(gradientPositions.size) {
            normalizedStartAngle + normalizedAvailableSpace * gradientPositions[it]
        }
    }

    fun draw(canvas: Canvas, progress: Float, viewType: Int) {
        this.progress = progress
        this.viewType = viewType
        draw(canvas)
    }

    override fun draw(canvas: Canvas) {
        val angle = ((360 - (startAngle * 2)) * progress)
      // if (angle > 0) {
            //Progress
            val rect = RectF(centerPosition.x - radiusPx + margin, centerPosition.y - radiusPx + margin, centerPosition.x + radiusPx - margin, centerPosition.y + radiusPx - margin)
            canvas.drawArc(rect, 90f + startAngle, angle, false, progressPaint)

            if (viewType == 0) {
                //Progress起點的黑線
                canvas.drawLine(
                    centerPosition.x,
                    centerPosition.y + radiusPx - trackWidthPx,
                    centerPosition.x,
                    centerPosition.y + radiusPx - (trackWidthPx * 2) - 2,
                    strokePaint1
                )
            }


            //Progress尾端的白線
            canvas.rotate(angle, centerPosition.x, centerPosition.y)
            canvas.drawLine(centerPosition.x,
                centerPosition.y + radiusPx, centerPosition.x,
                centerPosition.y + radiusPx - (trackWidthPx * 3), //白線內側起點，數字越大越短
                strokePaintLine)
    //    }
    }

    override fun setAlpha(alpha: Int) {}

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

    override fun setColorFilter(colorFilter: ColorFilter?) {}
}