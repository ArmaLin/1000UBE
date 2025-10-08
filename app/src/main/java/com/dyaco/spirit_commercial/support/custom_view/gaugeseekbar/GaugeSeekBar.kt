package com.dyaco.spirit_commercial.support.custom_view.gaugeseekbar

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.DimenRes
import androidx.annotation.FloatRange
import androidx.core.content.ContextCompat
import com.dyaco.spirit_commercial.MainActivity
import com.dyaco.spirit_commercial.R
import com.dyaco.spirit_commercial.workout.MainWorkoutTrainingFragment
import kotlin.math.atan2


class GaugeSeekBar : View {

    private companion object {
        private const val DEFAULT_START_ANGLE_DEG = 0f
        private const val DEFAULT_THUMB_RADIUS_DP = 11
        private const val DEFAULT_TRACK_WIDTH_DP = 8
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        applyAttributes(context.theme.obtainStyledAttributes(attrs, R.styleable.GaugeSeekBar, 0, 0))
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        applyAttributes(context.theme.obtainStyledAttributes(attrs, R.styleable.GaugeSeekBar, 0, 0))
    }

    private lateinit var bitmap: Bitmap
    private var radiusPx: Float = 0.0f
    private var thumbRadius = DEFAULT_THUMB_RADIUS_DP * resources.displayMetrics.density
    private var trackWidth = DEFAULT_TRACK_WIDTH_DP * resources.displayMetrics.density
    private var progressWidth = DEFAULT_TRACK_WIDTH_DP * resources.displayMetrics.density

    /**
     * Track Color
     */
    private var trackGradientArray: IntArray =
        context.resources.getIntArray(R.array.default_track_gradient)
    private var progressGradientArray =
        context.resources.getIntArray(R.array.default_index_gradient)
    private var progressGradientArrayPositions: FloatArray? = null
    private var thumbColor: Int = ContextCompat.getColor(context, R.color.default_thumb_color)
    private var startAngle = DEFAULT_START_ANGLE_DEG
    private var thumbDrawableId: Int = 0
    private var viewType: Int = 0

    private var trackDrawable: TrackDrawable? = null
    private var progressDrawable: ProgressDrawable? = null
    private var thumbEntity: ThumbEntity? = null

    private var showThumb: Boolean = true
    private var showProgress: Boolean = true

    private var progress: Float = 0f

    private var interactive: Boolean = true

    var progressChangedCallback: (progress: Float) -> Unit = {}

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    /**
     * Set whether thumb is visible. To update the view call invalidate().
     *
     * @param showThumb When set to false yhumb will not be drawn
     */
    fun setShowThumb(showThumb: Boolean) {
        this.showThumb = showThumb
    }

    /**
     * Set wheter progress bar is visible. To update the view call invalidate().
     *
     * @param showProgress When set to false progress bar will not be drawn
     */
    fun setShowProgress(showProgress: Boolean) {
        this.showProgress = showProgress
    }

    /**
     * @return boolean indicating whether progress bar is visible
     */
    fun getShowProgress() = showProgress

    /**
     * Set track width in dp. To update the view call invalidate().
     *
     * @param trackWidthDp Track width in dp
     */
    fun setTrackWidthDp(trackWidthDp: Int) {
        trackWidth = trackWidthDp * resources.displayMetrics.density
    }

    /**
     * Set track width in pixels. To update the view call invalidate().
     *
     * @param trackWidth Track width in pixels
     */
    fun setTrackWidth(trackWidth: Float) {
        this.trackWidth = trackWidth
    }

    /**
     * Set track width from dimension resource. To update the view call invalidate().
     *
     * @param widthDimensId Dimension resource id
     */
    fun setTrackWidth(@DimenRes widthDimensId: Int) {
        trackWidth = context.resources.getDimension(widthDimensId)
    }

    /**
     * Set progress width in dp. To update the view call invalidate().
     *
     * @param progressWidthDp Progress bar width in dp
     */
    fun setProgressWidthDp(progressWidthDp: Float) {
        this.progressWidth = progressWidthDp
    }

    /**
     * Set progress width in pixels. To update the view call invalidate().
     *
     * @param progressWidth Progress bar width in pixels
     */
    fun setProgressWidth(progressWidth: Float) {
        this.progressWidth = progressWidth
    }

