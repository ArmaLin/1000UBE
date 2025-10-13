package com.dyaco.spirit_commercial.alert_message;

import static com.dyaco.spirit_commercial.App.UNIT_E;
import static com.dyaco.spirit_commercial.MainActivity.isEmulator;
import static com.dyaco.spirit_commercial.MainActivity.isTreadmill;
import static com.dyaco.spirit_commercial.support.CommonUtils.showException;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.getConNum;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.getInclineLevel;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.getSpeedLevel;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.IMPERIAL;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.FTMS_SET_TARGET_INCLINATION;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.FTMS_SET_TARGET_SPEED;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.MEDIA_MENU_CHANGE;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.NEW_MEDIA_CONTROLLER;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.NEW_UPDATE_VALUE;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.NEW_VALUE_UPDATE;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.CLICK_MINUS_2;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.CLICK_PLUS_2;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_B5_RUNNING_AD_CHANGE_RSP;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_PAUSE_STANDBY;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_RUNNING_STANDBY;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.STATS_INCLINE;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.STATS_LEVEL;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.STATS_POWER;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.STATS_SPEED;
import static com.dyaco.spirit_commercial.support.intdef.WorkoutIntDef.TREADMILL_SPEED_IMPERIAL_US_NUM_1;
import static com.dyaco.spirit_commercial.support.intdef.WorkoutIntDef.TREADMILL_SPEED_IMPERIAL_US_NUM_10;
import static com.dyaco.spirit_commercial.support.intdef.WorkoutIntDef.TREADMILL_SPEED_IMPERIAL_US_NUM_11;
import static com.dyaco.spirit_commercial.support.intdef.WorkoutIntDef.TREADMILL_SPEED_IMPERIAL_US_NUM_12;
import static com.dyaco.spirit_commercial.support.intdef.WorkoutIntDef.TREADMILL_SPEED_IMPERIAL_US_NUM_2;
import static com.dyaco.spirit_commercial.support.intdef.WorkoutIntDef.TREADMILL_SPEED_IMPERIAL_US_NUM_3;
import static com.dyaco.spirit_commercial.support.intdef.WorkoutIntDef.TREADMILL_SPEED_IMPERIAL_US_NUM_4;
import static com.dyaco.spirit_commercial.support.intdef.WorkoutIntDef.TREADMILL_SPEED_IMPERIAL_US_NUM_5;
import static com.dyaco.spirit_commercial.support.intdef.WorkoutIntDef.TREADMILL_SPEED_IMPERIAL_US_NUM_6;
import static com.dyaco.spirit_commercial.support.intdef.WorkoutIntDef.TREADMILL_SPEED_IMPERIAL_US_NUM_7;
import static com.dyaco.spirit_commercial.support.intdef.WorkoutIntDef.TREADMILL_SPEED_IMPERIAL_US_NUM_8;
import static com.dyaco.spirit_commercial.support.intdef.WorkoutIntDef.TREADMILL_SPEED_IMPERIAL_US_NUM_9;
import static com.dyaco.spirit_commercial.support.intdef.WorkoutIntDef.TREADMILL_SPEED_METRIC_US_NUM_1;
import static com.dyaco.spirit_commercial.support.intdef.WorkoutIntDef.TREADMILL_SPEED_METRIC_US_NUM_10;
import static com.dyaco.spirit_commercial.support.intdef.WorkoutIntDef.TREADMILL_SPEED_METRIC_US_NUM_11;
import static com.dyaco.spirit_commercial.support.intdef.WorkoutIntDef.TREADMILL_SPEED_METRIC_US_NUM_12;
import static com.dyaco.spirit_commercial.support.intdef.WorkoutIntDef.TREADMILL_SPEED_METRIC_US_NUM_2;
import static com.dyaco.spirit_commercial.support.intdef.WorkoutIntDef.TREADMILL_SPEED_METRIC_US_NUM_3;
import static com.dyaco.spirit_commercial.support.intdef.WorkoutIntDef.TREADMILL_SPEED_METRIC_US_NUM_4;
import static com.dyaco.spirit_commercial.support.intdef.WorkoutIntDef.TREADMILL_SPEED_METRIC_US_NUM_5;
import static com.dyaco.spirit_commercial.support.intdef.WorkoutIntDef.TREADMILL_SPEED_METRIC_US_NUM_6;
import static com.dyaco.spirit_commercial.support.intdef.WorkoutIntDef.TREADMILL_SPEED_METRIC_US_NUM_7;
import static com.dyaco.spirit_commercial.support.intdef.WorkoutIntDef.TREADMILL_SPEED_METRIC_US_NUM_8;
import static com.dyaco.spirit_commercial.support.intdef.WorkoutIntDef.TREADMILL_SPEED_METRIC_US_NUM_9;
import static com.dyaco.spirit_commercial.workout.programs.ProgramsEnum.FITNESS_TEST;
import static com.dyaco.spirit_commercial.workout.programs.ProgramsEnum.HEART_RATE;
import static com.dyaco.spirit_commercial.workout.programs.ProgramsEnum.WATTS;

