package com.dyaco.spirit_commercial.support;

import android.annotation.SuppressLint;
import android.app.AppOpsManager;
import android.content.Context;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import timber.log.Timber;

public class UnknownSourcesHelper {
    private final Context mContext;
    private final AppOpsManager mAppOps;

    public UnknownSourcesHelper(Context context) {
        mContext = context;
        mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
    }

    public boolean isUnknownSourcesAllowed() {
        return mContext.getPackageManager().canRequestPackageInstalls();
    }

    @SuppressLint({"DiscouragedPrivateApi","SoonBlockedPrivateApi"})
    public boolean enableUnknownSources() {

        if (isUnknownSourcesAllowed()) {
            Timber.d("已有權限: ");
            return true;
        }
        Timber.d("無權限 開啟權限: ");
        boolean reflectionOK = true;
        try {
            // 1. 反射取 int 型 OP_REQUEST_INSTALL_PACKAGES
            Field opField = AppOpsManager.class.getDeclaredField("OP_REQUEST_INSTALL_PACKAGES");
            opField.setAccessible(true);
            int opRequestInstall = opField.getInt(null);

            // 2. 反射取隱藏的 setUidMode(int code, int uid, int mode)
            Method setUidMode = AppOpsManager.class
                    .getDeclaredMethod("setUidMode", int.class, int.class, int.class);
            setUidMode.setAccessible(true);

            // 3. 執行隱藏 API
            int uid = mContext.getApplicationInfo().uid;
            setUidMode.invoke(mAppOps, opRequestInstall, uid, AppOpsManager.MODE_ALLOWED);

        } catch (Exception e) {
            e.printStackTrace();
            reflectionOK = false;
        }

        return reflectionOK && isUnknownSourcesAllowed();
    }
}

