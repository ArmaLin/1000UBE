package com.dyaco.spirit_commercial.support.custom_view.gaugeseekbar

import android.graphics.Canvas
import android.graphics.PointF
import kotlin.math.cos
import kotlin.math.sin

class ThumbEntity(private val centerPosition: PointF,
                  private var progress: Float,
                  private val startAngle: Float,
                  private val thumbRadius: Float,
                  private val thumbDrawable: ThumbDrawable) {

    companion object {
        private const val DEGREE_TO_RADIAN_RATIO = 0.0174533
    }

    init {
        updatePosition(progress)
    }

    fun draw(canvas: Canvas, progress: Float) {
        this.progress = progress

        updatePosition(progress)

        thumbDrawable.draw(canvas,progress)
    }

    private fun updatePosition(progress: Float) {
        val seekBarRadius = centerPosition.x.coerceAtMost(centerPosition.y) - thumbRadius

        val angle = (startAngle + (360 - 2 * startAngle) * progress) * DEGREE_TO_RADIAN_RATIO


        val indicatorX = centerPosition.x - sin(angle) * seekBarRadius //左右
        val indicatorY = cos(angle) * seekBarRadius + centerPosition.y   //(+50 下面變短)

        thumbDrawable.setBounds(
                (indicatorX - thumbRadius).toInt(),
                (indicatorY - thumbRadius).toInt(),
                (indicatorX + thumbRadius).toInt(),
                (indicatorY + thumbRadius).toInt())

//        thumbDrawable.setBounds(
//                (indicatorX - 5).toInt(),
//                (indicatorY - 66).toInt(),
//                (indicatorX + 5).toInt(),
//                (indicatorY + 66).toInt())

     //   thumbDrawable.setBounds(indicatorX.toInt(),indicatorY.toInt(),indicatorX.toInt(),indicatorY.toInt())
    }
}