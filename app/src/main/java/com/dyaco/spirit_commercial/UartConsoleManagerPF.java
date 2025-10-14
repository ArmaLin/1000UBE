package com.dyaco.spirit_commercial;


import static com.dyaco.spirit_commercial.App.MODE;
import static com.dyaco.spirit_commercial.App.getApp;
import static com.dyaco.spirit_commercial.UartConst.DS_A0_PAUSE_STANDBY;
import static com.dyaco.spirit_commercial.UartConst.DS_ECB_IDLE_STANDBY;
import static com.dyaco.spirit_commercial.UartConst.DS_ECB_PAUSE_STANDBY;
import static com.dyaco.spirit_commercial.UartConst.DS_EMS_IDLE_STANDBY;
import static com.dyaco.spirit_commercial.UartConst.WORKLOAD_MIN;
import static com.dyaco.spirit_commercial.product_flavor.ModeEnum.UBE;
import static com.dyaco.spirit_commercial.support.CommonUtils.restartApp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Toast;

import com.corestar.libs.device.DeviceDyacoMedical;
import com.corestar.libs.ota.LwrMcuUpdateManager;
import com.dyaco.spirit_commercial.support.CoTimer;
import com.dyaco.spirit_commercial.support.CommonUtils;
import com.dyaco.spirit_commercial.support.UnitConv;
import com.dyaco.spirit_commercial.viewmodel.AppStatusViewModel;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingViewModel;
import com.dyaco.spirit_commercial.viewmodel.WorkoutViewModel;
import com.jeremyliao.liveeventbus.LiveEventBus;

import java.util.List;

import kotlinx.coroutines.Job;
import timber.log.Timber;


public class UartConsoleManagerPF implements DeviceDyacoMedical.DeviceEventListener {
    private static final String TAG = "UartConsoleManager";
    private boolean m_waitSpeedZero = false;
    private boolean m_criticalError = false;
    private final DeviceDyacoMedical consoleUart;
    private Job taskTimer;

    private final WorkoutViewModel woVM;
    private final DeviceSettingViewModel dsVM;
    private final UartVM uartVM;
    MainActivity m;
    public UartConsoleManagerPF(WorkoutViewModel woVM, MainActivity m, DeviceSettingViewModel dsVM, UartVM uartVM, AppStatusViewModel appStatusViewModel) {
        Timber.d("UartConsoleManager constructor");
        this.m = m;
        this.woVM = woVM;
        this.dsVM = dsVM;
        this.uartVM = uartVM;

        uartVM.clearKeyStatus();

        consoleUart = UartLocator.getDeviceDyacoMedical(getApp());
    }

    public void initialize() {

        // 已接實體機, 開始跑uart流程 (DEVICE_PHYSICAL, DEVICE_TEST皆要執行connect)
        setDevStep(UartConst.DS_CONNECT_RSP);

        consoleUart.connect();

        Timber.d("UartConsoleManager connected");
    }

    public void startUartFlowCommand() {

        // 僅針對 UBE及Stepper處理

        uartVM.isStartUartFlow.set(true);

        // uart未連接 或 uart已初始化完成, 不再跑流程
        if ((!uartVM.isUartConnected.get()) || uartVM.isUartInitialized.get()) {
            Timber.tag(TAG).d("初始化失敗: " + uartVM.isUartConnected.get() + "," + uartVM.isUartInitialized.get());
            return;
        }

        Timber.d("enter startUartFlowCommand");

        @UartConst.DeviceStep int devStep = getDevStep();

        if (devStep == UartConst.DS_CONNECT_RSP) {
            Timber.d("取得裝置資訊 (DS_99_DEV_INFO_RSP)");
            uartVM.unknownCounter.set(0);
            getDeviceInfo();
        }
        Timber.d("啟動定時發送 (EMS, ECB heartbeat)");

        startEcho();
    }

    private void setRealTimePwm() {
        @UartConst.ConstantType int constantType = uartVM.constantType.get();

        int workload = uartVM.workload.get();

        switch (constantType) {
            case UartConst.CT_SPEED:
                setDevTargetRpm(workload);
                break;

            case UartConst.CT_LEVEL:
                setPwmViaLevel(workload);
                break;

            case UartConst.CT_POWER:
                setPwmViaPower(workload);
                break;

            case UartConst.CT_NA:
                break;
        }
    }

    private void startEcho() {
        //持續傳送  >> 回傳  //onEchoTreadmill

        CoTimer.after(1000, () ->
                taskTimer = CoTimer.every(1000, elapsedMillis ->
                        doEchoTask()));


        //  開始接收UART發送錯誤
        uartVM.ignoreUartError.set(false);
        uartVM.isUartInitialized.set(true);

        Timber.d("setIgnoreUartError = false");

    }

    public void doEchoTask() {

        int modelCode = dsVM.modelCode.get();

        Timber.d("MachineCode= " + modelCode);

        if (!uartVM.isUartConnected.get()) {

            Timber.d("UART NOT connected");

            return;   // 在此先判斷uart如果未連接, 就不發錯誤(要做mcu update)
        }

        if (uartVM.stopHeartbeat.get()) {
            Timber.d("停止心跳包傳送");
            return;
        }

        if (uartVM.ignoreUartError.get()) {
            uartVM.lwrTimeoutCounter.set(0);
            uartVM.isLcbNotResponding.set(false);
        } else {
            uartVM.increaseLwrTimeoutCounter();
        }

        setRealTimePwm();  // workout可能不是每秒執行 setDevWorkload, 在這裡重新計算設定at_valudAd

        // 這裡依機型要定時傳送心跳包
        if (MODE == UBE) {
            // UBE
            // 定時傳送 0x80
            setDevResLevel();
        } else {
            // Stepper
            // 定時傳送0x80
            setDevPwmLevel();
        }

        if (uartVM.lwrTimeoutCounter.get() > 5) { // 5
            Timber.d("doEchoTask postUartError");

            // 下控未回應, 有可能是APP端已經CRASH, 直接設定為緊急錯誤
            uartVM.isLcbNotResponding.set(true);
            postUartError(ErrorInfoEnum.APP_UART_E401);
            m_criticalError = true;
        }

        if (m_criticalError) {
            CoTimer.cancelJob(taskTimer);
        }
    }

    public void resetSpeedAndGrade(boolean forceGradeReturn) {
    }

    public void setDevUnit() {
    }

    public void setDevMainMode(DeviceDyacoMedical.MAIN_MODE mainMode) {
    }

    public void setMainModeTreadmill(DeviceDyacoMedical.MAIN_MODE mode) {
    }

    public void setSpeedRateConverter() {
    }

    public void setHertzConvert() {

    }

    public void setTargetSpeedAndIncline(int speed, int frontGrade, int rearGrade) {
    }

    public void setDevSpeedAndInclineMT(int speedValue, int gradeValue, boolean gradeDC) {
    }

    public void setDevDriveMotorTreadmill(int beltSpeed) {
    }

    public void setDevAutoBrakeNonMT(DeviceDyacoMedical.BRAKE_MODE brakeMode) {
    }

    public void setDevBrakeModeMT(boolean brakeOn) {

    }

    public void updateDevSpeedValueMT(int speedValue) {
    }

    public void updateDevGradeValue(int gradeValue) {
    }

    public void clearUartError(DeviceDyacoMedical.ERROR_STATUS inverterErr, DeviceDyacoMedical.ERROR_STATUS bridgetErr) {
    }

    public void resetLwrAfterUpdate() {
    }

    public void startDevCalibration(DeviceDyacoMedical.TREADMILL_CALI_MODE caliMode) {
        //  開始揚升或變頻器校正
    }

    public int getLegalGradeAd(int ad) {
        return ad;
    }

    public int getLegalSpeedMT(int speed) {
        return speed;
    }

    public void setDevConsoleMode(DeviceDyacoMedical.CONSOLE_MODE consoleMode) {
        if (!uartVM.isUartConnected.get()) return;

        switch (consoleMode) {
            case SENSOR_CALI:
                setDevStep(UartConst.DS_A0_CALIBRATION_RSP);
                break;

            case SENSOR_TEST:
                setDevStep(UartConst.DS_A0_SENSOR_TEST_RSP);
                break;

            case IDLE:
                Timber.d("set consoleMode = IDLE");
                setDevStep(UartConst.DS_A0_IDLE_RSP);
                break;

            case PAUSE:
                setDevStep(UartConst.DS_A0_PAUSE_RSP);
                break;

            case POWER_DISTRIBUTION_ISO:    // workload: iso (power distribution)
            case POWER_DISTRIBUTION_LEVEL:  // workload: level (power distribution)
            case NORMAL:                    // workload: level, power
            case ISO:                       // workload: iso (speed)
                setDevStep(UartConst.DS_A0_RUNNING_RSP);
                break;

            case UNKNOWN:
                return;
        }

        uartVM.a0_consoleMode.set(consoleMode);
        consoleUart.setConsoleMode(uartVM.a0_machineType.get(), consoleMode);
    }

