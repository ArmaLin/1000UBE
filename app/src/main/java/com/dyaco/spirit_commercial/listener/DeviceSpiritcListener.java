package com.dyaco.spirit_commercial.listener;

import com.corestar.libs.device.DeviceSpiritC;

import java.util.List;

public class DeviceSpiritcListener implements DeviceSpiritC.DeviceEventListener {

    private final IUartConsole uartConsole;

    public DeviceSpiritcListener(IUartConsole uartConsole) {
        this.uartConsole = uartConsole;
    }

    @Override
    public void onConnectFail() {
        uartConsole.onConnectFail();
    }

    @Override
    public void onConnected() {
        uartConsole.onConnected();
    }

    @Override
    public void onDisconnected() {
        uartConsole.onDisconnected();
    }

    @Override
    public void onDataSend(String s) {
        uartConsole.onDataSend(s);
    }

    @Override
    public void onDataReceive(String s) {
        uartConsole.onDataReceive(s);
    }

    @Override
    public void onErrorMessage(String s) {
        uartConsole.onErrorMessage(s);
    }

    @Override
    public void onCommandError(DeviceSpiritC.COMMAND command, DeviceSpiritC.COMMAND_ERROR command_error) {
        uartConsole.onCommandError(command, command_error);
    }

    @Override
    public void onKeyTrigger(DeviceSpiritC.KEY key) {
        uartConsole.onKeyTrigger(key);
    }

    @Override
    public void onMultiKey(int i, List<DeviceSpiritC.KEY> list) {
        uartConsole.onMultiKey(i, list);
    }

    @Override
    public void onEupNotify(DeviceSpiritC.EUP eup) {
        uartConsole.onEupNotify(eup);
    }

    @Override
    public void onLwrMode(DeviceSpiritC.LWR_MODE lwr_mode) {
        uartConsole.onLwrMode(lwr_mode);
    }

    @Override
    public void onSpeedCali(DeviceSpiritC.SPEED_CALI_STEP speed_cali_step, DeviceSpiritC.SPEED_CALI_STATUS speed_cali_status, List<DeviceSpiritC.MCU_ERROR> list, int i, int i1) {
        uartConsole.onSpeedCali(speed_cali_step, speed_cali_status, list, i, i1);
    }

    @Override
    public void onEcbAdjust(DeviceSpiritC.ACTION_STATUS action_status, int i) {
        uartConsole.onEcbAdjust(action_status, i);
    }

    @Override
    public void onInclineAdjust(DeviceSpiritC.ACTION_STATUS action_status, int i) {
        uartConsole.onInclineAdjust(action_status, i);
    }

    @Override
    public void onInclineRead(int i, int i1, DeviceSpiritC.INCLINE_READ_STATUS incline_read_status) {
        uartConsole.onInclineRead(i, i1, incline_read_status);
    }

    @Override
    public void onInclineCali(DeviceSpiritC.INCLINE_CALI_STATUS incline_cali_status, int i) {
        uartConsole.onInclineCali(incline_cali_status, i);
    }

    @Override
    public void onDeviceInfo(DeviceSpiritC.MODEL model, String csMcuVer, String keyStatus, String lwrFwVer, int hrmStatus, String subMcuVer, String lwrHwVer) {
        uartConsole.onDeviceInfo(model, csMcuVer, keyStatus, lwrFwVer, hrmStatus, subMcuVer, lwrHwVer);
    }

    @Override
    public void onMcuSetting(DeviceSpiritC.MODEL model, DeviceSpiritC.MCU_SET mcu_set) {
        uartConsole.onMcuSetting(model, mcu_set);
    }

    @Override
    public void onMcuControl(DeviceSpiritC.MODEL model, List<DeviceSpiritC.MCU_ERROR> mcuErrors, int hpHr, int wpHr, DeviceSpiritC.ACTION_STATUS inclineStatus, int inclineAd, DeviceSpiritC.SAFE_KEY safeKey, int speed, int stepCount, DeviceSpiritC.DIRECTION direction, int rpm, DeviceSpiritC.ACTION_STATUS res, int resCode, int reedSwitchRpm1, int angleSensorRpm2, int pwmLevel) {
        uartConsole.onMcuControl(model, mcuErrors, hpHr, wpHr, inclineStatus, inclineAd, safeKey, speed, stepCount, direction, rpm, res, resCode, reedSwitchRpm1, angleSensorRpm2, pwmLevel);
    }

