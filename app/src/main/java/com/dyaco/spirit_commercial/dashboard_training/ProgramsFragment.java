package com.dyaco.spirit_commercial.dashboard_training;

import static com.dyaco.spirit_commercial.MainActivity.isTreadmill;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.getArmyTarget;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.getMarinesTarget;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.getNavyTarget;
import static com.dyaco.spirit_commercial.support.intdef.AppStatusIntDef.CURRENT_PAGE_MEDIA;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.EVENT_SET_UNIT;
import static com.dyaco.spirit_commercial.support.intdef.UnitEnum.DISTANCE;
import static com.dyaco.spirit_commercial.support.intdef.UnitEnum.getUnit;
import static com.dyaco.spirit_commercial.support.utils.MyAnimationUtils.crossFade2;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.FragmentProgramsBinding;
import com.dyaco.spirit_commercial.support.base_component.BaseBindingFragment;
import com.dyaco.spirit_commercial.support.intdef.WorkoutIntDef;
import com.dyaco.spirit_commercial.support.utils.CheckDoubleClick;
import com.dyaco.spirit_commercial.viewmodel.AppStatusViewModel;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingViewModel;
import com.dyaco.spirit_commercial.viewmodel.WorkoutViewModel;
import com.dyaco.spirit_commercial.workout.programs.ProgramsEnum;
import com.jeremyliao.liveeventbus.LiveEventBus;

public class ProgramsFragment extends BaseBindingFragment<FragmentProgramsBinding> implements View.OnClickListener {

    int programType = WorkoutIntDef.DEFAULT_PROGRAM;
    private AppStatusViewModel appStatusViewModel;
    public DeviceSettingViewModel deviceSettingViewModel;
    private WorkoutViewModel workoutViewModel;

    public ProgramsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView();
        initEvent();

        getBinding().setIsTreadmill(isTreadmill);

        workoutViewModel = new ViewModelProvider(requireActivity()).get(WorkoutViewModel.class);
        appStatusViewModel = new ViewModelProvider(requireActivity()).get(AppStatusViewModel.class);
        //   appStatusViewModel.selectSetTimeFragmentNavigate(0);

        deviceSettingViewModel = new ViewModelProvider(requireActivity()).get(DeviceSettingViewModel.class);
        getBinding().setDeviceSetting(deviceSettingViewModel);

        programType = ProgramsFragmentArgs.fromBundle(getArguments()).getProgramType();

        workoutViewModel.setSelProgram(ProgramsEnum.MANUAL);

        if (programType == WorkoutIntDef.DEFAULT_PROGRAM) {
            getBinding().rbDefaultPrograms.setChecked(true);
        } else {
            getBinding().rbFitnessTests.setChecked(true);
            getBinding().viewDefaultPrograms.setVisibility(View.GONE);
            getBinding().viewFitnessTests.setVisibility(View.VISIBLE);
        }

        LiveEventBus.get(EVENT_SET_UNIT).observe(getViewLifecycleOwner(), s -> initView());

    }

    private void initEvent() {

        getBinding().btnManual.setOnClickListener(this);
        getBinding().btnHill.setOnClickListener(this);
        getBinding().btn5KRun.setOnClickListener(this);
        getBinding().btnHeartRate.setOnClickListener(this);
        getBinding().btnCustom.setOnClickListener(this);

//        getBinding().btnInterval.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                xxxxWindow popupWindow = new xxxxWindow(requireActivity());
//                popupWindow.showAtLocation(requireActivity().getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);
//            }
//        });

        getBinding().btnWatts.setOnClickListener(this);

        getBinding().btnAirForce.setOnClickListener(this);
        getBinding().btnArmy.setOnClickListener(this);
        getBinding().btnCoastGuard.setOnClickListener(this);
        getBinding().btnGerkin.setOnClickListener(this);
        getBinding().btnPEB.setOnClickListener(this);
        getBinding().btnMarineCorps.setOnClickListener(this);
        getBinding().btnNavy.setOnClickListener(this);
        getBinding().btnWFI.setOnClickListener(this);
        getBinding().btnCTTPrediction.setOnClickListener(this);
        getBinding().btnCTTPerformance.setOnClickListener(this);

        getBinding().btnBack.setOnClickListener(this);

        getBinding().rgSelectProgram.setOnCheckedChangeListener((group, checkedId) -> {

            if (checkedId == R.id.rbDefaultPrograms) {
                crossFade2(getBinding().viewDefaultPrograms, getBinding().viewFitnessTests,100);
                programType = WorkoutIntDef.DEFAULT_PROGRAM;

            } else {
                crossFade2(getBinding().viewFitnessTests, getBinding().viewDefaultPrograms,100);
                programType = WorkoutIntDef.FITNESS_TESTS;
            }
        });
    }


    private void initView() {
        getBinding().airForceUnit.setText(String.format("%s %s", getNavyTarget(), getString(getUnit(DISTANCE))));
        getBinding().armyUnit.setText(String.format("%s %s", getArmyTarget(), getString(getUnit(DISTANCE))));
        getBinding().coastGuardUnit.setText(String.format("%s %s", getNavyTarget(), getString(getUnit(DISTANCE))));
        getBinding().marineCorpsUnit.setText(String.format("%s %s", getMarinesTarget(), getString(getUnit(DISTANCE))));
        getBinding().navyUnit.setText(String.format("%s %s", getNavyTarget(), getString(getUnit(DISTANCE))));
        getBinding().pebUnit.setText(String.format("%s %s", getNavyTarget(), getString(getUnit(DISTANCE))));
    }

    @Override
    public void onClick(View v) {

        if (CheckDoubleClick.isFastClick()) return;

        if (v.getId() == R.id.btnBack) {
            Navigation.findNavController(v).navigate(ProgramsFragmentDirections.actionProgramsFragmentToDashboardTrainingFragment());
        } else {

            if (appStatusViewModel.currentPage.get() == CURRENT_PAGE_MEDIA) return;

            ((MainActivity)requireActivity()).customBean.setDiagramIncline("");
            ((MainActivity)requireActivity()).customBean.setDiagramLevelOrSpeed("");
            ((MainActivity)requireActivity()).customBean.setTotalTime(-1);
            ((MainActivity)requireActivity()).customBean.setMaxSpeedKmh(0);
            ((MainActivity)requireActivity()).customBean.setMaxSpeedMph(0);

            NavDirections navDirections = ProgramsFragmentDirections.actionProgramsFragmentToProgramsBannerFragment(v.getId(), programType);
            Navigation.findNavController(v).navigate(navDirections);
        }
    }
}