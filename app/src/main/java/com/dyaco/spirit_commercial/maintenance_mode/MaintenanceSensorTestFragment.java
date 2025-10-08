package com.dyaco.spirit_commercial.maintenance_mode;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.dyaco.spirit_commercial.databinding.FragmentMaintenanceSensorTestBinding;
import com.dyaco.spirit_commercial.support.base_component.BaseBindingDialogFragment;
import com.dyaco.spirit_commercial.viewmodel.WorkoutViewModel;


public class MaintenanceSensorTestFragment extends BaseBindingDialogFragment<FragmentMaintenanceSensorTestBinding> {
    private WorkoutViewModel workoutViewModel;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        workoutViewModel = new ViewModelProvider(requireActivity()).get(WorkoutViewModel.class);
        getBinding().setWorkoutData(workoutViewModel);
        initEvent();
    }


    private void initEvent() {

        getBinding().btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismiss();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}