package com.dyaco.spirit_commercial;

import androidx.annotation.IntDef;

public class UartConst {

    // keypad
    public static final int    KEY_UNKNOWN   = 0;
    public static final int    KEY_STOP      = 1;
    public static final int    KEY_START     = 2;
    public static final int    KEY_CONFIRM   = 3;
    public static final int    KEY_MODE      = 4;
    public static final int    KEY_GRADE3    = 5;
    public static final int    KEY_GRADE6    = 6;
    public static final int    KEY_GRADE9    = 7;
    public static final int    KEY_SPEED3    = 8;
    public static final int    KEY_SPEED6    = 9;
    public static final int    KEY_SPEED9    = 10;
    public static final int    KEY_GRADE_INC = 11;
    public static final int    KEY_GRADE_DEC = 12;
    public static final int    KEY_SPEED_INC = 13;
    public static final int    KEY_SPEED_DEC = 14;
    public static final int    KEY_LEVEL_INC = 15;
    public static final int    KEY_LEVEL_DEC = 16;
    public static final int    KEY_INC_MORE  = 17;
    public static final int    KEY_DEC_MORE  = 18;
    public static final int    KEY_UP_MORE   = 19;
    public static final int    KEY_DOWN_MORE = 20;
    public static final int    KEY_SLED      = 21;
    public static final int    KEY_TREADMILL = 22;
    public static final int    KEY_FAN       = 23;
    public static final int    KEY_MAX_NUM   = 23;

    @IntDef({ KEY_UNKNOWN, KEY_STOP, KEY_START, KEY_CONFIRM, KEY_MODE,
            KEY_GRADE3, KEY_GRADE6, KEY_GRADE9,
            KEY_SPEED3, KEY_SPEED6, KEY_SPEED9,
            KEY_GRADE_INC, KEY_GRADE_DEC, KEY_SPEED_INC, KEY_SPEED_DEC,
            KEY_LEVEL_INC,  KEY_LEVEL_DEC,
            KEY_INC_MORE, KEY_DEC_MORE, KEY_UP_MORE, KEY_DOWN_MORE,
            KEY_SLED, KEY_TREADMILL, KEY_FAN
    })
    public @interface Keypad {
    }

    public static final int WORKLOAD_MIN             = 1;         // EMS, workload最小值


    // for Common
    public static final int DS_DEMO_CONNECT_RSP             = 700;   // 連接UART
    public static final int DS_DEMO_99_DEV_INFO_RSP         = 701;   // 取得裝置資訊
    public static final int DS_DEMO_DONE                    = 702;   // 已取得裝置資訊

    public static final int DS_CONNECT_RSP                  = 100;   // 連接UART
    public static final int DS_99_DEV_INFO_RSP              = 101;   // 取得裝置資訊

    // for Treadmill (TCR, ST22S)
    public static final int DS_BE_TO_TC_RSP                 = 200;   // 設定timeout及testControl的模式
    public static final int DS_LWR_INITIALIZING             = 201;   // 在初始化期間
    public static final int DS_B7_APP_START_RSP             = 202;   // 設定APP_START模式
    public static final int DS_B7_IDLE_RSP                  = 203;   // 設定IDLE模式
    public static final int DS_B3_IDLE_UNIT_RSP             = 204;   // 設定單位

    public static final int DS_B2_IDLE_AD_READ_RSP          = 205;   // 讀取速度及揚升的AD值
    public static final int DS_B5_IDLE_AD_RESET_RSP         = 206;   // 設定速度及揚升歸零 (若是GS mode, 則不做揚升歸零)
    public static final int DS_IDLE_SILENCE_RSP             = 207;   // 等待RPM為0及揚升在停止狀態 (若是GS mode, 則忽略揚升未歸零)
    public static final int DS_B6_IDLE_SPEED_RATE_RSP       = 208;   // 設定加減速時間 (RPM需為0) 東庚目前不需要此step
    public static final int DS_MT_IDLE_STANDBY              = 209;   // 在IDLE待命中 (Treadmill)

    //>>>
    public static final int DS_B2_IDLE_AD_READ_CONFIRM_RSP  = 801;   // 當RPM為0, 及揚升在停止狀態, 再次讀取目前的數值是否正確

    //>>>

    public static final int DS_B7_ERR_SAFETY_KEY_RSP        = 215;   // 通知LWR未插安全鎖
    public static final int DS_B7_CLR_SAFETY_KEY_RSP        = 216;   // 等待插上安全鎖

    // for Treadmill
    // >>>注意! 需為連續數值 start (因有區間值判斷)
    public static final int DS_B7_RUNNING_RSP               = 220;   // 設定進入RUNNING模式
    public static final int DS_MT_RUNNING_STANDBY           = 221;   // 已進入RUNNING模式
    public static final int DS_B5_RUNNING_AD_CHANGE_RSP     = 222;   // 設定速度及揚升的AD值

