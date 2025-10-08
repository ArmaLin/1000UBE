package com.dyaco.spirit_commercial.maintenance_mode;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dyaco.spirit_commercial.databinding.FragmentMaintenanceAdvertisementBinding;
import com.dyaco.spirit_commercial.support.base_component.BaseBindingDialogFragment;


public class MaintenanceAdvertisementFragment extends BaseBindingDialogFragment<FragmentMaintenanceAdvertisementBinding> {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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