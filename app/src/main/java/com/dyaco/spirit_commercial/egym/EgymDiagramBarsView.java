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

public class EgymDiagramBarsView extends View {
    private boolean showWhiteLine = false; // 是否顯示白線
    private float whiteLineX = 0; // 白線的 X 座標
    private Paint whiteLinePaint; // 白線的 Paint

    private Paint overlayPaint;

    private final int BAR_TYPE_INCLINE = 1;
    private final int BAR_TYPE_SPEED = 2;
    private final int BAR_TYPE_BLANK = 3;
    private float[] mBarSpaces; // 存儲每個 Bar 的間距

    private int mViewWidth; //整個View的寬度
    private int mViewHeight; //整個View的高度

    private boolean mPaintFingerPath;
    private boolean isTreadmill = true;
    private int mFingerPaintColor;

    private boolean mTouchable;
    private int mBackLineColor; //背景線顏色
    //    private Paint mBackgroundColorPaint; //makeitb
    private int mBarCount;
    private int mBarMaxLevel; //總長度格數
    private float mBarMinLevel;
    private int mBarColor;

    private Paint backgroundPaint;
    private Paint mBarPaint;
    private EgymDiagramBarsView.Bar[] mBars; //incline

    private Paint mBarPaint2;
    private EgymDiagramBarsView.Bar[] mBars2; //level

    private Paint mBarPaint3;
    private EgymDiagramBarsView.Bar[] mBars3; //blank


    private float mBarWidth;
    private float mSideWidth;//前後間隔
    private float mLevelUnitX;
    private float mLevelUnitY; //每一格的高度
    private float mBarSpace;//間隔
    private EgymDiagramBarsView.LevelChangedListener mLevelChangedListener;
    private EgymDiagramBarsView.LevelShowMsgListener levelShowMsgListener;
    private String mText = "8.0";
    private TextView iTextView;
    private TextView sTextView;
    private PopupWindow iPopupWindow;
    private PopupWindow sPopupWindow;

    private Context context;

    private int mBackgroundColor = Color.parseColor("#252e37"); // 預設背景色

    public void setCustomBackgroundColor(int color) {
        mBackgroundColor = color;
        invalidate(); // 重新繪製
    }

    public EgymDiagramBarsView(Context context) {
        this(context, null);
    }

    public EgymDiagramBarsView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context = context;
        mPaintFingerPath = true;
        mFingerPaintColor = -16776961;
        mTouchable = false;
        mBackLineColor = Color.parseColor("#70ffffff"); //背景線顏色
        mBarCount = 20;
        mBarMaxLevel = 20;
        mBarMinLevel = 0.0f;
        mBarColor = Color.parseColor("#991396EF"); //長條圖1顏色
        //    mBarWidth = 61.55F;
        //    mBarWidth = 65.6F;
        mSideWidth = 0.0F;


        //如果放在 onDraw() 會造成 onDraw 持續被觸發
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        initAttr(attributeSet);

