package com.dyaco.spirit_commercial.support.custom_view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.dyaco.spirit_commercial.R;

public class CircleBarView extends View {

    private int mViewWidth;
    private int mViewHeight;

    private Path mFingerPath;
    private Paint mFingerPaint;
    private boolean mPaintFingerPath = false;
    private int mFingerPaintColor = Color.BLUE;

    private Bitmap mBitmap;
    private Paint mBitmapPaint;

    private float mCurrentX, mCurrentY;
    private final float TOUCH_TOLERANCE = 4;
    private boolean mTouchable = true;

    // Background part
    private Paint mBackRectanglePaint;


    //點選前 大圓的顏色
    private int mBackColor = Color.parseColor("#E0E0E0");


    private Paint mBackgroundColorPaint;

    //點選後  小圓的顏色
    private int mBarSelectSolidColor = Color.parseColor("#9AFF35");

    // Bar part
//    private Canvas mCanvas;
    private Paint mBarPaint;


    // line part
    private Paint mLinePaint;

    //點選後 線的顏色
    private int mLineColor = Color.parseColor("#9D2227");
    private float mStrokeWidth = 0.4f;
    private int mLineWidth = 4;


    private int mBarCount = 20;
    private int mBarMaxLevel = 10;
    private int mBarMinLevel = 1;

    //點選前 小圓的顏色
    private int mBarColor = Color.parseColor("#999999");

    private Bar[] mBars;

    private float mBarWidth = 30f;
    private float mSideWidth = 0f;

    private float mLevelUnitX;
    private float mLevelUnitY;

    private float mBarSpace;

    private Paint mCirclePaint; //大圓
    private Paint mCirclePaintB; //大圓的邊線   新加的
    private float mBarCircleRadius = 15f;

    //小圓的寬度
    private float mBarCircleWidth = 5f;

    //點選後 大圓的顏色   //#9D2227
    private int mBarSelectColor = Color.parseColor("#E0E0E0");
    private int mBarSelectColorB = Color.parseColor("#1396ef");

    private LevelChangedListener mLevelChangedListener;
    private OnLevelTouchUpListener mOnLevelTouchUpListener;

    public CircleBarView(Context context) {
        this(context, null);
    }

    public CircleBarView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        initAttr(attributeSet);

        initBars();

        initPaints();
    }


    public int getBarCount() {
        return this.mBarCount;
    }

    public int getBarMaxLevel() {
        return this.mBarMaxLevel;
    }

    public int getBarMinLevel() {
        return this.mBarMinLevel;
    }

    //    public boolean setBarLevel(int bar, int level) {