    public static final int DS_B7_PAUSE_RSP                 = 223;   // 設定進入PAUSE模式
    public static final int DS_B7_PAUSE_GS_RSP              = 224;   // 設定進入PAUSE模式 (GS MODE, 揚升會立即停止, 不管有沒有到定位)
    public static final int DS_B5_PAUSE_AD_RESET_RSP        = 225;   // 設定速度及揚升歸零
    public static final int DS_PAUSE_SILENCE_RSP            = 226;   // 等待RPM為0及揚升在停止狀態 (GS mode不必判斷揚升是否歸零, 只要停止視同已silence)
    public static final int DS_MT_PAUSE_STANDBY             = 227;   // 在PAUSE待命中

    public static final int DS_B7_RESUME_RUNNING_RSP        = 228;   // 設定進入RUNNING模式
    public static final int DS_B5_RESUME_AD_CHANGE_RSP      = 229;   // 已進入RUNNING模式,恢復AD設定
    // <<<注意! 需為連續數值 end

    public static final int DS_B7_RESET_RSP                 = 230;   // 設定進入RESET模式
    public static final int DS_RESET_STANDBY                = 231;   // 下控已接收RESET, APP可以做RESTART

    // for Treadmill: stop running
    public static final int DS_B5_BREAK_SPD_RESET_RSP       = 240;   // GS mode, 中斷結束RUNNING, 僅設定速度歸零
    public static final int DS_B5_FINISH_AD_RESET_RSP       = 241;   // 設定速度及揚升歸零 (GS mode有完成program; 或非GS mode, 完成或未完成program)
    public static final int DS_FINISH_SILENCE_RSP           = 242;   // 等待RPM為0及揚升在停止狀態
    public static final int DS_B7_FINISH_PAUSE_RSP          = 243;   // 結束訓練, 設定進入PAUSE模式
    public static final int DS_B7_FINISH_IDLE_RSP           = 244;   // 設定進入IDLE模式

    public static final int DS_B7_MAINTENANCE_RSP           = 245;   // 設定進入MAINTENANCE模式
    public static final int DS_MAINTENANCE_STANDBY          = 246;   // 在MAINTENANCE模式待命中
    public static final int DS_BA_BRAKE_MODE_RSP            = 247;   // 設定BRAKE模式
    public static final int DS_B5_MAINTENANCE_AD_CHANGE_RSP = 248;   // MAINTENANCE模式單步測試揚升

    // after update, reset
    public static final int DS_FA_UPDATE_RESET_RSP          = 249;   // UPDATE LWR MCU後, reset LWR
    public static final int DS_FA_WAIT_COME_BACK            = 250;   // reset LWR, 等待3秒之後, 再執行下一步
    public static final int DS_99_UPDATE_DEV_INFO_RSP       = 251;   // 3秒結束, 取得機型, 僅於回應後, 直接做restart app)
    public static final int DS_BE_UPDATE_TO_TC_RSP          = 252;   // 取得Device info後, 恢復0xB0傳送, 並設定timeout及testControl的模式
    // 收到回覆後, 才下main mode = RESET

    // 發生MCU, RPM, RS485的錯誤時, 當user按下restart, 需進行reset LWR, 接著console app才restart app
    public static final int DS_FA_RESET_LWR_RSP             = 253;

    // 防止未進入IDLE, 就進入HOME SCREEN
    public static final int DS_B7_FORCE_ENTER_IDLE_RSP      = 276;   // 進入首頁時, 強制需在idle mode

//    public static final int DS_B7_FORCE_ENTER_IDLE_RSP      = 280;   // 進入首頁時, 強制需在idle mode

    // for Treadmill: Calibration
    public static final int DS_B7_CALIBRATION_RSP           = 300;  // 設定進入揚升校正模式
    public static final int DS_CALIBRATION_STANDBY          = 301;  // 在揚升校正模式待命中

    public static final int DS_B0_CALI_A_ONGOING            = 302;  // 前後揚升校正進行中(全)
    public static final int DS_B0_CALI_AF_MAX               = 303;  // 校正前揚升MAX進行中(全)
    public static final int DS_B0_CALI_AF_MIN               = 304;  // 校正前揚升MIN進行中(全)
    public static final int DS_B0_CALI_AR_MAX               = 305;  // 校正後揚升MAX進行中(全)
    public static final int DS_B0_CALI_AR_MIN               = 306;  // 校正後揚升MIN進行中(全)

    public static final int DS_B0_CALI_SF_ONGOING           = 307;  // 單獨前揚升校正進行中(單)
    public static final int DS_B0_CALI_SF_MAX               = 308;  // 校正前揚升MAX進行中(單)
    public static final int DS_B0_CALI_SF_MIN               = 309;  // 校正前揚升MIN進行中(單)

