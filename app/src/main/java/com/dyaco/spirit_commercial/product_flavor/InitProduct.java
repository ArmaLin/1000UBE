package com.dyaco.spirit_commercial.product_flavor;

import static com.dyaco.spirit_commercial.App.MODE;
import static com.dyaco.spirit_commercial.support.CommonUtils.setSleepMode;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.CONSOLE_SYSTEM_SPIRIT;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.DEFAULT_AUTO_PAUSE_TIME;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.DEFAULT_DISPLAY_BRIGHTNESS;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.DEFAULT_USE_TIME_LIMIT;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.OFF;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.ON;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.PROTOCOL_CSAFE;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.SCREEN_TIMEOUT_15;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.SCREEN_TIMEOUT_NEVER;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.TERRITORY_CANADA;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.TERRITORY_US;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.VIDEO_TV;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.NO_LIMIT;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.TV_TUNER_ANALOG_CABLE;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.TV_TUNER_SIGNAL_DIGITAL;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.MAX_INC_MAX;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.MAX_SPD_IU_MAX_DEF;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.MAX_SPD_MU_MAX_DEF;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;

import com.dyaco.spirit_commercial.App;
import com.dyaco.spirit_commercial.support.CommonUtils;
import com.dyaco.spirit_commercial.support.intdef.DeviceIntDef;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingBean;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Calendar;
import java.util.Date;

public class InitProduct {

    /**
     * <GS mode> 揚升動來動去 不安全,  >>> GS mode 安全
     * grade return = false (揚升不歸零)
     * 即表示GS mode = true
     * Workout 暫停 > 速度歸零 揚升不歸0 ，resume > 回復原來的速度及揚升 ，  結束 > 速度歸零 揚升不歸0
     * <p>
     * <非GS mode>
     * grade return = true  (揚升要歸零)
     * 即表示 GS mode = false
     * Workout 暫停 > 速度及揚升歸0，resume > 回復原來的速度及揚升 ， 結束 > 速度歸0 揚升歸0
     * =======================================
     * <p>
     * <MANUAL GS mode>
     * manual => pause時，incline不歸0，resume後，incline不變、run default speed (0.8km, 0.5mile)，
     * 圖型的部分，已經run過的不變，正在run的之後，incline不變，speed都改為default。
     * profile => pause時，incline 停止，resume後，incline與speed 執行 profile的數值。
     * ---------------------------------------------
     * <MANUAL 非GS mode>
     * manual => pause時，incline 歸零，resume後，incline 維持在 0 、run default speed (0.8km, 0.5mile)，
     * 圖型的部分，已經run過的不變，正在run的之後，incline都改為0，speed改為default。
     * profile => pause時，incline 歸零，resume後，incline與speed 執行 profile的數值。圖型不變。
     * <p>
     * =======================================
     * <p>
     * Elliptical,bike
     * beep > on
     * pause Mode >  off
     * sleep mode > on , 15min
     * workout time limit > off
     * units > imperial
     * video > tv
     * protocol > csafe
     * auto update > on
     * time unit > am/pm
     * display_brightness > 100
     * <p>
     * ==============================
     * treadmill
     * beep > on
     * gs mode > on   > 不會歸0
     * pause Mode >  off
     * auto pause > on , 1min
     * sleep mode > on , 15min
     * workout time limit > off
     * units > imperial
     * video > tv
     * protocol > csafe
     * auto update > on
     * time unit > am/pm
     * display_brightness > 100
     */

    private final Context context;

    public InitProduct(Context context) {
        this.context = context;
    }

    /**
     * 有找到設定檔，增加一些資料進去
     * <p>
     * 原始設定檔的資料:
     * <p>
     * {
     * "beep_sound": 1,
     * "display_brightness": 85,
     * "isGsMode": false,
     * "model_code": 0,
     * "model_name": "CT1000",
     * "protocol": 0,
     * "time_unit": 12,
     * "unit_code": 0,
     * "pauseMode": 1,
     * "sleep_mode": 1,
     * "sleepAfter": 900,
     * "autoPause": 1,
     * "pauseAfter": 180,
     * "isUseTimeLimit": 1,
     * "useTimeLimit": 5940,
     * "isAutoUpdate": true,
     * "video": 1
     * }
     * <p>
     * <p>
     * {
     * "beep_sound": 1,
     * "display_brightness": 85,
     * "isGsMode": false,
     * "model_code": 2,
     * "model_name": "CR1000",
     * "protocol": 0,
     * "time_unit": 12,
     * "unit_code": 0,
     * "pauseMode": 1,
     * "sleep_mode": 1,
     * "sleepAfter": 900,
     * "autoPause": 0,
     * "pauseAfter": 0,
     * "isUseTimeLimit": 1,
     * "useTimeLimit": 5940,
     * "isAutoUpdate": true,
     * "video": 1
     * }
     */
    public void setProductDefault(String settingText) {

        //  DeviceSettingBean deviceSettingBean = new Gson().fromJson(settingText, DeviceSettingBean.class);

        Gson gson = new GsonBuilder().setDateFormat("MMM dd, yyyy HH:mm:ss").create();
        DeviceSettingBean deviceSettingBean = gson.fromJson(settingText, DeviceSettingBean.class);

        //增加一些不在設定檔內的資料
        addData(deviceSettingBean);
    }

