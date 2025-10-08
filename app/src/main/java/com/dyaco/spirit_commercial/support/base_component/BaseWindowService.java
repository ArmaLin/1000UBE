package com.dyaco.spirit_commercial.support.base_component;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.viewbinding.ViewBinding;

import com.dyaco.spirit_commercial.R;
import com.dylanc.viewbinding.base.ViewBindingUtil;

public class BaseWindowService<VB extends ViewBinding> extends Service {
    private VB binding;

    int orientation; //0x橫 1y直
    protected View parentView;
    private int duration;
    protected WindowManager windowManager;
    protected WindowManager.LayoutParams layoutParams;
    WindowManager.LayoutParams setting;

    public void setArg(int duration, int orientation, WindowManager.LayoutParams setting) {
        this.orientation = orientation;
        this.duration = duration;
        this.setting = setting;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("@@#@#@#@#@#", "onBind: ");
        return null;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        Log.d("@@#@#@#@#@#", "attachBaseContext: ");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    private void initView() {
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        layoutParams.format = PixelFormat.TRANSLUCENT;
        layoutParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;


        layoutParams.width = setting.width;
        layoutParams.height = setting.height;
        layoutParams.x = setting.x;
        layoutParams.y = setting.y;
        layoutParams.gravity = setting.gravity;

        ContextThemeWrapper ctx = new ContextThemeWrapper(this, R.style.TranslucentAppTheme);
        binding = ViewBindingUtil.inflateWithGeneric(this, LayoutInflater.from(ctx));

        if (orientation == 0) {
            ObjectAnimator.ofFloat(binding.getRoot(), "translationX", layoutParams.width, 0).setDuration(duration).start();
        } else {
            ObjectAnimator.ofFloat(binding.getRoot(), "translationY", layoutParams.height, 0).setDuration(duration).start();
        }

        binding.getRoot().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);


        windowManager.addView(binding.getRoot(), layoutParams);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("@@#@#@#@#@#", "onCreate: ");
        initView();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        removeView();
    }

    private void animateOut(final Animator.AnimatorListener listener) {

        if (orientation == 0) {
            binding.getRoot().animate().translationX(layoutParams.width).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    listener.onAnimationEnd(animation);
                    if (binding != null) binding.getRoot().animate().setListener(null);
                }
            }).setDuration(duration).start();
        } else {
            binding.getRoot().animate().translationY(layoutParams.height).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    listener.onAnimationEnd(animation);
                    if (binding != null) binding.getRoot().animate().setListener(null);
                }
            }).setDuration(duration).start();
        }
    }

    protected void dismiss() {
        animateOut(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Log.d("OOOOOOO", "onAnimationEnd: ");
                stopSelf();
            }
        });
    }

    private void removeView() {
        windowManager.removeView(binding.getRoot());
        binding = null;
        layoutParams = null;
        windowManager = null;
    }


    public VB getBinding() {
        return binding;
    }
}