package com.dyaco.spirit_commercial.model.webapi;

import static com.dyaco.spirit_commercial.App.getApp;
import static com.dyaco.spirit_commercial.model.webapi.CloudData.UPDATE_FIELD;
import static com.dyaco.spirit_commercial.support.CommonUtils.getJson;
import static com.dyaco.spirit_commercial.support.CommonUtils.iExc;
import static com.dyaco.spirit_commercial.support.CommonUtils.isNetworkAvailable;
import static com.dyaco.spirit_commercial.support.CommonUtils.strDateTimeToMillis;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.DEVICE_MODEL_CE1000ENT;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.DEVICE_MODEL_CR1000ENT;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.DEVICE_MODEL_CT1000ENT;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.DEVICE_MODEL_CU1000ENT;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.MAC_ADDRESS_PARAM;

import android.content.Context;
import android.util.Log;

import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.model.webapi.bean.MemberCheckinMachineByQRCodeBean;
import com.dyaco.spirit_commercial.model.webapi.bean.NotifyNewestConsoleVersionBean;
import com.dyaco.spirit_commercial.model.webapi.bean.NotifyUpdateConsoleSuccessBean;
import com.dyaco.spirit_commercial.model.webapi.bean.UpdateBean;
import com.dyaco.spirit_commercial.model.webapi.bean.UpdateMyUserInfoFromMachineBean;
import com.dyaco.spirit_commercial.model.webapi.bean.UploadWorkoutFromMachineBean;
import com.dyaco.spirit_commercial.product_flavor.DownloadManagerCustom;
import com.dyaco.spirit_commercial.support.CommonUtils;
import com.dyaco.spirit_commercial.support.FormulaUtil;
import com.dyaco.spirit_commercial.support.HmacUtil;
import com.dyaco.spirit_commercial.support.room.DatabaseCallback;
import com.dyaco.spirit_commercial.support.room.spirit.SpiritDbManager;
import com.dyaco.spirit_commercial.support.room.spirit.spirit_entity.ErrorMsgEntity;
import com.dyaco.spirit_commercial.support.room.spirit.spirit_entity.UploadWorkoutDataEntity;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingBean;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import es.dmoral.toasty.Toasty;

public class CallWebApi {

    Context context;

    public CallWebApi(Context context) {
        this.context = context;
    }

    public void updateUserInfo(String params, String value, WebApiListener<UpdateMyUserInfoFromMachineBean> webApiListener) {
        ((MainActivity) context).showLoading(true);
        Map<String, Object> map = new HashMap<>();
        map.put(UPDATE_FIELD, params);
        map.put(params, value);

        BaseApi.request(BaseApi.createApi(IServiceApi.class).apiUpdateMyUserInfoFromMachine(getJson(map)),
                new BaseApi.IResponseListener<UpdateMyUserInfoFromMachineBean>() {
                    @Override
                    public void onSuccess(UpdateMyUserInfoFromMachineBean data) {

                        iExc(()-> {
                            Log.d("WEB_API", "##apiUpdateMyUserInfoFromMachine: " + data.toString());
                            if (data.getSuccess()) {
                                webApiListener.onSuccess(data);

                            } else {
                                if (data.getErrorMessage() != null)
                                    Toasty.warning(context, data.getErrorMessage(), Toasty.LENGTH_LONG).show();
                            }

                            ((MainActivity) context).showLoading(false);

                        });
                    }

                    @Override
                    public void onFail() {
                        Log.d("WEB_API", "失敗");
                        ((MainActivity) context).showLoading(false);
                    }
                });
    }


