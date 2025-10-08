package com.dyaco.spirit_commercial.dashboard_training;

import static com.dyaco.spirit_commercial.App.UNIT_E;
import static com.dyaco.spirit_commercial.App.getApp;
import static com.dyaco.spirit_commercial.MainActivity.isTreadmill;
import static com.dyaco.spirit_commercial.MainActivity.userProfileViewModel;
import static com.dyaco.spirit_commercial.support.CommonUtils.findMaxInt;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.getMaxSpeedMinValue;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.getMaxSpeedValue;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.getSpeedLevel;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.getSpeedValue;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.DEFAULT_USE_TIME_LIMIT;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.ON;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.EVENT_SET_AGE;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.EVENT_SET_UNIT;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.NO_HR_HIDE_BUTTON;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.ON_STOP_BACK_TO_MAIN_TRAINING;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.STEP1_TO_STEP_2;
import static com.dyaco.spirit_commercial.support.intdef.MainDashboardBottomButtonIntDef.DISAPPEAR;
import static com.dyaco.spirit_commercial.support.intdef.MainDashboardBottomButtonIntDef.JUST_NEXT;
import static com.dyaco.spirit_commercial.support.intdef.MainDashboardBottomButtonIntDef.START_THIS_PROGRAM;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.MAX_LEVEL_MAX;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.MAX_SPD_INC;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.POWER_DFT;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.POWER_MAX;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.POWER_MIN;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.SPRINT_LEVEL_DEF;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.SPRINT_LEVEL_MAX;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.SPRINT_LEVEL_MIN;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.SPRINT_TIME_DEF;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.SPRINT_TIME_MAX;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.SPRINT_TIME_MIN;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.THR_CONSTANT;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.THR_PERCENTAGE_65;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.THR_PERCENTAGE_80;
import static com.dyaco.spirit_commercial.workout.programs.ProgramsEnum.HEART_RATE;
import static com.dyaco.spirit_commercial.workout.programs.ProgramsEnum.HIIT;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.FragmentSetTimeBinding;
import com.dyaco.spirit_commercial.settings.KeyboardWindow;
import com.dyaco.spirit_commercial.support.CommonUtils;
import com.dyaco.spirit_commercial.support.FormulaUtil;
import com.dyaco.spirit_commercial.support.MsgEvent;
import com.dyaco.spirit_commercial.support.base_component.BaseBindingFragment;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.custom_view.RulerView;
import com.dyaco.spirit_commercial.support.intdef.DeviceIntDef;
import com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS;
import com.dyaco.spirit_commercial.support.intdef.WorkoutIntDef;
import com.dyaco.spirit_commercial.support.utils.CheckDoubleClick;
import com.dyaco.spirit_commercial.viewmodel.AppStatusViewModel;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingBean;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingViewModel;
import com.dyaco.spirit_commercial.viewmodel.WorkoutViewModel;
import com.dyaco.spirit_commercial.workout.programs.ProgramsEnum;
import com.google.android.material.button.MaterialButton;
import com.jeremyliao.liveeventbus.LiveEventBus;

import java.util.ArrayList;
import java.util.Arrays;

public class SetTimeFragment extends BaseBindingFragment<FragmentSetTimeBinding> {
    private float mphIU;
    private float mphMU;

    private ProgramsEnum programsEnum;

    private MaterialButton btnBack;
    private TextView tvRuler1Text;
    private TextView tvRuler2Text;
    private RulerView rulerView1;
    private RulerView rulerView2;
    private boolean isRule1Float = false; //1是否有小數點
    private boolean isRule2Float = true; //2是否有小數點
    private WorkoutViewModel workoutViewModel;
    private AppStatusViewModel appStatusViewModel;
    private DeviceSettingViewModel deviceSettingViewModel;
    private float MAX_VALUE_1 = 99;// 分
    private float MIN_VALUE_1 = 0;// 分

    private float MAX_VALUE_2 = 99;// 分
    private float MIN_VALUE_2 = 0;// 分

    private boolean isUseTimeLimit = false;
    private int useTimeLimitMax = 99; // 分
    private int useTimeLimitMin = 0;


    public SetTimeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.isOnStopBackToMainTraining = true;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        programsEnum = SetTimeFragmentArgs.fromBundle(getArguments()).getProgramEnum();


