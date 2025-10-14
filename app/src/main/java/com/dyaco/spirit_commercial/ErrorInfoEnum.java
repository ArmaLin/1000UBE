package com.dyaco.spirit_commercial;

import com.corestar.libs.device.DeviceDyacoMedical;

public enum ErrorInfoEnum {

    E_NONE                (UartConst.ET_NONE, 0, ""),

    // 電跑, 樓梯機
    INVERTER_RLER_E101    (UartConst.ET_INVERTER, DeviceDyacoMedical.INVERTER_ERROR.RLER.getCode(), "Low Voltage Is Occurred"),
    INVERTER_LU_E102      (UartConst.ET_INVERTER, DeviceDyacoMedical.INVERTER_ERROR.LU.getCode(), "Temperature Sensor Fail"),
    INVERTER_OCA_E103     (UartConst.ET_INVERTER, DeviceDyacoMedical.INVERTER_ERROR.OCA.getCode(), ""),
    INVERTER_OCD_E104     (UartConst.ET_INVERTER, DeviceDyacoMedical.INVERTER_ERROR.OCD.getCode(), "Over Output Current"),
    INVERTER_OCN_E105     (UartConst.ET_INVERTER, DeviceDyacoMedical.INVERTER_ERROR.OCN.getCode(), "OCN"),
    INVERTER_OL_E106      (UartConst.ET_INVERTER, DeviceDyacoMedical.INVERTER_ERROR.OL.getCode(), "Output Voltage"),
    INVERTER_OL1_E107     (UartConst.ET_INVERTER, DeviceDyacoMedical.INVERTER_ERROR.OL1.getCode(), "OL1"),
    INVERTER_OL2_E108     (UartConst.ET_INVERTER, DeviceDyacoMedical.INVERTER_ERROR.OL2.getCode(), "Ground Fail"),
    INVERTER_HOC1_E109    (UartConst.ET_INVERTER, DeviceDyacoMedical.INVERTER_ERROR.HOC1.getCode(), "Over Heat"),
    INVERTER_HOC2_E10A    (UartConst.ET_INVERTER, DeviceDyacoMedical.INVERTER_ERROR.HOC2.getCode(), "Motor Over Load"),
    INVERTER_HOC3_E10B    (UartConst.ET_INVERTER, DeviceDyacoMedical.INVERTER_ERROR.HOC3.getCode(), "Over Load"),
    INVERTER_HOU_E10C     (UartConst.ET_INVERTER, DeviceDyacoMedical.INVERTER_ERROR.HOU.getCode(), "System Over Load"),
    INVERTER_EF_E10D      (UartConst.ET_INVERTER, DeviceDyacoMedical.INVERTER_ERROR.EF.getCode(), "Motor Disconnected"),
    INVERTER_OCBE_E10E    (UartConst.ET_INVERTER, DeviceDyacoMedical.INVERTER_ERROR.OCBE.getCode(), "Brake Fail"),
    INVERTER_AUTF_E10F    (UartConst.ET_INVERTER, DeviceDyacoMedical.INVERTER_ERROR.AUTF.getCode(), "AUTF"),
    INVERTER_CT1E_E110    (UartConst.ET_INVERTER, DeviceDyacoMedical.INVERTER_ERROR.CT1E.getCode(), "CT1E"),
    INVERTER_CT2E_E111    (UartConst.ET_INVERTER, DeviceDyacoMedical.INVERTER_ERROR.CT2E.getCode(), "CT2E"),
    INVERTER_CT3E_E112    (UartConst.ET_INVERTER, DeviceDyacoMedical.INVERTER_ERROR.CT3E.getCode(), "CT3E"),
    INVERTER_ERP0_E113    (UartConst.ET_INVERTER, DeviceDyacoMedical.INVERTER_ERROR.ERP0.getCode(), "ERP0"),
    INVERTER_ERP1_E114    (UartConst.ET_INVERTER, DeviceDyacoMedical.INVERTER_ERROR.ERP1.getCode(), "ERP1"),
    INVERTER_ERP2_E115    (UartConst.ET_INVERTER, DeviceDyacoMedical.INVERTER_ERROR.ERP2.getCode(), "ERP2"),
    INVERTER_ERP3_E116    (UartConst.ET_INVERTER, DeviceDyacoMedical.INVERTER_ERROR.ERP3.getCode(), "ERP3"),
    INVERTER_CONF_E117    (UartConst.ET_INVERTER, DeviceDyacoMedical.INVERTER_ERROR.CONF.getCode(), "CONF"),
    INVERTER_ACIO_E118    (UartConst.ET_INVERTER, DeviceDyacoMedical.INVERTER_ERROR.ACIO.getCode(), "ACIO"),
    INVERTER_TPER_E119    (UartConst.ET_INVERTER, DeviceDyacoMedical.INVERTER_ERROR.TPER.getCode(), "TPER"),
    INVERTER_PGE_E11A     (UartConst.ET_INVERTER, DeviceDyacoMedical.INVERTER_ERROR.PGE.getCode(), "PGE"),
    INVERTER_PGO_E11B     (UartConst.ET_INVERTER, DeviceDyacoMedical.INVERTER_ERROR.PGO.getCode(), "PGO"),
    INVERTER_OS_E11C      (UartConst.ET_INVERTER, DeviceDyacoMedical.INVERTER_ERROR.OS.getCode(), "OS"),
    INVERTER_OES_E11D     (UartConst.ET_INVERTER, DeviceDyacoMedical.INVERTER_ERROR.OES.getCode(), "OES"),
    INVERTER_OH0_E11E     (UartConst.ET_INVERTER, DeviceDyacoMedical.INVERTER_ERROR.OH0.getCode(), "OH0"),
    INVERTER_OH1_E11F     (UartConst.ET_INVERTER, DeviceDyacoMedical.INVERTER_ERROR.OH1.getCode(), "OH1"),
    INVERTER_OH2_E120     (UartConst.ET_INVERTER, DeviceDyacoMedical.INVERTER_ERROR.OH2.getCode(), "OH2"),
    INVERTER_OH3_E121     (UartConst.ET_INVERTER, DeviceDyacoMedical.INVERTER_ERROR.OH3.getCode(), "Flash Program Fail"),
    INVERTER_FBF_E122     (UartConst.ET_INVERTER, DeviceDyacoMedical.INVERTER_ERROR.FBF.getCode(), "Flash Fail"),
    INVERTER_FBU_E123     (UartConst.ET_INVERTER, DeviceDyacoMedical.INVERTER_ERROR.FBU.getCode(), "AC Low Voltage"),
    INVERTER_FBEF_E124    (UartConst.ET_INVERTER, DeviceDyacoMedical.INVERTER_ERROR.FBEF.getCode(), "FBEF"),
    INVERTER_OS1_E125     (UartConst.ET_INVERTER, DeviceDyacoMedical.INVERTER_ERROR.OS1.getCode(), "OS1"),
    INVERTER_LL_E126      (UartConst.ET_INVERTER, DeviceDyacoMedical.INVERTER_ERROR.LL.getCode(), "Driver Settings Error"),
    INVERTER_NAUT_E127    (UartConst.ET_INVERTER, DeviceDyacoMedical.INVERTER_ERROR.NAUT.getCode(), "NAUT"),
    INVERTER_PF_E128      (UartConst.ET_INVERTER, DeviceDyacoMedical.INVERTER_ERROR.PF.getCode(), "PF"),
    INVERTER_EPE0_E129    (UartConst.ET_INVERTER, DeviceDyacoMedical.INVERTER_ERROR.EPE0.getCode(), "Motor Over Heat"),
    INVERTER_EPE1_E12A    (UartConst.ET_INVERTER, DeviceDyacoMedical.INVERTER_ERROR.EPE1.getCode(), "EPE1"),
    INVERTER_OUA_E12B     (UartConst.ET_INVERTER, DeviceDyacoMedical.INVERTER_ERROR.OUA.getCode(), "OUA"),
    INVERTER_OUD_E12C     (UartConst.ET_INVERTER, DeviceDyacoMedical.INVERTER_ERROR.OUD.getCode(), "OUD"),
    INVERTER_OUN_E12D     (UartConst.ET_INVERTER, DeviceDyacoMedical.INVERTER_ERROR.OUN.getCode(), "OUN"),
    INVERTER_ERP4_E12E    (UartConst.ET_INVERTER, DeviceDyacoMedical.INVERTER_ERROR.ERP4.getCode(), "ERP4"),
    INVERTER_LP_E12F      (UartConst.ET_INVERTER, DeviceDyacoMedical.INVERTER_ERROR.LP.getCode(), "LP"),
    INVERTER_STOP_E130    (UartConst.ET_INVERTER, DeviceDyacoMedical.INVERTER_ERROR.STOP.getCode(), "STOP"),
    INVERTER_FBB_E131     (UartConst.ET_INVERTER, DeviceDyacoMedical.INVERTER_ERROR.FBB.getCode(), "FBB"),
    INVERTER_RBB_E132     (UartConst.ET_INVERTER, DeviceDyacoMedical.INVERTER_ERROR.RBB.getCode(), "RBB"),
    INVERTER_DNE_E133     (UartConst.ET_INVERTER, DeviceDyacoMedical.INVERTER_ERROR.DNE.getCode(), "DNE"),
    INVERTER_HERR_E134    (UartConst.ET_INVERTER, DeviceDyacoMedical.INVERTER_ERROR.HERR.getCode(), "HERR"),
    INVERTER_FERR_E135    (UartConst.ET_INVERTER, DeviceDyacoMedical.INVERTER_ERROR.FERR.getCode(), "FERR"),
    INVERTER_RERR_E136    (UartConst.ET_INVERTER, DeviceDyacoMedical.INVERTER_ERROR.RERR.getCode(), "RERR"),
    INVERTER_MOH_E1E9     (UartConst.ET_INVERTER, DeviceDyacoMedical.INVERTER_ERROR.MOH.getCode(), "MOH"),
    INVERTER_UNKNOWN_E1FF (UartConst.ET_INVERTER, DeviceDyacoMedical.INVERTER_ERROR.UNKNOWN.getCode(), ""),