import android.content.Context;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.dashboard_media.NewUpdateData;
import com.dyaco.spirit_commercial.databinding.WindowWorkoutMediaControllerBinding;
import com.dyaco.spirit_commercial.support.MsgEvent;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.interaction.LongClickUtil;
import com.dyaco.spirit_commercial.support.custom_view.CustomToast;
import com.dyaco.spirit_commercial.support.intdef.AppStatusIntDef;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;
import com.dyaco.spirit_commercial.viewmodel.AppStatusViewModel;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingViewModel;
import com.dyaco.spirit_commercial.viewmodel.WorkoutViewModel;
import com.google.android.material.button.MaterialButton;
import com.jeremyliao.liveeventbus.LiveEventBus;

public class WorkoutMediaControllerWindow extends BasePopupWindow<WindowWorkoutMediaControllerBinding> {
    public static boolean isMediaWorkoutController = false;
    private AppStatusViewModel appStatusViewModel;
    public DeviceSettingViewModel deviceSettingViewModel;
    private WorkoutViewModel workoutViewModel;
    private MainActivity activity;

    private boolean isPause = false;
    private int valueType = STATS_SPEED;

    public WorkoutMediaControllerWindow(Context context) {
        super(context, 300, 472, 0, GENERAL.TRANSLATION_Y, false, true, true, true);

        activity = (MainActivity) mContext;

        workoutViewModel = new ViewModelProvider(activity).get(WorkoutViewModel.class);
        appStatusViewModel = new ViewModelProvider(activity).get(AppStatusViewModel.class);
        deviceSettingViewModel = new ViewModelProvider(activity).get(DeviceSettingViewModel.class);
        getBinding().setDeviceSetting(deviceSettingViewModel);
        getBinding().setWorkoutData(workoutViewModel);
        getBinding().setAppStatusViewModel(appStatusViewModel);

        getBinding().setIsTreadmill(isTreadmill);
        isMediaWorkoutController = true;
        LiveEventBus.get(MEDIA_MENU_CHANGE).observeForever(observer);
//        LiveEventBus.get(FTMS_SET_TARGET_INCLINATION).observeForever(observer2);
//        LiveEventBus.get(FTMS_SET_TARGET_SPEED).observeForever(observer3);
        LiveEventBus.get(NEW_UPDATE_VALUE).observeForever(observer4);


        initData();

        initNum();

        getMenu();

        getBinding().btnClose.setOnClickListener(view -> {
            returnValue(new MsgEvent(true));
            dismiss();
        });


        getBinding().rgSelectProgram.setOnCheckedChangeListener((group, checkedId) -> {

            if (checkedId == R.id.rbDefaultPrograms) {
                getBinding().tvTopTextUs.setText(R.string.direct_speed);
                valueType = STATS_SPEED;
                getBinding().tvTimeNum.setText(String.valueOf(workoutViewModel.currentSpeed.get()));

                getBinding().c12.setVisibility(View.VISIBLE);
                getBinding().c16.setVisibility(View.GONE);
            } else {
                getBinding().tvTopTextUs.setText(R.string.direct_incline);
                valueType = STATS_INCLINE;
                getBinding().tvTimeNum.setText(String.valueOf(workoutViewModel.currentInclineValue.get()));
                getBinding().c12.setVisibility(View.GONE);
                getBinding().c16.setVisibility(View.VISIBLE);
            }

            initNum();
        });


        getBinding().btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //       isPause = !isPause;


                if (appStatusViewModel.currentStatus.get() == AppStatusIntDef.STATUS_RUNNING) {

                    //等狀態
                    if (isTreadmill && (activity.uartConsole.getDevStep() != DS_RUNNING_STANDBY && activity.uartConsole.getDevStep() != DS_B5_RUNNING_AD_CHANGE_RSP) && !isEmulator) {
                        return;
                    }
//
//                    //在Running時 暫停Workout

                    getBinding().btnPause.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mContext, R.drawable.icon_resume_20), null, null, null);
                    getBinding().btnPause.setText(mContext.getString(R.string.resume));
                    getBinding().btnPause.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color0dac87));
                } else if (appStatusViewModel.currentStatus.get() == AppStatusIntDef.STATUS_PAUSE) {
//
//                    //Resume Workout
//
                    if (isTreadmill) {
                        //等狀態
                        if (activity.uartConsole.getDevStep() != DS_PAUSE_STANDBY && !isEmulator) {
                            CustomToast.showToast(activity, mContext.getString(R.string.resume_waiting));
                            return;
                        }
                    }
                    getBinding().btnPause.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mContext, R.drawable.icon_pause_20), null, null, null);
                    getBinding().btnPause.setText(mContext.getString(R.string.pause));
                    getBinding().btnPause.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colore24b44));
                }

                //通知MENU
                LiveEventBus.get(NEW_MEDIA_CONTROLLER).post(isPause);
            }
        });


        // getBinding().tvTimeNum.setText();

        //   new CommonUtils().addAutoClick(getBinding().btnTimePlus);
        //   new CommonUtils().addAutoClick(getBinding().btnTimeMinus);


        LongClickUtil.attachF(getBinding().btnTimePlus, () -> {
         //   Log.d("LKKKKKAKAKAKAK", "++++++WorkoutMediaControllerWindow: "+ valueType);
            switch (valueType) {
                case STATS_INCLINE:
                    LiveEventBus.get(FTMS_SET_TARGET_INCLINATION).post(CLICK_PLUS_2);
                    break;
                case STATS_SPEED:
                    LiveEventBus.get(FTMS_SET_TARGET_SPEED).post(CLICK_PLUS_2);
                    break;
                case STATS_LEVEL:
                case STATS_POWER:
                    LiveEventBus.get(FTMS_SET_TARGET_SPEED).post(CLICK_PLUS_2);
                    break;
            }
        });


        LongClickUtil.attachF(getBinding().btnTimeMinus, () -> {
            switch (valueType) {
                case STATS_INCLINE:
                    LiveEventBus.get(FTMS_SET_TARGET_INCLINATION).post(CLICK_MINUS_2);
                    break;
                case STATS_SPEED:
                    LiveEventBus.get(FTMS_SET_TARGET_SPEED).post(CLICK_MINUS_2);
                    break;
                case STATS_LEVEL:
                case STATS_POWER:
                    LiveEventBus.get(FTMS_SET_TARGET_SPEED).post(CLICK_MINUS_2);
                    break;
            }
        });

