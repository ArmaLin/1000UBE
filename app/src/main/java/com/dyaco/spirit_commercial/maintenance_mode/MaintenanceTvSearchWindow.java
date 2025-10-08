package com.dyaco.spirit_commercial.maintenance_mode;

import static com.dyaco.spirit_commercial.App.getApp;
import static com.dyaco.spirit_commercial.MainActivity.TV_TUNER_VOLUME;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.MAINTENANCE_BACK;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.MAINTENANCE_OPEN_TV_MENU;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.TV_TUNER_CLOSE_CHANNEL_LIST;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.TV_TUNER_HIDE_CHANNEL_LIST;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.TV_TUNER_SCAN_DONE;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.PORT_TV_TUNER;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;

import androidx.lifecycle.Observer;

import com.corestar.libs.device.DeviceTvTuner;
import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.databinding.FragmentMaintenanceTvSearchBinding;
import com.dyaco.spirit_commercial.support.MsgEvent;
import com.dyaco.spirit_commercial.support.RxTimer;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;
import com.dyaco.spirit_commercial.support.utils.CheckDoubleClick;
import com.fec.hdmiin.HdmiIn;
import com.jeremyliao.liveeventbus.LiveEventBus;

import es.dmoral.toasty.Toasty;

/**
 * 顯示影像主視窗 SCAN
 */
public class MaintenanceTvSearchWindow extends BasePopupWindow<FragmentMaintenanceTvSearchBinding> {
    private final Context mContext;

    public PopupWindow popupWindow;
    private final DeviceTvTuner deviceTvTuner;

    private RxTimer scanCheckTimer;

