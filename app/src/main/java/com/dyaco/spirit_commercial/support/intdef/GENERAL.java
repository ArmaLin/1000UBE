package com.dyaco.spirit_commercial.support.intdef;

import androidx.annotation.IntDef;

public class GENERAL {

    public static final String DEFAULT_SEEK_VALUE_INCLINE = "0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0"; //26列
    //    public static final String DEFAULT_SEEK_VALUE_LEVEL = "4#4#4#4#4#4#4#4#4#4#4#4#4#4#4#4#4#4#4#4";
    public static final String DEFAULT_SEEK_VALUE_LEVEL = "0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0";
    public static final String DEFAULT_SEEK_VALUE_SPEED = "0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0";
    public static final String DEFAULT_SEEK_VALUE_HR = "1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1";

    //TV tuner 類比
    public static final int TV_TUNER_ANALOG_CABLE = 0; //有線
    public static final int TV_TUNER_ANALOG_AIR = 1; //無線



    //TV tuner 訊號
    public static final int TV_TUNER_SIGNAL_DIGITAL = 0; //數位
    public static final int TV_TUNER_SIGNAL_ANALOG = 1; //類比

    public static final int NO_LIMIT = 0;
    public static final int DISTANCE_LIMIT = 1;
    public static final int TIME_LIMIT = 2;

    public static final int SUB_MCU = 0;
    public static final int LWR = 1;


    //WEB API
    public static final String MAC_ADDRESS_PARAM = "macAddress";

    public static final int MEDIA_TYPE_APP = 0;
    public static final int MEDIA_TYPE_WEBVIEW = 1;
    public static final int MEDIA_TYPE_HDMI = 2;


    //實體按鍵
    public static final int CLICK_PLUS = 10000001;
    public static final int CLICK_MINUS = 20000001;
    public static final int CLICK_PLUS_2 = 100000012;//沒beep
    public static final int CLICK_MINUS_2 = 200000012;//沒beep
    public static final int LONG_CLICK_PLUS = 100000002;
    public static final int LONG_CLICK_MINUS = 200000002;


    //STATS
    public static final int STATS_SPEED = 0;
    public static final int STATS_INCLINE = 1;
    public static final int STATS_ELAPSED_TIME = 2; // time
    public static final int STATS_CURRENT_DISTANCE = 3;
    public static final int STATS_PACE = 4;
    public static final int STATS_ELEVATION_GAIN = 5;
    public static final int STATS_REMAINING_TIME = 6; //time left
    public static final int STATS_DISTANCE_LEFT = 7;
    public static final int STATS_AVG_PACE = 8;
    public static final int STATS_CALORIES = 9;
    public static final int STATS_HEART_RATE = 10;
    public static final int STATS_METS = 11;
    public static final int STATS_POWER = 12;
    public static final int STATS_LEVEL = 13;
    public static final int STATS_AVG_POWER = 14;
    public static final int STATS_AVG_SPEED = 15;
    public static final int STATS_AVG_LEVEL = 16;

    public static final int STATS_INTERVAL = 17;

    public static final int STATS_INTERVAL_DISTANCE = 18;

    public static final int STATS_CADENCE = 19;

    public static final int STATS_REMAINING_CALORIES = 20; //calories left

    public static final int STATS_RESISTANCE = 21;
    public static final int STATS_TOTAL_REVOLUTIONS = 22;

    public static final int STATS_TOTAL_STEPS = 23;
    public static final int STATS_REMAINING_STEPS = 24;


    //DIAGRAM
    public static final int BAR_STATUS_SEGMENT_PENDING = 0; //還沒跑到
    public static final int BAR_STATUS_SEGMENT_RUNNING = 1; //正在跑
    public static final int BAR_STATUS_SEGMENT_FINISH = 2; //跑完
    public static final int BAR_STATUS_SEGMENT_BLANK = 3;
    public static final int BAR_STATUS_SEGMENT_NONE = 4;
    public static final int BAR_TYPE_INCLINE = 1;
    public static final int BAR_TYPE_LEVEL_SPEED = 2;
    public static final int BAR_TYPE_BLANK = 3;
    public static final int BAR_TYPE_WATT = 4;
    public static final int UPDATE_ALL_BAR = 0;
    public static final int UPDATE_INCLINE_BAR = 1;
    public static final int UPDATE_SPEED_BAR = 2;