        switch (programsEnum) {
            case HIIT:
                mphIU = getSpeedValue(OPT_SETTINGS.SPRINT_SPEED_IU_DEF);
                mphMU = getSpeedValue(OPT_SETTINGS.SPRINT_SPEED_MU_DEF);
                break;
            default:
                mphIU = getSpeedValue(OPT_SETTINGS.MAX_SPD_IU_DEF);
                mphMU = getSpeedValue(OPT_SETTINGS.MAX_SPD_MU_DEF);
        }


        workoutViewModel = new ViewModelProvider(requireActivity()).get(WorkoutViewModel.class);
        appStatusViewModel = new ViewModelProvider(requireActivity()).get(AppStatusViewModel.class);

        workoutViewModel.selProgram = programsEnum;

        deviceSettingViewModel = new ViewModelProvider(requireActivity()).get(DeviceSettingViewModel.class);

        getBinding().setWorkoutData(workoutViewModel);


        DeviceSettingBean d = getApp().getDeviceSettingBean();
        if (d.getIsUseTimeLimit() == ON) {
            useTimeLimitMax = d.getUseTimeLimit() == 0 ? DEFAULT_USE_TIME_LIMIT : (int) d.getUseTimeLimit() / 60;
            MAX_VALUE_1 = useTimeLimitMax;
            MIN_VALUE_1 = 1;

            useTimeLimitMin = 1;
        }

        initView();
        initEvent();

        initProgram();

        LiveEventBus.get(EVENT_SET_UNIT).observe(getViewLifecycleOwner(), s -> initProgram());

        LiveEventBus.get(EVENT_SET_AGE).observe(getViewLifecycleOwner(), s -> setTargetHR());


        //HR連線or斷線
        workoutViewModel.getIsHrConnected().observe(getViewLifecycleOwner(), item -> noHrCheck());

        noHrCheck();


