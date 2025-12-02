package com.dyaco.spirit_commercial.workout.programs;

import static com.dyaco.spirit_commercial.App.MODE;
import static com.dyaco.spirit_commercial.App.UNIT_E;
import static com.dyaco.spirit_commercial.MainActivity.isTreadmill;
import static com.dyaco.spirit_commercial.MainActivity.isUs;
import static com.dyaco.spirit_commercial.support.FormulaUtil.km2mi;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.getMinSpeedLevel;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.getSpeedValue;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.setBikeLevelDiagram;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.setMinSpeedDiagram;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.setTreadmillInclineDiagram;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.setTreadmillSpeedPresentDiagram;
import static com.dyaco.spirit_commercial.support.intdef.WorkoutIntDef.UNLIMITED;
import static com.dyaco.spirit_commercial.workout.programs.ProgramsEnum.EGYM;
import static com.dyaco.spirit_commercial.workout.programs.ProgramsEnum.MANUAL;

import android.util.Log;
import android.view.View;

import com.dyaco.spirit_commercial.UartConsoleManagerPF;
import com.dyaco.spirit_commercial.model.webapi.bean.EgymTrainingPlans;
import com.dyaco.spirit_commercial.product_flavor.ModeEnum;
import com.dyaco.spirit_commercial.support.intdef.DeviceIntDef;
import com.dyaco.spirit_commercial.viewmodel.EgymDataViewModel;
import com.dyaco.spirit_commercial.viewmodel.WorkoutViewModel;
import com.dyaco.spirit_commercial.workout.MainWorkoutTrainingFragment;
import com.dyaco.spirit_commercial.workout.WorkoutChartsFragment;
import com.dyaco.spirit_commercial.workout.WorkoutStatsFragment;

import java.util.Arrays;
import java.util.IntSummaryStatistics;

public class CommonPrograms implements IPrograms {

    WorkoutViewModel w;
    EgymDataViewModel e;
    MainWorkoutTrainingFragment m;
    WorkoutChartsFragment c;
    UartConsoleManagerPF u;

    public CommonPrograms(WorkoutViewModel workoutViewModel, MainWorkoutTrainingFragment mainWorkoutTrainingFragment, WorkoutChartsFragment c, UartConsoleManagerPF u, EgymDataViewModel e) {
        this.w = workoutViewModel;
        this.m = mainWorkoutTrainingFragment;
        this.c = c;
        this.u = u;
        this.e = e;
    }

    @Override
    public void initStats(WorkoutStatsFragment s) {


        if (EGYM == w.selProgram) {
            s.showStatsGroup(s.EGYM_STATS);

            s.initEgymInterval();
        } else {
            s.showStatsGroup(s.NORMAL_STATS);
        }
    }

