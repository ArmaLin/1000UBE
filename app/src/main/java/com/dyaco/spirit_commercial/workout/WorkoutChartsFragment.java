package com.dyaco.spirit_commercial.workout;

import static com.corestar.libs.device.DeviceGEM.EQUIPMENT_CONTROL_OPERATION.SET_TARGET_INCLINATION;
import static com.corestar.libs.device.DeviceGEM.EQUIPMENT_CONTROL_OPERATION.SET_TARGET_RESISTANCE_LEVEL;
import static com.corestar.libs.device.DeviceGEM.EQUIPMENT_CONTROL_OPERATION.SET_TARGET_SPEED;
import static com.corestar.libs.device.DeviceGEM.EQUIPMENT_CONTROL_RESPONSE.INVALID_PARAMETER;
import static com.corestar.libs.device.DeviceGEM.EQUIPMENT_CONTROL_RESPONSE.SUCCESS;
import static com.dyaco.spirit_commercial.App.MODE;
import static com.dyaco.spirit_commercial.MainActivity.isTreadmill;
import static com.dyaco.spirit_commercial.MainActivity.isUs;
import static com.dyaco.spirit_commercial.support.CommonUtils.getFloat;
import static com.dyaco.spirit_commercial.support.CommonUtils.makeDropDownMeasureSpec;
import static com.dyaco.spirit_commercial.support.FormulaUtil.initEgymWorkoutData;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.getInclineAd;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.getInclineValue;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.getMaxSpeedLevel;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.getMaxSpeedValue;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.getMinSpeedLevel;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.getSpeedValue;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.IMPERIAL;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.EGYM_STATS_INIT;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.SELECT_WORKOUT_PAGE;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.BAR_STATUS_SEGMENT_BLANK;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.BAR_STATUS_SEGMENT_FINISH;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.BAR_STATUS_SEGMENT_NONE;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.BAR_STATUS_SEGMENT_PENDING;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.BAR_STATUS_SEGMENT_RUNNING;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.BAR_TYPE_BLANK;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.BAR_TYPE_INCLINE;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.BAR_TYPE_LEVEL_SPEED;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.HIIT_REST_BAR;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.HIIT_SPRINT_BAR;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.NORMAL_BAR;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.UPDATE_ALL_BAR;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.UPDATE_INCLINE_BAR;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.UPDATE_SPEED_BAR;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.WORKOUT_PAGE_GARMIN;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.WORKOUT_PAGE_STATS;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.WORKOUT_PAGE_TRACK;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.BAR_COUNT_GERKIN;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.BAR_COUNT_NORMAL;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.BAR_WIDTH_NORMAL;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.MAX_LEVEL_MAX;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.POWER_MAX;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.viewX;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.viewY;
import static com.dyaco.spirit_commercial.support.intdef.WorkoutIntDef.UNLIMITED;
import static com.dyaco.spirit_commercial.workout.MainWorkoutTrainingFragment.isGGG;
import static com.dyaco.spirit_commercial.workout.programs.ProgramsEnum.EGYM;
import static com.dyaco.spirit_commercial.workout.programs.ProgramsEnum.FITNESS_TEST;
import static com.dyaco.spirit_commercial.workout.programs.ProgramsEnum.GERKIN;
import static com.dyaco.spirit_commercial.workout.programs.ProgramsEnum.HEART_RATE;
import static com.dyaco.spirit_commercial.workout.programs.ProgramsEnum.HIIT;
import static com.dyaco.spirit_commercial.workout.programs.ProgramsEnum.MANUAL;
import static com.dyaco.spirit_commercial.workout.programs.ProgramsEnum.WATTS;
import static com.dyaco.spirit_commercial.workout.programs.ProgramsEnum.WINGATE_TEST;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.Observable;
import androidx.lifecycle.ViewModelProvider;

import com.corestar.calculation_libs.Calculation;
import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.FragmentWorkoutChartsBinding;
import com.dyaco.spirit_commercial.egym.EgymDiagramBarsViewBySet;
import com.dyaco.spirit_commercial.egym.EgymUtil;
import com.dyaco.spirit_commercial.product_flavor.ModeEnum;
import com.dyaco.spirit_commercial.support.FormulaUtil;
import com.dyaco.spirit_commercial.support.RxTimer;
import com.dyaco.spirit_commercial.support.WorkoutUtil;
import com.dyaco.spirit_commercial.support.base_component.BaseBindingFragment;
import com.dyaco.spirit_commercial.support.custom_view.DiagramBarsView;
import com.dyaco.spirit_commercial.support.intdef.AppStatusIntDef;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;
import com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS;
import com.dyaco.spirit_commercial.support.intdef.WorkoutIntDef;
import com.dyaco.spirit_commercial.viewmodel.AppStatusViewModel;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingViewModel;
import com.dyaco.spirit_commercial.viewmodel.DiagramBarBean;
import com.dyaco.spirit_commercial.viewmodel.EgymDataViewModel;
import com.dyaco.spirit_commercial.viewmodel.WorkoutViewModel;
import com.dyaco.spirit_commercial.workout.programs.ProgramsEnum;
import com.jeremyliao.liveeventbus.LiveEventBus;

import java.util.List;


public class WorkoutChartsFragment extends BaseBindingFragment<FragmentWorkoutChartsBinding> {
    public Calculation calc;
    public DiagramBarsView diagramBarsView;
    private AppStatusViewModel appStatusViewModel;
    private WorkoutViewModel w;
    private DeviceSettingViewModel deviceSettingViewModel;

    private ProgramsEnum currentProgram;

    private MainWorkoutTrainingFragment parentFragment;

    public ChartMsgWindow chartMsgStageWindow;

    public EgymDataViewModel egymDataViewModel;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        appStatusViewModel = new ViewModelProvider(requireActivity()).get(AppStatusViewModel.class);
        w = new ViewModelProvider(requireActivity()).get(WorkoutViewModel.class);
        deviceSettingViewModel = new ViewModelProvider(requireActivity()).get(DeviceSettingViewModel.class);

        egymDataViewModel = new ViewModelProvider(requireActivity()).get(EgymDataViewModel.class);

        getBinding().setWorkoutViewModel(w);
        getBinding().setDeviceSetting(deviceSettingViewModel);
        getBinding().setIsUs(isUs);
        getBinding().setIsGGG(isGGG);
        currentProgram = w.selProgram;
        calc = parent.calculation;

        parentFragment = (MainWorkoutTrainingFragment) getParentFragment();

        initView();

        onSelect();


        initEgym();

        initDiagram();


//        w.inclineDiagramBarList.get(0).setBarNum(40);
//        w.inclineDiagramBarList.get(1).setBarNum(40);
//        w.inclineDiagramBarList.get(2).setBarNum(40);
//        w.inclineDiagramBarList.get(3).setBarNum(40);

        new RxTimer().timer(1000, number -> getBinding().eBG.setVisibility(View.GONE));


        //從InitTimer啟動
//        updateDiagramBarNum(isTreadmill ? UPDATE_ALL_BAR : UPDATE_SPEED_BAR, false);
    }


    public EgymDiagramBarsViewBySet egymDiagramBarsView;
