package com.dyaco.spirit_commercial.workout.programs;

import static com.dyaco.spirit_commercial.MainActivity.isUs;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.setBikeLevelDiagram;
import static com.dyaco.spirit_commercial.support.intdef.WorkoutIntDef.UNLIMITED;

import android.util.Log;
import android.view.View;

import com.corestar.calculation_libs.Calculation;
import com.dyaco.spirit_commercial.App;
import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.viewmodel.WorkoutViewModel;
import com.dyaco.spirit_commercial.workout.MainWorkoutTrainingFragment;
import com.dyaco.spirit_commercial.workout.WorkoutChartsFragment;
import com.dyaco.spirit_commercial.workout.WorkoutStatsFragment;

import java.util.Arrays;
import java.util.IntSummaryStatistics;

/**
 * Target :
 */
public class WingateProg implements IPrograms {

    WorkoutViewModel w;
    MainWorkoutTrainingFragment m;
    int weightInKg;
    MainActivity mainActivity;
    Calculation calc;
    WorkoutChartsFragment c;
    int levelllll;

    int watt5;

    public WingateProg(WorkoutViewModel workoutViewModel, MainWorkoutTrainingFragment mainWorkoutTrainingFragment, int weightInKg, MainActivity mainActivity, Calculation calc,WorkoutChartsFragment c) {
        this.w = workoutViewModel;
        this.m = mainWorkoutTrainingFragment;
        this.weightInKg = weightInKg;
        this.mainActivity = mainActivity;
        this.calc = calc;
        this.c = c;
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

        m.getBinding().cStatsUsNoViewBike.setVisibility(View.VISIBLE);
    }

    @Override
    public void init() {

        Log.d("WWINNNNNNN", "selTime: " + w.selWorkoutTime.get());
        Log.d("WWINNNNNNN", "selForce: " + w.selForce.get());
        Log.d("WWINNNNNNN", "selWeightMU: " + w.selWeightMU.get());


        levelllll = App.MODE.getLevelViaPowerAndRpm((int) (w.selForce.get() * 60), 60);

        Log.d("WWINNNNNNN", "#######LEVEL: " + levelllll);
        //MaxIncline給最大值
      //  m.hideBtnSkip();

//        w.warmUpTime.set(60 * 5);
        w.warmUpTime.set(60 * 5);
        w.coolDownTime.set(0);
        m.setMaxInclineMax();

//        w.disabledLevelUpdate.set(true);

        if (isUs) {
            m.getBinding().btnSkipUs.setVisibility(View.VISIBLE);
            m.getBinding().groupBottomNumberUs.setVisibility(View.VISIBLE);
            m.getBinding().groupTopNumberUs.setVisibility(View.VISIBLE);
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
        int t = w.elapsedTime.get();

        if (t == 1) {
            m.updateSpeedOrLevelNum(levelllll, true);
            watt5 = 0;
        }

        watt5 += calc.getWatt();

        if (t % 5 == 0) {
            w.wingateWattsList.add(watt5);
            watt5 = 0;
        }

        c.updateWattChart(calc.getWatt());
        // TODO: update watts

    }

    @Override
    public void out() {
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

    }

    @Override
    public void cancelTimer() {

    }
}