    public static final int HIIT_SPRINT_BAR = 1;
    public static final int HIIT_REST_BAR = 2;
    public static final int NORMAL_BAR = 0;

    //Workout warring window
    public static final int WARRING_NO_HR = 0;
    public static final int WARRING_HR_TOO_HIGH = 1;
    public static final int WARRING_HR_REACHED_TARGET = 2;
    public static final int WARRING_NO_RPM = 3;
    public static final int MEDIA_PHYSICAL_KEYS = 4;
    public static final int SELECT_HR_EXCEEDS_AGE = 5;
    public static final int INVALID_TEST_RPM_OUT_OF_RANGE = 6;
    public static final int INVALID_TEST_HR_OUT_OF_RANGE = 7;


    //WINDOW Animation
    public static final int TRANSLATION_X = 0;
    public static final int TRANSLATION_Y = 1;
    public static final int FADE = 2;
    public static final int SCALE_X = 3;
    public static final int SCALE_Y = 4;
    public static final int NONE = 5;

    //MEDIA
    public static final int MEDIA_PLAYING = 1;
    public static final int MEDIA_NOT_PLAYING = 0;

    //USER
    public static final int USER_TYPE_GUEST = 0;
    public static final int USER_TYPE_NORMAL = 1;
    public static final int USER_TYPE_VIP = 2;
    public static final int USER_TYPE_EGYM = 3;

    //BLE
    public static final int BLE_DEVICE_EVENT_NORMAL = 0;
    public static final int BLE_DEVICE_EVENT_RESCAN = 1;
    public static final int BLE_DEVICE_EVENT_WARRING = 2;
    public static final int BLE_DEVICE_EVENT_CONNECTED = 3;

    //WORKOUT PAGE
    public static final int WORKOUT_PAGE_STATS = 0;
    public static final int WORKOUT_PAGE_CHARTS = 1;
    public static final int WORKOUT_PAGE_TRACK = 2;
    public static final int WORKOUT_PAGE_GARMIN = 3;


    public static final int DO_TIME = 10;
    public static final int DO_SPEED = 20;
    public static final int DO_SPEED_RPM = 21;
    public static final int DO_SPEED_SEC = 22;
    public static final int DO_GRADE = 31;
    public static final int DO_ELEVATION = 32;
    public static final int DO_HR = 50;
    public static final int DO_PACE = 61;
    public static final int DO_POWER = 62;
    public static final int DO_METS = 63;
    public static final int DO_STEPS = 71;
    public static final int DO_STEP_LEN = 72;
    public static final int DO_LEFT_STEP_LEN = 73;
    public static final int DO_RIGHT_STEP_LEN = 74;
    public static final int DO_CADENCE = 75;
    public static final int DO_DISTANCE = 80;
    public static final int DO_CALORIES = 90;
    public static final int DO_WORKLOAD = 91;
    public static final int DO_REVOLUTION = 92;
    public static final int DO_VO2_1D = 100;
    public static final int DO_VO2_1D1 = 101;
    public static final int DO_VO2_1D2 = 102;
    public static final int DO_LEVEL = 103;

    public static final int MALE = 1;
    public static final int FEMALE = 0;


    @IntDef({UPDATE_ALL_BAR, UPDATE_INCLINE_BAR, UPDATE_SPEED_BAR})
    public @interface chartUpdateType {
    }

    @IntDef({BAR_STATUS_SEGMENT_PENDING, BAR_STATUS_SEGMENT_RUNNING, BAR_STATUS_SEGMENT_FINISH, BAR_STATUS_SEGMENT_BLANK, BAR_STATUS_SEGMENT_NONE})
    public @interface barStatus {
    }

    @IntDef({BAR_TYPE_INCLINE, BAR_TYPE_LEVEL_SPEED, BAR_TYPE_BLANK})
    public @interface barType {
    }

    @IntDef({TRANSLATION_Y, TRANSLATION_X, FADE})
    public @interface animationType {
    }

    @IntDef({MEDIA_PLAYING, MEDIA_NOT_PLAYING})
    public @interface mediaStatus {
    }


