package com.dyaco.spirit_commercial.listener;

import static com.dyaco.spirit_commercial.support.intdef.EventKey.ON_BT_DEVICE_FOUND;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.util.Log;

import com.corestar.libs.audio.AudioDeviceWatcher;
import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.support.RxTimer;
import com.dyaco.spirit_commercial.viewmodel.AppStatusViewModel;
import com.jeremyliao.liveeventbus.LiveEventBus;

import es.dmoral.toasty.Toasty;

public class AudioDeviceWatcherListener implements AudioDeviceWatcher.DeviceStateListener {
    AppStatusViewModel appStatusViewModel;
    MainActivity mainActivity;
    private static final String TAG = "###BLUETOOTH";

    public AudioDeviceWatcherListener(AppStatusViewModel appStatusViewModel, MainActivity mainActivity) {
        this.appStatusViewModel = appStatusViewModel;
        this.mainActivity = mainActivity;

    }

    //耳機
    @Override
    public void onAuxInStateChanged(AudioDeviceWatcher.CONNECTION_STATE connection_state) {
        Log.d(TAG, "onAuxInStateChanged: " + connection_state);
    }

    @Override
    public void onA2dpAvailable(boolean b) {
        Log.d(TAG, "onA2dpAvailable: " + b);
    }


    /**
     * 連線狀態改變
     */
//    @Override
//    public void onDeviceConnectionStateChanged(BluetoothDevice bluetoothDevice, AudioDeviceWatcher.CONNECTION_STATE connection_state) {
//        Log.d(TAG, "onDeviceConnectionStateChanged: bluetoothDevice:" + bluetoothDevice + ", connection_state:" + connection_state);
//
//        appStatusViewModel.isAudioConnected.set(connection_state == AudioDeviceWatcher.CONNECTION_STATE.CONNECTED);
//
//        new RxTimer().timer(1000, number -> {
//            LiveEventBus.get(ON_BT_DEVICE_SCAN).post(true);
//        });
//    }
//
//    @SuppressLint("MissingPermission")
//    @Override
//    public void onDeviceFound(BluetoothDevice bluetoothDevice) {
//        //    Log.d(TAG, "onDeviceFound: " + bluetoothDevice.getName() +", "+ bluetoothDevice.getAddress() +", "+ bluetoothDevice.getBondState());
//
//        LiveEventBus.get(ON_BT_DEVICE_FOUND).post(bluetoothDevice);
//    }
//    @SuppressLint("MissingPermission")
//    @Override
//    public void onDeviceBondStateChanged(BluetoothDevice bluetoothDevice, AudioDeviceWatcher.BOND_STATE bondState) {
//        Log.d(TAG, "onDeviceBondStateChanged: " + bluetoothDevice + ", 狀態:" + bondState);
//        try {
//            if (bondState == AudioDeviceWatcher.BOND_STATE.BONE_ERROR) {
//                Toasty.error(mainActivity, bluetoothDevice.getName() + " Connection failed ", Toasty.LENGTH_LONG).show();
//            }
//
//            if (bondState == AudioDeviceWatcher.BOND_STATE.BOND_NONE) {
//                new RxTimer().timer(1000, number -> {
//                    LiveEventBus.get(ON_BT_DEVICE_SCAN, Boolean.class).post(true);//重新scan
//                });
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
    @Override
    public void onDeviceConnectionStateChanged(BluetoothDevice bluetoothDevice, AudioDeviceWatcher.CONNECTION_STATE connection_state) {
        Log.d(TAG, "onDeviceConnectionStateChanged: bluetoothDevice:" + bluetoothDevice + ", connection_state:" + connection_state);

        appStatusViewModel.isAudioConnected.set(connection_state == AudioDeviceWatcher.CONNECTION_STATE.CONNECTED);


        new RxTimer().timer(500, number ->
                LiveEventBus.get(ON_BT_DEVICE_FOUND).post(bluetoothDevice));
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onDeviceFound(BluetoothDevice bluetoothDevice) {
        //    Log.d(TAG, "onDeviceFound: " + bluetoothDevice.getName() +", "+ bluetoothDevice.getAddress() +", "+ bluetoothDevice.getBondState());

        LiveEventBus.get(ON_BT_DEVICE_FOUND).post(bluetoothDevice);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onDeviceBondStateChanged(BluetoothDevice bluetoothDevice, AudioDeviceWatcher.BOND_STATE bondState) {
        Log.d(TAG, "onDeviceBondStateChanged: " + bluetoothDevice + ", 狀態:" + bondState);

        try {
            if (bondState == AudioDeviceWatcher.BOND_STATE.BONE_ERROR) {
                Toasty.error(mainActivity, bluetoothDevice.getName() + " Connection failed ", Toasty.LENGTH_LONG).show();
            }

            if (bondState == AudioDeviceWatcher.BOND_STATE.BOND_NONE) {
                new RxTimer().timer(500, number -> {
//                    LiveEventBus.get(ON_BT_DEVICE_BOND_NONE, Boolean.class).post(true);
                    LiveEventBus.get(ON_BT_DEVICE_FOUND).post(bluetoothDevice);
                });
            }

            if (bondState == AudioDeviceWatcher.BOND_STATE.BONDED) {
                new RxTimer().timer(500, number ->
                        LiveEventBus.get(ON_BT_DEVICE_FOUND).post(bluetoothDevice));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