    public static final int DS_B0_CALI_SR_ONGOING           = 310;  // 單獨後揚升校正進行中(單)
    public static final int DS_B0_CALI_SR_MAX               = 311;  // 校正後揚升MAX進行中(單)
    public static final int DS_B0_CALI_SR_MIN               = 312;  // 校正後揚升MIN進行中(單)

    public static final int DS_BB_CALI_A_START_RSP          = 313;  // 開始揚升校正(全)
    public static final int DS_BB_CALI_SF_START_RSP         = 314;  // 開始單獨前揚升校正(單)
    public static final int DS_BB_CALI_SR_START_RSP         = 315;  // 開始單獨後揚升校正(單)
    public static final int DS_BB_CALI_A_STOP_RSP           = 316;  // 停止揚升校正(全)
    public static final int DS_BB_CALI_SF_STOP_RSP          = 317;  // 停止前揚升校正(單)
    public static final int DS_BB_CALI_SR_STOP_RSP          = 318;  // 停止後揚升校正(單)

    public static final int DS_BD_CALI_A_RSLT_RSP           = 319;  // 讀取前後揚升最高最低點, 更前後揚升table
    public static final int DS_BD_CALI_F_RSLT_RSP           = 320;  // 讀取前後揚升最高最低點, 僅更新前揚升table
    public static final int DS_BD_CALI_R_RSLT_RSP           = 321;  // 讀取前後揚升最高最低點, 僅更新後揚升table

    public static final int DS_BB_CALI_M_STARTUP_RSP        = 322;  // 啟動變頻器校正
    public static final int DS_BB_CALI_M_START_RSP          = 323;  // 開始變頻器校正
    public static final int DS_B0_CALI_M_ONGOING            = 324;  // 變頻器校正中
    public static final int DS_B0_CALI_M_RSLT               = 325;  // 變頻器校正結果


    // for EMS - Workload (Level, Power, speed/ISO)
    public static final int DS_A0_IDLE_RSP                  = 400;   // 設定車種及IDLE模式
    public static final int DS_A9_SYMM_ANGLE_RSP            = 401;   // 設定SYMMETRY ANGLE
    public static final int DS_80_IDLE_ECHO_RSP             = 402;   // 設定PWM LEVEL (在此階段作提供給LWR做heartbeat依據)
    public static final int DS_EMS_IDLE_STANDBY             = 403;   // 設定在待命中

    public static final int DS_A0_RUNNING_RSP               = 404;   // 設定RUNNING模式
    public static final int DS_EMS_RUNNING_STANDBY          = 405;   // 已進入RUNNING模式
    public static final int DS_A5_RUNNING_RPM_RSP           = 406;   // 設定TARGET RPM (workload為speed)
    public static final int DS_80_RUNNING_PWM_RSP           = 407;   // 設定PWM LEVEL (workload為power及level, 及workload為speed時做為heartbeat用)
    public static final int DS_A0_PAUSE_RSP                 = 408;   // 在PAUSE模式
    public static final int DS_A0_PAUSE_STANDBY             = 409;   // 已進入PAUSE模式
    public static final int DS_A8_BRAKE_MODE_RSP            = 410;   // 設定BRAKE MODE (回到idle後, 需不需要啟動煞車功能)
    // 僅level control要做設定, power及ISO則不用, 因為會干擾speed及power control

    // for EMS: angle sensor calibration
    public static final int DS_A0_CALIBRATION_RSP           = 420;   // 設定進入angle sensor校正模式
    public static final int DS_ANGLE_CALI_STANDBY           = 421;   // 已進入angle sensor校正模式
    public static final int DS_A7_CALI_START_RSP            = 422;   // 開始angle sensor校正
    public static final int DS_A7_CALI_RESULT_RSP           = 423;   // 讀取校正結果

    // for EMS: maintenance mode, sensor test
    public static final int DS_A0_SENSOR_TEST_RSP           = 424;   // 設定進入sensor test
    public static final int DS_A0_SENSOR_TEST_STANDBY       = 425;   // 已進入sensor test

    public static final int DS_CORESTAR                     = 800;

    public static final int DS_FA_CONSOLE_RESET_RSP         = 900;   // 發生CONSOLE ERROR模式, 下達reset指令
    public static final int DS_B7_CONSOLE_ERR_RSP           = 901;   // 設定進入CONSOLE ERROR模式
    public static final int DS_CONSOLE_ERR_STANDBY          = 902;   // 已進入CONSOLE ERROR模式

    public static final int DS_BF_CLEAR_ERRS_RSP            = 950;   // 同時清除error1及error2
    public static final int DS_BF_CLEAR_ERR1_RSP            = 951;   // 僅清除error1
    public static final int DS_BF_CLEAR_ERR2_RSP            = 952;   // 僅清除error2
    public static final int DS_B8_STOP_SC_RSP               = 953;   // 停止速度及揚升
    public static final int DS_B8_STOP_SC_STANDBY           = 954;   // 已設定停止速度及揚升

