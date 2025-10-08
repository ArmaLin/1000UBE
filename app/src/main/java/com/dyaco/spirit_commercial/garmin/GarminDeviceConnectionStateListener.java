package com.dyaco.spirit_commercial.garmin;

import static com.dyaco.spirit_commercial.MainActivity.currentGarminAddress;
import static com.dyaco.spirit_commercial.garmin.GarminDevicesWindow.GARMIN_TAG;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.GARMIN_ON_DEVICE_CONNECTED;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.GARMIN_ON_DEVICE_DISCONNECTED;

import android.util.Log;

import androidx.annotation.NonNull;

import com.corestar.libs.hrms.HeartRateDeviceManager;
import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.support.intdef.AppStatusIntDef;
import com.dyaco.spirit_commercial.viewmodel.AppStatusViewModel;
import com.dyaco.spirit_commercial.viewmodel.WorkoutViewModel;
import com.garmin.device.realtime.listeners.RealTimeDataListener;
import com.garmin.health.ConnectionState;
import com.garmin.health.Device;
import com.garmin.health.DeviceConnectionStateListener;
import com.garmin.health.DeviceManager;
import com.garmin.health.bluetooth.FailureCode;
import com.jeremyliao.liveeventbus.LiveEventBus;

import java.util.Set;

public class GarminDeviceConnectionStateListener implements DeviceConnectionStateListener {

    RealTimeDataListener mRealTimeDataListener;
    WorkoutViewModel w;
    AppStatusViewModel appStatusViewModel;
    MainActivity m;
    public GarminDeviceConnectionStateListener(MainActivity mainActivity, WorkoutViewModel workoutViewModel, AppStatusViewModel appStatusViewModel, RealTimeDataListener mRealTimeDataListener) {
        this.w = workoutViewModel;
        this.mRealTimeDataListener = mRealTimeDataListener;
        this.appStatusViewModel = appStatusViewModel;
        this.m = mainActivity;
    }

    @Override
    public void onDeviceConnected(@NonNull Device device) {

        HeartRateDeviceManager.setHrsDevice(device.address());

        if (appStatusViewModel.currentStatus.get() != AppStatusIntDef.STATUS_RUNNING &&
                appStatusViewModel.currentStatus.get() != AppStatusIntDef.STATUS_PAUSE) {
            if (currentGarminAddress.equals("")) {
                //不在Workout時，第一個連線(currentGarminAddress為空)直接連
                m.enableRealTimeData(device);
            } else if (currentGarminAddress.equals("-1")){
                //-1 RealTimeData傳輸中，解除配對或斷線，但沒有馬上找到可連線的設備
                Log.d("GARMINNN", "onDeviceConnected: -1 RealTimeData傳輸中，解除配對或斷線，但沒有馬上找到可連線的設備，有找到連線時開啟RealTimeData:" + device.address());
                m.enableRealTimeData(device);
            }
            w.isGarminConnected.set(true);

            Log.d(GARMIN_TAG, "onDeviceConnected : 裝置連線: " + device.friendlyName() + "," + device.connectionState());
        } else {
            Log.d(GARMIN_TAG, "onDeviceConnected : WORKOUT中，不重連: " + device.friendlyName() + "," + device.connectionState());
        }

        LiveEventBus.get(GARMIN_ON_DEVICE_CONNECTED).post(device);
    }

    @Override
    public void onDeviceDisconnected(@NonNull Device device) {
        HeartRateDeviceManager.setHrsDevice(device.address());
        if (currentGarminAddress.equals(device.address())) {
            //正在傳realtime的設備斷線
            currentGarminAddress = "-1";
            w.isGarminConnected.set(false);
            w.setGarminHr(0);
            Log.d(GARMIN_TAG, "onDeviceDisconnected: 正在傳realtime的設備斷線:" + device.address());
        }

        //找其他還連著的設備，除了運動中
        if (appStatusViewModel.currentStatus.get() != AppStatusIntDef.STATUS_RUNNING &&
                appStatusViewModel.currentStatus.get() != AppStatusIntDef.STATUS_PAUSE) {

            Set<Device> pairedDevices = DeviceManager.getDeviceManager().getPairedDevices();
            if (pairedDevices != null) {
                for (com.garmin.health.Device device1 : pairedDevices) {
                    if (device1.connectionState() == ConnectionState.CONNECTED) {
                        //正在傳realtime的設備斷線，連另外一個
                        if (currentGarminAddress.equals("-1")) {
                            w.isGarminConnected.set(false);
                            w.setGarminHr(0);
                            currentGarminAddress = device1.address();
                            Log.d(GARMIN_TAG, "onDeviceDisconnected: 找其他還連著的設備:" + currentGarminAddress);
                            m.enableRealTimeData(device1);
                            break;
                        }
                    }
                }
            }

            LiveEventBus.get(GARMIN_ON_DEVICE_DISCONNECTED).post(device);

        } else {

            if (currentGarminAddress.equals("-1")) {
                Log.d(GARMIN_TAG, "onDeviceDisconnected: 運動中有傳輸資料中的裝置斷線:" + currentGarminAddress +","+device.address());
                //通知Workout 傳輸中的設備斷線，改變UI
                LiveEventBus.get(GARMIN_ON_DEVICE_DISCONNECTED).post(device);
            }
        }

        Log.d(GARMIN_TAG, "onDeviceDisconnected: " + device.address());
    }

    @Override
    public void onDeviceConnectionFailed(@NonNull Device device, @NonNull FailureCode failureCode) {
        HeartRateDeviceManager.setHrsDevice(device.address());
        Log.d(GARMIN_TAG, "onDeviceConnectionFailed: " + device.friendlyName());
    }

    @Override
    public void onServiceDisconnected() {
        Log.d(GARMIN_TAG, "onServiceDisconnected: ");
    }
}
