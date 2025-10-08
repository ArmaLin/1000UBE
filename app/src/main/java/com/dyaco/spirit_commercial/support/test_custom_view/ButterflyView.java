package com.dyaco.spirit_commercial.support.test_custom_view;

import static android.util.TypedValue.COMPLEX_UNIT_DIP;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.dyaco.spirit_commercial.R;

import java.util.List;

public class ButterflyView extends View {
    private int mViewWidth;
    private int mViewHeight;
    private float mBaseX;
    private float mBaseY;

    //圓的半徑
    private float mRadius;
    private float mTextSpace;
    private float mTextSize;
    private float mAngle;
    private int mSegment;
    private ButterflyView.Segment[] mSegments;
    private int mSegmentLevel; //每個Segment的總長
    private float mSegmentLevelUnit;
    private Canvas mCanvas;
    private Paint mSegmentPaint;
    private int mSegmentPullColor;
    private int mSegmentPushColor;
    private Paint mSideLinePaint;
    private float mSideLineWidth;
    private int mSideLineColor;
    private Paint mSideStartPaint;
    private Paint mAvgLevelPaint;
    private Paint mAvgLevelPaint2;
    private int mAvgPaintColor;

    private int mMaxPullLevel;
    private int mMaxPushLevel;

    private TextPaint mTextPaint;
    // private Paint mTextPaint;
    private int mTextColor = Color.WHITE;

    private Context context;

    public ButterflyView(Context context) {
        this(context, null);
    }