//    List<Integer> setsTimePosition = new ArrayList<>();//每個set 最後一個 index

    private void initEgym() {
        if (currentProgram == ProgramsEnum.EGYM) {

            getBinding().touchBarView.setVisibility(View.GONE);
            getBinding().bbbbbb.setVisibility(View.GONE);
            getBinding().egymDiagramBarsView.setVisibility(View.VISIBLE);


            initEgymWorkoutData(egymDataViewModel);

            egymDiagramBarsView = getBinding().egymDiagramBarsView;

            egymDiagramBarsView.isTreadmill(isTreadmill);

            int barMaxLevel = (int) (getFloat(getMaxSpeedValue(currentProgram)) > 0 ? getMaxSpeedValue(currentProgram) + 1 : getMaxSpeedValue(currentProgram));
            //bar 長度
            egymDiagramBarsView.setBarMaxLevel(barMaxLevel);
            //bar 數量
            egymDiagramBarsView.setDurationTimesList(egymDataViewModel.durationTimesList);


            LiveEventBus.get(EGYM_STATS_INIT, List.class).post(egymDataViewModel.setsTimePosition);

            egymDiagramBarsView.setWhiteLinePosition(0);

            w.egymTotalInterval.set(egymDataViewModel.durationTimesList.size());
            w.egymCurrentSet.set(0);


            if (MODE != ModeEnum.CT1000ENT) {
                //EGYM BIKE
                //    new RxTimer().timer(1000, number -> updateDiagramBarNumCadence());

                w.currentRpm.addOnPropertyChangedCallback(rpmCallback);


//                w.inclineDiagramBarList.get(0).setBarNum(40);
//                w.inclineDiagramBarList.get(1).setBarNum(40);
//                w.inclineDiagramBarList.get(2).setBarNum(40);
//                w.inclineDiagramBarList.get(3).setBarNum(40);
            }

        }

    }

    //RPM 改變時觸發
    private final Observable.OnPropertyChangedCallback rpmCallback = new Observable.OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable sender, int propertyId) {
            if (getBinding() != null) {
                Log.d("EEEQQQDDDAAA", "onPropertyChanged: " + w.currentRpm.get());
                egymDiagramBarsView.setBarLevel(BAR_TYPE_INCLINE, w.egymCurrentSet.get(), w.currentRpm.get(), w.egymCurrentSet.get(), false);

            }
        }
    };


    /**
     * 初始化圖表
     */
    private void initDiagram() {

        diagramBarsView.isTreadmill(isTreadmill);

        //bar 數量
        diagramBarsView.setBarCount(BAR_COUNT_NORMAL);

        //bar 寬度  // mViewWidth 1390   , gerkin 31.59 - 24, 69.5 - 61.55, - 7.5
        //  diagramBarsView.setBarWidth(currentProgram != GERKIN ? BAR_WIDTH_NORMAL : BAR_WIDTH_GERKIN);

        int barMaxLevel = (int) (getFloat(getMaxSpeedValue(currentProgram)) > 0 ? getMaxSpeedValue(currentProgram) + 1 : getMaxSpeedValue(currentProgram));

        if (currentProgram == WINGATE_TEST) {
            barMaxLevel = 1200;
        }

        Log.d("PPEPEPEPFPE", "initDiagram: " + barMaxLevel);

        //動態
//        if (isTreadmill) {
//            if (barMaxLevel < (getInclineValue(MAX_INC_MAX))) {
//                barMaxLevel = (int) (getInclineValue(MAX_INC_MAX));
//            }
//        }

        //bar 長度
        diagramBarsView.setBarMaxLevel(barMaxLevel);

        //換行時間
        timeTick = w.selWorkoutTime.get() == UNLIMITED ? 60 : w.selWorkoutTime.get() / diagramBarsView.getBarCount();

        if (timeTick == 0) {
            timeTick = 1;
        }

        //Program初始化Chart
        parentFragment.iPrograms.initChart(this);


        //   timeTick = 2;

        //bar 寬度
        //  diagramBarsView.setBarWidthA();

        w.speedDiagramBarList.clear();
        w.blankDiagramBarList.clear();
        w.hrList.clear();
        w.rpmList.clear();
        w.metsList.clear();
        w.inclineDiagramBarList.clear();

        int barkCheck = NORMAL_BAR;
        int barkTime = 0;
        int sprintI = 0;
        int restI = 0;

        //建立圖
        //    for (int i = 0; i < diagramBarsView.getBarCount(); i++) {
        for (int i = 0; i < w.orgArraySpeedAndLevel.length; i++) {

            //初始化HIIT資料
            if (currentProgram == HIIT) {
                barkCheck = (i + 1) % 2 == 0 ? HIIT_REST_BAR : HIIT_SPRINT_BAR;
                if (barkCheck == HIIT_SPRINT_BAR) {
                    barkTime = ((sprintI * w.selSprintTimeSec.get()) + (restI * w.selRestTimeSec.get())); //+1 1分1秒跳
                    sprintI++;
                } else {
                    barkTime = ((sprintI * w.selSprintTimeSec.get()) + (restI * w.selRestTimeSec.get()));
                    restI++;
                }
            }

            if (currentProgram.getProgramType() == WorkoutIntDef.PROFILE_PROGRAM) {


//                int maxLevelMax = getMaxSpeedLevel(currentProgram);
//                int maxLevelMin = isTreadmill ? (UNIT_E == IMPERIAL ? OPT_SETTINGS.MAX_SPD_IU_MIN : OPT_SETTINGS.MAX_SPD_MU_MIN) : MAX_LEVEL_DEF;
//                //取陣列中最大的一筆，大於maxLevelMax就帶maxLevelMax，小於maxLevelMin就帶maxLevelMin
//                IntSummaryStatistics stat = Arrays.stream(w.orgArraySpeedAndLevel).summaryStatistics();
//                int currentMaxLevel = MathUtils.clamp(stat.getMax(), maxLevelMin, maxLevelMax);//整個Profile的最大值
//                Log.d("YYYEEEYYE", "currentMaxLevel: " + currentMaxLevel +", orgMaxSpeedInProfile:"+ w.orgMaxSpeedInProfile.get());
                //根據選擇的 Max，算出新的速度或LEVEL 圖形
                // float newMaxLevel = w.selMaxSpeedOrLevel.get();//選擇的MAX

                //根據選擇的 Max，算出新的速度或LEVEL
                int newLevel = FormulaUtil.calcCurrentNum(w.selMaxSpeedOrLevel.get(), w.orgArraySpeedAndLevel[i], w.orgMaxSpeedInProfile.get());
                //       Log.d("YYYYYYYYY", "initDiagram: newLevel:" + newLevel +","+ newMaxLevel +","+  w.orgArraySpeedAndLevel[i] +","+ currentMaxLevel );
                if (isTreadmill) {
                    if (i <= 2) {//warmup
                        barkCheck = 1;
                    } else {
                        if (i >= 23) {//cooldown
                            barkCheck = 2;
                        } else {
                            barkCheck = 0;
                        }
                    }
                    //設定 SPEED or LEVEL 圖形
                    //設定第4根開始running
                    w.speedDiagramBarList.add(new DiagramBarBean(BAR_TYPE_LEVEL_SPEED, i != 3 ? BAR_STATUS_SEGMENT_PENDING : BAR_STATUS_SEGMENT_RUNNING, newLevel, w.orgArraySpeedAndLevel[i], barkCheck, barkTime));
                    w.blankDiagramBarList.add(new DiagramBarBean(BAR_TYPE_BLANK, i != 3 ? BAR_STATUS_SEGMENT_NONE : BAR_STATUS_SEGMENT_BLANK, i != 3 ? 0 : diagramBarsView.getBarMaxLevel(), 0, barkCheck, barkTime));
                    w.inclineDiagramBarList.add(new DiagramBarBean(BAR_TYPE_INCLINE, i != 3 ? BAR_STATUS_SEGMENT_PENDING : BAR_STATUS_SEGMENT_RUNNING, w.orgArrayIncline[i], w.orgArrayIncline[i], barkCheck, barkTime));

                } else {
                    //bike PROFILE_PROGRAM
                    w.speedDiagramBarList.add(new DiagramBarBean(BAR_TYPE_LEVEL_SPEED, i > 0 ? BAR_STATUS_SEGMENT_PENDING : BAR_STATUS_SEGMENT_RUNNING, newLevel, w.orgArraySpeedAndLevel[i], barkCheck, barkTime));
                    w.blankDiagramBarList.add(new DiagramBarBean(BAR_TYPE_BLANK, i > 0 ? BAR_STATUS_SEGMENT_NONE : BAR_STATUS_SEGMENT_BLANK, i > 0 ? 0 : diagramBarsView.getBarMaxLevel(), 0, barkCheck, barkTime));
                    w.inclineDiagramBarList.add(new DiagramBarBean(BAR_TYPE_INCLINE, i > 0 ? BAR_STATUS_SEGMENT_PENDING : BAR_STATUS_SEGMENT_RUNNING, 0, 0, barkCheck, barkTime));
                }

            } else {
                //非Profile Program 設定 SPEED or LEVEL 圖形
                w.speedDiagramBarList.add(new DiagramBarBean(BAR_TYPE_LEVEL_SPEED, i > 0 ? BAR_STATUS_SEGMENT_PENDING : BAR_STATUS_SEGMENT_RUNNING, w.orgArraySpeedAndLevel[i], w.orgArraySpeedAndLevel[i], barkCheck, barkTime));
                //     Log.d("PPPPPPP", "updateDiagramBarNum: " + w.orgArraySpeedAndLevel.length +","+ i);
                if (isTreadmill) {
                    //非Profile Program 設定 Incline 圖形
                    w.inclineDiagramBarList.add(new DiagramBarBean(BAR_TYPE_INCLINE, i > 0 ? BAR_STATUS_SEGMENT_PENDING : BAR_STATUS_SEGMENT_RUNNING, w.orgArrayIncline[i], w.orgArrayIncline[i], barkCheck, barkTime));
                } else {

                    if (currentProgram != EGYM) {
                        //Bike沒有Incline 給0
                        w.inclineDiagramBarList.add(new DiagramBarBean(BAR_TYPE_INCLINE, i > 0 ? BAR_STATUS_SEGMENT_PENDING : BAR_STATUS_SEGMENT_RUNNING, 0, 0, barkCheck, barkTime));
                    } else {
                        //EGYM存RPM
//                        w.inclineDiagramBarList.add(new DiagramBarBean(BAR_TYPE_INCLINE, i > 0 ? BAR_STATUS_SEGMENT_PENDING : BAR_STATUS_SEGMENT_RUNNING, w.orgArrayIncline[i], w.orgArrayIncline[i], barkCheck, barkTime));

                        //不放初始值
                        w.inclineDiagramBarList.add(new DiagramBarBean(BAR_TYPE_INCLINE, i > 0 ? BAR_STATUS_SEGMENT_PENDING : BAR_STATUS_SEGMENT_RUNNING, 0, 0, barkCheck, barkTime));

                    }
                }

                w.blankDiagramBarList.add(new DiagramBarBean(BAR_TYPE_BLANK, i > 0 ? BAR_STATUS_SEGMENT_NONE : BAR_STATUS_SEGMENT_BLANK, i > 0 ? 0 : diagramBarsView.getBarMaxLevel(), 0, barkCheck, barkTime));
            }

            //設定SUMMARY要顯示的HR圖形
            w.hrList.add(0);
        }


    }

    public void updateDiagramBarNumCadence() {
        int xi = 0;
        for (int i = 0; i < w.orgArrayIncline.length; i++) {
            DiagramBarBean diagramBarInclineBean = w.inclineDiagramBarList.get(i);
            egymDiagramBarsView.setBarLevel(diagramBarInclineBean.getBarType(), xi, diagramBarInclineBean.getBarNum(), w.currentSegment.get(), false);

            xi++;
        }
    }


    boolean isFirst = true;

    public void updateDiagramBarNum(@GENERAL.chartUpdateType int updateType, boolean isFlow) {
        Log.d("WWINNNNNNN", "updateDiagramBarNum: ");

        int xi = 0;
        for (int i = 0; i < w.orgArraySpeedAndLevel.length; i++) {

            DiagramBarBean diagramBarBlankBean = w.blankDiagramBarList.get(i);

            if (updateType == UPDATE_ALL_BAR || updateType == UPDATE_INCLINE_BAR) {
                DiagramBarBean diagramBarInclineBean = w.inclineDiagramBarList.get(i);

                //設定圖時 跳過warmup 和 cooldown
                if (currentProgram != HIIT && diagramBarInclineBean.getBarK() != 0) continue;

                diagramBarsView.setBarLevel(diagramBarInclineBean.getBarType(), xi, diagramBarInclineBean.getBarNum());
                diagramBarsView.setBarColor(diagramBarInclineBean.getBarType(), xi, getBarColorFormStatus(diagramBarInclineBean.getBarType(), diagramBarInclineBean.getBarStatus()));

                if (diagramBarInclineBean.getBarStatus() == BAR_STATUS_SEGMENT_RUNNING) {
                    //xe395是在這裡通知下控更新數值
                    w.currentInclineLevel.set(diagramBarInclineBean.getBarNum());
                    //實際數字incline階數除以2
                    w.currentInclineValue.set(getInclineValue(diagramBarInclineBean.getBarNum()));

                    w.currentFrontInclineAd.set(getInclineAd(w));
                }

                diagramBarsView.setBarLevel(BAR_TYPE_BLANK, xi, diagramBarBlankBean.getBarStatus() == BAR_STATUS_SEGMENT_BLANK ? diagramBarsView.getBarMaxLevel() : 0);
                diagramBarsView.setBarColor(BAR_TYPE_BLANK, xi, R.color.color_bar_blank);
            }

            //SPEED BAR
            if (updateType == UPDATE_ALL_BAR || updateType == UPDATE_SPEED_BAR) {
                DiagramBarBean diagramBarSpeedBean = w.speedDiagramBarList.get(i);

                //設定圖時 跳過warmup 和 cooldown
                if (currentProgram != HIIT && diagramBarSpeedBean.getBarK() != 0) continue;

                diagramBarsView.setBarLevel(diagramBarSpeedBean.getBarType(), xi, diagramBarSpeedBean.getBarNum());
                diagramBarsView.setBarColor(diagramBarSpeedBean.getBarType(), xi, getBarColorFormStatus(diagramBarSpeedBean.getBarType(), diagramBarSpeedBean.getBarStatus()));

                if (diagramBarSpeedBean.getBarStatus() == BAR_STATUS_SEGMENT_RUNNING) {
                    if (isTreadmill) {
                        w.currentSpeedLevel.set(diagramBarSpeedBean.getBarNum());
                        w.currentSpeed.set(getSpeedValue(w.currentSpeedLevel.get()));
                    } else {
//                        if (w.selProgram == WATTS || w.selProgram == FITNESS_TEST) {
//                            w.constantPowerW.set(diagramBarSpeedBean.getBarNum());
//                            w.currentLevel.set(calc.getLevel(w.constantPowerW.get(), w.currentRpm.get()));
//                        } else {
                        if (w.selProgram == WATTS && isFirst) {

                        } else {
                            w.currentLevel.set(diagramBarSpeedBean.getBarNum());
                        }

                        isFirst = false;

                        //    }
                    }
                }

                diagramBarsView.setBarLevel(BAR_TYPE_BLANK, xi, diagramBarBlankBean.getBarStatus() == BAR_STATUS_SEGMENT_BLANK ? diagramBarsView.getBarMaxLevel() : 0);
                diagramBarsView.setBarColor(BAR_TYPE_BLANK, xi, R.color.color_bar_blank);
            }

//            DiagramBarBean diagramBarBlankBean = w.blankDiagramBarList.get(xi);
//            diagramBarsView.setBarLevel(BAR_TYPE_BLANK, xi, diagramBarBlankBean.getBarStatus() == BAR_STATUS_SEGMENT_BLANK ? diagramBarsView.getBarMaxLevel() : 0);
//            diagramBarsView.setBarColor(BAR_TYPE_BLANK, xi, R.color.color_bar_blank);

            xi++;
        }
    }


    /**
     * Warmup Cooldown 不更新圖表
     *
     * @param updateType
     */
    public void updateDiagramBarNum2(@GENERAL.chartUpdateType int updateType) {
        //   for (int i = 0; i < diagramBarsView.getBarCount(); i++) {
        for (int i = 0; i < w.orgArraySpeedAndLevel.length; i++) {

            if (updateType == UPDATE_ALL_BAR || updateType == UPDATE_INCLINE_BAR) {
                DiagramBarBean diagramBarInclineBean = w.inclineDiagramBarList.get(i);
                if (i == w.currentSegment.get()) {
                    //xe395是在這裡通知下控更新數值
                    w.currentInclineLevel.set(diagramBarInclineBean.getBarNum());
                    //實際數字incline階數除以2
                    w.currentInclineValue.set(getInclineValue(diagramBarInclineBean.getBarNum()));

                    w.currentFrontInclineAd.set(getInclineAd(w));
                }
            }

            //SPEED BAR
            if (updateType == UPDATE_ALL_BAR || updateType == UPDATE_SPEED_BAR) {
                DiagramBarBean diagramBarSpeedBean = w.speedDiagramBarList.get(i);
                if (i == w.currentSegment.get()) {
                    if (isTreadmill) {
                        w.currentSpeedLevel.set(diagramBarSpeedBean.getBarNum());
                        w.currentSpeed.set(getSpeedValue(w.currentSpeedLevel.get()));
                    } else {
                        w.currentLevel.set(diagramBarSpeedBean.getBarNum());
                    }
                }
            }
        }
    }


    private void initView() {
        diagramBarsView = getBinding().touchBarView;
    }


    public ChartMsgWindow chartMsgSpeedWindow;
    public ChartMsgWindow chartMsgInclineWindow;
    RxTimer chartInclineTimer;
    private final int NOTIFY_POPUP_DURATION_TIME = 1000;
    RxTimer chartSpeedTimer;

    /**
     * DiagramBarsView 上面的通知
     *
     * @param bar  bar
     * @param type type
     */
    public void showBarMsg(DiagramBarsView.Bar bar, @GENERAL.barType int type) {

        //延遲一下顯示 不然會有問題
        new RxTimer().timer(20, n -> {
            try {
                //還沒切換為 chartsFragment 時會是null
                if (bar.getBarLocation() == null || isChartHidden) return;

                if (appStatusViewModel.currentStatus.get() != AppStatusIntDef.STATUS_RUNNING)
                    return;
                //如果在Media的頁面就不顯示
                if (appStatusViewModel.currentPage.get() != AppStatusIntDef.CURRENT_PAGE_TRAINING)
                    return;

                int x, y;
                int msgWidth;
                if (type == BAR_TYPE_LEVEL_SPEED) {

                    if (chartSpeedTimer != null) {
                        chartSpeedTimer.cancel();
                        chartSpeedTimer = null;
                    }

                    if (chartMsgSpeedWindow == null) {
                        chartMsgSpeedWindow = new ChartMsgWindow(requireActivity(), bar.getLevel(), type, currentProgram);
                        chartMsgSpeedWindow.getContentView().measure(makeDropDownMeasureSpec(chartMsgSpeedWindow.getWidth()), makeDropDownMeasureSpec(chartMsgSpeedWindow.getHeight()));
                        msgWidth = chartMsgSpeedWindow.getContentView().getMeasuredWidth();
                        x = Math.round((bar.getBarLocation().getX() + viewX) - Math.abs(msgWidth - BAR_WIDTH_NORMAL) / 2);
                        y = bar.getBarLocation().getY() + viewY;
                        chartMsgSpeedWindow.showAtLocation(requireActivity().getWindow().getDecorView(), Gravity.START | Gravity.TOP, x, y);

                    } else {
                        chartMsgSpeedWindow.setMsgType(type, bar.getLevel(), currentProgram);

                        msgWidth = chartMsgSpeedWindow.getContentView().getMeasuredWidth();
                        x = Math.round((bar.getBarLocation().getX() + viewX) - Math.abs(msgWidth - BAR_WIDTH_NORMAL) / 2);
                        y = bar.getBarLocation().getY() + viewY;
                        chartMsgSpeedWindow.update(x, y, chartMsgSpeedWindow.getWidth(), chartMsgSpeedWindow.getHeight(), true);

                        chartMsgSpeedWindow.setMsgShow(true);

                    }

                    chartSpeedTimer = new RxTimer();
                    chartSpeedTimer.timer(NOTIFY_POPUP_DURATION_TIME, c -> {
                        if (chartMsgSpeedWindow != null) {
                            chartMsgSpeedWindow.setMsgShow(false);
                        }
                    });
                } else {

                    if (chartInclineTimer != null) {
                        chartInclineTimer.cancel();
                        chartInclineTimer = null;
                    }

                    if (chartMsgInclineWindow == null) {
                        chartMsgInclineWindow = new ChartMsgWindow(requireActivity(), bar.getLevel(), type, currentProgram);
                        chartMsgInclineWindow.getContentView().measure(makeDropDownMeasureSpec(chartMsgInclineWindow.getWidth()), makeDropDownMeasureSpec(chartMsgInclineWindow.getHeight()));
                        msgWidth = chartMsgInclineWindow.getContentView().getMeasuredWidth();
                        x = Math.round((bar.getBarLocation().getX() + viewX) - Math.abs(msgWidth - BAR_WIDTH_NORMAL) / 2);
                        y = bar.getBarLocation().getY() + viewY;
                        chartMsgInclineWindow.showAtLocation(requireActivity().getWindow().getDecorView(), Gravity.START | Gravity.TOP, x, y);

                    } else {
                        chartMsgInclineWindow.setMsgType(type, bar.getLevel(), currentProgram);

                        msgWidth = chartMsgInclineWindow.getContentView().getMeasuredWidth();
                        x = Math.round((bar.getBarLocation().getX() + viewX) - Math.abs(msgWidth - BAR_WIDTH_NORMAL) / 2);
                        y = bar.getBarLocation().getY() + viewY;
                        chartMsgInclineWindow.update(x, y, chartMsgInclineWindow.getWidth(), chartMsgInclineWindow.getHeight(), true);

                        chartMsgInclineWindow.setMsgShow(true);

                    }

                    chartInclineTimer = new RxTimer();
                    chartInclineTimer.timer(NOTIFY_POPUP_DURATION_TIME, c -> {
                        if (chartMsgInclineWindow != null) {
                            chartMsgInclineWindow.setMsgShow(false);
                        }
                    });

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * @param updateInclineNum 調整的階數
     */
    public boolean updateInclineNum(int updateInclineNum) {
        int currentIncline = w.currentInclineLevel.get();
        int newCurrentIncline = currentIncline + (updateInclineNum);

        //   Log.d("@@@@@@@@@@", "updateInclineNum: " + currentIncline + "," + updateInclineNum);
        //ftms 傳同階回成功
        if (currentIncline == newCurrentIncline) {
            parentFragment.ftmsResponse(SET_TARGET_INCLINATION, SUCCESS);
            return false;
        }

        switch (currentProgram) {
            case MANUAL:
            case EGYM:
            case HEART_RATE:
            case AIR_FORCE:
            case ARMY:
            case COAST_GUARD:
            case CTT_PERFORMANCE:
            case CTT_PREDICTION:
            case FITNESS_TEST:
            case GERKIN:
            case PEB:
            case MARINE_CORPS:
            case NAVY:
            case WFI:
            case RUN_5K:
            case RUN_10K:
            case HIIT:

                boolean checkValue = newCurrentIncline <= OPT_SETTINGS.MAX_INC_MAX && newCurrentIncline >= 0;
                if (checkValue) {

                    //MANUAL 當前及之後的值 相同
                    for (int i = 0; i < diagramBarsView.getBarCount(); i++) {
                        DiagramBarBean diagramBean = w.inclineDiagramBarList.get(i);

                        if (diagramBean.getBarStatus() == BAR_STATUS_SEGMENT_RUNNING || diagramBean.getBarStatus() == BAR_STATUS_SEGMENT_PENDING) {
                            diagramBean.setBarNum(diagramBean.getBarNum() + (updateInclineNum));
                        }

                    }

                    //變更圖
//                    updateDiagramBarNum(UPDATE_INCLINE_BAR, false);

                    if (currentProgram == EGYM) {
                        EgymUtil.getInstance().updateInclineNumE(updateInclineNum, egymDiagramBarsView, w, false);
                    } else {
                        updateDiagramBarNum(UPDATE_INCLINE_BAR, false);
                        //Show Notify
                        showNotifyWindow(BAR_TYPE_INCLINE, updateInclineNum);
                        showBarMsg(diagramBarsView.getBar(BAR_TYPE_INCLINE, w.currentSegment.get()), BAR_TYPE_INCLINE);
                    }

                    parentFragment.ftmsResponse(SET_TARGET_INCLINATION, SUCCESS);
                } else {
                    parentFragment.ftmsResponse(SET_TARGET_INCLINATION, INVALID_PARAMETER);
                    return false;
                }

                break;
            case HILL:
            case FATBURN:
            case CARDIO:
            case STRENGTH:
            case CUSTOM:
            case INTERVAL:
                return updateInclineChartByRule(newCurrentIncline, updateInclineNum);
            default:
                return false;
        }
        return true;
    }


    /**
     * 根據公式調整 Incline 的 CHART
     *
     * @param newCurrentIncline newCurrentIncline
     * @param updateInclineNum  updateInclineNum
     * @return b
     */
    private boolean updateInclineChartByRule(int newCurrentIncline, int updateInclineNum) {
        float ccinc = newCurrentIncline == 0 ? 0.5f : newCurrentIncline;
//                //ftms 傳同階回成功
//                if (currentIncline == newCurrentIncline) {
//                    ftmsResponse(SET_TARGET_INCLINATION, INVALID_PARAMETER);
//                    return;
//                }

        //加減後的新 incline 大於30階 小於0 跳出
        if (newCurrentIncline > OPT_SETTINGS.MAX_INC_MAX || newCurrentIncline < 0) {
            parentFragment.ftmsResponse(SET_TARGET_INCLINATION, INVALID_PARAMETER);
            Log.d("IIINNNNCCC", "加減後的新 incline 大於30階 小於0 跳出," + newCurrentIncline);
            return false;
        }

        //按 +，MaxIncline 不可超過30階
        if (w.currentMaxInclineLevel.get() >= OPT_SETTINGS.MAX_INC_MAX && updateInclineNum >= 0) {
            parentFragment.ftmsResponse(SET_TARGET_INCLINATION, INVALID_PARAMETER);
            Log.d("IIINNNNCCC", "按 +，MaxIncline 不可超過30階 " + w.currentMaxInclineLevel.get() + "," + updateInclineNum);
            return false;
        }

        //當前bar的 原始 incline
        float orgIncline = w.inclineDiagramBarList.get(w.currentSegment.get()).getOrgBarNum() == 0 ? 0.5f : w.inclineDiagramBarList.get(w.currentSegment.get()).getOrgBarNum();
        float newMaxIncline = FormulaUtil.getNewMaxNum(orgIncline, ccinc, w.orgMaxInclineInProfile.get());

        Log.d("IIINNNNCCC", "orgIncline: " + orgIncline + ",ccinc:" + ccinc + ",maxIncline:" + "," + w.orgMaxInclineInProfile.get() + ",newMaxIncline:" + newMaxIncline);

        // 算出來的 newMaxLevel 不可 超過30階 或低於 orgMaxLevel
        if (newMaxIncline > OPT_SETTINGS.MAX_INC_MAX) {
            parentFragment.ftmsResponse(SET_TARGET_INCLINATION, INVALID_PARAMETER);
            Log.d("IIINNNNCCC", "算出來的 newMaxLevel 不可 超過30階: " + newMaxIncline);
            return false;
        }

        //檢查算出來的newCurrentLevel，若有一根小於1或大於30階，就調整失敗
        for (int i = 0; i < w.inclineDiagramBarList.size(); i++) {
            DiagramBarBean diagramBean = w.inclineDiagramBarList.get(i);
            if (diagramBean.getBarStatus() == BAR_STATUS_SEGMENT_RUNNING || diagramBean.getBarStatus() == BAR_STATUS_SEGMENT_PENDING) {
                float newPLevel = FormulaUtil.calcCurrentNum(diagramBean.getOrgBarNum() == 0 ? 0.5f : diagramBean.getOrgBarNum(), ccinc, w.inclineDiagramBarList.get(w.currentSegment.get()).getOrgBarNum() == 0 ? 0.5f : w.inclineDiagramBarList.get(w.currentSegment.get()).getOrgBarNum());
                if (newPLevel < 0 || newPLevel > OPT_SETTINGS.MAX_INC_MAX) {
                    parentFragment.ftmsResponse(SET_TARGET_INCLINATION, INVALID_PARAMETER);
                    Log.d("IIINNNNCCC", "檢查算出來的newCurrentLevel，若有一根小於1或大於30階，就調整失敗: " + newPLevel);
                    return false;
                }
            }
        }

        //更新當前及之後的Incline
        for (int i = 0; i < w.inclineDiagramBarList.size(); i++) {
            DiagramBarBean diagramBean = w.inclineDiagramBarList.get(i);
            if (diagramBean.getBarStatus() == BAR_STATUS_SEGMENT_RUNNING || diagramBean.getBarStatus() == BAR_STATUS_SEGMENT_PENDING) {
                int newPIncline = FormulaUtil.calcCurrentNum(diagramBean.getOrgBarNum() == 0 ? 0.5f : diagramBean.getOrgBarNum(), ccinc, w.inclineDiagramBarList.get(w.currentSegment.get()).getOrgBarNum() == 0 ? 0.5f : w.inclineDiagramBarList.get(w.currentSegment.get()).getOrgBarNum());
                diagramBean.setBarNum(newPIncline);
            }
        }

        //更新圖
        //  updateDiagramBarNum(UPDATE_INCLINE_BAR, false);

        if (w.isWarmUpIng.get() || w.isCoolDowning.get()) {
            updateDiagramBarNum2(UPDATE_INCLINE_BAR);
        } else {
            updateDiagramBarNum(UPDATE_INCLINE_BAR, false);

            int barSegment = w.currentSegment.get();
            if (currentProgram.getProgramType() == WorkoutIntDef.PROFILE_PROGRAM && isTreadmill) {
                //Treadmill的PROFILE_PROGRAM 有WarmUp
                barSegment = w.currentSegment.get() - 3;
            }
            showBarMsg(diagramBarsView.getBar(BAR_TYPE_INCLINE, barSegment), BAR_TYPE_INCLINE);
        }

        w.currentMaxInclineLevel.set((int) newMaxIncline);
        w.currentMaxInclineValue.set(getInclineValue(w.currentMaxInclineLevel.get()));

        //Show Notify
        showNotifyWindow(BAR_TYPE_INCLINE, updateInclineNum);
        //  showBarMsg(diagramBarsView.getBar(BAR_TYPE_INCLINE, w.currentSegment.get()), BAR_TYPE_INCLINE);

        parentFragment.ftmsResponse(SET_TARGET_INCLINATION, SUCCESS);

        return true;
    }


    /**
     * @param updateSpeedLevel 調整的階數
     * @return 是否通知下控更新
     */
    public boolean updateSpeedNum(int updateSpeedLevel) {

        //  int currentSpeedLevel = isTreadmill ? w.currentSpeedLevel.get() : Math.round(w.currentLevel.get());
        int currentSpeedLevel;

//        if (w.selProgram == WATTS || w.selProgram == FITNESS_TEST) {
//            currentSpeedLevel = w.constantPowerW.get();
//        } else {
        currentSpeedLevel = isTreadmill ? w.currentSpeedLevel.get() : Math.round(w.currentLevel.get());
        //    }

        int newCurrentSpeedLevel = currentSpeedLevel + (updateSpeedLevel);

        if (newCurrentSpeedLevel <= 0) return false;


        //ftms 同階 回成功
        if (currentSpeedLevel == newCurrentSpeedLevel) {
            parentFragment.ftmsResponse(isTreadmill ? SET_TARGET_SPEED : SET_TARGET_RESISTANCE_LEVEL, SUCCESS);
            return false;
        }

        switch (currentProgram) {
            case MANUAL:
            case EGYM:
            case HEART_RATE:
            case AIR_FORCE:
            case ARMY:
            case COAST_GUARD:
            case CTT_PERFORMANCE:
            case CTT_PREDICTION:
            case FITNESS_TEST:
            case GERKIN:
            case PEB:
            case MARINE_CORPS:
            case NAVY:
            case WFI:
            case WATTS:
            case RUN_5K:
            case RUN_10K:
            case HIIT:
            case CALORIES:
            case STEPS:
            case METS:
            case WINGATE_TEST:
                //調整 SPEED 的 CHART (將還未跑到的Chart全部調整成同樣數值)
                return updateSpeedChart(newCurrentSpeedLevel, updateSpeedLevel);
            case HILL:
            case PLATEAU:
            case FATBURN:
            case CARDIO:
            case STRENGTH:
            case CUSTOM:
            case INTERVAL:
                //根據公式調整 SPEED 的 CHART
                return updateSpeedChartByRule(newCurrentSpeedLevel, updateSpeedLevel);
            default:
                return false;
        }
    }

    /**
     * 根據公式調整 SPEED 的 CHART
     *
     * @param newCurrentSpeedLevel newCurrentSpeedLevel
     * @param updateSpeedLevel     updateSpeedLevel
     * @return b
     */
    private boolean updateSpeedChartByRule(int newCurrentSpeedLevel, int updateSpeedLevel) {
        //加減後的新 speed 大於? 小於? 跳出

        Log.d("CCCHHHCC", "更新後的新 speed:" + newCurrentSpeedLevel + ", 大於 : " + getMaxSpeedLevel(currentProgram) + ",小於:" + getMinSpeedLevel(currentProgram));

        if (newCurrentSpeedLevel > getMaxSpeedLevel(currentProgram) || newCurrentSpeedLevel < getMinSpeedLevel(currentProgram)) {
            parentFragment.ftmsResponse(isTreadmill ? SET_TARGET_SPEED : SET_TARGET_RESISTANCE_LEVEL, INVALID_PARAMETER);
            return false;
        }


        //當前bar的 原始 speed (根據所選擇的MAX 換算過的值)
        //  int orgSpeed = w.speedDiagramBarList.get(w.currentSegment.get()).getOrgBarNum();
        int orgSpeed = w.speedDiagramBarList.get(w.currentSegment.get()).getOrgBarNum();

        //  int orgSpeed = w.isWarmUpIng.get() ? 6 : w.speedDiagramBarList.get(w.currentSegment.get()).getOrgBarNum();

        int newMaxSpeed = FormulaUtil.getNewMaxNum(orgSpeed, newCurrentSpeedLevel, w.orgMaxSpeedInProfile.get());
        //6,31,30,155
        //
        //    Log.d("YYYYYYYYY", "@@@@@@newMaxSpeed: " + newMaxSpeed + "," + orgSpeed + "," + newCurrentSpeedLevel + "," + w.orgMaxSpeedInProfile.get());

        // 算出來的 newMaxSpeed 不可超過?
        if (newMaxSpeed > getMaxSpeedLevel(currentProgram)) {
            parentFragment.ftmsResponse(isTreadmill ? SET_TARGET_SPEED : SET_TARGET_RESISTANCE_LEVEL, INVALID_PARAMETER);
            Log.d("YYYYYYYYY", "算出來的 newMaxSpeed:" + newMaxSpeed + ". 不可超過: " + getMaxSpeedLevel(currentProgram));
            return false;
        }

        //先判斷有沒有超過的
        for (int i = 0; i < w.speedDiagramBarList.size(); i++) {
            DiagramBarBean diagramBean = w.speedDiagramBarList.get(i);
            if (diagramBean.getBarStatus() == BAR_STATUS_SEGMENT_RUNNING || diagramBean.getBarStatus() == BAR_STATUS_SEGMENT_PENDING) {
                int newPLevel = FormulaUtil.calcCurrentNum(diagramBean.getOrgBarNum(), newCurrentSpeedLevel, orgSpeed);

                //     Log.d("YYYYYYYYY", "###檢查算出來的新Speed: " + newPLevel +"," + diagramBean.getOrgBarNum() + "," + newCurrentSpeedLevel +","+ orgSpeed);

                //檢查算出來的新Speed，若有一根大於?，就調整失敗
                //   if (newPLevel > getMaxSpeedLevel(currentProgram) || newPLevel < getMinSpeedLevel(currentProgram) ) {
                if (newPLevel > getMaxSpeedLevel(currentProgram)) {
                    Log.d("YYYYYYYYY", "檢查算出來的新Speed，若有一根大於: " + getMaxSpeedLevel(currentProgram) + "," + newPLevel);
                    //   parent.responseFtmsUpdateIncline(false);
                    return false;
                }
            }
        }

        //更新當前及之後的 SPEED level
        for (int i = 0; i < w.speedDiagramBarList.size(); i++) {
            DiagramBarBean diagramBean = w.speedDiagramBarList.get(i);
            if (diagramBean.getBarStatus() == BAR_STATUS_SEGMENT_RUNNING || diagramBean.getBarStatus() == BAR_STATUS_SEGMENT_PENDING) {

                // calcCurrentNum(arg1,arg2,arg3)
                // [@diagramBean.getOrgBarNum() : 迴圈中每一根Segment的原始值(level)],
                // [@newCurrentSpeedLevel : 變更後的值(level)],
                // [@orgSpeed : 當前Segment的org值(level)]

                int newPLevel = FormulaUtil.calcCurrentNum(diagramBean.getOrgBarNum(), newCurrentSpeedLevel, orgSpeed);
                diagramBean.setBarNum(Math.max(newPLevel, getMinSpeedLevel(currentProgram))); //小於最小值，就帶最小
                //   Log.d("YYYYYYYYY", "updateSpeedChartByRule: 第"+i+", 新：" + newPLevel +", 原始："+ diagramBean.getOrgBarNum() +","+ newCurrentSpeedLevel +","+ orgSpeed +","+ w.orgMaxSpeedInProfile.get());
            }
        }

        if (w.isWarmUpIng.get() || w.isCoolDowning.get()) {
            updateDiagramBarNum2(UPDATE_SPEED_BAR);
        } else {
            updateDiagramBarNum(UPDATE_SPEED_BAR, false);

            int barSegment = w.currentSegment.get();
            if (currentProgram.getProgramType() == WorkoutIntDef.PROFILE_PROGRAM && isTreadmill) {
                //Treadmill的PROFILE_PROGRAM 有WarmUp
                barSegment = w.currentSegment.get() - 3;
            }
            showBarMsg(diagramBarsView.getBar(BAR_TYPE_LEVEL_SPEED, barSegment), BAR_TYPE_LEVEL_SPEED);
        }

        w.currentMaxSpeedLevel.set(newMaxSpeed);
        w.currentMaxSpeed.set(isTreadmill ? getSpeedValue(w.currentMaxSpeedLevel.get()) : w.currentMaxSpeedLevel.get());

        //Show Notify
        showNotifyWindow(BAR_TYPE_LEVEL_SPEED, updateSpeedLevel);

        parentFragment.ftmsResponse(isTreadmill ? SET_TARGET_SPEED : SET_TARGET_RESISTANCE_LEVEL, SUCCESS);

        return true;
    }

    /**
     * 調整 SPEED 的 CHART (將還未跑到的Chart全部調整成同樣數值)
     *
     * @param newCurrentSpeedLevel newCurrentSpeedLevel
     * @param updateSpeedLevel     updateSpeedLevel
     * @return b
     */
    private boolean updateSpeedChart(int newCurrentSpeedLevel, int updateSpeedLevel) {
        boolean checkValue = newCurrentSpeedLevel <= getMaxSpeedLevel(currentProgram) && newCurrentSpeedLevel >= getMinSpeedLevel(currentProgram);
        //     Log.d("#####$$$$$", "updateSpeedChart: " + newCurrentSpeedLevel + "," + updateSpeedLevel + "," + checkValue);

        if (currentProgram == WATTS || currentProgram == FITNESS_TEST) checkValue = true;

        if (checkValue) {

            //HIIT RULE
            if (currentProgram == HIIT) {
                if (w.isCoolDowning.get() || w.isWarmUpIng.get()) {
                    if (isTreadmill) {
                        w.currentSpeedLevel.set(w.currentSpeedLevel.get() + updateSpeedLevel);
                        w.currentSpeed.set(getSpeedValue(w.currentSpeedLevel.get()));
                    } else {
                        w.currentLevel.set(w.currentLevel.get() + updateSpeedLevel);
                    }
                    return true;
                }
            }

            int hiitCurrentBar = 0;
            //MANUAL 當前及之後Segment的CurrentSpeed 變一樣
            for (int i = 0; i < diagramBarsView.getBarCount(); i++) {
                DiagramBarBean diagramBean = w.speedDiagramBarList.get(i);
                if (diagramBean.getBarStatus() == BAR_STATUS_SEGMENT_RUNNING || diagramBean.getBarStatus() == BAR_STATUS_SEGMENT_PENDING) {
                    int newNum = diagramBean.getBarNum() + updateSpeedLevel;
                    if (currentProgram != HIIT) {
                        if (currentProgram == WATTS || currentProgram == FITNESS_TEST) {
                            diagramBean.setBarNum(calc.getLevel(w.constantPowerW.get(), w.currentRpm.get()));
                        } else {
                            diagramBean.setBarNum(newNum);
                        }
                    } else {
                        //HIIT RULE
                        if (diagramBean.getBarStatus() == BAR_STATUS_SEGMENT_RUNNING) {
                            hiitCurrentBar = diagramBean.getBarK();//當前的bar是 Sprint or rest
                        }
                        if (diagramBean.getBarK() == hiitCurrentBar) {
                            //只改變Sprint or rest的bar
//
                            if (hiitCurrentBar == HIIT_SPRINT_BAR) {

                                //不可小於等於rest
                                if (newNum <= w.selRestSpeedLevel.get()) return false;

                                w.selSprintSpeedLevel.set(newNum);
                            } else {
                                //不可大於等於sprint
                                if (newNum >= w.selSprintSpeedLevel.get()) return false;

                                w.selRestSpeedLevel.set(newNum);
                            }

                            diagramBean.setBarNum(newNum);
                        }
                    }
                }
            }

            //更新圖

            if (currentProgram == EGYM) {
                EgymUtil.getInstance().updateSpeedNumE(updateSpeedLevel, egymDiagramBarsView, w, false);
            } else {
                updateDiagramBarNum(UPDATE_SPEED_BAR, false);
            }

            //最大值 MAX_SPD_IU_MAX  MAX_SPD_MU_MAX
            float currentMaxSpeed = isTreadmill ? deviceSettingViewModel.unitCode.get() == IMPERIAL ? getSpeedValue(OPT_SETTINGS.MAX_SPD_IU_MAX) : getSpeedValue(OPT_SETTINGS.MAX_SPD_MU_MAX) : MAX_LEVEL_MAX;
//            if (currentProgram == WATTS || currentProgram == FITNESS_TEST) {
//                currentMaxSpeed = POWER_MAX;
//            }
            w.currentMaxSpeed.set(currentMaxSpeed);

            //Show Notify
            if (parentFragment != null) {

                showNotifyWindow(BAR_TYPE_LEVEL_SPEED, updateSpeedLevel);

                showBarMsg(diagramBarsView.getBar(BAR_TYPE_LEVEL_SPEED, w.currentSegment.get()), BAR_TYPE_LEVEL_SPEED);

                parentFragment.ftmsResponse(isTreadmill ? SET_TARGET_SPEED : SET_TARGET_RESISTANCE_LEVEL, SUCCESS);
            }
        } else {
            parentFragment.ftmsResponse(isTreadmill ? SET_TARGET_SPEED : SET_TARGET_RESISTANCE_LEVEL, INVALID_PARAMETER);
            return false;
        }

        return true;
    }

    /**
     * 顯示Speed or Incline改變的通知視窗
     *
     * @param type      type
     * @param updateNum n
     */
    private void showNotifyWindow(@GENERAL.barType int type, int updateNum) {

        if (type == BAR_TYPE_INCLINE) {
            //    Log.d("VVCCSS", "showNotifyWindow: " + appStatusViewModel.currentPage.get());
            //上面的在Media時才顯示
            if ((appStatusViewModel.currentPage.get() == AppStatusIntDef.CURRENT_PAGE_MEDIA || appStatusViewModel.isMediaPlaying.get()) && w.selProgram != HEART_RATE) {
                int drawableRes2 = updateNum > 0 ? R.drawable.bg_control_center_increase : R.drawable.bg_control_center_decrease;
                View showView2 = parent.getBinding().vTopIncline;
                parent.showNotifyWindow(drawableRes2, showView2, w.currentInclineValue.get(), w.currentMaxInclineValue.get(), true, type);
            }

            //disabled不顯示
            if (w.disabledInclineUpdate.get()) return;

            //左右的在
            if ((appStatusViewModel.currentPage.get() == AppStatusIntDef.CURRENT_PAGE_TRAINING && !appStatusViewModel.isMediaPlaying.get()) && w.selProgram != HEART_RATE) {

                if (w.selProgram == ProgramsEnum.GERKIN) return;

                int drawableRes1 = updateNum > 0 ? R.drawable.bg_control_left_increase : R.drawable.bg_control_left_decrease;
//                View showView1 = updateNum > 0 ? parentFragment.getBinding().btnWorkoutInclinePlus : parentFragment.getBinding().btnWorkoutInclineMinus;


                View showView1 = updateNum > 0 ? (isUs ? parentFragment.getBinding().btnWorkoutInclinePlusUs : parentFragment.getBinding().btnWorkoutInclinePlus) :
                        (isUs ? parentFragment.getBinding().btnWorkoutInclineMinusUs : parentFragment.getBinding().btnWorkoutInclineMinus);

                parent.showNotifyWindow(drawableRes1, showView1, w.currentInclineValue.get(), w.currentMaxInclineValue.get(), true, type);
            }

        } else {


            //上面的在Media時才顯示
            if ((appStatusViewModel.currentPage.get() == AppStatusIntDef.CURRENT_PAGE_MEDIA || appStatusViewModel.isMediaPlaying.get())
                    || (currentProgram == HEART_RATE && !isTreadmill) || currentProgram == FITNESS_TEST) {

                int drawableRes2 = updateNum > 0 ? R.drawable.bg_control_center_increase : R.drawable.bg_control_center_decrease;
                View showView2 = isTreadmill ? parent.getBinding().vTopSpeed : parent.getBinding().vTopLevelBike;
                //    float value = isTreadmill ? w.currentSpeed.get() : w.currentLevel.get();

                float value, valueMax;
                if (w.selProgram == WATTS || w.selProgram == FITNESS_TEST) {
                    value = w.constantPowerW.get();
                    valueMax = POWER_MAX;
                } else {
                    value = isTreadmill ? w.currentSpeed.get() : w.currentLevel.get();
                    valueMax = w.currentMaxSpeed.get();
                }

                parent.showNotifyWindow(drawableRes2, showView2, value, valueMax, isTreadmill, type);
            }

            //disabled不顯示
            if (w.disabledSpeedUpdate.get()) return;

            //speed & level
            //左右的
            boolean isBikeAndHeartRate = (!isTreadmill && currentProgram == HEART_RATE);

            if ((appStatusViewModel.currentPage.get() == AppStatusIntDef.CURRENT_PAGE_TRAINING && !appStatusViewModel.isMediaPlaying.get())
                    && (!isBikeAndHeartRate) && currentProgram != FITNESS_TEST) {

                if (w.selProgram == ProgramsEnum.GERKIN) return;

                //LEVEL 和 SPEED

                int drawableRes1;
                if (isUs) {
                    drawableRes1 = updateNum > 0 ? (isTreadmill ? R.drawable.bg_control_right_increase : R.drawable.bg_control_left_increase) :
                            (isTreadmill ? R.drawable.bg_control_right_decrease : R.drawable.bg_control_left_decrease);
                } else {
                    drawableRes1 = updateNum > 0 ? R.drawable.bg_control_right_increase : R.drawable.bg_control_right_decrease;
                }

                View showView1 = isTreadmill ?
                        (updateNum > 0 ? (isUs ? parentFragment.getBinding().btnWorkoutSpeedPlusUs : parentFragment.getBinding().btnWorkoutSpeedPlus) :
                                (isUs ? parentFragment.getBinding().btnWorkoutSpeedMinusUs : parentFragment.getBinding().btnWorkoutSpeedMinus)) :
                        //BIKE
//                        (updateNum > 0 ? parentFragment.getBinding().btnBikeLevelPlus : parentFragment.getBinding().btnBikeLevelMinus);

                        (updateNum > 0 ? (isUs ? parentFragment.getBinding().btnBikeLevelPlusUs : parentFragment.getBinding().btnBikeLevelPlus)
                                : isUs ? parentFragment.getBinding().btnBikeLevelMinusUs : parentFragment.getBinding().btnBikeLevelMinus);


                //float value = isTreadmill ? w.currentSpeed.get() : w.currentLevel.get();
                float value, valueMax;
                if (w.selProgram == WATTS || w.selProgram == FITNESS_TEST) {
                    value = w.constantPowerW.get();
                    valueMax = POWER_MAX;
                } else {
                    value = isTreadmill ? w.currentSpeed.get() : w.currentLevel.get();
                    valueMax = w.currentMaxSpeed.get();
                }

                parent.showNotifyWindow(drawableRes1, showView1, value, valueMax, isTreadmill, type);
            }
        }
    }

    public void hrNotify(boolean isPlus) {
        if (appStatusViewModel.currentPage.get() == AppStatusIntDef.CURRENT_PAGE_TRAINING) {

//            int drawableRes1 = isPlus ? R.drawable.bg_control_left_increase : R.drawable.bg_control_left_decrease;

            int drawableRes1 = isPlus ? (isTreadmill ? R.drawable.bg_control_left_increase : R.drawable.bg_control_right_increase) : (isTreadmill ? R.drawable.bg_control_left_decrease : R.drawable.bg_control_right_decrease);

            if (isUs & !isTreadmill) {
                drawableRes1 = isPlus ? R.drawable.bg_control_left_increase : R.drawable.bg_control_left_decrease;
            }

//            View showView1 = isPlus ? parentFragment.getBinding().btnWorkoutInclinePlus : parentFragment.getBinding().btnWorkoutInclineMinus;


            //   View showView1 = isPlus ? (isUs ? parentFragment.getBinding().btnWorkoutInclinePlusUs : parentFragment.getBinding().btnWorkoutInclinePlus) :  (isUs ? parentFragment.getBinding().btnWorkoutInclineMinusUs : parentFragment.getBinding().btnWorkoutInclineMinus);


            View showView1;
            if (isUs) {
                showView1 = isPlus ? (isTreadmill ? parentFragment.getBinding().btnWorkoutInclinePlusUs : parentFragment.getBinding().btnBikeLevelPlusUs) : (isTreadmill ? parentFragment.getBinding().btnWorkoutInclineMinusUs : parentFragment.getBinding().btnBikeLevelMinusUs);
            } else {
                showView1 = isPlus ? (isTreadmill ? parentFragment.getBinding().btnWorkoutInclinePlus : parentFragment.getBinding().btnBikeLevelPlus) : (isTreadmill ? parentFragment.getBinding().btnWorkoutInclineMinus : parentFragment.getBinding().btnBikeLevelMinus);
            }


            Log.d("USSSSSS", "showNotifyWindow:########### ");
            parent.showNotifyWindow(drawableRes1, showView1, w.selTargetHrBpm.get(), 200, true, BAR_TYPE_INCLINE);
        }
    }

//    private void ftmsResponse(DeviceGEM.EQUIPMENT_CONTROL_OPERATION controlOperation, DeviceGEM.EQUIPMENT_CONTROL_RESPONSE equipmentControlResponse) {
//        Map<DeviceGEM.EQUIPMENT_CONTROL_PARAMETER, Integer> parameters = new HashMap<>();
//        App.getDeviceGEM().gymConnectMessageNotifyEquipmentControlMessageReceived(controlOperation, equipmentControlResponse, parameters);
//    }

    private int getBarColorFormStatus(@GENERAL.barType int barType, @GENERAL.barStatus int status) {
        int res;
        if (status == BAR_STATUS_SEGMENT_PENDING) {
            res = barType == BAR_TYPE_INCLINE ? R.color.color_incline_bar_end : R.color.color_level_speed_bar_end;
            if (currentProgram == WINGATE_TEST) {
                res = R.color.color_watt_bar_end;
            }
        } else if (status == BAR_STATUS_SEGMENT_RUNNING) {
            res = barType == BAR_TYPE_INCLINE ? R.color.color_incline_bar_run : R.color.color_level_speed_bar_run;
            if (currentProgram == WINGATE_TEST) {
                res = R.color.color_watt_bar_run;
            }
        } else {
            res = barType == BAR_TYPE_INCLINE ? R.color.color_incline_bar_run : R.color.color_level_speed_bar_run;

            if (currentProgram == WINGATE_TEST) {
                res = R.color.color_watt_bar_run;
            }
        }
        return res;
    }

    private void onSelect() {
        getBinding().btnStats.setOnClickListener(v -> {
            dismissBar();
            LiveEventBus.get(SELECT_WORKOUT_PAGE).post(WORKOUT_PAGE_STATS);
        });
        getBinding().btnTrack.setOnClickListener(v -> {
            dismissBar();
            LiveEventBus.get(SELECT_WORKOUT_PAGE).post(WORKOUT_PAGE_TRACK);
        });
        getBinding().btnGarmin.setOnClickListener(v -> {
            dismissBar();
            LiveEventBus.get(SELECT_WORKOUT_PAGE).post(WORKOUT_PAGE_GARMIN);
        });
    }

    public void dismissBar() {
        if (chartMsgSpeedWindow != null && chartMsgSpeedWindow.isShowing()) {
            chartMsgSpeedWindow.setMsgShow(false);
        }

        if (chartMsgInclineWindow != null && chartMsgInclineWindow.isShowing()) {
            chartMsgInclineWindow.setMsgShow(false);
        }

        if (currentProgram == GERKIN) {
            if (chartMsgStageWindow != null && chartMsgStageWindow.isShowing()) {
                chartMsgStageWindow.setMsgShow(false);
            }
        }
    }

    /**
     * Segment隨時間變化
     */
    public int timeTick;

    public void segmentFlow(long sec) {
        //第一次的速度從 MainWorkoutTrainingFragment checkInit() 設定
        if (sec == 1) {
            showStageWindow();
            return;
        }

        if (currentProgram == EGYM) return;

        //HIIT Program
        if (currentProgram == HIIT) {
            for (int i = 0; i < w.speedDiagramBarList.size(); i++) {
                if (w.speedDiagramBarList.get(i).getTimeK() == sec) {
                    flow();
                    return;
                }
            }
            return;
        }

        //其他 Program
//        if ((sec - 1) % timeTick == 0) { //1分1秒跳
//            flow();
//        }

        if (currentProgram == FITNESS_TEST) {
            if (sec == timeTick) {
                flow();
            }
        } else {
            if (sec % timeTick == 0) { //1分鐘整跳
                if (currentProgram.getProgramType() == WorkoutIntDef.PROFILE_PROGRAM && isTreadmill) {
                    flow2();
                } else {
                    //EGYM & MANUAL
                    flow();
                }
            }
        }
    }

    public void flow() {

        for (int i = 0; i < diagramBarsView.getBarCount(); i++) {
            //把當前狀態是Running的Segment更新為Finish
            if (w.speedDiagramBarList.get(i).getBarStatus() == BAR_STATUS_SEGMENT_RUNNING) {
                w.speedDiagramBarList.get(i).setBarStatus(BAR_STATUS_SEGMENT_FINISH);
                w.inclineDiagramBarList.get(i).setBarStatus(BAR_STATUS_SEGMENT_FINISH);
                w.blankDiagramBarList.get(i).setBarStatus(BAR_STATUS_SEGMENT_NONE);

                w.hrList.set(i, w.currentHeartRate.get());

                w.rpmList.add(i, w.currentRpm.get());
                w.metsList.add(i, w.currentMets.get());


                if (currentProgram == ProgramsEnum.EGYM && !isTreadmill) {
                    //EGYM 存RPM
                    w.inclineDiagramBarList.get(i).setBarNum(w.currentRpm.get());
                    //     Log.d("EEEQQQDDDAAA", "flow: " + w.currentRpm.get());
                }

                //更新下一筆Segment的狀態，除非當前是最後一筆
                if (i != (diagramBarsView.getBarCount() - 1)) {
                    w.speedDiagramBarList.get(i + 1).setBarStatus(BAR_STATUS_SEGMENT_RUNNING);
                    w.inclineDiagramBarList.get(i + 1).setBarStatus(BAR_STATUS_SEGMENT_RUNNING);
                    w.blankDiagramBarList.get(i + 1).setBarStatus(BAR_STATUS_SEGMENT_BLANK);

                    //@當前Segment(換到第2條時才會加1)
                    w.currentSegment.set(i + 1);

                    //      Log.d("KKKKEEEE", "FLOW: " + w.currentSegment.get());
                    if (!isTreadmill) {
                        w.totalLevel.set(w.totalLevel.get() + w.currentLevel.get());
                        w.avgLevel.set(w.totalLevel.get() / w.currentSegment.get());
                    }

                    //存Gerkin 的 stage
                    if (currentProgram == GERKIN) {
                        w.currentStage.set(WorkoutUtil.setStage(w.currentSegment.get()));
                        showStageWindow();
                    }
                } else {
                    //最後一筆,如果是上數就重來
                    if (w.selWorkoutTime.get() == UNLIMITED) { //上數

//                        if (currentProgram == GERKIN) {
//                            //gerkin不重新
//                            w.currentSegment.set(BAR_COUNT_GERKIN);
//                        } else {
//                            //上數的跑到最後的Segment，重新開始
                        repeatDiagram();
                        //     }
                    }
                }

                if (currentProgram == WINGATE_TEST) {
                    updateDiagramWatt(UPDATE_SPEED_BAR,true);
                } else {
                    updateDiagramBarNum(isTreadmill ? UPDATE_ALL_BAR : UPDATE_SPEED_BAR, true);
                }


                if (isTreadmill) {

                    //與上一根的值一樣，就不要再通知下控更新
                    if (i != (diagramBarsView.getBarCount() - 1)) {

//                        Log.d("EEDEDEDE", "上一個incline1: " + w.inclineDiagramBarList.get(i).getBarNum());
//                        Log.d("EEDEDEDE", "當前incline2: " + w.inclineDiagramBarList.get(i + 1).getBarNum());
//
//                        Log.d("EEDEDEDE", "上一個speed1: " + w.speedDiagramBarList.get(i).getBarNum());
//                        Log.d("EEDEDEDE", "當前speed2: " + w.speedDiagramBarList.get(i + 1).getBarNum());

                        if ((w.inclineDiagramBarList.get(i).getBarNum() == w.inclineDiagramBarList.get(i + 1).getBarNum())
                                && w.speedDiagramBarList.get(i).getBarNum() == w.speedDiagramBarList.get(i + 1).getBarNum()) {
                            return;
                        }

                        //  Log.d("EEDEDEDE", "@@@@@@@@flow: 需要更新第" + (i + 1 + 1) + "根");
                        parentFragment.u.setDevSpeedAndIncline();
                    }

                } else {

                    //與上一根的值一樣，就不要再通知下控更新
                    if (i != (diagramBarsView.getBarCount() - 1)) {
                        if (w.speedDiagramBarList.get(i).getBarNum() == w.speedDiagramBarList.get(i + 1).getBarNum()) {
                            return;
                        }

                        parentFragment.u.setDevWorkload(w.currentLevel.get());
                    }
                }
                //只更新一筆
                return;
            }
        }

    }

    /**
     * Diagram重頭開始
     */
    private void repeatDiagram() {

        //Gerkin有44個Segment
        int barCount = currentProgram != GERKIN ? BAR_COUNT_NORMAL : BAR_COUNT_GERKIN;

        //MANUAL - 全部跟最後一個Segment一樣
        if (currentProgram == MANUAL) {
            int lastSpeedProgress = w.speedDiagramBarList.get(barCount - 1).getBarNum();
            int lastInclineProgress = 0;
            if (isTreadmill) {
                lastInclineProgress = w.inclineDiagramBarList.get(barCount - 1).getBarNum();
            }

            for (int i = 0; i < barCount; i++) {
                w.blankDiagramBarList.get(i).setBarStatus(i == 0 ? BAR_STATUS_SEGMENT_BLANK : BAR_STATUS_SEGMENT_NONE);
                DiagramBarBean diagramBeanSpeed = w.speedDiagramBarList.get(i);
                diagramBeanSpeed.setBarStatus(i == 0 ? BAR_STATUS_SEGMENT_RUNNING : BAR_STATUS_SEGMENT_PENDING);
                diagramBeanSpeed.setBarNum(lastSpeedProgress);

                if (isTreadmill) {
                    DiagramBarBean diagramBeanIncline = w.inclineDiagramBarList.get(i);
                    diagramBeanIncline.setBarStatus(i == 0 ? BAR_STATUS_SEGMENT_RUNNING : BAR_STATUS_SEGMENT_PENDING);
                    diagramBeanIncline.setBarNum(lastInclineProgress);
                }
            }

        } else {

            for (int i = 0; i < barCount; i++) {
                w.blankDiagramBarList.get(i).setBarStatus(i == 0 ? BAR_STATUS_SEGMENT_BLANK : BAR_STATUS_SEGMENT_NONE);
                DiagramBarBean diagramBeanSpeed = w.speedDiagramBarList.get(i);
                diagramBeanSpeed.setBarStatus(i == 0 ? BAR_STATUS_SEGMENT_RUNNING : BAR_STATUS_SEGMENT_PENDING);
                if (isTreadmill) {
                    DiagramBarBean diagramBeanIncline = w.inclineDiagramBarList.get(i);
                    diagramBeanIncline.setBarStatus(i == 0 ? BAR_STATUS_SEGMENT_RUNNING : BAR_STATUS_SEGMENT_PENDING);
                }
            }
        }

        w.repeatCount += 1;
    }


    private void flow2() {

        for (int i = 3; i < 23; i++) {
            //把當前狀態是Running的Segment更新為Finish
            if (w.speedDiagramBarList.get(i).getBarStatus() == BAR_STATUS_SEGMENT_RUNNING) {
                w.speedDiagramBarList.get(i).setBarStatus(BAR_STATUS_SEGMENT_FINISH);
                w.inclineDiagramBarList.get(i).setBarStatus(BAR_STATUS_SEGMENT_FINISH);
                w.blankDiagramBarList.get(i).setBarStatus(BAR_STATUS_SEGMENT_NONE);

                w.hrList.set(i, w.currentHeartRate.get());

                w.rpmList.add(i, w.currentRpm.get());
                w.metsList.add(i, w.currentMets.get());

                //更新下一筆Segment的狀態，除非當前是最後一筆
                if (i != (23 - 1)) {
                    w.speedDiagramBarList.get(i + 1).setBarStatus(BAR_STATUS_SEGMENT_RUNNING);
                    w.inclineDiagramBarList.get(i + 1).setBarStatus(BAR_STATUS_SEGMENT_RUNNING);
                    w.blankDiagramBarList.get(i + 1).setBarStatus(BAR_STATUS_SEGMENT_BLANK);

                    //#當前Segment(換到第2條時才會加1)
                    w.currentSegment.set(i + 1);
                    if (!isTreadmill) {
                        w.totalLevel.set(w.totalLevel.get() + w.currentLevel.get());
                        w.avgLevel.set(w.totalLevel.get() / w.currentSegment.get());
                        Log.d("PPEEFFFF", "flow2: " + w.totalLevel.get() + "," + w.currentLevel.get() + "," + w.avgLevel.get());
                    }

                } else {
                    if (w.selWorkoutTime.get() == UNLIMITED) { //上數
                        for (int i2 = 3; i2 < 23; i2++) {
                            w.blankDiagramBarList.get(i2).setBarStatus(i2 == 3 ? BAR_STATUS_SEGMENT_BLANK : BAR_STATUS_SEGMENT_NONE);
                            DiagramBarBean diagramBeanSpeed = w.speedDiagramBarList.get(i2);
                            diagramBeanSpeed.setBarStatus(i2 == 3 ? BAR_STATUS_SEGMENT_RUNNING : BAR_STATUS_SEGMENT_PENDING);
                            DiagramBarBean diagramBeanIncline = w.inclineDiagramBarList.get(i2);
                            diagramBeanIncline.setBarStatus(i2 == 3 ? BAR_STATUS_SEGMENT_RUNNING : BAR_STATUS_SEGMENT_PENDING);
                        }
                        w.repeatCount += 1;
                    }
                }

                updateDiagramBarNum(isTreadmill ? UPDATE_ALL_BAR : UPDATE_SPEED_BAR, true);


                parentFragment.u.setDevSpeedAndIncline();


                //只更新一筆
                return;
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (chartMsgSpeedWindow != null && chartMsgSpeedWindow.isShowing()) {
            chartMsgSpeedWindow.dismiss();
        }
        chartMsgSpeedWindow = null;
        if (chartSpeedTimer != null) {
            chartSpeedTimer.cancel();
            chartSpeedTimer = null;
        }


        if (chartMsgInclineWindow != null && chartMsgInclineWindow.isShowing()) {
            chartMsgInclineWindow.dismiss();
        }
        chartMsgInclineWindow = null;
        if (chartInclineTimer != null) {
            chartInclineTimer.cancel();
            chartInclineTimer = null;
        }

        if (chartMsgStageWindow != null) {
            chartMsgStageWindow.dismiss();
            chartMsgStageWindow = null;
        }


        w.currentRpm.removeOnPropertyChangedCallback(rpmCallback);
    }


    //GERKIN STAGE
    private void showStageWindow() {
        try {

            if (currentProgram == GERKIN) {
                if (chartMsgStageWindow != null) {
                    chartMsgStageWindow.dismiss();
                    chartMsgStageWindow = null;
                }
                DiagramBarsView.Bar bar = diagramBarsView.getBar(BAR_TYPE_BLANK, w.currentSegment.get());
                chartMsgStageWindow = new ChartMsgWindow(requireActivity(), w.currentStage.get(), BAR_TYPE_BLANK, currentProgram);
                chartMsgStageWindow.getContentView().measure(makeDropDownMeasureSpec(chartMsgStageWindow.getWidth()), makeDropDownMeasureSpec(chartMsgStageWindow.getHeight()));
                int msgWidth = chartMsgStageWindow.getContentView().getMeasuredWidth();


                int x = isUs && !isGGG ? 300 : 242;
                if (bar.getBarLocation() != null) {
                    x = Math.round((bar.getBarLocation().getX() + viewX) - Math.abs(msgWidth - BAR_WIDTH_NORMAL) / 2) - 14;
                }
                int y = (-45) + viewY;
                chartMsgStageWindow.showAtLocation(requireActivity().getWindow().getDecorView(), Gravity.START | Gravity.TOP, x, y);
                chartMsgStageWindow.setMsgShow(!isChartHidden);

                //   Log.d("##FFFFVVVVV", "GERKIN STAGE:" + w.currentStage.get() + ", x:" +x + ", y:" + y  );
            }
        } catch (Exception e) {
            Log.d("##FFFFVVVVV", "exception: " + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public static boolean isChartHidden = true;

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        isChartHidden = hidden;
        if (!hidden) {
            if (currentProgram == GERKIN) {
                if (chartMsgStageWindow != null) {
                    chartMsgStageWindow.setMsgShow(true);
                }
            }
        } else {

            if (currentProgram == EGYM) {
                if (egymDiagramBarsView != null) {
                    egymDiagramBarsView.dismissPopupText();
                }
            }

            Log.d("PPPOOEEEE", "hidden: " + hidden);
        }
    }


    public void updateWattChart(int updateWatt) {
//        Log.d("WWINNNNNNN", "11111updateWattChart: " + updateWatt);
        if (updateWatt == w.currentPower.get()) return;

        if (updateWatt <= 0 || updateWatt >= 1200) return;


        w.currentPower.set(updateWatt);
        Log.d("WWINNNNNNN", "222222updateWattChart: " + updateWatt);


        //MANUAL 當前及之後Segment的CurrentSpeed 變一樣
        for (int i = 0; i < diagramBarsView.getBarCount(); i++) {
            DiagramBarBean diagramBean = w.speedDiagramBarList.get(i);
            if (diagramBean.getBarStatus() == BAR_STATUS_SEGMENT_RUNNING || diagramBean.getBarStatus() == BAR_STATUS_SEGMENT_PENDING) {
                diagramBean.setBarNum(updateWatt);

            }
        }

        //更新圖

        updateDiagramWatt(UPDATE_SPEED_BAR, false);
        //Show Notify
        showBarMsg(diagramBarsView.getBar(BAR_TYPE_LEVEL_SPEED, w.currentSegment.get()), BAR_TYPE_LEVEL_SPEED);

    }


    public void updateDiagramWatt(@GENERAL.chartUpdateType int updateType, boolean isFlow) {


        int xi = 0;
        for (int i = 0; i < w.orgArraySpeedAndLevel.length; i++) {

            DiagramBarBean diagramBarBlankBean = w.blankDiagramBarList.get(i);

            DiagramBarBean diagramBarInclineBean = w.inclineDiagramBarList.get(i);

            //設定圖時 跳過warmup 和 cooldown
            if (currentProgram != HIIT && diagramBarInclineBean.getBarK() != 0) continue;

            diagramBarsView.setBarLevel(diagramBarInclineBean.getBarType(), xi, diagramBarInclineBean.getBarNum());
            diagramBarsView.setBarColor(diagramBarInclineBean.getBarType(), xi, getBarColorFormStatus(diagramBarInclineBean.getBarType(), diagramBarInclineBean.getBarStatus()));

            diagramBarsView.setBarLevel(BAR_TYPE_BLANK, xi, diagramBarBlankBean.getBarStatus() == BAR_STATUS_SEGMENT_BLANK ? diagramBarsView.getBarMaxLevel() : 0);
            diagramBarsView.setBarColor(BAR_TYPE_BLANK, xi, R.color.color_bar_blank);

            //SPEED BAR
            DiagramBarBean diagramBarSpeedBean = w.speedDiagramBarList.get(i);

            //設定圖時 跳過warmup 和 cooldown
            if (currentProgram != HIIT && diagramBarSpeedBean.getBarK() != 0) continue;

            diagramBarsView.setBarLevel(diagramBarSpeedBean.getBarType(), xi, diagramBarSpeedBean.getBarNum());
            diagramBarsView.setBarColor(diagramBarSpeedBean.getBarType(), xi, getBarColorFormStatus(diagramBarSpeedBean.getBarType(), diagramBarSpeedBean.getBarStatus()));



            diagramBarsView.setBarLevel(BAR_TYPE_BLANK, xi, diagramBarBlankBean.getBarStatus() == BAR_STATUS_SEGMENT_BLANK ? diagramBarsView.getBarMaxLevel() : 0);
            diagramBarsView.setBarColor(BAR_TYPE_BLANK, xi, R.color.color_bar_blank);

            xi++;
        }
    }


}