    @Override
    public void onEchoMode(DeviceSpiritC.ECHO_MODE echo_mode) {
        uartConsole.onEchoMode(echo_mode);
    }

    @Override
    public void onEEPRomWrite(DeviceSpiritC.MCU_SET mcu_set) {
        uartConsole.onEEPRomWrite(mcu_set);
    }

    @Override
    public void onEEPRomRead(DeviceSpiritC.MCU_GET mcu_get, byte[] bytes, byte[] bytes1) {
        uartConsole.onEEPRomRead(mcu_get, bytes, bytes1);
    }

    @Override
    public void onUsbModeSet(DeviceSpiritC.MCU_SET mcu_set) {
        uartConsole.onUsbModeSet(mcu_set);
    }

    @Override
    public void onHeartRateMode(DeviceSpiritC.HEART_RATE_MODE heart_rate_mode) {
        uartConsole.onHeartRateMode(heart_rate_mode);
    }

    @Override
    public void onRpmCounterMode(DeviceSpiritC.RPM_COUNTER_MODE rpm_counter_mode) {
        uartConsole.onRpmCounterMode(rpm_counter_mode);
    }

    @Override
    public void onConsoleMode(DeviceSpiritC.MACHINE_TYPE machine_type, DeviceSpiritC.CONSOLE_MODE console_mode) {
        uartConsole.onConsoleMode(machine_type, console_mode);
    }

    @Override
    public void onBrakeStatus(DeviceSpiritC.BRAKE_STATUS brake_status) {
        uartConsole.onBrakeStatus(brake_status);
    }

    @Override
    public void onTargetRpm(int targetRpm) {
        uartConsole.onTargetRpm(targetRpm);
    }

    @Override
    public void onCurrentRpm(int i, int i1, int i2, int i3, int i4) {
        uartConsole.onCurrentRpm(i, i1, i2, i3, i4);
    }

    @Override
    public void onAngleCali(DeviceSpiritC.ANGLE_CALI_STATUS angle_cali_status) {
        uartConsole.onAngleCali(angle_cali_status);
    }

    @Override
    public void onBrakeMode(DeviceSpiritC.BRAKE_MODE brake_mode) {
        uartConsole.onBrakeMode(brake_mode);
    }

    @Override
    public void onAngleSetting(int i, int i1) {
        uartConsole.onAngleSetting(i, i1);

    }

    @Override
    public void onCurrentStepCount(int i, int i1, int i2) {
        uartConsole.onCurrentStepCount(i, i1, i2);
    }

    //setEchoTreadmill 的callback 下控的錯誤 狀態 ,要存起來
    @Override
    public void onEchoTreadmill(DeviceSpiritC.MAIN_MODE main_mode, DeviceSpiritC.INIT_STATUS init_status, DeviceSpiritC.INVERTER_ERROR converterError, List<DeviceSpiritC.BRIDGE_ERROR> bridgeErrorList, int i, int i1, DeviceSpiritC.SAFE_KEY safe_key, int i2, DeviceSpiritC.INCLINE_STATUS incline_status, DeviceSpiritC.INCLINE_STATUS incline_status1, int i3, DeviceSpiritC.TREADMILL_CALI_STATUS treadmill_cali_status, int i4, int i5, int i6, int i7) {

        // uartConsole.saveError(converterError, bridgeErrorList);
        //main_mode >
        uartConsole.onEchoTreadmill(main_mode, init_status, converterError, bridgeErrorList, i, i1, safe_key, i2, incline_status, incline_status1, i3, treadmill_cali_status, i4, i5, i6, i7);
    }

