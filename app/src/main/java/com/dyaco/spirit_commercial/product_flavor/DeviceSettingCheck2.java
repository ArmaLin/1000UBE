package com.dyaco.spirit_commercial.product_flavor;

import static com.dyaco.spirit_commercial.App.MODE;
import static com.dyaco.spirit_commercial.App.getApp;
import static com.dyaco.spirit_commercial.App.getDeviceGEM;
import static com.dyaco.spirit_commercial.support.FormulaUtil.mi2km;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.DEVICE_TYPE_ELLIPTICAL;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.DEVICE_TYPE_RECUMBENT_BIKE;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.DEVICE_TYPE_TREADMILL;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.DEVICE_TYPE_UPRIGHT_BIKE;

import android.util.Log;

import com.corestar.libs.device.DeviceGEM;
import com.corestar.libs.device.MACHINE_LOG;
import com.dyaco.spirit_commercial.model.webapi.CallWebApi;
import com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS;
import com.dyaco.spirit_commercial.support.room.DatabaseCallback;
import com.dyaco.spirit_commercial.support.room.spirit.SpiritDbManager;
import com.dyaco.spirit_commercial.support.room.spirit.spirit_entity.ErrorMsgEntity;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingBean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 建立設定檔的資料庫，及檢查錯誤
 */
public class DeviceSettingCheck2 {

    private static final String TAG = "SETTING_FILE";
    public void checkError() {
        DeviceSettingBean deviceEntity = getApp().getDeviceSettingBean();
        //Treadmill
        if (check0x50(deviceEntity)) {
            Log.d(TAG, "0x50需要更換: ");
            insertErrorMsg(OPT_SETTINGS.E0x50, deviceEntity);
//        } else {
//            Log.d(TAG, "0x50不需要更換:");
        }

        if (check0x51(deviceEntity)) {
            Log.d(TAG, "0x51需要更換: ");
            insertErrorMsg(OPT_SETTINGS.E0x51, deviceEntity);
//        } else {
//            Log.d(TAG, "0x51不需要更換: ");
        }

        if (check0x52(deviceEntity)) {
            Log.d(TAG, "0x52需要更換: ");
            insertErrorMsg(OPT_SETTINGS.E0x52, deviceEntity);
//        } else {
//            Log.d(TAG, "0x52不需要更換: ");
        }

        if (check0x53(deviceEntity)) {
            Log.d(TAG, "0x53需要更換: ");
            insertErrorMsg(OPT_SETTINGS.E0x53, deviceEntity);
//        } else {
//            Log.d(TAG, "0x53不需要更換: ");
        }

        if (check0x54(deviceEntity)) {
            Log.d(TAG, "0x54需要更換: ");
            insertErrorMsg(OPT_SETTINGS.E0x54, deviceEntity);
//        } else {
//            Log.d(TAG, "0x54不需要更換: ");
        }


        //BIKE
        if (check0xBA(deviceEntity)) {
            Log.d(TAG, "0xBA需要更換: ");
            insertErrorMsg(OPT_SETTINGS.E0xBA, deviceEntity);
//        } else {
//            Log.d(TAG, "0xBA不需要更換: ");
        }

        if (check0xBB(deviceEntity)) {
            Log.d(TAG, "0xBB需要更換: ");
            insertErrorMsg(OPT_SETTINGS.E0xBB, deviceEntity);
//        } else {
//            Log.d(TAG, "0xBB不需要更換: ");
        }


        //Recumbent Bike
        if (check0xBC(deviceEntity)) {
            Log.d(TAG, "0xBC需要更換: ");
            insertErrorMsg(OPT_SETTINGS.E0xBC, deviceEntity);
//        } else {
//            Log.d(TAG, "0xBC不需要更換: ");
        }

        //Elliptical
        if (check0xE0(deviceEntity)) {
            Log.d(TAG, "0xE0需要更換: ");
            insertErrorMsg(OPT_SETTINGS.E0xE0, deviceEntity);
//        } else {
//            Log.d(TAG, "0xE0不需要更換: ");
        }

        if (check0xE1(deviceEntity)) {
            Log.d(TAG, "0xE1需要更換: ");
            insertErrorMsg(OPT_SETTINGS.E0xE1, deviceEntity);
//        } else {
//            Log.d(TAG, "0xE1不需要更換: ");
        }

        if (check0xE2(deviceEntity)) {
            Log.d(TAG, "0xE2需要更換: ");
            insertErrorMsg(OPT_SETTINGS.E0xE2, deviceEntity);
//        } else {
//            Log.d(TAG, "0xE2不需要更換: ");
        }



//        insertErrorMsg(OPT_SETTINGS.E0x50, deviceEntity);
//        insertErrorMsg(OPT_SETTINGS.E0x51, deviceEntity);
//        insertErrorMsg(OPT_SETTINGS.E0x52, deviceEntity);
//        insertErrorMsg(OPT_SETTINGS.E0x53, deviceEntity);
//        insertErrorMsg(OPT_SETTINGS.E0x54, deviceEntity);
    }

