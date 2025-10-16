package com.dyaco.spirit_commercial.listener;


import static com.corestar.libs.device.DeviceGEM.CENTRAL_DISCONNECT_REASON.CENTRAL_SOLICITED;
import static com.corestar.libs.device.DeviceGEM.CENTRAL_DISCONNECT_REASON.PERIPHERAL_SOLICITED;
import static com.dyaco.spirit_commercial.App.MODE;
import static com.dyaco.spirit_commercial.App.UNIT_E;
import static com.dyaco.spirit_commercial.App.getApp;
import static com.dyaco.spirit_commercial.App.getDeviceGEM;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.DEVICE_TYPE_ELLIPTICAL;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.DEVICE_TYPE_RECUMBENT_BIKE;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.DEVICE_TYPE_TREADMILL;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.DEVICE_TYPE_UPRIGHT_BIKE;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.METRIC;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.APPLE_WATCH_CLOSE_WINDOW;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.DISCOVERY_TIMEOUT;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.EVENT_NFC_ENABLED;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.EVENT_NFC_READ;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.FTMS_SET_TARGET_INCLINATION;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.FTMS_SET_TARGET_POWER;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.FTMS_SET_TARGET_RESISTANCE_LEVEL;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.FTMS_SET_TARGET_SPEED;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.FTMS_START_OR_RESUME;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.FTMS_STOP_OR_PAUSE;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.ON_CUSTOM_EVENT_HEART_RATE_DEVICES_DISCOVERED;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.SAMSUNG_WATCH_CLOSE_WINDOW;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.SHOW_SAMSUNG_DISCONNECTED;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.BLE_DEVICE_EVENT_CONNECTED;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.BLE_DEVICE_EVENT_NORMAL;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.BLE_DEVICE_EVENT_RESCAN;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.BLE_DEVICE_EVENT_WARRING;

import android.util.Log;

import com.corestar.libs.device.DeviceGEM;
import com.corestar.libs.device.GemSettings;
import com.corestar.libs.utils.GemSettingsFactory;
import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.product_flavor.DeviceSettingCheck2;
import com.dyaco.spirit_commercial.viewmodel.AppStatusViewModel;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingBean;
import com.dyaco.spirit_commercial.viewmodel.WorkoutViewModel;
import com.jeremyliao.liveeventbus.LiveEventBus;

import java.util.List;
import java.util.Map;

import timber.log.Timber;

/**
 * GemKit Gym3
 * <p>
 * 1.已被其他裝置連線的裝置掃描不到
 * 2.若已有連線的裝置，無法執行掃描
 * 3.曾經連過的裝置，即使不在掃描範圍，掃描也會出現
 * 4.藍芽沒斷線就 重build 或 crash 會找不到裝置和無法掃描，只能斷電重啟 > customMessageDisconnectHeartRateDevice() 先斷線
 * <p>
 * <p>
 * ====================================================================================================
 * <p>
 * 1.initGem3()
 * DeviceGEM.GEMEventListener gemEventListener = new DeviceGemEventListener(this, workoutViewModel, appStatusViewModel);
 * getDeviceGEM().setEventListener(gemEventListener);
 * <p>
 * 2.ping
 * getDeviceGEM().systemMessagePing(); > onSystemMessagePing()
 * <p>
 * 3.取MAC
 * getDeviceGEM().bluetoothConfigurationMessageGetDeviceAddress(); > onBluetoothConfigurationMessageGetDeviceAddress
 * <p>
 * 4.設定設備
 * getDeviceGEM().gymConnectMessageSetFitnessEquipmentType(ftmsEquipmentType);
 * <p>
 * 5.設定名稱
 * getDeviceGEM().bluetoothConfigurationMessageSetDeviceName(ftmsName);
 */
public class DeviceGemEventListener implements DeviceGEM.GEMEventListener {

    //  public static final String TAG = "GEM3";
    WorkoutViewModel workoutViewModel;
    AppStatusViewModel appStatusViewModel;
    MainActivity m;


    public DeviceGemEventListener(MainActivity m, WorkoutViewModel workoutViewModel, AppStatusViewModel appStatusViewModel) {
        this.workoutViewModel = workoutViewModel;
        this.m = m;
        this.appStatusViewModel = appStatusViewModel;
    }


    //收到ping成功，不用了
    @Override
    public void onSystemMessagePing(int i) {

//        if (m.pingGemTimer != null) {
//            m.pingGemTimer.cancel();
//            m.pingGemTimer = null;
//        }

//        if (MainActivity.isGen3PingDone) return;
//        MainActivity.isGen3PingDone = true;

   //     Log.d("GEM3", "ping成功 onSystemMessagePing: ");


//        //全部斷線
//       getDeviceGEM().customMessageDisconnectHeartRateDevice();
//
//
//        new RxTimer().timer(1000, number -> {
//            Log.d("GEM3", "取MAC bluetoothConfigurationMessageGetDeviceAddress: ");
//            getDeviceGEM().bluetoothConfigurationMessageGetDeviceAddress();
//        });
    }

    //取得 Gem3 Mac Address
    // TODO: getDeviceGEM().bluetoothConfigurationMessageGetDeviceAddress(); 以執行 但  onBluetoothConfigurationMessageGetDeviceAddress 沒被呼叫
    @Override
    public void onBluetoothConfigurationMessageGetDeviceAddress(int i, DeviceGEM.ADDRESS_TYPE address_type, String mac) {
        //  mac = new StringBuilder(mac).reverse().toString();

        Timber.tag("GEM3").d(" ⭐MAC:%s", mac);

     //   Log.d("AAASSDDDDD", "⭐️MAC: " + mac);
        DeviceSettingBean deviceSettingBean = getApp().getDeviceSettingBean();
        if (!mac.equals(deviceSettingBean.getMachine_mac())) {//有新MAC，就替換
            deviceSettingBean.setMachine_mac(mac);
        }

        if (!deviceSettingBean.isFtmsNameEdit()) {//還未被手動修改過，要更新
            //CD:E9:A1:17:3D:D2 > CT1000ENT-3DD2 後4Bytes
            mac = mac.replace(":", "");
            deviceSettingBean.setBle_device_name(deviceSettingBean.getModel_name() + "-" + mac.substring(mac.length() - 4));
            Timber.tag("GEM3").d("ftms名稱 尚未被手動修改: ");
        } else {
            Timber.tag("GEM3").d("ftms名稱 已被手動修改: ");
        }
        //  Log.d("GEM3", "ftms名稱: " + deviceSettingBean.getBle_device_name());

        getApp().setDeviceSettingBean(deviceSettingBean);

        m.initFTMS();
    }


