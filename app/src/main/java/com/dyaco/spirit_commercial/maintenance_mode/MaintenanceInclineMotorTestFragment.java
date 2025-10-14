package com.dyaco.spirit_commercial.maintenance_mode;

import static com.dyaco.spirit_commercial.App.getDeviceSpiritC;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.corestar.libs.device.DeviceDyacoMedical;
import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.UartConsoleManagerPF;
import com.dyaco.spirit_commercial.databinding.FragmentMaintenanceInclineMotorTestBinding;
import com.dyaco.spirit_commercial.support.RxTimer;
import com.dyaco.spirit_commercial.support.base_component.BaseBindingDialogFragment;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingViewModel;
import com.dyaco.spirit_commercial.viewmodel.WorkoutViewModel;

import java.util.ArrayList;
import java.util.List;


public class MaintenanceInclineMotorTestFragment extends BaseBindingDialogFragment<FragmentMaintenanceInclineMotorTestBinding> {
    public UartConsoleManagerPF uartConsole;
    public WorkoutViewModel workoutViewModel;
    private List<Integer> frontInclineAdList;
    private MainActivity m;
    private DeviceSettingViewModel deviceSettingViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDialogType(GENERAL.TRANSLATION_X);
        m = ((MainActivity) requireActivity());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        deviceSettingViewModel = new ViewModelProvider(requireActivity()).get(DeviceSettingViewModel.class);
        getBinding().setDeviceSettingViewModel(deviceSettingViewModel);

        workoutViewModel = new ViewModelProvider(requireActivity()).get(WorkoutViewModel.class);
        frontInclineAdList = workoutViewModel.getFrontInclineAdList();

        uartConsole = ((MainActivity) requireActivity()).uartConsole;
        //  uartConsole.setDevStep(DS_CALIBRATION_STANDBY);


        getBinding().etDA.setTransformationMethod(HideReturnsTransformationMethod.getInstance());


        getBinding().etDA.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

        initEvent();

    }


    private int highLightTag = 0;

    private void filterChange(int tag) {
        getBinding().page1.setVisibility(tag == 0 ? View.VISIBLE : View.GONE);
        getBinding().page2.setVisibility(tag == 0 ? View.GONE : View.VISIBLE);

        if (tag == 0) {
            getBinding().etDA.requestFocus();
        }
    }

//    private void initPick() {
//
//        List<String> list1 = new ArrayList<>(1);
//        for (int i = 0; i <= 30; i++) {
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
//
//        pickDriveMotorTest.setOpt1SelectedPosition(20, false);
//
//        pickDriveMotorTest.setOnOptionsSelectedListener((opt1Pos, opt1Data, opt2Pos, opt2Data, opt3Pos, opt3Data) -> {
//
//            if (opt1Data == null) return;
//
//            updateIncline(opt1Pos);
//        });
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
//
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
//
//        });
//    }

    private void updateIncline(int level) {

        int ad = frontInclineAdList.get(level);

        getDeviceSpiritC().setTargetSpeedAndInclineTreadmill(DeviceDyacoMedical.MOTOR_DIRECTION.FORWARD, 0, ad, 0);
    }


    private void initEvent() {

        getBinding().rgFilter.setOnCheckedChangeListener((group, checkedId) -> {
            ArrayList<RadioButton> listOfRadioButtons = new ArrayList<>();
            for (int i = 0; i < getBinding().rgFilter.getChildCount(); i++) {
                View o = getBinding().rgFilter.getChildAt(i);
                if (o instanceof RadioButton) {
                    listOfRadioButtons.add((RadioButton) o);
                    if (listOfRadioButtons.get(i).getId() == checkedId) {
                        filterChange(Integer.parseInt((String) listOfRadioButtons.get(i).getTag()));
                        //   listOfRadioButtons.get(i).setTextColor(ContextCompat.getColor(requireActivity(), R.color.color1396ef));
                    }
                }
            }
        });


        getBinding().btnCallibration.setOnClickListener(view -> {


//            if (rxTimer != null) {
//                rxTimer.cancel();
//                rxTimer = null;
//            } else {
//                rxTimer = new RxTimer();
//                rxTimer.timer(1000 * 10, number -> {
//                    if (rocketAnimation != null) {
//                        rocketAnimation.stop();
//                        rocketAnimation = null;
//                        getBinding().btnCallibration.setVisibility(View.VISIBLE);
//                        getBinding().btnDone.setEnabled(true);
//                        getBinding().ivLoading.setVisibility(View.INVISIBLE);
//                    }
//                });
//            }

         //   showLoading(true);

            m.showLoading2(true);
            uartConsole.startDevCalibration(DeviceDyacoMedical.TREADMILL_CALI_MODE.FRONT);
        });


        getBinding().btnApply.setOnClickListener(view -> {
            int da = 0;
            if (getBinding().etDA.getText() != null && !"".contentEquals(getBinding().etDA.getText())) {
                da = Integer.parseInt(getBinding().etDA.getText().toString());
            }

            if (da < 2000 || da > 30000) {
                da = 2000;
                getBinding().etDA.setText(String.valueOf(da));
            }

            getDeviceSpiritC().setTargetSpeedAndInclineTreadmill(DeviceDyacoMedical.MOTOR_DIRECTION.FORWARD, 0, da, 0);
        });


        getBinding().btnDone.setOnClickListener(v -> {
//            uartConsole.setDevStep(DS_BB_CALIBRATION_STOP_RSP);
//            getDeviceSpiritC().setInclineCaliModeTreadmill(DeviceSpiritC.TREADMILL_CALI_MODE.STOP);
            uartConsole.setDevMainMode(DeviceDyacoMedical.MAIN_MODE.ENG);
            dismiss();
        });
    }

    AnimationDrawable rocketAnimation;
    RxTimer rxTimer;

    private void showLoading(boolean isShow) {
        if (isShow) {

            if (rocketAnimation != null) {
                rocketAnimation.stop();
                rocketAnimation = null;
            }

            getBinding().ivLoading.setBackgroundResource(R.drawable.load_animation);
            rocketAnimation = (AnimationDrawable) getBinding().ivLoading.getBackground();
            rocketAnimation.start();
        } else {
            if (rocketAnimation != null) {
                rocketAnimation.stop();
                rocketAnimation = null;
            }
        }

        getBinding().btnCallibration.setVisibility(isShow ? View.INVISIBLE : View.VISIBLE);
        getBinding().btnDone.setEnabled(!isShow);
        getBinding().btnCallibration.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        if (rocketAnimation != null) {
//            rocketAnimation.stop();
//            rocketAnimation = null;
//        }
//
//        if (rxTimer != null) {
//            rxTimer.cancel();
//            rxTimer = null;
//        }
    }
}