//
//        if (bar > (mBarCount - 1) || bar < 0) {
//            return false;
//        }
//        if (level > mBarMaxLevel || level < mBarMinLevel) {
//            return false;
//        }
//
//        mBars[bar].setLevel(level);
//        LogUtils.d("setBarLevel() bar:" + bar +", level:" + level + ", x:" + mBars[bar].getCenterX()  +", y:" + mBars[bar].getCenterY());
//        invalidate();
//        return true;
//    }
    public boolean setBarLevel(int bar, int level) {
        if (bar > (mBarCount - 1) || bar < 0) {
            return false;
        }
        if (level > mBarMaxLevel || level < mBarMinLevel) {
            return false;
        }

        mBars[bar].setLevel(level);

        float centerX = mSideWidth + (mLevelUnitX / 2) + mLevelUnitX * bar;
        float centerY = (mLevelUnitY / 2) + mLevelUnitY * (mBarMaxLevel - level);

        mBars[bar].setCenterX(centerX);
        mBars[bar].setCenterY(centerY);

        invalidate();
        return true;
    }

    public Bar[] getBars() {
        return mBars;
    }

    public void setLevelChangedListener(LevelChangedListener listener) {
        this.mLevelChangedListener = listener;
    }

    public void setOnLevelTouchUpListener(OnLevelTouchUpListener mOnLevelTouchUpListener) {
        this.mOnLevelTouchUpListener = mOnLevelTouchUpListener;
    }

    private void initAttr(AttributeSet attributeSet) {
        TypedArray typedArray = attributeSet == null ? null : getContext().obtainStyledAttributes(attributeSet, R.styleable.CircleBarView);
        if (typedArray != null) {
            mPaintFingerPath = typedArray.getBoolean(R.styleable.CircleBarView_paintFingerPath, mPaintFingerPath);
            mFingerPaintColor = typedArray.getInteger(R.styleable.CircleBarView_fingerPaintColor, mFingerPaintColor);
            mTouchable = typedArray.getBoolean(R.styleable.CircleBarView_touchable, mTouchable);
            mBarCount = typedArray.getInt(R.styleable.CircleBarView_barCount, mBarCount);
            mBarMaxLevel = typedArray.getInteger(R.styleable.CircleBarView_barMaxLevel, mBarMaxLevel);
            mBarMinLevel = typedArray.getInteger(R.styleable.CircleBarView_barMinLevel, mBarMinLevel);
            mBarColor = typedArray.getColor(R.styleable.CircleBarView_barColor, mBarColor);
            mBackColor = typedArray.getColor(R.styleable.CircleBarView_backColor, mBackColor);
            mBarWidth = dp2px(typedArray.getInt(R.styleable.CircleBarView_barWidth, (int) mBarWidth));
            mSideWidth = dp2px(typedArray.getInteger(R.styleable.CircleBarView_sideWidth, (int) mSideWidth));

            mBarSelectColor = typedArray.getColor(R.styleable.CircleBarView_barSelectColor, mBarSelectColor);
            mBarSelectSolidColor = typedArray.getColor(R.styleable.CircleBarView_barSelectSolidColor, mBarSelectSolidColor);

            mBarCircleRadius = typedArray.getFloat(R.styleable.CircleBarView_barCircleRadius, mBarCircleRadius);
            mBarCircleWidth = typedArray.getFloat(R.styleable.CircleBarView_barCircleWidth, mBarCircleWidth);

            mLineColor = typedArray.getColor(R.styleable.CircleBarView_lineColor, mLineColor);

            mLineWidth = typedArray.getInt(R.styleable.CircleBarView_pb_lineWidth, mLineWidth);

            typedArray.recycle();

        }
    }

    private void initBars() {
        mBars = new Bar[mBarCount];
        for (int i = 0; i < mBars.length; i++) {
            mBars[i] = new Bar();
        }
    }

    // 方便您複製貼上的完整方法
    private void initPaints() {
        mFingerPaint = new Paint();
        mFingerPaint.setAntiAlias(true);
        mFingerPaint.setDither(true);
        mFingerPaint.setColor(mFingerPaintColor);
        mFingerPaint.setStyle(Paint.Style.STROKE);
        mFingerPaint.setStrokeJoin(Paint.Join.ROUND);
        mFingerPaint.setStrokeCap(Paint.Cap.ROUND);
        mFingerPaint.setStrokeWidth(12);
//        mFingerPaint.setAlpha(200);

        mFingerPath = new Path();

        //  mBitmapPaint = new Paint(Paint.DITHER_FLAG);

        // background paint setting
        mBackRectanglePaint = new Paint();
        mBackRectanglePaint.setStyle(Paint.Style.FILL);
        mBackRectanglePaint.setAntiAlias(true); //抗鋸齒
        mBackRectanglePaint.setDither(true); //防抖動
        mBackRectanglePaint.setColor(mBackColor);

        mBackgroundColorPaint = new Paint();
        mBackgroundColorPaint.setStyle(Paint.Style.FILL);
        mBackgroundColorPaint.setAntiAlias(true);
        mBackgroundColorPaint.setDither(true);
//        mBackgroundColorPaint.setStrokeWidth(8f);
        ColorDrawable background = (ColorDrawable) getBackground();
        mBackgroundColorPaint.setColor(background != null ? background.getColor() : mBarSelectSolidColor);
//        mBackgroundColorPaint.setColor(background != null ? background.getColor() : Color.WHITE);


        // bar paint setting
        mBarPaint = new Paint();
        mBarPaint.setStyle(Paint.Style.FILL);
        mBarPaint.setAntiAlias(true);
        mBarPaint.setDither(true);
        mBarPaint.setStrokeWidth(mBarCircleWidth);
        mBarPaint.setColor(mBarColor);

        //點選後的大圓
        mCirclePaint = new Paint();
        mCirclePaint.setStyle(Paint.Style.FILL);
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setDither(true);
//        mCirclePaint.setStrokeWidth(mBarCircleWidth);
        // 【*** 修改點 ***】將點選後大圓的背景顏色改為 #11181D
        mCirclePaint.setColor(Color.parseColor("#11181D"));


        //點選後的大圓 外圍的線
        mCirclePaintB = new Paint();
        mCirclePaintB.setStyle(Paint.Style.STROKE);
        mCirclePaintB.setAntiAlias(true);
        mCirclePaintB.setDither(true);
        mCirclePaintB.setStrokeWidth(4);
        mCirclePaintB.setColor(mBarSelectColor);


        mLinePaint = new Paint();
        mLinePaint.setStyle(Paint.Style.FILL);
        mLinePaint.setAntiAlias(true);
        mLinePaint.setDither(true);
        mLinePaint.setStrokeWidth(mLineWidth);
        mLinePaint.setColor(mLineColor);
    }

    public void setLineColor(int color) {
        mLineColor = color;
        mLinePaint.setColor(color);
        invalidate();
    }

    public void setBarSelectColor(int color) {
        mBarSelectColor = color;
        mCirclePaint.setColor(color);
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawBars(canvas);

        // draw finger path
        if (mPaintFingerPath) {
            canvas.drawPath(mFingerPath, mFingerPaint);
        }

//        setBarLevel(0, 10);
//        setBarLevel(2, 9);
//        setBarLevel(3, 8);
    }

    private void drawBars(Canvas canvas) {

        drawBackgroundCircle(canvas);

        drawLinkLine(canvas);

        for (Bar bar : mBars) {

            if (bar.getLevel() != 0) {

                //大圓
                canvas.drawCircle(bar.getCenterX(), bar.getCenterY(), mBarCircleRadius, mCirclePaint);


                //點選後的大圓 外圍的線 < 新加的
                canvas.drawCircle(bar.getCenterX(), bar.getCenterY(), mBarCircleRadius, mCirclePaintB);
//                canvas.drawCircle(bar.getCenterX(), bar.getCenterY(), mBarCircleRadius - mBarCircleWidth, mBackgroundColorPaint);

                //被點選的圓寬
                canvas.drawCircle(bar.getCenterX(), bar.getCenterY(), 4, mBackgroundColorPaint);

              //  Log.d("VVVVDDDD", "drawBars: " + mBarCircleRadius + "," + mBarCircleWidth);
            }
        }
    }

    private void makeItBeautiful(Canvas canvas) {
        float left = 0;
        float top = mViewHeight;
        float right = mViewWidth;
        float bottom = mViewHeight - mLevelUnitY / 2;

        canvas.drawRect(left, top, right, bottom, mBackgroundColorPaint);
    }

