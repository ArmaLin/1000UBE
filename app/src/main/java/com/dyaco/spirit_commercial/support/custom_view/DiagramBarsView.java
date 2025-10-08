package com.dyaco.spirit_commercial.support.custom_view;

import static com.dyaco.spirit_commercial.App.UNIT_E;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.support.intdef.DeviceIntDef;

public class DiagramBarsView extends View {
    private final int BAR_TYPE_INCLINE = 1;
    private final int BAR_TYPE_SPEED = 2;
    private final int BAR_TYPE_BLANK = 3;

    private int mViewWidth; //整個View的寬度
    private int mViewHeight; //整個View的高度
    private Path mFingerPath;
    private Paint mFingerPaint;
    private boolean mPaintFingerPath;
    private boolean isTreadmill = true;
    private int mFingerPaintColor;

    private float mCurrentX;
    private float mCurrentY;
    private final float TOUCH_TOLERANCE;
    private boolean mTouchable;
    private Paint mBackRectanglePaint; //背景線paint
    private int mBackLineColor; //背景線顏色
//    private Paint mBackgroundColorPaint; //makeitb
    private int mBarCount;
    private int mBarMaxLevel; //總長度格數
    private float mBarMinLevel;
    private int mBarColor;

    private Paint mBarPaint;
    private DiagramBarsView.Bar[] mBars; //incline

    private Paint mBarPaint2;
    private DiagramBarsView.Bar[] mBars2; //level

    private Paint mBarPaint3;
    private DiagramBarsView.Bar[] mBars3; //blank


    private float mBarWidth;
    private float mSideWidth;//前後間隔
    private float mLevelUnitX;
    private float mLevelUnitY; //每一格的高度
    private float mBarSpace;//間隔
    private DiagramBarsView.LevelChangedListener mLevelChangedListener;
    private DiagramBarsView.LevelShowMsgListener levelShowMsgListener;
    private String mText = "8.0";

//    private Paint bPaint;
    private Resources resources;
    private Bitmap bitmap;
    private NinePatch ninePatch;

    private TextPaint mTextPaint;

    private Context context;

    public DiagramBarsView(Context context) {
        this(context, (AttributeSet) null);
    }

    public DiagramBarsView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context = context;
        mPaintFingerPath = true;
        mFingerPaintColor = -16776961;
        TOUCH_TOLERANCE = 4.0F;
        mTouchable = false;
        mBackLineColor = Color.parseColor("#70ffffff"); //背景線顏色
        mBarCount = 20;
        mBarMaxLevel = 20;
        mBarMinLevel = 0.0f;
        mBarColor = Color.parseColor("#991396EF"); //長條圖1顏色
        mBarWidth = 61.55F;
        mSideWidth = 0.0F;

        initAttr(attributeSet);

        initBars();
        initPaints();

        //Dialog bitmap

        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mTextPaint.setTextSize(44f);
        mTextPaint.setTextAlign(Paint.Align.CENTER);


//        bPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        bPaint.setFilterBitmap(true);
//        bPaint.setAntiAlias(true);

        resources = context.getResources();
        //   bitmap = BitmapFactory.decodeResource(resources, R.drawable.element_popup_diagram_incline);
        // bitmap = Bitmap.createScaledBitmap(bitmap, 149, 64, true);

