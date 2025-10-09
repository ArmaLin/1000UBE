package com.dyaco.spirit_commercial.support.base_component;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.view.ContextThemeWrapper;
import androidx.viewbinding.ViewBinding;

import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.support.MsgEvent;
import com.dylanc.viewbinding.base.ViewBindingUtil;

public abstract class BaseWindow<VB extends ViewBinding> {
    private VB binding;
    private final Context mContext;
    protected WindowManager mWindowManager;
    protected WindowManager.LayoutParams layoutParams;

    public BaseWindow(final Context context, int width, int height, int gravity,int x, int y) {

        mContext = context.getApplicationContext();
//        // 获取WindowManager
//        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
//        //  mView = setUpView(context);
//        final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
//        // 类型
//        params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
//
//        // 设置flag
//        int flags = WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
//        // | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
//        // 如果设置了WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE，弹出的View收不到Back键的事件
//        params.flags = flags;
//        // 不设置这个弹出框的透明遮罩显示为黑色
//        params.format = PixelFormat.TRANSLUCENT;
//        // FLAG_NOT_TOUCH_MODAL不阻塞事件传递到后面的窗口
//        // 设置 FLAG_NOT_FOCUSABLE 悬浮窗口较小时，后面的应用图标由不可长按变为可长按
//        // 不设置这个flag的话，home页的划屏会有问题



        ContextThemeWrapper ctx = new ContextThemeWrapper(mContext, R.style.TranslucentAppTheme);
        binding = ViewBindingUtil.inflateWithGeneric(this, LayoutInflater.from(ctx));

        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        layoutParams.format = PixelFormat.TRANSLUCENT; //讓背景透明
        layoutParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;

        layoutParams.flags =
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

        //😱Android 14以上 避免Window亂跳
        layoutParams.setCanPlayMoveAnimation(false);

        fullScreenImmersive(binding.getRoot());

        int w = width;

        if (width == 1) w = WRAP_CONTENT;

        layoutParams.width = w;
//        layoutParams.width = width;
        layoutParams.height = height;
        layoutParams.gravity = gravity;
        layoutParams.x = x;
        layoutParams.y = y;


        //   if (orientation == 0) {
     //   ObjectAnimator.ofFloat(binding.getRoot(), "translationX", layoutParams.width, 0).setDuration(500).start();
//        } else {
//            ObjectAnimator.ofFloat(binding.getRoot(), "translationY", layoutParams.height, 0).setDuration(duration).start();
//        }

        mWindowManager.addView(binding.getRoot(), layoutParams);


    }

    public void setVisible(boolean isShow) {
        binding.getRoot().setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);


        //Window type can not be changed after the window is added.
//        if (isShow) {
//            layoutParams.type = WindowManager.LayoutParams.TYPE_BASE_APPLICATION;
//        } else {
//            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
//        }
//        mWindowManager.updateViewLayout(binding.getRoot(),layoutParams);
    }

    public void setVisibleFade(boolean isShow) {
        if (isShow) {
            binding.getRoot().setAlpha(0f);
            binding.getRoot().setVisibility(View.VISIBLE);
            binding.getRoot().animate()
                    .alpha(1f)
                    .setDuration(500)
                    .setListener(null);
        } else {
            binding.getRoot().animate()
                    .alpha(0f)
                    .setDuration(300)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (binding != null) {
                                binding.getRoot().setVisibility(View.INVISIBLE);
                                binding.getRoot().setAlpha(1f);
                                binding.getRoot().animate().setListener(null);
                            } else {
                                Log.d("GGGGGGGG", "#####onAnimationEnd: binding null");
                            }
                        }
                    });
        }
    }


    public void setOnCustomDismissListener(OnCustomDismissListener onCustomDismissListener) {
        this.onCustomDismissListener = onCustomDismissListener;
    }
    protected OnCustomDismissListener onCustomDismissListener;
    public interface OnCustomDismissListener {
        void onDismiss(MsgEvent value);
    }

    public void dismiss() {

        try {
            if (onCustomDismissListener != null) {
                onCustomDismissListener.onDismiss(null);
            }
            if (binding != null) {
                mWindowManager.removeViewImmediate(binding.getRoot());
            }
            binding = null;
            layoutParams = null;
            mWindowManager = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int dp2px(final float dpValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
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
}
