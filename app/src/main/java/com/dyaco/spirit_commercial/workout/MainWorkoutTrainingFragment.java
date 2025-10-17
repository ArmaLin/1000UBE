package com.dyaco.spirit_commercial.workout;

import static androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE;
import static com.corestar.libs.device.DeviceGEM.EQUIPMENT_CONTROL_OPERATION.SET_TARGET_INCLINATION;
import static com.corestar.libs.device.DeviceGEM.EQUIPMENT_CONTROL_OPERATION.SET_TARGET_SPEED;
import static com.corestar.libs.device.DeviceGEM.EQUIPMENT_CONTROL_RESPONSE.SUCCESS;
import static com.dyaco.spirit_commercial.App.MAX_HR;
import static com.dyaco.spirit_commercial.App.MODE;
import static com.dyaco.spirit_commercial.App.UNIT_E;
import static com.dyaco.spirit_commercial.App.getApp;
import static com.dyaco.spirit_commercial.MainActivity.isEmulator;
import static com.dyaco.spirit_commercial.MainActivity.isHomeScreen;
import static com.dyaco.spirit_commercial.MainActivity.isSummary;
import static com.dyaco.spirit_commercial.MainActivity.isTreadmill;
import static com.dyaco.spirit_commercial.MainActivity.isUs;
import static com.dyaco.spirit_commercial.MainActivity.userProfileViewModel;
import static com.dyaco.spirit_commercial.egym.EgymUtil.SYMBOL_DURATION;
import static com.dyaco.spirit_commercial.support.CommonUtils.formatSecToM;
import static com.dyaco.spirit_commercial.support.CommonUtils.isNumeric;
import static com.dyaco.spirit_commercial.support.FormulaUtil.E_BLANK;
import static com.dyaco.spirit_commercial.support.FormulaUtil.mi2km;
import static com.dyaco.spirit_commercial.support.FormulaUtil.mph2kph;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.getConNum;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.getInclineLevel;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.getInclineValue;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.getSpecifyValue;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.getSpeedLevel;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.getSpeedValue;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.CONSOLE_SYSTEM_EGYM;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.CONSOLE_SYSTEM_SPIRIT;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.DEVICE_TYPE_ELLIPTICAL;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.DEVICE_TYPE_RECUMBENT_BIKE;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.DEVICE_TYPE_TREADMILL;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.DEVICE_TYPE_UPRIGHT_BIKE;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.FTMS_TYPE_CROSS_TRAINER;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.FTMS_TYPE_INDOOR_BIKE;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.FTMS_TYPE_TREADMILL;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.IMPERIAL;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.METRIC;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.OFF;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.PROTOCOL_CSAFE;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.AUTO_PAUSE_EVENT;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.CLOSE_SETTINGS;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.EVT_ERR;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.FITNESS_TEST_STOP_WORKOUT;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.FTMS_SET_TARGET_INCLINATION;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.FTMS_SET_TARGET_POWER;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.FTMS_SET_TARGET_RESISTANCE_LEVEL;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.FTMS_SET_TARGET_SPEED;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.FTMS_STOP_OR_PAUSE;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.GARMIN_ON_DEVICE_DISCONNECTED;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.KEY_UNKNOWN;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.MEDIA_MENU_CHANGE;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.MEDIA_PAUSE_WORKOUT;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.NEW_VALUE_UPDATE;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.PAUSE_WORKOUT;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.RESUME_WORKOUT;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.SELECT_WORKOUT_PAGE;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.CLICK_MINUS;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.CLICK_MINUS_2;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.CLICK_PLUS;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.CLICK_PLUS_2;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DISTANCE_LIMIT;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_B5_RUNNING_AD_CHANGE_RSP;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_RUNNING_STANDBY;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.INVALID_TEST_HR_OUT_OF_RANGE;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.INVALID_TEST_RPM_OUT_OF_RANGE;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.LONG_CLICK_MINUS;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.LONG_CLICK_PLUS;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.NO_LIMIT;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.SELECT_HR_EXCEEDS_AGE;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.STATS_INCLINE;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.STATS_LEVEL;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.STATS_POWER;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.STATS_SPEED;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.TIME_LIMIT;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.UE_INIT;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.UE_INVERTER;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.UE_MACHINE_TYPE;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.UE_MCU;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.UE_MCU_EMS;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.UE_NONE;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.UE_RPM;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.UE_RS485;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.UE_SAFETY_KEY;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.UE_UART;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.UE_UART_LWR;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.UPDATE_ALL_BAR;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.UPDATE_SPEED_BAR;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.USER_TYPE_GUEST;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.WARRING_HR_REACHED_TARGET;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.WARRING_HR_TOO_HIGH;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.WARRING_NO_HR;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.WARRING_NO_RPM;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.WORKOUT_PAGE_CHARTS;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.WORKOUT_PAGE_GARMIN;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.WORKOUT_PAGE_STATS;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.WORKOUT_PAGE_TRACK;
import static com.dyaco.spirit_commercial.support.intdef.MainDashboardBottomButtonIntDef.WORK_OUT_STOP;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.HEIGHT_IU_DEF;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.HEIGHT_MU_DEF;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.MAX_INC_MAX;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.MIN_SPD_IU;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.MIN_SPD_MU;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.NO_HR_HR;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.POWER_MAX;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.POWER_MIN;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.THR_CONSTANT;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.WEIGHT_IU_DEF;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.WEIGHT_MU_DEF;
import static com.dyaco.spirit_commercial.support.intdef.WorkoutIntDef.*;
import static com.dyaco.spirit_commercial.support.utils.MyAnimationUtils.crossFade;
import static com.dyaco.spirit_commercial.workout.WorkoutPauseFragment.isResuming;
import static com.dyaco.spirit_commercial.workout.programs.ProgramsEnum.CTT_PERFORMANCE;
import static com.dyaco.spirit_commercial.workout.programs.ProgramsEnum.CTT_PREDICTION;
import static com.dyaco.spirit_commercial.workout.programs.ProgramsEnum.EGYM;
import static com.dyaco.spirit_commercial.workout.programs.ProgramsEnum.FITNESS_TEST;
import static com.dyaco.spirit_commercial.workout.programs.ProgramsEnum.GERKIN;
import static com.dyaco.spirit_commercial.workout.programs.ProgramsEnum.HEART_RATE;
import static com.dyaco.spirit_commercial.workout.programs.ProgramsEnum.HIIT;
import static com.dyaco.spirit_commercial.workout.programs.ProgramsEnum.MANUAL;
import static com.dyaco.spirit_commercial.workout.programs.ProgramsEnum.WATTS;
import static com.dyaco.spirit_commercial.workout.programs.ProgramsEnum.WFI;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.transition.TransitionManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.corestar.calculation_libs.Calculation;
import com.corestar.libs.device.DeviceCsafe;
import com.corestar.libs.device.DeviceDyacoMedical;
import com.corestar.libs.device.DeviceGEM;
import com.dyaco.spirit_commercial.App;
import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.UartConsoleManagerPF;
import com.dyaco.spirit_commercial.alert_message.CautionSpeedWindow;
import com.dyaco.spirit_commercial.alert_message.ModalHrMaxWindow;
import com.dyaco.spirit_commercial.alert_message.ModalHrUpdateWindow;
import com.dyaco.spirit_commercial.alert_message.ModalHrUpdateWindow2;
import com.dyaco.spirit_commercial.alert_message.WarringCountDownWindow;
import com.dyaco.spirit_commercial.alert_message.WarringNoButtonWindow;
import com.dyaco.spirit_commercial.alert_message.WarringNoHrCountDownWindow;
import com.dyaco.spirit_commercial.dashboard_media.NewUpdateData;
import com.dyaco.spirit_commercial.databinding.FragmentMainWorkoutTrainingBinding;
import com.dyaco.spirit_commercial.egym.EgymUtil;
import com.dyaco.spirit_commercial.model.webapi.bean.CreateWorkoutParam;
import com.dyaco.spirit_commercial.support.CommonUtils;
import com.dyaco.spirit_commercial.support.MsgEvent;
import com.dyaco.spirit_commercial.support.RpmUtil;
import com.dyaco.spirit_commercial.support.RxTimer;
import com.dyaco.spirit_commercial.support.base_component.BaseBindingFragment;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.intdef.AppStatusIntDef;
import com.dyaco.spirit_commercial.support.intdef.DeviceIntDef;
import com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS;
import com.dyaco.spirit_commercial.support.intdef.WorkoutIntDef;
import com.dyaco.spirit_commercial.support.utils.CheckDoubleClick;
import com.dyaco.spirit_commercial.viewmodel.AppStatusViewModel;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingBean;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingViewModel;
import com.dyaco.spirit_commercial.viewmodel.EgymDataViewModel;
import com.dyaco.spirit_commercial.viewmodel.ErrorInfo;
import com.dyaco.spirit_commercial.viewmodel.WorkoutViewModel;
import com.dyaco.spirit_commercial.workout.programs.AirForce;
import com.dyaco.spirit_commercial.workout.programs.Army;
import com.dyaco.spirit_commercial.workout.programs.CoastGuard;
import com.dyaco.spirit_commercial.workout.programs.CommonPrograms;
import com.dyaco.spirit_commercial.workout.programs.CttPerformance;
import com.dyaco.spirit_commercial.workout.programs.CttPrediction;
import com.dyaco.spirit_commercial.workout.programs.FitnessTest;
import com.dyaco.spirit_commercial.workout.programs.Gerkin;
import com.dyaco.spirit_commercial.workout.programs.HeartRate;
import com.dyaco.spirit_commercial.workout.programs.HeartRateTreadmill;
import com.dyaco.spirit_commercial.workout.programs.Hiit;
import com.dyaco.spirit_commercial.workout.programs.IPrograms;
import com.dyaco.spirit_commercial.workout.programs.MarineCorps;
import com.dyaco.spirit_commercial.workout.programs.Navy;
import com.dyaco.spirit_commercial.workout.programs.Peb;
import com.dyaco.spirit_commercial.workout.programs.ProgramsEnum;
import com.dyaco.spirit_commercial.workout.programs.Run10K;
import com.dyaco.spirit_commercial.workout.programs.Run5K;
import com.dyaco.spirit_commercial.workout.programs.Watt;
import com.dyaco.spirit_commercial.workout.programs.Wfi;
import com.garmin.health.Device;
import com.google.android.material.transition.MaterialFadeThrough;
import com.jeremyliao.liveeventbus.LiveEventBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * if (isTreadmill) {
 * //還原暫停前的狀態
 * u.setDevMainMode(DeviceSpiritC.MAIN_MODE.RUNNING);
 * } else {
 * u.setDevConsoleMode(DeviceSpiritC.CONSOLE_MODE.NORMAL);
 * }
 * <p>
 * <p>
 * Summary
 * //結束workout
 * new RxTimer().timer(1000, number -> m.uartConsole.setDevWorkoutFinish());
 */
public class MainWorkoutTrainingFragment extends BaseBindingFragment<FragmentMainWorkoutTrainingBinding> {
    private CautionSpeedWindow cautionSpeedWindow;
    private DeviceCsafe deviceCsafe;
    private int ftmsType;

    int resistance = RT_LEVEL;
    public final int PANEL_ALL_SHOW = 0;
    public final int PANEL_INCLINE_SHOW = 1;
    public final int PANEL_SPEED_SHOW = 2;
    public final int PANEL_ALL_DISAPPEAR = 3;

    /**
     * grade return = true  (揚升要歸零)
     * 即表示 GS mode = false
     * <p>
     * <p>
     * grade return) = false (揚升不歸零)
     * 即表示GS mode = true
     */
    public boolean isGsMode;
    private AppStatusViewModel appStatusViewModel;
    private WorkoutViewModel w;
    private DeviceSettingViewModel deviceSettingViewModel;
    private EgymDataViewModel egymDataViewModel;
    public UartConsoleManagerPF u;

    private RxTimer rxWorkOutTimer;
    public boolean isWorkoutTimerRunning = false; //workout是否可計時
    long timeCount; //ms
    public IPrograms iPrograms;
    public Calculation calc;

    private Fragment m_currentFragment;
    private WorkoutChartsFragment workoutChartsFragment;
    private WorkoutStatsFragment workoutStatsFragment;
    private WorkoutTrackFragment workoutTrackFragment;
    private WorkoutGarminFragment workoutGarminFragment;
    private RxTimer startCheckTimer;

    // private int useTimeLimit = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.isOnStopBackToMainTraining = false;

        isHomeScreen = false;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        OPT_SETTINGS.MAX_LEVEL_MAX = MODE.isUbeType() ? 30: 50;

        u = parent.uartConsole;
        appStatusViewModel = new ViewModelProvider(requireActivity()).get(AppStatusViewModel.class);
        w = new ViewModelProvider(requireActivity()).get(WorkoutViewModel.class);
        deviceSettingViewModel = new ViewModelProvider(requireActivity()).get(DeviceSettingViewModel.class);
        egymDataViewModel = new ViewModelProvider(requireActivity()).get(EgymDataViewModel.class);


        deviceCsafe = ((MainActivity) requireActivity()).deviceCsafe;

        ((MainActivity) requireActivity()).btNotifyWindow(false);

        getBinding().setWorkoutData(w);
        getBinding().setDeviceSetting(deviceSettingViewModel);

        getBinding().setIsTreadmill(isTreadmill);
        getBinding().setIsUs(isUs);


        if (!isTreadmill) {
            RpmUtil.reset();
        }

        //Gerkin, WFI, CttPerformance, CttPrediction 中間頁籤大小跟國際版一樣
        isGGG = false;
        if (isUs) {
            if (w.selProgram == GERKIN || w.selProgram == WFI || w.selProgram == CTT_PERFORMANCE || w.selProgram == CTT_PREDICTION) {
                isGGG = true;
            }
        }
        getBinding().setIsGGG(isGGG);

        ((MainActivity) requireActivity()).rawDataListDTOList.clear();


        parent.getUartConsoleManager().setDevStartWorkout();


        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) getBinding().workoutFragmentContainerView.getLayoutParams();
        //  params.setMargins(isUs ? 312 : 261, isUs ? 160 : 24, isUs ? 312 : 261, isUs ? 160 : 24);
        params.setMargins(isUs && !isGGG ? 312 : 261, isUs && !isGGG ? 160 : 24, isUs && !isGGG ? 312 : 261, isUs && !isGGG ? 160 : 24);
        getBinding().workoutFragmentContainerView.setLayoutParams(params);

        //CHARTS bar 的位置
//        OPT_SETTINGS.viewX = isUs ? 312 : 261;
//        OPT_SETTINGS.viewY = isUs ? 280 : 254;

        OPT_SETTINGS.viewX = isUs && !isGGG ? 312 : 261;
        OPT_SETTINGS.viewY = isUs && !isGGG ? 280 : 254;


        Glide.with(getApp())
                .load(drawableRs[n])
                .transition(DrawableTransitionOptions.withCrossFade(1500))
                .skipMemoryCache(true)
                .centerCrop()
                .into(getBinding().viewBase);

        if (MODE.getTypeCode() == DEVICE_TYPE_TREADMILL) {
            ftmsType = DEVICE_TYPE_TREADMILL;
        } else if (MODE.getTypeCode() == DEVICE_TYPE_ELLIPTICAL) {
            ftmsType = FTMS_TYPE_CROSS_TRAINER;
        } else {
            ftmsType = FTMS_TYPE_INDOOR_BIKE;
        }

        if (w.selProgram == HIIT) {
            //讓btnWorkoutStop一開始就先消失
            w.isWarmUpIng.set(true);
        }

        getLimit();


        if (isTreadmill) {
            // u.setDevMainMode(DeviceSpiritC.MAIN_MODE.RUNNING);
            init();
        } else {

            //  getDeviceSpiritC().setEchoMode(DeviceSpiritC.ECHO_MODE.SECOND);

            //  Looper.myQueue().addIdleHandler(() -> {

            //       return false;
            //    });
            //  parent.getBinding().vBackground.setVisibility(View.GONE);
            //    Looper.myQueue().addIdleHandler(() -> {
            init();
            //       return false;
            //    });
            new RxTimer().timer(500, number -> parent.showGoBackground(false));
        }


//        for (int id : getBinding().groupTreadmillViewIncline.getReferencedIds()) {
//            view.findViewById(id).setClickable(false);
//        }

