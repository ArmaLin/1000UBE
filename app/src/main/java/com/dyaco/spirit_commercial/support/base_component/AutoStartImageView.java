package com.dyaco.spirit_commercial.support.base_component;

import android.content.Context;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

public class AutoStartImageView extends AppCompatImageView {

    public AutoStartImageView(Context context) {
        super(context);
    }

    public AutoStartImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoStartImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        // 當元件加入畫面後，自動檢查並啟動動畫
        Drawable drawable = getDrawable();
        if (drawable instanceof AnimatedVectorDrawable) {
            ((AnimatedVectorDrawable) drawable).start();
        }
    }
}
