package com.dyaco.spirit_commercial.support.rulerpicker;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.CheckResult;
import androidx.annotation.ColorInt;
import androidx.annotation.Dimension;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dyaco.spirit_commercial.R;


/**
 * Created by Keval Patel on 28 Mar 2018.
 * <p>
 * This is custom {@link View} which will draw a ruler with indicators.
 * There are two types of indicators:
 * <li><b>Long Indicators:</b> These indicators marks specific important value after some periodic interval.
 * e.g. Long indicator represents evert 10th (10, 20, 30...) value.</li>
 * <li><b>Short Indicators:</b> There indicators represents single value.</li>
 */

final class RulerView extends View {

    /**
     * Height of the view. This view height is measured in {@link #onMeasure(int, int)}.
     *
     * @see #onMeasure(int, int)
     */
    private int mViewHeight;

    /**
     * {@link Paint} for the line in the ruler view.
     *
     * @see #refreshPaint()
     */
    private Paint mIndicatorPaint;

    /**
     * {@link Paint} to display the text on the ruler view.
     *
     * @see #refreshPaint()
     */
    private Paint mTextPaint;

    /**
     * Distance interval between two subsequent indicators on the ruler.
     *
     * @see #setIndicatorIntervalDistance(int)
     * @see #getIndicatorIntervalWidth()
     */
    private int mIndicatorInterval = 14 /* Default value */;

    /**
     * Minimum value. This value will be displayed at the left-most end of the ruler. This value
     * must be less than {@link #mMaxValue}.
     *
     * @see #setValueRange(int, int)
     * @see #getMinValue()
     */
    private int mMinValue = 0 /* Default value */;

    /**
     * Maximum value. This value will be displayed at the right-most end of the ruler. This value
     * must be greater than {@link #mMinValue}.
     *
     * @see #setValueRange(int, int)
     * @see #getMaxValue()
     */
    private int mMaxValue = 100 /* Default maximum value */;

    /**
     * Ratio of long indicator height to the ruler height. This value must be between 0 to 1. The
     * value should greater than {@link #mShortIndicatorHeight}. Default value is 0.6 (i.e. 60%).
     * If the value is 0, indicator won't be displayed. If the value is 1, indicator height will be
     * same as the ruler height.
     *
     * @see #setIndicatorHeight(float, float)
     * @see #getLongIndicatorHeightRatio()
     */
    private float mLongIndicatorHeightRatio = 0.6f /* Default value */;

    /**
     * Ratio of short indicator height to the ruler height. This value must be between 0 to 1. The
     * value should less than {@link #mLongIndicatorHeight}. Default value is 0.4 (i.e. 40%).
     * If the value is 0, indicator won't be displayed. If the value is 1, indicator height will be
     * same as the ruler height.
     *
     * @see #setIndicatorHeight(float, float)
     * @see #getShortIndicatorHeightRatio()
     */
    private float mShortIndicatorHeightRatio = 0.4f /* Default value */;

    /**
     * Actual height of the long indicator in pixels. This height is derived from
     * {@link #mLongIndicatorHeightRatio}.
     *
     * @see #updateIndicatorHeight(float, float)
     */
    private int mLongIndicatorHeight = 0;

    /**
     * Actual height of the short indicator in pixels. This height is derived from
     * {@link #mShortIndicatorHeightRatio}.
     *
     * @see #updateIndicatorHeight(float, float)
     */
    private int mShortIndicatorHeight = 0;

    /**
     * Integer color of the text, that is displayed on the ruler.
     *
     * @see #setTextColor(int)
     * @see #getTextColor()
     */
    @ColorInt
    private int mTextColor = Color.WHITE;

    /**
     * Integer color of the indicators.
     *
     * @see #setIndicatorColor(int)
     * @see #getIndicatorColor()
     */
    @ColorInt
    private int mIndicatorColor = Color.WHITE;

    /**
     * Height of the text, that is displayed on ruler in pixels.
     *
     * @see #setTextSize(int)
     * @see #getTextSize()
     */
    @Dimension
    private int mTextSize = 36;

    /**
     * Width of the indicator in pixels.
     *
     * @see #setIndicatorWidth(int)
     * @see #getIndicatorWidth()
     */
    @Dimension
    private float mIndicatorWidthPx = 4f;


    /**
     *
     */
    private final int offsetStart = 0;

    /**
     * 多少間隔畫長刻度
     */
    private final int longIndicatorStartInterval = 5;

    /**
     * 刻度文字距離上方
     */
    private int mTextMarginTop = 10;

    /**
     * 設定文字字形
     */
    private int mTextFontTypeId = R.font.inter_regular;

    /**
     * 是否顯示所有刻度數字
     */
    private boolean textIsAllShow = false;

    /**
     *
     */
    private int drawViewType = 0;

    /**
     *
     */
    private int startTextMarginLeft = 0;
    /**
     *
     */
    private int endTextMarginRight = 0;

    public RulerView(@NonNull final Context context) {
        super(context);
        parseAttr(null);
    }

    public RulerView(@NonNull final Context context,
                     @Nullable final AttributeSet attrs) {
        super(context, attrs);
        parseAttr(attrs);
    }

    public RulerView(@NonNull final Context context,
                     @Nullable final AttributeSet attrs,
                     final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        parseAttr(attrs);
    }