    public void setDevMotorTestPT(int level) {
        // Maintenance Mode: Motor Test for UBE 產測使用
        setDevStep(UartConst.DS_80_MOTOR_TEST_RES_RSP);
        setPwmViaLevel(level);
    }

    public void setDevMotorTestDS(int adcValue) {
        // Maintenance Mode: Motor Test for UBE 開發階段使用
        setDevStep(UartConst.DS_80_MOTOR_TEST_RES_RSP);
        uartVM.at_valueAd.set(adcValue);
    }

    public void setEmsBrakeTest(int pwmLevelDA) {

        // Maintenance Mode: for Stepper - Brake Test
        uartVM.at_valuePwm.set(pwmLevelDA);

        if (!uartVM.isUartConnected.get()) return;

        consoleUart.setMyCareEms(DeviceDyacoMedical.MODEL.EMS_M2, pwmLevelDA);
    }

    public void setDevWorkload(int workload) {

        @UartConst.ConstantType int constantType = uartVM.constantType.get();

        uartVM.workload.set(workload);

        switch (constantType) {
            case UartConst.CT_SPEED:
                // workload為SPEED(ISO) constant, 直接設定速度
                // (1) uartVM.constantType.set(CT_SPEED);  // default
                // (2) setDevWorkload(speed);
                setDevStep(UartConst.DS_A5_RUNNING_RPM_RSP);
                setDevTargetRpm(workload);

                Timber.d("setDevWorkload: setTargetRpm, workload = " + workload);

                break;

            case UartConst.CT_LEVEL:
                // workload為level, 此處依UBE或stepper, 傳送不同devStep
                // (1) uartVM.constantType.set(CT_LEVEL);  // default
                // (2) setDevWorkload(level);
                setDevStep(MODE == UBE ?
                        UartConst.DS_80_RUNNING_RES_RSP :   // ube
                        UartConst.DS_80_RUNNING_PWM_RSP);   // stepper
                setPwmViaLevel(workload);
                Timber.d("setDevWorkload: setDevPwmLevel, getValueAD = " + uartVM.at_valueAd.get());
                break;

            case UartConst.CT_POWER:
                // workload為power, 此處依UBE或stepper, 傳送不同devStep
                // (1) uartVM.constantType.set(CT_POWER);
                // (2) setDevWorkload(power);
                setDevStep(UartConst.DS_80_RUNNING_PWM_RSP);
                setPwmViaPower(workload);
                Timber.d("setDevWorkload: setDevPwmLevel, getValuePWM = " + uartVM.at_valuePwm.get());
                break;

            case UartConst.CT_NA:
                break;
        }
    }

    public void setDevPwmLevel() {

        if (!uartVM.isUartConnected.get()) return;

        @UartConst.DeviceStep int devStep = getDevStep();
        @UartConst.ConstantType int constantType = uartVM.constantType.get();

        int pwmLevelDA = 0;

        // 僅在running期間(非pause), 且constant非ISO(speed)才送值, 其餘情況皆送0
        Timber.d("setDevPwmLevel: devStep = " + devStep);
        if ((devStep == UartConst.DS_EMS_RUNNING_STANDBY) ||
                (devStep == UartConst.DS_80_RUNNING_PWM_RSP)) {
            if (constantType != UartConst.CT_SPEED) {
                pwmLevelDA = uartVM.at_valuePwm.get();
                Timber.d("setDevPwmLevel: pwmLevelDA = " + pwmLevelDA);
            }
        }
        //  工程模式如果有Brake Test, 則需enable 此段
//        else {
//            if (dKcityWorkout.getMyPosition() == R.id.posBrakeTest) {
//                // EMS在工程模式有brake test, 在此階段要讀取此值, 否則會造成沒阻力(僅送了一次)
//                pwmLevelDA = getValuePWM();
//            }
//        }

        // 限定pwm level D/A值在範圍內 0~1023
        pwmLevelDA = Math.max(pwmLevelDA, 0);
        pwmLevelDA = Math.min(pwmLevelDA, 1023);

        Timber.d("setMyCareEms: setDevPwmLevel, workload = " + uartVM.workload.get() + ", pwmLevelDA = " + pwmLevelDA);

        consoleUart.setMyCareEms(DeviceDyacoMedical.MODEL.EMS_M2, pwmLevelDA);
    }

    public void setDevResLevel() {

        if (!uartVM.isUartConnected.get()) return;

        @UartConst.DeviceStep int devStep = getDevStep();

        // 常態送最小值
        int resAd = MODE.getPwmViaLevel(WORKLOAD_MIN);

        // 送設定值僅在以下狀況
        if ((devStep == UartConst.DS_82_RESUME_RPM_COUNTER_RSP) ||
                (devStep == UartConst.DS_ECB_RUNNING) ||
                (devStep == UartConst.DS_82_PAUSE_RPM_COUNTER_RSP) ||
                (devStep == DS_ECB_PAUSE_STANDBY) ||
                (devStep == UartConst.DS_80_RUNNING_RES_RSP) ||
                (devStep == UartConst.DS_80_MOTOR_TEST_STANDBY) ||
                (devStep == UartConst.DS_80_MOTOR_TEST_RES_RSP)) {
            resAd = uartVM.at_valueAd.get();
        }

        // 限定pwm level D/A值在範圍內 0~1023
//        resAd = Math.max(resAd, 0);
//        resAd = Math.min(resAd, 1023);
//        Timber.d("setDevResLevel, resAd = " + resAd);

        // ACTION_MODE： NORMAL作動, STOP不作動
        // 後面下達的AD值, 需配合ACTION_MODE為NORMAL, 否則即使異動, 也不會作動

        Timber.d("ECB setControl, resAd = " + resAd);
        consoleUart.setControl(0,
                (devStep == UartConst.DS_ECB_ERR_OCCURRED) ?
                        DeviceDyacoMedical.ACTION_MODE.STOP :   // 拉線器發生錯誤時, 則直接設定為停止
                        DeviceDyacoMedical.ACTION_MODE.NORMAL,
                resAd,
                DeviceDyacoMedical.ACTION_MODE.STOP,
                0,
                0);
    }

    public void setDevTargetRpm(int targetRpm) {

        if (!uartVM.isUartConnected.get()) return;

        // for stepper
        // TODO: random
        int targetRpmValue = (targetRpm == -1) ? CommonUtils.getRandomValue(25, 100) : targetRpm;
        Timber.d("設定target RPM = " + targetRpmValue + " (0xA5)");
        // 限定target RPM的數值範圍
        targetRpmValue = Math.min(targetRpmValue, 255);
        targetRpmValue = Math.max(targetRpmValue, 2);
        uartVM.a5_targetRpm.set(targetRpmValue);

        Timber.d("設定target RPM (限定範圍後) = " + targetRpmValue + " (0xA5)");

        consoleUart.setTargetRpm(targetRpmValue);  // rpm的值從25~100(待確認)
    }

    public void startDevAngleSensorCalibration() {
    }

    public void setPwmViaLevel(int level) {

        // workload為LEVEL constant, 取得PWM level
        int pwmLevelDA = MODE.getPwmViaLevel(level);
        uartVM.at_valuePwm.set(pwmLevelDA);

        Timber.d("依LEVEL constant, 取得D/A Level: level = " + level + ", m_pwmLevelDA = " + pwmLevelDA);

    }

    public void setPwmViaLevelAndRpm(int level, int rpmInTime) {
        // workload為LEVEL constant, 取得PWM level
        int pwmLevelDA = MODE.getPwmViaPower(level, rpmInTime);
        uartVM.at_valuePwm.set(pwmLevelDA);

        Timber.d("依LEVEL constant, 取得D/A Level: level = " + level + ", m_pwmLevelDA = " + pwmLevelDA);
    }

    public void setPwmViaPower(int power) {

        // workload為POWER constant, 取得PWM
        power = Math.min(power, UartConst.EMS_LWR_POWER_MAX);
        power = Math.max(power, UartConst.EMS_LWR_POWER_MIN);
        int currentRpm = woVM.currentRpm.get();
        int pwmValue = MODE.getPwmViaPower(power, currentRpm);
        Timber.d("currentRpm = " + currentRpm + ", power = " + power + ", pwmValue = " + pwmValue);
        uartVM.at_valuePwm.set(pwmValue);
    }