    fun progressGradient(progressGradient: IntArray) {
        this.progressGradientArray = progressGradient
//        invalidate()
    }

    fun thumbDrawableId(thumbDrawableId: Int) {
        this.thumbDrawableId = thumbDrawableId
        init(measuredWidth / 2f, measuredHeight / 2f)
        invalidate()
    }

    /**
     * Set progress width from dimension resource. To update the view call invalidate().
     *
     * @param progressWidthResourceId Progress bar width dimension resource
     */
    fun setProgressWidth(@DimenRes progressWidthResourceId: Int) {
        progressWidth = context.resources.getDimension(progressWidthResourceId)
    }

    /**
     * Set progress and invalidate the view
     *
     * @param progress Progress from 0.0 to 1.0
     */
    fun setProgress(@FloatRange(from = 0.0, to = 1.0) progress: Float) {
        this.progress = when {
            progress in 0f..1f -> progress
            progress > 1f -> 1f
            else -> 0f
        }


        progressChangedCallback.invoke(progress)

        invalidate()
    }


    /**
     * @return Progress as float in range from 0.0 to 1.0
     */
    fun getProgress() = progress

    private fun applyAttributes(attributes: TypedArray) {
        try {
            viewType = attributes.getInteger(R.styleable.GaugeSeekBar_viewType, viewType)

            startAngle = attributes.getFloat(R.styleable.GaugeSeekBar_startAngleDegrees, startAngle)
            thumbRadius = attributes.getDimension(R.styleable.GaugeSeekBar_thumbRadius, thumbRadius)
            thumbColor = attributes.getColor(R.styleable.GaugeSeekBar_thumbColor, thumbColor)
            val trackGradientArrayId =
                attributes.getResourceId(R.styleable.GaugeSeekBar_trackGradient, 0)
            if (trackGradientArrayId != 0) {
                trackGradientArray = resources.getIntArray(trackGradientArrayId)
            }

            val trackGradientArrayPositionsResourceId =
                attributes.getResourceId(R.styleable.GaugeSeekBar_trackGradientPositions, 0)
            if (trackGradientArrayPositionsResourceId != 0) {
                val positionsIntArray = resources.getIntArray(trackGradientArrayPositionsResourceId)
                progressGradientArrayPositions =
                    FloatArray(positionsIntArray.size) { positionsIntArray[it].div(100f) }
            }

            showThumb = attributes.getBoolean(R.styleable.GaugeSeekBar_showThumb, showThumb)
            progressWidth =
                attributes.getDimension(R.styleable.GaugeSeekBar_progressWidth, progressWidth)
            trackWidth = attributes.getDimension(R.styleable.GaugeSeekBar_trackWidth, trackWidth)
            progress = attributes.getFloat(R.styleable.GaugeSeekBar_progress, 0f)

            //設定progress 顏色
            val gradientArrayResourceId =
                attributes.getResourceId(R.styleable.GaugeSeekBar_progressGradient, 0)
            if (gradientArrayResourceId != 0) {
                progressGradientArray = resources.getIntArray(gradientArrayResourceId)
            }
            val gradientArrayPositionsResourceId =
                attributes.getResourceId(R.styleable.GaugeSeekBar_progressGradientPositions, 0)
            if (gradientArrayPositionsResourceId != 0) {
                val positionsIntArray = resources.getIntArray(gradientArrayPositionsResourceId)
                progressGradientArrayPositions =
                    FloatArray(positionsIntArray.size) { positionsIntArray[it].div(100f) }
            }

            interactive = attributes.getBoolean(R.styleable.GaugeSeekBar_interactive, interactive)

            //thumbDrawable
            thumbDrawableId = attributes.getResourceId(R.styleable.GaugeSeekBar_thumbDrawable, 0)
            showProgress =
                attributes.getBoolean(R.styleable.GaugeSeekBar_showProgress, showProgress)
        } finally {
            attributes.recycle()
        }

        /**
         * 畫可點擊區域
         */
        initPaint()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        init(measuredWidth / 2f, measuredHeight / 2f)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean =
        if (interactive) {

            /**
             * 畫可點擊區域
             */
            val isContain = region!!.contains(event.x.toInt(), event.y.toInt())

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    /**
                     * 畫可點擊區域
                     */
                    if (!isContain) {
                        performClick()
                        handleMotionEvent(event)
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    /**
                     * 畫可點擊區域
                     */
                    if (!isContain) {
                        handleMotionEvent(event)
                    }
                }
            }
            true
        } else {
            super.onTouchEvent(event)
        }

