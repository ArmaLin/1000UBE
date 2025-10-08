package com.dyaco.spirit_commercial.garmin;

import static com.dyaco.spirit_commercial.App.getDeviceGEM;
import static com.dyaco.spirit_commercial.MainActivity.currentGarminAddress;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.GARMIN_ON_DEVICE_CONNECTED;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.GARMIN_ON_DEVICE_DISCONNECTED;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.GARMIN_ON_DEVICE_PAIRED;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.GARMIN_ON_DEVICE_SETUP_COMPLETE;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.GARMIN_ON_DEVICE_UNPAIRED;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.corestar.libs.hrms.HeartRateDeviceManager;
import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.WindowGarminDevicesBinding;
import com.dyaco.spirit_commercial.support.MsgEvent;
import com.dyaco.spirit_commercial.support.RxTimer;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.custom_view.CustomToast;
import com.dyaco.spirit_commercial.support.intdef.AppStatusIntDef;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;
import com.dyaco.spirit_commercial.support.utils.CheckDoubleClick;
import com.dyaco.spirit_commercial.viewmodel.AppStatusViewModel;
import com.dyaco.spirit_commercial.viewmodel.WorkoutViewModel;
import com.garmin.health.ConnectionState;
import com.garmin.health.Device;
import com.garmin.health.DeviceManager;
import com.jeremyliao.liveeventbus.LiveEventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 1. Garmin 首頁 : 可選擇 解除配對, 新增裝置, 選擇資料
 **/
public class GarminDevicesWindow extends BasePopupWindow<WindowGarminDevicesBinding> {

    public static boolean isGarminWindowOn;

    public static final String GARMIN_TAG = "GARMINNN";

    private final DeviceManager garminDeviceManager;

    private final MainActivity mainActivity;
    private final WorkoutViewModel w;
    private final AppStatusViewModel appStatusViewModel;
    private final boolean isWorkout;

    public GarminDevicesWindow(Context context, WorkoutViewModel workoutViewModel, AppStatusViewModel appStatusViewModel) {
        super(context, 500, 0, 795, GENERAL.TRANSLATION_X, false, true, false, true);

        isGarminWindowOn = true;

        mainActivity = (MainActivity) mContext;
        this.appStatusViewModel = appStatusViewModel;
        this.w = workoutViewModel;

        isWorkout = appStatusViewModel.currentStatus.get() == AppStatusIntDef.STATUS_RUNNING || appStatusViewModel.currentStatus.get() == AppStatusIntDef.STATUS_PAUSE;

        if (isWorkout) {
            setWindowLayoutType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        }

        garminDeviceManager = ((MainActivity) context).mGarminDeviceManager;

        if (garminDeviceManager == null) {
            CustomToast.showToast(((MainActivity) context), "GARMIN NOT WORKING");
            dismiss();
        }

        initView();

        initGarminDevices();

//        LiveEventBus.get(ON_CUSTOM_EVENT_HEART_RATE_DEVICES_DISCOVERED, Integer.class).observeForever(observer);
//        LiveEventBus.get(DISCOVERY_TIMEOUT, Boolean.class).observeForever(observer2);

        getPairedDevice();

        initEventBus();

    }

    private void initEventBus() {
        LiveEventBus.get(GARMIN_ON_DEVICE_DISCONNECTED, Device.class).observeForever(observerDeviceDisconnectedObserver);
        LiveEventBus.get(GARMIN_ON_DEVICE_UNPAIRED, String.class).observeForever(observerDeviceUnpairedObserver);
        LiveEventBus.get(GARMIN_ON_DEVICE_CONNECTED, Device.class).observeForever(observerDeviceConnectedObserver);
        LiveEventBus.get(GARMIN_ON_DEVICE_PAIRED, Device.class).observeForever(observerGarminOnDevicePairedObserver);
        LiveEventBus.get(GARMIN_ON_DEVICE_SETUP_COMPLETE, Device.class).observeForever(observerGarminOnDeviceSetupCompleteObserver);
    }

    //裝置設置完成
    Observer<Device> observerGarminOnDeviceSetupCompleteObserver = this::onDeviceSetupComplete;

    private void onDeviceSetupComplete(Device device) {
        Log.d("GARMINNN", "onDeviceSetupComplete: " + device.address());
        HeartRateDeviceManager.setHrsDevice(device.address());
        mainActivity.runOnUiThread(() -> {
            getPairedDevice();
            hideProgress(false);

            if (devicePairedTimer != null) {
                devicePairedTimer.cancel();
                devicePairedTimer = null;
            }
        });
    }

