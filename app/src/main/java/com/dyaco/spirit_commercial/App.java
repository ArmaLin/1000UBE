package com.dyaco.spirit_commercial;

import static com.dyaco.spirit_commercial.support.CommonUtils.showException;

import android.app.Application;
import android.database.CursorWindow;

import com.corestar.libs.device.DeviceDyacoMedical;
import com.corestar.libs.device.DeviceGEM;
import com.corestar.libs.uart.UartConnection;
import com.dyaco.spirit_commercial.model.webapi.CustomDns;
import com.dyaco.spirit_commercial.product_flavor.ModeEnum;
import com.dyaco.spirit_commercial.support.CommonUtils;
import com.dyaco.spirit_commercial.support.ProcessUtils;
import com.dyaco.spirit_commercial.support.RootTools;
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
    public static boolean isFirmwareUpdating = false;
    public static String IMAGE_KEY = "IMAGE_KEY";
    public static String APK_MD5 = "0eba50a45c15b35d977d04d84379b355";
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

    private static DeviceDyacoMedical deviceSpiritC;

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

        MMKV.initialize(this);

        DebugInitializer.init(this);

        LiveEventBus.config().enableLogger(false);
        RootTools.setDefaultHomeLauncher(getPackageName(), MainActivity.class);
        // LiveEventBus.config().lifecycleObserverAlwaysActive(true);

    }

    public static DeviceGEM getDeviceGEM() {
        if (deviceGEM == null) {
            deviceGEM = getDeviceSpiritC().getDeviceGem();
        }
        return deviceGEM;
    }

    public static DeviceDyacoMedical getDeviceSpiritC() {
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

            deviceSpiritC = new DeviceDyacoMedical(INSTANCE);
        }

        return deviceSpiritC;
    }

    private void initEvent() {


    }

    public static App getApp() {
        return INSTANCE;
    }



    /**
     * OkHttp初始化
     *
     * @return OkHttpClient
     */
    public OkHttpClient genericClient() {

        if (mOkHttpClient != null)
            return mOkHttpClient;



        Interceptor headerIntercept = chain -> {

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

    public void setDeviceSettingBean(DeviceSettingBean deviceSettingBean) {
        MMKV.defaultMMKV().encode("DeviceSettingBean", deviceSettingBean);

    }


}
