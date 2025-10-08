package com.dyaco.spirit_commercial.settings;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 藍芽配對視窗監聽
 */
public final class BluetoothPairingRequest extends BroadcastReceiver {
    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_PAIRING_REQUEST.equals(action)) {


                abortBroadcast();
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                //配對時自動點確認
                device.setPairingConfirmation(true);






//                String pinCode = "123456";
//                // 自動設置 PIN 碼
//                try {
//                    byte[] pinBytes = pinCode.getBytes();
//                    Method setPinMethod = device.getClass().getMethod("setPin", byte[].class);
//                    setPinMethod.invoke(device, pinBytes);
//
//                    // 自動確認配對
//                    Method setPairingConfirmation = device.getClass().getMethod("setPairingConfirmation", boolean.class);
//                    setPairingConfirmation.invoke(device, true);
//
//                    Log.d("GARMINNN", "設定PIN碼並確認配對請求: " + device.getAddress());
//                } catch (Exception e) {
//                    e.getLocalizedMessage();
//                    Log.d("GARMINNN", "自動配對過程出現錯誤", e);
//                }


                /**
                 *         //BluetoothPairingRequest
                 *         IntentFilter filter = new IntentFilter();
                 *         filter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
                 *         filter.setPriority(10000);//設置最大優先級
                 *         registerReceiver(new BluetoothPairingRequest(), filter);
                 */
            }
        }
    }
}
