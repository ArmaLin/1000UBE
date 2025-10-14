package com.dyaco.spirit_commercial.workout;

import static com.dyaco.spirit_commercial.App.MODE;
import static com.dyaco.spirit_commercial.App.getApp;
import static com.dyaco.spirit_commercial.MainActivity.isTreadmill;
import static com.dyaco.spirit_commercial.MainActivity.isUs;
import static com.dyaco.spirit_commercial.egym.EgymUtil.SYMBOL_DURATION;
import static com.dyaco.spirit_commercial.product_flavor.ModeEnum.CE1000ENT;
import static com.dyaco.spirit_commercial.product_flavor.ModeEnum.CT1000ENT;
import static com.dyaco.spirit_commercial.support.CommonUtils.formatS;
import static com.dyaco.spirit_commercial.support.CommonUtils.formatSecToM;
import static com.dyaco.spirit_commercial.support.CommonUtils.showException;
import static com.dyaco.spirit_commercial.support.FormulaUtil.E_BLANK;
import static com.dyaco.spirit_commercial.support.FormulaUtil.getEgymUnitD;
import static com.dyaco.spirit_commercial.support.FormulaUtil.getEgymUnitS;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.EGYM_STATS_INIT;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.SELECT_WORKOUT_PAGE;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.STATS_CADENCE;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.STATS_CALORIES;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.STATS_DISTANCE_LEFT;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.STATS_ELAPSED_TIME;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.STATS_ELEVATION_GAIN;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.STATS_HEART_RATE;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.STATS_INCLINE;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.STATS_INTERVAL;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.STATS_INTERVAL_DISTANCE;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.STATS_LEVEL;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.STATS_POWER;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.STATS_REMAINING_TIME;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.STATS_SPEED;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.WORKOUT_PAGE_CHARTS;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.WORKOUT_PAGE_GARMIN;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.WORKOUT_PAGE_TRACK;
import static com.dyaco.spirit_commercial.workout.MainWorkoutTrainingFragment.isGGG;
import static com.dyaco.spirit_commercial.workout.WorkoutSettingWindow.isSettingShow;
import static com.dyaco.spirit_commercial.workout.programs.ProgramsEnum.AIR_FORCE;
import static com.dyaco.spirit_commercial.workout.programs.ProgramsEnum.ARMY;
import static com.dyaco.spirit_commercial.workout.programs.ProgramsEnum.COAST_GUARD;
import static com.dyaco.spirit_commercial.workout.programs.ProgramsEnum.EGYM;
import static com.dyaco.spirit_commercial.workout.programs.ProgramsEnum.FITNESS_TEST;
import static com.dyaco.spirit_commercial.workout.programs.ProgramsEnum.MARINE_CORPS;
import static com.dyaco.spirit_commercial.workout.programs.ProgramsEnum.NAVY;
import static com.dyaco.spirit_commercial.workout.programs.ProgramsEnum.PEB;
import static com.dyaco.spirit_commercial.workout.programs.ProgramsEnum.RUN_10K;
import static com.dyaco.spirit_commercial.workout.programs.ProgramsEnum.RUN_5K;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.FragmentWorkoutStatsBinding;
import com.dyaco.spirit_commercial.product_flavor.ModeEnum;
import com.dyaco.spirit_commercial.support.CommonUtils;
import com.dyaco.spirit_commercial.support.MsgEvent;
import com.dyaco.spirit_commercial.support.base_component.BaseBindingFragment;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.intdef.EventKey;
import com.dyaco.spirit_commercial.support.utils.MyViewUtils;
import com.dyaco.spirit_commercial.viewmodel.AppStatusViewModel;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingViewModel;
import com.dyaco.spirit_commercial.viewmodel.EgymDataViewModel;
import com.dyaco.spirit_commercial.viewmodel.WorkoutViewModel;
import com.jeremyliao.liveeventbus.LiveEventBus;

import java.util.List;
import java.util.Optional;

