package com.dyaco.spirit_commercial.egym;

import static com.dyaco.spirit_commercial.App.MODE;
import static com.dyaco.spirit_commercial.App.UNIT_E;
import static com.dyaco.spirit_commercial.support.CommonUtils.subZeroAndDot;
import static com.dyaco.spirit_commercial.support.CommonUtils.subZeroAndDot2;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.MAX_RPM;
import static com.dyaco.spirit_commercial.workout.WorkoutChartsFragment.isChartHidden;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.product_flavor.ModeEnum;
import com.dyaco.spirit_commercial.support.RxTimer;
import com.dyaco.spirit_commercial.support.custom_view.BarLocation;
import com.dyaco.spirit_commercial.support.intdef.DeviceIntDef;

import java.util.List;

public class EgymDiagramBarsViewBySet extends View {
    // ──【新需求】─
    // 使用 durationTimesList 來決定 Bar 數量、各 Bar 寬度（依比例展現）與間隔
    private List<Integer> durationTimesList;         // 例如：[60, 120, 60, 120, ...]
    private float[] mBarWidths;                        // 每個 Bar 的寬度（依比例計算）
    // ─────────────────────────────

    private boolean showWhiteLine = false; // 是否顯示白線
    private float whiteLineX = 0;          // 白線的 X 座標
    private Paint whiteLinePaint;          // 白線的 Paint

    private Paint overlayPaint;

    private final int BAR_TYPE_INCLINE = 1;
    private final int BAR_TYPE_SPEED = 2;
    private final int BAR_TYPE_BLANK = 3;
    private float[] mBarSpaces;            // 每個 Bar 間的間隔

    private int mViewWidth;                // View 的寬度
    private int mViewHeight;               // View 的高度

    private boolean mPaintFingerPath;
    private boolean isTreadmill = true;
    private int mFingerPaintColor;

    private boolean mTouchable;
    private int mBackLineColor;            // 背景線顏色
    private int mBarCount;                 // Bar 數量（根據 durationTimesList.size() 更新）
    private int mBarMaxLevel;              // 長條總格數
    private float mBarMinLevel;
    private int mBarColor;

    private Paint backgroundPaint;
    private Paint mBarPaint;
    private EgymDiagramBarsViewBySet.Bar[] mBars;   // Incline
    private Paint mBarPaint2;
    private EgymDiagramBarsViewBySet.Bar[] mBars2;    // Level
    private Paint mBarPaint3;
    private EgymDiagramBarsViewBySet.Bar[] mBars3;    // Blank

    private float mBarWidth;               // 原有預設值（當未設定 durationTimesList 時使用）
    private float mSideWidth;              // 左右邊界間隔
    private float mLevelUnitX;
    private float mLevelUnitY;             // 每一格的高度
    private float mBarSpace;               // 內部間隔（參考用）
    private EgymDiagramBarsViewBySet.LevelChangedListener mLevelChangedListener;
    private EgymDiagramBarsViewBySet.LevelShowMsgListener levelShowMsgListener;
    private String mText = "8.0";
    private TextView iTextView;
    private TextView sTextView;
    private PopupWindow iPopupWindow;
    private PopupWindow sPopupWindow;

    private Context context;

    private int mBackgroundColor = Color.parseColor("#252e37"); // 預設背景色

    public void setCustomBackgroundColor(int color) {
        mBackgroundColor = color;
        invalidate();
    }

    public EgymDiagramBarsViewBySet(Context context) {
        this(context, null);
    }

    public EgymDiagramBarsViewBySet(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context = context;
        mPaintFingerPath = true;
        mFingerPaintColor = -16776961;
        mTouchable = false;
        mBackLineColor = Color.parseColor("#70ffffff");
        mBarCount = 20;        // 預設值，後續若呼叫 setDurationTimesList() 會更新
        mBarMaxLevel = 20;
        mBarMinLevel = 0.0f;
        mBarColor = Color.parseColor("#991396EF");
        mSideWidth = 0.0F;
        mBarWidth = 65.6F;      // 預設寬度
        mBarSpace = 0;         // 預設間隔

        // 初始化間隔陣列，初始以預設 mBarCount 大小
        mBarSpaces = new float[mBarCount];

        // 為避免 onDraw() 持續觸發，設定軟體層級
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        initAttr(attributeSet);
        initBars();
        // initPaints() 會在 onSizeChanged() 中呼叫
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
    }

