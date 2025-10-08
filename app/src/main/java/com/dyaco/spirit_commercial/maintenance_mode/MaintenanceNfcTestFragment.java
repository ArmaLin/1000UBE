package com.dyaco.spirit_commercial.maintenance_mode;

import static com.dyaco.spirit_commercial.App.getDeviceGEM;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.EVENT_NFC_READ;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.corestar.libs.device.DeviceGEM;
import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.databinding.FragmentMaintenanceNfcTestBinding;
import com.dyaco.spirit_commercial.support.RxTimer;
import com.dyaco.spirit_commercial.support.base_component.BaseBindingDialogFragment;
import com.jeremyliao.liveeventbus.LiveEventBus;


public class MaintenanceNfcTestFragment extends BaseBindingDialogFragment<FragmentMaintenanceNfcTestBinding> {

    private RxTimer rxTimer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        getDeviceGEM().nfcMessageEnableNfcRadio(DeviceGEM.NFC_READ_EVENT.NFC_READ);


        rxTimer = new RxTimer();
        rxTimer.timer(2000, number ->
                getDeviceGEM().nfcMessageEnableNfcRadio(DeviceGEM.NFC_READ_EVENT.NFC_READ));

        initEvent();
    }


    private void initEvent() {


//        ((MainActivity) requireActivity()).nfcReadListener = data -> {
//            Log.d("NFCCCCC", "onNfcRead: ");
//
//        };

        getBinding().btnDone.setOnClickListener(v -> dismiss());


        getBinding().btnAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getBinding().groupNfc.setVisibility(View.GONE);
                getBinding().groupReadyNfc.setVisibility(View.VISIBLE);
            }
        });

        LiveEventBus.get(EVENT_NFC_READ, String.class).observe(getViewLifecycleOwner(), s -> {

            if (!isAdded()) return;

            ((MainActivity)requireActivity()).uartConsole.setBuzzer();

            requireActivity().runOnUiThread(() -> {
                if (getBinding() != null) {
                    getBinding().tvNfcNumber.setText(s);
                    getBinding().groupNfc.setVisibility(View.VISIBLE);
                    getBinding().groupReadyNfc.setVisibility(View.GONE);
                }

            });
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (rxTimer != null) {
            rxTimer.cancel();
            rxTimer = null;
        }
        getDeviceGEM().nfcMessageDisableNfcRadio();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}