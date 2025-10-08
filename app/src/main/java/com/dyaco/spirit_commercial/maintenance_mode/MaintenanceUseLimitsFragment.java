//package com.dyaco.spirit_commercial.maintenance_mode;
//
//import static com.dyaco.spirit_commercial.App.UNIT_E;
//import static com.dyaco.spirit_commercial.App.getApp;
//import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.IMPERIAL;
//import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.OFF;
//import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.ON;
//
//import android.graphics.Color;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.CompoundButton;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.lifecycle.ViewModelProvider;
//
//import com.dyaco.spirit_commercial.R;
//import com.dyaco.spirit_commercial.databinding.FragmentMaintenanceUseLimitsBinding;
//import com.dyaco.spirit_commercial.support.CommonUtils;
//import com.dyaco.spirit_commercial.support.base_component.BaseBindingDialogFragment;
//import com.dyaco.spirit_commercial.support.custom_view.wheelPicker.OptionsPickerView;
//import com.dyaco.spirit_commercial.support.custom_view.wheelPicker.WheelView;
//import com.dyaco.spirit_commercial.viewmodel.DeviceSettingBean;
//import com.dyaco.spirit_commercial.viewmodel.DeviceSettingViewModel;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
//
//public class MaintenanceUseLimitsFragment extends BaseBindingDialogFragment<FragmentMaintenanceUseLimitsBinding> {
//    private DeviceSettingViewModel dsvm;
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        dsvm = new ViewModelProvider(requireActivity()).get(DeviceSettingViewModel.class);
//
//        getBinding().setDeviceSettingViewModel(dsvm);
//
//        initEvent();
//
//        getBinding().tvSpeedTitle.setText(UNIT_E == IMPERIAL ? R.string.max_speed___mph : R.string.max_speed___kph);
//
//        initPauseAfterSelect();
//
//        initUseTimeLimitSelect();
//
////        initMaxSpeedSelect();
////
////        initMaxInclineSelect();
//
//
//    }
//
//
//    //Max Incline=10.0% (Range 10.0~15.0) Step 1.0% MAX_INC_MAX (20階 ~ 30階, 10% ~ 15 %)
//    private void initMaxInclineSelect() {
//
//        List<String> list1 = new ArrayList<>(1);
//        for (int i = 10; i <= 15; i++) {
//            list1.add(String.valueOf(i));
//        }
//
//        OptionsPickerView<String> pickSleepAfterHour = getBinding().pickMaxIncline;
//        pickSleepAfterHour.setData(list1);
//        pickSleepAfterHour.setVisibleItems(2);
//        pickSleepAfterHour.setNormalItemTextColor(Color.parseColor("#5a7085"));
//        pickSleepAfterHour.setTextSize(32, false);
//        pickSleepAfterHour.setCurved(true);
//        pickSleepAfterHour.setLineSpacing(-12, true);
//        pickSleepAfterHour.setCurvedArcDirection(WheelView.CURVED_ARC_DIRECTION_CENTER);
//        pickSleepAfterHour.setCurvedArcDirectionFactor(1.0f);
//        pickSleepAfterHour.setSelectedItemTextColor(Color.parseColor("#ffffff"));
//        pickSleepAfterHour.setCyclic(true);
//        pickSleepAfterHour.setOnOptionsSelectedListener((opt1Pos, opt1Data, opt2Pos, opt2Data, opt3Pos, opt3Data) -> {
//            if (opt1Data == null)  return;
//
//            DeviceSettingBean deviceSettingBean = getApp().getDeviceSettingBean();
//            deviceSettingBean.setMaxIncline(Double.parseDouble(opt1Data));
//            getApp().setDeviceSettingBean(deviceSettingBean);
//          //  OPT_SETTINGS.MAX_INC_MAX = Integer.parseInt(opt1Data) * 2;
//
//        });
//
//        int selIncline = (int) getApp().getDeviceSettingBean().getMaxIncline();
//        pickSleepAfterHour.setOpt1SelectedPosition(selIncline - 10, false);
//        //10~15
//    }
//
//
//    // 20.0KPH(Range 16.0~20.0 KPH) / 12MPH(Range 10.0~12.0 MPH)Step 0.1
//    private void initMaxSpeedSelect() {
//        //MAX_SPD_IU_MAX = 150;
//        //MAX_SPD_MU_MAX = 240;
//        int speed1Max = UNIT_E == IMPERIAL ? 12 : 20;
//        int speed1Min = UNIT_E == IMPERIAL ? 10 : 16;
//
//        List<Integer> list1 = new ArrayList<>(1);
//        for (int i = speed1Min; i <= speed1Max; i++) {
//            list1.add(i);
//        }
//
//        List<Integer> list2 = new ArrayList<>(1);
//        for (int i = 0; i <= 9; i++) {
//            list2.add(i);
//        }
//
//        OptionsPickerView<Integer> pickSpeed2 = getBinding().pickSpeedMph2;
//        OptionsPickerView<Integer> pickSpeed1 = getBinding().pickSpeedMph1;
//
//        pickSpeed1.setData(list1);
//        pickSpeed1.setVisibleItems(2);
//        pickSpeed1.setNormalItemTextColor(Color.parseColor("#5a7085"));
//        pickSpeed1.setTextSize(32, false);
//        pickSpeed1.setCurved(true);
//        pickSpeed1.setLineSpacing(-12, true);
//        pickSpeed1.setCurvedArcDirection(WheelView.CURVED_ARC_DIRECTION_CENTER);
//        pickSpeed1.setCurvedArcDirectionFactor(1.0f);
//        pickSpeed1.setSelectedItemTextColor(Color.parseColor("#ffffff"));
//        pickSpeed1.setCyclic(true);
//        pickSpeed1.setOnOptionsSelectedListener((opt1Pos, opt1Data, opt2Pos, opt2Data, opt3Pos, opt3Data) -> {
//            if (opt1Data == null) return;
//
//            if (opt1Data == speed1Max) {
//                pickSpeed2.setOpt1SelectedPosition(0,true);
//            }
//
//            //目前選的小數
//            double s2 = pickSpeed2.getOpt1SelectedData() * 0.1;
//
//            double cSpeed = opt1Data + s2;
//
//            DeviceSettingBean deviceSettingBean = getApp().getDeviceSettingBean();
//            if (UNIT_E == IMPERIAL) {
//                deviceSettingBean.setMaxSpeedIu(cSpeed);
//             //   OPT_SETTINGS.MAX_SPD_IU_MAX = (int) (cSpeed * 10);
//            } else {
//                deviceSettingBean.setMaxSpeedMu(cSpeed);
//              //  OPT_SETTINGS.MAX_SPD_MU_MAX = (int) (cSpeed * 10);
//            }
//            getApp().setDeviceSettingBean(deviceSettingBean);
//
//
//        });
//
//        pickSpeed2.setData(list2);
//        pickSpeed2.setVisibleItems(2);
//        pickSpeed2.setNormalItemTextColor(Color.parseColor("#5a7085"));
//        pickSpeed2.setTextSize(32, false);
//        pickSpeed2.setCurved(true);
//        pickSpeed2.setLineSpacing(-12, true);
//        pickSpeed2.setCurvedArcDirection(WheelView.CURVED_ARC_DIRECTION_CENTER);
//        pickSpeed2.setCurvedArcDirectionFactor(1.0f);
//        pickSpeed2.setSelectedItemTextColor(Color.parseColor("#ffffff"));
//        pickSpeed2.setCyclic(true);
//        pickSpeed2.setOnOptionsSelectedListener((opt1Pos, opt1Data, opt2Pos, opt2Data, opt3Pos, opt3Data) -> {
//            if (opt1Data == null)  return;
//
//            if (pickSpeed1.getOpt1SelectedData() == speed1Max) {
//                pickSpeed2.setOpt1SelectedPosition(0,true);
//            }
//
//            int s1 = (int) (UNIT_E == IMPERIAL ? getApp().getDeviceSettingBean().getMaxSpeedIu() : getApp().getDeviceSettingBean().getMaxSpeedMu());
//            double sss1 = opt1Data * 0.1;
//            double ss1 = s1 + sss1;
//
//            Log.d("EEEEFFFEFEF", "initMaxSpeedSelect: " + s1 +","+ sss1);
//
//
//            DeviceSettingBean deviceSettingBean = getApp().getDeviceSettingBean();
//            if (UNIT_E == IMPERIAL) {
//                deviceSettingBean.setMaxSpeedIu(ss1);
//                //OPT_SETTINGS.MAX_SPD_IU_MAX = (int) (ss1 * 10);
//            } else {
//                deviceSettingBean.setMaxSpeedMu(ss1);
//              //  OPT_SETTINGS.MAX_SPD_MU_MAX = (int) (ss1 * 10);
//            }
//            getApp().setDeviceSettingBean(deviceSettingBean);
//
//        });
//
//
//        int speed1 = (int) (UNIT_E == IMPERIAL ? getApp().getDeviceSettingBean().getMaxSpeedIu() : getApp().getDeviceSettingBean().getMaxSpeedMu());
//        double speed2 = (UNIT_E == IMPERIAL ? getApp().getDeviceSettingBean().getMaxSpeedIu() : getApp().getDeviceSettingBean().getMaxSpeedMu());
//        pickSpeed1.setOpt1SelectedPosition(speed1 - speed1Min, false);
//
//        int s = 0;
//        try {
//            String s2 = String.valueOf(speed2);
//            String[] a = s2.split("\\.");
//            s = Integer.parseInt(a[1]);//取小數位
//            Log.d("SETTING_FILE", "initMaxSpeedSelect: " + s);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        pickSpeed2.setOpt1SelectedPosition(s, false);
//
//    }
//
//
//    OptionsPickerView<String> pickUseTimeLimitHour;
//    OptionsPickerView<String> pickUseTimeLimitMin;
//    OptionsPickerView<String> pickUseTimeLimitSec;
//    private void initUseTimeLimitSelect() {
//        List<String> list1 = new ArrayList<>(1);
//        for (int i = 0; i < 60; i++) {
//            list1.add(("0" + i).substring(("0" + i).length() - 2));
//        }
//
//        pickUseTimeLimitHour = getBinding().pickUseTimeLimitHour;
//        pickUseTimeLimitHour.setData(list1);
//        pickUseTimeLimitHour.setVisibleItems(2);
//        pickUseTimeLimitHour.setNormalItemTextColor(Color.parseColor("#5a7085"));
//        pickUseTimeLimitHour.setTextSize(32, false);
//        pickUseTimeLimitHour.setCurved(true);
//        pickUseTimeLimitHour.setLineSpacing(-12, true);
//        pickUseTimeLimitHour.setCurvedArcDirection(WheelView.CURVED_ARC_DIRECTION_CENTER);
//        pickUseTimeLimitHour.setCurvedArcDirectionFactor(1.0f);
//        pickUseTimeLimitHour.setSelectedItemTextColor(Color.parseColor("#ffffff"));
//        pickUseTimeLimitHour.setCyclic(true);
//        pickUseTimeLimitHour.setOnOptionsSelectedListener((opt1Pos, opt1Data, opt2Pos, opt2Data, opt3Pos, opt3Data) -> {
//            if (opt1Data == null) return;
//            DeviceSettingBean deviceSettingBean = getApp().getDeviceSettingBean();
//            long hour = opt1Pos * 60L * 60;
//            long min = pickUseTimeLimitMin.getOpt1SelectedPosition() * 60L;
//            long sec = pickUseTimeLimitSec.getOpt1SelectedPosition();
//            deviceSettingBean.setUseTimeLimit(hour + min + sec);
//            getApp().setDeviceSettingBean(deviceSettingBean);
//        });
//
//
//        List<String> list2 = new ArrayList<>(1);
//        for (int i = 0; i < 60; i++) {
//            list2.add(("0" + i).substring(("0" + i).length() - 2));
//        }
//
//        pickUseTimeLimitMin = getBinding().picUseTimeLimitMin;
//        pickUseTimeLimitMin.setData(list2);
//        pickUseTimeLimitMin.setVisibleItems(2);
//        pickUseTimeLimitMin.setNormalItemTextColor(Color.parseColor("#5a7085"));
//        pickUseTimeLimitMin.setTextSize(32, false);
//        pickUseTimeLimitMin.setCurved(true);
//        pickUseTimeLimitMin.setLineSpacing(-12, true);
//        pickUseTimeLimitMin.setCurvedArcDirection(WheelView.CURVED_ARC_DIRECTION_CENTER);
//        pickUseTimeLimitMin.setCurvedArcDirectionFactor(1.0f);
//        pickUseTimeLimitMin.setSelectedItemTextColor(Color.parseColor("#ffffff"));
//        pickUseTimeLimitMin.setCyclic(true);
//        pickUseTimeLimitMin.setOnOptionsSelectedListener((opt1Pos, opt1Data, opt2Pos, opt2Data, opt3Pos, opt3Data) -> {
//            if (opt1Data == null) return;
//            DeviceSettingBean deviceSettingBean = getApp().getDeviceSettingBean();
//            long hour = pickUseTimeLimitHour.getOpt1SelectedPosition() * 60L * 60;
//            long min = opt1Pos * 60L;
//            long sec = pickUseTimeLimitSec.getOpt1SelectedPosition();
//            deviceSettingBean.setUseTimeLimit(hour + min + sec);
//            getApp().setDeviceSettingBean(deviceSettingBean);
//        });
//
//
//        List<String> list3 = new ArrayList<>(1);
//        for (int i = 0; i < 60; i++) {
//            list3.add(("0" + i).substring(("0" + i).length() - 2));
//        }
//
//        pickUseTimeLimitSec = getBinding().pickUseTimeLimitSec;
//        pickUseTimeLimitSec.setData(list3);
//        pickUseTimeLimitSec.setVisibleItems(2);
//        pickUseTimeLimitSec.setNormalItemTextColor(Color.parseColor("#5a7085"));
//        pickUseTimeLimitSec.setTextSize(32, false);
//        pickUseTimeLimitSec.setCurved(true);
//        pickUseTimeLimitSec.setLineSpacing(-12, true);
//        pickUseTimeLimitSec.setCurvedArcDirection(WheelView.CURVED_ARC_DIRECTION_CENTER);
//        pickUseTimeLimitSec.setCurvedArcDirectionFactor(1.0f);
//        pickUseTimeLimitSec.setSelectedItemTextColor(Color.parseColor("#ffffff"));
//        pickUseTimeLimitSec.setCyclic(true);
//        pickUseTimeLimitSec.setOnOptionsSelectedListener((opt1Pos, opt1Data, opt2Pos, opt2Data, opt3Pos, opt3Data) -> {
//            if (opt1Data == null) return;
//            DeviceSettingBean deviceSettingBean = getApp().getDeviceSettingBean();
//            long hour = pickUseTimeLimitHour.getOpt1SelectedPosition() * 60L * 60;
//            long min = pickUseTimeLimitMin.getOpt1SelectedPosition() * 60L;
//            deviceSettingBean.setUseTimeLimit(hour + min + opt1Pos);
//            getApp().setDeviceSettingBean(deviceSettingBean);
//        });
//
//
//        long timeLimit = getApp().getDeviceSettingBean().getUseTimeLimit();
//        int timeLimitHour = (int) (TimeUnit.SECONDS.toHours(timeLimit));
//        int timeLimitMin = (int) (TimeUnit.SECONDS.toMinutes(timeLimit) - (TimeUnit.SECONDS.toHours(timeLimit) * 60));
//        int timeLimitSec = (int) (TimeUnit.SECONDS.toSeconds(timeLimit) - (TimeUnit.SECONDS.toMinutes(timeLimit) * 60));
//        pickUseTimeLimitHour.setOpt1SelectedPosition(timeLimitHour, false);
//        pickUseTimeLimitMin.setOpt1SelectedPosition(timeLimitMin, false);
//        pickUseTimeLimitSec.setOpt1SelectedPosition(timeLimitSec, false);
//
//    }
//
//    @SuppressWarnings("unchecked")
//    private void initPauseAfterSelect() {
//
//        //Auto Pause只會在 workout開始後才會偵測，當橋接板error code2 bit4 =1時，
//        // 表示跑帶在動作但無人在使用；因此，當此狀況持續時間超過上表設定時間後，上表直接結束此次workout，並logout，回到login畫面；
//        getBinding().cbAutoPause.setChecked(dsvm.autoPause.getValue() == ON);
//
//        getBinding().hidePauseAfter.setVisibility(dsvm.autoPause.getValue() == ON ? View.GONE : View.VISIBLE);
////        if (deviceSettingViewModel.autoPause.getValue() == 1 )
//
//        getBinding().cbAutoPause.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
//                dsvm.autoPause.setValue(isChecked ? ON : OFF);
//
//                getBinding().hidePauseAfter.setVisibility(isChecked ? View.GONE : View.VISIBLE);
//
//            }
//        });
//
//
//        List<String> list1 = new ArrayList<>(1);
//        for (int i = 0; i < 60; i++) {
//            list1.add(("0" + i).substring(("0" + i).length() - 2));
//        }
//
//        List<String> list2 = new ArrayList<>(1);
//        for (int i = 0; i < 60; i++) {
//            list2.add(("0" + i).substring(("0" + i).length() - 2));
//        }
//
//        List<String> list3 = new ArrayList<>(1);
//        for (int i = 0; i < 60; i++) {
//            list3.add(("0" + i).substring(("0" + i).length() - 2));
//        }
//
//
//        OptionsPickerView<String> pickPauseAfterHour = getBinding().pickPauseAfterHour;
//        OptionsPickerView<String> pickPauseAfterSec = getBinding().pickPauseAfterSec;
//        OptionsPickerView<String> pickPauseAfterMin = getBinding().pickPauseAfterMin;
//
//        pickPauseAfterHour.setData(list1);
//        pickPauseAfterHour.setVisibleItems(2);
//        pickPauseAfterHour.setNormalItemTextColor(Color.parseColor("#5a7085"));
//        pickPauseAfterHour.setTextSize(32, false);
//        pickPauseAfterHour.setCurved(true);
//        pickPauseAfterHour.setLineSpacing(-12, true);
//        pickPauseAfterHour.setCurvedArcDirection(WheelView.CURVED_ARC_DIRECTION_CENTER);
//        pickPauseAfterHour.setCurvedArcDirectionFactor(1.0f);
//        pickPauseAfterHour.setSelectedItemTextColor(Color.parseColor("#ffffff"));
//        pickPauseAfterHour.setCyclic(true);
//        pickPauseAfterHour.setOnOptionsSelectedListener((opt1Pos, opt1Data, opt2Pos, opt2Data, opt3Pos, opt3Data) -> {
//            if (opt1Data == null) return;
//            DeviceSettingBean deviceSettingBean = getApp().getDeviceSettingBean();
//            long hour = opt1Pos * 60L * 60;
//            long min = pickPauseAfterMin.getOpt1SelectedPosition() * 60L;
//            long sec = pickPauseAfterSec.getOpt1SelectedPosition();
//            deviceSettingBean.setPauseAfter(hour + min + sec);
//            getApp().setDeviceSettingBean(deviceSettingBean);
//        });
//
//        pickPauseAfterMin.setData(list2);
//        pickPauseAfterMin.setVisibleItems(2);
//        pickPauseAfterMin.setNormalItemTextColor(Color.parseColor("#5a7085"));
//        pickPauseAfterMin.setTextSize(32, false);
//        pickPauseAfterMin.setCurved(true);
//        pickPauseAfterMin.setLineSpacing(-12, true);
//        pickPauseAfterMin.setCurvedArcDirection(WheelView.CURVED_ARC_DIRECTION_CENTER);
//        pickPauseAfterMin.setCurvedArcDirectionFactor(1.0f);
//        pickPauseAfterMin.setSelectedItemTextColor(Color.parseColor("#ffffff"));
//        pickPauseAfterMin.setCyclic(true);
//        pickPauseAfterMin.setOnOptionsSelectedListener((opt1Pos, opt1Data, opt2Pos, opt2Data, opt3Pos, opt3Data) -> {
//            if (opt1Data == null) return;
//            DeviceSettingBean deviceSettingBean = getApp().getDeviceSettingBean();
//            long hour = pickPauseAfterHour.getOpt1SelectedPosition() * 60L * 60;
//            long min = opt1Pos * 60L;
//            long sec = pickPauseAfterSec.getOpt1SelectedPosition();
//            deviceSettingBean.setPauseAfter(hour + min + sec);
//            getApp().setDeviceSettingBean(deviceSettingBean);
//        });
//
//        pickPauseAfterSec.setData(list3);
//        pickPauseAfterSec.setVisibleItems(2);
//        pickPauseAfterSec.setNormalItemTextColor(Color.parseColor("#5a7085"));
//        pickPauseAfterSec.setTextSize(32, false);
//        pickPauseAfterSec.setCurved(true);
//        pickPauseAfterSec.setLineSpacing(-12, true);
//        pickPauseAfterSec.setCurvedArcDirection(WheelView.CURVED_ARC_DIRECTION_CENTER);
//        pickPauseAfterSec.setCurvedArcDirectionFactor(1.0f);
//        pickPauseAfterSec.setSelectedItemTextColor(Color.parseColor("#ffffff"));
//        pickPauseAfterSec.setCyclic(true);
//        pickPauseAfterSec.setOnOptionsSelectedListener((opt1Pos, opt1Data, opt2Pos, opt2Data, opt3Pos, opt3Data) -> {
//            if (opt1Data == null) return;
//
//            DeviceSettingBean deviceSettingBean = getApp().getDeviceSettingBean();
//            long hour = pickPauseAfterHour.getOpt1SelectedPosition() * 60L * 60;
//            long min = pickPauseAfterMin.getOpt1SelectedPosition() * 60L;
//            deviceSettingBean.setPauseAfter(hour + min + opt1Pos);
//            getApp().setDeviceSettingBean(deviceSettingBean);
//        });
//
//        //初始值
//        long timeLimit = getApp().getDeviceSettingBean().getPauseAfter();
//        int timeLimitHour = (int) (TimeUnit.SECONDS.toHours(timeLimit));
//        int timeLimitMin = (int) (TimeUnit.SECONDS.toMinutes(timeLimit) - (TimeUnit.SECONDS.toHours(timeLimit) * 60));
//        int timeLimitSec = (int) (TimeUnit.SECONDS.toSeconds(timeLimit) - (TimeUnit.SECONDS.toMinutes(timeLimit) * 60));
//
//        pickPauseAfterHour.setOpt1SelectedPosition(timeLimitHour, false);
//        pickPauseAfterMin.setOpt1SelectedPosition(timeLimitMin, false);
//        pickPauseAfterSec.setOpt1SelectedPosition(timeLimitSec, false);
//    }
//
//
//    private void initEvent() {
//
//        getBinding().btnDone.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                new CommonUtils().deviceSettingViewModelToMMKV(dsvm);
//
//                dismiss();
//            }
//        });
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//    }
//}