    public void setDevRpmCounterModeEcb(DeviceDyacoMedical.RPM_COUNTER_MODE rpmCounterMode) {

        // ECB rpm counter的模式設定
        if (!uartVM.isUartConnected.get()) return;

        try {
            consoleUart.setRPMCounterMode(rpmCounterMode);
        } catch (Exception e) {

            Timber.d("rpmCounterMode = " + rpmCounterMode + ", Exception = " + e);

        }
    }

    public void setEcbRunning() {
        Timber.d("設定恢復RPM COUNTER計數 (DS_82_RESUME_RPM_COUNTER_RSP)");
        setDevStep(UartConst.DS_82_RESUME_RPM_COUNTER_RSP);
        setDevRpmCounterModeEcb(DeviceDyacoMedical.RPM_COUNTER_MODE.RESUME);
    }

    public void setEcbPause() {

        Timber.d("設定暫停RPM COUNTER計數 (DS_82_PAUSE_RPM_COUNTER_RSP)");
        setDevStep(UartConst.DS_82_PAUSE_RPM_COUNTER_RSP);
        setDevRpmCounterModeEcb(DeviceDyacoMedical.RPM_COUNTER_MODE.PAUSE);
    }

    public void setEcbIdle() {
        setPwmViaLevel(WORKLOAD_MIN);

        // 清除RPM counter
        Timber.d("設定清除RPM COUNTER(DS_82_CLEAR_RPM_COUNTER_RSP)");
        setDevStep(UartConst.DS_82_CLEAR_RPM_COUNTER_RSP);
        setDevRpmCounterModeEcb(DeviceDyacoMedical.RPM_COUNTER_MODE.CLEAR);
    }

    public void changeTrackMasterProtocol(int protocolOpt) {

    }

    public boolean isLegalWeightTM(float requestWeight) {
        return false;
    }

    public boolean isLegalHeightTM(float requestHeight) {
        return false;
    }

    public boolean isLegalAgeTM(int requestAge) {
        return false;
    }

    public boolean isLegalElevationTM(float requestElevation) {
        return false;
    }

    public boolean isLegalLevelTM(int requestLevel) {
        return false;
    }

    public boolean isLegalPowerTM(int requestPower) {
        return false;
    }

    public void getDeviceInfo() {

        if (!uartVM.isUartConnected.get()) return;

        setDevStep(UartConst.DS_99_DEV_INFO_RSP);
        consoleUart.getDeviceInfo();
    }

    public void setDevBeacon(@UartConst.BeaconColor int color) {
    }

    public void setScbWatchdog(DeviceDyacoMedical.SUB_TIMER subTimer) {
        // 東庚案無需執行此動作, 因sub板已原lwr已整合在一起

        if (!uartVM.isUartConnected.get()) return;
        Timber.d("SCB Timer = " + subTimer);
        consoleUart.setSubTimer(subTimer, 5);
    }

    public void setLwrMode(DeviceDyacoMedical.LWR_MODE lwrMode) {
        consoleUart.setLwrMode(lwrMode);
    }

    public void setUsbMode(DeviceDyacoMedical.USB_MODE usbMode) {
        if (!uartVM.isUartConnected.get()) return;

        consoleUart.setUsbMode(usbMode);
        uartVM.usbMode.set(usbMode);

        Timber.d("setUsbMode, usbMode = " + usbMode);
    }

    public void setBuzzer(DeviceDyacoMedical.BEEP beep, int second) {
//        if (dsVM.isBeep.get() && !woVM.isLongClicking.get()) {
//            consoleUart.setBuzzer(beep, second);
//        }
    }

    public void setDevEnterEngineerMode() {
    }

    public void setDevResumeIdleMode() {


        // TODO: gem3
//        if (getApp().getGem3Manager() != null) {
//            getApp().getGem3Manager().ftmsNotifyIdle();
//        }
    }

    public void setDevStartWorkout() {
        // 這裡裡依機型做設定
        if (MODE == UBE) {
            // for UBE
            setEcbRunning();
        } else {
            // for Stepper
            setDevConsoleMode(uartVM.constantType.get() == UartConst.CT_SPEED ?
                    DeviceDyacoMedical.CONSOLE_MODE.ISO : DeviceDyacoMedical.CONSOLE_MODE.NORMAL);
        }


        // TODO: gem3
//        if (getApp().getGem3Manager() != null) {
//            getApp().getGem3Manager().ftmsNotifyWorkoutStarted();
//        }

    }

    public void setDevPauseWorkout() {
        // 這裡裡依機型做設定
        if (MODE == UBE) {
            // for UBE
            setEcbPause();
        } else {
            // for Stepper
            setDevConsoleMode(DeviceDyacoMedical.CONSOLE_MODE.PAUSE);
        }


        // TODO: gem3
//        if (getApp().getGem3Manager() != null) {
//            getApp().getGem3Manager().ftmsNotifyWorkoutPaused();
//        }
    }

    public void setDevEndWorkout(boolean stoppedByUser) {


// TODO: gem3
//        if (getApp().getGem3Manager() != null) {
//            getApp().getGem3Manager().ftmsNotifyWorkoutFinish();
//        }

        Timber.d("setDevWorkoutFinish: breakWorkout = " + stoppedByUser);

        //  設定結束訓練
        if (!uartVM.isUartConnected.get()) return;
        int devStep = getDevStep();

        Timber.d("devStep: " + devStep);

        // 這裡依機型處理
        if (MODE == UBE) {
            if (devStep >= UartConst.DS_82_RESUME_RPM_COUNTER_RSP && devStep <= UartConst.DS_80_RUNNING_RES_RSP) {
                setEcbIdle();
            } else {
                Timber.d("UBE: OUT of devStep = " + devStep);
            }
        } else {
            if (devStep >= UartConst.DS_A0_RUNNING_RSP && devStep <= DS_A0_PAUSE_STANDBY) {
                setDevConsoleMode(DeviceDyacoMedical.CONSOLE_MODE.IDLE);
            } else {
                Timber.d("Stepper: OUT of devStep = " + devStep);
            }
        }
    }

    private String getErrorTypePrefix(@UartConst.ErrorType int errorType) {
        return "";
    }

    private void recordToDB(String errorCode, String errorMsg) {
    }

    public void postUartError(ErrorInfoEnum errorInfoEnum) {
        //  僅設定為實體機模式, 才發錯誤
        if (!uartVM.isUartConnected.get()) return;

    }

    private void recordToDbOnly(ErrorInfoEnum errorInfoEnum) {
    }

    public void postEvtOngoing(Bundle bundle) {
    }

    public boolean isTestProgram() {
        return false;
    }

    public boolean isHrmProgram() {
        return false;
    }

    public int getNoHeartRateTimeoutSec() {
        return 0;
    }

    @SuppressLint("SwitchIntDef")
    public void enterIdleMode() {
    }

    @Override
    public void onConnectFail() {
        uartVM.isUartConnected.set(false);
        uartVM.isUartConnected.set(false);

        Timber.d("uart connected fail");
    }

    @Override
    public void onConnected() {
        uartVM.isUartConnected.set(true);

        Timber.d("uart connected success");

//        mainActivity.initGEM3();  移到99有回覆再做


        CoTimer.after(500, () -> {
            // 於uart連接時, 再判斷一次是否已做完初始化
            if (!uartVM.isUartInitialized.get()) {
                Timber.d("onConnected startUartFlowCommand");
                startUartFlowCommand();
            }
        });
    }

    private void setLwrMcuOnApRom() {
        Timber.d("setLwrMcuOnApRom");
        LwrMcuUpdateManager lwrMcuUpdateManager = consoleUart.getLwrMcuUpdateManager();
//        由於更新完後, 模式可能在LDROM, 需切換到APROM, 才可以下達getDeviceInfo
        lwrMcuUpdateManager.connect();  // 回應connect之後, 才可以下runAprom


        // 若是在ap rom, 則會回timeout
        CoTimer.after(500, lwrMcuUpdateManager::runAprom);
    }

    @Override
    public void onDisconnected() {
        uartVM.isUartConnected.set(false);

        Timber.d("uart disconnected");
    }

    @Override
    public void onDataSend(String dataSendInHex) {
        Timber.d(">>>" + dataSendInHex);
    }

