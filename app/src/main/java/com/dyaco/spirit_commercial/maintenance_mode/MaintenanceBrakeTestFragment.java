package com.dyaco.spirit_commercial.maintenance_mode;

import static com.dyaco.spirit_commercial.MainActivity.isTreadmill;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.UartConsoleManagerPF;
import com.dyaco.spirit_commercial.databinding.FragmentMaintenanceBrakeTestBinding;
import com.dyaco.spirit_commercial.support.base_component.BaseBindingDialogFragment;
import com.dyaco.spirit_commercial.support.custom_view.wheelPicker.OptionsPickerView;
import com.dyaco.spirit_commercial.support.custom_view.wheelPicker.WheelView;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingViewModel;

import java.util.ArrayList;
import java.util.List;


public class MaintenanceBrakeTestFragment extends BaseBindingDialogFragment<FragmentMaintenanceBrakeTestBinding> {
    public UartConsoleManagerPF uartConsole;
    private DeviceSettingViewModel dsViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dsViewModel = new ViewModelProvider(requireActivity()).get(DeviceSettingViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initEvent();

        uartConsole = ((MainActivity) requireActivity()).uartConsole;

        if (isTreadmill) {
            getBinding().groupBike.setVisibility(View.GONE);
            getBinding().groupTreadmill.setVisibility(View.VISIBLE);

            initTreadmillBrake();

        } else {
            getBinding().groupBike.setVisibility(View.VISIBLE);
            getBinding().groupTreadmill.setVisibility(View.GONE);
            initBikeBrakeSelect();
        }
    }

    private void initTreadmillBrake() {
        getBinding().tvTip.setText(R.string.Try_turning_brake_on_and_off_to_check_its_response);
        getBinding().cbBrakeTest.setOnCheckedChangeListener((buttonView, isChecked) -> {
//                if (isChecked) {
//                    getBinding().noBtIcon.setVisibility(View.GONE);
//                    getBinding().noBtText.setVisibility(View.GONE);
//                } else {
//                    getBinding().noBtIcon.setVisibility(View.VISIBLE);
//                    getBinding().noBtText.setVisibility(View.VISIBLE);
//                }
        });
    }

    int max = 2000;

    @SuppressWarnings("unchecked")
    private void initBikeBrakeSelect() {
        getBinding().tvTip.setText(R.string.Try_changing_resistance);

        List<String> list1 = new ArrayList<>(1);
        for (int i = 0; i <= max; i++) {
            list1.add(String.valueOf(i));
        }
        OptionsPickerView<String> pickBrake = getBinding().pickBrake;
        pickBrake.setData(list1);
        pickBrake.setVisibleItems(8);
        pickBrake.setNormalItemTextColor(Color.parseColor("#5a7085"));
        pickBrake.setTextSize(54, false);
        pickBrake.setCurved(true);
        pickBrake.setCurvedArcDirection(WheelView.CURVED_ARC_DIRECTION_CENTER);
        pickBrake.setCurvedArcDirectionFactor(1.0f);
        pickBrake.setSelectedItemTextColor(Color.parseColor("#ffffff"));
        pickBrake.setCyclic(true);
        pickBrake.setOnOptionsSelectedListener((opt1Pos, opt1Data, opt2Pos, opt2Data, opt3Pos, opt3Data) -> {
            if (opt1Data == null) return;

            int d = Integer.parseInt(opt1Data);

//            uartConsole.setEmsBrakeTest(d / 10);
            uartConsole.setEmsBrakeTest(d);

            Log.d("FFFFFFFFF", ""+d +","+ d/10);

//            try {
//                int x = MODE.getLevelAD()[d - 1];
//                Log.d("FFFFFFFFF", "LEVEL:"+(d-1)+", AD:" + x);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        });

        pickBrake.setOpt1SelectedPosition(0, false);
        uartConsole.setEmsBrakeTest(0);

        getBinding().ibMinus.setOnClickListener(v -> {
            int position = pickBrake.getOpt1SelectedPosition();

            if (position > 0) {
                position -= 1;
            } else {
                position = max;
            }

            pickBrake.setOpt1SelectedPosition(position, true);
        });

        getBinding().ibPlus.setOnClickListener(v -> {
            int position = pickBrake.getOpt1SelectedPosition();

            if (position < max) {
                position += 1;
            } else {
                position = 0;
            }

            pickBrake.setOpt1SelectedPosition(position, true);
        });

    }


    private void initEvent() {

        getBinding().btnDone.setOnClickListener(v -> {

            uartConsole.setEmsBrakeTest(0);
            dismiss();
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}