    public ButterflyView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context = context;
        this.mTextSpace = 40.0F;
        this.mTextSize = 20.0F;
        this.mSegment = 40;
        this.mSegmentLevel = 100; //每個Segment的總長
        this.mSegmentPullColor = Color.parseColor("#4f6b9c");
        this.mSegmentPushColor = Color.parseColor("#34779d");
        this.mSideLineWidth = dp2px(2);
        this.mSideLineColor = Color.parseColor("#084150");
        this.mAvgPaintColor = Color.parseColor("#C94C22");
        this.mTextColor = Color.parseColor("#33FDF6E4");
        this.initAttr(attributeSet);
        this.initSegments();
        this.initPaints();
    }

    public void setColor(int pullColor, int pushColor) {
        this.mSegmentPullColor = pullColor;
        this.mSegmentPushColor = pushColor;
        this.invalidate();
    }

    public void setAvgLineColor(int color) {
        this.mAvgPaintColor = color;
        this.invalidate();
    }

    public void reset() {
        this.initSegments();
        this.invalidate();
    }


    public int getSegmentCount() {
        return this.mSegment;
    }

    private void initAttr(AttributeSet attributeSet) {
        TypedArray typedArray = attributeSet == null ? null : this.getContext().obtainStyledAttributes(attributeSet, R.styleable.ButterflyView);
        if (typedArray != null) {
            this.mSegment = typedArray.getInteger(R.styleable.ButterflyView_segment, this.mSegment);
            this.mSegmentLevel = typedArray.getInteger(R.styleable.ButterflyView_segmentLevel, this.mSegmentLevel);
            this.mSegmentPullColor = typedArray.getInteger(R.styleable.ButterflyView_segmentPullColor, this.mSegmentPullColor);
            this.mSegmentPushColor = typedArray.getInteger(R.styleable.ButterflyView_segmentPushColor, this.mSegmentPushColor);
            typedArray.recycle();
        }
    }

    private void initSegments() {
        this.mSegments = new ButterflyView.Segment[this.mSegment];

        for (int i = 0; i < this.mSegments.length; ++i) {
            this.mSegments[i] = new ButterflyView.Segment();
        }

        this.mMaxPullLevel = this.mMaxPushLevel = 0;
    }

    private void initPaints() {

        this.mSideLinePaint = new Paint();
        this.mSideLinePaint.setAntiAlias(true); //取消鋸齒
        this.mSideLinePaint.setDither(true);
        this.mSideLinePaint.setStyle(Paint.Style.STROKE);
        this.mSideLinePaint.setColor(this.mSideLineColor);
        this.mSideLinePaint.setStrokeWidth(this.mSideLineWidth);

        this.mSideStartPaint = new Paint();
        this.mSideStartPaint.setStyle(Paint.Style.STROKE);
        this.mSideStartPaint.setColor(Color.parseColor("#fdf6e4"));
        this.mSideStartPaint.setStrokeWidth(dp2px(3));

        this.mSegmentPaint = new Paint();
        this.mSegmentPaint.setAntiAlias(true); //取消鋸齒
        this.mSegmentPaint.setColor(this.mSegmentPullColor);

        this.mAvgLevelPaint = new Paint();
        this.mAvgLevelPaint.setAntiAlias(true); //取消鋸齒
        this.mAvgLevelPaint.setColor(this.mAvgPaintColor);
        this.mAvgLevelPaint.setStyle(Paint.Style.STROKE); //設置畫圓弧的畫筆的屬性, FILL 填滿
        this.mAvgLevelPaint.setStrokeWidth(dp2px(6));


        this.mAvgLevelPaint2 = new Paint();
        this.mAvgLevelPaint2.setAntiAlias(true); //取消鋸齒
        this.mAvgLevelPaint2.setColor(Color.parseColor("#33000000"));
        this.mAvgLevelPaint2.setStyle(Paint.Style.FILL); //設置畫圓弧的畫筆的屬性, FILL 填滿


        //  this.mTextPaint = new Paint();
        this.mTextPaint = new TextPaint();
        this.mTextPaint.setColor(this.mTextColor);
        this.mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setAntiAlias(true);
        this.mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
        // Typeface typeFace = Typeface.createFromAsset(context.getAssets(), "font/poppins_semibold.ttf");
        //    this.mTextPaint.setTypeface(typeFace);
        //  this.mTextPaint.setTypeface(ResourcesCompat.getFont(context, R.font.poppins_semibold));
        this.mTextPaint.setTextSize(this.mTextSize);
        this.mTextPaint.setTextAlign(Paint.Align.CENTER);
    }


    /**
     * invalidate 觸發 onDraw
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.mCanvas = canvas;
        drawSegment();
        drawSideLine();
        //   drawTest();
        drawAvgArc();
    }

    /**
     * 裡面的色塊
     */
    private void drawSegment() {
//        for(int i = 0; i < this.mSegments.length; ++i) {
//            ButterflyView.Segment seg = this.mSegments[i];
//            float level = (float)seg.getLevel() * this.mSegmentLevelUnit;
//
//            RectF rectF = new RectF(this.mBaseX - level, this.mBaseY - level, this.mBaseX + level, this.mBaseY + level);
//            this.mSegmentPaint.setColor(seg.isPull() ? this.mSegmentPullColor : this.mSegmentPushColor);
//            this.mCanvas.drawArc(rectF, (float)i * this.mAngle - 90.0F, this.mAngle, true, this.mSegmentPaint);
//        }

        for (int i = 0; i < this.mSegments.length; ++i) {
            ButterflyView.Segment seg = this.mSegments[i];
            float level = (float) seg.getLevel() * this.mSegmentLevelUnit;
            RectF rectF = new RectF(this.mBaseX - level, this.mBaseY - level, this.mBaseX + level, this.mBaseY + level);
            this.mSegmentPaint.setColor(seg.isPull() ? this.mSegmentPullColor : this.mSegmentPushColor);
            this.mCanvas.drawArc(rectF, (float) i * this.mAngle - 90.0F, this.mAngle, true, this.mSegmentPaint);
            this.mTextPaint.setColor(seg.isPull() ? (seg.getLevel() == this.mMaxPullLevel ? this.mSegmentPullColor : this.mTextColor) : (seg.getLevel() == this.mMaxPushLevel ? this.mSegmentPushColor : this.mTextColor));

            //數字
            float x = (float) ((double) this.mBaseX + Math.cos(Math.toRadians(((float) i * this.mAngle - 90.0F + this.mAngle / 2.0F))) * (double) (this.mRadius + this.mTextSize));
            float y = (float) ((double) this.mBaseY + Math.sin(Math.toRadians(((float) i * this.mAngle - 90.0F + this.mAngle / 2.0F))) * (double) (this.mRadius + this.mTextSize)) + this.mTextPaint.getFontMetrics().descent;
            this.mCanvas.drawText(seg.getLevel() != 0 ? String.valueOf(seg.getLevel()) : "", x, y, this.mTextPaint);
        }

    }

    private Rect mRect;

    private void drawTest() {
        String text = "23";
        if (mRect == null) {
            mRect = new Rect();
        }
        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);
        mTextPaint.setTextSize(18);
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.getTextBounds(text, 0, text.length(), mRect);
        for (float angle = 0.0F; angle < 361.0F; angle += mAngle) {
            float x = (float) ((double) mBaseX + Math.cos(Math.toRadians(angle)) * (double) mRadius);
            float y = (float) ((double) mBaseY + Math.sin(Math.toRadians(angle)) * (double) mRadius);
            mCanvas.drawText(text, 0, text.length(), x, y, mTextPaint);
        }
    }

    @Override
    public void onDrawForeground(Canvas canvas) {
        //   canvas.drawColor(Color.BLUE);
        super.onDrawForeground(canvas);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

//        drawBackground(canvas); // 绘制背景（不能重写）
//        onDraw(canvas); // 绘制主体
//        dispatchDraw(canvas); // 绘制子 View
//        onDrawForeground(canvas); // 绘制滑动相关和前景
    }

    /**
     * 放射線
     */

    private void drawSideLine() {

        //外面的圓
        //cx：圓心的x坐標。 cy：圓心的y坐標。 radius：圓的半徑。 paint：繪製時所使用的畫筆。
        this.mCanvas.drawCircle(this.mBaseX, this.mBaseY, this.mRadius, this.mSideLinePaint);

        //放射線
        for (float angle = 0.0F; angle < 361.0F; angle += this.mAngle) {
            float x = (float) ((double) this.mBaseX + Math.cos(Math.toRadians(angle)) * (double) this.mRadius);
            float y = (float) ((double) this.mBaseY + Math.sin(Math.toRadians(angle)) * (double) this.mRadius);
            this.mCanvas.drawLine(this.mBaseX, this.mBaseY, x, y, this.mSideLinePaint);
            Log.d("@@@@@@@", "drawSideLine: ");


            ///NEW
            // 在放射線外繪製數字
            double textRadius = mRadius + mTextSize * 1.5; // 調整數字與放射線的距離
            float textX = (float) ((double) mBaseX + Math.cos(Math.toRadians(angle)) * textRadius);
            float textY = (float) ((double) mBaseY + Math.sin(Math.toRadians(angle)) * textRadius) + mTextSize / 3; // 調整垂直位置

            // 調整角度以將 0 放在頂部
            float adjustedAngle = angle - 270; // 減去 90 度
            if (adjustedAngle < 0) {
                adjustedAngle += 360;
            }
            mCanvas.drawText(String.valueOf((int) adjustedAngle / (int) mAngle), textX, textY, mTextPaint);
//            this.mCanvas.drawText(String.valueOf((int) angle / (int) mAngle), textX, textY, mTextPaint);

            //NEW
        }

        //中間白線
        //startX：起始端點的X坐標。 startY：起始端點的Y坐標。 stopX：終止端點的X坐標。 stopY：終止端點的Y坐標。 paint：繪製直線所使用的畫筆。
        mCanvas.drawLine(mBaseX, mBaseY, mBaseX, 0.0F, mSideStartPaint);
    }

    float avgLevel1 = 0;
    float avgLevel2 = 0;

    public void setData(List<Segment> data) {
        int sum1 = 0;
        int sum2 = 0;
        int s1 = 0;
        int s2 = 0;

        for (Segment segment : data) {
            if (segment.isPull()) {
                sum1 += segment.getLevel();
                s1++;
            } else {
                sum2 += segment.getLevel();
                s2++;
            }
        }
        avgLevel1 = (float) (sum1 * 1.0 / s1);
        avgLevel2 = (float) (sum2 * 1.0 / s2);
    }

    /**
     * 平均線
     */
    private void drawAvgArc() {
        mAvgLevelPaint.setColor(mAvgPaintColor);

        for (int i = 0; i < this.mSegments.length; ++i) {
            ButterflyView.Segment seg = this.mSegments[i];
            if (seg.getLevel() != 0) {
                float avgLevel = seg.isPull() ? this.getAvgLevel(true) : this.getAvgLevel(false);

                // TODO:
                //  avgLevel = seg.isPull() ? avgLevel1 : avgLevel2;

                avgLevel *= this.mSegmentLevelUnit;
                RectF rectLevel = new RectF(mBaseX - avgLevel, mBaseY - avgLevel, mBaseX + avgLevel, mBaseY + avgLevel);

                /**
                 第一個參數 oval：定義承載圓弧形狀的矩形。通過設置該矩形可以指定圓弧的位置和大小。
                 第二個參數 startAngle: 設置圓弧是從哪個角度順時針繪畫的。
                 第三個參數 sweepAngle: 設置圓弧順時針掃過的角度。
                 第四個參數 useCenter: 繪製的時候是否使用圓心，我們繪製圓弧的時候設置為false,如果設置為true, 並且當前畫筆的描邊屬性設置為Paint.Style.FILL的時候，畫出的就是扇形。
                 第五個參數 paint: 指定繪製的畫筆。
                 */
                //平均線
                this.mCanvas.drawArc(rectLevel, (float) i * this.mAngle - 90.0F, this.mAngle, false, this.mAvgLevelPaint);

                //陰影
                this.mCanvas.drawArc(rectLevel, (float) i * this.mAngle - 90.0F, this.mAngle, true, this.mAvgLevelPaint2);
            }
        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);

        //w h  > dp
        Log.d("UUUUU", "onSizeChanged: " + w + "," + h + "," + oldW + "," + oldH);
        this.mViewWidth = w;
        this.mViewHeight = h;
        this.mBaseX = (float) this.mViewWidth / 2.0F;
        this.mBaseY = (float) this.mViewHeight / 2.0F;

        //圓的半徑
        this.mRadius = (float) Math.min(this.mViewWidth, this.mViewHeight) / 2.0F - this.mSideLineWidth - this.mTextSize * 2.0F;
        //  this.mRadius = (float)Math.min(this.mViewWidth, this.mViewHeight) / 2.0F - this.mSideLineWidth;

        this.mAngle = 360.0F / (float) this.mSegment;
        this.mSegmentLevelUnit = this.mRadius / (float) this.mSegmentLevel;
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
////        this.mViewWidth = MeasureSpec.getSize(widthMeasureSpec);
////        this.mViewHeight = MeasureSpec.getSize(heightMeasureSpec);
//        mViewWidth = getMeasuredWidth();
//        mViewHeight = getMeasuredHeight();
//
//
//        Log.d("UUUUU", "onMeasure1: " + mViewWidth +","+ mViewHeight);
//        Log.d("UUUUU", "onMeasure2: " + getMeasuredWidth() +","+ getMeasuredHeight());
//
//
//        this.mBaseX = (float)this.mViewWidth / 2.0F;
//        this.mBaseY = (float)this.mViewHeight / 2.0F;
//
//        //圓的半徑
//        this.mRadius = (float) Math.min(this.mViewWidth, this.mViewHeight) / 2.0F - this.mSideLineWidth - this.mTextSize * 2.0F;
//        //  this.mRadius = (float)Math.min(this.mViewWidth, this.mViewHeight) / 2.0F - this.mSideLineWidth;
//
//        this.mAngle = 360.0F / (float)this.mSegment;
//        this.mSegmentLevelUnit = this.mRadius / (float)this.mSegmentLevel;
//
//    }

    //    public void setSegmentLevel(int segment, int level, boolean pull) {
//        ButterflyView.Segment seg = this.mSegments[segment];
//        seg.setLevel(level);
//        seg.setPull(pull);
//        this.invalidate();
//    }

    public void setSegmentLevel(int segment, int level, boolean pull) {
        if (pull) {
            this.mMaxPullLevel = Math.max(level, this.mMaxPullLevel);
        } else {
            this.mMaxPushLevel = Math.max(level, this.mMaxPushLevel);
        }

        ButterflyView.Segment seg = this.mSegments[segment];
        seg.setLevel(level);
        seg.setPull(pull);
        this.invalidate();
    }

    private float getAvgLevel(boolean isPull) {
        int pullCount = 0;
        int totalPullLevel = 0;
        int pushCount = 0;
        int totalPushLevel = 0;
        ButterflyView.Segment[] var6 = this.mSegments;

        for (Segment segment : var6) {
            if (segment.getLevel() != 0) {
                if (segment.isPull()) {
                    totalPullLevel += segment.getLevel();
                    ++pullCount;
                } else {
                    totalPushLevel += segment.getLevel();
                    ++pushCount;
                }
            }
        }

        return isPull ? (float) totalPullLevel / (float) pullCount : (float) totalPushLevel / (float) pushCount;
    }

    private static float dp2px(int dp) {
        // 參數1：表示單位，其常用單位有如下表示（COMPLEX_UNIT_PX 、COMPLEX_UNIT_DIP、COMPLEX_UNIT_SP、COMPLEX_UNIT_PT、COMPLEX_UNIT_IN、COMPLEX_UNIT_MM ）
        // 參數2：表示單位對應值的大小
        // 參數3：封裝了顯示區域的各種屬性值
        return TypedValue.applyDimension(COMPLEX_UNIT_DIP, (float) dp, Resources.getSystem().getDisplayMetrics());
    }

    public static class Segment {
        int level;
        int color;
        boolean pull;

        public Segment() {
            this.level = 0;
            this.pull = true;
        }

        private String textNum;

        public String getTextNum() {
            return textNum;
        }

        public void setTextNum(String textNum) {
            this.textNum = textNum;
        }

        public int getLevel() {
            return this.level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public int getColor() {
            return this.color;
        }

        public void setColor(int color) {
            this.color = color;
        }

        public boolean isPull() {
            return this.pull;
        }

        public void setPull(boolean pull) {
            this.pull = pull;
        }
    }

}