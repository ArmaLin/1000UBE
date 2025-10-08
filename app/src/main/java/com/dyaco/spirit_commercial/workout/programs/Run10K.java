package com.dyaco.spirit_commercial.workout.programs;

import static com.dyaco.spirit_commercial.MainActivity.isUs;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.get10kTarget;
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
public class Run10K implements IPrograms {

    WorkoutViewModel w;
    MainWorkoutTrainingFragment m;

    public Run10K(WorkoutViewModel workoutViewModel, MainWorkoutTrainingFragment mainWorkoutTrainingFragment) {
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

      //  w.targetDistance.set(get10kTarget());

        //距離倒數
        w.targetDistance.set(get10kTarget());//設定目標距離
        w.distanceLeft.set(get10kTarget()); //剩餘距離

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

    }

    @Override
    public void runTime() {
        if (w.elapsedTime.get() == 1) {
            m.updateSpeedOrLevelNum(getSpeedLevel(w.selTargetSpeed.get()), true);
        }
        //扣掉距離 todo
     //   double target = (workoutViewModel.currentDistance.get() - 0.1f) < 0 ? 0 : workoutViewModel.currentDistance.get() - 0.1f;
      //  workoutViewModel.currentDistance.set(getRoundDecimal(target));
    }

    @Override
    public void out() {

    }

    @Override
    public void target() {
        m.targetDistance(get10kTarget());
    }

    @Override
    public void coolDown(long time) {

    }

    @Override
    public void cancelTimer() {

    }
}