    String f1FtmsName = "";

    //F1 APP 要求修改Nick Name 事件, 模組重開
    @Override
    public void onCustomEventSetNickName(String name) {


        if (name == null || name.isEmpty()) return;

        f1FtmsName = name;
        //改名
        //     getDeviceGEM().bluetoothConfigurationMessageSetDeviceName(name); // > onBluetoothConfigurationMessageSetDeviceName

        DeviceGEM.FITNESS_EQUIPMENT_TYPE ftmsEquipmentType = DeviceGEM.FITNESS_EQUIPMENT_TYPE.TREADMILL;
        switch (MODE.getTypeCode()) {
            case DEVICE_TYPE_TREADMILL:
                ftmsEquipmentType = DeviceGEM.FITNESS_EQUIPMENT_TYPE.TREADMILL;
                break;
            case DEVICE_TYPE_ELLIPTICAL:
                ftmsEquipmentType = DeviceGEM.FITNESS_EQUIPMENT_TYPE.CROSS_TRAINER;
                break;
            case DEVICE_TYPE_RECUMBENT_BIKE:
                ftmsEquipmentType = DeviceGEM.FITNESS_EQUIPMENT_TYPE.RECUMBENT_BIKE;
                break;
            case DEVICE_TYPE_UPRIGHT_BIKE:
                ftmsEquipmentType = DeviceGEM.FITNESS_EQUIPMENT_TYPE.BIKE;
                break;
        }

        GemSettings settings = GemSettingsFactory.getInstance(ftmsEquipmentType);
        settings.setDeviceName(name);

        Log.d("GEM3", "onInit: (1) init ");
        getDeviceGEM().init(settings, state -> {
            //gem3啟動
            Log.d("GEM3", "onInit: " + state);
        //    if (state == DeviceGEM.STATE.SUCCESS) {
                //開啟廣播
                getDeviceGEM().bluetoothControlMessageStartAdvertising();
    //        }
        });


        //重新啟動 > onSystemEventPowerUp> 開啟廣播
        //  getDeviceGEM().systemMessageRestart();

    }

    int setFtmsNameTime = 0;

    //FTMS命名成功
    @Override
    public void onBluetoothConfigurationMessageSetDeviceName(int i, DeviceGEM.RESPONSE response) {

//        if (response == DeviceGEM.RESPONSE.SUCCESS) {
//
//            //F1改名
//            if (!"".equals(f1FtmsName)) {
//                DeviceSettingBean deviceSettingBean = getApp().getDeviceSettingBean();
//                deviceSettingBean.setBle_device_name(f1FtmsName);
//                deviceSettingBean.setFtmsNameEdit(true);
//                getApp().setDeviceSettingBean(deviceSettingBean);
//            }
//            f1FtmsName = "";
//
//            //重新啟動
//            getDeviceGEM().systemMessageRestart();
//        } else {
//            if (setFtmsNameTime > 3) {
//                //重新啟動
//                getDeviceGEM().systemMessageRestart();
//                setFtmsNameTime = 0;
//            } else {
//                getDeviceGEM().bluetoothConfigurationMessageSetDeviceName(getApp().getDeviceSettingBean().getBle_device_name());
//                setFtmsNameTime++;
//            }
//        }

        //開啟廣播
        // getDeviceGEM().bluetoothControlMessageStartAdvertising();
    }

    //F2 APP 要求取得ODO事件 總里程
    @Override
    public void onCustomEventGetOdo() {

        DeviceSettingCheck2.sendOdoData();
    }


    /**
     * F3 APP 要求取得所有Log 事件
     */
    @Override
    public void onCustomEventGetLog() {

        // 從資料庫回傳Log 資料給APP
        DeviceSettingCheck2.sendError();

        //     MACHINE_LOG[] logs = new MACHINE_LOG[]{MACHINE_LOG.BIKE_CMD_ERROR,MACHINE_LOG.BIKE_CMD_ERROR,MACHINE_LOG.BIKE_REPLACE_BELT};
        //   getDeviceGEM().customMessageSendLog(logs);

    }

    @Override
    public void onSystemMessageShutdown(int i, DeviceGEM.RESPONSE response) {
//                if (pingTimer != null) {
//                    pingTimer.cancel();
//                    pingTimer = null;
//                }
    }

    @Override
    public void onSystemMessageGetGemModuleVersionInformation(int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, int i11) {
    }

    @Override
    public void onSystemMessageGetSerialNumber(int i, DeviceGEM.RESPONSE response, String s) {
    }

    @Override
    public void onSystemMessageDisableGemHciInterface(int i, DeviceGEM.RESPONSE response) {
    }

    @Override
    public void onSystemMessageConfigureBaudRate(int i, DeviceGEM.RESPONSE response) {
    }

    @Override
    public void onSystemMessageStartRadioTest(int i, DeviceGEM.RESPONSE response) {
    }

    @Override
    public void onSystemMessageStopRadioTest(int i, DeviceGEM.RESPONSE response) {
    }

    @Override
    public void onSystemMessageInitiateBootloader(int i, DeviceGEM.RESPONSE response) {
    }

    @Override
    public void onSystemMessageRestart(int i, DeviceGEM.RESPONSE response) {
        if (m.pingGemTimer != null) {
            m.pingGemTimer.cancel();
            m.pingGemTimer = null;
        }
    }

    @Override
    public void onSystemEventPowerUp(int i) {

//        //開啟廣播
//        getDeviceGEM().bluetoothControlMessageStartAdvertising();
//
//
//        appStatusViewModel.isGem3On.set(true);
//        LiveEventBus.get(GYM3_ON_EVENT).post(true);


        //todo APPLE WATCH 新增GEM3開啟後，直接設定成IDLE
//        getDeviceGEM().gymConnectMessageSetMachineStatus(DeviceGEM.FITNESS_EQUIPMENT_STATE.IDLE, new HashMap<>());
//        getDeviceGEM().gymConnectMessageSetTrainingStatus(DeviceGEM.TRAINING_STATUS.IDLE);
    }

    @Override
    public void onSystemEventShutdown(int i) {
    }

