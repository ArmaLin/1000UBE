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

    private final int selectedColor;
    private final int itemsColor;
    private final Typeface selectedTypeface;
    private final Typeface itemTypeface;

    private final float baseTextSizePx;
    private final float textScaleFactor;

    private final float baseItemHeightPx;
    private final float spacingScaleFactor;
    private final float[] cumulativeGapsPx;
    private static final int MAX_OFFSET = 10;

    public TimeItemEffector(Context context, WheelPicker wheelPicker,
                            @ColorRes int selectedColorRes, @ColorRes int itemColorRes,
                            @FontRes int selectedFontRes, @FontRes int itemFontRes,
                            float baseTextSizeSP, float textScaleFactor,
                            float baseItemHeightDP, float spacingScaleFactor
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

        Resources res = context.getResources();
        this.baseTextSizePx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, baseTextSizeSP, res.getDisplayMetrics()
        );
        this.baseItemHeightPx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, baseItemHeightDP, res.getDisplayMetrics()
        );
        this.textScaleFactor = textScaleFactor;
        this.spacingScaleFactor = spacingScaleFactor;

        // 4. 預先計算 "視覺 Y 軸位置"
        this.cumulativeGapsPx = new float[MAX_OFFSET];
        this.cumulativeGapsPx[0] = 0;
        float lastSum = 0;
        for (int i = 1; i < MAX_OFFSET; i++) {
            float currentGap = this.baseItemHeightPx * (float) Math.pow(this.spacingScaleFactor, i - 1);
            lastSum += currentGap;
            this.cumulativeGapsPx[i] = lastSum;
        }
    }

    private float getWheelPickerHeight() {
        return (float) wheelPicker.getMeasuredHeight();
    }

    @Override
    public void applyEffectOnScrollStateChanged(@NonNull View view, int newState, int positionOffset, int centerOffset) {
        // 留空
    }

    /**
     * 所有的 Alpha (透明度) 和顏色邏輯都在這裡處理
     */
    @Override
    public void applyEffectOnScrolled(@NonNull View view, int delta, int positionOffset, int centerOffset) {

        TextView textView = view.findViewById(R.id.tv_wheel_item);
        if (textView == null) return;

        // ✅ ================== V4 核心修正 ==================
        RecyclerView.Adapter<?> adapter = wheelPicker.getAdapter();
        if (adapter == null) return;
        int itemCount = adapter.getItemCount();
        if (itemCount == 0) return;

        int normalizedOffset = positionOffset;
        int half = itemCount / 2;

        // 如果 offset "跳躍" 了 (例如 99)
        if (positionOffset > half) {
            normalizedOffset = positionOffset - itemCount; // (例如: 99 - 100 = -1)
        }
        // 如果 offset 往另一個方向 "跳躍" (例如 -99)
        else if (positionOffset < -half) {
            normalizedOffset = positionOffset + itemCount; // (例如: -99 + 100 = 1)
        }
        // 現在 normalizedOffset 永遠是線性的 (..., -2, -1, 0, 1, 2, ...)
        // ✅ ===============================================

        float height = getWheelPickerHeight();
        if (height == 0) return; // 避免除以零

        // ======================= 邏輯 1：透明度 (Alpha) =======================
        float alpha = 1f - (float) Math.abs(centerOffset) / (height / 2f);
        view.setAlpha(alpha);

        // (使用 "正規化" 後的 offset)
        int absOffset = Math.abs(normalizedOffset);
        if (absOffset >= MAX_OFFSET) absOffset = MAX_OFFSET - 1;

        // ======================= 邏輯 2：文字大小 (Text Size) =======================
        float scale = (float) Math.pow(textScaleFactor, absOffset);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, baseTextSizePx * scale);

        // ======================= 邏輯 3：顏色 (Color) + 字型 (Typeface) =======================
        if (normalizedOffset == 0) {
            // 在中心
            textView.setTextColor(selectedColor);
            if (textView.getTypeface() != selectedTypeface) {
                textView.setTypeface(selectedTypeface);
            }
        } else {
            // 不在中心
            textView.setTextColor(itemsColor);
            if (textView.getTypeface() != itemTypeface) {
                textView.setTypeface(itemTypeface);
            }
        }

        // ======================= 邏輯 4：間距 (TranslationY) =======================

        int sign = (int) Math.signum(normalizedOffset);
        float layoutY = normalizedOffset * baseItemHeightPx; // (使用正規化 offset)
        float visualY = sign * cumulativeGapsPx[absOffset];  // (使用正規化 offset)
        float translationY = visualY - layoutY;
        view.setTranslationY(translationY);
    }

    @Override
    public void applyEffectOnItemSelected(@NonNull View view, int position) {
        view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK);
    }
}