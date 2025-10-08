package com.dyaco.spirit_commercial.support.mediaapp;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import androidx.core.content.pm.PackageInfoCompat;

import java.math.BigInteger;
import java.util.List;

/**
 * adb shell dumpsys package com.dyaco.spirit_commercial | grep versionCode
 * >>>>>> versionCode=116 minSdk=29 targetSdk=31
 */
 //**/Users/corestarmac/Library/Android/sdk/build-tools/34.0.0/aapt dump badging /Users/corestarmac/Desktop/xxx.apk | grep version


public class PackageManagerUtils {
    public PackageManagerUtils() {
    }

    /**
     * 获取当前设备上安装的所有App
     */
    public List<PackageInfo> getAllApp(Context context) {
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packageInfo = pm.getInstalledPackages(PackageManager.GET_ACTIVITIES);
        return packageInfo;
    }

    /**
     * 判断 App 是否安装
     */
    public boolean isInstalled(Context context, String packageName) {
        if (packageName == null || packageName.length() < 1) {
            return false;
        }
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return packageInfo != null;
        } catch (Throwable ignore) {

        }

        return false;
    }

    /**
     * 根据包名获取 PackageInfo
     */
    public PackageInfo getPackageInfo(Context context, String packageName) {
        if (packageName == null || packageName.length() < 1) {
            return null;
        }
        PackageManager pm = context.getPackageManager();
        try {
            return pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
        } catch (Throwable ignore) {

        }

        return null;
    }

    /**
     * 根据包名获取 版本号
     */
    public int getPackageVersionCode(Context context, String packageName) {
        PackageInfo packageInfo = getPackageInfo(context, packageName);
        if (packageName != null) {
            return packageInfo.versionCode;
        }

        return -1;
    }

    //VERSION CODE
    public int getPackageVersionCode2(Context context, String packageName) {

        PackageInfo pInfo;
        int longVersionCode;
        try {
            pInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            longVersionCode = (int) PackageInfoCompat.getLongVersionCode(pInfo);
        } catch (PackageManager.NameNotFoundException e) {
            return -1;
        }

        return longVersionCode;
    }

    /**
     * 根据包名获取 版本名
     */
    public String getPackageVersionName(Context context, String packageName) {
        PackageInfo packageInfo = getPackageInfo(context, packageName);
        if (packageName != null) {
            return packageInfo.versionName;
        }

        return null;
    }

    /**
     * 获取 App名
     */
    public String getApplicationLabel(Context context, String packageName) {
        if (packageName == null || packageName.length() < 1) {
            return null;
        }
        PackageManager pm = context.getPackageManager();
        PackageInfo packageInfo = getPackageInfo(context, packageName);
        if (packageInfo != null) {
            return packageInfo.applicationInfo.loadLabel(pm).toString();
        }
        return null;
    }

    /**
     * 获取 App的 icon
     */
    public Drawable getApplicationIcon(Context context, String packageName) {
        if (packageName == null || packageName.length() < 1) {
            return null;
        }
        PackageManager pm = context.getPackageManager();
        PackageInfo packageInfo = getPackageInfo(context, packageName);
        if (packageInfo != null) {
            return packageInfo.applicationInfo.loadIcon(pm);
        }
        return null;
    }

    /**
     * 通过Apk路径，获取Apk信息
     */
    public PackageInfo getPackageArchiveInfo(Context context, String apkPath) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo packageInfo = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
            return packageInfo;
        } catch (Throwable ignore) {
            return null;
        }
    }

    /**
     * 當本地地端oldVersion(app版本)小於的newVersion時(service版本),回傳true:更新 /false: 不更新
     * 新需求產生,暫時保留此方法,以防需求改回
     * @param oldVersion   app本地端版本
     * @param newVersion   service 要更新的新版本
     * @return
     */
