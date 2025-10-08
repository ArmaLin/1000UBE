package com.dyaco.spirit_commercial.dashboard_training;

import static com.dyaco.spirit_commercial.App.UNIT_E;
import static com.dyaco.spirit_commercial.MainActivity.isTreadmill;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.getSpeedLevel;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.getSpeedValue;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.IMPERIAL;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.EVENT_SET_UNIT;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.ON_STOP_BACK_TO_MAIN_TRAINING;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.STEP2_TO_STEP_1;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.STEP2_TO_STEP_3;
import static com.dyaco.spirit_commercial.support.intdef.MainDashboardBottomButtonIntDef.DISAPPEAR;
import static com.dyaco.spirit_commercial.support.intdef.MainDashboardBottomButtonIntDef.PREVIOUS_AND_NEXT;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.MAX_SPD_INC;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.REST_LEVEL_DEF;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.REST_LEVEL_MIN;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.REST_TIME_DEF;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.REST_TIME_MAX;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.REST_TIME_MIN;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.FragmentSetTimeStep2Binding;
import com.dyaco.spirit_commercial.settings.KeyboardWindow;
import com.dyaco.spirit_commercial.support.CommonUtils;
import com.dyaco.spirit_commercial.support.FormulaUtil;
import com.dyaco.spirit_commercial.support.MsgEvent;
import com.dyaco.spirit_commercial.support.base_component.BaseBindingFragment;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.custom_view.RulerView;
import com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS;
import com.dyaco.spirit_commercial.support.intdef.WorkoutIntDef;
import com.dyaco.spirit_commercial.support.utils.CheckDoubleClick;
import com.dyaco.spirit_commercial.viewmodel.AppStatusViewModel;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingViewModel;
import com.dyaco.spirit_commercial.viewmodel.WorkoutViewModel;
import com.dyaco.spirit_commercial.workout.programs.ProgramsEnum;
import com.jeremyliao.liveeventbus.LiveEventBus;

public class SetTimeStep2Fragment extends BaseBindingFragment<FragmentSetTimeStep2Binding> {
    private float mphIU = OPT_SETTINGS.REST_SPEED_IU_DEF / 10f;
    private float mphMU = OPT_SETTINGS.REST_SPEED_MU_DEF / 10f;
    private ProgramsEnum programsEnum;

    private TextView tvTimeNum;
    private TextView tvSpeedNum;
    private RulerView rulerView1;
    private RulerView rulerView2;
    private boolean isRule1Float = false;
    private boolean isRule2Float = true;
    private float MAX_VALUE_1 = 99;
    private float MIN_VALUE_1 = 0;

    private float MAX_VALUE_2 = 99;
    private float MIN_VALUE_2 = 0;
    private AppStatusViewModel appStatusViewModel;
    private WorkoutViewModel workoutViewModel;
    private DeviceSettingViewModel deviceSettingViewModel;

    public SetTimeStep2Fragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.isOnStopBackToMainTraining = true;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d("SET_TIME", "SetTimeStep2Fragment: ");

        appStatusViewModel = new ViewModelProvider(requireActivity()).get(AppStatusViewModel.class);
        workoutViewModel = new ViewModelProvider(requireActivity()).get(WorkoutViewModel.class);
        deviceSettingViewModel = new ViewModelProvider(requireActivity()).get(DeviceSettingViewModel.class);
        programsEnum = SetTimeFragmentArgs.fromBundle(getArguments()).getProgramEnum();
        getBinding().tvProgramName.setText(programsEnum.getProgramName());

        initView();
        initEvent();

        initProgram();

        LiveEventBus.get(EVENT_SET_UNIT).observe(getViewLifecycleOwner(), s -> initProgram());