//        w.disabledInclineUpdate.set(true);
//        w.disabledSpeedUpdate.set(true);
    }


    private void init() {

        // TODO EGYM
//        if (w.selProgram.getProgramType() == EGYM_PROGRAM) {
        if (deviceSettingViewModel.consoleSystem.get() == CONSOLE_SYSTEM_EGYM) {
            egymDataViewModel.createWorkoutParam = new CreateWorkoutParam();
            //起始時間
            egymDataViewModel.createWorkoutParam.setStartTimestamp(System.currentTimeMillis());
        }


        resistance = w.selProgram == WATTS ? RT_POWER : RT_LEVEL;
        //   isGsMode = !deviceSettingViewModel.gsMode.get();
        isGsMode = deviceSettingViewModel.gsMode.get();

        int age = w.selYO.get() == 0 ? userProfileViewModel.getUserAge() : w.selYO.get();
        w.selYO.set(age);
        w.selGender.set(w.selGender.get() == 0 ? userProfileViewModel.getUserGender() : w.selGender.get());

        int weight;
        int height;
        if (UNIT_E == IMPERIAL) {
            weight = w.selWeightIU.get() == 0 ? (int) userProfileViewModel.getWeight_imperial() : w.selWeightIU.get();
            w.selWeightIU.set(weight);
        } else {
            weight = w.selWeightMU.get() == 0 ? (int) userProfileViewModel.getWeight_metric() : w.selWeightMU.get();
            w.selWeightMU.set(weight);
        }
        //  int height = (int) (UNIT_E == IMPERIAL ? userProfileViewModel.getHeight_imperial() : userProfileViewModel.getHeight_metric());

        if (UNIT_E == IMPERIAL) {
            height = w.selHeightIU.get() == 0 ? (int) userProfileViewModel.getHeight_imperial() : w.selHeightIU.get();
            w.selHeightIU.set(height);
        } else {
            height = w.selHeightMU.get() == 0 ? (int) userProfileViewModel.getHeight_metric() : w.selHeightMU.get();
            w.selHeightMU.set(height);
        }

        if (weight == 0) weight = (UNIT_E == IMPERIAL) ? WEIGHT_IU_DEF : WEIGHT_MU_DEF;
        if (height == 0) height = (UNIT_E == IMPERIAL) ? HEIGHT_IU_DEF : HEIGHT_MU_DEF;

        // weight or height 為0 Calculation會crash
        parent.calculation = new Calculation(getCalculationMachineType(deviceSettingViewModel.typeCode.get()),
                (UNIT_E == IMPERIAL) ? Calculation.UNIT.IMPERIAL : Calculation.UNIT.METRIC,
                weight, height, age);

//        Log.d("VVVBBBNNN", "XXXXXXXinit: " +age);
//        Log.d("VVVBBBNNN", "XXXXXXXinit: " +weight);
//        Log.d("VVVBBBNNN", "XXXXXXXinit: " +height);
//        Log.d("VVVBBBNNN", "XXXXXXXinit: " +deviceSettingViewModel.typeCode.get());
//        Log.d("VVVBBBNNN", "XXXXXXXinit: " + parent.calculation);
//        Log.d("VVVBBBNNN", "XXXXXXXinit: " + MODE.getWattTable());
        parent.calculation.setWattTable(MODE.getWattTable());


        Log.d("OOODDDODODO", "init CALC-  AGE: " + age + ", weight:" + weight + ", height:" + height + "," + userProfileViewModel.getHeight_metric() + "," + userProfileViewModel.getHeight_imperial());
        //   Log.d("OOODDDODODO", "init CALC-  AGE: " + w.selYO.get() +", weight:"+ w.selWeightIU.get() +", height:"+ w.selWeightMU.get() +","+w.selGender.get());
        calc = parent.calculation;

        setFtmsEquipmentType(true);

        initProgramData();

        initFragment();

        initEvent();

        //HEART_RATE 沒有選年齡，所以在這裡取得
        MAX_HR = THR_CONSTANT - userProfileViewModel.getUserAge();

        //   initProgramData();

        initWorkoutData();


        //執行Workout時，若Media執行中，要改變MediaMenu狀態
        if (appStatusViewModel.isMediaPlaying.get())
            LiveEventBus.get(MEDIA_MENU_CHANGE).post("");

        if (isTreadmill) {
            initWorkoutControllerTreadmill();

            //檢查狀態
            startCheckTimer = new RxTimer();
            startCheckTimer.interval3(500, number -> {

                if (u == null) {
                    u = parent.uartConsole;
                }

                if (u.getDevStep() == DS_RUNNING_STANDBY || isEmulator) {
                    startCheckTimer.cancel();
                    Log.d("UART_CONSOLE", "開始initWarmUp:");
                    initWarmUp();
//                    //執行Workout時，若Media執行中，要改變MediaMenu狀態
//                    if (appStatusViewModel.isMediaPlaying.get())
//                        LiveEventBus.get(MEDIA_MENU_CHANGE).post("");
                    initExControl();
                } else {
                    Log.d("UART_CONSOLE", "目前狀態 不是 DS_RUNNING_STANDBY - 重試:");
                }
            });
        } else {
            initWorkoutControllerBike();
            new RxTimer().timer(300, number -> initWarmUp());
            initExControl();
        }


        initUs();
    }


    public static boolean isGGG = false;//Gerkin, WFI, CttPerformance, CttPrediction 中間頁籤大小跟國際版一樣

    private void initUs() {

        if (!isUs) return;

        //app:layout_constraintLeft_toLeftOf="@+id/parent"
        //set.connect(YOURVIEW.getId(),ConstraintSet.LEFT,ConstraintSet.PARENT_ID,ConstraintSet.LEFT,0);

//        ConstraintLayout constraintLayout = getBinding().container;
//        ConstraintSet constraintSet = new ConstraintSet();
//        constraintSet.clone(constraintLayout);
//        constraintSet.connect(R.id.workoutFragmentContainerView, ConstraintSet.LEFT, constraintLayout.getId(), ConstraintSet.LEFT, 0);
//        constraintSet.connect(R.id.workoutFragmentContainerView, ConstraintSet.RIGHT, constraintLayout.getId(), ConstraintSet.RIGHT, 0);
//        constraintSet.connect(R.id.workoutFragmentContainerView, ConstraintSet.TOP, constraintLayout.getId(), ConstraintSet.TOP, 0);
//        constraintSet.connect(R.id.workoutFragmentContainerView, ConstraintSet.BOTTOM, constraintLayout.getId(), ConstraintSet.BOTTOM, 0);
//        constraintSet.applyTo(constraintLayout);


        //   w.isGarminConnected.set(false);

        //   if (!w.isGarminConnected.get()) {
        //   getBinding().rbGarmin.setVisibility(View.GONE);
        //     getBinding().rbTrack.setBackgroundResource(R.drawable.btn_stats_select_2);
        //    }


        getBinding().rgStatss.setOnCheckedChangeListener((group, checkedId) -> {
            ArrayList<RadioButton> listOfRadioButtons = new ArrayList<>();
            for (int i = 0; i < getBinding().rgStatss.getChildCount(); i++) {
                View o = getBinding().rgStatss.getChildAt(i);
                if (o instanceof RadioButton) {
                    listOfRadioButtons.add((RadioButton) o);
                    if (listOfRadioButtons.get(i).getId() == checkedId) {
                        int t = Integer.parseInt((String) listOfRadioButtons.get(i).getTag());
                        switch (t) {
                            case 0:
                                if (workoutChartsFragment != null)
                                    workoutChartsFragment.dismissBar();
                                LiveEventBus.get(SELECT_WORKOUT_PAGE).post(WORKOUT_PAGE_STATS);
                                break;
                            case 1:
                                LiveEventBus.get(SELECT_WORKOUT_PAGE).post(WORKOUT_PAGE_CHARTS);
                                break;
                            case 2:
                                if (workoutChartsFragment != null)
                                    workoutChartsFragment.dismissBar();
                                LiveEventBus.get(SELECT_WORKOUT_PAGE).post(WORKOUT_PAGE_TRACK);
                                break;
                            case 3:
                                if (workoutChartsFragment != null)
                                    workoutChartsFragment.dismissBar();
                                LiveEventBus.get(SELECT_WORKOUT_PAGE).post(WORKOUT_PAGE_GARMIN);
                                break;
                        }
                    }
                }
            }
        });


        if (isTreadmill) {
            //Menu位置
            getBinding().phLeftStatsTreadmillUs.setContentId(R.id.cStatsUs);

            //speed +-
            getBinding().phRightTopControllerUs.setContentId(R.id.speed_controller_us);
        } else {

            getBinding().speedControllerUs.setVisibility(View.GONE);

            getBinding().phRightTopControllerUs.setContentId(R.id.cStatsUs);

            getBinding().groupViewLevelUs.setVisibility(View.VISIBLE);


        }

        //隱藏MENU
        //    getBinding().cStatsUsNoView.setVisibility(View.GONE);


        //   initUsBottomNumber();

        getBinding().btnWorkoutStopUs.setVisibility(View.VISIBLE);
        //    getBinding().btnCoolDownUs.setVisibility(View.VISIBLE);

        getBinding().btnCoolDown.setVisibility(View.INVISIBLE);
        getBinding().btnWorkoutStop.setVisibility(View.INVISIBLE);

        //Skip
        //   getBinding().btnSkipUs.setVisibility(View.VISIBLE);

        getBinding().btnSkip.setVisibility(View.GONE);
        // getBinding().warmUpAndCoolDownTitle.setVisibility(View.GONE);


        if (w.disabledSpeedUpdate.get()) {
            getBinding().btnCoolDownUs.setVisibility(View.GONE);
        }

        getBinding().btnWorkoutStopUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isBottomStopShowing) {
                    LiveEventBus.get(FITNESS_TEST_STOP_WORKOUT).post(true);
                } else {
                    getBinding().btnWorkoutStop.callOnClick();
                }
            }
        });

    }

    private void initUsBottomNumber() {

//        w.disabledSpeedUpdate.set(false);

//        getBinding().groupBottomNumberUs.setVisibility(View.VISIBLE);

        getBinding().tvBottomTextUs.setText(R.string.direct_speed);


        getBinding().tvBottomNumberUs1.setText(UNIT_E == IMPERIAL ? TREADMILL_SPEED_IMPERIAL_US_NUM_1 : TREADMILL_SPEED_METRIC_US_NUM_1);
        getBinding().tvBottomNumberUs2.setText(UNIT_E == IMPERIAL ? TREADMILL_SPEED_IMPERIAL_US_NUM_2 : TREADMILL_SPEED_METRIC_US_NUM_2);
        getBinding().tvBottomNumberUs3.setText(UNIT_E == IMPERIAL ? TREADMILL_SPEED_IMPERIAL_US_NUM_3 : TREADMILL_SPEED_METRIC_US_NUM_3);
        getBinding().tvBottomNumberUs4.setText(UNIT_E == IMPERIAL ? TREADMILL_SPEED_IMPERIAL_US_NUM_4 : TREADMILL_SPEED_METRIC_US_NUM_4);
        getBinding().tvBottomNumberUs5.setText(UNIT_E == IMPERIAL ? TREADMILL_SPEED_IMPERIAL_US_NUM_5 : TREADMILL_SPEED_METRIC_US_NUM_5);
        getBinding().tvBottomNumberUs6.setText(UNIT_E == IMPERIAL ? TREADMILL_SPEED_IMPERIAL_US_NUM_6 : TREADMILL_SPEED_METRIC_US_NUM_6);
        getBinding().tvBottomNumberUs7.setText(UNIT_E == IMPERIAL ? TREADMILL_SPEED_IMPERIAL_US_NUM_7 : TREADMILL_SPEED_METRIC_US_NUM_7);
        getBinding().tvBottomNumberUs8.setText(UNIT_E == IMPERIAL ? TREADMILL_SPEED_IMPERIAL_US_NUM_8 : TREADMILL_SPEED_METRIC_US_NUM_8);
        getBinding().tvBottomNumberUs9.setText(UNIT_E == IMPERIAL ? TREADMILL_SPEED_IMPERIAL_US_NUM_9 : TREADMILL_SPEED_METRIC_US_NUM_9);
        getBinding().tvBottomNumberUs10.setText(UNIT_E == IMPERIAL ? TREADMILL_SPEED_IMPERIAL_US_NUM_10 : TREADMILL_SPEED_METRIC_US_NUM_10);
        getBinding().tvBottomNumberUs11.setText(UNIT_E == IMPERIAL ? TREADMILL_SPEED_IMPERIAL_US_NUM_11 : TREADMILL_SPEED_METRIC_US_NUM_11);
        getBinding().tvBottomNumberUs12.setText(UNIT_E == IMPERIAL ? TREADMILL_SPEED_IMPERIAL_US_NUM_12 : TREADMILL_SPEED_METRIC_US_NUM_12);


        getBinding().tvBottomNumberUs1.setOnClickListener(v -> updateSpeedOrLevelNum(getSpeedLevel(getConNum(getBinding().tvBottomNumberUs1)), true));
        getBinding().tvBottomNumberUs2.setOnClickListener(v -> updateSpeedOrLevelNum(getSpeedLevel(getConNum(getBinding().tvBottomNumberUs2)), true));
        getBinding().tvBottomNumberUs3.setOnClickListener(v -> updateSpeedOrLevelNum(getSpeedLevel(getConNum(getBinding().tvBottomNumberUs3)), true));
        getBinding().tvBottomNumberUs4.setOnClickListener(v -> updateSpeedOrLevelNum(getSpeedLevel(getConNum(getBinding().tvBottomNumberUs4)), true));
        getBinding().tvBottomNumberUs5.setOnClickListener(v -> updateSpeedOrLevelNum(getSpeedLevel(getConNum(getBinding().tvBottomNumberUs5)), true));
        getBinding().tvBottomNumberUs6.setOnClickListener(v -> updateSpeedOrLevelNum(getSpeedLevel(getConNum(getBinding().tvBottomNumberUs6)), true));
        getBinding().tvBottomNumberUs7.setOnClickListener(v -> updateSpeedOrLevelNum(getSpeedLevel(getConNum(getBinding().tvBottomNumberUs7)), true));
        getBinding().tvBottomNumberUs8.setOnClickListener(v -> updateSpeedOrLevelNum(getSpeedLevel(getConNum(getBinding().tvBottomNumberUs8)), true));
        getBinding().tvBottomNumberUs9.setOnClickListener(v -> updateSpeedOrLevelNum(getSpeedLevel(getConNum(getBinding().tvBottomNumberUs9)), true));
        getBinding().tvBottomNumberUs10.setOnClickListener(v -> updateSpeedOrLevelNum(getSpeedLevel(getConNum(getBinding().tvBottomNumberUs10)), true));
        getBinding().tvBottomNumberUs11.setOnClickListener(v -> updateSpeedOrLevelNum(getSpeedLevel(getConNum(getBinding().tvBottomNumberUs11)), true));
        getBinding().tvBottomNumberUs12.setOnClickListener(v -> updateSpeedOrLevelNum(getSpeedLevel(getConNum(getBinding().tvBottomNumberUs12)), true));

    }


    private void initUsTopNumber() {

//        w.disabledInclineUpdate.set(false);

//        getBinding().groupTopNumberUs.setVisibility(View.VISIBLE);

        getBinding().tvTopTextUs.setText(R.string.direct_incline);


        getBinding().tvTopNumberUs1.setOnClickListener(v -> updateInclineNum(getInclineLevel(getConNum(getBinding().tvTopNumberUs1)), true));
        getBinding().tvTopNumberUs2.setOnClickListener(v -> updateInclineNum(getInclineLevel(getConNum(getBinding().tvTopNumberUs2)), true));
        getBinding().tvTopNumberUs3.setOnClickListener(v -> updateInclineNum(getInclineLevel(getConNum(getBinding().tvTopNumberUs3)), true));
        getBinding().tvTopNumberUs4.setOnClickListener(v -> updateInclineNum(getInclineLevel(getConNum(getBinding().tvTopNumberUs4)), true));
        getBinding().tvTopNumberUs5.setOnClickListener(v -> updateInclineNum(getInclineLevel(getConNum(getBinding().tvTopNumberUs5)), true));
        getBinding().tvTopNumberUs6.setOnClickListener(v -> updateInclineNum(getInclineLevel(getConNum(getBinding().tvTopNumberUs6)), true));
        getBinding().tvTopNumberUs7.setOnClickListener(v -> updateInclineNum(getInclineLevel(getConNum(getBinding().tvTopNumberUs7)), true));
        getBinding().tvTopNumberUs8.setOnClickListener(v -> updateInclineNum(getInclineLevel(getConNum(getBinding().tvTopNumberUs8)), true));
        getBinding().tvTopNumberUs9.setOnClickListener(v -> updateInclineNum(getInclineLevel(getConNum(getBinding().tvTopNumberUs9)), true));
        getBinding().tvTopNumberUs10.setOnClickListener(v -> updateInclineNum(getInclineLevel(getConNum(getBinding().tvTopNumberUs10)), true));
        getBinding().tvTopNumberUs11.setOnClickListener(v -> updateInclineNum(getInclineLevel(getConNum(getBinding().tvTopNumberUs11)), true));
        getBinding().tvTopNumberUs12.setOnClickListener(v -> updateInclineNum(getInclineLevel(getConNum(getBinding().tvTopNumberUs12)), true));

    }

    public void usWorkoutStopLong() {
        ViewGroup.LayoutParams layoutParams = getBinding().btnWorkoutStopUs.getLayoutParams();
        layoutParams.height = 400;
        getBinding().btnWorkoutStopUs.setLayoutParams(layoutParams);
    }

    /**
     * init fragment
     */
    private void initFragment() {
        workoutStatsFragment = new WorkoutStatsFragment();
        workoutChartsFragment = new WorkoutChartsFragment();
        workoutTrackFragment = new WorkoutTrackFragment();

        initGarmin();

        changeFragment(workoutTrackFragment);


        new RxTimer().timer(50, b ->
                changeFragment(workoutChartsFragment));


        if (w.selProgram == EGYM) {
            getBinding().warmUpAndCoolDownTitle.setVisibility(View.GONE);
            getBinding().tvWarmUpAndCoolDownTime.setVisibility(View.GONE);
            getBinding().btnSkip.setVisibility(View.GONE);
        }

        new RxTimer().timer(100, number -> {
            changeFragment(workoutStatsFragment);
        });
    }

    private void initGarmin() {
        //isWorkoutGarmin=true,isGarminConnected=false >> 顯示未連線的Garmin圖
        //isWorkoutGarmin=false,isGarminConnected=false >> 都不顯示
        //isWorkoutGarmin=false,isGarminConnected=true >> 會顯示已連線的Garmin圖
        //isWorkoutGarmin=true,isGarminConnected=true >> 會顯示已連線的Garmin圖


        if (!w.isGarminConnected.get()) return;

        workoutGarminFragment = new WorkoutGarminFragment();
//        changeFragment(workoutGarminFragment);

        new RxTimer().timer(30, b ->
                changeFragment(workoutGarminFragment));

        w.isWorkoutGarmin.set(w.isGarminConnected.get());

        //  parent.enableRealTimeData(currentGarminAddress);

    }

    public Calculation.TYPE getCalculationMachineType(int machineType) {

        Calculation.TYPE calculationType = Calculation.TYPE.Treadmill;
        switch (machineType) {
            case DEVICE_TYPE_TREADMILL:
                calculationType = Calculation.TYPE.Treadmill;
                break;
            case DEVICE_TYPE_ELLIPTICAL:
                calculationType = Calculation.TYPE.EcbElliptical;
                break;
            case DEVICE_TYPE_UPRIGHT_BIKE:
            case DEVICE_TYPE_RECUMBENT_BIKE:
                calculationType = Calculation.TYPE.EcbBike;
                break;
        }
        return calculationType;
    }


    /**
     * 初始化Workout資料
     */
    private void initWorkoutData() {

        //IPrograms 執行完後才執行 initWorkoutData

        w.setWorkoutStartTime(Calendar.getInstance().getTimeInMillis());

        //  w.currentShowTimeText.set(w.selWorkoutTime.get() == UNLIMITED ? "00:00" : formatSecToM(w.selWorkoutTime.get()));

        //EGYM WORKOUT 時間 有可能設為 0
        if (w.selProgram == EGYM && w.selWorkoutTime.get() == UNLIMITED) {
            w.selWorkoutTime.set(1);
        }

        //   Log.d("GGGGGEEEEEEE", "initWorkoutData: " + w.selWorkoutTime.get());

        //下數(倒數計時)的時間
        if (w.selWorkoutTime.get() != UNLIMITED) {
            w.remainingTime.set(w.selWorkoutTime.get());

            //     Log.d("FFFFFFCCCC", "initWorkoutData: "+w.warmUpTime.get() +","+ w.coolDownTime.get());

            //倒數計時 program設定的時間 加上 warmUp coolDown 的時間
            w.remainingTimeShow.set(w.remainingTime.get() + w.warmUpTime.get() + w.coolDownTime.get());
        }

        w.currentShowTimeText.set(w.remainingTimeShow.get() == UNLIMITED ? "00:00" : formatSecToM(w.remainingTimeShow.get()));


    }


    /**
     * 初始化 Warm-Up
     */
    private void initWarmUp() {

        getBinding().btnSkip.setOnClickListener(v -> {
            //一定都會執行到
            if (CheckDoubleClick.isFastClick()) return;
            warmUpSkip();
            initTimer(false);
            setFtmsEquipmentType(true);
        });


        getBinding().btnSkipUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getBinding().btnSkip.callOnClick();
            }
        });

        //WarmUp時間0，不用WarmUp >> 跳出
        if (w.warmUpTime.get() <= 0) {
            getBinding().btnSkip.callOnClick();
            return;
        } else {
            w.isWarmUpIng.set(true);
        }

        //@FTMS WarmUp