    public void getUserInfo(WebApiListener<MemberCheckinMachineByQRCodeBean> webApiListener) {
        BaseApi.request(BaseApi.createApi(IServiceApi.class).apiGetMyUserInfoFromMachine(""),
                new BaseApi.IResponseListener<MemberCheckinMachineByQRCodeBean>() {
                    @Override
                    public void onSuccess(MemberCheckinMachineByQRCodeBean data) {

                        try {
                            Log.d("WEB_API", "apiUpdateMyUserInfoFromMachine: " + data.toString());
                            if (data.getSuccess()) {
                                ((MainActivity) context).setUserData(data.getDataMap().getData());
                                webApiListener.onSuccess(data);
                            } else {
                                Toasty.warning(getApp(), data.getErrorMessage(), Toasty.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFail() {
                        Log.d("WEB_API", "失敗");
                    }
                });
    }

    //通知Dyaco Cloud有新的Console版本
    public void apiNotifyNewestConsoleVersion(UpdateBean updateBean) {

        String releaseDateTime = "04/20/2000 11:54:00";
        if (updateBean.getReleaseDateTime() != null) {
            releaseDateTime = updateBean.getReleaseDateTime();
        }

        DeviceSettingBean d = getApp().getDeviceSettingBean();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(MAC_ADDRESS_PARAM, d.getMachine_mac() == null ? "98:7C:4A:80:33:44:6F" : d.getMachine_mac()); // 1開始
        paramMap.put("isAutoUpdate", d.isAutoUpdate());
        paramMap.put("newestSoftwareVersion", updateBean.getVersion());
        paramMap.put("newestSoftwareReleaseDateTime", releaseDateTime);
        paramMap.put("newestSoftwareReleaseMillis", strDateTimeToMillis(releaseDateTime));
        paramMap.put("timestamp", Calendar.getInstance().getTimeInMillis());
        String paramJson = getJson(paramMap);

        Map<String, Object> header = new HashMap<>();
        header.put("signature", HmacUtil.hash(paramJson));

        Log.d(DownloadManagerCustom.TAG, "###### 通知cloud有新檔案 (NotifyNewestConsoleVersion) API參數: " + HmacUtil.hash(paramJson) + "," + paramJson);

        BaseApi.request(BaseApi.createApi(IServiceApi.class).apiNotifyNewestConsoleVersion(header, paramJson), new BaseApi.IResponseListener<NotifyNewestConsoleVersionBean>() {
            @Override
            public void onSuccess(NotifyNewestConsoleVersionBean data) {
                Log.d(DownloadManagerCustom.TAG, "###### 通知cloud有新檔案 NotifyNewestConsoleVersion API結果: " + data.toString());
            }

            @Override
            public void onFail() {
                Log.d(DownloadManagerCustom.TAG, "###### 通知cloud有新檔案 NotifyNewestConsoleVersion 失敗: ");
            }
        });
//        BaseApi.request(BaseApi.createApi(IServiceApi.class).apiNotifyNewestConsoleVersion(header, paramJson),
//                new BaseApi.IResponseListener<Response<String>>() {
//            @Override
//            public void onSuccess(Response<String> data) {
//                Log.d("PPPPPP", "onSuccess: " + data.body());
//            }
//
//            @Override
//            public void onFail() {
//
//            }
//        });

    }

    //Console通知Dyaco Cloud 已更新
    public void apiNotifyUpdateConsoleSuccess() {

        //目前的版本 大於 儲存的版本，代表有更新過
        if (new CommonUtils().getLocalVersionCode() > getApp().getDeviceSettingBean().getCurrentVersionCode()) {
        } else {
            return;
        }


        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault());
        long softwareUpdatedMillis = getApp().getDeviceSettingBean().getSoftwareUpdatedMillis() == 0 ? Calendar.getInstance().getTimeInMillis() : getApp().getDeviceSettingBean().getSoftwareUpdatedMillis();
        String softwareUpdatedDateTime = sdf.format(softwareUpdatedMillis);

        DeviceSettingBean d = getApp().getDeviceSettingBean();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(MAC_ADDRESS_PARAM, d.getMachine_mac() == null ? "98:7C:4A:80:33:44:6F" : d.getMachine_mac());
        paramMap.put("isAutoUpdate", d.isAutoUpdate());
        paramMap.put("newestSoftwareVersion", new CommonUtils().getLocalVersionName(getApp()));
        paramMap.put("softwareUpdatedDateTime", softwareUpdatedDateTime);
        paramMap.put("softwareUpdatedMillis", softwareUpdatedMillis);
        paramMap.put("timestamp", Calendar.getInstance().getTimeInMillis());

        String paramJson = getJson(paramMap);

        Map<String, Object> header = new HashMap<>();
        header.put("signature", HmacUtil.hash(paramJson));


        BaseApi.request(BaseApi.createApi(IServiceApi.class).apiNotifyUpdateConsoleSuccess(header, paramJson),
                new BaseApi.IResponseListener<NotifyUpdateConsoleSuccessBean>() {
                    @Override
                    public void onSuccess(NotifyUpdateConsoleSuccessBean data) {
                        if (data.getSuccess()) {
                            DeviceSettingBean d = getApp().getDeviceSettingBean();
                            d.setCurrentVersionCode(new CommonUtils().getLocalVersionCode());
                            d.setSoftwareUpdatedMillis(0);
                            getApp().setDeviceSettingBean(d);
                            Log.d(DownloadManagerCustom.TAG, "###### NotifyNewestConsoleVersion API 呼叫成功，更新Console儲存版本為實際版本，目前Console儲存版本為: " +d.getCurrentVersionCode() + " 更新時間改為零," + d.getSoftwareUpdatedMillis());

                        }
                    }

                    @Override
                    public void onFail() {
                        Log.d(DownloadManagerCustom.TAG, "###### NotifyUpdateConsoleSuccess onFail: ");
                    }
                });
    }







    public void apiNotifyUpdateConsoleSuccess2(WebApiListener2<NotifyUpdateConsoleSuccessBean> webApiListener) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault());
        long softwareUpdatedMillis = getApp().getDeviceSettingBean().getSoftwareUpdatedMillis() == 0 ? Calendar.getInstance().getTimeInMillis() : getApp().getDeviceSettingBean().getSoftwareUpdatedMillis();
        String softwareUpdatedDateTime = sdf.format(softwareUpdatedMillis);

        DeviceSettingBean d = getApp().getDeviceSettingBean();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(MAC_ADDRESS_PARAM, d.getMachine_mac() == null ? "98:7C:4A:80:33:44:6F" : d.getMachine_mac());
        paramMap.put("isAutoUpdate", d.isAutoUpdate());
        paramMap.put("newestSoftwareVersion", new CommonUtils().getLocalVersionName(getApp()));
        paramMap.put("softwareUpdatedDateTime", softwareUpdatedDateTime);
        paramMap.put("softwareUpdatedMillis", softwareUpdatedMillis);
        paramMap.put("timestamp", Calendar.getInstance().getTimeInMillis());

        String paramJson = getJson(paramMap);

        Map<String, Object> header = new HashMap<>();
        header.put("signature", HmacUtil.hash(paramJson));

        Log.d("WORK_MANAGER", "######CONSOLE 更新時間" + softwareUpdatedDateTime +"########" +", 原始："+ getApp().getDeviceSettingBean().getSoftwareUpdatedMillis());
        Log.d("WORK_MANAGER", "###### NotifyNewestConsoleVersion 參數: " + paramJson + "," + HmacUtil.hash(paramJson));

        BaseApi.request(BaseApi.createApi(IServiceApi.class).apiNotifyUpdateConsoleSuccess(header, paramJson),
                new BaseApi.IResponseListener<NotifyUpdateConsoleSuccessBean>() {
                    @Override
                    public void onSuccess(NotifyUpdateConsoleSuccessBean data) {
                        Log.d("WORK_MANAGER", "###### NotifyNewestConsoleVersion 結果: " + data);
                        if (data.getSuccess()) {
                            DeviceSettingBean d = getApp().getDeviceSettingBean();
                            d.setCurrentVersionCode(new CommonUtils().getLocalVersionCode());
                            d.setSoftwareUpdatedMillis(0);
                            getApp().setDeviceSettingBean(d);
                            Log.d("WORK_MANAGER", "###### NotifyNewestConsoleVersion API 呼叫成功，更新Console儲存版本為實際版本，目前Console儲存版本為: " +d.getCurrentVersionCode() + " 更新時間改為零," + d.getSoftwareUpdatedMillis());
                            webApiListener.onSuccess(data);
                        } else {
                            webApiListener.onFail();
                        }
                    }

                    @Override
                    public void onFail() {
                        webApiListener.onFail();
                        Log.d("WORK_MANAGER", "###### NotifyUpdateConsoleSuccess onFail: ");
                    }
                });
    }


    /**
     * Error Code
     * NO_AUTH_TO_ACCESS (100) 沒有存取的權限
     * MISSING_REQUIRED_PARAMETER(102) 必要參數沒給
     * LOGIN_REQUIRED(113) 尚未登入
     * NOT_GYM_MEMBER_YET(1213) 該使用者不是健身房會員
     * DUPLICATE_WORKOUT_DATA(1219) 上傳的workout已重複
     * MACHINE_NOT_EXISTS(1407) 非健身房所屬的機器
     */

    //上傳儲存的運動資料
    public void apiUploadWorkoutFromMachineSigned() {

        SpiritDbManager.getInstance(getApp()).getUploadWorkoutDataList(new DatabaseCallback<UploadWorkoutDataEntity>() {
            @Override
            public void onDataLoadedList(@NotNull List<? extends UploadWorkoutDataEntity> uploadWorkoutDataEntityList) {
                super.onDataLoadedList(uploadWorkoutDataEntityList);
                for (UploadWorkoutDataEntity uploadWorkoutDataEntity : uploadWorkoutDataEntityList) {


                    String signature = HmacUtil.hash(uploadWorkoutDataEntity.getDataJson());

                    Map<String, Object> header = new HashMap<>();
                    header.put("signature", signature);


                    BaseApi.request(BaseApi.createApi(IServiceApi.class).apiUploadWorkoutFromMachineSigned(header, uploadWorkoutDataEntity.getDataJson()),
                            new BaseApi.IResponseListener<UploadWorkoutFromMachineBean>() {
                                @Override
                                public void onSuccess(UploadWorkoutFromMachineBean data) {

                                    if (data.getSuccess()) {


                                        SpiritDbManager.getInstance(getApp()).deleteUploadWorkoutData(uploadWorkoutDataEntity.getUid(),
                                                new DatabaseCallback<UploadWorkoutDataEntity>() {
                                                    @Override
                                                    public void onDeleted() {
                                                        super.onDeleted();
                                                    }

                                                    @Override
                                                    public void onError(String err) {
                                                        super.onError(err);
                                                    }
                                                });


                                    } else {
                                        // TODO:  Signature has expired(100)', errorCode='100'

                                        if ("100".equals(data.getErrorCode())) {


                                            SpiritDbManager.getInstance(getApp()).deleteUploadWorkoutData(uploadWorkoutDataEntity.getUid(),
                                                    new DatabaseCallback<UploadWorkoutDataEntity>() {
                                                        @Override
                                                        public void onDeleted() {
                                                            super.onDeleted();
                                                        }

                                                        @Override
                                                        public void onError(String err) {
                                                            super.onError(err);
                                                        }
                                                    });
                                        }
                                    }
                                }

                                @Override
                                public void onFail() {
                                }
                            });



                }
            }
        });


    }







    /**
     * 上傳ErrorLog
     * 1.四小時發送一次
     * 3.Error Log 成功送出後，即可於本機端刪除。
     */
    public void apiUploadErrorLog() {

        SpiritDbManager.getInstance(getApp()).getErrorMsgList(new DatabaseCallback<ErrorMsgEntity>() {
            @Override
            public void onDataLoadedList(@NotNull List<? extends ErrorMsgEntity> errorMsgEntityList) {
                super.onDataLoadedList(errorMsgEntityList);

                JSONArray jsArray = new JSONArray();
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault());


            //    if (errorMsgEntityList.size() == 0) return;

                if (!isNetworkAvailable(getApp())) return;

                for (ErrorMsgEntity errorMsgEntity : errorMsgEntityList) {

                    JSONObject jsonObject = new JSONObject();
                    try {
                        String logType = errorMsgEntity.getErrorCode();
                      //  Log.d("UPLOAD_ERROR_LOG", "#############: " + logType.substring(0,2));

                        if (logType.length() == 1) {
                            logType = "0" + logType;
                        }

                        if (!(logType).startsWith("0x")) {
                            //前兩字不是0x，加上0x
                            logType = "0x" + logType;
                        }

                        jsonObject.put("logType", logType);
                        jsonObject.put("logDateTime", sdf.format(errorMsgEntity.getErrorDate()));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    jsArray.put(jsonObject);
                }

                DeviceSettingBean d = getApp().getDeviceSettingBean();

                int machineType = -1;
                switch (d.getModel_code()){
                    case DEVICE_MODEL_CT1000ENT:
                        machineType = 0;
                        break;
                    case DEVICE_MODEL_CE1000ENT:
                        machineType = 2;
                        break;
                    case DEVICE_MODEL_CU1000ENT:
                        machineType = 12;
                        break;
                    case DEVICE_MODEL_CR1000ENT:
                        machineType = 11;
                        break;
                }

            //    Log.d("UPLOAD_ERROR_LOG","MODE:" + d.getModel_code());

                Map<String, Object> paramMap = new HashMap<>();
                paramMap.put("machineType", machineType);//設備類別(0=Treadmill; 1=Bike; 11=Recumbent_Bike; 12=Upright_Bike; 2=Elliptical; 3=Stepper; 4=Rower)
                paramMap.put(MAC_ADDRESS_PARAM, d.getMachine_mac() == null ? "00:00:00:00:00:00:00" : d.getMachine_mac());
                paramMap.put("totalRuntimeHours", (d.getODO_time() / 3600)); //小時
                paramMap.put("totalDistanceKm", FormulaUtil.mi2km(d.getODO_distance())); //公里
                paramMap.put("totalSteps", "0");
                paramMap.put("timezone", TimeZone.getDefault().getID());//"Asia/Taipei"
                paramMap.put("timestamp", Calendar.getInstance().getTimeInMillis());
                if(errorMsgEntityList.size() > 0) {
                    paramMap.put("logMessageList", jsArray);
                }

                String paramJson = getJson(paramMap);

                Map<String, Object> header = new HashMap<>();
                header.put("signature", HmacUtil.hash(paramJson));


                BaseApi.request(BaseApi.createApi(IServiceApi.class).apiUploadMachineMaintainedLog(header, paramJson),
                        new BaseApi.IResponseListener<UploadWorkoutFromMachineBean>() {
                            @Override
                            public void onSuccess(UploadWorkoutFromMachineBean data) {

                                if (data.getSuccess()) {

                                    Log.d("UPLOAD_ERROR_LOG", "onSuccess: ");

                                    for (ErrorMsgEntity errorMsgEntity : errorMsgEntityList) {

                                        SpiritDbManager.getInstance(getApp()).deleteErrorMsg(errorMsgEntity.getUid(),
                                                new DatabaseCallback<ErrorMsgEntity>() {
                                                    @Override
                                                    public void onDeleted() {
                                                        super.onDeleted();
                                                        Log.d("UPLOAD_ERROR_LOG", "onDeleted: ");
                                                    }

                                                    @Override
                                                    public void onError(String err) {
                                                        super.onError(err);
                                                    }
                                                });
                                    }


                                } else {
                                }
                            }

                            @Override
                            public void onFail() {
                            }
                        });


            }
        });


    }
}