    public RulerView(@NonNull final Context context,
                     @Nullable final AttributeSet attrs,
                     int defStyleAttr,
                     int defStyleRes) {

        super(context, attrs, defStyleAttr, defStyleRes);
        parseAttr(attrs);
    }

    private void parseAttr(@Nullable AttributeSet attributeSet) {
        if (attributeSet != null) {
            TypedArray a = getContext().getTheme().obtainStyledAttributes(attributeSet,
                    R.styleable.RulerView,
                    0,
                    0);

            try { //Parse params
                if (a.hasValue(R.styleable.RulerView_ruler_text_color)) {
                    mTextColor = a.getColor(R.styleable.RulerView_ruler_text_color, Color.WHITE);
                }

                if (a.hasValue(R.styleable.RulerView_ruler_text_size)) {
                    mTextSize = a.getDimensionPixelSize(R.styleable.RulerView_ruler_text_size, 14);
                }

                if (a.hasValue(R.styleable.RulerView_indicator_color)) {
                    mIndicatorColor = a.getColor(R.styleable.RulerView_indicator_color, Color.WHITE);
                }

                if (a.hasValue(R.styleable.RulerView_indicator_width)) {
                    mIndicatorWidthPx = a.getDimensionPixelSize(R.styleable.RulerView_indicator_width,
                            4);
                }

                if (a.hasValue(R.styleable.RulerView_indicator_interval)) {
                    mIndicatorInterval = a.getDimensionPixelSize(R.styleable.RulerView_indicator_interval,
                            4);
                }

                if (a.hasValue(R.styleable.RulerView_ruler_text_margin_top)) {
                    mTextMarginTop = a.getDimensionPixelSize(R.styleable.RulerView_ruler_text_margin_top,
                            0);
                }

                if (a.hasValue(R.styleable.RulerView_long_height_height_ratio)) {
                    mLongIndicatorHeightRatio = a.getFraction(R.styleable.RulerView_long_height_height_ratio,
                            1, 1, 0.6f);
                }
                if (a.hasValue(R.styleable.RulerView_short_height_height_ratio)) {
                    mShortIndicatorHeightRatio = a.getFraction(R.styleable.RulerView_short_height_height_ratio,
                            1, 1, 0.4f);
                }
                setIndicatorHeight(mLongIndicatorHeightRatio, mShortIndicatorHeightRatio);

                if (a.hasValue(R.styleable.RulerView_min_value)) {
                    mMinValue = a.getInteger(R.styleable.RulerView_min_value, 0);
                }
                if (a.hasValue(R.styleable.RulerView_max_value)) {
                    mMaxValue = a.getInteger(R.styleable.RulerView_max_value, 100);
                }

                if (a.hasValue(R.styleable.RulerView_ruler_text_is_all_show)) {
                    textIsAllShow = a.getBoolean(R.styleable.RulerView_ruler_text_is_all_show, false);
                }

                if (a.hasValue(R.styleable.RulerView_ruler_draw_view_type)) {
                    drawViewType = a.getInteger(R.styleable.RulerView_ruler_draw_view_type, 0);
                }

                setValueRange(mMinValue, mMaxValue);
            } finally {
                a.recycle();
            }
        }
        refreshPaint();
    }

    /**
     * Create the indicator paint and value text color.
     */
    private void refreshPaint() {
        mIndicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mIndicatorPaint.setColor(mIndicatorColor);
        mIndicatorPaint.setStrokeWidth(mIndicatorWidthPx);
        mIndicatorPaint.setStyle(Paint.Style.STROKE);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTypeface(getContext().getResources().getFont(mTextFontTypeId));


        invalidate();
        requestLayout();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (drawViewType == 0) {
            drawView(canvas);
        } else if (drawViewType == 1) {
            drawAllTextView(canvas);
        } else if (drawViewType == 2) {
            drawStartEndTextView(canvas);
        }
        super.onDraw(canvas);
    }

    private void drawView(Canvas canvas) {
        for (int value = 1; value < mMaxValue - mMinValue; value++) {
            Log.v("hank", "onDraw: value:" + value);
            if (value % longIndicatorStartInterval == 0) {
                drawLongIndicator(canvas, value);
                drawValueText(canvas, value);
                Log.v("hank", "onDraw 長座標: value:" + value);
            } else {
                Log.v("hank", "onDraw 短座標: value:" + value);
                drawSmallIndicator(canvas, value);
            }
        }

        //Draw the first indicator.
        drawSmallIndicator(canvas, 0);

        //Draw the last indicator.
        drawSmallIndicator(canvas, getWidth());
    }

    private void drawAllTextView(Canvas canvas) {
        for (int value = 1; value < mMaxValue - mMinValue; value++) {
            Log.v("hank", "onDraw: value:" + value + ", maxValue:" + mMaxValue);
            if (value % longIndicatorStartInterval == 0) {
                drawLongIndicator(canvas, value);
                if (value != mMaxValue - mMinValue - 1) {
                    drawValueText(canvas, value);
                }
                Log.v("hank", "onDraw 長座標: value:" + value);
            } else {
                Log.v("hank", "onDraw 短座標: value:" + value);
                drawSmallIndicator(canvas, value);
                if (value != mMaxValue - mMinValue - 1) drawValueText(canvas, value);
            }

            if (value == (mMaxValue - mMinValue - 1)) {
                drawLongIndicator(canvas, value, 2);
                drawValueText(canvas, value, true ,11);
            }
        }

        //Draw the first indicator.
        drawSmallIndicator(canvas, 0);
        drawValueText(canvas, 0, false ,startTextMarginLeft);

        //Draw the last indicator.
        drawSmallIndicator(canvas, getWidth());
    }

