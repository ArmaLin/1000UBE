package com.dyaco.spirit_commercial.maintenance_mode;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.dyaco.spirit_commercial.App.SETTING_SHOW;
import static com.dyaco.spirit_commercial.App.UNIT_E;
import static com.dyaco.spirit_commercial.App.getApp;
import static com.dyaco.spirit_commercial.App.getDeviceSpiritC;
import static com.dyaco.spirit_commercial.App.isShowMediaMenuOnStop;
import static com.dyaco.spirit_commercial.MainActivity.isHomeScreen;
import static com.dyaco.spirit_commercial.MainActivity.isTreadmill;
import static com.dyaco.spirit_commercial.MainActivity.isUs;
import static com.dyaco.spirit_commercial.support.CommonUtils.getBrightnessPresent;
import static com.dyaco.spirit_commercial.support.CommonUtils.getMaxBrightness;
import static com.dyaco.spirit_commercial.support.CommonUtils.getMinBrightness;
import static com.dyaco.spirit_commercial.support.CommonUtils.getScreenBrightness;
import static com.dyaco.spirit_commercial.support.CommonUtils.saveBrightness;
import static com.dyaco.spirit_commercial.support.CommonUtils.secToTimeHour;
import static com.dyaco.spirit_commercial.support.CommonUtils.secToTimeMin;
import static com.dyaco.spirit_commercial.support.CommonUtils.secToTimeSec;
import static com.dyaco.spirit_commercial.support.CommonUtils.setBrightness;
import static com.dyaco.spirit_commercial.support.CommonUtils.setSleepMode;
import static com.dyaco.spirit_commercial.support.CommonUtils.updateTime;
import static com.dyaco.spirit_commercial.support.FormulaUtil.getRoundDecimal;
import static com.dyaco.spirit_commercial.support.FormulaUtil.km2mi;
import static com.dyaco.spirit_commercial.support.FormulaUtil.mi2km;
import static com.dyaco.spirit_commercial.support.FormulaUtil.nMi2Km;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.CONSOLE_SYSTEM_EGYM;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.CONSOLE_SYSTEM_SPIRIT;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.DEFAULT_AUTO_PAUSE_TIME;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.DEFAULT_USE_TIME_LIMIT;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.IMPERIAL;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.OFF;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.ON;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.PROTOCOL_CSAFE;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.SCREEN_TIMEOUT_15;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.SCREEN_TIMEOUT_NEVER;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.SLEEP_MODE_RETAIL;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.VIDEO_STB;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.VIDEO_TV;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.MAX_SPD_IU_MAX;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.MAX_SPD_MU_MAX;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.MIN_SPD_IU_MAX;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.MIN_SPD_IU_MIN;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.MIN_SPD_MU_MAX;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.MIN_SPD_MU_MIN;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.corestar.libs.device.DeviceSpiritC;
import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.dashboard_media.DashboardMediaFragment;
import com.dyaco.spirit_commercial.databinding.PopupMaintenanceBinding;
import com.dyaco.spirit_commercial.listener.IUartConsole;
import com.dyaco.spirit_commercial.product_flavor.DownloadManagerCustom;
import com.dyaco.spirit_commercial.product_flavor.ModeEnum;
import com.dyaco.spirit_commercial.support.CommonUtils;
import com.dyaco.spirit_commercial.support.DelayedExecutor;
import com.dyaco.spirit_commercial.support.FloatingWidget;
import com.dyaco.spirit_commercial.support.MsgEvent;
import com.dyaco.spirit_commercial.support.RxTimer;
import com.dyaco.spirit_commercial.support.UsbFileCopierKt;
import com.dyaco.spirit_commercial.support.base_component.BaseBindingFragment;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.custom_view.CustomToast;
import com.dyaco.spirit_commercial.support.custom_view.wheelPicker.OptionsPickerView;
import com.dyaco.spirit_commercial.support.custom_view.wheelPicker.WheelView;
import com.dyaco.spirit_commercial.support.intdef.AppStatusIntDef;
import com.dyaco.spirit_commercial.support.intdef.EventKey;
import com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS;
import com.dyaco.spirit_commercial.support.intdef.UnitEnum;
import com.dyaco.spirit_commercial.support.interaction.TenTapClick;
import com.dyaco.spirit_commercial.support.mediaapp.MediaAppUtils;
import com.dyaco.spirit_commercial.support.room.spirit.SpiritDbManager;
import com.dyaco.spirit_commercial.support.utils.CheckDoubleClick;
import com.dyaco.spirit_commercial.viewmodel.AppStatusViewModel;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingBean;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingViewModel;
import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;
import com.jeremyliao.liveeventbus.LiveEventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;


public class MaintenanceModeFragment extends BaseBindingFragment<PopupMaintenanceBinding> {


    private AppStatusViewModel appStatusViewModel;
    private DeviceSettingViewModel deviceSettingViewModel;

    private ModeEnum modeEnum;

    private MainActivity mainActivity;

    public IUartConsole uartConsole;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        DashboardMediaFragment.isLoaded = false;
        if (MediaAppUtils.downloadUtil != null) {
            MediaAppUtils.downloadUtil.stop();
            MediaAppUtils.downloadUtil = null;
        }

        mainActivity = ((MainActivity) requireActivity());

        isHomeScreen = false;
        //   Log.d("RPM_CHECK", "isHomeScreen: " + isHomeScreen);

        appStatusViewModel = new ViewModelProvider(requireActivity()).get(AppStatusViewModel.class);
        appStatusViewModel.currentStatus.set(AppStatusIntDef.STATUS_MAINTENANCE);

        deviceSettingViewModel = new ViewModelProvider(requireActivity()).get(DeviceSettingViewModel.class);

        uartConsole = ((MainActivity) requireActivity()).uartConsole;

        if (isTreadmill) {
            try {
                uartConsole.setDevMainMode(DeviceSpiritC.MAIN_MODE.ENG);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        checkSystemWritePermission();

        setAutoDateTime(1);
        setAutoDateTimeZone(1);

//        //開啟狀態列 //關閉隱藏列按鈕
        //  new CommonUtils().hideStatusBar(0);

        isShowMediaMenuOnStop = false;

//        try {
//            //取得Console設定的休眠時間
//
//            int timeout = Settings.System.getInt(requireActivity().getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT);
//            DeviceSettingBean deviceSettingBean = getApp().getDeviceSettingBean();
//
//            deviceSettingBean.setSleep_mode(timeout != SCREEN_TIMEOUT_NEVER ? ON : OFF); // 1 on, 0 0ff
//            if (deviceSettingBean.getSleep_mode() == ON) {
//                deviceSettingBean.setSleepAfter(timeout != SCREEN_TIMEOUT_NEVER ? timeout / 1000 : 0);
//            }
//            getApp().setDeviceSettingBean(deviceSettingBean);
//        } catch (Settings.SettingNotFoundException e) {
//            e.printStackTrace();
//        }

        requireActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        if (mainActivity.downloadManagerCustom != null) {
            Log.d(DownloadManagerCustom.TAG, "取消正在下載的檔案");
            mainActivity.downloadManagerCustom.cancelDownload();
        }

        mainActivity.initTvTuner();

        //設定TimeZone
        //      new SetTimeZone(requireActivity()).checkTimeZone();

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getBinding().setDeviceSettingViewModel(deviceSettingViewModel);
        getBinding().setIsTreadmill(isTreadmill);
        getBinding().setIsUs(isUs);

        modeEnum = ModeEnum.getMode(deviceSettingViewModel.modelCode.get());

        getBinding().setModeEnum(modeEnum);

        initView();

        initDisplayBrightnessBar();

        initSleepAfterSelect();

        initUseTimeLimit();

        initAutoPauseSelect();

        initData();

        initEvent();

        initMinSpeed();
        initMaxSpeed();

        initConsoleSystem();


        //建立資料夾
        File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "/CoreStar/Dyaco/Spirit/");
        if (!root.exists()) {
            if (root.mkdirs()) {
                Log.d("#####CCCCCC", "資料夾建立成功: ");
            } else {
                Log.d("#####CCCCCC", "資料夾建立失敗: ");
            }
        } else {
            Log.d("#####CCCCCC", "資料夾已存在 ");
        }

        // Log.d("PPPEEEE", "onViewCreated: " +deviceSettingViewModel.typeName.get());
        // Log.d("PPPEEEE", "onViewCreated: " +deviceSettingViewModel.typeCode.get());

        //   getBinding().tvType.setOnClickListener(x -> {


//            final String[] dinner = {"TREADMILL","ELLIPTICAL","UPRIGHT BIKE","RECUMBENT BIKE"};
//            AlertDialog.Builder dialog_list = new AlertDialog.Builder(requireActivity());
//            dialog_list.setItems(dinner, (dialog, which) -> {
//                DeviceSettingBean d = getApp().getDeviceSettingBean();
//                switch (which) {
//                    case 0:
//                        d.setType(DeviceIntDef.DEVICE_TYPE_TREADMILL);
//                        break;
//                    case 1:
//                        d.setType(DeviceIntDef.DEVICE_TYPE_ELLIPTICAL);
//                        break;
//                    case 2:
//                        d.setType(DeviceIntDef.DEVICE_TYPE_UPRIGHT_BIKE);
//                        break;
//                    case 3:
//                        d.setType(DeviceIntDef.DEVICE_TYPE_RECUMBENT_BIKE);
//                        break;
//                }
//                getApp().setDeviceSettingBean(d);
//                new CommonUtils().mmkvDeviceSettingToViewModel(deviceSettingViewModel, getApp().getDeviceSettingBean());
//
//
//            });
//            dialog_list.show();
        //    });


    }

