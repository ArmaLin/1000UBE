package com.dyaco.spirit_commercial.workout;


import static com.dyaco.spirit_commercial.App.MODE;
import static com.dyaco.spirit_commercial.App.UNIT_E;
import static com.dyaco.spirit_commercial.App.getApp;
import static com.dyaco.spirit_commercial.App.getDeviceGEM;
import static com.dyaco.spirit_commercial.MainActivity.isSummary;
import static com.dyaco.spirit_commercial.MainActivity.isTreadmill;
import static com.dyaco.spirit_commercial.MainActivity.isUs;
import static com.dyaco.spirit_commercial.MainActivity.userProfileViewModel;
import static com.dyaco.spirit_commercial.egym.EgymUtil.EGYM_MACHINE_TYPE;
import static com.dyaco.spirit_commercial.egym.EgymUtil.RFID_CODE;
import static com.dyaco.spirit_commercial.support.CommonUtils.animateMarginStart;
import static com.dyaco.spirit_commercial.support.CommonUtils.findMaxInt;
import static com.dyaco.spirit_commercial.support.CommonUtils.isNetworkAvailable;
import static com.dyaco.spirit_commercial.support.CommonUtils.isValidIdI;
import static com.dyaco.spirit_commercial.support.CommonUtils.isValidIdL;
import static com.dyaco.spirit_commercial.support.CommonUtils.reverseUidHex;
import static com.dyaco.spirit_commercial.support.CommonUtils.showException;
import static com.dyaco.spirit_commercial.support.FormulaUtil.convertPace;
import static com.dyaco.spirit_commercial.support.FormulaUtil.formatDecimal;
import static com.dyaco.spirit_commercial.support.FormulaUtil.getConvert;
import static com.dyaco.spirit_commercial.support.FormulaUtil.getConvertHalfUp;
import static com.dyaco.spirit_commercial.support.FormulaUtil.getRoundDecimal;
import static com.dyaco.spirit_commercial.support.FormulaUtil.kph2mph;
import static com.dyaco.spirit_commercial.support.FormulaUtil.mph2kph;
import static com.dyaco.spirit_commercial.support.FormulaUtil.saveDistance2KM;
import static com.dyaco.spirit_commercial.support.FormulaUtil.saveDistance2Mi;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.getInclineValue;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.getSpeedValue;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.CONSOLE_SYSTEM_EGYM;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.DEVICE_TYPE_ELLIPTICAL;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.DEVICE_TYPE_RECUMBENT_BIKE;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.DEVICE_TYPE_TREADMILL;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.DEVICE_TYPE_UPRIGHT_BIKE;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.IMPERIAL;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.METRIC;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.ON;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.EVENT_NFC_READ;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.EVENT_SET_UNIT;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.MEDIA_MENU_CHANGE;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.SET_ALPHA_BACKGROUND;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DISTANCE_LIMIT;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.MALE;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.NO_LIMIT;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.TIME_LIMIT;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.USER_TYPE_EGYM;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.USER_TYPE_GUEST;
import static com.dyaco.spirit_commercial.support.intdef.MainDashboardBottomButtonIntDef.DISAPPEAR;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.MAX_INC_MAX;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.MAX_LEVEL_MAX;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.MAX_RPM;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.MAX_SPD_IU_MAX;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.MAX_SPD_MU_MAX;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.THR_MAX;
import static com.dyaco.spirit_commercial.support.intdef.WorkoutIntDef.UNLIMITED;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.corestar.libs.device.DeviceGEM;
import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.WindowWorkoutSummaryBinding;
import com.dyaco.spirit_commercial.egym.EgymUtil;
import com.dyaco.spirit_commercial.egym.StackedBarBean;
import com.dyaco.spirit_commercial.model.webapi.BaseApi;
import com.dyaco.spirit_commercial.model.webapi.CloudData;
import com.dyaco.spirit_commercial.model.webapi.EgymWebListener;
import com.dyaco.spirit_commercial.model.webapi.IServiceApi;
import com.dyaco.spirit_commercial.model.webapi.bean.CreateWorkoutParam;
import com.dyaco.spirit_commercial.model.webapi.bean.EgymTrainingPlans;
import com.dyaco.spirit_commercial.model.webapi.bean.UploadWorkoutFromMachineBean;
import com.dyaco.spirit_commercial.product_flavor.ModeEnum;
import com.dyaco.spirit_commercial.support.CommonUtils;
import com.dyaco.spirit_commercial.support.GlideApp;
import com.dyaco.spirit_commercial.support.RxTimer;
import com.dyaco.spirit_commercial.support.SafeClickListener;
import com.dyaco.spirit_commercial.support.WorkoutUtil;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;
import com.dyaco.spirit_commercial.support.intdef.WorkoutIntDef;
import com.dyaco.spirit_commercial.support.room.DatabaseCallback;
import com.dyaco.spirit_commercial.support.room.fitness_test.FitnessTestDbManager;
import com.dyaco.spirit_commercial.support.room.fitness_test.entity_fitness_test.TableAirForce;
import com.dyaco.spirit_commercial.support.room.fitness_test.entity_fitness_test.TableArmy;
import com.dyaco.spirit_commercial.support.room.fitness_test.entity_fitness_test.TableCoastGuard;
import com.dyaco.spirit_commercial.support.room.fitness_test.entity_fitness_test.TableMarines;
import com.dyaco.spirit_commercial.support.room.fitness_test.entity_fitness_test.TableNavy;
import com.dyaco.spirit_commercial.support.room.fitness_test.entity_fitness_test.TablePeb;
import com.dyaco.spirit_commercial.support.room.spirit.SpiritDbManager;
import com.dyaco.spirit_commercial.support.room.spirit.spirit_entity.UploadWorkoutDataEntity;
import com.dyaco.spirit_commercial.support.utils.CheckDoubleClick;
import com.dyaco.spirit_commercial.viewmodel.AppStatusViewModel;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingBean;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingViewModel;
import com.dyaco.spirit_commercial.viewmodel.DiagramBarBean;
import com.dyaco.spirit_commercial.viewmodel.EgymDataViewModel;
import com.dyaco.spirit_commercial.viewmodel.WorkoutViewModel;
import com.dyaco.spirit_commercial.workout.programs.ProgramsEnum;
import com.garmin.health.ConnectionState;
import com.garmin.health.Device;
import com.garmin.health.DeviceManager;
import com.google.gson.Gson;
import com.jeremyliao.liveeventbus.LiveEventBus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class WorkoutSummaryWindow extends BasePopupWindow<WindowWorkoutSummaryBinding> {
    private static final Logger log = LoggerFactory.getLogger(WorkoutSummaryWindow.class);
    boolean isEgymNfc = false;//避免 Apple Watch 開啟 NFC 時 被 EGYM 登入
    public static boolean isUploadDone = false;
    WorkoutViewModel w;
    DeviceSettingViewModel deviceSettingViewModel;
    AppStatusViewModel appStatusViewModel;
    Context context;
    MainActivity m;
    EgymDataViewModel egymDataViewModel;
    int passScore = 60;

    int[] airForceRisk = {R.string.high_risk, R.string.moderate_risk, R.string.low_risk};
    int[] navyCategory = {R.string.failure, R.string.satisfactory, R.string.good, R.string.excellent, R.string.outstanding, R.string.perfect};
    int[] coastGuardCategory = {R.string.very_poor, R.string.poor, R.string.fair, R.string.good, R.string.excellent, R.string.superior};


    String iSpeedAndLevelChartNum;
    String mSpeedAndLevelChartNum;
//    private SystemStrategy systemStrategy;
    //  SystemContext systemContext;

    List<StackedBarBean> hrOutcomeDataList;
    List<StackedBarBean> hrTartgetDataList;


    private String convertSpeedString(String speedStr, boolean isKphToMph) {
        String[] values = speedStr.split("#");
        StringBuilder result = new StringBuilder();

        for (String value : values) {
            float speed = Float.parseFloat(value);
            float convertedSpeed = isKphToMph ? kph2mph(speed) : mph2kph(speed);
            result.append(Math.round(convertedSpeed)).append("#"); // **四捨五入**
        }

        return result.substring(0, result.length() - 1); // 移除最後一個 `#`
    }

    @SuppressLint("ClickableViewAccessibility")
    public WorkoutSummaryWindow(Context context, WorkoutViewModel workoutViewModel, DeviceSettingViewModel deviceSettingViewModel, AppStatusViewModel appStatusViewModel, EgymDataViewModel egymDataViewModel) {
        super(context, 500, 1000, 0, GENERAL.FADE, false, false, true, true);

        m = ((MainActivity) context);
        isSummary = true;

//        systemContext = new SystemContext("spirit");
//        systemContext = new SystemContext(new SystemEgymStrategy());
//        systemContext.executeLogic();

//        systemStrategy = new SystemEgymStrategy();
//        systemStrategy.setupLogic();

        m.btNotifyWindow(false);

        if (m.popupWindow3 != null) {
            m.popupWindow3.dismiss();
            m.popupWindow3 = null;
        }

        this.w = workoutViewModel;
        this.deviceSettingViewModel = deviceSettingViewModel;
        this.appStatusViewModel = appStatusViewModel;
        this.egymDataViewModel = egymDataViewModel;


        initView();
        //   LiveEventBus.get(DISMISS_WINDOW_EVENT, String.class).observeForever(observer1);
        LiveEventBus.get(SET_ALPHA_BACKGROUND, Boolean.class).observeForever(observer2);

        try {
            addLastRawData();
        } catch (Exception e) {
            showException(e);
        }

        appStatusViewModel.changeMainButtonType(DISAPPEAR);

        getBinding().setWorkoutData(workoutViewModel);
        getBinding().setDeviceSetting(deviceSettingViewModel);
        getBinding().setComm(new CommonUtils());
        getBinding().setIsTreadmill(isTreadmill);
        getBinding().setIsUs(isUs);

        getBinding().setUserProfileViewModel(userProfileViewModel);

        this.context = context;
        workoutViewModel.summaryMets.set(m.calculation.getMetsAverage());
        workoutViewModel.summaryPace.set((int) m.calculation.getPaceAverage());

        if (workoutViewModel.selProgram != ProgramsEnum.EGYM) {
            saveDataToWorkOutBean();

            if (UNIT_E == METRIC) {
                mSpeedAndLevelChartNum = w.speedAndLevelChartNum;
                iSpeedAndLevelChartNum = convertSpeedString(w.speedAndLevelChartNum, true);
            } else {
                iSpeedAndLevelChartNum = w.speedAndLevelChartNum;
                mSpeedAndLevelChartNum = convertSpeedString(w.speedAndLevelChartNum, false);
            }
        } else {
            saveDataToWorkOutBeanEgym();
        }


//        try {
//            initChart();
//        } catch (Exception e) {
//            showException(e);
//        }

        if (workoutViewModel.selProgram.getProgramType() == WorkoutIntDef.FITNESS_TESTS) {
            try {
                getScore();
            } catch (Exception e) {
                showException(e);
            }
        }
        startCountDown();
        //  if (countDownTimer != null) countDownTimer.start();

        LiveEventBus.get(MEDIA_MENU_CHANGE).post("");

        // TODO: NEW GEM3
        //FTMS 設備狀態 IDLE
        //   Map<DeviceGEM.EQUIPMENT_CONTROL_PARAMETER, Integer> parameters = new HashMap<>();
        //    m.updateFtmsMachineStatus(DeviceGEM.FITNESS_EQUIPMENT_STATE.PAUSE_OR_STOPPED_BY_THE_USER, parameters);
        getDeviceGEM().gymConnectMessageSetTrainingStatus(DeviceGEM.TRAINING_STATUS.IDLE);
        //      m.updateFtmsMachineStatus2(DeviceGEM.TRAINING_STATUS.IDLE);


        // saveDeviceData();

        updateDeviceData();


        LiveEventBus.get(EVENT_SET_UNIT, Boolean.class).observeForever(observer);
        orgUnit = UNIT_E;

        //結束workout
        new RxTimer().timer(500, number -> {
            if (isTreadmill) {
                m.uartConsole.setDevWorkoutFinish();
            } else {
                m.uartConsole.emsWorkoutFinish();
            }
        });


        initEgymWebApi();

        initSummaryEgym();

        initSummaryEgymBottom();

        try {
            initChart();
        } catch (Exception e) {
            showException(e);
        }


        //在Workout時沒有連到Garmin，離開workout後，找一台Garmin設備開啟RealTimeData

        if (!w.isWorkoutGarmin.get()) {
            if (m.mGarminDeviceManager == null) return;
            //   if (DeviceManager.getDeviceManager() == null) return;
            Set<Device> pairedDevices = DeviceManager.getDeviceManager().getPairedDevices();
            if (pairedDevices == null) return;
            for (Device d : pairedDevices) {
                if (d.connectionState() == ConnectionState.CONNECTED) {
                    m.enableRealTimeData(d);
                    break;
                }
            }
        }

        w.isWorkoutGarmin.set(false);


        //重新計時
        getBinding().clBase.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                startCountDown();
            }
            return false;
        });


        //重新計時
        getBinding().baseScrollView.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                startCountDown();
            }
            return false;
        });

        getBinding().baseScrollViewEgym.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                startCountDown();
            }
            return false;
        });