//    public boolean isUpgrade(String oldVersion, String newVersion) {
//        if (newVersion == null || TextUtils.isEmpty(newVersion)) {
//            newVersion = "0";
//        }
//
//
//        LogUtils.d("isUpgrade-> v1 " + "isUpgrade:" + oldVersion + "/newVersion:" + newVersion);
//        String tempOldVersion = oldVersion.replaceAll("[^0-9]", "");
//        String tempNewVersion = newVersion.replaceAll("[^0-9]", "");
//
//        LogUtils.d("isUpgrade-> v2 " + "tempOldVersion:" + tempOldVersion + "/tempNewVersion:" + tempNewVersion);
//        if (tempOldVersion.length() < tempNewVersion.length()) {
//            int fillNumber = tempNewVersion.length() - tempOldVersion.length();
//            for (int i = 0; i < fillNumber; i++) {
//                tempOldVersion = tempOldVersion + "0";
//            }
//        } else if (tempOldVersion.length() > tempNewVersion.length()) {
//            int fillNumber = tempOldVersion.length() - tempNewVersion.length();
//            for (int i = 0; i < fillNumber; i++) {
//                tempNewVersion = tempNewVersion + "0";
//            }
//        }
//        LogUtils.d("isUpgrade-> v3 " + "tempOldVersion:" + tempOldVersion + "/tempNewVersion:" + tempNewVersion);
////        int intOldVersion = Integer.parseInt(tempOldVersion);
////        int intNewVersion = Integer.parseInt(tempNewVersion);
//
////        if (intOldVersion >= intNewVersion) {
////            return false;
////        } else {
////            return true;
////        }
//
//        BigInteger bigOldVersion = new BigInteger(tempOldVersion);
//
//        BigInteger bigIntNewVersion = new BigInteger(tempNewVersion);
//
//        if (bigOldVersion.compareTo(bigIntNewVersion) == -1) {
//            return true;
//        } else if (bigIntNewVersion.compareTo(bigIntNewVersion) > 0) {
//            return false;
//        } else {
//            return false;
//        }
//    }


    public boolean isUpgrade( String oldVersion, String newVersion) {
        try {
        //    Log.d("MEDIA版本", "isUpgrade: 舊版本：" + oldVersion  +  ", 新版本:"+ newVersion);
            return !oldVersion.equals(newVersion);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 當本地地端oldVersion(app版本)不等於newVersion時(service版本),回傳true:更新 /false: 不更新
     *
     * @param oldVersion app本地端版本
     * @param newVersion service 要更新的新版本
     * @return
     */
    public boolean isUpgradeOld(String oldVersion, String newVersion) {

        if (newVersion == null || TextUtils.isEmpty(newVersion)) {
            return false;
        }

       //   Log.d("UPDATE_APP","isUpgrade-> v1 " + "isUpgrade:" + oldVersion + "/newVersion:" + newVersion);
        //只留下數字   8.128.0 build 5 50793  >  81280550793
        StringBuilder tempOldVersion = new StringBuilder(oldVersion.replaceAll("[^0-9]", ""));
        StringBuilder tempNewVersion = new StringBuilder(newVersion.replaceAll("[^0-9]", ""));

    //       Log.d("UPDATE_APP","isUpgrade-> v2 " + "tempOldVersion:" + tempOldVersion + "/tempNewVersion:" + tempNewVersion);

      //  Log.d("UPDATE_APP", "PPPP: " + TextUtils.isEmpty(tempOldVersion));



        //不補0就要加這段
      //  if (TextUtils.isEmpty(tempOldVersion)) tempOldVersion.append("0");
// TODO:  @@#########checkAppUpdate Exception:java.lang.NumberFormatException: Invalid BigInteger:

        //補0
        if (tempOldVersion.length() < tempNewVersion.length()) {
            int fillNumber = tempNewVersion.length() - tempOldVersion.length();
            for (int i = 0; i < fillNumber; i++) {
                tempOldVersion.append("0");
            }
        } else if (tempOldVersion.length() > tempNewVersion.length()) {
            int fillNumber = tempOldVersion.length() - tempNewVersion.length();
            for (int i = 0; i < fillNumber; i++) {
                tempNewVersion.append("0");
            }
        }


        //補0後版本有問題
        //versionName='8.99.1 build 6 50588'  >>  8991650588   >> 89916505880
        //versionName='8.128.0 build 5 50793' >>  81280550793  >> 81280550793



        //   Log.d("UPDATE_APP", "==================================: ");
    //    Log.d("UPDATE_APP", "isUpgrade-> v3 " + "tempOldVersion:" + tempOldVersion + ", tempNewVersion:" + tempNewVersion);

        BigInteger bigOldVersion = new BigInteger(tempOldVersion.toString());

        BigInteger bigIntNewVersion = new BigInteger(tempNewVersion.toString());


    //    Log.d("UPDATE_APP", "isUpgrade-> v4 " + "tempOldVersion:" + bigOldVersion + ", tempNewVersion:" + bigIntNewVersion);

        if (bigOldVersion.compareTo(bigIntNewVersion) < 0) {
            //如果本地端版本小於新版本的話更新
      //      Log.d("UPDATE_APP", "isUpgrade: 如果本地端版本小於新版本的話更新" );
            return true;
        } else if (bigOldVersion.compareTo(bigIntNewVersion) > 0) {
            //如果本地端版大於新版本的話更新(降版處理)
     //       Log.d("UPDATE_APP", "isUpgrade: 如果本地端版大於新版本的話更新(降版處理)" );
            return true;
   //         return false;
        } else {
            //如果版本一致不更新
            return false;
        }
    }

    public void getPackageSystemDataLog(Context context) {
        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo packageInfo : packages) {
            if ((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
//                LogUtils.d("Installed package (System) :" + packageInfo.packageName);
            } else {
//                LogUtils.d("Installed package (User) :" + packageInfo.packageName);
            }
        }
    }
}