        initBars();
        //   initPaints();


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
        mBarSpace = dp2px(space);
        //  invalidate();
    }

    public void setBarCount(int count) {
        mBarCount = count;
        //  invalidate();
    }

    public void setBarWidth(float width) {
        mBarWidth = dp2px(width);
        //  invalidate();
    }

    public void setBarWidthA() {
        //   float w = (float) ((1390 / mBarCount) - 7.5);
        float w = (float) ((mViewWidth / mBarCount) - 7.5);
        mBarWidth = dp2px(w);
        //     invalidate();
    }

    public void setBarColor(int barType, int bar, int color) {

        EgymDiagramBarsView.Bar[] bars;

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

    public EgymDiagramBarsView.Bar[] getBars() {
        return this.mBars;
    }

    public void setLevelChangedListener(EgymDiagramBarsView.LevelChangedListener listener) {
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
            //  mBarSpace = dp2px2(typedArray.getFloat(R.styleable.DiagramBarsView_barSpace,  mBarSpace));

            typedArray.recycle();
        }

    }

    private void initBars() {
        mBars = new EgymDiagramBarsView.Bar[mBarCount];
        mBars2 = new EgymDiagramBarsView.Bar[mBarCount];
        mBars3 = new EgymDiagramBarsView.Bar[mBarCount];

        mBarSpaces = new float[mBarCount]; // 初始化間距陣列

        for (int i = 0; i < mBars.length; ++i) {
            mBars[i] = new EgymDiagramBarsView.Bar();
            mBars[i].setLevel(0);
            mBars[i].setColor(ContextCompat.getColor(context, R.color.colorCd5bff));

            mBars2[i] = new EgymDiagramBarsView.Bar();
            mBars2[i].setLevel(0);
            mBars2[i].setColor(ContextCompat.getColor(context, R.color.color1396ef));

            mBars3[i] = new EgymDiagramBarsView.Bar();
            mBars3[i].setLevel(0);
            mBars3[i].setColor(ContextCompat.getColor(context, R.color.color8673ed));

            // ✅ 預設每個 Bar 間距為 0
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
        mBarPaint.setStyle(Paint.Style.FILL);

        mBarPaint2 = new Paint();
        mBarPaint2.setStyle(Paint.Style.FILL);


        mBarPaint3 = new Paint();
        mBarPaint3.setStyle(Style.FILL);


        // 初始化背景 Paint
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(mBackgroundColor); // 讓背景色可動態設定


        whiteLinePaint = new Paint();
        whiteLinePaint.setColor(ContextCompat.getColor(context, R.color.white_tran));
        whiteLinePaint.setStyle(Paint.Style.STROKE);
        whiteLinePaint.setStrokeWidth(dp2px(2)); // 2dp 寬度
        whiteLinePaint.setAntiAlias(true);


        // ✅ 初始化半透明遮罩 Paint
        overlayPaint = new Paint();
        overlayPaint.setColor(ContextCompat.getColor(context, R.color.half_transparent20));
        overlayPaint.setStyle(Paint.Style.FILL);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // **保持畫布透明**
        canvas.drawColor(Color.TRANSPARENT);

        // **繪製背景**
        canvas.drawRect(0, 0, getWidth(), getHeight(), backgroundPaint);

        // **繪製 Bars**
        drawBars(canvas);

        // **繪製白線**
        if (showWhiteLine) {

            // **繪製半透明遮罩**
            canvas.drawRect(whiteLineX, 0, getWidth(), getHeight(), overlayPaint);

            //     Log.d("EgymDiagramBarsView", "白線繪製於 X = " + whiteLineX);
            canvas.drawLine(whiteLineX, 0, whiteLineX, mViewHeight, whiteLinePaint);
        }

        //    Log.d("EgymDiagramBarsView", "onDraw: ");
    }


    float mTvX;
    float mTvY;
    boolean isShowBar3 = true;

    private void drawBars(Canvas canvas) {
        for (int i = 0; i < mBarCount; i++) {

            drawBars(canvas, mBars2, mBarPaint2, i, BAR_TYPE_SPEED);


            //   if (isTreadmill) { //BIKE INCLINE 變成 Cadence
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
                //BIKE
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

            //     }

        }
    }


    public boolean setBarLevel(int barType, int index, float level, int currentIndex, boolean isFlow) {
        EgymDiagramBarsView.Bar[] bars;
        int orgRpm = (int) level;
        if (barType == BAR_TYPE_INCLINE) { // incline
            bars = mBars;
        } else if (barType == BAR_TYPE_SPEED) { // level
            bars = mBars2;
        } else {
            bars = mBars3;
        }

        if (index <= mBarCount - 1 && index >= 0) {

            if (barType == BAR_TYPE_INCLINE) {
                if (MODE == ModeEnum.CT1000ENT) {
                    level = level / 2; // 0.5 一階
                } else {
                    //BIKE CADENCE
                    level = scaleValue(level);

                }
            } else if ((barType == BAR_TYPE_SPEED) && isTreadmill) {
                level = level / 10; //0.1 一階
                mBars3[index].setLevel(level);
            }

            bars[index].setLevel(level);


            // **呼叫 showPopupText，顯示數值**
            float whiteLineX = calculateWhiteLineX(index);

            //執行一次 updateDiagramBarNum，會跑20根bar , 只 show 出當前index bar的 PopupText
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
            x += mBarWidth;
            if (i < mBarSpaces.length) {
                x += mBarSpaces[i];
            }
        }
        return x + mBarWidth; // 讓白線在 bar 右側
    }


    private void drawBars(Canvas canvas, EgymDiagramBarsView.Bar[] bars, Paint paint, int count, int barType) {
        //最大的bar格數level - 當前bar的 level, level 越大數字就越小，數字越小越靠近螢幕y軸的上方，因此bar就越長
        float negativeLevel = mBarMaxLevel - bars[count].getLevel();
        paint.setColor(bars[count].getColor());


        // ✅ 修正 `left` 計算，確保 Bar 不變窄
        float left = mSideWidth;
        for (int i = 0; i < count; i++) {
            left += mBarWidth; // 先加上 Bar 自身寬度
            if (i < mBarSpaces.length) {
                left += mBarSpaces[i]; // ✅ 只在 Bar 之間加間隔，確保不影響 Bar 寬度
            }
        }
        float right = left + mBarWidth; // ✅ 保持 Bar 的寬度不變
        float bottom = (float) mViewHeight; // 整個view的高度, y數字越大，就越靠近螢幕下方，所以是 bottom
        float top = mLevelUnitY * negativeLevel; //每一格level的高度 * 當前level 計算用的格數， 代表在當前view上所在的 上方y軸, 數字越小就越高

        if (MODE == ModeEnum.CT1000ENT) {
            if (barType == BAR_TYPE_INCLINE && UNIT_E == DeviceIntDef.METRIC) {
                // bar每一格的高度 = 整個View的高度 / bar的高度總共幾格 , incline 是15
                top = ((float) mViewHeight / 15) * (15 - bars[count].getLevel());
            }
        }

        // ✅ 讓間隔真正透明
        if (count > 0 && count < mBarSpaces.length && mBarSpaces[count - 1] > 0) {
            Paint clearPaint = new Paint();
            clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR)); // 讓間隔真正透明
            canvas.drawRect(left - mBarSpaces[count - 1], 0, left, mViewHeight, clearPaint);
        }

        int iColor1 = ContextCompat.getColor(context, R.color.colorCd5bff);
        int iColor2 = Color.parseColor("#6D2391");

        int sColor1 = ContextCompat.getColor(context, R.color.color1396ef);
        int sColor2 = Color.parseColor("#083C64");

        //incline 蓋住 speed 的顏色
        int bColor1 = ContextCompat.getColor(context, R.color.color8673ED);
        int bColor2 = ContextCompat.getColor(context, R.color.color3E357A);


        // ✅ Incline和Speed 重疊時的顏色
        if (barType == BAR_TYPE_INCLINE) {
            float barHeightI = mViewHeight - top; // 計算 `mBars` (Incline) 高度
            float barHeightS = mViewHeight - (mLevelUnitY * (mBarMaxLevel - mBars2[count].getLevel())); // `mBars2` 高度

            if (isShowBar3) {
                if (barHeightI <= barHeightS) {
                    // ❗當 Incline Bar 比 Speed Bar 矮時，變更為 `Blank` 顏色
                    iColor1 = bColor1;
                    iColor2 = bColor2;
                }
            }
        }

        // ✅ 設定 `LinearGradient`
        int[] gradientColors = (barType == BAR_TYPE_INCLINE)
                ? new int[]{iColor1, iColor2}
                : (barType == BAR_TYPE_SPEED)
                ? new int[]{sColor1, sColor2}
                : new int[]{bColor1, bColor2};

        LinearGradient dynamicGradient = new LinearGradient(
                0, top, 0, bottom, //漸層範圍
                gradientColors,
                null,
                Shader.TileMode.CLAMP
        );

        paint.setShader(dynamicGradient);

        paint.setXfermode(null);

        // bottom 是整個 view的高度 720, y越往下越大, 因此 720 在底部,
        // top 數字越小,越靠近view的上方,因此level越高,top 的數字就越小,bar的長度也就越高 (top代表每個bar的y軸上面位置)
        //須確保 bottom > top , 不然會出問題
        canvas.drawRect(left, top, right, bottom, paint);

        mTvX = left;
        mTvY = top;

        bars[count].setBarLocation(new BarLocation((int) mTvX, (int) mTvY));

        // ✅ 保留原始邏輯，確保 `mBars`, `mBars2`, `mBars3` 正確更新
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

        Log.d("VVVCCXAAA", "⚽onSizeChanged: ");
        mViewWidth = w - (int) mSideWidth * 2;
        mViewHeight = h;

        if (mViewWidth <= 0) {
            Log.e("EgymDiagramBarsView", "❌ `onSizeChanged()` 錯誤！mViewWidth 計算錯誤：" + mViewWidth);
            return;
        }

        calculateDynamicBarWidth(); // **這裡正確計算 mBarWidth**

        mLevelUnitX = (float) mViewWidth / (float) mBarCount;

        // bar每一格的高度 = 整個View的高度 / bar的高度總共幾格
        mLevelUnitY = (float) mViewHeight / (float) mBarMaxLevel;


        initPaints();
    }


    private void calculateDynamicBarWidth() {
        float totalSpacing = 0;
        for (int i = 0; i < mBarCount - 1; i++) {
            totalSpacing += mBarSpaces[i];
        }

        float availableWidth = mViewWidth - totalSpacing;
        if (availableWidth <= 0) {
            //       Log.e("EgymDiagramBarsView", "❌ 錯誤！`barWidth` 計算失敗，View 寬度不足！");
            mBarWidth = 1; // 避免 `mBarWidth` 變負數
        } else {
            mBarWidth = availableWidth / mBarCount;
        }

        //      Log.d("EgymDiagramBarsView", "✅ 計算 barWidth = " + mBarWidth + ", 總間隔 = " + totalSpacing);
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

    public void setLevelShowMsgListener(EgymDiagramBarsView.LevelShowMsgListener listener) {
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

        @NonNull
        @Override
        public String toString() {
            return "Bar{" +
                    "level=" + level +
                    ", color=" + color +
                    ", YYY=" + barLocation.getY() +
                    ", XXX=" + barLocation.getX() +
                    '}';
        }
    }

    //index 5 >>> 從0開始算,第5 跟第6 之間有 spacing
    public void setBarSpacing(int index, float spacing) {
        if (index >= 0 && index < mBarCount - 1) {
            mBarSpaces[index] = dp2px(spacing);
            invalidate(); // **不要這裡計算 `barWidth`**
        }
    }


    public void setWhiteLinePosition(int barIndex) {
        if (barIndex < 0 || barIndex >= mBarCount) return;

        if (mBarWidth == 0) {
            Log.w("EgymDiagramBarsView", "⚠️ `mBarWidth` 尚未初始化，等待計算...");
            post(() -> setWhiteLinePosition(barIndex)); // 延遲執行，確保 `mBarWidth` 已初始化
            return;
        }

        float x = mSideWidth; // **從 `mSideWidth` 開始**
        for (int i = 0; i < barIndex; i++) {
            x += mBarWidth;
            if (i < mBarSpaces.length) {
                x += mBarSpaces[i]; // **加上 spacing**
            }
        }

        // **不要加上 `mBarSpaces[barIndex]`，這樣白線才會緊貼 `bar[5]` 的右側**
        x += mBarWidth;

        // **✨ 微調白線位置，避免浮點誤差**
        x = Math.round(x);

        //    Log.d("EgymDiagramBarsView", "✅ 修正後白線 X (應該緊貼 bar[" + barIndex + "] 右側) = " + x);

        whiteLineX = x;
        showWhiteLine = true;
        invalidate();
    }


    /**
     * 隱藏白線
     */
    public void hideWhiteLine() {
        showWhiteLine = false;
        invalidate();
    }

    RxTimer iTimer;
    RxTimer sTimer;
    String tempRpm = "0";
    private void showPopupText(String text, float whiteLineX, float level, int index, int barType) {

        if (barType == BAR_TYPE_INCLINE && MODE == ModeEnum.CE1000ENT) {
            //EGYM SPM = RPM * 2
            int number = (int) Double.parseDouble(text);
            number *= 2;
            text = String.valueOf(number); // 轉回字串

        }

//setBarLevel 時，馬上執行showPopupText，但drawBars還沒跑完，無法取得正確的 bar高度，所以延遲執行
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
                    //###Incline
                    iTextView.setText(String.format("%s%% ", subZeroAndDot2(finalText)));
                    iTextView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.element_popup_diagram_incline));
                } else {
                    //###Cadence rpm, spm
                    iTextView.setText(subZeroAndDot(finalText));
                    iTextView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bubble_workout_charts));
                }
                // 測量 `TextView` 大小
                iTextView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
                textViewWidth = iTextView.getMeasuredWidth();
                textViewHeight = iTextView.getMeasuredHeight();
            } else {
                int dd;
                sTextView.setText(String.format("%s ", subZeroAndDot2(finalText)));
                if (isTreadmill) {
                    //###Speed
                    dd = R.drawable.element_popup_diagram_speed;
                } else {
                    //###Level
                    dd = R.drawable.element_popup_diagram_level;
                }
                sTextView.setBackground(ContextCompat.getDrawable(getContext(), dd));
                // 測量 `TextView` 大小
                sTextView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
                textViewWidth = sTextView.getMeasuredWidth();
                textViewHeight = sTextView.getMeasuredHeight();
            }

            // 獲取 `EgymDiagramBarsView` 在螢幕上的位置
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

//            int popupX = (int) (barsViewLocation[0] + whiteLineX - textViewWidth / 2);
            int popupX = (int) (barsViewLocation[0] + this.whiteLineX - textViewWidth / 2); //this.whiteLineX 當前白線所在位置
            int popupY = (int) barTopY;

            // 顯示 `PopupWindow`

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

                    // TODO 換行也要顯示
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

        }, 20); //20
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


    public EgymDiagramBarsView.Bar getBar(int barType, int barPosition) {
        EgymDiagramBarsView.Bar[] bars;
        if (barType == BAR_TYPE_INCLINE) { // incline
            bars = mBars;
        } else if (barType == BAR_TYPE_SPEED) { // level
            bars = mBars2;
        } else {
            bars = mBars3;
        }

        return bars[barPosition];
    }


}