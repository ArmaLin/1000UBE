package com.dyaco.spirit_commercial.support.custom_view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.animation.LinearInterpolator
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.dyaco.spirit_commercial.R
import kotlin.math.sin

class WavyTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatTextView(context, attrs) {

    companion object {
        const val WAVE_TYPE_NONE = 0
        const val WAVE_TYPE_OFFSET = 1
        const val WAVE_TYPE_COLOR = 2
        const val WAVE_TYPE_BOTH = 3
    }

    private var waveType = WAVE_TYPE_BOTH
    private var animator: ValueAnimator? = null
    private var animatedPhase: Float = 0f
    private var isAnimating = false
    private var shouldDrawWave = false

    private val wavePaint = TextPaint()
    private var charArray: CharArray = charArrayOf()

    private val normalColor = currentTextColor
//    private val waveColor = ContextCompat.getColor(context, R.color.white_20)
private val waveColor: Int = Color.argb(
    128, // 50% 的透明度 (128/255)
    Color.red(currentTextColor),
    Color.green(currentTextColor),
    Color.blue(currentTextColor)
)

    fun setWaveType(type: Int) {
        waveType = type
    }

    override fun onDraw(canvas: Canvas) {
        val textStr = text?.toString() ?: return

        if (!shouldDrawWave || waveType == WAVE_TYPE_NONE) {
            super.onDraw(canvas)
            return
        }

        wavePaint.set(paint)

        if (charArray.size != textStr.length) {
            charArray = textStr.toCharArray()
        }

        val phase = animatedPhase
        var x = paddingLeft.toFloat()
        val yBase = baseline.toFloat()

        for ((i, c) in charArray.withIndex()) {
            if (i == 0) {
                wavePaint.color = normalColor
                canvas.drawText(c.toString(), x, yBase, wavePaint)
            } else {
                val yOffset = when (waveType) {
                    WAVE_TYPE_OFFSET, WAVE_TYPE_BOTH ->
                        (sin((i + phase * charArray.size) * 0.6) * 10f).toFloat()
                    else -> 0f
                }

                val color = when (waveType) {
                    WAVE_TYPE_COLOR, WAVE_TYPE_BOTH -> {
                        val waveProgress = (i - phase * charArray.size)
                        val alphaFactor = ((sin(waveProgress * 0.6) + 1f) / 2f).toFloat()
                        blendColors(normalColor, waveColor, alphaFactor)
                    }
                    else -> normalColor
                }

                wavePaint.color = color
                canvas.drawText(c.toString(), x, yBase + yOffset, wavePaint)
            }

            x += wavePaint.measureText(c.toString())
        }
    }

    fun startWaveOnce(duration: Long = 1000L) {
        if (isAnimating) return

        shouldDrawWave = true
        isAnimating = true

        animator = ValueAnimator.ofFloat(0f, 1f).apply {
            this.duration = duration
            interpolator = LinearInterpolator()
            addUpdateListener {
                animatedPhase = it.animatedValue as Float
                invalidate()
            }
            addListener(object : android.animation.Animator.AnimatorListener {
                override fun onAnimationStart(animation: android.animation.Animator) {}
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    isAnimating = false
                    animatedPhase = 0f
                    shouldDrawWave = false
                    invalidate()
                }

                override fun onAnimationCancel(animation: android.animation.Animator) {}
                override fun onAnimationRepeat(animation: android.animation.Animator) {}
            })
            start()
        }
    }

    private fun blendColors(color1: Int, color2: Int, ratio: Float): Int {
        val inverseRatio = 1f - ratio
        val a = Color.alpha(color1) * inverseRatio + Color.alpha(color2) * ratio
        val r = Color.red(color1) * inverseRatio + Color.red(color2) * ratio
        val g = Color.green(color1) * inverseRatio + Color.green(color2) * ratio
        val b = Color.blue(color1) * inverseRatio + Color.blue(color2) * ratio
        return Color.argb(a.toInt(), r.toInt(), g.toInt(), b.toInt())
    }
}
