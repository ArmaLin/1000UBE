//// app/src/main/kotlin/com/dyaco/spirit_commercial/support/custom_view/CircleBarView.kt
//package com.dyaco.spirit_commercial.support.custom_view
//
//import android.animation.ValueAnimator
//import android.content.Context
//import android.content.res.Resources
//import android.graphics.*
//import android.graphics.drawable.ColorDrawable
//import android.util.AttributeSet
//import android.util.TypedValue
//import android.view.MotionEvent
//import android.view.View
//import com.dyaco.spirit_commercial.R
//
//class CircleBarView @JvmOverloads constructor(
//    context: Context,
//    attrs: AttributeSet? = null,
//    defStyle: Int = 0
//) : View(context, attrs, defStyle) {
//
//    // ------ 尺寸 & 觸控 ------
//    private var viewWidth = 0
//    private var viewHeight = 0
//    private var touchable = true
//    private val touchTolerance = 4f
//    private var currentX = 0f
//    private var currentY = 0f
//
//    // ------ 手指軌跡 ------
//    private val fingerPath = Path()
//    private val fingerPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG).apply {
//        style = Paint.Style.STROKE
//        strokeJoin = Paint.Join.ROUND
//        strokeCap = Paint.Cap.ROUND
//        strokeWidth = 12f
//    }
//    private var paintFingerPath = false
//    private var fingerPaintColor = Color.BLUE
//
//    // ------ Bar 參數 ------
//    private var barCount = 20
//    private var barMaxLevel = 10
//    private var barMinLevel = 1
//    private var barCircleRadius = 15f
//    private var barCircleWidth = 5f
//    private var sideWidth = 0f
//
//    // ------ 畫筆 ------
//    private val backRectPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG).apply {
//        style = Paint.Style.FILL
//    }
//    private val backgroundColorPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG).apply {
//        style = Paint.Style.FILL
//    }
//    private val barPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG).apply {
//        style = Paint.Style.FILL
//    }
//    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG).apply {
//        style = Paint.Style.FILL
//    }
//    private val circlePaintB = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG).apply {
//        style = Paint.Style.STROKE
//        strokeWidth = 4f
//    }
//    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG).apply {
//        style = Paint.Style.STROKE
//        strokeWidth = 4f
//    }
//
//    // ------ 顏色預設 ------
//    private var backColor = Color.parseColor("#E0E0E0")
//    private var barColor = Color.parseColor("#999999")
//    private var barSelectSolidColor = Color.parseColor("#9AFF35")
//    private var barSelectColor = Color.parseColor("#9D2227")
//    private var lineColor = Color.parseColor("#9D2227")
//
//    // ------ 連線動畫變數 ------
//    private var animSegmentIndex = -1
//    private var animStartX = 0f
//    private var animStartY = 0f
//    private var animEndX = 0f
//    private var animEndY = 0f
//    private var animFraction = 0f
//    private val connectAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
//        duration = 300
//        addUpdateListener {
//            animFraction = it.animatedValue as Float
//            invalidate()
//        }
//        // 不自動重複、只跑一次
//    }
//
//    // ------ 計算單位 ------
//    private var levelUnitX = 0f
//    private var levelUnitY = 0f
//
//    // ------ 資料 & 監聽 ------
//    private lateinit var bars: Array<Bar>
//    private var levelChangedListener: LevelChangedListener? = null
//    private var onLevelTouchUpListener: OnLevelTouchUpListener? = null
//
//    init {
//        attrs?.let { initAttr(it) }
//        initBars()
//        applyPaintSettings()
//    }
//
//    private fun initAttr(attributeSet: AttributeSet) {
//        context.obtainStyledAttributes(attributeSet, R.styleable.CircleBarView).apply {
//            paintFingerPath     = getBoolean(R.styleable.CircleBarView_paintFingerPath, paintFingerPath)
//            fingerPaintColor    = getColor(R.styleable.CircleBarView_fingerPaintColor, fingerPaintColor)
//            touchable           = getBoolean(R.styleable.CircleBarView_touchable, touchable)
//
//            barCount            = getInt(R.styleable.CircleBarView_barCount, barCount)
//            barMaxLevel         = getInt(R.styleable.CircleBarView_barMaxLevel, barMaxLevel)
//            barMinLevel         = getInt(R.styleable.CircleBarView_barMinLevel, barMinLevel)
//
//            barColor            = getColor(R.styleable.CircleBarView_barColor, barColor)
//            backColor           = getColor(R.styleable.CircleBarView_backColor, backColor)
//            barSelectColor      = getColor(R.styleable.CircleBarView_barSelectColor, barSelectColor)
//            barSelectSolidColor = getColor(R.styleable.CircleBarView_barSelectSolidColor, barSelectSolidColor)
//            lineColor           = getColor(R.styleable.CircleBarView_lineColor, lineColor)
//
//            linePaint.strokeWidth = getInt(R.styleable.CircleBarView_pb_lineWidth, linePaint.strokeWidth.toInt()).toFloat()
//
//            fun readDim(idx: Int, def: Float): Float {
//                return if (hasValue(idx)) {
//                    val tv = peekValue(idx)
//                    when (tv?.type) {
//                        TypedValue.TYPE_DIMENSION -> getDimension(idx, def)
//                        TypedValue.TYPE_FLOAT     -> tv.float
//                        TypedValue.TYPE_INT_DEC,
//                        TypedValue.TYPE_INT_HEX   -> tv.data.toFloat()
//                        else                      -> def
//                    }
//                } else def
//            }
//            barCircleRadius = readDim(R.styleable.CircleBarView_barCircleRadius, barCircleRadius)
//            barCircleWidth  = readDim(R.styleable.CircleBarView_barCircleWidth, barCircleWidth)
//            sideWidth       = readDim(R.styleable.CircleBarView_sideWidth, sideWidth)
//
//            recycle()
//        }
//    }
//
//    private fun initBars() {
//        bars = Array(barCount) { Bar() }
//    }
//
//    private fun applyPaintSettings() {
//        fingerPaint.color      = fingerPaintColor
//        backRectPaint.color    = backColor
//        barPaint.apply {
//            color      = barColor
//            strokeWidth = barCircleWidth
//        }
//        circlePaint.color      = Color.parseColor("#445565") // 跟 Java 版一致
//        circlePaintB.color     = barSelectColor
//        linePaint.color        = lineColor
//        backgroundColorPaint.color =
//            (background as? ColorDrawable)?.color ?: barSelectSolidColor
//    }
//
//    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
//        super.onSizeChanged(w, h, oldw, oldh)
//        viewWidth   = (w - sideWidth * 2).toInt()
//        viewHeight  = h
//        levelUnitX  = viewWidth   / barCount.toFloat()
//        levelUnitY  = viewHeight  / barMaxLevel.toFloat()
//    }
//
//    override fun onDraw(canvas: Canvas) {
//        super.onDraw(canvas)
//        drawBackgroundCircles(canvas)
//        drawLinkLines(canvas)
//        drawSelectedBars(canvas)
//        if (paintFingerPath) canvas.drawPath(fingerPath, fingerPaint)
//    }
//
//    private fun drawBackgroundCircles(canvas: Canvas) {
//        repeat(barCount) { i ->
//            val cx = sideWidth + levelUnitX / 2 + levelUnitX * i
//            repeat(barMaxLevel) { j ->
//                val cy = levelUnitY / 2 + levelUnitY * (barMaxLevel - j - 1)
//                canvas.drawCircle(cx, cy, barCircleRadius, backRectPaint)
//                canvas.drawCircle(cx, cy, barCircleWidth, barPaint)
//            }
//        }
//    }
//
//    private fun drawLinkLines(canvas: Canvas) {
//        for (i in 1 until bars.size) {
//            val c = bars[i - 1]
//            val n = bars[i]
//            if (c.centerX != 0f && c.centerY != 0f &&
//                n.centerX != 0f && n.centerY != 0f) {
//                if (i == animSegmentIndex && connectAnimator.isRunning) {
//                    // 正在動畫：只畫到 animFraction
//                    val ex = c.centerX + (n.centerX - c.centerX) * animFraction
//                    val ey = c.centerY + (n.centerY - c.centerY) * animFraction
//                    canvas.drawLine(c.centerX, c.centerY, ex, ey, linePaint)
//                    break
//                } else {
//                    // 靜態完整線
//                    canvas.drawLine(c.centerX, c.centerY, n.centerX, n.centerY, linePaint)
//                }
//            }
//        }
//    }
//
//    private fun drawSelectedBars(canvas: Canvas) {
//        val dotRadius = 4f
//        bars.filter { it.level > 0 }.forEach { b ->
//            canvas.drawCircle(b.centerX, b.centerY, barCircleRadius, circlePaint)
//            canvas.drawCircle(b.centerX, b.centerY, barCircleRadius, circlePaintB)
//            canvas.drawCircle(b.centerX, b.centerY, dotRadius, backgroundColorPaint)
//        }
//    }
//
//    override fun onTouchEvent(event: MotionEvent): Boolean {
//        if (!touchable) return false
//        val x = event.x; val y = event.y
//        when (event.action) {
//            MotionEvent.ACTION_DOWN -> {
//                // 只在新的連線開始時啟動動畫
//                // 如果手指剛碰到，就認為要開始連 A -> B
//                // prevIndex 要是 i-1
//                // 暫時不做任何動畫，真正的動畫觸發留給 updateBar
//                fingerPath.reset()
//                fingerPath.moveTo(x, y)
//                fingerPath.lineTo(x + 1, y + 1)
//                currentX = x; currentY = y
//                updateBar(x, y)
//                invalidate()
//            }
//            MotionEvent.ACTION_MOVE -> {
//                val dx = kotlin.math.abs(x - currentX)
//                val dy = kotlin.math.abs(y - currentY)
//                if (dx >= touchTolerance || dy >= touchTolerance) {
//                    fingerPath.quadTo(
//                        currentX, currentY,
//                        (x + currentX) / 2, (y + currentY) / 2
//                    )
//                    currentX = x; currentY = y
//                    updateBar(x, y)
//                    invalidate()
//                }
//            }
//            MotionEvent.ACTION_UP -> {
//                onLevelTouchUpListener?.onTouchUp()
//                fingerPath.reset()
//                invalidate()
//                performClick()
//            }
//        }
//        return true
//    }
//
//    private fun updateBar(x: Float, y: Float) {
//        val idx = ((x - sideWidth) / levelUnitX).toInt().coerceIn(0, barCount - 1)
//        val lvl = (barMaxLevel - (y / levelUnitY).toInt()).coerceIn(barMinLevel, barMaxLevel)
//
//        // 設定 bar 資料
//        bars[idx].apply {
//            level   = lvl
//            centerX = sideWidth + levelUnitX / 2 + levelUnitX * idx
//            centerY = levelUnitY / 2 + levelUnitY * (barMaxLevel - lvl)
//            isSelect = true
//        }
//
//        // 如果有前一個 bar 且它已經有 center，就對這段線做動畫
//        if (idx > 0 && bars[idx - 1].centerX != 0f) {
//            val prev = bars[idx - 1]
//            animSegmentIndex = idx
//            animStartX = prev.centerX
//            animStartY = prev.centerY
//            animEndX = bars[idx].centerX
//            animEndY = bars[idx].centerY
//            animFraction = 0f
//            connectAnimator.cancel()
//            connectAnimator.start()
//        }
//
//        levelChangedListener?.onLevelChanged(idx, lvl)
//        if (idx == barCount - 1) levelChangedListener?.onLastBarSelect(true)
//    }
//
//    override fun performClick(): Boolean {
//        super.performClick()
//        return true
//    }
//
//    // ==== 公有 API ====
//    fun setBarLevel(bar: Int, level: Int): Boolean {
//        if (bar !in 0 until barCount || level !in barMinLevel..barMaxLevel) return false
//        bars[bar].apply {
//            this.level   = level
//            centerX = sideWidth + levelUnitX / 2 + levelUnitX * bar
//            centerY = levelUnitY / 2 + levelUnitY * (barMaxLevel - level)
//        }
//        invalidate()
//        return true
//    }
//    fun getBars(): Array<Bar>                       = bars
//    fun getBarCount(): Int                         = barCount
//    fun getBarMaxLevel(): Int                      = barMaxLevel
//    fun getBarMinLevel(): Int                      = barMinLevel
//    fun setLevelChangedListener(l: LevelChangedListener)    = run { levelChangedListener = l }
//    fun setOnLevelTouchUpListener(l: OnLevelTouchUpListener)= run { onLevelTouchUpListener = l }
//    fun reset()                                    = run { initBars(); invalidate() }
//    fun setLineColor(c: Int)                      = run { lineColor = c; linePaint.color = c; invalidate() }
//    fun setBarSelectColor(c: Int)                 = run { barSelectColor = c; circlePaintB.color = c; invalidate() }
//    fun setBarMaxLevel(n: Int)                    = run { barMaxLevel = n; levelUnitY = viewHeight / barMaxLevel.toFloat(); invalidate() }
//
//    interface LevelChangedListener {
//        fun onLevelChanged(bar: Int, level: Int)
//        fun onLastBarSelect(isSelect: Boolean)
//    }
//    interface OnLevelTouchUpListener { fun onTouchUp() }
//
//    data class Bar(
//        var level: Int = 0,
//        var centerX: Float = 0f,
//        var centerY: Float = 0f,
//        var isSelect: Boolean = false
//    )
//
//    companion object {
//        @JvmStatic
//        fun dp2px(dp: Int): Float =
//            TypedValue.applyDimension(
//                TypedValue.COMPLEX_UNIT_DIP,
//                dp.toFloat(),
//                Resources.getSystem().displayMetrics
//            )
//    }
//}
