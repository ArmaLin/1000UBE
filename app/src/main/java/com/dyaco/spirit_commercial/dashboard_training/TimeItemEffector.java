package com.dyaco.spirit_commercial.dashboard_training;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.util.Log;
import android.util.TypedValue; // ✅ 需要這個
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.FontRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.cheonjaeung.powerwheelpicker.android.WheelPicker;
import com.dyaco.spirit_commercial.R;

public class TimeItemEffector extends WheelPicker.ItemEffector {

    private final WheelPicker wheelPicker;

    private final int selectedColor;
    private final int itemsColor;
    private  Typeface selectedTypeface;
    private  Typeface itemTypeface;

    private final float baseTextSizePx;
    private final float textScaleFactor; // (例如 0.8f，代表縮小 20%)

    // ✅ V3 新G增：用於計算 "偽" 間距
    private final float baseItemHeightPx;
    private final float spacingScaleFactor; // (例如 0.8f，代表間距縮小 20%)
    private final float[] cumulativeGapsPx; // 預先算好的間距總和 (Y軸位置)
    private static final int MAX_OFFSET = 10; // 我們預先計算 10 層的距離

    /**
     *
     * @param context            上下文
     * @param wheelPicker        WheelPicker 實例
     * @param selectedColorRes   選中時的 "顏色" 資源 ID (例如 R.color.white)
     * @param itemColorRes       未選中時的 "顏色" 資源 ID (例如 R.color.gray)
     * @param selectedFontRes    選中時的 "字型" 資源 ID (例如 R.font.inter_bold)
     * @param itemFontRes        未選中時的 "字型" 資源 ID (例如 R.font.inter_regular)
     * @param baseTextSizeSP     "中心項目" 的文字大小 (用 SP，例如 24f)
     * @param textScaleFactor    "文字" 縮放因子 (例如 0.8f，代表每遠一層就縮小 20%)
     * @param baseItemHeightDP   "單一項目" 的高度 (用 DP，例如 72f)
     * @param spacingScaleFactor "間距" 縮放因子 (例如 0.8f，代表每遠一層，間距就縮小 20%)
     */
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
        try {
            this.selectedTypeface = ResourcesCompat.getFont(context, selectedFontRes);
            this.itemTypeface = ResourcesCompat.getFont(context, itemFontRes);
        } catch (Resources.NotFoundException e) {
            Log.e("TimeItemEffector", "字型資源找不到，使用系統預設", e);
            this.selectedTypeface = Typeface.DEFAULT_BOLD;
            this.itemTypeface = Typeface.DEFAULT;
        }

        // 3. ✅ 處理 SP 和 DP 轉換 (很重要)
        Resources res = context.getResources();
        this.baseTextSizePx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, baseTextSizeSP, res.getDisplayMetrics()
        );
        this.baseItemHeightPx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, baseItemHeightDP, res.getDisplayMetrics()
        );
        this.textScaleFactor = textScaleFactor;
        this.spacingScaleFactor = spacingScaleFactor;

        // 4. ✅ 預先計算 "視覺 Y 軸位置" (核心數學)
        // 根據你的需求 (間距 100%, 80%, 64%, ...)
        this.cumulativeGapsPx = new float[MAX_OFFSET];
        this.cumulativeGapsPx[0] = 0;
        float lastSum = 0;
        for (int i = 1; i < MAX_OFFSET; i++) {
            // 計算當前的間距 (G_1, G_1*0.8, G_1*0.8^2, ...)
            float currentGap = this.baseItemHeightPx * (float) Math.pow(this.spacingScaleFactor, i - 1);
            // 累加總和
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

        float height = getWheelPickerHeight();
        if (height == 0) return; // 避免除以零

        // ======================= 邏輯 1：透明度 (Alpha) =======================
        // (保持不變) 根據 "像素距離" (centerOffset) 來計算淡出效果
        float alpha = 1f - (float) Math.abs(centerOffset) / (height / 2f);
        view.setAlpha(alpha);

        // 獲取絕對偏移量 (0, 1, 2, ...)
        int absOffset = Math.abs(positionOffset);
        if (absOffset >= MAX_OFFSET) absOffset = MAX_OFFSET - 1; // 避免陣列溢位

        // ======================= 邏輯 2：文字大小 (Text Size) =======================
        // ✅ V3 新增
        // 計算縮放比例 (1.0, 0.8, 0.64, ...)
        float scale = (float) Math.pow(textScaleFactor, absOffset);
        // 設定文字大小 (用 PX)
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, baseTextSizePx * scale);


        // ======================= 邏輯 3：顏色 (Color) + 字型 (Typeface) =======================
        if (positionOffset == 0) {
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
        // ✅ V3 新增 (核心數學)

        // 1. 獲取方向 (中心 = 0, 上方 = -1, 下方 = 1)
        int sign = (int) Math.signum(positionOffset);

        // 2. 獲取 "標準" Y 軸位置 (LayoutManager 放置它的位置)
        // (例如: -144px, -72px, 0, 72px, 144px)
        float layoutY = positionOffset * baseItemHeightPx;

        // 3. 獲取計算出的 "視覺" Y 軸位置
        // (例如: -129.6px, -72px, 0, 72px, 129.6px)
        float visualY = sign * cumulativeGapsPx[absOffset];

        // 4. 計算差值，並設定 Y 軸位移
        // (例如: Item 2: (-129.6) - (-144) = +14.4px)
        // (這會把 Item 2 "往下拉 14.4px"，使其更靠近 Item 3)
        float translationY = visualY - layoutY;
        view.setTranslationY(translationY);
    }

    @Override
    public void applyEffectOnItemSelected(@NonNull View view, int position) {
        view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK);
    }
}