//        getBinding().btnTimePlus.setOnClickListener(view -> {
//            switch (valueType) {
//                case STATS_INCLINE:
//                    LiveEventBus.get(FTMS_SET_TARGET_INCLINATION).post(CLICK_PLUS_2);
//                    break;
//                case STATS_SPEED:
//                    LiveEventBus.get(FTMS_SET_TARGET_SPEED).post(CLICK_PLUS_2);
//                    break;
//                case STATS_LEVEL:
//                case STATS_POWER:
//                    LiveEventBus.get(FTMS_SET_TARGET_SPEED).post(CLICK_PLUS_2);
//                    break;
//            }
//        });

//        getBinding().btnTimeMinus.setOnClickListener(view -> {
//            switch (valueType) {
//                case STATS_INCLINE:
//                    LiveEventBus.get(FTMS_SET_TARGET_INCLINATION).post(CLICK_MINUS_2);
//                    break;
//                case STATS_SPEED:
//                    LiveEventBus.get(FTMS_SET_TARGET_SPEED).post(CLICK_MINUS_2);
//                    break;
//                case STATS_LEVEL:
//                case STATS_POWER:
//                    LiveEventBus.get(FTMS_SET_TARGET_SPEED).post(CLICK_MINUS_2);
//                    break;
//            }
//
//            //     updateValue();
//        });


        getBinding().tvTopNumberUs1.setOnClickListener(v -> updateSpecify((MaterialButton) v));
        getBinding().tvTopNumberUs2.setOnClickListener(v -> updateSpecify((MaterialButton) v));
        getBinding().tvTopNumberUs3.setOnClickListener(v -> updateSpecify((MaterialButton) v));
        getBinding().tvTopNumberUs4.setOnClickListener(v -> updateSpecify((MaterialButton) v));
        getBinding().tvTopNumberUs5.setOnClickListener(v -> updateSpecify((MaterialButton) v));
        getBinding().tvTopNumberUs6.setOnClickListener(v -> updateSpecify((MaterialButton) v));
        getBinding().tvTopNumberUs7.setOnClickListener(v -> updateSpecify((MaterialButton) v));
        getBinding().tvTopNumberUs8.setOnClickListener(v -> updateSpecify((MaterialButton) v));
        getBinding().tvTopNumberUs9.setOnClickListener(v -> updateSpecify((MaterialButton) v));
        getBinding().tvTopNumberUs10.setOnClickListener(v -> updateSpecify((MaterialButton) v));
        getBinding().tvTopNumberUs11.setOnClickListener(v -> updateSpecify((MaterialButton) v));
        getBinding().tvTopNumberUs12.setOnClickListener(v -> updateSpecify((MaterialButton) v));


        getBinding().tv161.setOnClickListener(v -> updateSpecify((MaterialButton) v));
        getBinding().tv162.setOnClickListener(v -> updateSpecify((MaterialButton) v));
        getBinding().tv163.setOnClickListener(v -> updateSpecify((MaterialButton) v));
        getBinding().tv164.setOnClickListener(v -> updateSpecify((MaterialButton) v));
        getBinding().tv165.setOnClickListener(v -> updateSpecify((MaterialButton) v));
        getBinding().tv166.setOnClickListener(v -> updateSpecify((MaterialButton) v));
        getBinding().tv167.setOnClickListener(v -> updateSpecify((MaterialButton) v));
        getBinding().tv168.setOnClickListener(v -> updateSpecify((MaterialButton) v));
        getBinding().tv169.setOnClickListener(v -> updateSpecify((MaterialButton) v));
        getBinding().tv1610.setOnClickListener(v -> updateSpecify((MaterialButton) v));
        getBinding().tv1611.setOnClickListener(v -> updateSpecify((MaterialButton) v));
        getBinding().tv1612.setOnClickListener(v -> updateSpecify((MaterialButton) v));
        getBinding().tv1613.setOnClickListener(v -> updateSpecify((MaterialButton) v));
        getBinding().tv1614.setOnClickListener(v -> updateSpecify((MaterialButton) v));
        getBinding().tv1615.setOnClickListener(v -> updateSpecify((MaterialButton) v));
        getBinding().tv1616.setOnClickListener(v -> updateSpecify((MaterialButton) v));
    }

    private void updateSpecify(MaterialButton btn) {
        int value = 0;
        int type = -1;
        switch (valueType) {
            case STATS_INCLINE:
                type = STATS_INCLINE;
                value = getInclineLevel(getConNum(btn));
                break;
            case STATS_SPEED:
                type = STATS_SPEED;
                value = getSpeedLevel(getConNum(btn));
                break;
            case STATS_LEVEL:
                type = STATS_LEVEL;
                value = getConNum(btn);
                break;
            case STATS_POWER:
                type = STATS_POWER;
                value = getConNum(btn);
                break;

        }
        LiveEventBus.get(NEW_VALUE_UPDATE).post(new NewUpdateData(type, value));
        //    updateValue();
    }

    private void updateValue() {
        //  new RxTimer().timer(200, number -> {
        String valuee = "0";
        switch (valueType) {
            case STATS_INCLINE:
                valuee = String.valueOf(workoutViewModel.currentInclineValue.get());
                break;
            case STATS_SPEED:
                valuee = String.valueOf(workoutViewModel.currentSpeed.get());
                break;
            case STATS_LEVEL:
                valuee = String.valueOf(workoutViewModel.currentLevel.get());
                break;
            case STATS_POWER:
                valuee = String.valueOf(workoutViewModel.constantPowerW.get());
                break;
        }
        try {
            getBinding().tvTimeNum.setText(valuee);
        } catch (Exception e) {
            showException(e);
        }
        //    });
    }

    private void initData() {
        if (isTreadmill) {
            valueType = STATS_SPEED;
            getBinding().tvTimeNum.setText(String.valueOf(workoutViewModel.currentSpeed.get()));

            getBinding().tvTopTextUs.setText(R.string.direct_speed);
            getBinding().c12.setVisibility(View.VISIBLE);
            getBinding().c16.setVisibility(View.GONE);

            if (workoutViewModel.selProgram == HEART_RATE) {
                getBinding().rgSelectProgram.setVisibility(View.INVISIBLE);
            }

        } else {

            getBinding().c12.setVisibility(View.GONE);

            getBinding().c16.setVisibility(View.VISIBLE);

            if (workoutViewModel.selProgram == FITNESS_TEST || workoutViewModel.selProgram == HEART_RATE) {
                getBinding().ggg.setVisibility(View.INVISIBLE);
                getBinding().c16.setVisibility(View.INVISIBLE);
            }

            getBinding().rgSelectProgram.setVisibility(View.INVISIBLE);

            if (workoutViewModel.selProgram == WATTS) {
                valueType = STATS_POWER;
                getBinding().tvTimeNum.setText(String.valueOf(workoutViewModel.constantPowerW.get()));
            } else {
                valueType = STATS_LEVEL;
                getBinding().tvTimeNum.setText(String.valueOf(workoutViewModel.currentLevel.get()));
            }

            getBinding().tvTopTextUs.setText((workoutViewModel.selProgram == WATTS) ? R.string.direct_watts : R.string.direct_level);
        }

    }

    private void initNum() {
        switch (valueType) {
            case STATS_SPEED:
                getBinding().tvTopNumberUs1.setText(UNIT_E == IMPERIAL ? TREADMILL_SPEED_IMPERIAL_US_NUM_1 : TREADMILL_SPEED_METRIC_US_NUM_1);
                getBinding().tvTopNumberUs2.setText(UNIT_E == IMPERIAL ? TREADMILL_SPEED_IMPERIAL_US_NUM_2 : TREADMILL_SPEED_METRIC_US_NUM_2);
                getBinding().tvTopNumberUs3.setText(UNIT_E == IMPERIAL ? TREADMILL_SPEED_IMPERIAL_US_NUM_3 : TREADMILL_SPEED_METRIC_US_NUM_3);
                getBinding().tvTopNumberUs4.setText(UNIT_E == IMPERIAL ? TREADMILL_SPEED_IMPERIAL_US_NUM_4 : TREADMILL_SPEED_METRIC_US_NUM_4);
                getBinding().tvTopNumberUs5.setText(UNIT_E == IMPERIAL ? TREADMILL_SPEED_IMPERIAL_US_NUM_5 : TREADMILL_SPEED_METRIC_US_NUM_5);
                getBinding().tvTopNumberUs6.setText(UNIT_E == IMPERIAL ? TREADMILL_SPEED_IMPERIAL_US_NUM_6 : TREADMILL_SPEED_METRIC_US_NUM_6);
                getBinding().tvTopNumberUs7.setText(UNIT_E == IMPERIAL ? TREADMILL_SPEED_IMPERIAL_US_NUM_7 : TREADMILL_SPEED_METRIC_US_NUM_7);
                getBinding().tvTopNumberUs8.setText(UNIT_E == IMPERIAL ? TREADMILL_SPEED_IMPERIAL_US_NUM_8 : TREADMILL_SPEED_METRIC_US_NUM_8);
                getBinding().tvTopNumberUs9.setText(UNIT_E == IMPERIAL ? TREADMILL_SPEED_IMPERIAL_US_NUM_9 : TREADMILL_SPEED_METRIC_US_NUM_9);
                getBinding().tvTopNumberUs10.setText(UNIT_E == IMPERIAL ? TREADMILL_SPEED_IMPERIAL_US_NUM_10 : TREADMILL_SPEED_METRIC_US_NUM_10);
                getBinding().tvTopNumberUs11.setText(UNIT_E == IMPERIAL ? TREADMILL_SPEED_IMPERIAL_US_NUM_11 : TREADMILL_SPEED_METRIC_US_NUM_11);
                getBinding().tvTopNumberUs12.setText(UNIT_E == IMPERIAL ? TREADMILL_SPEED_IMPERIAL_US_NUM_12 : TREADMILL_SPEED_METRIC_US_NUM_12);
                break;
            case STATS_INCLINE:
//                getBinding().tvTopNumberUs1.setText(TREADMILL_INCLINE_NUM_1_N);
//                getBinding().tvTopNumberUs2.setText(TREADMILL_INCLINE_NUM_2_N);
//                getBinding().tvTopNumberUs3.setText(TREADMILL_INCLINE_NUM_3_N);
//                getBinding().tvTopNumberUs4.setText(TREADMILL_INCLINE_NUM_4_N);
//                getBinding().tvTopNumberUs5.setText(TREADMILL_INCLINE_NUM_5_N);
//                getBinding().tvTopNumberUs6.setText(TREADMILL_INCLINE_NUM_6_N);
//                getBinding().tvTopNumberUs7.setText(TREADMILL_INCLINE_NUM_7_N);
//                getBinding().tvTopNumberUs8.setText(TREADMILL_INCLINE_NUM_8_N);
//                getBinding().tvTopNumberUs9.setText(TREADMILL_INCLINE_NUM_9_N);
//                getBinding().tvTopNumberUs10.setText(TREADMILL_INCLINE_NUM_10_N);
//                getBinding().tvTopNumberUs11.setText(TREADMILL_INCLINE_NUM_11_N);
//                getBinding().tvTopNumberUs12.setText(TREADMILL_INCLINE_NUM_12_N);

                getBinding().tv161.setText("0");
                getBinding().tv162.setText("1");
                getBinding().tv163.setText("2");
                getBinding().tv164.setText("3");
                getBinding().tv165.setText("4");
                getBinding().tv166.setText("5");
                getBinding().tv167.setText("6");
                getBinding().tv168.setText("7");
                getBinding().tv169.setText("8");
                getBinding().tv1610.setText("9");
                getBinding().tv1611.setText("10");
                getBinding().tv1612.setText("11");
                getBinding().tv1613.setText("12");
                getBinding().tv1614.setText("13");
                getBinding().tv1615.setText("14");
                getBinding().tv1616.setText("15");
                break;
            case STATS_LEVEL:
                getBinding().tv161.setText("0");
                getBinding().tv162.setText("2");
                getBinding().tv163.setText("4");
                getBinding().tv164.setText("6");
                getBinding().tv165.setText("8");
                getBinding().tv166.setText("10");
                getBinding().tv167.setText("12");
                getBinding().tv168.setText("14");
                getBinding().tv169.setText("16");
                getBinding().tv1610.setText("20");
                getBinding().tv1611.setText("25");
                getBinding().tv1612.setText("30");
                getBinding().tv1613.setText("35");
                getBinding().tv1614.setText("40");
                getBinding().tv1615.setText("45");
                getBinding().tv1616.setText("50");
                break;
            case STATS_POWER:
                getBinding().tv161.setText("30");
                getBinding().tv162.setText("40");
                getBinding().tv163.setText("50");
                getBinding().tv164.setText("70");
                getBinding().tv165.setText("90");
                getBinding().tv166.setText("100");
                getBinding().tv167.setText("110");
                getBinding().tv168.setText("120");
                getBinding().tv169.setText("130");
                getBinding().tv1610.setText("140");
                getBinding().tv1611.setText("150");
                getBinding().tv1612.setText("160");
                getBinding().tv1613.setText("170");
                getBinding().tv1614.setText("180");
                getBinding().tv1615.setText("190");
                getBinding().tv1616.setText("200");
                break;
        }
    }


    Observer<Object> observer = s -> getMenu();
    //    Observer<Object> observer2 = s -> updateValue();