    // 橋接板 (即SUB板, 東庚已將橋接板與sub板合併)
    BRIDGE_INCLINE_E201   (UartConst.ET_BRIDGE, DeviceDyacoMedical.BRIDGE_ERROR.INCLINE.getCode(), "Incline Error"),
    BRIDGE_DECLINE_E202   (UartConst.ET_BRIDGE, DeviceDyacoMedical.BRIDGE_ERROR.DECLINE.getCode(), "Decline Error"),
    BRIDGE_UART_E204      (UartConst.ET_BRIDGE, DeviceDyacoMedical.BRIDGE_ERROR.UART.getCode(), "UART Error"),
    BRIDGE_RPM_E208       (UartConst.ET_BRIDGE, DeviceDyacoMedical.BRIDGE_ERROR.RPM.getCode(), "RPM Error"),
    BRIDGE_SENSOR_E210    (UartConst.ET_BRIDGE, DeviceDyacoMedical.BRIDGE_ERROR.SENSOR.getCode(), "NPN Sensor Pause"),   // 用於樓梯機的pause sensor, 但不需顯示error視窗, 僅作為判斷立即停止
    BRIDGE_RS485_E220     (UartConst.ET_BRIDGE, DeviceDyacoMedical.BRIDGE_ERROR.RS485.getCode(), "RS485 Error"),
    BRIDGE_SAFETY_KEY_E240(UartConst.ET_BRIDGE, DeviceDyacoMedical.BRIDGE_ERROR.SAFETY_KEY.getCode(), "Please replace the safety key"),
    BRIDGE_MCU_E280       (UartConst.ET_BRIDGE, DeviceDyacoMedical.BRIDGE_ERROR.MCU.getCode(), "MCU Error"),

