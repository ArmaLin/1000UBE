package com.dyaco.spirit_commercial.workout.programs;

import static com.dyaco.spirit_commercial.MainActivity.isUs;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.get5kTarget;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.getSpeedLevel;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.setMinSpeedDiagram;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.setTreadmillInclineDiagram;

import android.view.View;

import com.dyaco.spirit_commercial.viewmodel.WorkoutViewModel;
import com.dyaco.spirit_commercial.workout.MainWorkoutTrainingFragment;
import com.dyaco.spirit_commercial.workout.WorkoutChartsFragment;
import com.dyaco.spirit_commercial.workout.WorkoutStatsFragment;

/**
 * Target :
 */
public class Run5K implements IPrograms {

    WorkoutViewModel w;
    MainWorkoutTrainingFragment m;

    public Run5K(WorkoutViewModel workoutViewModel, MainWorkoutTrainingFragment mainWorkoutTrainingFragment) {
        this.w = workoutViewModel;
        this.m = mainWorkoutTrainingFragment;
    }

    @Override
    public void initChart(WorkoutChartsFragment c) {
        setMinSpeedDiagram(w);
        setTreadmillInclineDiagram(w);
    }

    @Override
    public void initStats(WorkoutStatsFragment s) {
        s.showStatsGroup(s.NORMAL_STATS);
    }

    @Override
    public void init() {

     //   w.targetDistance.set(get5kTarget());

        //距離倒數
        w.targetDistance.set(get5kTarget());//設定目標距離
        w.distanceLeft.set(get5kTarget()); //剩餘距離

        //MaxIncline給最大值
        m.setMaxInclineMax();

        if (isUs) {
            //SPEED 按鈕顯示
            m.getBinding().groupBottomNumberUs.setVisibility(View.VISIBLE);
            m.getBinding().groupTopNumberUs.setVisibility(View.VISIBLE);
            m.usWorkoutStopLong();
        }
    }

    @Override
    public void warmUp(long time) {

    }

    @Override
    public void calcDistance(double distanceAccumulate) {
        //距離倒數
        double d = w.targetDistance.get() - distanceAccumulate;
        d = d >= 0 ? d : 0;
        w.currentDistance.set(d);
    }

    @Override
    public void runTime() {
        if (w.elapsedTime.get() == 1) {
            m.updateSpeedOrLevelNum(getSpeedLevel(w.selTargetSpeed.get()), true);
        }
    }

    @Override
    public void out() {

    }

    @Override
    public void target() {
        m.targetDistance(get5kTarget());
    }

    @Override
    public void coolDown(long time) {

    }

    @Override
    public void cancelTimer() {

    }
}
