package com.dyaco.spirit_commercial.listener;

import com.corestar.libs.device.DeviceSpiritC;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;

import java.util.List;

public interface IUartConsole {

    void initialize();

    void setDevUnit();

    void setDevSpeedAndIncline();

    void setDevDriveMotorTreadmill();

    void startDevCalibration(DeviceSpiritC.TREADMILL_CALI_MODE caliMode);

    void setDevMainMode(DeviceSpiritC.MAIN_MODE mainMode); // >>setDevMainMode > onMainModeTreadmill

    void setDevStep(@GENERAL.DeviceStep int devStep);

    void startUartFlowCommand();

    int getDevStep();

    void setDevWorkoutFinish();

    void onConnectFail();


    void onConnected();


    void onDisconnected();


    void onDataSend(String s);


    void onDataReceive(String s);


    void onErrorMessage(String s);


    void onCommandError(DeviceSpiritC.COMMAND command, DeviceSpiritC.COMMAND_ERROR command_error);


    void onKeyTrigger(DeviceSpiritC.KEY key);


    void onMultiKey(int i, List<DeviceSpiritC.KEY> list);


    void onEupNotify(DeviceSpiritC.EUP eup);


    void onLwrMode(DeviceSpiritC.LWR_MODE lwr_mode);


    void onSpeedCali(DeviceSpiritC.SPEED_CALI_STEP speed_cali_step, DeviceSpiritC.SPEED_CALI_STATUS speed_cali_status, List<DeviceSpiritC.MCU_ERROR> list, int i, int i1);


    void onEcbAdjust(DeviceSpiritC.ACTION_STATUS action_status, int i);


    void onInclineAdjust(DeviceSpiritC.ACTION_STATUS action_status, int i);


    void onInclineRead(int i, int i1, DeviceSpiritC.INCLINE_READ_STATUS incline_read_status);


    void onInclineCali(DeviceSpiritC.INCLINE_CALI_STATUS incline_cali_status, int i);


    void onDeviceInfo(DeviceSpiritC.MODEL model, String csMcuVer, String keyStatus, String lwrFwVer, int hrmStatus, String subMcuVer, String lwrHwVer);


    void onMcuSetting(DeviceSpiritC.MODEL model, DeviceSpiritC.MCU_SET mcu_set);


    void onMcuControl(DeviceSpiritC.MODEL model, List<DeviceSpiritC.MCU_ERROR> mcuErrors, int hpHr, int wpHr, DeviceSpiritC.ACTION_STATUS inclineStatus, int inclineAd, DeviceSpiritC.SAFE_KEY safeKey, int speed, int stepCount, DeviceSpiritC.DIRECTION direction, int rpm, DeviceSpiritC.ACTION_STATUS res, int resCode, int reedSwitchRpm1, int angleSensorRpm2, int pwmLevel);

    void onEchoMode(DeviceSpiritC.ECHO_MODE echo_mode);


    void onEEPRomWrite(DeviceSpiritC.MCU_SET mcu_set);


    void onEEPRomRead(DeviceSpiritC.MCU_GET mcu_get, byte[] bytes, byte[] bytes1);


    void onUsbModeSet(DeviceSpiritC.MCU_SET mcu_set);


    void onHeartRateMode(DeviceSpiritC.HEART_RATE_MODE heart_rate_mode);


    void onRpmCounterMode(DeviceSpiritC.RPM_COUNTER_MODE rpm_counter_mode);


    void onConsoleMode(DeviceSpiritC.MACHINE_TYPE machine_type, DeviceSpiritC.CONSOLE_MODE console_mode);


    void onBrakeStatus(DeviceSpiritC.BRAKE_STATUS brake_status);


    void onTargetRpm(int targetRpm);


    void onCurrentRpm(int i, int i1, int i2, int i3, int i4);


    void onAngleCali(DeviceSpiritC.ANGLE_CALI_STATUS angle_cali_status);


    void onBrakeMode(DeviceSpiritC.BRAKE_MODE brake_mode);


    void onAngleSetting(int i, int i1);


    void onCurrentStepCount(int i, int i1, int i2);

    void onEchoTreadmill(DeviceSpiritC.MAIN_MODE main_mode, DeviceSpiritC.INIT_STATUS init_status, DeviceSpiritC.INVERTER_ERROR converterError, List<DeviceSpiritC.BRIDGE_ERROR> bridgeErrorList, int i, int i1, DeviceSpiritC.SAFE_KEY safe_key, int i2, DeviceSpiritC.INCLINE_STATUS incline_status, DeviceSpiritC.INCLINE_STATUS incline_status1, int i3, DeviceSpiritC.TREADMILL_CALI_STATUS treadmill_cali_status, int i4, int i5, int i6, int i7);

    void onSettingSpeedAndInclineTreadmill(DeviceSpiritC.MOTOR_DIRECTION motor_direction, int i, int i1, int i2);


    void onCurrentSpeedAndInclineTreadmill(DeviceSpiritC.MOTOR_DIRECTION motor_direction, int i, int i1, int i2);


    void onUnitTreadmill(DeviceSpiritC.UNIT unit);


    void onStepInfoTreadmill(int i, int i1, int i2, int i3, int i4, int i5);


    void onTargetSpeedAndInclineTreadmill(DeviceSpiritC.MOTOR_DIRECTION motor_direction, int speed, int frontGrade, int rearGrade);


    void onTargetSpeedRateTreadmill(int i, int i1);


    void onMainModeTreadmill(DeviceSpiritC.MAIN_MODE main_mode);


    void onSpeedAndInclineStopTreadmill(DeviceSpiritC.STOP_STATUS stop_status, DeviceSpiritC.STOP_STATUS stop_status1, DeviceSpiritC.STOP_STATUS stop_status2);


    void onCurrentInclineAndSensorTreadmill(int i, int i1, int i2, int i3);


    void onBrakeModeTreadmill(DeviceSpiritC.BRAKE_MODE brake_mode);


    void onInclineCaliModeTreadmill(DeviceSpiritC.TREADMILL_CALI_MODE treadmill_cali_mode);


    void onDefaultSpeedRateTreadmill(int i, int i1);


    void onInclineRangeTreadmill(int i, int i1, int i2, int i3);


    void onTimeoutControlTreadmill(DeviceSpiritC.TIMEOUT_CONTROL timeout_control, DeviceSpiritC.TEST_CONTROL test_control);


    void onErrorClearTreadmill(DeviceSpiritC.ERROR_STATUS error_status, DeviceSpiritC.ERROR_STATUS error_status1);


    void onResetTreadmill(DeviceSpiritC.RESET_STATUS reset_status);

    void onResetToBootloaderTreadmill(DeviceSpiritC.RESET_STATUS reset_status);

    String getMyStepString();

    void setDevWorkload(int level, int resistance);

    void setBuzzer();

    void emsWorkoutStart();

    void emsWorkoutPause(int resistance);

    void emsWorkoutFinish();

    void resetSpeedAndIncline(boolean forceGradeReturn);
    //void setDevConsoleMode(DeviceSpiritC.CONSOLE_MODE consoleMode);

    void getDeviceInfo();

    void clearUartErrorAll();

    void setEmsBrakeTest(int pwmLevelDA);

    void onSensibilityTreadmill(int i);

    void resetLwrAfterUpdate();

    boolean isCriticalError();

    boolean checkErrorShowed(int u);

    void updateErrorMapValue(int u);

    void setConsoleTimeoutCnt(int count);

    void setConsoleRS485ErrorCnt(int count);
}