    @Override
    public void onSettingSpeedAndInclineTreadmill(DeviceSpiritC.MOTOR_DIRECTION motor_direction, int i, int i1, int i2) {
        uartConsole.onSettingSpeedAndInclineTreadmill(motor_direction, i, i1, i2);
    }

    @Override
    public void onCurrentSpeedAndInclineTreadmill(DeviceSpiritC.MOTOR_DIRECTION motor_direction, int i, int i1, int i2) {
        uartConsole.onCurrentSpeedAndInclineTreadmill(motor_direction, i, i1, i2);
    }

    @Override
    public void onUnitTreadmill(DeviceSpiritC.UNIT unit) {
        //setUnitTreadmill
        uartConsole.onUnitTreadmill(unit);
    }

    @Override
    public void onStepInfoTreadmill(int i, int i1, int i2, int i3, int i4, int i5) {
        uartConsole.onStepInfoTreadmill(i, i1, i2, i3, i4, i5);
    }

    @Override
    public void onTargetSpeedAndInclineTreadmill(DeviceSpiritC.MOTOR_DIRECTION motor_direction, int speed, int frontGrade, int rearGrade) {
        uartConsole.onTargetSpeedAndInclineTreadmill(motor_direction, speed, frontGrade, rearGrade);
    }

    @Override
    public void onTargetSpeedRateTreadmill(int i, int i1) {
        uartConsole.onTargetSpeedRateTreadmill(i, i1);
    }

    @Override
    public void onMainModeTreadmill(DeviceSpiritC.MAIN_MODE main_mode) {
        uartConsole.onMainModeTreadmill(main_mode);
    }

    @Override
    public void onSpeedAndInclineStopTreadmill(DeviceSpiritC.STOP_STATUS stop_status, DeviceSpiritC.STOP_STATUS stop_status1, DeviceSpiritC.STOP_STATUS stop_status2) {
        uartConsole.onSpeedAndInclineStopTreadmill(stop_status, stop_status1, stop_status2);
    }

    @Override
    public void onCurrentInclineAndSensorTreadmill(int i, int i1, int i2, int i3) {
        uartConsole.onCurrentInclineAndSensorTreadmill(i, i1, i2, i3);
    }

    @Override
    public void onBrakeModeTreadmill(DeviceSpiritC.BRAKE_MODE brake_mode) {
        uartConsole.onBrakeModeTreadmill(brake_mode);
    }

    @Override
    public void onInclineCaliModeTreadmill(DeviceSpiritC.TREADMILL_CALI_MODE treadmill_cali_mode) {
        uartConsole.onInclineCaliModeTreadmill(treadmill_cali_mode);
    }

    @Override
    public void onDefaultSpeedRateTreadmill(int i, int i1) {
        uartConsole.onDefaultSpeedRateTreadmill(i, i1);
    }

    @Override
    public void onInclineRangeTreadmill(int i, int i1, int i2, int i3) {
        uartConsole.onInclineRangeTreadmill(i, i1, i2, i3);
    }

    @Override
    public void onTimeoutControlTreadmill(DeviceSpiritC.TIMEOUT_CONTROL timeout_control, DeviceSpiritC.TEST_CONTROL test_control) {
        uartConsole.onTimeoutControlTreadmill(timeout_control, test_control);
    }

    @Override
    public void onErrorClearTreadmill(DeviceSpiritC.ERROR_STATUS error_status, DeviceSpiritC.ERROR_STATUS error_status1) {
        uartConsole.onErrorClearTreadmill(error_status, error_status1);
    }

    @Override
    public void onResetTreadmill(DeviceSpiritC.RESET_STATUS reset_status) {
        uartConsole.onResetTreadmill(reset_status);
    }

    @Override
    public void onResetToBootloaderTreadmill(DeviceSpiritC.RESET_STATUS reset_status) {
        uartConsole.onResetToBootloaderTreadmill(reset_status);
    }

    @Override
    public void onSensibilityTreadmill(int i) {
        uartConsole.onSensibilityTreadmill(i);
    }

    @Override
    public void onBtAudioState(DeviceSpiritC.BT_AUDIO_STATE bt_audio_state) {

    }

}