    // for ECB (Rehab UBE)
    public static final int DS_90_NORMAL_MODE_RSP           = 500;   // 設定LWR mode為normal
    public static final int DS_82_CLEAR_RPM_COUNTER_RSP     = 501;   // 先clear rpm counter
    public static final int DS_ECB_IDLE_STANDBY             = 502;   // 已進入normal, ready
    // >>>注意! 需為連續數值 start (因有區間值判斷)
    public static final int DS_82_RESUME_RPM_COUNTER_RSP    = 503;   // 進入running之前, 恢復revolution計數
    public static final int DS_ECB_RUNNING                  = 504;   // 進入running
    public static final int DS_82_PAUSE_RPM_COUNTER_RSP     = 505;   // 進入pause前, 先停止revolution計數
    public static final int DS_ECB_PAUSE_STANDBY            = 506;   // 已進入pause, Rehab UBE (PAUSE)
    public static final int DS_80_RUNNING_RES_RSP           = 507;   // running期間, 設定resistance, 等待回應
    // <<<注意! 需為連續數值 end7
    public static final int DS_80_MOTOR_TEST_STANDBY        = 508;   // Rehab UBE (工程模式-Motor Test測試)
    public static final int DS_80_MOTOR_TEST_RES_RSP        = 509;   // Rehab UBE (工程模式-Motor Test測試)
    public static final int DS_ECB_ERR_OCCURRED             = 510;   // 拉線器發生錯誤
    public static final int DS_EMS_ERR_OCCURRED             = 511;   // EMS發生RS485相關錯誤 (DK)

    public static final int DS_TM_PROTOCOL_RSP              = 600;   // TrackMaster設定protocol opt
    public static final int DS_TM_STANDBY                   = 601;   // TrackMaster已完成protocol opt
    @IntDef({
            DS_DEMO_CONNECT_RSP, DS_DEMO_99_DEV_INFO_RSP, DS_DEMO_DONE,
            DS_CONNECT_RSP, DS_99_DEV_INFO_RSP,

            DS_BE_TO_TC_RSP, DS_LWR_INITIALIZING, DS_B7_APP_START_RSP,
            DS_B7_IDLE_RSP,  DS_B3_IDLE_UNIT_RSP,  DS_B2_IDLE_AD_READ_RSP, DS_B5_IDLE_AD_RESET_RSP, DS_IDLE_SILENCE_RSP,
            DS_B6_IDLE_SPEED_RATE_RSP, DS_B2_IDLE_AD_READ_CONFIRM_RSP, DS_MT_IDLE_STANDBY,

            DS_B7_ERR_SAFETY_KEY_RSP, DS_B7_CLR_SAFETY_KEY_RSP,

            DS_B7_RUNNING_RSP, DS_MT_RUNNING_STANDBY, DS_B5_RUNNING_AD_CHANGE_RSP,

            DS_B7_PAUSE_RSP, DS_B7_PAUSE_GS_RSP, DS_B5_PAUSE_AD_RESET_RSP, DS_PAUSE_SILENCE_RSP, DS_MT_PAUSE_STANDBY,
            DS_B7_RESUME_RUNNING_RSP, DS_B5_RESUME_AD_CHANGE_RSP,
            DS_B7_RESET_RSP, DS_RESET_STANDBY,
            DS_B5_BREAK_SPD_RESET_RSP, DS_B5_FINISH_AD_RESET_RSP, DS_FINISH_SILENCE_RSP, DS_B7_FINISH_PAUSE_RSP, DS_B7_FINISH_IDLE_RSP,

            DS_B7_MAINTENANCE_RSP, DS_MAINTENANCE_STANDBY, DS_BA_BRAKE_MODE_RSP, DS_B5_MAINTENANCE_AD_CHANGE_RSP,
            DS_FA_UPDATE_RESET_RSP, DS_FA_WAIT_COME_BACK, DS_99_UPDATE_DEV_INFO_RSP, DS_BE_UPDATE_TO_TC_RSP,

            DS_B7_CALIBRATION_RSP, DS_CALIBRATION_STANDBY,

            DS_B0_CALI_A_ONGOING, DS_B0_CALI_AF_MAX, DS_B0_CALI_AF_MIN,
            DS_B0_CALI_AR_MAX, DS_B0_CALI_AR_MIN,

            DS_B0_CALI_SF_ONGOING, DS_B0_CALI_SF_MAX, DS_B0_CALI_SF_MIN,
            DS_B0_CALI_SR_ONGOING, DS_B0_CALI_SR_MAX, DS_B0_CALI_SR_MIN,

            DS_BB_CALI_A_START_RSP, DS_BB_CALI_SF_START_RSP, DS_BB_CALI_SR_START_RSP,
            DS_BB_CALI_A_STOP_RSP, DS_BB_CALI_SF_STOP_RSP, DS_BB_CALI_SR_STOP_RSP,

            DS_BD_CALI_A_RSLT_RSP, DS_BD_CALI_F_RSLT_RSP,  DS_BD_CALI_R_RSLT_RSP,
            DS_BB_CALI_M_STARTUP_RSP, DS_BB_CALI_M_START_RSP, DS_B0_CALI_M_ONGOING, DS_B0_CALI_M_RSLT,

            DS_A0_IDLE_RSP, DS_A9_SYMM_ANGLE_RSP, DS_80_IDLE_ECHO_RSP, DS_EMS_IDLE_STANDBY,
            DS_A0_RUNNING_RSP, DS_EMS_RUNNING_STANDBY, DS_A5_RUNNING_RPM_RSP, DS_80_RUNNING_PWM_RSP,
            DS_A0_PAUSE_RSP, DS_A0_PAUSE_STANDBY, DS_A8_BRAKE_MODE_RSP,
            DS_A0_CALIBRATION_RSP, DS_ANGLE_CALI_STANDBY, DS_A7_CALI_START_RSP, DS_A7_CALI_RESULT_RSP,
            DS_A0_SENSOR_TEST_RSP, DS_A0_SENSOR_TEST_STANDBY,

            DS_CORESTAR,

            DS_FA_CONSOLE_RESET_RSP, DS_B7_CONSOLE_ERR_RSP, DS_CONSOLE_ERR_STANDBY,
            DS_BF_CLEAR_ERRS_RSP, DS_BF_CLEAR_ERR1_RSP,  DS_BF_CLEAR_ERR2_RSP, DS_B8_STOP_SC_RSP, DS_B8_STOP_SC_STANDBY,

            DS_90_NORMAL_MODE_RSP, DS_ECB_IDLE_STANDBY, DS_82_CLEAR_RPM_COUNTER_RSP,
            DS_ECB_RUNNING, DS_82_PAUSE_RPM_COUNTER_RSP, DS_ECB_PAUSE_STANDBY, DS_82_RESUME_RPM_COUNTER_RSP,
            DS_80_RUNNING_RES_RSP, DS_80_MOTOR_TEST_STANDBY, DS_80_MOTOR_TEST_RES_RSP, DS_ECB_ERR_OCCURRED, DS_EMS_ERR_OCCURRED,

            DS_TM_PROTOCOL_RSP, DS_TM_STANDBY,
    })
    public @interface DeviceStep {
    }