    @Override
    public void onDataReceive(String dataReceiveInHex) {
        Timber.d("<<<" + dataReceiveInHex);
        uartVM.lwrTimeoutCounter.set(0);
        uartVM.isLcbNotResponding.set(false);
    }

    @Override
    public void onErrorMessage(String errMsg) {
        Timber.d("errorMsg = " + errMsg);
    }

    @Override
    public void onCommandError(DeviceDyacoMedical.COMMAND cmd, DeviceDyacoMedical.COMMAND_ERROR cmdError) {

        Timber.d("cmdError = " + cmdError);
    }

    @Override
    public void onKeyTrigger(DeviceDyacoMedical.KEY key) {

        Timber.d("key = " + key.name());
        // 若長按後放掉, 會收到KEY_UNKNOWN 0xFF屬正常
        // 若長按後放掉, 會收到KEY_UNKNOWN 0xFF屬正常

        switch (key) {
            // 端子座向上, 由左至右
            case KEY00:
                break;    // 面板 "-"
            case KEY01:
                break;    // 面板 "PLAY/STOP"
            case KEY02:
                break;    // 面板 "ENTER"
            case KEY03:
                break;    // 面板 "+"
            case KEY04:
                break;    // 手握 "+"
            case KEY05:
                break;    // 手握 "PLAY/STOP"
            case KEY06:
                break;    // 手握 "-"
            case KEY07:
            default:
                break;
        }
    }

    @Override
    public void onMultiKey(int i, List<DeviceDyacoMedical.KEY> list) {

        if (list == null) return;

        for (DeviceDyacoMedical.KEY key : list) {
            Timber.d("multi-key = " + key.name());
        }

        // 長按
        for (DeviceDyacoMedical.KEY key : list) {

            switch (key) {
                // 端子座向上, 由左至右
                case KEY00:
                    break;      // 面板 "-"
                case KEY01:
                    break;      // 面板 "PLAY/STOP"
                case KEY02:
                    break;      // 面板 "ENTER"
                case KEY03:
                    break;      // 面板 "+"
                case KEY04:
                    break;      // 手握 "+"
                case KEY05:
                    break;      // 手握 "PLAY/STOP"
                case KEY06:
                    break;      // 手握 "-"

                case KEY07:
                default:
                    break;
            }
        }
    }


    private void longClickBeep() {
//        if (woVM.isLongClickBeep.get() && dsVM.isBeep.get()) {
//            getApp().getDeviceDyacoMedical().setBuzzer(DeviceDyacoMedical.BEEP.SHORT, 1);
//        }
//        woVM.isLongClickBeep.set(false);
//        woVM.isLongClicking.set(false);

    }


    public void postKey(int key, DeviceDyacoMedical.KEY originalKey) {
    }

    @Override
    public void onEupNotify(DeviceDyacoMedical.EUP eup) {
        Timber.d("@@@@@@@@@@@ eup = " + eup);
    }

    @Override
    public void onLwrMode(DeviceDyacoMedical.LWR_MODE lwrMode) {

        @UartConst.DeviceStep int devStep = getDevStep();

        // for rehab UBE
        if (devStep == UartConst.DS_90_NORMAL_MODE_RSP) {

            if (lwrMode == DeviceDyacoMedical.LWR_MODE.NORMAL) {
                Timber.d("LWR MODE符合, setEcbIdle");
                setEcbIdle();
            } else {
                Timber.d("LWR MODE不符, 重送指令 (DS_90_NORMAL_MODE_RSP)");
                consoleUart.setLwrMode(DeviceDyacoMedical.LWR_MODE.NORMAL);
            }
        }
    }

    @Override
    public void onSpeedCali(DeviceDyacoMedical.SPEED_CALI_STEP speedCaliStep,
                            DeviceDyacoMedical.SPEED_CALI_STATUS speedCaliStatus,
                            List<DeviceDyacoMedical.MCU_ERROR> list, int i, int i1) {
    }

    @Override
    public void onEcbAdjust(DeviceDyacoMedical.ACTION_STATUS actionStatus, int i) {
    }

    @Override
    public void onInclineAdjust(DeviceDyacoMedical.ACTION_STATUS actionStatus, int i) {
    }

    @Override
    public void onInclineRead(int i, int i1, DeviceDyacoMedical.INCLINE_READ_STATUS inclineReadStatus) {
    }

    @Override
    public void onInclineCali(DeviceDyacoMedical.INCLINE_CALI_STATUS inclineCaliStatus, int i) {
    }

    @Override
    public void onDeviceInfo(DeviceDyacoMedical.MODEL model, String subMcuFwVer, String keyStatus,
                             String lwrMcuFwVer, int hrmStatus, String subMcuHwVer, String lwrMcuHwVer) {

        // nDeviceInfo:{裝置資訊}  model = TREADMILL,
        // subMcuFwVer = V11A11, firmwareVersionInt = 11,
        // keyStatus = 5x7,
        // lwrMcuFwVer = V255A255,
        // hrmStatus = 0,
        // subMcuHwVer = 0,
        // lwrMcuHwVer = 17

        int fwVersionInt = 0;
        if (!subMcuFwVer.isEmpty()) {
            fwVersionInt = Integer.parseInt(subMcuFwVer.substring(1, subMcuFwVer.indexOf("A")));
        }

        Timber.d(
                " onDeviceInfo:{裝置資訊}" +
                        "  model = " + model +  // 在東庚案, 不具參考價值, 直接以 getDeviceInfo的第二個參數為表 (MACHINE_TYPE)
                        ", subMcuFwVer = " + subMcuFwVer +
//                        ", firmwareVersionInt = " + getInstance().m_medicalBean.getFirmwareVersionInt() +
                        ", firmwareVersionInt = " + fwVersionInt +
                        ", keyStatus = " + keyStatus +
                        ", lwrMcuFwVer = " + lwrMcuFwVer +
                        ", hrmStatus = " + hrmStatus +
                        ", subMcuHwVer = " + subMcuHwVer +
                        ", lwrMcuHwVer = " + lwrMcuHwVer);

        dsVM.subMcuFwVer.set(subMcuFwVer);
        dsVM.subMcuHwVer.set(subMcuHwVer);
        dsVM.lwrMcuFwVer.set(lwrMcuFwVer);
        dsVM.lwrMcuHwVer.set(lwrMcuHwVer);

        @UartConst.DeviceStep int devStep = getDevStep();
        int modelCode = dsVM.modelCode.get();

        Timber.d("modelCode = " + modelCode);

        if (devStep == UartConst.DS_99_UPDATE_DEV_INFO_RSP) {

            // restart APP在EMS, ECB(Rehab UBE)下, 不需通知
            Timber.d("nonMT: restart app");
            restartApp(m);
            return;
        }

        if (devStep != UartConst.DS_99_DEV_INFO_RSP)
            return;

        if (MODE == UBE) {
            if (model != DeviceDyacoMedical.MODEL.ECB) {
                Timber.d("LWR MODEL不符, 僅彈出錯誤訊息");
                if (model == DeviceDyacoMedical.MODEL.UNKNOWN) {
//                    postUartError(GENERAL.UE_UNKNOWN_MACHINE_TYPE, "", GENERAL.LC_MT_UNKNOWN);
                } else {
//                    postUartError(GENERAL.UE_MACHINE_TYPE, "", GENERAL.LC_MT_MISMATCH);
                }
                return;
            } else {
                Timber.d("LWR MODEL符合, 設定LWR在NORMAL MODE");
                setDevStep(UartConst.DS_90_NORMAL_MODE_RSP);
                //TODO: 模組符合, 設定機型
                consoleUart.setMachineType(DeviceDyacoMedical.MACHINE_TYPE.REHAB_UBE);  // 僅設定lib內的值, 不做傳送
                consoleUart.setLwrMode(DeviceDyacoMedical.LWR_MODE.NORMAL);
//              DyacoMedicalApplication.getUartConsole().setECBTimer(10);               // 設定逾時秒數, 預設為30秒
//              DyacoMedicalApplication.getUartConsole().setECBErrorTestMode(true);     // true: 於逾時時間後, 固定發送onECBError, 以供測試; false: 逾時時間過後, 狀態未在stop, 則發error
                consoleUart.setECBErrorNotify(true);     // 啟動逾時發送錯誤
            }
        } else {
            // Stepper
            if (model != DeviceDyacoMedical.MODEL.EMS_M2) {
                Timber.d("LWR MODEL不符, 僅彈出錯誤訊息");
                if (model == DeviceDyacoMedical.MODEL.UNKNOWN) {
//                 postUartError(GENERAL.UE_UNKNOWN_MACHINE_TYPE, "", GENERAL.LC_MT_UNKNOWN);
                    Timber.d("LWR MODEL不符, 僅彈出錯誤訊息");
                } else {
//                 postUartError(GENERAL.UE_MACHINE_TYPE, "", GENERAL.LC_MT_MISMATCH);
                    Timber.d("LWR MODEL不符, 僅彈出錯誤訊息");
                }
                return;
            } else {
                setEmsMachineType();
                DeviceDyacoMedical.MACHINE_TYPE devMachineType = uartVM.a0_machineType.get();
                Timber.d("LWR MODEL符合, 設定車種及目前模式 (DS_A0_IDLE_RSP), m_devMachineType = " + devMachineType + ", consoleMode = IDLE");
                setDevStep(UartConst.DS_A0_IDLE_RSP);
                consoleUart.setConsoleMode(devMachineType, DeviceDyacoMedical.CONSOLE_MODE.IDLE);
            }
        }

        Timber.d("onDeviceInfo: ");
        // 確定取得device info之後, 再初始化GEM3

        m.initGem3();
       // getApp().getGem3Manager().initialize();
    }

