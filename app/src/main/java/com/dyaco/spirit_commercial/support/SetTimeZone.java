package com.dyaco.spirit_commercial.support;

import android.app.AlarmManager;
import android.content.Context;
import android.util.Log;

import com.dyaco.spirit_commercial.model.webapi.BaseApi;
import com.dyaco.spirit_commercial.model.webapi.IServiceApi;
import com.dyaco.spirit_commercial.model.webapi.bean.TimeZoneBean;
import com.dyaco.spirit_commercial.model.webapi.bean.TimeZoneBean2;

import java.util.TimeZone;

public class SetTimeZone {
    private final Context context;

    public SetTimeZone(Context context) {
        this.context = context;
    }

    public void checkTimeZone() {
        Log.d("TIME_ZONE", "Start checkTimeZone: ");
        BaseApi.request(BaseApi.createApi2(IServiceApi.class, "http://www.geoplugin.net/").apiGetTimeZone(),
                new BaseApi.IResponseListener<TimeZoneBean>() {
                    @Override
                    public void onSuccess(TimeZoneBean data) {
                        try {
                            Log.d("TIME_ZONE", "onSuccess: " + data.getGeoplugin_timezone());
                            updateTimeZone(data.getGeoplugin_timezone());
                        } catch (Exception e) {
                            Log.d("TIME_ZONE", "try catch_onSuccess: ");
                            checkTimeZone2();
                        }
                    }

                    @Override
                    public void onFail() {
                        Log.d("TIME_ZONE", "@@@onFail: ");
                        checkTimeZone2();
                    }
                });
    }

    private void checkTimeZone2() {
        Log.d("TIME_ZONE", "Start checkTimeZone2: ");
        BaseApi.request(BaseApi.createApi2(IServiceApi.class, "http://ip-api.com/").apiGetTimeZone2(),
                new BaseApi.IResponseListener<TimeZoneBean2>() {
                    @Override
                    public void onSuccess(TimeZoneBean2 data) {
                        try {
                            Log.d("TIME_ZONE", "onSuccess: " + data.getTimezone());
                            updateTimeZone(data.getTimezone());
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.d("TIME_ZONE", "try catch_onSuccess: ");

                            updateTimeZone(TimeZone.getDefault().getID());
                        }
                    }

                    @Override
                    public void onFail() {
                        Log.d("TIME_ZONE", "onFail: ");
                        // setTimeZone("Asia/Taipei");
                        updateTimeZone(TimeZone.getDefault().getID());
                    }
                });
    }

    private void updateTimeZone(String timeZone) {
        try {
            AlarmManager am = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            am.setTimeZone(timeZone);
            Log.d("TIME_ZONE", "TIME_ZONE: " + TimeZone.getDefault().getID());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
