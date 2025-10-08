package com.dyaco.spirit_commercial.listener;

import static com.dyaco.spirit_commercial.App.getApp;
import static com.dyaco.spirit_commercial.MainActivity.TV_TUNER_VOLUME;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.GET_TV_CHANNEL_LIST;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.GET_TV_CURRENT_CHANNEL;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.GET_TV_SUBTITLE_OPTION;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.TV_TUNER_SCAN_DONE;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.TV_TUNER_ANALOG_CABLE;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.TV_TUNER_SIGNAL_DIGITAL;

import android.util.Log;

import com.corestar.libs.device.DeviceTvTuner;
import com.corestar.libs.tvtuner.ATVChannelSignalInformation;
import com.corestar.libs.tvtuner.Channel;
import com.corestar.libs.tvtuner.DTVChannelModulationInformation;
import com.corestar.libs.tvtuner.ProgramElementaryStreamInformation;
import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingBean;
import com.jeremyliao.liveeventbus.LiveEventBus;

import java.util.Arrays;

public class TvTunerDeviceEventListener implements DeviceTvTuner.DeviceEventListener {
    private static final String TAG = "TvTuner";
    private final DeviceTvTuner deviceTvTuner;


//    public static boolean isG = false;

    public TvTunerDeviceEventListener(DeviceTvTuner deviceTvTuner) {
        this.deviceTvTuner = deviceTvTuner;
    }

    @Override
    public void onConnectFail() {
        Log.d(TAG, "onConnectFail: ");
    }

    @Override
    public void onConnected() {
        Log.d(TAG, "onConnected:TvTuner連線 ");

        MainActivity.isTvOn = false;

        deviceTvTuner.getFirmwareVersion(); //onGetFirmwareVersion

//        deviceTvTuner.getTvMode(); //> onGetTvMode


     //   deviceTvTuner.setDigitalVideoOutputResolution(DeviceTvTuner.VIDEO_RESOLUTION.r1080p50Hz);
    }


    @Override
    public void onGetFirmwareVersion(DeviceTvTuner.COMMAND command, String s) {
        Log.d(TAG, "onGetFirmwareVersion: " + s);

        if (s == null || "".equals(s)){
            MainActivity.isTvOn = false;
            return;
        }
        MainActivity.isTvOn = true;
        deviceTvTuner.getTvMode(); //> onGetTvMode


        deviceTvTuner.setDigitalVideoOutputResolution(DeviceTvTuner.VIDEO_RESOLUTION.r1080p50Hz);
    }

    // DTV_MODE: 數位，ATV_CABLE_MODE: 類比有線，ATV_AIR_MODE: 類比無線
    @Override
    public void onGetTvMode(DeviceTvTuner.COMMAND command, DeviceTvTuner.TV_MODE tv_mode) {
        DeviceSettingBean dsb = getApp().getDeviceSettingBean();

        setDeviceTvMode(dsb, tv_mode);

        if (dsb.getTvCountry() != null && !"".equals(dsb.getTvCountry().name())) {
            deviceTvTuner.setTvCountry(dsb.getTvCountry());
        } else {
            deviceTvTuner.getTvCountry();
        }


        if (dsb.getTvTunerVolume() == 0) {
            TV_TUNER_VOLUME = 10;
            dsb.setTvTunerVolume(TV_TUNER_VOLUME);
            getApp().setDeviceSettingBean(dsb);
        } else {
            TV_TUNER_VOLUME = dsb.getTvTunerVolume();
        }
        deviceTvTuner.setVolume(TV_TUNER_VOLUME);
        Log.d(TAG, "TvTuner音量: " + dsb.getTvTunerVolume());
    }

    // DTV_MODE: 數位，ATV_CABLE_MODE: 類比有線，ATV_AIR_MODE: 類比無線
    private void setDeviceTvMode(DeviceSettingBean dsb, DeviceTvTuner.TV_MODE tv_mode) {
        int signal = dsb.getTvTunerSignal(); //0 Digital Signal, 1 Analog Signal
        int cableAir = dsb.getTvTunerAnalogType(); //0 cable, 1 air


        //數位
        if (signal == TV_TUNER_SIGNAL_DIGITAL) {
            if (tv_mode == DeviceTvTuner.TV_MODE.DTV_MODE) {
                //裝置存的是數位，取得的也是數位
                return;
            }
            tv_mode = DeviceTvTuner.TV_MODE.DTV_MODE;
        } else {
            if (cableAir == TV_TUNER_ANALOG_CABLE) {//ATV_CABLE_MODE: 類比有線
                if (tv_mode == DeviceTvTuner.TV_MODE.ATV_CABLE_MODE) {
                    //裝置存的是類比有線，取得的也是類比有線
                    return;
                }
                tv_mode = DeviceTvTuner.TV_MODE.ATV_CABLE_MODE;
            } else {//ATV_AIR_MODE: 類比無線

                if (tv_mode == DeviceTvTuner.TV_MODE.ATV_AIR_MODE) {
                    return;
                }

                tv_mode = DeviceTvTuner.TV_MODE.ATV_AIR_MODE;
            }
        }

        deviceTvTuner.setTvMode(tv_mode);

    }