    public MaintenanceTvSearchWindow(Context context) {
        super(context, 500, 0, 0, GENERAL.FADE, false, false, false, false);
        mContext = context;
        deviceTvTuner = ((MainActivity) mContext).deviceTvTuner;

        getBinding().btnDone.setOnClickListener(view -> {
            //#TV TUNER 停止掃描
            ((MainActivity) mContext).deviceTvTuner.stopScan();
            dismiss();
        });

        getBinding().btnStartScan.setOnClickListener(view -> {

            if (CheckDoubleClick.isFastClick()) return;

            ((MainActivity) mContext).showLoading(true);

            try {
                new RxTimer().timer(10000, number ->
                        ((MainActivity) mContext).showLoading(false));
            } catch (Exception e) {
                e.printStackTrace();
            }

            getBinding().btnStartScan.setVisibility(View.GONE);
            getBinding().btnMenu.setVisibility(View.VISIBLE);

            new RxTimer().timer(500, number -> {
                //#TV TUNER 開始掃描
                try {

                    //日本才要
                    if (getApp().getDeviceSettingBean().getTvCountry() == DeviceTvTuner.TV_COUNTRY.Japan) {
                        deviceTvTuner.channelScanExtensionEnableDisableDTVScramblingChannel(true);
                        deviceTvTuner.setMultiVoiceSystem(DeviceTvTuner.VOICE_TYPE.MAIN_VOICE);
                    }

                    deviceTvTuner.startScan();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            cancelScanCheck();
            scanCheckTimer = new RxTimer();
            scanCheckTimer.interval(2000, number -> {
                //#TV TUNER 取得掃描狀態，(true掃描中, false掃描完成)
                deviceTvTuner.getScanStatus(); // > onGetScanStatus
            });

            new RxTimer().timer(1000, number -> {
                initHdmi();
            });

        });

        //停止掃描，開啟頻道
        getBinding().btnMenu.setOnClickListener(view -> {

            deviceTvTuner.stopScan();

            cancelScanCheck();

            getBinding().vTop.setVisibility(View.GONE);


            showChannel(true);

            showMaintenanceTvMenu(true);

            deviceTvTuner.setVolume(TV_TUNER_VOLUME);

        });

        // LiveEventBus.get(GET_TV_CHANNEL_LIST, Channel[].class).observeForever(observer);

        LiveEventBus.get(MAINTENANCE_OPEN_TV_MENU, Boolean.class).observeForever(observer2);

        LiveEventBus.get(TV_TUNER_SCAN_DONE, Boolean.class).observeForever(observer3);

        LiveEventBus.get(MAINTENANCE_BACK, Boolean.class).observeForever(observer4);

        LiveEventBus.get(TV_TUNER_CLOSE_CHANNEL_LIST, Boolean.class).observeForever(observer5);
        LiveEventBus.get(TV_TUNER_HIDE_CHANNEL_LIST, Boolean.class).observeForever(observer6);


        //   Looper.myQueue().addIdleHandler(() -> {
        //     initHdmi();
//            return false;
//        });
    }

    Observer<Boolean> observer4 = b ->
            getBinding().btnDone.callOnClick();

    //SCAN 結束 onGetScanStatus
    Observer<Boolean> observer3 = isOpen ->
            getBinding().btnMenu.callOnClick();


    //SCAN AGAIN
    Observer<Boolean> observer2 = isOpen -> {
        showMaintenanceTvMenu(false);
        getBinding().vTop.setVisibility(View.VISIBLE);

        getBinding().btnStartScan.callOnClick();
    };


    Observer<Boolean> observer5 = isOpen -> showChannel(false);

    Observer<Boolean> observer6 = this::showHideChannel;

    private void showHideChannel(boolean isShow) {
        maintenanceTvChannelsWindow.hideB(isShow);
    }


    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);

        //等設定國家完成
        try {
            ((MainActivity) mContext).showLoading(true);
            new RxTimer().timer(1000, number ->
                    ((MainActivity) mContext).showLoading(false));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();

        cancelScanCheck();

        if (deviceTvTuner != null) {
            deviceTvTuner.stopScan();
        }

        closeHdmi();

        //    LiveEventBus.get(GET_TV_CHANNEL_LIST, Channel[].class).removeObserver(observer);
        LiveEventBus.get(MAINTENANCE_OPEN_TV_MENU, Boolean.class).removeObserver(observer2);
        LiveEventBus.get(TV_TUNER_SCAN_DONE, Boolean.class).removeObserver(observer3);

        LiveEventBus.get(MAINTENANCE_BACK, Boolean.class).removeObserver(observer4);

        LiveEventBus.get(TV_TUNER_CLOSE_CHANNEL_LIST, Boolean.class).removeObserver(observer5);

        LiveEventBus.get(TV_TUNER_HIDE_CHANNEL_LIST, Boolean.class).removeObserver(observer6);
    }


    MaintenanceTvMenuWindow maintenanceTvMenuWindow;

    public void showMaintenanceTvMenu(boolean isShow) {

        if (isShow) {
            if (maintenanceTvMenuWindow != null && maintenanceTvMenuWindow.isShowing()) return;
            maintenanceTvMenuWindow = new MaintenanceTvMenuWindow(mContext);
            maintenanceTvMenuWindow.showAtLocation(((MainActivity) mContext).getWindow().getDecorView(), Gravity.START | Gravity.TOP, 50, 80);
            maintenanceTvMenuWindow.setOnCustomDismissListener(new BasePopupWindow.OnCustomDismissListener() {
                @Override
                public void onStartDismiss(MsgEvent value) {
                }

                @Override
                public void onDismiss() {
                    maintenanceTvMenuWindow = null;
                }
            });

        } else {
            if (maintenanceTvMenuWindow != null) {
                maintenanceTvMenuWindow.dismiss();
            }
        }
    }


    /**
     * 右邊list Channel選單
     */
    MaintenanceTvChannelsWindow maintenanceTvChannelsWindow;

    private void showChannel(boolean isShow) {

        if (maintenanceTvChannelsWindow != null) {
            maintenanceTvChannelsWindow.dismiss();
            maintenanceTvChannelsWindow = null;
        }

        if (isShow) {

            maintenanceTvChannelsWindow = new MaintenanceTvChannelsWindow(mContext);
            maintenanceTvChannelsWindow.showAtLocation(((MainActivity) mContext).getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);
            maintenanceTvChannelsWindow.setOnCustomDismissListener(new BasePopupWindow.OnCustomDismissListener() {
                @Override
                public void onStartDismiss(MsgEvent value) {

                }

                @Override
                public void onDismiss() {
                    maintenanceTvChannelsWindow = null;
                }
            });
        }
    }


    //初始化時會卡
    private boolean isOpen = false;

    public void initHdmi() {
        if (isOpen) return;
        try {
            isOpen = true;
            MainActivity.isReAssignView = true;
            HdmiIn hdmiIn = ((MainActivity) mContext).hdmiIn;
            //如果videoFullViewContainer2 一開始為 INVISIBLE ， AssignView不會往下跑
            hdmiIn.AssignView(getBinding().videoFullViewContainer2);

            getBinding().videoFullViewContainer2.setVisibility(View.INVISIBLE);

            new RxTimer().timer(3000, number -> {
                if (hdmiIn.SwitchHdmiSource(PORT_TV_TUNER)) {
                    hdmiIn.resumeVideo();
                    new RxTimer().timer(4000, x -> {
                        if (getBinding() != null) {
                            ((MainActivity) mContext).showLoading(false);
                            getBinding().videoFullViewContainer2.setVisibility(View.VISIBLE);
                        }
                    });
                } else {
                    Toasty.warning(mContext, "Please Try Again.", Toasty.LENGTH_SHORT).show();
                }

            });


        } catch (Exception e) {
            Log.d("VVVCCCVVV", "initHdmi: " + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    private void cancelScanCheck() {
        if (scanCheckTimer != null) {
            scanCheckTimer.cancel();
            scanCheckTimer = null;
        }
    }


    public void closeHdmi() {

        if (((MainActivity) mContext).hdmiIn != null) {
            // Session has been closed; further changes are illegal.
            try {
                ((MainActivity) mContext).hdmiIn.pauseVideo();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        if (getBinding() != null) {
            try {
                getBinding().videoFullViewContainer2.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