    private void drawStartEndTextView(Canvas canvas) {
        for (int value = 1; value < mMaxValue - mMinValue; value++) {
            Log.v("hank", "onDraw: value:" + value + ", maxValue:" + mMaxValue);
            if (value % longIndicatorStartInterval == 0) {
                drawLongIndicator(canvas, value);
                if (value != mMaxValue - mMinValue - 1) {
                    drawValueText(canvas, value);
                }
                Log.v("hank", "onDraw 長座標: value:" + value);
            } else {
                Log.v("hank", "onDraw 短座標: value:" + value);
                drawSmallIndicator(canvas, value);
//                if (value != mMaxValue - mMinValue - 1) drawValueText(canvas, value);
            }

            //last
            if (value == (mMaxValue - mMinValue - 1)) {
                drawLongIndicator(canvas, value, 2);
                drawValueText(canvas, value, true, endTextMarginRight);
            }
        }

        //first
        drawSmallIndicator(canvas, 0);
        drawValueText(canvas, 0, false, startTextMarginLeft);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //Measure dimensions
        mViewHeight = MeasureSpec.getSize(heightMeasureSpec);
        int viewWidth = (mMaxValue - mMinValue - 1) * mIndicatorInterval;

        updateIndicatorHeight(mLongIndicatorHeightRatio, mShortIndicatorHeightRatio);

        this.setMeasuredDimension(viewWidth, mViewHeight);
    }

    /**
     * Calculate and update the height of the long and the short indicators based on new ratios.
     *
     * @param longIndicatorHeightRatio  Ratio of long indicator height to the ruler height.
     * @param shortIndicatorHeightRatio Ratio of short indicator height to the ruler height.
     */
    private void updateIndicatorHeight(final float longIndicatorHeightRatio,
                                       final float shortIndicatorHeightRatio) {
        mLongIndicatorHeight = (int) (mViewHeight * longIndicatorHeightRatio);
        mShortIndicatorHeight = (int) (mViewHeight * shortIndicatorHeightRatio);

    }

    /**
     * Draw the vertical short line at every value.
     *
     * @param canvas {@link Canvas} on which the line will be drawn.
     * @param value  Value to calculate the position of the indicator.
     */
    private void drawSmallIndicator(@NonNull final Canvas canvas,
                                    final int value) {
        canvas.drawLine(mIndicatorInterval * value + offsetStart,
                0,
                mIndicatorInterval * value + offsetStart,
                mShortIndicatorHeight,
                mIndicatorPaint);
    }

    /**
     * Draw the vertical long line.
     *
     * @param canvas {@link Canvas} on which the line will be drawn.
     * @param value  Value to calculate the position of the indicator.
     */
    private void drawLongIndicator(@NonNull final Canvas canvas,
                                   final int value) {
        canvas.drawLine(mIndicatorInterval * value + offsetStart,
                0,
                mIndicatorInterval * value + offsetStart,
                mLongIndicatorHeight,
                mIndicatorPaint);
    }


    private void drawLongIndicator(@NonNull final Canvas canvas,
                                   final int value, int type) {
        canvas.drawLine(mIndicatorInterval * value - 2,
                0,
                mIndicatorInterval * value - 2,
                mLongIndicatorHeight,
                mIndicatorPaint);
    }

    /**
     * Draw the value number below the longer indicator. This will use {@link #mTextPaint} to draw
     * the text.
     *
     * @param canvas {@link Canvas} on which the text will be drawn.
     * @param value  Value to draw.
     */
    private void drawValueText(@NonNull final Canvas canvas,
                               final int value) {

        Log.v("hank", "drawValueText() value:" + value);
        canvas.drawText(String.valueOf(value + mMinValue),
                mIndicatorInterval * value + offsetStart,
                mLongIndicatorHeight + mTextPaint.getTextSize() + mTextMarginTop,
                mTextPaint);
    }

    private void drawValueText(@NonNull final Canvas canvas,
                               final int value, boolean isMarginRight, int marginPx) {

        float x;
        if (isMarginRight) {
            x = mIndicatorInterval * value - marginPx;
        } else {
            x = mIndicatorInterval * value + marginPx;
        }

        Log.v("hank", "新drawValueText() value:" + value);
        canvas.drawText(String.valueOf(value + mMinValue),
                x,
                mLongIndicatorHeight + mTextPaint.getTextSize() + mTextMarginTop,
                mTextPaint);
    }

    /////////////////////// Properties getter/setter ///////////////////////

    /**
     * @return Color integer value of the ruler text color.
     * @see #setTextColor(int)
     */
    @CheckResult
    @ColorInt
    int getTextColor() {
        return mIndicatorColor;
    }

    /**
     * Set the color of the text to display on the ruler.
     *
     * @param color Color integer value.
     */
    void setTextColor(@ColorInt final int color) {
        mTextColor = color;
        refreshPaint();
    }

    /**
     * @return Size of the text of ruler in pixels.
     * @see #setTextSize(int)
     */
    @CheckResult
    float getTextSize() {
        return mTextSize;
    }