    /**
     * ,int territoryCode
     * 找不到設定檔，使用本機預設的設定
     */
    public void setProductDefault(ModeEnum modeEnum, int territoryCode) {

        DeviceSettingBean deviceSettingBean = new DeviceSettingBean();
        // String modelName = context.getString(R.string.model_name);
        deviceSettingBean.setBrand_name("SPIRIT COMMERCIAL");
        deviceSettingBean.setModel_code(modeEnum.getModelCode());
        deviceSettingBean.setModel_name(modeEnum.getModelName());
//        deviceSettingBean.setTerritoryCode(TERRITORY_GLOBAL);
        deviceSettingBean.setTerritoryCode(territoryCode);
        deviceSettingBean.setConsoleSystem(CONSOLE_SYSTEM_SPIRIT);

        switch (modeEnum) {
            case CT1000ENT:
//                deviceSettingBean.setMaxRpm(120);
//                deviceSettingBean.setMinRpm(20);
//                deviceSettingBean.setMaxLevel(20);
//                deviceSettingBean.setMinLevel(1);

                //AUTO PAUSE  >> Auto Logout
//                2.Auto Pause只會在 workout開始後才會偵測，當橋接板error code2 bit4 =1時 (UART lib需要再新增此Event)，表示跑帶在動作但無人在使用；因此，當此狀況持續時間超過上表設定時間 (Pause After時間) 後，上表會自動進入pause mode (就像user按下 pause key)；若此時pause mode設置為on，則5分鐘後結束此次workout，並自動logout，回到login畫面；
//                3.Auto Pause時間預設1分鐘(最低1分鐘，若設為 0表示Auto Pause disable)；
//                4.error code2 bit4 = 1 的Event觸發時間為無人使用後30秒會被觸發，上表要計算時間需直接加上30秒，例如：當Pause After時間設定為1分鐘時，此Event觸發後，只需要再計數30秒即可進入Pause Mode；
//                5.在Pause After時間到之前 error code2 bit4 = 1這個Event被移除了，時間將停止計數，下次這個Event再被觸發時，時間需重新計數，規則同第4點所述；
//                deviceSettingBean.setAutoPause(ON);
//                deviceSettingBean.setPauseAfter(DEFAULT_AUTO_PAUSE_TIME);

                deviceSettingBean.setAutoPause(ON);//auto logout
                deviceSettingBean.setPauseAfter(DEFAULT_AUTO_PAUSE_TIME);

//                deviceSettingBean.setMaxSpeedMu(MAX_SPD_MU_MAX / 10f);//kph
//                deviceSettingBean.setMaxSpeedIu(MAX_SPD_IU_MAX / 10f);//kph
                deviceSettingBean.setMaxSpeedMu(MAX_SPD_MU_MAX_DEF);
                deviceSettingBean.setMaxSpeedIu(MAX_SPD_IU_MAX_DEF);
                deviceSettingBean.setMaxIncline(MAX_INC_MAX >> 1);
                deviceSettingBean.setGsMode(false);
                break;
            case CE1000ENT:
            case CR1000ENT:
            case CU1000ENT:
            case UBE:
                deviceSettingBean.setAutoPause(ON);//auto logout
                deviceSettingBean.setPauseAfter(DEFAULT_AUTO_PAUSE_TIME);

                deviceSettingBean.setSleep_mode(ON);
                deviceSettingBean.setSleepAfter(SCREEN_TIMEOUT_15 / 1000);
                break;
            case STEPPER:
                deviceSettingBean.setAutoPause(ON);//auto logout
                deviceSettingBean.setPauseAfter(DEFAULT_AUTO_PAUSE_TIME);
                deviceSettingBean.setSleep_mode(OFF);
                deviceSettingBean.setSleepAfter(SCREEN_TIMEOUT_NEVER);
                break;
            default:
        }

        deviceSettingBean.setPauseMode(ON);

//        //WORKOUT TIME LIMIT
        deviceSettingBean.setIsUseTimeLimit(ON);//秒
        deviceSettingBean.setUseTimeLimit(DEFAULT_USE_TIME_LIMIT);//秒

        deviceSettingBean.setTvTunerVolume(10);
        deviceSettingBean.setVideo(VIDEO_TV);

        deviceSettingBean.setBle_device_name("");

        // TODO: UNIT
        int unit;
        if (territoryCode == TERRITORY_US || territoryCode == TERRITORY_CANADA) {
            unit = DeviceIntDef.IMPERIAL;
        } else {
            unit = DeviceIntDef.METRIC;
        }
        deviceSettingBean.setUnit_code(unit);

        //SLEEP MODE
//        deviceSettingBean.setSleep_mode(ON);
//        deviceSettingBean.setSleepAfter(SCREEN_TIMEOUT_15 / 1000);

        deviceSettingBean.setProtocol(PROTOCOL_CSAFE);
        //  deviceSettingBean.setNfc(NFC_MEMBERSHIP);
        deviceSettingBean.setBeep_sound(ON);
        deviceSettingBean.setDisplay_brightness(DEFAULT_DISPLAY_BRIGHTNESS);
        deviceSettingBean.setTime_unit(DeviceIntDef.TF_AM_PM);
        deviceSettingBean.setAutoUpdate(true);

        deviceSettingBean.setLimitCode("");

        deviceSettingBean.setUsageRestrictionsType(NO_LIMIT);
        deviceSettingBean.setUsageRestrictionsTimeLimit(0);
        deviceSettingBean.setUsageRestrictionsDistanceLimit(0);
        deviceSettingBean.setCurrentUsageRestrictionsTime(0);
        deviceSettingBean.setCurrentUsageRestrictionsDistance(0);


        //增加一些不在設定檔內的資料
        addData(deviceSettingBean);
    }