    private void initConsoleSystem() {
        getBinding().cbSystem.setChecked(deviceSettingViewModel.consoleSystem.get() == CONSOLE_SYSTEM_EGYM);//CONSOLE_SYSTEM_SPIRIT > 0

        getBinding().cbSystem.setOnCheckedChangeListener((compoundButton, isChecked) -> {

            deviceSettingViewModel.consoleSystem.set(isChecked ? CONSOLE_SYSTEM_EGYM : CONSOLE_SYSTEM_SPIRIT);

            DeviceSettingBean d = getApp().getDeviceSettingBean();
            d.setConsoleSystem(deviceSettingViewModel.consoleSystem.get());
            getApp().setDeviceSettingBean(d);

            //刪除資料庫
            new Thread(() -> SpiritDbManager.getInstance(getApp()).clearTable()).start();

            DeviceSettingBean de = getApp().getDeviceSettingBean();
            if (isChecked) {
                //選擇EGYM模式後, auto logout = 1 min
                de.setPauseAfter(60);
                getApp().setDeviceSettingBean(de);

                long timeLimit = getApp().getDeviceSettingBean().getPauseAfter();
                getBinding().pickPauseAfterMin.setOpt1SelectedPosition(secToTimeMin(timeLimit) - 1, false);
                getBinding().pickPauseAfterSec.setOpt1SelectedPosition(secToTimeSec(timeLimit), false);

            } else {

                de.setPauseAfter(5 * 60);
                getApp().setDeviceSettingBean(de);

                long timeLimit = getApp().getDeviceSettingBean().getPauseAfter();
                getBinding().pickPauseAfterMin.setOpt1SelectedPosition(secToTimeMin(timeLimit) - 1, false);
                getBinding().pickPauseAfterSec.setOpt1SelectedPosition(secToTimeSec(timeLimit), false);

            }
            getBinding().cbAutoPause.setChecked(true);

        });
    }


    private void initData() {
        DeviceSettingBean deviceEntity = getApp().getDeviceSettingBean();

        //odo Time
        initOdoTime();
        //odo Distance
        initOdoDistance();

        getBinding().cbPauseMode.setChecked(deviceEntity.getPauseMode() == ON); // 1 on, 0 0ff


        Log.d("RRRRRRRRRR", "initData: " + deviceSettingViewModel.video.getValue());

        //Video
        if (isUs) {
            //      deviceEntity.setVideo(VIDEO_NONE);
            //     getApp().setDeviceSettingBean(deviceEntity);
            //     deviceSettingViewModel.video.setValue(VIDEO_NONE);
            //    getBinding().scVideo.setEnabled(false);
            //    getBinding().scVideo.setAlpha(0.4f);
            //     getBinding().btnSplashScreenImage.setEnabled(false);
            //      getBinding().btnSplashScreenImage.setAlpha(0.6f);
            //      getBinding().titleVideo.setAlpha(0.3f);
        }

    }

    private void setSleepAfterPick() {
        DeviceSettingBean dsb = getApp().getDeviceSettingBean();
        long sleepAfter = dsb.getSleepAfter();
        //  Log.d("@@@@@@@@@", "setSleepAfterPick: " + sleepAfter);
        //   pickSleepAfterHour.setOpt1SelectedPosition(secToTimeHour(sleepAfter), true);

        if (sleepAfter == 3600) {
            pickSleepAfterMin.setOpt1SelectedPosition(60, false);
        } else {
            pickSleepAfterMin.setOpt1SelectedPosition(secToTimeMin(sleepAfter), false);
        }

        pickSleepAfterSec.setOpt1SelectedPosition(secToTimeSec(sleepAfter), false);
    }


    public void initOdoDistance() {
        Log.d("#####################", "updateDeviceData: " + getApp().getDeviceSettingBean().getODO_distance());
        getBinding().tvDistance.setText(String.format("%s %s", getRoundDecimal((float) nMi2Km(getApp().getDeviceSettingBean().getODO_distance())), getString(UnitEnum.getUnit(UnitEnum.DISTANCE))));
    }

    private void initOdoTime() {
        String hours = String.valueOf(getRoundDecimal((float) (getApp().getDeviceSettingBean().getODO_time() / 3600f)));
        getBinding().tvTotalTime.setText(getString(R.string.nHours, hours));
    }


    boolean isFirst = true;
    boolean fromUnit = false;

    private void initEvent() {

        //Protocol
        deviceSettingViewModel.onProtocolChanged.observe(getViewLifecycleOwner(), value -> {
            if (isFirst) return;

            Log.d("CSAFE", "onProtocolChanged: " + value);
            // 切換Cab/Csafe設備時需要把設備先斷線
            //csafe 跟遙控器無關
            if (value == PROTOCOL_CSAFE) {
                mainActivity.removeCab();
                mainActivity.initCSAFE();
            } else {
                mainActivity.removeCSAFE();
                mainActivity.initCab();
            }

            DeviceSettingBean deviceSettingBean = getApp().getDeviceSettingBean();
            deviceSettingBean.setProtocol(value);
            getApp().setDeviceSettingBean(deviceSettingBean);
        });

        //VIDEO
        deviceSettingViewModel.onVideoChanged.observe(getViewLifecycleOwner(), value -> {

            if (isFirst) return;


            DeviceSettingBean deviceSettingBean = getApp().getDeviceSettingBean();
            deviceSettingBean.setVideo(value);
            getApp().setDeviceSettingBean(deviceSettingBean);

            //STB 如果開啟，就用 Port 4，如果是TV就用 Port 1
            //hdmi = CAB
            if (value == VIDEO_STB) {
                //      getBinding().scProtocol.setPosition(PROTOCOL_CAB, true);
                mainActivity.removeTvTuner();
            } else if (value == VIDEO_TV) {
                //CABLE TV = TV TUNER
                mainActivity.initTvTuner();
            } else {
                mainActivity.removeTvTuner();
            }
        });


        getBinding().scUnit.setOnPositionChangedListener(position -> {

            DeviceSettingBean deviceSettingBean = getApp().getDeviceSettingBean();
            deviceSettingBean.setUnit_code(position);
            getApp().setDeviceSettingBean(deviceSettingBean);

            deviceSettingViewModel.unitCode.set(position);

            UNIT_E = position;

            initOdoDistance();
            save();

            fromUnit = true;
            initMinSpeedValue();

            initMaxSpeedValue();
        });

        getBinding().cbPauseMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            //off:0, on:1
//            DeviceSettingBean deviceSettingBean = getApp().getDeviceSettingBean();
//            deviceSettingBean.setPauseMode(isChecked ? ON : OFF);
//            getApp().setDeviceSettingBean(deviceSettingBean);

            deviceSettingViewModel.pauseMode.set(isChecked ? ON : OFF);
            save();
        });


//        getBinding().hideSleepAfter.setVisibility(getBinding().cbSleepMode.isChecked() ? View.GONE : View.VISIBLE);
//
//        getBinding().cbSleepMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            //off:0, on:1
////            DeviceSettingBean deviceSettingBean = getApp().getDeviceSettingBean();
////            deviceSettingBean.setSleep_mode(isChecked ? ON : OFF);
////            getApp().setDeviceSettingBean(deviceSettingBean);
//
//            deviceSettingViewModel.sleepMode.setValue(isChecked ? ON : OFF);
//            save();
//            setSleep();
//
//            if (isChecked) {
//                DeviceSettingBean dsb = getApp().getDeviceSettingBean();
//                if (dsb.getSleepAfter() == 0 || dsb.getSleepAfter() == SCREEN_TIMEOUT_NEVER) {
//                    dsb.setSleepAfter(SCREEN_TIMEOUT_15 / 1000);
//                    getApp().setDeviceSettingBean(dsb);
//                    setSleepAfterPick();
//                }
//
//                getBinding().hideSleepAfter.setVisibility(View.GONE);
//            } else {
//                getBinding().hideSleepAfter.setVisibility(View.VISIBLE);
//            }
//        });


        // TODO:
        int sPosition;
        if (getApp().getDeviceSettingBean().getSleep_mode() == ON) {
            sPosition = 1;
        } else if (getApp().getDeviceSettingBean().getSleep_mode() == OFF) {
            sPosition = 2;
        } else {
            sPosition = 0;
        }
        getBinding().hideSleepAfter.setVisibility(sPosition == 2 ? VISIBLE : GONE);
        getBinding().scSleepMode.setPosition(sPosition, false);
        getBinding().scSleepMode.setOnPositionChangedListener(position -> {
            int sleepValue;
            if (position == 0) {
                sleepValue = SLEEP_MODE_RETAIL;
            } else if (position == 1) {
                sleepValue = ON;
            } else {
                sleepValue = OFF;
            }

            deviceSettingViewModel.sleepMode.setValue(sleepValue);
            save();

            setSleep();

            switch (position) {
                case 0:
                case 1:
                    DeviceSettingBean dsb = getApp().getDeviceSettingBean();
                    if (dsb.getSleepAfter() == 0 || dsb.getSleepAfter() == SCREEN_TIMEOUT_NEVER) {
                        dsb.setSleepAfter(SCREEN_TIMEOUT_15 / 1000);
                        getApp().setDeviceSettingBean(dsb);
                        setSleepAfterPick();
                    }
                    getBinding().hideSleepAfter.setVisibility(GONE);

                    break;
                case 2:
                    getBinding().hideSleepAfter.setVisibility(VISIBLE);
                    break;
            }
        });


        //beep 雙向綁定
        deviceSettingViewModel.getBeep.observe(getViewLifecycleOwner(), value -> {
            save();
            if (isFirst) return;
            uartConsole.setBuzzer();
        });

//        getBinding().btnProtocolHow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });

//        getBinding().cbBeep.setOnCheckedChangeListener((buttonView, isChecked) -> {
//
//            deviceSettingViewModel.beep.setValue(isChecked);
//            save();
//            Log.d("BBBBBBBBB", "3333setBuzzer: " + deviceSettingViewModel.beep.getValue());
//            uartConsole.setBuzzer();
//        });


