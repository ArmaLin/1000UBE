package com.dyaco.spirit_commercial.maintenance_mode;

import static com.corestar.libs.device.DeviceSpiritC.KEY.KEY00;
import static com.corestar.libs.device.DeviceSpiritC.KEY.KEY01;
import static com.corestar.libs.device.DeviceSpiritC.KEY.KEY02;
import static com.corestar.libs.device.DeviceSpiritC.KEY.KEY03;
import static com.corestar.libs.device.DeviceSpiritC.KEY.KEY04;
import static com.corestar.libs.device.DeviceSpiritC.KEY.KEY05;
import static com.corestar.libs.device.DeviceSpiritC.KEY.KEY06;
import static com.dyaco.spirit_commercial.MainActivity.isTreadmill;
import static com.dyaco.spirit_commercial.support.CommonUtils.castList;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.FTMS_SET_TARGET_INCLINATION;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.FTMS_SET_TARGET_SPEED;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.FTMS_START_OR_RESUME;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.FTMS_STOP_OR_PAUSE;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.KEY_ENTER;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.KEY_UNKNOWN;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.ON_MULTI_KEY;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.CLICK_MINUS;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.CLICK_PLUS;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.lifecycle.ViewModelProvider;

import com.corestar.libs.device.DeviceSpiritC;
import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.databinding.FragmentMaintenanceKeyTestBinding;
import com.dyaco.spirit_commercial.support.RxTimer;
import com.dyaco.spirit_commercial.support.base_component.BaseBindingDialogFragment;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingViewModel;
import com.jeremyliao.liveeventbus.LiveEventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class MaintenanceKeyTestFragment extends BaseBindingDialogFragment<FragmentMaintenanceKeyTestBinding> {
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

        if (isTreadmill) {
            getBinding().viewKeyTestTreadmill.setVisibility(View.VISIBLE);
            getBinding().viewKeyTestBike.setVisibility(View.GONE);

            initTREADMILL();
        } else {
            getBinding().viewKeyTestTreadmill.setVisibility(View.GONE);
            getBinding().viewKeyTestBike.setVisibility(View.VISIBLE);
            initBike();
        }
    }

    private void initBike() {

        map.put(KEY02, getBinding().btnKeyStart2);
        map.put(KEY03, getBinding().btnKeyStop2);
        map.put(KEY04, getBinding().btnKeyEnter2);

        map.put(KEY05, getBinding().btnSpeedMinus2);
        map.put(KEY06, getBinding().btnSpeedPlus2);


//        map.put(KEY05, getBinding().btnSpeedPlus2);
//        map.put(KEY06, getBinding().btnSpeedMinus2);
    }

    Map<DeviceSpiritC.KEY, View> map = new HashMap<>();

    private void initTREADMILL() {

//        for (int id : getBinding().groupTreadmillViewIncline.getReferencedIds()) {
//            view.findViewById(id).setClickable(false);
//        }
        map.put(KEY00, getBinding().btnInclinePlus);
        map.put(KEY01, getBinding().btnInclineMinus);
        map.put(KEY02, getBinding().btnKeyStart);
        map.put(KEY03, getBinding().btnKeyEnter);
        map.put(KEY04, getBinding().btnKeyStop);
        map.put(KEY05, getBinding().btnSpeedMinus);
        map.put(KEY06, getBinding().btnSpeedPlus);



    }

    private void commandButton(AppCompatImageButton button) {
        button.performClick();
        button.setPressed(true);
        new RxTimer().timer(50, n -> button.setPressed(false));
        ((MainActivity)requireActivity()).uartConsole.setBuzzer();
    }

    private void initEvent() {
        LiveEventBus.get(ON_MULTI_KEY).observe(getViewLifecycleOwner(), s -> {

            List<DeviceSpiritC.KEY> list = castList(s, DeviceSpiritC.KEY.class);
            if (list == null) return;
            for (DeviceSpiritC.KEY key : list) {
                if (map.containsKey(key)) {
                    Objects.requireNonNull(map.get(key)).setPressed(true);
                }
            }
        });


        LiveEventBus.get(KEY_ENTER).observe(getViewLifecycleOwner(), s -> {
            if (isTreadmill) {
                commandButton(getBinding().btnKeyEnter);
            } else {
                commandButton(getBinding().btnKeyEnter2);
            }
        });

        LiveEventBus.get(FTMS_START_OR_RESUME).observe(getViewLifecycleOwner(), s -> {
            if (isTreadmill) {
                commandButton(getBinding().btnKeyStart);
            } else {
                commandButton(getBinding().btnKeyStart2);
            }
        });

        LiveEventBus.get(FTMS_STOP_OR_PAUSE).observe(getViewLifecycleOwner(), s -> {
            if (isTreadmill) {
                commandButton(getBinding().btnKeyStop);
            } else {
                commandButton(getBinding().btnKeyStop2);
            }
        });

        //FTMS控制SPEED
        LiveEventBus.get(FTMS_SET_TARGET_SPEED).observe(getViewLifecycleOwner(), s -> {
            Integer n = (Integer) s;
            if (n == CLICK_PLUS) {// 實體按鍵 +1
                if (isTreadmill) {
                    commandButton(getBinding().btnSpeedPlus);
                } else {
                    commandButton(getBinding().btnSpeedPlus2);
                }
            } else if (n == CLICK_MINUS) { // 實體按鍵 -1
                if (isTreadmill) {
                    commandButton(getBinding().btnSpeedMinus);
                } else {
                    commandButton(getBinding().btnSpeedMinus2);
                }
//            } else if (n == LONG_CLICK_PLUS) {
//                longDown(getBinding().btnSpeedPlus);
//            } else if (n == LONG_CLICK_MINUS) {
//                longDown(getBinding().btnSpeedMinus);
            }
        });


        //取消長按
        LiveEventBus.get(KEY_UNKNOWN).observe(getViewLifecycleOwner(), s -> {
            //  longUp();

            for (Map.Entry<DeviceSpiritC.KEY, View> entry : map.entrySet()) {
                entry.getValue().setPressed(false);
            }

            ((MainActivity)requireActivity()).uartConsole.setBuzzer();
        });

        //FTMS控制INCLINE
        LiveEventBus.get(FTMS_SET_TARGET_INCLINATION).observe(getViewLifecycleOwner(), s -> {
            Integer n = (Integer) s;
            if (n == CLICK_PLUS) {// 實體按鍵 +1
                commandButton(getBinding().btnInclinePlus);
            } else if (n == CLICK_MINUS) { // 實體按鍵 -1
                commandButton(getBinding().btnInclineMinus);
//            } else if (n == LONG_CLICK_PLUS) {
//                longDown(getBinding().btnInclinePlus);
//            } else if (n == LONG_CLICK_MINUS) {
//                longDown(getBinding().btnInclineMinus);
            }
        });

        getBinding().btnDone.setOnClickListener(v -> dismiss());

    }

    AppCompatImageButton iButton;
    boolean isLongClickIng;
    RxTimer longTimer;

    private void longDown(AppCompatImageButton button) {

        if (isLongClickIng) return;
        isLongClickIng = true;
        if (longTimer != null) longTimer.cancel();
        iButton = button;
        button.setPressed(true);
        longTimer = new RxTimer();
        longTimer.interval3(100, n -> {
            button.performClick();
        });
    }

    private void longUp() {
        isLongClickIng = false;
        if (longTimer != null) longTimer.cancel();
        if (iButton != null) iButton.setPressed(false);
        iButton = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}