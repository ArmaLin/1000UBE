package com.dkcity.gym.support.common.customview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.OverScroller
import androidx.core.content.ContextCompat
import com.dkcity.gym.R
import kotlin.math.abs
import androidx.core.content.withStyledAttributes
import androidx.core.graphics.withScale


class WheelOptionPicker @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    interface OnItemSelectedListener {
        fun onItemSelected(index: Int, item: String)
    }

    private var listener: OnItemSelectedListener? = null

    private var data: List<String> = emptyList()
    private var selectedIndex = 0

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
    }
    private val dividerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var itemHeight = 0f
    private var itemTextColor: Int = Color.BLACK
    private var selectedItemTextColor: Int = Color.BLUE
    private var itemTextSize: Float = 0f
    private var visibleItemCount = 5
    private var dividerColor: Int = Color.LTGRAY
    private var dividerHeight: Float = dpToPx(2f)
    private var centerItemTop = 0f
    private var centerItemBottom = 0f

    private val scroller: OverScroller = OverScroller(context)
    private val gestureDetector: GestureDetector
    private var scrollOffsetY = 0f
    private var isFling = false

    private var isInitialPositionSet = false
    private var pendingSelectedIndex: Int? = null

    init {
        itemTextSize = spToPx(20f)
        attrs?.let {
            context.withStyledAttributes(it, R.styleable.WheelOptionPicker, 0, 0) {
                itemTextColor =
                    getColor(R.styleable.WheelOptionPicker_wop_itemTextColor, Color.BLACK)
                selectedItemTextColor = getColor(
                    R.styleable.WheelOptionPicker_wop_selectedItemTextColor,
                    ContextCompat.getColor(context, R.color.purple_500)
                )
                itemTextSize =
                    getDimension(R.styleable.WheelOptionPicker_wop_itemTextSize, itemTextSize)
                visibleItemCount = getInt(R.styleable.WheelOptionPicker_wop_visibleItemCount, 5)
                if (visibleItemCount % 2 == 0) {
                    visibleItemCount += 1
                }
                dividerColor =
                    getColor(R.styleable.WheelOptionPicker_wop_dividerColor, Color.LTGRAY)
                dividerHeight =
                    getDimension(R.styleable.WheelOptionPicker_wop_dividerHeight, dpToPx(1f))
            }
        }

        textPaint.textSize = itemTextSize

        gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDown(e: MotionEvent): Boolean {
                if (!scroller.isFinished) {
                    scroller.forceFinished(true)
                }
                isFling = false
                return true
            }

            override fun onScroll(
                e1: MotionEvent?,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                scrollOffsetY += distanceY
                invalidate()
                return true
            }

            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                isFling = true
                scroller.fling(
                    0, scrollOffsetY.toInt(),
                    0, -velocityY.toInt(),
                    0, 0,
                    getMinScrollY(), getMaxScrollY()
                )
                invalidate()
                return true
            }
        })
        isClickable = true
    }

    fun setData(newData: List<String>) {
        this.data = newData
        this.selectedIndex = 0
        this.scrollOffsetY = 0f
        scroller.forceFinished(true)
        this.isInitialPositionSet = false
        if (height > 0) {
            isInitialPositionSet = true
            listener?.onItemSelected(selectedIndex, data[selectedIndex])
        }
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (!isInitialPositionSet && data.isNotEmpty()) {
            isInitialPositionSet = true
            listener?.onItemSelected(selectedIndex, data[selectedIndex])
        }

        pendingSelectedIndex?.let { index ->
            setSelectedIndex(index, false)
            pendingSelectedIndex = null
        }
    }

    fun setOnItemSelectedListener(listener: OnItemSelectedListener) {
        this.listener = listener
    }

    fun getCurrentIndex(): Int {
        return selectedIndex
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        itemHeight = itemTextSize * 2
        val measuredHeight = if (heightMode == MeasureSpec.EXACTLY) {
            heightSize
        } else {
            (itemHeight * (visibleItemCount + 1) + paddingTop + paddingBottom).toInt()
        }

        val measuredWidth = MeasureSpec.getSize(widthMeasureSpec)
        setMeasuredDimension(measuredWidth, measuredHeight)

        centerItemTop = (measuredHeight - itemHeight) / 2
        centerItemBottom = (measuredHeight + itemHeight) / 2
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (data.isEmpty()) return

        val currentCenterIndex = calculateCurrentIndex()

        for (i in 0 until visibleItemCount) {
            val itemIndex = currentCenterIndex - (visibleItemCount / 2) + i
            if (itemIndex >= 0 && itemIndex < data.size) {
                drawItem(canvas, itemIndex, i)
            }
        }

        dividerPaint.color = dividerColor
        dividerPaint.strokeWidth = dividerHeight
        canvas.drawLine(0f, centerItemTop, width.toFloat(), centerItemTop, dividerPaint)
        canvas.drawLine(0f, centerItemBottom, width.toFloat(), centerItemBottom, dividerPaint)
    }

    private fun drawItem(canvas: Canvas, itemIndex: Int, visiblePosition: Int) {
        val itemText = data[itemIndex]

        val itemCenterY = (height / 2f) + (visiblePosition - visibleItemCount / 2) * itemHeight - (scrollOffsetY % itemHeight)

        val distanceFromCenter = abs(height / 2f - itemCenterY)
        val scale = 1f - (distanceFromCenter / (visibleItemCount / 2f * itemHeight)) * 0.25f
        val alpha = (255 * (1f - (distanceFromCenter / (visibleItemCount / 2f * itemHeight)) * 0.5f)).toInt()

        if (selectedIndex == itemIndex) {
            textPaint.color = selectedItemTextColor
            textPaint.typeface = Typeface.DEFAULT_BOLD
        } else {
            textPaint.color = itemTextColor
            textPaint.typeface = Typeface.DEFAULT
        }
        textPaint.alpha = alpha.coerceIn(50, 255)

        canvas.withScale(scale, scale, width / 2f, itemCenterY) {
            val textY = itemCenterY - (textPaint.descent() + textPaint.ascent()) / 2f
            drawText(itemText, width / 2f, textY, textPaint)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val handled = gestureDetector.onTouchEvent(event)
        if (!handled && event.action == MotionEvent.ACTION_UP) {
            if (!isFling) {
                snapToNearestItem()
            }
        }
        return handled
    }

    override fun computeScroll() {
        super.computeScroll()
        if (scroller.computeScrollOffset()) {
            scrollOffsetY = scroller.currY.toFloat()
            invalidate()
        } else if (isFling) {
            isFling = false
            snapToNearestItem()
        }
    }

    private fun snapToNearestItem() {
        val targetIndex = calculateCurrentIndex().coerceIn(0, data.size - 1)
        val finalOffsetY = targetIndex * itemHeight

        scroller.startScroll(0, scrollOffsetY.toInt(), 0, (finalOffsetY - scrollOffsetY).toInt())
        invalidate()

        if (selectedIndex != targetIndex) {
            selectedIndex = targetIndex
            listener?.onItemSelected(selectedIndex, data[selectedIndex])
        }
    }

    private fun calculateCurrentIndex(): Int {
        if (itemHeight == 0f) return 0
        return ((scrollOffsetY + itemHeight / 2) / itemHeight).toInt().coerceIn(0, data.size - 1)
    }

    private fun getMinScrollY(): Int = 0
    private fun getMaxScrollY(): Int = if (data.isEmpty()) 0 else ((data.size - 1) * itemHeight).toInt()

    private fun spToPx(sp: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, resources.displayMetrics)
    }

    private fun dpToPx(dp: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)
    }


    /**
     * 設定選中的項目 (根據索引)
     * @param index 要選中的項目索引
     * @param animated 是否要有滾動動畫
     */
    @JvmOverloads
    fun setSelectedIndex(index: Int, animated: Boolean = true) {
        if (index !in data.indices) {
            return
        }

        if (height > 0 && itemHeight > 0f) {
            pendingSelectedIndex = null
            selectedIndex = index
            val targetScrollY = index * itemHeight
            if (animated) {
                scroller.startScroll(0, scrollOffsetY.toInt(), 0, (targetScrollY - scrollOffsetY).toInt())
            } else {
                scrollOffsetY = targetScrollY
            }
            invalidate()
        } else {
            pendingSelectedIndex = index
            selectedIndex = index
        }
    }

    /**
     * 設定選中的項目 (根據內容值)
     * @param value 要選中的項目字串
     * @param animated 是否要有滾動動畫
     */
    @JvmOverloads
    fun setSelectedValue(value: String, animated: Boolean = true) {
        val index = data.indexOf(value)
        if (index != -1) {
            setSelectedIndex(index, animated)
        }
    }
}