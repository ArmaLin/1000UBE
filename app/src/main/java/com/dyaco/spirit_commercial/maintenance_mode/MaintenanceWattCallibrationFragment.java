package com.dyaco.spirit_commercial.maintenance_mode;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dyaco.spirit_commercial.databinding.FragmentMaintenanceWattCalibrationBinding;
import com.dyaco.spirit_commercial.support.base_component.BaseBindingDialogFragment;
import com.dyaco.spirit_commercial.support.custom_view.wheelPicker.OptionsPickerView;
import com.dyaco.spirit_commercial.support.custom_view.wheelPicker.WheelView;

import java.util.ArrayList;
import java.util.List;


public class MaintenanceWattCallibrationFragment extends BaseBindingDialogFragment<FragmentMaintenanceWattCalibrationBinding> {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initEvent();

        initPick();
    }

    private void initPick() {

        List<String> list1 = new ArrayList<>(1);
        for (int i = 0; i <= 999; i++) {
            list1.add(String.valueOf(i));
        }
        OptionsPickerView<String> pickWattCallibration = getBinding().pickWattCallibration;
        pickWattCallibration.setData(list1);
        pickWattCallibration.setVisibleItems(8);
        pickWattCallibration.setNormalItemTextColor(Color.parseColor("#5a7085"));
        pickWattCallibration.setTextSize(54, false);
        pickWattCallibration.setCurved(true);
        pickWattCallibration.setCurvedArcDirection(WheelView.CURVED_ARC_DIRECTION_CENTER);
        pickWattCallibration.setCurvedArcDirectionFactor(1.0f);
        pickWattCallibration.setSelectedItemTextColor(Color.parseColor("#ffffff"));
        pickWattCallibration.setCyclic(true);
        pickWattCallibration.setOnOptionsSelectedListener((opt1Pos, opt1Data, opt2Pos, opt2Data, opt3Pos, opt3Data) -> {
            if (opt1Data == null || opt3Data == null) {
                return;
            }
            Log.d("@@@@@@@", "onOptionsSelected: two Linkage op1Pos=" + opt1Pos + ",op1Data=" + opt1Data + ",op2Pos=" + opt2Pos
                    + ",op2Data=" + opt2Data + ",op3Pos=" + opt3Pos + ",op3Data=" + opt3Data);
        });

        pickWattCallibration.setOpt1SelectedPosition(20, false);

        getBinding().ibMinus.setOnClickListener(v -> {
            int position = pickWattCallibration.getOpt1SelectedPosition();

            if (position > 0) {
                position -= 1;
            } else {
                position = 999;
            }

            pickWattCallibration.setOpt1SelectedPosition(position, true);
        });

        getBinding().ibPlus.setOnClickListener(v -> {
            int position = pickWattCallibration.getOpt1SelectedPosition();

            if (position < 999) {
                position += 1;
            } else {
                position = 0;
            }

            pickWattCallibration.setOpt1SelectedPosition(position, true);
        });

    }


    private void initEvent() {

        getBinding().btnDone.setOnClickListener(v -> dismiss());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}