package com.dyaco.spirit_commercial;

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
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
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
import androidx.core.splashscreen.SplashScreen;
import androidx.core.widget.PopupWindowCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.work.WorkInfo;

import com.bumptech.glide.Glide;
import com.corestar.calculation_libs.Calculation;
import com.corestar.libs.audio.AudioDeviceWatcher;
import com.corestar.libs.device.DeviceCab;
import com.corestar.libs.device.DeviceCsafe;
import com.corestar.libs.device.DeviceDyacoMedical;
import com.corestar.libs.device.DeviceGEM;
import com.corestar.libs.device.DeviceTvTuner;
import com.corestar.libs.device.GemSettings;
import com.corestar.libs.utils.GemSettingsFactory;
import com.dyaco.spirit_commercial.alert_message.BackgroundWindow;
import com.dyaco.spirit_commercial.alert_message.NotifyWarringWindow;
import com.dyaco.spirit_commercial.alert_message.SafetyKeyWindow;
import com.dyaco.spirit_commercial.alert_message.SplashWindow;
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
import com.dyaco.spirit_commercial.support.LoadingWindowAllB;
import com.dyaco.spirit_commercial.support.LoadingWindowEx;
import com.dyaco.spirit_commercial.support.MsgEvent;
import com.dyaco.spirit_commercial.support.RootTools;
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
import timber.log.Timber;

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
    public UartVM uartVM;
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

//    public IUartConsole uartConsole;
    public Calculation calculation;

    private SplashWindow splashWindow;

    public DownloadManagerCustom downloadManagerCustom;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(updateResourceFontSize(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SplashScreen.installSplashScreen(this);


        super.onCreate(savedInstanceState);
        //   MMKV.defaultMMKV().encode(IMAGE_KEY, 0);


        //garminUnPair();
        clearAppData();
        clearCookies();
        closePackage(this);
        deleteCache(this);


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


        //   Looper.myQueue().addIdleHandler(() -> {

        //  installExFatBinary();

        controlParameterMap = new HashMap<>();

        //UART初始化
        initUartConsole();

        //外部控制(FTMS、實體按鍵)
        initExControl();


        //關閉狀態列
//        new CommonUtils().hideStatusBar(1);
        RootTools.hideStatusBar();
        RootTools.hideNavigationBar();


        closePackage(this);


        getDeviceSpiritC().setUsbMode(DeviceDyacoMedical.USB_MODE.CHARGER);


        //     initHdmi();


        if (!isNetworkAvailable(getApplication())) {
            internetNotifyWarringWindow(true);
        }


        checkLimit();


        checkUploadWebApi();


        holdScreen();

        getBinding().setIsUs(isUs);


        //檢查Error Log 上傳
        new CallWebApi(this).apiUploadErrorLog();


        new WorkManagerUtil().checkUploadErrorLog();

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

            Timber.tag("RPM_CHECK").d("準備登出: ");

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
                Timber.tag("RPM_CHECK").d("US >>>> AUTO LOGOUT: ");
            } else {
                webApiLogout();
            }
        };

        MediaAppUtils.checkConsoleMediaApp();


        MediaAppUtils.checkForceUpdate();

        EgymUtil.init(this, deviceSettingViewModel, egymDataViewModel);






//        splashWindow = new SplashWindow(this);
//        splashWindow.showAtLocation(getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);
//
//        new RxTimer().timer(1000, number -> {
//            splashWindow.dismiss();
//            splashWindow = null;
//        });

//        showSplashWindow(true);

    }

