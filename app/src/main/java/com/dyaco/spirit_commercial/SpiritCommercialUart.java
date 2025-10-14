//package com.dyaco.spirit_commercial;
//
//import static com.corestar.libs.device.DeviceSpiritC.BRIDGE_ERROR.AUTO_PAUSE;
//import static com.corestar.libs.device.DeviceSpiritC.BRIDGE_ERROR.SAFETY_KEY;
//import static com.corestar.libs.device.DeviceSpiritC.ERROR_STATUS.CLEAR;
//import static com.corestar.libs.device.DeviceSpiritC.ERROR_STATUS.NONE;
//import static com.corestar.libs.device.DeviceSpiritC.INCLINE_STATUS.STOP;
//import static com.corestar.libs.device.DeviceSpiritC.MAIN_MODE.APP_START;
//import static com.corestar.libs.device.DeviceSpiritC.MAIN_MODE.CALIBRATE;
//import static com.corestar.libs.device.DeviceSpiritC.MAIN_MODE.CONSOLE_ERROR;
//import static com.corestar.libs.device.DeviceSpiritC.MAIN_MODE.ENG;
//import static com.corestar.libs.device.DeviceSpiritC.MAIN_MODE.EUP;
//import static com.corestar.libs.device.DeviceSpiritC.MAIN_MODE.GS;
//import static com.corestar.libs.device.DeviceSpiritC.MAIN_MODE.IDLE;
//import static com.corestar.libs.device.DeviceSpiritC.MAIN_MODE.LWR_INIT;
//import static com.corestar.libs.device.DeviceSpiritC.MAIN_MODE.PAUSE;
//import static com.corestar.libs.device.DeviceSpiritC.MAIN_MODE.RESET;
//import static com.corestar.libs.device.DeviceSpiritC.MAIN_MODE.RUNNING;
//import static com.corestar.libs.device.DeviceSpiritC.TEST_CONTROL.ENABLE;
//import static com.corestar.libs.device.DeviceSpiritC.TIMEOUT_CONTROL.DISABLE;
//import static com.corestar.libs.device.DeviceSpiritC.TREADMILL_CALI_STATUS.CALI_INC_MAX;
//import static com.corestar.libs.device.DeviceSpiritC.TREADMILL_CALI_STATUS.CALI_INC_MIN;
//import static com.dyaco.spirit_commercial.App.MODE;
//import static com.dyaco.spirit_commercial.App.UNIT_E;
//import static com.dyaco.spirit_commercial.App.getApp;
//import static com.dyaco.spirit_commercial.App.getDeviceSpiritC;
//import static com.dyaco.spirit_commercial.MainActivity.isEmulator;
//import static com.dyaco.spirit_commercial.MainActivity.isTreadmill;
//import static com.dyaco.spirit_commercial.alert_message.WorkoutMediaControllerWindow.isMediaWorkoutController;
//import static com.dyaco.spirit_commercial.product_flavor.ModeEnum.CT1000ENT;
//import static com.dyaco.spirit_commercial.product_flavor.ModeEnum.T_spd16_km;
//import static com.dyaco.spirit_commercial.product_flavor.ModeEnum.T_spd16_mile;
//import static com.dyaco.spirit_commercial.product_flavor.ModeEnum.orgInclineAdArray;
//import static com.dyaco.spirit_commercial.support.CommonUtils.getRandomValue;
//import static com.dyaco.spirit_commercial.support.CommonUtils.restartApp;
//import static com.dyaco.spirit_commercial.support.WorkoutUtil.getInclineAd;
//import static com.dyaco.spirit_commercial.support.WorkoutUtil.getInclineLevel;
//import static com.dyaco.spirit_commercial.support.WorkoutUtil.getSpeedLevel;
//import static com.dyaco.spirit_commercial.support.WorkoutUtil.getSpeedValue;
//import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.DEVICE_TYPE_ELLIPTICAL;
//import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.DEVICE_TYPE_RECUMBENT_BIKE;
//import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.DEVICE_TYPE_TREADMILL;
//import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.DEVICE_TYPE_UPRIGHT_BIKE;
//import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.IMPERIAL;
//import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.METRIC;
//import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.OFF;
//import static com.dyaco.spirit_commercial.support.intdef.EventKey.AUTO_PAUSE_EVENT;
//import static com.dyaco.spirit_commercial.support.intdef.EventKey.EVT_ERR;
//import static com.dyaco.spirit_commercial.support.intdef.EventKey.FTMS_SET_TARGET_INCLINATION;
//import static com.dyaco.spirit_commercial.support.intdef.EventKey.FTMS_SET_TARGET_SPEED;
//import static com.dyaco.spirit_commercial.support.intdef.EventKey.FTMS_START_OR_RESUME;
//import static com.dyaco.spirit_commercial.support.intdef.EventKey.FTMS_STOP_OR_PAUSE;
//import static com.dyaco.spirit_commercial.support.intdef.EventKey.KEY_ENTER;
//import static com.dyaco.spirit_commercial.support.intdef.EventKey.KEY_UNKNOWN;
//import static com.dyaco.spirit_commercial.support.intdef.EventKey.NEW_UPDATE_VALUE;
//import static com.dyaco.spirit_commercial.support.intdef.EventKey.ON_MULTI_KEY;
//import static com.dyaco.spirit_commercial.support.intdef.EventKey.ON_USB_MODE_SET;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.CLICK_MINUS;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.CLICK_PLUS;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_80_PWM_LEVEL_RSP;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_90_NORMAL_MODE_RSP;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_99_DEV_INFO_RSP;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_99_UPDATE_DEV_INFO_RSP;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_A0_IDLE_RSP;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_A0_PAUSE_RSP;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_A0_PAUSE_STANDBY;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_A0_RUNNING_RSP;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_A0_RUNNING_STANDBY;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_A5_TARGET_RPM_RSP;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_A9_SYMM_ANGLE_RSP;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_B0_CALIBRATION_FRONT_MAX;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_B0_CALIBRATION_FRONT_MIN;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_B0_CALIBRATION_ONGOING;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_B0_CALIBRATION_REAR_MAX;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_B0_CALIBRATION_REAR_MIN;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_B2_IDLE_AD_READ_RSP;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_B3_IDLE_UNIT_RSP;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_B5_BREAK_SPD_RESET_RSP;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_B5_FINISH_AD_RESET_RSP;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_B5_IDLE_AD_RESET_RSP;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_B5_MAINTENANCE_AD_CHANGE_RSP;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_B5_PAUSE_AD_RESET_RSP;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_B5_RESUME_AD_CHANGE_RSP;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_B5_RUNNING_AD_CHANGE_RSP;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_B6_IDLE_SPEED_RATE_RSP;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_B7_APP_START_RSP;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_B7_CALIBRATION_RSP;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_B7_CLR_SAFETY_KEY_RSP;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_B7_CONSOLE_ERR_RSP;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_B7_ERR_SAFETY_KEY_RSP;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_B7_FINISH_IDLE_RSP;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_B7_FINISH_PAUSE_RSP;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_B7_IDLE_RSP;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_B7_MAINTENANCE_RSP;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_B7_PAUSE_GS_RSP;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_B7_PAUSE_RSP;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_B7_RESET_RSP;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_B7_RESUME_RUNNING_RSP;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_B7_RUNNING_RSP;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_B8_STOP_SC_RSP;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_B8_STOP_SC_STANDBY;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_BA_BREAK_MODE_RSP;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_BB_CALIBRATION_FRONT_RSP;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_BB_CALIBRATION_REAR_RSP;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_BB_CALIBRATION_START_RSP;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_BB_CALIBRATION_STOP_RSP;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_BD_CALIBRATION_RESULT_RSP;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_BE_TO_TC_RSP;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_BE_UPDATE_TO_TC_RSP;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_CALIBRATION_STANDBY;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_CONNECT_RSP;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_CONSOLE_ERR_STANDBY;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_EMS_IDLE_STANDBY;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_EMS_PAUSE;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_EMS_RUNNING;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_FA_CONSOLE_RESET_RSP;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_FA_UPDATE_RESET_RSP;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_FA_WAIT_COME_BACK;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_FINISH_SILENCE_RSP;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_IDLE_SILENCE_RSP;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_IDLE_STANDBY;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_LWR_INITIALIZING;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_MAINTENANCE_STANDBY;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_PAUSE_SILENCE_RSP;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_PAUSE_STANDBY;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_RESET_STANDBY;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_RUNNING_STANDBY;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.LONG_CLICK_MINUS;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.LONG_CLICK_PLUS;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.UE_INIT;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.UE_INVERTER;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.UE_MACHINE_TYPE;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.UE_MCU;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.UE_NONE;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.UE_RPM;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.UE_RS485;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.UE_SAFETY_KEY;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.UE_UART;
//import static com.dyaco.spirit_commercial.support.intdef.GENERAL.UE_UART_LWR;
//import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.MAX_LEVEL_MAX;
//import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.MAX_LEVEL_MIN;
//import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.ORG_FRONT_INCLINE_DIFF;
//import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.ORG_FRONT_INCLINE_MIN;
//import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.SPEED_CHANGE_TIME_MAX;
//import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.SPEED_CHANGE_TIME_MIN;
//import static com.dyaco.spirit_commercial.support.intdef.WorkoutIntDef.RT_LEVEL;
//import static com.dyaco.spirit_commercial.support.intdef.WorkoutIntDef.RT_POWER;
//
//import android.content.res.Resources;
//
//import com.corestar.libs.device.DeviceSpiritC;
//import com.corestar.libs.ota.LwrMcuUpdateManager;
//import com.corestar.libs.utils.HertzConverter;
//import com.dyaco.spirit_commercial.listener.DeviceSpiritcListener;
//import com.dyaco.spirit_commercial.listener.IUartConsole;
//import com.dyaco.spirit_commercial.product_flavor.DeviceSettingCheck2;
//import com.dyaco.spirit_commercial.support.LogUtil;
//import com.dyaco.spirit_commercial.support.RpmUtil;
//import com.dyaco.spirit_commercial.support.RxTimer;
//import com.dyaco.spirit_commercial.support.intdef.AppStatusIntDef;
//import com.dyaco.spirit_commercial.support.intdef.GENERAL;
//import com.dyaco.spirit_commercial.support.room.spirit.spirit_entity.ErrorMsgEntity;
//import com.dyaco.spirit_commercial.viewmodel.AppStatusViewModel;
//import com.dyaco.spirit_commercial.viewmodel.DeviceSettingBean;
//import com.dyaco.spirit_commercial.viewmodel.DeviceSettingViewModel;
//import com.dyaco.spirit_commercial.viewmodel.ErrorInfo;
//import com.dyaco.spirit_commercial.viewmodel.WorkoutViewModel;
//import com.dyaco.spirit_commercial.workout.programs.ProgramsEnum;
//import com.jeremyliao.liveeventbus.LiveEventBus;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//import es.dmoral.toasty.Toasty;
//
//
///**
// * <Treadmill> 開機流程
// * 1.initUartConsole()
// * 2.connect()
// * 3.onConnected() 連線後
// * 4.startUartFlowCommand() > 同時執行 5、6
// * 5.#getDeviceInfo()
// * 6.每秒執行 doEchoTask > #setEchoTreadmill()
// * 7.每秒收到 onEchoTreadmill > 每秒執行 checkTreadmillSteps
// * </Treadmill>
//
// * <EMS>
// * 在EMS橋接板都沒接其它週邊及外部電源時, 就會收到CH, 只有判斷在TEST_MODE = true時, 才會不發CH
// * 正常情況下(即都有接週邊及外部電源), 就會依情況發送錯誤
// * </EMS>
// */
//public class SpiritCommercialUart implements IUartConsole {
//    //    private RxTimer safeKeyTimer;
////    private boolean isRestarting = false;
//    public static final String TAG = "UART_CONSOLE";
//    WorkoutViewModel w;
//    DeviceSettingViewModel deviceSettingViewModel;
//    AppStatusViewModel appStatusViewModel;
//
//    public final int DEFAULT_PAUSE_AFTER_TIME = 30;
//    private int pauseTime = DEFAULT_PAUSE_AFTER_TIME;
//
//    int m_majorUartError = UE_NONE;
//    public int mLwrTimeoutCnt = 0;
//    public int mConsoleUartTimeoutCnt = 0;
//    public int mConsoleRS485ErrorCnt = 0;
//    public boolean m_uartInitialized = false;  // UART已執行初始化
//    public boolean mUartConnected = false;
//    public boolean m_ignoreUartResponse = false;
//    public boolean mIgnoreUartError;   // 忽略UART錯誤(暫不做錯誤顯示)
//    public boolean mCriticalError = false;
//    private int m_A9_anglePositive = 140;
//    private int m_A9_angleNegative = 140;
//    private int m_targetRpm;
//    private int m_AD_leftStepClockCnt;
//    private int m_AD_rightStepClockCnt;
//    private int m_AD_stepsForStepper;
//    private int m_80_reedSwitch;
//    private int m_80_angleSensor;
//    private int m_80_pwmLevel;
//    private int m_A6_rpmL;
//    private int m_A6_rpmR;
//    private int m_A6_revolution;
//    private int m_A6_pwmL;
//    private int m_A6_pwmR;
//    private int m_B4_symmetry;
//    private int m_B4_leftStep;
//    private int m_B4_rightStep;
//    private int m_B4_leftLen;
//    private int m_B4_rightLen;
//    private int m_B4_cadence;
//    private int m_B9_frontGradeAd;
//    private int m_B9_rearGradeAd;
//    private int m_B9_LeftStepSensorRawData;
//    private int m_B9_RightStepSensorRawData;
//    private boolean m_devResetAd;
//    private int m_devAccRateInSec;
//    private int m_devDecRateInSec;
//    private int m_devHrmStatus;
////    private int m_currentSpeedValue;
////    private int m_currentFrontGradeValue;
//
//    private int m_B0_currentRpm;
//    private int m_B0_electricCurrent;
//    public DeviceSpiritC.MACHINE_TYPE m_devMachineType;
//    public DeviceSpiritC.CONSOLE_MODE m_devConsoleMode;
//    private DeviceSpiritC.MAIN_MODE m_devMainMode = DeviceSpiritC.MAIN_MODE.UNKNOWN;
//    private DeviceSpiritC.MODEL m_devModel = DeviceSpiritC.MODEL.UNKNOWN;
//    private DeviceSpiritC.BEEP m_devBeep = DeviceSpiritC.BEEP.SHORT;
//    private DeviceSpiritC.ECHO_MODE m_devEchoMode = DeviceSpiritC.ECHO_MODE.AA;
//    private DeviceSpiritC.BRAKE_MODE m_BA_brakeMode;
//    private DeviceSpiritC.UNIT m_devUnit = DeviceSpiritC.UNIT.METRIC;
//    public int m_B0_frontGradeAd;
//    public boolean m_B0_devFrontGradeErr;
//    public boolean m_B0_devClbrFrontErr;
//
//    public @GENERAL.DeviceStep
//    int devStep;
//    public @GENERAL.DeviceStep
//    int lastDevStep;
//
//    MainActivity m;
//
//    //測試用
////    public DeviceSpiritC.TIMEOUT_CONTROL m_timeoutControl = DeviceSpiritC.TIMEOUT_CONTROL.DISABLE;   // 正常執行要 ENABLE, 未接實體機要測試收發用DISABLE
////    public DeviceSpiritC.TEST_CONTROL m_testControl = DeviceSpiritC.TEST_CONTROL.ENABLE;     // 正常執行要 DISABLE, 未接實體機要測試收發用ENABLE
//
//    //正式用
////    public DeviceSpiritC.TIMEOUT_CONTROL m_timeoutControl = DeviceSpiritC.TIMEOUT_CONTROL.ENABLE;   // 正常執行要 ENABLE, 未接實體機要測試收發用DISABLE
////    public DeviceSpiritC.TEST_CONTROL m_testControl = DeviceSpiritC.TEST_CONTROL.DISABLE;     // 正常執行要 DISABLE, 未接實體機要測試收發用ENABLE
//
//    //測試用2
////    public DeviceSpiritC.TIMEOUT_CONTROL m_timeoutControl = DeviceSpiritC.TIMEOUT_CONTROL.DISABLE;   // 正常執行要 ENABLE, 未接實體機要測試收發用DISABLE
////    public DeviceSpiritC.TEST_CONTROL m_testControl = DeviceSpiritC.TEST_CONTROL.DISABLE;     // 正常執行要 DISABLE, 未接實體機要測試收發用ENABLE
//
//    public DeviceSpiritC.TIMEOUT_CONTROL m_timeoutControl;
//    public DeviceSpiritC.TEST_CONTROL m_testControl;
//
//
//    public SpiritCommercialUart(WorkoutViewModel w, MainActivity m, DeviceSettingViewModel deviceSettingViewModel, AppStatusViewModel appStatusViewModel) {
//        this.w = w;
//        this.m = m;
//        this.deviceSettingViewModel = deviceSettingViewModel;
//        this.appStatusViewModel = appStatusViewModel;
//
//        //  if (TEST_MODE) {
//        if (BuildConfig.TEST_MODE) {
//            m_timeoutControl = DeviceSpiritC.TIMEOUT_CONTROL.DISABLE;   // 未接實體機要測試收發用DISABLE
//            m_testControl = DeviceSpiritC.TEST_CONTROL.ENABLE;     // 未接實體機要測試收發用ENABLE
//        } else {
//            m_timeoutControl = DeviceSpiritC.TIMEOUT_CONTROL.ENABLE;   // 正常執行要 ENABLE
//            m_testControl = DeviceSpiritC.TEST_CONTROL.DISABLE;     // 正常執行要 DISABLE
//        }
//    }
//
//
//    @Override
//    public void initialize() {
//
//        DeviceSpiritC.DeviceEventListener deviceEventListener = new DeviceSpiritcListener(this);
//        getDeviceSpiritC().registerListener(deviceEventListener);
//
//        loadSettings();
//
//        loadInclineAd();
//
//        // -> onConnected
//        if (!isEmulator) {
//            setDevStep(DS_CONNECT_RSP);
//            LogUtil.d(TAG, "connect() >>>>>>>");
//            getDeviceSpiritC().connect(); //> onConnected
//        }
//    }
//
//    private void loadSettings() {
//
//        // 取得加速時間, 減速時間, 機型
////        m_devAccRateInSec = m_medicalBean.getAccSpdChangeTime();
////        m_devDecRateInSec = m_medicalBean.getDecSpdChangeTime();
//        m_devAccRateInSec = 60;
//        m_devDecRateInSec = 60;
//
//        // 使數值合法化, 避免重送指令
//        setDevSpeedAccDecTimeLegalization();
//
//        // 設定LWR非電跑的子類型
//        if (!isTreadmill)
//            setEmsMachineType();
//    }
//
//    //加減速時間
//    private void setDevSpeedAccDecTimeLegalization() {
//        m_devAccRateInSec = Math.min(m_devAccRateInSec, SPEED_CHANGE_TIME_MAX);
//        m_devAccRateInSec = Math.max(m_devAccRateInSec, SPEED_CHANGE_TIME_MIN);
//        m_devDecRateInSec = Math.min(m_devDecRateInSec, SPEED_CHANGE_TIME_MAX);
//        m_devDecRateInSec = Math.max(m_devDecRateInSec, SPEED_CHANGE_TIME_MIN);
//    }
//
//    @Override
//    public void setDevStep(@GENERAL.DeviceStep int devStep) {
//        this.devStep = devStep;
////        CommonUtils.getMemory();
//        if (lastDevStep != this.devStep) {
//            //   LogUtil.d(TAG, "目前STEP: " + getMyStepString() + "," + devStep);
//            lastDevStep = this.devStep;
//        }
//
//        w.isWorkoutReadyStart.set(devStep == DS_IDLE_STANDBY || devStep == DS_EMS_IDLE_STANDBY);
//    }
//
//    @Override
//    public void setDevMainMode(DeviceSpiritC.MAIN_MODE mainMode) {
//        if (!mUartConnected) return;
//
//        //onDataSend > 5B:03:[B7]:[02]:44:5D
//
////        LWR_INIT(0),
////        IDLE(1), 01
////        RUNNING(2), 02
////        PAUSE(3), 03
////        GS(4),
////        SAFE_STEP(5),
////        APP_START(80),            // 0x50
////        CONSOLE_ERROR(85),  // 0x55
////        ENG(112),
////        CALIBRATE(144),
////        CORESTAR(176),
////        UNKNOWN(255);
//        switch (mainMode) {
//            case RESET:
//                LogUtil.d(TAG, "設定進入 RESET (DS_B7_RESET_RSP)");
//                setDevStep(DS_B7_RESET_RSP);
//                setMainModeTreadmill(RESET);
//                break;
//
//            case LWR_INIT:  // Lwr init(Normal rsp)
//                break;
//
//            case IDLE:
//                enterIdleMode();
//                break;
//
//            case RUNNING:   // Running (Normal rsp)
//                //  getDeviceSpiritC().connect();
//                if (devStep == DS_IDLE_STANDBY) {
//                    LogUtil.d(TAG, "setDevMainMode DS_IDLE_STANDBY, 設定進入RUNNING模式 (DS_B7_RUNNING_RSP)");
//                    setDevStep(DS_B7_RUNNING_RSP);
//                    getDeviceSpiritC().setMainModeTreadmill(RUNNING);
//                } else if (devStep == DS_PAUSE_STANDBY) { // 從DS_PAUSE_STANDBY 設定為RUNNING，就會進入這裡
//                    LogUtil.d(TAG, "setDevMainMode DS_PAUSE_STANDBY, 設定回到RUNNING模式 (DS_B7_RESUME_RUNNING_RSP)");
//                    setDevStep(DS_B7_RESUME_RUNNING_RSP);
//                    getDeviceSpiritC().setMainModeTreadmill(RUNNING);
//                }
//                break;
//
//            case PAUSE:     // Pause (Normal rsp)
//                if (devStep == DS_B5_RUNNING_AD_CHANGE_RSP || devStep == DS_RUNNING_STANDBY) {
//                    LogUtil.d(TAG, "DS_B5_RUNNING_AD_CHANGE_RSP | DS_RUNNING_STANDBY, 設定 進入PAUSE模式 (DS_B7_PAUSE_RSP)");
//                    setDevStep(DS_B7_PAUSE_RSP);
////                    getDeviceSpiritC().setMainModeTreadmill(!deviceSettingViewModel.gsMode.get() ? GS : PAUSE);
//                    getDeviceSpiritC().setMainModeTreadmill(deviceSettingViewModel.gsMode.get() ? GS : PAUSE);
//                }
//                break;
//
//            case GS:        // GS_mode (Normal rsp)
//                if (devStep == DS_B5_RUNNING_AD_CHANGE_RSP || devStep == DS_RUNNING_STANDBY) {
//                    LogUtil.d(TAG, "DS_B5_RUNNING_AD_CHANGE_RSP | DS_RUNNING_STANDBY, 設定 進入PAUSE-GS模式 (DS_B7_PAUSE_GS_RSP)");
//                    setDevStep(DS_B7_PAUSE_GS_RSP);
//                    getDeviceSpiritC().setMainModeTreadmill(GS);
//                }
//                break;
//
//            case APP_START: // APP_start (Normal rsp)
//                setDevStep(DS_B7_APP_START_RSP);
//                getDeviceSpiritC().setMainModeTreadmill(APP_START);
//                break;
//
//            case ENG:       // Maintenance Mode(ENG rsp)
//                LogUtil.d(TAG, "[setDevMainMode] 設定進入 {工程模式ENG(0xB7)}");
//                setDevStep(DS_B7_MAINTENANCE_RSP);
//                getDeviceSpiritC().setMainModeTreadmill(ENG);
//                break;
//
//            case CALIBRATE: // Calibration (Factory rsp)
//                if (devStep == DS_IDLE_STANDBY) {
//                    LogUtil.d(TAG, "DS_IDLE_STANDBY 設定 進入校正模式 (DS_B7_CALIBRATION_RSP)}");
//                    setDevStep(DS_B7_CALIBRATION_RSP);
//                    getDeviceSpiritC().setMainModeTreadmill(CALIBRATE);
//                }
//                break;
//
//            case CORESTAR: // CoreStar Mode (for mass production)
//                getDeviceSpiritC().setMainModeTreadmill(mainMode);
//                break;
//
//            case CONSOLE_ERROR:
//                LogUtil.d(TAG, "設定進入CONSOLE_ERROR");
//                setDevStep(DS_B7_CONSOLE_ERR_RSP);
//                getDeviceSpiritC().setMainModeTreadmill(CONSOLE_ERROR);
//
//                break;
//        }
//    }
//
//    private void enterIdleMode() {
//        switch (devStep) {
//            case DS_CALIBRATION_STANDBY:
//                //在這裡才做grade AD table的更新
//                LogUtil.d(TAG, "[enterIdleMode] 結束校正模式, 設定{進入IDLE模式(0xB7)}");
////                if (!m_B0_devClbrFrontErr && !m_B0_devClbrRearErr) {
////                    updateFrontInclineAd();
////                }
//                setDevStep(DS_B7_IDLE_RSP);
//                getDeviceSpiritC().setMainModeTreadmill(IDLE);
//                break;
//
//            case DS_MAINTENANCE_STANDBY:
//                LogUtil.d(TAG, "[enterIdleMode] 結束工程模式, 設定 {進入IDLE模式(0xB7)}");
//                setDevStep(DS_B7_IDLE_RSP);
//                getDeviceSpiritC().setMainModeTreadmill(IDLE);
//                break;
//            default:
//                //  LogUtil.d(TAG, "[enterIdleMode] 無符合，目前STEP:" + getMyStepString());
//                break;
//        }
//    }
//
//
//    private void loadInclineAd() {
//        String inclineAd = getApp().getDeviceSettingBean().getDsFrontInclineAd();
//
//        LogUtil.d(TAG, "loadInclineAd: " + inclineAd);
//
//        if (inclineAd != null && !"".equals(inclineAd)) {
//            int[] levelAd = Arrays.stream(inclineAd.split("#"))
//                    .mapToInt(Integer::parseInt)
//                    .toArray();
//            w.setFrontInclineAdList(Arrays.stream(levelAd).boxed().collect(Collectors.toList()));
//
//        } else {
//            DeviceSettingBean deviceSettingBean = getApp().getDeviceSettingBean();
//            StringBuilder incAdStr = new StringBuilder();
//            for (int ad : orgInclineAdArray) {
//                incAdStr.append(ad).append("#");
//                w.getFrontInclineAdList().add(ad);
//            }
//            deviceSettingBean.setDsFrontInclineAd(incAdStr.toString());
//            getApp().setDeviceSettingBean(deviceSettingBean);
//        }
//    }
//
//    /**
//     * 更新Incline
//     *
//     * @param frontInclineMax frontInclineMax
//     * @param frontInclineMin frontInclineMin
//     */
//    private void updateFrontInclineAd(int frontInclineMax, int frontInclineMin) {
//        DeviceSettingBean deviceSettingBean = getApp().getDeviceSettingBean();
//        StringBuilder incAdStr = new StringBuilder();
//        int newDiff = frontInclineMax - frontInclineMin;
//        for (int orgInclineAd : orgInclineAdArray) {
//            int newInclineAd = frontInclineMin + (newDiff * (orgInclineAd - ORG_FRONT_INCLINE_MIN)) / ORG_FRONT_INCLINE_DIFF;
//            incAdStr.append(newInclineAd).append("#");
//        }
//        LogUtil.d(TAG, "updateFrontInclineAd: 存入新AD資料:" + incAdStr);
//        deviceSettingBean.setDsFrontInclineAd(incAdStr.toString());
//        getApp().setDeviceSettingBean(deviceSettingBean);
//    }
//
//    private void checkTreadmillSteps(DeviceSpiritC.MAIN_MODE mainMode, DeviceSpiritC.INCLINE_STATUS frontGradeStatus, DeviceSpiritC.INCLINE_STATUS rearGradeStatus, DeviceSpiritC.INIT_STATUS initStatus, DeviceSpiritC.INVERTER_ERROR converterErr, List<DeviceSpiritC.BRIDGE_ERROR> bridgeErr, DeviceSpiritC.TREADMILL_CALI_STATUS caliStatus, int frontGradeAd, int rearGradeAd, int rpm, DeviceSpiritC.SAFE_KEY safeKey) {
//
//
//        //  getMemory();
//        //確認command是否已有回覆, 未回覆則重送指令
//
//        //  LogUtil.d(TAG, "####checkTreadmillSteps: "+ getMyStepString() +","+ devStep);
//
////        if (devStep != DS_B7_RESET_RSP) {
////            if ((mainMode == LWR_INIT) && (devStep != DS_LWR_INITIALIZING)) {
////
////                setDevStep(DS_LWR_INITIALIZING);
////                LogUtil.d(TAG, "非在初始化狀態, 但下控正在初始化中, 重新設定在初始化中 (DS_LWR_INITIALIZING)");
////            }
////        }
//
//        if ((mainMode == LWR_INIT) && (devStep != DS_LWR_INITIALIZING)) {
//            if (!(devStep >= DS_FA_UPDATE_RESET_RSP && devStep <= DS_BE_UPDATE_TO_TC_RSP)) {
//                setDevStep(DS_LWR_INITIALIZING);
//                LogUtil.d(TAG, "非在初始化狀態, 但下控正在初始化中, 重新設定在初始化中 (DS_LWR_INITIALIZING)");
//            }
//        }
//
//
//        switch (devStep) {
//            case DS_FA_UPDATE_RESET_RSP:
//                LogUtil.d(TAG, "尚未收到LWR重置指令, 重送指令");
//                getDeviceSpiritC().setResetTreadmill();
//                break;
//
//            case DS_B7_RESET_RSP: //#SafeKey6 設定了 MainMode RESET ，等待下控回覆中
//                LogUtil.d(TAG, "尚未進入RESET模式, 重送指令");
//                setMainModeTreadmill(RESET);
//                break;
//
//            case DS_B5_FINISH_AD_RESET_RSP:
//                boolean breakWorkout = !w.isWorkoutDone();
//                //   boolean gsMode = !deviceSettingViewModel.gsMode.get();
//                boolean gsMode = deviceSettingViewModel.gsMode.get();
//                LogUtil.d(TAG, "DS_B5_FINISH_AD_RESET_RSP 結束訓練, 設定 {速度及揚升歸零(0xB5)} 重送");
//                resetSpeedAndIncline(!gsMode || !breakWorkout);
//                break;
//            case DS_B5_IDLE_AD_RESET_RSP:
//                LogUtil.d(TAG, "checkTreadmillSteps: DS_B5_IDLE_AD_RESET_RSP 重送指令");
//                resetSpeedAndIncline(false);
//                break;
//            case DS_B6_IDLE_SPEED_RATE_RSP:
//                LogUtil.d(TAG, "checkTreadmillSteps: DS_B6_IDLE_SPEED_RATE_RSP 重送指令");
//                getDeviceSpiritC().setTargetSpeedRateTreadmill(m_devAccRateInSec, m_devDecRateInSec);
//                break;
//            case DS_B3_IDLE_UNIT_RSP:
//                LogUtil.d(TAG, "checkTreadmillSteps: DS_B3_IDLE_UNIT_RSP  重送指令");
//                setDevUnit();
//                break;
//            case DS_B5_PAUSE_AD_RESET_RSP:
//                LogUtil.d(TAG, "checkTreadmillSteps DS_B5_PAUSE_AD_RESET_RSP - checkTreadmillSteps: 重送指令");
//                resetSpeedAndIncline(false);
//                break;
//            case DS_B5_RUNNING_AD_CHANGE_RSP:
//                LogUtil.d(TAG, "checkTreadmillSteps STEP: DS_B5_RUNNING_AD_CHANGE_RSP 重送指令");
//                setTargetSpeedAndInclineTreadmill(w.currentSpeedLevel.get(), w.currentFrontInclineAd.get());
//                break;
//            // case DS_B2_RUNNING_AD_READ_RSP:
//            case DS_B2_IDLE_AD_READ_RSP:
//                LogUtil.d(TAG, "checkTreadmillSteps (DS_B2_RUNNING_AD_READ_RSP | DS_B2_IDLE_AD_READ_RSP) 尚未(/數值不符)應速度及前後揚升的數值, 重送指令  ");
//                getDeviceSpiritC().getCurrentSpeedAndInclineTreadmill();
//                break;
//            case DS_B7_APP_START_RSP:
//                LogUtil.d(TAG, "checkTreadmillSteps 尚未進入APP_START模式, 重送指令");
//                getDeviceSpiritC().setMainModeTreadmill(APP_START);
//                break;
//
//            case DS_BE_TO_TC_RSP:
//            case DS_BE_UPDATE_TO_TC_RSP:
//                LogUtil.d(TAG, "checkTreadmillSteps 尚未回應逾時控制及測試控制設定, 重送指令");
//                setUartTimeoutControl();
//                break;
//
////            case DS_FA_CONSOLE_RESET_RSP:
////                LogUtil.d(TAG, "checkTreadmillSteps 尚未回應LWR重置, 重送指令");
////                getDeviceSpiritC().setResetTreadmill();
////                break;
//
//            case DS_B7_CLR_SAFETY_KEY_RSP:
//                //#SafeKey5 安全鎖已插上 設定 MainMode >> RESET
//                if (safeKey == DeviceSpiritC.SAFE_KEY.ON && !bridgeErr.contains(SAFETY_KEY)) {
//
//                    //  if (w.isSafeKey.get()) return; //安全鎖已插上
//
//                    //插上安全鎖後, LWR會進行初始化
//                    //    LogUtil.d(TAG, "已插上安全鎖, 關閉錯誤訊息" + w.isSafeKey.get());
//
//
//                    LogUtil.d(TAG, "已插上安全鎖 RPM:" + rpm);
//
//                    if (m.safetyKeyWindow != null) {
//                        m.safetyKeyWindow.setText(R.string.Wait_Speed_ZERO);
//                    }
//
//                    //拔安全鎖 -> 復位 & 確認rpm = 0 -> 等3秒 -> 下main mode = reset -> 確認 main mode = reset -> restart app
////                    if (rpm <= 0) {
////                        if (isRestarting) return;
////
////                        isRestarting = true;
////                        if (safeKeyTimer != null) {
////                            safeKeyTimer.cancel();
////                            safeKeyTimer = null;
////                        }
////                        safeKeyTimer = new RxTimer();
////                        safeKeyTimer.timer(3000, number -> {
////                            if (!isRestarting) return;
////                            //安全鎖插回去時, 先下達指令
////                            LogUtil.d(TAG, "重啟: ");
////                            m.mRestartApp();
////                        });
////                    }
//
//
////                    //安全鎖插回去時, 先下達指令
//                    m.mRestartApp(); //等onMainModeTreadmill回覆
//
//                }
//                break;
//
//            case DS_BA_BREAK_MODE_RSP:
//                LogUtil.d(TAG, "checkTreadmillSteps 尚未回應剎車模式設定, 重送指令");
//                getDeviceSpiritC().setBrakeModeTreadmill(m_BA_brakeMode);
//                break;
//
//            case DS_99_DEV_INFO_RSP:
//                LogUtil.d(TAG, "DEVICE_INFO 尚未收到裝置資訊, 重送指令");
//                getDeviceSpiritC().getDeviceInfo();
//                break;
//
//            case DS_B7_IDLE_RSP:
//                LogUtil.d(TAG, "checkTreadmillSteps 尚未回應進入IDLE模式, 重送指令");
//                getDeviceSpiritC().setMainModeTreadmill(IDLE);
//                break;
//
//            case DS_B5_RESUME_AD_CHANGE_RSP:
//            case DS_B5_MAINTENANCE_AD_CHANGE_RSP:
//                if (devStep == DS_B5_RESUME_AD_CHANGE_RSP) {
//                    LogUtil.d(TAG, "checkTreadmillSteps 尚未接收速度及揚升設定, 重送指令 (DS_B5_RESUME_AD_CHANGE_RSP)");
//                } else {
//                    LogUtil.d(TAG, "checkTreadmillSteps 尚未接收速度及揚升設定, 重送指令 (DS_B5_MAINTENANCE_AD_CHANGE_RSP)");
//                }
//                setTargetSpeedAndInclineTreadmill(w.currentSpeedLevel.get(), w.currentFrontInclineAd.get());
//                break;
//
//            case DS_B7_RESUME_RUNNING_RSP:
//                LogUtil.d(TAG, "checkTreadmillSteps 尚未回到RUNNING模式, 重送指令");
//                getDeviceSpiritC().setMainModeTreadmill(RUNNING);
//                break;
//
//            case DS_B7_PAUSE_RSP:
//                LogUtil.d(TAG, "checkTreadmillSteps 尚未進入PAUSE模式, 重送指令");
//                getDeviceSpiritC().setMainModeTreadmill(PAUSE);
//                break;
//
//            case DS_B7_PAUSE_GS_RSP:
//                LogUtil.d(TAG, "checkTreadmillSteps 尚未進入GS模式, 重送指令");
//                getDeviceSpiritC().setMainModeTreadmill(GS);
//                break;
//
//            case DS_FINISH_SILENCE_RSP:
//                if ((rpm == 0 && frontGradeStatus == STOP) || m_ignoreUartResponse) {// 若是揚升在停止狀態, 或是揚升錯誤
//                    LogUtil.d(TAG, "checkTreadmillSteps rpm為0, 前後揚升在停止狀態, 設定進入PAUSE模式 (DS_B7_FINISH_PAUSE_RSP)");
//                    setDevStep(DS_B7_FINISH_PAUSE_RSP);
//                    getDeviceSpiritC().setMainModeTreadmill(PAUSE);
//                } else {
//                    LogUtil.d(TAG, "checkTreadmillSteps 結束訓練, 仍在等待rpm = 0及前後揚升停止，rpm=" + rpm + "," + frontGradeStatus);
//                }
//                break;
//
//            case DS_B7_RUNNING_RSP:
//                LogUtil.d(TAG, "checkTreadmillSteps 尚未進入RUNNING模式, 重送指令");
//                getDeviceSpiritC().setMainModeTreadmill(RUNNING);
//                break;
//
//            case DS_LWR_INITIALIZING:
//                if (mainMode == LWR_INIT) {
//
//                    switch (initStatus) {
//                        case INITIALIZING:
//                            LogUtil.d(TAG, "checkTreadmillSteps 在INIT模式, 正在初始化中");
//                            break;
//
//                        case SUCCESS:  // 初始化成功
//                            LogUtil.d(TAG, "checkTreadmillSteps 在INIT模式, 初始化結束(成功), 設定進入APP_START模式 (DS_B7_APP_START_RSP)");
//                            setDevMainMode(DeviceSpiritC.MAIN_MODE.APP_START);
//
//                            break;
//
//                        case FAIL:    // 初始化失敗
//                            LogUtil.d(TAG, "checkTreadmillSteps 在INIT模式, 初始化結束(失敗), converterErr = " + converterErr + ", 設定進入APP_START模式 (DS_B7_APP_START_RSP)");
//                            setDevMainMode(DeviceSpiritC.MAIN_MODE.APP_START);
//                            break;
//
//                        default:
//                            break;
//                    }
//                } else {
//                    LogUtil.d(TAG, "checkTreadmillSteps 非在初始化模式, 設定進入APP_START模式 (DS_B7_APP_START_RSP)");
//                    setDevStep(DS_B7_APP_START_RSP);
//                    setMainModeTreadmill(APP_START);
//                }
//                break;
//
//            case DS_IDLE_SILENCE_RSP:
//                LogUtil.d(TAG, "checkTreadmillSteps: DS_IDLE_SILENCE_RSP ");
//                if (mainMode == IDLE) {
//                    if ((rpm == 0 && (frontGradeStatus == DeviceSpiritC.INCLINE_STATUS.STOP || m_B0_devFrontGradeErr)) || m_ignoreUartResponse) {
//                        LogUtil.d(TAG, "checkTreadmillSteps RPM為0, 前後揚升在停止狀態, 設定加減速時間 (DS_B6_IDLE_SPEED_RATE_RSP）" + frontGradeStatus);
//                        setDevStep(DS_B6_IDLE_SPEED_RATE_RSP);
//                        getDeviceSpiritC().setTargetSpeedRateTreadmill(m_devAccRateInSec, m_devDecRateInSec);
//                    } else {
//
//                        LogUtil.d(TAG, "checkTreadmillSteps 等待RPM為0及前後揚升在停止狀態, rpm = " + rpm + ", frontGradeStatus = " + frontGradeStatus);
//                    }
//                } else {
//
//                    LogUtil.d(TAG, "checkTreadmillSteps DS_IDLE_SILENCE_RSP 目前模式: " + mainMode);
//                }
//                break;
//
//            case DS_PAUSE_SILENCE_RSP:
//                if (mainMode == PAUSE || mainMode == GS) {
//                    LogUtil.d(TAG, "checkTreadmillSteps DS_PAUSE_SILENCE_RSP:前揚升狀態: " + frontGradeStatus + ", RPM:" + rpm);
//                    if ((rpm == 0 && frontGradeStatus == STOP) || m_ignoreUartResponse) {
//                        LogUtil.d(TAG, "rpm為0, 前後揚升在停止狀態, 待命中 (DS_PAUSE_STANDBY), 允許user按RESUME");
//                        setDevStep(DS_PAUSE_STANDBY);
//                    }
//                }
//                break;
//
//            case DS_B7_MAINTENANCE_RSP:
//                setMainModeTreadmill(ENG);
//                LogUtil.d(TAG, "checkTreadmillSteps DS_B7_MAINTENANCE_RSP, 重送指令");
//                break;
//            case DS_B7_CALIBRATION_RSP:
//                LogUtil.d(TAG, "checkTreadmillSteps 尚未進入揚升校正模式, 重送指令");
//                setMainModeTreadmill(CALIBRATE);
//                break;
//
//            case DS_B0_CALIBRATION_ONGOING:
//            case DS_B0_CALIBRATION_FRONT_MAX:
//            case DS_B0_CALIBRATION_FRONT_MIN:
//            case DS_B0_CALIBRATION_REAR_MAX:
//            case DS_B0_CALIBRATION_REAR_MIN:
//
//                if (mainMode == CALIBRATE || mainMode == ENG) {
//
//                    //關閉Progress
//                    if (caliStatus != CALI_INC_MAX && caliStatus != CALI_INC_MIN)
//                        m.showLoading2(false);
//
//                    boolean calibrationDone = false;
//                    switch (caliStatus) {
//                        case STOP:
//                            LogUtil.d(TAG, "checkTreadmillSteps 未開始校正 或 校正中途停止");
//                            //  Toasty.error(getApp(), "INC_ERROR STOP", Toasty.LENGTH_LONG).show();
//                            //   calibrationDone = true;
//                            break;
//
//                        case CALI_INC_MAX:
//                            LogUtil.d(TAG, "checkTreadmillSteps 開始校正 前揚升最大值 (DS_B0_CALIBRATION_FRONT_MAX)");
//                            setDevStep(DS_B0_CALIBRATION_FRONT_MAX);
//                            break;
//
//                        case CALI_INC_MIN:
//                            LogUtil.d(TAG, "checkTreadmillSteps 開始校正 前揚升最小值 (DS_B0_CALIBRATION_FRONT_MIN)");
//                            setDevStep(DS_B0_CALIBRATION_FRONT_MIN);
//                            break;
//
//                        case FINISH:
//                            LogUtil.d(TAG, "checkTreadmillSteps 校正結束, 讀取前後揚升的最高及最低點 (DS_BD_CALIBRATION_RESULT_RSP)");
//                            m_B0_devClbrFrontErr = false;
//                            setDevStep(DS_BD_CALIBRATION_RESULT_RSP);
//                            getDeviceSpiritC().getInclineRangeTreadmill(); // >> onInclineRangeTreadmill
//
//                            Toasty.success(getApp(), "SUCCESS", Toasty.LENGTH_LONG).show();
//                            break;
//
//                        case INC_ERROR:
//                            LogUtil.d(TAG, "checkTreadmillSteps 校正結束, 前揚升發生錯誤");
//                            m_B0_devClbrFrontErr = true;
//                            calibrationDone = true;
//                            Toasty.error(getApp(), "INC_ERROR Failure", Toasty.LENGTH_LONG).show();
//                            break;
//
////                        case DEC_ERROR:
////                            LogUtil.d(TAG, "校正結束, 後揚升發生錯誤");
////                            m_B0_devClbrRearErr = true;
////                            calibrationDone = true;
////                            break;
//
//                        case BOTH_ERROR:
//                            LogUtil.d(TAG, "checkTreadmillSteps 校正結束, 前後揚升發生錯誤");
//                            m_B0_devClbrFrontErr = true;
//                            calibrationDone = true;
//                            //    Toasty.error(getApp(), "BOTH_ERROR Failure", Toasty.LENGTH_LONG).show();
//                            break;
//                    }
//
//                    if (calibrationDone) {
//                        LogUtil.d(TAG, "checkTreadmillSteps 校正結束, 校正模式待命中 (DS_CALIBRATION_STANDBY)");
//                        setDevStep(DS_CALIBRATION_STANDBY);
//                        m.showLoading2(false);
//                    }
//                } else {
//                    m.showLoading2(false);
//                    LogUtil.d(TAG, "checkTreadmillSteps 校正 mainMode: " + mainMode);
//                }
//                break;
//        }
//    }
//
//    public void setUartTimeoutControl() {
//        //DISABLE & ENABLE 是測試模式
//        m_ignoreUartResponse = (m_timeoutControl == DISABLE) && (m_testControl == ENABLE);
//
//        getDeviceSpiritC().setTimeoutControlTreadmill(m_timeoutControl, m_testControl);
//    }
//
//    private void setMainModeTreadmill(DeviceSpiritC.MAIN_MODE mode) {
//        if (!mUartConnected) return;
//        getDeviceSpiritC().setMainModeTreadmill(mode);
//        //  LogUtil.d(TAG, ">>>>>>>>>>setMainModeTreadmill");
//    }
//
//    private void enterConsoleErrorMode(@GENERAL.UartError int uartErr, String errDesc) {
//        if (m_majorUartError != uartErr) {
//            //#SafeKey2
//            setDevStep(uartErr == UE_SAFETY_KEY ? DS_B7_ERR_SAFETY_KEY_RSP : DS_B7_CONSOLE_ERR_RSP);
//            setMainModeTreadmill(CONSOLE_ERROR);
//            LogUtil.d(TAG, "enterConsoleErrorMode postUartError:" + getMyStepString());
//        }
//        m_majorUartError = uartErr;
//        postUartError(uartErr, errDesc);
//    }
//
//    @Override
//    public void setBuzzer() {
//        if (!deviceSettingViewModel.beep.getValue()) return;
//        getDeviceSpiritC().setBuzzer(DeviceSpiritC.BEEP.SHORT, 1);
//    }
//
//    private void postUartError(@GENERAL.UartError int uartError, String errDesc) {
//
//        if (mIgnoreUartError) {
//            return;
//        }
//
//        int titleId = R.string.empty_str;
//
//        switch (uartError) {
//            case UE_MACHINE_TYPE:
//                titleId = R.string.machine_type_mismatch;
//                break;
//            case UE_INVERTER:
//                titleId = R.string.error_e1;
//                break;
//            case UE_RPM:
//                titleId = R.string.rpm_error;
//                break;
//            case UE_RS485:
//                titleId = R.string.rs485_error;
//                break;
//            case UE_SAFETY_KEY:
//                titleId = R.string.replace_safety_key;
//                break;
//            case UE_MCU:
//                titleId = R.string.mcu_error;
//                break;
//            case UE_INIT:
//                titleId = R.string.failed_to_init;
//                break;
//            case UE_UART_LWR: //5秒沒回應
//                titleId = R.string.uart_error_lwr;
//                break;
//            case UE_UART:
//                titleId = R.string.uart_error;
//                break;
//            case UE_NONE:
//                break;
//        }
//
//        String errorText;
//        try {
//            errorText = getApp().getString(titleId) + " " + errDesc;
//        } catch (Resources.NotFoundException e) {
//            errorText = "Unknown error " + errDesc; // ✅ 預設錯誤訊息，避免崩潰
//            LogUtil.e(TAG, "❌ Resource not found: " + titleId);
//        }
//
//        ErrorInfo errorInfo = new ErrorInfo(uartError, errorText, errDesc);
//
//        if (uartError == UE_SAFETY_KEY) {//#SafeKey3 顯示視窗
//            m.showSafeKeyWarring(true);
//        } else {
//            if (uartError != UE_NONE) {
//                m.showErrorMsg(errorInfo, true);
//                LogUtil.d(TAG, "[postUartError] " + errorText + " " + errDesc);
//            }
//        }
//
//        //傳給workout
//        LiveEventBus.get(EVT_ERR).post(errorInfo);
//    }
//
//    public void clearUartError1() {
//        LogUtil.d(TAG, "clearUartError1: INVERTER");
//        getDeviceSpiritC().setErrorClearTreadmill(CLEAR, NONE);
//    }
//
//    public void clearUartError2() {
//        LogUtil.d(TAG, "clearUartError2: BRIDGE");
//        getDeviceSpiritC().setErrorClearTreadmill(NONE, CLEAR); // converter, bridge
//    }
//
//    @Override
//    public void clearUartErrorAll() {
//        LogUtil.d(TAG, "clearUartErrorAll: ");
//        getDeviceSpiritC().setErrorClearTreadmill(CLEAR, CLEAR); // converter, bridge
//    }
//
//    //onConnected >
//    @Override
//    public void startUartFlowCommand() {
//        mConsoleUartTimeoutCnt = 0;
//        mConsoleRS485ErrorCnt = 0;
//        if (!mUartConnected || m_uartInitialized) return;
//
//        if (isTreadmill) setHertzConvert();
//
//        if (devStep == DS_CONNECT_RSP) {
//            // 1.取得裝置資訊 0x99 (ems step check)
//            //  LogUtil.d(TAG, "getDeviceInfo: 取得裝置資訊 (DS_99_DEV_INFO_RSP)");
//            getDeviceInfo(); // -> onDeviceInfo
//        }
//
//        startEcho();
//    }
//
//    private void startEcho() {
//        //持續傳送  >> 回傳  //onEchoTreadmill
//        if (isTreadmill) {
//            LogUtil.d(TAG, "startUartFlowCommand:doEchoTask啟動 每秒發送 setEchoTreadmill");
//
//            //   taskTimer.interval(1000, n -> doEchoTask());
//            new RxTimer().timer(1000, number ->
//                    taskTimer.interval(1000, n -> doEchoTask()));
//        } else {
//         //   LogUtil.d("ECHOOOOO", "EMS 開啟ECHO: ");
////            getDeviceSpiritC().setEchoMode(DeviceSpiritC.ECHO_MODE.SECOND);
//
//            new RxTimer().timer(2000, number -> getDeviceSpiritC().setEchoMode(DeviceSpiritC.ECHO_MODE.SECOND));
//        }
//
//        // 開始接收UART發送錯誤
//        mIgnoreUartError = false;
//        m_uartInitialized = true;
//    }
//
//    @Override
//    public int getDevStep() {
//        return devStep;
//    }
//
//    RxTimer taskTimer = new RxTimer();
//    public static boolean isFirmwareUpdating = false;
//
//    //持續傳送  >> 回傳  //onEchoTreadmill
//    public void doEchoTask() {
//
//        //   LogUtil.d(TAG, "111111doEchoTask: " + mUartConnected + ","+mLwrTimeoutCnt);
//        if (!mUartConnected) return;   // 在此先判斷uart如果未連接, 就不發錯誤(要做mcu update)
//
//        // 判斷是否已回應
//        if (getDevStep() == DS_FA_UPDATE_RESET_RSP) {
//            // 指令未回應, 重新發送指令
//            LogUtil.d(TAG, "doEchoTask: DS_FA_UPDATE_RESET_RSP  指令未回應, 重新發送指令");
//            getDeviceSpiritC().setResetTreadmill();
//        }
//
//        if (isFirmwareUpdating) return;
//
//        //    LogUtil.d(TAG, ">>>>>>>>>>>>>>>>>>>>>發送EchoTreadmill: ");
//
//        getDeviceSpiritC().setEchoTreadmill(); //> onEchoTreadmill
//
//        if (mIgnoreUartError) mLwrTimeoutCnt = 0;
//
//        mLwrTimeoutCnt++;
//        if (mLwrTimeoutCnt > 7) {
//            //下控未回應, 有可能是APP端已經CRASH, 直接設定為緊急錯誤
//            postUartError(UE_UART_LWR, "No response");
//            mCriticalError = true;
//
//            //
//            //   isEmulator = true;
//
//            //立即通知下控停機 （需確認是否需要多等待1秒)
//            setDevMainMode(CONSOLE_ERROR);
//        }
//
//        if (mCriticalError) {
//            LogUtil.d(TAG, "doEchoTask:mCriticalError 下控未回應, 有可能是APP端已經CRASH, 直接設定為緊急錯誤:  mLwrTimeoutCnt:" + mLwrTimeoutCnt);
//            taskTimer.cancel();
//            //  clearUartErrorAll();
////            Toasty.error(getApp(), "[CRITICAL ERROR] - RESTART", Toasty.LENGTH_LONG).show();
////            new RxTimer().timer(2000, number -> restartApp(m));
//        }
//    }
//
//    @Override
//    public void getDeviceInfo() {
//        if (!mUartConnected) return;
//        setDevStep(DS_99_DEV_INFO_RSP);
//        LogUtil.d(TAG, "getDeviceInfo >>>>>>>>");
//        getDeviceSpiritC().getDeviceInfo();
//    }
//
//    private void setHertzConvert() {
//        getDeviceSpiritC().setSpeedHertzConverter(new HertzConverter() {
//            @Override
//            public String getTitle() {
//                return "";
//            }
//
//            @Override
//            public int hertzToSpeed(int hz) {
//
//                int[] table = UNIT_E == IMPERIAL ? T_spd16_mile : T_spd16_km;
////                if (hz == 522) {//因為前幾個ad都是522
////                    //  LogUtil.d(TAG, "@收到的hertzToSpeed: speed:" + w.currentSpeedLevel.get() + ",hz:" + hz);
////                    return w.currentSpeedLevel.get();
////                } else {
//                int speed = 0;
//                // LogUtil.d(TAG, "@XXXXXXX收到的hertzToSpeed: speed:" + speed + ",hz:" + hz);
//                for (int i = 0; i < table.length; i++) {
//                    if (table[i] == hz) {
//                        speed = i;
//                    }
//                }
//                //   LogUtil.d(TAG, "@收到的hertzToSpeed: speed:" + speed + ",hz:" + hz);
//                return speed;
//                //       }
//            }
//
//            @Override
//            public int speedToHertz(int speed) {
//                int[] table = UNIT_E == IMPERIAL ? T_spd16_mile : T_spd16_km;
//                //  LogUtil.d(TAG, "#發送的speedToHertz: speed:" + speed + ",hz:" + table[speed]);
//
//                // 防止索引越界
//                // TODO: 防止索引越界
//                if (speed < 0 || speed >= table.length) {
//                    LogUtil.e(TAG, "Invalid speed index: " + speed + ", max index: " + (table.length - 1));
//                    Toasty.error(getApp(),"Invalid speed index: " + speed + ", max index: " + (table.length - 1),Toasty.LENGTH_LONG).show();
//                    return table[0];
//                }
//            //    LogUtil.d("PPPPQQQQQ", "speedToHertz: "+ UNIT_E +" , " +speed +","+  Arrays.toString(table));
//                return table[speed];
//            }
//        });
//    }
//
//
//    /**
//     * 5B:0C:  [99]
//     */
//    @Override
//    public void onDeviceInfo(DeviceSpiritC.MODEL model, String subMcuFwVer, String
//            keyStatus, String lwrMcuFwVer, int hrmStatus, String subMcuHwVer, String lwrMcuHwVer) {
//
//        LogUtil.d(TAG, "onDeviceInfo: {裝置資訊}" +
//                "  ##model## (機型) = " + model +
//                ", subMcuFwVer = " + subMcuFwVer +
//                ", keyStatus = " + keyStatus +
//                ", lwrMcuFwVer = " + lwrMcuFwVer +
//                ", hrmStatus = " + hrmStatus +
//                ", subMcuHwVer = " + subMcuHwVer +
//                ", lwrMcuHwVer = " + lwrMcuHwVer);
//
//
//        if (devStep == DS_99_UPDATE_DEV_INFO_RSP) {
//
//            //  m.mRestartApp();
//
//            if (isEmulator || !isTreadmill) {
//                // restart APP在EMS, ECB(Commercial UBE)下, 不需通知
//                restartApp(m);
//            } else {
//                LogUtil.d(TAG, "onDeviceInfo 恢復0xB0的傳送");
//                //     setDevMainMode(RESET);
//                isFirmwareUpdating = false;
//                //   deniedAllCmd(false); // 恢復0xB0的傳送 (若沒收到回應, 主要用以重傳指令)
//                setDevStep(DS_BE_UPDATE_TO_TC_RSP);   // 已回應device info, 設定timeout及control test mode
//                setUartTimeoutControl();
//            }
//
//            return;
//        }
//
//
//        if (devStep != DS_99_DEV_INFO_RSP) return;
//
//        // deviceSettingViewModel.csMcuVer.set(subMcuFwVer);
//        deviceSettingViewModel.lwrMcuFwVer.set(lwrMcuFwVer);
//        deviceSettingViewModel.subMcuFwVer.set(subMcuFwVer);
//        //   deviceSettingViewModel.lwrHwVer.set(lwrMcuHwVer);
//
//        //  LogUtil.d(TAG, "onDeviceInfo:  是否為Treadmill:" + isTreadmill + "," + model);
//        if (isTreadmill) { //目前儲存的機型
//            if (model != DeviceSpiritC.MODEL.TREADMILL_SC) { //下控取得的機型
//                LogUtil.d(TAG, "onDeviceInfo:LWR機型不符, 設定進入CONSOLE ERROR模式 (DS_B7_CONSOLE_ERR_RSP) 直接停機");
//                postUartError(UE_MACHINE_TYPE, "");
//            } else {
//                LogUtil.d(TAG, "onDeviceInfo:LWR機型符合, 設定逾時控制及測試控制 (DS_BE_TO_TC_RSP), m_timeoutControl = " + m_timeoutControl + ", m_testControl = " + m_testControl);
//                setDevStep(DS_BE_TO_TC_RSP);
//                setUartTimeoutControl();
//            }
//        } else {
//            if (model != DeviceSpiritC.MODEL.EMS) {
//                LogUtil.d(TAG, "onDeviceInfo LWR機型不符, 直接停機");
//                enterConsoleErrorMode(UE_MACHINE_TYPE, "");
//            } else {
//                // 2.回覆裝置資訊 0x99, 設定車種及模式0xA0 (ems step check)
//                LogUtil.d(TAG, "onDeviceInfo LWR機型符合, 設定LWR為一般模式 (DS_90_NORMAL_MODE_RSP), m_devMachineType = " + m_devMachineType);
//                setDevStep(DS_90_NORMAL_MODE_RSP);
//                getDeviceSpiritC().setLwrMode(DeviceSpiritC.LWR_MODE.NORMAL); // > onLwrMode
//            }
//        }
//
//        //機型相符，若無下控時，可關閉逾時及開啟測試模式
//
//        //  startEcho();
//    }
//
//    @Override
//    public void onMcuSetting(DeviceSpiritC.MODEL model, DeviceSpiritC.MCU_SET mcu_set) {
//
//    }
//
//    @Override
//    public void onTargetRpm(int targetRpm) {
//        LogUtil.d(TAG, "[0xA5] targetRpm = " + targetRpm);
//        if (targetRpm == m_targetRpm) {
//            LogUtil.d(TAG, "target RPM符合, targetRpm = " + targetRpm);
//            setDevStep(DS_A0_RUNNING_STANDBY);
//        } else {
//            LogUtil.d(TAG, "target RPM不符, targetRpm = " + targetRpm + ", 重送指令");
//            //  getDeviceSpiritC().setTargetRpm(m_targetRpm);  // rpm的值從25~100(待確認)
//            setDevTargetRpm(m_targetRpm);
//        }
//    }
//
//    public void saveError(DeviceSpiritC.INVERTER_ERROR
//                                  inverter_error, List<DeviceSpiritC.BRIDGE_ERROR> bridgeErrorList) {
//
//        List<ErrorMsgEntity> errorMsgEntityList = new ArrayList<>();
//        Date date = Calendar.getInstance().getTime();
//        if (inverter_error != null) {
//            //變頻器
//            if (inverter_error != DeviceSpiritC.INVERTER_ERROR.NONE &&
//                    inverter_error != DeviceSpiritC.INVERTER_ERROR.UNKNOWN) {
//                ErrorMsgEntity errorMsgEntity = new ErrorMsgEntity();
//
//                String el = Integer.toString(inverter_error.getCode(),16);
//                if (el.length() == 1) {
//                    el = "0" + el;
//                }
//                errorMsgEntity.setErrorCode("0xE1" + el);
//                errorMsgEntity.setErrorMessage(inverter_error.name());
//                errorMsgEntity.setErrorDate(date);
//                errorMsgEntityList.add(errorMsgEntity);
//            }
//        }
//
//        if (bridgeErrorList != null) {
//            //橋接板
//            if (bridgeErrorList.size() > 0) {
//                for (DeviceSpiritC.BRIDGE_ERROR bridgeError : bridgeErrorList) {
//                    //AUTO_PAUSE, SAFE_KEY
//                    if (bridgeError == DeviceSpiritC.BRIDGE_ERROR.NONE){
//                        return;
//                    }else {
//                        if (bridgeError == AUTO_PAUSE || bridgeError == SAFETY_KEY) continue;
//                        ErrorMsgEntity errorMsgEntity = new ErrorMsgEntity();
//
//                        String el = Integer.toString(bridgeError.getCode(),16);
//                        if (el.length() == 1) {
//                            el = "0" + el;
//                        }
//                        errorMsgEntity.setErrorCode("0xE2" + el);
////                    errorMsgEntity.setErrorCode("0x" + bridgeError.getCode());
//                        errorMsgEntity.setErrorMessage(bridgeError.name());
//                        errorMsgEntity.setErrorDate(date);
//                        errorMsgEntityList.add(errorMsgEntity);
//                    }
//                }
//            }
//        }
//
//        if (errorMsgEntityList.size() > 0) {
//            isErrorSaved = true;
//            DeviceSettingCheck2.insertErrorMsg(errorMsgEntityList);
//            //  LogUtil.d(TAG, "@@@@@@@@@儲存錯誤: " + errorMsgEntityList);
//        }
//    }
//
//
//    private boolean isErrorSaved = false;
//
//    @Override
//    public void onEchoTreadmill(DeviceSpiritC.MAIN_MODE mainMode, DeviceSpiritC.INIT_STATUS
//            initStatus, DeviceSpiritC.INVERTER_ERROR frequencyConverterErr, List<DeviceSpiritC.BRIDGE_ERROR> bridgeErrs, int hpHr,
//                                int wpHr, DeviceSpiritC.SAFE_KEY safeKey, int rpm, DeviceSpiritC.
//                                        INCLINE_STATUS frontGradeStatus, DeviceSpiritC.INCLINE_STATUS rearGradeStatus,
//                                int amp, DeviceSpiritC.TREADMILL_CALI_STATUS caliStatus, int frontGradeAd, int rearGradeAd,
//                                int sensor1Ad, int sensor2Ad) {
//
//        //   Toasty.warning(getApp(), "incline:" + frontGradeStatus + ",rpm:" + rpm, Toasty.LENGTH_SHORT).show();
////        LogUtil.d(TAG, "[onEchoTreadmill] m_devStep = " + devStep +
////                "\n, mainMode = " + mainMode +
////                "\n, initStatus = " + initStatus +
////                "\n, converterErr = " + frequencyConverterErr +
////                "\n, bridgeErrs = " + Arrays.toString(bridgeErrs.toArray()) +
////                "\n, handheldHrm = " + handheldHrm +
////                "\n, wirelessHrm = " + wirelessHrm +
////                "\n, rpm = " + rpm +
////                "\n, frontGradeStatus = " + frontGradeStatus +
////                "\n, rearGradeStatus = " + rearGradeStatus +
////                "\n, amp = " + amp +
////                "\n, caliStatus = " + caliStatus +
////                "\n, frontGradeAd = " + frontGradeAd +
////                "\n, rearGradeAd = " + rearGradeAd +
////                "\n, sensor1Ad = " + sensor1Ad +
////                "\n, sensor2Ad = " + sensor2Ad +
////                "\n, safeKey = " + safeKey);
//
//        //    LogUtil.d(TAG, "<<<<<<<<<<<<<onEchoTreadmill: " + mLwrTimeoutCnt);
//
//        w.setHpHr(hpHr);
//        w.setWpHr(wpHr);
//        setFirstHeartRate();
//
//        w.currentRpm.set(rpm);
//
//        //工程模式 DriveMotorTest
//        if (devStep == DS_B5_MAINTENANCE_AD_CHANGE_RSP || devStep == DS_MAINTENANCE_STANDBY) {
//            deviceSettingViewModel.rotationalSpeedValue.setValue(rpm);
////            deviceSettingViewModel.electricCurrentValue.setValue(amp);
//            deviceSettingViewModel.electricCurrentValue.set(amp * 0.1f);
//
//            //  LogUtil.d(TAG, "rpm(rotationalSpeed):" + rpm + ",amp(electric)" + amp + "," + deviceSettingViewModel.rotationalSpeedValue.getValue() + "," + deviceSettingViewModel.electricCurrentValue.get());
//        }
//
//
////        if (isRestarting) {
////            //重啟中被拔下安全鎖
////            if ((safeKey == DeviceSpiritC.SAFE_KEY.OFF) || bridgeErrs.contains(SAFETY_KEY)) {
////                isRestarting = false;
////                if (safeKeyTimer != null) {
////                    safeKeyTimer.cancel();
////                    safeKeyTimer = null;
////                }
////                if (m.safetyKeyWindow != null) {
////                    m.safetyKeyWindow.setText(R.string.replace_safety_key);
////                }
////                LogUtil.d(TAG, "取消計數: ");
////            }
////        }
//
//        // LogUtil.d(TAG, "onEchoTreadmill: " + frequencyConverterErr);
//
//        //檢查下達的指令是否已回傳, 否則重送指令
//        checkTreadmillSteps(mainMode, frontGradeStatus, rearGradeStatus,
//                initStatus, frequencyConverterErr, bridgeErrs, caliStatus, frontGradeAd, rearGradeAd, rpm, safeKey);
//
//        //   if (devStep == DS_B7_RESET_RSP) return; //避免DeviceSpiritC.INVERTER_ERROR.SAFE todo xxxxxxxx
//
//        if (!w.isSafeKey.get()) return; //安全鎖沒插就不往下執行
//        //  if (isRestarting) return; //安全鎖已拔 就不往下執行
//
//        //剛插上安全鎖 bridgeErrs 會變 NULL，現在應該不會了
//        if (bridgeErrs == null) return;
//
//        //存第一次錯誤
//        if (m.errorMsgWindow == null) {
//            //錯誤訊息存到資料庫
//            if (frequencyConverterErr != DeviceSpiritC.INVERTER_ERROR.NONE || bridgeErrs.size() > 0) {
//                if (w.isSafeKey.get() && !isErrorSaved) { //安全鎖有插才存錯誤
//
//                    saveError(frequencyConverterErr, bridgeErrs);
//                }
//            }
//        }
//
//        //AUTO_PAUSE 取消
//        if (!bridgeErrs.contains(AUTO_PAUSE)) {
//            pauseTime = DEFAULT_PAUSE_AFTER_TIME;
//        }
//
//        //錯誤處理的優先順序: safety key -> inverter ->BRIDGE -> MCU-> uart -> rs485 -> rpm -> incline -> decline -> sensor
//        if ((safeKey == DeviceSpiritC.SAFE_KEY.OFF) || bridgeErrs.contains(SAFETY_KEY)) {
//            if (((devStep != DS_CONSOLE_ERR_STANDBY) &&
//                    (devStep != DS_B7_CLR_SAFETY_KEY_RSP)) || (m_majorUartError != UE_SAFETY_KEY)) {
//                //#SafeKey1 設定進入ConsoleErrorMode
//                w.isSafeKey.set(false);
//                LogUtil.d(TAG, "SAFETY KEY已拔除, 設定進入CONSOLE ERROR模式" + "," + devStep + "," + mainMode);
//                enterConsoleErrorMode(UE_SAFETY_KEY, "");
//            }
//            return;
//        }
//
//        //若同時發生多個/連續錯誤，安全鎖未解決或正在準備重置，不顯示其他錯誤
//        if ((devStep == DS_B7_RESET_RSP) || (devStep == DS_B7_CLR_SAFETY_KEY_RSP) || (devStep == DS_B7_ERR_SAFETY_KEY_RSP))
//            return;
//
//        if (frequencyConverterErr != DeviceSpiritC.INVERTER_ERROR.NONE) {
//            LogUtil.d(TAG, "變頻器錯誤 = " + frequencyConverterErr + "," + devStep + "," + mainMode);
//            enterConsoleErrorMode(UE_INVERTER, getApp().getString(R.string.inverter_error) + " " + frequencyConverterErr);
//        } else if (bridgeErrs.contains(DeviceSpiritC.BRIDGE_ERROR.MCU)) {
//            // 發生於晶片出了狀況
//            LogUtil.d(TAG, "MCU錯誤");
//            enterConsoleErrorMode(UE_MCU, "");
//        } else if (bridgeErrs.contains(DeviceSpiritC.BRIDGE_ERROR.UART)) {
//            LogUtil.d(TAG, "UART錯誤(time out), 設定進入CONSOLE ERROR模式 暫時隱藏錯誤");
//            if (mConsoleUartTimeoutCnt < 2){
//                mConsoleUartTimeoutCnt ++;
//                clearUartError();
//            }else {
//                enterConsoleErrorMode(UE_UART, "");
//            }
//        } else if (bridgeErrs.contains(DeviceSpiritC.BRIDGE_ERROR.RS485)) {
//            LogUtil.d(TAG, "RS485錯誤, 設定進入CONSOLE ERROR模式");
//            if (mConsoleRS485ErrorCnt < 2){
//                mConsoleRS485ErrorCnt ++;
//                clearUartError();
//            }else {
//                enterConsoleErrorMode(UE_RS485, "");
//            }
//        } else if (bridgeErrs.contains(DeviceSpiritC.BRIDGE_ERROR.RPM)) {
//            // LWR目前不會發送此錯誤
//            enterConsoleErrorMode(UE_RPM, "");
//        } else if (bridgeErrs.contains(DeviceSpiritC.BRIDGE_ERROR.INCLINE)) {
//            // 前揚升錯誤: 不需顯示訊息, UI揚升禁止操作 (揚升button上方顯示error訊息)
//            w.disabledInclineUpdate.set(true);
//            LogUtil.d(TAG, "前揚升錯誤");
//            m_B0_devFrontGradeErr = true;
////        } else if (bridgeErrs.contains(DeviceSpiritC.BRIDGE_ERROR.DECLINE)) {
////            // 後揚升錯誤: 不需顯示訊息, UI揚升禁止操作 (揚升button上方顯示error訊息)
//        } else if (bridgeErrs.contains(AUTO_PAUSE)) {
//
//            //  LogUtil.d(TAG, "AUTO_PAUSE 進入暫停: 開始計時：" + pauseTime + "," + deviceSettingViewModel.pauseAfter.get());
//
//            if (deviceSettingViewModel.autoPause.getValue() == OFF) {
//                pauseTime = DEFAULT_PAUSE_AFTER_TIME;
//                return;
//            }
//
//            if (appStatusViewModel.currentStatus.get() != AppStatusIntDef.STATUS_RUNNING) {
//                pauseTime = DEFAULT_PAUSE_AFTER_TIME;
//                // LogUtil.d(TAG, "AUTO_PAUSE 不在RUNNING:取消計時：" + pauseTime);
//                return;
//            }
//
//            pauseTime++;
//
//
//            if (pauseTime > deviceSettingViewModel.pauseAfter.get()) {
//                pauseTime = DEFAULT_PAUSE_AFTER_TIME;
//                LogUtil.d(TAG, "AUTO_PAUSE 超過時間 發送暫停:" + pauseTime);
//                LiveEventBus.get(AUTO_PAUSE_EVENT).post(true);
//            }
//
//        } else {
//            //沒錯誤時的處理
//            postUartError(UE_NONE, "");
//        }
//    }
//
//    private void clearUartError() {
//        clearUartError2();
//        new RxTimer().timer(500, number -> {
//
//            // (2) 過500ms, 設定timeout disable, test control disable
//            getDeviceSpiritC().setTimeoutControlTreadmill(
//                    DeviceSpiritC.TIMEOUT_CONTROL.DISABLE,
//                    DeviceSpiritC.TEST_CONTROL.DISABLE
//            );
//
//            new RxTimer().timer(500, number1-> {
//
//                // (3) 再過500ms, 設定timeout enable, test control disable
//                getDeviceSpiritC().setTimeoutControlTreadmill(
//                        DeviceSpiritC.TIMEOUT_CONTROL.ENABLE,
//                        DeviceSpiritC.TEST_CONTROL.DISABLE
//                );
//            });
//
//        });
//    }
//
//    @Override
//    public void onSettingSpeedAndInclineTreadmill(DeviceSpiritC.MOTOR_DIRECTION motor_direction, int i, int i1, int i2) {
//
//    }
//
//    @Override
//    public void onTimeoutControlTreadmill(DeviceSpiritC.TIMEOUT_CONTROL timeOutCtrl, DeviceSpiritC.TEST_CONTROL testCtrl) {
//
//        //  LogUtil.d(TAG, "onTimeoutControlTreadmill = " + timeOutCtrl + ", test_control = " + testCtrl + ", 設定完成");
//
//        if (devStep == DS_BE_TO_TC_RSP) {
//
//            if ((timeOutCtrl == m_timeoutControl && testCtrl == m_testControl) || m_ignoreUartResponse) {
//                LogUtil.d(TAG, "逾時控制及測試控制設定符合 (DS_LWR_INITIALIZING), timeOutCtrl = " + timeOutCtrl + ", test_control = " + testCtrl);
//                setDevStep(DS_LWR_INITIALIZING);
//
//                //  LogUtil.d(TAG, "提早發 setEchoTreadmill: ");
//                getDeviceSpiritC().setEchoTreadmill();
//            } else {
//                LogUtil.d(TAG, "逾時控制及測試控制設定不符, timeOutCtrl = " + timeOutCtrl + ", test_control = " + testCtrl + ", 重送指令");
//                setUartTimeoutControl();
//            }
//        } else if (devStep == DS_BE_UPDATE_TO_TC_RSP) {  // 判斷回應模式是否相符
//            if ((timeOutCtrl == m_timeoutControl && testCtrl == m_testControl) || m_ignoreUartResponse) {
//
//                LogUtil.d(TAG, "逾時控制及測試控制設定符合 (DS_BE_UPDATE_TO_TC_RSP), timeOutCtrl = " + timeOutCtrl + ", test_control = " + testCtrl);
//                //     setDevMainMode(RESET);  // 在這裡才設定main mode = RESET
//                LogUtil.d(TAG, "LWR更新完成，重新開機: ");
//                restartApp(m);
//
//            } else {
//                LogUtil.d(TAG, "逾時控制及測試控制設定不符, timeOutCtrl = " + timeOutCtrl + ", test_control = " + testCtrl + ", 重送指令");
//                setUartTimeoutControl();
//            }
//        }
//    }
//
//    @Override
//    public void onErrorClearTreadmill(DeviceSpiritC.ERROR_STATUS error_status, DeviceSpiritC.ERROR_STATUS error_status1) {
//
//    }
//
//
//    /**
//     * FOR 商用電跑 (台達電變頻器)
//     * 進 EUP
//     * 1．設定 MainMode = EUP mode ( 0xB7 = 0x52)
//     * 2．Console 等 lwr 回傳 MainMode = EUP mode ( 0xB7 = 0x52) 後，才通知 SubMCU切斷 lwr 的電源 (連接橋接板端子的 pin 5 設為 low)
//     * <p>
//     * Wake up
//     * 1．SubMCU 將連接橋接板端子的 pin 6 設為 low，維持 1.4秒使變頻器 wake up後，再設為 high。
//     * 2．步驟 1完成後，等3秒，SubMCU恢復 lwr 的電源 (連接橋接板端子的 pin 5 設為 high)
//     */
//
//    @Override
//    public void onMainModeTreadmill(DeviceSpiritC.MAIN_MODE mainMode) {
//
//        //   LogUtil.d(TAG, "onMainModeTreadmill: " + mainMode);
//
//        if (mainMode == EUP) {
//            LogUtil.d(TAG, "onMainModeTreadmill: 休眠2222" + mainMode);
//            App.getDeviceSpiritC().setEUP(4);
//            return;
//        }
//
//        if (mainMode == RESET) {
//            LogUtil.d(TAG, "onMainModeTreadmill:收到RESET: " + mainMode + "," + devStep);
//        }
//
//        switch (devStep) {
//            //等回覆
//            case DS_B7_RESET_RSP://#SafeKey7 下控已回覆 MainMode RESET >> 重啟
//                if (mainMode == RESET) {
//                    LogUtil.d(TAG, "已進入RESET模式, 待命中 (DS_RESET_STANDBY)");
//                    setDevStep(DS_RESET_STANDBY);
//                    restartApp(m);
//                } else {
//                    LogUtil.d(TAG, "尚未進入RESET模式, 重送指令");
//                    setMainModeTreadmill(RESET);
//                }
//                break;
//
//
////            case DS_B7_RESET_RSP:
////                //安全鎖
////                setDevStep(DS_RESET_STANDBY);
////                LogUtil.d(TAG, "onMainModeTreadmill : DS_RESET_STANDBY: 重啟APP");
////                // 在這裡做APP restart
////                restartApp(m);
//
//            //              break;
//            case DS_B7_CONSOLE_ERR_RSP:
//                if (mainMode == DeviceSpiritC.MAIN_MODE.CONSOLE_ERROR) {
//                    LogUtil.d(TAG, "已進入CONSOLE ERROR模式, 待命中 (DS_CONSOLE_ERR_STANDBY)");
//                    setDevStep(DS_CONSOLE_ERR_STANDBY);
//                    //   setDevStep(DS_B7_CLR_SAFETY_KEY_RSP);
//                } else {
//                    LogUtil.d(TAG, "尚未進入CONSOLE ERROR模式, 重送指令");
//                    setMainModeTreadmill(DeviceSpiritC.MAIN_MODE.CONSOLE_ERROR);
//                }
//                break;
//
//            case DS_B7_ERR_SAFETY_KEY_RSP:
//                //console error主要是告知下控, console出現重大error(安全鎖未歸位, 其他..., 下控收到後會停止馬達及揚升)
//                //#SafeKey4 等待安全鎖復位
//                if (mainMode == DeviceSpiritC.MAIN_MODE.CONSOLE_ERROR) {
//                    LogUtil.d(TAG, "已進入CONSOLE ERROR模式, 等待安全鎖復位 (DS_B7_CLR_SAFETY_KEY_RSP)");
//                    setDevStep(DS_B7_CLR_SAFETY_KEY_RSP);
//                } else {
//                    LogUtil.d(TAG, "尚未進入CONSOLE ERROR模式, 重送指令");
//                    setMainModeTreadmill(DeviceSpiritC.MAIN_MODE.CONSOLE_ERROR);
//                }
//                break;
//
//            case DS_B7_MAINTENANCE_RSP:
//                if (mainMode == DeviceSpiritC.MAIN_MODE.ENG) {
//                    LogUtil.d(TAG, "已進入工程模式, 待命中 (DS_MAINTENANCE_STANDBY)");
//                    setDevStep(DS_MAINTENANCE_STANDBY);
//                } else {
//                    LogUtil.d(TAG, "尚未進入工程模式, 重送指令");
//                    setMainModeTreadmill(DeviceSpiritC.MAIN_MODE.ENG);
//                }
//                break;
//
//            case DS_B7_FINISH_PAUSE_RSP:
//                if (mainMode == DeviceSpiritC.MAIN_MODE.PAUSE || mainMode == GS) {
//                    LogUtil.d(TAG, "結束訓練, 已進入PAUSE模式, 設定 進入IDLE模式 (DS_B7_FINISH_IDLE_RSP)");
//                    setDevStep(DS_B7_FINISH_IDLE_RSP);
//                    setMainModeTreadmill(IDLE);
//                } else {
//                    LogUtil.d(TAG, "結束訓練, 尚未進入PAUSE模式, 重送指令");
//                    //  setMainModeTreadmill(!deviceSettingViewModel.gsMode.get() ? GS : DeviceSpiritC.MAIN_MODE.PAUSE);
//                    setMainModeTreadmill(deviceSettingViewModel.gsMode.get() ? GS : DeviceSpiritC.MAIN_MODE.PAUSE);
//                }
//                break;
//            case DS_B7_APP_START_RSP:
//                if (mainMode == DeviceSpiritC.MAIN_MODE.APP_START) {
//                    LogUtil.d(TAG, "已進入APP_START, 設定進入IDLE模式 (DS_B7_IDLE_RSP)");
//                    setDevMainMode(IDLE);
//                    setDevStep(DS_B7_IDLE_RSP);
//                    setMainModeTreadmill(IDLE);
//                }
//                break;
//
//            case DS_B7_IDLE_RSP:
//                if (mainMode == IDLE) {
//                    LogUtil.d(TAG, "已進入IDLE模式, 執行 單位設定 (DS_B3_IDLE_UNIT_RSP)");
//                    setDevUnit();
//                }
//                break;
//
//            case DS_B7_FINISH_IDLE_RSP:
//                if (mainMode == IDLE) {
//                    LogUtil.d(TAG, "結束訓練, 已回到IDLE模式, 待命中 (DS_IDLE_STANDBY) 允許user按START");
//                    setDevStep(DS_IDLE_STANDBY);
//                } else {
//                    LogUtil.d(TAG, "結束訓練, 未回到IDLE模式, 重送指令");
//                    setMainModeTreadmill(IDLE);
//                }
//                break;
//
//            case DS_B7_CALIBRATION_RSP:
//                if (mainMode == DeviceSpiritC.MAIN_MODE.CALIBRATE) {
//                    LogUtil.d(TAG, "已進入校正模式, 待命中 (DS_CALIBRATION_STANDBY) 允許user按[Start Calibration]");
//                    setDevStep(DS_CALIBRATION_STANDBY);
//                } else {
//                    LogUtil.d(TAG, "未進入校正模式, 重送指令");
//                    setMainModeTreadmill(DeviceSpiritC.MAIN_MODE.CALIBRATE);
//                }
//                break;
//
//            case DS_B7_RUNNING_RSP:
//                if (mainMode == DeviceSpiritC.MAIN_MODE.RUNNING) {
//
//                    LogUtil.d(TAG, "onMainModeTreadmill 已進入RUNNING模式, 待命中 (DS_RUNNING_STANDBY)");
//                    setDevStep(DS_RUNNING_STANDBY);
//
//                } else {
//                    LogUtil.d(TAG, "未進入RUNNING模式, 重送指令");
//                    setMainModeTreadmill(DeviceSpiritC.MAIN_MODE.RUNNING);
//                }
//                break;
//
//            case DS_B7_RESUME_RUNNING_RSP:
//                if (mainMode == DeviceSpiritC.MAIN_MODE.RUNNING) {
//
////                    w.currentFrontInclineAd.set(getInclineAd(w));
//
//
//                    LogUtil.d(TAG, "onMainModeTreadmill RESUME 已進入RUNNING模式, 恢復速度及揚升 (DS_B5_RESUME_AD_CHANGE_RSP), speed = " + w.currentSpeedLevel.get() +
//                            ", 前揚升 = " + w.currentFrontInclineAd.get());
//
//                    setDevStep(DS_B5_RESUME_AD_CHANGE_RSP);
//
//                    setTargetSpeedAndInclineTreadmill(w.currentSpeedLevel.get(), w.currentFrontInclineAd.get());
//
//                } else {
//                    LogUtil.d(TAG, "onMainModeTreadmill DS_B7_RESUME_RUNNING_RSP: 還未進入MAIN_MODE.RUNNING 無法恢復速度");
//                }
//                break;
//
//            case DS_B7_PAUSE_RSP:
//                if (mainMode == DeviceSpiritC.MAIN_MODE.PAUSE || mainMode == GS) {
//                    LogUtil.d(TAG, "1已進入PAUSE模式, 設定速度及前後揚升歸零 (DS_B5_PAUSE_AD_RESET_RSP)");
//                    setDevStep(DS_B5_PAUSE_AD_RESET_RSP);
//                    resetSpeedAndIncline(false);
//                }
//                break;
//
//            case DS_B7_PAUSE_GS_RSP:
//                if (mainMode == GS) {
//                    LogUtil.d(TAG, "已進入GS模式,PAUSE階段揚升不用做歸零動作 (DS_B5_PAUSE_AD_RESET_RSP)");
//                    setDevStep(DS_B5_PAUSE_AD_RESET_RSP);
//                    gsConstraintSpeedAndGrade();
//                }
//                break;
//        }
//    }
//
//    private void gsConstraintSpeedAndGrade() {
//        if (!mUartConnected) return;
//
//        // GS mode, PAUSE階段揚升不用做歸零動作 (非GS mode, PAUSE階段揚升要做歸零動作)
//        //   boolean gradeZero = deviceSettingViewModel.gsMode.get();
//        boolean gradeZero = !deviceSettingViewModel.gsMode.get();
//        //  LogUtil.d(TAG, " GS mode, PAUSE階段揚升不用做歸零動作 STEP: gsConstraintSpeedAndGrade");
//        setTargetSpeedAndInclineTreadmill(0, gradeZero ? w.getFrontInclineAdList().get(0) : 0);
//    }
//
//    @Override
//    public void onSpeedAndInclineStopTreadmill(DeviceSpiritC.STOP_STATUS speedStatus, DeviceSpiritC.STOP_STATUS inclineStatus, DeviceSpiritC.STOP_STATUS declineStatus) {
//
//        if (devStep == DS_B8_STOP_SC_RSP) {
//            if ((speedStatus == DeviceSpiritC.STOP_STATUS.STOP) && (inclineStatus == DeviceSpiritC.STOP_STATUS.STOP)) {
//                LogUtil.d(TAG, "停止速度及揚升設定符合 (DS_B8_STOP_SC_STANDBY)");
//                setDevStep(DS_B8_STOP_SC_STANDBY);
//            } else {
//                LogUtil.d(TAG, "停止速度及揚升設定不符, 重送指令 (DS_B8_STOP_SC_RSP)");
//                getDeviceSpiritC().setSpeedAndInclineStopTreadmill();
//            }
//        }
//
//        LogUtil.d(TAG, "stopStatus = " + speedStatus + ", stopStatus1 = " + inclineStatus + ", stopStatus2 = " + declineStatus);
//    }
//
//    public void setDevUnit() {
//        if (!isTreadmill) return;
//        setDevStep(DS_B3_IDLE_UNIT_RSP);
//        m_devUnit = (UNIT_E == METRIC) ? DeviceSpiritC.UNIT.METRIC : DeviceSpiritC.UNIT.IMPERIAL;
//        getDeviceSpiritC().setUnitTreadmill(m_devUnit); // > onUnitTreadmill
//        //   LogUtil.d(TAG, "進行單位設定 等待 onUnitTreadmill: ");
//    }
//
//    /**
//     * @param forceGradeReturn true : 不歸0，false : 歸0
//     */
//    @Override
//    public void resetSpeedAndIncline(boolean forceGradeReturn) {
//
//        if (!mUartConnected) return;
//
//        //   boolean isGsMode = !deviceSettingViewModel.gsMode.get();
//        boolean isGsMode = deviceSettingViewModel.gsMode.get();
//
//        boolean gradeDontCare = !forceGradeReturn && isGsMode;
//
//        LogUtil.d(TAG, gradeDontCare ? "GS MODE:設定速度歸零, 前後揚升不歸零" : "非GS MODE:設定速度及前後揚升歸零");
//
////        int inc;
////        if (gradeDontCare) {
////            inc = 0; //incline AD 給 0 > 不改變
////        } else {
////            inc = w.getFrontInclineAdList().get(0);
////            w.currentInclineValue.set(0);
////        }
////        setTargetSpeedAndInclineTreadmill(0, inc);
//
//        //incline AD 給 0 > 不改變
//
////        new RxTimer().timer(5000,number -> {
////            setTargetSpeedAndInclineTreadmill(0, gradeDontCare ? 0 : w.getFrontInclineAdList().get(0));
////        });
//
//        setTargetSpeedAndInclineTreadmill(0, gradeDontCare ? 0 : w.getFrontInclineAdList().get(0));
//        //  setTargetSpeedAndInclineTreadmill(0, 2460);
//    }
//
//    /**
//     * 暫停
//     */
//    private void pauseSpeedAndIncline() {
//        if (!mUartConnected) return;
//        LogUtil.d(TAG, "resetSpeedAndIncline: " + w.getFrontInclineAdList().get(0));
//        setTargetSpeedAndInclineTreadmill(0, 0);
//        //   getDeviceSpiritC().setTargetSpeedAndInclineTreadmill(DeviceSpiritC.MOTOR_DIRECTION.FORWARD, 0, 0, 0);
//    }
//
//    @Override
//    public void onBrakeModeTreadmill(DeviceSpiritC.BRAKE_MODE brakeMode) {
//        if (devStep == DS_BA_BREAK_MODE_RSP) {
//            if (brakeMode == m_BA_brakeMode) {
//                LogUtil.d(TAG, "剎車模式設定符合, 進入工程模式 待命中 (DS_MAINTENANCE_STANDBY)");
//                setDevStep(DS_MAINTENANCE_STANDBY);
//            } else {
//                LogUtil.d(TAG, "剎車模式不符, brakeMode = " + brakeMode + ", 重送指令");
//                getDeviceSpiritC().setBrakeModeTreadmill(m_BA_brakeMode);
//            }
//        }
//    }
//
//    @Override
//    public void onAngleSetting(int positive, int negative) {
//        LogUtil.d(TAG, "onAngleSetting positive = " + positive + ", negative = " + negative);
//
////        if (devStep == DS_A9_SYMM_ANGLE_RSP) {
////            if ((m_A9_anglePositive == positive) && (m_A9_angleNegative == negative)) {
////                LogUtil.d(TAG, "symmetry angle數值符合, 進入DS_EMS_IDLE_STANDBY");
////                setDevStep(DS_EMS_IDLE_STANDBY);
////            } else {
////                LogUtil.d(TAG, "symmetry angle數值不符, positive = " + positive + ", negative = " + negative + " 重送指令");
////                getDeviceSpiritC().setAngleSetting(m_A9_anglePositive, m_A9_angleNegative);
////            }
////        }
//    }
//
//    @Override
//    public void onBrakeStatus(DeviceSpiritC.BRAKE_STATUS brake_status) {
//
//    }
//
//    private void setSymmetryAngle(int positive, int negative) {
//        if (!mUartConnected) return;
//
//        m_A9_anglePositive = positive;
//        m_A9_angleNegative = negative;
//        getDeviceSpiritC().setAngleSetting(m_A9_anglePositive, m_A9_angleNegative);
//    }
//
//    private boolean acceptAllTargetValues(int speed, int frontGradeAd) {
//        return (speed == w.currentSpeedLevel.get() && frontGradeAd == w.currentFrontInclineAd.get());
//    }
//
//    private boolean acceptTargetSpeedButGradeError(int speed) {
//        return m_B0_devFrontGradeErr && (speed == w.currentSpeedLevel.get());
//    }
//
//
//    private boolean acceptResetTargetValues(int speed, int frontGradeAd, int rearGradeAd, boolean ignoreGrade) {
//        LogUtil.d(TAG, "acceptResetTargetValues speed = " + speed + ", frontGradeAd = " + frontGradeAd + ", rearGradeAd = " + rearGradeAd + ", ignoreGrade = " + ignoreGrade);
//
//        return (speed == 0) && (frontGradeAd == (ignoreGrade ? 0 : w.getFrontInclineAdList().get(0)));
//    }
//
//    @Override
//    public void onTargetSpeedAndInclineTreadmill(DeviceSpiritC.MOTOR_DIRECTION motorDir, int speed, int incline, int rearGrade) {
//
//        //設定跑步機的速度及揚升
//        LogUtil.d(TAG, "<<<<<<<<<<回應,<SPEED>level=" + speed + ",value=" + getSpeedValue(speed) + ", <INCLINE>ad=" + incline + ",value=" + w.currentInclineValue.get() + ",level=" + getInclineLevel(w.currentInclineValue.get()));
//        switch (devStep) {
////            case DS_B5_RUNNING_AD_RESET_RSP:
////                if (acceptResetTargetValues(speed, frontGrade, rearGrade, false)) {
////                    LogUtil.d(TAG, "(GS mode) 速度及前後揚升歸零設定符合, 等待RPM為0及揚升無動作 (DS_RUNNING_SILENCE_RSP)");
////                    setDevStep(DS_RUNNING_SILENCE_RSP);
////                } else {
////                    LogUtil.d(TAG, "(GS mode) 速度或前後揚升歸零設定不符, 重新傳送 (DS_B5_RUNNING_AD_RESET_RSP)");
////                    resetSpeedAndIncline(true);
////                }
////                break;
//
//
//            case DS_B5_MAINTENANCE_AD_CHANGE_RSP:
//                if (acceptAllTargetValues(speed, incline)) {
//                    LogUtil.d(TAG, "速度及前後揚升設定符合, MAINTENANCE模式 待命中(DS_MAINTENANCE_STANDBY)");
//                    setDevStep(DS_MAINTENANCE_STANDBY);
//                } else {
//                    if (acceptTargetSpeedButGradeError(speed)) {
//                        LogUtil.d(TAG, "速度設定符合(忽略前後揚升錯誤), MAINTENANCE模式 待命中(DS_MAINTENANCE_STANDBY)");
//                        setDevStep(DS_MAINTENANCE_STANDBY);
//                    } else {
//                        LogUtil.d(TAG, "速度 or 前/後揚升數值不符, speed = " + speed + ", frontGrade = " + incline + " , 重送指令");
//
//                        setTargetSpeedAndInclineTreadmill(w.currentSpeedLevel.get(), w.currentFrontInclineAd.get());
//                    }
//                }
//                break;
//
//            case DS_B5_RESUME_AD_CHANGE_RSP:
//            case DS_B5_RUNNING_AD_CHANGE_RSP:
//                if (acceptAllTargetValues(speed, incline)) {
//                    LogUtil.d(TAG, "速度及前揚升設定符合, RUNNING模式 待命中(DS_RUNNING_STANDBY)");
//                    setDevStep(DS_RUNNING_STANDBY);
//                } else {
//                    //  LogUtil.d(TAG, "onTargetSpeedAndInclineTreadmill: " + m_B0_devFrontGradeErr +","+ speed +","+w.currentSpeedLevel.get());
//                    if ((m_B0_devFrontGradeErr) && (speed == w.currentSpeedLevel.get())) {
//                        LogUtil.d(TAG, "速度設定符合(忽略前後揚升錯誤), RUNNING模式 待命中(DS_RUNNING_STANDBY)");
//                        setDevStep(DS_RUNNING_STANDBY);
//                    } else {
//                        LogUtil.d(TAG, "#速度及前揚升數值不符, 收到的speed = " + speed + ",重送的 speed:" + w.currentSpeedLevel.get() + ",收到的incline = " + incline + ",重送的 incline" + w.currentFrontInclineAd.get() + ",m_B0_devFrontGradeErr:" + m_B0_devFrontGradeErr + ", 重送指令");
//                        setTargetSpeedAndInclineTreadmill(w.currentSpeedLevel.get(), w.currentFrontInclineAd.get());
//                    }
//                }
//                break;
//            case DS_B5_BREAK_SPD_RESET_RSP:
//                LogUtil.d(TAG, "GS mode break, 前後揚升不歸零");
//            case DS_B5_FINISH_AD_RESET_RSP:
//                if (acceptResetTargetValues(speed, incline, rearGrade, devStep == DS_B5_BREAK_SPD_RESET_RSP)) {
//                    LogUtil.d(TAG, "速度及前後揚升歸零設定符合, 等待RPM為0及揚升無動作 (DS_FINISH_SILENCE_RSP)");
//                    setDevStep(DS_FINISH_SILENCE_RSP);
//                } else {
//                    if ((m_B0_devFrontGradeErr) && (speed == 0)) {
//                        LogUtil.d(TAG, "速度歸零設定符合(忽略前後揚升錯誤), 等待RPM為0 (DS_FINISH_SILENCE_RSP)");
//                        setDevStep(DS_FINISH_SILENCE_RSP);
//                    } else {
//                        LogUtil.d(TAG, "2速度及前後揚升數值不符, frontGrade = " + incline + "rearGrade = " + rearGrade + ", 重送指令");
//                        resetSpeedAndIncline(false);
//                    }
//
//                }
//                break;
//
//            case DS_B5_PAUSE_AD_RESET_RSP:
//                //   if (acceptResetTargetValues(speed, incline, rearGrade, !deviceSettingViewModel.gsMode.get())) {
//                if (acceptResetTargetValues(speed, incline, rearGrade, deviceSettingViewModel.gsMode.get())) {
//                    LogUtil.d(TAG, "已接收 速度及前後揚升歸零 設定, 等待RPM為0及揚升無動作 (DS_PAUSE_SILENCE_RSP)");
//                    setDevStep(DS_PAUSE_SILENCE_RSP);
//                } else {
//                    if ((m_B0_devFrontGradeErr) && (speed == 0)) {
//                        LogUtil.d(TAG, "已接收 速度歸零設定(忽略前後揚升錯誤), 等待RPM為0 (DS_PAUSE_SILENCE_RSP)");
//                        setDevStep(DS_PAUSE_SILENCE_RSP);
//                    } else {
//                        LogUtil.d(TAG, "3速度及前後揚升設定不符, frontGrade = " + incline + ",rearGrade = " + rearGrade + ", 重送指令");
//                        resetSpeedAndIncline(false);
//                    }
//                }
//                break;
//
//            case DS_B5_IDLE_AD_RESET_RSP:
//
//                //  if (acceptResetTargetValues(speed, incline, rearGrade, !deviceSettingViewModel.gsMode.get())) {
//                if (acceptResetTargetValues(speed, incline, rearGrade, deviceSettingViewModel.gsMode.get())) {
//                    setDevStep(DS_IDLE_SILENCE_RSP);
//                    //   LogUtil.d(TAG, "提早發 setEchoTreadmill: ");
//                    getDeviceSpiritC().setEchoTreadmill();
//                } else {
//                    boolean disableGradePicker = ((incline == 0xFFF5));
//
//                    if (disableGradePicker) m_B0_devFrontGradeErr = true;
//
//                    LogUtil.d(TAG, "onTargetSpeedAndInclineTreadmill: " + m_B0_devFrontGradeErr + "," + speed);
//                    if ((m_B0_devFrontGradeErr) && (speed == 0)) {
//                        w.disabledInclineUpdate.set(true);
//                        setDevStep(DS_IDLE_SILENCE_RSP);
//                    } else {
//                        if (disableGradePicker) {
//                            LogUtil.d(TAG, "前後揚升錯誤, 禁能GRADE PICKER");
//                            w.disabledInclineUpdate.set(true);
//                        } else {
//                            LogUtil.d(TAG, "速度及前後揚升 數值不符, frontGrade = " + incline + ", 重送指令");
//                            resetSpeedAndIncline(false);
//                        }
//                    }
////                    LiveEventBus
////                            .get(EVT_GRADE_PICKER)
////                            .post(false);
//                }
//                break;
//            case DS_CALIBRATION_STANDBY:
//                LogUtil.d(TAG, "DS_CALIBRATION_STANDBY frontGrade:" + incline);
//                break;
//        }
//    }
//
//
//    /**
//     * 心跳接收順序：
//     *
//     * Apple watch/Samsung watch > Garmin > BLE 心跳帶 > 5K 心跳 > (HP)手握心跳。
//     */
//    private void setFirstHeartRate() {
//        //Garmin-->BLE-->ANT+-->無線心跳-->手握心跳；
//        //HR 優先權  : HRS --> WP(無線心跳) --> HP(手握)
//        int currentHr;
//        if (w.getGarminHr() > 0) { //garmin
//            currentHr = w.getGarminHr();
//            w.setBleHr(0);
//            w.setWpHr(0);
//            w.setHpHr(0);
//        } else {
//            if (w.getBleHr() > 0) { //ble
//                currentHr = w.getBleHr();
//                w.setWpHr(0);
//                w.setHpHr(0);
//            } else {
//                if (w.getWpHr() > 0) {
//                    currentHr = w.getWpHr();
//                    w.setHpHr(0);
//                } else {
//                    currentHr = Math.max(w.getHpHr(), 0);
//                }
//            }
//        }
//
//
//        if (w.getSamsungWatchHr() > 0 && w.isSamsungWatchEnabled.get()) {
//            currentHr = w.getSamsungWatchHr();
//        }
//
//        //todo APPLE WATCH
//        if (w.getAppleWatchHr() > 0 && w.isAppleWatchEnabled.get()) {
//            currentHr = w.getAppleWatchHr();
//            //w.setBleHr(0);
////            w.setWpHr(0);
////            w.setHpHr(0);
////            w.setGarminHr(0);
//        }
//
//        //   LogUtil.d("GEM3", "setFirstHeartRate: " + w.getAppleWatchHr());
//
//        //   LogUtil.d(TAG, "setFirstHeartRate: " + w.getGarminHr() + "," + w.getBleHr() + "," + w.getWpHr() + "," + w.getHpHr());
//
//        w.currentHeartRate.set(currentHr);
//
//        // TODO:USSSSSS
//        //   w.currentHeartRate.set(80);
//
//        w.setIsHrConnected(w.currentHeartRate.get() > 0);
//    }
//
//    private void processEmsErrors(List<DeviceSpiritC.MCU_ERROR> mcuErrors) {
//
//// 模擬產生ems error
////        int randomValue = getRandomValue(1, 100);
////
////        LogUtil.d("EMS_MCU_ERROR", "randomValue = " + randomValue);
////
////        switch (randomValue / 10) {
////            case 3:
////                if (getRandomValue(0, 1) == 1) {
////                    mcuErrors.removeIf(Objects::nonNull);
////                    mcuErrors.add(DeviceSpiritC.MCU_ERROR.IDLE_OC);
////                }
////                else {
////                    mcuErrors.removeIf(Objects::nonNull);
////                    mcuErrors.add(DeviceSpiritC.MCU_ERROR.OC);
////                }
////                break;
////
////            case 8:
////                if (getRandomValue(0, 1) == 1) {
////                    mcuErrors.removeIf(Objects::nonNull);
////                    mcuErrors.add(DeviceSpiritC.MCU_ERROR.CH);
////                }
////                else {
////                    mcuErrors.removeIf(Objects::nonNull);
////                    mcuErrors.add(DeviceSpiritC.MCU_ERROR.CL);
////
////                }
////                break;
////            default:
////                mcuErrors.removeIf(Objects::nonNull);
////                break;
////        }
//
//        if (mcuErrors.isEmpty()) {
//            postUartError(UE_NONE, "");
//            return;
//        }
//
//        if (mIgnoreUartError) {
//            return;
//        }
//
//        String errorText = "";
//        for (DeviceSpiritC.MCU_ERROR mcuError : mcuErrors) {
//
//            LogUtil.d("EMS_MCU_ERROR", "error: " + mcuError.toString());
//            // 在ems橋接板未接電吸鐵及外部電源時, 會一直持續發 CH 的error, 在測試版忽略此錯誤
//            if (mcuError == DeviceSpiritC.MCU_ERROR.CH || mcuError == DeviceSpiritC.MCU_ERROR.CL) {
//                if (BuildConfig.TEST_MODE)
//                    continue;
//            }
//
//            errorText = getApp().getString(R.string.error_code) + " " + mcuError;
//            LogUtil.d("EMS_MCU_ERROR", "errorText = " + errorText);
//            break;
//        }
//
//        if (errorText.isEmpty())
//            return;
//
//        ErrorInfo errorInfo = new ErrorInfo(GENERAL.UE_MCU_EMS, errorText, "");
//        m.showErrorMsg(errorInfo, true);
//
//        //傳給workout
//        LiveEventBus.get(EVT_ERR).post(errorInfo);
//    }
//
//    @Override
//    public void onMcuControl(DeviceSpiritC.MODEL model,
//                             List<DeviceSpiritC.MCU_ERROR> mcuErrors,
//                             int hpHr,
//                             int wpHr,
//                             DeviceSpiritC.ACTION_STATUS inclineStatus,
//                             int inclineAd,
//                             DeviceSpiritC.SAFE_KEY safeKey,
//                             int speed,
//                             int stepCount,
//                             DeviceSpiritC.DIRECTION direction,
//                             int rpm,
//                             DeviceSpiritC.ACTION_STATUS res,
//                             int resValue,
//                             int reedSwitchRpm1,
//                             int angleSensorRpm2,
//                             int rpmCounter) {
//
//        w.currentRpm.set(rpm);
//      //  LogUtil.d("AAAAAAAAA", "onMcuControl: " + w.currentRpm.get());
//        if (w.selProgram == ProgramsEnum.EGYM && MODE != CT1000ENT) {
//            w.currentRpm.notifyChange();
//        }
//
//
//        //   w.currentRpm.set(120);
//        //  w.currentSpeed.set(pwmLevel);
//        w.setHpHr(hpHr);
//        w.setWpHr(wpHr);
//        w.pwmLevelDA.set(resValue);
//
//        w.rpmCounter.set(RpmUtil.getStrideCount(rpmCounter) * 2);
//
//
//
//
//        setFirstHeartRate();
//
////        LogUtil.d("ECHOOOOO", "[0x80] " + "目前AD的值 =" + resValue + ", 機型 = " + model + ", 手握心跳 = " + hpHr +
////                ", 無線心跳 = " + wpHr +
////                ", rpm = " + rpm +
////                ", speed = " + speed +
////                ", stepCount = " + stepCount +
////                ", rpm的計數器 = " + rpmCounter);
//
////        CommonUtils.getMemory();
//
//        // 增加顯示EMS error (EMS_MCU_ERROR)
//        processEmsErrors(mcuErrors);
//
//        if (devStep == DS_90_NORMAL_MODE_RSP) {
//            LogUtil.d(TAG, "onMcuControl 設定LWR為一般模式 (DS_90_NORMAL_MODE_RSP), 重送指令");
//            //     Toasty.warning(getApp(),lwr_mode.name(),Toasty.LENGTH_SHORT).show();
//            getDeviceSpiritC().setLwrMode(DeviceSpiritC.LWR_MODE.NORMAL);
//        }
//
//        //   checkEmsSteps();
//    }
//
//    public void setDevTargetRpm(int targetRpm) {
//
//        if (!mUartConnected) return;
//
//        int machineType = App.getApp().getDeviceSettingBean().getType();
//
//        m_targetRpm = (targetRpm == -1) ? getRandomValue(25, 100) : targetRpm;
//        LogUtil.d(TAG, "設定target RPM = " + m_targetRpm + " (0xA5)");
//
//        // 限定target RPM的數值範圍
//        m_targetRpm = Math.min(m_targetRpm, 255);
//        // m_targetRpm = Math.max(m_targetRpm, (machineType == MT_RECUMBENT_STEPPER) ? 2 : 10);
//        m_targetRpm = Math.max(m_targetRpm, (machineType == DEVICE_TYPE_UPRIGHT_BIKE) ? 2 : 10);
//        LogUtil.d(TAG, "設定target RPM (限定範圍後) = " + m_targetRpm + " (0xA5)");
//
//        getDeviceSpiritC().setTargetRpm(m_targetRpm);  // rpm的值從25~100(待確認)
//    }
//
//
////    public void setDevSpeedAccDecTimeTreadmill(int accSpeedTime, int decSpeedTime) {
////        // 限制數值在合法範圍, 以避免重送指令
////        accSpeedTime = Math.min(accSpeedTime, SPEED_CHANGE_TIME_MAX);
////        accSpeedTime = Math.max(accSpeedTime, SPEED_CHANGE_TIME_MIN);
////        decSpeedTime = Math.min(decSpeedTime, SPEED_CHANGE_TIME_MAX);
////        decSpeedTime = Math.max(decSpeedTime, SPEED_CHANGE_TIME_MIN);
////
////        if ((m_devAccRateInSec == accSpeedTime) && (m_devDecRateInSec == decSpeedTime))
////            return;
////
////        m_devAccRateInSec = accSpeedTime;
////        m_devDecRateInSec = decSpeedTime;
////
////        // 設定加減速之前, 需先確定RPM為0
////        setDevStep(DS_IDLE_SILENCE_RSP);
////    }
//
//    @Override
//    public void onEchoMode(DeviceSpiritC.ECHO_MODE echo_mode) {
//      //  LogUtil.d("ECHOOOOO", "onEchoMode: " + echo_mode);
//    }
//
//    @Override
//    public void onEEPRomWrite(DeviceSpiritC.MCU_SET mcu_set) {
//
//    }
//
//    @Override
//    public void onEEPRomRead(DeviceSpiritC.MCU_GET mcuGet, byte[] bytes, byte[] bytes1) {
//        LogUtil.d(TAG, "mcuGet = " + mcuGet);
//        if (bytes != null) {
//            for (byte b : bytes) {
//                LogUtil.d(TAG, "byte = " + b);
//            }
//        }
//        if (bytes1 != null) {
//            for (byte b1 : bytes1) {
//                LogUtil.d(TAG, "byte1 = " + b1);
//            }
//        }
//    }
//
//    @Override
//    public void onCurrentStepCount(int leftStepClockCnt, int rightStepClockCnt,
//                                   int stepsForStepper) {
//        m_AD_leftStepClockCnt = leftStepClockCnt;
//        m_AD_rightStepClockCnt = rightStepClockCnt;
//        m_AD_stepsForStepper = stepsForStepper;
//        LogUtil.d(TAG, "leftStepClockCnt = " + leftStepClockCnt + ", rightStepClockCnt = " + rightStepClockCnt + ", stepsForStepper = " + stepsForStepper);
//    }
//
//    @Override
//    public void onCurrentRpm(int rpmL, int rpmR, int revolution, int isoPwmL, int isoPwmR) {
//////        m_A6_rpmL = rpmL;
//////        m_A6_rpmR = rpmR;
////        m_A6_revolution = revolution;
////        m_A6_pwmL = isoPwmL;
////        m_A6_pwmR = isoPwmR;
////
////        w.rpmL.set(rpmL);
////        w.rpmR.set(rpmR);
////        LogUtil.d(TAG, "######onCurrentRpm ,rpmL = " + rpmL + ", rpmR = " + rpmR + ", revolution = " + revolution + ", isoPwmL = " + isoPwmL + ", isoPwmR = " + isoPwmR);
//    }
//
//    @Override
//    public void onAngleCali(DeviceSpiritC.ANGLE_CALI_STATUS angle_cali_status) {
//
//    }
//
//    @Override
//    public void onBrakeMode(DeviceSpiritC.BRAKE_MODE brake_mode) {
//
//    }
//
//    @Override
//    public void onCurrentSpeedAndInclineTreadmill(DeviceSpiritC.MOTOR_DIRECTION motorDir, int speedValue, int frontGradeAd, int rearGradeAd) {
//        //    LogUtil.d(TAG, "onCurrentSpeedAndInclineTreadmill  : speedValue = " + speedValue + ", frontGradeAd = " + frontGradeAd + ", rearGradeAd = " + rearGradeAd);
//
//        // 揚升是否在歸零狀態
//        boolean gradeZero = (frontGradeAd == w.getFrontInclineAdList().get(0));
//        //     boolean gradeZero = (frontGradeAd <= w.getFrontInclineAdList().get(0));
//        LogUtil.d(TAG, "########onCurrentSpeedAndInclineTreadmill:速度："+speedValue+", 前揚升AD：" + frontGradeAd + ", Console設定的第0階揚升AD:"+ w.getFrontInclineAdList().get(0)+", 揚升是否為第一階(歸0)：" + gradeZero);
//        switch (devStep) {
//            case DS_B2_IDLE_AD_READ_RSP:
//                if (speedValue != 0 || !gradeZero) {
//                    LogUtil.d(TAG, "回應速度及前後揚升未歸零, 設定速度及揚升歸零 (DS_B5_IDLE_AD_RESET_RSP)");
//                    setDevStep(DS_B5_IDLE_AD_RESET_RSP);
//                    resetSpeedAndIncline(false);
//                } else {
//                    LogUtil.d(TAG, "速度及前後揚升已歸零, 設定加減速時間 (DS_B6_IDLE_SPEED_RATE_RSP)");
//                    setDevStep(DS_B6_IDLE_SPEED_RATE_RSP);
//                    getDeviceSpiritC().setTargetSpeedRateTreadmill(m_devAccRateInSec, m_devDecRateInSec);
//                }
//                break;
//
////            case DS_B2_RUNNING_AD_READ_RSP:
////                if (gradeZero) {
////                    // GS mode, 揚升已歸零 (DS_RUNNING_STANDBY)
////                    LogUtil.d(TAG, "揚升已歸零(GS mode), RUNNING模式, 待命中 (DS_RUNNING_STANDBY)");
////                    setDevStep(DS_RUNNING_STANDBY);
////                } else {
////                    LogUtil.d(TAG, "揚升未歸零(GS mode), 設定揚升歸零 (DS_B5_RUNNING_AD_RESET_RSP)");
////                    setDevStep(DS_B5_RUNNING_AD_RESET_RSP);
////                    resetSpeedAndIncline(true);
////                }
////                break;
//        }
//    }
//
//    @Override
//    public void onUnitTreadmill(DeviceSpiritC.UNIT unit) {
//        switch (devStep) {
//            case DS_B3_IDLE_UNIT_RSP:
//                if (unit == m_devUnit) {
//                    LogUtil.d(TAG, "onUnitTreadmill: 單位符合, 讀取目前速度及揚升 (DS_B2_IDLE_AD_READ_RSP)");
//                    setDevStep(DS_B2_IDLE_AD_READ_RSP);
//                    getDeviceSpiritC().getCurrentSpeedAndInclineTreadmill();
//                } else {
//                    LogUtil.d(TAG, "onUnitTreadmill: 單位不符, 重送指令 (DS_B3_IDLE_UNIT_RSP)");
//                    getDeviceSpiritC().setUnitTreadmill(m_devUnit);
//                }
//        }
//    }
//
//    @Override
//    public void onStepInfoTreadmill(int symmetry, int leftStep, int rightStep, int leftLen,
//                                    int rightLen, int cadence) {
//
//        LogUtil.d(TAG, "onStepInfoTreadmill: " + cadence);
//        m_B4_symmetry = symmetry;
//        m_B4_leftStep = leftStep;
//        m_B4_rightStep = rightStep;
//        m_B4_leftLen = leftLen;
//        m_B4_rightLen = rightLen;
//        m_B4_cadence = cadence;
//    }
//
//    @Override
//    public void onTargetSpeedRateTreadmill(int accRate, int decRate) {
//        if (devStep == DS_B6_IDLE_SPEED_RATE_RSP) {
//            //   LogUtil.d(TAG, "accRate = " + accRate + ", m_devAccRateInSec = " + m_devAccRateInSec + ", decRate = " + decRate + ", m_devDecRateInSec = " + m_devDecRateInSec);
//            if ((accRate == m_devAccRateInSec) && (decRate == m_devDecRateInSec)) {
//                LogUtil.d(TAG, "********加減速時間設定符合, 待命中 (DS_IDLE_STANDBY) 允許user按[START]*******");
//                setDevStep(DS_IDLE_STANDBY);
//            } else {
//                LogUtil.d(TAG, "加減速時間設定不符, accRate = " + accRate + ", decRate = " + decRate + ", 重送指令");
//                getDeviceSpiritC().setTargetSpeedRateTreadmill(m_devAccRateInSec, m_devDecRateInSec);
//            }
//        }
//    }
//
//    /**
//     * 於工程模式下，每0.5秒自動上傳目前揚升AD、sensor AD
//     */
//    @Override
//    public void onCurrentInclineAndSensorTreadmill(int frontGradeAd, int rearGradeAd, int leftStepRawData, int rightStepRawData) {
//        //  LogUtil.d(TAG, "onCurrentInclineAndSensorTreadmill  前揚升AD = " + frontGradeAd );
//
//        if (deviceSettingViewModel.frontInclineAd.getValue() == frontGradeAd) return;
//
//        deviceSettingViewModel.frontInclineAd.setValue(frontGradeAd);
////        m_B9_frontGradeAd = frontGradeAd;
////        m_B9_rearGradeAd = rearGradeAd;
////        m_B9_LeftStepSensorRawData = leftStepRawData;
////        m_B9_RightStepSensorRawData = rightStepRawData;
//    }
//
//    @Override
//    public void onInclineCaliModeTreadmill(DeviceSpiritC.TREADMILL_CALI_MODE treadmillCaliMode) {
//        LogUtil.d(TAG, "onInclineCaliModeTreadmill: " + treadmillCaliMode.name());
//        switch (devStep) {
//            case DS_BB_CALIBRATION_START_RSP:
//                if (treadmillCaliMode == DeviceSpiritC.TREADMILL_CALI_MODE.START) {
//                    LogUtil.d(TAG, "已接收開始揚升校正 (DS_B0_CALIBRATION_ONGOING)");
//                    setDevStep(DS_B0_CALIBRATION_ONGOING);
//                } else {
//                    LogUtil.d(TAG, "尚未接收開始揚升校正, 重送指令");
//                    getDeviceSpiritC().setInclineCaliModeTreadmill(DeviceSpiritC.TREADMILL_CALI_MODE.START);
//                }
//                break;
//
//            case DS_BB_CALIBRATION_FRONT_RSP:
//                if (treadmillCaliMode == DeviceSpiritC.TREADMILL_CALI_MODE.FRONT) {
//                    LogUtil.d(TAG, "已接收開始單獨前揚升校正 (DS_B0_CALIBRATION_ONGOING)");
//                    setDevStep(DS_B0_CALIBRATION_ONGOING);
//                } else {
//                    LogUtil.d(TAG, "尚未接收開始單獨前揚升校正, 重送指令");
//                    getDeviceSpiritC().setInclineCaliModeTreadmill(DeviceSpiritC.TREADMILL_CALI_MODE.FRONT);
//                }
//                break;
//
//            case DS_BB_CALIBRATION_REAR_RSP:
//                if (treadmillCaliMode == DeviceSpiritC.TREADMILL_CALI_MODE.REAR) {
//                    LogUtil.d(TAG, "開始單獨後揚升校正設定符合 (DS_B0_CALIBRATION_ONGOING)");
//                    setDevStep(DS_B0_CALIBRATION_ONGOING);
//                } else {
//                    LogUtil.d(TAG, "開始單獨後揚升校正設定不符, 重送指令");
//                    getDeviceSpiritC().setInclineCaliModeTreadmill(DeviceSpiritC.TREADMILL_CALI_MODE.REAR);
//                }
//                break;
//
//            case DS_BB_CALIBRATION_STOP_RSP:
//                if (treadmillCaliMode == DeviceSpiritC.TREADMILL_CALI_MODE.STOP) {
//                    LogUtil.d(TAG, "停止揚升校正設定符合, 待命中 (DS_CALIBRATION_STANDBY), 允許user按[Start Calibration]");
//                    setDevStep(DS_CALIBRATION_STANDBY);
//                } else {
//                    LogUtil.d(TAG, "停止揚升校正設定不符, 重送指令");
//                    getDeviceSpiritC().setInclineCaliModeTreadmill(DeviceSpiritC.TREADMILL_CALI_MODE.STOP);
//                }
//                break;
//        }
//    }
//
//    @Override
//    public void onDefaultSpeedRateTreadmill(int i, int i1) {
//
//    }
//
//    /**
//     * Incline校正結束
//     */
//    @Override
//    public void onInclineRangeTreadmill(int frontGradeMax, int frontGradeMin, int rearGradeMax, int rearGradeMin) {
//        if (devStep == DS_BD_CALIBRATION_RESULT_RSP) {
//            LogUtil.d(TAG, "前後揚升最高點及最低點, 設定校正模式在待命中 (DS_CALIBRATION_STANDBY) 允許user按[Done] 或 [Calibration Again]");
//            setDevStep(DS_CALIBRATION_STANDBY);
//
//            updateFrontInclineAd(frontGradeMax, frontGradeMin);
//        }
//    }
//
//    @Override
//    public void onResetTreadmill(DeviceSpiritC.RESET_STATUS resetStatus) {
//
//        LogUtil.d(TAG, "onResetTreadmill: " + resetStatus + "," + devStep);
//        if (devStep == DS_FA_CONSOLE_RESET_RSP) {
//            if (resetStatus == DeviceSpiritC.RESET_STATUS.RESET) {
//                LogUtil.d(TAG, "已接收LWR重置指令, 設定進入CONSOLE ERROR");
//                setDevStep(DS_B7_CONSOLE_ERR_RSP);
//                setMainModeTreadmill(DeviceSpiritC.MAIN_MODE.CONSOLE_ERROR);
//            } else {
//                LogUtil.d(TAG, "尚未接收LWR重置指令, 重送指令");
//                getDeviceSpiritC().setResetTreadmill();
//            }
//        } else if (devStep == DS_FA_UPDATE_RESET_RSP) {
//            if (resetStatus == DeviceSpiritC.RESET_STATUS.RESET) {
//                setDevStep(DS_FA_WAIT_COME_BACK);
//                LogUtil.d(TAG, "已接收LWR重置指令, 3秒後, 進行讀取DeviceInfo");
//
//                new RxTimer().timer(3000, number -> {
//                    setDevStep(DS_99_UPDATE_DEV_INFO_RSP);
//                    LogUtil.d(TAG, "執行GetDeviceInfo()");
//                    getDeviceSpiritC().getDeviceInfo();
//                });
//            } else {
//                LogUtil.d(TAG, "尚未接收LWR重置指令, 重送指令");
//                getDeviceSpiritC().setResetTreadmill(); // 此指令: EMS及Treadmill共用
//            }
//        }
//    }
//
//    @Override
//    public void onResetToBootloaderTreadmill(DeviceSpiritC.RESET_STATUS reset_status) {
//
//    }
//
//    @Override
//    public void onMultiKey(int value, List<DeviceSpiritC.KEY> list) {
//
//     //   LogUtil.d("KKKKKKEEEEEYYYYY", "onMultiKey(長按): " + list);
//
//        if (list == null) return;
//
//        if (!w.isSafeKey.get()) return;
//
//        if (m.retailVideoWindow != null && m.retailVideoWindow.isShowing()) {
//            m.retailVideoWindow.dismiss();
//            m.retailVideoWindow = null;
//            m.onUserInteraction();
//            return;
//        }
//
//
//        if (appStatusViewModel.currentStatus.get() == AppStatusIntDef.STATUS_MAINTENANCE) {
//            LiveEventBus.get(ON_MULTI_KEY).post(list);
//            return;
//        }
//
//        for (DeviceSpiritC.KEY key : list) {
//            switch (key) {
//                case KEY00: //長按INCLINE +
//                    //  LiveEventBus.get(FTMS_SET_TARGET_INCLINATION).post(CLICK_PLUS);
//                    LiveEventBus.get(FTMS_SET_TARGET_INCLINATION).post(LONG_CLICK_PLUS);
//                    break;
//                case KEY01: //長按INCLINE -
//                    //  LiveEventBus.get(FTMS_SET_TARGET_INCLINATION).post(CLICK_MINUS);
//                    LiveEventBus.get(FTMS_SET_TARGET_INCLINATION).post(LONG_CLICK_MINUS);
//                    break;
//                case KEY02: //START
//                    break;
//                case KEY03: //ENTER
//                    break;
//                case KEY04: //STOP
//                    break;
//                case KEY05: //長按SPEED -
//
//                    if (isTreadmill) {
//                        LiveEventBus.get(FTMS_SET_TARGET_SPEED).post(LONG_CLICK_MINUS);
//                    } else {
////                        LiveEventBus.get(FTMS_SET_TARGET_SPEED).post(LONG_CLICK_PLUS);
//                        //bike level
//                        LiveEventBus.get(FTMS_SET_TARGET_SPEED).post(LONG_CLICK_MINUS);
//                    }
//
//                    break;
//                case KEY06: //長按SPEED +
//                    if (isTreadmill) {
//                        LiveEventBus.get(FTMS_SET_TARGET_SPEED).post(LONG_CLICK_PLUS);
//                    } else {
////                        LiveEventBus.get(FTMS_SET_TARGET_SPEED).post(LONG_CLICK_MINUS);
//
//                        LiveEventBus.get(FTMS_SET_TARGET_SPEED).post(LONG_CLICK_PLUS);
//                    }
//
//                    break;
//                case KEY_UNKNOWN:
//                    break;
//                default:
//                    break;
//            }
//        }
//    }
//
//    @Override
//    public void onKeyTrigger(DeviceSpiritC.KEY key) {
//
//     //   LogUtil.d("KKKKKKEEEEEYYYYY", "onKeyTrigger: " + key);
//
//        //   LogUtil.d("休眠", "CommonUtils.isScreenOn(): " + CommonUtils.isScreenOn());
//
////        if (!CommonUtils.isScreenOn()) {
////            CommonUtils.wakeUpScreen(m);
////        }
//
//        if (!w.isSafeKey.get()) return;
//
//        if (m.retailVideoWindow != null && m.retailVideoWindow.isShowing()) {
//            m.retailVideoWindow.dismiss();
//            m.retailVideoWindow = null;
//            m.onUserInteraction();
//            return;
//        }
//
//        switch (key) {
//            case KEY00: //INCLINE +
//                //   LiveEventBus.get(FTMS_SET_TARGET_INCLINATION).post(1);
//                LiveEventBus.get(FTMS_SET_TARGET_INCLINATION).post(CLICK_PLUS);
//                break;
//            case KEY01: //INCLINE -
//                //  LiveEventBus.get(FTMS_SET_TARGET_INCLINATION).post(-1);
//                LiveEventBus.get(FTMS_SET_TARGET_INCLINATION).post(CLICK_MINUS);
//                break;
//            case KEY02: //START
//                LiveEventBus.get(FTMS_START_OR_RESUME).post(true);
//                break;
//            case KEY03: //ENTER
//                if (isTreadmill) {
//                    LiveEventBus.get(KEY_ENTER).post(true);
//                } else {
//                    LiveEventBus.get(FTMS_STOP_OR_PAUSE).post(2);
//                }
//                break;
//            case KEY04: //STOP
//                if (isTreadmill) {
//                    LiveEventBus.get(FTMS_STOP_OR_PAUSE).post(2);
//                } else {
//                    LiveEventBus.get(KEY_ENTER).post(true);
//                }
//                break;
//            case KEY05: //SPEED -
//                if (isTreadmill) {
//                    LiveEventBus.get(FTMS_SET_TARGET_SPEED).post(CLICK_MINUS);
//                } else {
//                    //BIKE KEY05 LEVEL +
//                    //   LiveEventBus.get(FTMS_SET_TARGET_SPEED).post(CLICK_PLUS);
//
//
//                    //BIKE KEY05 LEVEL -
//                    LiveEventBus.get(FTMS_SET_TARGET_SPEED).post(CLICK_MINUS);
//
//
//                }
//                break;
//            case KEY06: //SPEED +
//                if (isTreadmill) {
//                    LiveEventBus.get(FTMS_SET_TARGET_SPEED).post(CLICK_PLUS);
//                } else {
//                    //     LiveEventBus.get(FTMS_SET_TARGET_SPEED).post(CLICK_MINUS);
//
//
//                    //BIKE KEY05 LEVEL +
//                    LiveEventBus.get(FTMS_SET_TARGET_SPEED).post(CLICK_PLUS);
//
//                }
//                break;
//            case KEY_UNKNOWN: //長按結束
//                // 若長按後放掉, 會收到KEY_UNKNOWN 0xFF屬正常
//
//                LiveEventBus.get(KEY_UNKNOWN).post(true);
//                break;
//            default:
//                break;
//        }
//    }
//
//    @Override
//    public void onUsbModeSet(DeviceSpiritC.MCU_SET mcuSet) {
//     //   LogUtil.d("HHHAAAAA", "onUsbModeSet: " + mcuSet);
//        LiveEventBus.get(ON_USB_MODE_SET).post(mcuSet);
//    }
//
//    @Override
//    public void onHeartRateMode(DeviceSpiritC.HEART_RATE_MODE heart_rate_mode) {
//
//    }
//
//    @Override
//    public void onRpmCounterMode(DeviceSpiritC.RPM_COUNTER_MODE rpm_counter_mode) {
//
//    }
//
//
//    /**
//     * 結束訓練
//     */
//    @Override
//    public void setDevWorkoutFinish() {
//
//        if (!mUartConnected) return;
//
//        boolean breakWorkout = !w.isWorkoutDone();
//        //  boolean gsMode = !deviceSettingViewModel.gsMode.get();
//        boolean gsMode = deviceSettingViewModel.gsMode.get();
//        if (devStep >= DS_B7_RUNNING_RSP && devStep <= DS_B5_RESUME_AD_CHANGE_RSP) {
//            LogUtil.d(TAG, "[setDevWorkoutFinish] 結束訓練, 設定 {速度及揚升歸零(0xB5)}");
//            setDevStep(gsMode ? (breakWorkout ? DS_B5_BREAK_SPD_RESET_RSP : DS_B5_FINISH_AD_RESET_RSP) : DS_B5_FINISH_AD_RESET_RSP);
//
//            resetSpeedAndIncline(!gsMode || !breakWorkout);
//        } else {
//            LogUtil.d(TAG, "[setDevWorkoutFinish] 無法結束Workout STEP:" + getMyStepString());
//        }
//    }
//
////    public void setDevConsoleMode(DeviceSpiritC.CONSOLE_MODE consoleMode) {
////        if (!mUartConnected) return;
////
////        switch (consoleMode) {
////            case IDLE:
////                setDevStep(DS_A0_IDLE_RSP);
//////                setDevStep(DS_EMS_IDLE_STANDBY);
////                break;
////
////            case PAUSE:
////                setDevStep(DS_A0_PAUSE_RSP);
////                break;
////
////            case NORMAL:    // workload: level, power
////            case ISO:       // workload: iso (speed)
////                setDevStep(DS_A0_RUNNING_RSP);
////                break;
////
////            case UNKNOWN:
////                return;
////        }
////        m_devConsoleMode = consoleMode;
////        getDeviceSpiritC().setConsoleMode(m_devMachineType, m_devConsoleMode); //onConsoleMode
////        LogUtil.d(TAG, "setDevConsoleMode: " + m_devMachineType + "," + m_devConsoleMode);
////
////    }
//
//    @Override
//    public String getMyStepString() {
//        switch (devStep) {
//            case GENERAL.DS_RESET_STANDBY:
//                return "DS_RESET_STANDBY";
//            case GENERAL.DS_B7_RESET_RSP:
//                return "DS_B7_RESET_RSP";
//            case GENERAL.DS_FA_UPDATE_RESET_RSP:
//                return "DS_FA_UPDATE_RESET_RSP";
//            case GENERAL.DS_FA_WAIT_COME_BACK:
//                return "DS_FA_WAIT_COME_BACK";
//            case GENERAL.DS_99_UPDATE_DEV_INFO_RSP:
//                return "DS_99_UPDATE_DEV_INFO_RSP";
//            case GENERAL.DS_BE_UPDATE_TO_TC_RSP:
//                return "DS_BE_UPDATE_TO_TC_RSP";
//            case GENERAL.DS_CONNECT_RSP:
//                return "DS_CONNECT_RSP";
//            case GENERAL.DS_99_DEV_INFO_RSP:
//                return "DS_99_DEV_INFO_RSP";
//            case GENERAL.DS_99_ERR_MACHINE_TYPE:
//                return "DS_99_ERR_MACHINE_TYPE";
//            case GENERAL.DS_BE_TO_TC_RSP:
//                return "DS_BE_TO_TC_RSP";
//            case GENERAL.DS_LWR_INITIALIZING:
//                return "DS_LWR_INITIALIZING";
//            case GENERAL.DS_B7_APP_START_RSP:
//                return "DS_B7_APP_START_RSP";
//            case GENERAL.DS_B7_IDLE_RSP:
//                return "DS_B7_IDLE_RSP";
//            case GENERAL.DS_B3_IDLE_UNIT_RSP:
//                return "DS_B3_IDLE_UNIT_RSP";
//            case GENERAL.DS_B2_IDLE_AD_READ_RSP:
//                return "DS_B2_IDLE_AD_READ_RSP";
//            case GENERAL.DS_B5_IDLE_AD_RESET_RSP:
//                return "DS_B5_IDLE_AD_RESET_RSP";
//            case GENERAL.DS_B6_IDLE_SPEED_RATE_RSP:
//                return "DS_B6_IDLE_SPEED_RATE_RSP";
//            case GENERAL.DS_IDLE_SILENCE_RSP:
//                return "DS_IDLE_SILENCE_RSP";
//            case GENERAL.DS_IDLE_STANDBY:
//                return "DS_IDLE_STANDBY";
//            case GENERAL.DS_B0_ERR_LWR_INIT_FAIL:
//                return "DS_B0_ERR_LWR_INIT_FAIL";
//            case GENERAL.DS_B0_ERR_CONVERTER:
//                return "DS_B0_ERR_CONVERTER";
//            case GENERAL.DS_B7_ERR_SAFETY_KEY_RSP:
//                return "DS_B7_ERR_SAFETY_KEY_RSP";
//            case GENERAL.DS_B7_CLR_SAFETY_KEY_RSP:
//                return "DS_B7_CLR_SAFETY_KEY_RSP";
//            case GENERAL.DS_B7_PAUSE_RSP:
//                return "DS_B7_PAUSE_RSP";
//            case GENERAL.DS_B7_RUNNING_RSP:
//                return "DS_B7_RUNNING_RSP";
//            case GENERAL.DS_RUNNING_STANDBY:
//                return "DS_RUNNING_STANDBY";
//            case GENERAL.DS_B5_RUNNING_AD_CHANGE_RSP:
//                return "DS_B5_RUNNING_AD_CHANGE_RSP";
//            case GENERAL.DS_B7_PAUSE_GS_RSP:
//                return "DS_B7_PAUSE_GS_RSP";
//            case GENERAL.DS_B5_PAUSE_AD_RESET_RSP:
//                return "DS_B5_PAUSE_AD_RESET_RSP";
//            case GENERAL.DS_PAUSE_SILENCE_RSP:
//                return "DS_PAUSE_SILENCE_RSP";
//            case GENERAL.DS_PAUSE_STANDBY:
//                return "DS_PAUSE_STANDBY";
//            case GENERAL.DS_B7_RESUME_RUNNING_RSP:
//                return "DS_B7_RESUME_RUNNING_RSP";
//            case GENERAL.DS_B5_RESUME_AD_CHANGE_RSP:
//                return "DS_B5_RESUME_AD_CHANGE_RSP";
//            case GENERAL.DS_B5_FINISH_AD_RESET_RSP:
//                return "DS_B5_FINISH_AD_RESET_RSP";
//            case GENERAL.DS_FINISH_SILENCE_RSP:
//                return "DS_FINISH_SILENCE_RSP";
//            case GENERAL.DS_B7_FINISH_PAUSE_RSP:
//                return "DS_B7_FINISH_PAUSE_RSP";
//            case GENERAL.DS_B7_FINISH_IDLE_RSP:
//                return "DS_B7_FINISH_IDLE_RSP";
//            case GENERAL.DS_B7_MAINTENANCE_RSP:
//                return "DS_B7_MAINTENANCE_RSP";
//            case GENERAL.DS_MAINTENANCE_STANDBY:
//                return "DS_MAINTENANCE_STANDBY";
//            case GENERAL.DS_BA_BREAK_MODE_RSP:
//                return "DS_BA_BREAK_MODE_RSP";
//            case GENERAL.DS_B5_MAINTENANCE_AD_CHANGE_RSP:
//                return "DS_B5_MAINTENANCE_AD_CHANGE_RSP";
//            case GENERAL.DS_B7_CALIBRATION_RSP:
//                return "DS_B7_CALIBRATION_RSP";
//            case GENERAL.DS_CALIBRATION_STANDBY:
//                return "DS_CALIBRATION_STANDBY";
//            case GENERAL.DS_BB_CALIBRATION_START_RSP:
//                return "DS_BB_CALIBRATION_START_RSP";
//            case GENERAL.DS_BB_CALIBRATION_FRONT_RSP:
//                return "DS_BB_CALIBRATION_FRONT_RSP";
//            case GENERAL.DS_BB_CALIBRATION_REAR_RSP:
//                return "DS_BB_CALIBRATION_REAR_RSP";
//            case GENERAL.DS_BB_CALIBRATION_STOP_RSP:
//                return "DS_BB_CALIBRATION_STOP_RSP";
//            case GENERAL.DS_B0_CALIBRATION_ONGOING:
//                return "DS_B0_CALIBRATION_ONGOING";
//            case GENERAL.DS_B0_CALIBRATION_FRONT_MAX:
//                return "DS_B0_CALIBRATION_FRONT_MAX";
//            case GENERAL.DS_B0_CALIBRATION_FRONT_MIN:
//                return "DS_B0_CALIBRATION_FRONT_MIN";
//            case GENERAL.DS_B0_CALIBRATION_REAR_MAX:
//                return "DS_B0_CALIBRATION_REAR_MAX";
//            case GENERAL.DS_B0_CALIBRATION_REAR_MIN:
//                return "DS_B0_CALIBRATION_REAR_MIN";
//            case GENERAL.DS_BD_CALIBRATION_RESULT_RSP:
//                return "DS_BD_CALIBRATION_RESULT_RSP";
//            case GENERAL.DS_A0_IDLE_RSP:
//                return "DS_A0_IDLE_RSP";
//            case GENERAL.DS_A9_SYMM_ANGLE_RSP:
//                return "DS_A9_SYMM_ANGLE_RSP";
//            case GENERAL.DS_80_IDLE_ECHO_RSP:
//                return "DS_80_IDLE_ECHO_RSP";
//            case GENERAL.DS_EMS_IDLE_STANDBY:
//                return "DS_EMS_IDLE_STANDBY";
//            case GENERAL.DS_A0_RUNNING_RSP:
//                return "DS_A0_RUNNING_RSP";
//            case GENERAL.DS_A0_RUNNING_STANDBY:
//                return "DS_A0_RUNNING_STANDBY";
//            case GENERAL.DS_A5_TARGET_RPM_RSP:
//                return "DS_A5_TARGET_RPM_RSP";
//            case GENERAL.DS_80_PWM_LEVEL_RSP:
//                return "DS_80_PWM_LEVEL_RSP";
//            case GENERAL.DS_A0_PAUSE_RSP:
//                return "DS_A0_PAUSE_RSP";
//            case GENERAL.DS_A0_PAUSE_STANDBY:
//                return "DS_A0_PAUSE_STANDBY";
//            case GENERAL.DS_A0_FINISH_IDLE_RSP:
//                return "DS_A0_FINISH_IDLE_RSP";
//            case GENERAL.DS_CORESTAR:
//                return "DS_CORESTAR";
//            case GENERAL.DS_FA_CONSOLE_RESET_RSP:
//                return "DS_FA_CONSOLE_RESET_RSP";
//            case GENERAL.DS_B7_CONSOLE_ERR_RSP:
//                return "DS_B7_CONSOLE_ERR_RSP";
//            case GENERAL.DS_CONSOLE_ERR_STANDBY:
//                return "DS_CONSOLE_ERR_STANDBY";
//            case GENERAL.DS_BF_CLEAR_ERRS_RSP:
//                return "DS_BF_CLEAR_ERRS_RSP";
//            case GENERAL.DS_BF_CLEAR_ERR1_RSP:
//                return "DS_BF_CLEAR_ERR1_RSP";
//            case GENERAL.DS_BF_CLEAR_ERR2_RSP:
//                return "DS_BF_CLEAR_ERR2_RSP";
//            case GENERAL.DS_B8_STOP_SC_RSP:
//                return "DS_B8_STOP_SC_RSP";
//            case GENERAL.DS_B8_STOP_SC_STANDBY:
//                return "DS_B8_STOP_SC_STANDBY";
//            case GENERAL.DS_90_NORMAL_MODE_RSP:
//                return "DS_90_NORMAL_MODE_RSP";
//            case GENERAL.DS_EMS_RUNNING:
//                return "DS_EMS_RUNNING";
//            case GENERAL.DS_EMS_PAUSE:
//                return "DS_EMS_PAUSE";
//            case GENERAL.DS_B5_BREAK_SPD_RESET_RSP:
//                return "DS_B5_BREAK_SPD_RESET_RSP";
////            case GENERAL.DS_B2_RUNNING_AD_READ_RSP:
////                return "DS_B2_RUNNING_AD_READ_RSP";
////            case GENERAL.DS_B5_RUNNING_AD_RESET_RSP:
////                return "DS_B5_RUNNING_AD_RESET_RSP";
////            case GENERAL.DS_RUNNING_SILENCE_RSP:
////                return "DS_RUNNING_SILENCE_RSP";
//        }
//        return "UNKNOWN STEP!";
//    }
//
//    /**
//     * 設定跑步機的速度及揚升
//     */
//    @Override
//    public void setDevSpeedAndIncline() {
//
//        if (!mUartConnected) return;
//
//        w.currentSpeedLevel.set(getSpeedLevel(w.currentSpeed.get()));
//
//        w.currentFrontInclineAd.set(getInclineAd(w));
//
//        switch (devStep) {
//            case DS_RUNNING_STANDBY:
////                LogUtil.d(TAG, "在RUNNING模式, 設定速度 = " + w.currentSpeedLevel.get() +
////                        ", 前揚升AD = " + w.currentFrontInclineAd.get());
//
//                setDevStep(DS_B5_RUNNING_AD_CHANGE_RSP);
//
//                setTargetSpeedAndInclineTreadmill(w.currentSpeedLevel.get(), w.currentFrontInclineAd.get());
//                break;
//
//            case DS_MAINTENANCE_STANDBY:
//                LogUtil.d(TAG, "在工程模式,  m_devStep = " + getMyStepString());
//
//                setDevStep(DS_B5_MAINTENANCE_AD_CHANGE_RSP);
//
//
//                setTargetSpeedAndInclineTreadmill(w.currentSpeedLevel.get(), w.currentFrontInclineAd.get());
//
//                break;
//            default:
//                LogUtil.d(TAG, "setDevSpeedAndIncline 尚未準備好: STEP:" + getMyStepString() + "重發");
//                //    setDevSpeedAndIncline();
//
//                break;
//        }
//    }
//
//    //Speed 是 level
//    private void setTargetSpeedAndInclineTreadmill(int speed, int incline) {
//
//        LogUtil.d(TAG, ">>>>>>>>>>發送,<SPEED>level=" + speed + ",value=" + getSpeedValue(speed) + ", <INCLINE>ad=" + incline + ",value=" + w.currentInclineValue.get() + ",level=" + getInclineLevel(w.currentInclineValue.get()));
//
//
//        getDeviceSpiritC().setTargetSpeedAndInclineTreadmill(DeviceSpiritC.MOTOR_DIRECTION.FORWARD, speed, incline, 0);
//
//
//        getDeviceSpiritC().getCurrentSpeedAndInclineTreadmill();
//
//
//        if (isMediaWorkoutController) {
//            LiveEventBus.get(NEW_UPDATE_VALUE).post(true);
//        }
//    }
//
//
//    public void setEmsMachineType() {
//
//        switch (getApp().getDeviceSettingBean().getType()) {
//            case DEVICE_TYPE_RECUMBENT_BIKE:
//                m_devMachineType = DeviceSpiritC.MACHINE_TYPE.RECUMBENT_BIKE;
//                break;
//            case DEVICE_TYPE_ELLIPTICAL:
//                m_devMachineType = DeviceSpiritC.MACHINE_TYPE.RECUMBENT_STEPPER;
//                break;
//            case DEVICE_TYPE_UPRIGHT_BIKE:
//                m_devMachineType = DeviceSpiritC.MACHINE_TYPE.UPRIGHT_BIKE;
//                break;
//            case DEVICE_TYPE_TREADMILL:
//                m_devMachineType = DeviceSpiritC.MACHINE_TYPE.UNKNOWN;
//                break;
//        }
//        LogUtil.d(TAG, "setEmsMachineType: emsMachineType = " + m_devMachineType);
//    }
//
//    @Override
//    public void startDevCalibration(DeviceSpiritC.TREADMILL_CALI_MODE caliMode) {
//
//        if (!mUartConnected) return;
//
//        if ((devStep == DS_CALIBRATION_STANDBY) || (devStep == DS_MAINTENANCE_STANDBY)) {
//
//            if (caliMode == DeviceSpiritC.TREADMILL_CALI_MODE.START) {
//                LogUtil.d(TAG, "[startDevCalibration] 設定 {開始揚升校正(0xBB)}");
//                setDevStep(DS_BB_CALIBRATION_START_RSP);
//            } else if (caliMode == DeviceSpiritC.TREADMILL_CALI_MODE.FRONT) {
//                LogUtil.d(TAG, "[startDevCalibration] 設定 {開始前揚升校正(0xBB)}");
//                setDevStep(DS_BB_CALIBRATION_FRONT_RSP);
//            } else if (caliMode == DeviceSpiritC.TREADMILL_CALI_MODE.REAR) {
//                LogUtil.d(TAG, "[startDevCalibration] 設定 {開始後揚升校正(0xBB)}");
//                setDevStep(DS_BB_CALIBRATION_REAR_RSP);
//            }
//            getDeviceSpiritC().setInclineCaliModeTreadmill(caliMode);
//        }
//    }
//
//
//    public static final int EDIT_GRADE_AD_MIN = 10;
//    public static final int EDIT_GRADE_AD_MAX = 32700;
//
//    @Override
//    public void onEupNotify(DeviceSpiritC.EUP eup) {
//        LogUtil.d(TAG, "onEupNotify: " + eup);
//
//        //    App.getDeviceSpiritC().setEUP(2);
//    }
//
//    @Override
//    public void onLwrMode(DeviceSpiritC.LWR_MODE lwr_mode) {
//
//        if (lwr_mode == DeviceSpiritC.LWR_MODE.NORMAL) {
//            LogUtil.d(TAG, "onLwrMode LWR為一般模式符合, 待命中 (DS_EMS_IDLE_STANDBY)");
//            setDevStep(DS_EMS_IDLE_STANDBY);
//        } else {
//            //   Toasty.warning(getApp(),lwr_mode.name(),Toasty.LENGTH_SHORT).show();
//            LogUtil.d(TAG, "onLwrMode LWR模式不符, 重送指令 (DS_EMS_IDLE_STANDBY)");
//            getDeviceSpiritC().setLwrMode(DeviceSpiritC.LWR_MODE.NORMAL);
//        }
//    }
//
//    @Override
//    public void onSpeedCali(DeviceSpiritC.SPEED_CALI_STEP
//                                    speed_cali_step, DeviceSpiritC.SPEED_CALI_STATUS
//                                    speed_cali_status, List<DeviceSpiritC.MCU_ERROR> list, int i, int i1) {
//        LogUtil.d(TAG, "onSpeedCali: " + speed_cali_status + "," + speed_cali_status + "," + list + "," + i + "," + i1);
//    }
//
//    @Override
//    public void onEcbAdjust(DeviceSpiritC.ACTION_STATUS action_status, int i) {
//        LogUtil.d(TAG, "onEcbAdjust: ");
//    }
//
//    @Override
//    public void onInclineAdjust(DeviceSpiritC.ACTION_STATUS action_status, int i) {
//        LogUtil.d(TAG, "onInclineAdjust: ");
//    }
//
//    @Override
//    public void onInclineRead(int i, int i1, DeviceSpiritC.
//            INCLINE_READ_STATUS incline_read_status) {
//        LogUtil.d(TAG, "onInclineRead: ");
//    }
//
//    @Override
//    public void onInclineCali(DeviceSpiritC.INCLINE_CALI_STATUS incline_cali_status, int i) {
//        LogUtil.d(TAG, "onInclineCali: ");
//    }
//
//    @Override
//    public void onConnectFail() {
//        mUartConnected = false;
//        LogUtil.d(TAG, "UART onConnectFail: ");
//    }
//
//    //uartConsole.initialize();
//    @Override
//    public void onConnected() {
//        LogUtil.d(TAG, "UART onConnected: ");
//        LogUtil.d("GEM3", "UART onConnected: ");
//        mUartConnected = true;
//        //  setLwrMcuOnApRom();
//
//        startUartFlowCommand();
//        m.initGem3();
//    }
//
//    @Override
//    public void onDisconnected() {
//        mUartConnected = false;
//        LogUtil.d(TAG, "UART onDisconnected: ");
//        //重啟
//    }
//
//    @Override
//    public void onDataSend(String s) {
//        //    LogUtil.d(TAG, "onDataSend >>>>>: " + s);
//    }
//
//    @Override
//    public void onDataReceive(String s) {
//        // 已接收到LWR回應
//        mLwrTimeoutCnt = 0;
//
//        //   LogUtil.d(TAG, "onDataReceive <<<<<<: " + s);
//    }
//
//    @Override
//    public void onErrorMessage(String s) {
//        LogUtil.d(TAG, "onErrorMessage: " + s);
//    }
//
//    @Override
//    public void onCommandError(DeviceSpiritC.COMMAND command, DeviceSpiritC.COMMAND_ERROR command_error) {
//
//        LogUtil.d(TAG, "onCommandError: " + command.name() + "," + command_error.name());
//    }
//
//
//    public void setDevDriveMotorTreadmill() {
//
//        // 工程模式僅限speed為正值 (主要測試電流)
//        if (devStep == DS_MAINTENANCE_STANDBY) {
//            setDevStep(DS_B5_MAINTENANCE_AD_CHANGE_RSP);
//            setTargetSpeedAndInclineTreadmill(w.currentSpeedLevel.get(), 0);
//        } else {
//            LogUtil.d(TAG, "尚未收到回覆指令, 不發送, m_devStep = " + getMyStepString());
//        }
//
//        LogUtil.d(TAG, "設定跑帶速度, m_devStep = " + getMyStepString() + ", m_currentSpeedValue = " + w.currentSpeedLevel.get());
//
//    }
//
//
//    /**
//     * #######################EMS#######################
//     */
//
//    // private int m_pwmLevelDA;
//    public void setDevPwmLevel() {
//        if (!mUartConnected) return;
//
//        int pwmLevelDA = 0;
//
//        if (devStep == DS_EMS_RUNNING)  //判斷是否在RUNNING, 其它情況就是送0的值
//            pwmLevelDA = w.pwmLevelDA.get();
//
//        // 限定pwm level D/A值在範圍內 0~1023
//        pwmLevelDA = Math.max(pwmLevelDA, 0);
//        pwmLevelDA = Math.min(pwmLevelDA, 1023);
//
//        //  LogUtil.d(TAG, "setDevPwmLevel, 傳送的pwmLevelDA = " + pwmLevelDA);
//
//        getDeviceSpiritC().setEMS(DeviceSpiritC.MODEL.EMS, pwmLevelDA, DeviceSpiritC.ACTION_MODE.STOP, 0);
//        if (isMediaWorkoutController) {
//            LiveEventBus.get(NEW_UPDATE_VALUE).post(true);
//        }
//    }
//
//    //Update Level
//    @Override
//    public void setDevWorkload(int level, int resistance) {
//        switch (resistance) {
//            case RT_LEVEL:
//                setPwmLevel_LEVEL(level); // 取得 Level AD
//                getDeviceSpiritC().setEMS(DeviceSpiritC.MODEL.EMS, w.pwmLevelDA.get(), DeviceSpiritC.ACTION_MODE.STOP, 0);
//                if (isMediaWorkoutController) {
//                    LiveEventBus.get(NEW_UPDATE_VALUE).post(true);
//                }
//                break;
//            case RT_POWER: //WATT
//                setPwmLevel_POWER(level);
//                setDevPwmLevel();
//                break;
//        }
//    }
//
//
//    /**
//     * BIKE， RPM<15時，watt = 0。
//     * 1．workload = Level
//     * (int) rpm_level = (RPM / 5) - 2
//     * Level = 1~150
//     * DA_Level = (Level - 1) * 6
//     * Watt = rpm_level + DA_Level * (rpm_level + 1200) * rpm_level / 13800
//     * *若要自己算watt就可以套此公式, 或是由Martin的lib
//     * <p>
//     * 2．workload = power
//     * target power = Target_watt
//     * DA_Level= (Watt - rpm_level ) * 13800 / ((rpm_level + 1200) * rpm_level )
//     * *此處的Watt是目前要設定的workload
//     *
//     * @param level
//     */
//    public static final int RNG_LEVEL_MIN = 0;
//    public static final int RNG_LEVEL_MAX = 150;
//
//    // workload為Level時, 依設定的level, 取得pwmLevel(UART設定值)
//    public void setPwmLevel_LEVEL(int level) {
//        // 若是之後改查表方式, 則回傳以level查表得到的數值, 傳送給LWR時, 需*2, 即pwmLevel = getTableValueByLevel * 2
//
//        //目前先依公式做計算, 傳送給LWR的值就不需再*2
//        // 限定level的範圍值在1~150
////        level = Math.max(level, RNG_LEVEL_MIN + 1);
////        level = Math.min(level, RNG_LEVEL_MAX);
////        w.pwmLevelDA.set((level - 1) * 4095 / 30 / 40); // == 查表
//
//        level = Math.max(level, MAX_LEVEL_MIN);
//        level = Math.min(level, MAX_LEVEL_MAX);
//
//        // TODO: PF
////        w.pwmLevelDA.set(MODE.getLevelAD()[level - 1]); // == 查表
//        w.pwmLevelDA.set(MODE.getLevelAD()[level]); // == 查表
//
//
//        LogUtil.d(TAG, "setPwmLevel_LEVEL = " + w.pwmLevelDA.get() + ", level = " + level);
//    }
//
//    //workload為Power時, 依power計算取得pwmLevel(UART設定值)
//    public void setPwmLevel_POWER(int level) {
//
////        int value = (level - 1) * 4095 / 30 / 40;
////        if (value < 0) value = 0;
////        w.pwmLevelDA.set(value);// == 查表
//
//        int dLevel = level - 1;
//        if (dLevel < 0) dLevel = 0;
//
//        w.pwmLevelDA.set(MODE.getLevelAD()[dLevel]);
////        w.pwmLevelDA.set(MODE.getLevelAD()[level - 1]);
//
//
//        LogUtil.d(TAG, "m_pwmLevelDA = " + w.pwmLevelDA.get() + ", level = " + level);
//
//        LogUtil.d("FITNESS_TEST_PROGRAM", "22222setPwmLevel_POWER: " + w.currentLevel.get());
//    }
//
//
//    private int getRpmLevel() {
//        // T依據目前的RPM, 計算RPM level
//        int machineType = App.getApp().getDeviceSettingBean().getType();
//        int rpm = w.currentRpm.get();
//        int subtrahend = (machineType == DEVICE_TYPE_UPRIGHT_BIKE) ? 1 : 2;
//        int rpmMin = (machineType == DEVICE_TYPE_UPRIGHT_BIKE) ? 10 : 15;
//        LogUtil.d(TAG, "rpm = " + rpm);
//
//        // 因扭力機只能測到RPM >= 15, 若低於15, 則直接設定為0
////        rpmMin = 15;
//        return (rpm < rpmMin) ? 0 : (rpm / 5) - subtrahend;
//    }
//
//    private void checkEmsSteps() {
//
//        //檢查下達的EMS指令是否已回傳, 否則重送指令
//
//        switch (devStep) {
//            case DS_A0_IDLE_RSP:
//                LogUtil.d(TAG, "設定車種及目前模式 (DS_A0_IDLE_RSP), 重送指令, m_devMachineType = " + m_devMachineType + ", consoleMode = " + DeviceSpiritC.CONSOLE_MODE.IDLE);
//                getDeviceSpiritC().setConsoleMode(m_devMachineType, DeviceSpiritC.CONSOLE_MODE.IDLE);
//                break;
//
//            case DS_A9_SYMM_ANGLE_RSP:
//                LogUtil.d(TAG, "設定SYMMETRY ANGLE (DS_A9_SYMM_ANGLE_RSP), 重送指令, m_A9_anglePositive =" + m_A9_anglePositive + ", m_A9_angleNegative = " + m_A9_angleNegative + " (0xA9)");
//                setSymmetryAngle(m_A9_anglePositive, m_A9_angleNegative);
//                break;
//
//            case DS_EMS_IDLE_STANDBY:
//                break;
//
//            case DS_A0_RUNNING_RSP:
//                //  @WORKOUT.ResistanceType int workloadType = m_medicalWorkout.getResistance();
//                m_devConsoleMode = DeviceSpiritC.CONSOLE_MODE.NORMAL;
//                LogUtil.d(TAG, "設定車種及目前模式 (DS_A0_RUNNING_RSP), 重送指令, m_devMachineType = " + m_devMachineType + ", consoleMode = " + m_devConsoleMode);
//                getDeviceSpiritC().setConsoleMode(m_devMachineType, m_devConsoleMode);
//                break;
//
//            case DS_80_PWM_LEVEL_RSP:
//                LogUtil.d(TAG, "設定PWM LEVEL = " + w.pwmLevelDA.get() + ", 重送指令");
//                setDevPwmLevel(); // pwm的值從0~65535
//                break;
//
//            case DS_A5_TARGET_RPM_RSP:
//                LogUtil.d(TAG, "設定target RPM = " + m_targetRpm + ", 重送指令");
//                setDevTargetRpm(m_targetRpm);
//                break;
//
//            case DS_A0_PAUSE_RSP:
////                postMsg("設定PAUSE模式, 重送指令", PT_UART);
////                m_uartConsole.setConsoleMode(m_devMachineType, m_devConsoleMode);  // rpm的值從25~100(待確認)
//                setDevStep(DS_A0_PAUSE_STANDBY);
//                break;
//        }
//    }
//
//
//    //單步揚升測試
//    public void setDevInclineAdTreadmill(int gradeAd) {
//
//        if (gradeAd == 0) {
//            // grade don't care
//            //    m_currentFrontGradeAd = 0;
//        } else {
//            // 移動後揚升, 前揚升DC
//            if (gradeAd < 0) {
//                gradeAd = Math.abs(gradeAd);
//                gradeAd = Math.min(gradeAd, EDIT_GRADE_AD_MAX);
//                gradeAd = Math.max(gradeAd, EDIT_GRADE_AD_MIN);
//
//                gradeAd = 0;
//            }
//
//            setTargetSpeedAndInclineTreadmill(0, gradeAd);
//
//        }
//
//
//        //setDevStep(DS_B5_MAINTENANCE_AD_CHANGE_RSP);
//
//        LogUtil.d(TAG, "[setDevInclineDaTreadmill] 在MAINTENANCE模式" +
//                ", 前揚升AD = " + gradeAd);
//
//        setTargetSpeedAndInclineTreadmill(0, gradeAd);
//    }
//
//
//    @Override
//    public void emsWorkoutStart() {
//        //開啟ECHO
//        LogUtil.d(TAG, "######emsWorkoutStart: ");
////        if (w.selProgram != ProgramsEnum.WATTS && w.selProgram != ProgramsEnum.FITNESS_TEST)
////            getDeviceSpiritC().setEchoMode(DeviceSpiritC.ECHO_MODE.SECOND);
//
//        getDeviceSpiritC().setRPMCounterMode(DeviceSpiritC.RPM_COUNTER_MODE.RESUME);
//        setDevStep(DS_EMS_RUNNING);
//    }
//
//    @Override
//    public void emsWorkoutPause(int resistance) {
//        LogUtil.d(TAG, "####emsWorkoutPause: ");
//        //通知PAUSE
//        getDeviceSpiritC().setRPMCounterMode(DeviceSpiritC.RPM_COUNTER_MODE.PAUSE);
//        //取消ECHO
//        //  getDeviceSpiritC().setEchoMode(DeviceSpiritC.ECHO_MODE.AA);
//        setDevStep(DS_EMS_PAUSE);
//        //LEVEL設為0
//        setDevWorkload(0, resistance);
//    }
//
//    @Override
//    public void emsWorkoutFinish() {
//        LogUtil.d(TAG, "####emsWorkoutFinish: ");
//        getDeviceSpiritC().setRPMCounterMode(DeviceSpiritC.RPM_COUNTER_MODE.CLEAR);
//        //   getDeviceSpiritC().setEchoMode(DeviceSpiritC.ECHO_MODE.AA);
//        setDevStep(DS_EMS_IDLE_STANDBY);
//    }
//
//    @Override
//    public void onConsoleMode(DeviceSpiritC.MACHINE_TYPE machineType, DeviceSpiritC.CONSOLE_MODE consoleMode) {
//    }
//
//    @Override
//    public void setEmsBrakeTest(int pwmLevelDA) {
//        getDeviceSpiritC().setEMS(DeviceSpiritC.MODEL.EMS, pwmLevelDA, DeviceSpiritC.ACTION_MODE.STOP, 0);
//
//    }
//
//    @Override
//    public void onSensibilityTreadmill(int i) {
//        LogUtil.d(TAG, "onSensibilityTreadmill: ");
//    }
//
//
//    private void setLwrMcuOnApRom() {
//        LwrMcuUpdateManager lwrMcuUpdateManager = getDeviceSpiritC().getLwrMcuUpdateManager();
////        由於更新完後, 模式可能在LDROM, 需切換到APROM, 才可以下達getDeviceInfo
//        lwrMcuUpdateManager.connect();  // 回應connect之後, 才可以下runAprom
//        new RxTimer().timer(200, n -> {
//            lwrMcuUpdateManager.runAprom(); // 若是在ap rom, 則會回timeout
//            LogUtil.d(TAG, "setLwrMcuOnApRom:");
//            new RxTimer().timer(200, x -> startUartFlowCommand());
//
////            m.initGem3();
//        });
//    }
//
//
//    public void resetLwrAfterUpdate() {
//        LogUtil.d(TAG, "LWR MCU更新完成, 進行RESET LWR");
//        setDevStep(DS_FA_UPDATE_RESET_RSP);
//        getDeviceSpiritC().setResetTreadmill();
//    }
//
//    @Override
//    public boolean isCriticalError() {
//        return mCriticalError;
//    }
//
////    @GENERAL.UartError int uartErr
////    private Map<Integer, Boolean> errorList = new ;
//
//    private final Map<Integer, Boolean> errorMap = Stream.of(new Object[][]{
//            {UE_NONE, false},
//            {UE_MACHINE_TYPE, false},
//            {UE_INVERTER, false},
//            {UE_UART, false},
//            {UE_RPM, false},
//            {UE_RS485, false},
//            {UE_SAFETY_KEY, false},
//            {UE_MCU, false},
//            {UE_INIT, false},
//            {UE_UART_LWR, false},
//    }).collect(Collectors.toMap(data -> (Integer) data[0], data -> (Boolean) data[1]));
//
//    @Override
//    public void updateErrorMapValue(int uartError) {
//        if (errorMap != null) errorMap.replace(uartError, true);
//    }
//
//    @Override
//    public boolean checkErrorShowed(int uartError) {
//        boolean isErrorShowed = false;
//        for (Integer errorId : errorMap.keySet()) {
//            if (errorId == uartError) {
//                isErrorShowed = Boolean.TRUE.equals(errorMap.get(errorId));
//            }
//        }
//        //   LogUtil.d(TAG, "Key : " + uartError + " Value : "+ isErrorShowed);
//        return isErrorShowed;
//    }
//
//    public void setConsoleTimeoutCnt(int count){
//        mConsoleUartTimeoutCnt = count;
//    }
//    public void setConsoleRS485ErrorCnt(int count){
//        mConsoleRS485ErrorCnt = count;
//    }
//}