    @IntDef({DO_TIME, DO_SPEED, DO_SPEED_RPM, DO_SPEED_SEC, DO_GRADE, DO_ELEVATION, DO_HR, DO_PACE, DO_POWER, DO_METS,
            DO_STEPS, DO_STEP_LEN, DO_LEFT_STEP_LEN, DO_RIGHT_STEP_LEN, DO_CADENCE, DO_DISTANCE,
            DO_CALORIES, DO_WORKLOAD, DO_REVOLUTION, DO_VO2_1D, DO_VO2_1D1, DO_VO2_1D2})
    public @interface DataOption {
    }


    public static final int UART_PORT_0 = 0;
    public static final int UART_PORT_1 = 1;
    public static final int UART_PORT_2 = 2;

    // LWR error
    public static final int LE_UNKNOWN = 0;
    public static final int LE_MACHINE_TYPE = 100;   // LWR機型與APP不符
    public static final int LE_INIT_FAIL = 101;   // LWR初始化失敗
    public static final int LE_SAFETY_KEY = 102;   // 未插SAFETY KEY
    public static final int LE_CALIBRATE_FRONT_GRADE = 201;   // 校正結束, 前揚升校正異常
    public static final int LE_CALIBRATE_REAR_GRADE = 202;   // 校正結束, 後揚升校正異常
    public static final int LE_CALIBRATE_BOTH_GRADE = 203;   // 校正結束, 前後揚升校正異常
    public static final int LE_CONVERTER = 204;   // 變頻器錯誤

    // error style
    public static final int ES_NONE = 100;   // 錯誤解除, 關閉popup
    public static final int ES_SHORT_TITLE = 101;   // 抬頭+訊息+2個按鈕
    public static final int ES_MSG = 102;   // 訊息
    public static final int ES_ERROR_CODE = 103;   // 抬頭+訊息+1個按鈕
    public static final int ES_LONG_TITLE = 104;   // 長抬頭+1個按鈕

    // for LWR action steps
    public static String DEV_STEP = "dev_step";

    // for Common
    public static final int DS_CONNECT_RSP = 100;   // 連接UART
    public static final int DS_99_DEV_INFO_RSP = 101;   // 取得裝置資訊
    public static final int DS_99_ERR_MACHINE_TYPE = 102;   // LWR機型與APP不符

    // for Treadmill
    public static final int DS_BE_TO_TC_RSP = 201;   // 設定timeout及testControl的模式
    public static final int DS_LWR_INITIALIZING = 202;   // 在初始化期間
    public static final int DS_B7_APP_START_RSP = 203;   // 設定APP_START模式
    public static final int DS_B7_IDLE_RSP = 204;   // 設定IDLE模式
    public static final int DS_B3_IDLE_UNIT_RSP = 205;   // 設定單位
    public static final int DS_B2_IDLE_AD_READ_RSP = 206;   // 讀取速度及揚升的AD值
    public static final int DS_B5_IDLE_AD_RESET_RSP = 207;   // 設定速度及揚升歸零
    public static final int DS_B6_IDLE_SPEED_RATE_RSP = 208;   // 設定加減速時間
    public static final int DS_IDLE_SILENCE_RSP = 209;   // 等待速度及揚升皆為0
    public static final int DS_IDLE_STANDBY = 210;   // 在IDLE待命中 (Treadmill)


    public static final int DS_B0_ERR_LWR_INIT_FAIL = 211;   // LWR初始化失敗
    public static final int DS_B0_ERR_CONVERTER = 212;   // LWR初始化失敗

    public static final int DS_B7_ERR_SAFETY_KEY_RSP = 213;   // 通知LWR未插安全鎖
    public static final int DS_B7_CLR_SAFETY_KEY_RSP = 214;   // 等待插上安全鎖


    public static final int DS_FA_UPDATE_RESET_RSP = 2490;   // UPDATE LWR MCU後, reset LWR
    public static final int DS_FA_WAIT_COME_BACK = 2491;   // reset LWR, 等待3秒之後, 再執行下一步
    public static final int DS_99_UPDATE_DEV_INFO_RSP = 2492;   // 3秒結束, 取得機型, 僅於回應後, 直接做restart app)
    public static final int DS_BE_UPDATE_TO_TC_RSP = 2493;   // 取得Device info後, 恢復0xB0傳送, 並設定timeout及testControl的模式，收到回覆後, 才下main mode = RESET

    // for Treadmill (don't change order below)