//    public void showSplashWindow(boolean isShow) {
//        splashWindow = new SplashWindow(this);
//        splashWindow.showAtLocation(getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);
//
//    }


    private void installExFatBinary() {
        BinaryInstaller.installBinaryIfNeeded(this, success -> {
            if (success) {
                Timber.tag("USB_UPDATE").d("Binary 安裝成功，可執行掛載流程");
            } else {
                Timber.tag("USB_UPDATE").e("Binary 安裝失敗，請確認是否已 root 並允許權限");
            }
        });

    }


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


        Timber.tag("LIMIT_SHOW").d("LIMIT種類: " + deviceSettingBean.getUsageRestrictionsType() + ",\n" +
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
                    if (!a2dpConnectedDevices.isEmpty()) {
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


        UPDATE_FILE_PATH = getExternalCacheDir().getAbsolutePath();


        SETTING_FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/CoreStar/Dyaco/Spirit/default_settings.json";


        initEvent();


        App.UNIT_E = getApp().getDeviceSettingBean().getUnit_code();


        getBinding().setModeEnum(MODE);

        egymDataViewModel = new ViewModelProvider(this).get(EgymDataViewModel.class);

        uartVM = new ViewModelProvider(this).get(UartVM.class);
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


        initBluetooth();
        initReceiver(); // BITGYM, KINOMAP


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
                    Timber.tag("安裝未知").d("OK安裝未知來源權限: %s", result);
                }
            });

    //    public static boolean isGen3PingDone;
    public void initGem3() {

        Timber.tag("GEM3").d("initGem3: ");

        DeviceGEM.GEMEventListener gemEventListener = new DeviceGemEventListener(this, workoutViewModel, appStatusViewModel);
        getDeviceGEM().setEventListener(gemEventListener);
        getDeviceGEM().ping(state -> {
            Timber.tag("GEM3").d("onPing: %s", state);
            //全部斷線
            //同時呼叫 可能造成 bluetoothConfigurationMessageGetDeviceAddress 沒成功, 可試試延遲呼叫 << 沒用
            //   getDeviceGEM().customMessageDisconnectHeartRateDevice();

            Timber.tag("GEM3").d("取MAC bluetoothConfigurationMessageGetDeviceAddress: ");
            getDeviceGEM().bluetoothConfigurationMessageGetDeviceAddress();  // >>> onBluetoothConfigurationMessageGetDeviceAddress
            // TODO: 有機會  bluetoothConfigurationMessageGetDeviceAddress 沒回應

            if (state != DeviceGEM.STATE.SUCCESS) {
                Timber.tag("GEM3").e("ping:%s", state);
            }
        });

    }


    public UartConsoleManagerPF uartConsole;
    /**
     * UART
     */
    private void initUartConsole() {

//        uartConsole = new SpiritCommercialUart(workoutViewModel, this, deviceSettingViewModel, appStatusViewModel);
        uartConsole = new UartConsoleManagerPF(workoutViewModel, this, deviceSettingViewModel,uartVM, appStatusViewModel);
        uartConsole.initialize();
        Timber.tag("GEM3").d("initialize: ");





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
            Timber.e(e);
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
            Timber.e(e);
        }
    }

    public DeviceCab deviceCab;

    /**
     * CAB - CSAFE PORT一樣
     */
    public void initCab() {
        if (isEmulator) return;
        Timber.tag("CSAFE").d("initCab: ");
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
            Timber.e(e);
        }
    }

    public void removeCab() {
        if (deviceCab != null) {

            Timber.tag("CSAFE").d("removeCab: ");
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
            Timber.e(e);
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
            Timber.e(e);
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

        Timber.tag("GEM3").d("ftms名稱: %s", ftmsName);
        // getDeviceGEM().bluetoothConfigurationMessageGetDeviceName(); //onBluetoothConfigurationMessageGetDeviceName

        //設定名稱 onBluetoothConfigurationMessageSetDeviceName
        //     getDeviceGEM().bluetoothConfigurationMessageSetDeviceName(ftmsName);
        // onBluetoothConfigurationMessageSetDeviceName  <<< 有機會沒回應


        GemSettings settings = GemSettingsFactory.getInstance(ftmsEquipmentType);
        settings.setDeviceName(ftmsName);
        settings.setManufacturerName("Dyaco International Inc.");

        Timber.tag("GEM3").d("Start init");
        getDeviceGEM().init(settings, state -> {
            //gem3啟動
            Timber.tag("GEM3").d("onInit state: %s", state);
            if (state == DeviceGEM.STATE.SUCCESS) {
                //開啟廣播
                getDeviceGEM().bluetoothControlMessageStartAdvertising();
                appStatusViewModel.isGem3On.set(true);
                LiveEventBus.get(GYM3_ON_EVENT).post(true);

                Timber.tag("GEM3").d("onInit:GEM3開啟成功 ");
            } else {
                //  Toasty.error()
                // TODO: 有機會開啟失敗  state > TIMEOUT  > 但還是可以執行 > TIMEOUT 時間加長一些
                Timber.tag("GEM3").e("onInit:GEM3開啟失敗 ");

                getDeviceGEM().bluetoothControlMessageStartAdvertising();
                appStatusViewModel.isGem3On.set(true);
                LiveEventBus.get(GYM3_ON_EVENT).post(true);
                //        Toasty.error(getApp(),"GEM3" + state.name(),Toasty.LENGTH_LONG).show();

            }
        });

    }


    public HdmiIn hdmiIn;


    public void closeHdmi() {


        getBinding().vHdmiBg.setVisibility(View.GONE);
        getBinding().videoFullViewContainer.setVisibility(View.INVISIBLE);


        if (hdmiIn != null) {
            showLoadingEx(true, 500);//onPauseVideo 關閉
            try {
                hdmiIn.pauseVideo(); // onPauseVideo > pauseAudio > onPauseAudio
            } catch (Exception e) {
                Timber.e(e);
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
                    Timber.e(e);
                }
            });


        } else {
            showLoading(false);
            //   showLoadingEx(false);
            closeHdmi();
        }


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
            Timber.tag(GARMIN_TAG).d("garmin restart.");
            deviceSettingViewModel.isGarminEnabled.set(true);
        } else {
            Timber.tag(GARMIN_TAG).d("init garmin sdk.");
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

                            Timber.tag(GARMIN_TAG).d("init garmin service success..%s", result);

                            deviceSettingViewModel.isGarminEnabled.set(true);

                            garminUnPair();
                        } else {
                            Timber.tag(GARMIN_TAG).d("init garmin service error..%s", result);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Throwable t) {
                        deviceSettingViewModel.isGarminEnabled.set(false);

                        if (gRetry < 46) {
                            initGarmin();
                        }
                        Timber.tag(GARMIN_TAG).d("init garmin service failed.%s", t.getMessage());
                        gRetry += 1;
                        Timber.tag(GARMIN_TAG).d("onFailure:  RETRY:%s", gRetry);
                    }
                }, Executors.newSingleThreadExecutor());
            } catch (GarminHealthInitializationException e) {
                deviceSettingViewModel.isGarminEnabled.set(false);
                Timber.tag(GARMIN_TAG).e(e, "Garmin Health initialization failed.");
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
                Timber.tag(GARMIN_TAG).d("切換 開啟RealTimeData:" + device.address() + "," + currentGarminAddress);
            });
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public void stopListening() {
        if (DeviceManager.getDeviceManager().getPairedDevices() == null) return;
        for (com.garmin.health.Device device : DeviceManager.getDeviceManager().getPairedDevices()) {
            Timber.tag("GARMINNN").d("關閉: %s", device.address());
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

        Timber.tag("PPEPPEPEPEPF").d("quickStart: %s", time);

        workoutViewModel.selWorkoutTime.set(time);

        startWorkout(isBuzzer);
    }

    public void startWorkout(boolean isBuzzer) {
        if (SETTING_SHOW) return;

        if (CHECK_START) return;

        if (loadingWindow != null && loadingWindow.isShowing()) return;

        //不是快速啟動
        if (isTreadmill) {
            Timber.tag("UART_CONSOLE").d("Treadmill startWorkout: " + uartConsole.getDevStep() + "," + isEmulator);
            if (uartConsole.getDevStep() != DS_IDLE_STANDBY && !isEmulator) {
                CustomToast.showToast(this, getString(R.string.start_waiting));
                return;
            }
        } else {
            Timber.tag("UART_CONSOLE").d("startWorkout: %s", uartConsole.getDevStep());
            // TODO:
//            if (uartConsole.getDevStep() != DS_EMS_IDLE_STANDBY && !isEmulator) {
//                CustomToast.showToast(this, getString(R.string.start_waiting_bike));
//                return;
//            }
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
                    Timber.e(e);
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


        NavOptions navOptions = new NavOptions.Builder()
                .setEnterAnim(android.R.anim.fade_in)
                .setExitAnim(android.R.anim.fade_out)
                .build();

        try {
            navController.navigate(R.id.navigation_maintenance, null, navOptions);
        } catch (Exception e) {
            Timber.e(e);
        }
    }


    private void showGarmin() {

        if (CheckDoubleClick.isFastClick()) return;


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
            Timber.e(e);
        }
    }

    @SuppressLint("GestureBackNavigation")
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (popupWindow != null && popupWindow.isShowing()) return false;

        return super.dispatchTouchEvent(ev);
    }

    private void alphaFloatingBack(Boolean isShow) {

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


    @Override
    protected void onDestroy() {
        super.onDestroy();


        if (mAudioDeviceWatcher != null) {
            mAudioDeviceWatcher.removeListener();
            mAudioDeviceWatcher = null;
        }

        if (internetNotifyWarringWindow != null) {
            internetNotifyWarringWindow.dismiss();
            internetNotifyWarringWindow = null;
        }

        try {

            if (GarminHealth.isInitialized()) {
                // Stop the Health SDK to pause or close resource currently running in the application space.
                GarminHealth.stop(getApp());

                Timber.tag("GARMIN").d("onDestroy: GarminHealth.stop()");
            }

            showMediaMenu(false);

            //>>> BITGYM, KINOMAP
            if (mDyacoDataReceiver != null) {
                Timber.tag("GYM_APP").d("stopReceiveCtrl & stopReceiveInfo");

                mDyacoDataReceiver.stopReceiveCtrl(getApp());
                mDyacoDataReceiver.stopReceiveInfo(getApp());
            }
            //<<<

        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public SystemErrorWindow errorMsgWindow;

    public void showErrorMsg(ErrorInfo errorInfo, boolean isShow) {

        if (isShow) {
            if (errorMsgWindow != null) return;


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

            safetyKeyWindow = new SafetyKeyWindow(this);
            safetyKeyWindow.showAtLocation(getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);

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
            Timber.e(e);
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


    @Override
    protected void onStop() {
        super.onStop();

        btNotifyWindow(false);

        if (isShowMediaMenuOnStop && isScreenOn()) {
            try {


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
                Timber.e(e);
            }
        }

        try {
            showLoading(false);
            showLoading2(false);
            showMediaAlphaBackground(false);
            showErrorMsg(null, false);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();


        if (SETTING_SHOW) {
            overridePendingTransition(0, R.anim.fade_out_3);
        } else {

            overridePendingTransition(R.anim.fade_in_2s, 0);

//            Log.d("OOOIIIIEEE", "onResume: " + MainActivity.isOnStopBackToMainTraining);
            if (MainActivity.isOnStopBackToMainTraining) {
                LiveEventBus.get(ON_STOP_BACK_TO_MAIN_TRAINING).post(true);
            }
        }

        //WebView開啟時，點Gboard鍵盤設定會觸發Resume，導致menu被關閉
        if (!isWebViewOn) {
            showMediaMenu(false);
        }


        getBinding().vBackground.setVisibility(View.GONE);

        SETTING_SHOW = false;

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

        uuRl = BuildConfig.UPDATE_URL_GLOBAL;


        //    Log.d("##UpdateProcess##", "#######網址: " + uuRl + "update.json");


        Timber.tag(DownloadManagerCustom.TAG).d("checkUpdate: 呼叫 apiCheckUpdate()");
        BaseApi.request(BaseApi.createApi2(IServiceApi.class, uuRl).apiCheckUpdate(),
                new BaseApi.IResponseListener<>() {
                    @Override
                    public void onSuccess(UpdateBean data) {

                        LogS.printJson(DownloadManagerCustom.TAG, new Gson().toJson(data), "");

                        DeviceSettingBean d = getApp().getDeviceSettingBean();
                        try {
                            if (data.getVersionCode() > new CommonUtils().getLocalVersionCode()) {
                                Timber.tag(DownloadManagerCustom.TAG).d("######有新版本########");
                                //有新版本都要通知
                                new CallWebApi(MainActivity.this).apiNotifyNewestConsoleVersion(data);

                                if (d.isAutoUpdate()) {//自動更新有開啟，下載檔案
                                    Timber.tag(DownloadManagerCustom.TAG).d("<AUTO_UPDATE> ON，下載檔案");
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
                                    Timber.tag(DownloadManagerCustom.TAG).d("<AUTO_UPDATE> OFF，不需要下載，通知Dyaco Cloud有新的Console版本 ");
//                                    new CallWebApi(MainActivity.this).apiNotifyNewestConsoleVersion(data);
                                    new WorkManagerUtil().cancelWorkByTag(WORK_NOTIFY_UPDATE_MSG_TAG);
                                }
                            } else {
                                new WorkManagerUtil().cancelWorkByTag(WORK_NOTIFY_UPDATE_MSG_TAG);
                                Timber.tag(DownloadManagerCustom.TAG).d("沒有新版本，不需要更新， update.json版本：" + data.getVersionCode() + ", 目前版本：" + new CommonUtils().getLocalVersionCode());
                            }
                        } catch (Exception e) {
                            Timber.tag(DownloadManagerCustom.TAG).d("不需要更新:%s", e.getLocalizedMessage());
                            Timber.e(e);
                        }
                    }

                    @Override
                    public void onFail() {
                        Timber.tag(DownloadManagerCustom.TAG).d("連線失敗");
                    }
                });
    }


    /**
     * 取得 default_settings.json 的資料，存入 DeviceSettingBean(mmkv)
     */
    private void initDeviceModel() {


        DeviceSettingBean dd = getApp().getDeviceSettingBean();
        int modelCode = dd.getModel_code();//設備裡面存的

        if (modelCode != -1 && dd.getDeviceFirstLaunchTime() != null) {
            MODE = getMode(dd.getModel_code());
        } else {
            //初始值
            new InitProduct(getApplicationContext()).setProductDefault(CU1000ENT, TERRITORY_US);
        }


        new CommonUtils().mmkvDeviceSettingToViewModel(deviceSettingViewModel, getApp().getDeviceSettingBean());


        isTreadmill = deviceSettingViewModel.typeCode.get() == DeviceIntDef.DEVICE_TYPE_TREADMILL;


        Timber.tag("AppSetting").d("Model_code: %s", getApp().getDeviceSettingBean().getModel_code());
        Timber.tag("AppSetting").d("TerritoryCode: %s", getApp().getDeviceSettingBean().getTerritoryCode());

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


    /**
     * 更新上面頭像
     *
     * @param isSetting 從Console設定的 不更新urlPhoto，console沒照片
     */
    public void setAvatar(boolean isSetting) {
        if (userProfileViewModel.getAvatarId() != null) {
            int avatarRes = CommonUtils.getAvatarSelectedFromTag(userProfileViewModel.getAvatarId(), false);
            Glide.with(getApp())
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
                Timber.e(e);
            }
        }

        if (isSetting) return;
//        if (userProfileViewModel.getPhotoFileUrl() != null) {
        //有上傳圖片
        if (userProfileViewModel.getAvatarId() == null) {
            try {
                Glide.with(getApp())
                        .load(userProfileViewModel.getPhotoFileUrl())
//                    .diskCacheStrategy(DiskCacheStrategy.NONE)
//                    .skipMemoryCache(true)
                        .error(R.drawable.avatar_normal_1_default)
                        .circleCrop()
                        .into(getBinding().ivMemberIcon);
            } catch (Exception e) {
                Timber.e(e);
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
            Timber.tag(GARMIN_TAG).d("取得已配對裝置: " + d.address() + "," + d.connectionState() + "," + d.friendlyName());
            mGarminDeviceManager.forget(d.address());
        }
    }


    public void showVBG(boolean isShow) {
        getBinding().vBackground.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    @SuppressLint("MissingPermission")
    public void webApiLogout() {

        CommonUtils.wakeUpScreen(null);

        if (hdmiIn != null) {
            //切port3 > 拔掉port3 > 重啟app > hdmi 抓上一個port > port3沒東西  > anr
            hdmiIn.SwitchHdmiSource(PORT_EZ_CAST);
            mAudioDeviceWatcher = null;
        }

        if (pingGemTimer != null) {
            pingGemTimer.cancel();
            pingGemTimer = null;
        }

        //斷開藍芽音訊
        unBondAudio();
        if (mAudioDeviceWatcher != null) {
            //todo AudioDeviceWatcher.removeListener() 裡面呼叫了 unregisterReceiver()，但傳入的 receiver 沒註冊或已被註銷 →
            mAudioDeviceWatcher.removeListener();
        }

        appStatusViewModel.isGem3On.set(false);


        //   garminUnPair2();
        garminUnPair();


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


                            Timber.tag("WEB_API").d("MemberCheckoutMachineBean: %s", data.toString());
                            if (data.getSuccess()) {
                                COOKIE = "";
                                //  } else {
                                //     if (data.getErrorMessage() != null)
                                //    Toasty.warning(MainActivity.this, data.getErrorMessage(), Toasty.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFail() {
                            Timber.tag("EWWWWWWWWW").d("失敗");
                        }
                    });
        }

        Timber.tag("VVVVVEEEE").d("webApiLogout: ");
        //登出就重啟APP
        new RxTimer().timer(1000, number -> {
            try {
                mRestartApp();
                // restartApp(this);
                //   navController.navigate(MainDashboardFragmentDirections.actionMainBlankFragmentToNavigationLogin());
            } catch (Exception e) {
                Timber.e(e);
            }
        });
    }

    //重啟 Console APP, AppRestart
    public void mRestartApp() {

//        DeviceSettingBean d = getApp().getDeviceSettingBean();
//        d.setFakeStart(true);
//        getApp().setDeviceSettingBean(d);

        if (isEmulator || !isTreadmill) {
            Timber.tag("UART_CONSOLE").d("BIKE OR Emulator mRestartApp: APP重啟 ");
            restartApp(this);
        } else {
            Timber.tag("UART_CONSOLE").d("電跑: mRestartApp: APP重啟  ");
            uartConsole.setDevMainMode(DeviceDyacoMedical.MAIN_MODE.RESET);
            //  restartApp(this);
        }
    }


    public void setUserData(MemberCheckinMachineByQRCodeBean.DataMapDTO.DataDTO data) {

        Timber.tag("OOODDDODODO").d("登入資料: %s", data.toString());
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
                                //   Timber.tag("WEB_API").d("GetUnreadMessageCountFromMachineBean: %s", data.getDataMap().getData());
                                deviceSettingViewModel.alertNotifyCount.set(data.getDataMap().getData().getUnreadCount());
                            } else {
                                Toasty.warning(getApp(), "# " + data.getErrorMessage(), Toasty.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            Timber.e(e);
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


    public static boolean isGymApp = false;

    AnplusBean anplusBean = new AnplusBean();
    AnplusMachine anplusMachine = new AnplusMachine();  // BITGYM, KINOMAP
    DyacoDataEmitter mDyacoEmitter = new DyacoDataEmitter();
    DyacoDataReceiver mDyacoDataReceiver;               // BITGYM, KINOMAP

    public void initReceiver() {  // BITGYM, KINOMAP
        Timber.tag("GYM_APP").d("setListener, startReceiveInfo & startReceiveCtrl");

        mDyacoDataReceiver = DyacoDataReceiver.getInstance();
        mDyacoDataReceiver.setListener(dyacoDataListener);
        mDyacoDataReceiver.startReceiveInfo(this);
        mDyacoDataReceiver.startReceiveCtrl(this);
    }

    // BITGYM, KINOMAP
    DyacoDataReceiver.DyacoDataListener dyacoDataListener = new DyacoDataReceiver.DyacoDataListener() {
        @Override
        public void onInfoDataReceived(AnplusBean anplusBean) {
//            Timber.tag("GYM_APP").d("onInfoDataReceived%s", new Gson().toJson(anplusBean));
        }

        @Override
        public void onCtrlDataReceived(AnplusCtrlBean anplusCtrlBean) {
//            Timber.tag("GYM_APP").d("onCtrlDataReceived%s", new Gson().toJson(anplusCtrlBean));

            Timber.tag("GYM_APP").d("設定速度 = " + anplusCtrlBean.getTg_speed() +
                    " = " + anplusCtrlBean.getTg_speed() +
                    "設定揚升 = " + anplusCtrlBean.getTg_incline() +
                    "設定阻力 = " + anplusCtrlBean.getTg_level());
        }

        @Override
        public void onModelReceived() {

            // BitGym, Kinomap 要求取得Machine Info
            Timber.tag("GYM_APP").d("onModelReceived: 要求傳送Machine Info");

            runOnUiThread(() -> {
                try {
                    getMachineInfo();
                    //    Timber.tag("GYM_APP").d("傳送Machine Info: %s", new Gson().toJson(anplusMachine));
                    mDyacoEmitter.emit(getApp(), anplusMachine);
                } catch (Exception e) {
                    Timber.e(e);
                }
            });
        }

        @Override
        public void onModelDataReceived(AnplusMachine anplusMachine) {

            //   Timber.tag("GYM_APP").d("onModelDataReceived%s", new Gson().toJson(anplusMachine));

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
                Timber.tag("AutoLogout").w("偵測到無效或已損壞的 pauseAfter 值：" + pauseAfterSeconds + "，將使用預設值。");
            }
            // 如果 pauseAfterSeconds 是 0，則自然會使用上面的預設值，無需額外處理
            //4624633867356078080


        } catch (Exception e) {
            showException(e);
        }

        CHILD_LOCK_TIME = logoutDelayMillis;


        if (userInteractionHandler != null) userInteractionHandler.postDelayed(r, CHILD_LOCK_TIME);
//        if (userInteractionHandler != null) userInteractionHandler.postDelayed(r, 1 * 1000 * 10);
      //  Timber.tag("RPM_CHECK").d("startHandler: 開始childlock 計時 ");
    }


    public static long CHILD_LOCK_TIME = 8000;


    public void insertEgymError(String errorText) {
        Timber.tag("UPLOAD_ERROR_LOG").d("insertEgymError: %s", errorText);
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