    // UART error
    public static final int UE_NONE                 = 0;
    public static final int UE_UNKNOWN_MACHINE_TYPE = 1;
    public static final int UE_MACHINE_TYPE         = 2;
    public static final int UE_INVERTER             = 3;
    public static final int UE_MOH                  = 4;
    public static final int UE_UART                 = 5;
    public static final int UE_RPM                  = 6;
    public static final int UE_RS485                = 7;
    public static final int UE_SAFETY_KEY           = 8;
    public static final int UE_MCU                  = 9;
    public static final int UE_INIT                 = 10;
    public static final int UE_UART_LWR             = 11; // 下控逾時未回應
    public static final int UE_ECB                  = 12; // ECB發生錯誤
    public static final int UE_INCLINE              = 13;
    public static final int UE_DECLINE              = 14;
    public static final int UE_REED_SWITCH          = 15;
    public static final int UE_OVER_CURRENT         = 16; // 過電流
    public static final int UE_OVER_VOLT            = 17; // 過電壓
    public static final int UE_ANGLE_SENSOR         = 18; // ANGEL SENSOR讀取有異
    public static final int UE_EMS                  = 19; // EMS控制器異常
    public static final int UE_SAFETY_KEY_SPD_ZERO  = 20; // 插回安全鎖, 等待速度為0 (此時雖已解除錯誤, 但訊息還需持續顯示)
    // INCLINE及DECLINE的錯誤都不必彈視窗
    @IntDef({ UE_NONE, UE_UNKNOWN_MACHINE_TYPE, UE_MACHINE_TYPE, UE_MOH, UE_INVERTER, UE_UART,
            UE_RPM, UE_RS485, UE_SAFETY_KEY, UE_MCU, UE_INIT, UE_UART_LWR,
            UE_ECB, UE_INCLINE, UE_DECLINE, UE_REED_SWITCH, UE_OVER_CURRENT, UE_OVER_VOLT, UE_ANGLE_SENSOR,
            UE_EMS, UE_SAFETY_KEY_SPD_ZERO
    })
    public @interface UartError {
    }