    @Override
    public void onSystemEventBootloaderInitiated(int i, DeviceGEM.BOOTLOADER_INTERFACE bootloader_interface) {
    }

    @Override
    public void onHardwareMessageGetPinIoConfiguration(int i, Map<Integer, DeviceGEM.PIN_IO_MODE> map) {
    }

    @Override
    public void onHardwareMessageSetPinIoConfiguration(int i, DeviceGEM.RESPONSE response) {
    }

    @Override
    public void onHardwareMessageGetLowFrequencyClockConfiguration(int i, DeviceGEM.LF_CLOCK lf_clock) {
    }

    @Override
    public void onBluetoothControlMessageStartAdvertising(int i, DeviceGEM.RESPONSE response) {
        Log.d("GEM3", "廣播開啟 onBluetoothControlMessageStartAdvertising: ");
    }

    @Override
    public void onBluetoothControlMessageStopAdvertising(int i, DeviceGEM.RESPONSE response) {
    }

    @Override
    public void onBluetoothControlMessageGetBluetoothState(int i, DeviceGEM.BLUETOOTH_STATE bluetooth_state) {
    }

    @Override
    public void onBluetoothControlMessageDisconnectCentral(int i, DeviceGEM.RESPONSE response) {
    }

    @Override
    public void onBluetoothControlMessageEnableBleMultipleConnections(int i, DeviceGEM.RESPONSE response) {

    }

    @Override
    public void onBluetoothControlEventAdvertisingTimeout(int i) {
        //   Log.d(TAG, "BLUETOOTH 廣播 TIME OUT: ");
        getDeviceGEM().bluetoothControlMessageStartAdvertising();
    }


    //APPLE WATCH 或 FTMS 斷線
    @Override
    public void onBluetoothControlEventCentralDisconnected(int i, DeviceGEM.CENTRAL_DISCONNECT_REASON central_disconnect_reason) {

        if (central_disconnect_reason == PERIPHERAL_SOLICITED || central_disconnect_reason == CENTRAL_SOLICITED) {
            if (workoutViewModel.isSamsungWatchConnected.get() || workoutViewModel.isSamsungWatchEnabled.get()) {
                LiveEventBus.get(SHOW_SAMSUNG_DISCONNECTED, Boolean.class).post(true);
            }
        }

        //todo APPLE WATCH 無法判斷是誰斷線
        workoutViewModel.setAppleWatchHr(0);
        workoutViewModel.setAppleWatchCalories(0);
        workoutViewModel.isAppleWatchConnected.set(false);
        workoutViewModel.isAppleWatchEnabled.set(false);

        workoutViewModel.isSamsungWatchConnected.set(false);
        workoutViewModel.isSamsungWatchEnabled.set(false);


        appStatusViewModel.isFtmsConnected.set(false);
        getDeviceGEM().bluetoothControlMessageStartAdvertising();
    }


    // FTMS & APPLE WATCH 連線事件
    @Override
    public void onBluetoothControlEventCentralConnected(int i, int i1, DeviceGEM.ADDRESS_TYPE address_type, String s, DeviceGEM.DEVICE_TYPE device_type) {

        Log.d("SAMSUNGXXX", "onBluetoothControlEventCentralConnected: " + device_type);
        //DEVICE_TYPE.BLE > FTMS
  //      Log.d("GEM3", "onBluetoothControlEventCentralConnected: " + device_type);
//        if (device_type == DeviceGEM.DEVICE_TYPE.APPLE_WATCH || device_type == DeviceGEM.DEVICE_TYPE.SAMSUNG) {
        if (device_type == DeviceGEM.DEVICE_TYPE.APPLE_WATCH) {
            workoutViewModel.isAppleWatchEnabled.set(true);
            workoutViewModel.isAppleWatchConnected.set(true);

            //關閉APPLE WATCH連線視窗
            LiveEventBus.get(APPLE_WATCH_CLOSE_WINDOW, Boolean.class).post(true);
            LiveEventBus.get(SAMSUNG_WATCH_CLOSE_WINDOW, Boolean.class).post(true);
        }


        if (device_type == DeviceGEM.DEVICE_TYPE.SAMSUNG) {
            workoutViewModel.isSamsungWatchEnabled.set(true);
            workoutViewModel.isSamsungWatchConnected.set(true);

            //關閉APPLE WATCH連線視窗
            LiveEventBus.get(APPLE_WATCH_CLOSE_WINDOW, Boolean.class).post(true);
            LiveEventBus.get(SAMSUNG_WATCH_CLOSE_WINDOW, Boolean.class).post(true);
            LiveEventBus.get(SHOW_SAMSUNG_DISCONNECTED, Boolean.class).post(false);
        }



        appStatusViewModel.isFtmsConnected.set(true);

//        //改狀態
//        if (appStatusViewModel.currentStatus.get() == STATUS_IDLE || appStatusViewModel.currentStatus.get() == STATUS_LOGIN_PAGE || appStatusViewModel.currentStatus.get() == STATUS_SUMMARY) {
//            m.updateFtmsMachineStatus2(DeviceGEM.TRAINING_STATUS.IDLE);
//        }
    }

    // APPLE WATCH 卡路里資料事件
    @Override
    public void onGymConnectEventCalorieValueReceived(int uid, int totalCalories, int activeCalories, int currentCalorieRate, DeviceGEM.VALUE_SOURCE value_source) {
        //todo APPLE WATCH
 //       Log.d("GEM3", "onGymConnectEventCalorieValueReceived: " + currentCal);
        workoutViewModel.setAppleWatchCalories(activeCalories);
    }

