package com.dyaco.spirit_commercial.workout.programs;

import static com.dyaco.spirit_commercial.MainActivity.isUs;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.setBikeLevelDiagram;

import android.view.View;

import com.dyaco.spirit_commercial.viewmodel.WorkoutViewModel;
import com.dyaco.spirit_commercial.workout.MainWorkoutTrainingFragment;
import com.dyaco.spirit_commercial.workout.WorkoutChartsFragment;
import com.dyaco.spirit_commercial.workout.WorkoutStatsFragment;

import java.util.Arrays;
import java.util.IntSummaryStatistics;

/**
 * Target :
 */
public class StepsProg implements IPrograms {

    WorkoutViewModel w;
    MainWorkoutTrainingFragment m;

    public StepsProg(WorkoutViewModel workoutViewModel, MainWorkoutTrainingFragment mainWorkoutTrainingFragment) {
        this.w = workoutViewModel;
        this.m = mainWorkoutTrainingFragment;
    }

    @Override
    public void initChart(WorkoutChartsFragment c) {
        //BIKE
        setBikeLevelDiagram(w);
        //profile中最大的值
        IntSummaryStatistics sSpeed = Arrays.stream(w.orgArraySpeedAndLevel).summaryStatistics();
        w.orgMaxSpeedInProfile.set(sSpeed.getMax());
        w.selMaxSpeedOrLevel.set(sSpeed.getMax());
    }

    @Override
    public void initStats(WorkoutStatsFragment s) {
        s.showStatsGroup(s.NORMAL_STATS);
    }

    @Override
    public void init() {


        w.stepLeft.set(w.targetSteps.get()); //剩餘距離
        //MaxIncline給最大值
        m.hideBtnSkip();
        if (isUs) {
         //   m.usWorkoutStopLong();
            m.getBinding().cStatsUsNoView.setVisibility(View.GONE);
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
    }

    @Override
    public void out() {

    }

    @Override
    public void target() {
        m.targetSteps();
    }

    @Override
    public void coolDown(long time) {

    }

    @Override
    public void cancelTimer() {

    }
}