    // log code (console端發起的error)
    public static final int LC_UART          = 1;  // 未收到LWR回應超過5次
    public static final int LC_MT_UNKNOWN    = 2;  // unknown machine type
    public static final int LC_MT_MISMATCH   = 3;  // machine type mismatch
    public static final int LC_INIT          = 4;  // init error
    public static final int LC_CMD_SG        = 5;  // set speed and grade
    public static final int LC_CMD_EUP       = 6;  // set eup mode
    public static final int LC_SUB_SAFE_KEY  = 7;  // sub板發的安全鎖錯誤
    public static final int LC_REBOOT_SCB    = 8;  // 通知SCB重新啟動
    public static final int LC_SCB_REBOOTED  = 9;  // SCB已重新啟動完畢
    public static final int LC_ECB           = 10; // 拉線器錯誤
    @IntDef({ LC_UART, LC_MT_UNKNOWN, LC_MT_MISMATCH, LC_INIT, LC_CMD_SG, LC_CMD_EUP,
            LC_SUB_SAFE_KEY, LC_REBOOT_SCB, LC_SCB_REBOOTED, LC_ECB})
    public @interface LogCode {
    }

    // LWR error
    public static final int LE_UNKNOWN                      = 0;
    public static final int LE_MACHINE_TYPE                 = 100;   // LWR機型與APP不符
    public static final int LE_INIT_FAIL                    = 101;   // LWR初始化失敗
    public static final int LE_SAFETY_KEY                   = 102;   // 未插SAFETY KEY
    public static final int LE_CALIBRATE_FRONT_GRADE        = 201;   // 校正結束, 前揚升校正異常
    public static final int LE_CALIBRATE_REAR_GRADE         = 202;   // 校正結束, 後揚升校正異常
    public static final int LE_CALIBRATE_BOTH_GRADE         = 203;   // 校正結束, 前後揚升校正異常
    public static final int LE_CONVERTER                    = 204;   // 變頻器錯誤
    @IntDef({LE_UNKNOWN, LE_MACHINE_TYPE, LE_INIT_FAIL, LE_SAFETY_KEY,
            LE_CALIBRATE_FRONT_GRADE, LE_CALIBRATE_REAR_GRADE, LE_CALIBRATE_BOTH_GRADE,
            LE_CONVERTER,
    })
    public @interface LwrError {
    }

    public static final int CT_NA    = -1;
    public static final int CT_SPEED = 0;
    public static final int CT_POWER = 1;
    public static final int CT_LEVEL = 2;
    public static final int CT_DEFAULT = CT_LEVEL;
    @IntDef({CT_NA, CT_SPEED, CT_POWER, CT_LEVEL})
    public @interface ConstantType {
    }

    // stress test protocol
    public static final int PO_TRACK_MASTER = 0;
    public static final int PO_DYACO        = 1;

    @IntDef({ PO_TRACK_MASTER, PO_DYACO })
    public @interface ProtocolOpt {
    }

    // Beacon Color (燈條的顏色)
    public static final int BC_WHITE  = 0;
    public static final int BC_BLUE   = 1;
    public static final int BC_GREEN  = 2;
    public static final int BC_YELLOW = 3;
    public static final int BC_RED    = 4;
    public static final int BC_NONE   = 5;
    @IntDef({BC_WHITE, BC_BLUE, BC_GREEN, BC_YELLOW, BC_RED, BC_NONE})
    public @interface BeaconColor {
    }


    // switch status
    public static final int SW_OFF   = 0;
    public static final int SW_ON    = 1;
    @IntDef({ SW_OFF, SW_ON })
    public @interface Switch {
    }

    // angle calibrate result
    public static final int CALI_RESULT_NA      = 0;
    public static final int CALI_RESULT_SUCCESS = 1;
    public static final int CALI_RESULT_FAIL    = 2;
    @IntDef({CALI_RESULT_NA, CALI_RESULT_SUCCESS, CALI_RESULT_FAIL})
    public @interface CalibrationResult {
    }
    // Calibration Type
    public static final int CALI_NA    = 0;
    public static final int CALI_ALL   = 1;  // 校正前後揚升
    public static final int CALI_FRONT = 2;  // 校正前揚升
    public static final int CALI_REAR  = 3;  // 校正後揚升
    public static final int CALI_ANGLE = 4;  // 校正angle sensor
    public static final int CALI_MOTOR = 5;  // 校正變頻器 (drive motor)
    @IntDef({ CALI_NA, CALI_ALL, CALI_FRONT, CALI_REAR, CALI_ANGLE, CALI_MOTOR
    })
    public @interface CalibrationType {
    }

    // Error Type
    public static final int ET_NONE        = 0;
    public static final int ET_INVERTER    = 1;  // /INVERTER_ERROR: 電跑. 樓梯機變頻器錯誤. 履跑
    public static final int ET_BRIDGE      = 2;  // BRIDGE_ERROR歸此類
    public static final int ET_MCU         = 3;  // MCU_ERROR (車類)
    public static final int ET_APP         = 4;  // app端發出的錯誤
    public static final int ET_EMS         = 5;  // DK_EMS_ERROR (車類) 東庚專屬EMS error(RS485)
    public static final int ET_UNKNOWN     = 6;  // 不明錯誤
    @IntDef({ ET_NONE, ET_INVERTER, ET_BRIDGE, ET_MCU, ET_APP, ET_EMS, ET_UNKNOWN
    })
    public @interface ErrorType {
    }