public class WorkoutStatsFragment extends BaseBindingFragment<FragmentWorkoutStatsBinding> {
    private MainWorkoutTrainingFragment parentFragment;
    private AppStatusViewModel appStatusViewModel;
    private WorkoutViewModel workoutViewModel;
    private DeviceSettingViewModel deviceSettingViewModel;
    //初始顯示
//    int stats1Tag = isTreadmill ? STATS_REMAINING_TIME : STATS_ELEVATION_GAIN;
    int stats1Tag = isTreadmill ? STATS_ELAPSED_TIME : STATS_ELEVATION_GAIN;
    int stats2Tag = isTreadmill ? STATS_INCLINE : STATS_POWER;
    int stats3Tag = isTreadmill ? STATS_SPEED : STATS_CALORIES;

    int stats4Tag = isTreadmill ? STATS_SPEED : STATS_CALORIES;
    int stats5Tag = isTreadmill ? STATS_SPEED : STATS_CALORIES;
    int stats6Tag = isTreadmill ? STATS_SPEED : STATS_CALORIES;

    private CommonUtils comm;
    public final int PANEL_1 = 1;
    public final int PANEL_2 = 2;
    public final int PANEL_3 = 3;

    public final int PANEL_4 = 4;
    public final int PANEL_5 = 5;
    public final int PANEL_6 = 6;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        appStatusViewModel = new ViewModelProvider(requireActivity()).get(AppStatusViewModel.class);
        workoutViewModel = new ViewModelProvider(requireActivity()).get(WorkoutViewModel.class);
        deviceSettingViewModel = new ViewModelProvider(requireActivity()).get(DeviceSettingViewModel.class);

        getBinding().setWorkoutData(workoutViewModel);
        getBinding().setDeviceSetting(deviceSettingViewModel);
        getBinding().setIsUs(isUs);
        getBinding().setIsGGG(isGGG);
        parentFragment = (MainWorkoutTrainingFragment) getParentFragment();

        comm = new CommonUtils();

        //個別預設
        if (workoutViewModel.selProgram == FITNESS_TEST) {
            stats1Tag = STATS_REMAINING_TIME;
            stats2Tag = STATS_POWER;
            stats3Tag = STATS_HEART_RATE;
        }

        if (workoutViewModel.selProgram == EGYM) {

            if (isUs && !isGGG) {

                if (isTreadmill) {
                    stats1Tag = STATS_INTERVAL;
                    stats2Tag = STATS_INCLINE;
                    stats4Tag = STATS_INTERVAL_DISTANCE;
                    stats5Tag = STATS_SPEED;
                } else {
                    stats1Tag = STATS_INTERVAL;
                    stats2Tag = STATS_CADENCE;
                    stats4Tag = STATS_INTERVAL_DISTANCE;
                    stats5Tag = STATS_LEVEL;
                }
            } else {

                if (isTreadmill) {
                    stats1Tag = STATS_CALORIES;
                    stats2Tag = STATS_INCLINE;
                    stats3Tag = STATS_INTERVAL;
                    stats4Tag = STATS_INTERVAL_DISTANCE;
                    stats5Tag = STATS_SPEED;
                    stats6Tag = STATS_HEART_RATE;
                } else {
                    stats1Tag = STATS_CALORIES;
                    stats2Tag = STATS_CADENCE;
                    stats3Tag = STATS_INTERVAL;
                    stats4Tag = STATS_INTERVAL_DISTANCE;
                    stats5Tag = STATS_LEVEL;
                    stats6Tag = STATS_HEART_RATE;
                }
            }
        }


        initView();
        initEvent();

        onSelect();