        // ninePatch = new NinePatch(bitmap, bitmap.getNinePatchChunk(), null);

    }

    RectF rectf;

    private void drawDialog(Canvas canvas) {
        if (isShowDialog) {
            mTvX = mTvX - (textWidth / 4f);
            rectf = new RectF(mTvX, mTvY, mTvX + textWidth, mTvY + 64);
            ninePatch.draw(canvas, rectf);

            //  canvas.drawBitmap(bitmap, mTvX, mTvY, bPaint);

            //字的位置
            //   float x = mTvX + ((int) dp2px(112));
            float x = mTvX + ((int) dp2px(112));
            float y = mTvY + (ninePatch.getHeight() / 2.0f) + (mTextPaint.getTextSize() / 4); //字置中
            canvas.drawText(mText, x, y, mTextPaint);
        }
    }


    public void showDialog(String text, int bar, boolean isShow) {
        mText = text;
        textWidth = (int) Layout.getDesiredWidth(mText, mTextPaint) + ((int) dp2px(56));

        isShowDialog = isShow;
        dialogBar = bar;
        invalidate();
    }

    int textWidth;

    public void setText(String text) {
        mText = text;
        //107 82 57
        textWidth = (int) Layout.getDesiredWidth(mText, mTextPaint) + ((int) dp2px(56));

        //   Log.d("KKKKKKK", "setText: " + textWidth);
        //    textWidth += textWidth + ((int) dp2px(10));
        //  bitmap = Bitmap.createScaledBitmap(bitmap, width, 64, true);

        // ninePatch.setPaint(bPaint);
        //   rectf = new RectF(300, 0, width, 64);
        invalidate();
    }

    public int getBarCount() {
        return this.mBarCount;
    }

    public int getBarMaxLevel() {
        return this.mBarMaxLevel;
    }

    public void setBarMaxLevel(int mBarMaxLevel) {
        this.mBarMaxLevel = mBarMaxLevel;
    }

    public float getBarMinLevel() {
        return mBarMinLevel;
    }

    public void isTreadmill(boolean isTreadmill) {
        this.isTreadmill = isTreadmill;
        //  invalidate();
    }

    public void setBarSpace(float space) {
        mBarSpace = dp2px2(space);
        //  invalidate();
    }

    public void setBarCount(int count) {
        mBarCount = count;
        //  invalidate();
    }

    public void setBarWidth(float width) {
        mBarWidth = dp2px2(width);
        //  invalidate();
    }

    public void setBarWidthA() {
        //   float w = (float) ((1390 / mBarCount) - 7.5);
        float w = (float) ((mViewWidth / mBarCount) - 7.5);
        mBarWidth = dp2px2(w);
        //     invalidate();
    }

    public void setBarColor(int barType, int bar, int color) {

        DiagramBarsView.Bar[] bars;

        if (barType == 1) {
            bars = mBars;
        } else if (barType == 2) {
            bars = mBars2;
        } else {
            bars = mBars3;
        }

        if (bar <= mBarCount - 1 && bar >= 0) {
            //  bars[bar].setColor(color);
            bars[bar].setColor(ContextCompat.getColor(context, color));
            invalidate();
        }
    }

    public DiagramBarsView.Bar[] getBars() {
        return this.mBars;
    }

    public void setLevelChangedListener(DiagramBarsView.LevelChangedListener listener) {
        this.mLevelChangedListener = listener;
    }

    private void initAttr(AttributeSet attributeSet) {
        TypedArray typedArray = attributeSet == null ? null : getContext().obtainStyledAttributes(attributeSet, R.styleable.DiagramBarsView);
        if (typedArray != null) {
            mPaintFingerPath = typedArray.getBoolean(R.styleable.DiagramBarsView_paintFingerPathS, mPaintFingerPath);
            mFingerPaintColor = typedArray.getInteger(R.styleable.DiagramBarsView_fingerPaintColorS, mFingerPaintColor);
            mTouchable = typedArray.getBoolean(R.styleable.DiagramBarsView_touchableS, mTouchable);
            mBarCount = typedArray.getInt(R.styleable.DiagramBarsView_barCountS, mBarCount);
            mBarMaxLevel = typedArray.getInteger(R.styleable.DiagramBarsView_barMaxLevelS, mBarMaxLevel);
            mBarMinLevel = typedArray.getFloat(R.styleable.DiagramBarsView_barMinLevel2S, mBarMinLevel);
            mBarColor = typedArray.getColor(R.styleable.DiagramBarsView_barColorS, mBarColor);
            mBackLineColor = typedArray.getColor(R.styleable.DiagramBarsView_backLineColorS, mBackLineColor);
            mBarWidth = dp2px2(typedArray.getFloat(R.styleable.DiagramBarsView_barWidthS, mBarWidth));
            mSideWidth = dp2px2(typedArray.getFloat(R.styleable.DiagramBarsView_sideWidthS, mSideWidth));
            //  mBarSpace = dp2px2(typedArray.getFloat(R.styleable.DiagramBarsView_barSpace,  mBarSpace));

            typedArray.recycle();
        }

    }

    private void initBars() {
        mBars = new DiagramBarsView.Bar[mBarCount];
        mBars2 = new DiagramBarsView.Bar[mBarCount];
        mBars3 = new DiagramBarsView.Bar[mBarCount];

        for (int i = 0; i < mBars2.length; ++i) {
            mBars[i] = new DiagramBarsView.Bar();
            mBars[i].setLevel(0);
            //    mBars[i].setColor(Color.parseColor("#991396EF"));
            mBars[i].setColor(ContextCompat.getColor(context, R.color.color_incline_bar_run));

            mBars2[i] = new DiagramBarsView.Bar();
            mBars2[i].setLevel(0);
            mBars2[i].setColor(ContextCompat.getColor(context, R.color.color_level_speed_bar_run));
            //    mBars2[i].setColor(Color.parseColor("#99CD5BFF"));

            mBars3[i] = new DiagramBarsView.Bar();
            mBars3[i].setLevel(0);
            mBars3[i].setColor(ContextCompat.getColor(context, R.color.color_bar_blank));
            //   mBars3[i].setColor(Color.parseColor("#26FFFFFF"));
        }
    }

    private void initPaints() {
        mFingerPaint = new Paint();
        mFingerPaint.setAntiAlias(true); //抗鋸齒
        mFingerPaint.setDither(true); //防抖動
        mFingerPaint.setColor(this.mFingerPaintColor);
        mFingerPaint.setStyle(Style.STROKE);
        mFingerPaint.setStrokeJoin(Join.ROUND);
        mFingerPaint.setStrokeCap(Cap.ROUND);
        mFingerPaint.setStrokeWidth(12.0F);
        mFingerPath = new Path();
        //mBitmapPaint = new Paint(Paint.DITHER_FLAG);

        //背景線
        mBackRectanglePaint = new Paint();
        mBackRectanglePaint.setStyle(Style.STROKE);
        //  mBackRectanglePaint.setColor(mBackLineColor);
        mBackRectanglePaint.setColor(mBackLineColor);
        //   mBackRectanglePaint.setAntiAlias(true); //抗鋸齒
        //  mBackRectanglePaint.setAlpha(50);
        //    mBackRectanglePaint.setDither(true); //防抖動
        //mBackRectanglePaint.setStrokeWidth(1f);

//        mBackgroundColorPaint = new Paint();
//        mBackgroundColorPaint.setStyle(Style.FILL);
//        ColorDrawable background = (ColorDrawable) this.getBackground();
//        mBackgroundColorPaint.setColor(background != null ? background.getColor() : -1);

        mBarPaint = new Paint();
        mBarPaint.setStyle(Style.FILL);
        mBarPaint.setColor(mBarColor);

        mBarPaint2 = new Paint();
        mBarPaint2.setStyle(Style.FILL);

        mBarPaint3 = new Paint();
        mBarPaint3.setStyle(Style.FILL);
    }


    @Override
    protected void onDraw(Canvas canvas) {

        //繪製整個view的圓角
        Path path = getPath(20, false, false, true, true);
        canvas.clipPath(path);


//        Bitmap finalBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);// 一张白纸位图
//        Canvas mCanvas = new Canvas(finalBitmap);// 用指定的位图构造一个画布来绘制
//        mCanvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));// 画布绘制Bitmap时搞锯齿
//        mCanvas.drawPath(path, mBackRectanglePaint);// 绘制Dst
//        mBackRectanglePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));// 设置转换模式（显示Scr与Dst交接的区域）
//     //   mCanvas.drawBitmap(mBitmapOuter, src, dsc, mPaint);// 绘制Src
//        // 2. 再在原来的Canvas中将finalBitmap绘制出来。
//        canvas.drawBitmap(finalBitmap, 0, 0, null);


        super.onDraw(canvas);


        drawBackgroundLine(canvas, 0, 0);

        drawBars(canvas);

//        if (mPaintFingerPath) {
//            canvas.drawPath(mFingerPath, mFingerPaint);
//        }

        //    drawDialog(canvas);
    }


    private boolean isShowDialog = false;
    private int dialogBar = 0;
    float mTvX;
    float mTvY;

    private void drawBars(Canvas canvas) {
        for (int i = 0; i < mBarCount; i++) {

            drawBars(canvas, mBars2, mBarPaint2, i, BAR_TYPE_SPEED);
            if (isTreadmill) drawBars(canvas, mBars, mBarPaint, i, BAR_TYPE_INCLINE);
            drawBars(canvas, mBars3, mBarPaint3, i, BAR_TYPE_BLANK);
        }
//        drawBackgroundRectangle(canvas, 0, 0);
    }


    public boolean setBarLevel(int barType, int bar, float level) {
        dialogBar = bar;
        DiagramBarsView.Bar[] bars;

        if (barType == BAR_TYPE_INCLINE) { // incline
            bars = mBars;
        } else if (barType == BAR_TYPE_SPEED) { // level
            bars = mBars2;
        } else {
            bars = mBars3;
        }

        if (bar <= mBarCount - 1 && bar >= 0) {

            if (barType == BAR_TYPE_INCLINE) {
                level = level / 2; // 0.5 一階
            } else if ((barType == BAR_TYPE_SPEED) && isTreadmill) {
                level = level / 10; //0.1 一階
            }
            bars[bar].setLevel(level);
            invalidate();
            return true;

        } else {
            return false;
        }
    }

    private void drawBars(Canvas canvas, DiagramBarsView.Bar[] bars, Paint paint, int count, int barType) {

        float negativeLevel = mBarMaxLevel - bars[count].getLevel(); //剩下幾格 = 總長度格數 - 長度格數
        paint.setColor(bars[count].getColor());

        float left = mSideWidth + mBarWidth * (float) count + mBarSpace * (float) count; //Bar的左邊位置
        float top = (float) mViewHeight;
        float right = mSideWidth + mBarWidth * (float) count + mBarSpace * (float) count + mBarWidth; //Bar的右邊位置
        float bottom = mLevelUnitY * negativeLevel; //每一格的高度 * 剩下幾格 = Bar的底部

        if (barType == BAR_TYPE_INCLINE && UNIT_E == DeviceIntDef.METRIC) {
          //  incline 最高15，照比例顯示
            bottom = ((float) mViewHeight / 15) * (15 - bars[count].getLevel());
        //    bottom = ((float) mViewHeight / (MAX_INC_MAX >> 1)) * ((MAX_INC_MAX >> 1) - bars[count].getLevel());
        }

        //rx：x方向上的圓角半徑。
        //ry：y方向上的圓角半徑。
        // canvas.drawRoundRect(left, top, right, bottom, 8, 8, paint);        //繪製圓角矩形
        canvas.drawRect(left, top, right, bottom, paint);

        mTvX = left;
        mTvY = bottom - 70;

        bars[count].setBarLocation(new BarLocation((int) mTvX, (int) mTvY));

        if (bars == mBars) {
            //  if(count == 0) Log.d("UUUUUU", "drawBars: "+negativeLevel +"="+ mBarMaxLevel+"-"+ bars[count].getLevel() +",bottom="+bottom);
            mBars[count] = bars[count];
        } else if (bars == mBars2) {
            mBars2[count] = bars[count];
        } else {
            mBars3[count] = bars[count];
        }


//        //dialog的位置
//        if (count == dialogBar && bars == mBars) {
//
////            float bitmapWidth = (ninePatch.getWidth() / 2.0f) - (mBarWidth / 2.0f);
////            float bitmapHeight = (ninePatch.getHeight());
////            mTvX = left - bitmapWidth;
////            mTvY = bottom - bitmapHeight;
//
//            float bitmapWidth = (160 / 2.0f) - (mBarWidth / 2.0f);
//            float bitmapHeight = 70;
//
//            mTvY = bottom - bitmapHeight;
//            mTvX = left;
//
////            if (levelShowMsgListener != null) {
////                levelShowMsgListener.onLevelShow((int) mTvX, (int) mTvY, bars[count].getLevel());
////            }
//        }
    }


    //整個view的圓角
    private Path getPath(float radius, boolean topLeft, boolean topRight,
                         boolean bottomRight, boolean bottomLeft) {

        final Path path = new Path();
        final float[] radii = new float[8];

        if (topLeft) {
            radii[0] = radius;
            radii[1] = radius;
        }

        if (topRight) {
            radii[2] = radius;
            radii[3] = radius;
        }

        if (bottomRight) {
            radii[4] = radius;
            radii[5] = radius;
        }

        if (bottomLeft) {
            radii[6] = radius;
            radii[7] = radius;
        }

        path.addRoundRect(new RectF(0, 0, getWidth(), getHeight()),
                radii, Path.Direction.CW);

        return path;
    }

    public Bar getBar(int barType, int barPosition) {
        DiagramBarsView.Bar[] bars;
        if (barType == BAR_TYPE_INCLINE) { // incline
            bars = mBars;
        } else if (barType == BAR_TYPE_SPEED) { // level
            bars = mBars2;
        } else {
            bars = mBars3;
        }

        return bars[barPosition];
    }

    //畫背景線
    private void drawBackgroundLine(Canvas canvas, float left, float right) {

//        for (int i = 0; i < mBarMaxLevel; ++i) {
//            float bottom = mLevelUnitY * (float) i; // mLevelUnitY = mViewHeight / mBarMaxLevel;
//            canvas.drawLine(0, bottom, mViewWidth, bottom, mBackRectanglePaint);
//        }

        float levelHeight = mViewHeight / 20f;
        for (int i = 0; i < 20; ++i) {
            float bottom = levelHeight * (float) i; // mLevelUnitY = mViewHeight / mBarMaxLevel;
            canvas.drawLine(0, bottom, mViewWidth, bottom, mBackRectanglePaint);
        }
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w - (int) mSideWidth * 2;
        mViewHeight = h;

        setBarWidthA();

        //計算每一格的寬度
        mLevelUnitX = (float) mViewWidth / (float) mBarCount;

        //計算每一格的高度
        mLevelUnitY = (float) mViewHeight / (float) mBarMaxLevel;

        //計算間隔
        mBarSpace = ((float) mViewWidth - mBarWidth * (float) mBarCount) / (float) (mBarCount - 1);
        //   mBitmap = Bitmap.createBitmap(w, h, Config.ARGB_8888);

    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        performClick();
        if (!mTouchable) return false;

        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
        }
        return true;
    }


    /**
     * 根據手指的觸碰位置更新對應的長條高度。
     * @param x 手指當前的 X 座標
     * @param y 手指當前的 Y 座標
     */
    private void updateBar(float x, float y) {
        // 計算對應的長條索引：
        // - 公式：(x - mSideWidth) / mLevelUnitX 表示手指在哪個長條的範圍內
        // - 使用 Math.min 和 Math.max 限制範圍，防止索引越界
        int whichBar = Math.max(Math.min((int) ((x - mSideWidth) / mLevelUnitX), mBarCount - 1), 0);

        // 計算對應的高度級別（level）：
        // - 公式：mBarMaxLevel - y / mLevelUnitY，表示手指在長條的高度位置
        // - 使用 Math.min 和 Math.max 限制高度級別在合法範圍內
        float whichLevel = Math.max(mBarMaxLevel - Math.max(Math.min((int) (y / mLevelUnitY), mBarMaxLevel), 0), mBarMinLevel);

        // 更新對應的長條高度
        mBars[whichBar].setLevel(whichLevel);

        // 如果有監聽器，通知監聽器長條的高度已更改
        if (mLevelChangedListener != null) {
            mLevelChangedListener.onLevelChanged(whichBar, whichLevel);
        }
    }

    /**
     * 當手指開始觸碰時，初始化路徑並更新對應的長條狀態。
     * @param x 手指觸碰的 X 座標
     * @param y 手指觸碰的 Y 座標
     */
    private void touch_start(float x, float y) {
        // 重置手指繪製路徑，清除之前的路徑
        mFingerPath.reset();
        // 移動路徑的起點到手指觸碰的位置
        mFingerPath.moveTo(x, y);
        mFingerPath.lineTo(x + 1.0F, y + 1.0F);
        mCurrentX = x;
        mCurrentY = y;
        updateBar(mCurrentX, mCurrentY);
    }

    private void touch_move(float x, float y) {
        // 計算手指在 X 和 Y 軸上的位移
        float dx = Math.abs(x - mCurrentX);
        float dy = Math.abs(y - mCurrentY);

        // 如果位移大於容差（TOUCH_TOLERANCE），則更新路徑和長條狀態
        if (dx >= 4.0F || dy >= 4.0F) {

            // 使用貝茲曲線（quadTo）平滑連接上一點和當前點，形成流暢的手指移動路徑
            mFingerPath.quadTo(mCurrentX, mCurrentY, (x + mCurrentX) / 2.0F, (y + mCurrentY) / 2.0F);
            // 更新當前的觸碰座標
            mCurrentX = x;
            mCurrentY = y;
            // 根據當前觸碰座標更新長條狀態
            updateBar(mCurrentX, mCurrentY);
        }

    }

    private void touch_up() {
        // 手指抬起後清除路徑
        this.mFingerPath.reset();
    }

    private float dp2px(int dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) dp, Resources.getSystem().getDisplayMetrics());
    }

    private float dp2px2(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
    }

    public interface LevelChangedListener {
        void onLevelChanged(int var1, float var2);
    }

    public interface LevelShowMsgListener {
        void onLevelShow(int x, int y, float i);
    }

    public void setLevelShowMsgListener(DiagramBarsView.LevelShowMsgListener listener) {
        this.levelShowMsgListener = listener;
    }


    public static class Bar {
        float level = 1;
        int color = 1;
        BarLocation barLocation;

        public Bar(BarLocation barLocation) {
            this.barLocation = barLocation;
        }

        public BarLocation getBarLocation() {
            return barLocation;
        }

        public void setBarLocation(BarLocation barLocation) {
            this.barLocation = barLocation;
        }

        public Bar() {
        }

        public float getLevel() {
            return level;
        }

        public void setLevel(float level) {
            this.level = level;
        }

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }
    }
}