    @Override
    public void initChart(WorkoutChartsFragment c) {

        if (isTreadmill) {

//            if (MANUAL == w.selProgram || EGYM == w.selProgram) {
            if (MANUAL == w.selProgram) {
                //依照最低速度產生圖
                setMinSpeedDiagram(w); // orgArraySpeedAndLevel
            } else {
                //取得原始速度Profile  > orgArraySpeedAndLevel
                setTreadmillSpeedPresentDiagram(w);
                //   Log.d("####RRR@@@", "initChart: " + Arrays.toString(w.orgArraySpeedAndLevel));

                //profile中最大的值
                IntSummaryStatistics sSpeed = Arrays.stream(w.orgArraySpeedAndLevel).summaryStatistics();
                w.orgMaxSpeedInProfile.set(sSpeed.getMax());

            }
            //Incline初始值
            setTreadmillInclineDiagram(w); //orgArrayIncline

            //profile中最大的值
            IntSummaryStatistics sIncline = Arrays.stream(w.orgArrayIncline).summaryStatistics();
            w.orgMaxInclineInProfile.set(sIncline.getMax());


            // TODO EGYM
            //初始值為EGYM提供
            if (EGYM == w.selProgram) {

                w.orgArraySpeedAndLevelE = new int[e.durationTimesList.size()];
                w.orgArrayInclineE = new int[e.durationTimesList.size()];

                w.newArraySpeedAndLevelE = new int[w.orgArraySpeedAndLevelE.length];
                w.newArrayInclineE = new int[w.orgArrayInclineE.length];

                for (int i = 0; i < e.durationTimesList.size(); i++) {

                    // 檢查 selTrainer 是否為 null
                    if (e.selTrainer == null || e.selTrainer.getIntervals() == null || e.selTrainer.getIntervals().size() <= i) {
                        Log.e("CommonPrograms", "selTrainer 或 Intervals 為 null，或索引超出範圍！");
                        continue; // 直接跳過此迴圈
                    }

                    // 取得當前 interval
                    EgymTrainingPlans.TrainerDTO.IntervalsDTO interval = e.selTrainer.getIntervals().get(i);
                    if (interval == null) {
                        Log.e("CommonPrograms", "interval 為 null，索引：" + i);
                        continue;
                    }

                    Double speed = interval.getSpeed(); //都是公制
                    Double incline = interval.getIncline();

                    int speedValue;
                    if (speed == null) {
                        // 如果是null, 就取console最小速度
                        speedValue = getMinSpeedLevel(w.selProgram);
                    } else {
                        // TODO EGYM unitSystem
                        if (UNIT_E == DeviceIntDef.METRIC) {
                            speedValue = (int) (speed * 10);
                        } else {
                            speedValue = (int) (km2mi(speed) * 10);
                        }
                    }

                    int inclineValue = (int) ((incline != null ? incline : 0) * 2);

                    w.orgArraySpeedAndLevelE[i] = speedValue;
                    w.orgArrayInclineE[i] = inclineValue;
                }


                System.arraycopy(w.orgArraySpeedAndLevelE, 0, w.newArraySpeedAndLevelE, 0, w.orgArraySpeedAndLevelE.length);
                System.arraycopy(w.orgArrayInclineE, 0, w.newArrayInclineE, 0, w.orgArrayInclineE.length);



            }
        } else {
            //BIKE
            setBikeLevelDiagram(w);
            //profile中最大的值
            IntSummaryStatistics sSpeed = Arrays.stream(w.orgArraySpeedAndLevel).summaryStatistics();
            w.orgMaxSpeedInProfile.set(sSpeed.getMax());
          //  w.selMaxSpeedOrLevel.set(sSpeed.getMax());

            // TODO EGYM
            //初始值為EGYM提供
            if (EGYM == w.selProgram) {
                //EGYM BIKE
                w.orgArraySpeedAndLevelE = new int[e.durationTimesList.size()];
                w.orgArrayInclineE = new int[e.durationTimesList.size()];

                w.newArraySpeedAndLevelE = new int[w.orgArraySpeedAndLevelE.length];
                w.newArrayInclineE = new int[w.orgArrayInclineE.length];




                for (int i = 0; i < e.durationTimesList.size(); i++) {

                    // 檢查 selTrainer 是否為 null
                    if (e.selTrainer == null || e.selTrainer.getIntervals() == null || e.selTrainer.getIntervals().size() <= i) {
                        Log.e("CommonPrograms", "selTrainer 或 Intervals 為 null，或索引超出範圍！");
                        continue; // 直接跳過此迴圈
                    }

                    // 取得當前 interval
                    EgymTrainingPlans.TrainerDTO.IntervalsDTO interval = e.selTrainer.getIntervals().get(i);
                    if (interval == null) {
                        Log.e("CommonPrograms", "interval 為 null，索引：" + i);
                        continue;
                    }

                    // 檢查 speed 和 incline 是否為 null
                    Integer level = interval.getResistance();
                    Integer incline;
                    if (MODE == ModeEnum.CE1000ENT || MODE == ModeEnum.STEPPER) {
                        //SPM
                        incline = interval.getStepsPerMinute();
                    } else {
                        //RPM
                        incline = interval.getRotations();
                    }

                    //如果是Null 就用Console最小速度
                    int speedValue = level != null ? level : 1;
                    int inclineValue = (incline != null ? incline : 0);

                    w.orgArraySpeedAndLevelE[i] = speedValue;
                    w.orgArrayInclineE[i] = inclineValue;
                }

                System.arraycopy(w.orgArraySpeedAndLevelE, 0, w.newArraySpeedAndLevelE, 0, w.orgArraySpeedAndLevelE.length);
                System.arraycopy(w.orgArrayInclineE, 0, w.newArrayInclineE, 0, w.orgArrayInclineE.length);
            }
        }
        Log.d("VVVCCXAAA", "LEVEL: " + Arrays.toString(w.orgArraySpeedAndLevelE));
        Log.d("VVVCCXAAA", "Cadence: " + Arrays.toString(w.orgArrayInclineE));
    }

    @Override
    public void init() {
        //Incline初始設定
        // TODO EGYM
        if (MANUAL == w.selProgram || EGYM == w.selProgram) {
            //MaxIncline給最大值
            m.setMaxInclineMax();
            m.hideBtnSkip();
            if (isUs) {
                m.usWorkoutStopLong();

                //     if (isTreadmill) {
                m.getBinding().cStatsUsNoView.setVisibility(View.GONE);
                //     }
//                m.getBinding().btnWorkoutStopUs.setLayoutParams(LayoutParams(232, 400));
                if (isTreadmill) {
                    m.getBinding().groupTopNumberUs.setVisibility(View.VISIBLE);
                    m.getBinding().groupBottomNumberUs.setVisibility(View.VISIBLE);
//                } else {
//                    m.getBinding().tvTopTextUs.setText(R.string.direct_level);
//                    m.getBinding().groupTopNumberUs.setVisibility(View.VISIBLE);
//                    m.getBinding().groupBottomNumberUs.setVisibility(View.VISIBLE);
//                    m.getBinding().tvBottomTextUs.setText(R.string.direct_level);
                }
            }
        } else {

            if (isTreadmill) {

                w.warmUpTime.set(60 * 3);
                //  w.warmUpTime.set(20);
                if (w.selWorkoutTime.get() == UNLIMITED) { //上數不要CoolDown
                    w.coolDownTime.set(0);
                } else {
                    w.coolDownTime.set(60 * 3);
                    //    w.coolDownTime.set(20);
                }

                if (isUs) {
                    m.getBinding().cStatsUsNoView.setVisibility(View.VISIBLE);
                    m.getBinding().btnSkipUs.setVisibility(View.VISIBLE);

                    m.getBinding().groupTopNumberUs.setVisibility(View.VISIBLE);
                    m.getBinding().groupBottomNumberUs.setVisibility(View.VISIBLE);
                }

            } else {
                //bike
                if (isUs) {
                    m.usWorkoutStopLong();
                }
            }

            w.currentMaxSpeed.set(getSpeedValue(w.selMaxSpeedOrLevel.get()));
            w.currentMaxSpeedLevel.set(w.selMaxSpeedOrLevel.get());

        }

        //其他program自己會算max
    }

