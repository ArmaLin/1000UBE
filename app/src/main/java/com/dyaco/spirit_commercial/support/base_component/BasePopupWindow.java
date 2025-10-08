package com.dyaco.spirit_commercial.support.base_component;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.SET_ALPHA_BACKGROUND;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import android.view.WindowManager;
import android.widget.PopupWindow;

import androidx.viewbinding.ViewBinding;

import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.support.MsgEvent;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;
import com.dylanc.viewbinding.base.ViewBindingUtil;
import com.jeremyliao.liveeventbus.LiveEventBus;

public class BasePopupWindow<VB extends ViewBinding> extends PopupWindow {

    //AutoObserver
//    private final java.util.List<AutoObserver<?>> autoObservers = new java.util.ArrayList<>();

    private VB binding;

    int orientation;
    protected View parentView;
    protected int duration;
    protected Context mContext;

    private final boolean isDark;
    public boolean isClearDark;

    protected int viewX, viewY;

    // protected View v;


    /**
     * @param context       context
     * @param duration      動畫持續時間
     * @param height        window高度
     * @param width         window寬度
     * @param animateType   動畫類型
     * @param isTouchCancel 是否點擊視窗外面關閉視窗
     * @param isDark        視窗後面的背景色 是否變暗
     * @param isFloating    是否為 OVERLAY視窗
     */
    public BasePopupWindow(Context context, int duration, int height, int width, @GENERAL.animationType int animateType, boolean isTouchCancel, boolean isDark, boolean isFloating, boolean isClearDark) {

        this.orientation = animateType;
        this.duration = duration;
        this.mContext = context;
        this.isDark = isDark;
        this.isClearDark = isClearDark;

        //  LinearLayout viewGroup = (LinearLayout) context.findViewById(R.id.llSortChangePopup);
        //  LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //    v = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.popup_inbox, null);

        binding = ViewBindingUtil.inflateWithGeneric(this, LayoutInflater.from(context));
        setContentView(binding.getRoot());

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
        //  setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        //這個方法是設置是否允許PopupWindow超出屏幕邊界，默認的，彈窗超出屏幕邊界是要被剪裁掉。如果傳入false,將允許彈窗顯示實際的（正確無誤）位置。
        // setClippingEnabled(false);
        //android 10 沒設定 TYPE_APPLICATION _OVERLAY ，下面會有空隙
        if (isFloating) {
            setWindowLayoutType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        }
        //  setWindowLayoutType(2042);
        // } else {
        fullScreenImmersive(getContentView());
        //    }

        //SUMMARY的時候用
        if (isDark) LiveEventBus.get(SET_ALPHA_BACKGROUND).post(true);



        getBinding().getRoot().setOnTouchListener((view, motionEvent) -> {
            ((MainActivity)context).onUserInteraction();
            view.performClick();
            return false;
        });

    }


    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
        parentView = parent;

        if (isDark) setAlpha();


        switch (orientation) {
            case GENERAL.TRANSLATION_X:
                ObjectAnimator.ofFloat(getContentView(), "translationX", getWidth(), 0).setDuration(duration).start();
                break;
            case GENERAL.TRANSLATION_Y:
                ObjectAnimator.ofFloat(getContentView(), "translationY", getHeight(), 0).setDuration(duration).start();
                break;
            case GENERAL.FADE:
                ObjectAnimator.ofFloat(getContentView(), "alpha", 0f, 1f).setDuration(duration).start();
                break;
            case GENERAL.SCALE_X:
                ObjectAnimator.ofFloat(getContentView(), "scaleX", 0f, 1f).setDuration(duration).start();
                break;
            case GENERAL.SCALE_Y:
                ObjectAnimator.ofFloat(getContentView(), "scaleY", 0f, 1f).setDuration(duration).start();
                break;
            case GENERAL.NONE:
                break;
        }

        viewX = x;
        viewY = y;

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
    public int dp2px(final float dpValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    protected OnCustomDismissListener onCustomDismissListener;

    /**
     * 直接關閉PopupWindow，沒有動畫效果
     */
    public void superDismiss() {
        super.dismiss();
        if (onCustomDismissListener != null) {
            onCustomDismissListener.onDismiss();
        }
    }


    //AutoObserver
//    public <T> void addForeverObserver(String eventKey, Class<T> clazz, androidx.lifecycle.Observer<T> observer) {
//        com.jeremyliao.liveeventbus.LiveEventBus.get(eventKey, clazz).observeForever(observer);
//        autoObservers.add(new AutoObserver<>(eventKey, clazz, observer));
//    }


    @Override
    public void dismiss() {
        try {

            if (isClearDark) {
                if (parentView != null) {
                    ViewGroup parent = (ViewGroup) parentView.getRootView();
                    ViewGroupOverlay overlay = parent.getOverlay();
                    overlay.clear();
                }
            }

            animateOut(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    superDismiss();
                }
            });
            if (onCustomDismissListener != null) {
                onCustomDismissListener.onStartDismiss(returnValue);
            }
            binding = null;
            parentView = null;

            //AutoObserver
//            for (AutoObserver<?> o : autoObservers) {
//                o.unregister();
//            }
//            autoObservers.clear();

            if (isDark) LiveEventBus.get(SET_ALPHA_BACKGROUND).post(false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void animateOut(final Animator.AnimatorListener listener) {

        Animator.AnimatorListener animatorListenerAdapter = new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                listener.onAnimationEnd(animation);
                getContentView().animate().setListener(null);
            }
        };


        switch (orientation) {
            case GENERAL.TRANSLATION_X:
                getContentView().animate().translationX(getWidth()).setListener(animatorListenerAdapter).setDuration(duration).start();
                break;
            case GENERAL.TRANSLATION_Y:
                getContentView().animate().translationY(getHeight()).setListener(animatorListenerAdapter).setDuration(duration).start();
                break;
            case GENERAL.FADE:
                getContentView().animate().alpha(0.1f).setListener(animatorListenerAdapter).setDuration(duration).start();
                break;
            case GENERAL.SCALE_X:
                getContentView().animate().scaleX(0.1f).setListener(animatorListenerAdapter).setDuration(duration).start();
                break;
            case GENERAL.SCALE_Y:
                getContentView().animate().scaleY(0.1f).setListener(animatorListenerAdapter).setDuration(duration).start();
                break;
            case GENERAL.NONE:
                listener.onAnimationEnd(null);
                break;

        }



    }

    public void setOnCustomDismissListener(OnCustomDismissListener onCustomDismissListener) {
        this.onCustomDismissListener = onCustomDismissListener;
    }

    public interface OnCustomDismissListener {

        void onStartDismiss(MsgEvent value);

        void onDismiss();
    }

    protected MsgEvent returnValue;

    protected void returnValue(MsgEvent value) {
        returnValue = value;
    }

    public VB getBinding() {
        return binding;
    }


    protected void fullScreenImmersive(View view) {
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        view.setSystemUiVisibility(uiOptions);
    }





    //AutoObserver
//    private static class AutoObserver<T> {
//        private final String key;
//        private final Class<T> clazz;
//        private final androidx.lifecycle.Observer<T> observer;
//
//        AutoObserver(String key, Class<T> clazz, androidx.lifecycle.Observer<T> observer) {
//            this.key = key;
//            this.clazz = clazz;
//            this.observer = observer;
//        }
//
//        void unregister() {
//            com.jeremyliao.liveeventbus.LiveEventBus.get(key, clazz).removeObserver(observer);
//        }
//    }


}