    private fun handleMotionEvent(event: MotionEvent) {
        val relativeX = measuredWidth / 2f - event.x
        val relativeY = event.y - measuredHeight / 2f
        val angle = Math.toDegrees(atan2(relativeX.toDouble(), relativeY.toDouble()))
        setProgress(angleToProgress(if (angle > 0) angle else angle + 360f))
        progressChangedCallback.invoke(progress)


    }

    private fun angleToProgress(angle: Double): Float {
        val availableAngle = 360 - 2 * startAngle
        val relativeAngle = angle - startAngle
        return (relativeAngle / availableAngle).toFloat()
    }

    private fun init(centerX: Float, centerY: Float) {
        val centerPosition = PointF(centerX, centerY)
        radiusPx = centerX.coerceAtMost(centerY)
        val margin = thumbRadius.coerceAtLeast(trackWidth / 2f)
        trackDrawable = TrackDrawable(
            centerPosition,
            radiusPx,
            margin,
            trackGradientArray,
            startAngle,
            trackWidth,
            ContextCompat.getColor(context, R.color.color252e37)
        )

      //  if (showProgress) {
            progressDrawable = ProgressDrawable(
                centerPosition,
                progress,
                radiusPx,
                margin,
                progressGradientArray,
                startAngle,
                progressWidth,
                ContextCompat.getColor(context, R.color.color252e37),
                viewType,
                progressGradientArrayPositions
            )
   //     }

        if (showThumb) {
//            val thumbDrawable = if (thumbDrawableId != 0) ContextCompat.getDrawable(
//                context,
//                thumbDrawableId
//            )!! else ThumbDrawable(thumbColor,progress)

            val thumbDrawable = ThumbDrawable(thumbColor, progress)

            thumbEntity =
                ThumbEntity(centerPosition, progress, startAngle, thumbRadius, thumbDrawable)
        }

        if (viewType == 1) {
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.element_chart_respiration)
        } else if (viewType == 2) {
            bitmap = if (MainActivity.isUs && !MainWorkoutTrainingFragment.isGGG) {
                BitmapFactory.decodeResource(resources, R.drawable.element_chart_hr_us)
            } else {
                BitmapFactory.decodeResource(resources, R.drawable.element_chart_hr)
            }
//
        }

    }

    private val paint33 = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
    }


    override fun onDraw(canvas: Canvas) {
        canvas.apply {
            trackDrawable?.draw(this)
            if (viewType == 1) {
                canvas.drawBitmap(bitmap, 30f, 30f, paint33)
            } else if (viewType == 2) {
                canvas.drawBitmap(bitmap, 30f, 30f, paint33)
            }

            if(showProgress) {
                progressDrawable?.draw(this, progress, viewType)
            }


            //    thumbEntity?.draw(canvas, progress)

            /**
             * 畫可點擊區域
             */
            //   mPaint?.let { drawRegion(canvas, region, it) }
        }
    }

    /**
     * 畫可點擊區域
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // ▼在屏幕中间添加一个圆
        circlePath!!.addCircle(
            (w / 2).toFloat(), //位置
            (h / 2).toFloat(),
            radiusPx * 0.8f, //半徑
            Path.Direction.CW
        )
        // ▼将剪裁边界设置为视图大小
        val globalRegion = Region(-w, -h, w, h)
        // ▼将 Path 添加到 Region 中
        region?.setPath(circlePath!!, globalRegion)
    }

    /**
     * 畫可點擊區域
     */
    private fun initPaint() {
        mPaint = Paint()
        mPaint?.color = Color.TRANSPARENT
        mPaint?.strokeWidth = 3f
        circlePath = Path()
        region = Region()
    }

    /**
     * 畫可點擊區域
     */
    private var region: Region? = null
    private var mPaint: Paint? = null
    private var circlePath: Path? = null
//    private fun drawRegion(canvas: Canvas?, rgn: Region?, paint: Paint) {
//        val circle = circlePath!!
//        canvas!!.drawPath(circle, paint)
//    }

}