//        Map<DeviceGEM.EQUIPMENT_CONTROL_PARAMETER, Integer> parameters = new HashMap<>();
//        parent.updateFtmsMachineStatus(DeviceGEM.FITNESS_EQUIPMENT_STATE.IN_USE_WARMING_UP, parameters);


        // Warm Up
        getBinding().warmUpAndCoolDownTitle.setText(R.string.Warm_up);
        getBinding().btnSkip.setText(R.string.Skip_Warm_up);

        if (isUs) {
            getBinding().btnSkipUs.setText(R.string.Skip_Warm_up);
        }

        coolDownAndWarmUpTimeSecond = w.warmUpTime.get();
        getBinding().tvWarmUpAndCoolDownTime.setText(formatSecToM(w.warmUpTime.get()));

        //warmUp計時
        coolDownAndWarmUpTimer = new RxTimer();
        coolDownAndWarmUpTimer.interval(1000, number -> {
            if (coolDownAndWarmUpTimeSecond <= 0) {
                coolDownAndWarmUpTimer.cancel();
                coolDownAndWarmUpTimer = null;
                //Warm-Up 結束
                getBinding().btnSkip.callOnClick();
            } else {

                //執行各個program的 warm up
                iPrograms.warmUp(coolDownAndWarmUpTimeSecond);

                //倒數時間
                coolDownAndWarmUpTimeSecond--;
                w.remainingTimeShow.set(w.remainingTimeShow.get() - 1);
                //經過的時間
                w.elapsedTimeShow.set((int) (w.warmUpTime.get() - coolDownAndWarmUpTimeSecond));
                w.totalElapsedTimeShow.set(w.totalElapsedTimeShow.get() + 1);


                //執行各個program的 warm up
                //      iPrograms.warmUp(coolDownAndWarmUpTimeSecond);

                //warmup資料計算
                calcWorkoutData();

                if (coolDownAndWarmUpTimeSecond >= 0) {
                    //warm up 傳送FTMS資料
                    updateFtmsData();
                }

                getBinding().tvWarmUpAndCoolDownTime.setText(formatSecToM(coolDownAndWarmUpTimeSecond));


                //更新上方欄位顯示的時間
                w.currentShowTimeText.set(formatSecToM(w.remainingTimeShow.get()));


//                //倒數時間
//                coolDownAndWarmUpTimeSecond--;
//                w.remainingTimeShow.set(w.remainingTimeShow.get() - 1);
//                //經過的時間
//                w.elapsedTimeShow.set((int) (w.warmUpTime.get() - coolDownAndWarmUpTimeSecond));
//                w.totalElapsedTimeShow.set(w.totalElapsedTimeShow.get() + 1);

//                Log.d("FFFFFFCCCC", "經過時間: " + w.elapsedTimeShow.get() +",倒數時間:"+w.remainingTimeShow.get());
            }

        });
    }

    private RxTimer coolDownAndWarmUpTimer;
    long coolDownAndWarmUpTimeSecond;

    /**
     * 初始化Cool-Down
     */
    public void initCoolDown() {

        onSelect(WORKOUT_PAGE_STATS);

        if (w.isCoolDowning.get()) return;
        w.isCoolDowning.set(true);
        showCooldownButton(false);
        //停止計時
        isWorkoutTimerRunning = false;

        // 若已經 STATUS_SUMMARY 就不執行下去
        if (appStatusViewModel.currentStatus.get() == AppStatusIntDef.STATUS_SUMMARY || isSummary)
            return;

        //CoolDown時間0，不進行CoolDown，直接結束workout
        if (w.coolDownTime.get() <= 0) {
            w.isCoolDowning.set(false);
            finishWorkout(true);
            return;
        }


        //todo
        //@FTMS CoolDown
//        Map<DeviceGEM.EQUIPMENT_CONTROL_PARAMETER, Integer> parameters = new HashMap<>();
//        parent.updateFtmsMachineStatus(DeviceGEM.FITNESS_EQUIPMENT_STATE.IS_USE_COOL_DOWN, parameters);

        //CoolDown Skip and Finish
        getBinding().btnSkip.setOnClickListener(v -> {
            if (CheckDoubleClick.isFastClick()) return;

            //點下去按鈕會消失，finishWorkout時 要等狀態，狀態不對會return, 如果此時狀態不對，按鈕也不能按了
            //等狀態 // 219 已進入RUNNING模式            x224 等待速度及揚升皆為0
            if (isTreadmill && (u.getDevStep() != DS_RUNNING_STANDBY && u.getDevStep() != DS_B5_RUNNING_AD_CHANGE_RSP) && !isEmulator) {
                //按 cooldown 時 按鈕會消失  , 如果此時狀態不對，就壞了
                return;
            }


            getBinding().viewWarmUpAndCoolDown.setVisibility(View.INVISIBLE);

            if (isUs) {
                Log.d("USSSSSS", "initCoolDown: btnSkip");
                getBinding().cStatsUsNoView.setVisibility(View.INVISIBLE);
                getBinding().cStatsUsNoViewBike.setVisibility(View.INVISIBLE);
            }
            //   getBinding().workoutFragmentContainerView.setVisibility(View.VISIBLE);
            finishWorkout(false);
        });


        getBinding().btnSkipUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getBinding().btnSkip.callOnClick();
            }
        });

        //cooldown 的時候不可按
        getBinding().btnWorkoutStop.setClickable(false);


        if (coolDownAndWarmUpTimer != null) {
            coolDownAndWarmUpTimer.cancel();
            coolDownAndWarmUpTimer = null;
        }

        // getBinding().viewBase.setBackgroundResource(R.drawable.img_workout_1);
        if (n >= drawableRs.length) n = 0;
        setBackground(drawableRs[n]);

        //  showCooldownButton(false);

        MaterialFadeThrough materialFade = new MaterialFadeThrough();
        materialFade.setDuration(300);
        TransitionManager.beginDelayedTransition(getBinding().container, materialFade);

        getBinding().viewWarmUpAndCoolDown.setVisibility(View.VISIBLE);

        if (isUs) {
            if (isTreadmill) {
                getBinding().cStatsUsNoView.setVisibility(View.VISIBLE);
            } else {
                getBinding().cStatsUsNoViewBike.setVisibility(View.VISIBLE);
            }
        }
        getBinding().workoutFragmentContainerView.setVisibility(View.INVISIBLE);
        //   getBinding().tvWarmUpAndCoolDownTime.setText("02:56");

        getBinding().warmUpAndCoolDownTitle.setText(R.string.Cool_Down);
        getBinding().btnSkip.setText(R.string.Skip_and_Finish);

        getBinding().btnSkipUs.setText(R.string.Skip_and_Finish);

        coolDownAndWarmUpTimeSecond = w.coolDownTime.get();
        getBinding().tvWarmUpAndCoolDownTime.setText(formatSecToM(w.coolDownTime.get()));


        //cooldown倒數時間變成cooldown設定的時間
        w.remainingTimeShow.set((int) coolDownAndWarmUpTimeSecond);


        //cooldown計時
        coolDownAndWarmUpTimer = new RxTimer();
        coolDownAndWarmUpTimer.interval(1000, number -> {
            if (coolDownAndWarmUpTimeSecond <= 0) {
                coolDownAndWarmUpTimer.cancel();
                coolDownAndWarmUpTimer = null;
                finishWorkout(true);
            } else {

                //program 執行cooldown
                iPrograms.coolDown(coolDownAndWarmUpTimeSecond);

                //倒數時間
                coolDownAndWarmUpTimeSecond--;
                w.remainingTimeShow.set((int) coolDownAndWarmUpTimeSecond);
                //經過的時間
                w.elapsedTimeShow.set(w.elapsedTimeShow.get() + 1);
                w.totalElapsedTimeShow.set(w.totalElapsedTimeShow.get() + 1);

                //cooldown資料計算
                calcWorkoutData();

                if (coolDownAndWarmUpTimeSecond >= 0) {
                    //cool down 傳送FTMS資料
                    updateFtmsData();
                }

                getBinding().tvWarmUpAndCoolDownTime.setText(formatSecToM(coolDownAndWarmUpTimeSecond));


                //更新上方欄位顯示的時間
                w.currentShowTimeText.set(formatSecToM(coolDownAndWarmUpTimeSecond));


//                //program 執行cooldown
//                iPrograms.coolDown(coolDownAndWarmUpTimeSecond);


//                //倒數時間
//                coolDownAndWarmUpTimeSecond--;
//                w.remainingTimeShow.set((int) coolDownAndWarmUpTimeSecond);
//                //經過的時間
//                w.elapsedTimeShow.set(w.elapsedTimeShow.get() + 1);
//                w.totalElapsedTimeShow.set(w.totalElapsedTimeShow.get() + 1);

                Log.d("FFFFFFCCCC", "FTMS: " + w.elapsedTimeShow.get() + ", CONSOLE:" + w.totalElapsedTimeShow.get());
            }


//            workoutData.put(DeviceGEM.WORKOUT_DATA_FIELD.ELAPSED_WORKOUT_TIME, w.elapsedTimeShow.get()); //過去時間，從0開始數  根據這裡發送運動紀錄，時間差30秒以上或時間倒退就會送一筆運動紀錄
//            workoutData.put(DeviceGEM.WORKOUT_DATA_FIELD.REMAINING_WORKOUT_TIME, w.remainingTimeShow.get());

        });
    }


    /**
     * FTMS 設備狀態
     *
     * @param isStart 開始或暫停
     */
    private void setFtmsEquipmentType(boolean isStart) {
        Map<DeviceGEM.EQUIPMENT_CONTROL_PARAMETER, Integer> parameters = new HashMap<>();
        Log.d("GEM3", "setFtmsEquipmentType: " + isStart);
        if (!isStart) { // false
//            parent.updateFtmsMachineStatus(DeviceGEM.FITNESS_EQUIPMENT_STATE.FINISHED, parameters);
            // TODO: NEW GEM3
            parent.updateFtmsMachineStatus(DeviceGEM.FITNESS_EQUIPMENT_STATE.PAUSE_OR_STOPPED_BY_THE_USER, parameters);
            return;
        }

        //todo com.dyaco.spirit_commercial.workout.programs.ProgramsEnum.getProgramType()' on a null object reference


        // TODO: NEW GEM3
        //RESUME
        parent.updateFtmsMachineStatus(DeviceGEM.FITNESS_EQUIPMENT_STATE.IN_USE, parameters);


        if (w.selProgram.getProgramType() != WorkoutIntDef.FITNESS_TESTS) {

            //     parent.updateFtmsMachineStatus2(DeviceGEM.TRAINING_STATUS.MANUAL_MODE);
            //     parent.updateFtmsMachineStatus(DeviceGEM.FITNESS_EQUIPMENT_STATE.MANUAL_MODE_QUICK_START, parameters);
        } else {
            parent.updateFtmsMachineStatus2(DeviceGEM.TRAINING_STATUS.FITNESS_TEST);
//            parent.updateFtmsMachineStatus(DeviceGEM.FITNESS_EQUIPMENT_STATE.IS_USE_FITNESS_TEST, parameters);
        }
    }


    boolean longClickBuzzerCheck = false;

    /**
     * 外部控制
     * 實體按鍵 FTMS
     */
    private void initExControl() {

        //長按結束
        LiveEventBus.get(KEY_UNKNOWN).observe(getViewLifecycleOwner(), s -> {
            if (longClickBuzzerCheck) {
                u.setBuzzer();
            }
            longClickBuzzerCheck = false;
        });

        //FTMS控制 STOP_OR_PAUSE
        LiveEventBus.get(FTMS_STOP_OR_PAUSE).observe(getViewLifecycleOwner(), s -> {

            //等狀態
            if (isTreadmill && (u.getDevStep() != DS_RUNNING_STANDBY && u.getDevStep() != DS_B5_RUNNING_AD_CHANGE_RSP) && !isEmulator) {
                return;
            }

            if (CheckDoubleClick.isFastClick2()) return;

            //外部控制PAUSE
            if (w.selProgram == CTT_PREDICTION || w.selProgram == CTT_PERFORMANCE || w.selProgram == GERKIN) {
                //下方STOP按鈕 直接結束
                LiveEventBus.get(FITNESS_TEST_STOP_WORKOUT).post(true);
                return;
            }

            if (w.isWarmUpIng.get() || w.isCoolDowning.get()) {

                getBinding().btnSkip.callOnClick();
                u.setBuzzer();
                return;
            }

            if (appStatusViewModel.currentStatus.get() == AppStatusIntDef.STATUS_RUNNING) {

                if (isResuming) return; //WorkoutPauseFragment Resume 倒數中，不執行Stop

                getBinding().btnWorkoutStop.callOnClick();
                u.setBuzzer();
                //MediaMenu狀態改變
                LiveEventBus.get(MEDIA_MENU_CHANGE).post("");
            }

        });

        //FTMS、實體按鍵控制SPEED or Level > 階數
        //Treadmill HeartRate 用Incline控制 THR
        //Bike HeartRate 用Level控制 THR
        LiveEventBus.get(FTMS_SET_TARGET_SPEED).observe(getViewLifecycleOwner(), s -> {

            if (isResuming) return;

            if (cautionSpeedWindow != null && cautionSpeedWindow.isShowing()) return;

            if (isTreadmill) {
                if (w.disabledSpeedUpdate.get()) return;
            } else {
                if (w.disabledLevelUpdate.get()) return;
            }


            if (appStatusViewModel.currentStatus.get() != AppStatusIntDef.STATUS_RUNNING) return;

            Log.d("@@@", "FTMS: Control Point:" + s);
            Integer n = (Integer) s;
            Log.d("@@@", "FTMS: Control Point:" + n);
            if (n == CLICK_PLUS) { // SPEED +

                if (isTreadmill) {
                    if (updateSpeedOrLevelNum(1, false)) {// 實體按鍵 +1
                        u.setBuzzer();
                    }
                } else {
                    if (w.selProgram != HEART_RATE) {
                        if (updateSpeedOrLevelNum(1, false)) {
                            u.setBuzzer();
                        }
                    } else {
                        //Bike HeartRate 用Level控制 THR
                        if (updateTargetHeartRate(true)) {
                            u.setBuzzer();
                        }
                    }
                }
            } else if (n == CLICK_PLUS_2) {
                if (isTreadmill) {
                    if (updateSpeedOrLevelNum(1, false)) {// 實體按鍵 +1
                        //  u.setBuzzer();
                    }
                } else {
                    if (w.selProgram != HEART_RATE) {
                        if (updateSpeedOrLevelNum(1, false)) {
                            //   u.setBuzzer();
                        }
                    } else {
                        //Bike HeartRate 用Level控制 THR
                        if (updateTargetHeartRate(true)) {
                            //   u.setBuzzer();
                        }
                    }
                }

            } else if (n == CLICK_MINUS) {

                if (isTreadmill) {
                    if (updateSpeedOrLevelNum(-1, false)) {// 實體按鍵 -1
                        u.setBuzzer();
                    }
                } else {
                    if (w.selProgram != HEART_RATE) {
                        if (updateSpeedOrLevelNum(-1, false)) {
                            u.setBuzzer();
                        }
                    } else {
                        //Bike HeartRate 用Level控制 THR
                        if (updateTargetHeartRate(false)) {
                            u.setBuzzer();
                        }
                    }
                }
            } else if (n == CLICK_MINUS_2) {
                if (isTreadmill) {
                    if (updateSpeedOrLevelNum(-1, false)) {// 實體按鍵 -1
                        //     u.setBuzzer();
                    }
                } else {
                    if (w.selProgram != HEART_RATE) {
                        if (updateSpeedOrLevelNum(-1, false)) {
                            //     u.setBuzzer();
                        }
                    } else {
                        //Bike HeartRate 用Level控制 THR
                        if (updateTargetHeartRate(false)) {
                            //       u.setBuzzer();
                        }
                    }
                }

            } else if (n == LONG_CLICK_PLUS) { // SPEED實體按鍵長按 +1

                if (!isTreadmill) {
                    if (w.selProgram == HEART_RATE) return;
                }

                if (updateSpeedOrLevelNum(1, false)) {
                    longClickBuzzerCheck = true;
                }
            } else if (n == LONG_CLICK_MINUS) {// SPEED實體按鍵長按 -1

                if (!isTreadmill) {
                    if (w.selProgram == HEART_RATE) return;
                }

                if (updateSpeedOrLevelNum(-1, false)) {
                    longClickBuzzerCheck = true;
                }
            } else {//FTMS
                Log.d("GEM3", "1111FTMS: Control Point: FTMS_SET_TARGET_SPEED:" + n);
                if (!isTreadmill) {
                    if (w.selProgram == HEART_RATE) return;
                }
                Log.d("GEM3", "2222FTMS: Control Point: FTMS_SET_TARGET_SPEED:" + n);
                if (updateSpeedOrLevelNum(n, true)) { //FTMS
                    u.setBuzzer();
                }
            }
        });

        //FTMS控制INCLINE
        //Treadmill HeartRate 用Incline控制 THR
        LiveEventBus.get(FTMS_SET_TARGET_INCLINATION).observe(getViewLifecycleOwner(), s -> {

            if (cautionSpeedWindow != null && cautionSpeedWindow.isShowing()) return;
            if (isResuming) return;
            if (!isTreadmill) return;

            if (w.disabledInclineUpdate.get()) return;
            if (appStatusViewModel.currentStatus.get() != AppStatusIntDef.STATUS_RUNNING) return;
//            if (CheckDoubleClick.isFastClick2()) return;

            Integer n = (Integer) s;
            if (n == CLICK_PLUS) {
//                if (updateInclineNum(1, false)) {// 實體按鍵
//                    u.setBuzzer();
//                }
                if (w.selProgram != HEART_RATE) {
                    if (updateInclineNum(1, false)) {
                        u.setBuzzer();
                    }
                } else {
                    //Treadmill HeartRate 用Incline控制 THR
                    if (updateTargetHeartRate(true)) {
                        u.setBuzzer();
                    }
                }
            } else if (n == CLICK_MINUS) {
//                if (updateInclineNum(-1, false)) {// 實體按鍵
//                    u.setBuzzer();
//                }

                if (w.selProgram != HEART_RATE) {
                    if (updateInclineNum(-1, false)) {
                        u.setBuzzer();
                    }
                } else {
                    //Treadmill HeartRate 用Incline控制 THR
                    if (updateTargetHeartRate(false)) {
                        u.setBuzzer();
                    }
                }
            } else if (n == CLICK_PLUS_2) {
                if (w.selProgram != HEART_RATE) {
                    if (updateInclineNum(1, false)) {
                        //   u.setBuzzer();
                    }
                } else {
                    //Treadmill HeartRate 用Incline控制 THR
                    if (updateTargetHeartRate(true)) {
                        //   u.setBuzzer();
                    }
                }

            } else if (n == CLICK_MINUS_2) {
                if (w.selProgram != HEART_RATE) {
                    if (updateInclineNum(-1, false)) {
                        //   u.setBuzzer();
                    }
                } else {
                    if (updateTargetHeartRate(false)) {
                        //    u.setBuzzer();
                    }
                }

            } else if (n == LONG_CLICK_PLUS) {

                if (w.selProgram == HEART_RATE) return;

                if (updateInclineNum(1, false)) {
                    longClickBuzzerCheck = true;
                }
            } else if (n == LONG_CLICK_MINUS) {

                if (w.selProgram == HEART_RATE) return;

                if (updateInclineNum(-1, false)) {
                    longClickBuzzerCheck = true;
                }
            } else {//FTMS

                if (w.selProgram == HEART_RATE) return;

                if (updateInclineNum(n, true)) { //FTMS
                    u.setBuzzer();
                }
            }

        });


        //**************BIKE**********/
        //FTMS控制LEVEL
        LiveEventBus.get(FTMS_SET_TARGET_RESISTANCE_LEVEL).observe(getViewLifecycleOwner(), s -> {

            if (cautionSpeedWindow != null && cautionSpeedWindow.isShowing()) return;
            if (isResuming) return;
            if (isTreadmill) return;
            if (w.disabledLevelUpdate.get()) return;
            if (appStatusViewModel.currentStatus.get() != AppStatusIntDef.STATUS_RUNNING) return;

//            if (updateSpeedOrLevelNum((Integer) s, true)) {
//                u.setBuzzer();
//            }

//            if (w.selProgram != HEART_RATE) {
            if (updateSpeedOrLevelNum((Integer) s, true)) {
                u.setBuzzer();
            }
//            } else {
//                if (updateTargetHeartRate(true)) {
//                    u.setBuzzer();
//                }
//            }

        });

        //FTMS控制POWER
        LiveEventBus.get(FTMS_SET_TARGET_POWER).observe(getViewLifecycleOwner(), s -> {
            if (cautionSpeedWindow != null && cautionSpeedWindow.isShowing()) return;
            if (isTreadmill) return;
            if (w.disabledLevelUpdate.get()) return;
            if (appStatusViewModel.currentStatus.get() != AppStatusIntDef.STATUS_RUNNING) return;

            if (updateSpeedOrLevelNum((Integer) s, true)) {
                u.setBuzzer();
            }
        });
    }

    /**
     * @param num       階數 (實際數值 * 2 )
     * @param isSpecify 是否為指定數值
     */
    public boolean updateInclineNum(int num, boolean isSpecify) {

        //  w.disabledInclineUpdate.set(true);
        // w.disabledSpeedUpdate.set(true);

        if (CheckDoubleClick.isFastClick2()) return false;

        if (!w.isSafeKey.get()) return false;

        num = getSpecifyValue(w.currentInclineLevel.get(), num, isSpecify);

        if (workoutChartsFragment == null) return false;

     //   Log.d("OOOOOOOOOO", "updateInclineNum: " + num + "," + isSpecify);
        if (workoutChartsFragment.updateInclineNum(num)) {
       //     u.setDevSpeedAndIncline();
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param num       階數
     * @param isSpecify 是否為指定數值
     */
    public boolean updateSpeedOrLevelNum(int num, boolean isSpecify) {
        if (CheckDoubleClick.isFastClick2()) return false;
        if (!w.isSafeKey.get()) return false;

        if (w.selProgram == WATTS || w.selProgram == FITNESS_TEST) {
            int x = getSpecifyValue(w.constantPowerW.get(), num, isSpecify);
            if ((w.constantPowerW.get() + x) > POWER_MAX || (w.constantPowerW.get() + x) < POWER_MIN) {
                return false;
            }

            w.constantPowerW.set(w.constantPowerW.get() + x);

            Log.d("VVVVVVVV", "updateSpeedOrLevelNum: " + w.constantPowerW.get() + "," + w.currentRpm.get() + "," + calc.getLevel(w.constantPowerW.get(), w.currentRpm.get()));
        }
        num = getSpecifyValue(isTreadmill ? w.currentSpeedLevel.get() : w.currentLevel.get(), num, isSpecify);

        //電跑的 HeartRateProgram 最大速度不同
//        if (isTreadmill && w.selProgram == HEART_RATE) {
//            if ((w.currentSpeedLevel.get() + num) > (UNIT_E == IMPERIAL ? getSpeedLevel(HEART_IU_MAX_SPEED) : getSpeedLevel(HEART_MU_MAX_SPEED))){
//                return false;
//            }
//        }

        //HIIT WarmUp And Cooldown 調整speed或level 不可超過 Sprint
        if ((w.isWarmUpIng.get() || w.isCoolDowning.get()) && w.selProgram == HIIT) {
            int n = isTreadmill ? w.currentSpeedLevel.get() : w.currentLevel.get();
            if ((n + num) > w.selSprintSpeedLevel.get()) return false;
        }


        if (workoutChartsFragment == null) return false;

        if (workoutChartsFragment.updateSpeedNum(num)) {
            if (isTreadmill) {
              //  u.setDevSpeedAndIncline();
            } else {
                if (w.selProgram == WATTS || w.selProgram == FITNESS_TEST) {
                    int level;
                    if (w.selProgram == WATTS) {
                        level = calc.getLevel(w.constantPowerW.get(), w.currentRpm.get());
                        //       Log.d("EEEEEEFFFF", "updateSpeedOrLevelNum: " + level);
                    } else {
                        level = calc.getWattLevel(w.constantPowerW.get(), 50);
                    }
                    u.setDevWorkload(level);
                    w.currentLevel.set(level);

                    Log.d("FITNESS_TEST_PROGRAM", "11111setPwmLevel_POWER: " + w.currentLevel.get());
                } else {
               //     Log.d("UartConsoleManagerPF", "updateSpeedOrLevelNumWATT: " + w.currentLevel.get());
                    u.setDevWorkload(w.currentLevel.get());
                }
                //    u.setDevWorkload(w.currentLevel.get(), resistance);
            }
            return true;
        }
        return false;
    }

    /**
     * @param num       階數
     * @param isSpecify 是否為指定數值
     */
    public boolean updateSpeedOrLevelNumWATT(int num, boolean isSpecify) {
        if (CheckDoubleClick.isFastClick2()) return false;
        if (!w.isSafeKey.get()) return false;

        num = getSpecifyValue(isTreadmill ? w.currentSpeedLevel.get() : w.currentLevel.get(), num, isSpecify);

        Log.d("VVVVVVVV", "WATTupdateSpeedOrLevelNum: " + w.constantPowerW.get() + "," + w.currentRpm.get() + "," + calc.getWattLevel(w.constantPowerW.get(), w.currentRpm.get()));

        if (workoutChartsFragment == null) return false;

        if (workoutChartsFragment.updateSpeedNum(num)) {
            u.setDevWorkload(w.currentLevel.get());
            return true;
        }
        return false;
    }

    /**
     * 初始化Bike控制面板
     */
    private void initWorkoutControllerBike() {

        setBikeControllerNum();

//        getBinding().groupTreadmillViewIncline.setVisibility(View.GONE);
//        getBinding().groupTreadmillViewInclineNoView.setVisibility(View.GONE);

        if (!w.disabledLevelUpdate.get()) {

            if (isUs) {
                new CommonUtils().addAutoClick(getBinding().btnBikeLevelPlusUs, 300);
                new CommonUtils().addAutoClick(getBinding().btnBikeLevelMinusUs, 300);
            } else {
                new CommonUtils().addAutoClick(getBinding().btnBikeLevelPlus, 300);
                new CommonUtils().addAutoClick(getBinding().btnBikeLevelMinus, 300);
            }
        }

//        getBinding().btnBikeLevelPlus.setOnClickListener(v -> updateSpeedOrLevelNum(1, false));
//        getBinding().btnBikeLevelMinus.setOnClickListener(v -> updateSpeedOrLevelNum(-1, false));
//        getBinding().btnBikeLevelNum8.setOnClickListener(v -> updateSpeedOrLevelNum(8, true));
//        getBinding().btnBikeLevelNum16.setOnClickListener(v -> updateSpeedOrLevelNum(16, true));
//        getBinding().btnBikeLevelNum24.setOnClickListener(v -> updateSpeedOrLevelNum(24, true));
//        getBinding().btnBikeLevelNum32.setOnClickListener(v -> updateSpeedOrLevelNum(32, true));
//        getBinding().btnBikeLevelNum40.setOnClickListener(v -> updateSpeedOrLevelNum(40, true));

        if (w.selProgram != HEART_RATE) {
            getBinding().btnBikeLevelNum8.setOnClickListener(v -> updateSpeedOrLevelNum(getConNum(getBinding().btnBikeLevelNum8), true));
            getBinding().btnBikeLevelNum16.setOnClickListener(v -> updateSpeedOrLevelNum(getConNum(getBinding().btnBikeLevelNum16), true));
            getBinding().btnBikeLevelNum24.setOnClickListener(v -> updateSpeedOrLevelNum(getConNum(getBinding().btnBikeLevelNum24), true));
            getBinding().btnBikeLevelNum32.setOnClickListener(v -> updateSpeedOrLevelNum(getConNum(getBinding().btnBikeLevelNum32), true));
            getBinding().btnBikeLevelNum40.setOnClickListener(v -> updateSpeedOrLevelNum(getConNum(getBinding().btnBikeLevelNum40), true));

            if (isUs) {
                getBinding().tvTopNumberUs1.setOnClickListener(v -> updateSpeedOrLevelNum(getConNum(getBinding().tvTopNumberUs1), true));
                getBinding().tvTopNumberUs2.setOnClickListener(v -> updateSpeedOrLevelNum(getConNum(getBinding().tvTopNumberUs2), true));
                getBinding().tvTopNumberUs3.setOnClickListener(v -> updateSpeedOrLevelNum(getConNum(getBinding().tvTopNumberUs3), true));
                getBinding().tvTopNumberUs4.setOnClickListener(v -> updateSpeedOrLevelNum(getConNum(getBinding().tvTopNumberUs4), true));
                getBinding().tvTopNumberUs5.setOnClickListener(v -> updateSpeedOrLevelNum(getConNum(getBinding().tvTopNumberUs5), true));
                getBinding().tvTopNumberUs6.setOnClickListener(v -> updateSpeedOrLevelNum(getConNum(getBinding().tvTopNumberUs6), true));
                getBinding().tvTopNumberUs7.setOnClickListener(v -> updateSpeedOrLevelNum(getConNum(getBinding().tvTopNumberUs7), true));
                getBinding().tvTopNumberUs8.setOnClickListener(v -> updateSpeedOrLevelNum(getConNum(getBinding().tvTopNumberUs8), true));
                getBinding().tvTopNumberUs9.setOnClickListener(v -> updateSpeedOrLevelNum(getConNum(getBinding().tvTopNumberUs9), true));
                getBinding().tvTopNumberUs10.setOnClickListener(v -> updateSpeedOrLevelNum(getConNum(getBinding().tvTopNumberUs10), true));

                getBinding().tvBottomNumberUs1.setOnClickListener(v -> updateSpeedOrLevelNum(getConNum(getBinding().tvBottomNumberUs1), true));
                getBinding().tvBottomNumberUs2.setOnClickListener(v -> updateSpeedOrLevelNum(getConNum(getBinding().tvBottomNumberUs2), true));
                getBinding().tvBottomNumberUs3.setOnClickListener(v -> updateSpeedOrLevelNum(getConNum(getBinding().tvBottomNumberUs3), true));
                getBinding().tvBottomNumberUs4.setOnClickListener(v -> updateSpeedOrLevelNum(getConNum(getBinding().tvBottomNumberUs4), true));
                getBinding().tvBottomNumberUs5.setOnClickListener(v -> updateSpeedOrLevelNum(getConNum(getBinding().tvBottomNumberUs5), true));
                getBinding().tvBottomNumberUs6.setOnClickListener(v -> updateSpeedOrLevelNum(getConNum(getBinding().tvBottomNumberUs6), true));
                getBinding().tvBottomNumberUs7.setOnClickListener(v -> updateSpeedOrLevelNum(getConNum(getBinding().tvBottomNumberUs7), true));
                getBinding().tvBottomNumberUs8.setOnClickListener(v -> updateSpeedOrLevelNum(getConNum(getBinding().tvBottomNumberUs8), true));
                getBinding().tvBottomNumberUs9.setOnClickListener(v -> updateSpeedOrLevelNum(getConNum(getBinding().tvBottomNumberUs9), true));
                getBinding().tvBottomNumberUs10.setOnClickListener(v -> updateSpeedOrLevelNum(getConNum(getBinding().tvBottomNumberUs10), true));
            }


        }
        getBinding().btnBikeLevelPlus.setOnClickListener(v -> {
            if (w.selProgram != HEART_RATE) {
                updateSpeedOrLevelNum(1, false);
            } else {
                updateTargetHeartRate(true);
            }
        });

        getBinding().btnBikeLevelMinus.setOnClickListener(v -> {
            if (w.selProgram != HEART_RATE) {
                updateSpeedOrLevelNum(-1, false);
            } else {
                updateTargetHeartRate(false);
            }
        });

        getBinding().btnBikeLevelPlusUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getBinding().btnBikeLevelPlus.callOnClick();
            }
        });

        getBinding().btnBikeLevelMinusUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getBinding().btnBikeLevelMinus.callOnClick();
            }
        });
    }

    /**
     * 初始化Treadmill控制面板
     */
    private void initWorkoutControllerTreadmill() {


        setTreadmillSpeedControllerNum();

        if (!w.disabledInclineUpdate.get()) {
            if (isUs) {
                new CommonUtils().addAutoClick(getBinding().btnWorkoutInclinePlusUs, 300);
                new CommonUtils().addAutoClick(getBinding().btnWorkoutInclineMinusUs, 300);
            } else {
                new CommonUtils().addAutoClick(getBinding().btnWorkoutInclinePlus, 300);
                new CommonUtils().addAutoClick(getBinding().btnWorkoutInclineMinus, 300);
            }
        }

        if (!w.disabledSpeedUpdate.get()) {
            if (isUs) {
                new CommonUtils().addAutoClick(getBinding().btnWorkoutSpeedPlusUs, 300);
                new CommonUtils().addAutoClick(getBinding().btnWorkoutSpeedMinusUs, 300);
            } else {
                new CommonUtils().addAutoClick(getBinding().btnWorkoutSpeedPlus, 300);
                new CommonUtils().addAutoClick(getBinding().btnWorkoutSpeedMinus, 300);
            }
        }

        setTreadmillInclineControllerNum();

    }

    /**
     * 隱藏控制面板
     *
     * @param type PANEL_ALL_SHOW:全出現
     */
    public void disappearTreadmillControl(int type) {
        switch (type) {
            case PANEL_ALL_SHOW://全出現
                getBinding().groupTreadmillViewIncline.setVisibility(View.VISIBLE);
                getBinding().groupTreadmillViewInclineNoView.setVisibility(View.GONE);
                getBinding().groupTreadmillViewSpeed.setVisibility(View.VISIBLE);
                getBinding().groupTreadmillViewSpeedNoView.setVisibility(View.GONE);
                break;
            case PANEL_INCLINE_SHOW://show incline
                getBinding().groupTreadmillViewIncline.setVisibility(View.VISIBLE);
                getBinding().groupTreadmillViewInclineNoView.setVisibility(View.GONE);
                getBinding().groupTreadmillViewSpeed.setVisibility(View.GONE);
                getBinding().groupTreadmillViewSpeedNoView.setVisibility(View.VISIBLE);
                break;
            case PANEL_SPEED_SHOW://show SPEED
                getBinding().groupTreadmillViewIncline.setVisibility(View.GONE);
                getBinding().groupTreadmillViewInclineNoView.setVisibility(View.VISIBLE);
                getBinding().groupTreadmillViewSpeed.setVisibility(View.VISIBLE);
                getBinding().groupTreadmillViewSpeedNoView.setVisibility(View.GONE);
                break;
            case PANEL_ALL_DISAPPEAR://全消失
                getBinding().groupTreadmillViewIncline.setVisibility(View.INVISIBLE);
                getBinding().groupTreadmillViewSpeed.setVisibility(View.INVISIBLE);

                getBinding().groupTreadmillViewInclineNoView.setVisibility(View.VISIBLE);
                getBinding().groupTreadmillViewSpeedNoView.setVisibility(View.VISIBLE);
                break;
        }
    }

    private boolean checkIsFitnessTest() {

        //  if (w.selProgram == FITNESS_TEST) return false;

        return w.selProgram.getProgramType() == WorkoutIntDef.FITNESS_TESTS;
    }

    /**
     * 隱藏cooldown按鈕
     *
     * @param isShow isShow
     */
    public void showCooldownButton(boolean isShow) {

        if (checkIsFitnessTest()) return;

        if (isShow) {

            if (isUs) {
                getBinding().btnCoolDownUs.setVisibility(View.VISIBLE);
            } else {
                getBinding().btnCoolDown.setBackgroundResource(R.drawable.panel_bg_all_20_252e37);
                getBinding().btnCoolDown.setText(R.string.Cool_Down);
                getBinding().btnCoolDown.setClickable(true);
                getBinding().btnCoolDown.setEnabled(true);
            }

        } else {
            if (isUs) {
                getBinding().btnCoolDownUs.setVisibility(View.GONE);
            } else {
                getBinding().btnCoolDown.setBackgroundResource(R.drawable.panel_no_cooldown);
                getBinding().btnCoolDown.setText("");
                getBinding().btnCoolDown.setClickable(false);
                getBinding().btnCoolDown.setEnabled(false);
            }
        }


        //  getBinding().btnCoolDown.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    /**
     * 隱藏WorkoutStop按鈕
     *
     * @param isShow isShow
     */
    public void showWorkoutStopButton(boolean isShow) {
        getBinding().btnWorkoutStop.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    /**
     * 錯誤處理
     *
     * @param errorInfo errorInfo
     */
    private void handleError(ErrorInfo errorInfo) {
        switch (errorInfo.getUartErrorType()) {

            case UE_INVERTER: //變頻器
            case UE_MCU:
            case UE_RS485:
            case UE_UART:
            case UE_UART_LWR:
                w.disabledSpeedUpdate.set(true);
                w.disabledInclineUpdate.set(true);
                break;
            case UE_INIT://單位初始化
                break;
            case UE_MACHINE_TYPE://機型不符
                break;
            case UE_NONE:
                //沒錯誤時的處理
//                if (w.disabledLevelUpdate.get() == defaultInclineDisabled && w.disabledSpeedUpdate.get() == defaultSpeedDisabled) return;
//                w.disabledInclineUpdate.set(defaultInclineDisabled);
//                w.disabledSpeedUpdate.set(defaultSpeedDisabled);
                break;
            case UE_SAFETY_KEY:
                if (!w.isSafeKey.get()) {
                    isWorkoutTimerRunning = false;
                }
                //    Log.d("UART_CONSOLE", "安全鎖:w.isSafeKey.get(): " + w.isSafeKey.get());
                break;
            case UE_RPM:
                break;

            case UE_MCU_EMS:  // EMS_MCU_ERROR
                isWorkoutTimerRunning = false;
                break;
        }
    }


    /**
     * 初始化事件
     */
    private void initEvent() {

        LiveEventBus.get(NEW_VALUE_UPDATE, NewUpdateData.class).observe(getViewLifecycleOwner(), data -> {
            if (appStatusViewModel.currentStatus.get() == AppStatusIntDef.STATUS_PAUSE) return;
            if (data == null || data.getType() == -1) return;
            switch (data.getType()) {
                case STATS_INCLINE:
                    updateInclineNum(data.getValue(), true);
                    break;
                case STATS_SPEED:
                case STATS_LEVEL:
                case STATS_POWER:
                    Log.d("KKJJHHGG", "initEvent: " + data.getValue());
                    updateSpeedOrLevelNum(data.getValue(), true);
                    break;
            }
        });

        //GARMIN EVENT
        LiveEventBus.get(GARMIN_ON_DEVICE_DISCONNECTED, Device.class).observe(getViewLifecycleOwner(), device -> {
            if (!w.isWorkoutGarmin.get()) return;
            if (appStatusViewModel.currentStatus.get() == AppStatusIntDef.STATUS_RUNNING ||
                    appStatusViewModel.currentStatus.get() == AppStatusIntDef.STATUS_PAUSE) {
                changeFragment(workoutStatsFragment);

                if (n >= drawableRs.length) n = 0;
                setBackground(drawableRs[n]);
                //   getBinding().viewBase.setBackgroundResource(R.drawable.img_workout_1);
                Log.d("GARMINNN", "Garmin斷線 關閉 Garmin 頁籤: ");
            }
        });

        getBinding().btnCoolDown.setOnClickListener(v -> {
            if (CheckDoubleClick.isFastClick()) return;
            initCoolDown();
        });

        if (isUs) {
            getBinding().btnCoolDownUs.setOnClickListener(v -> {
                getBinding().btnCoolDown.callOnClick();
            });
        }

        LiveEventBus.get(EVT_ERR, Object.class).observe(getViewLifecycleOwner(), o -> {
            ErrorInfo errorInfo = (ErrorInfo) o;
            if (errorInfo != null) {
                handleError(errorInfo);
            }
        });

        //AUTO PAUSE 無人在跑帶上，自動進入PAUSE
        LiveEventBus.get(AUTO_PAUSE_EVENT, Boolean.class).observe(getViewLifecycleOwner(), o -> {

            if (!isTreadmill) return;
            if (isResuming) return;
            if (appStatusViewModel.currentStatus.get() != AppStatusIntDef.STATUS_RUNNING) return;

            if (o) {
                if (w.selProgram == CTT_PREDICTION || w.selProgram == CTT_PERFORMANCE) {
                    LiveEventBus.get(FITNESS_TEST_STOP_WORKOUT).post(true);
                    return;
                }

                if (w.isWarmUpIng.get() || w.isCoolDowning.get()) {
                    getBinding().btnSkip.callOnClick();
                }

                getBinding().btnWorkoutStop.callOnClick();

            }
        });

        //換頁籤
        LiveEventBus.get(SELECT_WORKOUT_PAGE, Integer.class).observe(getViewLifecycleOwner(), this::onSelect);

        //WorkoutPauseFragment發來的 Resume通知
        LiveEventBus.get(RESUME_WORKOUT, String.class).observe(getViewLifecycleOwner(), s -> {
            isResuming = false;
            if (appStatusViewModel.currentStatus.get() == AppStatusIntDef.STATUS_RUNNING) {
                if ("COOL_DOWN".equals(s)) {

                    parent.getUartConsoleManager().setDevStartWorkout();

                    u.setDevMainMode(DeviceDyacoMedical.MAIN_MODE.RUNNING);//電跑

                    initCoolDown();
                } else {

                    if (cautionSpeedWindow != null) {
                        cautionSpeedWindow.dismiss();
                        cautionSpeedWindow = null;
                    }

                    if (isTreadmill) {
                        int num = w.currentSpeedLevel.get();
                        if (w.selProgram == MANUAL) {
                            num = UNIT_E == IMPERIAL ? MIN_SPD_IU : MIN_SPD_MU;
                        }

                        cautionSpeedWindow = new CautionSpeedWindow(requireActivity(), num);
                        cautionSpeedWindow.showAtLocation(requireActivity().getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);
                        cautionSpeedWindow.setOnCustomDismissListener(new BasePopupWindow.OnCustomDismissListener() {
                            @Override
                            public void onStartDismiss(MsgEvent value) {
                                resumeManual();
                                //RESUME 還原暫停前的狀態
                                initTimer(true);
                                cautionSpeedWindow = null;
                            }

                            @Override
                            public void onDismiss() {
                            }
                        });


                    } else {
                        parent.getUartConsoleManager().setDevStartWorkout();
                        resumeManual();
                        //RESUME 還原暫停前的狀態
                        initTimer(true);
                    }
                }
                setFtmsEquipmentType(true);
            }
        });

        //Media發的Pause
        LiveEventBus.get(MEDIA_PAUSE_WORKOUT, String.class).observe(getViewLifecycleOwner(), s -> {
            if (appStatusViewModel.currentStatus.get() != AppStatusIntDef.STATUS_RUNNING) return;

            if (w.isCoolDowning.get() || w.isWarmUpIng.get()) {
                getBinding().btnSkip.callOnClick();
            }

            Log.d("UART_CONSOLE", "############MEDIA_PAUSE_WORKOUT########: ");

            getBinding().btnWorkoutStop.callOnClick();
        });


        //FITNESS_TEST_STOP_WORKOUT
        LiveEventBus.get(FITNESS_TEST_STOP_WORKOUT, Boolean.class).observe(getViewLifecycleOwner(), s -> {

            if (appStatusViewModel.currentStatus.get() != AppStatusIntDef.STATUS_RUNNING) return;

            if (w.selProgram == CTT_PREDICTION || w.selProgram == CTT_PERFORMANCE) {
                if (!w.isCoolDowning.get() && !w.isWarmUpIng.get()) {
                    //Warmup時STOP > 直接結束
                    //Workout時STOP > cooldown
                    //Cooldown時STOP > 直接結束
                    //CTT_PREDICTION 、CTT_PERFORMANCE，非 CoolDown 和 WarmUp 時按STOP進CoolDown
                    //當USER無法承受,按下STOP時,依時間查表,以取得VO2的分數
                    Log.d("CttPrediction_TEST", "COOL_DOWN: ");
                    initCoolDown();
                    return;
                }
            }

            finishWorkout(false);
        });

        //workout暫停按鈕
        getBinding().btnWorkoutStop.setOnClickListener(v -> {

            rpmCheck = 0;

            if (App.SETTING_SHOW) {
                LiveEventBus.get(CLOSE_SETTINGS).post(true);
            }


            //等狀態 // 219 已進入RUNNING模式            x224 等待速度及揚升皆為0
            if (isTreadmill && (u.getDevStep() != DS_RUNNING_STANDBY && u.getDevStep() != DS_B5_RUNNING_AD_CHANGE_RSP) && !isEmulator) {
                //按 cooldown 時 按鈕會消失  , 如果此時狀態不對，就壞了
                return;
            }


            clearChartMsg();

            closeWindow();

            if (CheckDoubleClick.isFastClick()) return;

            if (w.selProgram == CTT_PREDICTION || w.selProgram == CTT_PERFORMANCE) {
                if (!w.isCoolDowning.get() && !w.isWarmUpIng.get() && w.isWorkoutDone()) {
                    //CTT_PREDICTION 、CTT_PERFORMANCE，非 CoolDown 和 WarmUp 時按STOP進CoolDown
                    //當USER無法承受,按下STOP時,依時間查表,以取得VO2的分數
                    Log.d("CttPrediction_TEST", "COOL_DOWN: ");
                    initCoolDown();
                    return;
                }
            }


            //FTMS狀態設定
            setFtmsEquipmentType(false);

            //PAUSE 暫停
            if (isTreadmill) {
                // > resetSpeedAndIncline() 停止
                u.setDevMainMode(isGsMode ? DeviceDyacoMedical.MAIN_MODE.GS : DeviceDyacoMedical.MAIN_MODE.PAUSE);
            } else {
                u.emsWorkoutPause();
            }

            if (coolDownAndWarmUpTimer != null) coolDownAndWarmUpTimer.cancel();

            workoutTrackFragment.stopAnimation();

            if (appStatusViewModel.currentStatus.get() != AppStatusIntDef.STATUS_SUMMARY)
                appStatusViewModel.currentStatus.set(AppStatusIntDef.STATUS_PAUSE);

            isWorkoutTimerRunning = false;


            //FITNESS_TESTS 直接結束Workout
            if (checkIsFitnessTest()) {
                appStatusViewModel.currentStatus.set(AppStatusIntDef.STATUS_SUMMARY);
            }


            //目前狀態為PAUSE，通知 MainDashboardFragment換頁到 PAUSE
            LiveEventBus.get(PAUSE_WORKOUT).post("");


            //點stop 時同時按 state window會跑出來
            getBinding().hhBG.setVisibility(View.VISIBLE);
            new RxTimer().timer(1000, number -> {
                if (getBinding() != null) {
                    getBinding().hhBG.setVisibility(View.GONE);
                }
            });

        });
    }

    /**
     * 結束WorkOut
     */
    boolean isFinishWorkout = false;

    public void finishWorkout(boolean isWorkoutDone) {
        if (isFinishWorkout) return;
        isFinishWorkout = true;
        isWorkoutTimerRunning = false;
        if (getBinding() == null) return;
        //設定為STATUS_SUMMARY，才會直接離開Pause 到 Summary
        appStatusViewModel.currentStatus.set(AppStatusIntDef.STATUS_SUMMARY);


        //GERKIN WFI 達成目標時就算完成
        if (w.selProgram != GERKIN && w.selProgram != WFI && w.selProgram != CTT_PREDICTION) {
            w.setWorkoutDone(isWorkoutDone); //是否完成Workout
        }

        if (appStatusViewModel.currentPage.get() == AppStatusIntDef.CURRENT_PAGE_MEDIA) {
            //在 Media頁面時，達成目標，離開Workout
            //   w.currentPauseTimeText.set(getString(R.string.ending_time, formatSecToM(w.elapsedTime.get())));
            LiveEventBus.get(MEDIA_MENU_CHANGE).post("");

            //   LiveEventBus.get(FTMS_STOP_OR_PAUSE).post("");
//        } else {
//            //在Workout頁面時，達成目標，離開Workout
//            getBinding().btnWorkoutStop.callOnClick();
        }

        getBinding().btnWorkoutStop.callOnClick();
    }

    /**
     * <非GS mode>
     * manual => pause時，incline 歸零，resume後，incline 維持在 0 、run default speed (0.8km, 0.5mile)，
     * 圖型的部分，已經run過的不變，正在run的之後，incline都改為0，speed改為default。
     * <p>
     * profile => pause時，incline 歸零，resume後，incline與speed 執行 profile的數值。圖型不變。
     * <p>
     * ---------------------------------------------
     * <GS mode>
     * manual => pause時，incline stop，resume後，incline不歸零、run default speed (0.8km, 0.5mile)，
     * 圖型的部分，已經run過的不變，正在run的之後，incline不變，speed都改為default。
     * <p>
     * profile => pause時，incline stop，resume後，incline與speed 執行 profile的數值。
     */
    private void resumeManual() {
        if (!isTreadmill) return;
        if (workoutChartsFragment == null) return;
        //MANUAL 且不是 GS MODE，暫停重啟後，速度及揚升要回到預設值
        if (w.selProgram == MANUAL && !isGsMode) { //沒開GS MODE
            Log.d("UART_CONSOLE", "resumeManual: MANUAL 且不是 GS MODE，暫停重啟後，速度及揚升要回到預設值");
            int num1 = getSpecifyValue(w.currentInclineLevel.get(), 0, true);
            workoutChartsFragment.updateInclineNum(num1);

            int num2 = getSpecifyValue(w.currentSpeedLevel.get(), UNIT_E == IMPERIAL ? MIN_SPD_IU : MIN_SPD_MU, true);
            workoutChartsFragment.updateSpeedNum(num2);
        } else if (w.selProgram == MANUAL) {
            //MANUAL 且是 GS MODE，暫停重啟後，速度要回到預設值，揚升不變
            Log.d("UART_CONSOLE", "resumeManual: MANUAL 且是 GS MODE，暫停重啟後，速度要回到預設值，揚升不變");
            int num2 = getSpecifyValue(w.currentSpeedLevel.get(), UNIT_E == IMPERIAL ? MIN_SPD_IU : MIN_SPD_MU, true);
            workoutChartsFragment.updateSpeedNum(num2);
        }

        //從DS_PAUSE_STANDBY 回到 RUNNING ，會自動恢復之前的速度，
        u.setDevMainMode(DeviceDyacoMedical.MAIN_MODE.RUNNING); //電跑的
    }


    /**
     * 跳過WarmUp或WarmUp結束
     */
    private void warmUpSkip() {

        w.isWarmUpIng.set(false);

        if (coolDownAndWarmUpTimer != null) {
            coolDownAndWarmUpTimer.cancel();
            coolDownAndWarmUpTimer = null;
        }


//        int c = (int) (w.warmUpTime.get() - coolDownAndWarmUpTimeSecond); //跑了幾秒
        int c = (int) coolDownAndWarmUpTimeSecond; //剩下多少時間
        w.remainingTimeShow.set((int) (w.remainingTimeShow.get() - coolDownAndWarmUpTimeSecond));
        w.currentShowTimeText.set(formatSecToM(w.remainingTimeShow.get()));
        Log.d("FFFFFFCCCC", "warmUpSkip:還剩下多少時間沒跑完 " + c);


        //WarmUp結束顯示CoolDown按鈕
        showCooldownButton(w.coolDownTime.get() > 0);


        crossFade(getBinding().workoutFragmentContainerView, getBinding().viewWarmUpAndCoolDown, 200);
        if (isUs) {
            getBinding().cStatsUsNoView.setVisibility(View.GONE);
            getBinding().cStatsUsNoViewBike.setVisibility(View.GONE);
            //   getBinding().cStatsUs.setVisibility(View.VISIBLE);
        }


        getBinding().tvWarmUpAndCoolDownTime.setText("");

//        if (w.selProgram == ProgramsEnum.GERKIN) {
//            showCooldownButton(false);
//        }
    }

    private void changeFragment(Fragment fragment) {

        if (m_currentFragment != fragment) {

            clearChartMsg();

            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
            //動畫
            //   fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
            //   fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);

//            if (isTreadmill) fragmentTransaction.setTransition(TRANSIT_FRAGMENT_FADE);
            //不知為何，不加這個的話，EgymDiagramBarsView  會無法執行 onSizeChanged,
            //可能是動畫會 強制觸發整個 View 層級的重新測量與佈局 (layout pass)。
            fragmentTransaction.setTransition(TRANSIT_FRAGMENT_FADE);

            if (m_currentFragment != null)
                fragmentTransaction.hide(m_currentFragment);

            //在onStop(準確說是 onSaveInstanceState ) 之後不能執行 .commit() 否則會 crash, commitAllowingStateLoss 可以
            if (fragment.isAdded()) {
                fragmentTransaction.show(fragment).commitAllowingStateLoss();
                //  fragmentTransaction.show(fragment).commit();
            } else {
                fragmentTransaction.add(R.id.workoutFragmentContainerView, fragment).commitAllowingStateLoss();
                //   fragmentTransaction.add(R.id.workoutFragmentContainerView, fragment).commit();
            }
            m_currentFragment = fragment;

        }
    }

    /**
     * 換頁籤
     *
     * @param id id
     */
    private void onSelect(int id) {

        getBinding().ivLapAnimationM.setVisibility(View.GONE);
        if (id == WORKOUT_PAGE_STATS) {
            changeFragment(workoutStatsFragment);
            //   showBackground(R.drawable.img_workout_1);
            if (n >= drawableRs.length) n = 0;
            setBackground(drawableRs[n]);
//            getBinding().viewBase.setBackgroundResource(drawableRs[n]);
        } else if (id == WORKOUT_PAGE_CHARTS) {
            changeFragment(workoutChartsFragment);
            setBackground(R.drawable.color11151a);
            //   getBinding().viewBase.setBackgroundResource(R.color.color11151a);
        } else if (id == WORKOUT_PAGE_TRACK) {
            changeFragment(workoutTrackFragment);
            setBackground(R.drawable.color11151a);
            getBinding().ivLapAnimationM.setVisibility(View.VISIBLE);
            //  getBinding().viewBase.setBackgroundResource(R.color.color11151a);
        } else if (id == WORKOUT_PAGE_GARMIN) {
            changeFragment(workoutGarminFragment);
            setBackground(R.drawable.color11151a);
            //  getBinding().viewBase.setBackgroundResource(R.color.color11151a);
        }
    }

    public void setBackground(int res) {
        try {
            Glide.with(getApp())
                    .load(res)
                    .centerCrop()
                    // .transition(DrawableTransitionOptions.withCrossFade(1000))
                    //    .skipMemoryCache(true)
                    .into(getBinding().viewBase);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private void showBackground(int res) {
//        GlideApp.with(this)
//                .load(res)
//                .transition(DrawableTransitionOptions.withCrossFade(500))
//                .skipMemoryCache(true)
//                .into(new CustomTarget<Drawable>() {
//                    @Override
//                    public void onResourceReady(@NonNull Drawable resource, @Nullable @org.jetbrains.annotations.Nullable Transition<? super Drawable> transition) {
//                        getBinding().viewBase.setBackground(resource);
//                    }
//                    @Override
//                    public void onLoadCleared(@Nullable @org.jetbrains.annotations.Nullable Drawable placeholder) {
//                    }
//                });
//    }

    @Override
    public void onResume() {
        super.onResume();
        parent.hrNotifyWarringWindow(false);

        requireActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

    }

    private void checkInit() {

        if (workoutChartsFragment.diagramBarsView != null) {

            //Profile Program 從 第4根開始，warmup不算
            if (w.selProgram.getProgramType() == WorkoutIntDef.PROFILE_PROGRAM && isTreadmill) {
                w.currentSegment.set(3);
                Log.d("QQQQQQQQQ", "checkInit: " + w.currentSegment.get());
            }

            //初始化圖表
            workoutChartsFragment.updateDiagramBarNum(isTreadmill ? UPDATE_ALL_BAR : UPDATE_SPEED_BAR, false);

            if (w.selProgram == ProgramsEnum.EGYM) {
                EgymUtil.getInstance().initEgymDiagramData(UPDATE_ALL_BAR, workoutChartsFragment.egymDiagramBarsView, w, false);
            }

            //設定下控一開始的速度
            if (isTreadmill) {
                //   Log.d("UART_CONSOLE", "initTimer 第一次啟動: setDevSpeedAndIncline" );
                u.setDevSpeedAndIncline();
            } else {
                u.setDevWorkload(w.currentLevel.get());
            }

            if (rxWorkOutTimer != null) {
                rxWorkOutTimer.cancel();
                rxWorkOutTimer = null;
            }

            rxWorkOutTimer = new RxTimer();
            rxWorkOutTimer.interval(1000, t -> workoutRunning());
            //        rxWorkOutTimer.interval(100, t -> workoutRunning());
        } else {
            new RxTimer().timer(100, number -> checkInit());
        }
    }

    /**
     * @param isResume 是否暫停後重啟
     */
    private void initTimer(boolean isResume) {

        isFinishWorkout = false;

        w.isWarmUpIng.set(false);
        //Treadmill在 ReadyStartPopup、resumeManual 設定 RUNNING
        if (!isTreadmill) {
            u.emsWorkoutStart();
        }

        isWorkoutTimerRunning = true;
        if (!isResume) {
            //第一次啟動
            checkInit();

        } else {

            //暫停重啟
            //track重新啟動
            workoutTrackFragment.startAnimation();
            //     isKeyPauseWorkout = false;

            if (!isTreadmill) {
                //回復之前的Level
                u.setDevWorkload(w.currentLevel.get());
            }
        }
    }


    /**
     * Workout執行中 1秒呼叫一次
     */
    private void workoutRunning() {

        //安全鎖判斷
        //   if (!w.isSafeKey.get()) return;

        if (isWorkoutTimerRunning) { //是否可計時

            if (w.selProgram != EGYM) {
                //elapsedTime 超過 100分鐘 從0開始
                if (w.elapsedTime.get() >= (60 * 99)) {
                    w.elapsedTime.set(0);
                }
                if (w.elapsedTimeShow.get() >= (60 * 99)) {
                    w.elapsedTimeShow.set(0);
                }
            }

            //POWER要每秒送
            if (w.selProgram == WATTS) {
                //   if (w.selProgram == WATTS || w.selProgram == FITNESS_TEST) {
                //  w.currentLevel.set(calc.getWattLevel(w.constantPowerW.get(), w.currentRpm.get()));
                updateSpeedOrLevelNumWATT(calc.getWattLevel(w.constantPowerW.get(), w.currentRpm.get()), true);
            }


            //總時間
            w.totalElapsedTime.set(w.totalElapsedTime.get() + 1);
            w.totalElapsedTimeShow.set(w.totalElapsedTimeShow.get() + 1);

            //經過的秒數 上數
            w.elapsedTime.set(w.elapsedTime.get() + 1);
            w.elapsedTimeShow.set(w.elapsedTimeShow.get() + 1);

            //剩餘的秒數 下數 ,UNLIMITED 上數
            if (w.selWorkoutTime.get() != UNLIMITED) w.remainingTime.set(w.remainingTime.get() - 1);
            if (w.selWorkoutTime.get() != UNLIMITED)
                w.remainingTimeShow.set(w.remainingTimeShow.get() - 1);

            //根據上數或下數 顯示不同的計時方式
//            timeCount = w.selWorkoutTime.get() == UNLIMITED ? w.elapsedTime.get() : w.remainingTime.get();
            timeCount = w.selWorkoutTime.get() == UNLIMITED ? w.elapsedTimeShow.get() : w.remainingTimeShow.get();

            //更新上方欄位顯示的時間
            w.currentShowTimeText.set(formatSecToM(timeCount));

            //更新圖表資料
            workoutChartsFragment.segmentFlow(w.elapsedTime.get());

            //workout資料計算
            calcWorkoutData();

//            //更新STATS的資料
//            workoutStatsFragment.updateStatsData();

            //換背景圖
            showBackground();

            double trackSpeed = UNIT_E == DeviceIntDef.METRIC ? calc.getDistanceAccumulate() * 1000 : calc.getDistanceAccumulate() * 5280;
            //更新Track速度
            workoutTrackFragment.updateLapSpeed(trackSpeed, UNIT_E);


            //傳送FTMS資料
            updateFtmsData(); //warmup 到 workout時會少顯示一秒，因為前面先加1秒了
            setWorkoutCloudRawData();
            sendCsafeData();
            ((MainActivity) requireActivity()).sendGymAppData();

            //更新Egym資料
            setEgymCloudIntervalData();
            //更新STATS的資料
            workoutStatsFragment.updateStatsData();

            //調整Runtime數值
            if (isWorkoutTimerRunning) iPrograms.runTime();

            //目標結束條件
            if (isWorkoutTimerRunning) iPrograms.target();

            //中斷條件
            if (isWorkoutTimerRunning) iPrograms.out();


//            if (w.isGarminConnected.get()) {
//                workoutGarminFragment.setGarminData();
//            }


//            if (useTimeLimit != -1) {//有使用TimeLimit
//                if (timeCount >= useTimeLimit) {
//                    Toast.makeText(parent, "TIME OUT", Toast.LENGTH_LONG).show();
//                    finishWorkout(false);
//                }
//            }

            //useTimeLimit
            //  Log.d("useTimeLimit", "workoutRunning: timeCount:" + timeCount +", useTimeLimit:"+ useTimeLimit);


            //當RPM=0，持續超過設定的 Autologout 時間 (例如：3 分鐘)，進 PAUSE 並倒數Autologout 設定的時間 (例如：3 分鐘)，
            // 時間到後進到Summary，30秒後，logout (若有第三方APP播放中，一併結束並登出)
            if (!isTreadmill) {
                if (deviceSettingViewModel.autoPause.getValue() == OFF) return;

                if (w.currentRpm.get() <= 0) {
                    rpmCheck++;
                    //    Log.d("RPM_CHECK", "workoutRunning: " + w.currentRpm.get() + ", " + rpmCheck + ", 設定的秒數:" + deviceSettingViewModel.pauseAfter.get());
                } else {
                    rpmCheck = 0;
                    //    Log.d("RPM_CHECK", "workoutRunning: " + w.currentRpm.get() + ", " + rpmCheck);
                }
                if (rpmCheck >= deviceSettingViewModel.pauseAfter.get()) {

                    //    Log.d("RPM_CHECK", "PAUSE: " + rpmCheck);

                    getBinding().btnWorkoutStop.callOnClick();

                    rpmCheck = 0;
                }
            }


            //   checkLimit();


        }
    }

    double tempDistance = 0;

    //計算 AvgResistance
    int eTotalResistanceSet;
    int eTotalHrSet;
    int eTotalRpmSet;
    int eTotalInclineSet;
    int eTotalSpeedSet;

    //存太多會不能上傳和讀取
    private void setEgymCloudIntervalData() {

        if (deviceSettingViewModel.consoleSystem.get() == CONSOLE_SYSTEM_SPIRIT) return;

        //不是Egym Program, 到Summary才存
        if (w.selProgram != EGYM) return;

//        //每個SET已跑的秒數
        w.egymTimePerSets.set(w.egymTimePerSets.get() + 1);

        //每個SET已跑的距離
        w.egymIntervalDistance.set(calc.getDistanceAccumulate() - tempDistance);


        if (w.egymCurrentSet.get() < 0 || w.egymCurrentSet.get() >= egymDataViewModel.durationTimesListKnowZero.size()) {
            Log.e(EgymUtil.TAG, "realInterval 超出範圍: " + w.egymCurrentSet.get() + ", size: " + egymDataViewModel.durationTimesListKnowZero.size());
            return; // 直接返回，避免崩潰
        }


        // egymTimePerSets 為0 會出問題
        //⚡️計算 AvgResistance
        eTotalResistanceSet += w.currentLevel.get(); //當前Set,每秒的Resistance總數
        w.avgLevel.set((float) eTotalResistanceSet / w.egymTimePerSets.get());  //當前set總Resistance,除以當前 set秒數 = 此set平均 Resistance
        //     Log.d(EgymUtil.TAG, "EGYM計算 AvgResistance: mTotalResistance:" + eTotalResistanceSet + ", mAvgResistance:" + w.avgLevel.get());

        //⚡️計算 Avg RPM & SPM
        eTotalRpmSet += w.currentRpm.get(); //當前Set,每秒的RPM總數
        w.avgRpm.set((float) eTotalRpmSet / w.egymTimePerSets.get());  //當前set總Resistance,除以當前 set秒數 = 此set平均 Resistance


        //⚡️計算 Avg Hr
        if (w.currentHeartRate.get() > 40 && w.currentHeartRate.get() < 240) {
            eTotalHrSet += w.currentHeartRate.get();
            w.avgHeartRate.set((float) eTotalHrSet / w.egymTimePerSets.get());
            //       Log.d(EgymUtil.TAG, "EGYM計算 avgHeartRate: eTotalHrSet:" + eTotalHrSet +", mAvgHr:"+ w.avgHeartRate.get());
        }

        //⚡️計算AvgSpeed
        eTotalSpeedSet += Math.round(w.currentSpeed.get());
        w.avgSpeed.set((float) eTotalSpeedSet / w.egymTimePerSets.get());

        //⚡️計算AvgIncline
        eTotalInclineSet += Math.round(w.currentInclineValue.get());
        w.avgIncline.set((float) eTotalInclineSet / w.egymTimePerSets.get());



        // ⭐️ 時間設定為 00:00(-99)，就先給30分鐘， TargetDistance到了，就換下一個 SET
        if ((egymDataViewModel.durationTimesListKnowZero.get(w.egymCurrentSet.get()) == SYMBOL_DURATION) && !Objects.equals(w.egymTargetDistance.get(), E_BLANK)) {
            String targetDistanceStr = w.egymTargetDistance.get();
            if (isNumeric(targetDistanceStr)) {
                double targetDistance = Double.parseDouble(targetDistanceStr);
           //     Log.d(EgymUtil.TAG, "此set已跑距離: " + w.egymIntervalDistance.get() + ", 目標距離" + targetDistance);
                if (w.egymIntervalDistance.get() >= targetDistance) {
                    Log.d(EgymUtil.TAG, "⭐已到達目標");

                    //此set原始秒數 - 此set已跑的秒數 = 還沒跑的秒數 , 100 - 60 = 40,
                    int xxTime = egymDataViewModel.durationTimesList.get(w.egymCurrentSet.get()) - w.egymTimePerSets.get();
                    Log.d(EgymUtil.TAG, "此set原始秒數: " + egymDataViewModel.durationTimesList.get(w.egymCurrentSet.get()) + ", 此set已跑的秒數:" + w.egymTimePerSets.get() + " , 還沒跑的秒數:" + xxTime);

                    w.elapsedTime.set(w.elapsedTime.get() + xxTime);
                    w.remainingTime.set(w.remainingTime.get() - xxTime);
                    w.remainingTimeShow.set(w.remainingTimeShow.get() - xxTime);
                    w.currentShowTimeText.set(formatSecToM(w.remainingTime.get()));

                    //往下跑，到下一個SET


               //     Log.d(EgymUtil.TAG, "新時間: " + w.elapsedTime.get() + ", " + formatSecToM(w.remainingTime.get()));
              //      Log.d(EgymUtil.TAG, "新剩餘時間: " + w.remainingTime.get() + ", " + w.remainingTimeShow.get());
//                    Log.d(EgymUtil.TAG, "每個SET的秒數: " + egymDataViewModel.durationRealTimesList.toString());
//                    Log.d(EgymUtil.TAG, "當前Set 秒數: " + egymDataViewModel.durationRealTimesList.get(w.egymCurrentSet.get()));
//                    Log.d(EgymUtil.TAG, "當前總秒數: " + w.elapsedTime.get());

                    Log.d(EgymUtil.TAG, "====================================");


                }
            }
        }


    //    Log.d(EgymUtil.TAG, "@#############此Set 秒數: " + w.egymTimePerSets.get());

        //每個Set在第幾秒 [120, 480]  最後一個+2秒, 因為要到summary才算
        //‼️時間到了，要換下一個SET
        if (!egymDataViewModel.durationRealTimesList.contains(w.elapsedTime.get())) return;
        Log.d(EgymUtil.TAG, "開始進入下一個SET, 計算數值 " +  " Set秒數: " + w.egymTimePerSets.get() +"秒,   當前Set:" + (w.egymCurrentSet.get() + 1) );

        tempDistance = calc.getDistanceAccumulate();

        //儲存要給Cloud的資料
        EgymUtil.getInstance().saveInterval(w, egymDataViewModel);


        //當前Set(Interval) 加 1
        w.egymCurrentSet.set(w.egymCurrentSet.get() + 1);

        workoutChartsFragment.getBinding().egymDiagramBarsView.setWhiteLinePosition(w.egymCurrentSet.get());

        //進入下一個set時，調整 speed incline level
        EgymUtil.getInstance().updateFlowData(this, w, u);

        //通知 StatsFragment 更新數據
        workoutStatsFragment.updateEgymStats(egymDataViewModel);
//
//        //通知 StatsFragment 更新數據
//        LiveEventBus.get(EGYM_SETS_DATA_UPDATE_REAL, Integer.class).postDelay(0, 0); // > WorkoutStatsFragment

        //每個 set 當前的時間歸0
        w.egymTimePerSets.set(0);
        eTotalResistanceSet = 0;
        eTotalHrSet = 0;
        eTotalRpmSet = 0;
        eTotalInclineSet = 0;
        eTotalSpeedSet = 0;
    }

    int rpmCheck = 0;

    private void sendCsafeData() {
        try {
            //    Log.d("CSAFE", "protocol : " + deviceSettingViewModel.protocol.getValue());
            if (deviceSettingViewModel.protocol.getValue() == PROTOCOL_CSAFE && deviceCsafe != null) {
//                Log.d("CSAFE", "sendData ");
                deviceCsafe.setCalories((int) w.currentCalories.get());
                deviceCsafe.setCadence(0);
                deviceCsafe.setCurrentHeartRate(w.currentHeartRate.get());
                deviceCsafe.setGrade(w.currentInclineLevel.get());
//                deviceCsafe.setGradeUnit((int) w.currentCalories.get());
                deviceCsafe.setHeartRateAverage((int) w.avgHeartRate.get());
                deviceCsafe.setHorizontalDistance((int) w.currentDistance.get());
//                deviceCsafe.setHorizontalDistanceUnit((int) w.currentCalories.get());
                deviceCsafe.setMets((int) w.currentMets.get());
                deviceCsafe.setPace((int) w.currentPace.get());
                deviceCsafe.setPower(w.currentPower.get());
//                deviceCsafe.setProgram((int) w.currentCalories.get());
                deviceCsafe.setSpeed((int) w.currentSpeed.get());


                int timeHour = (int) (TimeUnit.SECONDS.toHours(w.totalElapsedTime.get()));
                int timeMin = (int) (TimeUnit.SECONDS.toMinutes(w.totalElapsedTime.get()) - (TimeUnit.SECONDS.toHours(w.totalElapsedTime.get()) * 60));
                int timeSec = (int) (TimeUnit.SECONDS.toSeconds(w.totalElapsedTime.get()) - (TimeUnit.SECONDS.toMinutes(w.totalElapsedTime.get()) * 60));

                deviceCsafe.setWorkoutTime(timeHour, timeMin, timeSec);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 每三秒產生 一筆workoutRawData，不足三秒 當成三秒
     */
    private void setWorkoutCloudRawData() {
        if (userProfileViewModel.userType.get() == USER_TYPE_GUEST) return;
        if (w.totalElapsedTime.get() % 3 == 0) {
            UploadWorkoutParam.FormDTO.RawDataListDTO rawDataListDTO = new UploadWorkoutParam.FormDTO.RawDataListDTO();

            // rawDataListDTO.setTotalWorkoutTime(w.totalElapsedTime.get());
//            rawDataListDTO.setTotalTimeLeft(w.remainingTime.get());
            rawDataListDTO.setTotalWorkoutTime(w.totalElapsedTimeShow.get());
            rawDataListDTO.setTotalTimeLeft(w.remainingTimeShow.get());

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
            ((MainActivity) requireActivity()).rawDataListDTOList.add(rawDataListDTO);
        }
    }

    //@FTMS
    private void updateFtmsData() {

        if (!appStatusViewModel.isFtmsConnected.get()) return;

        //FTMS 只有公制

        // WORKOUT時更新機器的各項數值
        Map<DeviceGEM.WORKOUT_DATA_FIELD, Integer> workoutData = new HashMap<>();

        int hDistance = (int) ((UNIT_E == IMPERIAL ? mi2km(w.currentDistance.get()) * 1000 : w.currentDistance.get() * 1000));
//        int vDistance = (int) ((UNIT_E == IMPERIAL ? mi2km(w.currentElevationGain.get()) / 10 : w.currentElevationGain.get()) / 10);
//        int vDistance = (int) ((UNIT_E == IMPERIAL ? mi2km(w.currentElevationGain.get()) * 10 : w.currentElevationGain.get() * 10));
        int vDistance = (int) ((UNIT_E == IMPERIAL ? (w.currentElevationGain.get() * 0.3) * 10 : w.currentElevationGain.get() * 10));
        int speed = (int) ((UNIT_E == IMPERIAL ? mi2km(w.currentSpeed.get() * 100) : w.currentSpeed.get() * 100)); //0.5 > 5
        int avgSpeed = (int) ((UNIT_E == IMPERIAL ? mi2km(w.avgSpeed.get() * 1000000) : w.avgSpeed.get() * 1000000)); //0.5 > 5

        switch (ftmsType) {
            case FTMS_TYPE_TREADMILL:
                // 單位：0.01km/h, 實際的速度如果是 4km/h, 更新時要乘上100，speed = 4 * 100
                workoutData.put(DeviceGEM.WORKOUT_DATA_FIELD.SPEED, speed);
                workoutData.put(DeviceGEM.WORKOUT_DATA_FIELD.CUMULATIVE_VERTICAL_DISTANCE, vDistance);
                // 單位：inclination: 0.1%, 實際揚升值如果是15，更新時要乘上10, inclination = 15 * 10
                workoutData.put(DeviceGEM.WORKOUT_DATA_FIELD.GRADE, (int) (w.currentInclineValue.get() * 10));
                workoutData.put(DeviceGEM.WORKOUT_DATA_FIELD.AVERAGE_POWER, (int) w.avgPower.get());
                workoutData.put(DeviceGEM.WORKOUT_DATA_FIELD.POWER, w.currentPower.get());
                workoutData.put(DeviceGEM.WORKOUT_DATA_FIELD.AVERAGE_SPEED, avgSpeed);
                break;
            case FTMS_TYPE_CROSS_TRAINER:
                workoutData.put(DeviceGEM.WORKOUT_DATA_FIELD.POWER, w.currentPower.get());
                workoutData.put(DeviceGEM.WORKOUT_DATA_FIELD.SPEED, speed);
                workoutData.put(DeviceGEM.WORKOUT_DATA_FIELD.CADENCE, w.currentRpm.get() * 2 * 10);
                workoutData.put(DeviceGEM.WORKOUT_DATA_FIELD.AVERAGE_CADENE, (int) (w.avgRpm.get() * 20));
                workoutData.put(DeviceGEM.WORKOUT_DATA_FIELD.AVERAGE_POWER, (int) w.avgPower.get());
                workoutData.put(DeviceGEM.WORKOUT_DATA_FIELD.CUMULATIVE_VERTICAL_DISTANCE, 0);
                workoutData.put(DeviceGEM.WORKOUT_DATA_FIELD.SIGNED_LEVEL, w.currentLevel.get());
                workoutData.put(DeviceGEM.WORKOUT_DATA_FIELD.GRADE, 0);
                workoutData.put(DeviceGEM.WORKOUT_DATA_FIELD.AVERAGE_SPEED, avgSpeed);
                workoutData.put(DeviceGEM.WORKOUT_DATA_FIELD.CUMULATIVE_MOVEMENTS_COUNT, w.rpmCounter.get());
                break;
            case FTMS_TYPE_INDOOR_BIKE:
                workoutData.put(DeviceGEM.WORKOUT_DATA_FIELD.SPEED, speed);
//                workoutData.put(DeviceGEM.WORKOUT_DATA_FIELD.CADENCE, w.currentRpm.get() * 2 * 10);
                workoutData.put(DeviceGEM.WORKOUT_DATA_FIELD.CADENCE, w.currentRpm.get() * 10);
                workoutData.put(DeviceGEM.WORKOUT_DATA_FIELD.POWER, w.currentPower.get());
                workoutData.put(DeviceGEM.WORKOUT_DATA_FIELD.AVERAGE_POWER, (int) w.avgPower.get());
                workoutData.put(DeviceGEM.WORKOUT_DATA_FIELD.AVERAGE_CADENE, (int) (w.avgRpm.get() * 10));
                workoutData.put(DeviceGEM.WORKOUT_DATA_FIELD.AVERAGE_SPEED, avgSpeed);
//                workoutData.put(DeviceGEM.WORKOUT_DATA_FIELD.SIGNED_LEVEL, (int) (w.currentLevel.get() * 0.1));
                workoutData.put(DeviceGEM.WORKOUT_DATA_FIELD.SIGNED_LEVEL, (int) (w.currentLevel.get() * 10));
                break;
        }
        workoutData.put(DeviceGEM.WORKOUT_DATA_FIELD.CUMULATIVE_HORIZONTAL_DISTANCE, hDistance);

//        if (w.isWarmUpIng.get()) {
//            workoutData.put(DeviceGEM.WORKOUT_DATA_FIELD.ELAPSED_WORKOUT_TIME, (int) (w.warmUpTime.get() - coolDownAndWarmUpTimeSecond));
//            workoutData.put(DeviceGEM.WORKOUT_DATA_FIELD.REMAINING_WORKOUT_TIME, (int) coolDownAndWarmUpTimeSecond);
//        } else if (w.isCoolDowning.get()) {
//            workoutData.put(DeviceGEM.WORKOUT_DATA_FIELD.ELAPSED_WORKOUT_TIME, (int) (w.coolDownTime.get() - coolDownAndWarmUpTimeSecond));//
//            workoutData.put(DeviceGEM.WORKOUT_DATA_FIELD.REMAINING_WORKOUT_TIME, (int) coolDownAndWarmUpTimeSecond);
//        } else {
//            workoutData.put(DeviceGEM.WORKOUT_DATA_FIELD.ELAPSED_WORKOUT_TIME, w.elapsedTime.get()); //過去時間，從0開始數  根據這裡發送運動紀錄，時間差30秒以上或時間倒退就會送一筆運動紀錄
//            workoutData.put(DeviceGEM.WORKOUT_DATA_FIELD.REMAINING_WORKOUT_TIME, w.remainingTime.get());
//        }

        workoutData.put(DeviceGEM.WORKOUT_DATA_FIELD.ELAPSED_WORKOUT_TIME, w.elapsedTimeShow.get()); //過去時間，從0開始數  根據這裡發送運動紀錄，時間差30秒以上或時間倒退就會送一筆運動紀錄
        workoutData.put(DeviceGEM.WORKOUT_DATA_FIELD.REMAINING_WORKOUT_TIME, w.remainingTimeShow.get());


        workoutData.put(DeviceGEM.WORKOUT_DATA_FIELD.HEART_RATE, w.currentHeartRate.get());
        workoutData.put(DeviceGEM.WORKOUT_DATA_FIELD.CUMULATIVE_ENERGY, (int) w.currentCalories.get());


        App.getDeviceGEM().gymConnectMessageUpdateWorkoutData(workoutData);

        //   Log.d("WWWWEEEQQEE", "FTMS_workoutData: " + workoutData);
    }

    int hrCount = 0;
    private int mAvgHr;
    private int mTotalHR;

    private void calcWorkoutData() {

        if (w.selProgram != EGYM) {
            //非Egym 再算 計算hr
            if (w.currentHeartRate.get() > 40 && w.currentHeartRate.get() < 240) {
                mTotalHR += w.currentHeartRate.get();
                hrCount++;
                mAvgHr = mTotalHR / hrCount;
                w.avgHeartRate.set(mAvgHr);
            }

            //非Egym 再算
            w.avgRpm.set(calc.getRpmAverage());
            w.avgIncline.set((float) calc.getInclineAverage());
            w.avgSpeed.set(calc.getSpeedHourAverage());
        }

        if (isTreadmill) {
            calc.setData((double) w.currentSpeed.get(), (double) w.currentInclineValue.get(),0);
        } else {
            // calc.setData(w.currentRpm.get(), 0, 0, w.pwmLevelDA.get());
            calc.setData(w.currentRpm.get(), 0, 0, w.currentLevel.get());
        }
        w.currentPace.set(calc.getPace());


        //Program距離計算規則
        //  programsWork.calcDistance(calc.getDistanceAccumulate());

        //  w.currentDistance.set(d);

        double d = w.targetDistance.get() - calc.getDistanceAccumulate();
        d = d >= 0 ? d : 0;
        w.distanceLeft.set(d); //剩餘距離

        w.currentDistance.set(calc.getDistanceAccumulate());//目前已跑距離

        //todo APPLE WATCH
//        if (w.getAppleWatchCalories() > 0 && w.isAppleWatchEnabled.get()) {
        if (w.isAppleWatchEnabled.get()) {
            w.currentCalories.set(w.getAppleWatchCalories() >= 9999 ? 9999 : w.getAppleWatchCalories());
        } else {
            w.currentCalories.set(calc.getKcalAccumulate() >= 9999 ? 9999 : calc.getKcalAccumulate());
        }

//        w.avgPace.set(calc.getPaceAverage());
        w.currentPower.set(calc.getWatt());


        w.currentMets.set(calc.getMets());
        w.currentElevationGain.set(calc.getAltitudeAccumulate());


        w.avgMet.set(calc.getMetsAverage());
        w.totalCalories.set(calc.getKcalAccumulate());

//        w.avgIncline.set((float) calc.getInclineAverage());

        //    Log.d("AAAGGGGGG", "calcWorkoutData: " + calc.getInclineAverage());


        //Treadmill 是手動調整 currentSpeed, Bike 靠計算
        if (!isTreadmill) {
            //   Log.d("AAAGGGGGG", "calcWorkoutData: " + calc.getSpeedHourAverage());
            w.currentSpeed.set((float) calc.getSpeedHour());
            //     w.currentPower.set(calc.getWatt()); //根據rpm及level 查表
        }

//        w.avgSpeed.set(calc.getSpeedHourAverage());
        w.avgPower.set(calc.getWattAverage());
    }

    private void initProgramData() {

        switch (w.selProgram) {
            case ARMY:
                iPrograms = new Army(w, this);
                break;
            case NAVY:
                iPrograms = new Navy(w, this);
                break;
            case AIR_FORCE:
                iPrograms = new AirForce(w, this);
                break;
            case PEB:
                iPrograms = new Peb(w, this);
                break;
            case COAST_GUARD:
                iPrograms = new CoastGuard(w, this);
                break;
            case MARINE_CORPS:
                iPrograms = new MarineCorps(w, this);
                break;
            case GERKIN://到達THR 或 完成所有Stage
                iPrograms = new Gerkin(w, this, u);
                break;
            case WFI: //到達THR 或 到達目標時間 18分鐘
                iPrograms = new Wfi(w, this, u);
                break;
            case CTT_PERFORMANCE://目標時間，或主動停止
                iPrograms = new CttPerformance(w, this, u);
                break;
            case CTT_PREDICTION://目標心跳，或完成 6 Level
                iPrograms = new CttPrediction(w, this, u);
                break;
            case RUN_5K:
                iPrograms = new Run5K(w, this);
                break;
            case RUN_10K:
                iPrograms = new Run10K(w, this);
                break;
            case HEART_RATE:
                if (isTreadmill) {
                    iPrograms = new HeartRateTreadmill(w, this);
                } else {
                    iPrograms = new HeartRate(w, this);
                }
                break;
            case HIIT:
                iPrograms = new Hiit(w, this, u);
                break;
            case WATTS:
                iPrograms = new Watt(w, this, calc);
                break;
            case FITNESS_TEST:
                iPrograms = new FitnessTest(w, this, calc);
                break;
            default:
                iPrograms = new CommonPrograms(w, this, workoutChartsFragment, u, egymDataViewModel);
        }

        iPrograms.init();
    }

    /**
     * 到達目標距離
     */
    public void targetDistance(float targetDistance) {

//        if (w.currentDistance.get() >= targetDistance) {
//            if (!isWorkoutTimerRunning) return;
//            isWorkoutTimerRunning = false;
//            new RxTimer().timer(500, number -> initCoolDown());
//        }
        //  Log.d("PPEPEPEPFF", "targetDistance: " + w.currentDistance.get());

//        if (w.currentDistance.get() <= 0) {
//            if (!isWorkoutTimerRunning) return;
//            isWorkoutTimerRunning = false;
//            new RxTimer().timer(500, number -> initCoolDown());
//        }

        if (w.distanceLeft.get() <= 0) {

            isWorkoutTimerRunning = false;
            new RxTimer().timer(300, number -> initCoolDown());

            //   w.setWorkoutDone(true);
        }
    }

    /**
     * 到達目標時間
     */
    public void targetTime() {
        if (w.remainingTime.get() <= 0) {
            Log.d("GGGEEE", "222222initWorkoutData: " + w.remainingTime.get());

            isWorkoutTimerRunning = false;
            new RxTimer().timer(300, number -> initCoolDown());

        }
    }

    /**
     * 到達目標心跳
     */
    public void targetHeartRate() {

        Log.d("WORK_OUT", "目前心跳：" + w.currentHeartRate.get() + ",目標心跳：" + w.selTargetHrBpm.get());

        if (w.currentHeartRate.get() >= w.selTargetHrBpm.get()) {

            showWarring(WARRING_HR_REACHED_TARGET);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        workoutStatsFragment = null;
        workoutChartsFragment = null;
        workoutTrackFragment = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        closeWindow();

        if (coolDownAndWarmUpTimer != null) {
            coolDownAndWarmUpTimer.cancel();
            coolDownAndWarmUpTimer = null;
        }

        if (rxWorkOutTimer != null) {
            rxWorkOutTimer.cancel();
            rxWorkOutTimer = null;
        }
        if (startCheckTimer != null) {
            startCheckTimer.cancel();
            startCheckTimer = null;
        }

        if (iPrograms != null) {
            iPrograms.cancelTimer();
            iPrograms = null;
        }

        m_currentFragment = null;

//        if (longTimer != null) {
//            longTimer.cancel();
//            longTimer = null;
//        }
    }


    /**
     * 調整目標心跳
     *
     * @param isPlus 增加 / 減少
     */
    public boolean updateTargetHeartRate(boolean isPlus) {

        //  if (hrStatusBean.getMode() != MAINTAINING_MODE) return;
        int targetHR = w.selTargetHrBpm.get();
        if (!isPlus && (targetHR <= 72)) return false;
        if (isPlus && (targetHR >= 200)) return false;

//        if (!parent.isLongClickIng) { //不是long click
//            getInstance().commandSetBuzzer(Device.BEEP.SHORT, 1);
//        } else {
//            parent.isLongBeep = true;
//        }

        w.selTargetHrBpm.set(isPlus ? ++targetHR : --targetHR);

        workoutChartsFragment.hrNotify(isPlus);

        return true;
    }


    PopupWindow warringWindow;

    public void showWarring(int type) {

//        //停止計時
//        if (!isWorkoutTimerRunning) return;
        isWorkoutTimerRunning = false;

        if (warringWindow != null) {
            warringWindow.dismiss();
            warringWindow = null;
        }

        if (!isAdded()) return;

        if (type == SELECT_HR_EXCEEDS_AGE) {
            try {
                warringWindow = new ModalHrMaxWindow(requireActivity(), w);
                warringWindow.showAtLocation(requireActivity().getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);
                ((ModalHrMaxWindow) warringWindow).setOnCustomDismissListener(new BasePopupWindow.OnCustomDismissListener() {
                    @Override
                    public void onStartDismiss(MsgEvent value) {
                        isWorkoutTimerRunning = true;//恢復計時
                    }

                    @Override
                    public void onDismiss() {
                        warringWindow = null;
                    }
                });
            } catch (Exception e) {
                isWorkoutTimerRunning = true;//恢復計時
                e.printStackTrace();
            }

            return;
        }

        switch (type) {
            case WARRING_NO_HR:
            case WARRING_HR_TOO_HIGH:
            case WARRING_NO_RPM:
            case WARRING_HR_REACHED_TARGET:
            case INVALID_TEST_RPM_OUT_OF_RANGE:
            case INVALID_TEST_HR_OUT_OF_RANGE:
                try {
                    warringWindow = new WarringNoButtonWindow(requireActivity(), type, w);
                    warringWindow.showAtLocation(requireActivity().getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);
                    ((WarringNoButtonWindow) warringWindow).setOnCustomDismissListener(new BasePopupWindow.OnCustomDismissListener() {
                        @Override
                        public void onStartDismiss(MsgEvent value) {
                        }

                        @Override
                        public void onDismiss() {
                            warringWindow = null;
                            if (type == WARRING_HR_REACHED_TARGET) {
                                //成功 到達目標心跳，進行CoolDown
                                initCoolDown();
                            } else {
                                //失敗直接離開
                                finishWorkout(false);
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

//    //只提示
//    public void showWarring2(int type) {
//
//        if (warringWindow != null) {
//            warringWindow.dismiss();
//            warringWindow = null;
//        }
//
//        if (!isAdded()) return;
//
//        switch (type) {
//            case WARRING_NO_HR:
//            case WARRING_NO_RPM:
//            case INVALID_TEST_RPM_OUT_OF_RANGE:
//            case INVALID_TEST_HR_OUT_OF_RANGE:
//                try {
//                    warringWindow = new WarringNoButtonWindow(requireActivity(), type, w);
//                    warringWindow.showAtLocation(requireActivity().getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);
//                    ((WarringNoButtonWindow) warringWindow).setOnCustomDismissListener(new BasePopupWindow.OnCustomDismissListener() {
//                        @Override
//                        public void onStartDismiss(MsgEvent value) {
//                        }
//
//                        @Override
//                        public void onDismiss() {
//                            warringWindow = null;
//                        }
//                    });
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                break;
//        }
//
//        new RxTimer().timer(2000, number -> {
//            try {
//                if (warringWindow != null) {
//                    warringWindow.dismiss();
//                    warringWindow = null;
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
//    }

    private WarringCountDownWindow warringCountDownWindow;

    public void showRpmError(boolean isShow) {
        if (isShow) {
            if (warringCountDownWindow != null && warringCountDownWindow.isShowing()) return;
            if (warringNoHrCountDownWindow != null && warringNoHrCountDownWindow.isShowing())
                return;

            if (!isAdded()) return;

            try {
                warringCountDownWindow = new WarringCountDownWindow(requireActivity(), 0, w);
                warringCountDownWindow.showAtLocation(requireActivity().getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);
                warringCountDownWindow.setOnCustomDismissListener(new BasePopupWindow.OnCustomDismissListener() {
                    @Override
                    public void onStartDismiss(MsgEvent value) {
                        if (value != null) {
                            finishWorkout(false);
                        }
                    }

                    @Override
                    public void onDismiss() {
                        warringCountDownWindow = null;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (warringCountDownWindow != null) {
                warringCountDownWindow.dismiss();
                warringCountDownWindow = null;
            }
        }
    }

    ModalHrUpdateWindow hrUpdateWindow;
    ModalHrUpdateWindow2 hrUpdateWindow2;

    /**
     * 提示HR改變的視窗
     *
     * @param isHigh 是否增加
     * @param num    數值
     */
    public void showHrHint(boolean isHigh, int num) {

        if (warringNoHrCountDownWindow != null && warringNoHrCountDownWindow.isShowing()) return;

        if (hrUpdateWindow != null) {
            hrUpdateWindow.dismiss();
            hrUpdateWindow = null;
        }

        hrUpdateWindow = new ModalHrUpdateWindow(requireActivity(), isHigh, num);
        hrUpdateWindow.showAtLocation(requireActivity().getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);
        hrUpdateWindow.setOnCustomDismissListener(new BasePopupWindow.OnCustomDismissListener() {
            @Override
            public void onStartDismiss(MsgEvent value) {
                if (value != null) {
                    updateSpeedOrLevelNum(isHigh ? -1 : 1, false);
                }
            }

            @Override
            public void onDismiss() {
                //   hrUpdateWindow = null; 不能加
            }
        });
    }

    //incline
    public void showHrHint2(boolean isPlus, int num, int type) {

        if (warringNoHrCountDownWindow != null && warringNoHrCountDownWindow.isShowing()) return;

        if (hrUpdateWindow2 != null) {
            hrUpdateWindow2.dismiss();
            hrUpdateWindow2 = null;
        }

        if (hrUpdateWindow != null) {
            hrUpdateWindow.dismiss();
            hrUpdateWindow = null;
            Log.d("HEART_RATE_PROGRAM", "dismiss speed: ");
        }

        hrUpdateWindow2 = new ModalHrUpdateWindow2(requireActivity(), isPlus, num);
        hrUpdateWindow2.showAtLocation(requireActivity().getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);
    }

    public void setMaxInclineMax() {
        w.currentMaxInclineValue.set(getInclineValue(MAX_INC_MAX)); // 15 最大Incline
        w.currentMaxInclineLevel.set(MAX_INC_MAX); //30
    }


    public void ftmsResponse(DeviceGEM.EQUIPMENT_CONTROL_OPERATION controlOperation, DeviceGEM.EQUIPMENT_CONTROL_RESPONSE equipmentControlResponse) {
        if (!appStatusViewModel.isFtmsConnected.get()) return;
        //  Map<DeviceGEM.EQUIPMENT_CONTROL_PARAMETER, Integer> parameters = new HashMap<>();
        // parameters.put(TARGET_SPEED,1);
        App.getDeviceGEM().gymConnectMessageNotifyEquipmentControlMessageReceived(controlOperation, equipmentControlResponse, MainActivity.controlParameterMap);
        //   Log.d("GEM3", "ftmsResponse: " + MainActivity.controlParameterMap);
        MainActivity.controlParameterMap = new HashMap<>();
    }

    public void warmUpSpeedUpdate(int num) {

        if (!w.isSafeKey.get()) return;

        num = getSpecifyValue(w.currentSpeedLevel.get(), num, true);

        int newCurrentSpeedLevel = w.currentSpeedLevel.get() + num;

        ftmsResponse(SET_TARGET_SPEED, SUCCESS);

        w.currentSpeedLevel.set(newCurrentSpeedLevel);
        w.currentSpeed.set(getSpeedValue(w.currentSpeedLevel.get()));
    }

    public void warmUpInclineUpdate(int num) {

        if (!w.isSafeKey.get()) return;

        num = getSpecifyValue(w.currentInclineLevel.get(), num, true);

        int newCurrentIncline = w.currentInclineLevel.get() + num;

//        //ftms 傳同階回成功
//        if (currentIncline == newCurrentIncline) {
//            parentFragment.ftmsResponse(SET_TARGET_INCLINATION, SUCCESS);
//            return false;
//        }

        ftmsResponse(SET_TARGET_INCLINATION, SUCCESS);

        w.currentInclineLevel.set(newCurrentIncline);
        w.currentInclineValue.set(getInclineValue(w.currentInclineLevel.get()));
//        u.setDevSpeedAndIncline();
    }

    public void warmUpSpeedAndIncline(int s, int i) {

        if (!w.isSafeKey.get()) return;

        s = getSpecifyValue(isTreadmill ? w.currentSpeedLevel.get() : w.currentLevel.get(), s, true);
        int newCurrentSpeedLevel = (isTreadmill ? w.currentSpeedLevel.get() : w.currentLevel.get()) + s;
        ftmsResponse(SET_TARGET_SPEED, SUCCESS);

        i = getSpecifyValue(w.currentInclineLevel.get(), i, true);
        int newCurrentIncline = w.currentInclineLevel.get() + i;
        ftmsResponse(SET_TARGET_INCLINATION, SUCCESS);

        w.currentInclineLevel.set(newCurrentIncline);
        w.currentInclineValue.set(getSpeedValue(w.currentInclineLevel.get()));

        w.currentSpeedLevel.set(newCurrentSpeedLevel);
        w.currentSpeed.set(getSpeedValue(w.currentSpeedLevel.get()));

        // u.setDevSpeedAndIncline();


    }


    public void hideBtnWorkoutStop() {
        getBinding().btnWorkoutStop.setVisibility(View.INVISIBLE);
    }

    public void hideBtnCoolDown() {
        getBinding().btnCoolDown.setVisibility(View.INVISIBLE);

        if (isUs) {
            getBinding().btnCoolDownUs.setVisibility(View.GONE);
        }
    }

    public void hideBtnSkip() {
        getBinding().btnSkip.setVisibility(View.INVISIBLE);

        getBinding().btnSkipUs.setVisibility(View.INVISIBLE);
    }

    boolean isBottomStopShowing = false;

    public void showBottomStopButton() {
        appStatusViewModel.changeMainButtonType(WORK_OUT_STOP);//顯示下方STOP按紐
        isBottomStopShowing = true;
        //appStatusViewModel.getMainButtonType.observe
    }

    /**
     * 設定BIKE按鈕
     */
    public void setBikeControllerNum() {


        if (isUs) {

            if (w.selProgram != FITNESS_TEST && w.selProgram != HEART_RATE) {

                getBinding().groupTopNumberUs.setVisibility(View.VISIBLE);
                getBinding().groupBottomNumberUs.setVisibility(View.VISIBLE);

                getBinding().tvTopNumberUs11.setVisibility(View.GONE);
                getBinding().tvTopNumberUs12.setVisibility(View.GONE);

                getBinding().tvBottomNumberUs11.setVisibility(View.GONE);
                getBinding().tvBottomNumberUs12.setVisibility(View.GONE);


                if (w.selProgram == WATTS) {
                    getBinding().tvTopNumberUs10.setVisibility(View.GONE);
                    getBinding().tvBottomNumberUs10.setVisibility(View.GONE);
                }


                getBinding().tvTopNumberUs1.setText((w.selProgram == WATTS) ? BIKE_WATT_US_NUM_1 : (MODE.isUbeType() ?  UBE_LEVEL_US_NUM_1 : STEPPER_LEVEL_US_NUM_1));
                getBinding().tvTopNumberUs2.setText((w.selProgram == WATTS) ? BIKE_WATT_US_NUM_2 :(MODE.isUbeType() ?  UBE_LEVEL_US_NUM_2 : STEPPER_LEVEL_US_NUM_2));
                getBinding().tvTopNumberUs3.setText((w.selProgram == WATTS) ? BIKE_WATT_US_NUM_3 : (MODE.isUbeType() ?  UBE_LEVEL_US_NUM_3 : STEPPER_LEVEL_US_NUM_3));
                getBinding().tvTopNumberUs4.setText((w.selProgram == WATTS) ? BIKE_WATT_US_NUM_4 : (MODE.isUbeType() ?  UBE_LEVEL_US_NUM_4 : STEPPER_LEVEL_US_NUM_4));
                getBinding().tvTopNumberUs5.setText((w.selProgram == WATTS) ? BIKE_WATT_US_NUM_5 : (MODE.isUbeType() ?  UBE_LEVEL_US_NUM_5 : STEPPER_LEVEL_US_NUM_5));
                getBinding().tvTopNumberUs6.setText((w.selProgram == WATTS) ? BIKE_WATT_US_NUM_6 : (MODE.isUbeType() ?  UBE_LEVEL_US_NUM_6 : STEPPER_LEVEL_US_NUM_6));
                getBinding().tvTopNumberUs7.setText((w.selProgram == WATTS) ? BIKE_WATT_US_NUM_7 : (MODE.isUbeType() ?  UBE_LEVEL_US_NUM_7 : STEPPER_LEVEL_US_NUM_7));
                getBinding().tvTopNumberUs8.setText((w.selProgram == WATTS) ? BIKE_WATT_US_NUM_8 : (MODE.isUbeType() ?  UBE_LEVEL_US_NUM_8 : STEPPER_LEVEL_US_NUM_8));
                getBinding().tvTopNumberUs9.setText((w.selProgram == WATTS) ? BIKE_WATT_US_NUM_9 : (MODE.isUbeType() ?  UBE_LEVEL_US_NUM_9 : STEPPER_LEVEL_US_NUM_9));
                getBinding().tvTopNumberUs10.setText((w.selProgram == WATTS) ? BIKE_WATT_US_NUM_10 : (MODE.isUbeType() ?  UBE_LEVEL_US_NUM_10 : STEPPER_LEVEL_US_NUM_10));

                getBinding().tvBottomNumberUs1.setText((w.selProgram == WATTS) ? BIKE_WATT_US_NUM_10 : (MODE.isUbeType() ?  UBE_LEVEL_US_NUM_11 : STEPPER_LEVEL_US_NUM_11));
                getBinding().tvBottomNumberUs2.setText((w.selProgram == WATTS) ? BIKE_WATT_US_NUM_11 : (MODE.isUbeType() ?  UBE_LEVEL_US_NUM_12 : STEPPER_LEVEL_US_NUM_12));
                getBinding().tvBottomNumberUs3.setText((w.selProgram == WATTS) ? BIKE_WATT_US_NUM_12 : (MODE.isUbeType() ?  UBE_LEVEL_US_NUM_13 : STEPPER_LEVEL_US_NUM_13));
                getBinding().tvBottomNumberUs4.setText((w.selProgram == WATTS) ? BIKE_WATT_US_NUM_13 : (MODE.isUbeType() ?  UBE_LEVEL_US_NUM_14 : STEPPER_LEVEL_US_NUM_14));
                getBinding().tvBottomNumberUs5.setText((w.selProgram == WATTS) ? BIKE_WATT_US_NUM_14 : (MODE.isUbeType() ?  UBE_LEVEL_US_NUM_15 : STEPPER_LEVEL_US_NUM_15));
                getBinding().tvBottomNumberUs6.setText((w.selProgram == WATTS) ? BIKE_WATT_US_NUM_15 : (MODE.isUbeType() ?  UBE_LEVEL_US_NUM_16 : STEPPER_LEVEL_US_NUM_16));
                getBinding().tvBottomNumberUs7.setText((w.selProgram == WATTS) ? BIKE_WATT_US_NUM_16 : (MODE.isUbeType() ?  UBE_LEVEL_US_NUM_17 : STEPPER_LEVEL_US_NUM_17));
                getBinding().tvBottomNumberUs8.setText((w.selProgram == WATTS) ? BIKE_WATT_US_NUM_17 : (MODE.isUbeType() ?  UBE_LEVEL_US_NUM_18 : STEPPER_LEVEL_US_NUM_18));
                getBinding().tvBottomNumberUs9.setText((w.selProgram == WATTS) ? BIKE_WATT_US_NUM_18 : (MODE.isUbeType() ?  UBE_LEVEL_US_NUM_19 : STEPPER_LEVEL_US_NUM_19));
                getBinding().tvBottomNumberUs10.setText((w.selProgram == WATTS) ? BIKE_WATT_US_NUM_18 : (MODE.isUbeType() ?  UBE_LEVEL_US_NUM_20 : STEPPER_LEVEL_US_NUM_20));


                getBinding().tvTopTextUs.setText((w.selProgram == WATTS) ? R.string.direct_watts : R.string.direct_level);
                getBinding().tvBottomTextUs.setText((w.selProgram == WATTS) ? R.string.direct_watts : R.string.direct_level);

            }

        } else {
            getBinding().btnBikeLevelNum8.setText((w.selProgram == WATTS || w.selProgram == FITNESS_TEST) ? BIKE_WATT_NUM_1 : BIKE_LEVEL_NUM_1);
            getBinding().btnBikeLevelNum16.setText((w.selProgram == WATTS || w.selProgram == FITNESS_TEST) ? BIKE_WATT_NUM_2 : BIKE_LEVEL_NUM_2);
            getBinding().btnBikeLevelNum24.setText((w.selProgram == WATTS || w.selProgram == FITNESS_TEST) ? BIKE_WATT_NUM_3 : BIKE_LEVEL_NUM_3);
            getBinding().btnBikeLevelNum32.setText((w.selProgram == WATTS || w.selProgram == FITNESS_TEST) ? BIKE_WATT_NUM_4 : BIKE_LEVEL_NUM_4);
            getBinding().btnBikeLevelNum40.setText((w.selProgram == WATTS || w.selProgram == FITNESS_TEST) ? BIKE_WATT_NUM_5 : BIKE_LEVEL_NUM_5);

            getBinding().viewBikeLevelTitle.setText((w.selProgram == WATTS || w.selProgram == FITNESS_TEST) ? R.string.Power : R.string.Level);

            getBinding().viewBikeLevelTitleRight.setText((w.selProgram == WATTS) ? R.string.Power : (w.selProgram == HEART_RATE) ? R.string.THR : R.string.Level);

            //   getBinding().viewBikeLevelTitleRight.setText((w.selProgram == WATTS || w.selProgram == FITNESS_TEST) ? R.string.Power : R.string.Level);
        }
    }

    /**
     * 設定Treadmill SPEED 按鈕
     */
    public void setTreadmillSpeedControllerNum() {

//        int maxSpeed = (UNIT_E == IMPERIAL ? MAX_SPD_IU_MAX : MAX_SPD_MU_MAX) / 10;
//        int minSpeed = 1;
//        int i = Math.round(maxSpeed / 4f);
//
//        if (UNIT_E == IMPERIAL) {
//            TREADMILL_SPEED_IMPERIAL_NUM_1 = String.valueOf(minSpeed);
//            TREADMILL_SPEED_IMPERIAL_NUM_2 = String.valueOf(i);
//            TREADMILL_SPEED_IMPERIAL_NUM_3 = String.valueOf(Integer.parseInt(TREADMILL_SPEED_IMPERIAL_NUM_2) + i);
//            TREADMILL_SPEED_IMPERIAL_NUM_4 = String.valueOf(Integer.parseInt(TREADMILL_SPEED_IMPERIAL_NUM_3) + i);
//            TREADMILL_SPEED_IMPERIAL_NUM_5 = String.valueOf(maxSpeed);
//        } else {
//            TREADMILL_SPEED_METRIC_NUM_1 = String.valueOf(minSpeed);
//            TREADMILL_SPEED_METRIC_NUM_2 = String.valueOf(i);
//            TREADMILL_SPEED_METRIC_NUM_3 = String.valueOf(Integer.parseInt(TREADMILL_SPEED_METRIC_NUM_2) + i);
//            TREADMILL_SPEED_METRIC_NUM_4 = String.valueOf(Integer.parseInt(TREADMILL_SPEED_METRIC_NUM_3) + i);
//            TREADMILL_SPEED_METRIC_NUM_5 = String.valueOf(maxSpeed);
//        }

//        getBinding().btnWorkout1.setText(UNIT_E == IMPERIAL ? TREADMILL_SPEED_IMPERIAL_NUM_1 : TREADMILL_SPEED_METRIC_NUM_1);
//        getBinding().btnWorkout3.setText(UNIT_E == IMPERIAL ? TREADMILL_SPEED_IMPERIAL_NUM_2 : TREADMILL_SPEED_METRIC_NUM_2);
//        getBinding().btnWorkout6.setText(UNIT_E == IMPERIAL ? TREADMILL_SPEED_IMPERIAL_NUM_3 : TREADMILL_SPEED_METRIC_NUM_3);
//        getBinding().btnWorkout9.setText(UNIT_E == IMPERIAL ? TREADMILL_SPEED_IMPERIAL_NUM_4 : TREADMILL_SPEED_METRIC_NUM_4);
//        getBinding().btnWorkout12.setText(UNIT_E == IMPERIAL ? TREADMILL_SPEED_IMPERIAL_NUM_5 : TREADMILL_SPEED_METRIC_NUM_5);

        getBinding().btnWorkout1.setText(UNIT_E == IMPERIAL ? TREADMILL_SPEED_IMPERIAL_NUM_5 : TREADMILL_SPEED_METRIC_NUM_5);
        getBinding().btnWorkout3.setText(UNIT_E == IMPERIAL ? TREADMILL_SPEED_IMPERIAL_NUM_4 : TREADMILL_SPEED_METRIC_NUM_4);
        getBinding().btnWorkout6.setText(UNIT_E == IMPERIAL ? TREADMILL_SPEED_IMPERIAL_NUM_3 : TREADMILL_SPEED_METRIC_NUM_3);
        getBinding().btnWorkout9.setText(UNIT_E == IMPERIAL ? TREADMILL_SPEED_IMPERIAL_NUM_2 : TREADMILL_SPEED_METRIC_NUM_2);
        getBinding().btnWorkout12.setText(UNIT_E == IMPERIAL ? TREADMILL_SPEED_IMPERIAL_NUM_1 : TREADMILL_SPEED_METRIC_NUM_1);

        //文字
        getBinding().viewSpeedTitle.setText(R.string.Speed);


        //SPEED
        getBinding().btnWorkout1.setOnClickListener(v -> updateSpeedOrLevelNum(getSpeedLevel(getConNum(getBinding().btnWorkout1)), true));
        getBinding().btnWorkout3.setOnClickListener(v -> updateSpeedOrLevelNum(getSpeedLevel(getConNum(getBinding().btnWorkout3)), true));
        getBinding().btnWorkout6.setOnClickListener(v -> updateSpeedOrLevelNum(getSpeedLevel(getConNum(getBinding().btnWorkout6)), true));
        getBinding().btnWorkout9.setOnClickListener(v -> updateSpeedOrLevelNum(getSpeedLevel(getConNum(getBinding().btnWorkout9)), true));
        getBinding().btnWorkout12.setOnClickListener(v -> updateSpeedOrLevelNum(getSpeedLevel(getConNum(getBinding().btnWorkout12)), true));


        if (isUs) {
            getBinding().btnWorkoutSpeedPlusUs.setOnClickListener(v -> {
                if (getBinding() == null) return;
                getBinding().btnWorkoutSpeedPlus.callOnClick();
            });

            getBinding().btnWorkoutSpeedMinusUs.setOnClickListener(v -> {
                if (getBinding() == null) return;
                getBinding().btnWorkoutSpeedMinus.callOnClick();
            });

            initUsBottomNumber();
        }

        getBinding().btnWorkoutSpeedPlus.setOnClickListener(v -> {

            updateSpeedOrLevelNum(1, false);

//            if (w.selProgram != HEART_RATE) {
//                updateSpeedOrLevelNum(1, false);
//            } else {
//                updateTargetHeartRate(true);
//            }
        });

        getBinding().btnWorkoutSpeedMinus.setOnClickListener(v -> {

            updateSpeedOrLevelNum(-1, false);

//            if (w.selProgram != HEART_RATE) {
//                updateSpeedOrLevelNum(-1, false);
//            } else {
//                updateTargetHeartRate(false);
//            }
        });

    }


    /**
     * 設定Treadmill Incline 按鈕
     */
    public void setTreadmillInclineControllerNum() {

//        int maxIncline = MAX_INC_MAX / 2;
//        int minIncline = 0;
//        int i = Math.round(maxIncline / 4f);

//        TREADMILL_INCLINE_NUM_1 = String.valueOf(minIncline);
//        TREADMILL_INCLINE_NUM_2 = String.valueOf(i);
//        TREADMILL_INCLINE_NUM_3 = String.valueOf(Integer.parseInt(TREADMILL_INCLINE_NUM_2) + i);
//        TREADMILL_INCLINE_NUM_4 = String.valueOf(Integer.parseInt(TREADMILL_INCLINE_NUM_3) + i);
//        TREADMILL_INCLINE_NUM_5 = String.valueOf(maxIncline);


        if (w.selProgram != HEART_RATE) {
            //Incline

//            getBinding().btnWorkout0.setText(TREADMILL_INCLINE_NUM_1);
//            getBinding().btnWorkout2.setText(TREADMILL_INCLINE_NUM_2);
//            getBinding().btnWorkout5.setText(TREADMILL_INCLINE_NUM_3);
//            getBinding().btnWorkout10.setText(TREADMILL_INCLINE_NUM_4);
//            getBinding().btnWorkout15.setText(TREADMILL_INCLINE_NUM_5);
            getBinding().btnWorkout0.setText(TREADMILL_INCLINE_NUM_5);
            getBinding().btnWorkout2.setText(TREADMILL_INCLINE_NUM_4);
            getBinding().btnWorkout5.setText(TREADMILL_INCLINE_NUM_3);
            getBinding().btnWorkout10.setText(TREADMILL_INCLINE_NUM_2);
            getBinding().btnWorkout15.setText(TREADMILL_INCLINE_NUM_1);

            getBinding().btnWorkout0.setOnClickListener(v -> updateInclineNum(getInclineLevel(getConNum(getBinding().btnWorkout0)), true));
            getBinding().btnWorkout2.setOnClickListener(v -> updateInclineNum(getInclineLevel(getConNum(getBinding().btnWorkout2)), true));
            getBinding().btnWorkout5.setOnClickListener(v -> updateInclineNum(getInclineLevel(getConNum(getBinding().btnWorkout5)), true));
            getBinding().btnWorkout10.setOnClickListener(v -> updateInclineNum(getInclineLevel(getConNum(getBinding().btnWorkout10)), true));
            getBinding().btnWorkout15.setOnClickListener(v -> updateInclineNum(getInclineLevel(getConNum(getBinding().btnWorkout15)), true));


//        } else {
//            //Treadmill HeartRate 用Incline控制 THR
//            getBinding().btnWorkout0.setClickable(false);
//            getBinding().btnWorkout0.setEnabled(false);
//            getBinding().btnWorkout2.setClickable(false);
//            getBinding().btnWorkout5.setClickable(false);
//            getBinding().btnWorkout10.setClickable(false);
//            getBinding().btnWorkout15.setClickable(false);
        }


        //Treadmill
        if (isUs) {
            getBinding().btnWorkoutInclinePlusUs.setOnClickListener(v -> {
                if (getBinding() == null) return;
                getBinding().btnWorkoutInclinePlus.callOnClick();
            });

            getBinding().btnWorkoutInclineMinusUs.setOnClickListener(v -> {
                if (getBinding() == null) return;
                getBinding().btnWorkoutInclineMinus.callOnClick();
            });


            initUsTopNumber();
        }

        getBinding().btnWorkoutInclinePlus.setOnClickListener(v -> {
            if (w.selProgram != HEART_RATE) {
                updateInclineNum(1, false);
            } else {
                //Treadmill HeartRate 用Incline控制 THR
                updateTargetHeartRate(true);
            }

            // updateInclineNum(1, false);
        });

        getBinding().btnWorkoutInclineMinus.setOnClickListener(v -> {

            if (w.selProgram != HEART_RATE) {
                updateInclineNum(-1, false);
            } else {
                //Treadmill HeartRate 用Incline控制 THR
                updateTargetHeartRate(false);
            }

            //    updateInclineNum(-1, false);
        });


        getBinding().viewInclineTitle.setText((w.selProgram == HEART_RATE) ? R.string.THR : R.string.Incline);

    }


    private WarringNoHrCountDownWindow warringNoHrCountDownWindow;

    public void noHrCheck() {

        //如果連續30秒未偵測到心跳結束
//        if (w.currentHeartRate.get() < NO_HR_HR) {
//            noHrTime++;
//            if (noHrTime == 1) {
//                showWarring2(WARRING_NO_HR);
//            }
//
//            if (noHrTime > 30) {
//                Log.d("FITNESS_TEST_PROGRAM", "noHrCheck: 30秒無心跳");
//                isWorkoutTimerRunning = false;
//                showWarring(WARRING_NO_HR);
//            }
//        } else {
//            noHrTime = 0;
//        }


        if (w.currentHeartRate.get() >= NO_HR_HR) return;

        if (warringNoHrCountDownWindow != null && warringNoHrCountDownWindow.isShowing()) return;
        if (warringCountDownWindow != null && warringCountDownWindow.isShowing()) return;

        if (!isAdded()) return;

        try {
            warringNoHrCountDownWindow = new WarringNoHrCountDownWindow(requireActivity(), WARRING_NO_HR, w);
            warringNoHrCountDownWindow.showAtLocation(requireActivity().getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);
            warringNoHrCountDownWindow.setOnCustomDismissListener(new BasePopupWindow.OnCustomDismissListener() {
                @Override
                public void onStartDismiss(MsgEvent value) {
                    if (value != null) {
                        finishWorkout(false);
                    }
                }

                @Override
                public void onDismiss() {
                    warringNoHrCountDownWindow = null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onStop() {
        super.onStop();

        try {
            if (warringWindow != null) {
                warringWindow.dismiss();
                warringWindow = null;
            }

            if (hrUpdateWindow != null) {
                hrUpdateWindow.dismiss();
                hrUpdateWindow = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setHrJustRight() {
        if (workoutStatsFragment != null) {

            boolean isHigh = w.currentHeartRate.get() > (w.selTargetHrBpm.get() * 1.1);

            int x = w.selTargetHrBpm.get() - w.currentHeartRate.get();

            boolean isRight = (x <= 3) && (x >= -3);

            if (isRight) {
                workoutStatsFragment.getBinding().tvHrStatus.setText(R.string.Just_Right);
                workoutStatsFragment.getBinding().tvHrStatus.setBackgroundResource(R.drawable.panel_bg_all_4_0dac87);
            }

            if (isHigh) {
                workoutStatsFragment.getBinding().tvHrStatus.setText(R.string.Too_High);
                workoutStatsFragment.getBinding().tvHrStatus.setBackgroundResource(R.drawable.panel_bg_all_4_e24b44);
            }

            workoutStatsFragment.getBinding().tvHrStatus.setVisibility(!isRight && !isHigh ? View.INVISIBLE : View.VISIBLE);

        }
    }

    private void closeWindow() {

        if (cautionSpeedWindow != null) {
            cautionSpeedWindow.dismiss();
            cautionSpeedWindow = null;
        }

        if (hrUpdateWindow != null) {
            hrUpdateWindow.dismiss();
            hrUpdateWindow = null;
        }

        if (hrUpdateWindow2 != null) {
            hrUpdateWindow2.dismiss();
            hrUpdateWindow2 = null;
        }

        if (warringCountDownWindow != null) {
            warringCountDownWindow.dismiss();
            warringCountDownWindow = null;
        }

        if (warringNoHrCountDownWindow != null) {
            warringNoHrCountDownWindow.dismiss();
            warringNoHrCountDownWindow = null;
        }

        if (warringWindow != null) {
            warringWindow.dismiss();
            warringWindow = null;
        }

        if (parent != null && parent.popupWindow != null) {
            parent.popupWindow.dismiss();
            parent.popupWindow = null;
        }
    }


    int[] drawableRs = new int[]{R.drawable.img_workout_1, R.drawable.img_workout_2, R.drawable.img_workout_3, R.drawable.img_workout_4, R.drawable.img_workout_5};
    int n = 0;

    /**
     * 換背景圖
     */
    private void showBackground() {
        try {
            if (workoutStatsFragment == null) return;
            if (w.elapsedTime.get() % 60 == 0) {
                n++;
                if (n >= drawableRs.length) n = 0;
                if (!workoutStatsFragment.isStatsPageShow) return;
                Glide.with(getApp())
                        .load(drawableRs[n])
                        .transition(DrawableTransitionOptions.withCrossFade(1000))
                        .skipMemoryCache(true)
                        .centerCrop()
                        .into(getBinding().viewBase);
            }
        } catch (Exception e) {
            n = 0;
            e.printStackTrace();
        }
    }

    private void clearChartMsg() {
        if (workoutChartsFragment.chartMsgSpeedWindow != null) {
            workoutChartsFragment.chartMsgSpeedWindow.dismiss();
            workoutChartsFragment.chartMsgSpeedWindow = null;
        }

        if (workoutChartsFragment.chartMsgInclineWindow != null) {
            workoutChartsFragment.chartMsgInclineWindow.dismiss();
            workoutChartsFragment.chartMsgInclineWindow = null;
        }
    }


    int limitType = NO_LIMIT;
    double targetLimitDistance;//公制
    double currentLimitDistance;//公制

    long targetLimitTime;
    long currentLimitTime;

    // int
    private void getLimit() {
        DeviceSettingBean deviceSettingBean = getApp().getDeviceSettingBean();
        //0:都不限制, 1:限制 distance,2:限制 time
        if (deviceSettingBean.getUsageRestrictionsType() == NO_LIMIT) {
            limitType = NO_LIMIT;
            return;
        }

        if (deviceSettingBean.getUsageRestrictionsType() == DISTANCE_LIMIT) {
            limitType = DISTANCE_LIMIT;
            targetLimitDistance = deviceSettingBean.getUsageRestrictionsDistanceLimit();//公制
            currentLimitDistance = deviceSettingBean.getCurrentUsageRestrictionsDistance();//公制
        } else {
            limitType = TIME_LIMIT;
            targetLimitTime = deviceSettingBean.getUsageRestrictionsTimeLimit();
            currentLimitTime = deviceSettingBean.getCurrentUsageRestrictionsTime();
        }
    }

    private void checkLimit() {
        if (limitType == NO_LIMIT) return;

        if (limitType == DISTANCE_LIMIT) {
            double x;
            if (UNIT_E == METRIC) {
                x = currentLimitDistance + w.currentDistance.get();
            } else {
                x = currentLimitDistance + mi2km(w.currentDistance.get());
            }

            Log.d("LIMIT_SHOW", "checkLimit:目前LIMIT距離 :" + x + " km, 目標LIMIT距離：" + targetLimitDistance + " km");


            if (x >= targetLimitDistance) {
                Log.d("LIMIT_SHOW", "###############  " + x + "  超過了距離 " + targetLimitDistance);

                ((MainActivity) requireActivity()).showLoading(true);
                MainActivity.limitLoutOut = true;
                finishWorkout(false);
            }
        } else {
            long x;
            x = currentLimitTime + w.totalElapsedTime.get();
            Log.d("LIMIT_SHOW", "checkLimit:目前LIMIT時間 :" + x + ", 目標LIMIT時間：" + targetLimitTime);
            //   targetLimitTime = 10;
            if (x >= targetLimitTime) {
                Log.d("LIMIT_SHOW", "###############  " + x + "  超過了時間 " + targetLimitTime);
                ((MainActivity) requireActivity()).showLoading(true);
                MainActivity.limitLoutOut = true;
                finishWorkout(false);
            }
        }
    }

}