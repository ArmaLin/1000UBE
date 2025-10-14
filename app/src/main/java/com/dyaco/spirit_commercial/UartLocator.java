package com.dyaco.spirit_commercial;

import android.content.Context;

import com.corestar.libs.device.DeviceDyacoMedical;

public class UartLocator {
    private static DeviceDyacoMedical deviceDyacoMedical;

    public static synchronized DeviceDyacoMedical getDeviceDyacoMedical(Context appContext) {
        if (deviceDyacoMedical == null) {
//            UartConnectionAsync.PORT port ;
//            port = UartConnectionAsync.PORT.PORT_2;
//            devDkCity = new DeviceDyacoMedical(appContext.getApplicationContext(),port);

            deviceDyacoMedical = new DeviceDyacoMedical(appContext.getApplicationContext());


        }
        return deviceDyacoMedical;
    }
}