    /**
     * Set the size of the text to display on the ruler.
     *
     * @param textSizeSp Text size dimension in dp.
     */
    void setTextSize(final int textSizeSp) {
        mTextSize = RulerViewUtils.sp2px(getContext(), textSizeSp);
        refreshPaint();
    }


    /**
     * @return Color integer value of the indicator color.
     * @see #setIndicatorColor(int)
     */
    @CheckResult
    @ColorInt
    int getIndicatorColor() {
        return mIndicatorColor;
    }

    /**
     * Set the indicator color.
     *
     * @param color Color integer value.
     */
    void setIndicatorColor(@ColorInt final int color) {
        mIndicatorColor = color;
        refreshPaint();
    }

    /**
     * @return Width of the indicator in pixels.
     * @see #setIndicatorWidth(int)
     */
    @CheckResult
    float getIndicatorWidth() {
        return mIndicatorWidthPx;
    }

    /**
     * Set the width of the indicator line in the ruler.
     *
     * @param widthPx Width in pixels.
     */
    void setIndicatorWidth(final int widthPx) {
        mIndicatorWidthPx = widthPx;
        refreshPaint();
    }


    /**
     * @return Get the minimum value displayed on the ruler.
     * @see #setValueRange(int, int)
     */
    @CheckResult
    int getMinValue() {
        return mMinValue;
    }

    /**
     * @return Get the maximum value displayed on the ruler.
     * @see #setValueRange(int, int)
     */
    @CheckResult
    int getMaxValue() {
        return mMaxValue;
    }

    /**
     * Set the maximum value to display on the ruler. This will decide the range of values and number
     * of indicators that ruler will draw.
     *
     * @param minValue Value to display at the left end of the ruler. This can be positive, negative
     *                 or zero. Default minimum value is 0.
     * @param maxValue Value to display at the right end of the ruler. This can be positive, negative
     *                 or zero.This value must be greater than min value. Default minimum value is 100.
     */
    void setValueRange(final int minValue, final int maxValue) {
        mMinValue = minValue;
        mMaxValue = maxValue;
        invalidate();
    }

    /**
     * @return Get distance between two indicator in pixels.
     * @see #setIndicatorIntervalDistance(int)
     */
    @CheckResult
    int getIndicatorIntervalWidth() {
        return mIndicatorInterval;
    }

    /**
     * Set the spacing between two vertical lines/indicators. Default value is 14 pixels.
     *
     * @param indicatorIntervalPx Distance in pixels. This cannot be negative number or zero.
     * @throws IllegalArgumentException if interval is negative or zero.
     */
    void setIndicatorIntervalDistance(final int indicatorIntervalPx) {
        if (indicatorIntervalPx <= 0)
            throw new IllegalArgumentException("Interval cannot be negative or zero.");

        mIndicatorInterval = indicatorIntervalPx;
        invalidate();
    }

    /**
     * @return Ratio of long indicator height to the ruler height.
     * @see #setIndicatorHeight(float, float)
     */
    @CheckResult
    float getLongIndicatorHeightRatio() {
        return mLongIndicatorHeightRatio;
    }

    /**
     * @return Ratio of short indicator height to the ruler height.
     * @see #setIndicatorHeight(float, float)
     */
    @CheckResult
    float getShortIndicatorHeightRatio() {
        return mShortIndicatorHeightRatio;
    }

    /**
     * Set the height of the long and short indicators.
     *
     * @param longHeightRatio  Ratio of long indicator height to the ruler height. This value must
     *                         be between 0 to 1. The value should greater than {@link #mShortIndicatorHeight}.
     *                         Default value is 0.6 (i.e. 60%). If the value is 0, indicator won't
     *                         be displayed. If the value is 1, indicator height will be same as the
     *                         ruler height.
     * @param shortHeightRatio Ratio of short indicator height to the ruler height. This value must
     *                         be between 0 to 1. The value should less than {@link #mLongIndicatorHeight}.
     *                         Default value is 0.4 (i.e. 40%). If the value is 0, indicator won't
     *                         be displayed. If the value is 1, indicator height will be same as
     *                         the ruler height.
     * @throws IllegalArgumentException if any of the parameter is invalid.
     */
    void setIndicatorHeight(final float longHeightRatio,
                            final float shortHeightRatio) {

        if (shortHeightRatio < 0 || shortHeightRatio > 1) {
            throw new IllegalArgumentException("Sort indicator height must be between 0 to 1.");
        }

        if (longHeightRatio < 0 || longHeightRatio > 1) {
            throw new IllegalArgumentException("Long indicator height must be between 0 to 1.");
        }

        if (shortHeightRatio > longHeightRatio) {
            throw new IllegalArgumentException("Long indicator height cannot be less than sort indicator height.");
        }

        mLongIndicatorHeightRatio = longHeightRatio;
        mShortIndicatorHeightRatio = shortHeightRatio;

        updateIndicatorHeight(mLongIndicatorHeightRatio, mShortIndicatorHeightRatio);

        invalidate();
    }

    public boolean isTextIsAllShow() {
        return textIsAllShow;
    }

    public void setTextIsAllShow(boolean textIsAllShow) {
        this.textIsAllShow = textIsAllShow;
        refreshPaint();
    }


    public int getDrawViewType() {
        return drawViewType;
    }

    public void setDrawViewType(int drawViewType) {
        this.drawViewType = drawViewType;
        refreshPaint();
    }


