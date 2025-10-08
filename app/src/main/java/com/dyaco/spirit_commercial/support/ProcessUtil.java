package com.dyaco.spirit_commercial.support;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

public class ProcessUtil {
    public static String getProcessName(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> apps = am.getRunningAppProcesses();
        if (apps == null) {
            return null;
        }

        for (ActivityManager.RunningAppProcessInfo app : apps) {
            if (app.pid == android.os.Process.myPid()) {
                return app.processName;
            }
        }

        return null;
    }
}