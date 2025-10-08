package com.dyaco.spirit_commercial;

import static com.corestar.libs.device.DeviceSpiritC.MAIN_MODE.RESET;
import static com.dyaco.spirit_commercial.App.COOKIE;
import static com.dyaco.spirit_commercial.App.MODE;
import static com.dyaco.spirit_commercial.App.SETTING_SHOW;
import static com.dyaco.spirit_commercial.App.UNIT_E;
import static com.dyaco.spirit_commercial.App.getApp;
import static com.dyaco.spirit_commercial.App.getDeviceGEM;
import static com.dyaco.spirit_commercial.App.getDeviceSpiritC;
import static com.dyaco.spirit_commercial.App.isShowMediaMenuOnStop;
import static com.dyaco.spirit_commercial.dashboard_media.MediaMenuWindow2.isShowTv;
import static com.dyaco.spirit_commercial.dashboard_media.YouTubeWindow.isWebViewOn;
import static com.dyaco.spirit_commercial.garmin.GarminDevicesWindow.GARMIN_TAG;
import static com.dyaco.spirit_commercial.garmin.GarminDevicesWindow.isGarminWindowOn;
import static com.dyaco.spirit_commercial.login.LoginFragment.isGuestQuickStart;
import static com.dyaco.spirit_commercial.product_flavor.ModeEnum.CU1000ENT;
import static com.dyaco.spirit_commercial.product_flavor.ModeEnum.getMode;
import static com.dyaco.spirit_commercial.support.CommonUtils.changeSystemFontSize;
import static com.dyaco.spirit_commercial.support.CommonUtils.clearAppData;
import static com.dyaco.spirit_commercial.support.CommonUtils.clearCookies;
import static com.dyaco.spirit_commercial.support.CommonUtils.closePackage;
import static com.dyaco.spirit_commercial.support.CommonUtils.deleteCache;
import static com.dyaco.spirit_commercial.support.CommonUtils.getSystemFontSize;
import static com.dyaco.spirit_commercial.support.CommonUtils.isNetworkAvailable;
import static com.dyaco.spirit_commercial.support.CommonUtils.isScreenOn;
import static com.dyaco.spirit_commercial.support.CommonUtils.makeDropDownMeasureSpec;
import static com.dyaco.spirit_commercial.support.CommonUtils.restartApp;
import static com.dyaco.spirit_commercial.support.CommonUtils.showException;
import static com.dyaco.spirit_commercial.support.CommonUtils.updateResourceFontSize;
import static com.dyaco.spirit_commercial.support.CommonUtils.updateTime;
import static com.dyaco.spirit_commercial.support.FormulaUtil.mph2kph;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.getInclineValue;
import static com.dyaco.spirit_commercial.support.intdef.AppStatusIntDef.CURRENT_PAGE_MEDIA;
import static com.dyaco.spirit_commercial.support.intdef.AppStatusIntDef.STATUS_MAINTENANCE;
import static com.dyaco.spirit_commercial.support.intdef.AppStatusIntDef.STATUS_PAUSE;
import static com.dyaco.spirit_commercial.support.intdef.AppStatusIntDef.STATUS_RUNNING;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.CONSOLE_SYSTEM_EGYM;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.DEFAULT_AUTO_PAUSE_TIME;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.DEFAULT_USE_TIME_LIMIT;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.DEVICE_TYPE_ELLIPTICAL;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.DEVICE_TYPE_RECUMBENT_BIKE;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.DEVICE_TYPE_TREADMILL;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.DEVICE_TYPE_UPRIGHT_BIKE;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.IMPERIAL;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.METRIC;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.OFF;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.ON;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.PROTOCOL_CSAFE;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.TERRITORY_CANADA;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.TERRITORY_JAPAN;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.TERRITORY_US;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.VIDEO_NONE;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.VIDEO_TV;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.CLOSE_YOUTUBE_FULL_SCREEN;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.EVENT_WORK_NOTIFY_UPDATE;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.FTMS_START_OR_RESUME;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.GYM3_ON_EVENT;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.MEDIA_MENU_GO_TO_MEDIA;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.ON_STOP_BACK_TO_MAIN_TRAINING;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.SHOW_PANELS;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.SHOW_SAMSUNG_DISCONNECTED;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.START_WORKOUT;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.WIFI_WORK;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DISTANCE_LIMIT;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_EMS_IDLE_STANDBY;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_IDLE_STANDBY;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.FEMALE;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.MALE;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.MEDIA_TYPE_HDMI;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.NO_LIMIT;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.TV_TUNER_ANALOG_CABLE;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.TV_TUNER_SIGNAL_DIGITAL;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.UE_MCU_EMS;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.USER_TYPE_GUEST;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.USER_TYPE_NORMAL;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.USER_TYPE_VIP;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.MAX_LEVEL_MAX;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.MAX_LEVEL_MIN;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.PORT_EZ_CAST;
import static com.dyaco.spirit_commercial.support.intdef.WorkoutIntDef.UNLIMITED;
import static com.dyaco.spirit_commercial.work_task.WorkManagerUtil.WORK_NOTIFY_UPDATE_MSG_TAG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.PopupWindowCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.work.WorkInfo;

import com.corestar.calculation_libs.Calculation;
import com.corestar.libs.audio.AudioDeviceWatcher;
import com.corestar.libs.device.DeviceCab;
import com.corestar.libs.device.DeviceCsafe;
import com.corestar.libs.device.DeviceGEM;
import com.corestar.libs.device.DeviceSpiritC;
import com.corestar.libs.device.DeviceTvTuner;
import com.corestar.libs.device.GemSettings;
import com.corestar.libs.utils.GemSettingsFactory;
import com.dyaco.spirit_commercial.alert_message.BackgroundWindow;
import com.dyaco.spirit_commercial.alert_message.NotifyWarringWindow;
import com.dyaco.spirit_commercial.alert_message.SafetyKeyWindow;
import com.dyaco.spirit_commercial.alert_message.SystemErrorWindow;
import com.dyaco.spirit_commercial.alert_message.UpdateRestartWindow;
import com.dyaco.spirit_commercial.alert_message.WebApiAlertWindow;
import com.dyaco.spirit_commercial.dashboard_media.FloatingBottomMenuWindow2;
import com.dyaco.spirit_commercial.dashboard_media.FloatingTopDashBoardWindow2;
import com.dyaco.spirit_commercial.dashboard_media.MediaMenuWindow2;
import com.dyaco.spirit_commercial.dashboard_media.YouTubeWindow;
import com.dyaco.spirit_commercial.dashboard_training.CustomBean;
import com.dyaco.spirit_commercial.databinding.ActivityMainBinding;
import com.dyaco.spirit_commercial.egym.EgymUtil;
import com.dyaco.spirit_commercial.garmin.GarminDeviceConnectionStateListener;
import com.dyaco.spirit_commercial.garmin.GarminDevicesWindow;
import com.dyaco.spirit_commercial.garmin.GarminPairedStateListener;
import com.dyaco.spirit_commercial.garmin.GarminRealTimeDataListener;
import com.dyaco.spirit_commercial.garmin.HealthSDKManager;
import com.dyaco.spirit_commercial.listener.AudioDeviceWatcherListener;
import com.dyaco.spirit_commercial.listener.CabDeviceEventListener;
import com.dyaco.spirit_commercial.listener.CsafeDeviceEventListener;
import com.dyaco.spirit_commercial.listener.DeviceGemEventListener;
import com.dyaco.spirit_commercial.listener.IUartConsole;
import com.dyaco.spirit_commercial.listener.TvTunerDeviceEventListener;
import com.dyaco.spirit_commercial.login.LoginFragment;
import com.dyaco.spirit_commercial.maintenance_mode.MaintenanceUsageRestrictionsWindow;
import com.dyaco.spirit_commercial.maintenance_mode.RetailVideoWindow;
import com.dyaco.spirit_commercial.maintenance_mode.UsageLimitReachedWindow;
import com.dyaco.spirit_commercial.maintenance_mode.usbx.BinaryInstaller;
import com.dyaco.spirit_commercial.model.webapi.BaseApi;
import com.dyaco.spirit_commercial.model.webapi.CallWebApi;
import com.dyaco.spirit_commercial.model.webapi.IServiceApi;
import com.dyaco.spirit_commercial.model.webapi.bean.GetUnreadMessageCountFromMachineBean;
import com.dyaco.spirit_commercial.model.webapi.bean.MemberCheckinMachineByQRCodeBean;
import com.dyaco.spirit_commercial.model.webapi.bean.MemberCheckoutMachineBean;
import com.dyaco.spirit_commercial.model.webapi.bean.UpdateBean;
import com.dyaco.spirit_commercial.product_flavor.DeviceSettingCheck2;
import com.dyaco.spirit_commercial.product_flavor.DownloadManagerCustom;
import com.dyaco.spirit_commercial.product_flavor.InitProduct;
import com.dyaco.spirit_commercial.settings.AppleWatchWindow;
import com.dyaco.spirit_commercial.settings.BtAudioWindow;
import com.dyaco.spirit_commercial.settings.HRPopupWindow;
import com.dyaco.spirit_commercial.settings.MemberProfileWindow;
import com.dyaco.spirit_commercial.settings.QrCodePopupWindow;
import com.dyaco.spirit_commercial.settings.SamsungWatchWindow;
import com.dyaco.spirit_commercial.settings.SettingPopupWindow;
import com.dyaco.spirit_commercial.settings.SoundPopupWindow;
import com.dyaco.spirit_commercial.settings.WifiQrCodePopupWindow;
import com.dyaco.spirit_commercial.settings.WifiWindow;
import com.dyaco.spirit_commercial.support.CommonUtils;
import com.dyaco.spirit_commercial.support.ConnectionStateMonitor;
import com.dyaco.spirit_commercial.support.FloatingWidget;
import com.dyaco.spirit_commercial.support.FormulaUtil;
import com.dyaco.spirit_commercial.support.GlideApp;
import com.dyaco.spirit_commercial.support.LoadingWindowAllB;
import com.dyaco.spirit_commercial.support.LoadingWindowEx;
import com.dyaco.spirit_commercial.support.MsgEvent;
import com.dyaco.spirit_commercial.support.RxTimer;
import com.dyaco.spirit_commercial.support.ScreenReceiver;
import com.dyaco.spirit_commercial.support.SystemProperty;
import com.dyaco.spirit_commercial.support.TimeUpdateManager;
import com.dyaco.spirit_commercial.support.base_component.BaseBindingActivity;
import com.dyaco.spirit_commercial.support.base_component.BasePopupMsgWindow;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.custom_view.CustomToast;
import com.dyaco.spirit_commercial.support.custom_view.LoadingWindow;
import com.dyaco.spirit_commercial.support.custom_view.LoadingWindow2;
import com.dyaco.spirit_commercial.support.intdef.AppStatusIntDef;
import com.dyaco.spirit_commercial.support.intdef.DeviceIntDef;
import com.dyaco.spirit_commercial.support.intdef.EventKey;
import com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS;
import com.dyaco.spirit_commercial.support.interaction.DebounceClick;
import com.dyaco.spirit_commercial.support.mediaapp.MediaAppUtils;
import com.dyaco.spirit_commercial.support.room.fitness_test.FitnessTestDbManager;
import com.dyaco.spirit_commercial.support.room.spirit.SpiritDbManager;
import com.dyaco.spirit_commercial.support.room.spirit.spirit_entity.ErrorMsgEntity;
import com.dyaco.spirit_commercial.support.room.spirit.spirit_entity.MediaAppsEntity;
import com.dyaco.spirit_commercial.support.utils.CheckDoubleClick;
import com.dyaco.spirit_commercial.support.utils.LanguageUtils;
import com.dyaco.spirit_commercial.support.utils.LogS;
import com.dyaco.spirit_commercial.viewmodel.AppStatusViewModel;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingBean;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingViewModel;
import com.dyaco.spirit_commercial.viewmodel.EgymDataViewModel;
import com.dyaco.spirit_commercial.viewmodel.ErrorInfo;
import com.dyaco.spirit_commercial.viewmodel.UserProfileViewModel;
import com.dyaco.spirit_commercial.viewmodel.WorkoutViewModel;
import com.dyaco.spirit_commercial.work_task.WorkManagerUtil;
import com.dyaco.spirit_commercial.workout.NotifyNumWindow;
import com.dyaco.spirit_commercial.workout.ReadyStartPopupWindow;
import com.dyaco.spirit_commercial.workout.UploadWorkoutParam;
import com.dyaco.spirit_commercial.workout.programs.ProgramsEnum;
import com.fec.hdmiin.HdmiIn;
import com.garmin.device.realtime.RealTimeDataType;
import com.garmin.health.Device;
import com.garmin.health.DeviceManager;
import com.garmin.health.GarminHealth;
import com.garmin.health.GarminHealthInitializationException;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.gson.Gson;
import com.jeremyliao.liveeventbus.LiveEventBus;
import com.samade.remoteservice.dyaco.AnplusBean;
import com.samade.remoteservice.dyaco.AnplusCtrlBean;
import com.samade.remoteservice.dyaco.AnplusMachine;
import com.samade.remoteservice.dyaco.DyacoDataEmitter;
import com.samade.remoteservice.dyaco.DyacoDataReceiver;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;

import es.dmoral.toasty.Toasty;

public class MainActivity extends BaseBindingActivity<ActivityMainBinding> {
    public boolean isEgymNoData = false;
    public static String forcePkName;
    public static int forcePkNameSort;
    public static boolean isGMS = false;//此機器無GMS
    public static boolean isHomeScreen;
    public static String mRetailVideoPath;
    public static String splashImagePath;
    // public static String splashImagePathPNG;
    public static String splashImagePathJPG;
    public CustomBean customBean = new CustomBean();

    public static boolean isTvSUC = false;
    public static boolean isTvOn = false;

    //工程模式切換 HDMI View的判斷
    //進tv設定時會換成另一個view ，退出時換回原本的view
    //AssignView 若有替換成新View  會觸發 onActive
    public static boolean isReAssignView;

    public static boolean isStopLogin = false;
    public static boolean isOnStopBackToMainTraining = false;
    //    public static String lastMedia = "";

    //最後開啟的MediaApp，如果Media開啟時 切換成Training頁籤，再切回Media頁籤時，要自動開啟MediaApp
    public static MediaAppsEntity lastMedia;

    public static boolean limitLoutOut = false;
    public static boolean isCbHrOn = true;
    public static boolean isCbBtOn = true;
    public static boolean isCbWifiOn = true;
    public static int mediaType;
    public static boolean isSummary = false;
    public static String currentGarminAddress = ""; //目前RealTimeData的Garmin裝置

    public static int TV_TUNER_VOLUME = 10; // range: 1 -20

    public static Map<DeviceGEM.EQUIPMENT_CONTROL_PARAMETER, Integer> controlParameterMap;
    public List<UploadWorkoutParam.FormDTO.RawDataListDTO> rawDataListDTOList = new ArrayList<>();

    public RxTimer pingGemTimer;
    public static String UPDATE_FILE_PATH;
    // //mnt/sdcard/Android/data
    // /storage/emulated/0/Android/data/com.dyaco.spirit_commercial/files
    public static String SETTING_FILE_PATH;
    public WorkoutViewModel workoutViewModel;
    public AppStatusViewModel appStatusViewModel;
    public DeviceSettingViewModel deviceSettingViewModel;
    public static UserProfileViewModel userProfileViewModel;
    public EgymDataViewModel egymDataViewModel;
    public PopupWindow popupWindow;
    public PopupWindow popupWindow3;
    public NavController navController;
    private BackgroundWindow backgroundWindow;
    public static boolean isTreadmill = true;
    public static boolean isUs = false;
    public static boolean isEmulator;

    public IUartConsole uartConsole;
    public Calculation calculation;

