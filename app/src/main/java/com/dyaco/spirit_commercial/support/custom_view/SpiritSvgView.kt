package com.dyaco.spirit_commercial.support.custom_view

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.PathParser
import com.dyaco.spirit_commercial.R
import kotlin.math.max
import kotlin.math.min

/**
 * 使用 SVG 資料繪製整個圖形的自訂 View
 *
 * 此 SVG 的原始尺寸為 431×64，不進行縮放，
 * 僅平移置中；並以動畫效果從零逐步繪製全部路徑。
 */
class SpiritSvgView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // 若有需要可自訂屬性 (例如是否循環播放動畫與動畫持續時間)
    private var loopAnimation: Boolean = true
    private var animationDuration: Long = 3000L

    init {
        // 如有自定義屬性，可依需求從 AttributeSet 取得設定
        attrs?.let {
            val a: TypedArray = context.obtainStyledAttributes(it, R.styleable.SpiritTextView)
            loopAnimation = a.getBoolean(R.styleable.SpiritTextView_loopAnimation, true)
            animationDuration = a.getInteger(R.styleable.SpiritTextView_animationDuration, 3000).toLong()
            a.recycle()
        }
    }

    // SVG 原始尺寸（根據 viewBox 定義）
    private val svgWidth = 431f
    private val svgHeight = 64f

    // ----------------------------------------------------
    // SVG 各圖形路徑資料（6 條 Path）
    // ----------------------------------------------------
    private val svgPathData1 =
        "M219.642 0H200.716C198.724 0 197.23 1.24514 196.731 2.98833L178.801 63.751H197.728C199.72 63.751 201.214 62.5059 201.712 60.7627L219.642 0Z"
    private val svgPathData2 =
        "M339.673 0H320.747C318.755 0 317.261 1.24514 316.763 2.98833L298.833 63.751H317.759C319.751 63.751 321.245 62.5059 321.743 60.7627L339.673 0Z"
    private val svgPathData3 =
        "M182.288 5.22957C179.3 1.74319 172.825 0 162.864 0H113.058C111.066 0 109.572 1.24514 109.074 2.98833L91.1439 63.751H110.319C112.311 63.751 113.805 62.5059 114.303 60.7627L122.521 32.3736L126.008 37.6031C126.755 38.8483 128 39.5953 129.494 39.5953H147.922C170.335 39.5953 181.541 31.3774 185.525 17.93C187.268 12.4514 185.027 7.96887 182.288 5.22957ZM161.868 18.677C159.128 27.642 148.918 30.8794 140.451 30.8794H123.019L125.51 22.9105L129.494 8.96498H145.432C151.658 8.96498 157.883 9.21401 160.623 12.4514C161.619 13.6965 162.615 15.6887 161.868 18.677Z"
    private val svgPathData4 =
        "M349.136 0C347.144 0 345.65 1.24514 345.152 2.98833L343.16 8.96498H373.292L356.856 63.751H375.782C377.774 63.751 379.268 62.5059 379.766 60.7627L395.206 8.96498H417.619C419.362 8.96498 420.856 8.46693 421.852 7.47082L430.568 0H349.136V0Z"
    private val svgPathData5 =
        "M302.07 5.22957C298.833 1.74319 292.358 0 282.646 0H232.093C230.101 0 228.607 1.24514 228.109 2.98833L210.179 64H229.852C231.844 64 233.339 62.7549 233.837 61.0117L242.802 30.3813L262.724 61.0117C263.969 62.7549 265.961 64 268.202 64H290.117L272.436 37.3541C291.86 36.607 301.323 30.8794 305.058 18.179C306.802 12.4514 304.56 7.96887 302.07 5.22957ZM281.401 18.677C278.661 27.642 269.198 28.3891 260.732 28.3891H243.3L249.027 9.21401H264.965C271.191 9.21401 277.416 9.46303 280.156 12.7004C281.152 13.6965 282.397 15.6887 281.401 18.677Z"
    private val svgPathData6 =
        "M63.0039 0C67.9844 64 89.1517 55.284 89.1517 44.5759C89.1517 38.8482 83.1751 33.8677 73.9611 30.1323L51.2996 21.6654C47.8132 20.4202 45.821 18.428 45.821 16.4358C45.821 12.4514 53.7899 9.21401 63.5019 9.21401H92.1401C93.8833 9.21401 95.3774 8.71595 96.3735 7.71984L104.591 0H63.0039Z"

    // ----------------------------------------------------
    // 由路徑資料產生原始 Path 物件
    // ----------------------------------------------------
    private val originalPath1: Path = PathParser.createPathFromPathData(svgPathData1)
    private val originalPath2: Path = PathParser.createPathFromPathData(svgPathData2)
    private val originalPath3: Path = PathParser.createPathFromPathData(svgPathData3)
    private val originalPath4: Path = PathParser.createPathFromPathData(svgPathData4)
    private val originalPath5: Path = PathParser.createPathFromPathData(svgPathData5)
    private val originalPath6: Path = PathParser.createPathFromPathData(svgPathData6)

    // ----------------------------------------------------
    // 經過平移後置中的 Path
    // ----------------------------------------------------
    private val translatedPath1 = Path()
    private val translatedPath2 = Path()
    private val translatedPath3 = Path()
    private val translatedPath4 = Path()
    private val translatedPath5 = Path()
    private val translatedPath6 = Path()

    // 用於動畫繪製時依段取出部分路徑的暫存 Path
    private val animatedPath1 = Path()
    private val animatedPath2 = Path()
    private val animatedPath3 = Path()
    private val animatedPath4 = Path()
    private val animatedPath5 = Path()
    private val animatedPath6 = Path()

    // 單一輪廓（非複合）的 PathMeasure (路徑 1、2、4、6)
    private var measure1: PathMeasure? = null
    private var measure2: PathMeasure? = null
    private var measure4: PathMeasure? = null
    private var measure6: PathMeasure? = null

    // 複合路徑：包含多個輪廓 (路徑 3 與 5)
    private var measures3: List<PathMeasure>? = null
    private var totalLength3: Float = 0f
    private var measures5: List<PathMeasure>? = null
    private var totalLength5: Float = 0f

    // 用來儲存複合路徑中各輪廓的暫存路徑清單
    private var tempSegmentPaths3: MutableList<Path> = mutableListOf()
    private var tempSegmentPaths5: MutableList<Path> = mutableListOf()

    // 用來計算所有路徑中最大總長度（用於動畫進度歸一化）
    private var maxTotalLength: Float = 0f

    // 動畫控制進度 (0f 到 1f)
    private var animatedFraction: Float = 0f
    private var animator: ValueAnimator? = null

    /**
     * 輔助函式：
     * 根據複合路徑中多個 PathMeasure，依據動畫進度依序合併路徑到 dest 中
     */
    private fun drawCompositePath(
        measures: List<PathMeasure>,
        totalLength: Float,
        fraction: Float,
        dest: Path
    ) {
        dest.reset()
        var remaining = fraction * totalLength
        val tempPath = Path()
        for (measure in measures) {
            if (remaining <= 0f) break
            tempPath.reset()
            if (remaining >= measure.length) {
                measure.getSegment(0f, measure.length, tempPath, true)
                dest.addPath(tempPath)
                remaining -= measure.length
            } else {
                measure.getSegment(0f, remaining, tempPath, true)
                dest.addPath(tempPath)
                remaining = 0f
                break
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        // 重置各個 translatedPath
        translatedPath1.reset()
        translatedPath2.reset()
        translatedPath3.reset()
        translatedPath4.reset()
        translatedPath5.reset()
        translatedPath6.reset()

        // 計算平移量 (不縮放)，使得 SVG 圖形 (431×64) 能在 View 中置中
        val dx = (w - svgWidth) / 2f
        val dy = (h - svgHeight) / 2f

        // 建立平移矩陣並作用於各原始 Path
        val translateMatrix = Matrix().apply {
            postTranslate(dx, dy)
        }
        originalPath1.transform(translateMatrix, translatedPath1)
        originalPath2.transform(translateMatrix, translatedPath2)
        originalPath3.transform(translateMatrix, translatedPath3)
        originalPath4.transform(translateMatrix, translatedPath4)
        originalPath5.transform(translateMatrix, translatedPath5)
        originalPath6.transform(translateMatrix, translatedPath6)

        // 為非複合路徑建立 PathMeasure
        measure1 = PathMeasure(translatedPath1, false)
        measure2 = PathMeasure(translatedPath2, false)
        measure4 = PathMeasure(translatedPath4, false)
        measure6 = PathMeasure(translatedPath6, false)

        // 處理複合路徑：路徑 3
        val contourPaths3 = mutableListOf<Path>()
        val pm3 = PathMeasure(translatedPath3, false)
        do {
            val contour = Path()
            pm3.getSegment(0f, pm3.length, contour, true)
            contourPaths3.add(Path(contour))
        } while (pm3.nextContour())
        measures3 = contourPaths3.map { PathMeasure(it, false) }
        totalLength3 = measures3?.sumOf { it.length.toDouble() }?.toFloat() ?: 0f
        tempSegmentPaths3.clear()
        for (i in contourPaths3.indices) {
            tempSegmentPaths3.add(Path())
        }

        // 處理複合路徑：路徑 5
        val contourPaths5 = mutableListOf<Path>()
        val pm5 = PathMeasure(translatedPath5, false)
        do {
            val contour = Path()
            pm5.getSegment(0f, pm5.length, contour, true)
            contourPaths5.add(Path(contour))
        } while (pm5.nextContour())
        measures5 = contourPaths5.map { PathMeasure(it, false) }
        totalLength5 = measures5?.sumOf { it.length.toDouble() }?.toFloat() ?: 0f
        tempSegmentPaths5.clear()
        for (i in contourPaths5.indices) {
            tempSegmentPaths5.add(Path())
        }

        // 計算所有路徑中最大的總長度 (用於動畫進度歸一化)
        val length1 = measure1?.length ?: 0f
        val length2 = measure2?.length ?: 0f
        val length4 = measure4?.length ?: 0f
        val length6 = measure6?.length ?: 0f
        maxTotalLength = max(length1, max(length2, max(totalLength3, max(length4, max(totalLength5, length6)))))

        // 初始化並啟動動畫
        animator?.cancel()
        animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = animationDuration
            repeatCount = if (loopAnimation) ValueAnimator.INFINITE else 0
            addUpdateListener { animation ->
                this@SpiritSvgView.animatedFraction = animation.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 使用相同的畫筆 (白色、抗鋸齒)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = Color.WHITE
        }

        // 繪製路徑 1 (非複合)
        animatedPath1.reset()
        measure1?.let { m ->
            val normFraction = min(animatedFraction * (maxTotalLength / m.length), 1f)
            m.getSegment(0f, normFraction * m.length, animatedPath1, true)
            // 修正第一圖因閉合路徑 getSegment 可能失去起點的問題
            animatedPath1.rLineTo(0f, 0f)
            canvas.drawPath(animatedPath1, paint)
        }

        // 繪製路徑 2 (非複合)
        animatedPath2.reset()
        measure2?.let { m ->
            val normFraction = min(animatedFraction * (maxTotalLength / m.length), 1f)
            m.getSegment(0f, normFraction * m.length, animatedPath2, true)
            canvas.drawPath(animatedPath2, paint)
        }

        // 繪製路徑 3 (複合)
        animatedPath3.reset()
        measures3?.let { ms ->
            val normFraction = min(animatedFraction * (maxTotalLength / totalLength3), 1f)
            drawCompositePath(ms, totalLength3, normFraction, animatedPath3)
            canvas.drawPath(animatedPath3, paint)
        }

        // 繪製路徑 4 (非複合)
        animatedPath4.reset()
        measure4?.let { m ->
            val normFraction = min(animatedFraction * (maxTotalLength / m.length), 1f)
            m.getSegment(0f, normFraction * m.length, animatedPath4, true)
            canvas.drawPath(animatedPath4, paint)
        }

        // 繪製路徑 5 (複合)
        animatedPath5.reset()
        measures5?.let { ms ->
            val normFraction = min(animatedFraction * (maxTotalLength / totalLength5), 1f)
            drawCompositePath(ms, totalLength5, normFraction, animatedPath5)
            canvas.drawPath(animatedPath5, paint)
        }

        // 繪製路徑 6 (非複合)
        animatedPath6.reset()
        measure6?.let { m ->
            val normFraction = min(animatedFraction * (maxTotalLength / m.length), 1f)
            m.getSegment(0f, normFraction * m.length, animatedPath6, true)
            canvas.drawPath(animatedPath6, paint)
        }
    }

    override fun onDetachedFromWindow() {
        animator?.cancel()
        super.onDetachedFromWindow()
    }
}