    // 車類
    MCU_RES_E301          (UartConst.ET_MCU, DeviceDyacoMedical.MCU_ERROR.RES.getCode(), "Motor Error"),              // 拉線器錯誤, VR沒接 或 馬達沒動
    MCU_INC_E302          (UartConst.ET_MCU, DeviceDyacoMedical.MCU_ERROR.INC.getCode(), "Incline Error"),            // 揚升錯誤, VR沒接 或 馬達沒動
    MCU_LS_E304           (UartConst.ET_MCU, DeviceDyacoMedical.MCU_ERROR.LS.getCode(),  "Reed Switch Error"),        // Reed Switch沒信號(跑帶堵住過久也會)
    MCU_OCP_E308          (UartConst.ET_MCU, DeviceDyacoMedical.MCU_ERROR.OCP.getCode(), "Over Current Protection"),  // 過電流
    MCU_OVP_E310          (UartConst.ET_MCU, DeviceDyacoMedical.MCU_ERROR.OVP.getCode(), "Over Voltage Protection"),  // 過電壓
    MCU_RPM2_E320         (UartConst.ET_MCU, DeviceDyacoMedical.MCU_ERROR.RPM2.getCode(), "Angle Sensor Error"),      // ECB拉線器錯誤
    MCU_EMS_E340          (UartConst.ET_MCU, DeviceDyacoMedical.MCU_ERROR.EMS.getCode(),  "EMS Error"),               // EMS
//    EMS_E5_0002           (UartConst.ET_EMS, DeviceDyacoMedical.DK_EMS_ERROR.MOTOR_DOWN.getCode(),  "Incline Motor Descending Error"),    // 揚升馬達下降異常
//    EMS_E5_0004           (UartConst.ET_EMS, DeviceDyacoMedical.DK_EMS_ERROR.MOTOR_UP.getCode(),  "Incline Motor Rising Error"),        // 揚升馬達上降異常
//    EMS_E5_0008           (UartConst.ET_EMS, DeviceDyacoMedical.DK_EMS_ERROR.MOTOR_CALIBRATE.getCode(),  "Incline Motor Calibration Error"),        // 揚升馬達歸零定位異常
//    EMS_E5_0010           (UartConst.ET_EMS, DeviceDyacoMedical.DK_EMS_ERROR.RES_OUTPUT.getCode(),  "Resistance Control (Back-end) Error"),        // 阻力控制異常 (電磁鐵後端)
//    EMS_E5_0020           (UartConst.ET_EMS, DeviceDyacoMedical.DK_EMS_ERROR.RES_DRIVE.getCode(),  "Resistance Control (Front-end) Error"),        // 阻力控制異常 (電磁鐵前端)
//    EMS_E5_0040           (UartConst.ET_EMS, DeviceDyacoMedical.DK_EMS_ERROR.VOLTAGE_GEN.getCode(),  "Power Generation Voltage Error"),        // 發電穩壓電壓異常
//    EMS_E5_0080           (UartConst.ET_EMS, DeviceDyacoMedical.DK_EMS_ERROR.VOLTAGE_EXT.getCode(),  "External Voltage Error"),        // 外接電壓異常
//    EMS_E5_0100           (UartConst.ET_EMS, DeviceDyacoMedical.DK_EMS_ERROR.VOLTAGE_BAT.getCode(),  "Battery Voltage Error"),        // 蓄電池電壓異常
//    EMS_E5_0200           (UartConst.ET_EMS, DeviceDyacoMedical.DK_EMS_ERROR.GEN_ADC.getCode(),  "Self-generator AC-DC Error"),        // 自發電AC-DC異常
//    EMS_E5_0400           (UartConst.ET_EMS, DeviceDyacoMedical.DK_EMS_ERROR.AC3.getCode(),  "Self-generator AC3 Error"),        // 自發電AC3異常
//    EMS_E5_0800           (UartConst.ET_EMS, DeviceDyacoMedical.DK_EMS_ERROR.AC2.getCode(),  "Self-generator AC2 Error"),        // 自發電AC2異常
//    EMS_E5_1000           (UartConst.ET_EMS, DeviceDyacoMedical.DK_EMS_ERROR.AC1.getCode(),  "Self-generator AC1 Error"),        // 自發電AC1異常
//    EMS_E5_2000           (UartConst.ET_EMS, DeviceDyacoMedical.DK_EMS_ERROR.RPM_DETECT.getCode(),  "RPM Detection Error"),        // RPM偵測異常
//    EMS_E5_4000           (UartConst.ET_EMS, DeviceDyacoMedical.DK_EMS_ERROR.TIMEOUT.getCode(),  "Communication Timeout"),        // 通訊異常
//    EMS_E5_8000           (UartConst.ET_EMS, DeviceDyacoMedical.DK_EMS_ERROR.GROUP1.getCode(),  "Flags Error"),        // 異常旗標