    public DownloadManagerCustom downloadManagerCustom;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(updateResourceFontSize(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //   MMKV.defaultMMKV().encode(IMAGE_KEY, 0);

        //garminUnPair();
        clearAppData();
        clearCookies();
        closePackage(this);
        deleteCache(this);

//        TouchIdleWatcher watcher;
//        watcher = new TouchIdleWatcher();
//        watcher.setIdleTimeoutMs(10000);
//        watcher.setCallback(new TouchIdleWatcher.Callback() {
//            @Override
//            public void onScreenTouched() {
//                Log.v("TouchIdleWatcher", "被碰了: ");
//            }
//
//            @Override
//            public void onIdleTimeout() {
//                Log.v("TouchIdleWatcher", "時間到了: ");
//            }
//        }).start();


//        //todo app語言
//        if (getApp().getDeviceSettingBean().getDefaultLanguage() == null) {
//            DeviceSettingBean deviceSettingBean = getApp().getDeviceSettingBean();
//            deviceSettingBean.setDefaultLanguage(LanguageEnum.ENGLISH.getLocale());
//            getApp().setDeviceSettingBean(deviceSettingBean);
//        }


        mRetailVideoPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/CoreStar/Dyaco/Spirit/retail.mp4";

        //splashImagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/CoreStar/Dyaco/Spirit/";

        //   splashImagePathPNG = Environment.getExternalStorageDirectory().getAbsolutePath() + "/CoreStar/Dyaco/Spirit/welcome.png";
        splashImagePathJPG = Environment.getExternalStorageDirectory().getAbsolutePath() + "/CoreStar/Dyaco/Spirit/welcome.jpg";


        initialize();


        //取得 default_settings.json 的資料，存入 DeviceSettingBean(mmkv)
        initDeviceModel();

        initAppUpdate();


        WorkManagerUtil workManagerUtil = new WorkManagerUtil();
        if (workManagerUtil.checkWorkStateFromTag(WORK_NOTIFY_UPDATE_MSG_TAG) == WorkInfo.State.ENQUEUED &&
                "".equals(getApp().getDeviceSettingBean().getTmpApkPath())) {
            workManagerUtil.cancelWorkByTag(WORK_NOTIFY_UPDATE_MSG_TAG);
        }


        //建立設定檔的資料庫，及檢查錯誤
        new DeviceSettingCheck2().checkError();


//        new RxTimer().timer(2000, number ->
//                getBinding().spiritView.setVisibility(View.GONE));


        Looper.myQueue().addIdleHandler(() -> {

            //   installExFatBinary();

            controlParameterMap = new HashMap<>();

            //UART初始化
            initUartConsole();

            //外部控制(FTMS、實體按鍵)
            initExControl();

//            if (isTreadmill) {
//                getDeviceSpiritC().setEchoMode(DeviceSpiritC.ECHO_MODE.AA);
//            }

            //關閉狀態列
            new CommonUtils().hideStatusBar(1);


            closePackage(this);


            getDeviceSpiritC().setUsbMode(DeviceSpiritC.USB_MODE.CHARGER);

            //   requestPermission();

            initHdmi();

//            if (!NetworkHelper.isNetworkAvailable(getApplication())) {
//                internetNotifyWarringWindow(true);
//            }

            if (!isNetworkAvailable(getApplication())) {
                internetNotifyWarringWindow(true);
            }


            checkLimit();


            checkUploadWebApi();


            holdScreen();

            getBinding().setIsUs(isUs);


            //檢查Error Log 上傳
            new CallWebApi(this).apiUploadErrorLog();


            //   DeviceSettingBean d = getApp().getDeviceSettingBean();
            //   Log.d("UPLOAD_ERROR_LOG", "onCreate: " + FormulaUtil.mi2km(d.getODO_distance()) + ","+ d.getODO_time());
            new WorkManagerUtil().checkUploadErrorLog();
            //    new WorkManagerUtil().checkWorkStateFromTag(WORK_UPLOAD_ERROR_LOG_TAG);


            //    Log.d("FONT_SIZE", "onCreate: " + CommonUtils.getSystemFontSize(this));
//            new RxTimer().timer( 2000,number -> {
//                CommonUtils.changeSystemFontSize(this,0.85f);
//                new RxTimer().timer(1000,number1 -> {
//                    Log.d("FONT_SIZE", "onCreate: " + CommonUtils.getSystemFontSize(this));
//                });
//            });


            //   ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            //   Log.d("VVVVVVVVV", "onCreate: " + getMacAddress());

            //  Log.d("OOOOOOEEEE", "onCreate: " + getSystemFontSize(this));
            if (getSystemFontSize(this) < 1.3f) {
                //    Log.d("OOOOOOEEEE", "onCreate: 改變大小");
                changeSystemFontSize(this, 1.3f);
                //    Log.d("OOOOOOEEEE", "get: " + getSystemFontSize(this));
            }

            /**
             * 讓app的字體不要變大， 不知為何FloatingTopDashBoardWindow的字變大了
             *     protected void attachBaseContext(Context newBase) {
             *         super.attachBaseContext(updateResourceFontSize(newBase));
             *     }
             */

            //    workoutViewModel.isGarminConnected.set(true);


            //######## AUTO LOGOUT
            userInteractionHandler = new Handler();
            r = () -> {

                if (deviceSettingViewModel.autoPause.getValue() == OFF) return;

                if (appStatusViewModel.currentStatus.get() == STATUS_RUNNING) return;
                if (appStatusViewModel.currentStatus.get() == STATUS_PAUSE) return;

                if (!isHomeScreen) {
                    stopHandler();
                    return;
                }

                Log.d("RPM_CHECK", "準備登出: ");

                if (isUs) {
                    if (deviceSettingViewModel.consoleSystem.get() == CONSOLE_SYSTEM_EGYM) {
                        webApiLogout();
                    } else {
                        clearAppData();
                        clearCookies();
                        closePackage(this);
                        deleteCache(this);
                        stopHandler();
                    }
                    Log.d("RPM_CHECK", "US >>>> AUTO LOGOUT: ");
                } else {
                    webApiLogout();
                }
            };


            MediaAppUtils.checkConsoleMediaApp();


            MediaAppUtils.checkForceUpdate();

            //  if (deviceSettingViewModel.consoleSystem.get() == CONSOLE_SYSTEM_EGYM) {

            EgymUtil.init(this, deviceSettingViewModel, egymDataViewModel);
            //    }


//            // APK 文件的路径
//            Log.d("OOOOOOOEEEEEE", "onCreate: " + UPDATE_FILE_PATH);
//            PackageManager pm = getPackageManager();
//            PackageInfo packageInfo = pm.getPackageArchiveInfo(UPDATE_FILE_PATH, 0);
//
//            if (packageInfo != null) {
//                String versionName = packageInfo.versionName;
//                long versionCode = packageInfo.getLongVersionCode();
//                Log.d("OOOOOOOEEEEEE", "APK Version Name: " + versionName);
//                Log.d("OOOOOOOEEEEEE", "APK Version Code: " + versionCode);
//            } else {
//                Log.d("OOOOOOOEEEEEE", "无法读取 APK 文件信息");
//            }

            return false;
        });

    }


//    private void aaa() {
//        //    Log.d("PPPPPPPPPP", Arrays.toString(ModeEnum.getBikeWattTable2()).);
//        int[][] wattArray = ModeEnum.getEllWattTable2();
//        String[] rpmArray = {"20", "25", "30", "35", "40", "45", "50", "55", "60", "65", "70", "75", "80", "85", "90", "95", "100", "105", "110", "115", "120"};
//
//        JSONArray jsonArray = new JSONArray();
//        JSONObject jsonObject = new JSONObject();
//        try {
//            for (int i = 0; i < wattArray.length; i++) {
//                for (int j = 0; j < rpmArray.length; j++) {
//                    jsonObject.put((rpmArray[j]), String.valueOf(Math.round(wattArray[i][j])));
//                }
//                jsonArray.put(i, jsonObject);
//                Log.d("PPPPPPPPEE@@@@@@", jsonObject.toString() + ",");
//            }
//
//            //  longLog(jsonArray.toString());
//            //  Log.d("PPPPPPPPEE@@@@@@", jsonArray.toString());
//        } catch (JSONException | AssertionError e) {
//            Log.d("PPPPPPPPEE@@@@@@", "exception: " + e.getLocalizedMessage());
//        }
//
//    }

    private void checkUploadWebApi() {


        new CallWebApi(MainActivity.this).apiUploadWorkoutFromMachineSigned();
    }


    public void checkLimit() {
        DeviceSettingBean deviceSettingBean = getApp().getDeviceSettingBean();
        //0:都不限制, 1:限制 distance,2:限制 time
        if (deviceSettingBean.getUsageRestrictionsType() == NO_LIMIT) return;

        boolean isShow = false;
        if (deviceSettingBean.getUsageRestrictionsType() == DISTANCE_LIMIT) {
            if (deviceSettingBean.getCurrentUsageRestrictionsDistance() >= deviceSettingBean.getUsageRestrictionsDistanceLimit()) {
                isShow = true;
            }
        } else {
//            if (deviceSettingBean.getCurrentUsageRestrictionsTime() > deviceSettingBean.getUsageRestrictionsTimeLimit()) {
            if (deviceSettingBean.getCurrentUsageRestrictionsTime() >= (deviceSettingBean.getUsageRestrictionsTimeLimit())) {
                isShow = true;
            }
        }


        Log.d("LIMIT_SHOW", "LIMIT種類: " + deviceSettingBean.getUsageRestrictionsType() + ",\n" +
                "(2 = 時間)設定的時間:" + deviceSettingBean.getUsageRestrictionsTimeLimit() + " 秒, 當前時間:" + deviceSettingBean.getCurrentUsageRestrictionsTime() + " 秒\n" +
                "(1 = 距離)設定的距離:" + deviceSettingBean.getUsageRestrictionsDistanceLimit() + " km, 當前距離:" + deviceSettingBean.getCurrentUsageRestrictionsDistance());

        if (!isShow) return;
        popupWindow = new UsageLimitReachedWindow(this);
        popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);
        ((UsageLimitReachedWindow) popupWindow).setOnCustomDismissListener(new BasePopupWindow.OnCustomDismissListener() {
            @Override
            public void onStartDismiss(MsgEvent value) {

                if (value != null) {
                    if (((boolean) value.getObj())) {
                        MaintenanceUsageRestrictionsWindow usageRestrictionsWindow = new MaintenanceUsageRestrictionsWindow(MainActivity.this);
                        usageRestrictionsWindow.showAtLocation(getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);
                    }
                }
            }

            @Override
            public void onDismiss() {
                popupWindow = null;
            }
        });
    }

    @Override
    protected ActivityMainBinding onCreateViewBinding(@NonNull LayoutInflater layoutInflater) {
        return ActivityMainBinding.inflate(layoutInflater);
    }


    public BluetoothAdapter bluetoothAdapter;

