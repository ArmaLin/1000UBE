package com.dyaco.spirit_commercial.maintenance_mode;

import static com.dyaco.spirit_commercial.support.CommonUtils.ignoringExc;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.PROTOCOL_CSAFE;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.EVENT_CSAFE_ECHO_TEST;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.corestar.libs.device.DeviceCsafe;
import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.databinding.FragmentMaintenanceCommunicationTestBinding;
import com.dyaco.spirit_commercial.listener.CsafeDeviceEventListener;
import com.dyaco.spirit_commercial.support.base_component.BaseBindingDialogFragment;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingViewModel;
import com.jeremyliao.liveeventbus.LiveEventBus;

import es.dmoral.toasty.Toasty;


public class MaintenanceCommunicationTestFragment extends BaseBindingDialogFragment<FragmentMaintenanceCommunicationTestBinding> {
    private DeviceCsafe deviceCsafe;
    private DeviceSettingViewModel deviceSettingViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        deviceSettingViewModel = new ViewModelProvider(requireActivity()).get(DeviceSettingViewModel.class);

        // ((MainActivity) requireActivity()).initCSAFE();

        if (deviceSettingViewModel.protocol.getValue() != PROTOCOL_CSAFE) {
            getBinding().btnRunTest1.setEnabled(false);
            getBinding().btnRunTest1.setClickable(false);
        }

        deviceCsafe = ((MainActivity) requireActivity()).deviceCsafe;
        initEvent();

        LiveEventBus.get(EVENT_CSAFE_ECHO_TEST, DeviceCsafe.ECHO_STATUS.class).observe(getViewLifecycleOwner(), c -> {
            Log.d(CsafeDeviceEventListener.TAG, "EVENT_CSAFE_ECHO_TEST: " + c);
            String result = "TEST " + DeviceCsafe.ECHO_STATUS.FAIL.name() + "   ";
            try {
                getBinding().progress.setVisibility(View.GONE);
                result = "TEST " + c.name() + "   ";
                if (c == DeviceCsafe.ECHO_STATUS.OK) {
                    Toasty.success(requireActivity(), result, Toasty.LENGTH_LONG).show();
                } else {
                    Toasty.error(requireActivity(), result, Toasty.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toasty.error(requireActivity(), result, Toasty.LENGTH_LONG).show();
            }

            // adapter.changeUnit();
        });
    }

    private void initEvent() {
        getBinding().btnRunTest1.setOnClickListener(v -> {

            Log.d(CsafeDeviceEventListener.TAG, "btnRunTest1: ");

//            try {
//                deviceCsafe.echoTest();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

            ignoringExc(CsafeDeviceEventListener.TAG, () -> {
                getBinding().progress.setVisibility(View.VISIBLE);
                deviceCsafe.echoTest();
            });

//            TestingWindow testingWindow = new TestingWindow(requireActivity());
//            testingWindow.showAtLocation(requireActivity().getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);
//            testingWindow.setOnCustomDismissListener(new BasePopupWindow.OnCustomDismissListener() {
//                @Override
//                public void onStartDismiss(MsgEvent value) {
//                }
//
//                @Override
//                public void onDismiss() {
//                }
//            });
        });

        getBinding().btnDone.setOnClickListener(v -> dismiss());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}