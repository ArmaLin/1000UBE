package com.dyaco.spirit_commercial.dashboard_training;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.FontRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.cheonjaeung.powerwheelpicker.android.WheelPicker;
import com.dyaco.spirit_commercial.R;

import timber.log.Timber;

public class TimeItemEffector extends WheelPicker.ItemEffector {

    private final WheelPicker wheelPicker;

    // 顏色 & 字型
    private final int selectedColor;
    private final int itemsColor;
    private final Typeface selectedTypeface;
    private final Typeface itemTypeface;

    // 文字縮放
    private final float baseTextSizePx;
    private final float textScaleFactor;

    // 間距
    private final float baseItemHeightPx;
    private final float[] cumulativeGapsPx; // 預先算好的間距總和 (Y軸位置)
    private static final int MAX_OFFSET = 10; // 預先計算 10 層

    /**
     * ✅ 全新的建構子 (Constructor) V5
     *
     * @param context            上下文
     * @param wheelPicker        WheelPicker 實例
     * @param selectedColorRes   選中時的 "顏色" 資源 ID
     * @param itemColorRes       未選中時的 "顏色" 資源 ID
     * @param selectedFontRes    選中時的 "字型" 資源 ID
     * @param itemFontRes      未選中時的 "字型" 資源 ID
     * @param baseTextSizeSP     "中心項目" 的文字大小 (用 SP，例如 24f)
     * @param textScaleFactor    "文字" 縮放因子 (例如 0.8f)
     * @param baseItemHeightDP   "單一項目" 的高度 (用 DP，例如 72f)
     * @param firstGapScale      ✅ (新) "首層間距" 倍率 (例如 1.2f, 代表 120%)
     * @param gapFalloffFactor   ✅ (新) "間距遞減率" (例如 0.8f, 代表後一層是前一層的 80%)
     */
    public TimeItemEffector(Context context, WheelPicker wheelPicker,
                            @ColorRes int selectedColorRes, @ColorRes int itemColorRes,
                            @FontRes int selectedFontRes, @FontRes int itemFontRes,
                            float baseTextSizeSP, float textScaleFactor,
                            float baseItemHeightDP, float firstGapScale, float gapFalloffFactor
    ) {
        this.wheelPicker = wheelPicker;

        // 1. 載入顏色
        this.selectedColor = ContextCompat.getColor(context, selectedColorRes);
        this.itemsColor = ContextCompat.getColor(context, itemColorRes);

        // 2. 載入字型
        Typeface bold, regular;
        try {
            bold = ResourcesCompat.getFont(context, selectedFontRes);
            regular = ResourcesCompat.getFont(context, itemFontRes);
        } catch (Resources.NotFoundException e) {
            Timber.e(e, "字型資源找不到，使用系統預設");
            bold = Typeface.DEFAULT_BOLD;
            regular = Typeface.DEFAULT;
        }
        this.selectedTypeface = bold;
        this.itemTypeface = regular;

        // 3. 處理 SP 和 DP 轉換
        Resources res = context.getResources();
        this.baseTextSizePx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, baseTextSizeSP, res.getDisplayMetrics()
        );
        this.baseItemHeightPx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, baseItemHeightDP, res.getDisplayMetrics()
        );
        this.textScaleFactor = textScaleFactor;

        // 4. ✅ V5 核心數學：計算 "凸透鏡" 間距
        // (這取代了 V4 的 Math.pow)
        this.cumulativeGapsPx = new float[MAX_OFFSET];
        this.cumulativeGapsPx[0] = 0; // 中心點 (Item 4) 的位置是 0

        // 計算 "首層" 間距 (Item 3, 5)
        float currentGapPx = this.baseItemHeightPx * firstGapScale; // 例如: 72dp * 1.2 = 86.4px
        float cumulativeSum = 0;

        for (int i = 1; i < MAX_OFFSET; i++) {
            // 1. 累加 "目前" 的間距
            cumulativeSum += currentGapPx;
            this.cumulativeGapsPx[i] = cumulativeSum; // Item (4-i) 的位置

            // 2. 為了 "下一層" (i+1)，計算 "下一個" 間距
            // 例如: 86.4px * 0.8 = 69.12px
            currentGapPx = currentGapPx * gapFalloffFactor;
        }
    }

    // (getWheelPickerHeight, applyEffectOnScrollStateChanged 保持不變)
    private float getWheelPickerHeight() {
        return (float) wheelPicker.getMeasuredHeight();
    }
    @Override
    public void applyEffectOnScrollStateChanged(@NonNull View view, int newState, int positionOffset, int centerOffset) {
        // 留空
    }

    /**
     * 所有的 Alpha, 顏色, 字型, Y軸位移 邏輯
     * (這整段函式 V4, V5 完全一樣，它只是 "消耗" cumulativeGapsPx)
     */
    @Override
    public void applyEffectOnScrolled(@NonNull View view, int delta, int positionOffset, int centerOffset) {

        TextView textView = view.findViewById(R.id.tv_wheel_item);
        if (textView == null) return;

        // V4 的正規化 (Normalize) 邏輯 (保持不變)
        RecyclerView.Adapter<?> adapter = wheelPicker.getAdapter();
        if (adapter == null) return;
        int itemCount = adapter.getItemCount();
        if (itemCount == 0) return;

        int normalizedOffset = positionOffset;
        int half = itemCount / 2;
        if (positionOffset > half) {
            normalizedOffset = positionOffset - itemCount; // (99 -> -1)
        } else if (positionOffset < -half) {
            normalizedOffset = positionOffset + itemCount; // (-99 -> 1)
        }

        float height = getWheelPickerHeight();
        if (height == 0) return;

        // 邏輯 1：透明度 (Alpha) (不變)
        float alpha = 1f - (float) Math.abs(centerOffset) / (height / 2f);
        view.setAlpha(alpha);

        int absOffset = Math.abs(normalizedOffset);
        if (absOffset >= MAX_OFFSET) absOffset = MAX_OFFSET - 1;

        // 邏輯 2：文字大小 (Text Size) (不變)
        float scale = (float) Math.pow(textScaleFactor, absOffset);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, baseTextSizePx * scale);

        // 邏輯 3：顏色 (Color) + 字型 (Typeface) (不變)
        if (normalizedOffset == 0) {
            textView.setTextColor(selectedColor);
            if (textView.getTypeface() != selectedTypeface) {
                textView.setTypeface(selectedTypeface);
            }
        } else {
            textView.setTextColor(itemsColor);
            if (textView.getTypeface() != itemTypeface) {
                textView.setTypeface(itemTypeface);
            }
        }

        // 邏輯 4：間距 (TranslationY) (不變)
        // (這段程式碼會自動使用 V5 算好的 "凸透鏡" 陣列)
        int sign = (int) Math.signum(normalizedOffset);
        float layoutY = normalizedOffset * baseItemHeightPx;
        float visualY = sign * cumulativeGapsPx[absOffset];
        float translationY = visualY - layoutY;
        view.setTranslationY(translationY);
    }

    @Override
    public void applyEffectOnItemSelected(@NonNull View view, int position) {
        view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK);
    }
}