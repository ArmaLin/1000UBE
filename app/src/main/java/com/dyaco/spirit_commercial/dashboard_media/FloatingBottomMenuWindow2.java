package com.dyaco.spirit_commercial.dashboard_media;


import static com.dyaco.spirit_commercial.support.intdef.EventKey.MEDIA_GO_TO_TRAINING;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.MEDIA_MENU_GO_TO_MEDIA_2;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.WindowFloatingBottomMenuBinding;
import com.dyaco.spirit_commercial.support.base_component.BaseWindow;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingViewModel;
import com.jeremyliao.liveeventbus.LiveEventBus;


//attachBaseContext 不能影響到這個
public class FloatingBottomMenuWindow2 extends BaseWindow<WindowFloatingBottomMenuBinding> {

    DeviceSettingViewModel deviceSettingViewModel;
    public FloatingBottomMenuWindow2(Context context, DeviceSettingViewModel deviceSettingViewModel, boolean isEnable) {
        super(context, WindowManager.LayoutParams.MATCH_PARENT,136, Gravity.BOTTOM,0,0);

        this.deviceSettingViewModel = deviceSettingViewModel;
        getBinding().setDeviceSetting(deviceSettingViewModel);

        //attachBaseContext 不能影響到這個  >  用setText(getString(R.string.Training)) 語言會變, 用setText(R.string.Training) 語言不會變
        getBinding().mbTraining.setText(context.getString(R.string.Training));
        getBinding().mbMedia.setText(context.getString(R.string.Media));

        if (isEnable) {
            initView();
        }
    }

    public void enableView() {
        Log.d("@@@@@@@@", "enableView: ");
        initView();
    }

    private void initView() {
        getBinding().mbTraining.setOnClickListener(v -> {
            LiveEventBus.get(MEDIA_GO_TO_TRAINING).post("");
        });

        getBinding().mbMedia.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                if (getBinding() != null) {
                    getBinding().mbMedia.setAlpha(0.7f);
                }
                return true;
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                view.performClick();
                return false;
            }else if (motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
                if (getBinding() != null) {
                    getBinding().mbMedia.setAlpha(1f);
                }
                return false;
            } else {
                return false;
            }
        });

        getBinding().mbTraining.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                if (getBinding() != null) {
                    getBinding().mbTraining.setAlpha(0.7f);
                }
                return true;
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                view.performClick();
                return false;
            }else if (motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
                if (getBinding() != null) {
                    getBinding().mbTraining.setAlpha(1f);
                }
                return false;
            } else {
                return false;
            }
        });

        getBinding().mbMedia.setOnClickListener(v -> {

            MainActivity.lastMedia = null;

            LiveEventBus.get(MEDIA_MENU_GO_TO_MEDIA_2).post(true);
        });

        //  LiveEventBus.get(ALPHA_FLOATING_MEDIA_BACKGROUND, Boolean.class).observeForever(observer);
    }
}