    public int getStartTextMarginLeft() {
        return startTextMarginLeft;
    }

    public void setStartTextMarginLeft(int startTextMarginLeft) {
        this.startTextMarginLeft = startTextMarginLeft;
        refreshPaint();
    }

    public int getEndTextMarginRight() {
        return endTextMarginRight;
    }

    public void setEndTextMarginRight(int endTextMarginRight) {
        this.endTextMarginRight = endTextMarginRight;
        refreshPaint();
    }
}




//10/23為改版前
//final class RulerView extends View {
//
//    /**
//     * Height of the view. This view height is measured in {@link #onMeasure(int, int)}.
//     *
//     * @see #onMeasure(int, int)
//     */
//    private int mViewHeight;
//
//    /**
//     * {@link Paint} for the line in the ruler view.
//     *
//     * @see #refreshPaint()
//     */
//    private Paint mIndicatorPaint;
//
//    /**
//     * {@link Paint} to display the text on the ruler view.
//     *
//     * @see #refreshPaint()
//     */
//    private Paint mTextPaint;
//
//    /**
//     * Distance interval between two subsequent indicators on the ruler.
//     *
//     * @see #setIndicatorIntervalDistance(int)
//     * @see #getIndicatorIntervalWidth()
//     */
//    private int mIndicatorInterval = 14 /* Default value */;
//
//    /**
//     * Minimum value. This value will be displayed at the left-most end of the ruler. This value
//     * must be less than {@link #mMaxValue}.
//     *
//     * @see #setValueRange(int, int)
//     * @see #getMinValue()
//     */
//    private int mMinValue = 0 /* Default value */;
//
//    /**
//     * Maximum value. This value will be displayed at the right-most end of the ruler. This value
//     * must be greater than {@link #mMinValue}.
//     *
//     * @see #setValueRange(int, int)
//     * @see #getMaxValue()
//     */
//    private int mMaxValue = 100 /* Default maximum value */;
//
//    /**
//     * Ratio of long indicator height to the ruler height. This value must be between 0 to 1. The
//     * value should greater than {@link #mShortIndicatorHeight}. Default value is 0.6 (i.e. 60%).
//     * If the value is 0, indicator won't be displayed. If the value is 1, indicator height will be
//     * same as the ruler height.
//     *
//     * @see #setIndicatorHeight(float, float)
//     * @see #getLongIndicatorHeightRatio()
//     */
//    private float mLongIndicatorHeightRatio = 0.6f /* Default value */;
//
//    /**
//     * Ratio of short indicator height to the ruler height. This value must be between 0 to 1. The
//     * value should less than {@link #mLongIndicatorHeight}. Default value is 0.4 (i.e. 40%).
//     * If the value is 0, indicator won't be displayed. If the value is 1, indicator height will be
//     * same as the ruler height.
//     *
//     * @see #setIndicatorHeight(float, float)
//     * @see #getShortIndicatorHeightRatio()
//     */
//    private float mShortIndicatorHeightRatio = 0.4f /* Default value */;
//
//    /**
//     * Actual height of the long indicator in pixels. This height is derived from
//     * {@link #mLongIndicatorHeightRatio}.
//     *
//     * @see #updateIndicatorHeight(float, float)
//     */
//    private int mLongIndicatorHeight = 0;
//
//    /**
//     * Actual height of the short indicator in pixels. This height is derived from
//     * {@link #mShortIndicatorHeightRatio}.
//     *
//     * @see #updateIndicatorHeight(float, float)
//     */
//    private int mShortIndicatorHeight = 0;
//
//    /**
//     * Integer color of the text, that is displayed on the ruler.
//     *
//     * @see #setTextColor(int)
//     * @see #getTextColor()
//     */
//    @ColorInt
//    private int mTextColor = Color.WHITE;
//
//    /**
//     * Integer color of the indicators.
//     *
//     * @see #setIndicatorColor(int)
//     * @see #getIndicatorColor()
//     */
//    @ColorInt
//    private int mIndicatorColor = Color.WHITE;
//
//    /**
//     * Height of the text, that is displayed on ruler in pixels.
//     *
//     * @see #setTextSize(int)
//     * @see #getTextSize()
//     */
//    @Dimension
//    private int mTextSize = 36;
//
//    /**
//     * Width of the indicator in pixels.
//     *
//     * @see #setIndicatorWidth(int)
//     * @see #getIndicatorWidth()
//     */
//    @Dimension
//    private float mIndicatorWidthPx = 4f;
//
//
//    /**
//     *
//     */
//    private int offsetStart = 0;
//
//    /**
//     * 多少間隔畫長刻度
//     */
//    private int longIndicatorStartInterval = 5;
//
//    /**
//     * 刻度文字距離上方
//     */
//    private int mTextMarginTop = 10;
//
//    /**
//     * 設定文字字形
//     */
//    private int mTextFontTypeId = R.font.inter_extra_bold;
//
//    /**
//     * 是否顯示所有刻度數字
//     */
//    private boolean textIsAllShow = false;
//
//    public RulerView(@NonNull final Context context) {
//        super(context);
//        parseAttr(null);
//    }
//
//    public RulerView(@NonNull final Context context,
//                     @Nullable final AttributeSet attrs) {
//        super(context, attrs);
//        parseAttr(attrs);
//    }
//
//    public RulerView(@NonNull final Context context,
//                     @Nullable final AttributeSet attrs,
//                     final int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        parseAttr(attrs);
//    }
//
//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    public RulerView(@NonNull final Context context,
//                     @Nullable final AttributeSet attrs,
//                     int defStyleAttr,
//                     int defStyleRes) {
//
//        super(context, attrs, defStyleAttr, defStyleRes);
//        parseAttr(attrs);
//    }
//
//    private void parseAttr(@Nullable AttributeSet attributeSet) {
//        if (attributeSet != null) {
//            TypedArray a = getContext().getTheme().obtainStyledAttributes(attributeSet,
//                    R.styleable.RulerView,
//                    0,
//                    0);
//
//            try { //Parse params
//                if (a.hasValue(R.styleable.RulerView_ruler_text_color)) {
//                    mTextColor = a.getColor(R.styleable.RulerView_ruler_text_color, Color.WHITE);
//                }
//
//                if (a.hasValue(R.styleable.RulerView_ruler_text_size)) {
//                    mTextSize = a.getDimensionPixelSize(R.styleable.RulerView_ruler_text_size, 14);
//                }
//
//                if (a.hasValue(R.styleable.RulerView_indicator_color)) {
//                    mIndicatorColor = a.getColor(R.styleable.RulerView_indicator_color, Color.WHITE);
//                }
//
//                if (a.hasValue(R.styleable.RulerView_indicator_width)) {
//                    mIndicatorWidthPx = a.getDimensionPixelSize(R.styleable.RulerView_indicator_width,
//                            4);
//                }
//
//                if (a.hasValue(R.styleable.RulerView_indicator_interval)) {
//                    mIndicatorInterval = a.getDimensionPixelSize(R.styleable.RulerView_indicator_interval,
//                            4);
//                }
//
//                if (a.hasValue(R.styleable.RulerView_ruler_text_margin_top)) {
//                    mTextMarginTop = a.getDimensionPixelSize(R.styleable.RulerView_ruler_text_margin_top,
//                            0);
//                }
//
//                if (a.hasValue(R.styleable.RulerView_long_height_height_ratio)) {
//                    mLongIndicatorHeightRatio = a.getFraction(R.styleable.RulerView_long_height_height_ratio,
//                            1, 1, 0.6f);
//                }
//                if (a.hasValue(R.styleable.RulerView_short_height_height_ratio)) {
//                    mShortIndicatorHeightRatio = a.getFraction(R.styleable.RulerView_short_height_height_ratio,
//                            1, 1, 0.4f);
//                }
//                setIndicatorHeight(mLongIndicatorHeightRatio, mShortIndicatorHeightRatio);
//
//                if (a.hasValue(R.styleable.RulerView_min_value)) {
//                    mMinValue = a.getInteger(R.styleable.RulerView_min_value, 0);
//                }
//                if (a.hasValue(R.styleable.RulerView_max_value)) {
//                    mMaxValue = a.getInteger(R.styleable.RulerView_max_value, 100);
//                }
//
//                if (a.hasValue(R.styleable.RulerView_ruler_text_is_all_show)) {
//                    textIsAllShow = a.getBoolean(R.styleable.RulerView_ruler_text_is_all_show, false);
//                }
//                setValueRange(mMinValue, mMaxValue);
//            } finally {
//                a.recycle();
//            }
//        }
//        refreshPaint();
//    }
//
//    /**
//     * Create the indicator paint and value text color.
//     */
//    private void refreshPaint() {
//        mIndicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        mIndicatorPaint.setColor(mIndicatorColor);
//        mIndicatorPaint.setStrokeWidth(mIndicatorWidthPx);
//        mIndicatorPaint.setStyle(Paint.Style.STROKE);
//
//        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        mTextPaint.setColor(mTextColor);
//        mTextPaint.setTextSize(mTextSize);
//        mTextPaint.setTextAlign(Paint.Align.CENTER);
//        mTextPaint.setTypeface(getContext().getResources().getFont(mTextFontTypeId));
//
//
//        invalidate();
//        requestLayout();
//    }
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//        if (textIsAllShow) {
//            //Iterate through all value
//            for (int value = 1; value < mMaxValue - mMinValue; value++) {
//                Log.v("hank", "onDraw: value:" + value + ", maxValue:" + mMaxValue);
//                if (value % longIndicatorStartInterval == 0) {
//                    drawLongIndicator(canvas, value);
//                    if (value != mMaxValue - mMinValue - 1) {
//                        drawValueText(canvas, value);
//                    }
//                    Log.v("hank", "onDraw 長座標: value:" + value);
//                } else {
//                    Log.v("hank", "onDraw 短座標: value:" + value);
//                    drawSmallIndicator(canvas, value);
//                    if (value != mMaxValue - mMinValue - 1) drawValueText(canvas, value);
//                }
//
//                if (value == (mMaxValue - mMinValue - 1)) {
//                    drawLongIndicator(canvas, value, 2);
//                    drawValueText(canvas, value, true ,11);
//                }
//            }
//
//            //Draw the first indicator.
//            drawSmallIndicator(canvas, 0);
//            drawValueText(canvas, 0, false ,4);
//
//            //Draw the last indicator.
//            drawSmallIndicator(canvas, getWidth());
//        } else {
//            for (int value = 1; value < mMaxValue - mMinValue; value++) {
//                Log.v("hank", "onDraw: value:" + value);
//                if (value % longIndicatorStartInterval == 0) {
//                    drawLongIndicator(canvas, value);
//                    drawValueText(canvas, value);
//                    Log.v("hank", "onDraw 長座標: value:" + value);
//                } else {
//                    Log.v("hank", "onDraw 短座標: value:" + value);
//                    drawSmallIndicator(canvas, value);
//                }
//            }
//
//            //Draw the first indicator.
//            drawSmallIndicator(canvas, 0);
//
//            //Draw the last indicator.
//            drawSmallIndicator(canvas, getWidth());
//        }
//        super.onDraw(canvas);
//    }
//
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        //Measure dimensions
//        mViewHeight = MeasureSpec.getSize(heightMeasureSpec);
//        int viewWidth = (mMaxValue - mMinValue - 1) * mIndicatorInterval;
//
//        updateIndicatorHeight(mLongIndicatorHeightRatio, mShortIndicatorHeightRatio);
//
//        this.setMeasuredDimension(viewWidth, mViewHeight);
//    }
//
//    /**
//     * Calculate and update the height of the long and the short indicators based on new ratios.
//     *
//     * @param longIndicatorHeightRatio  Ratio of long indicator height to the ruler height.
//     * @param shortIndicatorHeightRatio Ratio of short indicator height to the ruler height.
//     */
//    private void updateIndicatorHeight(final float longIndicatorHeightRatio,
//                                       final float shortIndicatorHeightRatio) {
//        mLongIndicatorHeight = (int) (mViewHeight * longIndicatorHeightRatio);
//        mShortIndicatorHeight = (int) (mViewHeight * shortIndicatorHeightRatio);
//
//    }
//
//    /**
//     * Draw the vertical short line at every value.
//     *
//     * @param canvas {@link Canvas} on which the line will be drawn.
//     * @param value  Value to calculate the position of the indicator.
//     */
//    private void drawSmallIndicator(@NonNull final Canvas canvas,
//                                    final int value) {
//        canvas.drawLine(mIndicatorInterval * value + offsetStart,
//                0,
//                mIndicatorInterval * value + offsetStart,
//                mShortIndicatorHeight,
//                mIndicatorPaint);
//    }
//
//    /**
//     * Draw the vertical long line.
//     *
//     * @param canvas {@link Canvas} on which the line will be drawn.
//     * @param value  Value to calculate the position of the indicator.
//     */
//    private void drawLongIndicator(@NonNull final Canvas canvas,
//                                   final int value) {
//        canvas.drawLine(mIndicatorInterval * value + offsetStart,
//                0,
//                mIndicatorInterval * value + offsetStart,
//                mLongIndicatorHeight,
//                mIndicatorPaint);
//    }
//
//
//    private void drawLongIndicator(@NonNull final Canvas canvas,
//                                   final int value, int type) {
//        canvas.drawLine(mIndicatorInterval * value - 2,
//                0,
//                mIndicatorInterval * value - 2,
//                mLongIndicatorHeight,
//                mIndicatorPaint);
//    }
//
//    /**
//     * Draw the value number below the longer indicator. This will use {@link #mTextPaint} to draw
//     * the text.
//     *
//     * @param canvas {@link Canvas} on which the text will be drawn.
//     * @param value  Value to draw.
//     */
//    private void drawValueText(@NonNull final Canvas canvas,
//                               final int value) {
//
//        Log.v("hank", "drawValueText() value:" + value);
//        canvas.drawText(String.valueOf(value + mMinValue),
//                mIndicatorInterval * value + offsetStart,
//                mLongIndicatorHeight + mTextPaint.getTextSize() + mTextMarginTop,
//                mTextPaint);
//    }
//
//    private void drawValueText(@NonNull final Canvas canvas,
//                               final int value, boolean isMarginRight, int marginPx) {
//
//        float x;
//        if (isMarginRight) {
//            x = mIndicatorInterval * value - marginPx;
//        } else {
//            x = mIndicatorInterval * value + marginPx;
//        }
//
//        Log.v("hank", "新drawValueText() value:" + value);
//        canvas.drawText(String.valueOf(value + mMinValue),
//                x,
//                mLongIndicatorHeight + mTextPaint.getTextSize() + mTextMarginTop,
//                mTextPaint);
//    }
//
//    /////////////////////// Properties getter/setter ///////////////////////
//
//    /**
//     * @return Color integer value of the ruler text color.
//     * @see #setTextColor(int)
//     */
//    @CheckResult
//    @ColorInt
//    int getTextColor() {
//        return mIndicatorColor;
//    }
//
//    /**
//     * Set the color of the text to display on the ruler.
//     *
//     * @param color Color integer value.
//     */
//    void setTextColor(@ColorInt final int color) {
//        mTextColor = color;
//        refreshPaint();
//    }
//
//    /**
//     * @return Size of the text of ruler in pixels.
//     * @see #setTextSize(int)
//     */
//    @CheckResult
//    float getTextSize() {
//        return mTextSize;
//    }
//
//    /**
//     * Set the size of the text to display on the ruler.
//     *
//     * @param textSizeSp Text size dimension in dp.
//     */
//    void setTextSize(final int textSizeSp) {
//        mTextSize = RulerViewUtils.sp2px(getContext(), textSizeSp);
//        refreshPaint();
//    }
//
//
//    /**
//     * @return Color integer value of the indicator color.
//     * @see #setIndicatorColor(int)
//     */
//    @CheckResult
//    @ColorInt
//    int getIndicatorColor() {
//        return mIndicatorColor;
//    }
//
//    /**
//     * Set the indicator color.
//     *
//     * @param color Color integer value.
//     */
//    void setIndicatorColor(@ColorInt final int color) {
//        mIndicatorColor = color;
//        refreshPaint();
//    }
//
//    /**
//     * @return Width of the indicator in pixels.
//     * @see #setIndicatorWidth(int)
//     */
//    @CheckResult
//    float getIndicatorWidth() {
//        return mIndicatorWidthPx;
//    }
//
//    /**
//     * Set the width of the indicator line in the ruler.
//     *
//     * @param widthPx Width in pixels.
//     */
//    void setIndicatorWidth(final int widthPx) {
//        mIndicatorWidthPx = widthPx;
//        refreshPaint();
//    }
//
//
//    /**
//     * @return Get the minimum value displayed on the ruler.
//     * @see #setValueRange(int, int)
//     */
//    @CheckResult
//    int getMinValue() {
//        return mMinValue;
//    }
//
//    /**
//     * @return Get the maximum value displayed on the ruler.
//     * @see #setValueRange(int, int)
//     */
//    @CheckResult
//    int getMaxValue() {
//        return mMaxValue;
//    }
//
//    /**
//     * Set the maximum value to display on the ruler. This will decide the range of values and number
//     * of indicators that ruler will draw.
//     *
//     * @param minValue Value to display at the left end of the ruler. This can be positive, negative
//     *                 or zero. Default minimum value is 0.
//     * @param maxValue Value to display at the right end of the ruler. This can be positive, negative
//     *                 or zero.This value must be greater than min value. Default minimum value is 100.
//     */
//    void setValueRange(final int minValue, final int maxValue) {
//        mMinValue = minValue;
//        mMaxValue = maxValue;
//        invalidate();
//    }
//
//    /**
//     * @return Get distance between two indicator in pixels.
//     * @see #setIndicatorIntervalDistance(int)
//     */
//    @CheckResult
//    int getIndicatorIntervalWidth() {
//        return mIndicatorInterval;
//    }
//
//    /**
//     * Set the spacing between two vertical lines/indicators. Default value is 14 pixels.
//     *
//     * @param indicatorIntervalPx Distance in pixels. This cannot be negative number or zero.
//     * @throws IllegalArgumentException if interval is negative or zero.
//     */
//    void setIndicatorIntervalDistance(final int indicatorIntervalPx) {
//        if (indicatorIntervalPx <= 0)
//            throw new IllegalArgumentException("Interval cannot be negative or zero.");
//
//        mIndicatorInterval = indicatorIntervalPx;
//        invalidate();
//    }
//
//    /**
//     * @return Ratio of long indicator height to the ruler height.
//     * @see #setIndicatorHeight(float, float)
//     */
//    @CheckResult
//    float getLongIndicatorHeightRatio() {
//        return mLongIndicatorHeightRatio;
//    }
//
//    /**
//     * @return Ratio of short indicator height to the ruler height.
//     * @see #setIndicatorHeight(float, float)
//     */
//    @CheckResult
//    float getShortIndicatorHeightRatio() {
//        return mShortIndicatorHeightRatio;
//    }
//
//    /**
//     * Set the height of the long and short indicators.
//     *
//     * @param longHeightRatio  Ratio of long indicator height to the ruler height. This value must
//     *                         be between 0 to 1. The value should greater than {@link #mShortIndicatorHeight}.
//     *                         Default value is 0.6 (i.e. 60%). If the value is 0, indicator won't
//     *                         be displayed. If the value is 1, indicator height will be same as the
//     *                         ruler height.
//     * @param shortHeightRatio Ratio of short indicator height to the ruler height. This value must
//     *                         be between 0 to 1. The value should less than {@link #mLongIndicatorHeight}.
//     *                         Default value is 0.4 (i.e. 40%). If the value is 0, indicator won't
//     *                         be displayed. If the value is 1, indicator height will be same as
//     *                         the ruler height.
//     * @throws IllegalArgumentException if any of the parameter is invalid.
//     */
//    void setIndicatorHeight(final float longHeightRatio,
//                            final float shortHeightRatio) {
//
//        if (shortHeightRatio < 0 || shortHeightRatio > 1) {
//            throw new IllegalArgumentException("Sort indicator height must be between 0 to 1.");
//        }
//
//        if (longHeightRatio < 0 || longHeightRatio > 1) {
//            throw new IllegalArgumentException("Long indicator height must be between 0 to 1.");
//        }
//
//        if (shortHeightRatio > longHeightRatio) {
//            throw new IllegalArgumentException("Long indicator height cannot be less than sort indicator height.");
//        }
//
//        mLongIndicatorHeightRatio = longHeightRatio;
//        mShortIndicatorHeightRatio = shortHeightRatio;
//
//        updateIndicatorHeight(mLongIndicatorHeightRatio, mShortIndicatorHeightRatio);
//
//        invalidate();
//    }
//
//    public boolean isTextIsAllShow() {
//        return textIsAllShow;
//    }
//
//    public void setTextIsAllShow(boolean textIsAllShow) {
//        this.textIsAllShow = textIsAllShow;
//        refreshPaint();
//    }
//}