    @Override
    public void warmUp(long time) {

        //  Log.d("updateSpeedNum", "warmUp: " + time);

        if (time == w.warmUpTime.get()) { //初始速度
            //  m.updateSpeedOrLevelNum(getTreadmillSpeed(w, 20), true);
            //  m.warmUpSpeedUpdate(getTreadmillSpeed(w, 20));
            w.currentSegment.set(0);
            m.warmUpSpeedUpdate(w.speedDiagramBarList.get(0).getBarNum());

            m.warmUpInclineUpdate(w.inclineDiagramBarList.get(0).getBarNum());

            u.setDevSpeedAndIncline();

            return;
        }

        if (time == 120) {
            //   if (time == 175) {
            //  m.updateSpeedOrLevelNum(getTreadmillSpeed(w, 30), true);
            //   m.warmUpSpeedUpdate(getTreadmillSpeed(w, 30));
            w.currentSegment.set(1);
            m.warmUpSpeedUpdate(w.speedDiagramBarList.get(1).getBarNum());

            m.warmUpInclineUpdate(w.inclineDiagramBarList.get(1).getBarNum());

            u.setDevSpeedAndIncline();
            return;
        }

        if (time == 60) {
            //  m.updateSpeedOrLevelNum(getTreadmillSpeed(w, 40), true);
            //  m.warmUpSpeedUpdate(getTreadmillSpeed(w, 40));
            w.currentSegment.set(2);
            m.warmUpSpeedUpdate(w.speedDiagramBarList.get(2).getBarNum());

            m.warmUpInclineUpdate(w.inclineDiagramBarList.get(2).getBarNum());

            u.setDevSpeedAndIncline();
        }


    }

    @Override
    public void calcDistance(double distanceAccumulate) {

    }

    @Override
    public void runTime() {
        if (w.selProgram == EGYM) {
            //    Log.d("PPPQQQAAAAA", "runTime: " + w.elapsedTime.get());
        }
    }

    @Override
    public void out() {

//        if (w.selWorkoutTime.get() != UNLIMITED) {
//            m.initCoolDown();
//        }

        //下數時間小於等於0，離開Workout
        if (w.selWorkoutTime.get() != UNLIMITED && w.remainingTime.get() <= 0) {
            //沒有設定cooldown時間會直接結束workout
            m.initCoolDown();
        }
    }

    @Override
    public void target() {

    }

    @Override
    public void coolDown(long time) {

        if (time == w.coolDownTime.get()) {
            w.currentSegment.set(23);
            m.warmUpSpeedUpdate(w.speedDiagramBarList.get(23).getBarNum());

            m.warmUpInclineUpdate(w.inclineDiagramBarList.get(23).getBarNum());

            u.setDevSpeedAndIncline();
            Log.d("PPPPPPEEEEE", "coolDown:LEVEL: 180 " + w.speedDiagramBarList.get(23).getBarNum());
            Log.d("PPPPPPEEEEE", "coolDown:INCLINE: 180 " + w.inclineDiagramBarList.get(23).getBarNum());
            return;
        }

        if (time == 120) {
            w.currentSegment.set(24);
            m.warmUpSpeedUpdate(w.speedDiagramBarList.get(24).getBarNum());

            m.warmUpInclineUpdate(w.inclineDiagramBarList.get(24).getBarNum());

            u.setDevSpeedAndIncline();

//            Log.d("PPPPPPEEEEE", "coolDown:LEVEL: 120 " + w.speedDiagramBarList.get(24).getBarNum());
//            Log.d("PPPPPPEEEEE", "coolDown:INCLINE: 120 " + w.inclineDiagramBarList.get(24).getBarNum());
            return;
        }

        if (time == 60) {
            w.currentSegment.set(25);
            m.warmUpSpeedUpdate(w.speedDiagramBarList.get(25).getBarNum());

            m.warmUpInclineUpdate(w.inclineDiagramBarList.get(25).getBarNum());

            u.setDevSpeedAndIncline();

//            Log.d("PPPPPPEEEEE", "coolDown:LEVEL: 180 " + w.speedDiagramBarList.get(23).getBarNum());
//            Log.d("PPPPPPEEEEE", "coolDown:INCLINE: 180 " + w.inclineDiagramBarList.get(23).getBarNum());
        }
    }

    @Override
    public void cancelTimer() {

    }
}