//    Observer<Object> observer3 = s -> updateValue();
    Observer<Object> observer4 = s -> updateValue();

    private void getMenu() {
        if (appStatusViewModel.currentStatus.get() == AppStatusIntDef.STATUS_RUNNING) {
            getBinding().btnPause.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mContext, R.drawable.icon_pause_20), null, null, null);
            getBinding().btnPause.setText(mContext.getString(R.string.pause));
            getBinding().btnPause.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colore24b44));
        } else if (appStatusViewModel.currentStatus.get() == AppStatusIntDef.STATUS_PAUSE) {

            getBinding().btnPause.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mContext, R.drawable.icon_resume_20), null, null, null);
            getBinding().btnPause.setText(mContext.getString(R.string.resume));
            getBinding().btnPause.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color0dac87));
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        isMediaWorkoutController = false;

        LiveEventBus.get(MEDIA_MENU_CHANGE).removeObserver(observer);
//        LiveEventBus.get(FTMS_SET_TARGET_INCLINATION).removeObserver(observer2);
//        LiveEventBus.get(FTMS_SET_TARGET_SPEED).removeObserver(observer3);
        LiveEventBus.get(NEW_UPDATE_VALUE).removeObserver(observer4);


//        ClickUtils.detachF(getBinding().btnTimePlus);
//        ClickUtils.detachF(getBinding().btnTimeMinus);
    }
}
