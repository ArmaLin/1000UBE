package com.dyaco.spirit_commercial.egym;

import static com.dyaco.spirit_commercial.App.EGYM_BASIC_AUTHORIZATION;
import static com.dyaco.spirit_commercial.App.EGYM_BEARER_AUTHORIZATION;
import static com.dyaco.spirit_commercial.App.UNIT_E;
import static com.dyaco.spirit_commercial.App.getApp;
import static com.dyaco.spirit_commercial.MainActivity.isTreadmill;
import static com.dyaco.spirit_commercial.MainActivity.userProfileViewModel;
import static com.dyaco.spirit_commercial.support.CommonUtils.getAgeFormBirth;
import static com.dyaco.spirit_commercial.support.CommonUtils.getJson;
import static com.dyaco.spirit_commercial.support.CommonUtils.iExc;
import static com.dyaco.spirit_commercial.support.CommonUtils.showException;
import static com.dyaco.spirit_commercial.support.FormulaUtil.m2Km;
import static com.dyaco.spirit_commercial.support.FormulaUtil.mi2km;
import static com.dyaco.spirit_commercial.support.FormulaUtil.mph2kph;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.getInclineAd;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.getInclineValue;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.getSpeedValue;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.DEVICE_TYPE_ELLIPTICAL;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.DEVICE_TYPE_RECUMBENT_BIKE;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.DEVICE_TYPE_TREADMILL;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.DEVICE_TYPE_UPRIGHT_BIKE;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.IMPERIAL;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.METRIC;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.LOG_IN_EVENT;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.BAR_TYPE_INCLINE;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.BAR_TYPE_LEVEL_SPEED;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.MAC_ADDRESS_PARAM;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.UPDATE_ALL_BAR;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.UPDATE_INCLINE_BAR;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.UPDATE_SPEED_BAR;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.USER_TYPE_EGYM;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.MAX_INC_MAX;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.MAX_LEVEL_MAX;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.MAX_RPM;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.MAX_SPD_IU_MAX;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.MAX_SPD_MU_MAX;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.TARGET_TIME_MAX;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.THR_MAX;
import static com.dyaco.spirit_commercial.support.intdef.WorkoutIntDef.RT_LEVEL;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.view.Gravity;

import androidx.annotation.NonNull;

import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.listener.IUartConsole;
import com.dyaco.spirit_commercial.login.LoginFragment;
import com.dyaco.spirit_commercial.model.kotlin.ApiResponseListener;
import com.dyaco.spirit_commercial.model.kotlin.EgymApiManager;
import com.dyaco.spirit_commercial.model.webapi.BaseApi;
import com.dyaco.spirit_commercial.model.webapi.DefaultEgymWebListener;
import com.dyaco.spirit_commercial.model.webapi.EgymWebListener;
import com.dyaco.spirit_commercial.model.webapi.IServiceApi;
import com.dyaco.spirit_commercial.model.webapi.bean.CreateWorkoutParam;
import com.dyaco.spirit_commercial.model.webapi.bean.EgymLoginBean;
import com.dyaco.spirit_commercial.model.webapi.bean.EgymTrainingPlans;
import com.dyaco.spirit_commercial.model.webapi.bean.EgymUserDetailsBean;
import com.dyaco.spirit_commercial.model.webapi.bean.GetGymInfo2Bean;
import com.dyaco.spirit_commercial.support.CommonUtils;
import com.dyaco.spirit_commercial.support.FormulaUtil;
import com.dyaco.spirit_commercial.support.GlideApp;
import com.dyaco.spirit_commercial.support.HmacUtil;
import com.dyaco.spirit_commercial.support.MsgEvent;
import com.dyaco.spirit_commercial.support.RxTimer;
import com.dyaco.spirit_commercial.support.SyncerUtil;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;
import com.dyaco.spirit_commercial.support.room.DatabaseCallback;
import com.dyaco.spirit_commercial.support.room.spirit.SpiritDbManager;
import com.dyaco.spirit_commercial.support.room.spirit.spirit_entity.EgymEntity;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingBean;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingViewModel;
import com.dyaco.spirit_commercial.viewmodel.EgymDataViewModel;
import com.dyaco.spirit_commercial.viewmodel.WorkoutViewModel;
import com.dyaco.spirit_commercial.workout.MainWorkoutTrainingFragment;
import com.dyaco.spirit_commercial.workout.programs.ProgramsEnum;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jeremyliao.liveeventbus.LiveEventBus;

import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import es.dmoral.toasty.Toasty;
import okhttp3.ResponseBody;

public class EgymUtil {
    public static int ZERO_DURATION_DURATION = 30 * 60 * 1000;
    public static int SYMBOL_DURATION = -99;
    String egymClientId = "Spirit/Dyaco_test";
    String egymClientSecret = "LZdJjHHaiDKuH5Mz1rkYoOsxS3zLCcWp";
    //    String egymClientId = "SpiritFitness";
//    String egymClientSecret = "NewSecret1234";
    String GymId = "3";
    public static String EGYM_MACHINE_TYPE = "";
    public static String EGYM_SERIAL_NUMBER = "";
    public static String RFID_FORMAT = "MIFARE";
    public static String RFID_CODE = "C0313F1E";//16進制   //SP10001

    private final MainActivity mainActivity;
    private final DeviceSettingViewModel deviceSettingViewModel;
    private final EgymDataViewModel egymDataViewModel;
    private static EgymUtil instance;
    public static final String TAG = "EgymUtil";

    // HTTP Basic Authentication  (Authorization: Basic base64(username:password))
    // EGYM_BASIC_AUTHORIZATION = "Basic " + Base64.getEncoder().encodeToString((egymClientId + ":" + egymClientSecret).getBytes());


    private EgymUtil(MainActivity mainActivity, DeviceSettingViewModel deviceSettingViewModel, EgymDataViewModel egymDataViewModel) {
        this.mainActivity = mainActivity;
        this.deviceSettingViewModel = deviceSettingViewModel;
        this.egymDataViewModel = egymDataViewModel;
        getEgymMachineType();
    }


    // 初始化方法，只能呼叫一次
    public static void init(MainActivity mainActivity, DeviceSettingViewModel deviceSettingViewModel, EgymDataViewModel egymDataViewModel) {
        if (instance == null) {
            instance = new EgymUtil(mainActivity, deviceSettingViewModel, egymDataViewModel);
        }
    }

    public void getEgymMachineType() {
        switch (deviceSettingViewModel.typeCode.get()) {
            case DEVICE_TYPE_TREADMILL:
                EgymUtil.EGYM_MACHINE_TYPE = "Treadmill";
                break;
            case DEVICE_TYPE_ELLIPTICAL:
                EgymUtil.EGYM_MACHINE_TYPE = "Elliptical";
                break;
            case DEVICE_TYPE_UPRIGHT_BIKE:
                EgymUtil.EGYM_MACHINE_TYPE = "Upright Bike";
                break;
            case DEVICE_TYPE_RECUMBENT_BIKE:
                EgymUtil.EGYM_MACHINE_TYPE = "Recumbent Bike";
                break;
        }
    }

    // 取得單例實例
    public static EgymUtil getInstance() {
        if (instance == null) {
            throw new IllegalStateException("EgymUtil 未初始化，請先呼叫 init() 方法");
        }
        return instance;
    }


