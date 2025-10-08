package com.dyaco.spirit_commercial.maintenance_mode;


import static com.dyaco.spirit_commercial.App.getApp;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.MAINTENANCE_BACK;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.MAINTENANCE_OPEN_TV_MENU;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.TV_TUNER_CLOSE_CHANNEL_LIST;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.TV_TUNER_HIDE_CHANNEL_LIST;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.core.content.ContextCompat;

import com.corestar.libs.device.DeviceTvTuner;
import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.alert_message.BackgroundWindow;
import com.dyaco.spirit_commercial.dashboard_media.TvControllerWindow;
import com.dyaco.spirit_commercial.databinding.WindowMaintenanceTvMenuBinding;
import com.dyaco.spirit_commercial.support.CommonUtils;
import com.dyaco.spirit_commercial.support.MsgEvent;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;
import com.dyaco.spirit_commercial.support.utils.CheckDoubleClick;
import com.jeremyliao.liveeventbus.LiveEventBus;

/**
 * TvMenu
 */
public class MaintenanceTvMenuWindow extends BasePopupWindow<WindowMaintenanceTvMenuBinding> {
    MainActivity activity;
    int maxWidth = 421;
    int minWidth = 136;

    int viewHeight = 408;
    boolean isDashboardOpen = true;
    boolean isExpand = false;

    public MaintenanceTvMenuWindow(Context context) {
        super(context, 0, 408, 421, GENERAL.TRANSLATION_Y, false, false, true, true);
        activity = (MainActivity) context;
        initView();
    }


    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);

    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {

        //返回
        getBinding().btnMaintenanceMode.setOnClickListener(v -> {
            if (CheckDoubleClick.isFastClick()) return;
        //    showChannel(false);
            LiveEventBus.get(TV_TUNER_CLOSE_CHANNEL_LIST).post(false);
            LiveEventBus.get(MAINTENANCE_BACK).post(true);
            dismiss();
        });

        getBinding().btnHideChannels.setOnClickListener(v -> {
            if (CheckDoubleClick.isFastClick()) return;

            isDashboardOpen = !isDashboardOpen;


            LiveEventBus.get(TV_TUNER_HIDE_CHANNEL_LIST).post(isDashboardOpen);
          //  maintenanceTvChannelsWindow.hideB(isDashboardOpen);

            getBinding().btnHideChannels.setCompoundDrawablesWithIntrinsicBounds(isDashboardOpen ? ContextCompat.getDrawable(mContext, R.drawable.icon_screen_off) : ContextCompat.getDrawable(mContext, R.drawable.icon_screen_on), null, null, null);
            getBinding().btnHideChannels.setText(isExpand ? "" : (isDashboardOpen ? mContext.getString(R.string.Show_Channel) : mContext.getString(R.string.hide_channels)));
        });

        getBinding().btnExpand.setOnClickListener(v -> {
            if (CheckDoubleClick.isFastClick()) return;
            expandMenu(isExpand);
            isExpand = !isExpand;
        });

        getBinding().btnTvController.setOnClickListener(v -> {

            if (CheckDoubleClick.isFastClick()) return;

            BackgroundWindow backgroundWindow = new BackgroundWindow(mContext);
            backgroundWindow.showAtLocation(((MainActivity) mContext).getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);

            TvControllerWindow popupWindow = new TvControllerWindow(mContext,true);
            popupWindow.showAtLocation(((MainActivity) mContext).getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);

            popupWindow.setOnCustomDismissListener(new OnCustomDismissListener() {
                @Override
                public void onStartDismiss(MsgEvent value) {
                    backgroundWindow.dismiss();
                }

                @Override
                public void onDismiss() {

                }
            });
        });

        getBinding().btnScanAgain.setOnClickListener(v -> {
            if (CheckDoubleClick.isFastClick()) return;

            if (activity.deviceTvTuner != null) {


                //日本才要
                if (getApp().getDeviceSettingBean().getTvCountry() == DeviceTvTuner.TV_COUNTRY.Japan) {
                    activity.deviceTvTuner.channelScanExtensionEnableDisableDTVScramblingChannel(true);
                    activity.deviceTvTuner.setMultiVoiceSystem(DeviceTvTuner.VOICE_TYPE.MAIN_VOICE);
                }

                activity.deviceTvTuner.startScan();
            }

            LiveEventBus.get(MAINTENANCE_OPEN_TV_MENU).post(true);
        });

        getBinding().btnDragMenu.setOnTouchListener(touchListener);
        getBinding().btnDragMenu2.setOnTouchListener(touchListener);

        CommonUtils.expandViewTouchDelegate(getBinding().btnDragMenu,0,0,0,130);

    }


    View.OnTouchListener touchListener = new View.OnTouchListener() {
        private int x;
        private int y;

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x = (int) event.getRawX();
                    y = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int nowX = (int) event.getRawX();
                    int nowY = (int) event.getRawY();
                    int movedX = nowX - x;
                    int movedY = nowY - y;
                    x = nowX;
                    y = nowY;
                    viewX = viewX + movedX;
                    viewY = viewY + movedY;
                    update(viewX, viewY, -1, -1, true);
                    break;
                case MotionEvent.ACTION_UP:
                    getBinding().btnDragMenu.performClick();
                    break;
            }
            return true;
        }
    };

    public void expandMenu(Boolean isExpand) {

        ValueAnimator valueAnimator = ValueAnimator.ofInt(getWidth(), isExpand ? maxWidth : minWidth);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setDuration(100);
        valueAnimator.addUpdateListener(animation ->
                update((int) animation.getAnimatedValue(), viewHeight));
        valueAnimator.start();

        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                getBinding().btnMaintenanceMode.setText(isExpand ? mContext.getString(R.string.to_maintenance_mode) : "");
                getBinding().btnScanAgain.setText(isExpand ? mContext.getString(R.string.scan_again) : "");
                getBinding().btnTvController.setText(isExpand ? mContext.getString(R.string.TV_Controller) : "");
                getBinding().btnHideChannels.setText(isExpand ? mContext.getString(R.string.hide_channels) : "");
                getBinding().iconMenuArrow.setBackgroundResource(isExpand ? R.drawable.icon_fold : R.drawable.icon_extend);
            }
        });
    }

    @Override
    public void dismiss() {
        super.dismiss();
        LiveEventBus.get(TV_TUNER_CLOSE_CHANNEL_LIST).post(false);
      //  showChannel(false);
        activity = null;
    }


//    /**
//     * 右邊list Channel選單
//     */
//    MaintenanceTvChannelsWindow maintenanceTvChannelsWindow;
//    private void showChannel(boolean isShow) {
//
//        if (maintenanceTvChannelsWindow != null) {
//            maintenanceTvChannelsWindow.dismiss();
//            maintenanceTvChannelsWindow = null;
//        }
//
//        if (isShow) {
//
//            maintenanceTvChannelsWindow = new MaintenanceTvChannelsWindow(mContext);
//            maintenanceTvChannelsWindow.showAtLocation(activity.getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);
//            maintenanceTvChannelsWindow.setOnCustomDismissListener(new BasePopupWindow.OnCustomDismissListener() {
//                @Override
//                public void onStartDismiss(MsgEvent value) {
//
//                }
//
//                @Override
//                public void onDismiss() {
//                    maintenanceTvChannelsWindow = null;
//                }
//            });
//        }
//    }

}
