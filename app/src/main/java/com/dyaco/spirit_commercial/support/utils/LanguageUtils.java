package com.dyaco.spirit_commercial.support.utils;

import static com.dyaco.spirit_commercial.App.getApp;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.backup.BackupManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.LocaleList;

import com.dyaco.spirit_commercial.support.intdef.LanguageEnum;

import java.lang.reflect.Method;
import java.util.EnumSet;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

public class LanguageUtils {

    public static String getLan() {
        //系統語言
        if (!Locale.getDefault().getCountry().isEmpty()) {
            return Locale.getDefault().getLanguage() + "_" + Locale.getDefault().getCountry();
        } else {
            return Locale.getDefault().getLanguage();
        }



        //todo app語言
//        if (getApp().getDeviceSettingBean().getDefaultLanguage() == null) {
//            DeviceSettingBean deviceSettingBean = getApp().getDeviceSettingBean();
//            deviceSettingBean.setDefaultLanguage(LanguageEnum.ENGLISH.getLocale());
//            getApp().setDeviceSettingBean(deviceSettingBean);
//        }
//        Log.d("OOIIOOPP", "取得系統儲存的語言: " + getApp().getDeviceSettingBean().getDefaultLanguage().toString());
//        //自訂語言
//        return getApp().getDeviceSettingBean().getDefaultLanguage().toString();
    }

    public static int getLanguageName() {
        AtomicInteger languageName = new AtomicInteger();

        EnumSet.allOf(LanguageEnum.class)
                .forEach(languageEnum -> {
                    if (languageEnum.getLanguageTag().equals(Locale.getDefault().toLanguageTag())) {
                        languageName.set(languageEnum.getLanguageStr());
                    }
                });

        return languageName.get();
    }

    @SuppressLint("PrivateApi")
    public static void changeLanguageSetting(Locale locale) {
        try {
            Class<?> activityManagerNative = Class.forName("android.app.ActivityManager");
            Object am = activityManagerNative.getMethod("getService").invoke(activityManagerNative);
            Configuration config = (Configuration) am.getClass().getMethod("getConfiguration").invoke(am);
           // config.setLocale(locale);
            config.setLocales(new LocaleList(locale, LanguageEnum.ENGLISH.getLocale())); // 切換語言時,加入第二語言 英文，避免netflix變中文
            config.getClass().getDeclaredField("userSetLocale").setBoolean(config, true);
            am.getClass().getMethod("updatePersistentConfiguration", android.content.res.Configuration.class).invoke(am, config);
            BackupManager.dataChanged("com.android.providers.settings");
            //在改變語言以後所有的Activity都會被kill掉，然後在重新調用 Activity的onCreate方法
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("PrivateApi")
    public static void updateLanguage(Locale locale) {
        try {
            Object objIAm;

            Class<ActivityManager> ClzAm = ActivityManager.class;
            ActivityManager am = (ActivityManager) getApp().getSystemService(Context.ACTIVITY_SERVICE);
            Class<?> clzIAM = Class.forName("android.app.IActivityManager");

            Method getIamMethod = ClzAm.getMethod("getService");
            objIAm = getIamMethod.invoke(am);
            Method mtdIActMag$getConfiguration = clzIAM.getDeclaredMethod("getConfiguration");
            Configuration config = (Configuration) mtdIActMag$getConfiguration.invoke(objIAm);
            config.locale = locale;
            config.setLocales(new LocaleList(locale));
            // android.permission.CHANGE_CONFIGURATION
            // 會重新調用 onCreate();

            Method updatePersistentConfigurationMethod = clzIAM.getMethod("updatePersistentConfiguration", Configuration.class);
            updatePersistentConfigurationMethod.invoke(objIAm, config);

            Class[] clzParams = { Configuration.class };
            Method mtdIActMag$updateConfiguration = clzIAM.getDeclaredMethod("updateConfiguration", clzParams);
            mtdIActMag$updateConfiguration.invoke(objIAm, config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public String getLocaleString(int id, Locale locale) {
//        Configuration configuration = new Configuration(getApp().getResources().getConfiguration());
//        configuration.setLocale(locale);
//        return getApp().createConfigurationContext(configuration).getResources().getString(id);
//    }
}
