package com.dyaco.spirit_commercial;

import static com.dyaco.spirit_commercial.UartConst.CALI_NA;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.databinding.ObservableList;
import androidx.lifecycle.AndroidViewModel;

import com.corestar.libs.device.DeviceDyacoMedical;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class UartVM extends AndroidViewModel {


    public UartVM(@NonNull Application application) {
        super(application);

    }

    public final ObservableBoolean ad_hallSensorStatus = new ObservableBoolean(false);
    public final ObservableInt ad_pulseCount = new ObservableInt(0);

    public final ObservableBoolean isExStart = new ObservableBoolean(false);
    public final ObservableBoolean isExStop = new ObservableBoolean(false);

    // 拒絕所有指令: 在做 sub更新時, 需停止心跳包的傳送, 以避免接收到下控傳來的error
    public final ObservableBoolean stopHeartbeat = new ObservableBoolean(false);

    public final ObservableField<DeviceDyacoMedical.MCU_SET> usbSet = new ObservableField<>();
    public final ObservableField<DeviceDyacoMedical.USB_MODE> usbMode = new ObservableField<>();
    // 進入IDLE是否準備就緒: 從ENG(工程模式)切換到IDLE, 是否就緒 (可不可以按button)
//    public final ObservableBoolean isEnterIdleReady = new ObservableBoolean(false);

    // 下控已切換至RUNNING ready, 可以設定速度. 揚升. level
    public final ObservableBoolean isEnterRunningReady = new ObservableBoolean(false);

    // 進入ENG是否準備就緒: 要做service相關測試時, 需確認已切換到ENG, 才可以開始測試
    public final ObservableBoolean isEnterMaintenanceReady = new ObservableBoolean(false);

    // 進入RUNNING是否準備就緒: 是否可按[Quick Start]. [Start this Program]
//    public final ObservableBoolean isStartWorkoutReady = new ObservableBoolean(false);

    // 於Paused頁面, 是否可按 [Resume]: 電跑需等待跑帶停止才可以再按Resume
    public final ObservableBoolean isResumeWorkoutReady = new ObservableBoolean(false);

    // UART已連接
    public final ObservableBoolean isUartConnected = new ObservableBoolean(false);

    // 開始執行UART流程
    public final ObservableBoolean isStartUartFlow = new ObservableBoolean(false);

    // 忽略來自LWR的所有錯誤
    public final ObservableBoolean ignoreUartError = new ObservableBoolean(false);

    // UART已完成初始化
    public final ObservableBoolean isUartInitialized = new ObservableBoolean(false);

    // 下控未回應flag, counter
    public final ObservableBoolean isGradeError = new ObservableBoolean(false);
    public final ObservableBoolean isLcbNotResponding = new ObservableBoolean(false);
    public final ObservableBoolean isFrontGradeError = new ObservableBoolean(false);
    public final ObservableBoolean isRearGradeError = new ObservableBoolean(false);
    public final ObservableBoolean isGradeStop = new ObservableBoolean(true);
    public final ObservableInt lwrTimeoutCounter = new ObservableInt(0);
    public final ObservableInt at_valuePwm = new ObservableInt(0);  // 0~1023 (command 80
    public final ObservableInt at_valueAd = new ObservableInt(0);  // resistance AD (command 80, ECB)
    public final ObservableInt workload = new ObservableInt(0);  // 依據 constantType代表數值不同 (target speed, power, level)
    public final ObservableInt a6_rpmL = new ObservableInt(0);  // 左側RPM
    public final ObservableInt a6_rpmR = new ObservableInt(0);  // 右側RPM
    public final ObservableInt a6_revolution = new ObservableInt(0);  // 圈數
    public final ObservableInt a6_iso_pwmL = new ObservableInt(0);  // 左側 ISO PWM數值
    public final ObservableInt a6_iso_pwmR = new ObservableInt(0);  // 右側 ISO PWM數值
    public final ObservableInt b0_currentRpmTCR = new ObservableInt(0);  // 即時RPM
    public final ObservableInt b0_electricCurrentTCR = new ObservableInt(0);  // 電流, 用在Settings-Drive Motor Test
    public final ObservableInt b0_frontGradeAd = new ObservableInt(0);  // 出廠模式-前揚升校正進行中的AD值
    public final ObservableInt b0_rearGradeAd = new ObservableInt(0);  // 出廠模式-後揚升校正進行中的AD值
    public final ObservableInt b5_frontGradeAd = new ObservableInt(0);  // 設定前揚升的ad值
    public final ObservableInt b5_frontGradeIndex = new ObservableInt(0);  // 設定前揚升對應的index (產生的揚升ad表)
    public final ObservableInt b5_rearGradeAd = new ObservableInt(0);  // 設定後揚升的ad值
    public final ObservableInt b5_rearGradeAdIndex = new ObservableInt(0);  // 設定後揚升對應的index (產生的揚升ad表)
    public final ObservableInt b5_speed = new ObservableInt(0);  // 設定跑帶速度 (resolution 0.1)


    public final ObservableInt ad_stepPulseCount1 = new ObservableInt(0);
    public final ObservableInt ad_stepPulseCount2 = new ObservableInt(0);
    public final ObservableInt ad_newStep = new ObservableInt(0);
    public final ObservableInt aa_spm = new ObservableInt(0);
    public final ObservableInt aa_rpm = new ObservableInt(0);
    public final ObservableInt aa_stepPulleyRpmOpticalSensor = new ObservableInt(0);

    public final ObservableInt devStep = new ObservableInt(0);  // UART流程
    public final ObservableInt lastDevStep = new ObservableInt(0);  // UART流程
    public final ObservableInt calibrationType = new ObservableInt(CALI_NA);  // 校正類型

    public final ObservableField<DeviceDyacoMedical.TIMEOUT_CONTROL> be_timeoutControl = new ObservableField<>();       // uart逾時通知
    public final ObservableField<DeviceDyacoMedical.TEST_CONTROL> be_testControl = new ObservableField<>();             // uart測試模式 (true: 只有接sub板, Device = DEVICE_TEST)
    public final ObservableField<DeviceDyacoMedical.INCLINE_STATUS> b0_gradeStatus = new ObservableField<>();           // 整合前後揚升的狀態
    public final ObservableField<DeviceDyacoMedical.INCLINE_STATUS> b0_frontGradeStatus = new ObservableField<>();     // 前揚升狀態
    public final ObservableField<DeviceDyacoMedical.INCLINE_STATUS> b0_rearGradeStatus = new ObservableField<>();      // 後揚升狀態
    public final ObservableField<DeviceDyacoMedical.MAIN_MODE> b7_mainMode = new ObservableField<>();       // 下控main mode
    public final ObservableField<DeviceDyacoMedical.SAFE_KEY> b0_safeKeyStatus = new ObservableField<>();   // 安全鎖狀態
    public final ObservableField<DeviceDyacoMedical.UNIT> b3_unit = new ObservableField<>(DeviceDyacoMedical.UNIT.METRIC);                // 單位設定
    public final ObservableField<DeviceDyacoMedical.BRAKE_MODE> ba_brakeMode = new ObservableField<>();     // 電跑, 是否要啟動剎車(auto brake)
    public final ObservableField<DeviceDyacoMedical.BRAKE_MODE> a8_brakeMode = new ObservableField<>();     // 非電跑, 設定console回到idle時, 是否要啟動剎車(auto brake)
    public final ObservableField<DeviceDyacoMedical.BRAKE_MODE> a3_brakeStatus = new ObservableField<>();     // 剎車狀態
    public final ObservableField<DeviceDyacoMedical.CONSOLE_MODE> a0_consoleMode = new ObservableField<>();     // 剎車狀態
    public final ObservableField<DeviceDyacoMedical.MACHINE_TYPE> a0_machineType = new ObservableField<>();     // 剎車狀態

    public final ObservableInt a5_targetRpm = new ObservableInt();  // 25~100
    public final ObservableInt a9_anglePositive = new ObservableInt(140);  // 設定正踩角度
    public final ObservableInt a9_angleNegative = new ObservableInt(140);  // 設定反踩角度
    public final ObservableInt b9_frontGradeTCR = new ObservableInt(0);  // 前揚升AD
    public final ObservableInt b9_rearGradeTCR = new ObservableInt(0);  // 後揚升AD
    public final ObservableInt b9_leftSensorTCR = new ObservableInt(0);  // left step sensor
    public final ObservableInt b9_rightSensorTCR = new ObservableInt(0);  // right step sensor
    public final ObservableInt at_rpmBRE200 = new ObservableInt(0);  // for ECB
    public final ObservableInt at_rpmCounterBRE200 = new ObservableInt(0);  // for ECB
    public final ObservableInt at_resAdBRE200 = new ObservableInt(0);  // for ECB
    public final ObservableInt at_pwmLevelBike = new ObservableInt(0);   // for upright bike, recumbent bike
    public final ObservableInt beltSpeedTCR = new ObservableInt(0);  // 設定跑帶速度
    public final ObservableInt unknownCounter = new ObservableInt(0);  // 設定跑帶速度
    public final ObservableInt b6_accRateInSec = new ObservableInt(0);  // 加速時間
    public final ObservableInt b6_decRateInSec = new ObservableInt(0);  // 減速時間

    public final ObservableField<String> nfcTag = new ObservableField<>("");

    // >>> Power Distribution
    public List<Integer> b5_frontGradeAds = new ArrayList<>();  // 前揚升各階對應的AD值
    public List<Integer> b5_rearGradeAds = new ArrayList<>();   // 後揚升各階對應的AD值
    public List<Integer> numOfPowers = new ArrayList<>();       // 各個角度的Power筆數 (for Power Distribution)
    public List<Integer> sumOfPowers = new ArrayList<>();       // 各個角度的Power累計 (for Power Distribution)
    public List<Integer> minOfPowers = new ArrayList<>();       // 各個角度的最小值 (for Power Distribution)
    public List<Integer> maxOfPowers = new ArrayList<>();       // 各個角度的最大值 (for Power Distribution)
    public List<Integer> ongoingPowersPD = new ArrayList<>();   // 最近24筆的power數值 (Power Distribution)

    public final ObservableList<Boolean> keyStatus = new ObservableArrayList<>();

    public final ObservableInt keycode = new ObservableInt(-1);              // keypad鍵值
    public final ObservableInt sumOfPower = new ObservableInt(0);           // 累計power的總筆數 (Power Distribution)
    public final ObservableInt numOfPower = new ObservableInt(0);           // 累計power的總筆數 (Power Distribution)
    public final ObservableInt firstAngleIndex = new ObservableInt(0);      // 第一次收到的角度
    public final ObservableInt shiftAngle = new ObservableInt(0);           // 在summary顯示雷達圖時, 需要逆轉的角度(數值, 非index)
    public final ObservableBoolean b0_sensorPauseST22S = new ObservableBoolean(false);    // 樓梯機sensor pause觸發
    public final ObservableBoolean isWaitFirstLap = new ObservableBoolean(false);         // 正在等待第一圈結束
    public final ObservableBoolean isFirstAngleObtained = new ObservableBoolean(false);   // 已獲得第一次的角度
    public final ObservableBoolean isPopupSafetyKeyError = new ObservableBoolean(false);  // 已顯示安全鎖視窗
    // <<< Power Distribution

    public void refreshGradeError() {

        boolean result = isFrontGradeError.get() || isRearGradeError.get();
        isGradeError.set(result);

        if (result) {

            if (isFrontGradeError.get())
                Timber.d("前揚升錯誤");

            if (isRearGradeError.get())
                Timber.d("後揚升錯誤");

            Timber.d("揚升錯誤");
        }
//        LiveEventBus.get(UartConst.EVT_GRADE_ERROR).post(isFrontGradeError.get() || isRearGradeError.get());

    }
    public void refreshGradeStatus() {
        // 依據前後揚升的狀態, 整合後僅傳回一個狀態

        if ((b0_frontGradeStatus.get() == DeviceDyacoMedical.INCLINE_STATUS.MOVING) ||
            (b0_rearGradeStatus .get() == DeviceDyacoMedical.INCLINE_STATUS.MOVING))
            b0_gradeStatus.set(DeviceDyacoMedical.INCLINE_STATUS.MOVING);
        else
        if ((b0_frontGradeStatus.get()  == DeviceDyacoMedical.INCLINE_STATUS.STOP) &&
            (b0_rearGradeStatus.get()  == DeviceDyacoMedical.INCLINE_STATUS.STOP))
            b0_gradeStatus.set(DeviceDyacoMedical.INCLINE_STATUS.STOP);
        else
        if ((b0_frontGradeStatus.get()  == DeviceDyacoMedical.INCLINE_STATUS.LOADING) ||
            (b0_rearGradeStatus .get() == DeviceDyacoMedical.INCLINE_STATUS.LOADING))
            b0_gradeStatus.set(DeviceDyacoMedical.INCLINE_STATUS.LOADING);
        else
            b0_gradeStatus.set(DeviceDyacoMedical.INCLINE_STATUS.UNKNOWN);
    }

    public void refreshGradeStop() {
        // 揚升停止: 揚升壞了 或 揚升狀態為停止
        isGradeStop.set(isGradeError.get() || b0_gradeStatus.get() == DeviceDyacoMedical.INCLINE_STATUS.STOP);
    }

    public void clearKeyStatus() {
        for (int index = 0; index < UartConst.KEY_MAX_NUM; index++) {
            if (index >= keyStatus.size())
                keyStatus.add(index, false);
            else
                keyStatus.set(index, false);
        }
    }

    public void incUnknownCounter() {
        int count = unknownCounter.get() + 1;
        unknownCounter.set(count);
    }


    // 非電跑: BR22的workload type
    public final ObservableInt constantType = new ObservableInt(UartConst.CT_DEFAULT);


    public void increaseLwrTimeoutCounter() {
        lwrTimeoutCounter.set(lwrTimeoutCounter.get() + 1);
    }

    public void incNumOfPower() {
        numOfPower.set(numOfPower.get() + 1);
    }

    public void addSumOfPowers(int index, Integer power) {
        sumOfPowers.set(index, sumOfPowers.get(index) + power);
    }

    public void addSumOfPower(int power) {
        sumOfPower.set(sumOfPower.get() + power);
    }

    public void incNumOfPowers(int index) {
        int numOfPower = numOfPowers.get(index);
        numOfPowers.set(index, ++numOfPower);
    }

    public void setMinPowerOfAssignDegree(int index, int minPower) {
        // 設定指定角度的power最小值
        boolean force = minOfPowers.get(index) == 0;

        if (force || minPower < minOfPowers.get(index)) {
            minOfPowers.set(index, minPower);
        }
    }

    public void setMaxPowerOfAssignDegree(int index, int maxPower) {

        // 設定個別角度的最大值
        if (maxPower > maxOfPowers.get(index))
            maxOfPowers.set(index, maxPower);
    }

//    public List<Integer> getMaxPowerOfEachDegree() {
//        // 取得所有角度的power最大值
//        return m_maxOfPowers;
//    }

    public void setSumOfPowers(List<Integer> sumOfPowers) {
        this.sumOfPowers = sumOfPowers;
    }



    public void reset() {
        isExStart.set(false);
        isExStop.set(false);
    }

    public void clearFrontGradeAds() {
        if (b5_frontGradeAds.isEmpty()) return;
        b5_frontGradeAds.subList(0, b5_frontGradeAds.size()).clear();
    }

    public void setDevStep(@UartConst.DeviceStep int step) {
        devStep.set(step);
        if (lastDevStep.get() != devStep.get()) {
            lastDevStep.set(devStep.get());
        }
    }

    public String getStepString() {
        switch (devStep.get()) {
            case UartConst.DS_DEMO_CONNECT_RSP:                 return "DS_DEMO_CONNECT_RSP";
            case UartConst.DS_DEMO_99_DEV_INFO_RSP:             return "DS_DEMO_99_DEV_INFO_RSP";
            case UartConst.DS_DEMO_DONE:                        return "DS_DEMO_DONE";
            case UartConst.DS_CONNECT_RSP:                      return "DS_CONNECT_RSP";
            case UartConst.DS_99_DEV_INFO_RSP:                  return "DS_99_DEV_INFO_RSP";

            case UartConst.DS_BE_TO_TC_RSP:                     return "DS_BE_TO_TC_RSP";
            case UartConst.DS_LWR_INITIALIZING:                 return "DS_LWR_INITIALIZING";
            case UartConst.DS_B7_APP_START_RSP:                 return "DS_B7_APP_START_RSP";
            case UartConst.DS_B7_IDLE_RSP:                      return "DS_B7_IDLE_RSP";
            case UartConst.DS_B3_IDLE_UNIT_RSP:                 return "DS_B3_IDLE_UNIT_RSP";
            case UartConst.DS_B2_IDLE_AD_READ_RSP:              return "DS_B2_IDLE_AD_READ_RSP";
            case UartConst.DS_B5_IDLE_AD_RESET_RSP:             return "DS_B5_IDLE_AD_RESET_RSP";
            case UartConst.DS_IDLE_SILENCE_RSP:                 return "DS_IDLE_SILENCE_RSP";
            case UartConst.DS_B6_IDLE_SPEED_RATE_RSP:           return "DS_B6_IDLE_SPEED_RATE_RSP";
            case UartConst.DS_MT_IDLE_STANDBY:                  return "DS_MT_IDLE_STANDBY";
            case UartConst.DS_B2_IDLE_AD_READ_CONFIRM_RSP:      return "DS_B2_IDLE_AD_READ_CONFIRM_RSP";
            case UartConst.DS_B7_ERR_SAFETY_KEY_RSP:            return "DS_B7_ERR_SAFETY_KEY_RSP";
            case UartConst.DS_B7_CLR_SAFETY_KEY_RSP:            return "DS_B7_CLR_SAFETY_KEY_RSP";

            case UartConst.DS_B7_RUNNING_RSP:                   return "DS_B7_RUNNING_RSP";
            case UartConst.DS_MT_RUNNING_STANDBY:               return "DS_MT_RUNNING_STANDBY";
            case UartConst.DS_B5_RUNNING_AD_CHANGE_RSP:         return "DS_B5_RUNNING_AD_CHANGE_RSP";

            case UartConst.DS_B7_PAUSE_RSP:                     return "DS_B7_PAUSE_RSP";
            case UartConst.DS_B7_PAUSE_GS_RSP:                  return "DS_B7_PAUSE_GS_RSP";
            case UartConst.DS_B5_PAUSE_AD_RESET_RSP:            return "DS_B5_PAUSE_AD_RESET_RSP";
            case UartConst.DS_PAUSE_SILENCE_RSP:                return "DS_PAUSE_SILENCE_RSP";
            case UartConst.DS_MT_PAUSE_STANDBY:                 return "DS_MT_PAUSE_STANDBY";

            case UartConst.DS_B7_RESUME_RUNNING_RSP:            return "DS_B7_RESUME_RUNNING_RSP";
            case UartConst.DS_B5_RESUME_AD_CHANGE_RSP:          return "DS_B5_RESUME_AD_CHANGE_RSP";

            case UartConst.DS_B7_RESET_RSP:                     return "DS_B7_RESET_RSP";
            case UartConst.DS_RESET_STANDBY:                    return "DS_RESET_STANDBY";

            case UartConst.DS_B5_BREAK_SPD_RESET_RSP:           return "DS_B5_BREAK_SPD_RESET_RSP";
            case UartConst.DS_B5_FINISH_AD_RESET_RSP:           return "DS_B5_FINISH_AD_RESET_RSP";
            case UartConst.DS_FINISH_SILENCE_RSP:               return "DS_FINISH_SILENCE_RSP";
            case UartConst.DS_B7_FINISH_PAUSE_RSP:              return "DS_B7_FINISH_PAUSE_RSP";
            case UartConst.DS_B7_FINISH_IDLE_RSP:               return "DS_B7_FINISH_IDLE_RSP";
            case UartConst.DS_B7_MAINTENANCE_RSP:               return "DS_B7_MAINTENANCE_RSP";
            case UartConst.DS_MAINTENANCE_STANDBY:              return "DS_MAINTENANCE_STANDBY";
            case UartConst.DS_BA_BRAKE_MODE_RSP:                return "DS_BA_BREAK_MODE_RSP";
            case UartConst.DS_B5_MAINTENANCE_AD_CHANGE_RSP:     return "DS_B5_MAINTENANCE_AD_CHANGE_RSP";

            case UartConst.DS_FA_UPDATE_RESET_RSP:              return "DS_FA_UPDATE_RESET_RSP";
            case UartConst.DS_FA_WAIT_COME_BACK:                return "DS_FA_WAIT_COME_BACK";
            case UartConst.DS_99_UPDATE_DEV_INFO_RSP:           return "DS_99_UPDATE_DEV_INFO_RSP";
            case UartConst.DS_BE_UPDATE_TO_TC_RSP:              return "DS_BE_UPDATE_TO_TC_RSP";

            case UartConst.DS_B7_CALIBRATION_RSP:               return "DS_B7_CALIBRATION_RSP";
            case UartConst.DS_CALIBRATION_STANDBY:              return "DS_CALIBRATION_STANDBY";

            case UartConst.DS_B0_CALI_A_ONGOING:                return "DS_B0_CALI_A_ONGOING";
            case UartConst.DS_B0_CALI_AF_MAX:                   return "DS_B0_CALI_AF_MAX";
            case UartConst.DS_B0_CALI_AF_MIN:                   return "DS_B0_CALI_AF_MIN";
            case UartConst.DS_B0_CALI_AR_MAX:                   return "DS_B0_CALI_AR_MAX";
            case UartConst.DS_B0_CALI_AR_MIN:                   return "DS_B0_CALI_AR_MIN";

            case UartConst.DS_B0_CALI_SF_ONGOING:               return "DS_B0_CALI_SF_ONGOING";
            case UartConst.DS_B0_CALI_SF_MAX:                   return "DS_B0_CALI_SF_MAX";
            case UartConst.DS_B0_CALI_SF_MIN:                   return "DS_B0_CALI_SF_MIN";

            case UartConst.DS_B0_CALI_SR_ONGOING:               return "DS_B0_CALI_SR_ONGOING";
            case UartConst.DS_B0_CALI_SR_MAX:                   return "DS_B0_CALI_SR_MAX";
            case UartConst.DS_B0_CALI_SR_MIN:                   return "DS_B0_CALI_SR_MIN";

            case UartConst.DS_BB_CALI_A_START_RSP:              return "DS_BB_CALI_A_START_RSP";
            case UartConst.DS_BB_CALI_SF_START_RSP:             return "DS_BB_CALI_SF_START_RSP";
            case UartConst.DS_BB_CALI_SR_START_RSP:             return "DS_BB_CALI_SR_START_RSP";
            case UartConst.DS_BB_CALI_A_STOP_RSP:               return "DS_BB_CALI_A_STOP_RSP";
            case UartConst.DS_BB_CALI_SF_STOP_RSP:              return "DS_BB_CALI_SF_STOP_RSP";
            case UartConst.DS_BB_CALI_SR_STOP_RSP:              return "DS_BB_CALI_SR_STOP_RSP";

            case UartConst.DS_BD_CALI_A_RSLT_RSP:               return "DS_BD_CALI_A_RSLT_RSP";
            case UartConst.DS_BD_CALI_F_RSLT_RSP:               return "DS_BD_CALI_F_RSLT_RSP";
            case UartConst.DS_BD_CALI_R_RSLT_RSP:               return "DS_BD_CALI_R_RSLT_RSP";

            case UartConst.DS_BB_CALI_M_STARTUP_RSP:            return "DS_BB_CALI_M_STARTUP_RSP";
            case UartConst.DS_BB_CALI_M_START_RSP:              return "DS_BB_CALI_M_START_RSP";
            case UartConst.DS_B0_CALI_M_ONGOING:                return "DS_B0_CALI_M_ONGOING";
            case UartConst.DS_B0_CALI_M_RSLT:                   return "DS_B0_CALI_M_RSLT";

            case UartConst.DS_A0_IDLE_RSP:                      return "DS_A0_IDLE_RSP";
            case UartConst.DS_A9_SYMM_ANGLE_RSP:                return "DS_A9_SYMM_ANGLE_RSP";
            case UartConst.DS_80_IDLE_ECHO_RSP:                 return "DS_80_IDLE_ECHO_RSP";
            case UartConst.DS_EMS_IDLE_STANDBY:                 return "DS_EMS_IDLE_STANDBY";

            case UartConst.DS_A0_RUNNING_RSP:                   return "DS_A0_RUNNING_RSP";
            case UartConst.DS_EMS_RUNNING_STANDBY:              return "DS_EMS_RUNNING_STANDBY";
            case UartConst.DS_A5_RUNNING_RPM_RSP:               return "DS_A5_RUNNING_RPM_RSP";
            case UartConst.DS_80_RUNNING_PWM_RSP:               return "DS_80_RUNNING_PWM_RSP";
            case UartConst.DS_A0_PAUSE_RSP:                     return "DS_A0_PAUSE_RSP";
            case UartConst.DS_A0_PAUSE_STANDBY:                 return "DS_A0_PAUSE_STANDBY";
            case UartConst.DS_A8_BRAKE_MODE_RSP:                return "DS_A8_BRAKE_MODE_RSP";

            case UartConst.DS_A0_CALIBRATION_RSP:               return "DS_A0_CALIBRATION_RSP";
            case UartConst.DS_ANGLE_CALI_STANDBY:               return "DS_ANGLE_CALI_STANDBY";
            case UartConst.DS_A7_CALI_START_RSP:                return "DS_A7_CALI_START_RSP";
            case UartConst.DS_A7_CALI_RESULT_RSP:               return "DS_A7_CALI_RESULT_RSP";

            case UartConst.DS_A0_SENSOR_TEST_RSP:               return "DS_A0_SENSOR_TEST_RSP";
            case UartConst.DS_A0_SENSOR_TEST_STANDBY:           return "DS_A0_SENSOR_TEST_STANDBY";

            case UartConst.DS_CORESTAR:                         return "DS_CORESTAR";

            case UartConst.DS_FA_CONSOLE_RESET_RSP:             return "DS_FA_CONSOLE_RESET_RSP";
            case UartConst.DS_B7_CONSOLE_ERR_RSP:               return "DS_B7_CONSOLE_ERR_RSP";
            case UartConst.DS_CONSOLE_ERR_STANDBY:              return "DS_CONSOLE_ERR_STANDBY";

            case UartConst.DS_BF_CLEAR_ERRS_RSP:                return "DS_BF_CLEAR_ERRS_RSP";
            case UartConst.DS_BF_CLEAR_ERR1_RSP:                return "DS_BF_CLEAR_ERR1_RSP";
            case UartConst.DS_BF_CLEAR_ERR2_RSP:                return "DS_BF_CLEAR_ERR2_RSP";
            case UartConst.DS_B8_STOP_SC_RSP:                   return "DS_B8_STOP_SC_RSP";
            case UartConst.DS_B8_STOP_SC_STANDBY:               return "DS_B8_STOP_SC_STANDBY";

            case UartConst.DS_90_NORMAL_MODE_RSP:               return "DS_90_NORMAL_MODE_RSP";
            case UartConst.DS_82_CLEAR_RPM_COUNTER_RSP:         return "DS_82_CLEAR_RPM_COUNTER_RSP";
            case UartConst.DS_ECB_IDLE_STANDBY:                 return "DS_ECB_IDLE_STANDBY";
            case UartConst.DS_82_RESUME_RPM_COUNTER_RSP:        return "DS_82_RESUME_RPM_COUNTER_RSP";
            case UartConst.DS_ECB_RUNNING:                      return "DS_ECB_RUNNING";
            case UartConst.DS_82_PAUSE_RPM_COUNTER_RSP:         return "DS_82_PAUSE_RPM_COUNTER_RSP";
            case UartConst.DS_ECB_PAUSE_STANDBY:                return "DS_ECB_PAUSE";
            case UartConst.DS_80_RUNNING_RES_RSP:               return "DS_80_RUNNING_RES_RSP";

            case UartConst.DS_80_MOTOR_TEST_STANDBY:            return "DS_80_MOTOR_TEST_STANDBY";
            case UartConst.DS_80_MOTOR_TEST_RES_RSP:            return "DS_80_MOTOR_TEST_RES_RSP";
            case UartConst.DS_ECB_ERR_OCCURRED:                 return "DS_ECB_ERR_OCCURRED";
            case UartConst.DS_EMS_ERR_OCCURRED:                 return "DS_EMS_ERR_OCCURRED";
        }
        return "UNKNOWN STEP! devStep =  " + devStep.get();
    }

}