//        if (MainActivity.limitLoutOut) {
//            getBinding().btnCloseAndLogout.callOnClick();
//        }
    }

    private void initSummaryEgymBottom() {
        if (deviceSettingViewModel.consoleSystem.get() != CONSOLE_SYSTEM_EGYM) return;
        getBinding().layoutEgymBottom.btnEgymSyncText.setVisibility(View.VISIBLE);
        getBinding().layoutEgymBottom.egymProgress.setVisibility(View.VISIBLE);
        getBinding().layoutEgymBottom.setWorkoutData(w);


        //EGYM GUEST
        if (userProfileViewModel.userType.get() != USER_TYPE_EGYM) {
            animateMarginStart(getBinding().layoutEgymBottom.mC, getBinding().layoutEgymBottom.tvEgymApiResult, 774, 32, 0);
            getBinding().layoutEgymBottom.tvEgymApiResult.setText(R.string.to_sync_your_workout_data_please_log_in);
            //     getBinding().layoutEgymBottom.tvEgymApiResult.setVisibility(View.VISIBLE);
            getBinding().layoutEgymBottom.tvEgymApiResult.setIcon(ContextCompat.getDrawable(mContext, R.drawable.icon_warning_32));
            getBinding().layoutEgymBottom.btnLogoutE.setText(R.string.Exit);

            getBinding().layoutEgymBottom.tvEgymApiResult.setOnClickListener(v -> {
                if (CheckDoubleClick.isFastClick()) return;

                //避免 Apple Watch 開啟 NFC 時 被 EGYM 登入
                isEgymNfc = true;

                getBinding().cLoginEgym.setVisibility(View.VISIBLE);
                getBinding().vBackground.setVisibility(View.VISIBLE);
                ((MainActivity) mContext).getBinding().bgE.setVisibility(View.VISIBLE);


                getDeviceGEM().nfcMessageEnableNfcRadio(DeviceGEM.NFC_READ_EVENT.NFC_READ);

            });

            //     getBinding().layoutEgymBottom.tvEgymApiResult.setText(HtmlCompat.fromHtml(mContext.getString(R.string.to_sync_your_workout_data_please_log_in), HtmlCompat.FROM_HTML_MODE_LEGACY));

            LiveEventBus.get(EVENT_NFC_READ, String.class).observeForever(observer3);

            getBinding().btnCloseEE.setOnClickListener(new SafeClickListener() {
                @Override
                public void onSafeClick(View v) {
                    //避免 Apple Watch 開啟 NFC 時 被 EGYM 登入
                    isEgymNfc = false;
                    isLogin = false;
                    getBinding().cLoginEgym.setVisibility(View.GONE);
                    getBinding().vBackground.setVisibility(View.GONE);
                    ((MainActivity) mContext).getBinding().bgE.setVisibility(View.GONE);
                    getDeviceGEM().nfcMessageDisableNfcRadio();
                }
            });

            getBinding().cLoginEgym.setOnClickListener(new SafeClickListener() {
                @Override
                public void onSafeClick(View v) {
                    //    egymLogin(RFID_CODE);


//                    EgymUtil.getInstance().getEgymSecret(new EgymWebListener() {
//                        @Override
//                        public void onFailText(String errorText) {
//                        //    mainActivity.showWebApiAlert(true,errorText);
//                        }
//                    });
                }
            });

            getBinding().layoutEgymBottom.tvEgymApiResult.setIconTint(ContextCompat.getColorStateList(mContext, R.color.colore24b44));

            getBinding().layoutEgymBottom.btnHomeE.setVisibility(View.VISIBLE);
            getBinding().layoutEgymBottom.btnLogoutE.setVisibility(View.VISIBLE);

            getBinding().layoutEgymBottom.egymProgress.setVisibility(View.INVISIBLE);
            getBinding().layoutEgymBottom.btnEgymSyncText.setVisibility(View.INVISIBLE);

            getBinding().layoutEgymBottom.tvEgymLogoutTimeText.setVisibility(View.INVISIBLE);
        }
    }

    private void initSummaryEgym() {

        //使用EGYM Program 才顯示 EGYM Summary 畫面
        if (w.selProgram == ProgramsEnum.EGYM) {
            //    if (!isSpirit) {

            //todo View STUB
//            if (getBinding().viewSSS.getViewStub() != null) {
//                View headerView = getBinding().viewSSS.getViewStub().inflate();
//                IncludeSummaryEgymBinding includeSummaryEgymBinding;
//                includeSummaryEgymBinding = DataBindingUtil.bind(headerView);
//                if (includeSummaryEgymBinding != null) {
//                    includeSummaryEgymBinding.setWorkoutData(w);
//                    includeSummaryEgymBinding.btnEgymSyncText.setVisibility(View.VISIBLE);
//                    includeSummaryEgymBinding.btnEgymSyncText.setText("!!!!!!!!!!!!!!!!!!!");
//                }
//            }


            getBinding().layoutEgym.setWorkoutData(w);
            getBinding().layoutEgym.setDeviceSetting(deviceSettingViewModel);
            getBinding().layoutEgym.setIsTreadmill(isTreadmill);
            //  getBinding().gEgym.setVisibility(View.VISIBLE);
            //    getBinding().layoutEgym.summaryLeftView.setVisibility(View.VISIBLE);
            getBinding().gSpirit.setVisibility(View.GONE);


            //Egym Heart Rate Diagram

            hrTartgetDataList = egymDataViewModel.selTrainer.getIntervals().stream()
                    .map(interval -> {
                        double value = Optional.ofNullable(interval.getHeartRate()).orElse(0);
                        int secTime = Optional.ofNullable(interval.getDuration()).orElse(0) / 1000;
                        StackedBarBean bean = new StackedBarBean();
                        bean.setValue(value);
                        bean.setSecTime(secTime);
                        return bean;
                    }).collect(Collectors.toList());


            //EGYM HR
            hrOutcomeDataList = egymDataViewModel.woIntervalData.stream()
                    .map(interval -> {
                        double value = Optional.ofNullable(interval.getHeartRate()).orElse(0);
                        int secTime = Optional.ofNullable(interval.getDuration()).orElse(0) / 1000;
                        StackedBarBean bean = new StackedBarBean();
                        bean.setValue(value);
                        bean.setSecTime(secTime);
                        return bean;
                    }).collect(Collectors.toList());


            //如果Target duration 為0, 就把 實際跑過的時間放入
            IntStream.range(0, hrTartgetDataList.size())
                    .filter(i -> hrTartgetDataList.get(i).getSecTime() == 0 && i < hrOutcomeDataList.size())
                    .forEach(i -> hrTartgetDataList.get(i).setSecTime(hrOutcomeDataList.get(i).getSecTime()));


//            for (StackedBarBean x : hrOutcomeDataList) {
//                Log.d(EgymUtil.TAG, "心跳: " + x.getValue() + "," + x.getSecTime());
//            }

            w.avgHeartRate.set((float) hrOutcomeDataList.stream()
                    .mapToDouble(StackedBarBean::getValue)
                    .average()
                    .orElse(0.0));


            //設定值
            onChangeUnit(true);

        } else {
            //非EGYM
            //   getBinding().layoutEgym.summaryLeftView.setVisibility(View.GONE);
            //  getBinding().gEgym.setVisibility(View.GONE);
            getBinding().gSpirit.setVisibility(View.VISIBLE);
            webApiUploadWorkoutFromMachineBean();
        }
    }


    private void initEgymWebApi() {
        // TODO EGYM   不管是不是EGYM PROGRAM，有登入EGYM 就要上傳 EGYM API

        // if (userProfileViewModel.userType.get() != USER_TYPE_EGYM) return;

        if (deviceSettingViewModel.consoleSystem.get() != CONSOLE_SYSTEM_EGYM) return;


        //儲存最後一筆 Interval
        EgymUtil.getInstance().saveInterval(w, egymDataViewModel);


        if (egymDataViewModel.selTrainer == null) {
            egymDataViewModel.selTrainer = new EgymTrainingPlans.TrainerDTO();
        }


        long trainingPlanId = isValidIdL(egymDataViewModel.selTrainer.getTrainingPlanId())
                ? egymDataViewModel.selTrainer.getTrainingPlanId()
                : 57579321596313601L;

        long trainingPlanExerciseId = isValidIdL(egymDataViewModel.selTrainer.getTrainingPlanExerciseId())
                ? egymDataViewModel.selTrainer.getTrainingPlanExerciseId()
                : 59374017637253121L;

        int frequency = isValidIdI(egymDataViewModel.selTrainer.getFrequency()) ? egymDataViewModel.selTrainer.getFrequency() : 0;

        egymDataViewModel.createWorkoutParam.setUniqueExerciseId(UUID.randomUUID().toString());
        egymDataViewModel.createWorkoutParam.setEndTimestamp(System.currentTimeMillis());
        egymDataViewModel.createWorkoutParam.setTimezone(TimeZone.getDefault().getID());
        egymDataViewModel.createWorkoutParam.setFrequency(frequency); //頻率,每周應重複此練習的次數。
//            egymDataViewModel.createWorkoutParam.setExerciseName(egymDataViewModel.currentExerciseName.get());
        egymDataViewModel.createWorkoutParam.setExerciseName(EGYM_MACHINE_TYPE); //為登入上傳， 要取 plan 的 ExerciseName, 還是機型名稱
//        egymDataViewModel.createWorkoutParam.setExerciseName("Elliptical "); //為登入上傳， 要取 plan 的 ExerciseName, 還是機型名稱
        egymDataViewModel.createWorkoutParam.setKiloCalories((int) w.totalCalories.get());
        egymDataViewModel.createWorkoutParam.setAverageHeartRate((int) w.avgHeartRate.get());
        egymDataViewModel.createWorkoutParam.setTrainingPlanId(trainingPlanId); //訓練計劃ID。當使用者從訓練計劃中選擇鍛煉時，此字段應在提交鍛煉時包含該鍛煉的訓練計劃ID。
        egymDataViewModel.createWorkoutParam.setAveragePace(w.currentPace.get());
        egymDataViewModel.createWorkoutParam.setTrainingPlanExerciseId(trainingPlanExerciseId);

        egymDataViewModel.createWorkoutParam.setIntervals(egymDataViewModel.woIntervalData);


        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String timeStr = format.format(new Date(egymDataViewModel.createWorkoutParam.getEndTimestamp()));

        Log.d("EgymUtil", "現在時間是: " + timeStr);

        //  Log.d(EgymUtil.TAG, "取得要上傳的資料 先放在 viewModel: ");
        if (userProfileViewModel.userType.get() != USER_TYPE_EGYM) return;

        final String requestBody = new Gson().toJson(egymDataViewModel.createWorkoutParam);
        //         LogS.printJson("###EGYMMMMM", requestBody, "apiCreateWorkouts");


        EgymUtil.getInstance().apiCreateWorkouts(requestBody, new EgymWebListener() {
            @Override
            public void onSuccess(String result) {
                if (getBinding() != null) {
                    getBinding().layoutEgymBottom.btnEgymSyncText.setVisibility(View.GONE);
                    getBinding().layoutEgymBottom.egymProgress.setVisibility(View.GONE);

                    animateMarginStart(getBinding().layoutEgymBottom.mC, getBinding().layoutEgymBottom.tvEgymApiResult, 774, 32, 300);
                    //     getBinding().layoutEgymBottom.tvEgymApiResult.setVisibility(View.VISIBLE);

                    getBinding().layoutEgymBottom.tvEgymLogoutTimeText.setAlpha(0f);
                    getBinding().layoutEgymBottom.tvEgymLogoutTimeText.setVisibility(View.VISIBLE);
                    getBinding().layoutEgymBottom.tvEgymLogoutTimeText.animate().alpha(1f).setDuration(600).start();

                    getBinding().layoutEgymBottom.btnHomeE.setVisibility(View.VISIBLE);
                    getBinding().layoutEgymBottom.btnLogoutE.setVisibility(View.VISIBLE);
                }

                Log.d(EgymUtil.TAG, "apiCreateWorkout 上傳成功: " + result);
            }

            @Override
            public void onFail(Throwable error, Integer httpCode) {
                if (getBinding() != null) {
                    getBinding().layoutEgymBottom.btnEgymSyncText.setVisibility(View.GONE);
                    getBinding().layoutEgymBottom.egymProgress.setVisibility(View.GONE);
                    getBinding().layoutEgymBottom.btnHomeE.setVisibility(View.VISIBLE);
                    getBinding().layoutEgymBottom.btnLogoutE.setVisibility(View.VISIBLE);
                    egymApiError();
                }
                Log.d(EgymUtil.TAG, "apiCreateWorkout  ErrorMsg:" + error.getMessage() + ",httpCode:" + httpCode);

                //上傳失敗,存到資料庫
                EgymUtil.getInstance().insertEgym(requestBody);
            }
        });
    }

    int orgUnit;
    Observer<Boolean> observer = this::onChangeUnit;
    Observer<String> observer3 = this::egymLogin;

    boolean isLogin;

    private void egymLogin(String rfidCode) {

        Log.d("EgymUtil", "egymLogin: " + isEgymNfc + ", " + isLogin);
        //避免 Apple Watch 開啟 NFC 時 被 EGYM 登入
        if (!isEgymNfc) return;

        if (isLogin){
            getBinding().btnCloseEE.callOnClick();
            return;
        }
        ((MainActivity) mContext).uartConsole.setBuzzer();
        isLogin = true;
//        RFID_CODE = toHex(rfidCode);
//        RFID_CODE = rfidCode;
        RFID_CODE = reverseUidHex(rfidCode);


        //⭐️egymClientId  WEB_API , apiGetGymInfo 取得
        //⭐️egymClientSecret WEB_API , apiGetGymInfo 取得
        EgymUtil.getInstance().getEgymSecret(new EgymWebListener() {
            @Override
            public void onFail() {
                isLogin = false;
            }
        });

//        EgymUtil.getInstance().loginEgymK(RFID_CODE, new EgymWebListener() {
//            @Override
//            public void onFail() {
//                isLogin = false;
//            }
//        });
    }

    private void egymApiError() {

        //     getBinding().layoutEgym.tvEgymApiResult.setIcon(ContextCompat.getDrawable(getApp(), R.drawable.icon_warning_40));
        //     getBinding().layoutEgym.tvEgymApiResult.setIconTint(ColorStateList.valueOf(ContextCompat.getColor(getApp(), R.color.colore24b44)));

        animateMarginStart(getBinding().layoutEgymBottom.mC, getBinding().layoutEgymBottom.tvEgymApiResult, 774, 32, 300);
        getBinding().layoutEgymBottom.tvEgymApiResult.setText(R.string.queued_to_sync_when_back_online);
        //    getBinding().layoutEgymBottom.tvEgymApiResult.setVisibility(View.VISIBLE);
        getBinding().layoutEgymBottom.tvEgymLogoutTimeText.setVisibility(View.VISIBLE);
    }

    //設定值
    private void onChangeUnit(Boolean aBoolean) {

        if (w.selProgram == ProgramsEnum.EGYM) {


            if (isTreadmill) {
                //四捨五入
                getBinding().layoutEgym.tvDistanceText.setText(getConvertHalfUp(w.currentDistance.get(), orgUnit, 3, 2));
                getBinding().layoutEgym.tvAscentText.setText(getConvert(w.currentElevationGain.get(), orgUnit, 4, 2));
                getBinding().layoutEgym.tvHrTextE.setText(String.valueOf((int) w.avgHeartRate.get()));


                //SPEED 固定  m/s, 公尺/每秒
                //算已跑過的平均速度
                egymDataViewModel.woIntervalData.stream()
                        .mapToDouble(CreateWorkoutParam.IntervalsDTO::getSpeed)
                        .average()
                        .ifPresent(avg -> w.avgSpeed.set(avg * 3.6));

                egymDataViewModel.woIntervalData.stream()
                        .mapToDouble(CreateWorkoutParam.IntervalsDTO::getIncline)
                        .average()
                        .ifPresent(incline -> w.avgIncline.set((float) incline));


                //四捨五入
                getBinding().layoutEgym.tvSpeedTextE.setText(getConvertHalfUp(w.avgSpeed.get(), orgUnit, 2, 2));
                getBinding().layoutEgym.tvInclineTextE.setText(String.valueOf(Math.round(w.avgIncline.get())));
                getBinding().layoutEgym.tvTotalTimeText.setText(CommonUtils.formatSecToHMS(w.totalElapsedTimeShow.get()));
                getBinding().layoutEgym.tvActiveCalorieTextE.setText(formatDecimal((float) w.totalCalories.get()));
                getBinding().layoutEgym.tvAvgMetsTextE.setText(formatDecimal((float) w.avgMet.get()));
                getBinding().layoutEgym.tvAvgPaceTextE.setText(convertPace(Double.parseDouble(getConvert(w.summaryPace.get(), orgUnit, 3, 2))));
                getBinding().layoutEgym.tvAvgOutputTextE.setText(formatDecimal((float) w.avgPower.get()));

                getBinding().tvSpeedAndLevelUnitE.setText(UNIT_E == METRIC ? R.string.km_h : R.string.mph);


                int speedMax = (UNIT_E == METRIC) ? MAX_SPD_MU_MAX : MAX_SPD_IU_MAX;


                // TODO: 2025/3/21  每個 interval 存的 速度 還要再確認

                //已跑過的資料
                List<StackedBarBean> speedOutcomeDataList = egymDataViewModel.woIntervalData.stream()
                        .map(interval -> {
                            double rawSpeed = Optional.ofNullable(interval.getSpeed()).orElse(0.0);
                            // 先將 m/s 轉換為 km/h
                            double speedKph = rawSpeed * 3.6;
                            // 若為 IMPERIAL 則轉換為 mph，否則維持 km/h
                            double convertedSpeed = UNIT_E == IMPERIAL ? kph2mph(speedKph) : speedKph;
                            int secTime = Optional.ofNullable(interval.getDuration()).orElse(0) / 1000;
                            StackedBarBean bean = new StackedBarBean();
                            bean.setValue(convertedSpeed);
                            bean.setSecTime(secTime);
                            return bean;
                        }).collect(Collectors.toList());


                List<StackedBarBean> speedTartgetDataList = egymDataViewModel.selTrainer.getIntervals().stream()
                        .map(interval -> {
                            double rawSpeed = Optional.ofNullable(interval.getSpeed()).orElse(0.0);
                            double convertedSpeed = UNIT_E == IMPERIAL ? kph2mph(rawSpeed) : rawSpeed;
                            int secTime = Optional.ofNullable(interval.getDuration()).orElse(0) / 1000;
                            StackedBarBean bean = new StackedBarBean();
                            bean.setValue(convertedSpeed);
                            bean.setSecTime(secTime);
                            return bean;
                        }).collect(Collectors.toList());

                //如果Target duration 為0, 就把 實際跑過的時間放入
                IntStream.range(0, speedTartgetDataList.size())
                        .filter(i -> speedTartgetDataList.get(i).getSecTime() == 0 && i < speedOutcomeDataList.size())
                        .forEach(i -> speedTartgetDataList.get(i).setSecTime(speedOutcomeDataList.get(i).getSecTime()));


                //    Log.d("BBBBBB", "initChart: " + speedTartgetDataList.toString());
//                for (StackedBarBean x : levelTartgetDataList) {
//                    Log.d("BBBBBB", "initChart: " + levelTartgetDataList);
//                }


                //          Log.d("AAAAAAAAAA", "@@: " + speedOutcomeDataList.toString());
                getBinding().stackedBarChartSpeedView.setMaxDataValue((speedMax / 10));// 24 or 15
                getBinding().stackedBarChartSpeedView.setOutcomeData(speedOutcomeDataList);
                getBinding().stackedBarChartSpeedView.setTargetData(speedTartgetDataList);

                getBinding().stackedBarChartSpeedView.setBarColors(
                        ContextCompat.getColor(mContext, R.color.color1396ef_90),
                        ContextCompat.getColor(mContext, R.color.color1396ef_20),
                        ContextCompat.getColor(mContext, R.color.color1A1396ef)  // Target 颜色
                );
            }


        } else {
            if (isTreadmill) {
                getBinding().tvSummaryDistanceAndPower.setText(getConvert(w.currentDistance.get(), orgUnit, 3, 2)); // mi km
                getBinding().tvSummaryElevationAndDistance.setText(getConvert(w.currentElevationGain.get(), orgUnit, 0, 1)); // ft cm
                getBinding().tvSpeedAndLevelNum.setText(getConvert(w.avgSpeed.get(), orgUnit, 2, 1));
                getBinding().tvSummaryPaceAndSpeed.setText(CommonUtils.formatSecToM((long) Float.parseFloat(getConvert(w.summaryPace.get(), orgUnit, 3, 1))));
            } else {
                getBinding().tvSummaryElevationAndDistance.setText(getConvert(w.currentDistance.get(), orgUnit, 1, 2)); // ft cm
            }
        }
    }

    int age;

    private void getScore() {

        age = Math.max(w.selYO.get(), 17);

        Log.d("SUMMARY", "getScore: age:" + age + ", gerder:" + w.selGender.get() + ", time:" + w.elapsedTime.get());

        switch (w.selProgram) {
            case ARMY:
                age = Math.min(w.selYO.get(), 41); //Table只到41歲
                //     w.elapsedTime.set(60 * 15);
                getBinding().viewScoreTextNum.setText(String.format("%s/100", 0));
                testResult(false);

                FitnessTestDbManager.getInstance(getApp().getApplicationContext()).
                        getArmy(w.selGender.get(), age, w.elapsedTime.get(),
                                new DatabaseCallback<TableArmy>() {
                                    @Override
                                    public void onDataLoadedBean(TableArmy entity) {
                                        super.onDataLoadedBean(entity);
                                        int point = w.isWorkoutDone() ? entity.getPoints() : 0;
                                        getBinding().viewScoreTextNum.setText(String.format("%s/100", getShowString(GENERAL.DO_VO2_1D, point)));
                                        getBinding().tvResult.setText("");
                                        getBinding().viewScoreText1.setText(R.string.Your_Score);
                                        Log.d("ARMY_TEST", "onDataLoadedBean: " + entity.toString());
                                        Log.d("ARMY_TEST", "getScore: " + age + "," + w.selGender.get() + "," + w.elapsedTime.get());

                                        testResult(point >= 50);
                                    }
                                });
                break;
            case MARINE_CORPS:

                getBinding().viewScoreTextNum.setText(String.format("%s/100", 0));
                testResult(false);

                FitnessTestDbManager.getInstance(getApp().getApplicationContext()).
                        getMarine(w.selGender.get(), w.elapsedTime.get(),
                                new DatabaseCallback<TableMarines>() {
                                    @Override
                                    public void onDataLoadedBean(TableMarines entity) {
                                        super.onDataLoadedBean(entity);
                                        int point = w.isWorkoutDone() ? entity.getPoints() : 0;
                                        getBinding().viewScoreTextNum.setText(String.format("%s/100", getShowString(GENERAL.DO_VO2_1D, point)));
                                        getBinding().tvResult.setText("");
                                        getBinding().viewScoreText1.setText(R.string.Your_Score);
                                        Log.d("MARINE_CORPS_TEST", "onDataLoadedBean: " + entity.toString());
                                        testResult(point > 60);
                                    }
                                });
                break;
            case AIR_FORCE:

                getBinding().viewScoreTextNum.setText(String.format("%s/60", 0));
                getBinding().tvResult.setText(context.getString(airForceRisk[0]));
                testResult(false);

                FitnessTestDbManager.getInstance(getApp().getApplicationContext()).
                        getAirForce(w.selGender.get(), age, w.elapsedTime.get(),
                                new DatabaseCallback<TableAirForce>() {
                                    @Override
                                    public void onDataLoadedBean(TableAirForce entity) {
                                        super.onDataLoadedBean(entity);
                                        int point = w.isWorkoutDone() ? entity.getPoints() : 0;
                                        getBinding().viewScoreTextNum.setText(String.format("%s/60", getShowString(GENERAL.DO_VO2_1D1, point)));
                                        getBinding().tvResult.setText(context.getString(airForceRisk[entity.getCategory() - 1]));
                                        testResult(point >= 60);

                                        Log.d("AIR_FORCE_TEST", "onDataLoadedBean: " + entity.toString());
                                    }
                                });
                break;
            case NAVY:

                getBinding().tvResult.setText(context.getString(navyCategory[0]));
                getBinding().viewScoreTextNum.setText(String.format("%s/100", 0));

                getBinding().tvResult.setText(context.getString(navyCategory[0]));
                getBinding().tvResult.setBackgroundResource(R.drawable.panel_bg_all_4_e24b44);

//                age = 30;
//                w.selGender.set(1);
//                w.elapsedTime.set(700);
//                w.setWorkoutDone(true);
                Log.d("NAVY_TEST", "getScore: " + w.selGender.get() + "," + age + "," + w.elapsedTime.get());

                FitnessTestDbManager.getInstance(getApp().getApplicationContext()).
                        getNavy(w.selGender.get(), age, w.elapsedTime.get(),
                                new DatabaseCallback<TableNavy>() {
                                    @Override
                                    public void onDataLoadedBean(TableNavy entity) {
                                        super.onDataLoadedBean(entity);
                                        try {
                                            int point = w.isWorkoutDone() ? entity.getPoints() : 0;
                                            int category = w.isWorkoutDone() ? entity.getCategory() : 1;
                                            getBinding().tvResult.setText(context.getString(navyCategory[category - 1]));
                                            getBinding().viewScoreTextNum.setText(String.format("%s/100", getShowString(GENERAL.DO_VO2_1D, point)));
                                            Log.d("NAVY_TEST", "onDataLoadedBean: " + entity.toString());
                                            Log.d("NAVY_TEST", "onDataLoadedBean: " + navyCategory[category - 1]);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        //    testResult(point >= 60);
                                    }
                                });
                break;
            case PEB:

                if (w.selYO.get() > 41) w.selYO.set(41);
                getBinding().viewScoreTextNum.setText(String.format("%s/99", 0));
                getBinding().viewScoreText1.setText(R.string.Your_Score);
                testResult(false);

                //Percentiles 75%以上 pass
                FitnessTestDbManager.getInstance(getApp().getApplicationContext()).
                        getPeb(w.selGender.get(), age, w.elapsedTime.get(),
                                new DatabaseCallback<TablePeb>() {
                                    @Override
                                    public void onDataLoadedBean(TablePeb entity) {
                                        super.onDataLoadedBean(entity);
                                        int point = w.isWorkoutDone() ? entity.getPercentiles() : 0;
                                        getBinding().viewScoreTextNum.setText(String.format("%s/99", getShowString(GENERAL.DO_VO2_1D, point)));
                                        getBinding().tvResult.setText("");
                                        getBinding().viewScoreText1.setText(R.string.Your_Score);
                                        Log.d("SUMMARY", "onDataLoadedBean: " + entity.toString());
                                        testResult(point >= 75);
                                    }
                                });
                break;
            case COAST_GUARD:

//                w.selGender.set(0);
//                age = 30;
//                w.elapsedTime.set(646);
//                w.setWorkoutDone(true);

                getBinding().viewScoreTextNum.setText(context.getString(coastGuardCategory[0]));

                //預設
                testResult(false);

                FitnessTestDbManager.getInstance(getApp().getApplicationContext()).
                        getCoastGuard(w.selGender.get(), age, w.elapsedTime.get(),
                                new DatabaseCallback<TableCoastGuard>() {
                                    @Override
                                    public void onDataLoadedBean(TableCoastGuard entity) {
                                        super.onDataLoadedBean(entity);
                                        int category = w.isWorkoutDone() ? entity.getCategory() - 1 : 0;
                                        String result = context.getString(coastGuardCategory[category]);
                                        getBinding().viewScoreTextNum.setText(result);
                                        getBinding().tvResult.setText("");
                                        Log.d("COAST_GUARD_TEST", "onDataLoadedBean: " + entity.toString());
                                        testResult(category >= 3);
                                    }
                                });
                break;
            case CTT_PERFORMANCE:
                //使用Workout經過的時間算分數(秒)
                int score = WorkoutUtil.getCttPerformanceVO2(w.elapsedTime.get());

                Log.d("CTT_PERFORMANCE_TEST", "getScore: " + w.elapsedTime.get() + "," + score);
                getBinding().tvResult.setText("");
                getBinding().viewScoreText1.setText(R.string.Your_VO2_Score);
                getBinding().viewScoreTextNum.setText(String.valueOf(score));
                break;
            case CTT_PREDICTION:


//            CTT prediction判斷為Test Invalid的情況:
//            (1) 從level 2開始, 只要目前level的HR數值小於前一個level
//            (2) 完成的level數不到2個
//            (3) 計算出的VO2高於100

                getBinding().tvResult.setText("");
                getBinding().viewScoreText1.setText(R.string.Your_VO2_Score);
                String resultText;


                Log.d("CttPrediction_TEST", "結果: " + w.cttLevelHrList.toString());

                double temp;
                boolean isInvalid = false;

                //(1) 2 至 6 級 HR 值等於或小於以前的 HR 讀數。
                for (int i = 0; i < w.cttLevelHrList.size(); i++) {
                    if (i > 0) {
                        temp = w.cttLevelHrList.get(i - 1);
                        if (w.cttLevelHrList.get(i) <= temp) {
                            isInvalid = true;
                            Log.d("CttPrediction_TEST", "結果1: 2 至 6 級 HR 值等於或小於以前的 HR 讀數: " + isInvalid);
                        }
                    }
                }

                Log.d("CttPrediction_TEST", "getScore: " + isInvalid);

                //測試失敗
                if (isInvalid) {
                    getBinding().viewScoreTextNum.setTextSize(70);
                    getBinding().viewScoreTextNum.setText(R.string.TEST_INVALID);
                    return;
                }

                //取得分數
                int vo2 = (int) Math.round(m.calculation.cttPredictionVO2(w.cttLevelHrList));
                Log.d("CttPrediction_TEST", "結果44:分數: " + vo2);
                if (vo2 <= 0 || vo2 > 100) {
                    getBinding().viewScoreTextNum.setTextSize(70);
                    resultText = context.getString(R.string.TEST_INVALID);
                    // (2) 用戶必須完成前兩個級別，否則測試無效。
                    // (3) 計算得出的 VO2 分數超過 100
                    Log.d("CttPrediction_TEST", "結果2:分數: " + vo2);
                } else {
                    resultText = String.valueOf(vo2);
                }
                Log.d("CttPrediction_TEST", "getScore: " + w.isWorkoutDone() + "," + resultText);
                getBinding().viewScoreTextNum.setText(resultText);
                break;

            case GERKIN:
                float gerkinVo2;
                try {
                    gerkinVo2 = w.isWorkoutDone() ? WorkoutUtil.getGerkinVO2(w.currentStage.get()) : 0;
                    //  gerkinVo2 = WorkoutUtil.getGerkinVO2(w.currentStage.get());
                } catch (Exception e) {
                    e.printStackTrace();
                    gerkinVo2 = 0;
                    Log.d("GERKIN_TEST", "getScore: " + e.getLocalizedMessage());
                }
                Log.d("GERKIN_TEST", "getScore: " + w.isWorkoutDone());
                Log.d("GERKIN_TEST", "getScore: " + w.currentStage.get());
                getBinding().tvResult.setText("");
                getBinding().viewScoreText1.setText(R.string.Your_VO2_Score);
                getBinding().viewScoreTextNum.setText(String.valueOf(gerkinVo2));
                break;

            case WFI:
                Log.d("WFI_TEST", "getScore: " + w.isWorkoutDone() + ", TT:" + w.getWfiTT());
                //VO2 max = 56.981 + (1.242 * TT) – (0.805 * BMI)
                int wfiVo2 = w.isWorkoutDone() ? (int) Math.round(m.calculation.wfiVo2(w.getWfiTT())) : 0;
                getBinding().tvResult.setText("");
                getBinding().viewScoreText1.setText(R.string.Your_VO2_Score);
                getBinding().viewScoreTextNum.setText(String.valueOf(wfiVo2));
                break;

            case FITNESS_TEST:

                if (w.isWorkoutDone()) {
                    //  int Vo2 = (int) (w.getVo2Max() * 100);
                    Log.d("FITNESS_TEST_PROGRAM", "測試成功,getScore: " + w.getVo2Max());
                    getBinding().viewScoreTextNum.setText(formatDecimal((float) w.getVo2Max()));
                    getBinding().tvResult.setText("");
                    getBinding().viewScoreText1.setText(R.string.Your_VO2_Score);
                } else {
                    Log.d("FITNESS_TEST_PROGRAM", "測試失敗, getScore: " + w.getVo2Max());
                    getBinding().viewScoreTextNum.setText(R.string.TEST_INVALID);
                    getBinding().viewScoreTextNum.setTextSize(80);
                }
        }
    }

    private void testResult(boolean isPass) {
        getBinding().tvResult.setText(isPass ? context.getString(R.string.You_Passed) : context.getString(R.string.You_Did_Not_Pass));
        getBinding().tvResult.setBackgroundResource(isPass ? R.drawable.panel_bg_all_4_0dac87 : R.drawable.panel_bg_all_4_e24b44);
    }

//    Observer<String> observer1 = s -> {
//       if (isUploadDone) {
//           dismiss();
//       }
//    };

    Observer<Boolean> observer2 = s -> {
        if (getBinding() != null)
            getBinding().vBackground.setVisibility(s ? View.VISIBLE : View.INVISIBLE);
    };


    private void initView() {

        if (deviceSettingViewModel.consoleSystem.get() == CONSOLE_SYSTEM_EGYM) {
            getBinding().layoutEgymBottom.btnHomeE.setOnClickListener(v -> {
                if (isLimitOut) {
                    getBinding().layoutEgymBottom.btnLogoutE.callOnClick();
                } else {
                    //    if (!isUploadDone) return;
                    if (CheckDoubleClick.isFastClick()) return;
                    getBinding().vBackground.setVisibility(View.VISIBLE);
                    m.onUserInteraction();
                    dismiss();
                }
            });

            getBinding().layoutEgymBottom.btnLogoutE.setOnClickListener(v -> {
                //  if (!isUploadDone) return;
                if (CheckDoubleClick.isFastClick()) return;
                try {
                    getBinding().vBackground.setVisibility(View.VISIBLE);
                    dismiss();
                } catch (Exception e) {
                    showException(e);
                }

                m.webApiLogout();
            });
        } else {
            getBinding().btnClose.setOnClickListener(v -> {

                if (isLimitOut) {
                    getBinding().btnCloseAndLogout.callOnClick();
                } else {
                    if (!isUploadDone) return;
                    if (CheckDoubleClick.isFastClick()) return;
                    getBinding().vBackground.setVisibility(View.VISIBLE);
                    dismiss();
                }
            });

            getBinding().btnCloseAndLogout.setOnClickListener(v -> {

                if (!isUploadDone) return;
                if (CheckDoubleClick.isFastClick()) return;
                try {
                    getBinding().vBackground.setVisibility(View.VISIBLE);
                    dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }

//            if(isUs) {
//                m.showLoading(true);
//
//                clearCookies();
//                clearAppData();
//                closePackage(getApp());
//
//                new RxTimer().timer(1000, u -> m.showLoading(false));
//            } else {
                m.webApiLogout();
                //    }

                // dismiss();
            });
        }
    }

    private void saveDataToWorkOutBeanEgym() {


        Date date = Calendar.getInstance().getTime();
        w.setUpdateTime(date);


//        if (isTreadmill) {
//
//            w.avgIncline.set(avgIncline);
//
//        } else {
//
//            w.avgSpeed.set(ca);
//
//        }
    }


    private void saveDataToWorkOutBean() {

        if (w.selProgram.getProgramType() == WorkoutIntDef.PROFILE_PROGRAM & isTreadmill) {

            final CopyOnWriteArrayList<DiagramBarBean> copySpeedList = new CopyOnWriteArrayList<>(w.speedDiagramBarList);
            copySpeedList.removeIf(diagramBarBean -> diagramBarBean.getBarK() != 0);
            w.speedDiagramBarList = copySpeedList;

            final CopyOnWriteArrayList<DiagramBarBean> copyInclineList = new CopyOnWriteArrayList<>(w.inclineDiagramBarList);
            copyInclineList.removeIf(diagramBarBean -> diagramBarBean.getBarK() != 0);
            w.inclineDiagramBarList = copyInclineList;


            w.hrList.subList(0, 3).clear();
            for (int i = 0; i < 3; i++) {
                w.hrList.remove(w.hrList.size() - 1);
            }
            int cs = w.currentSegment.get();
            if (cs == 23) w.currentSegment.set(cs - 4);
            if (cs == 24) w.currentSegment.set(cs - 5);
            if (cs == 25) w.currentSegment.set(cs - 6);
            if (cs < 23) w.currentSegment.set(cs - 3);

        }

        //跑到第幾階
        int runSegment = w.repeatCount > 1 ? w.speedDiagramBarList.size() : w.currentSegment.get() + 1;

        Date date = Calendar.getInstance().getTime();
        w.setUpdateTime(date);

        //存 Level / Incline 的值
        int totalLevel = 0;
        int totalIncline = 0;
        StringBuilder levelNum = new StringBuilder(); //全部段
        StringBuilder inclineNum = new StringBuilder();
        StringBuilder hrNum = new StringBuilder();

        StringBuilder levelNumReal = new StringBuilder(); //跑到第幾個段
        StringBuilder inclineNumReal = new StringBuilder();

        int size = w.speedDiagramBarList.size();

        //HIIT 和 FITNESS_TEST 男性，圖表根數少一
        if (w.selProgram == ProgramsEnum.HIIT ||
                (w.selProgram == ProgramsEnum.FITNESS_TEST && w.selGender.get() == MALE)) {
            size = size - 1;
        }

        //0#1#2#3#3#4#5#3#4#4#3#2#3#4#6#5#4#2#1#0

//        Log.d("PPXXXXXXXX", "saveDataToWorkOutBean: " + w.inclineDiagramBarList);
        for (int i = 0; i < size; i++) {

            if (isTreadmill) {
                //TREADMILL 有Incline
                int incline = w.inclineDiagramBarList.get(i).getBarNum();
                inclineNum.append(incline).append("#");

                //只加跑過的
                if (i <= (runSegment - 1)) {
                    totalIncline += incline;
                    inclineNumReal.append(incline).append("#");
                }
            }

            int level = w.speedDiagramBarList.get(i).getBarNum();
            int hr = w.hrList.get(i);

            levelNum.append(level).append("#");
            hrNum.append(hr).append("#");

            //只加跑過的
            if (i <= (runSegment - 1)) {
                totalLevel += level;
                levelNumReal.append(level).append("#");
            }
        }

//        Log.d("PPPPPPEEE", "saveDataToWorkOutBean: " + runSegment +","+workoutViewModel.repeatCount);
//        Log.d("PPPPPPEEE", "saveDataToWorkOutBean: " + levelNumReal.toString() +","+ totalLevel);


        levelNum = levelNum.deleteCharAt(levelNum.length() - 1);

        levelNumReal = levelNumReal.deleteCharAt(levelNumReal.length() - 1);

        hrNum = hrNum.deleteCharAt(hrNum.length() - 1);
        w.speedAndLevelChartNum = levelNum.toString();
        w.heartRateChartNum = hrNum.toString();


        //LEVEL 跑過的平均
        int al = totalLevel / runSegment;
        float avgLevel;

        //LEVEL 跑過的最大LEVEL
        int[] arrayLevel = Arrays.stream(levelNumReal.toString().split("#", -1))
                .mapToInt(Integer::parseInt)
                .toArray();

        if (isTreadmill) {

            inclineNum = inclineNum.deleteCharAt(inclineNum.length() - 1);
            w.inclineChartNum = inclineNum.toString();
            inclineNumReal = inclineNumReal.deleteCharAt(inclineNumReal.length() - 1);

            //INCLINE 跑過的平均
            int ai = totalIncline / runSegment;
            //  float avgIncline = getRoundDecimal(ai / 2f);
            float avgIncline = getRoundDecimal(getInclineValue(ai));
            w.avgIncline.set(avgIncline);

            //INCLINE 跑過的最大INCLINE
            int[] arrayIncline = Arrays.stream(inclineNumReal.toString().split("#", -1))
                    .mapToInt(Integer::parseInt)
                    .toArray();
            w.summaryMaxIncline.set(findMaxInt(arrayIncline));

            // avgLevel = getRoundDecimal(al / 10f);
            avgLevel = getRoundDecimal(getSpeedValue(al));

            Log.d("AAAGGGGGG", "============SPEED=======================");
            Log.d("AAAGGGGGG", "levelNumReal 實際跑的值: " + levelNumReal); //8#200#10#10  跑了四根
            Log.d("AAAGGGGGG", "跑了幾根: " + runSegment);
            Log.d("AAAGGGGGG", "已跑的根數加起來: " + totalLevel);
            Log.d("AAAGGGGGG", "平均: " + totalLevel + "/" + runSegment + "=" + al);
            Log.d("AAAGGGGGG", "轉換成速度 / 10 : " + avgLevel);

            Log.d("AAAGGGGGG", "============Incline=======================");
            Log.d("AAAGGGGGG", "inclineNumReal 實際跑的值: " + inclineNumReal); //8#200#10#10  跑了四根
            Log.d("AAAGGGGGG", "跑了幾根: " + runSegment);
            Log.d("AAAGGGGGG", "已跑的根數加起來: " + totalIncline);
            Log.d("AAAGGGGGG", "平均: " + totalIncline + "/" + runSegment + "=" + ai);
            Log.d("AAAGGGGGG", "轉換成INCLINE / 2: " + avgIncline);


        } else {
            //BIKE
            avgLevel = getRoundDecimal(al);

        }

        Log.d("KKKKAAAAAA", "3333333onChangeUnit: " + w.avgSpeed.get());
        w.avgSpeed.set(avgLevel);


        w.summaryMaxSpeedAndLevel.set(findMaxInt(arrayLevel));
        // workoutBean.setMaxWATT(maxWatt);

        int[] arrayHR = Arrays.stream(hrNum.toString().split("#", -1))
                .mapToInt(Integer::parseInt)
                .toArray();
        int maxHR = findMaxInt(arrayHR);


        w.summaryMaxHeartRate.set(maxHR);

        //少於一分鐘 不紀錄
//        if (workoutViewModel.elapsedTime.get() < 60) {
//            workoutViewModel.avgSpeed.set(0);
//            workoutViewModel.avgIncline.set(0);
//        }

    }


    private void initChart() {
        if (w.selProgram == ProgramsEnum.EGYM) {


            getBinding().baseScrollView.setVisibility(View.GONE);
            getBinding().baseScrollViewEgym.setVisibility(View.VISIBLE);


            if (MODE == ModeEnum.CT1000ENT) {


                //Incline 不用 Target
                List<StackedBarBean> inclineTartgetDataList = egymDataViewModel.selTrainer.getIntervals().stream()
                        .map(interval -> {
                            //    double value = Optional.ofNullable(interval.getIncline()).orElse(0.0);
                            double value = 0.0;
                            int secTime = Optional.ofNullable(interval.getDuration()).orElse(0) / 1000;
                            StackedBarBean bean = new StackedBarBean();
                            bean.setValue(value);
                            bean.setSecTime(secTime);
                            return bean;
                        }).collect(Collectors.toList());


                //已跑過的資料
                List<StackedBarBean> inclineOutcomeDataList = egymDataViewModel.woIntervalData.stream()
                        .map(interval -> {
                            double value = Optional.ofNullable(interval.getIncline()).orElse(0.0);
                            int secTime = Optional.ofNullable(interval.getDuration()).orElse(0) / 1000;
                            StackedBarBean bean = new StackedBarBean();
                            bean.setValue(value);
                            bean.setSecTime(secTime);
                            return bean;
                        }).collect(Collectors.toList());

                //如果Target duration 為0, 就把 實際跑過的時間放入
                IntStream.range(0, inclineTartgetDataList.size())
                        .filter(i -> inclineTartgetDataList.get(i).getSecTime() == 0 && i < inclineOutcomeDataList.size())
                        .forEach(i -> inclineTartgetDataList.get(i).setSecTime(inclineOutcomeDataList.get(i).getSecTime()));


                getBinding().stackedBarChartInclineView.setMaxDataValue((MAX_INC_MAX / 2));
                getBinding().stackedBarChartInclineView.setOutcomeData(inclineOutcomeDataList);
                getBinding().stackedBarChartInclineView.setTargetData(inclineTartgetDataList);


                getBinding().stackedBarChartInclineView.setBarColors(
                        ContextCompat.getColor(mContext, R.color.colorCd5bff_90),
                        ContextCompat.getColor(mContext, R.color.colorCd5bff_20),
                        ContextCompat.getColor(mContext, R.color.colorCd5bff_30)  // Target 颜色
                );


            } else {

                //已跑過的 Resistance LEVEL
                List<StackedBarBean> levelOutcomeDataList = egymDataViewModel.woIntervalData.stream()
                        .map(interval -> {
                            double value = Optional.ofNullable(interval.getResistance()).orElse(0);
                            int secTime = Optional.ofNullable(interval.getDuration()).orElse(0) / 1000;
                            StackedBarBean bean = new StackedBarBean();
                            bean.setValue(value);
                            bean.setSecTime(secTime);
                            return bean;
                        }).collect(Collectors.toList());


                //目標 Resistance LEVEL
                List<StackedBarBean> levelTartgetDataList = egymDataViewModel.selTrainer.getIntervals().stream()
                        .map(interval -> {
                            double value = Optional.ofNullable(interval.getResistance()).orElse(0);
                            int secTime = Optional.ofNullable(interval.getDuration()).orElse(0) / 1000;
                            StackedBarBean bean = new StackedBarBean();
                            bean.setValue(value);
                            bean.setSecTime(secTime);
                            return bean;
                        }).collect(Collectors.toList());

                //如果Target duration 為0, 就把 實際跑過的時間放入
                IntStream.range(0, levelTartgetDataList.size())
                        .filter(i -> levelTartgetDataList.get(i).getSecTime() == 0 && i < levelOutcomeDataList.size())
                        .forEach(i -> levelTartgetDataList.get(i).setSecTime(levelOutcomeDataList.get(i).getSecTime()));


                w.avgLevel.set((float) levelOutcomeDataList.stream()
                        .mapToDouble(StackedBarBean::getValue)
                        .average()
                        .orElse(0.0));
                //測試
                for (StackedBarBean x : levelOutcomeDataList) {
                    Log.d(EgymUtil.TAG, "LEVEL:" + x.getValue() + ", time:" + x.getSecTime());
                }


                //EGYM BIKE

                //四捨五入
                getBinding().layoutEgym.tvDistanceText.setText(getConvertHalfUp(w.currentDistance.get(), orgUnit, 3, 2));
                getBinding().layoutEgym.tvAscentText.setText(formatDecimal(w.avgLevel.get())); //LEVEL

                //四捨五入
                getBinding().layoutEgym.tvHrTextE.setText(getConvertHalfUp(w.avgSpeed.get(), orgUnit, 2, 2)); //SPEED
                getBinding().layoutEgym.tvSpeedTextE.setText(String.valueOf((int) w.avgHeartRate.get())); // HR
                getBinding().layoutEgym.tvInclineTextE.setText(formatDecimal((float) w.totalCalories.get())); //Calories
                getBinding().layoutEgym.tvTotalTimeText.setText(CommonUtils.formatSecToHMS(w.totalElapsedTimeShow.get())); //Time
                getBinding().layoutEgym.tvActiveCalorieTextE.setText(formatDecimal((float) w.avgPower.get()));//watts
                getBinding().layoutEgym.tvAvgPaceTextE.setText(formatDecimal((float) w.avgMet.get())); // METS

//                if (MODE == ModeEnum.CE1000ENT) {
//                    getBinding().layoutEgym.tvAvgMetsUnitE.setText(R.string.SPM);
//                    getBinding().layoutEgym.tvAvgMetsTextE.setText(formatDecimal((float) (w.avgRpm.get() * 2))); //Cadence RPM
//                } else {
//                    getBinding().layoutEgym.tvAvgMetsUnitE.setText(R.string.RPM);
//                    getBinding().layoutEgym.tvAvgMetsTextE.setText(formatDecimal((float) w.avgRpm.get())); //Cadence RPM
//                }

                getBinding().tvSpeedAndLevelNumE.setText(R.string.resistance_chart);


                getBinding().stackedBarChartSpeedView.setMaxDataValue(MAX_LEVEL_MAX);// 24 or 15
                getBinding().stackedBarChartSpeedView.setOutcomeData(levelOutcomeDataList);
                getBinding().stackedBarChartSpeedView.setTargetData(levelTartgetDataList);

                getBinding().stackedBarChartSpeedView.setBarColors(
                        ContextCompat.getColor(mContext, R.color.color1396ef_90),
                        ContextCompat.getColor(mContext, R.color.color1396ef_20),
                        ContextCompat.getColor(mContext, R.color.color1A1396ef)  // Target 颜色
                );


                //Cadence Chart
                getBinding().tvInclineTitle.setText(R.string.Candence_Chart);


                List<StackedBarBean> rpmOutcomeDataList;
                List<StackedBarBean> rpmTartgetDataList;

                if (MODE == ModeEnum.CE1000ENT) {

                    //SPM = RPM * 2

                    //已跑過的 SPM
                    rpmOutcomeDataList = egymDataViewModel.woIntervalData.stream()
                            .map(interval -> {
                                double value = Optional.ofNullable(interval.getStepsPerMinute()).orElse(0);
                                int secTime = Optional.ofNullable(interval.getDuration()).orElse(0) / 1000;
                                StackedBarBean bean = new StackedBarBean();
                                bean.setValue(value);
                                bean.setSecTime(secTime);
                                return bean;
                            }).collect(Collectors.toList());


                    //目標 SPM
                    rpmTartgetDataList = egymDataViewModel.selTrainer.getIntervals().stream()
                            .map(interval -> {
                                double value = Optional.ofNullable(interval.getStepsPerMinute()).orElse(0);
                                int secTime = Optional.ofNullable(interval.getDuration()).orElse(0) / 1000;
                                StackedBarBean bean = new StackedBarBean();
                                bean.setValue(value);
                                bean.setSecTime(secTime);
                                return bean;
                            }).collect(Collectors.toList());

                    w.avgRpm.set((float) rpmOutcomeDataList.stream()
                            .mapToDouble(StackedBarBean::getValue)
                            .average()
                            .orElse(0.0));


                    getBinding().stackedBarChartInclineView.setMaxDataValue((MAX_RPM * 2));

                    getBinding().layoutEgym.tvAvgMetsUnitE.setText(R.string.SPM);
                    getBinding().layoutEgym.tvAvgMetsTextE.setText(formatDecimal((float) (w.avgRpm.get()))); //Cadence RPM

                } else {

                    //已跑過的 RPM
                    rpmOutcomeDataList = egymDataViewModel.woIntervalData.stream()
                            .map(interval -> {
                                double value = Optional.ofNullable(interval.getRotations()).orElse(0);
                                int secTime = Optional.ofNullable(interval.getDuration()).orElse(0) / 1000;
                                StackedBarBean bean = new StackedBarBean();
                                bean.setValue(value);
                                bean.setSecTime(secTime);
                                return bean;
                            }).collect(Collectors.toList());


                    //目標 RPM
                    rpmTartgetDataList = egymDataViewModel.selTrainer.getIntervals().stream()
                            .map(interval -> {
                                double value = Optional.ofNullable(interval.getRotations()).orElse(0);
                                int secTime = Optional.ofNullable(interval.getDuration()).orElse(0) / 1000;
                                StackedBarBean bean = new StackedBarBean();
                                bean.setValue(value);
                                bean.setSecTime(secTime);
                                return bean;
                            }).collect(Collectors.toList());

                    w.avgRpm.set((float) rpmOutcomeDataList.stream()
                            .mapToDouble(StackedBarBean::getValue)
                            .average()
                            .orElse(0.0));

                    getBinding().stackedBarChartInclineView.setMaxDataValue((MAX_RPM));

                    getBinding().layoutEgym.tvAvgMetsUnitE.setText(R.string.RPM);
                    getBinding().layoutEgym.tvAvgMetsTextE.setText(formatDecimal((float) w.avgRpm.get())); //Cadence RPM
                }


                for (StackedBarBean x : rpmOutcomeDataList) {
                    Log.d(EgymUtil.TAG, "RPM: " + x.getValue() + "," + x.getSecTime());
                }


                //如果Target duration 為0, 就把 實際跑過的時間放入
                IntStream.range(0, levelTartgetDataList.size())
                        .filter(i -> rpmTartgetDataList.get(i).getSecTime() == 0 && i < rpmOutcomeDataList.size())
                        .forEach(i -> rpmTartgetDataList.get(i).setSecTime(rpmOutcomeDataList.get(i).getSecTime()));


                getBinding().stackedBarChartInclineView.setOutcomeData(rpmOutcomeDataList);
                getBinding().stackedBarChartInclineView.setTargetData(rpmTartgetDataList);
                getBinding().stackedBarChartInclineView.setBarColors(
                        ContextCompat.getColor(mContext, R.color.colorCd5bff_90),
                        ContextCompat.getColor(mContext, R.color.colorCd5bff_20),
                        ContextCompat.getColor(mContext, R.color.colorCd5bff_30)  // Target 颜色
                );


            }

            getBinding().stackedBarChartHrView.setMaxDataValue((THR_MAX));
            getBinding().stackedBarChartHrView.setOutcomeData(hrOutcomeDataList);
            getBinding().stackedBarChartHrView.setTargetData(hrTartgetDataList);
            getBinding().stackedBarChartHrView.setBarColors(
                    ContextCompat.getColor(mContext, R.color.colorFF715e_90),
                    ContextCompat.getColor(mContext, R.color.colorFF715e_20),
                    ContextCompat.getColor(mContext, R.color.colorFF715e_30)  // Target 颜色
            );

        } else {

            //非EGYM
            getBinding().baseScrollView.setVisibility(View.VISIBLE);
            getBinding().baseScrollViewEgym.setVisibility(View.GONE);

            if (isTreadmill) {
                GlideApp.with(getApp())
                        .load(new CommonUtils().getDiagramBitmap(getApp(), w.inclineChartNum, 0))
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(getBinding().ivDiagramIncline);
            }

            GlideApp.with(getApp())
                    .load(new CommonUtils().getDiagramBitmap(getApp(), w.speedAndLevelChartNum, 1))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(getBinding().ivDiagramSpeedAndLevel);


//            int[] levelData = ProfileChartView.parseDataString(w.speedAndLevelChartNum);
//            getBinding().ivDiagramSpeedAndLevel.setData(levelData);
//            getBinding().ivDiagramSpeedAndLevel.setMaxValue(40);


            if (w.avgHeartRate.get() == 0) {
                getBinding().ivNoHr.setVisibility(View.VISIBLE);
                getBinding().ivDiagramHeartRate.setVisibility(View.INVISIBLE);
            } else {
                getBinding().ivDiagramHeartRate.setVisibility(View.VISIBLE);
                getBinding().ivNoHr.setVisibility(View.INVISIBLE);

                GlideApp.with(getApp())
                        .load(new CommonUtils().getDiagramBitmap(context.getApplicationContext(), w.heartRateChartNum, 2))
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(getBinding().ivDiagramHeartRate);
            }
        }
    }


    private double[] distributeTarget(List<Double> speeds, List<Integer> positions) {
        //    Log.d("AAAACCVVVV", "onChangeUnit: " + speeds +","+ positions);
        int size = positions.get(positions.size() - 1) + 1; // 取最後一個索引作為陣列長度
        double[] result = new double[size];

        int startIndex = 0;
        for (int i = 0; i < speeds.size(); i++) {
            int endIndex = positions.get(i);
            double speedValue = Math.floor(speeds.get(i) * 10) / 10; // 取小數點 1 位，不四捨五入

            for (int j = startIndex; j <= endIndex; j++) {
                result[j] = speedValue;
            }

            startIndex = endIndex + 1; // 更新起始索引
        }

        return result;
    }

    @Override
    public void dismiss() {


        getDeviceGEM().nfcMessageDisableNfcRadio();
        isEgymNfc = false;
        isLogin = false;
        getBinding().cLoginEgym.setVisibility(View.GONE);
        ((MainActivity) mContext).getBinding().bgE.setVisibility(View.GONE);
        if (EgymUtil.getInstance().termsAndConditionsWindow != null) {
            EgymUtil.getInstance().termsAndConditionsWindow.dismiss();
            EgymUtil.getInstance().termsAndConditionsWindow = null;
        }
        Log.d("EgymUtil", "######關閉: ");


        cancelCountDown();


        //  LiveEventBus.get(DISMISS_WINDOW_EVENT, String.class).removeObserver(observer1);
        LiveEventBus.get(SET_ALPHA_BACKGROUND, Boolean.class).removeObserver(observer2);
        LiveEventBus.get(EVENT_SET_UNIT, Boolean.class).removeObserver(observer);
        LiveEventBus.get(EVENT_NFC_READ, String.class).removeObserver(observer3);

        WorkoutUtil.giCheck = -1;
        WorkoutUtil.gsCheck = -1;

        //  workoutViewModel.summaryMaxSpeedAndLevel.set(123);
        w = null;

        FitnessTestDbManager.getInstance(getApp()).clear();
        SpiritDbManager.getInstance(getApp()).clear();

        if (m.popupWindow != null) {
            m.popupWindow.dismiss();
            m.popupWindow = null;
        }

        if (m.calculation != null) {
            m.calculation.clear();
            m.calculation = null;
        }

        isSummary = false;
        isUploadDone = false;
        super.dismiss();
    }

    private String getTimeString(int time) {
        return String.format(Locale.getDefault(), "%02d:%02d", time / 60, time % 60);
    }

    @SuppressLint("DefaultLocale")
    public String getShowString(@GENERAL.DataOption int dataOpt, int value) {
        String formatValue = "";
        switch (dataOpt) {
            case GENERAL.DO_TIME:
                if (value >= 3600)
                    formatValue = String.format("%02d:%02d:%02d", (value / 60) / 60, (value / 60) % 60, value % 60);
                else
                    formatValue = String.format("%02d:%02d", value / 60, value % 60);
                break;

            case GENERAL.DO_GRADE:
            case GENERAL.DO_SPEED:
            case GENERAL.DO_VO2_1D1:
            case GENERAL.DO_METS:
            case GENERAL.DO_DISTANCE:
            case GENERAL.DO_CALORIES:
                formatValue = value / 10 + "." + Math.abs(value % 10);
                break;

            case GENERAL.DO_VO2_1D2:
                formatValue = String.format("%d.%02d", value / 100, value % 100);
                break;

            case GENERAL.DO_PACE:
                formatValue = String.format("%d'%02d\"", value / 60, value % 60);
                break;

            case GENERAL.DO_ELEVATION:
            case GENERAL.DO_HR:
            case GENERAL.DO_POWER:
            case GENERAL.DO_STEPS:
            case GENERAL.DO_STEP_LEN:
            case GENERAL.DO_LEFT_STEP_LEN:
            case GENERAL.DO_RIGHT_STEP_LEN:
            case GENERAL.DO_CADENCE:
            case GENERAL.DO_VO2_1D:
            case GENERAL.DO_WORKLOAD:
            case GENERAL.DO_REVOLUTION:
            case GENERAL.DO_SPEED_RPM:
            case GENERAL.DO_SPEED_SEC:
                formatValue = String.valueOf(value);
                break;
        }
        return formatValue;
    }


    public String convertMillisecondsToTimeFormat(long milliseconds) {
        // 將毫秒轉換為秒
        long totalSeconds = milliseconds / 1000;
        // 計算分鐘和秒
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        // 格式化成 0:00 格式
        return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds);
    }

    CountDownTimer countDownTimer;

    int countDownTime = 60 * 1000;

    private void startCountDown() {

        //AUTO LOGOUT 重新計時
        m.onUserInteraction();

        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }

        //AUTO_PAUSE開啟時，30秒登出
        if (deviceSettingViewModel.autoPause.getValue() == ON) {
            countDownTime = (int) getApp().getDeviceSettingBean().getPauseAfter() * 1000;
            //   countDownTime = 30 * 1000;
        }

        //    Log.d("RPM_CHECK", "countDownTime: " + countDownTime);
    //    countDownTime = 12000;
        countDownTimer = new CountDownTimer(countDownTime, 1000) {
            public void onTick(long millisUntilFinished) {
                if (deviceSettingViewModel.consoleSystem.get() == CONSOLE_SYSTEM_EGYM) {
                    if (getBinding() != null) {
                        getBinding().layoutEgymBottom.tvEgymLogoutTimeText.setText(String.format("%s %s", context.getString(R.string.auto_logout_in), convertMillisecondsToTimeFormat(millisUntilFinished)));
                    }
                }
            }

            public void onFinish() {
                try {
//                    dismiss();

                    //設定的 Autopause 偵測時間到 (例如：3 分鐘)，進 PAUSE 並倒數Autopause 設定的時間 (例如：3 分鐘)，時間到後進到Summary，30秒後，logout (若有第三方APP播放中，一併結束並登出)
                    if (deviceSettingViewModel.autoPause.getValue() == ON) {
                        if (deviceSettingViewModel.consoleSystem.get() == CONSOLE_SYSTEM_EGYM) {
                            getBinding().layoutEgymBottom.btnLogoutE.callOnClick();
                        } else {
                            getBinding().btnCloseAndLogout.callOnClick();
                        }

                    } else {
                        if (deviceSettingViewModel.consoleSystem.get() == CONSOLE_SYSTEM_EGYM) {
                            getBinding().layoutEgymBottom.btnHomeE.callOnClick();
                        } else {
                            getBinding().btnClose.callOnClick();
                        }
                    }
                } catch (Exception e) {
                    Log.d("E", "onFinish: " + e.getLocalizedMessage());
                }
            }
        };

        countDownTimer.start();

    }

    private void cancelCountDown() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }


    // private void saveDeviceData() {