    // 取得國家
    @Override
    public void onGetTvCountry(DeviceTvTuner.COMMAND command, DeviceTvTuner.TV_COUNTRY tv_country) {

        //   if (tv_country == null || tv_country == DeviceTvTuner.TV_COUNTRY.Unknown) return;
        if (tv_country == null) return;

        if (getApp().getDeviceSettingBean().getTvCountry() == null || "".equals(getApp().getDeviceSettingBean().getTvCountry().name())) {
            DeviceSettingBean deviceSettingBean = getApp().getDeviceSettingBean();
            deviceSettingBean.setTvCountry(tv_country);
            getApp().setDeviceSettingBean(deviceSettingBean);
        }
    }

    @Override
    public void onGetScanStatus(DeviceTvTuner.COMMAND command, boolean b) {

//        //false 掃描完畢
        if (!b) {
            LiveEventBus.get(TV_TUNER_SCAN_DONE).post(true);
        }
    }

    //數位的頻道
    @Override
    public void onGetDTVChannelListExtension(DeviceTvTuner.COMMAND command, Channel[] channels) {
        LiveEventBus.get(GET_TV_CHANNEL_LIST).post(channels);
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
    public void onCommandTimeout(DeviceTvTuner.COMMAND command) {

    }

    //類比的頻道
    @Override
    public void onGetChannelList(DeviceTvTuner.COMMAND command, Channel[] channels) {
        LiveEventBus.get(GET_TV_CHANNEL_LIST).post(channels);
    }

    @Override
    public void onGetChannelCount(DeviceTvTuner.COMMAND command, int i) {
        Log.d(TAG, "onGetChannelCount: ");
    }

    @Override
    public void onGetCurrentChannelNumber(DeviceTvTuner.COMMAND command, int i) {
        Log.d(TAG, "onGetCurrentChannelNumber: " + i);
        LiveEventBus.get(GET_TV_CURRENT_CHANNEL).post(i);
    }

    @Override
    public void onGetResultOfLastCommand(DeviceTvTuner.COMMAND command, DeviceTvTuner.COMMAND command1, DeviceTvTuner.COMMAND_RESULT command_result) {
        Log.d(TAG, "onGetResultOfLastCommand: ");
    }

    @Override
    public void onGetProgramStatus(DeviceTvTuner.COMMAND command, DeviceTvTuner.SIGNAL_LOCK_INDICATOR signal_lock_indicator, DeviceTvTuner.SIGNAL_WEAK_INDICATOR signal_weak_indicator, DeviceTvTuner.PROGRAM_CHANGE_INDICATOR program_change_indicator) {
        Log.d(TAG, "onGetProgramStatus: ");
    }

    @Override
    public void onGetTvCardModel(DeviceTvTuner.COMMAND command, DeviceTvTuner.TV_CARD_MODEL tv_card_model) {
        Log.d(TAG, "onGetTvCardModel: ");
    }

    @Override
    public void onGetProgramElementaryStreamInformation(DeviceTvTuner.COMMAND command, ProgramElementaryStreamInformation programElementaryStreamInformation) {
        Log.d(TAG, "onGetProgramElementaryStreamInformation: ");
    }

    @Override
    public void onGetProgramSubtitleAvailableLanguageList(DeviceTvTuner.COMMAND command, String[] strings) {
        Log.d(TAG, "onGetProgramSubtitleAvailableLanguageList: ");
    }

    @Override
    public void onGetFlashProgrammingStatus(DeviceTvTuner.COMMAND command, DeviceTvTuner.FLASH_PROGRAMMING_STATUS flash_programming_status) {
        Log.d(TAG, "onGetFlashProgrammingStatus: ");
    }

    @Override
    public void onGetUSBStorageAttachmentStatus(DeviceTvTuner.COMMAND command, boolean b) {
        Log.d(TAG, "onGetUSBStorageAttachmentStatus: ");
    }

    @Override
    public void onGetUSBStorageAvailableFirmwareVersion(DeviceTvTuner.COMMAND command, String s) {
        Log.d(TAG, "onGetUSBStorageAvailableFirmwareVersion: ");
    }

    @Override
    public void onGetNumberOfUARTCommandInQueue(DeviceTvTuner.COMMAND command, int i) {
        Log.d(TAG, "onGetNumberOfUARTCommandInQueue: ");
    }

    @Override
    public void onGetExportTvChannelDataStatus(DeviceTvTuner.COMMAND command, DeviceTvTuner.EXPORT_IMPORT_TV_CHANNEL_DATA_STATUS export_import_tv_channel_data_status) {
        Log.d(TAG, "onGetExportTvChannelDataStatus: ");
    }

    @Override
    public void onGetImportTvChannelDataStatus(DeviceTvTuner.COMMAND command, DeviceTvTuner.EXPORT_IMPORT_TV_CHANNEL_DATA_STATUS export_import_tv_channel_data_status) {
        Log.d(TAG, "onGetImportTvChannelDataStatus: ");
    }

    //取得當前頻道的可用字幕清單 getSubtitleOption
    @Override
    public void onGetSubtitleOption(DeviceTvTuner.COMMAND command, DeviceTvTuner.SUBTITLE_OPTION subtitle_option) {

        LiveEventBus.get(GET_TV_SUBTITLE_OPTION).post(subtitle_option);

        Log.d(TAG, "onGetSubtitleOption:取得當前頻道的可用字幕清單: " + subtitle_option.toString());
    }

    @Override
    public void onGetTvCardHardwareVersion(DeviceTvTuner.COMMAND command, String s) {
        Log.d(TAG, "onGetTvCardHardwareVersion: ");
    }

    @Override
    public void onExportTvChannelDataToHost(DeviceTvTuner.COMMAND command, byte[] bytes) {
        Log.d(TAG, "onExportTvChannelDataToHost: ");
    }

    @Override
    public void onGetATVChannelVideoLockStatus(DeviceTvTuner.COMMAND command, boolean b) {
        Log.d(TAG, "onGetATVChannelVideoLockStatus: ");
    }

    @Override
    public void onGetSource(DeviceTvTuner.COMMAND command, DeviceTvTuner.SOURCE_TYPE source_type) {
        Log.d(TAG, "onGetSource: ");
    }

    @Override
    public void onTransferAndUpdateFirmwareFromHost(DeviceTvTuner.COMMAND command, DeviceTvTuner.UPDATE_FIRMWARE_RESPONSE update_firmware_response) {
        Log.d(TAG, "onTransferAndUpdateFirmwareFromHost: ");
    }

    @Override
    public void onGetDVBCSymbolRate(DeviceTvTuner.COMMAND command, int i) {
        Log.d(TAG, "onGetDVBCSymbolRate: ");
    }

    @Override
    public void onGetDTVChannelSignalInformation(DeviceTvTuner.COMMAND command, DeviceTvTuner.SIGNAL_LOCK_INDICATOR signal_lock_indicator, int i, int i1, int i2) {
        Log.d(TAG, "onGetDTVChannelSignalInformation: ");
    }

    @Override
    public void onGetDTVChannelModulationInformation(DeviceTvTuner.COMMAND command, DTVChannelModulationInformation dtvChannelModulationInformation) {
        Log.d(TAG, "onGetDTVChannelModulationInformation: ");
    }

    @Override
    public void onGetMMPFilePlayInformation(DeviceTvTuner.COMMAND command, DeviceTvTuner.PLAYBACK_CONTROL playback_control, int i, int i1) {
        Log.d(TAG, "onGetMMPFilePlayInformation: ");
    }

    @Override
    public void onGetATVChannelSignalInformation(DeviceTvTuner.COMMAND command, ATVChannelSignalInformation atvChannelSignalInformation) {
        Log.d(TAG, "onGetATVChannelSignalInformation: ");
    }

    @Override
    public void onGetFlashProgrammingResult(DeviceTvTuner.COMMAND command, DeviceTvTuner.FLASH_PROGRAMMING_RESULT flash_programming_result) {
        Log.d(TAG, "onGetFlashProgrammingResult: ");
    }

    @Override
    public void onGetWatchdogRuntimeVerificationNumber(DeviceTvTuner.COMMAND command, int i) {
        Log.d(TAG, "onGetWatchdogRuntimeVerificationNumber: ");
    }

    @Override
    public void onGetSystemImageVersionString(DeviceTvTuner.COMMAND command, String s) {
        Log.d(TAG, "onGetSystemImageVersionString: ");
    }
}
