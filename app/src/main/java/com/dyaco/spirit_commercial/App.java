package com.dyaco.spirit_commercial;

import static com.dyaco.spirit_commercial.support.CommonUtils.showException;

import android.app.Application;
import android.database.CursorWindow;

import com.corestar.libs.device.DeviceGEM;
import com.corestar.libs.device.DeviceSpiritC;
import com.corestar.libs.uart.UartConnection;
import com.dyaco.spirit_commercial.model.webapi.CustomDns;
import com.dyaco.spirit_commercial.product_flavor.ModeEnum;
import com.dyaco.spirit_commercial.support.CommonUtils;
import com.dyaco.spirit_commercial.support.ProcessUtils;
import com.dyaco.spirit_commercial.support.intdef.DeviceIntDef;
import com.dyaco.spirit_commercial.support.room.spirit.spirit_entity.UserProfileEntity;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingBean;
import com.jeremyliao.liveeventbus.LiveEventBus;
import com.tencent.mmkv.MMKV;

import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class App extends Application {
    public static String IMAGE_KEY = "IMAGE_KEY";
    public static String APK_MD5 = "cda88afbd2352bd04f6ae97cb5f00225";
    public static boolean isShowMediaMenuOnStop = true; //onStop時，是否要顯示MediaMenu
    public static boolean SETTING_SHOW = false;
    public static ModeEnum MODE;
    public static int MAX_HR;
    public static int UNIT_E = DeviceIntDef.IMPERIAL;
    //    public static int MODE_E = DeviceIntDef.DEVICE_TYPE_TREADMILL;
    public static int TIME_UNIT_E = DeviceIntDef.TF_AM_PM;
    private static App INSTANCE;
    private static OkHttpClient mOkHttpClient;
    public static int SW_VERSION;
    public static int NEW_SW_VERSION;

    private static DeviceGEM deviceGEM;
    public static String COOKIE = "";
    public static String EGYM_BASIC_AUTHORIZATION = "";
    public static String EGYM_BEARER_AUTHORIZATION = "";
    //public static String MACHINE_MAC = "";

    private static DeviceSpiritC deviceSpiritC;

    //deviceSpiritC = new DeviceSpiritC(INSTANCE, port);
    //SpiritCommercialUart > DeviceSpiritC.TIMEOUT_CONTROL

    @Override
    public void onCreate() {
        super.onCreate();

        //Garmin ghProcess 造成重複執行
        if (!ProcessUtils.isMainProcess(this)) return;


        // TODO: android.database.sqlite.SQLiteBlobTooBigException: Row too big to fit into CursorWindow requiredPos=0, totalRows=1
        //增大 資料庫 CursorWindow
        try {
            Field field = CursorWindow.class.getDeclaredField("sCursorWindowSize");
            field.setAccessible(true);
            field.set(null, 10 * 1024 * 1024); // 設為 10MB
        } catch (Exception e) {
            showException(e);
        }


        INSTANCE = this;

//        if (BuildConfig.DEBUG) {
//            Stetho.initializeWithDefaults(this);
//        }

        initEvent();

        ///data/user/0/com.dyaco.spirit_commercial/files/mmkv
        try {
            MMKV.initialize(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        LiveEventBus.config().enableLogger(false);

        // LiveEventBus.config().lifecycleObserverAlwaysActive(true);

    }

    public static DeviceGEM getDeviceGEM() {
        if (deviceGEM == null) {
            deviceGEM = getDeviceSpiritC().getDeviceGem();
        }
        return deviceGEM;
    }

    public static DeviceSpiritC getDeviceSpiritC() {
        if (deviceSpiritC == null) {
            //      deviceSpiritC = new DeviceSpiritC(INSTANCE, UartConnection.PORT.PORT_0);
            //   deviceSpiritC = new DeviceSpiritC(INSTANCE);
            //   deviceSpiritC = new DeviceSpiritC(INSTANCE, UartConnection.PORT.PORT_1);//伍豐 五峰 五豐

            UartConnection.PORT port ;
//            if (TEST_MODE) {
            //          port = UartConnection.PORT.PORT_2;
//            } else {
            port = UartConnection.PORT.PORT_1;
            //      }

            deviceSpiritC = new DeviceSpiritC(INSTANCE, port);
        }

        return deviceSpiritC;
    }

    private void initEvent() {

//        //時鐘 Broadcast
//        IntentFilter filter = new IntentFilter(Intent.ACTION_TIME_TICK);
//        registerReceiver(new TimeTickReceiver(), filter);
    }

    public static App getApp() {
        return INSTANCE;
    }

//    public static Context getContext() {
//        return INSTANCE.getApplicationContext();
//    }

    /**
     * OkHttp初始化
     *
     * @return OkHttpClient
     */
    public OkHttpClient genericClient() {

        if (mOkHttpClient != null)
            return mOkHttpClient;

//        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor();
//        HttpLoggingInterceptor.Level level = HttpLoggingInterceptor.Level.HEADERS;
//        logInterceptor.setLevel(level);

        Interceptor headerIntercept = chain -> {
//            try {
//                Thread.sleep(2000); // 模擬 延遲
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
            Request original = chain.request();
            Request request = original.newBuilder()

                    .header("Cookie", COOKIE)
//                    .header("Accept-Encoding", "gzip")
//                    .header("Content-Encoding", "gzip")
//                    .header("timeZone", TimeZone.getDefault().getID())
//                    .header("now", Calendar.getInstance().getTime().toString())
//                    .header("nowMillis", String.valueOf(Calendar.getInstance().getTimeInMillis()))
//                    .header("app", "spirit")
                    .method(original.method(), original.body())
                    .build();
            //  Log.d(TAG, "genericClient: " + Calendar.getInstance().getTime() + "," + Calendar.getInstance().getTimeInMillis());
            return chain.proceed(request);
        };

        return mOkHttpClient = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .connectTimeout(10, TimeUnit.SECONDS)
                .dns(new CustomDns(25))
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                //     .addInterceptor(logInterceptor)
                //     .addInterceptor(new GzipRequestInterceptor()) // 加入 Gzip 攔截器
                //   .addInterceptor(getNetworkInterceptor()) // 檢查網路連線
                .addInterceptor(headerIntercept)
                .build();
    }

    // 檢查網路狀態
    private static Interceptor getNetworkInterceptor() {
        return chain -> {
            if (!CommonUtils.isNetworkAvailable(App.getApp())) {
                //  Toasty.warning(getApp(),"沒網路",Toasty.LENGTH_SHORT).show();
                throw new NoInternetException("No Internet Connection");
            }
            return chain.proceed(chain.request());
        };
    }

    // 自訂例外
    public static class NoInternetException extends RuntimeException {
        public NoInternetException(String message) {
            super(message);
        }
    }

    /**
     * 取得GUID
     *
     * @return GUID
     */
    public String getIdentity() {
        return MMKV.defaultMMKV().decodeString("GUID", "");
    }


    public void setTvCountry(UserProfileEntity userProfileEntity) {
        MMKV.defaultMMKV().encode("UserProfileEntity", userProfileEntity);
    }

    public DeviceSettingBean getTvCountry() {
        return MMKV.defaultMMKV().decodeParcelable("DeviceSettingBean", DeviceSettingBean.class, new DeviceSettingBean());
    }



    public void setUserProfile(UserProfileEntity userProfileEntity) {
        MMKV.defaultMMKV().encode("UserProfileEntity", userProfileEntity);
    }

    public DeviceSettingBean getDeviceSettingBean() {
        return MMKV.defaultMMKV().decodeParcelable("DeviceSettingBean", DeviceSettingBean.class, new DeviceSettingBean());
    }

//    public DeviceSettingBean getDeviceSettingBean2() {
//        byte[] rawBytes = MMKV.defaultMMKV().decodeBytes("DeviceSettingBean");
//        if (rawBytes == null || rawBytes.length == 0) {
//            Log.i("AppSetting", "找不到設定資料，將建立預設值。");
//            DeviceSettingBean defaultBean = new DeviceSettingBean();
//            setDeviceSettingBean(defaultBean);
//            return defaultBean;
//        }
//
//        DeviceSettingBean bean = null;
//        boolean isCorrupted = false;
//
//        // --- 第一重檢查：嘗試解析，如果直接崩潰，就是損毀 ---
//        Parcel parcelForCheck = Parcel.obtain();
//        try {
//            parcelForCheck.unmarshall(rawBytes, 0, rawBytes.length);
//            parcelForCheck.setDataPosition(0);
//            bean = new DeviceSettingBean(parcelForCheck);
//
//            // --- 第二重檢查：Parcel 剩餘資料量檢查 ---
//            // 如果成功讀取完所有欄位後，Parcel 中還有剩餘的資料，
//            // 代表寫入的總位元組數 > 讀取的總位元組數，資料版本肯定不對。
//            if (parcelForCheck.dataAvail() > 0) {
//                Log.e("AppSetting", "資料損毀：Parcel 讀取完畢後仍有 " + parcelForCheck.dataAvail() + " 位元組剩餘。");
//                isCorrupted = true;
//            }
//
//        } catch (Exception e) {
//            Log.e("AppSetting", "資料損毀：解析 Parcel 時發生崩潰。", e);
//            isCorrupted = true;
//        } finally {
//            parcelForCheck.recycle();
//        }
//
//        // --- 第三重檢查：擴展版資料健康檢查 ---
//        if (!isCorrupted && !isBeanDataSane(bean)) {
//            Log.w("AppSetting", "資料損毀：Sanity Check 失敗。");
//            isCorrupted = true;
//        }
//
//
//        // --- 根據檢查結果進行處理 ---
//        if (isCorrupted) {
//            // 資料確定已損毀，啟動救援或重置
//            Log.w("AppSetting", "偵測到資料損毀，嘗試啟動救援程序...");
//            DeviceSettingBean rescuedBean = rescueBeanFromOldVersion(rawBytes);
//
//            if (rescuedBean != null) {
//                Log.i("AppSetting", "成功從舊版資料中救援設定！");
//                setDeviceSettingBean(rescuedBean);
//                return rescuedBean;
//            } else {
//                Log.e("AppSetting", "救援程序失敗，將重置為預設值。");
//                DeviceSettingBean defaultBean = new DeviceSettingBean();
//                setDeviceSettingBean(defaultBean);
//                return defaultBean;
//            }
//        } else {
//            // 所有檢查都通過，資料是健康的
//            //   Log.i("AppSetting", "資料健康，正常載入。");
//            return bean;
//        }
//    }
//
    public void setDeviceSettingBean(DeviceSettingBean deviceSettingBean) {
        MMKV.defaultMMKV().encode("DeviceSettingBean", deviceSettingBean);

        //存入ViewModel
//        UNIT_E = deviceSettingViewModel.unitCode.get();
//        MODE_E = deviceSettingViewModel.modelCode.get();
        //  return MMKV.defaultMMKV().encode("DeviceSettingBean", deviceSettingBean);
    }
//
//
//    private DeviceSettingBean rescueBeanFromOldVersion(byte[] corruptedBytes) {
//        if (corruptedBytes == null || corruptedBytes.length == 0) {
//            return null;
//        }
//
//        Parcel parcel = Parcel.obtain();
//        try {
//            DeviceSettingBean rescuedBean = new DeviceSettingBean();
//            parcel.unmarshall(corruptedBytes, 0, corruptedBytes.length);
//            parcel.setDataPosition(0);
//
//            // --- 嚴格按照舊版的 `writeToParcel` 順序進行完整讀取 ---
//            rescuedBean.setUnit_code(parcel.readInt());
//            rescuedBean.setODO_time(parcel.readDouble());
//            rescuedBean.setODO_distance(parcel.readDouble());
//            rescuedBean.setSleep_mode(parcel.readInt());
//            rescuedBean.setBeep_sound(parcel.readInt());
//            rescuedBean.setDsFrontInclineAd(parcel.readString());
//            rescuedBean.setDsRearInclineAd(parcel.readString());
//            rescuedBean.setDsLevelAd(parcel.readString());
//            rescuedBean.setFirst_launch(parcel.readByte() != 0);
//            rescuedBean.setModel_code(parcel.readInt());
//            rescuedBean.setTime_unit(parcel.readInt());
//            rescuedBean.setBle_device_name(parcel.readString());
//            rescuedBean.setModel_name(parcel.readString());
//            rescuedBean.setChild_lock(parcel.readInt());
//            rescuedBean.setDisplay_brightness(parcel.readInt());
//            rescuedBean.setCategoryCode(parcel.readInt());
//            rescuedBean.setMaxRpm(parcel.readInt());
//            rescuedBean.setMinRpm(parcel.readInt());
//            rescuedBean.setMaxLevel(parcel.readInt());
//            rescuedBean.setMinLevel(parcel.readInt());
//            rescuedBean.setBrand_name(parcel.readString());
//            rescuedBean.setDeviceFirstLaunchTime((java.util.Date) parcel.readSerializable());
//            rescuedBean.setDeviceStep(parcel.readLong());
//            rescuedBean.setE050hour(parcel.readLong());
//            rescuedBean.setE052hour(parcel.readLong());
//            rescuedBean.setE0BAhour(parcel.readLong());
//            rescuedBean.setE051time((java.util.Date) parcel.readSerializable());
//            rescuedBean.setE052time((java.util.Date) parcel.readSerializable());
//            rescuedBean.setE053time((java.util.Date) parcel.readSerializable());
//            rescuedBean.setE054time((java.util.Date) parcel.readSerializable());
//            rescuedBean.setE0BAtime((java.util.Date) parcel.readSerializable());
//            rescuedBean.setE0BBtime((java.util.Date) parcel.readSerializable());
//            rescuedBean.setE0BCtime((java.util.Date) parcel.readSerializable());
//            rescuedBean.setE0E0time((java.util.Date) parcel.readSerializable());
//            rescuedBean.setE0E1time((java.util.Date) parcel.readSerializable());
//            rescuedBean.setE0E2time((java.util.Date) parcel.readSerializable());
//            rescuedBean.setType(parcel.readInt());
//            rescuedBean.setAntPlusDeviceId(parcel.readInt());
//            rescuedBean.setNfc(parcel.readInt());
//            rescuedBean.setVideo(parcel.readInt());
//            rescuedBean.setTv(parcel.readInt());
//            rescuedBean.setProtocol(parcel.readInt());
//            rescuedBean.setPauseMode(parcel.readInt());
//            rescuedBean.setSleepAfter(parcel.readLong());
//            rescuedBean.setUseTimeLimit(parcel.readLong());
//            rescuedBean.setGsMode(parcel.readBoolean());
//            rescuedBean.setTvCountry((com.corestar.libs.device.DeviceTvTuner.TV_COUNTRY) parcel.readSerializable());
//            rescuedBean.setMachine_mac(parcel.readString());
//            rescuedBean.setMachineEdit(parcel.readBoolean());
//            rescuedBean.setFtmsNameEdit(parcel.readBoolean());
//            rescuedBean.setTvTunerVolume(parcel.readInt());
//
//            // !!!==================== 關鍵救援點 ====================!!!
//            // 我們假設舊版在這裡寫入的是兩個 double
//            // 所以我們用 readDouble() 來讀取，以確保讀取的位元組數正確
//            double oldMaxSpeedMu = parcel.readDouble();
//            double oldMaxSpeedIu = parcel.readDouble();
//            // 將讀取到的 double 值，強制轉換為 int 後，存入新的物件
//            rescuedBean.setMaxSpeedMu((int)oldMaxSpeedMu);
//            rescuedBean.setMaxSpeedIu((int)oldMaxSpeedIu);
//            // !!!====================================================!!!
//
//            rescuedBean.setMaxIncline(parcel.readDouble());
//            rescuedBean.setPauseAfter(parcel.readLong());
//            rescuedBean.setAutoPause(parcel.readInt());
//            rescuedBean.setIsUseTimeLimit(parcel.readInt());
//            rescuedBean.setAutoUpdate(parcel.readBoolean());
//            rescuedBean.setFakeStart(parcel.readBoolean());
//            rescuedBean.setCurrentVersionCode(parcel.readLong());
//            rescuedBean.setSoftwareUpdatedMillis(parcel.readLong());
//            rescuedBean.setTmpApkPath(parcel.readString());
//            rescuedBean.setShowUpdateNotify(parcel.readBoolean());
//            rescuedBean.setUsageRestrictionsDistanceLimit(parcel.readInt());
//            rescuedBean.setLimitCode(parcel.readString());
//            rescuedBean.setCurrentUsageRestrictionsDistance(parcel.readDouble());
//            rescuedBean.setCurrentUsageRestrictionsTime(parcel.readInt());
//            rescuedBean.setUsageRestrictionsType(parcel.readInt());
//            rescuedBean.setUsageRestrictionsTimeLimit(parcel.readLong());
//            rescuedBean.setTvTunerSignal(parcel.readInt());
//            rescuedBean.setTvTunerAnalogType(parcel.readInt());
//            rescuedBean.setMinSpeedIu(parcel.readInt());
//            rescuedBean.setMinSpeedMu(parcel.readInt());
//            rescuedBean.setTerritoryCode(parcel.readInt());
//            rescuedBean.setDefaultLanguage((java.util.Locale) parcel.readSerializable());
//            rescuedBean.setMediaApps(parcel.readString());
//            rescuedBean.setConsoleSystem(parcel.readInt());
//
//            Log.i("AppSetting", "手動解析救援成功！大部分資料已被保留。");
//            return rescuedBean;
//
//        } catch (Exception e) {
//            Log.e("AppSetting", "手動解析救援過程中發生預期外的錯誤。", e);
//            return null;
//        } finally {
//            parcel.recycle();
//        }
//    }
//
//
//    /**
//     * 資料健康檢查的輔助方法
//     */
//    private boolean isBeanDataSane(DeviceSettingBean bean) {
//        if (bean.getTerritoryCode() == 5){
//            Log.e("AppSetting", "TerritoryCode 異常: " + bean.getTerritoryCode());
//            Log.e("AppSetting", "TerritoryCode 異常: " + bean.getModel_code());
//            return false;
//        }
////        if (bean.getUnit_code() < 0 || bean.getUnit_code() > 1) {
////            Log.e("AppSetting", "unit_code 異常: " + bean.getUnit_code());
////            return false;
////        }
////        if (bean.getSleep_mode() < 0 || bean.getSleep_mode() > 1) {
////            Log.e("AppSetting", "sleep_mode 異常: " + bean.getSleep_mode());
////            return false;
////        }
////        if (bean.getBeep_sound() < 0 || bean.getBeep_sound() > 1) {
////            Log.e("AppSetting", "beep_sound 異常: " + bean.getBeep_sound());
////            return false;
////        }
////        if (bean.getDisplay_brightness() < 0 || bean.getDisplay_brightness() > 100) {
////            Log.e("AppSetting", "display_brightness 異常: " + bean.getDisplay_brightness());
////            return false;
////        }
//        return true;
//    }

}
