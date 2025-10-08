package com.dyaco.spirit_commercial.support;

import static com.dyaco.spirit_commercial.support.CommonUtils.updateTime;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.dyaco.spirit_commercial.viewmodel.DeviceSettingViewModel;

public class TimeTickReceiver extends BroadcastReceiver {
    DeviceSettingViewModel deviceSettingViewModel;
    public TimeTickReceiver(DeviceSettingViewModel deviceSettingViewModel) {
        this.deviceSettingViewModel = deviceSettingViewModel;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        deviceSettingViewModel.timeText.set(updateTime());

    //    Log.d("UPLOAD_ERROR_LOG", "時間 " + Calendar.getInstance().get(Calendar.HOUR_OF_DAY) +","+ Calendar.getInstance().get(Calendar.MINUTE));
//        if ((Calendar.getInstance().get(Calendar.HOUR_OF_DAY) == 12))  {
//            if (Calendar.getInstance().get(Calendar.MINUTE) == 0) {
//                new CallWebApi(context).apiUploadErrorLog();
//            }
//        }
    }

}