//        getBinding().scVideo.setOnPositionChangedListener(new SegmentedButtonGroup.OnPositionChangedListener() {
//            @Override
//            public void onPositionChanged(int position) {
//                Log.d("UART_CONSOLE", "scVideo onPositionChanged: " + position);
//                if (position == 0) {
//                    Log.d("UART_CONSOLE", "2222scVideo onPositionChanged: " + position);
//                    getBinding().scProtocol.setPosition(1, true);
//                }
//            }
//        });


        getBinding().scSpeedMM.setOnPositionChangedListener(position -> {
            if (position == 0) {
                getBinding().pickMinSpeed1.setVisibility(VISIBLE);
                getBinding().pickMinSpeed2.setVisibility(VISIBLE);
                getBinding().pickMaxSpeed1.setVisibility(INVISIBLE);
                getBinding().pickMaxSpeed2.setVisibility(INVISIBLE);
            } else{
                getBinding().pickMinSpeed1.setVisibility(INVISIBLE);
                getBinding().pickMinSpeed2.setVisibility(INVISIBLE);
                getBinding().pickMaxSpeed1.setVisibility(VISIBLE);
                getBinding().pickMaxSpeed2.setVisibility(VISIBLE);
            }
        });
    }


    // OptionsPickerView<String> pickSleepAfterHour;
    OptionsPickerView<String> pickSleepAfterMin;
    OptionsPickerView<String> pickSleepAfterSec;

    //  Sleep Time 時間預設 15:00(15分鐘)，最少 0 秒(disable)，最多60分00秒；
    @SuppressWarnings("unchecked")
    private void initSleepAfterSelect() {

//        List<String> list1 = new ArrayList<>(1);
//        for (int i = 0; i < 60; i++) {
//            list1.add(("0" + i).substring(("0" + i).length() - 2));
//        }
//
//        pickSleepAfterHour = getBinding().pickSleepAfterHour;
//        pickSleepAfterHour.setData(list1);
//        pickSleepAfterHour.setVisibleItems(2);
//        pickSleepAfterHour.setNormalItemTextColor(Color.parseColor("#5a7085"));
//        pickSleepAfterHour.setTextSize(32, true);
//        pickSleepAfterHour.setCurved(true);
//        pickSleepAfterHour.setLineSpacing(-12, true);
//        pickSleepAfterHour.setCurvedArcDirection(WheelView.CURVED_ARC_DIRECTION_CENTER);
//        pickSleepAfterHour.setCurvedArcDirectionFactor(1.0f);
//        pickSleepAfterHour.setSelectedItemTextColor(Color.parseColor("#ffffff"));
//        pickSleepAfterHour.setCyclic(true);
//        pickSleepAfterHour.setOnOptionsSelectedListener((opt1Pos, opt1Data, opt2Pos, opt2Data, opt3Pos, opt3Data) -> {
//            if (opt1Data == null) return;
//            DeviceSettingBean deviceSettingBean = getApp().getDeviceSettingBean();
//            long hour = opt1Pos * 60L * 60;
//            long min = pickSleepAfterMin.getOpt1SelectedPosition() * 60L;
//            long sec = pickSleepAfterSec.getOpt1SelectedPosition();
//            deviceSettingBean.setSleepAfter(hour + min + sec);
//            getApp().setDeviceSettingBean(deviceSettingBean);
//
//            if (deviceSettingBean.getSleepAfter() == 0) {
//                getBinding().cbSleepMode.setChecked(false);
//            }
//
//            setSleep();
//        });


        List<String> list2 = new ArrayList<>(1);
        for (int i = 0; i < 61; i++) {
            list2.add(("0" + i).substring(("0" + i).length() - 2));
        }

        pickSleepAfterMin = getBinding().pickSleepAfterMin;
        pickSleepAfterMin.setData(list2);
        pickSleepAfterMin.setVisibleItems(2);
        pickSleepAfterMin.setNormalItemTextColor(Color.parseColor("#5a7085"));
        pickSleepAfterMin.setTextSize(32, false);
        pickSleepAfterMin.setCurved(true);
        pickSleepAfterMin.setLineSpacing(-12, true);
        pickSleepAfterMin.setCurvedArcDirection(WheelView.CURVED_ARC_DIRECTION_CENTER);
        pickSleepAfterMin.setCurvedArcDirectionFactor(1.0f);
        pickSleepAfterMin.setSelectedItemTextColor(Color.parseColor("#ffffff"));
        pickSleepAfterMin.setCyclic(true);
        pickSleepAfterMin.setOnOptionsSelectedListener((opt1Pos, opt1Data, opt2Pos, opt2Data, opt3Pos, opt3Data) -> {
            if (opt1Data == null) return;
            DeviceSettingBean deviceSettingBean = getApp().getDeviceSettingBean();
            //   long hour = pickSleepAfterHour.getOpt1SelectedPosition() * 60L * 60;
            long min = opt1Pos * 60L;
            long sec = pickSleepAfterSec.getOpt1SelectedPosition();


            if (opt1Pos == 60) {
                if (sec > 0) {
                    pickSleepAfterSec.setOpt1SelectedPosition(0, false);
                    sec = 0;
                }
                getBinding().hideSleepSec.setVisibility(VISIBLE);
            } else {
                getBinding().hideSleepSec.setVisibility(GONE);
            }

            //   deviceSettingBean.setSleepAfter(hour + min + sec);
            deviceSettingBean.setSleepAfter(min + sec);
            deviceSettingViewModel.sleepAfter.set(min + sec);
            getApp().setDeviceSettingBean(deviceSettingBean);


            if (deviceSettingBean.getSleepAfter() == 0) {
                getBinding().scSleepMode.setPosition(2, false);
            }

//            if (deviceSettingBean.getSleepAfter() == 0) {
//                getBinding().cbSleepMode.setChecked(false);
//            }

            //設定系統休眠時間
            setSleep();
        });


        List<String> list3 = new ArrayList<>(1);
        for (int i = 0; i < 60; i++) {
            list3.add(("0" + i).substring(("0" + i).length() - 2));
        }

        pickSleepAfterSec = getBinding().pickSleepAfterSec;
        pickSleepAfterSec.setData(list3);
        pickSleepAfterSec.setVisibleItems(2);
        pickSleepAfterSec.setNormalItemTextColor(Color.parseColor("#5a7085"));
        pickSleepAfterSec.setTextSize(32, false);
        pickSleepAfterSec.setCurved(true);
        pickSleepAfterSec.setLineSpacing(-12, true);
        pickSleepAfterSec.setCurvedArcDirection(WheelView.CURVED_ARC_DIRECTION_CENTER);
        pickSleepAfterSec.setCurvedArcDirectionFactor(1.0f);
        pickSleepAfterSec.setSelectedItemTextColor(Color.parseColor("#ffffff"));
        pickSleepAfterSec.setCyclic(true);
        pickSleepAfterSec.setOnOptionsSelectedListener((opt1Pos, opt1Data, opt2Pos, opt2Data, opt3Pos, opt3Data) -> {
            if (opt1Data == null) return;

            DeviceSettingBean deviceSettingBean = getApp().getDeviceSettingBean();
            //   long hour = pickSleepAfterHour.getOpt1SelectedPosition() * 60L * 60;
            long min = pickSleepAfterMin.getOpt1SelectedPosition() * 60L;

            //  deviceSettingBean.setSleepAfter(hour + min + opt1Pos);
            deviceSettingBean.setSleepAfter(min + opt1Pos);
            getApp().setDeviceSettingBean(deviceSettingBean);
//            if (deviceSettingBean.getSleepAfter() == 0) {
//                getBinding().cbSleepMode.setChecked(false);
//            }
            if (deviceSettingBean.getSleepAfter() == 0) {
                getBinding().scSleepMode.setPosition(2, false);
            }

            setSleep();
        });


        //    getBinding().cbSleepMode.setChecked(getApp().getDeviceSettingBean().getSleep_mode() == ON);// 1 on, 0 0ff

        setSleepAfterPick();

    }

    OptionsPickerView<String> pickUseTimeLimitHour;
    OptionsPickerView<String> pickUseTimeLimitMin;
    OptionsPickerView<String> pickUseTimeLimitSec;

    @SuppressWarnings("unchecked")
    private void initUseTimeLimit() {

        int currentUseTimeLimit = (int) getApp().getDeviceSettingBean().getUseTimeLimit();

        List<String> list1 = new ArrayList<>(1);
        for (int i = 0; i < 2; i++) {
            list1.add(("0" + i).substring(("0" + i).length() - 2));
        }

        pickUseTimeLimitHour = getBinding().pickUseTimeLimitHour;
        pickUseTimeLimitHour.setData(list1);
        pickUseTimeLimitHour.setVisibleItems(2);
        pickUseTimeLimitHour.setNormalItemTextColor(Color.parseColor("#5a7085"));
        pickUseTimeLimitHour.setTextSize(32, false);
        pickUseTimeLimitHour.setCurved(true);
        pickUseTimeLimitHour.setLineSpacing(-12, true);
        pickUseTimeLimitHour.setCurvedArcDirection(WheelView.CURVED_ARC_DIRECTION_CENTER);
        pickUseTimeLimitHour.setCurvedArcDirectionFactor(1.0f);
        pickUseTimeLimitHour.setSelectedItemTextColor(Color.parseColor("#ffffff"));
        pickUseTimeLimitHour.setCyclic(true);


        pickUseTimeLimitMin = getBinding().pickUseTimeLimitMin;

        //大於等於60分鐘，分 > 39
        int max = currentUseTimeLimit >= (60 * 60) ? 40 : 60;
        pickUseTimeLimitMin.setData(getUseTimeLimitMin(max));

        pickUseTimeLimitMin.setVisibleItems(2);
        pickUseTimeLimitMin.setNormalItemTextColor(Color.parseColor("#5a7085"));
        pickUseTimeLimitMin.setTextSize(32, false);
        pickUseTimeLimitMin.setCurved(true);
        pickUseTimeLimitMin.setLineSpacing(-12, true);
        pickUseTimeLimitMin.setCurvedArcDirection(WheelView.CURVED_ARC_DIRECTION_CENTER);
        pickUseTimeLimitMin.setCurvedArcDirectionFactor(1.0f);
        pickUseTimeLimitMin.setSelectedItemTextColor(Color.parseColor("#ffffff"));
        pickUseTimeLimitMin.setCyclic(true);


        List<String> list3 = new ArrayList<>(1);
        for (int i = 0; i < 1; i++) {
            list3.add(("0" + i).substring(("0" + i).length() - 2));
        }

        pickUseTimeLimitSec = getBinding().pickUseTimeLimitSec;


        pickUseTimeLimitSec.setData(list3);
        pickUseTimeLimitSec.setVisibleItems(2);
        pickUseTimeLimitSec.setNormalItemTextColor(Color.parseColor("#5a7085"));
        pickUseTimeLimitSec.setTextSize(32, false);
        pickUseTimeLimitSec.setCurved(true);
        pickUseTimeLimitSec.setLineSpacing(-12, true);
        pickUseTimeLimitSec.setCurvedArcDirection(WheelView.CURVED_ARC_DIRECTION_CENTER);
        pickUseTimeLimitSec.setCurvedArcDirectionFactor(1.0f);
        pickUseTimeLimitSec.setSelectedItemTextColor(Color.parseColor("#ffffff"));
        pickUseTimeLimitSec.setCyclic(false);

        //設定初始值
        pickUseTimeLimitHour.setOpt1SelectedPosition(secToTimeHour(currentUseTimeLimit), false);
        pickUseTimeLimitMin.setOpt1SelectedPosition(secToTimeMin(currentUseTimeLimit), false);
        pickUseTimeLimitSec.setOpt1SelectedPosition(secToTimeSec(currentUseTimeLimit), false);


        //CheckBox 初始值
        getBinding().cbUseTimeLimit.setChecked(getApp().getDeviceSettingBean().getIsUseTimeLimit() == ON);
        getBinding().hideUseTimeLimit.setVisibility(getBinding().cbUseTimeLimit.isChecked() ? GONE : VISIBLE);
        getBinding().cbUseTimeLimit.setOnCheckedChangeListener((compoundButton, isChecked) -> {

            DeviceSettingBean dsb = getApp().getDeviceSettingBean();
            if (isChecked) {
                if (dsb.getUseTimeLimit() == 0) {
                    dsb.setUseTimeLimit(DEFAULT_USE_TIME_LIMIT);
                }
                long t = dsb.getUseTimeLimit();

                pickUseTimeLimitHour.setOpt1SelectedPosition(secToTimeHour(t), false);
                pickUseTimeLimitMin.setOpt1SelectedPosition(secToTimeMin(t), false);
                pickUseTimeLimitSec.setOpt1SelectedPosition(secToTimeSec(t), false);
                dsb.setIsUseTimeLimit(ON);
                getBinding().hideUseTimeLimit.setVisibility(GONE);
            } else {
                dsb.setIsUseTimeLimit(OFF);
                //   deviceSettingViewModel.isUseTimeLimit.set(OFF);
                getBinding().hideUseTimeLimit.setVisibility(VISIBLE);
            }

            getApp().setDeviceSettingBean(dsb);

        });


        pickUseTimeLimitHour.setOnOptionsSelectedListener((opt1Pos, opt1Data, opt2Pos, opt2Data, opt3Pos, opt3Data) -> {
            if (opt1Data == null) return;
            DeviceSettingBean deviceSettingBean = getApp().getDeviceSettingBean();

            if (pickUseTimeLimitMin.getOpt1SelectedPosition() == 0 && opt1Pos == 0) {
                pickUseTimeLimitMin.setOpt1SelectedPosition(1);
                return;
            }

//            if (deviceSettingBean.getUseTimeLimit() == 0) {
//                getBinding().cbUseTimeLimit.setChecked(false);
//            }

            pickUseTimeLimitMin.setData(getUseTimeLimitMin("01".equals(opt1Data) ? 40 : 60));


            long hour = opt1Pos * 60L * 60;
            long min = pickUseTimeLimitMin.getOpt1SelectedPosition() * 60L;
            long sec = pickUseTimeLimitSec.getOpt1SelectedPosition();
            deviceSettingBean.setUseTimeLimit(hour + min + sec);
            getApp().setDeviceSettingBean(deviceSettingBean);

        });


        pickUseTimeLimitMin.setOnOptionsSelectedListener((opt1Pos, opt1Data, opt2Pos, opt2Data, opt3Pos, opt3Data) -> {
            if (opt1Data == null) return;
            DeviceSettingBean deviceSettingBean = getApp().getDeviceSettingBean();
            long hour = pickUseTimeLimitHour.getOpt1SelectedPosition() * 60L * 60;
            long min = opt1Pos * 60L;
            long sec = pickUseTimeLimitSec.getOpt1SelectedPosition();

//            if (deviceSettingBean.getUseTimeLimit() == 0) {
//                getBinding().cbUseTimeLimit.setChecked(false);
//            }

            if (pickUseTimeLimitHour.getOpt1SelectedPosition() == 0 && opt1Pos == 0) {
                pickUseTimeLimitMin.setOpt1SelectedPosition(1);
                return;
            }

            deviceSettingBean.setUseTimeLimit(hour + min + sec);
            getApp().setDeviceSettingBean(deviceSettingBean);

        });


//        pickUseTimeLimitSec.setOnOptionsSelectedListener((opt1Pos, opt1Data, opt2Pos, opt2Data, opt3Pos, opt3Data) -> {
//            if (opt1Data == null) return;
//            DeviceSettingBean deviceSettingBean = getApp().getDeviceSettingBean();
//            long hour = pickUseTimeLimitHour.getOpt1SelectedPosition() * 60L * 60;
//            long min = pickUseTimeLimitMin.getOpt1SelectedPosition() * 60L;
//            deviceSettingBean.setUseTimeLimit(hour + min + opt1Pos);
//            getApp().setDeviceSettingBean(deviceSettingBean);
//        });
    }

    private List<String> getUseTimeLimitMin(int max) {
        List<String> list = new ArrayList<>(1);
        for (int i = 0; i < max; i++) {
            list.add(("0" + i).substring(("0" + i).length() - 2));
        }
        return list;
    }

    //AUTO PAUSE
    // Auto Pause 時間預設 3:00(3分鐘)，最少 1 分鐘，最多30分00秒；
    @SuppressWarnings("unchecked")
    private void initAutoPauseSelect() {

//        if (!isTreadmill) {
//            getBinding().hidePauseAfter.setVisibility(View.GONE);
//            return;
//        }

//        OptionsPickerView<String> pickPauseAfterHour = getBinding().pickPauseAfterHour;
        OptionsPickerView<String> pickPauseAfterSec = getBinding().pickPauseAfterSec;
        OptionsPickerView<String> pickPauseAfterMin = getBinding().pickPauseAfterMin;

        //Auto Pause只會在 workout開始後才會偵測，當橋接板error code2 bit4 =1時，
        // 表示跑帶在動作但無人在使用；因此，當此狀況持續時間超過上表設定時間後，上表直接結束此次workout，並logout，回到login畫面；
        getBinding().cbAutoPause.setChecked(deviceSettingViewModel.autoPause.getValue() == ON);

        getBinding().hidePauseAfter.setVisibility(deviceSettingViewModel.autoPause.getValue() == ON ? GONE : VISIBLE);
//        if (deviceSettingViewModel.autoPause.getValue() == 1 )

        getBinding().cbAutoPause.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                deviceSettingViewModel.autoPause.setValue(isChecked ? ON : OFF);

                getBinding().hidePauseAfter.setVisibility(isChecked ? GONE : VISIBLE);

                if (isChecked) {
                    if (getApp().getDeviceSettingBean().getPauseAfter() == 0) {
                        DeviceSettingBean d = getApp().getDeviceSettingBean();
                        d.setPauseAfter(DEFAULT_AUTO_PAUSE_TIME);
                        getApp().setDeviceSettingBean(d);
                        deviceSettingViewModel.pauseAfter.set(0);

                        long timeLimit = getApp().getDeviceSettingBean().getPauseAfter();
//                        pickPauseAfterHour.setOpt1SelectedPosition(secToTimeHour(timeLimit), true);
                        pickPauseAfterMin.setOpt1SelectedPosition(secToTimeMin(timeLimit), false);
                        pickPauseAfterSec.setOpt1SelectedPosition(secToTimeSec(timeLimit), false);
                    }
                }

            }
        });


