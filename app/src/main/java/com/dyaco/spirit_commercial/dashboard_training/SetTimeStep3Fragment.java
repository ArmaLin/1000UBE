package com.dyaco.spirit_commercial.dashboard_training;

import static com.dyaco.spirit_commercial.support.intdef.EventKey.ON_STOP_BACK_TO_MAIN_TRAINING;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.STEP3_TO_STEP_2;
import static com.dyaco.spirit_commercial.support.intdef.MainDashboardBottomButtonIntDef.DISAPPEAR;
import static com.dyaco.spirit_commercial.support.intdef.MainDashboardBottomButtonIntDef.PREVIOUS_AND_START;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.databinding.FragmentSetTimeStep3Binding;
import com.dyaco.spirit_commercial.settings.KeyboardWindow;
import com.dyaco.spirit_commercial.support.CommonUtils;
import com.dyaco.spirit_commercial.support.MsgEvent;
import com.dyaco.spirit_commercial.support.base_component.BaseBindingFragment;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.intdef.WorkoutIntDef;
import com.dyaco.spirit_commercial.viewmodel.AppStatusViewModel;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingViewModel;
import com.dyaco.spirit_commercial.viewmodel.WorkoutViewModel;
import com.dyaco.spirit_commercial.workout.programs.ProgramsEnum;
import com.jeremyliao.liveeventbus.LiveEventBus;

public class SetTimeStep3Fragment extends BaseBindingFragment<FragmentSetTimeStep3Binding> {

    private ProgramsEnum programsEnum;

    private AppStatusViewModel appStatusViewModel;
    private WorkoutViewModel workoutViewModel;
    private DeviceSettingViewModel deviceSettingViewModel;
    private final int DEFAULT_INTERVALS = 10;

    public SetTimeStep3Fragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.isOnStopBackToMainTraining = true;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        appStatusViewModel = new ViewModelProvider(requireActivity()).get(AppStatusViewModel.class);
        workoutViewModel = new ViewModelProvider(requireActivity()).get(WorkoutViewModel.class);
        deviceSettingViewModel = new ViewModelProvider(requireActivity()).get(DeviceSettingViewModel.class);

        programsEnum = SetTimeFragmentArgs.fromBundle(getArguments()).getProgramEnum();
        getBinding().tvProgramName.setText(programsEnum.getProgramName());

        initView();
        initEvent();

        initProgram();

        //  LiveEventBus.get(EVENT_SET_UNIT).observe(getViewLifecycleOwner(), s -> initProgram());


        LiveEventBus.get(ON_STOP_BACK_TO_MAIN_TRAINING, Boolean.class).observe(getViewLifecycleOwner(), s -> {
            if (getBinding() != null) {
                getBinding().btnBack.callOnClick();
            }
        });
    }

    private void initProgram() {
        workoutViewModel.selIntervals.set(DEFAULT_INTERVALS);

        getEstimatedTime();
    }

    private void setRulerView1Data(float value) {
        getBinding().tvTimeNum.setText(CommonUtils.subZeroAndDot(String.valueOf(value)));
        workoutViewModel.selIntervals.set(Math.round(value));
        getEstimatedTime();

//        Log.d("WWWWWW", "@selRestSpeed:" + workoutViewModel.selRestSpeedLevel.get());
//        Log.d("WWWWWW", "@selRestTimeSec:" + workoutViewModel.selRestTimeSec.get());
//        Log.d("WWWWWW", "@selSprintTimeSec:" + workoutViewModel.selSprintTimeSec.get());
//        Log.d("EEEEEEE", "@selSprintSpeed:" + workoutViewModel.selSprintSpeedLevel.get());
//        Log.d("WWWWWW", "@selIntervals:" + workoutViewModel.selIntervals.get());
    }

    private void initEvent() {
        getBinding().btnBack.setOnClickListener(v -> {
            appStatusViewModel.changeMainButtonType(DISAPPEAR);
            //  appStatusViewModel.selectSetTimeFragmentNavigate(0);
            Navigation.findNavController(v).navigate(SetTimeStep3FragmentDirections.actionSetTimeStep3FragmentToProgramsFragment(WorkoutIntDef.DEFAULT_PROGRAM));
        });

        appStatusViewModel.changeNavigate().observe(getViewLifecycleOwner(), action -> {
            if (action == STEP3_TO_STEP_2) {
                Navigation.findNavController(requireView()).navigate(SetTimeStep3FragmentDirections.actionSetTimeStep3FragmentToSetTimeStep2Fragment(programsEnum));
            }
        });

        appStatusViewModel.changeMainButtonType(PREVIOUS_AND_START);
    }

    private void getEstimatedTime() {
//        workoutViewModel.selSprintTimeSec.set(2);
//        workoutViewModel.selRestTimeSec.set(3);

        //estimated time = sprint time * interval + rest time * (interval - 1)
        workoutViewModel.selWorkoutTime.set(workoutViewModel.selSprintTimeSec.get() * workoutViewModel.selIntervals.get() + (workoutViewModel.selRestTimeSec.get() * (workoutViewModel.selIntervals.get() - 1)));
        getBinding().bottomViewEstimatedTime.setText(CommonUtils.formatSecToM(workoutViewModel.selWorkoutTime.get()));

    }

    private void initView() {

        getBinding().rulerViewTime.setSelectedValue(DEFAULT_INTERVALS);


        new CommonUtils().addAutoClick(getBinding().btnTimePlus);
        new CommonUtils().addAutoClick(getBinding().btnTimeMinus);

        getBinding().rulerViewTime.setOnValueChangeListener((view, value) -> {
            setRulerView1Data(value);
        });

        getBinding().btnTimePlus.setOnClickListener(v ->
                getBinding().rulerViewTime.setSelectedValue(Float.parseFloat(getBinding().tvTimeNum.getText().toString()) + 1));
        getBinding().btnTimeMinus.setOnClickListener(v ->
                getBinding().rulerViewTime.setSelectedValue(Float.parseFloat(getBinding().tvTimeNum.getText().toString()) - 1));

        getBinding().tvTimeNum.setOnClickListener(v -> {
            String title = getBinding().tvTimeTitle.getText().toString();
            parent.popupWindow = new KeyboardWindow(requireActivity(), false, getBinding().tvTimeNum.getText().toString(), 15, 3, title);
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}