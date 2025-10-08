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
 * SpiritTextView 自定義 View
 *
 * 這個自定義 View 使用 SVG 的路徑資料來繪製 6 個圖形，
 * 並利用動畫效果依照比例從左到右逐步顯示圖形路徑。
 */
class SpiritTextViewScale @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // 定義預設圖形間間距（單位：dp）
    // 前 5 張圖的間距預設為 0dp（相鄰緊貼排列）
    private val DEFAULT_IMAGE_SPACING_DP = -10f
    // 第 5 張圖與第 6 張圖間額外增加 2dp 的間距
    private val EXTRA_SPACING_BETWEEN_5_AND_6_DP = 14f

    // 轉換 dp 為 px 的方法
    private fun dpToPx(dp: Float): Float {
        return dp * context.resources.displayMetrics.density
    }

    // 將 dp 常數轉換為 px 數值
    private val defaultImageSpacing = dpToPx(DEFAULT_IMAGE_SPACING_DP)
    private val extraSpacingBetween5And6 = dpToPx(EXTRA_SPACING_BETWEEN_5_AND_6_DP)

    // 動畫控制屬性
    // loopAnimation：是否循環播放（預設 true）
    // animationDuration：動畫持續時間（單位：毫秒，預設 3000 毫秒）
    private var loopAnimation: Boolean = true
    private var animationDuration: Long = 3000L

    init {
        // 取得自定義屬性設定
        attrs?.let {
            val a: TypedArray = context.obtainStyledAttributes(it, R.styleable.SpiritTextView)
            loopAnimation = a.getBoolean(R.styleable.SpiritTextView_loopAnimation, true)
            animationDuration = a.getInteger(R.styleable.SpiritTextView_animationDuration, 3000).toLong()
            a.recycle()
        }
    }

    // 定義各個 SVG 路徑資料（字母 S, P, -, R, -, - 分別對應 6 個圖形）
    // 第 1 個 SVG (S 字, 尺寸 105×64)
    private val svgPathData1 =
        "M63.0039 0C38.8482 0 19.1751 8.2179 19.1751 18.179C19.1751 23.1595 24.1556 27.642 31.8755 30.8794L54.537 40.5914C58.0233 42.0856 60.2646 44.0778 60.2646 46.3191C60.2646 50.5525 51.7977 54.2879 41.0895 54.2879H13.4475C11.7043 54.2879 10.2101 54.786 9.21401 55.7821L0 64H42.0856C67.9844 64 89.1517 55.284 89.1517 44.5759C89.1517 38.8482 83.1751 33.8677 73.9611 30.1323L51.2996 21.6654C47.8132 20.4202 45.821 18.428 45.821 16.4358C45.821 12.4514 53.7899 9.21401 63.5019 9.21401H92.1401C93.8833 9.21401 95.3774 8.71595 96.3735 7.71984L104.591 0"

    // 第 2 個 SVG (P 字, 尺寸 96×64) 複合路徑
    private val svgPathData2 =
        "M91.2879 5.22957C88.2996 1.74319 81.8249 0 71.8638 0H22.0583C20.0661 0 18.5719 1.24514 18.0739 2.98833L0.143921 63.751H19.319C21.3112 63.751 22.8054 62.5059 23.3035 60.7627L31.5214 32.3736L35.0077 37.6031C35.7548 38.8483 37 39.5953 38.4941 39.5953H56.9222C79.3346 39.5953 90.5408 31.3774 94.5253 17.93C96.2685 12.4514 94.0272 7.96887 91.2879 5.22957ZM70.8677 18.677C68.1284 27.642 57.9183 30.8794 49.4513 30.8794H32.0194L34.5097 22.9105L38.4941 8.96498H54.4319C60.6576 8.96498 66.8832 9.21401 69.6225 12.4514C70.6187 13.6965 71.6148 15.6887 70.8677 18.677Z"

    // 第 3 個 SVG (尺寸 42×64)
    private val svgPathData3 =
        "M41.642 0H22.7159C20.7237 0 19.2295 1.24514 18.7315 2.98833L0.801483 63.751H19.7276C21.7198 63.751 23.214 62.5059 23.712 60.7627L41.642 0Z"

    // 第 4 個 SVG (R 字, 尺寸 96×64) 複合路徑
    private val svgPathData4 =
        "M92.07 5.22957C88.8327 1.74319 82.358 0 72.6459 0H22.0934C20.1012 0 18.607 1.24514 18.109 2.98833L0.179047 64H19.8522C21.8444 64 23.3386 62.7549 23.8366 61.0117L32.8016 30.3813L52.7238 61.0117C53.9689 62.7549 55.9611 64 58.2023 64H80.1167L62.4358 37.3541C81.8599 36.607 91.3229 30.8794 95.0583 18.179C96.8015 12.4514 94.5603 7.96887 92.07 5.22957ZM71.4008 18.677C68.6615 27.642 59.1984 28.3891 50.7315 28.3891H33.2996L39.0273 9.21401H54.965C61.1907 9.21401 67.4164 9.46303 70.1556 12.7004C71.1518 13.6965 72.3969 15.6887 71.4008 18.677Z"

    // 第 5 個 SVG (尺寸 42×64)
    private val svgPathData5 =
        "M41.642 0H22.7159C20.7237 0 19.2295 1.24514 18.7315 2.98833L0.801483 63.751H19.7276C21.7198 63.751 23.214 62.5059 23.712 60.7627L41.642 0Z"

    // 第 6 個 SVG (尺寸 88×64)
    private val svgPathData6 =
        "M6.13615 0C4.14393 0 2.64979 1.24514 2.15174 2.98833L0.159515 8.96498H30.2918L13.856 63.751H32.7821C34.7743 63.751 36.2684 62.5059 36.7665 60.7627L52.2062 8.96498H74.6186C76.3618 8.96498 77.856 8.46693 78.8521 7.47082L87.568 0H6.13615V0Z"

    // 利用 PathParser 將 SVG 路徑資料解析為 Path 物件
    private val originalPath1: Path = PathParser.createPathFromPathData(svgPathData1)
    private val originalPath2: Path = PathParser.createPathFromPathData(svgPathData2)
    private val originalPath3: Path = PathParser.createPathFromPathData(svgPathData3)
    private val originalPath4: Path = PathParser.createPathFromPathData(svgPathData4)
    private val originalPath5: Path = PathParser.createPathFromPathData(svgPathData5)
    private val originalPath6: Path = PathParser.createPathFromPathData(svgPathData6)

    // 宣告放大縮小及平移後的路徑
    private val scaledPath1 = Path()
    private val scaledPath2 = Path()
    private val scaledPath3 = Path()
    private val scaledPath4 = Path()
    private val scaledPath5 = Path()
    private val scaledPath6 = Path()

    // 動畫處理時使用的臨時路徑（預先分配，避免每次 new 物件）
    private val animatedPath1 = Path()
    private val animatedPath2 = Path()
    private val animatedPath3 = Path()
    private val animatedPath4 = Path()
    private val animatedPath5 = Path()
    private val animatedPath6 = Path()

    // 原始 SVG 尺寸定義（寬與高）
    private val svgWidth1 = 105f
    private val svgWidth2 = 96f
    private val svgWidth3 = 42f
    private val svgWidth4 = 96f
    private val svgWidth5 = 42f
    private val svgWidth6 = 88f
    private val svgHeight = 64f

    // 畫筆設定：使用抗鋸齒，填滿白色
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.WHITE
    }

    // 定義 PathMeasure 以計算路徑長度等資訊

    // 第一個圖 (S 字) 為單一路徑
    private var pathMeasure1: PathMeasure? = null
    // 第二個圖 (P 字) 為複合路徑，需拆解成各個輪廓
    private var contourMeasures2: List<PathMeasure>? = null
    // 第三個圖，單一路徑
    private var pathMeasure3: PathMeasure? = null
    // 第四個圖 (R 字) 複合路徑，拆解後分割出各輪廓
    private var contourMeasures4: List<PathMeasure>? = null
    // 第五個圖，單一路徑
    private var pathMeasure5: PathMeasure? = null
    // 第六個圖，單一路徑
    private var pathMeasure6: PathMeasure? = null

    // 複合路徑（第 2 與第 4 圖）的總長度
    private var totalLength2: Float = 0f
    private var totalLength4: Float = 0f

    // 預先分配複合路徑段落的臨時 Path 清單
    private var tempSegmentPaths2: MutableList<Path> = mutableListOf()
    private var tempSegmentPaths4: MutableList<Path> = mutableListOf()

    // 全局動畫進度，範圍為 0f ~ 1f，可依此判斷每個路徑的顯示比例
    private var animatedFraction: Float = 0f
    // 動畫控制器
    private var animator: ValueAnimator? = null

    // 取得所有圖形的最大路徑長度，作為歸一化進度計算的依據
    private var maxTotalLength: Float = 0f

    /**
     * 輔助函式：根據複合路徑的多個 PathMeasure，依據動畫進度依序合併部分路徑到 dest 中
     *
     * @param measures 輪廓的 PathMeasure 清單
     * @param totalLength 複合路徑的總長度
     * @param fraction 動畫進度所對應的比例
     * @param dest 用來儲存組合後路徑的物件
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
                // 當剩餘長度大於等於當前輪廓長度時，整個輪廓皆顯示
                measure.getSegment(0f, measure.length, tempPath, true)
                dest.addPath(tempPath)
                remaining -= measure.length
            } else {
                // 剩餘長度不足，僅截取部分輪廓
                measure.getSegment(0f, remaining, tempPath, true)
                dest.addPath(tempPath)
                remaining = 0f
                break
            }
        }
    }

    /**
     * 當 View 尺寸發生改變時會呼叫此方法
     *
     * 此方法中會依據 View 的尺寸，計算出統一縮放比例、
     * 每個圖形的平移座標，並重新配置所有路徑與動畫參數
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        // 重置所有縮放後的路徑
        scaledPath1.reset()
        scaledPath2.reset()
        scaledPath3.reset()
        scaledPath4.reset()
        scaledPath5.reset()
        scaledPath6.reset()

        // 計算 6 個圖原始總寬度：105 + 96 + 42 + 96 + 42 + 88 = 469
        val totalOriginalWidth = svgWidth1 + svgWidth2 + svgWidth3 + svgWidth4 + svgWidth5 + svgWidth6
        // 計算總間距：前面圖與圖之間採用 default spacing (此處為 0)，但第5與第6圖間需加上額外間距
        val totalSpacing = defaultImageSpacing * 4 + (defaultImageSpacing + extraSpacingBetween5And6)
        // 根據 View 的寬度與高度計算水平與垂直的縮放比例
        val scaleX = w / (totalOriginalWidth + totalSpacing)
        val scaleY = h / svgHeight
        // 以最小者為實際縮放比例，避免圖形超出範圍
        val scale = min(scaleX, scaleY)

        // 總縮放後寬度與高度
        val totalScaledWidth = totalOriginalWidth * scale + totalSpacing
        val scaledHeight = svgHeight * scale

        // 計算水平與垂直的偏移量，以使圖形置中
        val dxTotal = (w - totalScaledWidth) / 2f
        val dy = (h - scaledHeight) / 2f

        // 計算每個圖形的 x 軸起始位移座標
        val x1 = dxTotal
        val x2 = dxTotal + svgWidth1 * scale + defaultImageSpacing
        val x3 = dxTotal + (svgWidth1 + svgWidth2) * scale + defaultImageSpacing * 2
        val x4 = dxTotal + (svgWidth1 + svgWidth2 + svgWidth3) * scale + defaultImageSpacing * 3
        val x5 = dxTotal + (svgWidth1 + svgWidth2 + svgWidth3 + svgWidth4) * scale + defaultImageSpacing * 4
        val x6 = dxTotal + (svgWidth1 + svgWidth2 + svgWidth3 + svgWidth4 + svgWidth5) * scale +
                defaultImageSpacing * 4 + (defaultImageSpacing + extraSpacingBetween5And6)

        // 分別建構變換矩陣，包含縮放與平移調整
        val matrix1 = Matrix().apply {
            postScale(scale, scale)
            postTranslate(x1, dy)
        }
        val matrix2 = Matrix().apply {
            postScale(scale, scale)
            postTranslate(x2, dy)
        }
        val matrix3 = Matrix().apply {
            postScale(scale, scale)
            postTranslate(x3, dy)
        }
        val matrix4 = Matrix().apply {
            postScale(scale, scale)
            postTranslate(x4, dy)
        }
        val matrix5 = Matrix().apply {
            postScale(scale, scale)
            postTranslate(x5, dy)
        }
        val matrix6 = Matrix().apply {
            postScale(scale, scale)
            postTranslate(x6, dy)
        }

        // 將原始 SVG 路徑依照變換矩陣轉換到對應的 scaledPath
        originalPath1.transform(matrix1, scaledPath1)
        originalPath2.transform(matrix2, scaledPath2)
        originalPath3.transform(matrix3, scaledPath3)
        originalPath4.transform(matrix4, scaledPath4)
        originalPath5.transform(matrix5, scaledPath5)
        originalPath6.transform(matrix6, scaledPath6)

        // 設定第 2 與第 4 個圖形採用 EVEN_ODD 填充規則
        scaledPath2.fillType = Path.FillType.EVEN_ODD
        scaledPath4.fillType = Path.FillType.EVEN_ODD

        // 建立各圖形的 PathMeasure，方便後續取得長度與截取路徑片段

        // 第 1 圖 (S 字)：建立單一路徑的 PathMeasure
        pathMeasure1 = PathMeasure(scaledPath1, false)

        // 第 2 圖 (P 字)：處理複合路徑，分解出各個輪廓
        val contourPaths2 = mutableListOf<Path>()
        val pm2 = PathMeasure(scaledPath2, false)
        do {
            val contour = Path()
            pm2.getSegment(0f, pm2.length, contour, true)
            contourPaths2.add(Path(contour))
        } while (pm2.nextContour())
        // 建立各個輪廓的 PathMeasure 清單
        contourMeasures2 = contourPaths2.map { PathMeasure(it, false) }
        // 計算複合路徑總長度
        totalLength2 = contourMeasures2?.sumOf { it.length.toDouble() }?.toFloat() ?: 0f

        // 第 3 圖：建立單一路徑的 PathMeasure
        pathMeasure3 = PathMeasure(scaledPath3, false)

        // 第 4 圖 (R 字)：拆解複合路徑為各個輪廓
        val contourPaths4 = mutableListOf<Path>()
        val pm4 = PathMeasure(scaledPath4, false)
        do {
            val contour = Path()
            pm4.getSegment(0f, pm4.length, contour, true)
            contourPaths4.add(Path(contour))
        } while (pm4.nextContour())
        contourMeasures4 = contourPaths4.map { PathMeasure(it, false) }
        totalLength4 = contourMeasures4?.sumOf { it.length.toDouble() }?.toFloat() ?: 0f

        // 第 5 與第 6 圖：建立單一路徑的 PathMeasure
        pathMeasure5 = PathMeasure(scaledPath5, false)
        pathMeasure6 = PathMeasure(scaledPath6, false)

        // 初始化或清空複合路徑中用到的臨時段落路徑清單
        tempSegmentPaths2.clear()
        for (i in contourPaths2.indices) {
            tempSegmentPaths2.add(Path())
        }
        tempSegmentPaths4.clear()
        for (i in contourPaths4.indices) {
            tempSegmentPaths4.add(Path())
        }

        // 取得各圖形的路徑長度，並設定其中最大的長度作為歸一化基準
        val length1 = pathMeasure1?.length ?: 0f
        val length3 = pathMeasure3?.length ?: 0f
        val length5 = pathMeasure5?.length ?: 0f
        val length6 = pathMeasure6?.length ?: 0f
        maxTotalLength = max(length1, max(totalLength2, max(length3, max(totalLength4, max(length5, length6)))))

        // 結束前先停止前一次的動畫，重新配置動畫參數
        animator?.cancel()
        animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = animationDuration
            repeatCount = if (loopAnimation) ValueAnimator.INFINITE else 0
            addUpdateListener { animation ->
                // 每次動畫更新時，更新全局進度並重繪 View
                this@SpiritTextViewScale.animatedFraction = animation.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    /**
     * onDraw() 方法會根據動畫進度逐步繪製各圖形的部分路徑
     *
     * 對於單一路徑圖形，使用 PathMeasure#getSegment 取得指定進度的路徑段落。
     * 對於複合路徑圖形，依序從每個輪廓中取出對應的段落並合併到一個路徑中。
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 繪製第 1 圖 (S 字)
        animatedPath1.reset()
        pathMeasure1?.let { measure ->
            // 以動畫進度及歸一化基準計算實際繪製比例
            val normFraction = min(animatedFraction * (maxTotalLength / measure.length), 1f)
            measure.getSegment(0f, normFraction * measure.length, animatedPath1, true)
        }
        canvas.drawPath(animatedPath1, paint)

        // 繪製第 2 圖 (P 字, 複合路徑)
        animatedPath2.reset()
        if (totalLength2 > 0f && contourMeasures2 != null) {
            val normFraction = min(animatedFraction * (maxTotalLength / totalLength2), 1f)
            // 利用輔助函式依序取出每個輪廓的路徑段落並合併
            drawCompositePath(contourMeasures2!!, totalLength2, normFraction, animatedPath2)
            animatedPath2.fillType = Path.FillType.EVEN_ODD
            canvas.drawPath(animatedPath2, paint)
        }

        // 繪製第 3 圖
        animatedPath3.reset()
        pathMeasure3?.let { measure ->
            val normFraction = min(animatedFraction * (maxTotalLength / measure.length), 1f)
            measure.getSegment(0f, normFraction * measure.length, animatedPath3, true)
        }
        canvas.drawPath(animatedPath3, paint)

        // 繪製第 4 圖 (R 字, 複合路徑)
        animatedPath4.reset()
        if (totalLength4 > 0f && contourMeasures4 != null) {
            val normFraction = min(animatedFraction * (maxTotalLength / totalLength4), 1f)
            drawCompositePath(contourMeasures4!!, totalLength4, normFraction, animatedPath4)
            animatedPath4.fillType = Path.FillType.EVEN_ODD
            canvas.drawPath(animatedPath4, paint)
        }

        // 繪製第 5 圖
        animatedPath5.reset()
        pathMeasure5?.let { measure ->
            val normFraction = min(animatedFraction * (maxTotalLength / measure.length), 1f)
            measure.getSegment(0f, normFraction * measure.length, animatedPath5, true)
        }
        canvas.drawPath(animatedPath5, paint)

        // 繪製第 6 圖
        animatedPath6.reset()
        pathMeasure6?.let { measure ->
            val normFraction = min(animatedFraction * (maxTotalLength / measure.length), 1f)
            measure.getSegment(0f, normFraction * measure.length, animatedPath6, true)
        }
        canvas.drawPath(animatedPath6, paint)
    }

    /**
     * 當 View 被移除時停止動畫
     */
    override fun onDetachedFromWindow() {
        animator?.cancel()
        super.onDetachedFromWindow()
    }
}
