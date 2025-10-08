// BottomShadowView.java
package com.dyaco.spirit_commercial.support.custom_view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class BottomShadowView extends View {
    private Paint paint;
    // 模糊半徑（dp）
    private static final float BLUR_DP = 16f;
    // 陰影顏色（你可以改成 #80000000、#40000000 看深淺）
    private static final int SHADOW_COLOR = Color.parseColor("#80000000");

    public BottomShadowView(Context context) {
        super(context);
        init();
    }

    public BottomShadowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BottomShadowView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        // 關閉硬體加速，才能讓 setShadowLayer 生效
        setLayerType(LAYER_TYPE_SOFTWARE, null);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        float density = getResources().getDisplayMetrics().density;
        float blurPx = BLUR_DP * density;

        // 在「頂部」畫一條厚度＝View 高度的線，並產生模糊
        paint.setStyle(Paint.Style.STROKE);
        // strokeWidth 先不設定，待 View 測量完在 onSizeChanged 裡設定
        paint.setShadowLayer(blurPx, 0, 0, SHADOW_COLOR);
        // 線本身顏色不重要（完全透明），只要有 shadowLayer
        paint.setColor(Color.TRANSPARENT);

        // 確保 onDraw 一定會被呼叫
        setWillNotDraw(false);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // strokeWidth 設為整個 View 高度，讓模糊往下半透明地分布
        paint.setStrokeWidth(h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 從 (0,0) 到 (width,0) 畫一條「厚度＝h」的線
        canvas.drawLine(0, 0, getWidth(), 0, paint);
    }
}
