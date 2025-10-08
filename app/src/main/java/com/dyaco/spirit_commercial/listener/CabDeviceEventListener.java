package com.dyaco.spirit_commercial.listener;

import android.util.Log;

import com.corestar.libs.device.DeviceCab;

/**
 * 初始化

 ```
 DeviceCab deviceCab = new DeviceCab(context);
 ```

 * 連線/斷線設備

 ```
 // 切換Cab/Csafe設備時需要把設備先斷線
 // 連線
 deviceCab.connect();

 // 斷線
 deviceCab.disconnect();
 ```

 * 註冊介面

 ```
 deviceCab.registerListener(DeviceCab.DeviceEventListener);
 ```

 * 取消監聽介面

 ```
 deviceCab.unregisterListener();
 ```

 * 功能與設定

 ```
 enum FUNCTIONS {
 VOL_DOWN("AB"),
 VOL_UP("AC"),
 CH_DOWN("AD"),
 CH_UP("AE"),
 POWER_TOGGLE("AG"),
 PREVIOUS_CH("AI"),
 MUTE("AJ"),
 CC("AQ"),
 ENTER("AR"),
 SOURCE("AS"),
 DIGIT_0("A0"),
 DIGIT_1("A1"),
 DIGIT_2("A2"),
 DIGIT_3("A3"),
 DIGIT_4("A4"),
 DIGIT_5("A5"),
 DIGIT_6("A6"),
 DIGIT_7("A7"),
 DIGIT_8("A8"),
 DIGIT_9("A9"),
 DASH("AZ"),
 EXIT("BA"),
 BACK("BB"),
 RETURN("BC"),
 GUIDE("BD"),
 ARROW_UP("BE"),
 ARROW_DOWN("BF"),
 ARROW_LEFT("BG"),
 ARROW_RIGHT("BH"),
 OK_OR_SELECT("BI"),
 PAGE_UP("BJ"),
 PAGE_DOWN("BK"),;
 }

 enum SETTINGS {
 SOFF("SOFF", "Disable STB (TV only)"),
 SCAB("SCAB", "Most Cable STBs"),
 SCAS("SCAS", "Most Cable STBs - Alternate"),
 SSAT("SSAT", "Most Satellite STBs"),
 VOLUME("", "Volume level"), // check key if empty
 GAIN("", "Gain reduction"), // check key if empty
 STB_00("STB 00", "Disables output to STB"),
 STB_01("STB 01", "ENTONE STBs"),
 STB_02("STB 02", "Motorola Cable STBs"),
 STB_03("STB 03", "Dish Network STBs"),
 STB_04("STB 04", "DirecTV STBs"),
 STB_05("STB 05", "Samsung/Scientific Atlanta Cable STBs"),
 STB_06("STB 06", "SciAtlanta IPN330HD Cable STB"),
 STB_07("STB 07", "Most small-sized Cable STBs"),
 STB_08("STB 08", "Enable cable STBs"),
 STB_09("STB 09", "Enable satellite STBs"),
 STB_10("STB 10", "Enables short version cable STBs"),
 STB_11("STB 11", "ViewTV Demo Tuner"),
 STB_12("STB 12", "ViewTV Demo Tuner v2"),
 STB_13("STB 13", "YES STBs (Blk/Silver remotes)"),
 STB_14("STB 14", "HOT STBs (Select button remotes)"),
 STB_15("STB 15", "HOT STBs (OK button remotes)"),
 STB_16("STB 16", "Integra IPTV"),
 STB_17("STB 17", "COX and Evo NewRev STBs"),
 STB_18("STB 18", "ROGERS Cable STBs"),
 STB_19("STB 19", "Google Fiber STBs"),
 STB_20("STB 20", "LG STBs"),
 STB_21("STB 21", "StarHub STBs"),
 STB_22("STB 22", "MIO TV STBs"),
 STB_23("STB 23", "ENSEO STBs"),
 STB_24("STB 24", "AMINO STBs"),
 STB_25("STB 25", "EVOLUTION STBs"),
 STB_26("STB 26", "Cable One STBs"),
 STB_27("STB 27", "Megasat STBs"),
 STB_28("STB 28", "ADB STBs"),
 STB_29("STB 29", "Haivision Mantaray STBs"),
 STB_30("STB 30", "Pinpoint Communication STBs"),
 STB_31("STB 31", "Google Fiber STBs (NEW VERSION)"),
 STB_32("STB 32", "Arantia STBs"),
 STB_33("STB 33", "Entone-NEW STBs"),
 STB_34("STB 34", "Frontier-NEW STBs"),
 STB_35("STB 35", "Exterity STBs"),
 STB_36("STB 36", "Orange STBs"),
 ENABLE_KEEP_ALIVE("KE", "Enable Keep Alive"),
 DISABLE_KEEP_ALIVE("KD", "Disable Keep Alive"),;
 }
 ```

 * 指令發送

 ```
 // FUNCTIONS 和 SETTINGS 都是 COMMAND
 COMMAND command = FUNCTIONS.VOL_DOWN;
 deviceCab.sendCabCommand(command);
 ```

 * 取得音量物件

 ```
 // level range 0 <= l <= 15;
 COMMAND volume = SETTINGS.getVolume(level);
 deviceCab.sendCabCommand(volume);
 ```

 * 取得增益物件

 ```
 // gain value: 0, 1, 2
 SETTINGS gain = SETTINGS.getGainReduction(gain);
 deviceCab.sendCabCommand(gain);
 ```

 */

public class CabDeviceEventListener implements DeviceCab.DeviceEventListener {
    private static final String TAG = "CABBBBB";
    @Override
    public void onConnectFail() {
        Log.d(TAG, "onConnectFail: ");
    }

    @Override
    public void onConnected() {
        Log.d(TAG, "onConnected: ");
    }

    @Override
    public void onDisconnected() {
        Log.d(TAG, "onDisconnected: ");
    }

    @Override
    public void onDataSend(String s) {
        Log.d(TAG, "onDataSend: " + s);
    }

    @Override
    public void onDataReceive(String s) {
        Log.d(TAG, "onDataReceive: " + s);
    }

    @Override
    public void onErrorMessage(String s) {
        Log.d(TAG, "onErrorMessage: " + s);
    }
}