    // APPLE WATCH 回傳心跳資料事件
    @Override
    public void onGymConnectEventHeartRateValueReceived(int uid, int currentHr, int avgHr, DeviceGEM.VALUE_SOURCE value_source) {
        //todo APPLE WATCH
  //      Log.d("GEM3", "onGymConnectEventHeartRateValueReceived: " + workoutViewModel.isAppleWatchEnabled.get()  +", "+ currentHr);
        if (workoutViewModel.isAppleWatchEnabled.get()) {
            workoutViewModel.setAppleWatchHr(currentHr > 300 ? 0 : currentHr);
        } else {
            workoutViewModel.setAppleWatchHr(0);
        }

        if (workoutViewModel.isSamsungWatchEnabled.get()) {
            workoutViewModel.setSamsungWatchHr(currentHr > 300 ? 0 : currentHr);
        } else {
            workoutViewModel.setSamsungWatchHr(0);
        }

        //  if (currentHr > 0)
        //   workoutViewModel.isAppleWatchConnected.set(true);
        //workoutViewModel.isAppleWatchEnabled.set(true);


//        //可被連線
//        getDeviceGEM().gymConnectMessageSetFitnessEquipmentState(DeviceGEM.FITNESS_EQUIPMENT_STATE.IDLE,new HashMap<>());

        //使用中
        //    getDeviceGEM().gymConnectMessageSetFitnessEquipmentState(DeviceGEM.FITNESS_EQUIPMENT_STATE.IN_USE,new HashMap<>());
    }

    @Override
    public void onBluetoothControlEventReconnectionAttemptTimeout(int i) {
    }

    @Override
    public void onBluetoothConfigurationMessageGetDeviceName(int i, String s) {
    }

    @Override
    public void onBluetoothConfigurationMessageGetAdvertisingRateAndTimeout(int i, int i1, int i2, int i3, int i4) {
    }

    @Override
    public void onBluetoothConfigurationMessageSetAdvertisingRateAndTimeout(int i, DeviceGEM.RESPONSE response) {
    }

    @Override
    public void onBluetoothConfigurationMessageSetDeviceAddress(int i, DeviceGEM.RESPONSE response) {
    }

    @Override
    public void onBluetoothConfigurationMessageGetRadioTransmitPower(int i, int i1) {
    }

    @Override
    public void onBluetoothConfigurationMessageSetRadioTransmitPower(int i, DeviceGEM.RESPONSE response) {
    }

    @Override
    public void onBluetoothConfigurationMessageGetConnectionInterval(int i, int i1, int i2) {
    }

    @Override
    public void onBluetoothConfigurationMessageSetConnectionInterval(int i, DeviceGEM.RESPONSE response) {
    }

    @Override
    public void onBluetoothConfigurationMessageGetManufacturerSpecificAdvertisingData(int i, byte[] bytes) {
    }

    @Override
    public void onBluetoothConfigurationMessageSetManufacturerSpecificAdvertisingData(int i, DeviceGEM.RESPONSE response) {
    }

    @Override
    public void onBluetoothDeviceInformationMessageGetManufacturerName(int i, String s) {
    }

    @Override
    public void onBluetoothDeviceInformationMessageSetManufacturerName(int i, DeviceGEM.RESPONSE response) {
    }

    @Override
    public void onBluetoothDeviceInformationMessageGetModelNumber(int i, String s) {
    }

    @Override
    public void onBluetoothDeviceInformationMessageSetModelNumber(int i, DeviceGEM.RESPONSE response) {
    }

    @Override
    public void onBluetoothDeviceInformationMessageGetSerialNumber(int i, String s) {
    }

    @Override
    public void onBluetoothDeviceInformationMessageSetSerialNumber(int i, DeviceGEM.RESPONSE response) {
    }

    @Override
    public void onBluetoothDeviceInformationMessageGetHardwareRevision(int i, String s) {
    }

    @Override
    public void onBluetoothDeviceInformationMessageSetHardwareRevision(int i, DeviceGEM.RESPONSE response) {
    }

    @Override
    public void onBluetoothDeviceInformationMessageGetFirmwareRevision(int i, String s) {
    }

    @Override
    public void onBluetoothDeviceInformationMessageSetFirmwareRevision(int i, DeviceGEM.RESPONSE response) {
    }

    @Override
    public void onBluetoothDeviceInformationMessageGetBatteryServiceIncluded(int i, boolean b) {
    }

    @Override
    public void onBluetoothDeviceInformationMessageSetBatteryServiceIncluded(int i, DeviceGEM.RESPONSE response) {
    }

    @Override
    public void onBluetoothDeviceInformationMessageGetReportedBatteryLevel(int i, int i1) {
    }

    @Override
    public void onBluetoothDeviceInformationMessageSetReportedBatteryLevel(int i, DeviceGEM.RESPONSE response) {
    }

    @Override
    public void onBluetoothDeviceInformationMessageGetPnpId(int i, DeviceGEM.VENDOR_SOURCE_ID vendor_source_id, int i1, int i2, int i3) {
    }

    @Override
    public void onBluetoothDeviceInformationMessageSetPnpId(int i, DeviceGEM.RESPONSE response) {
    }

    @Override
    public void onBluetoothDeviceInformationMessageGetBikeSpeedAndCadenceServiceIncluded(int i, boolean b) {
    }

    @Override
    public void onBluetoothDeviceInformationMessageSetBikeSpeedAndCadenceServiceIncluded(int i, DeviceGEM.RESPONSE response) {
    }

    @Override
    public void onBluetoothDeviceInformationMessageGetBikePowerServiceIncluded(int i, boolean b) {
    }

    @Override
    public void onBluetoothDeviceInformationMessageSetBikePowerServiceIncluded(int i, DeviceGEM.RESPONSE response) {
    }

    @Override
    public void onBluetoothLowEnergyReceiverMessageStartDiscovery(int i, DeviceGEM.BLE_SERVICE ble_service, DeviceGEM.RESPONSE response) {
//                if (response != DeviceGEM.RESPONSE.SUCCESS) {
//                    //掃描失敗
//                    LiveEventBus.get(DISCOVERY_TIMEOUT).post(false);
//                } else {
        LiveEventBus.get(DISCOVERY_TIMEOUT).post(response == DeviceGEM.RESPONSE.SUCCESS);
        //    }
    }

    @Override
    public void onBluetoothLowEnergyReceiverMessageStopDiscovery(int i, DeviceGEM.BLE_SERVICE ble_service, DeviceGEM.RESPONSE response) {
    }

    @Override
    public void onBluetoothLowEnergyReceiverMessageConnectDevice(int i, DeviceGEM.BLE_SERVICE ble_service, DeviceGEM.RESPONSE response) {
    }

    @Override
    public void onBluetoothLowEnergyReceiverMessageDisconnectDevice(int i, DeviceGEM.BLE_SERVICE ble_service, DeviceGEM.RESPONSE response) {
    }

    @Override
    public void onBluetoothLowEnergyReceiverMessageStartIBeaconDiscovery(int i, DeviceGEM.RESPONSE response) {
    }