    /**
     * 不在設定檔內的資料
     *
     * @param deviceSettingBean d
     */
    private void addData(DeviceSettingBean deviceSettingBean) {
        Date date = Calendar.getInstance().getTime();
        deviceSettingBean.setODO_distance(0d);
        deviceSettingBean.setODO_time(0d);
        deviceSettingBean.setDeviceFirstLaunchTime(date);
        deviceSettingBean.setE051time(date);
        deviceSettingBean.setE052time(date);
        deviceSettingBean.setE053time(date);
        deviceSettingBean.setE054time(date);
        deviceSettingBean.setE0BAtime(date);
        deviceSettingBean.setE0BBtime(date);
        deviceSettingBean.setE0BCtime(date);
        deviceSettingBean.setE0E0time(date);
        deviceSettingBean.setE0E1time(date);
        deviceSettingBean.setE0E2time(date);
        deviceSettingBean.setMachineEdit(false);
        deviceSettingBean.setFtmsNameEdit(false);
        deviceSettingBean.setFirst_launch(true);
        //  deviceSettingBean.setAutoUpdate(true);
        deviceSettingBean.setCurrentVersionCode(new CommonUtils().getLocalVersionCode());

        deviceSettingBean.setLimitCode("");
        deviceSettingBean.setUsageRestrictionsDistanceLimit(0);
        deviceSettingBean.setCurrentUsageRestrictionsTime(0);
        deviceSettingBean.setCurrentUsageRestrictionsDistance(0);

        deviceSettingBean.setTvTunerSignal(TV_TUNER_SIGNAL_DIGITAL);
        deviceSettingBean.setTvTunerAnalogType(TV_TUNER_ANALOG_CABLE);

        //WORKOUT TIME LIMIT
//        deviceSettingBean.setIsUseTimeLimit(OFF);//秒
//        deviceSettingBean.setUseTimeLimit(DEFAULT_USE_TIME_LIMIT);//秒

        //  deviceSettingBean.setTmpApkPath("");

        MODE = ModeEnum.getMode(deviceSettingBean.getModel_code());
        deviceSettingBean.setType(MODE.getTypeCode());
        deviceSettingBean.setCategoryCode(MODE.getCategoryCode()); //DeviceSpiritC.MODEL.TREADMILL_SC.getType()
        App.getApp().setDeviceSettingBean(deviceSettingBean);

        settingSleepAndBrightness(deviceSettingBean);
    }


//    public String getChannel() {
//        try {
//            PackageManager pm = context.getPackageManager();
//            ApplicationInfo appInfo = pm.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
//            return appInfo.metaData.getString("CHANNEL");
//        } catch (PackageManager.NameNotFoundException ignored) {
//        }
//        return "NO_NAME000";
//    }


    public void saveBrightness(int brightness) {
        try {
            ContentResolver resolver = context.getContentResolver();
            Uri uri = Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS);
            Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS, brightness);
            resolver.notifyChange(uri, null);
            Log.d("SETTING_FILE", "亮度: " + brightness);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getBrightness255(float value) {
        return (int) ((value * 0.01f) * 255f);
    }

    private void settingSleepAndBrightness(DeviceSettingBean deviceSettingBean) {

        int time;
        if (deviceSettingBean.getSleep_mode() == ON) {
            time = (int) deviceSettingBean.getSleepAfter() * 1000; //15分鐘
        } else {
            time = SCREEN_TIMEOUT_NEVER; //不休眠
        }

        setSleepMode(time);

        saveBrightness(getBrightness255(deviceSettingBean.getDisplay_brightness()));
    }
}