    public void apiCreateWorkouts(String dataJson, EgymWebListener egymWebListener) {

        Map<String, String> egymHeader = new HashMap<>();
        egymHeader.put("Authorization", EGYM_BEARER_AUTHORIZATION);

//        String xxx = "{\n" +
//                "  \"uniqueExerciseId\": \"12312312\",\n" +
//                "  \"startTimestamp\": 1733446800000,\n" +
//                "  \"endTimestamp\": 1733285591036,\n" +
//                "  \"timezone\": \"America/Chicago\",\n" +
//                "  \"frequency\": 2,\n" +
//                "  \"exerciseName\": \"Treadmill\",\n" +
//                "  \"kiloCalories\": 100,\n" +
//                "  \"averageHeartRate\": 80,\n" +
//                "  \"trainingPlanId\": 5757932159631360,\n" +
//                "  \"averagePace\": 30,\n" +
//                "  \"trainingPlanExerciseId\": 5937401763725312,\n" +
//                "  \"metadata\": {\n" +
//                "    \"property1\": \"string\",\n" +
//                "    \"property2\": \"string\"\n" +
//                "  },\n" +
//                "  \"intervals\": [\n" +
//                "    {\n" +
//                "      \"rampAngle\": 1,\n" +
//                "      \"resistance\": 1,\n" +
//                "      \"speed\": 2.7777,\n" +
//                "      \"duration\": 5000,\n" +
//                "      \"distance\": 13.8,\n" +
//                "      \"heartRate\": 130,\n" +
//                "      \"kiloCalories\": 5,\n" +
//                "      \"stepsPerMinute\": 70,\n" +
//                "      \"steps\": 10,\n" +
//                "      \"stepHeight\": 2,\n" +
//                "      \"strideLengthZone\": 1,\n" +
//                "      \"rotations\": 50,\n" +
//                "      \"incline\": 1.5,\n" +
//                "      \"floors\": 1,\n" +
//                "      \"watts\": 200\n" +
//                "    }\n" +
//                "  ]\n" +
//                "}";

        //    CreateWorkoutParam workout = new Gson().fromJson(xxx, CreateWorkoutParam.class);
//        final String requestBody = new Gson().toJson(workout);


//        CreateWorkoutParam createWorkoutParam = new CreateWorkoutParam();
//        createWorkoutParam.setUniqueExerciseId(UUID.randomUUID().toString());
//        createWorkoutParam.setStartTimestamp(1733446800000L);
//        createWorkoutParam.setEndTimestamp(System.currentTimeMillis());
//        createWorkoutParam.setTimezone("America/Chicago");
//        createWorkoutParam.setFrequency(2);
//        createWorkoutParam.setExerciseName(egymDataViewModel.currentExerciseName.get());
//        createWorkoutParam.setKiloCalories(100);
//        createWorkoutParam.setAverageHeartRate(80);
//        createWorkoutParam.setTrainingPlanId(5757932159631360L);
//        createWorkoutParam.setAveragePace(30d);
//        createWorkoutParam.setTrainingPlanExerciseId(5937401763725312L);
//        //  createWorkoutParam.setMetadata();
//
//        List<CreateWorkoutParam.IntervalsDTO> intervalsDTOList = new ArrayList<>();
//        CreateWorkoutParam.IntervalsDTO intervalsDTO = new CreateWorkoutParam.IntervalsDTO();
//        intervalsDTO.setRampAngle(1);
//        intervalsDTO.setResistance(1);
//        intervalsDTO.setSpeed(2.777);
//        intervalsDTO.setDuration(5000);
//        intervalsDTO.setHeartRate(130);
//        intervalsDTO.setKiloCalories(5);
//        intervalsDTO.setStepsPerMinute(70);
//        intervalsDTO.setSteps(10);
//        intervalsDTO.setStepHeight(2);
//        intervalsDTO.setStrideLengthZone(1);
//        intervalsDTO.setRotations(50);
//        intervalsDTO.setIncline(1.5d);
//        intervalsDTO.setFloors(1);
//        intervalsDTO.setWatts(200);
//
//        intervalsDTOList.add(intervalsDTO);
//
//        createWorkoutParam.setIntervals(intervalsDTOList);
//
//        final String requestBody = new Gson().toJson(createWorkoutParam);

        //   final String requestBody = dataJson;
//        LogS.printJson("###EGYMMMMM", requestBody, "apiCreateWorkouts");

        //    String dataJson = egymEntity.getWorkoutJson();

        EgymApiManager.createEgymWorkout(egymHeader, dataJson, new ApiResponseListener<String>() {

            @Override
            public void onSuccess(String result, int httpCode) {
                Log.d(TAG, "createEgymWorkout ########httpCode: " + httpCode);
                egymWebListener.onSuccess(result);
                egymWebListener.onSuccess();


            }

            @Override
            public void onFailure(@NonNull Throwable error, @Nullable Integer httpCode) {
                egymWebListener.onFail(error, httpCode);
                egymWebListener.onFail();
                Log.d(TAG, "apiCreateWorkouts 失敗" + error.getMessage());
                String errorText = "cardio/workouts" + " - " + httpCode + " - " + error.getMessage();
                mainActivity.insertEgymError(errorText);
            }
        });
    }

    public void loginEgym(String rfidCode) {
//        loginEgym(rfidCode, new DefaultEgymWebListener());
        loginEgymK(rfidCode, new DefaultEgymWebListener());


//        // 監聽登入成功與失敗的 LiveData
//        egymDataViewModel.getLoginResult().observe(this, result -> {
//            // 登入成功處理，例如更新 UI
//        });
//        egymDataViewModel.getLoginError().observe(this, error -> {
//            // 登入失敗處理，例如顯示錯誤訊息
//        });
//        egymDataViewModel.loginEgym(header, params);
    }