    APP_UART_E401         (UartConst.ET_APP, UartConst.EC_UART, "LCB Not Responding"),                              // UART error, 下控未回應
    APP_MOTOR_E402        (UartConst.ET_APP, UartConst.EC_MOTOR, "Motor Error"),     // 逾時尚未到達設定值, 視同馬達錯誤                         // UART error, 下控未回應
    APP_MILEAGE_E406      (UartConst.ET_APP, UartConst.EC_MILEAGE, "This machine is due for scheduled services."),     // mileage (保養里程數已屆)
    APP_TIME_E407         (UartConst.ET_APP, UartConst.EC_TIME, "This machine is due for scheduled services."),     // time    (保養時間已屆)
    E_UNKNOWN             (UartConst.ET_APP, UartConst.EC_UNKNOWN, "Unknown Error");


    private @UartConst.ErrorType int     errorType;
    private int                          errorCode;
    private String                       errorMsg;

    ErrorInfoEnum(@UartConst.ErrorType int errorType,
                    int errorCode,
                    String errorMsg) {
        this.errorType = errorType;
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public static ErrorInfoEnum getErrorInfoEnumByTypeAndCode(@UartConst.ErrorType int errorType, int errorCode) {
        // 依errorType及errorCode取得對應的 ErrorInfoEnum
        for (ErrorInfoEnum errorInfoEnum : values()) {
            if ((errorInfoEnum.getErrorType() == errorType) && (errorInfoEnum.getErrorCode() == errorCode))
                return errorInfoEnum;
        }

        // 取不到則使用unknown
        return E_UNKNOWN;
    }

    public int getErrorType() {
        return errorType;
    }

    public void setErrorType(int errorType) {
        this.errorType = errorType;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