        parentFragment.iPrograms.initStats(this);


    }


    public void initEgymInterval() {

        EgymDataViewModel egymDataViewModel = new ViewModelProvider(requireActivity()).get(EgymDataViewModel.class);


        //from WorkoutChartsFragment initEgym
        //observeSticky 開始觀察前就收到通知，還是可以收到
        //⭐️只有一開始執行一次
        LiveEventBus.get(EGYM_STATS_INIT, List.class).observeSticky(getViewLifecycleOwner(), s -> {

            //初始化 小白條,[60, 120, 60, 120, 60, 120, 60, 120, 60, 120, 60, 120]
            getBinding().egymIntervalView.setDurationTimesList(egymDataViewModel.durationTimesList);
            getBinding().egymIntervalView.setProgress(1);

            //判斷-99(就是此set 沒設定秒數) 改成 -
            String sIntervalTotal;
            if (egymDataViewModel.durationTimesListKnowZero.get(0) == SYMBOL_DURATION) {
                getBinding().tvIntervalTotal.setTextColor(cc2);
                sIntervalTotal = String.format("/ %s", E_BLANK + E_BLANK);
            } else {
                getBinding().tvIntervalTotal.setTextColor(cc1);
                sIntervalTotal = String.format("/ %s", formatSecToM(egymDataViewModel.durationTimesListKnowZero.get(0)));
            }
            getBinding().tvIntervalTotal.setText(sIntervalTotal);


        });

        //用 MutableLiveData 就不用手動移除
     //   workoutViewModel.currentSegment.addOnPropertyChangedCallback(segmentCallback);


        //從 MainWorkoutTrainingFragment - setEgymCloudIntervalData() 傳來的  > 用 durationRealTimesList 計算真實的 Interval,當 Interval 改變時呼叫
//        LiveEventBus.get(EGYM_SETS_DATA_UPDATE_REAL, Integer.class).observe(getViewLifecycleOwner(), p -> {
//            updateEgymStats(egymDataViewModel);
//        });

    }