    public void setEmsMachineType() {
        DeviceDyacoMedical.MACHINE_TYPE devMachineType = DeviceDyacoMedical.MACHINE_TYPE.UNKNOWN;

        // 依據機型, 設定下控的對應機型
        uartVM.a0_machineType.set(MODE == UBE ?
                DeviceDyacoMedical.MACHINE_TYPE.REHAB_UBE :
                DeviceDyacoMedical.MACHINE_TYPE.RECUMBENT_STEPPER);

        Timber.d("emsMachineType = " + devMachineType);
    }

    private void setUartTimeoutControl() {
        DeviceDyacoMedical.TIMEOUT_CONTROL timeoutControl = uartVM.be_timeoutControl.get();
        DeviceDyacoMedical.TEST_CONTROL testControl = uartVM.be_testControl.get();
        consoleUart.setTimeoutControlTreadmill(timeoutControl, testControl);
    }

    @Override
    public void onMcuSetting(DeviceDyacoMedical.MODEL model, DeviceDyacoMedical.MCU_SET mcuSet) {
        Timber.d("model = " + model + ", mcuSet = " + mcuSet);
    }

    /**
     * EMS 0x80
     */
    @Override
    public void onMcuControl(
            DeviceDyacoMedical.MODEL model,
            List<DeviceDyacoMedical.MCU_ERROR> mcuErrors,  //ECB: 阻力error
            int hpHr,
            int wpHr,
            DeviceDyacoMedical.ACTION_STATUS inclineStatus,
            int inclineAd,
            DeviceDyacoMedical.SAFE_KEY safeKey,
            int speed,
            int stepCount,
            DeviceDyacoMedical.DIRECTION direction,       // ECB: 也有正反轉
            int rpm_ECB,         // ECB算熱量的
            DeviceDyacoMedical.ACTION_STATUS resStatus,       // ECB: 阻力狀態
            int resCode,         // ECB: resAD
            int rpm1_D5D6,
            int rpm2_unused,     // 已改成d5 d6: rpm1, 故原來的rpm2, 要去0xAA拿
            int pwmLevel) {      // ECB: rpmCounter

//        boolean isWirelessHRM = dsVM.isHpHr.get();  // 5K心跳
//        Timber.d("isWPHr = " + isWirelessHRM);
//        if (!isWirelessHRM) {
//            wpHr = 0;
//        }

        Timber.d(
                "\n[0x80] model = " + model +
                        "\n, mcuErrors = " + mcuErrors.toString() +
                        "\n, hpHr = " + hpHr +
                        "\n, wpHr = " + wpHr +
                        "\n, safeKey  = " + safeKey.toString() +
                        "\n, speed  = " + speed +
                        "\n, stepCount  = " + stepCount +
                        "\n, direction  = " + direction.toString() +
                        "\n, rpm_ECB  = " + rpm_ECB +
                        "\n, resStatus  = " + resStatus +
                        "\n, resCode (resAd)  = " + resCode +
                        "\n, rpm1_D5D6= " + rpm1_D5D6 +
                        "\n, rpm2_unused = " + rpm2_unused +
                        "\n, pwmLevel = " + pwmLevel);

        woVM.setHpHr(hpHr);
        woVM.setWpHr(wpHr);
//        woVM.saveKeyStatus = safeKey;
//        woVM.model = model;
//        woVM.mcuErrorList = mcuErrors;
//        woVM.woIncline.set(inclineAd);
//        woVM.woSpeed.set(speed);
//        woVM.woInclineStatus.set(inclineStatus);
//        woVM.woStepCount.set(stepCount);
//        woVM.woDirectStatus.set(direction);
//        woVM.pwmLevelDAStatus.set(resStatus);
        woVM.pwmLevelDA.set(resCode);
//        woVM.woRpm1.set(rpm1_D5D6);
//        woVM.woRpm2.set(rpm2_unused);
        woVM.rpmCounter.set(pwmLevel);
//        woVM.setFirstHeartRate();

        setFirstHeartRate();

        // for UBE
        woVM.currentRpm.set(rpm_ECB);
        if (!mcuErrors.isEmpty()) {
            if (mcuErrors.contains(DeviceDyacoMedical.MCU_ERROR.RES)) {
                setDevStep(UartConst.DS_ECB_ERR_OCCURRED);
                postUartError(ErrorInfoEnum.MCU_RES_E301);
            }
        }
        // 檢查下達的EMS指令是否已回傳
        checkEcbSteps(resCode);


        /*
        // for Stepper
        // pwmLevelAD = pwmLevel

        //  檢查下達的EMS指令是否已回傳, 否則重送指令
        checkEmsSteps(pwmLevel);

         */
    }


    private void setFirstHeartRate() {
        //Garmin-->BLE-->ANT+-->無線心跳-->手握心跳；
        //HR 優先權  : HRS --> WP(無線心跳) --> HP(手握)
        int currentHr;
        if (woVM.getGarminHr() > 0) { //garmin
            currentHr = woVM.getGarminHr();
            woVM.setBleHr(0);
            woVM.setWpHr(0);
            woVM.setHpHr(0);
        } else {
            if (woVM.getBleHr() > 0) { //ble
                currentHr = woVM.getBleHr();
                woVM.setWpHr(0);
                woVM.setHpHr(0);
            } else {
                if (woVM.getWpHr() > 0) {
                    currentHr = woVM.getWpHr();
                    woVM.setHpHr(0);
                } else {
                    currentHr = Math.max(woVM.getHpHr(), 0);
                }
            }
        }


        if (woVM.getSamsungWatchHr() > 0 && woVM.isSamsungWatchEnabled.get()) {
            currentHr = woVM.getSamsungWatchHr();
        }

        //todo APPLE WATCH
        if (woVM.getAppleWatchHr() > 0 && woVM.isAppleWatchEnabled.get()) {
            currentHr = woVM.getAppleWatchHr();
            //w.setBleHr(0);
//            w.setWpHr(0);
//            w.setHpHr(0);
//            w.setGarminHr(0);
        }

        //   LogUtil.d("GEM3", "setFirstHeartRate: " + w.getAppleWatchHr());

        //   LogUtil.d(TAG, "setFirstHeartRate: " + w.getGarminHr() + "," + w.getBleHr() + "," + w.getWpHr() + "," + w.getHpHr());

        woVM.currentHeartRate.set(currentHr);

        // TODO:USSSSSS
        //   w.currentHeartRate.set(80);

        woVM.setIsHrConnected(woVM.currentHeartRate.get() > 0);
    }

    @SuppressLint("SwitchIntDef")
    private void checkTreadmillSteps(
            DeviceDyacoMedical.MAIN_MODE mainMode,
            DeviceDyacoMedical.INCLINE_STATUS frontGradeStatus,
            DeviceDyacoMedical.INCLINE_STATUS rearGradeStatus,
            DeviceDyacoMedical.INIT_STATUS initStatus,
            DeviceDyacoMedical.INVERTER_ERROR inverterErr,
            List<DeviceDyacoMedical.BRIDGE_ERROR> bridgeErr,
            DeviceDyacoMedical.TREADMILL_CALI_STATUS caliStatus,
            DeviceDyacoMedical.SAFE_KEY safeKey,
            int rpm,
            int frontGradeAd,
            int rearGradeAd) {

    }

    public boolean isWaitSpeedZero() {
        return m_waitSpeedZero;
    }

    public void setWaitSpeedZero(boolean m_waitSpeedZero) {
        this.m_waitSpeedZero = m_waitSpeedZero;
    }