    //000e8ea3c6d3
    public static String getWifiMacViaWifiManager(Context context) {
        // 1. 先判斷 EGYM_SERIAL_NUMBER
        if (EGYM_SERIAL_NUMBER != null && !EGYM_SERIAL_NUMBER.trim().isEmpty()) {
            // 只要非 null 且 trim() 後不為空，就直接回傳
            Log.d("AAASSDDDDD", "EGYM_SERIAL_NUMBER 有值: " + EGYM_SERIAL_NUMBER);
            return EGYM_SERIAL_NUMBER;
        }


        // 2. 如果 EGYM_SERIAL_NUMBER 沒有值，才繼續往下透過 WifiManager 取 MAC
        try {
            WifiManager wifiManager = (WifiManager) context.getApplicationContext()
                    .getSystemService(Context.WIFI_SERVICE);
            if (wifiManager == null) {
                return null;
            }

            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo == null) {
                return null;
            }

            String mac = wifiInfo.getMacAddress();
            // 如果 mac 為 "02:00:00:00:00:00" 或 null/空字串，就視為拿不到真實值
            if (mac != null
                    && !mac.isEmpty()
                    && !"02:00:00:00:00:00".equals(mac)) {
                // 把所有冒號移除後回傳
                return mac.replace(":", "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 若都拿不到，就回傳 null
        return null;
    }


    //E79E01748E83
    public static String getEgymSerialNumber() {
        if (EGYM_SERIAL_NUMBER != null && !EGYM_SERIAL_NUMBER.trim().isEmpty()) {
            // 只要非 null 且 trim() 後不為空，就直接回傳
            Log.d("AAASSDDDDD", "EGYM_SERIAL_NUMBER 有值: " + EGYM_SERIAL_NUMBER);
            return EGYM_SERIAL_NUMBER;
        }

        String mac = getApp().getDeviceSettingBean().getMachine_mac();
        // 如果 mac 為 "02:00:00:00:00:00" 或 null/空字串，就視為拿不到真實值
        if (mac != null && !mac.isEmpty() && !"02:00:00:00:00:00".equals(mac)) {
            // 把所有冒號移除後回傳
            return mac.replace(":", "");
        }
        return "";
    }


    // rfidCode 目前沒用到,
    public void loginEgymK(String rfidCode, EgymWebListener egymWebListener) {
        mainActivity.showLoading(true);

        //     EGYM_SERIAL_NUMBER = getWifiMacViaWifiManager(getApp());
        EGYM_SERIAL_NUMBER = EgymUtil.getEgymSerialNumber();
        Log.d(TAG, "😺EGYM_SERIAL_NUMBER: " + EGYM_SERIAL_NUMBER);

        //⭐️egymClientId  WEB_API , apiGetGymInfo 取得
        //⭐️egymClientSecret WEB_API , apiGetGymInfo 取得

        // HTTP Basic Authentication  (Authorization: Basic base64(username:password))
        //egymClientId = "123";
        EGYM_BASIC_AUTHORIZATION = "Basic " + Base64.getEncoder().encodeToString((egymClientId + ":" + egymClientSecret).getBytes());
        Log.d(TAG, "😺RFID_CODE:" + RFID_CODE);
        Log.d(TAG, "😺EGYM_BASIC_AUTHORIZATION: " + EGYM_BASIC_AUTHORIZATION);
        Map<String, String> map = new HashMap<>();
        map.put("grant_type", "RFID"); //⭐️PASSWORD,RFID,REFRESH_TOKEN,XID,NFC   //RFID 固定
        map.put("serial_number", EGYM_SERIAL_NUMBER); //⭐️GEM 3 MAC, E79E01748E83
        map.put("machine_type", EGYM_MACHINE_TYPE); //⭐️機型
        map.put("rfid", RFID_CODE); //⭐️rfidCode    在 EVENT_NFC_READ 直接給
        map.put("rfid_format", RFID_FORMAT);  //⭐️MIFARE 固定

        Map<String, String> egymHeader = new HashMap<>();
        egymHeader.put("Authorization", EGYM_BASIC_AUTHORIZATION);
        Log.d(TAG, "initEgym: " + map);

        EgymApiManager.loginEgym(egymHeader, map, new ApiResponseListener<>() {
            @Override
            public void onSuccess(EgymLoginBean data) {
                try {
                    if (data.getAccess_token() != null) {
                        EGYM_BEARER_AUTHORIZATION = "Bearer " + data.getAccess_token();
                        //    LogS.printJson("QQQQQQQQQQQEGYMMMMM", new Gson().toJson(data), "登入成功");


                        //     userProfileViewModel.userDisplayName.set("");
                        //    userProfileViewModel.userType.set(USER_TYPE_EGYM); //等T&C 同意時 才能變
                        apiUserDetails();


                    } else {
                        mainActivity.showWebApiAlert(true, "AccessToken error");
                        egymWebListener.onFail();
                        Log.d(TAG, "data error: ");
                        mainActivity.showLoading(false);
                    }
                } catch (Exception e) {
                    mainActivity.showWebApiAlert(true, "Exception" + e.getMessage());
                    egymWebListener.onFail();
                    Log.d(TAG, "Exception: " + e.getMessage());
                    mainActivity.showLoading(false);
                }

                //   // new RxTimer().timer(800, number -> mainActivity.showLoading(false));
            }

            @Override
            public void onFailure(@NonNull Throwable error, @Nullable Integer httpCode) {
                String errorText;
                if (error instanceof IOException) {
                    Log.e(TAG, "❌ 網路錯誤：" + error.getMessage());
                    errorText = "Please check your internet connection.";
                    Toasty.error(getApp(), errorText, Toasty.LENGTH_SHORT).show();

                } else if (httpCode != null) {
                    String errorMessage = error.getMessage();
                    Log.e(TAG, "❌ API 錯誤 原始訊息：" + errorMessage);
                    Log.e(TAG, "❌ API 錯誤 httpCode：" + httpCode);

                    try {
                        JSONObject errorJson = new JSONObject(errorMessage);
                        int status = errorJson.optInt("status");
                        String errorType = errorJson.optString("error");
                        String message = errorJson.optString("message");

                        errorText = "HTTP " + status + " - " + errorType + " - " + message;
                        Log.d(TAG, "toastMessage: " + errorText);

                        Toasty.error(getApp(), errorText, Toasty.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        // JSON 解析失敗， 顯示原始錯誤訊息
                        errorText = httpCode + " - JSONException " + e.getMessage() + "," + errorMessage;
                        Log.d(TAG, "JSON 解析失敗，顯示原始錯誤訊息: ");
                        Toasty.error(getApp(), errorText, Toasty.LENGTH_LONG).show();

                    }
                } else {
                    errorText = Objects.requireNonNull(error.getMessage());
                    Log.e(TAG, "❌ 其他錯誤：" + errorText);
                    Toasty.error(getApp(), errorText, Toasty.LENGTH_SHORT).show();
                }

                if (httpCode != null && httpCode == 401) {
//                    errorText = "Credential Error";
                    errorText = "Unauthorized";
                }

                mainActivity.showLoading(false);
                egymWebListener.onFail();

                mainActivity.showWebApiAlert(true, errorText);

                String errorTextXX = "oauth/token" + " - " + httpCode + " - " + error.getMessage();
                mainActivity.insertEgymError(errorTextXX);
            }
        });
    }


    /**
     * 將取得的 TrainingPlans 做數值調整，不管在APP設定公制或英制, API取得的都是公制
     * #超過Console最大值，以 console 最大值為準
     * 1.SPEED 是 m/s, 公尺每秒 ,轉成 km/h
     * 2.DISTANCE 取得的單位是公尺，轉成公里
     * 3.Duration 最大值為 99分鐘，這裡的單位是毫秒，如果是0 就改成 -99, 實際上是要跑30分鐘
     * 4.hr 最大值 為 200
     * 5.Incline 最大值為 15
     * 6.LEVEL 最大值 40
     */
    public EgymTrainingPlans normalizeEgymTrainingPlans(EgymTrainingPlans egymTrainingPlans) {
        if (egymTrainingPlans == null || egymTrainingPlans.getTrainer() == null) {
            return egymTrainingPlans; // 避免 NullPointerException，直接返回
        }

        for (EgymTrainingPlans.TrainerDTO trainer : egymTrainingPlans.getTrainer()) {
            if (trainer.getIntervals() == null) continue; // 確保 intervals 不為 null

            for (EgymTrainingPlans.TrainerDTO.IntervalsDTO interval : trainer.getIntervals()) {

                //Duration 最大值為 99分鐘，這裡的單位是毫秒，如果是0 就改成 -99, 實際上是要跑30分鐘
                if (interval.getDuration() != null && interval.getDuration() > (TARGET_TIME_MAX * 60 * 1000)) {
                    interval.setDuration((TARGET_TIME_MAX * 60 * 1000)); // 限制 duration 最大值
                }

                if (interval.getDuration() != null && interval.getDuration() == 0) {
                    interval.setDuration(SYMBOL_DURATION);
                    //    Log.d("KKKKEEEE", "normalizeEgymTrainingPlans: " + interval.getDuration() +","+ trainer.getSessionName());
                }

                //取得的單位是公尺，轉成公里
                if (interval.getDistance() != null) {
                    interval.setDistance(m2Km(interval.getDistance())); // 公尺轉公里
                }

                // 限制 hr 最大值 為 200
                if (interval.getHeartRate() != null && interval.getHeartRate() > (THR_MAX)) {
                    interval.setHeartRate(THR_MAX);
                }

                //限制Incline最大值為 15
//                if (interval.getIncline() != null && interval.getIncline() > ((double) MAX_INC_MAX / 2)) {
//                    interval.setIncline(((double) MAX_INC_MAX / 2));
//                }

                if (interval.getIncline() != null) {
                    double incline = interval.getIncline();

                    // 1️⃣：四捨五入到小數點第 1 位
                    incline = Math.round(incline * 10.0) / 10.0;

                    // 2️⃣ ：根據小數點後的值做自訂進位或捨去
                    double intPart = Math.floor(incline);
                    double decimalPart = incline - intPart;
                    incline = (decimalPart >= 0.1 && decimalPart <= 0.2) ? intPart
                            : ((decimalPart >= 0.3 && decimalPart <= 0.4) || (decimalPart >= 0.6 && decimalPart <= 0.7)) ? intPart + 0.5
                            : (decimalPart >= 0.8 && decimalPart <= 0.9) ? intPart + 1.0
                            : incline;

                    // 3️⃣：純小數（0.x）就變成 0
                    incline = (incline > 0 && incline < 1) ? 0d : incline;

                    // 4️⃣：大於 15 就變成 15
                    incline = Math.min(incline, (double) MAX_INC_MAX / 2);


                    // 設定修正後的 incline 值
                    interval.setIncline(incline);

                    // Log 結果
                    Log.d("XXXXXXX", "######### INCLINE 最終值: " + interval.getIncline());
                }


                //SPEED 是 m/s, 公尺每秒 ,轉成 km/h
                if (egymDataViewModel.unitSystem.get() == METRIC) {

                    if (interval.getSpeed() != null) {
                        interval.setSpeed((interval.getSpeed() * 3.6));
                    }

                    if (interval.getSpeed() != null && interval.getSpeed() > ((double) MAX_SPD_MU_MAX / 10)) {
                        interval.setSpeed(((double) MAX_SPD_MU_MAX / 10)); // 限制 speed 最大值
                    }
                } else {
                    //基本沒用 因為都是 METRIC
                    // TODO EGYM unitSystem
                    if (interval.getSpeed() != null && interval.getSpeed() > ((double) MAX_SPD_IU_MAX / 10)) {
                        interval.setSpeed(((double) MAX_SPD_IU_MAX / 10)); // 限制 speed 最大值
                    }
                }


                //********Elliptical & BIKE **********************
                //最大 LEVEL 40
                if (interval.getResistance() != null && interval.getResistance() > MAX_LEVEL_MAX) {
                    interval.setResistance(MAX_LEVEL_MAX);
                }

                //********Elliptical**********************

                //Cadence , Steps per minute (SPM) , 每分鐘步數
                if (interval.getStepsPerMinute() != null && interval.getStepsPerMinute() > MAX_RPM) {
                    interval.setStepsPerMinute(MAX_RPM);
                }

                //***********BIKE*****************************
                //Cadence , Rotations per minute (RPM) , 每分鐘旋轉速度
                if (interval.getRotations() != null && interval.getRotations() > MAX_RPM) {
                    interval.setRotations(MAX_RPM);
                }


            }
        }

        return egymTrainingPlans; // 🔥 返回修改後的物件
    }


    public void apiGetTrainingPlans() {

        Map<String, String> egymHeader = new HashMap<>();
        egymHeader.put("Authorization", EGYM_BEARER_AUTHORIZATION);

        EgymApiManager.getEgymTrainingPlans(egymHeader, new ApiResponseListener<EgymTrainingPlans>() { // ✅ 確保 Map 轉換正確
            @Override
            public void onSuccess(EgymTrainingPlans egymTrainingPlans) {

                if (egymTrainingPlans != null && egymTrainingPlans.getTrainer() != null) {
                    int totalCoaches = egymTrainingPlans.getTrainer().size();
                    List<byte[]> initialImages = new ArrayList<>(Collections.nCopies(totalCoaches, null));
                    egymDataViewModel.coachImages.postValue(initialImages);

                    Log.d(TAG, "取得 TrainingPlans，總教練數: " + totalCoaches);

                    for (int i = 0; i < totalCoaches; i++) {
                        EgymTrainingPlans.TrainerDTO trainer = egymTrainingPlans.getTrainer().get(i);
                        if (trainer.getAuthor().getImage() != null) {
                            String egymImgId = trainer.getAuthor().getImage().getImageId();
                            String egymImageType = trainer.getAuthor().getImage().getImageType();
                            String egymImageSize = "SMALL";
                            getCoachImage(egymImgId, egymImageType, egymImageSize, i);
                        }
                    }
                }

                egymDataViewModel.egymTrainingPlansData.setValue(normalizeEgymTrainingPlans(egymTrainingPlans));


                //    Log.d(TAG, "XCCVVBBBB: " + new Gson().toJson(egymDataViewModel.egymTrainingPlansData.getValue()));
            }

            @Override
            public void onFailure(@NonNull Throwable error, Integer httpCode) { // ✅ 適配新的 ApiResponseListener
                egymDataViewModel.egymTrainingPlansData.setValue(null);
                //     LiveEventBus.get(EGYM_GET_TRAINING_PLANS).post(null);
                Log.d(TAG, "EGYMMMMM GetTrainingPlans 失敗，錯誤：" + error.getMessage());

                String errorTextXX = "cardio/training-plans" + " - " + httpCode + " - " + error.getMessage();
                mainActivity.insertEgymError(errorTextXX);
            }
        });
    }

    private void getCoachImage(String egymImgId, String egymImageType, String egymImageSize, int position) {
        EgymApiManager.getEgymImage(EGYM_BEARER_AUTHORIZATION, egymImgId, egymImageType, egymImageSize, new ApiResponseListener<ResponseBody>() { // ✅ 改用 EgymApiKK

            @Override
            public void onSuccess(ResponseBody responseBody) {
                try {
                    byte[] imageData = CommonUtils.saveImageToMemory(responseBody);
                    if (imageData != null) {
                        List<byte[]> currentImages = egymDataViewModel.coachImages.getValue();
                        if (currentImages != null) {
                            currentImages.set(position, imageData);
                            egymDataViewModel.coachImages.postValue(currentImages);
                        }

                        Log.d(TAG, "成功取得教練圖片: " + position);
                    }
                } catch (Exception e) {
                    showException(e);
                    Log.d(TAG, "Exception: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(@NonNull Throwable error, Integer httpCode) { // ✅ 適配新的 ApiResponseListener
                Log.d(TAG, "教練圖片獲取失敗: " + position + "，錯誤：" + error.getMessage());

                String errorTextXX = "images/{imageId}" + " - " + httpCode + " - " + error.getMessage();
                mainActivity.insertEgymError(errorTextXX);
            }
        });
    }


    EgymUserDetailsBean egymUserDetailsBean;

    public void apiUserDetails() {
        //  Log.d("EGYMMMMM", "EGYM_BEARER_AUTHORIZATION: " + EGYM_BEARER_AUTHORIZATION);
        Map<String, String> egymHeader = new HashMap<>();
        egymHeader.put("Authorization", EGYM_BEARER_AUTHORIZATION);

        EgymApiManager.getEgymUserDetails(egymHeader, new ApiResponseListener<>() {
            @Override
            public void onSuccess(EgymUserDetailsBean data) {
                try {
                    if (data != null) {

                        //     Log.d("EgymUtil", "UserDetail: " + new Gson().toJson(data));
                        Log.d(TAG, "🚨🚨getTermsAndConditionsAccepted: " + data.getTermsAndConditionsAccepted());
                        egymUserDetailsBean = data;
                        //已同意條款
                        if (data.getTermsAndConditionsAccepted()) {
                        // TODO:   同意條款 開啟
                     //   if (1 != 1) {
                            Log.d(TAG, "已同意條款: ");
                            tcOk();
                        } else {
                            Log.d(TAG, "尚未同意條款: ");
                            checkTC();
                        }

                    } else {
                        Log.d(TAG, "data error: ");
                        mainActivity.showLoading(false);
                    }
                } catch (Exception e) {
                    Log.d(TAG, "#####Exception: " + e.getMessage());
                    mainActivity.showLoading(false);
                }
            }

            @Override
            public void onFailure(@NonNull Throwable error, Integer httpCode) { // 🔥 修正參數類型
                Log.d(TAG, "EGYMMMMM apiEgymLogin 失敗，錯誤：" + error.getMessage());
                mainActivity.showLoading(false);

                String errorTextXX = "users" + " - " + httpCode + error.getMessage();
                mainActivity.insertEgymError(errorTextXX);
            }
        });
    }

    //呼叫 T&C
    private void checkTC() {

        getTermsAndConditions(new EgymWebListener() {
            @Override
            public void onSuccess(String result) {
                showTermsAndConditionsWindow(result);
            }

            @Override
            public void onFailText(String errorText) {
                Log.d(TAG, "呼叫 T C 失敗: ");
                Toasty.error(getApp(), errorText, Toasty.LENGTH_LONG).show();
                mainActivity.showLoading(false);

                String errorTextXX = "GET users/terms-and-conditions" + errorText;
                mainActivity.insertEgymError(errorTextXX);
            }
        });
    }

    public TermsAndConditionsWindow termsAndConditionsWindow;

    public void showTermsAndConditionsWindow(String text) {

        mainActivity.showLoading(false);
        Log.d(TAG, "準備開啟條款視窗: ");
        if (termsAndConditionsWindow != null) {
            termsAndConditionsWindow.dismiss();
            termsAndConditionsWindow = null;
        }
        MainActivity.isStopLogin = true;
        termsAndConditionsWindow = new TermsAndConditionsWindow(mainActivity, text);
        termsAndConditionsWindow.showAtLocation(mainActivity.getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);
        termsAndConditionsWindow.setOnCustomDismissListener(new BasePopupWindow.OnCustomDismissListener() {
            @Override
            public void onStartDismiss(MsgEvent value) {
                if (value != null && ((boolean) value.getObj())) {
                    mainActivity.showLoading(true);
                    EgymApiManager.acceptTermsAndConditions(EGYM_BEARER_AUTHORIZATION, "en_US", new ApiResponseListener<>() {
                        @Override
                        public void onSuccess(ResponseBody responseBody) {
                            Log.d(TAG, "✅ 條款接受成功！");

                            termsAndConditionsWindow = null;

                            tcOk();
                            MainActivity.isStopLogin = false;
                        }

                        @Override
                        public void onFailure(@NonNull Throwable error, Integer httpCode) {
                            MainActivity.isStopLogin = false;
                            Log.e("EgymUtil", "❌ 條款接受失敗：" + error.getMessage());

                            Toasty.error(getApp(), Objects.requireNonNull(error.getMessage()), Toasty.LENGTH_LONG).show();
                            mainActivity.showLoading(false);

                            String errorTextXX = "PUT users/terms-and-conditions" + httpCode + error.getMessage();
                            mainActivity.insertEgymError(errorTextXX);
                        }
                    });
                } else {

                    termsAndConditionsWindow = null;
                    mainActivity.showLoading(false);
                    Log.d(TAG, "不同意: #####");
                    LoginFragment.isNfcLogin = false;
                }
            }

            @Override
            public void onDismiss() {
                termsAndConditionsWindow = null;
            }
        });
    }

    private void tcOk() {
        mainActivity.showLoading(true);
        Log.d(TAG, "執行執行登入: ");

        //先設定為 USER_TYPE_EGYM 在取得 apiGetTrainingPlans
        userProfileViewModel.userDisplayName.set("");
        userProfileViewModel.userType.set(USER_TYPE_EGYM);

        apiGetTrainingPlans();
        LiveEventBus.get(LOG_IN_EVENT).post("");

        egymDataViewModel.egymUserDetailsModel.setValue(egymUserDetailsBean);

        int unitSystem = "METRIC".equalsIgnoreCase(egymUserDetailsBean.getUnitSystem()) ? METRIC : IMPERIAL;
        egymDataViewModel.unitSystem.set(unitSystem);

        userProfileViewModel.userDisplayName.set(egymUserDetailsBean.getLastName() + " " + egymUserDetailsBean.getFirstName());
        userProfileViewModel.setUserAge(getAgeFormBirth(egymUserDetailsBean.getDateOfBirth()));
        userProfileViewModel.setHeight_imperial(unitSystem == METRIC ? FormulaUtil.cm2ft(egymUserDetailsBean.getHeight().floatValue()) : egymUserDetailsBean.getHeight());
        userProfileViewModel.setHeight_metric(unitSystem == IMPERIAL ? FormulaUtil.ft2cm(egymUserDetailsBean.getHeight().floatValue()) : egymUserDetailsBean.getHeight());
        userProfileViewModel.setWeight_imperial(unitSystem == METRIC ? FormulaUtil.kg2lb(egymUserDetailsBean.getWeight().intValue()) : egymUserDetailsBean.getWeight());
        userProfileViewModel.setWeight_metric(unitSystem == IMPERIAL ? FormulaUtil.lb2kg(egymUserDetailsBean.getWeight().intValue()) : egymUserDetailsBean.getWeight());
        //     userProfileViewModel.setAvatarId("default_avatar");
        //   userProfileViewModel.setPhotoFileUrl(data.getPhotoFileUrl());
        //       mainActivity.setAvatar(false);


        //    "image": {
        //        "imageType": "EXERCISE",
        //        "imageId": "string"
        //    },
        //有可能沒有 "image"
        if (egymUserDetailsBean.getImage() != null) {
            Log.d(TAG, "🤡取得圖");
            String egymImgId = egymUserDetailsBean.getImage().getImageId();
            String egymImageType = egymUserDetailsBean.getImage().getImageType();
            String egymImageSize = "SMALL";

            //      LogS.printJson("EGYMMMMM", new Gson().toJson(data), "apiUserDetails");

            apiGetImage(egymImgId, egymImageType, egymImageSize);
        } else {
            Log.d(TAG, "😾😾😾😾沒有圖");
        }

        new RxTimer().timer(800, number -> mainActivity.showLoading(false));
    }


    //使用者照片
    public void apiGetImage(String egymImgId, String egymImageType, String egymImageSize) {

        EgymApiManager.getEgymImage(
                EGYM_BEARER_AUTHORIZATION, // Authorization Header
                egymImgId,
                egymImageType, // 圖片類型
                egymImageSize, // 圖片大小
                new ApiResponseListener<ResponseBody>() {
                    @Override
                    public void onSuccess(ResponseBody responseBody) {
                        Log.d(TAG, "取得使用者圖片");
                        // ✅ 取得 ResponseBody，可轉換為 Bitmap
                        saveImageToFile1(responseBody);
                    }

                    @Override
                    public void onFailure(@NonNull Throwable error, @Nullable Integer httpCode) {
                        Log.e(TAG, "取得使用者照片失敗: " + error.getMessage());

                        String errorTextXX = "GET images/{imageId}" + httpCode + " - " + error;
                        mainActivity.insertEgymError(errorTextXX);
                    }
                });


    }


    public void saveImageToFile1(ResponseBody responseBody) {
        // File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "/CoreStar/Dyaco/Spirit/downloaded_image.png");
//        File file = new File(getApp().getFilesDir(), "egymUserIcon.jpg");

//        Log.d("EGYMMMMM###", "saveImageToFile: " + EGYM_USER_IMG);
//        File file = new File(EGYM_USER_IMG);
//        try (FileOutputStream fos = new FileOutputStream(file);
//             InputStream inputStream = responseBody.byteStream()) {
//
//            byte[] buffer = new byte[4096];
//            int bytesRead;
//            while ((bytesRead = inputStream.read(buffer)) != -1) {
//                fos.write(buffer, 0, bytesRead);
//            }
//
//            Log.d("EGYMMMMM", "圖片 onSuccess: Data saved to file.");
//
//            GlideApp.with(getApp())
//                    .load(file)
//                    .circleCrop()
//                    .placeholder(R.color.color252e37)
//                    .error(R.drawable.avatar_normal_1_default)
//                    .into(mainActivity.getBinding().ivMemberIcon);
//
//        } catch (IOException e) {
//            Log.e("EGYMMMMM", "Failed to save image", e);
//
//        }

        byte[] imageData = CommonUtils.saveImageToMemory(responseBody);
        if (imageData != null) {
            egymDataViewModel.userImg.set(imageData); // 存到 ViewModel
        }

        GlideApp.with(getApp())
                .load(egymDataViewModel.userImg.get())
                .circleCrop()
                .placeholder(R.color.color252e37)
                .error(R.drawable.avatar_normal_1_default)
                .into(mainActivity.getBinding().ivMemberIcon);

        //   Log.d("EGYMMMMM", "取得圖片: ");

    }


    public void insertEgym(String egymJson) {
        long timestamp = Calendar.getInstance().getTimeInMillis();
        EgymEntity egymEntity = new EgymEntity();
        egymEntity.setUpdateTime(timestamp);
        egymEntity.setWorkoutJson(egymJson);
        SpiritDbManager.getInstance(getApp()).insertEgymData(egymEntity,
                new DatabaseCallback<EgymEntity>() {

                    @Override
                    public void onAdded(long rowId) {
                        super.onAdded(rowId);
                        Log.d(TAG, "insertEgym-新增待上傳的資料 rowId: " + rowId);
                        // 資料新增成功後呼叫 sync 來強制存檔
                        SyncerUtil.callSync();
                    }

                    @Override
                    public void onError(String err) {
                        super.onError(err);
                        Log.d(TAG, "insertEgym ERROR: " + err);
                    }
                });
    }

    public void getEgymDataList() {
        SpiritDbManager.getInstance(getApp()).getEgymList(new DatabaseCallback<EgymEntity>() {
            @Override
            public void onError(String err) {
                super.onError(err);
                Log.e(TAG, "getEgymList failed" + err);
            }

            @Override
            public void onDataLoadedList(List<EgymEntity> egymEntityList) {
                super.onDataLoadedList(egymEntityList);
                Log.d(TAG, "Egym待上傳資料: " + egymEntityList.size());
                for (EgymEntity egymEntity : egymEntityList) {
//                    Log.d("###EGYMMMMM", "onDataLoadedList: " + egymEntity.getWorkoutJson());
                    Log.d(TAG, "onDataLoadedList: " + egymEntity.toString());

                    Log.d(TAG, "開始上傳 apiCreateWorkouts uid:" + egymEntity.getUid());
                    //     apiCreateWorkouts(egymEntity);

                    apiCreateWorkouts(egymEntity.getWorkoutJson(), new EgymWebListener() {
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "上傳 成功: uid:" + egymEntity.getUid());
                            //上傳完畢，刪除資料庫存的資料
                            deleteEgymData(egymEntity);
                        }

                        @Override
                        public void onFail() {
                            Log.d(TAG, "上傳失敗:" + egymEntity.getUid());
                        }
                    });
                }


            }
        });
    }


    public void deleteEgymData(EgymEntity egymEntity) {
        Log.d(TAG, "開始刪除Egym Data uid:" + egymEntity.getUid());
        SpiritDbManager.getInstance(getApp()).deleteEgymData(egymEntity.getUid(),
                new DatabaseCallback<EgymEntity>() {
                    @Override
                    public void onDeleted() {
                        super.onDeleted();
                        Log.d(TAG, "刪除EgymData成功 uid:" + egymEntity.getUid());
                    }

                    @Override
                    public void onError(String err) {
                        super.onError(err);
                        Log.d(TAG, "刪除Egym資料失敗 uid:" + egymEntity.getUid());
                    }
                });
    }


    int eRampAngle = 0;
    int eResistance = 0;
    double eSpeed = 0;
    int eDuration = 0;
    int eHeartRate = 0;
    double eKiloCalories = 0;
    int eStepsPerMinute = 0;
    int eSteps = 0;
    int StepHeight = 0;
    int eStrideLengthZone = 0;
    int eRotations = 0;
    double eIncline = 0;
    int eFloors = 0;
    int eWatts = 0;
    double eDistance = 0;

    public void saveInterval(WorkoutViewModel w, EgymDataViewModel e) {
        //檔案太大會無法上傳

        //轉成 m/s, 公尺/每秒
        if (UNIT_E == METRIC) {
            eSpeed = (w.avgSpeed.get() / 3.6);  //The speed in m/s.
        } else {
            eSpeed = mph2kph((float) w.avgSpeed.get()) / 3.6;
        }

        eStepsPerMinute = w.currentRpm.get() * 2; //SPM
        eRotations = w.currentRpm.get();

        eIncline = w.avgIncline.get();
        eHeartRate = (int) w.avgHeartRate.get();
        eKiloCalories = (w.currentCalories.get() / 1000); //The kilo calories (kcal) burned during the interval.
        eWatts = (int) w.avgPower.get();

        eResistance = (int) w.avgLevel.get();


        if (w.selProgram == ProgramsEnum.EGYM) {
            eDuration = (w.egymTimePerSets.get() * 1000); //The duration in milliseconds.
        } else {
            eDuration = (w.totalElapsedTimeShow.get() * 1000); //The duration in milliseconds.
        }


        if (w.selProgram == ProgramsEnum.EGYM) {
            if (UNIT_E == METRIC) {
                eDistance = (w.egymIntervalDistance.get() * 1000); //The distance in meters.
            } else {
                eDistance = (mi2km(w.egymIntervalDistance.get()) * 1000);
            }
        } else {
            if (UNIT_E == METRIC) {
                eDistance = (w.currentDistance.get() * 1000); //The distance in meters.
            } else {
                eDistance = (mi2km(w.currentDistance.get()) * 1000);
            }
        }


        List<CreateWorkoutParam.IntervalsDTO> intervalsDTOList = new ArrayList<>();
        CreateWorkoutParam.IntervalsDTO intervalsDTO = new CreateWorkoutParam.IntervalsDTO();
        intervalsDTO.setRampAngle(eRampAngle);
        intervalsDTO.setResistance(eResistance);


        /**
         * 第一筆：3.8880576308311112 m/s
         * 第二筆：4.916644305654157 m/s
         * 第三筆：4.888888994852702 m/s
         * 利用算術平均公式計算 m/s 平均速度：
         * 先將三筆速度相加：
         * 3.88806 + 4.91664 + 4.88889 ≈ 13.69359 m/s
         *
         * 除以筆數：
         * 13.69359 / 3 ≈ 4.56453 m/s
         *
         * 轉換成 km/h（乘以 3.6）：
         * 4.56453 × 3.6 ≈ 16.43 km/h
         */
        intervalsDTO.setSpeed(eSpeed);


        /**
         * 16,11, 15
         * EGYM APP > 3個intervals 加起來 = 42 秒
         */
        intervalsDTO.setDuration(eDuration);

        /**
         * 公尺
         * 89.98076231351999
         * 70.54047415936448
         * 79.14974462763327
         * 3個intervals 加起來後四捨五入 = 240m
         * 在EGYM APP 會變成 0.24km
         */
        intervalsDTO.setDistance(eDistance);

        intervalsDTO.setHeartRate(eHeartRate);
        intervalsDTO.setKiloCalories(eKiloCalories);
        intervalsDTO.setStepsPerMinute(eStepsPerMinute);//SPM
        intervalsDTO.setRotations(eRotations);
        intervalsDTO.setSteps(eSteps);
        intervalsDTO.setStepHeight(StepHeight);
        intervalsDTO.setStrideLengthZone(eStrideLengthZone);
        intervalsDTO.setIncline(eIncline);
        intervalsDTO.setFloors(eFloors);
        intervalsDTO.setWatts(eWatts);
        intervalsDTOList.add(intervalsDTO);

        //可以判斷長度
        e.woIntervalData.addAll(intervalsDTOList);


        //可檢查哪一個值是NaN
        Gson gson = new GsonBuilder()
                .serializeSpecialFloatingPointValues()
                .create();

        Log.d(TAG, "saveInterval：數量：" + e.woIntervalData.size() + ", " + gson.toJson(e.woIntervalData));


        //   Log.d(TAG, "saveInterval： 數量：" + e.woIntervalData.size() +", "+ new Gson().toJson(e.woIntervalData));

    }


    public void initEgymDiagramData(@GENERAL.chartUpdateType int updateType, EgymDiagramBarsViewBySet egymDiagramBarsView, WorkoutViewModel w, boolean isFlow) {

        if (isFlow) return;

        // Log.d("VVVCCXAAA", "egymCurrentSet: " + w.egymCurrentSet.get());
        for (int i = w.egymCurrentSet.get(); i < egymDataViewModel.durationTimesList.size(); i++) {
            switch (updateType) {
                case UPDATE_ALL_BAR:


                    if (isTreadmill) {
                        egymDiagramBarsView.setBarLevel(BAR_TYPE_INCLINE, i, w.orgArrayInclineE[i], w.egymCurrentSet.get(), false);
                        w.currentInclineLevel.set(w.orgArrayInclineE[0]);
                        w.currentInclineValue.set(getInclineValue(w.orgArrayInclineE[0]));
                        w.currentFrontInclineAd.set(getInclineAd(w));

                        egymDiagramBarsView.setBarLevel(BAR_TYPE_LEVEL_SPEED, i, w.orgArraySpeedAndLevelE[i], w.egymCurrentSet.get(), false);
                        w.currentSpeedLevel.set(w.orgArraySpeedAndLevelE[0]);
                        w.currentSpeed.set(getSpeedValue(w.currentSpeedLevel.get()));
                    } else {

                        egymDiagramBarsView.setBarLevel(BAR_TYPE_LEVEL_SPEED, i, w.orgArraySpeedAndLevelE[i], w.egymCurrentSet.get(), false);
                        w.currentLevel.set(w.orgArraySpeedAndLevelE[0]);
                    }


                    break;
                case UPDATE_INCLINE_BAR:
                    egymDiagramBarsView.setBarLevel(BAR_TYPE_INCLINE, i, w.orgArrayInclineE[i], w.egymCurrentSet.get(), false);
                    w.currentInclineLevel.set(w.orgArrayInclineE[0]);
                    w.currentInclineValue.set(getInclineValue(w.orgArrayInclineE[0]));
                    w.currentFrontInclineAd.set(getInclineAd(w));

                    break;
                case UPDATE_SPEED_BAR:
                    egymDiagramBarsView.setBarLevel(BAR_TYPE_LEVEL_SPEED, i, w.orgArraySpeedAndLevelE[i], w.egymCurrentSet.get(), false);
                    if (isTreadmill) {
                        w.currentSpeedLevel.set(w.orgArraySpeedAndLevelE[0]);
                        w.currentSpeed.set(getSpeedValue(w.currentSpeedLevel.get()));
                    } else {
                        w.currentLevel.set(w.orgArraySpeedAndLevelE[0]);
                    }
                    break;
            }
        }
//        Log.d("VVVCCXAAA", "initEgymDiagramData: " + w.currentSpeed.get() + "," + w.currentInclineValue.get() + "," + w.currentLevel.get());
        Log.d("VVVCCXAAA", "initEgymDiagramData: " + w.currentLevel.get());

    }

    public void updateInclineNumE(int updateInclineNum, EgymDiagramBarsViewBySet egymDiagramBarsView, WorkoutViewModel w, boolean isFlow) {

        if (isFlow) return;

        Log.d("VVVCCXAAA", "原始 Incline: " + Arrays.toString(w.newArrayInclineE));
        //   for (int i = w.egymCurrentSet.get(); i < egymDataViewModel.durationTimesList.size(); i++) {

        int i = w.egymCurrentSet.get();
        w.newArrayInclineE[i] = (updateInclineNum + w.newArrayInclineE[i]);

        egymDiagramBarsView.setBarLevel(BAR_TYPE_INCLINE, i, w.newArrayInclineE[i], w.egymCurrentSet.get(), false);

        w.currentInclineLevel.set(w.newArrayInclineE[i]);
        w.currentInclineValue.set(getInclineValue(w.newArrayInclineE[i]));
        w.currentFrontInclineAd.set(getInclineAd(w));

        Log.d("VVVCCXAAA", "新 Incline: " + Arrays.toString(w.newArrayInclineE));

        Log.d("VVVCCXAAA", "initEgymDiagramData: " + w.currentInclineLevel.get() + "," + w.currentInclineValue.get() + "," + w.currentFrontInclineAd.get());
    }


    public void updateSpeedNumE(int updateSpeedNum, EgymDiagramBarsViewBySet egymDiagramBarsView, WorkoutViewModel w, boolean isFlow) {

        if (isFlow) return;

        Log.d("VVVCCXAAA", "原始 level: " + Arrays.toString(w.newArraySpeedAndLevelE));

        int i = w.egymCurrentSet.get();
        w.newArraySpeedAndLevelE[i] = (updateSpeedNum + w.newArraySpeedAndLevelE[i]);

        egymDiagramBarsView.setBarLevel(BAR_TYPE_LEVEL_SPEED, i, w.newArraySpeedAndLevelE[i], w.egymCurrentSet.get(), false);

        if (isTreadmill) {
            w.currentSpeedLevel.set(w.newArraySpeedAndLevelE[i]);
            w.currentSpeed.set(getSpeedValue(w.currentSpeedLevel.get()));
        } else {
            Log.d("VVVCCXAAA", "currentLevel: " + w.currentLevel.get() + ", updateSpeedNum:" + updateSpeedNum);
            w.currentLevel.set(w.currentLevel.get() + updateSpeedNum);
        }

        Log.d("VVVCCXAAA", "新 level: " + Arrays.toString(w.newArraySpeedAndLevelE));
        Log.d("VVVCCXAAA", "####: " + w.currentLevel.get());
    }

    public void updateFlowData(MainWorkoutTrainingFragment m, WorkoutViewModel w, IUartConsole u) {

        int i = w.egymCurrentSet.get();

        if (isTreadmill) {
            m.warmUpSpeedUpdate(w.newArraySpeedAndLevelE[i]);
            m.warmUpInclineUpdate(w.newArrayInclineE[i]);
            u.setDevSpeedAndIncline();
        } else {
            w.currentLevel.set(w.newArraySpeedAndLevelE[i]);
            u.setDevWorkload(w.currentLevel.get(), RT_LEVEL);
        }

        //                   w.totalLevel.set(w.totalLevel.get() + w.currentLevel.get());
        //                        w.avgLevel.set(w.totalLevel.get() / w.currentSegment.get());

    }


    public void getEgymSecret(EgymWebListener egymWebListener) {
        Log.d(TAG, "⭕️GetGymInfo: 取得 ClientId, ClientSecret ");
        mainActivity.showLoading(true);
        DeviceSettingBean d = getApp().getDeviceSettingBean();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(MAC_ADDRESS_PARAM, d.getMachine_mac() == null ? "" : d.getMachine_mac());
        paramMap.put("timestamp", Calendar.getInstance().getTimeInMillis());
        String paramJson = getJson(paramMap);

        Map<String, Object> header = new HashMap<>();
        header.put("signature", HmacUtil.hash(paramJson));

        BaseApi.request(BaseApi.createApi(IServiceApi.class).apiGetGymInfo(header, paramJson),
                new BaseApi.IResponseListener<>() {
                    @Override
                    public void onSuccess(GetGymInfo2Bean data) {
                        iExc(() -> {
                            mainActivity.showLoading(false);
                            //   Log.d(TAG, "GetGymInfoBean: " + new Gson().toJson(data));
                            if (data.getSuccess()) {

                                /**
                                 * {
                                 *     "success": true,
                                 *     "msg": null,
                                 *     "errorMessage": null,
                                 *     "errorCode": null,
                                 *     "errorUuid": null,
                                 *     "dataMap": {
                                 *         "data": {
                                 *             "branchId": "",
                                 *             "logUrl": "",
                                 *             "clubFullName": "",
                                 *             "taxId": "",
                                 *             "contactEmail": "",
                                 *             "address": "",
                                 *             "thirdPartyClientCredential": null
                                 *         }
                                 *     }
                                 * }
                                 */
                                // 檢查空值或空字串
                                if (data.getDataMap().getData().getThirdPartyClientCredential() == null ||
                                        data.getDataMap().getData().getThirdPartyClientCredential().getClient_id() == null ||
                                        data.getDataMap().getData().getThirdPartyClientCredential().getClient_id().isEmpty() ||
                                        data.getDataMap().getData().getThirdPartyClientCredential().getClient_secret() == null
                                        || data.getDataMap().getData().getThirdPartyClientCredential().getClient_secret().isEmpty()) {
                                    Toasty.error(getApp(), "Get EGYM Secret Error", Toasty.LENGTH_LONG).show();
                                    egymWebListener.onFailText("Get EGYM Secret Error");
                                    String errorTextXX = "GetGymInfo" + "Get EGYM Secret Error";
                                    mainActivity.insertEgymError(errorTextXX);
                                    mainActivity.showWebApiAlert(true, "Get EGYM Secret Error");
                                    return; // 出錯就不要繼續
                                }

                                // 取值
                                String id = data.getDataMap().getData().getThirdPartyClientCredential().getClient_id();
                                String secret = data.getDataMap().getData().getThirdPartyClientCredential().getClient_secret();


                                // 都合法才存起來
                                egymClientId = id;
                                egymClientSecret = secret;
                                Log.d(TAG, "egymClientId: " + egymClientId);
                                Log.d(TAG, "egymClientSecret: " + egymClientSecret);
                                Log.d(TAG, "🩸🩸🩸: 取得 Secret 成功 開始登入 EGYM");
                                loginEgymK("", new DefaultEgymWebListener());
                            } else {
                                egymWebListener.onFailText("Get EGYM Secret Error");
                                Toasty.error(getApp(), "Get EGYM Secret Error", Toasty.LENGTH_LONG).show();
                                String errorTextXX = "GetGymInfo" + "Get EGYM Secret Error";
                                mainActivity.insertEgymError(errorTextXX);
                            }
                        });
                    }

                    @Override
                    public void onFail() {
                        egymWebListener.onFail();
                        mainActivity.showLoading(false);
                        Log.d(TAG, "onFail: getEgymSecret 失敗");
                        String errorTextXX = "GetGymInfo" + "Get EGYM Secret Error";
                        mainActivity.insertEgymError(errorTextXX);
                    }
                });
    }


    public void getTermsAndConditions(EgymWebListener egymWebListener) {
        EgymApiManager.getTermsAndConditions("en_US", new ApiResponseListener<>() {
            @Override
            public void onSuccess(ResponseBody data) {
                try {
                    String content = data.string();
                    //      Log.d(TAG, "內容：" + content);
                    egymWebListener.onSuccess(content);
                    // TODO: 可用 WebView 顯示 content 或自行解析 JSON
                } catch (IOException e) {
                    Log.e(TAG, "讀取內容失敗：" + e.getMessage());
                    egymWebListener.onFailText(e.getMessage());
                    mainActivity.showLoading(false);
                }
            }

            @Override
            public void onFailure(@NonNull Throwable error, Integer code) {
                Log.e(TAG, "失敗：" + error.getMessage());
                egymWebListener.onFailText(error.getMessage());
                mainActivity.showLoading(false);
            }
        });


//        BaseApi.request(BaseApi.createApiEgym(IServiceApi.class).apiGetTermsAndConditions("en_US"),
//                new BaseApi.IResponseListener<>() {
//
//                    @Override
//                    public void onSuccess(ResponseBody data) {
//                        Log.d(TAG, "👽👽👽👽apiGetTermsAndConditions: " + data.toString());
//                    }
//
//                    @Override
//                    public void onFail() {
//                     //   egymWebListener.onFail();
//                        Log.d(TAG, "👽👽👽👽apiGetTermsAndConditions: 失敗" );
//                    }
//                });
    }

}
