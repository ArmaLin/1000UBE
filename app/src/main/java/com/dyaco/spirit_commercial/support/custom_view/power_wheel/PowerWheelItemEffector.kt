package com.dyaco.spirit_commercial.support.custom_view.power_wheel

import android.content.Context
import android.content.res.Resources
import android.graphics.Typeface
import android.util.TypedValue
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.FontRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.cheonjaeung.powerwheelpicker.android.WheelPicker
import com.dyaco.spirit_commercial.R
import timber.log.Timber
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sign

/**
 * @param context            上下文
 * @param wheelPicker        WheelPicker 實例
 * @param selectedColorRes   選中時的 "顏色" 資源 ID
 * @param itemColorRes       未選中時的 "顏色" 資源 ID
 * @param selectedFontRes    選中時的 "字型" 資源 ID
 * @param itemFontRes      未選中時的 "字型" 資源 ID
 * @param baseTextSizeSP     "中心項目" 的文字大小 (用 SP，例如 24f)
 * @param textScaleFactor    "文字" 縮放因子 (例如 0.8f)
 * @param baseItemHeightDP   "單一項目" 的高度 (用 DP，例如 72f)
 * @param firstGapScale       "首層間距" 倍率 (例如 1.2f, 代表 120%)
 * @param gapFalloffFactor   "間距遞減率" (例如 0.8f, 代表後一層是前一層的 80%)
 */
class PowerWheelItemEffector(
    context: Context,
    private val wheelPicker: WheelPicker,
    @ColorRes selectedColorRes: Int,
    @ColorRes itemColorRes: Int,
    @FontRes selectedFontRes: Int,
    @FontRes itemFontRes: Int,
    baseTextSizeSP: Float,
    private val textScaleFactor: Float,
    baseItemHeightDP: Float,
    firstGapScale: Float,
    gapFalloffFactor: Float
) : WheelPicker.ItemEffector() {


    fun interface OnSettledListener {
        fun onSettled(position: Int, value: String?)
    }

    private var onSettledListener: OnSettledListener? = null
    private var lastSettledPosition: Int = WheelPicker.NO_POSITION


    private val selectedColor: Int = ContextCompat.getColor(context, selectedColorRes)
    private val itemsColor: Int = ContextCompat.getColor(context, itemColorRes)
    private val selectedTypeface: Typeface
    private val itemTypeface: Typeface

    private val baseTextSizePx: Float

    private val baseItemHeightPx: Float
    private val cumulativeGapsPx: FloatArray

    companion object {
        private const val MAX_OFFSET = 10
    }

    init {
        val (bold, regular) = try {
            val boldFont = ResourcesCompat.getFont(context, selectedFontRes) ?: Typeface.DEFAULT_BOLD
            val regularFont = ResourcesCompat.getFont(context, itemFontRes) ?: Typeface.DEFAULT
            boldFont to regularFont
        } catch (e: Resources.NotFoundException) {
            Timber.e(e, "字型資源找不到，使用系統預設")
            Typeface.DEFAULT_BOLD to Typeface.DEFAULT
        }
        this.selectedTypeface = bold
        this.itemTypeface = regular

        val res = context.resources
        this.baseTextSizePx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP, baseTextSizeSP, res.displayMetrics
        )
        this.baseItemHeightPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, baseItemHeightDP, res.displayMetrics
        )

        this.cumulativeGapsPx = FloatArray(MAX_OFFSET).apply {
            this[0] = 0f

            var currentGapPx = this@PowerWheelItemEffector.baseItemHeightPx * firstGapScale
            var cumulativeSum = 0f

            for (i in 1 until MAX_OFFSET) {
                cumulativeSum += currentGapPx
                this[i] = cumulativeSum
                currentGapPx *= gapFalloffFactor
            }
        }
    }


    fun setOnSettledListener(listener: OnSettledListener?) {
        this.onSettledListener = listener
    }

    override fun applyEffectOnScrollStateChanged(
        view: View,
        newState: Int,
        positionOffset: Int,
        centerOffset: Int
    ) {
        if (newState == WheelPicker.SCROLL_STATE_IDLE) {
            val currentPosition = wheelPicker.currentPosition
            if (currentPosition == WheelPicker.NO_POSITION) {
                return
            }

            if (currentPosition != lastSettledPosition) {
                val oldPosition = lastSettledPosition
                lastSettledPosition = currentPosition

                val adapter = wheelPicker.adapter as? PowerWheelAdapter
                val value = adapter?.getValueAt(currentPosition)

                onSettledListener?.onSettled(currentPosition, value)

                wheelPicker.performHapticFeedback(HapticFeedbackConstants.CONFIRM)

                Timber.d("滾輪已停止，【真正選中】: $currentPosition (舊: $oldPosition)")
            } else {
                Timber.d("滾輪 IDLE (Snap)，位置未變: $currentPosition (忽略)")
            }
        }
    }

    override fun applyEffectOnScrolled(
        view: View,
        delta: Int,
        positionOffset: Int,
        centerOffset: Int
    ) {
        val textView = view.findViewById<TextView>(R.id.tv_wheel_item) ?: return
        val adapter = wheelPicker.adapter ?: return
        if (adapter.itemCount == 0) return

        val height = wheelPicker.measuredHeight.toFloat()
        if (height == 0f) return

        val half = adapter.itemCount / 2
        val normalizedOffset = when {
            positionOffset > half -> positionOffset - adapter.itemCount
            positionOffset < -half -> positionOffset + adapter.itemCount
            else -> positionOffset
        }

        val alpha = 1f - abs(centerOffset) / (height / 2f)
        view.alpha = alpha

        val absOffset = abs(normalizedOffset).coerceAtMost(MAX_OFFSET - 1)

        val scale = textScaleFactor.pow(absOffset.toFloat())
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, baseTextSizePx * scale)

        if (normalizedOffset == 0) {
            textView.setTextColor(selectedColor)
            if (textView.typeface != selectedTypeface) {
                textView.typeface = selectedTypeface
            }
        } else {
            textView.setTextColor(itemsColor)
            if (textView.typeface != itemTypeface) {
                textView.typeface = itemTypeface
            }
        }

        val sign = normalizedOffset.sign
        val layoutY = normalizedOffset * baseItemHeightPx
        val visualY = sign * cumulativeGapsPx[absOffset]

        view.translationY = visualY - layoutY
    }

    override fun applyEffectOnItemSelected(view: View, position: Int) {
        view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
    }
}