    @SuppressLint("SwitchIntDef")
    private void checkEcbSteps(int resAd) {

        // Rehab UBE only
        //  檢查下達的ECB指令是否已回傳, 否則重送指令
//        Timber.d("checkEcbSteps, dev_step = " + uartVM.getStepString());
        @UartConst.DeviceStep int devStep = getDevStep();
        int valueAD = uartVM.at_valueAd.get();
        switch (devStep) {

            case UartConst.DS_80_MOTOR_TEST_RES_RSP:
                if (Math.abs(resAd - valueAD) <= 10) {  // 設定誤差在10以內, 表示到達設定值
                    Timber.d("設定Resistance AD符合(誤差值在10以內), m_resAd = " + resAd);
                    setDevStep(UartConst.DS_80_MOTOR_TEST_STANDBY);
                }
//                else {
//                    Timber.d("設定Resistance AD不符, resAd = " + resAd + ", 重送指令");
//                    Timber.d("設定Resistance AD不符, resAd = " + resAd);
//                    getInstance().setDevResLevel();  // (已定時傳送, 不必再重送指令)
//                }
                break;

            case UartConst.DS_80_RUNNING_RES_RSP:
                if (Math.abs(resAd - valueAD) <= 10) {  // 設定誤差在10以內, 表示到達設定值
                    Timber.d("設定Resistance AD符合(誤差值在10以內), m_resAd = " + resAd);
                    setDevStep(UartConst.DS_ECB_RUNNING);
                    LiveEventBus.get(UartConst.EVT_RES_DONE).post(true);
                }
//                else {
//                    Timber.d("設定Resistance AD不符, resAd = " + resAd + ", 重送指令");
//                    Timber.d("設定Resistance AD不符, resAd = " + resAd);
//                    getInstance().setDevResLevel();  // (已定時傳送, 不必再重送指令)
//                }
                break;

            case UartConst.DS_82_CLEAR_RPM_COUNTER_RSP:
                Timber.d("尚未收到設定 清除rpm counter 指令, 重送指令");
                setDevRpmCounterModeEcb(DeviceDyacoMedical.RPM_COUNTER_MODE.CLEAR);
                break;

            case UartConst.DS_82_RESUME_RPM_COUNTER_RSP:
                Timber.d("尚未收到設定 恢復rpm counter 指令, 重送指令");
                setDevRpmCounterModeEcb(DeviceDyacoMedical.RPM_COUNTER_MODE.RESUME);
                break;

            case UartConst.DS_82_PAUSE_RPM_COUNTER_RSP:
                Timber.d("尚未收到設定 暫停rpm counter 指令, 重送指令");
                setDevRpmCounterModeEcb(DeviceDyacoMedical.RPM_COUNTER_MODE.PAUSE);
                break;

            case UartConst.DS_90_NORMAL_MODE_RSP:
                Timber.d("尚未收到設定LWR MODE指令, 重送指令");
                consoleUart.setLwrMode(DeviceDyacoMedical.LWR_MODE.NORMAL);
                break;
        }
    }

    @SuppressLint("SwitchIntDef")
    private void checkEmsSteps(int pwmLevel) {

        Timber.d("checkEmsSteps, m_devStep = " + uartVM.getStepString());

        DeviceDyacoMedical.MACHINE_TYPE devMachineType = uartVM.a0_machineType.get();
        @UartConst.DeviceStep int devStep = getDevStep();
        int valuePWM = uartVM.at_valuePwm.get();

        // 檢查下達的EMS指令是否已回傳, 否則重送指令
        switch (devStep) {

            case UartConst.DS_80_RUNNING_PWM_RSP:
                if (pwmLevel == valuePWM) {
                    Timber.d("設定PWM LEVEL DA符合, valuePWM = " + valuePWM);
                    setDevStep(UartConst.DS_EMS_RUNNING_STANDBY);
                } else {
                    Timber.d("設定PWM LEVEL DA不符, valuePWM = " + valuePWM + ", 重送指令");
//                    setDevPwmLevel(); // pwm的值從0~65535 (已定時傳送, 不必再重送指令)
                }
                break;

            case UartConst.DS_99_DEV_INFO_RSP:
                Timber.d("尚未收到裝置資訊, 重送指令");
                consoleUart.getDeviceInfo();
                break;

            case UartConst.DS_A0_IDLE_RSP:
                Timber.d("尚未進入IDLE模式, 重送指令, devMachineType = " + devMachineType + ", consoleMode = " + DeviceDyacoMedical.CONSOLE_MODE.IDLE);
                consoleUart.setConsoleMode(devMachineType, DeviceDyacoMedical.CONSOLE_MODE.IDLE);
                break;

            case UartConst.DS_A0_RUNNING_RSP:
                @UartConst.ConstantType int constantType = uartVM.constantType.get();
                DeviceDyacoMedical.CONSOLE_MODE devConsoleMode = (constantType == UartConst.CT_SPEED) ?
                        DeviceDyacoMedical.CONSOLE_MODE.ISO : DeviceDyacoMedical.CONSOLE_MODE.NORMAL;

                uartVM.a0_consoleMode.set(devConsoleMode);
                Timber.d("設定車種及目前模式 (DS_A0_RUNNING_RSP), 重送指令, devMachineType = " + devMachineType +
                        ", consoleMode = " + devConsoleMode);
                consoleUart.setConsoleMode(devMachineType, devConsoleMode);
                break;

            case UartConst.DS_A0_PAUSE_RSP:
                //  目前下控無此模式(lib有), 故先略過
//                Timber.d("設定PAUSE模式, 重送指令");
//                m_uartConsole.setConsoleMode(m_devMachineType, m_devConsoleMode);  // rpm的值從25~100(待確認)
                setDevStep(DS_A0_PAUSE_STANDBY);
                break;

            case UartConst.DS_A5_RUNNING_RPM_RSP:
                int targetRpmValue = uartVM.a5_targetRpm.get();
                Timber.d("設定target RPM = " + targetRpmValue + ", 重送指令");
                setDevTargetRpm(targetRpmValue);
                break;

            case UartConst.DS_A8_BRAKE_MODE_RSP:
                DeviceDyacoMedical.BRAKE_MODE brakeMode = uartVM.a8_brakeMode.get();
                Timber.d("設定BRAKE MODE (DS_A8_BRAKE_MODE_RSP)未回應, 重送指令, brakeMode =" + brakeMode);
                consoleUart.setBrakeMode(brakeMode);
                break;

            case UartConst.DS_A9_SYMM_ANGLE_RSP:
                int anglePositiveA9 = uartVM.a9_anglePositive.get();
                int angleNegativeA9 = uartVM.a9_angleNegative.get();
                Timber.d("設定SYMMETRY ANGLE (DS_A9_SYMM_ANGLE_RSP), 重送指令, anglePositiveA9 =" + anglePositiveA9 + ", angleNegativeA9 = " + angleNegativeA9 + " (0xA9)");
                setSymmetryAngle(anglePositiveA9, angleNegativeA9);
                break;

            case UartConst.DS_FA_UPDATE_RESET_RSP:
                Timber.d("尚未收到LWR重置指令, 重送指令");
                consoleUart.setResetTreadmill();
                break;
        }
    }

    @Override
    public void onEchoMode(DeviceDyacoMedical.ECHO_MODE echoMode) {
        Timber.d("echoMode = " + echoMode);
    }

    @Override
    public void onEEPRomWrite(DeviceDyacoMedical.MCU_SET mcuSet) {
        Timber.d("mcuSet = " + mcuSet);
    }

    @Override
    public void onEEPRomRead(DeviceDyacoMedical.MCU_GET mcuGet, byte[] bytes, byte[] bytes1) {
        Timber.d("mcuGet = " + mcuGet);
        if (bytes != null) {
            for (byte b : bytes) {
                Timber.d("byte = " + b);
            }
        }
        if (bytes1 != null) {
            for (byte b1 : bytes1) {
                Timber.d("byte1 = " + b1);
            }
        }
    }

    @Override
    public void onUsbModeSet(DeviceDyacoMedical.MCU_SET mcuSet) {

        Timber.tag("#UART_CONSOLE").d("onUsbModeSet: %s", mcuSet + "," + uartVM.usbMode.get());


        boolean done = mcuSet.equals(DeviceDyacoMedical.MCU_SET.OK);

        if (done) {
            if (uartVM.usbMode.get() == DeviceDyacoMedical.USB_MODE.DATA) {
                LiveEventBus.get(UartConst.EVT_USB_MODE_DATA).post(true);
            }
        } else {
            Toast.makeText(getApp(), "USB_MODE_ERROR", Toast.LENGTH_LONG).show();
        }

        Timber.d("mcuSet = " + mcuSet);
    }