    //配對完成
    RxTimer devicePairedTimer;
    Observer<Device> observerGarminOnDevicePairedObserver = this::onDevicePaired;

    private void onDevicePaired(Device device) {
        Log.d("GARMINNN", "onDevicePaired: " + device.address());
        mainActivity.runOnUiThread(() -> {
            if (devicePairedTimer != null) {
                devicePairedTimer.cancel();
                devicePairedTimer = null;
            }
            //   getPairedDevice();
            devicePairedTimer = new RxTimer();
            devicePairedTimer.timer(10000, number -> {
                hideProgress(false);
                if (devicePairedTimer != null) {
                    devicePairedTimer.cancel();
                    devicePairedTimer = null;
                }
            });
            hideProgress(true);

        });
    }

    //設備連線
    Observer<Device> observerDeviceConnectedObserver = this::onDeviceConnected;

    private void onDeviceConnected(Device device) {
        Log.d("GARMINNN", "onDeviceConnected: " + device.address());
        mainActivity.runOnUiThread(this::getPairedDevice);
    }

    //設備斷線
    Observer<Device> observerDeviceDisconnectedObserver = this::onDeviceDisconnected;

    private void onDeviceDisconnected(Device device) {
        Log.d("GARMINNN", "onDeviceDisconnected: " + device.address());
        mainActivity.runOnUiThread(this::getPairedDevice);
    }

    //解除配對
    Observer<String> observerDeviceUnpairedObserver = this::onDeviceUnPaired;