        LiveEventBus.get(ON_STOP_BACK_TO_MAIN_TRAINING, Boolean.class).observe(getViewLifecycleOwner(), s -> {
            if (getBinding() != null) {
                getBinding().btnBack.callOnClick();
            }
        });

    }


    int currentCheckedId;
    int tempTHR;

    private void setTargetHR() {
        if (programsEnum != HEART_RATE) return;
        ArrayList<RadioButton> listOfRadioButtons = new ArrayList<>();
        for (int i = 0; i < getBinding().rgHrFilter.getChildCount(); i++) {
            View o = getBinding().rgHrFilter.getChildAt(i);
            if (o instanceof RadioButton) {
                listOfRadioButtons.add((RadioButton) o);
                if (listOfRadioButtons.get(i).getId() == currentCheckedId) {

                    int age = userProfileViewModel.getUserAge();

                    int targetHr = currentCheckedId == R.id.rb65Max ?
                            (THR_CONSTANT - age) * THR_PERCENTAGE_65 / 100 :
                            (THR_CONSTANT - age) * THR_PERCENTAGE_80 / 100;

                    if (currentCheckedId == R.id.rbSetManually) {
                        targetHr = tempTHR;
                    }

                    getBinding().topViewEstimatedTime.setText(String.valueOf(targetHr));

                    workoutViewModel.selTargetHrBpm.set(targetHr);
                }
            }
        }
    }

    private void initEvent() {
        btnBack.setOnClickListener(v -> {

            if (CheckDoubleClick.isFastClick()) return;

            try {
                new FormulaUtil().clearWorkoutViewModel(workoutViewModel);
                parent.hrNotifyWarringWindow(false);
                appStatusViewModel.changeMainButtonType(DISAPPEAR);
                Navigation.findNavController(v).navigate(SetTimeFragmentDirections.actionSetTimeFragmentToProgramsFragment(WorkoutIntDef.DEFAULT_PROGRAM));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        //HIIT的下一步
        appStatusViewModel.changeNavigate().observe(getViewLifecycleOwner(), action -> {
            if (action == STEP1_TO_STEP_2) {
                //app:popUpToInclusive="true" 沒加的話 SetTimeFragment 不會Destroy
                try {
                    Navigation.findNavController(requireView()).navigate(SetTimeFragmentDirections.actionSetTimeFragmentToSetTimeStep2Fragment(programsEnum));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        getBinding().rgHrFilter.setOnCheckedChangeListener((group, checkedId) -> {
            currentCheckedId = checkedId;
            setTargetHR();

            if (checkedId == R.id.rbSetManually) {
                getBinding().timeView.setVisibility(View.VISIBLE);
                getBinding().topView.setVisibility(View.INVISIBLE);
            } else {
                getBinding().timeView.setVisibility(View.INVISIBLE);
                getBinding().topView.setVisibility(View.VISIBLE);
            }
        });

        //RulerView1
        getBinding().tvTimeNum.setOnClickListener(v -> {
            String title = getBinding().tvTimeTitle.getText().toString();
            parent.popupWindow = new KeyboardWindow(requireActivity(), isRule1Float, getBinding().tvTimeNum.getText().toString(), MAX_VALUE_1, MIN_VALUE_1, title);
            parent.popupWindow.showAtLocation(parent.getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);
            ((KeyboardWindow) parent.popupWindow).setOnCustomDismissListener(new BasePopupWindow.OnCustomDismissListener() {
                @Override
                public void onStartDismiss(MsgEvent value) {
                    if (value != null) {
                        getBinding().tvTimeNum.setText(String.valueOf(value.getObj()));
                        getBinding().rulerViewTime.setSelectedValue(Float.parseFloat(value.getObj().toString()));
                    }
                }

                @Override
                public void onDismiss() {
                    parent.popupWindow = null;
                }
            });
        });

        getBinding().tvSpeedNum.setOnClickListener(v -> {
            String title = getBinding().tvSpeedTitle.getText().toString();
            parent.popupWindow = new KeyboardWindow(requireActivity(), isRule2Float, getBinding().tvSpeedNum.getText().toString(), MAX_VALUE_2, MIN_VALUE_2, title);
            parent.popupWindow.showAtLocation(parent.getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);
            ((KeyboardWindow) parent.popupWindow).setOnCustomDismissListener(new BasePopupWindow.OnCustomDismissListener() {
                @Override
                public void onStartDismiss(MsgEvent value) {
                    if (value != null) {
                        getBinding().tvSpeedNum.setText(String.valueOf(value.getObj()));
                        getBinding().rulerViewSpeed.setSelectedValue(Float.parseFloat(value.getObj().toString()));
                    }
                }

                @Override
                public void onDismiss() {
                    parent.popupWindow = null;
                }
            });
        });
    }

    private void initView() {
        btnBack = getBinding().btnBack;
        AppCompatImageButton btnTimeMinus = getBinding().btnTimeMinus;
        AppCompatImageButton btnTimePlus = getBinding().btnTimePlus;
        AppCompatImageButton btnSpeedPlus = getBinding().btnSpeedPlus;
        AppCompatImageButton btnSpeedMinus = getBinding().btnSpeedMinus;
        tvRuler1Text = getBinding().tvTimeNum;
        tvRuler2Text = getBinding().tvSpeedNum;
        rulerView1 = getBinding().rulerViewTime;
        rulerView2 = getBinding().rulerViewSpeed;

        new CommonUtils().addAutoClick(btnTimePlus);
        new CommonUtils().addAutoClick(btnTimeMinus);
        new CommonUtils().addAutoClick(btnSpeedPlus);
        new CommonUtils().addAutoClick(btnSpeedMinus);

        rulerView1.setOnValueChangeListener((view, value) ->
                setRulerView1Data(value));

        btnTimePlus.setOnClickListener(v ->
                rulerView1.setSelectedValue(Float.parseFloat(tvRuler1Text.getText().toString()) + (isRule1Float ? 0.1f : 1)));

        btnTimeMinus.setOnClickListener(v ->
                rulerView1.setSelectedValue(Float.parseFloat(tvRuler1Text.getText().toString()) - (isRule1Float ? 0.1f : 1)));

        rulerView2.setOnValueChangeListener((view, value) ->
                setRulerView2Data(value));

        btnSpeedPlus.setOnClickListener(v ->
                rulerView2.setSelectedValue(Float.parseFloat(tvRuler2Text.getText().toString()) + (isRule2Float ? 0.1f : 1)));

        btnSpeedMinus.setOnClickListener(v ->
                rulerView2.setSelectedValue(Float.parseFloat(tvRuler2Text.getText().toString()) - (isRule2Float ? 0.1f : 1)));

    }

    private void initProgram() {
        int viewType = START_THIS_PROGRAM;
        int topIcon = 0;
        int bottomIcon = 0;

        int timeTitle = 0;
        int speedTitle = 0;
        float defSpeed = UNIT_E == DeviceIntDef.IMPERIAL ? mphIU : mphMU; //MAX_SPD_IU_DEF

        MAX_VALUE_2 = UNIT_E == DeviceIntDef.IMPERIAL ? OPT_SETTINGS.MAX_SPD_IU_MAX / 10f : OPT_SETTINGS.MAX_SPD_MU_MAX / 10f;
        MIN_VALUE_2 = MAX_SPD_INC;

        switch (programsEnum) {
            case MANUAL:
                timeTitle = R.string.time_min;
                speedTitle = R.string.time_min;
                rulerView1.setMaxValue(MAX_VALUE_1);
                rulerView1.setMinValue(MIN_VALUE_1);
                rulerView1.setSelectedValue(OPT_SETTINGS.TARGET_TIME_DEF);
                topIcon = R.drawable.icon_time_48;

                //DashboardTrainingFragment btnStart 也有
                //Workout Data 初始設定
                //   workoutViewModel.selMaxSpeed.set(20);
                workoutViewModel.selWorkoutTime.set(OPT_SETTINGS.TARGET_TIME_DEF * 60);

                break;
            case HILL:
            case FATBURN:
            case CARDIO:
            case STRENGTH:
            case INTERVAL:

                timeTitle = R.string.time_min;
                topIcon = R.drawable.icon_time_48;
                if (isTreadmill) {

                    speedTitle = UNIT_E == DeviceIntDef.IMPERIAL ? R.string.max_speed_mph : R.string.max_speed_kph;
                    bottomIcon = R.drawable.icon_speed_48;

//                    int[] numArray = Arrays.stream(workoutViewModel.selProgram.getTreadmillSpeedNum().split("#", -1)).mapToInt(Integer::parseInt).toArray();
//                    IntSummaryStatistics s = Arrays.stream(numArray).summaryStatistics();
                    MAX_VALUE_2 = getMaxSpeedValue(programsEnum);
                    //   MAX_VALUE_2 = getSpeedValue((int) (getMaxSpeedLevel(programsEnum) * (s.getMax() / 100f)));
                    MIN_VALUE_2 = getMaxSpeedMinValue(programsEnum);
                    //   MIN_VALUE_2 = getSpeedValue((int) (getMaxSpeedLevel(programsEnum) * (s.getMin() / 100f)));

                    rulerView2.setMaxValue(MAX_VALUE_2);
                    rulerView2.setMinValue(MIN_VALUE_2);

                    //Speed RulerView設定
                    isRule2Float = true;

                    rulerView2.setIntervalValue(MAX_SPD_INC / 10f);
                    rulerView2.setRetainLength(1);

                    rulerView2.setSelectedValue(MIN_VALUE_2);

                    //Workout Data 初始設定
                    workoutViewModel.selMaxSpeedOrLevel.set(getSpeedLevel(MIN_VALUE_2));

                } else {
                    speedTitle = R.string.max_level;
                    bottomIcon = R.drawable.icon_level_48;

                    //Level RulerView設定
                    isRule2Float = false;
                    rulerView2.setMaxValue(MAX_LEVEL_MAX);
                    rulerView2.setIntervalValue(1);
                    rulerView2.setRetainLength(0);

                    //以profile中最大的值 當作預設最小的 MaxLevel
                    int[] arrayLevel = Arrays.stream(programsEnum.getBikeLevelNum().split("#", -1))
                            .mapToInt(Integer::parseInt)
                            .toArray();
                    int maxLevelInProfile = Math.max(findMaxInt(arrayLevel), 5);
                    rulerView2.setMinValue(maxLevelInProfile);
                    rulerView2.setSelectedValue(maxLevelInProfile);

                    //輸入框
                    MAX_VALUE_2 = MAX_LEVEL_MAX;
                    MIN_VALUE_2 = maxLevelInProfile;

                    //Workout Data 初始設定
                    workoutViewModel.selMaxSpeedOrLevel.set(maxLevelInProfile);
                }
                //初始時間
                workoutViewModel.selWorkoutTime.set(OPT_SETTINGS.TARGET_TIME_DEF * 60);
                //時間RulerView設定
                isRule1Float = false;
                rulerView1.setMaxValue(MAX_VALUE_1);
                rulerView1.setMinValue(MIN_VALUE_1);
                rulerView1.setIntervalValue(1);
                rulerView1.setRetainLength(0);
                rulerView1.setSelectedValue(OPT_SETTINGS.TARGET_TIME_DEF);


                break;
            case RUN_5K:
            case RUN_10K:
                //輸入框的最大最小值
                MAX_VALUE_1 = UNIT_E == DeviceIntDef.IMPERIAL ? OPT_SETTINGS.MAX_SPD_IU_MAX / 10f : OPT_SETTINGS.MAX_SPD_MU_MAX / 10f;
                MIN_VALUE_1 = MAX_SPD_INC;

                isRule1Float = true;
                timeTitle = UNIT_E == DeviceIntDef.IMPERIAL ? R.string.target_speed_mph : R.string.target_speed_kph;
                speedTitle = R.string.estimated_time;

                rulerView1.setMaxValue(MAX_VALUE_1);
                rulerView1.setMinValue(MIN_VALUE_1);
                rulerView1.setIntervalValue(MAX_SPD_INC / 10f); //0.1一格
                rulerView1.setRetainLength(1);
                topIcon = R.drawable.icon_speed_48;
                bottomIcon = R.drawable.icon_time_48;

                rulerView1.setSelectedValue(defSpeed);

                workoutViewModel.selTargetSpeed.set(defSpeed);

//                int estimatedTime = FormulaUtil.kRun2Sec(programsEnum, defSpeed);
                //   workoutViewModel.selWorkoutTime.set(estimatedTime);

                break;
            case HIIT:
//                sprint level: 10~40
//                rest level: 1~sprint level的設定(必須小於sprint), (default: 5)
                int sprintSpeedDef;
                timeTitle = R.string.sprint_time_sec;
                viewType = JUST_NEXT;
                topIcon = R.drawable.icon_time_48;

                if (isTreadmill) {
                    bottomIcon = R.drawable.icon_speed_48;
                    speedTitle = UNIT_E == DeviceIntDef.IMPERIAL ? R.string.sprint_speed_mph : R.string.sprint_speed_kph;
                    //Speed RulerView設定

//                    MAX_VALUE_2 = UNIT_E == DeviceIntDef.IMPERIAL ? OPT_SETTINGS.MAX_SPD_IU_MAX / 10f : OPT_SETTINGS.MAX_SPD_MU_MAX / 10f;
//                    MIN_VALUE_2 = REST_LEVEL_DEF;

                    MAX_VALUE_2 = getMaxSpeedValue(HIIT);
                    MIN_VALUE_2 = getMaxSpeedMinValue(HIIT);


                    isRule2Float = true;
                    rulerView2.setMaxValue(MAX_VALUE_2); //SPRINT SPEED
                    rulerView2.setMinValue(MIN_VALUE_2);
                    rulerView2.setIntervalValue(MAX_SPD_INC / 10f); //0.1
                    rulerView2.setRetainLength(1);

                    //Workout Data 衝刺速度 初始設定  //3.2 >> 32(階數)
                    if (workoutViewModel.selSprintSpeedLevel.get() != 0) {
                        rulerView2.setSelectedValue(workoutViewModel.selSprintSpeedLevel.get() / 10f);
                    } else {
                        rulerView2.setSelectedValue(defSpeed);
                    }
                    //   sprintSpeedDef = getSpeedLevel(defSpeed);

                } else {
                    bottomIcon = R.drawable.icon_level_48;
                    speedTitle = R.string.sprint_level;

                    //Level RulerView設定
                    isRule2Float = false;
                    rulerView2.setMaxValue(SPRINT_LEVEL_MAX); //SPRINT LEVEL
                    rulerView2.setMinValue(SPRINT_LEVEL_MIN); //SPRINT LEVEL
                    rulerView2.setIntervalValue(1);
                    rulerView2.setRetainLength(0);

                    //  rulerView2.setSelectedValue(SPRINT_LEVEL_DEF);

                    if (workoutViewModel.selSprintSpeedLevel.get() != 0) {
                        rulerView2.setSelectedValue(workoutViewModel.selSprintSpeedLevel.get());
                    } else {
                        rulerView2.setSelectedValue(SPRINT_LEVEL_DEF);
                    }

                    //輸入框
                    MAX_VALUE_2 = SPRINT_LEVEL_MAX;
                    MIN_VALUE_2 = SPRINT_LEVEL_DEF;

                    //   sprintSpeedDef = SPRINT_LEVEL_DEF;
                }

                //衝刺(SPRINT)時間設定
                isRule1Float = false;
                rulerView1.setMaxValue(SPRINT_TIME_MAX);
                rulerView1.setMinValue(SPRINT_TIME_MIN);
                rulerView1.setIntervalValue(1);
                rulerView1.setRetainLength(0);

                MAX_VALUE_1 = SPRINT_TIME_MAX;
                MIN_VALUE_1 = SPRINT_TIME_MIN;

                if (workoutViewModel.selSprintTimeSec.get() != 0) {
                    rulerView1.setSelectedValue(workoutViewModel.selSprintTimeSec.get());
                } else {
                    rulerView1.setSelectedValue(SPRINT_TIME_DEF);
                }

                //HIIT衝刺時間
                //    workoutViewModel.selSprintTimeSec.set(SPRINT_TIME_DEF);

                //Workout Data 衝刺速度 初始設定  //3.2 >> 32(階數)
                //    workoutViewModel.selSprintSpeedLevel.set(sprintSpeedDef);

                break;
            case HEART_RATE:
                MAX_VALUE_2 = useTimeLimitMax;
                MIN_VALUE_2 = useTimeLimitMin;

                timeTitle = R.string.target_heart_rate_bpm;
                speedTitle = R.string.time_min;

                //THR RulerView設定
                isRule1Float = false;
                rulerView1.setMaxValue(OPT_SETTINGS.THR_MAX);
                rulerView1.setMinValue(OPT_SETTINGS.THR_MIN);
                rulerView1.setIntervalValue(1);
                rulerView1.setRetainLength(0);
                rulerView1.setSelectedValue(OPT_SETTINGS.THR_DEF);
                workoutViewModel.selTargetHrBpm.set(OPT_SETTINGS.THR_DEF);
                //輸入框的最大最小值
                MAX_VALUE_1 = OPT_SETTINGS.THR_MAX;
                MIN_VALUE_1 = OPT_SETTINGS.THR_MIN;

                //TIME RulerView設定
                isRule2Float = false;
                rulerView2.setMaxValue(MAX_VALUE_2);
                rulerView2.setMinValue(MIN_VALUE_2);
                rulerView2.setIntervalValue(1);
                rulerView2.setRetainLength(0);
                rulerView2.setSelectedValue(OPT_SETTINGS.TARGET_TIME_DEF);
                workoutViewModel.selWorkoutTime.set(OPT_SETTINGS.TARGET_TIME_DEF * 60);

                topIcon = R.drawable.icon_hr_48;
                bottomIcon = R.drawable.icon_time_48;

                break;

            case WATTS:
                isRule1Float = false;
                isRule2Float = false;
                timeTitle = R.string.time_min;
                speedTitle = R.string.constant_power_w;

                rulerView1.setMaxValue(MAX_VALUE_1);
                rulerView1.setMinValue(MIN_VALUE_1);
                rulerView1.setIntervalValue(1);
                rulerView1.setRetainLength(0);
                rulerView1.setSelectedValue(20);

                rulerView2.setMaxValue(POWER_MAX);
                rulerView2.setMinValue(POWER_MIN);
                rulerView2.setIntervalValue(1f);
                rulerView2.setRetainLength(1);
                rulerView2.setSelectedValue(50);

                MAX_VALUE_2 = POWER_MAX;
                MIN_VALUE_2 = POWER_MIN;

                topIcon = R.drawable.icon_time_48;
                bottomIcon = R.drawable.icon_mets_48;

                workoutViewModel.selConstantPowerW.set(POWER_DFT);
                break;

            case CUSTOM:
                break;
        }

        try {
            getBinding().tvTimeTitle.setText(timeTitle);
            getBinding().tvSpeedTitle.setText(speedTitle);
            getBinding().topRuleViewIcon.setBackgroundResource(topIcon);
            getBinding().bottomRuleViewIcon.setBackgroundResource(bottomIcon);

            appStatusViewModel.changeMainButtonType(viewType);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
     //   Log.d("OOOIIIIEEE", "SetTimeFragment onDestroy 2222: ");
    }

    /**
     * 上方SeekBar
     *
     * @param value v
     */
    private void setRulerView1Data(float value) {
        tvRuler1Text.setText(isRule1Float ? String.valueOf(value) : CommonUtils.subZeroAndDot(String.valueOf(value)));

        switch (programsEnum) {
            case MANUAL:
            case CARDIO:
            case FATBURN:
            case INTERVAL:
            case HILL:
            case STRENGTH:
            case WATTS:
                workoutViewModel.selWorkoutTime.set(Math.round(value * 60));
                break;
            case RUN_5K:
            case RUN_10K:
                if (UNIT_E == DeviceIntDef.IMPERIAL) {
                    mphIU = value;
                    mphMU = FormulaUtil.mph2kph(value);
                } else {
                    mphIU = FormulaUtil.kph2mph(value);
                    mphMU = value;
                }
                int estimatedTime = FormulaUtil.kRun2Sec(programsEnum, value);
                workoutViewModel.selTargetSpeed.set(value);
                //  workoutViewModel.selWorkoutTime.set(estimatedTime);
                getBinding().bottomViewEstimatedTime.setText(CommonUtils.formatSecToM(estimatedTime));
                break;
            case HEART_RATE:
                if (getBinding().rbSetManually.isChecked()) {
                    workoutViewModel.selTargetHrBpm.set(Math.round(value));
                    tempTHR = Math.round(value);
                    //Log.d("HEART_RATE_PROGRAM", "setRulerView1Data: " + tempTHR);
                }
                break;
            case HIIT:
                workoutViewModel.selSprintTimeSec.set(Math.round(value));
        }
    }

    /**
     * 下方SeekBar
     *
     * @param value v
     */
    private void setRulerView2Data(float value) {
        tvRuler2Text.setText(isRule2Float ? String.valueOf(value) : CommonUtils.subZeroAndDot(String.valueOf(value)));
        if (UNIT_E == DeviceIntDef.IMPERIAL) {
            mphIU = value;
            mphMU = FormulaUtil.mph2kph(value); //因為小數點一位，換算成公里時精度有時會跑掉
        } else {
            mphIU = FormulaUtil.kph2mph(value);
            mphMU = value;
        }

        switch (programsEnum) {
            case MANUAL:
            case CARDIO:
            case FATBURN:
            case INTERVAL:
            case HILL:
            case STRENGTH:
                workoutViewModel.selMaxSpeedOrLevel.set(isTreadmill ? getSpeedLevel(value) : Math.round(value));
                break;
            case HEART_RATE:
                workoutViewModel.selWorkoutTime.set(Math.round(value * 60));
                break;
            case HIIT:
                //   workoutViewModel.selSprintSpeed.set(isTreadmill ? Math.round(value * 10) : Math.round(value));
                workoutViewModel.selSprintSpeedLevel.set(isTreadmill ? getSpeedLevel(value) : Math.round(value));

                break;
            case WATTS:
                workoutViewModel.selConstantPowerW.set(Math.round(value));
        }

    }


    private void noHrCheck() {
        boolean isHide;
        //無心跳不可執行
        switch (workoutViewModel.selProgram) {
            case HEART_RATE:
                isHide = !workoutViewModel.getIsHrConnected().getValue();
                break;
            default:
                isHide = false;
        }

        //提示
        parent.hrNotifyWarringWindow(isHide);

        //沒hr就不能啟動的program，下方button true 隱藏
        //    new RxTimer().timer(100, number -> {
        LiveEventBus.get(NO_HR_HIDE_BUTTON).post(isHide);
        //    });

    }


}