    public void insertErrorMsg(String errorCode, DeviceSettingBean deviceEntity) {
        String errorMsg = "";
        Date date = Calendar.getInstance().getTime();
        switch (errorCode) {
            case OPT_SETTINGS.E0x50:
                errorMsg = "Add Lubricating Oil";
                deviceEntity.setE050hour(0);
                break;
            case OPT_SETTINGS.E0x51:
                errorMsg = "Please clean motor room by vacuum cleaner. (Take off motor hood and clean the area where around motor/driving system)";
                deviceEntity.setE051time(date);
                break;
            case OPT_SETTINGS.E0x52:
                errorMsg = "Please replace running belt";
                deviceEntity.setE052hour(0);
                deviceEntity.setE052time(date);
                break;
            case OPT_SETTINGS.E0x53:
                errorMsg = "Please flip running deck(if both sides have been used then please replace the deck)";
                deviceEntity.setE053time(date);
                break;
            case OPT_SETTINGS.E0x54:
                errorMsg = "Please replace motor driving belt";
                deviceEntity.setE054time(date);
                break;
            case OPT_SETTINGS.E0xBA:
                errorMsg = "Please check belt tension(no slip)";
                deviceEntity.setE0BAhour(0);
                deviceEntity.setE0BAtime(date);
                break;
            case OPT_SETTINGS.E0xBB:
                errorMsg = "Please replace driving belt";
                deviceEntity.setE0BBtime(date);
                break;
            case OPT_SETTINGS.E0xBC:
                errorMsg = "Please clean siding rails(Under Seat)";
                deviceEntity.setE0BCtime(date);
                break;
            case OPT_SETTINGS.E0xE0:
                errorMsg = "Please check belt tension(no slip)";
                deviceEntity.setE0E0time(date);
                break;
            case OPT_SETTINGS.E0xE1:
                errorMsg = "Place replace driving belt";
                deviceEntity.setE0E1time(date);
                break;
            case OPT_SETTINGS.E0xE2:
                errorMsg = "Please clean siding rails(Under pedal arms)";
                deviceEntity.setE0E2time(date);
                break;
        }

        ErrorMsgEntity errorMsgEntity = new ErrorMsgEntity();
        errorMsgEntity.setErrorCode("0xE3" + errorCode.substring(2));
        errorMsgEntity.setErrorDate(date);
        errorMsgEntity.setErrorMessage(errorMsg);
        SpiritDbManager.getInstance(getApp()).insertErrorMsg(errorMsgEntity,
                new DatabaseCallback<ErrorMsgEntity>() {

                    @Override
                    public void onAdded(long rowId) {
                        super.onAdded(rowId);
                        Log.d(TAG, "新增ErrorMsg: ");

                        getApp().setDeviceSettingBean(deviceEntity);

                        Log.d(TAG, getApp().getDeviceSettingBean().getE051time().toString());
                    }

                    @Override
                    public void onError(String err) {
                        super.onError(err);
                    }
                });
    }

    public static void insertErrorMsg(List<ErrorMsgEntity> errorMsgEntityList) {

        for (ErrorMsgEntity errorMsgEntity : errorMsgEntityList) {
            SpiritDbManager.getInstance(getApp()).insertErrorMsg(errorMsgEntity,
                    new DatabaseCallback<>() {
                        @Override
                        public void onAdded(long rowId) {
                            super.onAdded(rowId);

                            Log.d("UPLOAD_ERROR_LOG", "新增ErrorMsg: " + rowId);

                            new CallWebApi(getApp()).apiUploadErrorLog();
                        }

                        @Override
                        public void onError(String err) {
                            super.onError(err);
                        }
                    });
        }

    }

    /**
     * 兩個日期差幾天
     *
     * @param startDate 開始的日期時間
     */
    public String printDifference(Date startDate) {
        Date endDate = Calendar.getInstance().getTime();
        long different = endDate.getTime() - startDate.getTime();
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        //1200 hour = 50 day
        //8000 hour = 333.33
        //Each quarterly = 90 day
        //1 year = 360 day
        //half year = 180 day
        //2 year = 720 day

        return elapsedDays + "日, " + elapsedHours + "時, " + elapsedMinutes + "分, " + elapsedSeconds + "秒 ";
        //  Log.d("DEVICE_SETTING", elapsedDays + "日, " + elapsedHours + "時, " + elapsedMinutes + "分, " + elapsedSeconds + "秒 ");

    }