    public static final int DS_B7_RUNNING_RSP = 215;   // 設定進入RUNNING模式
    //    public static final int DS_B2_RUNNING_AD_READ_RSP = 216;   // 讀取(速度及)揚升的AD值 (GS mode 多此step)
//    public static final int DS_B5_RUNNING_AD_RESET_RSP = 217;   // 設定(速度及)揚升歸零 (GS mode 多此step)
//    public static final int DS_RUNNING_SILENCE_RSP = 218;   // 等待(速度及)揚升歸零 (GS mode 多此step)
    public static final int DS_RUNNING_STANDBY = 219;   // 已進入RUNNING模式
    public static final int DS_B5_RUNNING_AD_CHANGE_RSP = 220;   // 設定速度及揚升的AD值

    public static final int DS_B7_PAUSE_RSP = 221;   // 設定進入PAUSE模式
    public static final int DS_B7_PAUSE_GS_RSP = 222;   // 設定進入PAUSE模式 (GS MODE, 揚升會立即停止, 不管有沒有到定位)
    public static final int DS_B5_PAUSE_AD_RESET_RSP = 223;   // 設定速度及揚升歸零
    public static final int DS_PAUSE_SILENCE_RSP = 224;   // 等待速度及揚升皆為0
    public static final int DS_PAUSE_STANDBY = 225;   // 在PAUSE待命中

    public static final int DS_B7_RESUME_RUNNING_RSP = 226;   // 設定進入RUNNING模式
    public static final int DS_B5_RESUME_AD_CHANGE_RSP = 227;   // 已進入RUNNING模式,恢復AD設定
    public static final int DS_B7_RESET_RSP = 228;   // 設定進入RESET模式
    public static final int DS_RESET_STANDBY = 229;   // 已進入RESET模式, APP可以做RESET
    public static final int DS_B5_BREAK_SPD_RESET_RSP = 243;   // GS mode, 中斷結束RUNNING, 僅設定速度歸零


    public static final int DS_FA_RESET_RSP = 244;   // 已進入RESET模式, 設定下控做RESET

    // for Treadmill: stop running
    public static final int DS_B5_FINISH_AD_RESET_RSP = 230;   // 設定速度及揚升歸零
    public static final int DS_FINISH_SILENCE_RSP = 231;   // 等待速度及揚升皆為0
    public static final int DS_B7_FINISH_PAUSE_RSP = 232;   // 結束訓練, 設定進入PAUSE模式
    public static final int DS_B7_FINISH_IDLE_RSP = 233;   // 設定進入IDLE模式
    public static final int DS_B7_MAINTENANCE_RSP = 234;   // 設定進入MAINTENANCE模式
    public static final int DS_MAINTENANCE_STANDBY = 235;   // 在MAINTENANCE模式待命中
    public static final int DS_BA_BREAK_MODE_RSP = 236;   // 設定BRAKE模式
    public static final int DS_B5_MAINTENANCE_AD_CHANGE_RSP = 237;   // MAINTENANCE模式單步測試揚升

    // for Treadmill: Calibration
    public static final int DS_B7_CALIBRATION_RSP = 250;  // 設定進入揚升校正模式
    public static final int DS_CALIBRATION_STANDBY = 251;  // 在揚升校正模式待命中
    public static final int DS_BB_CALIBRATION_START_RSP = 252;  // 開始揚升校正
    public static final int DS_BB_CALIBRATION_FRONT_RSP = 253;  // 開始單獨前揚升校正
    public static final int DS_BB_CALIBRATION_REAR_RSP = 254;  // 開始單獨後揚升校正
    public static final int DS_BB_CALIBRATION_STOP_RSP = 255;  // 停止揚升校正
    public static final int DS_B0_CALIBRATION_ONGOING = 256;  // 揚升校正進行中
    public static final int DS_B0_CALIBRATION_FRONT_MAX = 257;  // 揚升校正進行中(前揚升MAX)
    public static final int DS_B0_CALIBRATION_FRONT_MIN = 258;  // 揚升校正進行中(前揚升MIN)
    public static final int DS_B0_CALIBRATION_REAR_MAX = 259;  // 揚升校正進行中(後揚升MAX)
    public static final int DS_B0_CALIBRATION_REAR_MIN = 260;  // 揚升校正進行中(後揚升MIN)
    public static final int DS_BD_CALIBRATION_RESULT_RSP = 261;  // 讀取前後揚升最高最低點