    // app端發出的錯誤
    public static final int EC_UART    = 1;     // Uart error: LCB not responding
    public static final int EC_MOTOR   = 2;     // app端發出的錯誤
    public static final int EC_MILEAGE = 3;     // 維保里程數已屆
    public static final int EC_TIME    = 4;     // 維保時間已屆
    public static final int EC_UNKNOWN = 0xFF;  // 不明錯誤
    public @interface AppErrorCode {
    }

    // from LWR (for bundle)
    public static String    AD_D0                       = "ad_d0";
    public static String    AD_D0D1                     = "ad_d0d1";
    public static String    AD_D1                       = "ad_d1";
    public static String    AD_D2                       = "ad_d2";
//    public static String    AT_D5D6                     = "at_d5d6";
//    public static String    AT_REED_SWITCH              = "at_reed_switch";
//    public static String    AT_RPM_COUNTER              = "at_rpm_counter";
//    public static String    AT_RES_AD                   = "at_res_ad";  // ECB當下的AD值
//    public static String    AT_RPM                      = "at_rpm";
//    public static String    AT_PWM_LEVEL                = "at_pwm_level";
    public static String    A3_BRAKE_STATUS             = "a3_brake_status";
    public static String    A6_RPM_L                    = "a6_rpm_l";
    public static String    A6_RPM_R                    = "a6_rpm_r";
    public static String    A6_ISO_PWM_R                = "a6_iso_pwm_r";
    public static String    A6_ISO_PWM_L                = "a6_iso_pwm_l";
    public static String    A6_REVOLUTION               = "a6_revolution";
    public static String    A7_CALI_RESULT              = "a7_cali_result";
    public static String    A8_BRAKE_MODE               = "a8_brake_mode";
    public static String    AA_SPM                      = "aa_spm";
    public static String    AA_D2D3                     = "aa_d2d3";
    public static String    AB_ANGLE                    = "ab_angle";
    public static String    AB_RPM                      = "ab_rpm";
    public static String    AB_WATT                     = "ab_watt";
    public static String    AD_STEP_PULSE_COUNT1        = "ad_step_pulse_count1";
    public static String    AD_STEP_PULSE_COUNT2        = "ad_step_pulse_count2";
    public static String    AD_NEW_STEP                 = "ad_new_step";
    public static String    AD_PULSE_COUNT              = "ad_pulse_count";
    public static String    AD_HALL_SENSOR_STATUS       = "ad_hall_sensor_status";
    public static String    B4_STEP_LEFT_LEN            = "b4_step_left_len";
    public static String    B4_STEP_RIGHT_LEN           = "b4_step_right_len";
    public static String    B4_STEPS_L                  = "b4_steps_l";
    public static String    B4_STEPS_R                  = "b4_steps_r";
    public static String    B4_SPM                      = "b4_spm";    // total steps (stairmill)
    public static String    B4_CADENCE                  = "b4_cadence";
    public static String    B4_STEP_SPEED               = "b4_step_speed";
    public static String    ALERT_MSG                   = "alert_msg";         // 警示訊息 (string)
    public static String    ALERT_VISIBLE               = "alert_visible";     // true:顯示警示訊息, false:關閉警示訊息


    public static String    CALI_FRONT_RSLT             = "cali_front_rslt";   // 前揚升校正結果
    public static String    CALI_REAR_RSLT              = "cali_rear_rslt";    // 後揚升校正結果
    public static String    CALI_ANGLE_RSLT             = "cali_angle_rslt";   // Angle Sensor校正結果
    public static String    CALI_MOTOR_RSLT             = "cali_motor_rslt";   // Drive Motor校正結果


    // post message type
    public static final int PT_IGNORE            = 0;
    public static final int PT_UART              = 1;
    public static final int PT_KEY               = 2;
    public static final int PT_GENERAL           = 3;
    public static final int PT_WEB_API           = 4;
    public static final int PT_UPDATE            = 5;
    public static final int PT_POS               = 6;
    public static final int PT_GEM3              = 7;
    public static final int PT_DEFAULT           = 8; // default_settings.json相關訊息
    public static final int PT_PROTOCOL          = 9;
    public static final int PT_STRESS_TEST       = 10;
    public static final int PT_PC_DATA           = 11;
    public static final int PT_NOW               = 12;
    public static final int PT_PROTOCOL_TM       = 13;
    public static final int PT_B5_CMD            = 14;
    public static final int PT_HR_CONTROL        = 15;
    public static final int PT_SYS_BT            = 16;
    public static final int PT_GARMIN            = 17;
    public static final int PT_MMKV              = 19;
    public static final int PT_DISPLAY_MODE      = 20;
    public static final int PT_MIRRORING         = 21;
    public static final int PT_PAUSE             = 22;
    public static final int PT_VR                = 23;
    @IntDef({ PT_IGNORE, PT_UART, PT_KEY, PT_GENERAL, PT_WEB_API, PT_UPDATE,
            PT_POS, PT_GEM3 , PT_DEFAULT, PT_PROTOCOL, PT_STRESS_TEST, PT_PC_DATA,
            PT_NOW, PT_PROTOCOL_TM, PT_B5_CMD, PT_HR_CONTROL, PT_SYS_BT, PT_GARMIN, PT_MMKV, PT_DISPLAY_MODE, PT_MIRRORING, PT_PAUSE, PT_VR })
    public @interface PostType {
    }