    public long differenceDay(Date startDate) {
        Date endDate = Calendar.getInstance().getTime();
        long different = endDate.getTime() - startDate.getTime();
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;
        return different / daysInMilli;
    }

    //Workout時間 幾天
    public String sec2Day(long seconds) {
        long day = TimeUnit.SECONDS.toDays(seconds);
        long hours = TimeUnit.SECONDS.toHours(seconds) - (day * 24);
        long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds) * 60);
        long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) * 60);
        return day + "日, " + hours + "時, " + minute + "分, " + second + "秒 ";
    }

    /**
     * Add Lubricating Oil/ (添加機油)
     * workout 每 1200 hours 提示一次
     * 1200 hour = 50 day
     */
    private boolean check0x50(DeviceSettingBean deviceEntity) {
        Log.d("DEVICE_SETTING", "---");
        Log.d("DEVICE_SETTING", "檢查 機油 0x50 目前Workout使用時間：" + sec2Day(deviceEntity.getE050hour()));
        long workDay = TimeUnit.SECONDS.toDays(deviceEntity.getE050hour());
        return workDay >= 50;
    }

    /**
     * 0x51
     * Please clean motor room by vacuum cleaner.
     * (Take off motor hood and clean the area where around motor/driving system)
     * Each quarterly 開機時間
     * Each quarterly = 90 day
     */
    private boolean check0x51(DeviceSettingBean deviceEntity) {
        Log.d("DEVICE_SETTING", "---");
        Log.d("DEVICE_SETTING", "檢查 電機室 0x51 目前開機時間：" + printDifference(deviceEntity.getE051time()));
        long runDay = differenceDay(deviceEntity.getE051time());
        return runDay >= 90;
    }

    /**
     * 0x52 Please replace running belt
     * Each 2 years or each 8,000hr of usage
     * 8000 hour = 333.33
     * 2 year = 720 day
     */
    private boolean check0x52(DeviceSettingBean deviceEntity) {
        Log.d("DEVICE_SETTING", "---");
        Log.d("DEVICE_SETTING", "檢查 跑帶 0x52 目前Workout使用時間：" + sec2Day(deviceEntity.getE052hour()) + ",開機時間：" + printDifference(deviceEntity.getE052time()));
        long workDay = TimeUnit.SECONDS.toDays(deviceEntity.getE052hour());
        long runDay = differenceDay(deviceEntity.getE052time());
        return workDay >= 333 || runDay > 720;
    }


    /**
     * 0x53 Please flip running deck(if both sides have been used then please replace the deck)
     * Each 1 year
     * 360 day
     */
    private boolean check0x53(DeviceSettingBean deviceEntity) {
        Log.d("DEVICE_SETTING", "---");
        Log.d("DEVICE_SETTING", "檢查 跑步甲板 0x53 目前開機時間：" + printDifference(deviceEntity.getE053time()));
        long runDay = differenceDay(deviceEntity.getE053time());
        return runDay >= 360;
    }

    /**
     * 0x54 Please replace motor driving belt
     * Each 2 years
     * 720 day
     */
    private boolean check0x54(DeviceSettingBean deviceEntity) {
        Log.d("DEVICE_SETTING", "---");
        Log.d("DEVICE_SETTING", "檢查 電機傳動帶 0x54 目前開機時間：" + printDifference(deviceEntity.getE054time()));
        long runDay = differenceDay(deviceEntity.getE054time());
        return runDay >= 720;
    }

    /**
     * 0xBA Please check belt tension(no slip)
     * Each 1 year or 4,000hrs of usage
     * 360 day  ,166.6 day
     */
    private boolean check0xBA(DeviceSettingBean deviceEntity) {
        Log.d("DEVICE_SETTING", "---");
        Log.d("DEVICE_SETTING", "檢查 皮帶張力 0xBA 目前Workout使用時間：" + sec2Day(deviceEntity.getE0BAhour()) + ",開機時間：" + printDifference(deviceEntity.getE0BAtime()));
        long workDay = TimeUnit.SECONDS.toDays(deviceEntity.getE0BAhour());
        long runDay = differenceDay(deviceEntity.getE0BAtime());
        return workDay >= 167 || runDay > 360;
    }

    /**
     * 0xBB Please replace driving belt
     * Each 2 years
     */
    private boolean check0xBB(DeviceSettingBean deviceEntity) {
        Log.d("DEVICE_SETTING", "---");
        Log.d("DEVICE_SETTING", "檢查 傳動帶 0xBB 目前開機時間：" + printDifference(deviceEntity.getE0BBtime()));
        long runDay = differenceDay(deviceEntity.getE0BBtime());
        return runDay >= 720;
    }

    /**
     * 0xBC Please clean siding rails(Under Seat)
     * Each half year
     */
    private boolean check0xBC(DeviceSettingBean deviceEntity) {
        Log.d("DEVICE_SETTING", "---");
        Log.d("DEVICE_SETTING", "檢查 護欄 0xBC 目前開機時間：" + printDifference(deviceEntity.getE0BCtime()));
        long runDay = differenceDay(deviceEntity.getE0BCtime());
        return runDay >= 180;
    }

    /**
     * 0xE0 Please check belt tension(no slip)
     * Each 1 year
     */
    private boolean check0xE0(DeviceSettingBean deviceEntity) {
        Log.d("DEVICE_SETTING", "---");
        Log.d("DEVICE_SETTING", "檢查 皮帶張力 0xE0 目前開機時間：" + printDifference(deviceEntity.getE0E0time()));
        long runDay = differenceDay(deviceEntity.getE0E0time());
        return runDay > 360;
    }

    /**
     * 0xE1 Please replace driving belt
     * Each 2 years
     */
    private boolean check0xE1(DeviceSettingBean deviceEntity) {
        Log.d("DEVICE_SETTING", "---");
        Log.d("DEVICE_SETTING", "檢查 電機傳動帶 0xE1 目前開機時間：" + printDifference(deviceEntity.getE0E1time()));
        long runDay = differenceDay(deviceEntity.getE0E1time());
        return runDay > 720;
    }

    /**
     * 0xE2 Please clean siding rails(Under pedal arms)
     * Each quarterly
     * 90 day
     */
    private boolean check0xE2(DeviceSettingBean deviceEntity) {
        Log.d("DEVICE_SETTING", "---");
        Log.d("DEVICE_SETTING", "檢查 護欄 0xE2 目前開機時間：" + printDifference(deviceEntity.getE0E2time()));
        long runDay = differenceDay(deviceEntity.getE0E2time());
        return runDay > 90;
    }


    public void getErrorMsgByErrorCode(String errorCode) {

        SpiritDbManager.getInstance(getApp()).getErrorMsgByErrorCode(errorCode,
                new DatabaseCallback<ErrorMsgEntity>() {
                    @Override
                    public void onDataLoadedBean(ErrorMsgEntity errorMsgEntity) {
                        super.onDataLoadedBean(errorMsgEntity);

                        Log.d(TAG, "ErrorMsg:" + errorMsgEntity.toString());
                    }
                });
    }


    public static void sendError() {

        SpiritDbManager.getInstance(getApp()).getErrorMsgLit(new DatabaseCallback<ErrorMsgEntity>() {
            @Override
            public void onDataLoadedList(List<ErrorMsgEntity> errorMsgEntityList) {
                super.onDataLoadedList(errorMsgEntityList);

                try {
                    List<MACHINE_LOG> list = new ArrayList<>();
                    for (ErrorMsgEntity errorMsgEntity : errorMsgEntityList) {
                        String errorCode = errorMsgEntity.getErrorCode().replace("0x", "");
                        list.add(MACHINE_LOG.find(Integer.parseInt(errorCode, 16)));
                        Log.d("GEM3", "MACHINE_LOG TYPE: " + MACHINE_LOG.find(Integer.parseInt(errorCode, 16)));
                    }

                    MACHINE_LOG[] logs = list.toArray(new MACHINE_LOG[0]);
                    getDeviceGEM().customMessageSendLog(logs);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    // F2 回傳ODO 資料給APP
    public static void sendOdoData() {

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


        DeviceSettingBean deviceEntity = getApp().getDeviceSettingBean();
        int hour;
//        int distance = (int) deviceEntity.getODO_distance();
        int distance = (int) mi2km(deviceEntity.getODO_distance());
        int steps = (int) deviceEntity.getDeviceStep();

        long day = TimeUnit.SECONDS.toDays((long) deviceEntity.getODO_time());
        long hours = TimeUnit.SECONDS.toHours((long) deviceEntity.getODO_time()) - (day * 24);
        hour = (int) hours;
        Log.d("GEM3", "ODO TIME:" +deviceEntity.getODO_time());
        Log.d("GEM3", "ODO DISTANCE:" +deviceEntity.getODO_distance());
        Log.d("GEM3", "sendOdoData: HOUR:" +hour +" ,DISTANCE:"+distance +" ,STEPS,"+steps);
        getDeviceGEM().customMessageSendOdoInfo(ftmsEquipmentType, hour, distance, steps);
    }
}