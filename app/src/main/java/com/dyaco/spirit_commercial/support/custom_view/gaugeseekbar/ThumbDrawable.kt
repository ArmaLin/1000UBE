package com.dyaco.spirit_commercial.support.custom_view.gaugeseekbar

import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.Log
import kotlin.math.cos
import kotlin.math.sin

class ThumbDrawable(thumbColor: Int,private var progress: Float) : Drawable() {

    private val whitePaint = Paint().apply {
        color = Color.WHITE
      //  alpha = 255
        style = Paint.Style.FILL
        strokeWidth = 8f
        isAntiAlias = true
    }

    private val thumbOuterPaint = Paint().apply {
        isAntiAlias = true
        color = thumbColor
        alpha = 102
    }

    private val thumbInnerPaint = Paint().apply {
        isAntiAlias = true
        color = thumbColor
    }

    fun draw(canvas: Canvas, progress: Float) {
        this.progress = progress
        draw(canvas)
    }

    override fun draw(canvas: Canvas) {
        val centerX = bounds.exactCenterX()
        val centerY = bounds.exactCenterY()
        val radius = centerX - bounds.left // 50

        Log.d("PEPEPPE", "draw:$centerX, $centerY ,$radius")

        val x = (centerX  + cos(Math.toRadians(radius.toDouble())) * radius.toDouble()).toFloat()
        val y = (centerY + sin(Math.toRadians(radius.toDouble())) * radius.toDouble()).toFloat()

        canvas.apply {
//            drawCircle(centerX, centerY, radius, thumbOuterPaint)
//            drawCircle(centerX, centerY, radius / 2f, thumbInnerPaint)
//            drawCircle(centerX, centerY, 3f, whitePaint)

           // drawLine(centerX + (progress * 100), centerY - 30, centerX - (progress * 100),centerY + 50, whitePaint)

          //  drawLine(x, y, x - (progress * 1000),y + (progress * 1000), whitePaint)
            drawLine(x - (progress * 1000), y + (progress * 1000), x ,y, whitePaint)
        }
    }

    override fun setAlpha(alpha: Int) {}

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

    override fun setColorFilter(colorFilter: ColorFilter?) {}
}