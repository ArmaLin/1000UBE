package com.dyaco.spirit_commercial.support.base_component;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import android.widget.PopupWindow;

import com.dyaco.spirit_commercial.support.intdef.GENERAL;

public class BasePopupWindow2 extends PopupWindow {

    int orientation; //0x橫 1y直
    protected View parentView;
    private final int duration;
    protected Context mContext;

    private boolean isDark;


    public BasePopupWindow2(Context context, int duration, int height, int width, @GENERAL.animationType int orientation, boolean isTouchCancel, boolean isDark) {

        this.orientation = orientation;
        this.duration = duration;
        this.mContext = context;
        this.isDark = isDark;


        View baseView = ((Activity) context).findViewById(android.R.id.content);
        int w, h;

        if (width == 0) {
            w = baseView.getWidth();
        } else if (width == 1) {
            w = WRAP_CONTENT;
        } else {
            w = dp2px(width);
        }

        if (height == 0) {
            h = baseView.getHeight();
        } else if (width == 1) {
            h = WRAP_CONTENT;
        } else {
            h = dp2px(height);
        }
        setWidth(w);
        setHeight(h);

        setFocusable(false);
        setOutsideTouchable(isTouchCancel);

      //  setWindowLayoutType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        fullScreenImmersive(baseView);
        //  setWindowLayoutType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);

    }


    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
        parentView = parent;

        if (isDark) setAlpha();

        if (orientation == GENERAL.TRANSLATION_X) {
            ObjectAnimator.ofFloat(getContentView(), "translationX", getWidth(), 0).setDuration(duration).start();
        } else if (orientation == GENERAL.TRANSLATION_Y) {
            ObjectAnimator.ofFloat(getContentView(), "translationY", getHeight(), 0).setDuration(duration).start();
        } else {
            ObjectAnimator.ofFloat(getContentView(), "alpha", 0f, 1f).setDuration(duration).start();
        }
    }

    protected void setAlpha() {
        ViewGroup viewGroup = (ViewGroup) parentView.getRootView();
        Drawable dim = new ColorDrawable(Color.BLACK);
        dim.setBounds(0, 0, parentView.getWidth(), parentView.getHeight());
        dim.setAlpha(Math.round(255 * 0.6f));
        ViewGroupOverlay overlay = viewGroup.getOverlay();
        overlay.add(dim);
    }

    /**
     * Value of dp to value of px.
     *
     * @param dpValue The value of dp.
     * @return value of px
     */
    public static int dp2px(final float dpValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    private OnCustomDismissListener onCustomDismissListener;

    /**
     * 直接關閉PopupWindow，沒有動畫效果
     */
    public void superDismiss() {
        super.dismiss();
        if (onCustomDismissListener != null) {
            onCustomDismissListener.onDismiss();
        }
    }

    @Override
    public void dismiss() {
        try {
            ViewGroup parent = (ViewGroup) parentView.getRootView();
            ViewGroupOverlay overlay = parent.getOverlay();
            overlay.clear();
            animateOut(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    superDismiss();
                }
            });
            if (onCustomDismissListener != null) {
                onCustomDismissListener.onStartDismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void animateOut(final Animator.AnimatorListener listener) {

        if (orientation == GENERAL.TRANSLATION_X) {
            getContentView().animate().translationX(getWidth()).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    listener.onAnimationEnd(animation);
                    getContentView().animate().setListener(null);
                }
            }).setDuration(duration).start();
        } else if (orientation == GENERAL.TRANSLATION_Y) {
            getContentView().animate().translationY(getHeight()).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    listener.onAnimationEnd(animation);
                    getContentView().animate().setListener(null);
                }
            }).setDuration(duration).start();
        } else {
            getContentView().animate().alpha(0.1f).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    listener.onAnimationEnd(animation);
                    getContentView().animate().setListener(null);
                }
            }).setDuration(duration).start();
        }

    }

    public void setOnCustomDismissListener(OnCustomDismissListener onCustomDismissListener) {
        this.onCustomDismissListener = onCustomDismissListener;
    }

    public interface OnCustomDismissListener {

        void onStartDismiss();

        void onDismiss();
    }

    private void fullScreenImmersive(View view) {
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        view.setSystemUiVisibility(uiOptions);
    }

}
