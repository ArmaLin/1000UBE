package com.dyaco.spirit_commercial.support.mediaapp;

import static com.dyaco.spirit_commercial.App.getApp;
import static com.dyaco.spirit_commercial.maintenance_mode.MaintenanceAppManagerFragment.UPDATE_X;

public class AppManagerBean {
    private String appName;
    private String packageName;
    private String className;
    private int versionCode;
    private String versionName;
    private int appIcon;
    private int isUpdate;
    private AppUpdateManager appUpdateManager;

    public AppManagerBean(AppUpdateManager appUpdateManager) {
        this.appUpdateManager = appUpdateManager;
    }


    public AppManagerBean(String appName, String packageName, String className, String versionName, int versionCode, int appIcon, int isUpdate) {
        this.appName = appName;
        this.packageName = packageName;
        this.className = className;
        this.versionCode = versionCode;
        this.versionName = versionName;
        this.appIcon = appIcon;
        this.isUpdate = isUpdate;
    }

    public AppManagerBean setMediaEnum(MediaAppEnum mediaAppEnum) {
        return new AppManagerBean(mediaAppEnum, appUpdateManager);
    }

    public AppManagerBean(MediaAppEnum mediaAppEnum, AppUpdateManager appUpdateManager) {
        this.appName = getApp().getString(mediaAppEnum.getAppName());
        this.packageName = mediaAppEnum.getAppPackageName();
        this.className = mediaAppEnum.getAppClassName();
        this.versionCode = appUpdateManager.getPackageInfoVersionCode(packageName);
        this.versionName = appUpdateManager.getPackageInfoVersionName(packageName);
        this.appIcon = mediaAppEnum.getAppIcon();
        this.isUpdate = UPDATE_X;//0 > ota server 沒資料
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int isUpdate() {
        return isUpdate;
    }

    public void setUpdate(int update) {
        isUpdate = update;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(int appIcon) {
        this.appIcon = appIcon;
    }
}