        LiveEventBus.get(ON_STOP_BACK_TO_MAIN_TRAINING, Boolean.class).observe(getViewLifecycleOwner(), s -> {
            if (getBinding() != null) {
                getBinding().btnBack.callOnClick();
            }
        });

    }

    private void initProgram() {
        int bottomIcon;
        int speedTitle;

        speedTitle = isTreadmill ? (UNIT_E == IMPERIAL ? R.string.rest_speed_mph : R.string.rest_speed_kph) : R.string.rest_level;


        //sprint level: 10~40
        //rest level: 1~sprint level的設定(必須小於sprint), (default: 5)
        if (isTreadmill) {
            float defSpeed = UNIT_E == IMPERIAL ? mphIU : mphMU;
            MAX_VALUE_2 = UNIT_E == IMPERIAL ? getSpeedValue(OPT_SETTINGS.MAX_SPD_IU_MAX) : getSpeedValue(OPT_SETTINGS.MAX_SPD_MU_MAX);
            MIN_VALUE_2 = UNIT_E == IMPERIAL ? getSpeedValue(OPT_SETTINGS.MIN_SPD_IU) : getSpeedValue(OPT_SETTINGS.MIN_SPD_MU);
            float restMax = getSpeedValue(workoutViewModel.selSprintSpeedLevel.get()) - 0.1f;

            bottomIcon = R.drawable.icon_speed_48;
            //Speed RulerView設定
            isRule2Float = true;
            rulerView2.setMaxValue(restMax);
            rulerView2.setMinValue(MIN_VALUE_2);
            rulerView2.setIntervalValue(MAX_SPD_INC / 10f); //每個柯度的數值 0.1
            rulerView2.setRetainLength(1);

            //Workout Data 休息時間 初始設定
            //   workoutViewModel.selRestSpeedLevel.set(getSpeedLevel(defSpeed));


            if (workoutViewModel.selRestSpeedLevel.get() == 0 ||
                    workoutViewModel.selRestSpeedLevel.get() >= workoutViewModel.selSprintSpeedLevel.get()) {
                workoutViewModel.selRestSpeedLevel.set(getSpeedLevel(defSpeed)); //階數
            }


            //RestSpeed 預設
            rulerView2.setSelectedValue(getSpeedValue(workoutViewModel.selRestSpeedLevel.get()));


            //輸入框的最大最小值
            MAX_VALUE_2 = restMax;
            MIN_VALUE_2 = REST_LEVEL_DEF;

        } else {
            //不能超過衝刺速度
            int restMax = workoutViewModel.selSprintSpeedLevel.get() - 1;
            bottomIcon = R.drawable.icon_level_48;
            //Level RulerView設定
            isRule2Float = false;
            rulerView2.setMaxValue(restMax); //REST_LEVEL
            rulerView2.setMinValue(REST_LEVEL_MIN);
            rulerView2.setIntervalValue(1); //一格的數值
            rulerView2.setRetainLength(0); //保留的小數位數
            rulerView2.setSelectedValue(REST_LEVEL_DEF); //要放下面
            //輸入框
            MAX_VALUE_2 = restMax;
            MIN_VALUE_2 = REST_LEVEL_MIN;

            //初始值
            workoutViewModel.selRestSpeedLevel.set(REST_LEVEL_DEF);
        }

        //休息時間設定
        MAX_VALUE_1 = REST_TIME_MAX;
        MIN_VALUE_1 = REST_TIME_MIN;
        isRule1Float = false;

        rulerView1.setMaxValue(REST_TIME_MAX);
        rulerView1.setMinValue(REST_TIME_MIN);
        rulerView1.setIntervalValue(1);
        rulerView1.setRetainLength(0);


        if (workoutViewModel.selRestTimeSec.get() != 0) {
            rulerView1.setSelectedValue(workoutViewModel.selRestTimeSec.get());
        } else {
            rulerView1.setSelectedValue(REST_TIME_DEF);
        }
//        rulerView1.setSelectedValue(REST_TIME_DEF);

        //休息時間
        //    workoutViewModel.selRestTimeSec.set(REST_TIME_DEF);

        getBinding().tvSpeedTitle.setText(speedTitle);
        getBinding().bottomRuleViewIcon.setBackgroundResource(bottomIcon);
    }

    private void initEvent() {
        getBinding().btnBack.setOnClickListener(v -> {
            if (CheckDoubleClick.isFastClick()) return;
            appStatusViewModel.changeMainButtonType(DISAPPEAR);
            //  appStatusViewModel.selectSetTimeFragmentNavigate(0);
            try {
                Navigation.findNavController(v).navigate(SetTimeStep2FragmentDirections.actionSetTimeStep2FragmentToProgramsFragment(WorkoutIntDef.DEFAULT_PROGRAM));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        appStatusViewModel.changeNavigate().observe(getViewLifecycleOwner(), action -> {
            if (action == STEP2_TO_STEP_3) {
                try {
                    Navigation.findNavController(requireView()).navigate(SetTimeStep2FragmentDirections.actionSetTimeStep2FragmentToSetTimeStep3Fragment(programsEnum));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (action == STEP2_TO_STEP_1) {
                try {
                    Navigation.findNavController(requireView()).navigate(SetTimeStep2FragmentDirections.actionSetTimeStep2FragmentToSetTimeFragment(programsEnum));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        appStatusViewModel.changeMainButtonType(PREVIOUS_AND_NEXT);

    }

    private void initView() {
        AppCompatImageButton btnTimeMinus = getBinding().btnTimeMinus;
        AppCompatImageButton btnTimePlus = getBinding().btnTimePlus;
        AppCompatImageButton btnSpeedPlus = getBinding().btnSpeedPlus;
        AppCompatImageButton btnSpeedMinus = getBinding().btnSpeedMinus;
        tvTimeNum = getBinding().tvTimeNum;
        tvSpeedNum = getBinding().tvSpeedNum;
        rulerView1 = getBinding().rulerViewTime;
        rulerView2 = getBinding().rulerViewSpeed;

        new CommonUtils().addAutoClick(btnTimePlus);

        new CommonUtils().addAutoClick(btnTimeMinus);
        new CommonUtils().addAutoClick(btnSpeedPlus);
        new CommonUtils().addAutoClick(btnSpeedMinus);

        rulerView1.setOnValueChangeListener((view, value) -> {
            tvTimeNum.setText(isRule1Float ? String.valueOf(value) : CommonUtils.subZeroAndDot(String.valueOf(value)));
            workoutViewModel.selRestTimeSec.set(Math.round(value));
        });

        btnTimePlus.setOnClickListener(v ->
                rulerView1.setSelectedValue(Float.parseFloat(tvTimeNum.getText().toString()) + (isRule1Float ? 0.1f : 1)));
        btnTimeMinus.setOnClickListener(v ->
                rulerView1.setSelectedValue(Float.parseFloat(tvTimeNum.getText().toString()) - (isRule1Float ? 0.1f : 1)));


        rulerView2.setOnValueChangeListener((view, value) -> {
            tvSpeedNum.setText(isRule2Float ? String.valueOf(value) : CommonUtils.subZeroAndDot(String.valueOf(value)));
            workoutViewModel.selRestSpeedLevel.set(isTreadmill ? getSpeedLevel(value) : Math.round(value));

            if (UNIT_E == IMPERIAL) {
                mphIU = value;
                mphMU = FormulaUtil.mph2kph(value);
            } else {
                mphIU = FormulaUtil.kph2mph(value);
                mphMU = value;
            }
        });

        btnSpeedPlus.setOnClickListener(v ->
                rulerView2.setSelectedValue(Float.parseFloat(tvSpeedNum.getText().toString()) + (isRule2Float ? 0.1f : 1)));
        btnSpeedMinus.setOnClickListener(v ->
                rulerView2.setSelectedValue(Float.parseFloat(tvSpeedNum.getText().toString()) - (isRule2Float ? 0.1f : 1)));


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


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}