    @Override
    public void onHeartRateMode(DeviceDyacoMedical.HEART_RATE_MODE heartRateMode) {
        Timber.d("heartRateMode = " + heartRateMode);
    }

    @SuppressLint("SwitchIntDef")
    @Override
    public void onRpmCounterMode(DeviceDyacoMedical.RPM_COUNTER_MODE rpmCounterMode) {
        Timber.d("rpmCounterMode = " + rpmCounterMode);  // clear / resume / pause
        @UartConst.DeviceStep int devStep = getDevStep();

        switch (devStep) {
            case UartConst.DS_82_CLEAR_RPM_COUNTER_RSP:
                if (rpmCounterMode == DeviceDyacoMedical.RPM_COUNTER_MODE.CLEAR) {
                    Timber.d("已接收 清除RPM COUNTER 符合, 設定進入待命中 (DS_ECB_IDLE_STANDBY)");
                    setDevStep(DS_ECB_IDLE_STANDBY);
                }
                break;

            case UartConst.DS_82_RESUME_RPM_COUNTER_RSP:
                if (rpmCounterMode == DeviceDyacoMedical.RPM_COUNTER_MODE.RESUME) {
                    setDevStep(UartConst.DS_ECB_RUNNING);
                }
                break;

            case UartConst.DS_82_PAUSE_RPM_COUNTER_RSP:
                if (rpmCounterMode == DeviceDyacoMedical.RPM_COUNTER_MODE.PAUSE) {
                    Timber.d("已接收 暫停RPM COUNTER計數 符合, 設定進入待命中 (DS_ECB_PAUSE_STANDBY)");
                    setDevStep(DS_ECB_PAUSE_STANDBY);

                }
                break;
        }
    }

    public int getCmdWorkload(int modelCode,
                              @UartConst.ConstantType int constantType, int workload, boolean modeDps) {

        int cmdWorkload;

        if (constantType != UartConst.CT_SPEED)
            return workload;

        cmdWorkload = modeDps ? UnitConv.dpsToRpm(workload) : workload;

        Timber.d("cmdWorkload = " + cmdWorkload);

        return cmdWorkload;
    }

    @Override
    public void onConsoleMode(DeviceDyacoMedical.MACHINE_TYPE machineType, DeviceDyacoMedical.CONSOLE_MODE consoleMode) {

        //  若機型不符, 會直接在 onDeviceInfo處理, 這裡回覆的machineType, 直接視為相符, 不再做判斷
        Timber.d("machineType = " + machineType + ", consoleMode = " + consoleMode);
        @UartConst.DeviceStep int devStep = getDevStep();
        switch (devStep) {
            case UartConst.DS_A0_SENSOR_TEST_RSP:
                if (consoleMode == DeviceDyacoMedical.CONSOLE_MODE.SENSOR_TEST) {
                    setDevStep(UartConst.DS_A0_SENSOR_TEST_STANDBY);
                    Timber.d("CONSOLE MODE符合, SENSOR TEST中");
                } else {
                    Timber.d("CONSOLE MODE不符(DS_A0_SENSOR_TEST_STANDBY), 重送指令");
                    setDevConsoleMode(DeviceDyacoMedical.CONSOLE_MODE.SENSOR_TEST);
                }
                break;

            case UartConst.DS_A0_CALIBRATION_RSP:
                if (consoleMode == DeviceDyacoMedical.CONSOLE_MODE.SENSOR_CALI) {
                    setDevStep(UartConst.DS_ANGLE_CALI_STANDBY);
                    Timber.d("CONSOLE MODE符合, 校正待命中");
                } else {
                    Timber.d("CONSOLE MODE不符(DS_A0_CALIBRATION_RSP), 重送指令");
                    setDevConsoleMode(DeviceDyacoMedical.CONSOLE_MODE.SENSOR_CALI);
                }
                break;

            case UartConst.DS_A0_IDLE_RSP:
                if (consoleMode == DeviceDyacoMedical.CONSOLE_MODE.IDLE) {
                    setDevStep(DS_EMS_IDLE_STANDBY);
                    Timber.d("CONSOLE MODE符合, IDLE待命中");
                } else {
                    Timber.d("CONSOLE MODE不符 (DS_A0_IDLE_RSP), 重送指令");
                    setDevConsoleMode(DeviceDyacoMedical.CONSOLE_MODE.IDLE);
                }
                break;

            case UartConst.DS_A0_RUNNING_RSP:
                if (consoleMode == uartVM.a0_consoleMode.get()) {
                    Timber.d("CONSOLE MODE符合, 進入RUNNING");
                    setDevStep(UartConst.DS_EMS_RUNNING_STANDBY);
                } else {
                    Timber.d("CONSOLE MODE不符, 重送指令");
                    Timber.d("setEmsConsoleMode");
                    setEmsConsoleMode();
                }
                break;

            case UartConst.DS_A0_PAUSE_RSP:
                if (consoleMode == DeviceDyacoMedical.CONSOLE_MODE.PAUSE) {
                    Timber.d("CONSOLE MODE符合, 已進入PAUSE模式");
                    setDevStep(UartConst.DS_A0_PAUSE_RSP);
                } else {
                    Timber.d("CONSOLE MODE不符, consoleMode = " + consoleMode + ", 重送指令");
                    setDevConsoleMode(DeviceDyacoMedical.CONSOLE_MODE.PAUSE);
                }
                break;
            default:
                break;
        }
    }

    private void setEmsConsoleMode() {
        DeviceDyacoMedical.MACHINE_TYPE devMachineType = uartVM.a0_machineType.get();
        DeviceDyacoMedical.CONSOLE_MODE devConsoleMode = (uartVM.constantType.get() == UartConst.CT_SPEED) ?
                DeviceDyacoMedical.CONSOLE_MODE.ISO : DeviceDyacoMedical.CONSOLE_MODE.NORMAL;
        setDevConsoleMode(devConsoleMode);

        Timber.d("設定車種及目前模式 (DS_A0_RUNNING_RSP), 重送指令, devMachineType = " + devMachineType +
                ", consoleMode = " + devConsoleMode);

        consoleUart.setConsoleMode(devMachineType, devConsoleMode);
    }

    private void setSymmetryAngle(int positive, int negative) {
    }

    @Override
    public void onBrakeStatus(DeviceDyacoMedical.BRAKE_STATUS brakeStatus) {
    }

    @Override
    public void onTargetRpm(int targetRpm) {
        int targetRpmValue = uartVM.a5_targetRpm.get();
        Timber.d("[0xA5] m_targetRpm = " + targetRpmValue);

        @UartConst.DeviceStep int devStep = getDevStep();

        if ((devStep >= UartConst.DS_A0_RUNNING_RSP) && (devStep <= UartConst.DS_A0_PAUSE_RSP)) {
            if (targetRpm == targetRpmValue) {
                Timber.d("target RPM符合, targetRpm = " + targetRpm);
                setDevStep(UartConst.DS_EMS_RUNNING_STANDBY);
            } else {
                Timber.d("target RPM不符, targetRpm = " + targetRpm + ", 重送指令");
                setDevTargetRpm(targetRpmValue);
            }
        } else {
            Timber.d("非在DS_A0_RUNNING_RSP與DS_A0_PAUSE_RSP之間, 忽略不處理");
        }
    }

    @Override
    public void onCurrentRpm(int rpmL, int rpmR, int revolution, int isoPwmL, int isoPwmR) {
        Timber.d("rpmL = " + rpmL +
                ", rpmR = " + rpmR +
                ", revolution = " + revolution +
                ", isoPwmL = " + isoPwmL +
                ", isoPwmL = " + isoPwmL +
                ", isoPwmR = " + isoPwmR);

        uartVM.a6_rpmL.set(rpmL);   // stepper
        uartVM.a6_rpmR.set(rpmR);   // stepper
        uartVM.a6_revolution.set(revolution);  // N/A
        uartVM.a6_iso_pwmL.set(isoPwmL);    // workload是ISO, 才以此值為查表依據
        uartVM.a6_iso_pwmR.set(isoPwmR);    // workload是ISO, 才以此值為查表依據

    }

    @Override
    public void onAngleCali(DeviceDyacoMedical.ANGLE_CALI_STATUS angleCaliStatus) {

    }