//            SpiritDbManager.getInstance(getApp()).getDeviceData(
//                    new DatabaseCallback<DeviceEntity>() {
//                        @Override
//                        public void onDataLoadedBean(DeviceEntity deviceEntity) {
//                            super.onDataLoadedBean(deviceEntity);
//                         //   Log.d("DEVICE_SETTING", "目前workout總時間: " + deviceEntity.getDeviceRunningTime());
//                        //    Log.d("DEVICE_SETTING", "剛才的workout時間: " + workoutViewModel.totalElapsedTime.get());
//                            updateDeviceData(deviceEntity);
//
//                        }
//                    });
    //   }

    private void updateDeviceData() {

        try {
            DeviceSettingBean deviceEntity = getApp().getDeviceSettingBean();

            //時間
            deviceEntity.setODO_time(deviceEntity.getODO_time() + w.totalElapsedTime.get());
            //   deviceEntity.setODO_time(deviceEntity.getODO_time() + (60 * 60 * 3));

            //次數
            deviceEntity.setDeviceStep(deviceEntity.getDeviceStep() + 1);

            //距離 都存成英制
            double distance = saveDistance2Mi(w.currentDistance.get()) + deviceEntity.getODO_distance();
            //      double distance = saveDistance2Mi(500) + deviceEntity.getODO_distance();
            deviceEntity.setODO_distance(distance);


            if (deviceEntity.getUsageRestrictionsType() != NO_LIMIT) {
                //LIMIT
                //存公制
                if (deviceEntity.getUsageRestrictionsType() == DISTANCE_LIMIT) {
                    double distance2 = saveDistance2KM(w.currentDistance.get()) + deviceEntity.getCurrentUsageRestrictionsDistance();
                    deviceEntity.setCurrentUsageRestrictionsDistance(distance2);
//                    Log.d("LIMIT_SHOW", "updateDeviceData: " + w.currentDistance.get() +",  "+ saveDistance2KM(w.currentDistance.get()) +",  "+deviceEntity.getCurrentUsageRestrictionsDistance() +",  "+ distance2);
//                    Log.d("LIMIT_SHOW", "DISTANCE: " + deviceEntity.getCurrentUsageRestrictionsDistance());
                }

                if (deviceEntity.getUsageRestrictionsType() == TIME_LIMIT) { //秒
                    deviceEntity.setCurrentUsageRestrictionsTime(deviceEntity.getCurrentUsageRestrictionsTime() + w.totalElapsedTime.get());
                    //    Log.d("LIMIT_SHOW", "TIME: " + deviceEntity.getCurrentUsageRestrictionsTime());
                }
            }

            //  deviceEntity.setODO_distance(saveDistance2Mi(deviceEntity.getODO_distance()) + 1);

            deviceEntity.setE050hour(deviceEntity.getE050hour() + w.totalElapsedTime.get());
            deviceEntity.setE052hour(deviceEntity.getE052hour() + w.totalElapsedTime.get());
            deviceEntity.setE0BAhour(deviceEntity.getE0BAhour() + w.totalElapsedTime.get());

            getApp().setDeviceSettingBean(deviceEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }

        checkLimit();
    }


    boolean isLimitOut = false;

    private void checkLimit() {
        DeviceSettingBean deviceSettingBean = getApp().getDeviceSettingBean();
        //0:都不限制, 1:限制 distance,2:限制 time
        if (deviceSettingBean.getUsageRestrictionsType() == NO_LIMIT) return;

        if (deviceSettingBean.getUsageRestrictionsType() == DISTANCE_LIMIT) {
            if (deviceSettingBean.getCurrentUsageRestrictionsDistance() >= deviceSettingBean.getUsageRestrictionsDistanceLimit()) {
                isLimitOut = true;
            }
        } else {
            if (deviceSettingBean.getCurrentUsageRestrictionsTime() >= (deviceSettingBean.getUsageRestrictionsTimeLimit())) {
                isLimitOut = true;
            }
        }

        Log.d("LIMIT_SHOW", "checkLimit: " + (isLimitOut ? "要登出" : "不用登出"));

        Log.d("LIMIT_SHOW", "LIMIT種類: " + deviceSettingBean.getUsageRestrictionsType() + ",\n" +
                "(2 = 時間)設定的時間:" + deviceSettingBean.getUsageRestrictionsTimeLimit() + " 秒, 當前時間:" + deviceSettingBean.getCurrentUsageRestrictionsTime() + " 秒\n" +
                "(1 = 距離)設定的距離:" + deviceSettingBean.getUsageRestrictionsDistanceLimit() + " km, 當前距離:" + deviceSettingBean.getCurrentUsageRestrictionsDistance());

    }

    public void webApiUploadWorkoutFromMachineBean() {

        if (deviceSettingViewModel.consoleSystem.get() == CONSOLE_SYSTEM_EGYM || userProfileViewModel.userType.get() == USER_TYPE_GUEST) {
            isUploadDone = true;
            return;
        }

        m.showLoading(true);

        w.setWorkoutEndTime(Calendar.getInstance().getTimeInMillis());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        // 0 = Treadmill, 1 = Bike, 11 = Recumbent Bike, 12 = Upright Bike, 13 = Flywheel Bike, 2 = Elliptical, 3 = Stepper, 4= Rower
        @CloudData.CategoryType int categoryType = 0;
        switch (deviceSettingViewModel.typeCode.get()) {
            case DEVICE_TYPE_TREADMILL:
                categoryType = CloudData.TREADMILL;
                break;
            case DEVICE_TYPE_ELLIPTICAL:
                categoryType = CloudData.ELLIPTICAL;
                break;
            case DEVICE_TYPE_UPRIGHT_BIKE:
                categoryType = CloudData.UPRIGHT_BIKE;
                break;
            case DEVICE_TYPE_RECUMBENT_BIKE:
                categoryType = CloudData.RECUMBENT_BIKE;
                break;
        }

        UploadWorkoutParam uploadWorkoutParam = new UploadWorkoutParam();

//        w.currentDistance.set(123);
        //    w.avgSpeed.set(1);

        UploadWorkoutParam.FormDTO formDTO = new UploadWorkoutParam.FormDTO();
        formDTO.setClientWorkoutUuid(UUID.randomUUID().toString());
        formDTO.setMachineCategoryType(categoryType);//必填
        formDTO.setMachineName("F85-9C03");//必填
        formDTO.setMachineUuid("");
        formDTO.setMachineMac(getApp().getDeviceSettingBean().getMachine_mac());
        formDTO.setMachineModelName("SpiritClub");
        formDTO.setStartTime(sdf.format(w.getWorkoutStartTime()));//必填
        formDTO.setEndTime(sdf.format(w.getWorkoutEndTime()));//必填
        formDTO.setTimestamp(w.getWorkoutEndTime());//必填
        formDTO.setOrgId(userProfileViewModel.getOrgId());
        formDTO.setStartTimeMillis(w.getWorkoutStartTime());//必填
        formDTO.setEndTimeMillis(w.getWorkoutEndTime());//必填
        formDTO.setProgramName(w.selProgram.name());
        formDTO.setProgramId(w.selProgram.getWebApiProgramId());
        formDTO.setAvgIncline((double) w.avgIncline.get());
        formDTO.setAvgLevel((double) w.avgLevel.get());
        formDTO.setAvgHeartRate((double) w.avgHeartRate.get());
        formDTO.setAvgMet(w.avgMet.get());
        formDTO.setAvgSpeed(UNIT_E == METRIC ? w.avgSpeed.get() : mph2kph((float) w.avgSpeed.get()));
        formDTO.setAvgWatt(w.avgPower.get());
        formDTO.setAvgCadence(0.0); //平均節奏
        formDTO.setTotalTime(w.totalElapsedTime.get());//必填
        formDTO.setTotalDistance(UNIT_E == METRIC ? w.currentDistance.get() : mph2kph((float) w.currentDistance.get()));//必填
        formDTO.setTotalCalories((int) w.totalCalories.get());//必填
        //  formDTO.setTotalSteps(null);
        formDTO.setTotalElevation((int) w.currentElevationGain.get());
        formDTO.setUserUuid(userProfileViewModel.getUserUUID());
        formDTO.setTimeZone(TimeZone.getDefault().getID());

        formDTO.setRawDataList(m.rawDataListDTOList);
        uploadWorkoutParam.setForm(formDTO);

        String paramJson = new Gson().toJson(uploadWorkoutParam);

//        int maxLogSize = 1000;
//        for (int i = 0; i <= paramJson.length() / maxLogSize; i++) {
//            int start = i * maxLogSize;
//            int end = (i + 1) * maxLogSize;
//            end = Math.min(end, paramJson.length());
//            Log.d("####WEB_API,###參數", paramJson.substring(start, end));
//        }
        Log.d("####WEB_API", "參數: " + paramJson);
        //  Log.d("####WEB_API,###參數", "上傳運動資料 --------------------------------------");

        //   paramJson = CommonUtils.loadAssetFile(getApp(),"ww.json","[]");


        m.rawDataListDTOList.clear();

        //先把運動資料存到資料庫
        tempWebApiUploadData(paramJson);

    }

    /**
     * workoutRawData 不足三秒 當成三秒，
     */
    private void addLastRawData() {

        //   if (userProfileViewModel.userType.get() == USER_TYPE_GUEST) return;

        if (deviceSettingViewModel.consoleSystem.get() == CONSOLE_SYSTEM_EGYM ||
                userProfileViewModel.userType.get() == USER_TYPE_GUEST) return;

        if (w.selWorkoutTime.get() == UNLIMITED) {
            //上數
            int i = 0;
            int left_time = w.totalElapsedTime.get();
            for (UploadWorkoutParam.FormDTO.RawDataListDTO data : m.rawDataListDTOList) {
                data.setTotalTimeLeft(left_time -= 3);
                m.rawDataListDTOList.set(i, data);
                i++;
            }
        }

        //已有重複的就不加
        for (UploadWorkoutParam.FormDTO.RawDataListDTO rawDataListDTO : m.rawDataListDTOList) {
            if (rawDataListDTO.getTotalWorkoutTime() == w.totalElapsedTime.get()) {
                return;
            }
        }

        UploadWorkoutParam.FormDTO.RawDataListDTO rawDataListDTO = new UploadWorkoutParam.FormDTO.RawDataListDTO();


//        rawDataListDTO.setTotalWorkoutTime(w.totalElapsedTime.get());
        rawDataListDTO.setTotalWorkoutTime(w.totalElapsedTimeShow.get());
//        if (w.selWorkoutTime.get() == UNLIMITED) {
//            rawDataListDTO.setTotalTimeLeft(1);
//        } else {
//        rawDataListDTO.setTotalTimeLeft(w.remainingTime.get());
        rawDataListDTO.setTotalTimeLeft(w.remainingTimeShow.get());
        //   rawDataListDTO.setTotalTimeLeft(0);
        //     }

        rawDataListDTO.setNowHr(w.currentHeartRate.get());
        rawDataListDTO.setTotalDistance(UNIT_E == METRIC ? w.currentDistance.get() : mph2kph((float) w.currentDistance.get()));
        rawDataListDTO.setTotalCalorie((int) w.totalCalories.get());
        rawDataListDTO.setNowSpeed(UNIT_E == METRIC ? w.currentSpeed.get() : mph2kph(w.currentSpeed.get()));
        rawDataListDTO.setNowIncline(w.currentInclineValue.get());
        rawDataListDTO.setNowLevel(w.currentLevel.get());
        rawDataListDTO.setNowWatt(w.currentPower.get());
        rawDataListDTO.setAvgSpmRower(0);
        rawDataListDTO.setTotalStrokes(0);
        rawDataListDTO.setAvgRpm(w.avgRpm.get());
        rawDataListDTO.setTotalFloor(1);
        rawDataListDTO.setTotalElevation(w.currentElevationGain.get());
        rawDataListDTO.setTotalSteps(0);
        rawDataListDTO.setCurSpmStepper(0);
        rawDataListDTO.setAvgSpmStepper(0);
        m.rawDataListDTOList.add(rawDataListDTO);

        //  Log.d("參數3", "addLastRawData: " + new Gson().toJson(m.rawDataListDTOList));
    }


    //先把要上傳的資料存到資料庫
    private void tempWebApiUploadData(String jsonData) {
        UploadWorkoutDataEntity uploadWorkoutDataEntity = new UploadWorkoutDataEntity();
        uploadWorkoutDataEntity.setDataJson(jsonData);
        uploadWorkoutDataEntity.setUploaded(false);
        SpiritDbManager.getInstance(getApp()).insertUploadWorkoutData(uploadWorkoutDataEntity,
                new DatabaseCallback<UploadWorkoutDataEntity>() {
                    @Override
                    public void onAdded(long rowId) {
                        super.onAdded(rowId);
                        Log.d("####WEB_API", "######存到資料庫成功: " + rowId);
                        if (isNetworkAvailable(getApp())) {
                            Log.d("####WEB_API", "有網路 - 上傳");
                            apiUploadWorkoutData(jsonData, rowId);
                        } else {
                            Log.d("####WEB_API", "無網路 - 結束");
                            isUploadDone = true;
                            m.showLoading(false);
                        }
                    }

                    @Override
                    public void onError(String err) {
                        super.onError(err);
                        Log.d("####WEB_API", "######存到資料庫失敗: " + err);
                        if (isNetworkAvailable(getApp())) {
                            Log.d("####WEB_API", "有網路 - 上傳");
                            apiUploadWorkoutData(jsonData, -1);
                        } else {
                            Log.d("####WEB_API", "無網路 - 結束");
                            isUploadDone = true;
                            m.showLoading(false);
                        }
                    }
                });
    }


    private void apiUploadWorkoutData(String paramJson, long rowId) {
        BaseApi.request(BaseApi.createApi(IServiceApi.class).apiUploadWorkoutFromMachine(paramJson),
                new BaseApi.IResponseListener<UploadWorkoutFromMachineBean>() {
                    @Override
                    public void onSuccess(UploadWorkoutFromMachineBean data) {
                        isUploadDone = true;
                        try {
                            Log.d("####WEB_API", "上傳運動資料: " + data.toString());
                            if (data.getSuccess()) {
                                //上傳成功，刪掉資料
                                Log.d("####WEB_API", "上傳運動資料成功: ");

                                if (rowId != -1) {
                                    SpiritDbManager.getInstance(getApp()).deleteUploadWorkoutData(rowId,
                                            new DatabaseCallback<UploadWorkoutDataEntity>() {
                                                @Override
                                                public void onDeleted() {
                                                    super.onDeleted();
                                                    Log.d("####WEB_API", "刪除運動資料成功");
                                                }

                                                @Override
                                                public void onError(String err) {
                                                    super.onError(err);
                                                    Log.d("####WEB_API", "刪除運動資料失敗");
                                                }
                                            });
                                }

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.d("####WEB_API", "上傳運動資料 Exception: " + e.getLocalizedMessage());
                        }
                        m.showLoading(false);
                    }

                    @Override
                    public void onFail() {
                        isUploadDone = true;
                        Log.d("####WEB_API", "上傳運動資料 onFail: ");
                        //    Toasty.error(getApp(), "Upload WorkoutData Failed", Toasty.LENGTH_SHORT).show();
                        m.showLoading(false);
                    }
                });
    }

}