    // for EMS
    public static final int DS_90_NORMAL_MODE_RSP = 400; //DeviceInfo
    public static final int DS_EMS_RUNNING = 410;
    public static final int DS_EMS_PAUSE = 411;
    public static final int DS_EMS_IDLE_STANDBY = 303;   // 設定在待命中

    // for Bike - Workload (Level, Power )
    // for Bike - Workload (Speed, running ISO)
    public static final int DS_A0_IDLE_RSP = 300;   // 設定車種及IDLE模式
    public static final int DS_A9_SYMM_ANGLE_RSP = 301;   // 設定SYMMETRY ANGLE @@@@沒有
    public static final int DS_80_IDLE_ECHO_RSP = 302;   // 設定PWM LEVEL (在此階段作提供給LWR做heartbeat依據)

    public static final int DS_A0_RUNNING_RSP = 304;   // 設定RUNNING模式
    public static final int DS_A0_RUNNING_STANDBY = 305;   // 已進入RUNNING模式
    public static final int DS_A5_TARGET_RPM_RSP = 306;   // 設定TARGET RPM (workload為speed) @@@沒有
    public static final int DS_80_PWM_LEVEL_RSP = 307;   // 設定PWM LEVEL (workload為power及level, 及workload為speed時做為heartbeat用)
    public static final int DS_A0_PAUSE_RSP = 308;   // 在PAUSE模式
    public static final int DS_A0_PAUSE_STANDBY = 309;   // 已進入PAUSE模式

    // for EMS: stop running
    public static final int DS_A0_FINISH_IDLE_RSP = 320;   // 設定進入IDLE模式

    public static final int DS_CORESTAR = 800;

    public static final int DS_FA_CONSOLE_RESET_RSP = 900;   // 發生CONSOLE ERROR模式, 下達reset指令
    public static final int DS_B7_CONSOLE_ERR_RSP = 901;   // 設定進入CONSOLE ERROR模式
    public static final int DS_CONSOLE_ERR_STANDBY = 902;   // 已進入CONSOLE ERROR模式

    public static final int DS_BF_CLEAR_ERRS_RSP = 950;  // 同時清除error1及error2
    public static final int DS_BF_CLEAR_ERR1_RSP = 951;  // 僅清除error1
    public static final int DS_BF_CLEAR_ERR2_RSP = 952;  // 僅清除error2
    public static final int DS_B8_STOP_SC_RSP = 953;  // 停止速度及揚升
    public static final int DS_B8_STOP_SC_STANDBY = 954;  // 已設定停止速度及揚升

    // UART error
    public static final int UE_NONE = 0;
    public static final int UE_MACHINE_TYPE = 1;
    public static final int UE_INVERTER = 2;//變頻器
    public static final int UE_UART = 3;
    public static final int UE_RPM = 4;
    public static final int UE_RS485 = 5;
    public static final int UE_SAFETY_KEY = 6;
    public static final int UE_MCU = 7;
    public static final int UE_INIT = 8;
    public static final int UE_UART_LWR = 9; // 下控逾時未回應，app自己發的
    public static final int UE_MCU_EMS = 10; // EMS_MCU_ERROR


