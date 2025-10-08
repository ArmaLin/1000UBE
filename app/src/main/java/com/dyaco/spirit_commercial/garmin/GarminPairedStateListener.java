package com.dyaco.spirit_commercial.garmin;

import static com.dyaco.spirit_commercial.App.getApp;
import static com.dyaco.spirit_commercial.MainActivity.currentGarminAddress;
import static com.dyaco.spirit_commercial.garmin.GarminDevicesWindow.GARMIN_TAG;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.GARMIN_ON_DEVICE_PAIRED;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.GARMIN_ON_DEVICE_SETUP_COMPLETE;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.GARMIN_ON_DEVICE_UNPAIRED;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.corestar.libs.hrms.HeartRateDeviceManager;
import com.corestar.libs.utils.GarminUtil;
import com.dyaco.spirit_commercial.viewmodel.WorkoutViewModel;
import com.garmin.health.AuthCompletion;
import com.garmin.health.Device;
import com.garmin.health.DevicePairedStateListener;
import com.jeremyliao.liveeventbus.LiveEventBus;

public class GarminPairedStateListener implements DevicePairedStateListener {
    private final Context mAppContext;
    private WorkoutViewModel workoutViewModel;

    public GarminPairedStateListener(Context appContext, WorkoutViewModel workoutViewModel) {
        this.mAppContext = appContext.getApplicationContext();
        this.workoutViewModel = workoutViewModel;
    }

    @Override
    public void onDeviceSetupComplete(@NonNull Device device) {
        HeartRateDeviceManager.setHrsDevice(device.address());
        // 剛配對完成就連線
        Log.d(GARMIN_TAG, "onDeviceSetupComplete:開啟RealTimeData " + device.address() + "," + device.model());
        LiveEventBus.get(GARMIN_ON_DEVICE_SETUP_COMPLETE).post(device);
    }

    @Override
    public void onDevicePaired(@NonNull Device device) {
        HeartRateDeviceManager.setHrsDevice(device.address());
        Log.d(GARMIN_TAG, "onDevicePaired 配對完成: " + device.friendlyName());
        LiveEventBus.get(GARMIN_ON_DEVICE_PAIRED).post(device);
    }

    @Override
    public void onDeviceUnpaired(@NonNull String macAddress) {
        HeartRateDeviceManager.setHrsDevice(macAddress);
        Log.d(GARMIN_TAG, "Listener onDeviceUnpaired: " + macAddress);

        if (currentGarminAddress.equals(macAddress)) {
            //正在傳資料的手錶被解除配對
            currentGarminAddress = "-1";
            workoutViewModel.isGarminConnected.set(false);
            workoutViewModel.setGarminHr(0);
        }


        new GarminUtil().removeBondedDevice(getApp(), macAddress, new GarminUtil.BondStateListener() {
            @Override
            public void onCheckDeviceBondState(String s, GarminUtil.BOND_STATE bond_state) {
                Log.d(GARMIN_TAG, "系統解除配對 onCheckDeviceBondState " + s + "," + bond_state);
            }

            @Override
            public void onBondDevice(String s, GarminUtil.BOND_STATE bond_state) {
                Log.d(GARMIN_TAG, "系統解除配對 onBondDevice " + s + "," + bond_state);
            }
        });






        LiveEventBus.get(GARMIN_ON_DEVICE_UNPAIRED).post(macAddress);
    }


    @Override
    public boolean onAuthRequested(Device device, AuthCompletion completion) {
        if (Looper.myLooper() == null) Looper.prepare();
        Toast.makeText(mAppContext, String.format("Device %s Requesting Authentication.", device.model()), Toast.LENGTH_LONG).show();

        return true;
    }

    @Override
    public void onServiceDisconnected() {
    }
}