    public void setBarSpace(float space) {
        mBarSpace = dp2px(space);
        invalidate();
    }

    public void setBarCount(int count) {
        mBarCount = count;
        invalidate();
    }

    public void setBarWidth(float width) {
        mBarWidth = dp2px(width);
        invalidate();
    }

    public void setBarWidthA() {
        float w = (float) ((mViewWidth / mBarCount) - 7.5);
        mBarWidth = dp2px(w);
        invalidate();
    }

    public void setBarColor(int barType, int bar, int color) {
        EgymDiagramBarsViewBySet.Bar[] bars;
        if (barType == BAR_TYPE_INCLINE) {
            bars = mBars;
        } else if (barType == BAR_TYPE_SPEED) {
            bars = mBars2;
        } else {
            bars = mBars3;
        }
        if (bar <= mBarCount - 1 && bar >= 0) {
            bars[bar].setColor(ContextCompat.getColor(context, color));
            invalidate();
        }
    }

    public EgymDiagramBarsViewBySet.Bar[] getBars() {
        return this.mBars;
    }

    public void setLevelChangedListener(EgymDiagramBarsViewBySet.LevelChangedListener listener) {
        this.mLevelChangedListener = listener;
    }

    private void initAttr(AttributeSet attributeSet) {
        TypedArray typedArray = attributeSet == null ? null : getContext().obtainStyledAttributes(attributeSet, R.styleable.EgymDiagramBarsView);
        if (typedArray != null) {
            mPaintFingerPath = typedArray.getBoolean(R.styleable.EgymDiagramBarsView_paintFingerPathS_E, mPaintFingerPath);
            mFingerPaintColor = typedArray.getInteger(R.styleable.EgymDiagramBarsView_fingerPaintColorS_E, mFingerPaintColor);
            mTouchable = typedArray.getBoolean(R.styleable.EgymDiagramBarsView_touchableS_E, mTouchable);
            mBarCount = typedArray.getInt(R.styleable.EgymDiagramBarsView_barCountS_E, mBarCount);
            mBarMaxLevel = typedArray.getInteger(R.styleable.EgymDiagramBarsView_barMaxLevelS_E, mBarMaxLevel);
            mBarMinLevel = typedArray.getFloat(R.styleable.EgymDiagramBarsView_barMinLevel2S_E, mBarMinLevel);
            mBarColor = typedArray.getColor(R.styleable.EgymDiagramBarsView_barColorS_E, mBarColor);
            mBackLineColor = typedArray.getColor(R.styleable.EgymDiagramBarsView_backLineColorS_E, mBackLineColor);
            mBarWidth = dp2px(typedArray.getFloat(R.styleable.EgymDiagramBarsView_barWidthS_E, mBarWidth));
            mSideWidth = dp2px(typedArray.getFloat(R.styleable.EgymDiagramBarsView_sideWidthS_E, mSideWidth));
            typedArray.recycle();
        }
    }

    private void initBars() {
        mBars = new EgymDiagramBarsViewBySet.Bar[mBarCount];
        mBars2 = new EgymDiagramBarsViewBySet.Bar[mBarCount];
        mBars3 = new EgymDiagramBarsViewBySet.Bar[mBarCount];

        // 若 mBarSpaces 陣列大小不足則重新初始化
        if(mBarSpaces == null || mBarSpaces.length != mBarCount) {
            mBarSpaces = new float[mBarCount];
        }

        for (int i = 0; i < mBarCount; ++i) {
            mBars[i] = new EgymDiagramBarsViewBySet.Bar();
            mBars[i].setLevel(0);
            mBars[i].setColor(ContextCompat.getColor(context, R.color.colorCd5bff));

            mBars2[i] = new EgymDiagramBarsViewBySet.Bar();
            mBars2[i].setLevel(0);
            mBars2[i].setColor(ContextCompat.getColor(context, R.color.color1396ef));

            mBars3[i] = new EgymDiagramBarsViewBySet.Bar();
            mBars3[i].setLevel(0);
            mBars3[i].setColor(ContextCompat.getColor(context, R.color.color8673ed));

            // 預設間距先設為 0（後續 calculateDynamicBarWidth() 中會重新計算）
            mBarSpaces[i] = 0;
        }
    }

