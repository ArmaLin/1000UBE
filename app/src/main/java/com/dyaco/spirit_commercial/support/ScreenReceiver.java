package com.dyaco.spirit_commercial.support;

import static com.dyaco.spirit_commercial.App.getApp;
import static com.dyaco.spirit_commercial.MainActivity.isTreadmill;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.SLEEP_MODE_RETAIL;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.dyaco.spirit_commercial.App;
import com.dyaco.spirit_commercial.support.intdef.EventKey;
import com.jeremyliao.liveeventbus.LiveEventBus;

public class ScreenReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {

        String action = intent.getAction();
        //ACTION_SCREEN_OFF觸發之前，就會先進入 onStop
        if (Intent.ACTION_SCREEN_OFF.equalsIgnoreCase(action)) {
            Log.d("休眠", "1111休眠 ACTION_SCREEN_OFF: " + getApp().getDeviceSettingBean().getSleep_mode());
            if (getApp().getDeviceSettingBean().getSleep_mode() == SLEEP_MODE_RETAIL) {

                CommonUtils.wakeUpScreen(null);

                LiveEventBus.get(EventKey.RETAIL_SHOW, Boolean.class).post(true);
//                new RxTimer().timer(2000,number -> mainActivity.showRetail(true));
            } else {

                //休眠
                if (isTreadmill) {
                    Log.d("UART_CONSOLE", "休眠 ACTION_SCREEN_OFF: ");
               //     App.getDeviceSpiritC().setMainModeTreadmill(DeviceDyacoMedical.MAIN_MODE.EUP);//等call back 再下 setEUP
//                App.getDeviceSpiritC().setEUP(2);
                } else {
                    App.getDeviceSpiritC().setEUP(1);
                }

            }

        } else if (Intent.ACTION_SCREEN_ON.equalsIgnoreCase(action)) {
            Log.d("UART_CONSOLE", "休眠 ACTION_SCREEN_ON: ");

        }
    }

}