    @Override
    public void onBluetoothLowEnergyReceiverEventDiscoveryTimeout(int i, DeviceGEM.BLE_SERVICE ble_service) {
        //   LiveEventBus.get(DISCOVERY_TIMEOUT).post(false);
    }

    @Override
    public void onBluetoothLowEnergyReceiverEventDeviceConnected(int i, DeviceGEM.BLE_SERVICE ble_service, String s) {
    }

    @Override
    public void onBluetoothLowEnergyReceiverEventConnectionFailed(int i, DeviceGEM.BLE_SERVICE ble_service, DeviceGEM.RESPONSE response) {
        //重刷清單
        LiveEventBus.get(ON_CUSTOM_EVENT_HEART_RATE_DEVICES_DISCOVERED).post(BLE_DEVICE_EVENT_WARRING);
    }

    @Override
    public void onBluetoothLowEnergyReceiverEventDeviceDisconnected(int i, DeviceGEM.BLE_SERVICE ble_service, DeviceGEM.PERIPHERAL_DISCONNECT_REASON peripheral_disconnect_reason) {
    }

    @Override
    public void onBluetoothLowEnergyReceiverEventDeviceDiscovered(int i, DeviceGEM.BLE_SERVICE ble_service, String s, DeviceGEM.BLE_NAME_TYPE ble_name_type, String s1, int i1) {
        //   Log.d(TAG, "BLE發現設備: ");
    }

    @Override
    public void onBluetoothLowEnergyReceiverEventIBeaconDiscovery(int i, String s, int i1, String s1, int i2, int i3, DeviceGEM.BLE_NAME_TYPE ble_name_type, String s2, int i4, int i5) {
    }

    @Override
    public void onBluetoothLowEnergyReceiverEventConnectedDeviceBatteryStatusUpdated(int i, DeviceGEM.BLE_SERVICE ble_service, DeviceGEM.BATTERY_STATUS battery_status) {
    }

    //BLE 收到心跳資料的事件
    @Override
    public void onBluetoothLowEnergyReceiverEventConnectedHeartRateMonitorData(int uid, int hr) {
    }

    @Override
    public void onBluetoothLowEnergyReceiverEventConnectedCyclingPowerData(int i, Map<DeviceGEM.CYCLING_POWER_DATA, String> map, Map<String, Object> map1) {
    }

    @Override
    public void onSerialDataServiceMessageSendSerialData(int i, DeviceGEM.RESPONSE response) {
    }

    @Override
    public void onSerialDataServiceMessageRequestMaximumLength(int i, int i1) {
    }

    @Override
    public void onSerialDataServiceMessageEnableMultiConnections(int i, DeviceGEM.RESPONSE response) {
    }

    //自定義
    @Override
    public void onSerialDataServiceEventSerialDataReceived(int i, byte[] bytes) {
    }

    @Override
    public void onSerialDataServiceEventTransmitTxComplete(int i) {
    }

    @Override
    public void onSerialDataServiceEventTransmitTxFail(int i) {
    }

    @Override
    public void onSerialDataServiceEventSerialCommunicationsStarted(int i, int i1) {
    }

    @Override
    public void onSerialDataServiceEventSerialCommunicationsStopped(int i) {
    }

    @Override
    public void onSerialDataServiceEventSerialMtuSizeUpdated(int i, int i1) {
    }

    @Override
    public void onAntPlusControlMessageGetAntPlusProfileEnabled(int i, int i1, DeviceGEM.ANT_PLUS_PROFILE_STATUS ant_plus_profile_status) {
    }

    @Override
    public void onAntPlusControlMessageSetAntPlusProfileEnabled(int i, DeviceGEM.RESPONSE response) {
    }

    @Override
    public void onAntPlusConfigurationMessageGetAntDeviceNumber(int i, DeviceGEM.DEVICE_NUMBER_TYPE device_number_type, int i1) {
    }

    @Override
    public void onAntPlusConfigurationMessageSetAntDeviceNumber(int i, DeviceGEM.RESPONSE response) {
    }

    @Override
    public void onAntPlusConfigurationMessageGetRadioTransmitPower(int i, int i1) {
    }

    @Override
    public void onAntPlusConfigurationMessageSetRadioTransmitPower(int i, DeviceGEM.RESPONSE response) {
    }

    @Override
    public void onAntPlusConfigurationMessageGetReportedBatteryLevel(int i, DeviceGEM.BATTERY_STATUS battery_status, int i1) {
    }

    @Override
    public void onAntPlusConfigurationMessageSetReportedBatteryLevel(int i, DeviceGEM.RESPONSE response) {
    }

    @Override
    public void onAntPlusConfigurationMessageGetHardwareVersion(int i, int i1) {
    }

    @Override
    public void onAntPlusConfigurationMessageSetHardwareVersion(int i, DeviceGEM.RESPONSE response) {
    }

    @Override
    public void onAntPlusConfigurationMessageGetManufacturerIdentifier(int i, int i1) {
    }

    @Override
    public void onAntPlusConfigurationMessageSetManufacturerIdentifier(int i, DeviceGEM.RESPONSE response) {
    }

    @Override
    public void onAntPlusConfigurationMessageGetModelNumber(int i, int i1) {
    }

    @Override
    public void onAntPlusConfigurationMessageSetModelNumber(int i, DeviceGEM.RESPONSE response) {
    }

    @Override
    public void onAntPlusConfigurationMessageGetSoftwareVersion(int i, int i1, int i2) {
    }

    @Override
    public void onAntPlusConfigurationMessageSetSoftwareVersion(int i, DeviceGEM.RESPONSE response) {
    }

    @Override
    public void onAntPlusConfigurationMessageGetSerialNumber(int i, int i1) {
    }

    @Override
    public void onAntPlusConfigurationMessageSetSerialNumber(int i, DeviceGEM.RESPONSE response) {
    }

    @Override
    public void onAntPlusReceiverMessageStartDiscovery(int i, DeviceGEM.ANT_PLUS_PROFILE ant_plus_profile, DeviceGEM.RESPONSE response) {
    }

    @Override
    public void onAntPlusReceiverMessageStopDiscovery(int i, DeviceGEM.ANT_PLUS_PROFILE ant_plus_profile, DeviceGEM.RESPONSE response) {
    }

