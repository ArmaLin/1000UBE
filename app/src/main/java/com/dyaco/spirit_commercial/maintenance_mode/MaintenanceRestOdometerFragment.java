package com.dyaco.spirit_commercial.maintenance_mode;

import static com.dyaco.spirit_commercial.support.intdef.EventKey.MAINTENANCE_ODO_RESET;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dyaco.spirit_commercial.databinding.FragmentMaintenanceRestOdometerBinding;
import com.dyaco.spirit_commercial.support.base_component.BaseBindingDialogFragment;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;
import com.dyaco.spirit_commercial.support.utils.CheckDoubleClick;
import com.jeremyliao.liveeventbus.LiveEventBus;


public class MaintenanceRestOdometerFragment extends BaseBindingDialogFragment<FragmentMaintenanceRestOdometerBinding> {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDialogType(GENERAL.TRANSLATION_Y);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initEvent();
    }


    private void initEvent() {
        getBinding().btnClose.setOnClickListener(v -> {
            if (CheckDoubleClick.isFastClick()) return;
            dismiss();
        });

        getBinding().btnReset.setOnClickListener(v -> {
            if (CheckDoubleClick.isFastClick()) return;
            LiveEventBus.get(MAINTENANCE_ODO_RESET).post(true);
            dismiss();
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}