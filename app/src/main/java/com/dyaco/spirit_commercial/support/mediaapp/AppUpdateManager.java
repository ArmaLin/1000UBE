package com.dyaco.spirit_commercial.support.mediaapp;

import android.content.Context;
import android.content.pm.PackageInfo;

import androidx.core.content.pm.PackageInfoCompat;

public class AppUpdateManager {


    private final Context mContext;
    private final PackageManagerUtils mPackageManagerUtils;


    public AppUpdateManager(Context context, PackageManagerUtils packageManagerUtils) {
        this.mContext = context;
        this.mPackageManagerUtils = packageManagerUtils;
    }

    public String getPackageInfoVersionName(String packageName) {
        PackageInfo packageInfo = mPackageManagerUtils.getPackageInfo(mContext, packageName);
        return packageInfo == null ? "" : packageInfo.versionName;
    }

    public int getPackageInfoVersionCode(String packageName) {
        PackageInfo packageInfo = mPackageManagerUtils.getPackageInfo(mContext, packageName);
        return packageInfo == null ? -1 : (int) PackageInfoCompat.getLongVersionCode(packageInfo);
    }

    public String getPackageInfoPackageName(String packageName) {
        PackageInfo packageInfo = mPackageManagerUtils.getPackageInfo(mContext, packageName);
        return packageInfo == null ? "" : packageInfo.packageName;
    }

    public String getAppUpdateVersion(AppStoreBean.AppUpdateBeansDTO appUpdateBeans, String appUpdateName) {
        if (appUpdateBeans == null) return null;
        String version = "";

        if (appUpdateBeans.getPackageName().equals(appUpdateName)) {
            version = appUpdateBeans.getVersion();
        }
        return version;
    }

    public String getAppUpdateDownloadUrl(AppStoreBean.AppUpdateBeansDTO appUpdateBeans, String appUpdateName) {
        if (appUpdateBeans == null) return null;
        String DownloadUrl = "";

        if (appUpdateBeans.getPackageName().equals(appUpdateName)) {
            DownloadUrl = appUpdateBeans.getApk().get(0).getDownloadUrl();
        }
        return DownloadUrl;
    }

    public boolean isForceUpdates(AppStoreBean.AppUpdateBeansDTO appUpdateBeans, String appUpdateName) {
        boolean isForceUpdates = false;

        if (appUpdateBeans.getPackageName().equals(appUpdateName)) {
            if (appUpdateBeans.getForceUpdates().equals("YES"))
                isForceUpdates = true;
        }
        return isForceUpdates;
    }


}