    @Override
    public void onAntPlusReceiverMessageConnectDevice(int i, DeviceGEM.ANT_PLUS_PROFILE ant_plus_profile, DeviceGEM.RESPONSE response) {
    }

    @Override
    public void onAntPlusReceiverMessageDisconnectDevice(int i, DeviceGEM.ANT_PLUS_PROFILE ant_plus_profile, DeviceGEM.RESPONSE response) {
    }

    @Override
    public void onAntPlusReceiverMessageRequestCalibration(int i, DeviceGEM.ANT_PLUS_PROFILE ant_plus_profile, DeviceGEM.RESPONSE response) {
    }

    @Override
    public void onAntPlusReceiverMessageRequestManufacturerInfo(int i, DeviceGEM.ANT_PLUS_PROFILE ant_plus_profile, DeviceGEM.RESPONSE response, int i1, int i2, int i3, int i4, int i5, int i6) {
    }

    @Override
    public void onAntPlusReceiverMessageRequestBatteryStatus(int i, DeviceGEM.ANT_PLUS_PROFILE ant_plus_profile, DeviceGEM.RESPONSE response, DeviceGEM.BATTERY_STATUS battery_status, int i1, int i2, int i3, int i4) {
    }

    @Override
    public void onAntPlusReceiverMessageSaveSlope(int i, DeviceGEM.RESPONSE response) {
    }

    @Override
    public void onAntPlusReceiverMessageSaveSerial(int i, DeviceGEM.RESPONSE response) {
    }

    @Override
    public void onAntPlusReceiverEventDiscoveryTimeout(int i, DeviceGEM.ANT_PLUS_PROFILE ant_plus_profile) {
        LiveEventBus.get(DISCOVERY_TIMEOUT).post(false);
    }

    @Override
    public void onAntPlusReceiverEventDeviceConnected(int i, DeviceGEM.ANT_PLUS_PROFILE ant_plus_profile, int i1) {
    }

    @Override
    public void onAntPlusReceiverEventConnectDeviceTimeout(int i, DeviceGEM.ANT_PLUS_PROFILE ant_plus_profile) {

        //重刷清單
        LiveEventBus.get(ON_CUSTOM_EVENT_HEART_RATE_DEVICES_DISCOVERED).post(BLE_DEVICE_EVENT_WARRING);
    }

    @Override
    public void onAntPlusReceiverEventDeviceDisconnected(int i, DeviceGEM.ANT_PLUS_PROFILE ant_plus_profile, DeviceGEM.PERIPHERAL_DISCONNECT_REASON peripheral_disconnect_reason) {
    }

    @Override
    public void onAntPlusReceiverEventDeviceDiscovered(int i, DeviceGEM.ANT_PLUS_PROFILE ant_plus_profile, int i1, int i2) {
    }

    @Override
    public void onAntPlusReceiverEventAcknowledgedMessage(int i, DeviceGEM.ANT_PLUS_PROFILE ant_plus_profile, boolean b) {
    }

    @Override
    public void onAntPlusReceiverEventConnectedDeviceSerialNumberUpdated(int i, DeviceGEM.ANT_PLUS_PROFILE ant_plus_profile, int i1) {
    }

    @Override
    public void onAntPlusReceiverEventConnectedDeviceBatteryStatusUpdated(int i, DeviceGEM.ANT_PLUS_PROFILE ant_plus_profile, DeviceGEM.BATTERY_STATUS battery_status) {
    }

    @Override
    public void onAntPlusReceiverEventConnectedHeartRateMonitorData(int i, int i1) {
    }

    @Override
    public void onAntPlusReceiverEventConnectedBikePowerData(int i, int i1, int i2) {
    }

    @Override
    public void onAntPlusReceiverEventBikePowerCalibrationResponse(int i, boolean b, DeviceGEM.AUTO_ZERO_STATUS auto_zero_status, DeviceGEM.AUTO_ZERO_STATUS auto_zero_status1, int i1) {
    }

    @Override
    public void onAntPlusReceiverEventConnectedBikeSpeedAndDistanceData(int i, int i1, int i2) {
    }

    @Override
    public void onAntPlusReceiverEventConnectedBikeCadenceData(int i, int i1) {
    }

    @Override
    public void onAntPlusReceiverEventConnectedFeCBasicResistanceChange(int i, int i1) {
    }

    @Override
    public void onAntPlusReceiverEventConnectedFeCPowerChange(int i, int i1) {
    }

    @Override
    public void onAntPlusReceiverEventConnectedFeCWindResistanceChange(int i, int i1, int i2, int i3) {
    }

    @Override
    public void onAntPlusReceiverEventConnectedFeCTrackResistanceChange(int i, int i1, int i2) {
    }

    @Override
    public void onAntPlusReceiverEventConnectedFeCUserConfigurationChange(int i, int i1, int i2, int i3, int i4, int i5) {
    }

    @Override
    public void onAntBroadcastMeshMessageEnableAntBroadcastMesh(int i, DeviceGEM.RESPONSE response) {
    }

    @Override
    public void onAntBroadcastMeshMessageDisableAntBroadcastMesh(int i, DeviceGEM.RESPONSE response) {
    }

    @Override
    public void onAntBroadcastMeshMessageSetBroadcastStateData(int i, DeviceGEM.RESPONSE response) {
    }

    @Override
    public void onAntBroadcastMeshMessageGetBroadcastStateData(int i, int i1, int i2, int i3, int i4) {
    }

    @Override
    public void onAntBroadcastMeshMessageControlAntBroadcastMeshStateChange(int i, DeviceGEM.RESPONSE response) {
    }

    @Override
    public void onAntBroadcastMeshEventAntBroadcastMeshDataStateChange(int i, int i1, int i2, int i3, int i4) {
    }

    @Override
    public void onNfcMessageEnableNfcRadio(int i, DeviceGEM.RESPONSE response) {
        LiveEventBus.get(EVENT_NFC_ENABLED).post(response == DeviceGEM.RESPONSE.SUCCESS);
    }

    @Override
    public void onNfcMessageDisableNfcRadio(int i, DeviceGEM.RESPONSE response) {
    }

    @Override
    public void onNfcMessageSetNfcReaderScanPeriod(int i, DeviceGEM.RESPONSE response) {
    }

    @Override
    public void onNfcMessageGetNfcReaderScanPeriod(int i, int i1) {
    }