    public static final String EVT_DEV_STEP               = "evt_dev_step";                 // device step
    public static final String EVT_WORKLOAD_CHANGED       = "evt_workload_changed";
    public static final String EVT_INCLINE_CHANGED        = "evt_incline_changed";
    public static final String EVT_INCLINE_SETTING        = "evt_incline_setting";
    public static final String EVT_SPEED_CHANGED          = "evt_speed_changed";
    public static final String EVT_SPEED_SETTING          = "evt_speed_setting";

    public static final String EVT_SPEED_RESUME            = "EVT_SPEED_RESUME";              // 按下start key
    public static final String EVT_PRESS_START            = "evt_press_start";              // 按下start key
    public static final String EVT_PRESS_STOP            = "EVT_PRESS_STOP";              // 按下start key
    public static final String EVT_USB_MODE_DATA          = "evt_usb_mode_data";            // usb mode在資料模式(非充電)
    public static final String EVT_ECB_TIMEOUT            = "evt_ect_timeout";

    public static final String EVT_PRESS_CONFIRM          = "evt_press_confirm";            // 按下confirm鍵
    public static final String EVT_PRESS_MODE             = "evt_press_mode";               // 按下mode鍵
    public static final String EVT_SENSOR_PAUSE           = "evt_sensor_pause";             // 樓梯機sensor pause
    public static final String EVT_SENSOR_RESUME          = "evt_sensor_resume";            // 樓梯機sensor pause
    public static final String EVT_SENSOR_FINISH          = "evt_sensor_finish";            // 樓梯機sensor pause

    public static final String EVT_GRADE_ERROR            = "evt_grade_error";              // 揚升發生錯誤: true/false
    // 已接收到lift修改deck height (init,  workout之前的deck height調整)
    public static final String EVT_RES_DONE               = "evt_res_done";                 // resistance AD時, 已到達誤差值, 視為已到達, CLEAR下方訊息
    public static final String EVT_SPDRATE_TIME_DONE      = "evt_spdrate_time_done";        // 設定的加減速時間已符合
    public static final String EVT_CALI_RESULT            = "evt_cali_result";              // 校正結果
    public static final String EVT_ONGOING                = "evt_ongoing";                  // app進行中的參數

    public static final String EVT_80                     = "evt_80";                       // 非電跑80相關數值
    public static final String EVT_A3                     = "evt_a3";                       // 非電跑A3相關數值
    public static final String EVT_A6                     = "evt_a6";                       // 非電跑A6相關數值
    public static final String EVT_AA                     = "evt_aa";                       // 非電跑AA相關數值
    public static final String EVT_AB                     = "evt_ab";                       // 非電跑AB相關數值
    public static final String EVT_AD                     = "evt_ad";                       // 非電跑AD相關數值
    public static final String EVT_B0                     = "evt_b0";                       // 電跑B0相關數值
    public static final String EVT_B4                     = "evt_b4";                       // 電跑B4相關數值
    public static final String EVT_B9                     = "evt_b9";                       // 電跑B9相關數值


    public static final String EVT_ALERT_START            = "evt_alert_start";              // 通知Start按鈕警示是否要顯示, 及顯示的訊息

//    //  [EMS] Power Range
    public static final int EMS_LWR_POWER_MIN = 1;      // 1
    public static final int EMS_LWR_POWER_MAX = 500;    // 500
    public static final int EMS_LWR_POWER_INC = 1;      // 1

    public static final int EDIT_GRADE_VR_AD_MIN = 80;
    public static final int EDIT_GRADE_VR_MAX = 800;

    public static final int EDIT_GRADE_CD_AD_MIN = 5;
    public static final int EDIT_GRADE_CD_AD_MAX = 1500;

    //>>> for dk city newer
//    public static final double  DK_STEPS_PER_FLOOR = 23;  // for stair floor: steps per floor
    // 500 -> 400
    // 620 ->
    // ~730

    // 最低點: 80 (level 0)
    // 最高點: 650 (level 15)
    // 15段, 共16段
    //<<< for dk city newer

}