    /**
     * 取得Bluetooth MAC，要開藍牙
     */
    @SuppressLint("MissingPermission")
    public void initBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            if (!bluetoothAdapter.isEnabled()) {

                if (bluetoothAdapter.enable()) {
                    initBtDevice();

                    new RxTimer().timer(2000, number -> appStatusViewModel.isBtOn.set(true));
                } else {
                    appStatusViewModel.isBtOn.set(false);
                    Toasty.error(this, "Bluetooth ERROR !", Toasty.LENGTH_LONG).show();
                }
//                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
//                    //22:22:18:C6:0C:00
//                    App.MACHINE_MAC = "7E:C1:46:61:62:AE";
//                    Log.d("EEEEEEE", "getBluetoothMacAddress: " + android.provider.Settings.Secure.getString(getContentResolver(), "bluetooth_address"));
//                        }
//                ).launch(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE));
            } else {
                initBtDevice();

                new RxTimer().timer(2000, number -> appStatusViewModel.isBtOn.set(true));
            }
        } else {
            appStatusViewModel.isBtOn.set(false);
            Toasty.error(this, "Bluetooth ERROR !!", Toasty.LENGTH_LONG).show();
        }
    }

    private void initBtDevice() {
        initAudioDeviceWatcher();
        unBondAudio();

        new Thread(this::initGarmin).start();

    }

    @SuppressLint("MissingPermission")
    public void unBondAudio() {
        if (mAudioDeviceWatcher == null) return;
        Set<BluetoothDevice> bluetoothDeviceSet = getAudioDeviceWatcher().getBondedDevices();
        for (BluetoothDevice b : bluetoothDeviceSet) {
            mAudioDeviceWatcher.removeBondDevice(b);
        }
    }

    @SuppressLint("MissingPermission")
    private void checkBtAudio() {
        //檢查藍牙音訊是否連接
        BluetoothProfile.ServiceListener mProfileListener = new BluetoothProfile.ServiceListener() {
            public void onServiceConnected(int profile, BluetoothProfile proxy) {
                if (profile == BluetoothProfile.A2DP) {
                    BluetoothA2dp btA2dp = (BluetoothA2dp) proxy;
                    List<BluetoothDevice> a2dpConnectedDevices = btA2dp.getConnectedDevices();
                    if (a2dpConnectedDevices.size() != 0) {
                        for (BluetoothDevice device : a2dpConnectedDevices) {

                            if (appStatusViewModel != null)
                                appStatusViewModel.isAudioConnected.set(true);
//                                    if (device.getName().contains("DEVICE_NAME")) {
//                                        deviceConnected = true;
//                                    }
                        }
                    }
                    bluetoothAdapter.closeProfileProxy(BluetoothProfile.A2DP, btA2dp);
                }
            }

            public void onServiceDisconnected(int profile) {
            }
        };
        bluetoothAdapter.getProfileProxy(this, mProfileListener, BluetoothProfile.A2DP);


        new CommonUtils().consolePairedDevices(bluetoothAdapter);

    }


    private void initAppUpdate() {
//        //未知來源安裝
//        new RxTimer().timer(1000, number -> {
//            if (getPackageManager().canRequestPackageInstalls()) {
//                checkUpdate();
//            } else {
//                Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + getPackageName()));
//                someActivityResultLauncher.launch(intent);
//            }
//        });

        //未知來源安裝檢查
        if (getPackageManager().canRequestPackageInstalls()) {
            checkUpdate();
        } else {
            new RxTimer().timer(3000, number -> {
                Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + getPackageName()));
                someActivityResultLauncher.launch(intent);
            });
        }


        DeviceSettingBean deviceSettingBean = App.getApp().getDeviceSettingBean();
        if (deviceSettingBean.getCurrentVersionCode() == 0) {
            deviceSettingBean.setCurrentVersionCode(new CommonUtils().getLocalVersionCode());
            App.getApp().setDeviceSettingBean(deviceSettingBean);
        }

        //目前的版本 大於 儲存的版本，代表有更新過
        new CallWebApi(MainActivity.this).apiNotifyUpdateConsoleSuccess();
    }

    private void initialize() {

        //  initBluetooth();

        ///storage/emulated/0/Android/data/com.dyaco.spirit_commercial/cache
        UPDATE_FILE_PATH = getExternalCacheDir().getAbsolutePath();


        //ADB /mnt/sdcard/Android/data/com.dyaco.spirit_commercial/files
        //插線 >Android > data > com.dyaco.spirit_commercial
        //Android /storage/emulated/0/Android/data/com.dyaco.spirit_commercial/files
        //   SETTING_FILE_PATH = getExternalFilesDir(null).getAbsolutePath();
        SETTING_FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/CoreStar/Dyaco/Spirit/default_settings.json";
        ///storage/emulated/0/CoreStar/Dyaco/Spirit/default_settings.json

        ///data/user/0/com.dyaco.spirit_commercial/files
        //SETTING_FILE_PATH = getFilesDir().getAbsolutePath();

        //   Log.d("SETTING_FILE", "設定檔路徑: " + SETTING_FILE_PATH);


//        checkUpdate();

        initEvent();

        //   initDelay();

        App.UNIT_E = getApp().getDeviceSettingBean().getUnit_code();

        egymDataViewModel = new ViewModelProvider(this).get(EgymDataViewModel.class);

        workoutViewModel = new ViewModelProvider(this).get(WorkoutViewModel.class);
        getBinding().setWorkoutData(workoutViewModel);

        userProfileViewModel = new ViewModelProvider(this).get(UserProfileViewModel.class);
        getBinding().setUserProfileViewModel(userProfileViewModel);

        appStatusViewModel = new ViewModelProvider(this).get(AppStatusViewModel.class);
        //  appStatusViewModel.wifiState.set(R.drawable.btn_header_wifi_lv0_default);
        getBinding().setAppStatusViewModel(appStatusViewModel);

        getBinding().setComm(new CommonUtils());


        deviceSettingViewModel = new ViewModelProvider(this).get(DeviceSettingViewModel.class);
        getBinding().setDeviceSetting(deviceSettingViewModel);


        //網路監聽
        ConnectionStateMonitor networkCallback = new ConnectionStateMonitor();
        networkCallback.enable(getApplicationContext(), appStatusViewModel);

//        AccurateNetworkMonitor monitor = new AccurateNetworkMonitor(getApplicationContext(), appStatusViewModel);
//        monitor.enable();

        navController = Navigation.findNavController(this, R.id.mainNavigation);
        //  navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.mainNavigation);


        //   xx();

        //   initData();

        isEmulator = SystemProperty.get("ro.kernel.qemu", "0").equals("1");
        //   isEmulator = true;
        //同一個PORT，不能同時存在
        if (getApp().getDeviceSettingBean().getProtocol() == PROTOCOL_CSAFE) {
            removeCab();
            initCSAFE();
        } else {
            removeCSAFE();
            initCab();
        }

        if (getApp().getDeviceSettingBean().getVideo() != VIDEO_NONE) {
            initTvTuner();
        }


//        //懸浮視窗權限
//        if (!Settings.canDrawOverlays(this)) {
//            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result ->
//                    Log.d("@@@@@@@", "onActivityResult: " + result)
//            ).launch(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())));
//        }

        //安裝未知來源權限
//        if (!getPackageManager().canRequestPackageInstalls()) {
//            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
//                        Log.d("安裝未知", "1111安裝未知來源權限: " + result);
//                    }
//            ).launch(new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + getPackageName())));
//        }


        //  Settings.Global.putInt(getContentResolver(), Settings.Global.AUTO_TIME, checked);
//        android.provider.Settings.Secure.putInt(getContentResolver(), Settings.Secure.INSTALL_NON_MARKET_APPS, 0);
        //    android.provider.Settings.Secure.getString(this.getContentResolver(), "bluetooth_address");
//        android.provider.Settings.Secure.getString(this.getContentResolver(), Settings.Secure.INSTALL_NON_MARKET_APPS);
        //   android.provider.Settings.Secure.putInt(getContentResolver(), Settings.Secure.INSTALL_NON_MARKET_APPS,1);
        // Settings.Secure.putInt(getContentResolver(), Settings.Secure.INSTALL_NON_MARKET_APPS, 1);


        initBluetooth();
        initReceiver(); // BITGYM, KINOMAP

//        initAudioDeviceWatcher();
//        unBondAudio();

    }

    private void initAudioDeviceWatcher() {
        //監聽音訊
        mAudioDeviceWatcher = new AudioDeviceWatcher(this);
//        mAudioDeviceWatcher.setLog(true);
        // AudioDeviceWatcher mAudioDeviceWatcher = new AudioDeviceWatcher(this);
        mAudioDeviceWatcher.setDeviceStateListener(new AudioDeviceWatcherListener(appStatusViewModel, this));
    }

    private AudioDeviceWatcher mAudioDeviceWatcher;

    public AudioDeviceWatcher getAudioDeviceWatcher() {
        if (mAudioDeviceWatcher == null) {
            initAudioDeviceWatcher();
        }

        return mAudioDeviceWatcher;
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // There are no request codes
                    //  Intent data = result.getData();
                    checkUpdate();
                    Log.d("安裝未知", "OK安裝未知來源權限: " + result);
                }
            });

    //    public static boolean isGen3PingDone;
    public void initGem3() {

        Log.d("GEM3", "initGem3: ");

        DeviceGEM.GEMEventListener gemEventListener = new DeviceGemEventListener(this, workoutViewModel, appStatusViewModel);
        getDeviceGEM().setEventListener(gemEventListener);
        getDeviceGEM().ping(state -> {
            Log.d("GEM3", "onPing: " + state);
            //全部斷線
            //同時呼叫 可能造成 bluetoothConfigurationMessageGetDeviceAddress 沒成功, 可試試延遲呼叫 << 沒用
            //   getDeviceGEM().customMessageDisconnectHeartRateDevice();

            Log.d("GEM3", "取MAC bluetoothConfigurationMessageGetDeviceAddress: ");
            getDeviceGEM().bluetoothConfigurationMessageGetDeviceAddress();  // >>> onBluetoothConfigurationMessageGetDeviceAddress
            // TODO: 有機會  bluetoothConfigurationMessageGetDeviceAddress 沒回應

            if (state != DeviceGEM.STATE.SUCCESS) {
                Log.e("GEM3", "ping:" + state);
            }
        });

//        isGen3PingDone = false;
//        //ping GYM3
//        if (pingGemTimer != null) {
//            pingGemTimer.cancel();
//            pingGemTimer = null;
//        }
//        pingGemTimer = new RxTimer();
//        pingGemTimer.intervalComplete(1000, 1000, 60, new RxTimer.RxActionComplete() {
//            @Override
//            public void action(long number) {
//                Log.d("GEM3", "systemMessagePing: ");
//                getDeviceGEM().systemMessagePing(); //> onSystemMessagePing
//            }
//
//            @Override
//            public void complete() {
//                if (pingGemTimer != null) {
//                    pingGemTimer.cancel();
//                    pingGemTimer = null;
//                }
//            }
//        });
    }


    /**
     * UART
     */
    private void initUartConsole() {

        uartConsole = new SpiritCommercialUart(workoutViewModel, this, deviceSettingViewModel, appStatusViewModel);
        uartConsole.initialize();
        Log.d("GEM3", "initialize: ");
    }


    public DeviceTvTuner deviceTvTuner;

    public void initTvTuner() {


        if (isEmulator) return;

        if (isUs) return;

        //   Log.d("TvTuner", "Video Type: " + getApp().getDeviceSettingBean().getVideo());
        if (getApp().getDeviceSettingBean().getVideo() != VIDEO_TV) return;

        if (deviceTvTuner != null && isTvOn) {
            return;
        }

        if (deviceTvTuner != null) {
            removeTvTuner();
        }

        try {
            deviceTvTuner = new DeviceTvTuner(getApp()); // 預設PORT_4，伍豐 TvConnection.PORT.PORT_4
            deviceTvTuner.registerListener(new TvTunerDeviceEventListener(deviceTvTuner));

            //    TvTunerDeviceEventListener.isG = false;
            deviceTvTuner.connect();

        } catch (Exception e) {
            deviceTvTuner = null;
            e.printStackTrace();
            Toasty.warning(this, "TV Tuner init Failed", Toasty.LENGTH_LONG).show();
        }
    }

    // DTV_MODE: 數位，ATV_CABLE_MODE: 類比有線，ATV_AIR_MODE: 類比無線
    public void setDeviceTvMode() {
        DeviceSettingBean dsb = getApp().getDeviceSettingBean();
        int signal = dsb.getTvTunerSignal(); //0 Digital Signal, 1 Analog Signal
        int cableAir = dsb.getTvTunerAnalogType(); //0 cable, 1 air

        DeviceTvTuner.TV_MODE tv_mode;
        //數位
        if (signal == TV_TUNER_SIGNAL_DIGITAL) {
            tv_mode = DeviceTvTuner.TV_MODE.DTV_MODE;
        } else {
            if (cableAir == TV_TUNER_ANALOG_CABLE) {//ATV_CABLE_MODE: 類比有線
                tv_mode = DeviceTvTuner.TV_MODE.ATV_CABLE_MODE;
            } else {//ATV_AIR_MODE: 類比無線
                tv_mode = DeviceTvTuner.TV_MODE.ATV_AIR_MODE;
            }
        }


        if (deviceTvTuner != null) {
            deviceTvTuner.setTvMode(tv_mode);
        }

    }

    public void removeTvTuner() {
        if (isEmulator) return;

        isTvOn = false;
        if (deviceTvTuner == null) return;
        deviceTvTuner.disconnect();
        deviceTvTuner.unregisterListener();
        deviceTvTuner = null;
    }

    public void sendCabCommand(DeviceCab.COMMAND command) {
        try {
            if (deviceCab == null) return;
            deviceCab.sendCabCommand(command);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public DeviceCab deviceCab;

    /**
     * CAB - CSAFE PORT一樣
     */
    public void initCab() {
        if (isEmulator) return;
        Log.d("CSAFE", "initCab: ");
        try {
            deviceCab = new DeviceCab(getApp());
            // deviceCab = new DeviceCab(getApp(), UartConnection.PORT.PORT_3);//跟TV TUNER 不同PORT
            deviceCab.registerListener(new CabDeviceEventListener());

            // 連線
            deviceCab.connect();

            //* 指令發送
            // FUNCTIONS 和 SETTINGS 都是 COMMAND
            DeviceCab.COMMAND command = DeviceCab.FUNCTIONS.VOL_DOWN;
            deviceCab.sendCabCommand(command);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeCab() {
        if (deviceCab != null) {

            Log.d("CSAFE", "removeCab: ");
            // 斷線
            deviceCab.disconnect();

            // * 取消監聽介面
            deviceCab.unregisterListener();

            deviceCab = null;
        }
    }

    public DeviceCsafe deviceCsafe;

    public void initCSAFE() {
        if (isEmulator) return;
        try {
            if (deviceCsafe == null) {
                deviceCsafe = new DeviceCsafe(getApp());
                //    deviceCsafe = new DeviceCsafe(getApp(), CsafeConnection.PORT.PORT_3);//跟TV TUNER 不同PORT
                deviceCsafe.registerListener(new CsafeDeviceEventListener());

                // 連線
                deviceCsafe.connect();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeCSAFE() {

        try {
            if (deviceCsafe != null) {
                // 斷線
                deviceCsafe.disconnect();

                // * 取消監聽介面
                deviceCsafe.unregisterListener();

                deviceCsafe = null;
            }
        } catch (Exception e) {
            Log.d("CSAFE", "removeCSAFE: " + e.getLocalizedMessage());
        }
    }

    //
    public void initFTMS() {
        //設定完名稱 > 重啟systemMessageRestart > onSystemEventPowerUp > 開啟廣播

        //設定裝置名稱  > onBluetoothConfigurationMessageSetDeviceName > 開啟廣播 bluetoothControlMessageStartAdvertising

        DeviceGEM.FITNESS_EQUIPMENT_TYPE ftmsEquipmentType = DeviceGEM.FITNESS_EQUIPMENT_TYPE.TREADMILL;
        switch (MODE.getTypeCode()) {
            case DEVICE_TYPE_TREADMILL:
                ftmsEquipmentType = DeviceGEM.FITNESS_EQUIPMENT_TYPE.TREADMILL;
                break;
            case DEVICE_TYPE_ELLIPTICAL:
                ftmsEquipmentType = DeviceGEM.FITNESS_EQUIPMENT_TYPE.CROSS_TRAINER;
                break;
            case DEVICE_TYPE_RECUMBENT_BIKE:
                ftmsEquipmentType = DeviceGEM.FITNESS_EQUIPMENT_TYPE.RECUMBENT_BIKE;
                break;
            case DEVICE_TYPE_UPRIGHT_BIKE:
                ftmsEquipmentType = DeviceGEM.FITNESS_EQUIPMENT_TYPE.BIKE;
                break;
        }


        //設定設備
        //   getDeviceGEM().gymConnectMessageSetFitnessEquipmentType(ftmsEquipmentType);

        String ftmsName = getApp().getDeviceSettingBean().getBle_device_name();

        if (ftmsName == null || ftmsName.isEmpty()) {
            DeviceSettingBean deviceSettingBean = getApp().getDeviceSettingBean();
            deviceSettingBean.setBle_device_name(deviceSettingBean.getModel_name() + "-" + deviceSettingBean.getMachine_mac().replace(":", "").substring(0, 4));
            ftmsName = deviceSettingBean.getBle_device_name();
            getApp().setDeviceSettingBean(deviceSettingBean);
        }

        Log.d("GEM3", "ftms名稱: " + ftmsName);
        // getDeviceGEM().bluetoothConfigurationMessageGetDeviceName(); //onBluetoothConfigurationMessageGetDeviceName

        //設定名稱 onBluetoothConfigurationMessageSetDeviceName
        //     getDeviceGEM().bluetoothConfigurationMessageSetDeviceName(ftmsName);
        // onBluetoothConfigurationMessageSetDeviceName  <<< 有機會沒回應


        GemSettings settings = GemSettingsFactory.getInstance(ftmsEquipmentType);
        settings.setDeviceName(ftmsName);
        settings.setManufacturerName("Dyaco International Inc.");

        Log.d("GEM3", "Start init");
        getDeviceGEM().init(settings, state -> {
            //gem3啟動
            Log.d("GEM3", "onInit state: " + state);
            if (state == DeviceGEM.STATE.SUCCESS) {
                //開啟廣播
                getDeviceGEM().bluetoothControlMessageStartAdvertising();
                appStatusViewModel.isGem3On.set(true);
                LiveEventBus.get(GYM3_ON_EVENT).post(true);

                Log.d("GEM3", "onInit:GEM3開啟成功 ");
            } else {
                //  Toasty.error()
                // TODO: 有機會開啟失敗  state > TIMEOUT  > 但還是可以執行 > TIMEOUT 時間加長一些
                Log.e("GEM3", "onInit:GEM3開啟失敗 ");

                getDeviceGEM().bluetoothControlMessageStartAdvertising();
                appStatusViewModel.isGem3On.set(true);
                LiveEventBus.get(GYM3_ON_EVENT).post(true);
                //        Toasty.error(getApp(),"GEM3" + state.name(),Toasty.LENGTH_LONG).show();

            }
        });

    }


    public HdmiIn hdmiIn;

    public void initHdmi() {
        try {


            hdmiIn = new HdmiIn(this);
            //videoFullViewContainer如果一開始為 INVISIBLE  會壞掉
            hdmiIn.AssignView(getBinding().videoFullViewContainer);

            //   checkHdmiPort();

            //  hdmiIn.ScanHdmiSource();

            hdmiIn.SetDefaultHdmiPort(PORT_EZ_CAST);

            hdmiIn.setListener(new HdmiIn.HdmiInListener() {

                @Override
                public void onScanHdmiSource(boolean b, boolean b1, boolean b2, boolean b3) {
                    deviceSettingViewModel.isEnablePort1.set(b);
                    deviceSettingViewModel.isEnablePort2.set(b1);
                    deviceSettingViewModel.isEnablePort3.set(b2);
                    deviceSettingViewModel.isEnablePort4.set(b3);

                    Log.d("SSSSSTTTTTTT", "onScanHdmiSource: " + b + "," + b1 + "," + b2 + "," + b3);
                }

                @Override
                public void onPortSwitching(int i) {
                }

                @Override
                public void onReady() {
                    //HDMI 被拔掉 ，下 PauseAudio ，不會觸發onReady
                }

                @Override
                public void onActive() {


                    if (appStatusViewModel.currentStatus.get() != STATUS_MAINTENANCE) {
                        //非工程模式使用

                        //switchHdmi時開啟loading onActive 關閉loading
                        showLoading(false);

                        if (isReAssignView) {
                            isReAssignView = false;
                            hdmiIn.pauseVideo();
                            return;
                        }

                        //    if (appStatusViewModel.currentPage.get() != CURRENT_PAGE_MEDIA) {
                        if (!isTvSUC) {
                            //非點選hdmi按鈕啟動
                            getBinding().videoFullViewContainer.setVisibility(View.INVISIBLE);
                            hdmiIn.pauseVideo();
                            return;
                        }

                        getBinding().videoFullViewContainer.setVisibility(View.VISIBLE);
                        hdmiIn.resumeAudio();
                    } else {
                        //工程模式使用

                        //AssignView 時觸發
                        if (isReAssignView) {
                            isReAssignView = false;
                            hdmiIn.pauseVideo();
                            return;
                        }
//
//                      //  hdmiIn.AssignView(getBinding().videoFullViewContainer2);
                        hdmiIn.resumeAudio();
                    }
                }

                @Override
                public void onPortSwitched(int i) {
                    isReAssignView = false;
                }

                @Override
                public void onPauseVideo() {
                    hdmiIn.pauseAudio();
                    getBinding().videoFullViewContainer.setVisibility(View.INVISIBLE);
                    showLoadingEx(false, 500);
                }

                @Override
                public void onResumeVideo() {
                }

                @Override
                public void onPauseAudio() {
                }

                @Override
                public void onResumeAudio() {
                }
            });


            new RxTimer().timer(1000, q -> {
                hdmiIn.Open(); // > onReady
                getBinding().videoFullViewContainer.setVisibility(View.INVISIBLE);
            });

            //hdmiIn.Open() 之後 ，才顯示TextureView，無效


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


//    public void checkHdmiPort() {
//        if (hdmiIn == null) {
//            Toasty.warning(this, "HDMI ERROR", Toasty.LENGTH_LONG).show();
//            return;
//        }
//        hdmiIn.ScanHdmiSource();
//
//        deviceSettingViewModel.isEnablePort1.set(hdmiIn.CheckHdmiSource(PORT_TV_TUNER));
//        deviceSettingViewModel.isEnablePort2.set(hdmiIn.CheckHdmiSource(PORT_EZ_CAST));
//        deviceSettingViewModel.isEnablePort3.set(hdmiIn.CheckHdmiSource(PORT_WIRE_CAST));
//        deviceSettingViewModel.isEnablePort4.set(hdmiIn.CheckHdmiSource(PORT_MIRA_CAST));
//    }

    public void closeHdmi() {


        getBinding().vHdmiBg.setVisibility(View.GONE);
        getBinding().videoFullViewContainer.setVisibility(View.INVISIBLE);


        if (hdmiIn != null) {
            showLoadingEx(true, 500);//onPauseVideo 關閉
            try {
                hdmiIn.pauseVideo(); // onPauseVideo > pauseAudio > onPauseAudio
            } catch (Exception e) {
                e.printStackTrace();
            }

            //  Fatal Exception: java.lang.IllegalStateException: Session has been closed; further changes are illegal.
            //       at android.hardware.camera2.impl.CameraCaptureSessionImpl.checkNotClosed(CameraCaptureSessionImpl.java:886)
            //       at android.hardware.camera2.impl.CameraCaptureSessionImpl.stopRepeating(CameraCaptureSessionImpl.java:417)
            //       at com.fec.hdmiin.HdmiIn.pauseVideo(HdmiIn.java:836)

            new RxTimer().timer(2000, x -> {
                //故障的話不會觸發onPauseVideo
                if (loadingWindowEx != null) {
                    loadingWindowEx.dismiss();
                    loadingWindowEx = null;
                }
            });
        }

    }

    /**
     * 開同PORT: onScanHdmiSource > onPortSwitching > onResumeVideo > onActive > onResumeAudio > onPortSwitched
     * 開不同PORT: onScanHdmiSource > onPortSwitching > onReady > onActive > onResumeAudio > onPortSwitched > onResumeVideo
     */
    public void switchHdmi(int source) {


        if (hdmiIn.IsSwitching()) {
            return;
        }

        if (hdmiIn.SwitchHdmiSource(source)) {

            isTvSUC = true;

            hdmiIn.resumeVideo();

            mediaType = MEDIA_TYPE_HDMI;

            closePackage(this);

            internetNotifyWarringWindow(false);

            showMediaFloatingDashboard(true, true);


            //switchHdmi時開啟loading onActive 關閉loading
            //****把MediaMenu蓋住****
            showLoading(false);
            showMediaMenu(true);
            new RxTimer().timer(300, number -> showLoading(true));
            //****把MediaMenu蓋住*****

            getBinding().vHdmiBg.setVisibility(View.VISIBLE);


            new RxTimer().timer(4000, number -> {
                try {
                    //switchHdmi時開啟loading onActive 關閉loading
                    if (loadingWindow != null && loadingWindow.isShowing()) {
                        showLoading(false);
//                        if (hdmiIn != null) {
//                            hdmiIn.pauseVideo();
//                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });


        } else {
            showLoading(false);
            //   showLoadingEx(false);
            closeHdmi();
        }

        //    showLoading(false);
//        getBinding().videoFullViewContainer.setVisibility(View.VISIBLE);

    }


    private boolean verifyLocationServices() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager != null && (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
    }


    public DeviceManager mGarminDeviceManager;
    //public GarminDeviceFilter mGarminDeviceFilter;


    private int gRetry;

    /**
     * Garmin
     */
    public void initGarmin() {

        if (GarminHealth.isInitialized()) {
            GarminHealth.restart(getApp());
            Log.d(GARMIN_TAG, "garmin restart.");
            deviceSettingViewModel.isGarminEnabled.set(true);
        } else {
            Log.d(GARMIN_TAG, "init garmin sdk.");
            try {
                Futures.addCallback(HealthSDKManager.initializeHealthSDK(this), new FutureCallback<Boolean>() {
                    @Override
                    public void onSuccess(@Nullable Boolean result) {

                        if (result != null && result) {

                            mGarminDeviceManager = DeviceManager.getDeviceManager();

                            //Garmin連線狀態Listener
                            mGarminDeviceManager.addConnectionStateListener(new GarminDeviceConnectionStateListener(MainActivity.this, workoutViewModel, appStatusViewModel,
                                    new GarminRealTimeDataListener(workoutViewModel)));

                            //Garmin配對狀態Listener
                            mGarminDeviceManager.addPairedStateListener(new GarminPairedStateListener(getApp(), workoutViewModel));

                            mGarminDeviceManager.addRealTimeDataListener(new GarminRealTimeDataListener(workoutViewModel), EnumSet.allOf(RealTimeDataType.class));

                            Log.d(GARMIN_TAG, "init garmin service success.." + result);

                            deviceSettingViewModel.isGarminEnabled.set(true);

                            garminUnPair();
                        } else {
                            Log.d(GARMIN_TAG, "init garmin service error.." + result);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Throwable t) {
                        deviceSettingViewModel.isGarminEnabled.set(false);

                        if (gRetry < 46) {
                            initGarmin();
                        }
                        Log.d(GARMIN_TAG, "init garmin service failed." + t.getMessage());
                        gRetry += 1;
                        Log.d(GARMIN_TAG, "onFailure:  RETRY:" + gRetry);
                    }
                }, Executors.newSingleThreadExecutor());
            } catch (GarminHealthInitializationException e) {
                deviceSettingViewModel.isGarminEnabled.set(false);
                Log.e(GARMIN_TAG, "Garmin Health initialization failed.", e);
            }
        }
    }

    /**
     * 開啟RealTimeData
     *
     * @param device d
     */
    public void enableRealTimeData(com.garmin.health.Device device) {
        try {
            workoutViewModel.isGarminConnected.set(true);
            currentGarminAddress = device.address(); //目前RealTimeData的Garmin裝置
            stopListening();

            new RxTimer().timer(200, number -> {
                DeviceManager.getDeviceManager().enableRealTimeData(device.address(), EnumSet.allOf(RealTimeDataType.class));
                currentGarminAddress = device.address(); //目前RealTimeData的Garmin裝置
                Log.d(GARMIN_TAG, "切換 開啟RealTimeData:" + device.address() + "," + currentGarminAddress);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopListening() {
        if (DeviceManager.getDeviceManager().getPairedDevices() == null) return;
        for (com.garmin.health.Device device : DeviceManager.getDeviceManager().getPairedDevices()) {
            Log.d("GARMINNN", "關閉: " + device.address());
            DeviceManager.getDeviceManager().disableRealTimeData(device.address(), EnumSet.allOf(RealTimeDataType.class));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
//        if (GarminHealth.isInitialized()) {
//            // Restart the Health SDK in the current application space.
//            GarminHealth.restart();
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        //    isForeground = false;
        //    stopHandler();
    }

    public void closeWindow() {
        if (backgroundWindow != null) {
            backgroundWindow.dismiss();
            backgroundWindow = null;
        }

        if (popupWindow != null) {
            popupWindow.dismiss();
            popupWindow = null;
        }

        if (loadingWindowEx != null) {
            loadingWindowEx.dismiss();
            loadingWindowEx = null;
        }


//        if (updateRestartWindow != null) {
//            updateRestartWindow.dismiss();
//            updateRestartWindow = null;
//        }
    }

    /**
     * 外部控制(FTMS、實體按鍵)
     */
    private void initExControl() {

        //FTMS快速啟動
        LiveEventBus.get(FTMS_START_OR_RESUME).observe(this, s -> {
            if (SETTING_SHOW) return;
            if (isGarminWindowOn) return;
            if (isGuestQuickStart) return;
            //   if (appStatusViewModel.currentStatus.get() == AppStatusIntDef.STATUS_IDLE || appStatusViewModel.currentStatus.get() == AppStatusIntDef.STATUS_SUMMARY) {
            if (appStatusViewModel.currentStatus.get() == AppStatusIntDef.STATUS_IDLE && !isSummary) {
                quickStart(true);
            }
        });
    }

    /**
     * 快速啟動 直接跑Manual 上數
     */
    public static boolean CHECK_START = false;

    public void quickStart(boolean isBuzzer) {

        //  uartConsole.setBuzzer();
        //MANUAL
        int time = UNLIMITED;

        workoutViewModel.setSelProgram(ProgramsEnum.MANUAL);

        DeviceSettingBean d = getApp().getDeviceSettingBean();
        if (d.getIsUseTimeLimit() == ON) {
            time = d.getUseTimeLimit() == 0 ? DEFAULT_USE_TIME_LIMIT : (int) d.getUseTimeLimit();
        }

        Log.d("PPEPPEPEPEPF", "quickStart: " + time);

        workoutViewModel.selWorkoutTime.set(time);

        startWorkout(isBuzzer);
    }

    public void startWorkout(boolean isBuzzer) {
        if (SETTING_SHOW) return;

        if (CHECK_START) return;

        if (loadingWindow != null && loadingWindow.isShowing()) return;

        //不是快速啟動
        if (isTreadmill) {
            Log.d("UART_CONSOLE", "Treadmill startWorkout: " + uartConsole.getDevStep() + "," + isEmulator);
            if (uartConsole.getDevStep() != DS_IDLE_STANDBY && !isEmulator) {
                CustomToast.showToast(this, getString(R.string.start_waiting));
                return;
            }
        } else {
            Log.d("UART_CONSOLE", "startWorkout: " + uartConsole.getDevStep());
            if (uartConsole.getDevStep() != DS_EMS_IDLE_STANDBY && !isEmulator) {
                CustomToast.showToast(this, getString(R.string.start_waiting_bike));
                return;
            }
        }

        CHECK_START = true;

        internetNotifyWarringWindow(false);
        hrNotifyWarringWindow(false);
        btNotifyWindow(false);

        LiveEventBus.get(EventKey.EVENT_CLOSE_SUMMARY).post(true);
        closeWindow();

        if (isBuzzer) uartConsole.setBuzzer();

        if (isTreadmill) {
            ReadyStartPopupWindow readyStartPopupWindow = new ReadyStartPopupWindow(this);
            readyStartPopupWindow.showAtLocation(getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);
            readyStartPopupWindow.setOnCustomDismissListener(new BasePopupWindow.OnCustomDismissListener() {
                @Override
                public void onStartDismiss(MsgEvent value) {
                }

                @Override
                public void onDismiss() {
                    CHECK_START = false;
                }
            });
        } else {

            showGoBackground(true);
            //   getBinding().vBackground.setVisibility(View.VISIBLE);
            appStatusViewModel.currentStatus.set(STATUS_RUNNING);
            LiveEventBus.get(START_WORKOUT).post("");
            CHECK_START = false;
        }

        if (isGuestQuickStart) {
            isGuestQuickStart = false;
            new RxTimer().timer(200, number -> showLoadingAllB(false));
        }


        LiveEventBus.get(SHOW_PANELS).post(true);

    }


    NotifyWarringWindow galaxyWindow;

    public void showGalaxyWindow(boolean isShow) {

        if (galaxyWindow != null) {
            galaxyWindow.dismiss();
            galaxyWindow = null;
        }

        if (isShow) {

            String msg = getString(R.string.Galaxy_Watch_disconnected);
            int res = R.drawable.btn_notify_warring_left;
            if (getBinding() == null) return;
            View view = getBinding().btnSamsungWatch;

            galaxyWindow = new NotifyWarringWindow(this, msg, res);
            galaxyWindow.getContentView().measure(makeDropDownMeasureSpec(galaxyWindow.getWidth()), makeDropDownMeasureSpec(galaxyWindow.getHeight()));
            //左邊
            int offsetX = 10;
            int offsetY = -6;
            int gravity = Gravity.START;

            PopupWindowCompat.showAsDropDown(galaxyWindow, view, offsetX, offsetY, gravity);
            galaxyWindow.setOnCustomDismissListener(new BasePopupMsgWindow.OnCustomDismissListener() {
                @Override
                public void onStartDismiss(MsgEvent value) {
                }

                @Override
                public void onDismiss() {
                    galaxyWindow = null;
                }
            });

        }
    }

    private void initEvent() {

        LiveEventBus.get(SHOW_SAMSUNG_DISCONNECTED, Boolean.class).observe(this, s -> {
            showGalaxyWindow(s);
        });


        LiveEventBus.get(EventKey.RETAIL_SHOW, Boolean.class).observe(this, this::showRetail);


        LiveEventBus.get(EventKey.MAINTENANCE_UPDATE_LANGUAGE_2, Locale.class).observe(this, locale -> {
            if (getBinding() != null) {
                try {
                    webApiLogout();
                    int t = isTreadmill ? 1000 : 500;
//                    new RxTimer().timer(t, x -> LanguageUtils.updateLanguage(locale));
                    new RxTimer().timer(t, x -> LanguageUtils.changeLanguageSetting(locale));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        LiveEventBus.get(EVENT_WORK_NOTIFY_UPDATE, Boolean.class).observe(this, s ->
                showUpdateRestartWindow());

        //網路狀態
        LiveEventBus.get(WIFI_WORK, Boolean.class).observe(this, s ->
                internetNotifyWarringWindow(!s));


        getBinding().ibWifi.setOnClickListener(v -> {
            if (CheckDoubleClick.isFastClick()) return;
            internetNotifyWarringWindow(false);

            if (appStatusViewModel.currentStatus.get() == AppStatusIntDef.STATUS_SUMMARY || isSummary) {
                LiveEventBus.get(EventKey.EVENT_CLOSE_SUMMARY).post(true);
            }
            new FloatingWidget(MainActivity.this).callSetting(0, MainActivity.class, 1);
            //       showWifiWindow();
        });


        //    DebounceClick.attach(getBinding().ibSetting, v -> showWifiWindow());

        getBinding().ibSetting.setOnClickListener(v -> {

            if (CheckDoubleClick.isFastClick()) return;
            alphaFloatingBack(true);
            popupWindow = new SettingPopupWindow(MainActivity.this, deviceSettingViewModel, workoutViewModel);
            popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);
            ((SettingPopupWindow) popupWindow).setOnCustomDismissListener(new BasePopupWindow.OnCustomDismissListener() {
                @Override
                public void onStartDismiss(MsgEvent value) {
                    alphaFloatingBack(false);
                    popupWindow = null;
                }

                @Override
                public void onDismiss() {
                }
            });

        });

        getBinding().ibQrCode.setOnClickListener(v -> {
//            isEmulator = true;
//            isTreadmill = !isTreadmill;
//            Toasty.success(getApp(),"電跑：" + isTreadmill,Toasty.LENGTH_SHORT).show();

            if (CheckDoubleClick.isFastClick()) return;
            alphaFloatingBack(true);

            popupWindow = new QrCodePopupWindow(MainActivity.this);
            popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);
            ((QrCodePopupWindow) popupWindow).setOnCustomDismissListener(new BasePopupWindow.OnCustomDismissListener() {
                @Override
                public void onStartDismiss(MsgEvent value) {
                    alphaFloatingBack(false);
                }

                @Override
                public void onDismiss() {
                    popupWindow = null;
                }
            });

        });

//        getBinding().ibAlert.setOnClickListener(v -> {
//            if (CheckDoubleClick.isFastClick()) return;
//            if (userProfileViewModel.userType.get() == USER_TYPE_GUEST) return;
//
//            alphaFloatingBack(true);
//            popupWindow = new InboxPopupWindow(MainActivity.this, deviceSettingViewModel);
//            popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);
//            ((InboxPopupWindow) popupWindow).setOnCustomDismissListener(new BasePopupWindow.OnCustomDismissListener() {
//                @Override
//                public void onStartDismiss(MsgEvent value) {
//                    alphaFloatingBack(false);
//                }
//
//                @Override
//                public void onDismiss() {
//                    popupWindow = null;
//                }
//            });
//
//        });


        //登出
        DebounceClick.attach(getBinding().btnLogout, v -> {
            webApiLogout();
        });

        getBinding().btnAppleWatch.setOnClickListener(view -> showAppleWatch());
        //  getBinding().ivAppleWatchEnabled.setOnClickListener(view -> showAppleWatch());


        // TODO: Samsung
        getBinding().btnSamsungWatch.setOnClickListener(view -> showSamsungWatch());
        // getBinding().ivSamsungWatchEnabled.setOnClickListener(view -> showSamsungWatch());


        getBinding().ivGarmin.setOnClickListener(v -> showGarmin());
        getBinding().tvStress.setOnClickListener(v -> showGarmin());
        getBinding().tvStressUnit.setOnClickListener(v -> showGarmin());
        getBinding().tvSpo2.setOnClickListener(v -> showGarmin());
        getBinding().tvSpo2Unit.setOnClickListener(v -> showGarmin());

        getBinding().btnGarmin.setOnClickListener(v -> showGarmin());

        getBinding().btnHR.setOnClickListener(v -> {

            if (CheckDoubleClick.isFastClick()) return;

//            if (!appStatusViewModel.isGem3On()){
//                CustomToast.showToast(this,"GEM3 IS NOT ENABLED");
//                return;
//            }

            alphaFloatingBack(true);
            popupWindow = new HRPopupWindow(this);
            popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);
            ((HRPopupWindow) popupWindow).setOnCustomDismissListener(new BasePopupWindow.OnCustomDismissListener() {
                @Override
                public void onStartDismiss(MsgEvent value) {
                    alphaFloatingBack(false);
                }

                @Override
                public void onDismiss() {
                    popupWindow = null;
                }
            });
        });

        getBinding().btnBT.setOnClickListener(v -> {
            if (CheckDoubleClick.isFastClick()) return;

            //Summary開啟中按，關閉summary
//            if (appStatusViewModel.currentStatus.get() == AppStatusIntDef.STATUS_SUMMARY || isSummary) {
//                LiveEventBus.get(EventKey.EVENT_CLOSE_SUMMARY).post(true);
//            }
//            new FloatingWidget(MainActivity.this).callSetting(1, MainActivity.class, 1);

            if (appStatusViewModel.currentStatus.get() == AppStatusIntDef.STATUS_SUMMARY || isSummary) {
                LiveEventBus.get(EventKey.EVENT_CLOSE_SUMMARY).post(true);
            }

            alphaFloatingBack(true);
            popupWindow = new BtAudioWindow(this, true);
            popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);
            ((BtAudioWindow) popupWindow).setOnCustomDismissListener(new BasePopupWindow.OnCustomDismissListener() {
                @Override
                public void onStartDismiss(MsgEvent value) {
                    alphaFloatingBack(false);
                }

                @Override
                public void onDismiss() {
                    popupWindow = null;
                }
            });
        });

        getBinding().btnSound.setOnClickListener(v -> {

            if (CheckDoubleClick.isFastClick()) return;
            alphaFloatingBack(true);
            popupWindow = new SoundPopupWindow(this);
            popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);
            ((SoundPopupWindow) popupWindow).setOnCustomDismissListener(new BasePopupWindow.OnCustomDismissListener() {
                @Override
                public void onStartDismiss(MsgEvent value) {
                    alphaFloatingBack(false);
                }

                @Override
                public void onDismiss() {
                    popupWindow = null;
                }
            });
        });

//        getBinding().btnFan.setOnClickListener(view -> {
//
//            currentFan = currentFan == STOP ? STRONG : STOP;
//
//            setFan(currentFan);
//        });

        getBinding().btnMemberProfile.setOnClickListener(v -> {
            if (CheckDoubleClick.isFastClick()) return;
            if (userProfileViewModel.userType.get() != USER_TYPE_NORMAL && userProfileViewModel.userType.get() != USER_TYPE_VIP)
                return;
            alphaFloatingBack(true);

            popupWindow = new MemberProfileWindow(this, deviceSettingViewModel);
            popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);
            ((MemberProfileWindow) popupWindow).setOnCustomDismissListener(new BasePopupWindow.OnCustomDismissListener() {
                @Override
                public void onStartDismiss(MsgEvent value) {

                    alphaFloatingBack(false);

//                    getWindow().getDecorView().setSystemUiVisibility(
//                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
//                                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
//                                    View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                }

                @Override
                public void onDismiss() {
                    popupWindow = null;
                }
            });
        });
    }

    public void showWifiQrcodeWindow() {
        if (CheckDoubleClick.isFastClick()) return;
        alphaFloatingBack(true);
        popupWindow = new WifiQrCodePopupWindow(MainActivity.this);
        popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);
        ((WifiQrCodePopupWindow) popupWindow).setOnCustomDismissListener(new BasePopupWindow.OnCustomDismissListener() {
            @Override
            public void onStartDismiss(MsgEvent value) {
                alphaFloatingBack(false);
            }

            @Override
            public void onDismiss() {
                popupWindow = null;
            }
        });
    }

    public void showWifiWindow() {
        alphaFloatingBack(true);
        popupWindow = new WifiWindow(MainActivity.this, false);
        popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);
        ((WifiWindow) popupWindow).setOnCustomDismissListener(new BasePopupWindow.OnCustomDismissListener() {
            @Override
            public void onStartDismiss(MsgEvent value) {
                alphaFloatingBack(false);
            }

            @Override
            public void onDismiss() {
                popupWindow = null;
            }
        });
    }

    public void showAppleWatch() {
        if (CheckDoubleClick.isFastClick()) return;

//        workoutViewModel.isAppleWatchEnabled.set(true);
//        workoutViewModel.isAppleWatchConnected.set(true);


        alphaFloatingBack(true);
        popupWindow = new AppleWatchWindow(MainActivity.this);
        popupWindow.setAnimationStyle(R.style.PopupAnimationApple);
        popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);
        ((AppleWatchWindow) popupWindow).setOnCustomDismissListener(new BasePopupWindow.OnCustomDismissListener() {
            @Override
            public void onStartDismiss(MsgEvent value) {
                //  getDeviceGEM().nfcMessageDisableNfcRadio();
                alphaFloatingBack(false);
            }

            @Override
            public void onDismiss() {
                popupWindow = null;
            }
        });
    }


    public void showSamsungWatch() {
        if (CheckDoubleClick.isFastClick()) return;

        alphaFloatingBack(true);
        popupWindow = new SamsungWatchWindow(MainActivity.this);
        popupWindow.setAnimationStyle(R.style.PopupAnimationApple);
        popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);
        ((SamsungWatchWindow) popupWindow).setOnCustomDismissListener(new BasePopupWindow.OnCustomDismissListener() {
            @Override
            public void onStartDismiss(MsgEvent value) {
                //  getDeviceGEM().nfcMessageDisableNfcRadio();
                alphaFloatingBack(false);
            }

            @Override
            public void onDismiss() {
                popupWindow = null;
            }
        });
    }

    public void goMaintenanceMode() {

        MainActivity.isStopLogin = true;

//        if (uartConsole.getDevStep() != DS_IDLE_STANDBY && !isEmulator) {
//            Toasty.warning(this, "NOT READY PLEASE TRY AGAIN", Toasty.LENGTH_LONG).show();
//            return;
//        }

        NavOptions navOptions = new NavOptions.Builder()
                .setEnterAnim(android.R.anim.fade_in)
                .setExitAnim(android.R.anim.fade_out)
                .build();

        try {
            navController.navigate(R.id.navigation_maintenance, null, navOptions);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void showGarmin() {

        if (CheckDoubleClick.isFastClick()) return;

//        workoutViewModel.isAppleWatchEnabled.set(false);
//        workoutViewModel.isAppleWatchConnected.set(false);

        if (appStatusViewModel.currentStatus.get() == AppStatusIntDef.STATUS_SUMMARY || isSummary) {
            LiveEventBus.get(EventKey.EVENT_CLOSE_SUMMARY).post(true);
        }

        alphaFloatingBack(true);
        popupWindow = new GarminDevicesWindow(this, workoutViewModel, appStatusViewModel);
        popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);
        ((GarminDevicesWindow) popupWindow).setOnCustomDismissListener(new BasePopupWindow.OnCustomDismissListener() {
            @Override
            public void onStartDismiss(MsgEvent value) {
                alphaFloatingBack(false);

//                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//                getWindow().getDecorView().setSystemUiVisibility(
//                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
//                                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
//                                View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }

            @Override
            public void onDismiss() {
                popupWindow = null;
            }
        });
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        //onCreate->onStart->onResume->onAttachedToWindow
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    /**
     * Media開啟時的黑背景
     *
     * @param isShow isShow
     */
    public void showMediaAlphaBackground(Boolean isShow) {
        if (isShow) {
            if (appStatusViewModel.isMediaPlaying.get()) {
                backgroundWindow = new BackgroundWindow(this);
                backgroundWindow.showAtLocation(getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);
            }
        } else {

            //多媒體閃退，會關不掉
            //    if (appStatusViewModel.isMediaPlaying.get()) {
            if (backgroundWindow != null)
                backgroundWindow.dismiss();
            //     }
        }
    }

    /**
     * Media開啟時的黑背景
     */
    BackgroundWindow goWindow;

    public void showGoBackground(Boolean isShow) {
        try {
            if (isShow) {
                if (goWindow != null) {
                    goWindow.dismiss();
                    goWindow = null;
                }
                goWindow = new BackgroundWindow(this);
                goWindow.showAtLocation(getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);
            } else {
                if (goWindow != null) {
                    goWindow.dismiss();
                    goWindow = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //  Log.d("RPM_CHECK", "dispatchTouchEvent: ");
        if (popupWindow != null && popupWindow.isShowing()) return false;

        return super.dispatchTouchEvent(ev);
    }

    private void alphaFloatingBack(Boolean isShow) {
//        if ((floatingBottomMenuWindow != null && floatingBottomMenuWindow.isShowing()) && (floatingTopDashBoardWindow != null && floatingTopDashBoardWindow.isShowing())) {
//            LiveEventBus.get(ALPHA_FLOATING_MEDIA_BACKGROUND, Boolean.class).post(isShow);
//        }

        showMediaAlphaBackground(isShow);
    }

    MediaMenuWindow2 mediaMenuWindow;

    /**
     * @param isShow s
     */
    public void showMediaMenu(boolean isShow) {

        if (isShow) {
            //  if (mediaMenuWindow != null && mediaMenuWindow.isShowing()) return;
            if (mediaMenuWindow != null) return;
            mediaMenuWindow = new MediaMenuWindow2(this, appStatusViewModel);
            mediaMenuWindow.setOnCustomDismissListener(value -> mediaMenuWindow = null);
            //  PopNoRecordProxy.instance().noScreenRecord(mediaMenuWindow);
//            mediaMenuWindow.showAtLocation(getWindow().getDecorView(), Gravity.START | Gravity.TOP, 50, 80);
//            mediaMenuWindow.setOnCustomDismissListener(new BasePopupWindow.OnCustomDismissListener() {
//                @Override
//                public void onStartDismiss(MsgEvent value) {
//                }
//
//                @Override
//                public void onDismiss() {
//                    mediaMenuWindow = null;
//                }
//            });

        } else {
            if (mediaMenuWindow != null) {
                mediaMenuWindow.dismiss();
                mediaMenuWindow = null;
            }
        }

    }

    public FloatingBottomMenuWindow2 floatingBottomMenuWindow;
    public FloatingTopDashBoardWindow2 floatingTopDashBoardWindow;

    public void showMediaFloatingDashboard(boolean isShow, boolean isEnabled) {

        if (floatingBottomMenuWindow != null) {
            floatingBottomMenuWindow.setVisibleFade(isShow);
        } else {
            floatingBottomMenuWindow = new FloatingBottomMenuWindow2(this, deviceSettingViewModel, isEnabled);
        }

        if (floatingTopDashBoardWindow != null) {
            floatingTopDashBoardWindow.setVisibleFade(isShow);
        } else {
            floatingTopDashBoardWindow = new FloatingTopDashBoardWindow2(this, appStatusViewModel, workoutViewModel, deviceSettingViewModel, userProfileViewModel, egymDataViewModel, isEnabled);
        }

//        if (isShow) {
//
//
//            if (floatingTopDashBoardWindow != null) {
//                floatingTopDashBoardWindow.dismiss();
//                floatingTopDashBoardWindow = null;
//            }

//            floatingTopDashBoardWindow = new FloatingTopDashBoardWindow(this, appStatusViewModel, workoutViewModel, deviceSettingViewModel, userProfileViewModel, isEnabled);
//            floatingTopDashBoardWindow.showAtLocation(getWindow().getDecorView(), Gravity.START | Gravity.TOP, 0, 0);
//            floatingTopDashBoardWindow.setOnCustomDismissListener(new BasePopupWindow.OnCustomDismissListener() {
//                @Override
//                public void onStartDismiss(MsgEvent value) {
//                }
//
//                @Override
//                public void onDismiss() {
//                    //      floatingTopDashBoardWindow = null;
//                    //   LiveEventBus.get(MEDIA_GO_TO_TRAINING).post("");
//                }
//            });


        //   }
//        } else {
//            if (floatingTopDashBoardWindow != null) {
//                floatingTopDashBoardWindow.dismiss();
//                floatingTopDashBoardWindow = null;
//            }
//        }
    }

    public YouTubeWindow youTubeWindow;

    public void showYouTube(boolean isShow, String webUrl) {
        if (isShow) {
            youTubeWindow = new YouTubeWindow(this, webUrl);
            youTubeWindow.showAtLocation(getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);

        } else {
            LiveEventBus.get(CLOSE_YOUTUBE_FULL_SCREEN).post(true);
            if (youTubeWindow != null) {
                youTubeWindow.dismiss();
                youTubeWindow = null;
            }
        }
    }


//    private void a() {
//        registerForActivityResult(new ActivityResultContracts.RequestPermission(),
//                new ActivityResultCallback<Boolean>() {
//            @Override
//            public void onActivityResult(Boolean result) {
//
//            }
//        }).launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
//    }
//
//    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
//            new ActivityResultCallback<Uri>() {
//                @Override
//                public void onActivityResult(Uri uri) {
//                    // Handle the returned Uri
//                }
//            });


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (internetNotifyWarringWindow != null) {
            internetNotifyWarringWindow.dismiss();
            internetNotifyWarringWindow = null;
        }

        try {
            SpiritDbManager.getInstance(getApplicationContext()).close();
            FitnessTestDbManager.getInstance(getApplicationContext()).close();

            if (GarminHealth.isInitialized()) {
                // Stop the Health SDK to pause or close resource currently running in the application space.
                GarminHealth.stop(getApp());

                Log.d("GARMIN", "onDestroy: GarminHealth.stop()");
            }

            showMediaMenu(false);

            //>>> BITGYM, KINOMAP
            if (mDyacoDataReceiver != null) {
                Log.d("GYM_APP", "stopReceiveCtrl & stopReceiveInfo");

                mDyacoDataReceiver.stopReceiveCtrl(getApp());
                mDyacoDataReceiver.stopReceiveInfo(getApp());
            }
            //<<<

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SystemErrorWindow errorMsgWindow;

    public void showErrorMsg(ErrorInfo errorInfo, boolean isShow) {

        if (isShow) {
            if (errorMsgWindow != null) return;

//            if(uartConsole.checkErrorShowed(errorInfo.getUartErrorType())) return;
//
//            uartConsole.updateErrorMapValue(errorInfo.getUartErrorType());


            errorMsgWindow = new SystemErrorWindow(this, errorInfo);
            errorMsgWindow.showAtLocation(getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);
            errorMsgWindow.setOnCustomDismissListener(new BasePopupWindow.OnCustomDismissListener() {
                @Override
                public void onStartDismiss(MsgEvent value) {
                }

                @Override
                public void onDismiss() {
                    // EMS_MCU_ERROR
                    if (errorInfo.getUartErrorType() == UE_MCU_EMS) {
                        webApiLogout();
                    } else {
                        errorMsgWindow = null;
                    }
                }
            });
        } else {
            if (errorMsgWindow != null) {
                errorMsgWindow.dismiss();
                errorMsgWindow = null;
            }
        }
    }

    public SafetyKeyWindow safetyKeyWindow;

    //安全鎖
    public void showSafeKeyWarring(boolean isShow) {

        workoutViewModel.isSafeKey.set(!isShow);

        if (isShow) {
            if (safetyKeyWindow != null && safetyKeyWindow.isShowing()) return;

            //   getBinding().vBackground.setVisibility(View.VISIBLE);
            //  clearPopupWindow();

            safetyKeyWindow = new SafetyKeyWindow(this);
            safetyKeyWindow.showAtLocation(getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);


//            safetyKeyWindow.setOnCustomDismissListener(new BasePopupWindow.OnCustomDismissListener() {
//                @Override
//                public void onStartDismiss(MsgEvent value) {
////                    Intent launchIntent = new Intent(MainActivity.this, MainActivity.class);
////                    launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                    startActivity(launchIntent);
//                    Log.d("UART_CONSOLE", "onStartDismiss: ");
//
////                    //回到首頁
////                    SSEB = false;
////                    finishAffinity();
////                    Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(getApplication().getPackageName());
////                    LaunchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
////                    startActivity(LaunchIntent);
//                }
//
//                @Override
//                public void onDismiss() {
//                }
            //           });
        } else {
            if (safetyKeyWindow != null) {
                safetyKeyWindow.dismiss();
                safetyKeyWindow = null;
            }
        }
    }

    public void showNotifyWindow(int drawableRes, View view, float value, float maxValue, boolean isFloat, int barType) {

        try {
            if (appStatusViewModel.currentStatus.get() != STATUS_RUNNING) return;

            NotifyNumWindow notifyNumWindow = new NotifyNumWindow(this, value, maxValue, drawableRes, isFloat, (isTreadmill && workoutViewModel.selProgram == ProgramsEnum.HEART_RATE), barType);
            notifyNumWindow.getContentView().measure(makeDropDownMeasureSpec(notifyNumWindow.getWidth()), makeDropDownMeasureSpec(notifyNumWindow.getHeight()));

            int offsetX = 0;
            int offsetY = 0;
            int gravity = 0;
            if (drawableRes == R.drawable.bg_control_left_increase || drawableRes == R.drawable.bg_control_left_decrease) {
                offsetX = -10;
                offsetY = -(notifyNumWindow.getContentView().getMeasuredHeight() + view.getHeight()) / 2;
                gravity = Gravity.END;
            } else if (drawableRes == R.drawable.bg_control_right_increase || drawableRes == R.drawable.bg_control_right_decrease) {
                offsetX = -notifyNumWindow.getContentView().getMeasuredWidth() + 10;
                offsetY = -(notifyNumWindow.getContentView().getMeasuredHeight() + view.getHeight()) / 2;
                gravity = Gravity.START;
            } else if (drawableRes == R.drawable.bg_control_center_increase || drawableRes == R.drawable.bg_control_center_decrease) {
                offsetX = -Math.abs(notifyNumWindow.getContentView().getMeasuredWidth() - view.getWidth()) / 2;
                //   offsetY = 0;
                gravity = Gravity.START;
            }

            PopupWindowCompat.showAsDropDown(notifyNumWindow, view, offsetX, offsetY, gravity);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    NotifyWarringWindow btNotifyWindow;
    public static boolean isBtNotifyClose = false;

    public void btNotifyWindow(boolean isShow) {

        if (isBtNotifyClose) return;

        if (appStatusViewModel == null) return;

        if (isShow) {

            if (btNotifyWindow != null && btNotifyWindow.isShowing()) return;

            if (appStatusViewModel.currentStatus.get() == STATUS_MAINTENANCE) return;

            //    if (appStatusViewModel.currentStatus.get() == STATUS_RUNNING) return;

            String msg = getString(R.string.bt_notify);
            int res = R.drawable.btn_notify_warring_left;
            //     int res = R.drawable.shape_test;
            if (getBinding() == null) return;
            View view = getBinding().btnBT;

            btNotifyWindow = new NotifyWarringWindow(this, msg, res);
            btNotifyWindow.getContentView().measure(makeDropDownMeasureSpec(btNotifyWindow.getWidth()), makeDropDownMeasureSpec(btNotifyWindow.getHeight()));
            int offsetX = 10;
            int offsetY = -6;
            int gravity = Gravity.START;
            PopupWindowCompat.showAsDropDown(btNotifyWindow, view, offsetX, offsetY, gravity);

        } else {
            if (btNotifyWindow != null) {
                btNotifyWindow.dismiss();
                btNotifyWindow = null;
            }
        }
    }

    NotifyWarringWindow internetNotifyWarringWindow;

    public void internetNotifyWarringWindow(boolean isShow) {

        if (internetNotifyWarringWindow != null) {
            internetNotifyWarringWindow.dismiss();
            internetNotifyWarringWindow = null;
        }

        if (appStatusViewModel == null) return;


        if (isShow) {

            if (popupWindow != null && popupWindow.isShowing()) return;
            if (appStatusViewModel.currentStatus.get() == STATUS_MAINTENANCE) return;
            if (retailVideoWindow != null && retailVideoWindow.isShowing()) return;

            String msg = getString(R.string.No_internet_connection);
//            int res = R.drawable.btn_notify_warring_right;//右邊
            int res = R.drawable.btn_notify_warring_left;
            if (getBinding() == null) return;
            View view = getBinding().ibWifi;

            appStatusViewModel.wifiState.set(R.drawable.btn_header_wifi_none_default);

            internetNotifyWarringWindow = new NotifyWarringWindow(this, msg, res);
            internetNotifyWarringWindow.getContentView().measure(makeDropDownMeasureSpec(internetNotifyWarringWindow.getWidth()), makeDropDownMeasureSpec(internetNotifyWarringWindow.getHeight()));

            //右邊
            // int offsetX = -Math.abs(internetNotifyWarringWindow.getContentView().getMeasuredWidth() - view.getWidth()) - 6;
//            int offsetY = -6;
//            int gravity = Gravity.START;

            //左邊
            int offsetX = 10;
            int offsetY = -6;
            int gravity = Gravity.START;

            PopupWindowCompat.showAsDropDown(internetNotifyWarringWindow, view, offsetX, offsetY, gravity);
            internetNotifyWarringWindow.setOnCustomDismissListener(new BasePopupMsgWindow.OnCustomDismissListener() {
                @Override
                public void onStartDismiss(MsgEvent value) {
                }

                @Override
                public void onDismiss() {
                    internetNotifyWarringWindow = null;
                }
            });

        }
    }

    NotifyWarringWindow hrNotifyWarringWindow;

    public void hrNotifyWarringWindow(boolean isShow) {

        if (isShow) {
            if (hrNotifyWarringWindow != null && hrNotifyWarringWindow.isShowing()) return;

            String msg = getString(R.string.Heart_rate_monitor_required);
            int res = R.drawable.btn_notify_warring_left;
            if (getBinding() == null) return;
            View view = getBinding().btnHR;

            hrNotifyWarringWindow = new NotifyWarringWindow(this, msg, res);
            hrNotifyWarringWindow.getContentView().measure(makeDropDownMeasureSpec(hrNotifyWarringWindow.getWidth()), makeDropDownMeasureSpec(hrNotifyWarringWindow.getHeight()));
            int offsetX = 16;
            int offsetY = -6;
            int gravity = Gravity.START;
            PopupWindowCompat.showAsDropDown(hrNotifyWarringWindow, view, offsetX, offsetY, gravity);
            hrNotifyWarringWindow.setOnCustomDismissListener(new BasePopupMsgWindow.OnCustomDismissListener() {
                @Override
                public void onStartDismiss(MsgEvent value) {

                }

                @Override
                public void onDismiss() {
                    hrNotifyWarringWindow = null;
                }
            });
        } else {
            if (hrNotifyWarringWindow != null) {
                hrNotifyWarringWindow.dismiss();
                hrNotifyWarringWindow = null;
            }
        }
    }

//    // Activity被回收前，會呼叫這個方法，儲存資料
//    String mState = "KKK";
//
//    @Override
//    protected void onSaveInstanceState(@NonNull Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putString("state", mState);
//        Log.d("savedInstanceState", "onSaveInstanceState: " + mState);
//    }
//
//    // 也可以在這個方法裡恢復資料
//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        mState = savedInstanceState.getString("state");
//        Log.d("savedInstanceState", "onRestoreInstanceState: " + mState);
//    }

    @Override
    protected void onStop() {
        super.onStop();

        //開啟多媒體APP Media > App進入背景，顯示Media選單
        // if (isShowMediaMenuOnStop && CommonUtils.isScreenOn()) {

        btNotifyWindow(false);

        if (isShowMediaMenuOnStop && isScreenOn()) {
            try {
//                Log.d("OOOIIIIEEE", "onStop: " + MainActivity.isOnStopBackToMainTraining);
//                if (MainActivity.isOnStopBackToMainTraining) {
//                    LiveEventBus.get(ON_STOP_BACK_TO_MAIN_TRAINING).post(true);
//                }


                showMediaMenu(true);

                //   showMediaFloatingDashboard(true, true);


                if (floatingBottomMenuWindow != null) {
                    floatingBottomMenuWindow.enableView();
                    floatingBottomMenuWindow.setVisibleFade(false);
                }

                if (floatingTopDashBoardWindow != null) {
                    floatingTopDashBoardWindow.enableView();
                    floatingTopDashBoardWindow.setVisibleFade(false);
                }

                if (appStatusViewModel.currentPage.get() != CURRENT_PAGE_MEDIA) {
                    lastMedia = null;
                }
//                lastMedia = "";
                //跳去media頁籤
                LiveEventBus.get(MEDIA_MENU_GO_TO_MEDIA).post(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            showLoading(false);
            showLoading2(false);
            showMediaAlphaBackground(false);
            showErrorMsg(null, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //   isForeground = true;

        //  overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        if (SETTING_SHOW) {
            overridePendingTransition(0, R.anim.fade_out_3);
        } else {
//            overridePendingTransition(R.anim.fade_in_3s, 0);
//            overridePendingTransition(R.anim.fade_in_2s, 0);
            overridePendingTransition(R.anim.fade_in_2s, 0);

//            Log.d("OOOIIIIEEE", "onResume: " + MainActivity.isOnStopBackToMainTraining);
            if (MainActivity.isOnStopBackToMainTraining) {
                LiveEventBus.get(ON_STOP_BACK_TO_MAIN_TRAINING).post(true);
            }
        }
        //    if (!App.SETTING_SHOW) {//設定頁面
        //   showMediaFloatingDashboard(false);


        //WebView開啟時，點Gboard鍵盤設定會觸發Resume，導致menu被關閉
        if (!isWebViewOn) {
            showMediaMenu(false);
        }


        getBinding().vBackground.setVisibility(View.GONE);
        //   } else {
//            if(appStatusViewModel.isMediaPlaying.get()){
//                Log.d("EEEFEFEFE", "onResume: ");
//                showMediaFloatingDashboard(true, true);
//                showMediaMenu(true);
//            }
        //     }
        SETTING_SHOW = false;

        //   Log.d("亮度", "當前亮度: " + getScreenBrightness(getApp()) + ", 百分比" + getBrightnessPresent(getScreenBrightness(getApp())));

    }

    public void closeMedia() {

        // closePackage(this);

        isShowTv = false;
        MediaMenuWindow2.isShowMira = false;
        showYouTube(false, "");
        //   showMediaMenu(false);

        if (mediaType == MEDIA_TYPE_HDMI) closeHdmi();

        if (floatingBottomMenuWindow != null) {
            floatingBottomMenuWindow.dismiss();
            floatingBottomMenuWindow = null;
        }

        if (floatingTopDashBoardWindow != null) {
            floatingTopDashBoardWindow.dismiss();
            floatingTopDashBoardWindow = null;
        }

        getBinding().vBackground.setVisibility(View.GONE);


        startActivity(new Intent(this, MainActivity.class)); // > resume
        overridePendingTransition(0, 0);

        //  closePackage(this);

        //    new RxTimer().timer(1000, x -> closePackage(this));
    }

    /**
     * FTMS 更新機器狀態
     */
    public void updateFtmsMachineStatus(DeviceGEM.FITNESS_EQUIPMENT_STATE equipState, Map<DeviceGEM.EQUIPMENT_CONTROL_PARAMETER, Integer> parameters) {

        try {
            if (appStatusViewModel == null) return;
            if (!appStatusViewModel.isFtmsConnected.get()) return;

            getDeviceGEM().gymConnectMessageSetFitnessEquipmentState(equipState, parameters);

            // TODO: NEW GEM3
            //   getDeviceGEM().gymConnectMessageSetMachineStatus(equipState, parameters);
        } catch (Exception e) {
            showException(e);
        }
    }

    public void updateFtmsMachineStatus2(DeviceGEM.TRAINING_STATUS equipState) {
        try {
            if (appStatusViewModel == null) return;
            if (!appStatusViewModel.isFtmsConnected.get()) return;
            getDeviceGEM().gymConnectMessageSetTrainingStatus(equipState);
        } catch (Exception e) {
            showException(e);
        }
    }

    /**
     * 檢查app更新
     */
    public void checkUpdate() {

        String uuRl;
        if (deviceSettingViewModel.territoryCode.get() == TERRITORY_US) {
            uuRl = BuildConfig.UPDATE_URL_US;
        } else if (deviceSettingViewModel.territoryCode.get() == TERRITORY_JAPAN) {
            uuRl = BuildConfig.UPDATE_URL_JP;
        } else if (deviceSettingViewModel.territoryCode.get() == TERRITORY_CANADA) {
            uuRl = BuildConfig.UPDATE_URL_CA;
        } else {
            uuRl = BuildConfig.UPDATE_URL_GLOBAL;
        }

        //    Log.d("##UpdateProcess##", "#######網址: " + uuRl + "update.json");


        Log.d(DownloadManagerCustom.TAG, "checkUpdate: 呼叫 apiCheckUpdate()");
        BaseApi.request(BaseApi.createApi2(IServiceApi.class, uuRl).apiCheckUpdate(),
                new BaseApi.IResponseListener<UpdateBean>() {
                    @Override
                    public void onSuccess(UpdateBean data) {

                        LogS.printJson(DownloadManagerCustom.TAG, new Gson().toJson(data), "");

                        DeviceSettingBean d = getApp().getDeviceSettingBean();
                        try {
                            if (data.getVersionCode() > new CommonUtils().getLocalVersionCode()) {
                                Log.d(DownloadManagerCustom.TAG, "######有新版本########");
                                //有新版本都要通知
                                new CallWebApi(MainActivity.this).apiNotifyNewestConsoleVersion(data);

                                if (d.isAutoUpdate()) {//自動更新有開啟，下載檔案
                                    Log.d(DownloadManagerCustom.TAG, "<AUTO_UPDATE> ON，下載檔案");
                                    if (downloadManagerCustom != null) {
                                        downloadManagerCustom.cancelDownload();
                                        if (downloadManagerCustom.downloadManager != null) {
                                            downloadManagerCustom.downloadManager.destroy();
                                        }
                                        downloadManagerCustom = null;
                                    }
                                    downloadManagerCustom = new DownloadManagerCustom(MainActivity.this);
                                    downloadManagerCustom.initDownload(data);
                                } else {
                                    //通知Dyaco Cloud有新的Console版本
                                    Log.d(DownloadManagerCustom.TAG, "<AUTO_UPDATE> OFF，不需要下載，通知Dyaco Cloud有新的Console版本 ");
//                                    new CallWebApi(MainActivity.this).apiNotifyNewestConsoleVersion(data);
                                    new WorkManagerUtil().cancelWorkByTag(WORK_NOTIFY_UPDATE_MSG_TAG);
                                }
                            } else {
                                new WorkManagerUtil().cancelWorkByTag(WORK_NOTIFY_UPDATE_MSG_TAG);
                                Log.d(DownloadManagerCustom.TAG, "沒有新版本，不需要更新， update.json版本：" + data.getVersionCode() + ", 目前版本：" + new CommonUtils().getLocalVersionCode());
                            }
                        } catch (Exception e) {
                            Log.d(DownloadManagerCustom.TAG, "不需要更新:" + e.getLocalizedMessage());
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFail() {
                        Log.d(DownloadManagerCustom.TAG, "連線失敗");
                    }
                });
    }


    //  UpdateBean updateBean;
//    private final ActivityResultLauncher<String> mPermissionResult = registerForActivityResult(
//            new ActivityResultContracts.RequestPermission(),
//            result -> {
//                if (result) {
//                    Log.d("UPDATE@@@", "onActivityResult: PERMISSION GRANTED");
//                    UpdateAppWindow updateAppWindow = new UpdateAppWindow(MainActivity.this, updateBean);
//                    updateAppWindow.showAtLocation(getWindow().getDecorView(), Gravity.START | Gravity.BOTTOM, 0, 0);
//                    updateAppWindow.setOnCustomDismissListener(new BasePopupWindow.OnCustomDismissListener() {
//                        @Override
//                        public void onStartDismiss(MsgEvent value) {
//
//                        }
//
//                        @Override
//                        public void onDismiss() {
//
//                        }
//                    });
//                } else {
//                    Log.d("UPDATE@@@", "onActivityResult: PERMISSION DENIED");
//                }
//            });


    /**
     * 取得 default_settings.json 的資料，存入 DeviceSettingBean(mmkv)
     */
    private void initDeviceModel() {

//        if (!getApp().getDeviceSettingBean().isMachineEdit()) { //工程模式改過機型就不抓設定檔
//            //  MMKV.defaultMMKV().remove("DeviceSettingBean");
//            int modelCode = getApp().getDeviceSettingBean().getModel_code();//設備裡面存的
//            //Model Code 有改變，才會抓default_settings.json的設定
//            String settingFile = new CommonUtils().getSettingFile(SETTING_FILE_PATH); //若無設定檔 > 取預設值產生設定檔
//            if (null != settingFile) {//設定檔有資料
//                //設定檔的內容
//                //     DeviceSettingBean deviceSettingBean = new Gson().fromJson(settingFile, DeviceSettingBean.class);
//
//                Gson gson = new GsonBuilder().setDateFormat("MMM dd, yyyy HH:mm:ss").create();
//                DeviceSettingBean deviceSettingBean = gson.fromJson(settingFile, DeviceSettingBean.class);
//
//                Log.d("SETTING_FILE", "default_settings.json 裡面的內容: ");
//                LogS.printJson("SETTING_FILE", settingFile, "");
//
//                Log.d("SETTING_FILE", "讀取設定檔資料成功" + ",裝置內存放的ModelCode:" + modelCode + ",設定檔的ModelCode:" + deviceSettingBean.getModel_code());
//
//                //首次安裝 mmkv的mode_code 是 -1
//                if (modelCode == deviceSettingBean.getModel_code() && getApp().getDeviceSettingBean().getDeviceFirstLaunchTime() != null) {
//                    //  if (modelCode == deviceSettingBean.getModel_code()) {
//                    //只是檢查 設定檔 的 ModelName 不需要覆蓋 DeviceSettingBean
//                    //首次安裝， DeviceSettingBean 沒東西，不該跑這裡
//                    MODE = getMode(deviceSettingBean.getModel_code());
//                    Log.d("SETTING_FILE", "設定檔的機型(VersionName&VersionCode)與裝置內存放的機型(VersionName&VersionCode)相同");
//                } else {
//                    //將新設定檔的值重新存入 DeviceSettingBean(mmkv)
//                    //or首次安裝
//                    new InitProduct(getApplicationContext()).setProductDefault(settingFile);
//                    Log.d("SETTING_FILE", "設定檔的機型(VersionName&VersionCode)與裝置內存放的不同，取設定檔資料 重新寫入裝置資料庫:" + MODE);
//                }
//                Log.d("SETTING_FILE", "當前機型為：" + MODE + " 開機完成 ");
//
//            } else {
//                //設定檔無資料或讀取失敗
//                Log.d("SETTING_FILE", "讀取設定檔資料或建立設定檔失敗，直接取程式內機型預設值");
//                new InitProduct(getApplicationContext()).setProductDefault(CT1000ENT); //產生設定檔失敗，直接取程式內預設值 存入 DeviceSettingBean
//            }
//        } else {
//            //Console設定檔已被修改過
//            MODE = getMode(getApp().getDeviceSettingBean().getModel_code());
//            Log.d("SETTING_FILE", "Console的設定檔已被修改過，不重抓設定檔 ");
//        }


        DeviceSettingBean dd = getApp().getDeviceSettingBean();
        int modelCode = dd.getModel_code();//設備裡面存的

        if (modelCode != -1 && dd.getDeviceFirstLaunchTime() != null) {
            MODE = getMode(dd.getModel_code());
        } else {
            //初始值
            new InitProduct(getApplicationContext()).setProductDefault(CU1000ENT, TERRITORY_US);
        }


        //把設定檔存入ViewModel
        //   deviceSettingViewModel = new ViewModelProvider(this).get(DeviceSettingViewModel.class);
        //DeviceSettingBean mmkv的設定資料存入 ViewModel
        new CommonUtils().mmkvDeviceSettingToViewModel(deviceSettingViewModel, getApp().getDeviceSettingBean());

//        getBinding().setDeviceSetting(deviceSettingViewModel);


        //getIdentity()沒值的話就產生新的GUID
//        if (TextUtils.isEmpty(getApp().getIdentity())) {
//            MMKV.defaultMMKV().encode("GUID", UUID.randomUUID().toString() + "#" + deviceSettingViewModel.modelName);
//        }


        isTreadmill = deviceSettingViewModel.typeCode.get() == DeviceIntDef.DEVICE_TYPE_TREADMILL;

        //      isUs = deviceSettingViewModel.territoryCode.get() == DeviceIntDef.TERRITORY_US;

        Log.d("AppSetting", "Model_code: " + getApp().getDeviceSettingBean().getModel_code());
        Log.d("AppSetting", "TerritoryCode: " + getApp().getDeviceSettingBean().getTerritoryCode());

        isUs = deviceSettingViewModel.territoryCode.get() == TERRITORY_US ||
                deviceSettingViewModel.territoryCode.get() == DeviceIntDef.TERRITORY_CANADA;


        TimeUpdateManager.register(this);
        TimeUpdateManager.getTimeText().observeForever(time ->
                deviceSettingViewModel.timeText.set(time));


        final IntentFilter filter2 = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter2.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(new ScreenReceiver(), filter2);

        //設定初始時間
        deviceSettingViewModel.timeText.set(updateTime());


        //取得 Level AD
        if (TextUtils.isEmpty((App.getApp().getDeviceSettingBean().getDsLevelAd()))) {
            int[] levelAdArray = MODE.getLevelAD(); //getBikeLevel 來的
            StringBuilder levelAdStr = new StringBuilder();
            for (int i = 0; i < levelAdArray.length; i++) {
                levelAdStr.append(i == (MAX_LEVEL_MAX - 1) ? levelAdArray[i] : levelAdArray[i] + "#");
            }
            DeviceSettingBean d = App.getApp().getDeviceSettingBean();
            d.setDsLevelAd(levelAdStr.toString());
            App.getApp().setDeviceSettingBean(d);
        }
    }


    public LoadingWindow loadingWindow;

    public void showLoading(boolean isLoading) {
        if (isLoading) {
            if (loadingWindow == null) {
                loadingWindow = new LoadingWindow(this);
                loadingWindow.startDialog();
                loadingWindow.setOnCustomDismissListener(new BasePopupWindow.OnCustomDismissListener() {
                    @Override
                    public void onStartDismiss(MsgEvent value) {
                        loadingWindow = null;
                    }

                    @Override
                    public void onDismiss() {
                    }
                });
            }
        } else {
            runOnUiThread(() -> {
                if (loadingWindow != null) {
                    loadingWindow.dismiss();
                    loadingWindow = null;
                }
            });
        }
    }

    //純半透明
    LoadingWindowEx loadingWindowEx;

    public void showLoadingEx(boolean isLoading, int duration) {

        if (loadingWindowEx != null) {
            loadingWindowEx.dismiss();
            loadingWindowEx = null;
        }
        if (isLoading) {
            loadingWindowEx = new LoadingWindowEx(this, duration);
            loadingWindowEx.showAtLocation(getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);
        }
    }

    LoadingWindowAllB loadingWindowAllB;

    public void showLoadingAllB(boolean isLoading) {

        if (loadingWindowAllB != null) {
            loadingWindowAllB.dismiss();
            loadingWindowAllB = null;
        }
        if (isLoading) {
            loadingWindowAllB = new LoadingWindowAllB(this);
//            loadingWindowAllB.setAnimationStyle(R.style.left_animation);
            loadingWindowAllB.showAtLocation(getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);
        }
    }


//    public void showDashboard(boolean s) {
//        if (isTreadmill) {
//            getBinding().workoutTopViewTreadmill.setVisibility(s ? View.VISIBLE : View.GONE);
//        } else {
//            getBinding().workoutTopViewBike.setVisibility(s ? View.VISIBLE : View.GONE);
//        }
//    }

    LoadingWindow2 loadingWindow2;

    public void showLoading2(boolean isLoading) {
        if (isLoading) {

            if (loadingWindow2 == null) {
                loadingWindow2 = new LoadingWindow2(this);
                loadingWindow2.startDialog();
                loadingWindow2.setOnCustomDismissListener(new BasePopupWindow.OnCustomDismissListener() {
                    @Override
                    public void onStartDismiss(MsgEvent value) {
                    }

                    @Override
                    public void onDismiss() {
                        loadingWindow2 = null;
                    }
                });
            }
        } else {
            runOnUiThread(() -> {
                if (loadingWindow2 != null) {
                    loadingWindow2.dismiss();
                    loadingWindow2 = null;
                }
            });
        }
    }

//    private void clearPopupWindow() {
//        if (popupWindow != null) {
//            popupWindow.dismiss();
//            popupWindow = null;
//        }
//    }

    /**
     * 更新上面頭像
     *
     * @param isSetting 從Console設定的 不更新urlPhoto，console沒照片
     */
    public void setAvatar(boolean isSetting) {
        if (userProfileViewModel.getAvatarId() != null) {
            int avatarRes = CommonUtils.getAvatarSelectedFromTag(userProfileViewModel.getAvatarId(), false);
            GlideApp.with(getApp())
                    .load(avatarRes)
//                    .diskCacheStrategy(DiskCacheStrategy.NONE)
//                    .skipMemoryCache(true)
                    .error(R.drawable.avatar_normal_1_default)
                    .into(getBinding().ivMemberIcon);
        }

        if (floatingTopDashBoardWindow != null) {
            try {
                floatingTopDashBoardWindow.setAvatar();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (isSetting) return;
//        if (userProfileViewModel.getPhotoFileUrl() != null) {
        //有上傳圖片
        if (userProfileViewModel.getAvatarId() == null) {
            try {
                GlideApp.with(getApp())
                        .load(userProfileViewModel.getPhotoFileUrl())
//                    .diskCacheStrategy(DiskCacheStrategy.NONE)
//                    .skipMemoryCache(true)
                        .error(R.drawable.avatar_normal_1_default)
                        .circleCrop()
                        .into(getBinding().ivMemberIcon);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @SuppressLint("MissingPermission")
    private void garminUnPair() {
        if (mGarminDeviceManager == null) return;

        Set<Device> pairedDevices = mGarminDeviceManager.getPairedDevices();
        if (pairedDevices == null) return;
        List<Device> pairedDevicesList = new ArrayList<>(pairedDevices);
        for (Device d : pairedDevicesList) {
            Log.d(GARMIN_TAG, "取得已配對裝置: " + d.address() + "," + d.connectionState() + "," + d.friendlyName());
            mGarminDeviceManager.forget(d.address());
        }
    }


//    private void garminUnPair2() {
//        if (mGarminDeviceManager == null) return;
//
//        GarminUtil garminUtil = new GarminUtil();
//
//        Set<Device> pairedDevices = mGarminDeviceManager.getPairedDevices();
//        if (pairedDevices == null) return;
//        List<Device> pairedDevicesList = new ArrayList<>(pairedDevices);
//        for (Device d : pairedDevicesList) {
//            Log.d(GARMIN_TAG, "取得已配對裝置: " + d.address() + "," + d.connectionState() + "," + d.friendlyName());
//            mGarminDeviceManager.forget(d.address());
//
//            systemGarminUnPair(garminUtil, d);
//        }
//    }

//    private void systemGarminUnPair(GarminUtil garminUtil,com.garmin.health.Device device) {
//        garminUtil.removeBondedDevice(this, device.address(), new GarminUtil.BondStateListener() {
//            @Override
//            public void onCheckDeviceBondState(String s, GarminUtil.BOND_STATE bond_state) {
//                    Log.d("GARMINNN", "onCheckDeviceBondState " + s +","+ bond_state);
//            }
//
//            @Override
//            public void onBondDevice(String s, GarminUtil.BOND_STATE bond_state) {
//                Log.d("GARMINNN", "onBondDevice " + s +","+ bond_state);
//            }
//        });
//    }

    public void showVBG(boolean isShow) {
        getBinding().vBackground.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    @SuppressLint("MissingPermission")
    public void webApiLogout() {

        CommonUtils.wakeUpScreen(null);

        if (hdmiIn != null) {
            //切port3 > 拔掉port3 > 重啟app > hdmi 抓上一個port > port3沒東西  > anr
            hdmiIn.SwitchHdmiSource(PORT_EZ_CAST);
        }


        //斷開音訊
//        if (mBluetoothDevice != null && mAudioDeviceWatcher != null) {
//         //   mAudioDeviceWatcher.disconnectAudioDevice(mBluetoothDevice);
//            mAudioDeviceWatcher.removeBondDevice(mBluetoothDevice);
//
//            mAudioDeviceWatcher.removeListener();
//        }

        //斷開藍芽音訊
        unBondAudio();
        if (mAudioDeviceWatcher != null) {
            //todo AudioDeviceWatcher.removeListener() 裡面呼叫了 unregisterReceiver()，但傳入的 receiver 沒註冊或已被註銷 →
            mAudioDeviceWatcher.removeListener();
        }

        appStatusViewModel.isGem3On.set(false);


        //   garminUnPair2();
        garminUnPair();


        //  new CommonUtils().consolePairedDevices(bluetoothAdapter);


//        clearAppData();
//        clearCookies();
//        closePackage(this);


        //  getDeviceGEM().systemMessageRestart();


//        if (hdmiIn != null) {
//            hdmiIn.SwitchHdmiSource(PORT_EZ_CAST);
//        }

        // TODO: garmin解綁可能很慢，要晚一點關閉藍芽
        if (bluetoothAdapter != null) {
            if (bluetoothAdapter.isEnabled()) {
                if (bluetoothAdapter.disable()) {
                    appStatusViewModel.isBtOn.set(false);
                } else {
                }
            } else {
            }
        }


        getBinding().vBackground.setVisibility(View.VISIBLE);

        if (userProfileViewModel.userType.get() != USER_TYPE_GUEST) {

            BaseApi.request(BaseApi.createApi(IServiceApi.class).apiMemberCheckoutMachine(""),
                    new BaseApi.IResponseListener<MemberCheckoutMachineBean>() {
                        @Override
                        public void onSuccess(MemberCheckoutMachineBean data) {


                            Log.d("WEB_API", "MemberCheckoutMachineBean: " + data.toString());
                            if (data.getSuccess()) {
                                COOKIE = "";
                                //  } else {
                                //     if (data.getErrorMessage() != null)
                                //    Toasty.warning(MainActivity.this, data.getErrorMessage(), Toasty.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFail() {
                            Log.d("EWWWWWWWWW", "失敗");
                        }
                    });
        }

        Log.d("VVVVVEEEE", "webApiLogout: ");
        //登出就重啟APP
        new RxTimer().timer(1000, number -> {
            try {
                mRestartApp();
                // restartApp(this);
                //   navController.navigate(MainDashboardFragmentDirections.actionMainBlankFragmentToNavigationLogin());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    //重啟 Console APP, AppRestart
    public void mRestartApp() {

//        DeviceSettingBean d = getApp().getDeviceSettingBean();
//        d.setFakeStart(true);
//        getApp().setDeviceSettingBean(d);

        if (isEmulator || !isTreadmill) {
            Log.d("UART_CONSOLE", "BIKE OR Emulator mRestartApp: APP重啟 ");
            restartApp(this);
        } else {
            Log.d("UART_CONSOLE", "電跑: mRestartApp: APP重啟  ");
            uartConsole.setDevMainMode(RESET);
            //  restartApp(this);
        }
    }


    public void setUserData(MemberCheckinMachineByQRCodeBean.DataMapDTO.DataDTO data) {

        Log.d("OOODDDODODO", "登入資料: " + data.toString());
        int gender = data.getGender().equals("M") ? MALE : FEMALE;
        int unit = data.getMeasurementUnit() == 1 ? IMPERIAL : METRIC; //webapi 公制0,英制1


        double height = Double.parseDouble(data.getHeight());
        double weight = Double.parseDouble(data.getWeight());

        //都是傳來公制
        int weightMetric = (int) weight;
        int heightMetric = (int) height;
        int weightImperial = (int) FormulaUtil.kg2lb2(weight);
        int heightImperial = FormulaUtil.cm2in((int) height);

        userProfileViewModel.userType.set(USER_TYPE_NORMAL);
        userProfileViewModel.userDisplayName.set(data.getDisplayName());
        //     userProfileViewModel.userDisplayName.set(data.getFirstName() + " " + data.getLastName());
        userProfileViewModel.setOrgId(data.getOrgId());
        userProfileViewModel.setUserUUID(data.getUserUuid());
        userProfileViewModel.setUserUnit(unit);
        userProfileViewModel.setUserGender(gender);
        userProfileViewModel.setWeight_imperial(weightImperial);
        userProfileViewModel.setWeight_metric(weightMetric);
        userProfileViewModel.setHeight_metric(heightMetric);
        userProfileViewModel.setHeight_imperial(heightImperial);
        userProfileViewModel.setAvatarId(data.getAvatarId());
        userProfileViewModel.setPhotoFileUrl(data.getPhotoFileUrl());

        if (data.getAvatarId() == null && data.getPhotoFileUrl() == null) {
            userProfileViewModel.setAvatarId("default_avatar");
        }

        userProfileViewModel.setUserAge(data.getAge());
        userProfileViewModel.setUserLastName(data.getLastName());
        userProfileViewModel.setUserFirstName(data.getFirstName());
        userProfileViewModel.setJoinedGymDate(data.getJoinedGymDate());
        userProfileViewModel.setJoinedGymDateTimeMillis(data.getJoinedGymDateTimeMillis());
        userProfileViewModel.setJoinedMonthlyRanking(data.isJoinedMonthlyRanking());
//        userProfileViewModel.setJoinedGymDateTimeMillis(0l);
        userProfileViewModel.setMembershipExpirationDate(data.getMembershipExpirationDate());

        DeviceSettingBean deviceSettingBean = getApp().getDeviceSettingBean();
        deviceSettingBean.setUnit_code(userProfileViewModel.getUserUnit());
        getApp().setDeviceSettingBean(deviceSettingBean);
        new CommonUtils().deviceSettingViewModelToMMKV(deviceSettingViewModel);

        setAvatar(false);
    }

    /**
     * 未讀訊息
     */
    public void webApiGetUnreadMessageCountFromMachine() {

        if (userProfileViewModel.userType.get() != USER_TYPE_NORMAL) return;

        BaseApi.request(BaseApi.createApi(IServiceApi.class).apiGetUnreadMessageCountFromMachine(""),
                new BaseApi.IResponseListener<GetUnreadMessageCountFromMachineBean>() {
                    @Override
                    public void onSuccess(GetUnreadMessageCountFromMachineBean data) {


                        try {
                            if (data.getSuccess()) {
                                Log.d("WEB_API", "GetUnreadMessageCountFromMachineBean: " + data.getDataMap().getData());
                                deviceSettingViewModel.alertNotifyCount.set(data.getDataMap().getData().getUnreadCount());
                            } else {
                                Toasty.warning(getApp(), "# " + data.getErrorMessage(), Toasty.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFail() {
                    }
                });
    }


    private void holdScreen() {
        WindowManager mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        layoutParams.format = PixelFormat.TRANSLUCENT;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        layoutParams.width = 1;
        layoutParams.height = 1;
        layoutParams.gravity = Gravity.BOTTOM;
        layoutParams.x = 1;
        layoutParams.y = 1;
        View view = new View(this);
        view.setBackgroundColor(Color.TRANSPARENT);
        mWindowManager.addView(view, layoutParams);
    }

    public UpdateRestartWindow updateRestartWindow;

    public void showUpdateRestartWindow() {

        new RxTimer().timer(1000, number -> {

            //國際版在登入頁面才會通知更新
            if (appStatusViewModel.currentStatus.get() == AppStatusIntDef.STATUS_LOGIN_PAGE || (isUs && appStatusViewModel.currentStatus.get() == AppStatusIntDef.STATUS_IDLE)) {
                if (updateRestartWindow != null) {
                    updateRestartWindow.dismiss();
                    updateRestartWindow = null;
                }

                if (getApp().getDeviceSettingBean().isAutoUpdate() && !"".equals(getApp().getDeviceSettingBean().getTmpApkPath())) {
                    updateRestartWindow = new UpdateRestartWindow(this);
                    updateRestartWindow.showAtLocation(getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);
                } else {
                }
            } else {
                new WorkManagerUtil().cancelWorkByTag(WORK_NOTIFY_UPDATE_MSG_TAG);
            }
        });
    }


    public RetailVideoWindow retailVideoWindow;

    public void showRetail(boolean isShow) {

        if (retailVideoWindow != null) {
            retailVideoWindow.dismiss();
        }

        if (isShow) {

            if (popupWindow != null) {
                popupWindow.dismiss();
                popupWindow = null;
            }

            retailVideoWindow = new RetailVideoWindow(this);
            retailVideoWindow.showAtLocation(getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);

            retailVideoWindow.setOnCustomDismissListener(new BasePopupWindow.OnCustomDismissListener() {
                @Override
                public void onStartDismiss(MsgEvent value) {
                    retailVideoWindow = null;
                }

                @Override
                public void onDismiss() {

                }
            });

            //   new RxTimer().timer(2000, number -> showBg3(false));
        }
    }


//    public static String getMacAddress() {
//        //android 11非系統APP 無法讀取  android 11系統APP 待測試
//        String macAddress = "";
//        try {
//            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
//            while (interfaces.hasMoreElements()) {
//                NetworkInterface iF = interfaces.nextElement();
//
//                byte[] address = iF.getHardwareAddress();
//                if (address == null || address.length == 0) {
//                    continue;
//                }
//
//                StringBuilder buf = new StringBuilder();
//                for (byte b : address) {
//                    buf.append(String.format("%02X:", b));
//                }
//                if (buf.length() > 0) {
//                    buf.deleteCharAt(buf.length() - 1);
//                }
//                String mac = buf.toString();
//                //  Log.d("mac", "interfaceName="+iF.getName()+", mac="+mac);
//
//                if (TextUtils.equals(iF.getName(), "wlan0")) {
//                    return mac;
//                }
//            }
//        } catch (SocketException e) {
//            e.printStackTrace();
//            return macAddress;
//        }
//
//        return macAddress;
//    }


    public static boolean isGymApp = false;

    AnplusBean anplusBean = new AnplusBean();
    AnplusMachine anplusMachine = new AnplusMachine();  // BITGYM, KINOMAP
    DyacoDataEmitter mDyacoEmitter = new DyacoDataEmitter();
    DyacoDataReceiver mDyacoDataReceiver;               // BITGYM, KINOMAP

    public void initReceiver() {  // BITGYM, KINOMAP
        Log.d("GYM_APP", "setListener, startReceiveInfo & startReceiveCtrl");

        mDyacoDataReceiver = DyacoDataReceiver.getInstance();
        mDyacoDataReceiver.setListener(dyacoDataListener);
        mDyacoDataReceiver.startReceiveInfo(this);
        mDyacoDataReceiver.startReceiveCtrl(this);
    }

    // BITGYM, KINOMAP
    DyacoDataReceiver.DyacoDataListener dyacoDataListener = new DyacoDataReceiver.DyacoDataListener() {
        @Override
        public void onInfoDataReceived(AnplusBean anplusBean) {
            Log.d("GYM_APP", "onInfoDataReceived" + new Gson().toJson(anplusBean));
        }

        @Override
        public void onCtrlDataReceived(AnplusCtrlBean anplusCtrlBean) {
            Log.d("GYM_APP", "onCtrlDataReceived" + new Gson().toJson(anplusCtrlBean));

            Log.d("GYM_APP", "設定速度 = " + anplusCtrlBean.getTg_speed() +
                    " = " + anplusCtrlBean.getTg_speed() +
                    "設定揚升 = " + anplusCtrlBean.getTg_incline() +
                    "設定阻力 = " + anplusCtrlBean.getTg_level());
        }

        @Override
        public void onModelReceived() {

            // BitGym, Kinomap 要求取得Machine Info
            Log.d("GYM_APP", "onModelReceived: 要求傳送Machine Info");

            runOnUiThread(() -> {
                try {
                    getMachineInfo();
                    Log.d("GYM_APP", "傳送Machine Info: " + new Gson().toJson(anplusMachine));
                    mDyacoEmitter.emit(getApp(), anplusMachine);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        @Override
        public void onModelDataReceived(AnplusMachine anplusMachine) {

            Log.d("GYM_APP", "onModelDataReceived" + new Gson().toJson(anplusMachine));

        }
    };

    private void getMachineInfo() {

        if (anplusMachine == null) return;

        // {"brand_name":"Spirit",
        //  "brand_product_name":"CT1000ENT",
        //  "incline_max":20,
        //  "incline_min":2,
        //  "level_max":30,
        //  "level_min":3,
        //  "machine_type_key":"T",
        //  "machine_type_value":"Treadmill",
        //  "speed_km_max":10,
        //  "speed_km_min":1,
        //  "ver":"1.9",
        //  "ver_date":"20240320"}

        anplusMachine.setBrand_name("Spirit");
        anplusMachine.setBrand_product_name(MODE.getModelName());

        anplusMachine.setSpeed_km_max(0);
        anplusMachine.setSpeed_km_min(0);
        anplusMachine.setIncline_max(0);
        anplusMachine.setIncline_min(0);
        anplusMachine.setLevel_max(0);
        anplusMachine.setLevel_min(0);

        if (isTreadmill) {
            anplusMachine.setMachine_type_key("T");
            anplusMachine.setMachine_type_value("Treadmill");

            anplusMachine.setSpeed_km_max(OPT_SETTINGS.MAX_SPD_MU_MAX);
            anplusMachine.setSpeed_km_min(OPT_SETTINGS.MIN_SPD_MU_MIN);

            anplusMachine.setIncline_max((int) getInclineValue(OPT_SETTINGS.MAX_INC_MAX) * 10);
        } else {
            if (MODE.getTypeCode() == DEVICE_TYPE_ELLIPTICAL) {
                anplusMachine.setMachine_type_key("E");
                anplusMachine.setMachine_type_value("Elliptical");
            } else {
                anplusMachine.setMachine_type_value("B");
                anplusMachine.setMachine_type_value("Bike");
            }

            anplusMachine.setLevel_max(MAX_LEVEL_MAX * 10);
            anplusMachine.setLevel_min(MAX_LEVEL_MIN * 10);
        }

        anplusMachine.setVer(new CommonUtils().getLocalVersionName(getApp()));
        anplusMachine.setVer_date("20240322");
    }

    public void sendGymAppData() {

        // 傳送即時值, 不必 *10
        if (isGymApp) {

            //發送workout資料 (需固定傳送公制)
            anplusBean.setWatt(workoutViewModel.currentPower.get());
            anplusBean.setRpm(workoutViewModel.currentRpm.get());
            anplusBean.setHeartRate(workoutViewModel.currentHeartRate.get());

            anplusBean.setCur_speed((UNIT_E == METRIC) ?
                    workoutViewModel.currentSpeed.get() :
                    mph2kph(workoutViewModel.currentSpeed.get()));

            if (!isTreadmill) {
                anplusBean.setSpm(workoutViewModel.currentRpm.get() * 2);
                anplusBean.setCur_level(workoutViewModel.currentLevel.get());
                anplusBean.setCur_incline(0);
            } else {
                anplusBean.setSpm(0);
                anplusBean.setCur_level(0);
                anplusBean.setCur_incline(getInclineValue(workoutViewModel.currentInclineLevel.get()));
            }

            mDyacoEmitter.emit(getApp(), anplusBean);
        }
    }


    Handler userInteractionHandler;
    Runnable r;

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();

        //   Log.d("CHILD_LOCK_SSS", "onUserInteraction: ");

        if (appStatusViewModel == null) return;
        if (appStatusViewModel.currentStatus.get() == STATUS_RUNNING) return;
        stopHandler();
        startHandler();
    }

    public void stopHandler() {
        if (deviceSettingViewModel != null) {
            if (deviceSettingViewModel.autoPause.getValue() == OFF) return;
        }

        if (userInteractionHandler != null) userInteractionHandler.removeCallbacks(r);

        //   Log.d("RPM_CHECK", "stopHandler: 停止childlock 計時 ");
    }


    public void startHandler() {
        //    Log.d("RPM_CHECK", "deviceSettingViewModel: " + deviceSettingViewModel);

        if (deviceSettingViewModel == null) return;
        try {
            if (deviceSettingViewModel.autoPause.getValue() == OFF) return;
        } catch (Exception e) {
            showException(e);
            return;
        }

        if (!isHomeScreen) return;

        long logoutDelayMillis = (long) DEFAULT_AUTO_PAUSE_TIME * 1000;

        try {

            long pauseAfterSeconds = deviceSettingViewModel.pauseAfter.get();

            // 定義一個合理的秒數範圍
            final long MIN_VALID_SECONDS = 1L;
            final long MAX_VALID_SECONDS = 5 * 60 * 60L;

            // 如果 pauseAfter 的值落在我們的有效區間內，就採用它
            if (pauseAfterSeconds >= MIN_VALID_SECONDS && pauseAfterSeconds <= MAX_VALID_SECONDS) {
                logoutDelayMillis = pauseAfterSeconds * 1000L;
            } else if (pauseAfterSeconds != 0) {
                // 如果值是無效的（例如負數、超大數），但不是我們允許的 0，就印出日誌
                // 這樣可以幫助我們追蹤到潛在的資料錯誤
                Log.w("AutoLogout", "偵測到無效或已損壞的 pauseAfter 值：" + pauseAfterSeconds + "，將使用預設值。");
            }
            // 如果 pauseAfterSeconds 是 0，則自然會使用上面的預設值，無需額外處理
            //4624633867356078080


        } catch (Exception e) {
            showException(e);
        }

        CHILD_LOCK_TIME = logoutDelayMillis;


        if (userInteractionHandler != null) userInteractionHandler.postDelayed(r, CHILD_LOCK_TIME);
//        if (userInteractionHandler != null) userInteractionHandler.postDelayed(r, 1 * 1000 * 10);
        Log.d("RPM_CHECK", "startHandler: 開始childlock 計時 ");
    }


    //APP在前景
    public static boolean isForeground;
    //    public static int CHILD_LOCK_TIME = 5 * 1000 * 60;
    public static long CHILD_LOCK_TIME = 8000;


    private void installExFatBinary() {
        BinaryInstaller.installBinaryIfNeeded(this, success -> {
            if (success) {
                Log.d("USB_UPDATE", "Binary 安裝成功，可執行掛載流程");
            } else {
                Log.e("USB_UPDATE", "Binary 安裝失敗，請確認是否已 root 並允許權限");
            }
        });

    }

    // errorText > <API name> - <API error code> - <API response message>
    public void insertEgymError(String errorText) {
        Log.d("UPLOAD_ERROR_LOG", "insertEgymError: " + errorText);
        List<ErrorMsgEntity> errorMsgEntityList = new ArrayList<>();
        Date date = Calendar.getInstance().getTime();
        ErrorMsgEntity errorMsgEntity = new ErrorMsgEntity();
        errorMsgEntity.setErrorCode("0xEE");
        errorMsgEntity.setErrorMessage(errorText);
        errorMsgEntity.setErrorDate(date);
        errorMsgEntityList.add(errorMsgEntity);
        DeviceSettingCheck2.insertErrorMsg(errorMsgEntityList);

        LoginFragment.isNfcLogin = false;
    }

    private WebApiAlertWindow webApiAlertWindow;

    public void showWebApiAlert(boolean isShow, String errorText) {

        if (webApiAlertWindow != null) {
            webApiAlertWindow.dismiss();
            webApiAlertWindow = null;
        }
        if (isShow) {

            //     insertEgymError(errorText);
//            List<ErrorMsgEntity> errorMsgEntityList = new ArrayList<>();
//            Date date = Calendar.getInstance().getTime();
//            ErrorMsgEntity errorMsgEntity = new ErrorMsgEntity();
//            errorMsgEntity.setErrorCode("0xEE");
//            errorMsgEntity.setErrorMessage(errorText);
//            errorMsgEntity.setErrorDate(date);
//            errorMsgEntityList.add(errorMsgEntity);
//            DeviceSettingCheck2.insertErrorMsg(errorMsgEntityList);


            webApiAlertWindow = new WebApiAlertWindow(this, errorText);
            webApiAlertWindow.showAtLocation(getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);
            webApiAlertWindow.setOnCustomDismissListener(new BasePopupWindow.OnCustomDismissListener() {
                @Override
                public void onStartDismiss(MsgEvent value) {

                }

                @Override
                public void onDismiss() {
                }
            });
        }
    }


}