    @Override
    public void onNfcMessageSetNfcRadioTestMode(int i, DeviceGEM.RESPONSE response) {
    }

    @Override
    public void onNfcMessageSetNfcTagConfiguration(int i, DeviceGEM.RESPONSE response) {
    }

    @Override
    public void onNfcMessageGetNfcTagBoardConfiguration(int i) {
    }

    @Override
    public void onNfcMessageNfcTagBoardCommsCheck(int i, boolean b) {
    }

    @Override
    public void onNfcEventNfcEventPairingRequest(int uId) {

    }

    @Override
    public void onNfcEventNfcEventPairingResponse(int uId) {

    }

    @Override
    public void onNfcEventNfcTagBoardTagRead(int i) {
    }

    @Override
    public void onNfcEventNfcRead(DeviceGEM.NFC_TAG_TYPE nfcTagType, DeviceGEM.NFC_TAG_DATA_TYPE nfcTagDataType, String uid) {
        //NFC TAG
        Log.d("EgymUtil", "⭐️onNfcEventNfcRead: " + uid);
        LiveEventBus.get(EVENT_NFC_READ).post(uid);
    }


    @Override
    public void onNfcEventNfcTagBoardRead(int i) {
    }

    @Override
    public void onNfcEventNfcNdefRead(int i, DeviceGEM.NDEF_TNF_TYPE ndef_tnf_type, DeviceGEM.NDEF_RECORD_TYPE ndef_record_type, String uid) {
       //只有Dyaco的 NFC 登入 是用這個
        //Ndef
        //放上去觸發一次，離開超過3秒，再放上去才會再觸發
        //FTMS 連線 nfc不能用
        Log.d("EgymUtil", "⭐⭐onNfcEventNfcNdefRead: " + uid);
        LiveEventBus.get(EVENT_NFC_READ).post(uid);
    }

    @Override
    public void onGymConnectMessageGetFitnessEquipmentType(int i, DeviceGEM.FITNESS_EQUIPMENT_TYPE fitness_equipment_type) {
    }

    @Override
    public void onGymConnectMessageSetFitnessEquipmentType(int i, DeviceGEM.RESPONSE response) {
    }

    @Override
    public void onGymConnectMessageGetFitnessEquipmentState(int i, DeviceGEM.FITNESS_EQUIPMENT_STATE fitness_equipment_state) {
    }


    //FTMS & APPLE Watch 狀態改變
    @Override
    public void onGymConnectMessageSetFitnessEquipmentState(int i, DeviceGEM.RESPONSE response) {
    }

    @Override
    public void onGymConnectMessageGetFitnessEquipmentWorkoutProgramName(int i, String s) {
    }

    @Override
    public void onGymConnectMessageSetFitnessEquipmentWorkoutProgramName(int i, DeviceGEM.RESPONSE response) {
    }

    @Override
    public void onGymConnectMessageGetStaticMovementLength(int i, int i1) {
    }

    @Override
    public void onGymConnectMessageSetStaticMovementLength(int i, DeviceGEM.RESPONSE response) {
    }


    //gymConnectMessageUpdateWorkoutData
    @Override
    public void onGymConnectMessageUpdateWorkoutData(int i, DeviceGEM.RESPONSE response) {
        //更新機器的各項數值，如速度、階數、阻力、功率等
        // Log.d(TAG, "更新機器的各項數值 回傳狀態: " + i + "," + response);
    }

    @Override
    public void onGymConnectMessageGetGymConnectAdditionalOptions(int i, int i1) {
    }

    @Override
    public void onGymConnectMessageSetGymConnectAdditionalOptions(int i, DeviceGEM.RESPONSE response) {
    }

    @Override
    public void onGymConnectMessageNotifySupportedUploadItemTypes(int i, DeviceGEM.RESPONSE response) {
    }

    @Override
    public void onGymConnectMessageNotifyBeginUploadReceived(int i, DeviceGEM.RESPONSE response) {
    }

    @Override
    public void onGymConnectMessageNotifyUploadChunkReceived(int i, DeviceGEM.RESPONSE response) {
    }

    @Override
    public void onGymConnectMessageNotifyFinishUploadReceived(int i, DeviceGEM.RESPONSE response) {
    }

    @Override
    public void onGymConnectMessageNotifyCancelUploadReceived(int i, DeviceGEM.RESPONSE response) {
    }

    @Override
    public void onGymConnectMessageNotifyUploadError(int i, DeviceGEM.RESPONSE response) {
    }

    @Override
    public void onGymConnectMessageNotifyUploadProcessingAndIssues(int i, DeviceGEM.RESPONSE response) {
    }

    @Override
    public void onGymConnectMessageGetFitnessEquipmentControlFeatures(int i, List<DeviceGEM.EQUIPMENT_CONTROL_FIELD> list) {
    }

    @Override
    public void onGymConnectMessageSetFitnessEquipmentControlFeatures(int i, DeviceGEM.RESPONSE response) {
    }

    @Override
    public void onGymConnectMessageSetSupportedSpeedRange(int i, DeviceGEM.RESPONSE response) {
    }

    @Override
    public void onGymConnectMessageGetSupportedSpeedRange(int i, int i1, int i2, int i3) {
    }

    @Override
    public void onGymConnectMessageSetSupportedInclineControlRange(int i, DeviceGEM.RESPONSE response) {
    }

    @Override
    public void onGymConnectMessageGetSupportedInclineControlRange(int i, int i1, int i2, int i3) {
    }

    @Override
    public void onGymConnectMessageSetSupportedResistanceControlRange(int i, DeviceGEM.RESPONSE response) {
    }

    @Override
    public void onGymConnectMessageGetSupportedResistanceControlRange(int i, int i1, int i2, int i3) {
    }

    @Override
    public void onGymConnectMessageSetSupportedTargetPowerRange(int i, DeviceGEM.RESPONSE response) {
    }

    @Override
    public void onGymConnectMessageGetSupportedTargetPowerRange(int i, int i1, int i2, int i3) {
    }

    @Override
    public void onGymConnectMessageSetSupportedTargetHeartRateControlRange(int i, DeviceGEM.RESPONSE response) {
    }

    @Override
    public void onGymConnectMessageGetSupportedTargetHeartRateControlRange(int i, int i1, int i2, int i3) {
    }