    private void onDeviceUnPaired(String deviceAddress) {
        try {
            mainActivity.runOnUiThread(() -> {

                getBinding().progress.setVisibility(View.GONE);

                Log.d(GARMIN_TAG, "onDeviceUnPaired: 當前傳資料的裝置：" + currentGarminAddress + ",被解除的裝置:" + deviceAddress);
                if (currentGarminAddress.equals(deviceAddress)) {
                    String tmpAddress = currentGarminAddress;
                    currentGarminAddress = "-1";
                    w.isGarminConnected.set(false);
                    w.setGarminHr(0);

                    //正在傳資料的手錶被解除配對，讓另外一隻手錶開啟傳資料
                    Log.d(GARMIN_TAG, "onDeviceUnPaired: 正在傳資料的手錶被解除配對，讓另外一隻手錶開啟傳資料");
                    for (Device device : garminDeviceAdapter.getDeviceList()) { //舊的已配對資料
                        if (!tmpAddress.equals(device.address())) {//排除被刪除的那筆
                            if (device.connectionState() == ConnectionState.CONNECTED) {
                                currentGarminAddress = device.address();
                                w.isGarminConnected.set(true);

                                mainActivity.enableRealTimeData(device);
                                Log.d(GARMIN_TAG, "onDeviceUnPaired: 找到裝置，連結:" + currentGarminAddress);
                                break;
                            }
                        }
                    }
                }
                getPairedDevice();


//                new GarminUtil().removeBondedDevice(getApp(), deviceAddress, new GarminUtil.BondStateListener() {
//                    @Override
//                    public void onCheckDeviceBondState(String s, GarminUtil.BOND_STATE bond_state) {
//                        Log.d(GARMIN_TAG, "系統解除配對 onCheckDeviceBondState " + s + "," + bond_state);
//                    }
//
//                    @Override
//                    public void onBondDevice(String s, GarminUtil.BOND_STATE bond_state) {
//                        Log.d(GARMIN_TAG, "系統解除配對 onBondDevice " + s + "," + bond_state);
//                    }
//                });

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //配對完成時
    private void hideProgress(boolean isLoading) {
        getBinding().hideView.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        getBinding().progress2.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        LiveEventBus.get(GARMIN_ON_DEVICE_DISCONNECTED, Device.class).removeObserver(observerDeviceDisconnectedObserver);
        LiveEventBus.get(GARMIN_ON_DEVICE_UNPAIRED, String.class).removeObserver(observerDeviceUnpairedObserver);
        LiveEventBus.get(GARMIN_ON_DEVICE_CONNECTED, Device.class).removeObserver(observerDeviceConnectedObserver);
        LiveEventBus.get(GARMIN_ON_DEVICE_PAIRED, Device.class).removeObserver(observerGarminOnDevicePairedObserver);
        LiveEventBus.get(GARMIN_ON_DEVICE_SETUP_COMPLETE, Device.class).removeObserver(observerGarminOnDeviceSetupCompleteObserver);

        if (devicePairedTimer != null) {
            devicePairedTimer.cancel();
            devicePairedTimer = null;
        }

        isGarminWindowOn = false;
    }

    private void getPairedDevice() {
        Set<Device> pairedDevices = garminDeviceManager.getPairedDevices();
        if (pairedDevices == null) return;
        List<Device> pairedDevicesList = new ArrayList<>(pairedDevices);
        //  Log.d(GARMIN_TAG, "取得已配對裝置: " + pairedDevicesList);
        for (Device d : pairedDevicesList) {
            Log.d(GARMIN_TAG, "取得已配對裝置: " + d.address() + "," + d.connectionState() + "," + d.friendlyName());
        }

        getBinding().progress.setVisibility(View.INVISIBLE);

        garminDeviceAdapter.setData2View(pairedDevicesList);
        getBinding().noDeviceText.setVisibility(pairedDevicesList.size() <= 0 ? View.VISIBLE : View.INVISIBLE);
    }

    GarminDeviceAdapter garminDeviceAdapter;

    private void initGarminDevices() {

        if (getBinding() == null) dismiss();

        RecyclerView recyclerView = getBinding().recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setHasFixedSize(true);
        Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.divider_line_252e37);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(Objects.requireNonNull(drawable));
        recyclerView.addItemDecoration(dividerItemDecoration);
        garminDeviceAdapter = new GarminDeviceAdapter(mContext, isWorkout);
        recyclerView.setAdapter(garminDeviceAdapter);

        //忘記裝置;解除配對
        garminDeviceAdapter.setOnDeleteClickListener(device -> {
            if (CheckDoubleClick.isFastClick()) return;
            Log.d(GARMIN_TAG, "忘記. Device: " + device.address());
            garminDeviceManager.forget(device.address());
            getBinding().progress.setVisibility(View.VISIBLE);
            // > GarminPairedStateListener >  onDeviceUnpaired >  GARMIN_ON_DEVICE_UNPAIRED
        });

        //選擇RealTimeData
        garminDeviceAdapter.setOnRadioListener(new GarminDeviceAdapter.OnRadioListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onRadioClick(Device device) {
                try {
                    currentGarminAddress = device.address();
                    Log.d("GARMINNN", "點擊了 ######RadioClick####### 開啟: " + device.address());
                    mainActivity.enableRealTimeData(device);

                    //解除配對時也會呼叫notifyDataSetChanged
                    garminDeviceAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


//        //點擊
//        garminDeviceAdapter.setOnItemClickListener((heartRateDevice, view1, view2) -> {
//           // HRPopupWindow.this.getBinding().hideView.setVisibility(View.VISIBLE);
//            if (heartRateDevice.isConnected()) {
//
//                hrConnectingAnimation(view1, view2);
//                //斷線
//                mDeviceGem.customMessageDisconnectHeartRateDevice(heartRateDevice);
//            } else {
//
//                boolean isDisconnecting = false;
//                for (DeviceGEM.HeartRateDevice hrDevice : mDeviceGem.customMessageGetHeartRateDevices()) {
//                    if (hrDevice.isConnected()) {
//                        //切斷其他連線裝置
//                        //   mDeviceGem.customMessageDisconnectHeartRateDevice(hrDevice);
//                        isDisconnecting = true;
//                        break;
//                    }
//                }
//
//                //無其他連線裝置
//                if (!isDisconnecting) {
//
//                    hrConnectingAnimation(view1, view2);
//
//                    //連線
//                    mDeviceGem.customMessageConnectHeartRateDevice(heartRateDevice, 7);
//                }
//            }
//
//        });


        //增加裝置
        garminDeviceAdapter.setOnAddDeviceListener(() -> {
            if (CheckDoubleClick.isFastClick()) return;
            GarminPrepareDeviceMsgWindow garminPrepareDeviceMsgWindow = new GarminPrepareDeviceMsgWindow(mContext);
            garminPrepareDeviceMsgWindow.showAtLocation(parentView, Gravity.END | Gravity.BOTTOM, 0, 0);
            garminPrepareDeviceMsgWindow.setOnCustomDismissListener(new OnCustomDismissListener() {
                @Override
                public void onStartDismiss(MsgEvent value) {

                }

                @Override
                public void onDismiss() {

                }
            });
        });
    }


    private void initView() {

        if (getBinding() == null){
            dismiss();
            return;
        }

        getBinding().btnClose.setOnClickListener(v -> {

            //停止掃描
            getDeviceGEM().customMessageStopDiscoveryHeartRateDevice();

            dismiss();
        });
    }


}