//    private final Observable.OnPropertyChangedCallback segmentCallback = new Observable.OnPropertyChangedCallback() {
//        @Override
//        public void onPropertyChanged(Observable sender, int propertyId) {
//            Log.d("EGYM_W", "currentSegment改變: " + workoutViewModel.currentSegment.get());
//            if (getBinding() != null) {
//                getBinding().egymIntervalView.setProgress(workoutViewModel.currentSegment.get());
//            }
//        }
//    };


    //⭐️更新 eGym 每個Set的資料(EGYM_SETS_DATA_UPDATE_REAL) ,初始資料 (EGYM_SETS_DATA)
    public void updateEgymStats(EgymDataViewModel evm) {
     //   Log.d("KKKKEEEE", "egymIntervalDistance : " + egymDataViewModel.setsTimePosition + ", " + egymDataViewModel.durationTimesList + ", " + realInterval);

        int realInterval = workoutViewModel.egymCurrentSet.get();
        Log.d("KKKKEEEE", "updateEgymStats:更新Set資料 , 目前SET:" + realInterval +", ##("+ evm.durationTimesListKnowZero);

        //   getBinding().tvIntervalTotal.setText(String.format("/ %s", formatSecToM(egymDataViewModel.durationTimesList.get(workoutViewModel.egymCurrentInterval.get() - 1))));

        //判斷-99 改成 —
        String sIntervalTotal;
        if (evm.durationTimesListKnowZero.get(realInterval) == SYMBOL_DURATION) {
            getBinding().tvIntervalTotal.setTextColor(cc2);
            sIntervalTotal = String.format("/ %s", E_BLANK + E_BLANK);
        } else {
            getBinding().tvIntervalTotal.setTextColor(cc1);
            sIntervalTotal = String.format("/ %s", formatSecToM(evm.durationTimesListKnowZero.get(realInterval)));
        }
//
//        Log.d("XXXXXXXX", "111updateEgymStats: " + evm.durationTimesListKnowZero.toString());
//        Log.d("XXXXXXXX", "2222updateEgymStats: " + evm.durationTimesListKnowZero.get(realInterval) +", realInterval:"+ realInterval  +", sIntervalTotal:"+ sIntervalTotal);

//        getBinding().tvIntervalTotal.setWaveType(WavyTextView.WAVE_TYPE_COLOR);
//        getBinding().tvIntervalTotal.startWaveOnce(800);



        getBinding().tvIntervalTotal.setText(sIntervalTotal);

        getBinding().egymIntervalView.setProgress(workoutViewModel.egymCurrentSet.get() + 1);


        //Interval 變更時，變更EGYM TARGET
        if (MODE == CT1000ENT) {
            workoutViewModel.egymTargetSpeed.set(getEgymUnitS(evm.unitSystem.get(), evm.selTrainer.getIntervals().get((realInterval)).getSpeed()));
            workoutViewModel.egymTargetIncline.set(formatS(evm.selTrainer.getIntervals().get((realInterval)).getIncline()));
        } else if (MODE == CE1000ENT || MODE == ModeEnum.STEPPER) {
            workoutViewModel.egymTargetSpeed.set(String.valueOf(evm.selTrainer.getIntervals().get(realInterval).getResistance()));
            workoutViewModel.egymTargetIncline.set(formatS(Double.valueOf(evm.selTrainer.getIntervals().get(realInterval).getStepsPerMinute())));
        } else {
            workoutViewModel.egymTargetSpeed.set(String.valueOf(evm.selTrainer.getIntervals().get(realInterval).getResistance()));
            workoutViewModel.egymTargetIncline.set(formatS(Double.valueOf(evm.selTrainer.getIntervals().get(realInterval).getRotations())));
        }


        workoutViewModel.egymTargetDistance.set(
                getEgymUnitD(evm.unitSystem.get(), evm.selTrainer.getIntervals()
                        .get((realInterval)).getDistance()));

        workoutViewModel.egymTargetHeartRate.set(Optional.ofNullable(
                evm.selTrainer.getIntervals().get((realInterval)).getHeartRate()
        ).map(String::valueOf).orElse(E_BLANK));
    }


    private boolean checkDistanceLeft() {
        return (workoutViewModel.selProgram == ARMY || workoutViewModel.selProgram == AIR_FORCE || workoutViewModel.selProgram == COAST_GUARD || workoutViewModel.selProgram == NAVY || workoutViewModel.selProgram == PEB || workoutViewModel.selProgram == RUN_5K || workoutViewModel.selProgram == RUN_10K || workoutViewModel.selProgram == MARINE_CORPS);
    }


    private void changeStateEgym(int no) {

        TextView stateShow = getBinding().tvShow1Egym;
        TextView stateShow2 = getBinding().tvShow12Egym;
        TextView stateShowSelect = getBinding().tvShow1SelectEgym;
        TextView stateShowUnit = getBinding().tvShow1UnitEgym;
        int stateTag = stats1Tag;
        switch (no) {
            case PANEL_1:
                stateShow = getBinding().tvShow1Egym;
                stateShow2 = getBinding().tvShow12Egym;
                stateShowSelect = getBinding().tvShow1SelectEgym;
                stateShowUnit = getBinding().tvShow1UnitEgym;
                stateTag = stats1Tag;
                break;
            case PANEL_2:
                stateShow = getBinding().tvShow2Egym;
                stateShow2 = getBinding().tvShow22Egym;
                stateShowSelect = getBinding().tvShow2SelectEgym;
                stateShowUnit = getBinding().tvShow2UnitEgym;
                stateTag = stats2Tag;
                break;
            case PANEL_3:
                stateShow = getBinding().tvShow3Egym;
                stateShow2 = getBinding().tvShow32Egym;
                stateShowSelect = getBinding().tvShow3SelectEgym;
                stateShowUnit = getBinding().tvShow3UnitEgym;
                stateTag = stats3Tag;
                break;
            case PANEL_4:
                stateShow = getBinding().tvShow4Egym;
                stateShow2 = getBinding().tvShow42Egym;
                stateShowSelect = getBinding().tvShow4SelectEgym;
                stateShowUnit = getBinding().tvShow4UnitEgym;
                stateTag = stats4Tag;
                break;
            case PANEL_5:
                stateShow = getBinding().tvShow5Egym;
                stateShow2 = getBinding().tvShow52Egym;
                stateShowSelect = getBinding().tvShow5SelectEgym;
                stateShowUnit = getBinding().tvShow5UnitEgym;
                stateTag = stats5Tag;
                break;
            case PANEL_6:
                stateShow = getBinding().tvShow6Egym;
                stateShow2 = getBinding().tvShow62Egym;
                stateShowSelect = getBinding().tvShow6SelectEgym;
                stateShowUnit = getBinding().tvShow6UnitEgym;
                stateTag = stats6Tag;
                break;

        }

        //點下去時先重整一下，調整位置
        comm.setStats(stateShow, stateShowSelect, stateShowUnit, stateShow2, stateTag, workoutViewModel, requireActivity());


        //   getBinding().tvStateShow1.bringToFront();
        parent.popupWindow = new WorkoutSettingWindow(requireActivity(), workoutViewModel, deviceSettingViewModel, stateTag, no, stats1Tag, stats2Tag, stats3Tag, stats4Tag, stats5Tag, stats6Tag);
        parent.popupWindow.showAtLocation(requireActivity().getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);
        TextView finalStateShow = stateShow;
        TextView finalStateShow2 = stateShow2;
        TextView finalStateShowSelect = stateShowSelect;
        TextView finalStateShowUnit = stateShowUnit;

        ((WorkoutSettingWindow) parent.popupWindow).setOnCustomDismissListener(new BasePopupWindow.OnCustomDismissListener() {
            @Override
            public void onStartDismiss(MsgEvent value) {
                if (value != null) {
                    int tag = Integer.parseInt(value.getObj().toString());
                    switch (no) {
                        case PANEL_1:
                            stats1Tag = tag;
                            break;
                        case PANEL_2:
                            stats2Tag = tag;
                            break;
                        case PANEL_3:
                            stats3Tag = tag;
                            break;
                        case PANEL_4:
                            stats4Tag = tag;
                            break;
                        case PANEL_5:
                            stats5Tag = tag;
                            break;
                        case PANEL_6:
                            stats6Tag = tag;
                            break;
                    }

                    comm.setStats(finalStateShow, finalStateShowSelect, finalStateShowUnit, finalStateShow2, tag, workoutViewModel, requireActivity());
                }
            }

            @Override
            public void onDismiss() {
                try {
                    ((MainActivity) requireActivity()).popupWindow = null;
                } catch (Exception e) {
                    showException(e);
                }
            }
        });
    }


    private void changeState(int no) {

        TextView stateShow = getBinding().tvStateShow1;
        TextView stateShowSelect = getBinding().tvStateShowSelect1;
        TextView stateShowUnit = getBinding().tvStateShow1Unit;
        int stateTag = stats1Tag;
        switch (no) {
            case PANEL_1:
                stateShow = getBinding().tvStateShow1;
                stateShowSelect = getBinding().tvStateShowSelect1;
                stateShowUnit = getBinding().tvStateShow1Unit;
                stateTag = stats1Tag;
                break;
            case PANEL_2:
                stateShow = getBinding().tvStateShow2;
                stateShowSelect = getBinding().tvStateShowSelect2;
                stateShowUnit = getBinding().tvStateShow2Unit;
                stateTag = stats2Tag;
                break;
            case PANEL_3:
                stateShow = getBinding().tvStateShow3;
                stateShowSelect = getBinding().tvStateShowSelect3;
                stateShowUnit = getBinding().tvStateShow3Unit;
                stateTag = stats3Tag;
                break;

        }

        //點下去時先重整一下，調整位置
        comm.setStats(stateShow, stateShowSelect, stateShowUnit, null, stateTag, workoutViewModel, requireActivity());


        //   getBinding().tvStateShow1.bringToFront();
        parent.popupWindow = new WorkoutSettingWindow(requireActivity(), workoutViewModel, deviceSettingViewModel, stateTag, no, stats1Tag, stats2Tag, stats3Tag, stats4Tag, stats5Tag, stats6Tag);
        parent.popupWindow.showAtLocation(requireActivity().getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);
        TextView finalStateShow = stateShow;
        TextView finalStateShowSelect = stateShowSelect;
        TextView finalStateShowUnit = stateShowUnit;

        ((WorkoutSettingWindow) parent.popupWindow).setOnCustomDismissListener(new BasePopupWindow.OnCustomDismissListener() {
            @Override
            public void onStartDismiss(MsgEvent value) {
                if (value != null) {
                    int tag = Integer.parseInt(value.getObj().toString());
                    switch (no) {
                        case PANEL_1:
                            stats1Tag = tag;
                            break;
                        case PANEL_2:
                            stats2Tag = tag;
                            break;
                        case PANEL_3:
                            stats3Tag = tag;
                    }

                    comm.setStats(finalStateShow, finalStateShowSelect, finalStateShowUnit, null, tag, workoutViewModel, requireActivity());
                }
            }

            @Override
            public void onDismiss() {
                try {
                    ((MainActivity) requireActivity()).popupWindow = null;
                } catch (Exception e) {
                    showException(e);
                }
            }
        });
    }

    private void initEvent() {

        if (workoutViewModel.selProgram != EGYM) {
            MyViewUtils.expandTouchArea(getBinding().tvStateShowSelect1, 60);

            getBinding().statsNormal1.setOnClickListener(v -> changeState(PANEL_1));
            getBinding().statsNormal2.setOnClickListener(v -> changeState(PANEL_2));
            getBinding().statsNormal3.setOnClickListener(v -> changeState(PANEL_3));


        } else {
            getBinding().statsEgym1.setOnClickListener(v -> changeStateEgym(PANEL_1));
            getBinding().statsEgym2.setOnClickListener(v -> changeStateEgym(PANEL_2));
            getBinding().statsEgym3.setOnClickListener(v -> changeStateEgym(PANEL_3));
            getBinding().statsEgym4.setOnClickListener(v -> changeStateEgym(PANEL_4));
            getBinding().statsEgym5.setOnClickListener(v -> changeStateEgym(PANEL_5));
            getBinding().statsEgym6.setOnClickListener(v -> changeStateEgym(PANEL_6));
        }
    }

    private void initView() {

//        int stats1Tag = isTreadmill ? STATS_REMAINING_TIME : STATS_ELEVATION_GAIN;
//        int stats2Tag = isTreadmill ? STATS_INCLINE : STATS_POWER;
//        int stats3Tag = isTreadmill ? STATS_SPEED : STATS_CALORIES;

        //初始顯示
//        stats1Tag = workoutViewModel.selWorkoutTime.get() == UNLIMITED ? STATS_ELAPSED_TIME : STATS_REMAINING_TIME;

//        stats2Tag = isTreadmill ? (checkDistanceLeft() ? STATS_DISTANCE_LEFT : STATS_INCLINE) : STATS_POWER;
        if (workoutViewModel.selProgram != EGYM) {
            stats1Tag = STATS_ELAPSED_TIME;
            stats2Tag = isTreadmill ? (checkDistanceLeft() ? STATS_DISTANCE_LEFT : STATS_INCLINE) : STATS_POWER;

            comm.setStats(getBinding().tvStateShow1, getBinding().tvStateShowSelect1, getBinding().tvStateShow1Unit, null, stats1Tag, workoutViewModel, requireActivity());
            comm.setStats(getBinding().tvStateShow2, getBinding().tvStateShowSelect2, getBinding().tvStateShow2Unit, null, stats2Tag, workoutViewModel, requireActivity());
            comm.setStats(getBinding().tvStateShow3, getBinding().tvStateShowSelect3, getBinding().tvStateShow3Unit, null, stats3Tag, workoutViewModel, requireActivity());
        } else {

         //   stats2Tag =

            comm.setStats(getBinding().tvShow1Egym, getBinding().tvShow1SelectEgym, getBinding().tvShow1UnitEgym, getBinding().tvShow12Egym, stats1Tag, workoutViewModel, requireActivity());
            comm.setStats(getBinding().tvShow2Egym, getBinding().tvShow2SelectEgym, getBinding().tvShow2UnitEgym, getBinding().tvShow22Egym, stats2Tag, workoutViewModel, requireActivity());
            comm.setStats(getBinding().tvShow3Egym, getBinding().tvShow3SelectEgym, getBinding().tvShow3UnitEgym, getBinding().tvShow32Egym, stats3Tag, workoutViewModel, requireActivity());
            comm.setStats(getBinding().tvShow4Egym, getBinding().tvShow4SelectEgym, getBinding().tvShow4UnitEgym, getBinding().tvShow42Egym, stats4Tag, workoutViewModel, requireActivity());
            comm.setStats(getBinding().tvShow5Egym, getBinding().tvShow5SelectEgym, getBinding().tvShow5UnitEgym, getBinding().tvShow52Egym, stats5Tag, workoutViewModel, requireActivity());
            comm.setStats(getBinding().tvShow6Egym, getBinding().tvShow6SelectEgym, getBinding().tvShow6UnitEgym, getBinding().tvShow62Egym, stats6Tag, workoutViewModel, requireActivity());
        }

    }

    private void onSelect() {
        getBinding().btnCharts.setOnClickListener(v -> LiveEventBus.get(SELECT_WORKOUT_PAGE).post(WORKOUT_PAGE_CHARTS));
        getBinding().btnTrack.setOnClickListener(v -> LiveEventBus.get(SELECT_WORKOUT_PAGE).post(WORKOUT_PAGE_TRACK));
        getBinding().btnGarmin.setOnClickListener(v -> LiveEventBus.get(SELECT_WORKOUT_PAGE).post(WORKOUT_PAGE_GARMIN));
    }


    //更新STATS的資料  //每秒執行，全部stats 都更新
    public void updateStatsData() {
        if (workoutViewModel.selProgram != EGYM) {
            getBinding().tvStateShow1.setText(comm.getStatsValue(stats1Tag, workoutViewModel, isTreadmill, false));
            getBinding().tvStateShow2.setText(comm.getStatsValue(stats2Tag, workoutViewModel, isTreadmill, false));
            getBinding().tvStateShow3.setText(comm.getStatsValue(stats3Tag, workoutViewModel, isTreadmill, false));
        } else {
            getBinding().tvShow1Egym.setText(comm.getStatsValue(stats1Tag, workoutViewModel, isTreadmill, false));
            getBinding().tvShow12Egym.setText(comm.getStatsValue(stats1Tag, workoutViewModel, isTreadmill, true));

            getBinding().tvShow2Egym.setText(comm.getStatsValue(stats2Tag, workoutViewModel, isTreadmill, false));
            getBinding().tvShow22Egym.setText(comm.getStatsValue(stats2Tag, workoutViewModel, isTreadmill, true));

            getBinding().tvShow3Egym.setText(comm.getStatsValue(stats3Tag, workoutViewModel, isTreadmill, false));
            getBinding().tvShow32Egym.setText(comm.getStatsValue(stats3Tag, workoutViewModel, isTreadmill, true));

            getBinding().tvShow4Egym.setText(comm.getStatsValue(stats4Tag, workoutViewModel, isTreadmill, false));
            getBinding().tvShow42Egym.setText(comm.getStatsValue(stats4Tag, workoutViewModel, isTreadmill, true));

            getBinding().tvShow5Egym.setText(comm.getStatsValue(stats5Tag, workoutViewModel, isTreadmill, false));
            getBinding().tvShow52Egym.setText(comm.getStatsValue(stats5Tag, workoutViewModel, isTreadmill, true));

            getBinding().tvShow6Egym.setText(comm.getStatsValue(stats6Tag, workoutViewModel, isTreadmill, false));
            getBinding().tvShow62Egym.setText(comm.getStatsValue(stats6Tag, workoutViewModel, isTreadmill, true));


            changeViewColor(stats1Tag, getBinding().tvShow12Egym);
            changeViewColor(stats2Tag, getBinding().tvShow22Egym);
            changeViewColor(stats3Tag, getBinding().tvShow32Egym);
            changeViewColor(stats4Tag, getBinding().tvShow42Egym);
            changeViewColor(stats5Tag, getBinding().tvShow52Egym);
            changeViewColor(stats6Tag, getBinding().tvShow62Egym);
        }


        if (isSettingShow) {
            //改變高亮的值 > WorkoutSettingWindow
            LiveEventBus.get(EventKey.WORKOUT_STATS_SEND).post(true);
        }
    }

    int cc1 = ContextCompat.getColor(getApp(), R.color.white);
    int cc2 = ContextCompat.getColor(getApp(), R.color.white_20);

    //當沒有目標值時，要變色
    private void changeViewColor(int tag, TextView view) {
        String value = "1";
        switch (tag) {
            case STATS_SPEED:
            case STATS_LEVEL:
                value = workoutViewModel.egymTargetSpeed.get();
                break;
            case STATS_INCLINE:
                value = workoutViewModel.egymTargetIncline.get();
                break;
            case STATS_INTERVAL_DISTANCE:
                value = workoutViewModel.egymTargetDistance.get();
                break;
            case STATS_HEART_RATE:
                value = workoutViewModel.egymTargetHeartRate.get();
                break;
        }
        updateTextViewColor(view, value);
    }

    private void updateTextViewColor(TextView textView, String parameter) {
        int currentColor = textView.getCurrentTextColor();
        int targetColor = (E_BLANK.equals(parameter)) ? cc2 : cc1;

        if (currentColor != targetColor) { // 只有當顏色不同時才執行 setTextColor
            textView.setTextColor(targetColor);
        }
    }

    public final int NORMAL_STATS = 1;
    public final int HEART_RATE_STATS = 2;
    public final int HEART_TARGET_STATS = 3;
    public final int EGYM_STATS = 4;

    public void showStatsGroup(int statsType) {

        switch (statsType) {
            case NORMAL_STATS:
                getBinding().groupNormal.setVisibility(View.VISIBLE);
                getBinding().groupHR.setVisibility(View.GONE);
                getBinding().groupGerkin.setVisibility(View.GONE);
                getBinding().groupEgym.setVisibility(View.GONE);
                break;
            case HEART_RATE_STATS:
                getBinding().groupNormal.setVisibility(View.GONE);
                getBinding().groupHR.setVisibility(View.VISIBLE);
                getBinding().groupGerkin.setVisibility(View.GONE);
                getBinding().tvHrStatus.setVisibility(View.GONE);
                getBinding().groupEgym.setVisibility(View.GONE);
                break;
            case HEART_TARGET_STATS:
                getBinding().groupNormal.setVisibility(View.GONE);
                getBinding().groupHR.setVisibility(View.GONE);
                getBinding().groupGerkin.setVisibility(View.VISIBLE);
                getBinding().groupEgym.setVisibility(View.GONE);
                break;
            case EGYM_STATS:
                getBinding().groupNormal.setVisibility(View.GONE);
                getBinding().groupHR.setVisibility(View.GONE);
                getBinding().groupGerkin.setVisibility(View.GONE);
                getBinding().groupEgym.setVisibility(View.VISIBLE);

                if (isUs && !isGGG) {
                    getBinding().statsEgym3.setVisibility(View.GONE);
                    getBinding().statsEgym6.setVisibility(View.GONE);
                }
                break;
        }
    }

    public boolean isStatsPageShow = true;

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        isStatsPageShow = !hidden;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
     //   workoutViewModel.currentSegment.removeOnPropertyChangedCallback(segmentCallback);
    }
}