//        List<String> list1 = new ArrayList<>(1);
//        for (int i = 0; i < 60; i++) {
//            list1.add(("0" + i).substring(("0" + i).length() - 2));
//        }

        List<String> list2 = new ArrayList<>(1);
        for (int i = 1; i < 31; i++) {
            list2.add(("0" + i).substring(("0" + i).length() - 2));
        }

        List<String> list3 = new ArrayList<>(1);
        for (int i = 0; i < 60; i++) {
            list3.add(("0" + i).substring(("0" + i).length() - 2));
        }

//        pickPauseAfterHour.setData(list1);
//        pickPauseAfterHour.setVisibleItems(2);
//        pickPauseAfterHour.setNormalItemTextColor(Color.parseColor("#5a7085"));
//        pickPauseAfterHour.setTextSize(32, true);
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
//
//            if (deviceSettingBean.getPauseAfter() == 0) {
//                getBinding().cbAutoPause.setChecked(false);
//            }
//        });

        pickPauseAfterMin.setData(list2);
        pickPauseAfterMin.setVisibleItems(2);
        pickPauseAfterMin.setNormalItemTextColor(Color.parseColor("#5a7085"));
        pickPauseAfterMin.setTextSize(32, false);
        pickPauseAfterMin.setCurved(true);
        pickPauseAfterMin.setLineSpacing(-12, true);
        pickPauseAfterMin.setCurvedArcDirection(WheelView.CURVED_ARC_DIRECTION_CENTER);
        pickPauseAfterMin.setCurvedArcDirectionFactor(1.0f);
        pickPauseAfterMin.setSelectedItemTextColor(Color.parseColor("#ffffff"));
        pickPauseAfterMin.setCyclic(true);
        pickPauseAfterMin.setOnOptionsSelectedListener((opt1Pos, opt1Data, opt2Pos, opt2Data, opt3Pos, opt3Data) -> {
            if (opt1Data == null) return;
            DeviceSettingBean deviceSettingBean = getApp().getDeviceSettingBean();
            //  long hour = pickPauseAfterHour.getOpt1SelectedPosition() * 60L * 60;
            long min = (opt1Pos + 1) * 60L; //1~30 要+1
            long sec = pickPauseAfterSec.getOpt1SelectedPosition();
            //  deviceSettingBean.setPauseAfter(hour + min + sec);


            if ((opt1Pos + 1) == 30) {
                if (sec > 0) {
                    pickPauseAfterSec.setOpt1SelectedPosition(0, false);
                    sec = 0;
                }
                getBinding().hideAutoPauseSec.setVisibility(VISIBLE);
            } else {
                getBinding().hideAutoPauseSec.setVisibility(GONE);
            }

            deviceSettingBean.setPauseAfter(min + sec);
            getApp().setDeviceSettingBean(deviceSettingBean);
            deviceSettingViewModel.pauseAfter.set(deviceSettingBean.getPauseAfter());

//            if (deviceSettingBean.getPauseAfter() == 0) {
//                getBinding().cbAutoPause.setChecked(false);
//            }
        });

        pickPauseAfterSec.setData(list3);
        pickPauseAfterSec.setVisibleItems(2);
        pickPauseAfterSec.setNormalItemTextColor(Color.parseColor("#5a7085"));
        pickPauseAfterSec.setTextSize(32, false);
        pickPauseAfterSec.setCurved(true);
        pickPauseAfterSec.setLineSpacing(-12, true);
        pickPauseAfterSec.setCurvedArcDirection(WheelView.CURVED_ARC_DIRECTION_CENTER);
        pickPauseAfterSec.setCurvedArcDirectionFactor(1.0f);
        pickPauseAfterSec.setSelectedItemTextColor(Color.parseColor("#ffffff"));
        pickPauseAfterSec.setCyclic(true);
        pickPauseAfterSec.setOnOptionsSelectedListener((opt1Pos, opt1Data, opt2Pos, opt2Data, opt3Pos, opt3Data) -> {
            if (opt1Data == null) return;

            DeviceSettingBean deviceSettingBean = getApp().getDeviceSettingBean();
            //  long hour = pickPauseAfterHour.getOpt1SelectedPosition() * 60L * 60;
            long min = (pickPauseAfterMin.getOpt1SelectedPosition() + 1) * 60L; //1~30 要+1
            //  deviceSettingBean.setPauseAfter(hour + min + opt1Pos);
            deviceSettingBean.setPauseAfter(min + opt1Pos);
            getApp().setDeviceSettingBean(deviceSettingBean);
            deviceSettingViewModel.pauseAfter.set(deviceSettingBean.getPauseAfter());
            if (deviceSettingBean.getPauseAfter() == 0) {
                getBinding().cbAutoPause.setChecked(false);
            }
        });

        //初始值
        long timeLimit = getApp().getDeviceSettingBean().getPauseAfter();

        //   pickPauseAfterHour.setOpt1SelectedPosition(secToTimeHour(timeLimit), false);
        pickPauseAfterMin.setOpt1SelectedPosition(secToTimeMin(timeLimit) - 1, false);//1~30
        pickPauseAfterSec.setOpt1SelectedPosition(secToTimeSec(timeLimit), false);
    }


    /**
     * 最低速範圍：
     * 公制: 0.5 km/h (預設)  ~  0.8 km/h
     * 英制: 0.3 MPH (預設)  ~  0.5 MPH
     */
    //10位數
    private int get1(int v) {
        return (v / 10) % 10;
    }

    //個位數
    private int get2(int v) {
        return (v % 10);
    }

    OptionsPickerView<String> pickMinSpeed1;
    OptionsPickerView<String> pickMinSpeed2;

    List<String> list2;
    List<String> list3;

    OptionsPickerView<String> pickMaxSpeed1;
    OptionsPickerView<String> pickMaxSpeed2;

    List<String> list1MaxMU;
    List<String> list1MaxIU;

    List<String> list2MaxMU;
    List<String> list2MaxIU;

    private void initMaxSpeed() {

        pickMaxSpeed1 = getBinding().pickMaxSpeed1;
        pickMaxSpeed2 = getBinding().pickMaxSpeed2;


        // 24 , 25
        list1MaxMU = new ArrayList<>(2);
        list1MaxMU.add(String.valueOf(24));
        list1MaxMU.add(String.valueOf(25));

        //15
        list1MaxIU = new ArrayList<>(1);
        list1MaxIU.add(String.valueOf(15));


        //公 0
        list2MaxMU = new ArrayList<>(1);
        list2MaxMU.add(String.valueOf(0));

        //英: 0 , 5
        list2MaxIU = new ArrayList<>(2);
        list2MaxIU.add(String.valueOf(0));
        list2MaxIU.add(String.valueOf(5));


        if (UNIT_E == IMPERIAL) {
            pickMaxSpeed1.setData(list1MaxIU);
        } else {
            pickMaxSpeed1.setData(list1MaxMU);
        }

        pickMaxSpeed1.setVisibleItems(2);
        pickMaxSpeed1.setNormalItemTextColor(Color.parseColor("#5a7085"));
        pickMaxSpeed1.setTextSize(32, false);
        pickMaxSpeed1.setCurved(true);
        pickMaxSpeed1.setLineSpacing(-12, true);
        pickMaxSpeed1.setCurvedArcDirection(WheelView.CURVED_ARC_DIRECTION_CENTER);
        pickMaxSpeed1.setCurvedArcDirectionFactor(1.0f);
        pickMaxSpeed1.setSelectedItemTextColor(Color.parseColor("#ffffff"));
        pickMaxSpeed1.setCyclic(UNIT_E != IMPERIAL); //無限滾
   //     pickMaxSpeed1.setCyclic(true);




        if (UNIT_E == IMPERIAL) {
            pickMaxSpeed2.setData(list2MaxIU);
        } else {
            pickMaxSpeed2.setData(list2MaxMU);
        }

        pickMaxSpeed2.setVisibleItems(2);
        pickMaxSpeed2.setNormalItemTextColor(Color.parseColor("#5a7085"));
        pickMaxSpeed2.setTextSize(32, false);
        pickMaxSpeed2.setCurved(true);
        pickMaxSpeed2.setLineSpacing(-12, true);
        pickMaxSpeed2.setCurvedArcDirection(WheelView.CURVED_ARC_DIRECTION_CENTER);
        pickMaxSpeed2.setCurvedArcDirectionFactor(1.0f);
        pickMaxSpeed2.setSelectedItemTextColor(Color.parseColor("#ffffff"));
        pickMaxSpeed2.setCyclic(true);

    //    initMaxSpeedValue();

        pickMaxSpeed1.setOnOptionsSelectedListener((opt1Pos, opt1Data, opt2Pos, opt2Data, opt3Pos, opt3Data) -> {
            if (opt1Data == null) return;
            if (fromUnit) return;

            Log.d("MAXXXXXXXXX", "pickMaxSpeed1: " + opt1Data);

            String s1 = opt1Data;
            String s2 = pickMaxSpeed2.getOpt1SelectedData();
            int xxxx = Integer.parseInt(s1 + s2);
            Log.d("MAXXXXXXXXX", "pickMaxSpeed1: " + xxxx);
            DeviceSettingBean dsb = getApp().getDeviceSettingBean();
            if (UNIT_E == IMPERIAL) {
                dsb.setMaxSpeedIu(xxxx);
                int msmu= MAX_SPD_MU_MAX;
                if (dsb.getMaxSpeedIu() == 150){
                    msmu = 240;
                }
                if (dsb.getMaxSpeedIu() == 155) {
                    msmu = 250;
                }
                dsb.setMaxSpeedMu(msmu);
            } else {
                dsb.setMaxSpeedMu(xxxx);
                int msiu= MAX_SPD_IU_MAX;
                if (dsb.getMaxSpeedMu() == 240){
                    msiu = 150;
                }
                if (dsb.getMaxSpeedMu() == 250) {
                    msiu = 155;
                }
                dsb.setMaxSpeedIu(msiu);
            }
            getApp().setDeviceSettingBean(dsb);

            int iu = (int) dsb.getMaxSpeedIu();
            MAX_SPD_IU_MAX = (iu >= 150 && iu <= 155) ? iu : 150;

            int mu = (int) dsb.getMaxSpeedMu();
            MAX_SPD_MU_MAX = (mu >= 240 && mu <= 250) ? mu : 240;

            Log.d("MAXXXXXXXXX", "🐶🐶🐶🐶MAX_SPD_IU_MAX: " + MAX_SPD_IU_MAX  +", MAX_SPD_MU_MAX: "+ MAX_SPD_MU_MAX);

        });


        pickMaxSpeed2.setOnOptionsSelectedListener((opt1Pos, opt1Data, opt2Pos, opt2Data, opt3Pos, opt3Data) -> {
            if (opt1Data == null) return;
            if (fromUnit) return;
//            int s1 = pickMinSpeed1.getOpt1SelectedPosition() * 10;

            Log.d("MAXXXXXXXXX", "pickMaxSpeed2: " + opt1Data);
            String s1 = pickMaxSpeed1.getOpt1SelectedData();
            String s2 = opt1Data;
            int xxxx = Integer.parseInt(s1 + s2);
            Log.d("MAXXXXXXXXX", "pickMaxSpeed2: " + xxxx);
            DeviceSettingBean dsb = getApp().getDeviceSettingBean();
            if (UNIT_E == IMPERIAL) {
                dsb.setMaxSpeedIu(xxxx);
                int msmu= MAX_SPD_MU_MAX;
                if (dsb.getMaxSpeedIu() == 150){
                    msmu = 240;
                }
                if (dsb.getMaxSpeedIu() == 155) {
                    msmu = 250;
                }
                dsb.setMaxSpeedMu(msmu);
            } else {
                dsb.setMaxSpeedMu(xxxx);
                int msiu= MAX_SPD_IU_MAX;
                if (dsb.getMaxSpeedMu() == 240){
                    msiu = 150;
                }
                if (dsb.getMaxSpeedMu() == 250) {
                    msiu = 155;
                }
                dsb.setMaxSpeedIu(msiu);
            }
            getApp().setDeviceSettingBean(dsb);

            int iu = (int) dsb.getMaxSpeedIu();
            MAX_SPD_IU_MAX = (iu >= 150 && iu <= 155) ? iu : 150;

            int mu = (int) dsb.getMaxSpeedMu();
            MAX_SPD_MU_MAX = (mu >= 240 && mu <= 250) ? mu : 240;


            Log.d("MAXXXXXXXXX", "🐶🐶🐶🐶MAX_SPD_IU_MAX: " + MAX_SPD_IU_MAX  +", MAX_SPD_MU_MAX: "+ MAX_SPD_MU_MAX);
        });

    }

    private void initMinSpeed() {

        new RxTimer().timer(50,number -> {
            if (getBinding() != null) {
                getBinding().pickMaxSpeed1.setVisibility(INVISIBLE);
                getBinding().pickMaxSpeed2.setVisibility(INVISIBLE);
            }
        });



        //改工程模式預設速度  > DeviceSettingBean >minSpeedMu

        //      Log.d("CCC@@@@", "#######"+dsb.getMinSpeedIu() +","+"sI1: " + sI1 + ", sI2:" + sI2 + ", sM1:" + sM1 + ",sM2:" + sM2);

        pickMinSpeed1 = getBinding().pickMinSpeed1;
        pickMinSpeed2 = getBinding().pickMinSpeed2;

        List<String> list1 = new ArrayList<>(1);
//        for (int i = 0; i < 10; i++) {
//            list1.add(String.valueOf(i));
//        }
        list1.add(String.valueOf(0));

//        List<String> list2 = new ArrayList<>(1);
//        for (int i = 0; i < 10; i++) {
//            list2.add(String.valueOf(i));
//        }

        //英
        list2 = new ArrayList<>(1);
        for (int i = 3; i <= 5; i++) {
            list2.add(String.valueOf(i));
        }

        //公
        list3 = new ArrayList<>(1);
        for (int i = 5; i <= 8; i++) {
            list3.add(String.valueOf(i));
        }


        pickMinSpeed1.setData(list1);
        pickMinSpeed1.setVisibleItems(2);
        pickMinSpeed1.setNormalItemTextColor(Color.parseColor("#5a7085"));
        pickMinSpeed1.setTextSize(32, false);
        pickMinSpeed1.setCurved(true);
        pickMinSpeed1.setLineSpacing(-12, true);
        pickMinSpeed1.setCurvedArcDirection(WheelView.CURVED_ARC_DIRECTION_CENTER);
        pickMinSpeed1.setCurvedArcDirectionFactor(1.0f);
        pickMinSpeed1.setSelectedItemTextColor(Color.parseColor("#ffffff"));
//        pickMinSpeed1.setCyclic(true);
        pickMinSpeed1.setCyclic(false);

        if (UNIT_E == IMPERIAL) {
            pickMinSpeed2.setData(list2);
        } else {
            pickMinSpeed2.setData(list3);
        }

        pickMinSpeed2.setVisibleItems(2);
        pickMinSpeed2.setNormalItemTextColor(Color.parseColor("#5a7085"));
        pickMinSpeed2.setTextSize(32, false);
        pickMinSpeed2.setCurved(true);
        pickMinSpeed2.setLineSpacing(-12, true);
        pickMinSpeed2.setCurvedArcDirection(WheelView.CURVED_ARC_DIRECTION_CENTER);
        pickMinSpeed2.setCurvedArcDirectionFactor(1.0f);
        pickMinSpeed2.setSelectedItemTextColor(Color.parseColor("#ffffff"));
        pickMinSpeed2.setCyclic(true);

  //      initMinSpeedValue();

        pickMinSpeed1.setOnOptionsSelectedListener((opt1Pos, opt1Data, opt2Pos, opt2Data, opt3Pos, opt3Data) -> {
//            if (opt1Data == null) return;
//
//            if (fromUnit) return;
//
//            int s1 = opt1Pos * 10;
//            int s2 = pickMinSpeed2.getOpt1SelectedPosition();
//
//            DeviceSettingBean dsb = getApp().getDeviceSettingBean();
//            if (UNIT_E == IMPERIAL) {
//                dsb.setMinSpeedIu(s1 + s2);
//                dsb.setMinSpeedMu((int) mi2km(s1 + s2));
//            } else {
//                dsb.setMinSpeedIu((int) km2mi(s1 + s2));
//                dsb.setMinSpeedMu(s1 + s2);
//            }
//            getApp().setDeviceSettingBean(dsb);
//
//            OPT_SETTINGS.MIN_SPD_MU = dsb.getMinSpeedMu();
//            OPT_SETTINGS.MIN_SPD_IU = dsb.getMinSpeedIu();
//
//            Log.d("MMMMMMMMIIIIII", "11111initMinSpeedValue: " + dsb.getMinSpeedIu() +","+ dsb.getMinSpeedMu());

        });


        pickMinSpeed2.setOnOptionsSelectedListener((opt1Pos, opt1Data, opt2Pos, opt2Data, opt3Pos, opt3Data) -> {
            if (opt1Data == null) return;
            if (fromUnit) return;
//            int s1 = pickMinSpeed1.getOpt1SelectedPosition() * 10;
            int s1 = 0;
            int s2 = Integer.parseInt(opt1Data);

            DeviceSettingBean dsb = getApp().getDeviceSettingBean();
            if (UNIT_E == IMPERIAL) {
                dsb.setMinSpeedIu(s1 + s2);
                dsb.setMinSpeedMu((int) mi2km(s1 + s2));
            } else {
                dsb.setMinSpeedIu((int) km2mi(s1 + s2));
                dsb.setMinSpeedMu(s1 + s2);
            }
            getApp().setDeviceSettingBean(dsb);
//
//            OPT_SETTINGS.MIN_SPD_MU = dsb.getMinSpeedMu();
//            OPT_SETTINGS.MIN_SPD_IU = dsb.getMinSpeedIu();

            OPT_SETTINGS.MIN_SPD_IU = dsb.getMinSpeedIu() > MIN_SPD_IU_MAX || dsb.getMinSpeedIu() < MIN_SPD_IU_MIN ? MIN_SPD_IU_MIN : dsb.getMinSpeedIu();
            OPT_SETTINGS.MIN_SPD_MU = dsb.getMinSpeedMu() > MIN_SPD_MU_MAX || dsb.getMinSpeedMu() < MIN_SPD_MU_MIN ? MIN_SPD_MU_MIN : dsb.getMinSpeedMu();


        });

    }



    private void initMaxSpeedValue() {

        if (UNIT_E == IMPERIAL) {
            pickMaxSpeed1.setData(list1MaxIU);
            pickMaxSpeed2.setData(list2MaxIU);

            pickMaxSpeed1.setCyclic(false);
            pickMaxSpeed2.setCyclic(true);
        } else {
            pickMaxSpeed1.setData(list1MaxMU);
            pickMaxSpeed2.setData(list2MaxMU);

            pickMaxSpeed1.setCyclic(true);
            pickMaxSpeed2.setCyclic(false);
        }

//        DeviceSettingBean dsb = getApp().getDeviceSettingBean();
//        int sI1 = dsb.getMaxSpeedIu(); //getMaxSpeedIu只會有150,或155,  list1MaxIU 內容 15  ; 數值改成Position
//        int sI2 = dsb.getMaxSpeedIu(); //list2MaxIU 內容 0,5 ; 數值改成Position
//
//        int sM1 = dsb.getMaxSpeedMu(); //getMaxSpeedMu 只會有 240或250,  list1MaxMU 內容 24 , 25 ; 數值改成Position
//        int sM2 = dsb.getMaxSpeedMu(); //list2MaxMU 內容只有0; 數值改成Position
//
//        pickMaxSpeed1.setOpt1SelectedPosition(UNIT_E == IMPERIAL ? sI1 : sM1, false);
//        pickMaxSpeed2.setOpt1SelectedPosition(UNIT_E == IMPERIAL ? sI2 : sM2, false);


        DeviceSettingBean dsb = getApp().getDeviceSettingBean();

        int rawIu = (int) dsb.getMaxSpeedIu();          // 150 或 155
        String tensIU  = String.valueOf(rawIu / 10); // "15"
        String unitsIU = String.valueOf(rawIu % 10); // "0" 或 "5"

        int sI1 = list1MaxIU.indexOf(tensIU);
        if (sI1 < 0) sI1 = 0;  // 找不到就用第一个

        int sI2 = list2MaxIU.indexOf(unitsIU);
        if (sI2 < 0) sI2 = 0;  // 找不到就用第一个

        int rawMu = (int) dsb.getMaxSpeedMu();          // 240 或 250
        String tensMU  = String.valueOf(rawMu / 10); // "24" 或 "25"
        String unitsMU = String.valueOf(rawMu % 10); // 一定是 "0"

        int sM1 = list1MaxMU.indexOf(tensMU);
        if (sM1 < 0) sM1 = 0;

        int sM2 = list2MaxMU.indexOf(unitsMU);
        if (sM2 < 0) sM2 = 0;

        if (UNIT_E == IMPERIAL) {
            pickMaxSpeed1.setOpt1SelectedPosition(sI1, false);
            pickMaxSpeed2.setOpt1SelectedPosition(sI2, false);
        } else {
            pickMaxSpeed1.setOpt1SelectedPosition(sM1, false);
            pickMaxSpeed2.setOpt1SelectedPosition(sM2, false);
        }


        fromUnit = false;

        Log.d("MAXXXXXXXXX", "initMaxSpeedValue: " + dsb.getMaxSpeedIu() +","+ dsb.getMaxSpeedMu() );
    }



    private void initMinSpeedValue() {

        if (UNIT_E == IMPERIAL) {
            pickMinSpeed2.setData(list2);
        } else {
            pickMinSpeed2.setData(list3);
        }

        DeviceSettingBean dsb = getApp().getDeviceSettingBean();
        int sI1 = get1(dsb.getMinSpeedIu());
        int sI2 = get2(dsb.getMinSpeedIu() - 3); //數值改成Position

        int sM1 = get1(dsb.getMinSpeedMu());
        int sM2 = get2(dsb.getMinSpeedMu() - 5); //數值改成Position
        pickMinSpeed1.setOpt1SelectedPosition(UNIT_E == IMPERIAL ? sI1 : sM1, false);
        pickMinSpeed2.setOpt1SelectedPosition(UNIT_E == IMPERIAL ? sI2 : sM2, false);
        fromUnit = false;
    }

    /**
     * 開啟關閉自動亮度調節
     */
    public boolean autoBrightness(Context activity, boolean flag) {
        int value;
        if (flag) {
            value = Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC; //開啟
        } else {
            value = Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL;//關閉
        }
        return Settings.System.putInt(activity.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                value);
    }

    private void initDisplayBrightnessBar() {

//        int brightnessSettingMaximumId = getResources().getIdentifier("config_screenBrightnessSettingMaximum", "integer", "android");
//        int brightnessSettingMaximum = getResources().getInteger(brightnessSettingMaximumId);
//
//        Log.d("亮度", "initDisplayBrightnessBar: " + brightnessSettingMaximumId +","+ brightnessSettingMaximum);
//
//        int brightnessSettingMinimumId = getResources().getIdentifier("config_screenBrightnessSettingMinimum", "integer", "android");
//        int brightnessSettingMinimum = getResources().getInteger(brightnessSettingMinimumId);
//        Log.d("亮度", "initDisplayBrightnessBar: " + brightnessSettingMinimumId +","+ brightnessSettingMinimum);
        //255 /10


        int maxB = getMaxBrightness(requireActivity());
        int minB = getMinBrightness(requireActivity());

        getBinding().DisplayBrightnessBar.setRange(minB, maxB);


        float b = getScreenBrightness(requireActivity());
        if (getScreenBrightness(requireActivity()) < minB) b = minB;
        if (getScreenBrightness(requireActivity()) > maxB) b = maxB;

        Log.d("亮度", "當前亮度: " + b + "," + getScreenBrightness(requireActivity()) + ", 百分比" + getBrightnessPresent(getScreenBrightness(requireActivity())));

        getBinding().DisplayBrightnessBar.setProgress(b);

        final int[] v = {0};
        getBinding().DisplayBrightnessBar.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float brightnessPresent, float rightValue, boolean isFromUser) {

                int brightness255 = Math.round(brightnessPresent);
                float brightnessVale0_1 = brightness255 * (1f / 255f);

                //   Log.d("亮度", "onRangeChanged: " + brightness255 + "," + brightnessVale0_1);

                v[0] = brightness255;
                setBrightness(requireActivity(), brightnessVale0_1);
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {
                //  Log.d("亮度", "onStopTrackingTouch: ");

                saveBrightness(requireActivity(), v[0]);
            }
        });

    }

    private void save() {
        //SAVE
        new CommonUtils().deviceSettingViewModelToMMKV(deviceSettingViewModel);
    }

    private void initView() {
        getBinding().btnExit.setOnClickListener(v -> {
            if (CheckDoubleClick.isFastClick()) return;
            isShowMediaMenuOnStop = true;
            exitM();
        });


        //點10次
        TenTapClick.setTenClickListener(getBinding().btnToAndroid, true, () -> {
            // TODO: 開啟USB讀取
            getDeviceSpiritC().setUsbMode(DeviceSpiritC.USB_MODE.DATA);

            new FloatingWidget(requireActivity()).callSetting(2, MainActivity.class, 0);
            new CommonUtils().hideStatusBar(0);
        });


        TenTapClick.setTenClickListener(getBinding().tvTitleTTT, true, () -> {

            getDeviceSpiritC().setUsbMode(DeviceSpiritC.USB_MODE.DATA);

            mainActivity.showLoading(true);

            DelayedExecutor.timer(6000, () ->

                    UsbFileCopierKt.copyRetailVideosFromUsb(new UsbFileCopierKt.CopyCallback() {
                        @Override
                        public void onFileCopied(@NonNull String fileName) {
                            Toasty.success(getApp(), "Copied: " + fileName, Toast.LENGTH_SHORT).show();
                            Log.i("UsbFileCopier", " 👌🏽複製成功: " + fileName);
                        }

                        @Override
                        public void onCompleted(int successCount, int failCount, long durationMs) {
                            getDeviceSpiritC().setUsbMode(DeviceSpiritC.USB_MODE.CHARGER);
                            mainActivity.showLoading(false);
                            Toasty.success(getApp(), "Copy completed! Success: " + successCount + ", Failed: " + failCount + ", Duration: " + durationMs + "ms", Toasty.LENGTH_LONG).show();
                            Log.i("UsbFileCopier", "✅ 複製完成！成功: " + successCount + " 失敗: " + failCount + " 耗時: " + durationMs + "ms");
                        }

                        @Override
                        public void onError(@NonNull String errorMessage) {
                            getDeviceSpiritC().setUsbMode(DeviceSpiritC.USB_MODE.CHARGER);
                            mainActivity.showLoading(false);
                            Toasty.error(getApp(), "❌ Error occurred: " + errorMessage, Toasty.LENGTH_LONG).show();
                            Log.e("UsbFileCopier", "❌ 發生錯誤: " + errorMessage);
                        }
                    }));

        });


        TenTapClick.setTenClickListener(getBinding().viewTerritory, true, () -> {
            cNavigate(getView(), MaintenanceModeFragmentDirections.actionMaintenanceModeFragmentToMaintenanceDeviceTerritoryChooseFragment());
        });


        getBinding().btnUsageRestrictions.setOnClickListener(v ->
                cNavigate(v, MaintenanceModeFragmentDirections.actionMaintenanceModeFragmentToMaintenanceUsageRestrictionsFragment()));


        getBinding().btnResetOdometer.setOnClickListener(v ->
                cNavigate(v, MaintenanceModeFragmentDirections.actionMaintenanceModeFragmentToMaintenanceRestOdometerFragment()));


        getBinding().btnFirmware.setOnClickListener(v ->
                cNavigate(v, MaintenanceModeFragmentDirections.actionMaintenanceModeFragmentToMaintenanceFirmwareTestFragment()));


//        getBinding().tvDeviceView.setOnClickListener(v -> {
//            if (CheckDoubleClick.isFastClick()) return;
//            Navigation.findNavController(v).navigate(MaintenanceModeFragmentDirections.actionMaintenanceModeFragmentToDeviceIdFragment());
//        });

        getBinding().btnBrakeTest.setOnClickListener(v ->
                cNavigate(v, MaintenanceModeFragmentDirections.actionMaintenanceModeFragmentToMaintenanceBrakeTestFragment()));

        getBinding().btnClubInformation.setOnClickListener(v ->
                cNavigate(v, MaintenanceModeFragmentDirections.actionMaintenanceModeFragmentToMaintenanceOrganizationFragment()));

        getBinding().btnLanguage.setOnClickListener(v -> {
            save();
            cNavigate(v, MaintenanceModeFragmentDirections.actionMaintenanceModeFragmentToMaintenanceLanguageFragment());
        });

        getBinding().btnDateTime.setOnClickListener(v ->
                cNavigate(v, MaintenanceModeFragmentDirections.actionMaintenanceModeFragmentToMaintenanceDateTimeFragment()));

        getBinding().btnWifi.setOnClickListener(v -> {
            if (CheckDoubleClick.isFastClick()) return;
            SETTING_SHOW = true;
            // startActivity(new Intent("android.settings.panel.action.INTERNET_CONNECTIVITY"));
//            startActivity(new Intent(ACTION_WIFI));
            //   startActivity(new Intent(ACTION_INTERNET_CONNECTIVITY));

            new FloatingWidget(requireActivity()).callSetting(0, MainActivity.class, 1);
//            if (CheckDoubleClick.isFastClick()) return;
            //     Navigation.findNavController(v).navigate(MaintenanceModeFragmentDirections.actionMaintenanceModeFragmentToMaintenanceWifiTestFragment());
        });

        getBinding().btnSoftware.setOnClickListener(v ->
                cNavigate(v, MaintenanceModeFragmentDirections.actionMaintenanceModeFragmentToMaintenanceSoftwareTestFragment()));

        getBinding().btnAppManager.setOnClickListener(v ->
                cNavigate(v, MaintenanceModeFragmentDirections.actionMaintenanceModeFragmentToMaintenanceAppManagerFragment()));

//        getBinding().btnAdvertisement.setOnClickListener(v ->
//                cNavigate(v,MaintenanceModeFragmentDirections.actionMaintenanceModeFragmentToMaintenanceAdvertisementFragment()));

//        getBinding().btnUseLimits.setOnClickListener(v ->
//                cNavigate(v,MaintenanceModeFragmentDirections.actionMaintenanceModeFragmentToMaintenanceUseLimitsFragment()));

        getBinding().btnKeyTest.setOnClickListener(v ->
                cNavigate(v, MaintenanceModeFragmentDirections.actionMaintenanceModeFragmentToMaintenanceKeyTestFragment()));

        getBinding().btnNfcTest.setOnClickListener(v -> {
            if (!appStatusViewModel.isGem3On.get()) {
                CustomToast.showToast(mainActivity, "GEM3 IS NOT ENABLED");
                return;
            }
            cNavigate(v, MaintenanceModeFragmentDirections.actionMaintenanceModeFragmentToMaintenanceNfcTestFragment());
        });


        getBinding().tvTvSettings.setOnClickListener(v -> {

            if (isUs) {
                if (CheckDoubleClick.isFastClick()) return;

                MaintenanceTvSearchUsWindow maintenanceTvSearchUsWindow = new MaintenanceTvSearchUsWindow(requireActivity());
                maintenanceTvSearchUsWindow.showAtLocation(requireActivity().getWindow().getDecorView(), Gravity.START | Gravity.TOP, 50, 80);
                maintenanceTvSearchUsWindow.setOnCustomDismissListener(new BasePopupWindow.OnCustomDismissListener() {
                    @Override
                    public void onStartDismiss(MsgEvent value) {

                    }

                    @Override
                    public void onDismiss() {
                    }
                });


            } else {
                if (!MainActivity.isTvOn) {
                    if (CheckDoubleClick.isFastClick()) return;
                    Toasty.warning(requireActivity(), R.string.tvtuner_try_again, Toasty.LENGTH_SHORT).show();
                    mainActivity.initTvTuner();
                    return;
                }

                cNavigate(v, MaintenanceModeFragmentDirections.actionMaintenanceModeFragmentToMaintenanceTvSettingsFragment());
            }
        });


        getBinding().btnCommunicationTest.setOnClickListener(v ->
                cNavigate(v, MaintenanceModeFragmentDirections.actionMaintenanceModeFragmentToMaintenanceCommunicationTestFragment()));

        getBinding().btnBrakeTest.setOnClickListener(v ->
                cNavigate(v, MaintenanceModeFragmentDirections.actionMaintenanceModeFragmentToMaintenanceBrakeTestFragment()));

        getBinding().btnSensorTests.setOnClickListener(v ->
                cNavigate(v, MaintenanceModeFragmentDirections.actionMaintenanceModeFragmentToMaintenanceSensorTestFragment()));

//        getBinding().btnWattCalibration.setOnClickListener(v -> {
//            if (CheckDoubleClick.isFastClick()) return;
//            Navigation.findNavController(v).navigate(MaintenanceModeFragmentDirections.actionMaintenanceModeFragmentToMaintenanceWattCallibrationFragment());
//        });

        getBinding().btnDriveMotorTest.setOnClickListener(v ->
                cNavigate(v, MaintenanceModeFragmentDirections.actionMaintenanceModeFragmentToMaintenanceDriveMotorTestFragment()));

        getBinding().btnInclineMotor.setOnClickListener(v ->
                cNavigate(v, MaintenanceModeFragmentDirections.actionMaintenanceModeFragmentToMaintenanceInclineMotorTestFragment()));

        getBinding().btnErrorCodeLog.setOnClickListener(v ->
                cNavigate(v, MaintenanceModeFragmentDirections.actionMaintenanceModeFragmentToMaintenanceErrorCodeLogFragment()));

        getBinding().btnMachineType.setOnClickListener(v ->
                cNavigate(v, MaintenanceModeFragmentDirections.actionMaintenanceModeFragmentToMaintenanceDeviceTypeChooseFragment()));

        LiveEventBus.get(EventKey.MAINTENANCE_ODO_RESET).observe(getViewLifecycleOwner(), s -> {
            DeviceSettingBean deviceSettingBean = getApp().getDeviceSettingBean();
            deviceSettingBean.setODO_distance(0);
            deviceSettingBean.setODO_time(0);
            getApp().setDeviceSettingBean(deviceSettingBean);
            initOdoTime();
            initOdoDistance();
        });


        LiveEventBus.get(EventKey.MAINTENANCE_UPDATE_LANGUAGE_1, Locale.class).observe(getViewLifecycleOwner(), locale -> {
            if (getBinding() != null) {
                try {
                    new CommonUtils().hideStatusBar(1);
                    if (mainActivity != null && mainActivity.hdmiIn != null) {
                        MainActivity.isReAssignView = true;
                        mainActivity.hdmiIn.AssignView(mainActivity.getBinding().videoFullViewContainer);
                    }
                    getDeviceSpiritC().setUsbMode(DeviceSpiritC.USB_MODE.CHARGER);
                    uartConsole.setDevMainMode(DeviceSpiritC.MAIN_MODE.IDLE);
                    parent.navController.navigate(MaintenanceModeFragmentDirections.actionGlobalNavigationLogin());

                    new RxTimer().timer(500, number ->
                            LiveEventBus.get(EventKey.MAINTENANCE_UPDATE_LANGUAGE_2, Locale.class).post(locale));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        getBinding().btnSplashScreenImage.setOnClickListener(v ->
                cNavigate(v, MaintenanceModeFragmentDirections.actionMaintenanceModeFragmentToMaintenanceSplashScreenImageFragment()));

        getBinding().btnRetailModeVideo.setOnClickListener(v ->
                cNavigate(v, MaintenanceModeFragmentDirections.actionMaintenanceModeFragmentToMaintenanceRetailModeVideoFragment3()));

    }

    private void cNavigate(View v, NavDirections navDirections) {
        if (CheckDoubleClick.isFastClick()) return;
        try {
            Navigation.findNavController(v).navigate(navDirections);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isFirst = false;
//        requireActivity().getWindow().getDecorView().setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_VISIBLE);

        //1關閉狀態列
        new CommonUtils().hideStatusBar(1);

        getDeviceSpiritC().setUsbMode(DeviceSpiritC.USB_MODE.CHARGER);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

//        requireActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
//                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
//                View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        deviceSettingViewModel.timeText.set(updateTime());


        //  isShowMediaMenuOnStop = true;
        //1關閉狀態列
        new CommonUtils().hideStatusBar(1);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SpiritDbManager.getInstance(getApp()).cancel();
    }

    /**
     * 系統權限要手動開
     */
    private void checkSystemWritePermission() {
        boolean retVal;
        retVal = Settings.System.canWrite(requireActivity());
        if (!retVal) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + requireActivity().getPackageName()));
            startActivity(intent);
        }
    }

    //設定系統休眠時間
    private void setSleep() {
        int time = SCREEN_TIMEOUT_NEVER;
//        if (deviceSettingViewModel.sleepMode.getValue() == ON && getApp().getDeviceSettingBean().getSleepAfter() > 0) {
        if ((deviceSettingViewModel.sleepMode.getValue() == ON || deviceSettingViewModel.sleepMode.getValue() == SLEEP_MODE_RETAIL) && getApp().getDeviceSettingBean().getSleepAfter() > 0) {
            time = (int) (getApp().getDeviceSettingBean().getSleepAfter() * 1000);
        }
        //  Log.d("FEFEFE", "setSleep: " + deviceSettingViewModel.sleepMode.getValue());

        setSleepMode(time);
    }


    public void setAutoDateTime(int checked) {
        try {
            android.provider.Settings.Global.putInt(requireActivity().getContentResolver(),
                    android.provider.Settings.Global.AUTO_TIME, checked);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setAutoDateTimeZone(int checked) {
        try {
            android.provider.Settings.Global.putInt(requireActivity().getContentResolver(),
                    android.provider.Settings.Global.AUTO_TIME_ZONE, checked);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void exitM() {

        if (mainActivity != null && mainActivity.hdmiIn != null) {
            MainActivity.isReAssignView = true;
            mainActivity.hdmiIn.AssignView(mainActivity.getBinding().videoFullViewContainer);
            //    mainActivity.hdmiIn.SetDefaultHdmiPort(PORT_EZ_CAST);
            //進tv設定時會換成另一個view ，退出時換回原本的view
            //AssignView 若有替換成新View
            // 會觸發
            // onReady > onActive  > onPortSwitched

        }

        save();
        //SAVE
        //  new CommonUtils().deviceSettingViewModelToMMKV(deviceSettingViewModel);

        getDeviceSpiritC().setUsbMode(DeviceSpiritC.USB_MODE.CHARGER);

//            uartConsole.setDevMainMode(DeviceSpiritC.MAIN_MODE.IDLE);

        //  if (getApp().getDeviceSettingBean().isAutoUpdate()) {
        //     Log.d(DownloadManagerCustom.TAG, "AUTO UPDATE 是 ON 檢查有無新版本");
//                mainActivity.checkUpdate();
        //   }

        try {
            uartConsole.setDevMainMode(DeviceSpiritC.MAIN_MODE.IDLE);

            mainActivity.checkUpdate();


            MediaAppUtils.checkForceUpdate();


            parent.navController.navigate(MaintenanceModeFragmentDirections.actionGlobalNavigationLogin());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}