    @IntDef({
            DS_CONNECT_RSP, DS_99_DEV_INFO_RSP, DS_BE_TO_TC_RSP, DS_LWR_INITIALIZING, DS_B7_APP_START_RSP,
            DS_B7_IDLE_RSP, DS_B3_IDLE_UNIT_RSP, DS_B2_IDLE_AD_READ_RSP, DS_B5_IDLE_AD_RESET_RSP, DS_B6_IDLE_SPEED_RATE_RSP,
            DS_IDLE_SILENCE_RSP, DS_IDLE_STANDBY,

            DS_A0_IDLE_RSP, DS_A9_SYMM_ANGLE_RSP, DS_80_IDLE_ECHO_RSP, DS_EMS_IDLE_STANDBY,

            DS_99_ERR_MACHINE_TYPE, DS_B0_ERR_LWR_INIT_FAIL, DS_B0_ERR_CONVERTER,
            DS_B7_ERR_SAFETY_KEY_RSP, DS_B7_CLR_SAFETY_KEY_RSP,
            DS_FA_CONSOLE_RESET_RSP, DS_B7_CONSOLE_ERR_RSP, DS_CONSOLE_ERR_STANDBY,

            DS_B7_RUNNING_RSP, DS_RUNNING_STANDBY, DS_B5_RUNNING_AD_CHANGE_RSP,

            DS_B7_PAUSE_RSP, DS_B7_PAUSE_GS_RSP, DS_B5_PAUSE_AD_RESET_RSP, DS_PAUSE_SILENCE_RSP, DS_PAUSE_STANDBY,
            DS_B7_RESUME_RUNNING_RSP, DS_B5_RESUME_AD_CHANGE_RSP,

            DS_A0_PAUSE_RSP, DS_A0_PAUSE_STANDBY,

            DS_A0_RUNNING_RSP, DS_A0_RUNNING_STANDBY, DS_A5_TARGET_RPM_RSP, DS_80_PWM_LEVEL_RSP,

            DS_B5_FINISH_AD_RESET_RSP, DS_FINISH_SILENCE_RSP, DS_B7_FINISH_PAUSE_RSP, DS_B7_FINISH_IDLE_RSP,
            DS_A0_FINISH_IDLE_RSP, DS_B5_BREAK_SPD_RESET_RSP,

            DS_B7_MAINTENANCE_RSP, DS_MAINTENANCE_STANDBY, DS_BA_BREAK_MODE_RSP, DS_B5_MAINTENANCE_AD_CHANGE_RSP,
//            DS_B5_RUNNING_AD_RESET_RSP, DS_RUNNING_SILENCE_RSP, DS_B2_RUNNING_AD_READ_RSP,
            DS_B7_CALIBRATION_RSP, DS_CALIBRATION_STANDBY,
            DS_BB_CALIBRATION_START_RSP, DS_BB_CALIBRATION_FRONT_RSP, DS_BB_CALIBRATION_REAR_RSP,
            DS_BB_CALIBRATION_STOP_RSP,
            DS_B0_CALIBRATION_ONGOING, DS_B0_CALIBRATION_FRONT_MAX, DS_B0_CALIBRATION_FRONT_MIN,
            DS_B0_CALIBRATION_REAR_MAX, DS_B0_CALIBRATION_REAR_MIN, DS_BD_CALIBRATION_RESULT_RSP,
            DS_90_NORMAL_MODE_RSP, DS_EMS_RUNNING, DS_EMS_PAUSE,
            DS_CORESTAR,DS_BE_UPDATE_TO_TC_RSP,
            DS_FA_UPDATE_RESET_RSP, DS_FA_WAIT_COME_BACK, DS_99_UPDATE_DEV_INFO_RSP,
            DS_BF_CLEAR_ERRS_RSP, DS_BF_CLEAR_ERR1_RSP, DS_BF_CLEAR_ERR2_RSP, DS_B8_STOP_SC_RSP, DS_B8_STOP_SC_STANDBY, DS_B7_RESET_RSP, DS_RESET_STANDBY, DS_FA_RESET_RSP

    })
    public @interface DeviceStep {
    }

    @IntDef({LE_UNKNOWN, LE_MACHINE_TYPE, LE_INIT_FAIL, LE_SAFETY_KEY,
            LE_CALIBRATE_FRONT_GRADE, LE_CALIBRATE_REAR_GRADE, LE_CALIBRATE_BOTH_GRADE,
            LE_CONVERTER,
    })
    public @interface LwrError {
    }

    @IntDef({ES_NONE, ES_SHORT_TITLE, ES_MSG, ES_ERROR_CODE, ES_LONG_TITLE
    })
    public @interface ErrorStyle {
    }

    @IntDef({UART_PORT_0, UART_PORT_1, UART_PORT_2})
    public @interface UartPort {
    }

    @IntDef({UE_NONE, UE_MACHINE_TYPE, UE_INVERTER, UE_UART, UE_RPM, UE_RS485, UE_SAFETY_KEY, UE_MCU, UE_INIT, UE_UART_LWR, UE_MCU_EMS})
    public @interface UartError {
    }


    public static final int APP_WEB = 0;
    public static final int APP_APK = 1;
    public static final int APP_BROWSER = 2;

    public static final int OPEN_TYPE_USE_CLS_NAME = 0;
    public static final int OPEN_TYPE_USE_PKG_NAME = 1;

}
