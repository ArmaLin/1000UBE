package com.dyaco.spirit_commercial.maintenance_mode;

import static com.dyaco.spirit_commercial.App.UNIT_E;
import static com.dyaco.spirit_commercial.support.CommonUtils.showException;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.getMaxSpeedValue;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.getSpeedLevel;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.IMPERIAL;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.R;

import com.dyaco.spirit_commercial.UartConsoleManagerPF;
import com.dyaco.spirit_commercial.databinding.FragmentMaintenanceDriveMotorTestBinding;
import com.dyaco.spirit_commercial.support.CommonUtils;
import com.dyaco.spirit_commercial.support.base_component.BaseBindingDialogFragment;
import com.dyaco.spirit_commercial.support.interaction.LongClickUtil;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingViewModel;
import com.dyaco.spirit_commercial.viewmodel.WorkoutViewModel;
import com.dyaco.spirit_commercial.workout.programs.ProgramsEnum;


public class MaintenanceDriveMotorTestFragment extends BaseBindingDialogFragment<FragmentMaintenanceDriveMotorTestBinding> {

    public UartConsoleManagerPF uartConsole;
    public WorkoutViewModel w;
    private DeviceSettingViewModel deviceSettingViewModel;
    private MainActivity m;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    private CommonUtils commonUtils;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        commonUtils = new CommonUtils();
        initEvent();

        m = ((MainActivity) requireActivity());

        w = new ViewModelProvider(requireActivity()).get(WorkoutViewModel.class);

        deviceSettingViewModel = new ViewModelProvider(requireActivity()).get(DeviceSettingViewModel.class);
        getBinding().setDeviceSettingViewModel(deviceSettingViewModel);

        uartConsole = ((MainActivity) requireActivity()).uartConsole;

        getBinding().tvText2.setText(getString(R.string.Belt_Speed, UNIT_E == IMPERIAL ? getString(R.string.mph) : getString(R.string.km_h)));

        getBinding().tvText2Value.setText("0.0");



        LongClickUtil.attach(getBinding().ibMinus).observe(getViewLifecycleOwner(), unit -> {
            float position = 0.0f;
            try {
                position = Float.parseFloat(getBinding().tvText2Value.getText().toString());
            } catch (Exception e) {
               showException(e);
            }

            if (position > 0.0f) {
                position -= 0.1f;
            } else {
                position = getMaxSpeedValue(ProgramsEnum.MANUAL);
            }

            getBinding().tvText2Value.setText(commonUtils.formatDecimal(position, 1));

            testSpeed();
        });

        LongClickUtil.attach(getBinding().ibPlus).observe(getViewLifecycleOwner(), unit -> {
            float position = 0.0f;
            try {
                position = Float.parseFloat(getBinding().tvText2Value.getText().toString());
            } catch (Exception e) {
                showException(e);
            }

            if (position < getMaxSpeedValue(ProgramsEnum.MANUAL)) {
                position += 0.1f;
            } else {
                position = 0.0f;
            }
            getBinding().tvText2Value.setText(commonUtils.formatDecimal(position, 1));
            testSpeed();
        });



    }

    private void testSpeed() {

        try {
            float value = Float.parseFloat(getBinding().tvText2Value.getText().toString());
            w.currentSpeed.set(value);
            w.currentSpeedLevel.set(getSpeedLevel(w.currentSpeed.get()));
        } catch (Exception e) {

            Log.d("SpiritCommercialUart.TAG", "Exception:" + e.getLocalizedMessage());
            w.currentSpeed.set(0);
            w.currentSpeedLevel.set(0);
        }
        uartConsole.setDevDriveMotorTreadmill(0);

    }

//    private void initPick() {
//
//        List<String> list1 = new ArrayList<>(1);
//        for (int i = 0; i <= 999; i++) {
//            list1.add(String.valueOf(i));
//        }
//        OptionsPickerView<String> pickDriveMotorTest = getBinding().pickDriveMotorTest;
//        pickDriveMotorTest.setData(list1);
//        pickDriveMotorTest.setVisibleItems(8);
//        pickDriveMotorTest.setNormalItemTextColor(Color.parseColor("#5a7085"));
//        pickDriveMotorTest.setTextSize(54, true);
//        pickDriveMotorTest.setCurved(true);
//        pickDriveMotorTest.setCurvedArcDirection(WheelView.CURVED_ARC_DIRECTION_CENTER);
//        pickDriveMotorTest.setCurvedArcDirectionFactor(1.0f);
//        pickDriveMotorTest.setSelectedItemTextColor(Color.parseColor("#ffffff"));
//        pickDriveMotorTest.setCyclic(true);
//        pickDriveMotorTest.setOnOptionsSelectedListener((opt1Pos, opt1Data, opt2Pos, opt2Data, opt3Pos, opt3Data) -> {
//            if (opt1Data == null || opt3Data == null) {
//                return;
//            }
//            Log.d("@@@@@@@", "onOptionsSelected: two Linkage op1Pos=" + opt1Pos + ",op1Data=" + opt1Data + ",op2Pos=" + opt2Pos
//                    + ",op2Data=" + opt2Data + ",op3Pos=" + opt3Pos + ",op3Data=" + opt3Data);
//        });
//
//        pickDriveMotorTest.setOpt1SelectedPosition(20, false);
//
//        getBinding().ibMinus.setOnClickListener(v -> {
//            int position = pickDriveMotorTest.getOpt1SelectedPosition();
//
//            if (position > 0) {
//                position -= 1;
//            } else {
//                position = 999;
//            }
//
//            pickDriveMotorTest.setOpt1SelectedPosition(position, true);
//        });
//
//        getBinding().ibPlus.setOnClickListener(v -> {
//            int position = pickDriveMotorTest.getOpt1SelectedPosition();
//
//            if (position < 999) {
//                position += 1;
//            } else {
//                position = 0;
//            }
//
//            pickDriveMotorTest.setOpt1SelectedPosition(position, true);
//        });
//
//    }

    private void initEvent() {

        getBinding().btnDone.setOnClickListener(v -> {
            //退出時，speed要設定為0
            w.currentSpeed.set(0);
            w.currentSpeedLevel.set(0);
            uartConsole.setDevSpeedAndIncline();
            dismiss();
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //退出時，speed要設定為0
        if (w.currentSpeed.get() != 0) {
            w.currentSpeed.set(0);
            w.currentSpeedLevel.set(0);
            uartConsole.setDevSpeedAndIncline();
        }
    }
}