    @Override
    public void onGymConnectMessageNotifyEquipmentControlMessageReceived(int i, DeviceGEM.RESPONSE response) {
        //gymConnectMessageNotifyEquipmentControlMessageReceived() 回傳
        // Log.d(TAG, "回覆指令是否成功: " + i + "," + response);
    }

    @Override
    public void onGymConnectMessageGetTrainingStatus(int i, DeviceGEM.TRAINING_STATUS trainingStatus, DeviceGEM.RESPONSE response) {

    }

    @Override
    public void onGymConnectMessageSetTrainingStatus(int i, DeviceGEM.RESPONSE response) {

    }

    @Override
    public void onGymConnectMessageGetMachineStatus(int i, DeviceGEM.FITNESS_EQUIPMENT_STATE fitnessEquipmentState) {

    }

    @Override
    public void onGymConnectMessageSetMachineStatus(int i, DeviceGEM.RESPONSE response) {

    }

    @Override
    public void onGymConnectEventCadenceValueReceived(int i, int i1) {
    }

    @Override
    public void onGymConnectEventUserInformationReceived(int i, int i1, int i2, DeviceGEM.USER_GENDER user_gender, String s) {
    }

    @Override
    public void onGymConnectEventRequestSupportedUploadItemTypes(int i, boolean b, int i1) {
    }

    @Override
    public void onGymConnectEventBeginUpload(int i, DeviceGEM.UPLOAD_ITEM_TYPE upload_item_type) {
    }

    @Override
    public void onGymConnectEventUploadChunk(int i, int i1, byte[] bytes) {
    }

    @Override
    public void onGymConnectEventFinishUpload(int i, DeviceGEM.UPLOAD_ITEM_TYPE upload_item_type, int i1, int i2) {
    }

    @Override
    public void onGymConnectEventCancelUpload(int i) {
    }

    @Override
    public void onGymConnectEventRequestUploadProcessingAndIssues(int i, int i1) {
    }

    //控制的部分
    @Override
    public void onGymConnectEventEquipmentControl(int i, DeviceGEM.EQUIPMENT_CONTROL_SOURCE equipment_control_source, DeviceGEM.EQUIPMENT_CONTROL_OPERATION equipment_control_operation, Map<DeviceGEM.EQUIPMENT_CONTROL_PARAMETER, Integer> controlParameterMap) {

        //57425  ,BLE_FTMS  ,SET_TARGET_INCLINATION  ,{TARGET_INCLINATION=0}
        //EQUIPMENT_CONTROL_PARAMETER 整包再丟回去
        Integer fValue = 0;
        for (DeviceGEM.EQUIPMENT_CONTROL_PARAMETER p : equipment_control_operation.parameters) {
            fValue = controlParameterMap.get(p);
            fValue = fValue == null ? 0 : fValue;
            break;
        }

        MainActivity.controlParameterMap = controlParameterMap;


        switch (equipment_control_operation) {
            case START_OR_RESUME: // 07

                LiveEventBus.get(FTMS_START_OR_RESUME).post(true);
                break;
            case STOP_OR_PAUSE: // 08 01

                if (fValue == 1) {
                    //stop

                } else {//2
                    //pause

                }
                LiveEventBus.get(FTMS_STOP_OR_PAUSE).post(fValue);
                break;
            case SET_TARGET_SPEED: //( SET_TARGET_SPEED(2, 2)   #[第一個2 > 0x02] [第二個2 > 數值長度 (0100 = 1)]
                //02 0100 > 1
                //02 0C00 > 12
                //02 6400 > 100
                //02 6E00 > 110
                //02 7800 > 120
                //02 7F00 > 127
                //02 A000 > 160

                //02 C201 > 450 /10 > 4.5

                //  fValue = fValue / 10;
                int v = (int) (UNIT_E == METRIC ? fValue / 10f : ((fValue / 10f) / 1.609));

                LiveEventBus.get(FTMS_SET_TARGET_SPEED).post(v);

                break;
            case SET_TARGET_INCLINATION:
                // 03 0500  = 5  > 0.5 %
                // 03 0A00  = 10  > 1 %
                // 03 0F00  = 15 > 1.5 %
                LiveEventBus.get(FTMS_SET_TARGET_INCLINATION).post(fValue / 5);
                break;
            case SET_TARGET_RESISTANCE_LEVEL: //04 0100
                LiveEventBus.get(FTMS_SET_TARGET_RESISTANCE_LEVEL).post(fValue);
                break;
            case SET_TARGET_POWER: //05
                LiveEventBus.get(FTMS_SET_TARGET_POWER).post(fValue);
                break;
            case REQUEST_CONTROL:


                break;

            case SET_TARGET_DISTANCE:
                //(12) 0C 010000 > 1

        }
    }

    // (綜合) 心跳帶掃描事件
    @Override
    public void onCustomEventHeartRateDevicesDiscovered() {
        LiveEventBus.get(ON_CUSTOM_EVENT_HEART_RATE_DEVICES_DISCOVERED).post(BLE_DEVICE_EVENT_NORMAL);
    }

    // (綜合) 心跳帶連線事件
    @Override
    public void onCustomEventHeartRateDeviceConnected() {
        LiveEventBus.get(ON_CUSTOM_EVENT_HEART_RATE_DEVICES_DISCOVERED).post(BLE_DEVICE_EVENT_CONNECTED);

        // TODO: 還有其他心跳
        //沒心跳就不可使用的Program
        //   workoutViewModel.setIsHrConnected(true);


        appStatusViewModel.topHrIconConnected.set(true);
    }

    // (綜合) 心跳帶斷線事件
    @Override
    public void onCustomEventHeartRateDeviceDisconnected() {

        LiveEventBus.get(ON_CUSTOM_EVENT_HEART_RATE_DEVICES_DISCOVERED).post(BLE_DEVICE_EVENT_RESCAN);

        workoutViewModel.setBleHr(0);

        // TODO: 還有其他心跳
        //     workoutViewModel.setIsHrConnected(false);


        appStatusViewModel.topHrIconConnected.set(false);
    }

    // (綜合)  心跳帶心跳資料更新
    @Override
    public void onCustomEventHeartRateDeviceDataChanged(DeviceGEM.HeartRateDevice heartRateDevice) {

        workoutViewModel.setBleHr(heartRateDevice.getHeartRate());


        //  Log.d(TAG, "(綜合)心跳帶心跳資料更新: " + heartRateDevice.getHeartRate());
    }


}