    @Override
    public void onBrakeMode(DeviceDyacoMedical.BRAKE_MODE brakeMode) {
    }

    @Override
    public void onAngleSetting(int positive, int negative) {

    }

    @Override
    public void onCurrentStepCount(int stepPulseCount1, int stepPulseCount2, int newStep) {

        // 此callback, 適用Stepper

        // Maintenance mode: brake & step sensors
        uartVM.ad_stepPulseCount1.set(stepPulseCount1);  // (0xAD_D0)
        uartVM.ad_stepPulseCount2.set(stepPulseCount2);  // (0xAD_D1)

        // Running:
        // New Step, 0~255 (Step累加溢位為0)
        // Ex: 如果New Step的舊值是100, 新值是103,
        //     已累計的總步數是3000
        //     則 新總步數 = 3000 + (103 - 100) = 3003
        uartVM.ad_newStep.set(newStep);
    }


    /**
     * ECB 0x80
     */
    @Override
    public void onEchoTreadmill(
            DeviceDyacoMedical.MAIN_MODE mainMode,
            DeviceDyacoMedical.INIT_STATUS initStatus,
            DeviceDyacoMedical.INVERTER_ERROR frequencyInverterErr,
            List<DeviceDyacoMedical.BRIDGE_ERROR> bridgeErrs,
            int handheldHrm,
            int wirelessHrm,
            DeviceDyacoMedical.SAFE_KEY safeKey,
            int rpm,   // 20230509 實際值是 rpm * 0.1
            DeviceDyacoMedical.INCLINE_STATUS frontGradeStatus,
            DeviceDyacoMedical.INCLINE_STATUS rearGradeStatus,
            int amp,
            DeviceDyacoMedical.TREADMILL_CALI_STATUS caliStatus,
            int frontGradeAdInCalibration,   // 校正才會傳
            int rearGradeAdInCalibration,    // 校正才會傳
            int sensor1Ad,
            int sensor2Ad) {

    }

    @Override
    public void onSettingSpeedAndInclineTreadmill(DeviceDyacoMedical.MOTOR_DIRECTION motorDirection, int i, int i1, int i2) {
    }

    @SuppressLint("SwitchIntDef")
    @Override
    public void onCurrentSpeedAndInclineTreadmill(DeviceDyacoMedical.MOTOR_DIRECTION motorDir, int speedValue, int frontGradeAd, int rearGradeAd) {
    }

    @Override
    public void onUnitTreadmill(DeviceDyacoMedical.UNIT unit) {
    }

    @Override
    public void onStepInfoTreadmill(int symmetry, int leftSteps, int rightSteps, int leftLen, int rightLen, int cadence) {
    }

    @SuppressLint("SwitchIntDef")
    @Override
    public void onTargetSpeedAndInclineTreadmill(DeviceDyacoMedical.MOTOR_DIRECTION motorDir, int speed, int frontGradeAd, int rearGradeAd,
                                                 DeviceDyacoMedical.SETTING_ERROR speedStatus, DeviceDyacoMedical.SETTING_ERROR inclineStatus, DeviceDyacoMedical.SETTING_ERROR declineStatus) {
    }

    @Override
    public void onTargetSpeedRateTreadmill(int accRate, int decRate) {
    }


    @Override
    public void onMainModeTreadmill(DeviceDyacoMedical.MAIN_MODE mainMode) {

    }

    @Override
    public void onSpeedAndInclineStopTreadmill(
            DeviceDyacoMedical.STOP_STATUS speedStatus,
            DeviceDyacoMedical.STOP_STATUS inclineStatus,
            DeviceDyacoMedical.STOP_STATUS declineStatus) {
    }

    @Override
    public void onCurrentInclineAndSensorTreadmill(int frontGradeAd, int rearGradeAd, int leftStepRawData, int rightStepRawData) {
    }

    @Override
    public void onBrakeModeTreadmill(DeviceDyacoMedical.BRAKE_MODE brakeMode) {
    }

    @SuppressLint("SwitchIntDef")
    @Override
    public void onInclineCaliModeTreadmill(DeviceDyacoMedical.TREADMILL_CALI_MODE treadmillCaliMode) {
    }

    @Override
    public void onDefaultSpeedRateTreadmill(int accSpeedRate, int decSpeedRate) {
    }

    @Override
    public void onInclineRangeTreadmill(int frontGradeMax, int frontGradeMin, int rearGradeMax, int rearGradeMin) {
    }

    @Override
    public void onTimeoutControlTreadmill(DeviceDyacoMedical.TIMEOUT_CONTROL timeOutCtrl, DeviceDyacoMedical.TEST_CONTROL testCtrl) {
    }

    @Override
    public void onErrorClearTreadmill(DeviceDyacoMedical.ERROR_STATUS errStatus1, DeviceDyacoMedical.ERROR_STATUS errStatus2) {
        // 0xA5: 清除error code
        // 0xFF: 表示控制碼不對
        Timber.d("[0xBF] 已接收清除錯誤指令, errStatus1 = " + errStatus1 + ", errStatus2 = " + errStatus2);
    }

    @Override
    public void onResetTreadmill(DeviceDyacoMedical.RESET_STATUS resetStatus) {
    }

    @Override
    public void onResetToBootloaderTreadmill(DeviceDyacoMedical.RESET_STATUS resetStatus) {
        // 這個需下達2個指令
        // 此回應不表示已經切換至LDROM, 需connect有回應, 才能表示切到LDROM
        Timber.d("已接收LWR重置後停在LDROM, resetStatus = " + resetStatus);
    }

    @Override
    public void onSafeStepTreadmill(int stepSpeed, int stepLength, DeviceDyacoMedical.BRAKE_MODE beltBrakeMode) {

        /*
            stepSpeed: 回傳 0xffff 時，表示上表下的 SPD 值超過最大值，或 MainMode不對，不執行此 SPD。
            stepLength: 回傳 0xffff 時，表示上表下的 length 值超過最大值，或MainMode不對，不執行此 length。
            beltBrakeMode: lock belt_data = 0    => lock belt ON
                                          = 0xA5 => lock belt OFF
                                          = 0xFF => 表示控制碼不對
         */
    }

    @Override
    public void onStepPerMin(int spm, int rpm2_D2D3) {

        // WORKOUT
        // for stepper, SPM (step per minute) value
        uartVM.aa_spm.set(spm);           // spm
        uartVM.aa_rpm.set(rpm2_D2D3);     // current RP<
        woVM.currentRpm.set(rpm2_D2D3);        // current RPM

        // MAINTENANCE MODE
        // for stepper, Sensor Test - Pulley RPM Optical Sensor的數值
        uartVM.aa_stepPulleyRpmOpticalSensor.set(rpm2_D2D3);
    }

    @Override
    public void onSensorState(int pulseCount, DeviceDyacoMedical.SENSOR hallSensorStatus) {
    }

    @Override
    public void onPowerDistribution(int angleIndex, int rpm, int pwm, int shiftAngle) {

    }

    @Override
    public void onSubTimer(DeviceDyacoMedical.SUB_TIMER subTimer, int second) {
        // sub板重啟
        if (subTimer == DeviceDyacoMedical.SUB_TIMER.RESTART)
            Timber.d("sub rebooted");
    }

    @Override
    public void onECBError(int targetResAd, int currentResAd) {
        Timber.d("targetResAd = " + targetResAd + ", currentResAd = " + currentResAd);

        // 由lib判斷逾時未到達指定的AD值
        setDevStep(UartConst.DS_ECB_ERR_OCCURRED);
        // Motor Error
//        postUartError(ErrorInfoEnum.APP_MOTOR_E402);
    }

    @Override
    public void onGetTime(int[] ints) {

    }

    @Override
    public void onSetTime(DeviceDyacoMedical.STATUS status) {

    }

    public @UartConst.DeviceStep int getDevStep() {
        return uartVM.devStep.get();
    }

    public void setDevStep(@UartConst.DeviceStep int devStep) {
        uartVM.devStep.set(devStep);

        // 可按[Quick Start], [Start this Program]的時機
        uartVM.isStartWorkoutReady.set(
                devStep == DS_ECB_IDLE_STANDBY ||
                        devStep == DS_EMS_IDLE_STANDBY);

        uartVM.isEnterRunningReady.set(
                devStep == UartConst.DS_ECB_RUNNING ||
                        devStep == UartConst.DS_EMS_RUNNING_STANDBY);

        uartVM.isResumeWorkoutReady.set(
                devStep == DS_A0_PAUSE_STANDBY ||
                        devStep == DS_ECB_PAUSE_STANDBY);
    }
}
