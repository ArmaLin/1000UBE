package com.dyaco.spirit_commercial.listener;


import static com.dyaco.spirit_commercial.support.intdef.EventKey.EVENT_CSAFE_ECHO_TEST;

import android.util.Log;

import com.corestar.libs.csafe.AutoUpload;
import com.corestar.libs.device.DeviceCsafe;
import com.jeremyliao.liveeventbus.LiveEventBus;

public class CsafeDeviceEventListener implements DeviceCsafe.DeviceEventListener {
    public static final String TAG = "CSAFE";

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
      //  Log.d(TAG, "onDataSend: " + s);
    }

    @Override
    public void onDataReceive(String s) {
     //   Log.d(TAG, "onDataReceive: " + s);
    }

    @Override
    public void onErrorMessage(String s) {
        Log.d(TAG, "onErrorMessage: " + s);
    }

    @Override
    public void onGetStatus(DeviceCsafe.COMMAND command) {
        Log.d(TAG, "onGetStatus: " + command);
        
    }

    @Override
    public void onRequestReset(DeviceCsafe.COMMAND command) {
        Log.d(TAG, "onRequestReset: ");
    }

    @Override
    public void onRequestState(DeviceCsafe.COMMAND command, DeviceCsafe.STATE_OF_SLAVE state_of_slave) {
        Log.d(TAG, "onRequestState: ");
    }

    @Override
    public void onBadID(DeviceCsafe.COMMAND command) {
        Log.d(TAG, "onBadID: ");
    }

    @Override
    public void onAutoUpload(DeviceCsafe.COMMAND command, AutoUpload autoUpload) {
        Log.d(TAG, "onAutoUpload: ");
    }

    @Override
    public void onUpList(DeviceCsafe.COMMAND command, DeviceCsafe.COMMAND[] commands) {
        Log.d(TAG, "onUpList: ");
    }

    @Override
    public void onUpStatusSec(DeviceCsafe.COMMAND command, int i) {
        Log.d(TAG, "onUpStatusSec: ");
    }

    @Override
    public void onUpListSec(DeviceCsafe.COMMAND command, int i) {
        Log.d(TAG, "onUpListSec: ");
    }

    @Override
    public void onIDDigits(DeviceCsafe.COMMAND command, int i) {
        Log.d(TAG, "onIDDigits: ");
    }

    @Override
    public void onSetTime(DeviceCsafe.COMMAND command, int i, int i1, int i2) {
        Log.d(TAG, "onSetTime: ");
    }

    @Override
    public void onSetDate(DeviceCsafe.COMMAND command, int i, int i1, int i2) {
        Log.d(TAG, "onSetDate: ");
    }

    @Override
    public void onSetTimeout(DeviceCsafe.COMMAND command, int i) {
        Log.d(TAG, "onSetTimeout: ");
    }

    @Override
    public void onSetUserCFG1(DeviceCsafe.COMMAND command, String s) {
        Log.d(TAG, "onSetUserCFG1: ");
    }

    @Override
    public void onSetUserCFG2(DeviceCsafe.COMMAND command, String s) {
        Log.d(TAG, "onSetUserCFG2: ");
    }

    @Override
    public void onSetChannelRange(DeviceCsafe.COMMAND command, int i, int i1) {
        Log.d(TAG, "onSetChannelRange: ");
    }

    @Override
    public void onSetVolumeRange(DeviceCsafe.COMMAND command, int i, int i1) {
        Log.d(TAG, "onSetVolumeRange: ");
    }

    @Override
    public void onSetAudioMute(DeviceCsafe.COMMAND command, boolean b) {
        Log.d(TAG, "onSetAudioMute: ");
    }

    @Override
    public void onSetAudioChannel(DeviceCsafe.COMMAND command, int i) {
        Log.d(TAG, "onSetAudioChannel: ");
    }

    @Override
    public void onSetAudioVolume(DeviceCsafe.COMMAND command, int i) {
        Log.d(TAG, "onSetAudioVolume: ");
    }

    @Override
    public void onGetVersion(DeviceCsafe.COMMAND command) {
        Log.d(TAG, "onGetVersion: ");
    }

    @Override
    public void onGetID(DeviceCsafe.COMMAND command) {
        Log.d(TAG, "onGetID: ");
    }

    @Override
    public void onGetUnits(DeviceCsafe.COMMAND command) {
        Log.d(TAG, "onGetUnits: ");
    }

    @Override
    public void onGetSerial(DeviceCsafe.COMMAND command) {
        Log.d(TAG, "onGetSerial: ");
    }

    @Override
    public void onGetList(DeviceCsafe.COMMAND command) {
        Log.d(TAG, "onGetList: ");
    }

    @Override
    public void onGetUtilization(DeviceCsafe.COMMAND command) {
        Log.d(TAG, "onGetUtilization: ");
    }

    @Override
    public void onGetMotorCurrent(DeviceCsafe.COMMAND command) {
        Log.d(TAG, "onGetMotorCurrent: ");
    }

    @Override
    public void onGetODOMeter(DeviceCsafe.COMMAND command) {
        Log.d(TAG, "onGetODOMeter: ");
    }

    @Override
    public void onGetErrorCode(DeviceCsafe.COMMAND command) {
        Log.d(TAG, "onGetErrorCode: ");
    }

    @Override
    public void onGetServiceCode(DeviceCsafe.COMMAND command) {
        Log.d(TAG, "onGetServiceCode: ");
    }

    @Override
    public void onGetUserCFG1(DeviceCsafe.COMMAND command) {
        Log.d(TAG, "onGetUserCFG1: ");
    }

    @Override
    public void onGetUserCFG2(DeviceCsafe.COMMAND command) {
        Log.d(TAG, "onGetUserCFG2: ");
    }

    @Override
    public void onGetTWork(DeviceCsafe.COMMAND command) {
        Log.d(TAG, "onGetTWork: ");
    }

    @Override
    public void onGetHorizontal(DeviceCsafe.COMMAND command) {
        Log.d(TAG, "onGetHorizontal: ");
    }

    @Override
    public void onGetVertical(DeviceCsafe.COMMAND command) {
        Log.d(TAG, "onGetVertical: ");
    }

    @Override
    public void onGetCalories(DeviceCsafe.COMMAND command) {
        Log.d(TAG, "onGetCalories: ");
    }

    @Override
    public void onGetProgram(DeviceCsafe.COMMAND command) {
        Log.d(TAG, "onGetProgram: ");
    }

    @Override
    public void onGetSpeed(DeviceCsafe.COMMAND command) {
        Log.d(TAG, "onGetSpeed: ");
    }

    @Override
    public void onGetPace(DeviceCsafe.COMMAND command) {
        Log.d(TAG, "onGetPace: ");
    }

    @Override
    public void onGetCadence(DeviceCsafe.COMMAND command) {
        Log.d(TAG, "onGetCadence: ");
    }

    @Override
    public void onGetGrade(DeviceCsafe.COMMAND command) {
        Log.d(TAG, "onGetGrade: ");
    }

    @Override
    public void onGetGear(DeviceCsafe.COMMAND command) {
        Log.d(TAG, "onGetGear: ");
    }

    @Override
    public void onGetUpList(DeviceCsafe.COMMAND command) {
        Log.d(TAG, "onGetUpList: ");
    }

    @Override
    public void onGetUserInfo(DeviceCsafe.COMMAND command) {
        Log.d(TAG, "onGetUserInfo: ");
    }

    @Override
    public void onGetTorque(DeviceCsafe.COMMAND command) {
        Log.d(TAG, "onGetTorque: ");
    }

    @Override
    public void onGetHARCurrent(DeviceCsafe.COMMAND command) {
        Log.d(TAG, "onGetHARCurrent: ");
    }

    @Override
    public void onGetHRTone(DeviceCsafe.COMMAND command) {
        Log.d(TAG, "onGetHRTone: ");
    }

    @Override
    public void onGetMets(DeviceCsafe.COMMAND command) {
        Log.d(TAG, "onGetMets: ");
    }

    @Override
    public void onGetPower(DeviceCsafe.COMMAND command) {
        Log.d(TAG, "onGetPower: ");
    }

    @Override
    public void onGetHRAvg(DeviceCsafe.COMMAND command) {
        Log.d(TAG, "onGetHRAvg: ");
    }

    @Override
    public void onGetHRMax(DeviceCsafe.COMMAND command) {
        Log.d(TAG, "onGetHRMax: ");
    }

    @Override
    public void onGetUserData1(DeviceCsafe.COMMAND command) {
        Log.d(TAG, "onGetUserData1: ");
    }

    @Override
    public void onGetUserData2(DeviceCsafe.COMMAND command) {
        Log.d(TAG, "onGetUserData2: ");
    }

    @Override
    public void onGetAudioChannel(DeviceCsafe.COMMAND command) {
        Log.d(TAG, "onGetAudioChannel: ");
    }

    @Override
    public void onGetAudioVolume(DeviceCsafe.COMMAND command) {
        Log.d(TAG, "onGetAudioVolume: ");
    }

    @Override
    public void onGetAudioMute(DeviceCsafe.COMMAND command) {
        Log.d(TAG, "onGetAudioMute: ");
    }

    @Override
    public void onGetCaps(DeviceCsafe.COMMAND command) {
        Log.d(TAG, "onGetCaps: ");
    }

    @Override
    public void onGetInfo(DeviceCsafe.COMMAND command) {
        Log.d(TAG, "onGetInfo: ");
    }

    @Override
    public void onGetErrorString(DeviceCsafe.COMMAND command) {
        Log.d(TAG, "onGetErrorString: ");
    }

    @Override
    public void onEchoTest(DeviceCsafe.COMMAND command, DeviceCsafe.ECHO_STATUS echo_status) {
        Log.d(TAG, "onEchoTest: " + command);
        LiveEventBus.get(EVENT_CSAFE_ECHO_TEST).post(echo_status);
    }
}