//    private void drawBackgroundRectangle(Canvas canvas, float left, float right) {
////        canvas.drawRect(left, mViewHeight, right, 0, mBackgroundPaint);
//
//        for (int i = 0; i < mBarMaxLevel; i++) {
//            float top = (mLevelUnitY / 2) + mLevelUnitY * i;
//            float bottom = mLevelUnitY * i;
//
//            canvas.drawRect(left, top, right, bottom, mBackRectanglePaint);
//        }
//    }

    private void drawLinkLine(Canvas canvas) {
        for (int i = 0; i < mBars.length - 1; i++) {
            Bar current = mBars[i];
            if (current.getCenterX() != 0 & current.getCenterY() != 0) {
                for (int j = i + 1; j < mBars.length; j++) {
                    Bar next = mBars[j];
                    if (next.getCenterX() != 0 & next.getCenterY() != 0) {

                        //相連的圓才畫線
                        if ((i + 1) == j) {
//                        canvas.drawLine(current.getCenterX(), current.getCenterY(), next.getCenterX(), next.getCenterY(), mBarPaint);
                            canvas.drawLine(current.getCenterX(), current.getCenterY(), next.getCenterX(), next.getCenterY(), mLinePaint);
                        }


                        //一律連線
                   //     canvas.drawLine(current.getCenterX(), current.getCenterY(), next.getCenterX(), next.getCenterY(), mLinePaint);
                        break;
                    }
                }
            }
        }
    }

    //畫背景圓型
    private void drawBackgroundCircle(Canvas canvas) {

        for (int i = 0; i < mBarCount; i++) {


            float currentX = mSideWidth + (mLevelUnitX / 2) + mLevelUnitX * i;

            for (int j = 0; j < mBarMaxLevel; j++) {
                float currentY = (mLevelUnitY / 2) + mLevelUnitY * (mBarMaxLevel - j - 1);

                canvas.drawCircle(currentX, currentY, mBarCircleRadius, mBackRectanglePaint);
                canvas.drawCircle(currentX, currentY, mBarCircleWidth, mBarPaint);
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

//        mViewWidth = w;
        mViewWidth = w - ((int) mSideWidth * 2);
        mViewHeight = h;

        mLevelUnitX = (float) mViewWidth / (float) mBarCount;
        mLevelUnitY = (float) mViewHeight / (float) mBarMaxLevel;

        mBarSpace = ((float) mViewWidth - (mBarWidth * mBarCount)) / (mBarCount - 1);

        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
     //   LogUtils.d("onSizeChanged -> mViewWidth" + mViewWidth + ", mViewHeight:" + mViewHeight + ",mLevelUnitX:" + mLevelUnitX + ", mLevelUnitY:" + mLevelUnitY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mTouchable) return false;
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
        }

        return true;
    }

    private void updateBar(float x, float y) {

        int whichBar = Math.max(Math.min((int) ((x - mSideWidth) / mLevelUnitX), mBarCount - 1), 0);

        // level upside down
//        int whichLevel = mBarMaxLevel - Math.max(Math.min((int) ((y / mLevelUnitY)), mBarMaxLevel), 0);
        int whichLevel = Math.max((mBarMaxLevel - Math.max(Math.min((int) ((y / mLevelUnitY)), mBarMaxLevel), 0)), mBarMinLevel); // add min level check.

//        Log.d(TAG, "bar: " + whichBar + ", level: " + whichLevel);

        mBars[whichBar].setLevel(whichLevel);

        float centerX = mSideWidth + (mLevelUnitX / 2) + mLevelUnitX * whichBar;
        float centerY = (mLevelUnitY / 2) + mLevelUnitY * (mBarMaxLevel - whichLevel);

        mBars[whichBar].setCenterX(centerX);
        mBars[whichBar].setCenterY(centerY);


        mBars[whichBar].setSelect(true);

//        if (mBars[whichBar].getLevel() != 0) {
//            LogUtils.d("updateBar () 有滑動");
//            if (mLevelChangedListener != null) {
//                mLevelChangedListener.onDragLevel(true, mBars[whichBar].getLevel());
//            }
//        } else {
//            if (mLevelChangedListener != null) {
//                mLevelChangedListener.onDragLevel(false, -1);
//            }
//            LogUtils.d("updateBar () 沒滑動");
//        }

        if (whichBar == 19) {
         //  LogUtils.d("updateBar () 滑到最後一行");
            if (mLevelChangedListener != null) {
                mLevelChangedListener.onLastBarSelect(true);
            }
        } else {
//            mLevelChangedListener.onLastBarSelect(false);
          //  LogUtils.d("updateBar () 沒有滑到最後一行");
        }


     //   LogUtils.d("updateBar () whichBar:" + whichBar);
        if (whichBar == 19 && mBars[whichBar].getLevel() != 0) {
        //    LogUtils.d("updateBar () mBars20:" + mBars[whichBar].getLevel());
        }
   //     LogUtils.d("updateBar () mBars.length:" + mBars.length);
        // notify listener
        if (mLevelChangedListener != null) {
            mLevelChangedListener.onLevelChanged(whichBar, whichLevel);
        }
    }

    private void touch_start(float x, float y) {
        mFingerPath.reset();
        mFingerPath.moveTo(x, y);
        mFingerPath.lineTo(x + 1, y + 1); // draw a point on start
        mCurrentX = x;
        mCurrentY = y;

        updateBar(mCurrentX, mCurrentY);
    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mCurrentX);
        float dy = Math.abs(y - mCurrentY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mFingerPath.quadTo(mCurrentX, mCurrentY, (x + mCurrentX) / 2, (y + mCurrentY) / 2);
            mCurrentX = x;
            mCurrentY = y;

            updateBar(mCurrentX, mCurrentY);
        }
    }

    public void reset() {
        mBars = null;
        initBars();
        invalidate();
    }

    private void touch_up() {
        if (mOnLevelTouchUpListener != null) {
            mOnLevelTouchUpListener.onTouchUp();
        }
        mFingerPath.reset();

    }

    private static class Bar {
        int level = 0;

        float centerX;
        float centerY;
        boolean isSelect;

        public Bar() {
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public float getCenterX() {
            return centerX;
        }

        public void setCenterX(float centerX) {
            this.centerX = centerX;
        }

        public float getCenterY() {
            return centerY;
        }

        public void setCenterY(float centerY) {
            this.centerY = centerY;
        }

        public void setSelect(boolean select) {
            isSelect = select;
        }
    }

    private static float dp2px(int dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
    }

    public interface LevelChangedListener {
        void onLevelChanged(int bar, int level);

        void onLastBarSelect(boolean isSelect);
    }

    public interface OnLevelTouchUpListener {
        void onTouchUp();
    }


    public void setBarMaxLevel(int n) {
        mBarMaxLevel = n;
        mLevelUnitY = (float) mViewHeight / (float) mBarMaxLevel;
        invalidate();
    }

}