    private void initPaints() {
        iTextView = new TextView(getContext());
        iTextView.setTextColor(Color.WHITE);
        iTextView.setTypeface(ResourcesCompat.getFont(context, R.font.inter_bold));
        iTextView.setTextSize(44);
        iTextView.setPadding(0, 0, 0, 0);
        iTextView.setGravity(Gravity.START);
        iTextView.setIncludeFontPadding(false);
        iTextView.setElevation(10f);
        iTextView.setTranslationZ(10f);

        sTextView = new TextView(getContext());
        sTextView.setTextColor(Color.WHITE);
        sTextView.setTypeface(ResourcesCompat.getFont(context, R.font.inter_bold));
        sTextView.setTextSize(44);
        sTextView.setPadding(0, 0, 0, 0);
        sTextView.setGravity(Gravity.START);
        sTextView.setIncludeFontPadding(false);
        sTextView.setElevation(10f);
        sTextView.setTranslationZ(10f);

        iPopupWindow = new PopupWindow(iTextView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        sPopupWindow = new PopupWindow(sTextView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        mBarPaint = new Paint();
        mBarPaint.setStyle(Style.FILL);

        mBarPaint2 = new Paint();
        mBarPaint2.setStyle(Style.FILL);

        mBarPaint3 = new Paint();
        mBarPaint3.setStyle(Style.FILL);

        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(mBackgroundColor);

        whiteLinePaint = new Paint();
        whiteLinePaint.setColor(ContextCompat.getColor(context, R.color.white_tran));
        whiteLinePaint.setStyle(Style.STROKE);
        whiteLinePaint.setStrokeWidth(dp2px(2));
        whiteLinePaint.setAntiAlias(true);

        overlayPaint = new Paint();
        overlayPaint.setColor(ContextCompat.getColor(context, R.color.half_transparent20));
        overlayPaint.setStyle(Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.TRANSPARENT);
        canvas.drawRect(0, 0, getWidth(), getHeight(), backgroundPaint);
        drawBars(canvas);
        if (showWhiteLine) {
            canvas.drawRect(whiteLineX, 0, getWidth(), getHeight(), overlayPaint);
            canvas.drawLine(whiteLineX, 0, whiteLineX, mViewHeight, whiteLinePaint);
        }
    }

    float mTvX;
    float mTvY;
    boolean isShowBar3 = true;

    private void drawBars(Canvas canvas) {
        for (int i = 0; i < mBarCount; i++) {
            drawBars(canvas, mBars2, mBarPaint2, i, BAR_TYPE_SPEED);
            drawBars(canvas, mBars, mBarPaint, i, BAR_TYPE_INCLINE);

            float barHeightI;
            float barHeightS;
            float negativeLevelI = mBarMaxLevel - mBars[i].getLevel();
            float negativeLevelS = mBarMaxLevel - mBars2[i].getLevel();
            float bottomI = mLevelUnitY * negativeLevelI;
            float bottomS = mLevelUnitY * negativeLevelS;

            if (MODE == ModeEnum.CT1000ENT) {
                if (UNIT_E == DeviceIntDef.METRIC) {
                    barHeightI = ((float) mViewHeight / 15) * (15 - mBars[i].getLevel());
                } else {
                    barHeightI = bottomI;
                }
            } else {
                barHeightI = bottomI;
            }
            barHeightI = mViewHeight - barHeightI;
            barHeightS = mViewHeight - bottomS;
            if (barHeightI >= barHeightS) {
                mBars3[i].setLevel(mBars2[i].getLevel());
            } else {
                mBars3[i].setLevel(0);
            }
            if (isShowBar3) {
                drawBars(canvas, mBars3, mBarPaint3, i, BAR_TYPE_BLANK);
            }
        }
    }

    public boolean setBarLevel(int barType, int index, float level, int currentIndex, boolean isFlow) {
        EgymDiagramBarsViewBySet.Bar[] bars;
        int orgRpm = (int) level;
        if (barType == BAR_TYPE_INCLINE) {
            bars = mBars;
        } else if (barType == BAR_TYPE_SPEED) {
            bars = mBars2;
        } else {
            bars = mBars3;
        }
        if (index <= mBarCount - 1 && index >= 0) {
            if (barType == BAR_TYPE_INCLINE) {
                if (MODE == ModeEnum.CT1000ENT) {
                    level = level / 2;
                } else {
                    level = scaleValue(level);
                }
            } else if ((barType == BAR_TYPE_SPEED) && isTreadmill) {
                level = level / 10;
                mBars3[index].setLevel(level);
            }
            bars[index].setLevel(level);
            float whiteLineX = calculateWhiteLineX(index);
            if (index == currentIndex) {
                if (!isFlow) {
                    if (MODE != ModeEnum.CT1000ENT && barType == BAR_TYPE_INCLINE) level = orgRpm;
                    showPopupText(String.valueOf(level), whiteLineX, level, index, barType);
                }
            }
            invalidate();
            return true;
        } else {
            return false;
        }
    }

    public int scaleValue(float originalValue) {
        if (originalValue > MAX_RPM) originalValue = MAX_RPM;
        return Math.round((originalValue / MAX_RPM) * mBarMaxLevel);
    }

    private float calculateWhiteLineX(int barIndex) {
        float x = mSideWidth;
        for (int i = 0; i < barIndex; i++) {
            if (mBarWidths != null && i < mBarWidths.length) {
                x += mBarWidths[i];
            } else {
                x += mBarWidth;
            }
            if (i < mBarSpaces.length) {
                x += mBarSpaces[i];
            }
        }
        float currentWidth = (mBarWidths != null && barIndex < mBarWidths.length) ? mBarWidths[barIndex] : mBarWidth;
        return x + currentWidth;
    }

    private void drawBars(Canvas canvas, EgymDiagramBarsViewBySet.Bar[] bars, Paint paint, int count, int barType) {
        float negativeLevel = mBarMaxLevel - bars[count].getLevel();
        paint.setColor(bars[count].getColor());
        float left = mSideWidth;
        for (int i = 0; i < count; i++) {
            if (mBarWidths != null && i < mBarWidths.length) {
                left += mBarWidths[i];
            } else {
                left += mBarWidth;
            }
            if (i < mBarSpaces.length) {
                left += mBarSpaces[i];
            }
        }
        float currentBarWidth = (mBarWidths != null && count < mBarWidths.length) ? mBarWidths[count] : mBarWidth;
        float right = left + currentBarWidth;
        float bottom = (float) mViewHeight;
        float top = mLevelUnitY * negativeLevel;
        if (MODE == ModeEnum.CT1000ENT) {
            if (barType == BAR_TYPE_INCLINE && UNIT_E == DeviceIntDef.METRIC) {
                top = ((float) mViewHeight / 15) * (15 - bars[count].getLevel());
            }
        }
        if (count > 0 && count < mBarSpaces.length && mBarSpaces[count - 1] > 0) {
            Paint clearPaint = new Paint();
            clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            canvas.drawRect(left - mBarSpaces[count - 1], 0, left, mViewHeight, clearPaint);
        }
        int iColor1 = ContextCompat.getColor(context, R.color.colorCd5bff);
        int iColor2 = Color.parseColor("#6D2391");
        int sColor1 = ContextCompat.getColor(context, R.color.color1396ef);
        int sColor2 = Color.parseColor("#083C64");
        int bColor1 = ContextCompat.getColor(context, R.color.color8673ED);
        int bColor2 = ContextCompat.getColor(context, R.color.color3E357A);
        if (barType == BAR_TYPE_INCLINE) {
            float barHeightI = mViewHeight - top;
            float barHeightS = mViewHeight - (mLevelUnitY * (mBarMaxLevel - mBars2[count].getLevel()));
            if (isShowBar3) {
                if (barHeightI <= barHeightS) {
                    iColor1 = bColor1;
                    iColor2 = bColor2;
                }
            }
        }
        int[] gradientColors = (barType == BAR_TYPE_INCLINE)
                ? new int[]{iColor1, iColor2}
                : (barType == BAR_TYPE_SPEED)
                ? new int[]{sColor1, sColor2}
                : new int[]{bColor1, bColor2};

        LinearGradient dynamicGradient = new LinearGradient(
                0, top, 0, bottom,
                gradientColors,
                null,
                Shader.TileMode.CLAMP
        );
        paint.setShader(dynamicGradient);
        paint.setXfermode(null);
        canvas.drawRect(left, top, right, bottom, paint);
        mTvX = left;
        mTvY = top;
        bars[count].setBarLocation(new BarLocation((int) mTvX, (int) mTvY));
        if (bars == mBars) {
            mBars[count] = bars[count];
        } else if (bars == mBars2) {
            mBars2[count] = bars[count];
        } else {
            mBars3[count] = bars[count];
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d("EgymDiagramBarsView", "onSizeChanged: ");
        mViewWidth = w - (int) mSideWidth * 2;
        mViewHeight = h;
        if (mViewWidth <= 0) {
            Log.e("EgymDiagramBarsView", "錯誤！mViewWidth 計算錯誤：" + mViewWidth);
            return;
        }
        calculateDynamicBarWidth();
        mLevelUnitX = (float) mViewWidth / (float) mBarCount;
        mLevelUnitY = (float) mViewHeight / (float) mBarMaxLevel;
        initPaints();
    }

    // ──【修改重點】─
    // 根據 durationTimesList 的數值來計算每個 Bar 的寬度與間隔，
    // 若格子數量過多而無法擺下，則自動將 Bar 寬度與間隔依比例縮小，
    // 並確保每個 Bar 的寬度至少不小於設定的最小寬度（預設 8dp，但會依實際狀況縮小）
    private void calculateDynamicBarWidth() {
        if (durationTimesList == null || durationTimesList.isEmpty()) {
            // 若未設定 durationTimesList，則使用固定寬度
            mBarWidth = mViewWidth / (float) mBarCount;
            mBarWidths = new float[mBarCount];
            for (int i = 0; i < mBarCount; i++) {
                mBarWidths[i] = mBarWidth;
            }
            for (int i = 0; i < mBarCount; i++) {
                mBarSpaces[i] = 0;
            }
        } else {
            // 設定理想值
            float desiredSpacing = dp2px(8);     // 理想間隔 8dp
            float desiredMinWidth = dp2px(8);      // 理想最小寬度 8dp

            // 計算若按理想值配置所需的總寬度
            float totalDesired = mBarCount * desiredMinWidth + (mBarCount - 1) * desiredSpacing;
            // 若所需寬度超過 mViewWidth，則縮放
            float scaleFactor = 1.0f;
            if (totalDesired > mViewWidth) {
                scaleFactor = mViewWidth / totalDesired;
            }
            float spacingConstant = desiredSpacing * scaleFactor;
            float minWidth = desiredMinWidth * scaleFactor;
            // 可用寬度扣除所有間隔
            float availableWidth = mViewWidth - (mBarCount - 1) * spacingConstant;

            // 計算所有 duration 值總和（用於寬度比例分配）
            float totalDuration = 0;
            for (int i = 0; i < durationTimesList.size(); i++) {
                totalDuration += durationTimesList.get(i);
            }
            // 初步依比例分配各 Bar 寬度
            float[] initialWidths = new float[mBarCount];
            for (int i = 0; i < mBarCount; i++) {
                initialWidths[i] = (durationTimesList.get(i) / totalDuration) * availableWidth;
            }
            // 將小於 minWidth 的項目視為固定寬度
            float sumFixed = 0;
            float sumVariable = 0;
            boolean[] isFixed = new boolean[mBarCount];
            for (int i = 0; i < mBarCount; i++) {
                if (initialWidths[i] < minWidth) {
                    isFixed[i] = true;
                    sumFixed += minWidth;
                } else {
                    isFixed[i] = false;
                    sumVariable += initialWidths[i];
                }
            }
            mBarWidths = new float[mBarCount];
            if (sumFixed > availableWidth) {
                // 若固定項目總和超過 availableWidth，則全部以 minWidth 處理
                for (int i = 0; i < mBarCount; i++) {
                    mBarWidths[i] = minWidth;
                }
            } else {
                float remainingAvailable = availableWidth - sumFixed;
                for (int i = 0; i < mBarCount; i++) {
                    if (isFixed[i]) {
                        mBarWidths[i] = minWidth;
                    } else {
                        mBarWidths[i] = (initialWidths[i] / sumVariable) * remainingAvailable;
                        if (mBarWidths[i] < minWidth) {
                            mBarWidths[i] = minWidth;
                        }
                    }
                }
            }
            // 設定間隔陣列：所有間隔皆固定為 spacingConstant
            mBarSpaces = new float[mBarCount];
            for (int i = 0; i < mBarCount; i++) {
                mBarSpaces[i] = (i < mBarCount - 1) ? spacingConstant : 0;
            }
        }
    }

    private float dp2px(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
    }

    public interface LevelChangedListener {
        void onLevelChanged(int var1, float var2);
    }

    public interface LevelShowMsgListener {
        void onLevelShow(int x, int y, float i);
    }

    public void setLevelShowMsgListener(EgymDiagramBarsViewBySet.LevelShowMsgListener listener) {
        this.levelShowMsgListener = listener;
    }

    public static class Bar {
        float level = 1;
        int color = 1;
        BarLocation barLocation;

        public Bar() {
        }

        public Bar(BarLocation barLocation) {
            this.barLocation = barLocation;
        }

        public BarLocation getBarLocation() {
            return barLocation;
        }

        public void setBarLocation(BarLocation barLocation) {
            this.barLocation = barLocation;
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

        @NonNull
        @Override
        public String toString() {
            return "Bar{" +
                    "level=" + level +
                    ", color=" + color +
                    ", Y=" + barLocation.getY() +
                    ", X=" + barLocation.getX() +
                    '}';
        }
    }

    // 若有需求個別設定某一個 Bar 間隔，可使用此方法（index 介於 0 ~ mBarCount-2）
    public void setBarSpacing(int index, float spacing) {
        if (index >= 0 && index < mBarCount - 1) {
            mBarSpaces[index] = dp2px(spacing);
            invalidate();
        }
    }

    public void setWhiteLinePosition(int barIndex) {
        if (barIndex < 0 || barIndex >= mBarCount) return;
        if (mBarWidths == null || mBarWidths.length == 0) {
            Log.w("EgymDiagramBarsView", "mBarWidths 尚未初始化，等待計算...");
            post(() -> setWhiteLinePosition(barIndex));
            return;
        }
        float x = mSideWidth;
        for (int i = 0; i < barIndex; i++) {
            if(mBarWidths != null && i < mBarWidths.length){
                x += mBarWidths[i];
            } else {
                x += mBarWidth;
            }
            if (i < mBarSpaces.length) {
                x += mBarSpaces[i];
            }
        }
        x += (mBarWidths != null && barIndex < mBarWidths.length) ? mBarWidths[barIndex] : mBarWidth;
        x = Math.round(x);
        whiteLineX = x;
        showWhiteLine = true;
        invalidate();
    }

    public void hideWhiteLine() {
        showWhiteLine = false;
        invalidate();
    }

    RxTimer iTimer;
    RxTimer sTimer;
    String tempRpm = "0";
    private void showPopupText(String text, float whiteLineX, float level, int index, int barType) {
        if (barType == BAR_TYPE_INCLINE && MODE == ModeEnum.CE1000ENT) {
            int number = (int) Double.parseDouble(text);
            number *= 2;
            text = String.valueOf(number);
        }
        String finalText = text;
        postDelayed(() -> {
            if (barType == BAR_TYPE_INCLINE) {
                if (getBar(BAR_TYPE_INCLINE, index).getBarLocation() == null) return;
            } else {
                if (getBar(BAR_TYPE_SPEED, index).getBarLocation() == null) return;
            }
            if (isChartHidden) return;
            if (getHeight() == 0) return;
            if (iTextView == null || sTextView == null) return;
            if (iPopupWindow == null || sPopupWindow == null) return;

            int textViewWidth;
            int textViewHeight;

            if (barType == BAR_TYPE_INCLINE) {
                if (MODE == ModeEnum.CT1000ENT) {
                    iTextView.setText(String.format("%s%% ", subZeroAndDot2(finalText)));
                    iTextView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.element_popup_diagram_incline));
                } else {
                    iTextView.setText(subZeroAndDot(finalText));
                    iTextView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bubble_workout_charts));
                }
                iTextView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
                textViewWidth = iTextView.getMeasuredWidth();
                textViewHeight = iTextView.getMeasuredHeight();
            } else {
                int dd;
                sTextView.setText(String.format("%s ", subZeroAndDot2(finalText)));
                if (isTreadmill) {
                    dd = R.drawable.element_popup_diagram_speed;
                } else {
                    dd = R.drawable.element_popup_diagram_level;
                }
                sTextView.setBackground(ContextCompat.getDrawable(getContext(), dd));
                sTextView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
                textViewWidth = sTextView.getMeasuredWidth();
                textViewHeight = sTextView.getMeasuredHeight();
            }

            int[] barsViewLocation = new int[2];
            getLocationOnScreen(barsViewLocation);
            int viewTopY = barsViewLocation[1];
            float barTopY;
            if (barType == BAR_TYPE_INCLINE) {
                barTopY = getBar(BAR_TYPE_INCLINE, index).getBarLocation().getY();
            } else {
                barTopY = getBar(BAR_TYPE_SPEED, index).getBarLocation().getY();
            }
            barTopY += (viewTopY - textViewHeight) + dp2px(10);
            int popupX = (int) (barsViewLocation[0] + this.whiteLineX - textViewWidth / 2);
            int popupY = (int) barTopY;
            if (barType == BAR_TYPE_INCLINE) {
                if (MODE == ModeEnum.CT1000ENT) {
                    if (iPopupWindow.isShowing()) {
                        iPopupWindow.update(popupX, popupY, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    } else {
                        iPopupWindow.showAtLocation(((MainActivity) context).getWindow().getDecorView(), Gravity.NO_GRAVITY, popupX, popupY);
                    }
                    if (iTimer != null) {
                        iTimer.cancel();
                        iTimer = null;
                    }
                    iTimer = new RxTimer();
                    iTimer.timer(1000, i -> iPopupWindow.dismiss());
                } else {
                    if (!finalText.equalsIgnoreCase(tempRpm)) {
                        if (iPopupWindow.isShowing()) {
                            iPopupWindow.update(popupX, popupY, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        } else {
                            iPopupWindow.showAtLocation(((MainActivity) context).getWindow().getDecorView(), Gravity.NO_GRAVITY, popupX, popupY);
                        }
                        if (iTimer != null) {
                            iTimer.cancel();
                            iTimer = null;
                        }
                        iTimer = new RxTimer();
                        iTimer.timer(1000, i -> iPopupWindow.dismiss());
                    }
                    tempRpm = finalText;
                }
            } else {
                if (sPopupWindow.isShowing()) {
                    sPopupWindow.update(popupX, popupY, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                } else {
                    sPopupWindow.showAtLocation(((MainActivity) context).getWindow().getDecorView(), Gravity.NO_GRAVITY, popupX, popupY);
                }
                if (sTimer != null) {
                    sTimer.cancel();
                    sTimer = null;
                }
                sTimer = new RxTimer();
                sTimer.timer(1000, i -> sPopupWindow.dismiss());
            }
        }, 20);
    }

    public void dismissPopupText() {
        if (sPopupWindow != null) {
            sPopupWindow.dismiss();
        }
        if (sTimer != null) {
            sTimer.cancel();
            sTimer = null;
        }
        if (iPopupWindow != null) {
            iPopupWindow.dismiss();
        }
        if (iTimer != null) {
            iTimer.cancel();
            iTimer = null;
        }
    }

    public EgymDiagramBarsViewBySet.Bar getBar(int barType, int barPosition) {
        EgymDiagramBarsViewBySet.Bar[] bars;
        if (barType == BAR_TYPE_INCLINE) {
            bars = mBars;
        } else if (barType == BAR_TYPE_SPEED) {
            bars = mBars2;
        } else {
            bars = mBars3;
        }
        return bars[barPosition];
    }

    // ──【新需求】─
    // 設定 durationTimesList，此方法會根據 List 的內容更新 mBarCount、重新初始化間距與 Bar 陣列，並觸發重新計算尺寸
    public void setDurationTimesList(List<Integer> list) {
        this.durationTimesList = list;
        mBarCount = list.size();
        mBarSpaces = new float[mBarCount];
        initBars();
        requestLayout();
        invalidate();
    }
}
