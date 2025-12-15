package com.dyaco.spirit_commercial.workout.programs;

import static com.dyaco.spirit_commercial.MainActivity.isUs;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.setBikeLevelDiagram;
import static com.dyaco.spirit_commercial.support.intdef.WorkoutIntDef.UNLIMITED;

import android.util.Log;
import android.view.View;

import com.corestar.calculation_libs.Calculation;
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
public class METsProg implements IPrograms {

    WorkoutViewModel w;
    MainWorkoutTrainingFragment m;
    int weightInKg;
    MainActivity mainActivity;
    Calculation calc;

    public METsProg(WorkoutViewModel workoutViewModel, MainWorkoutTrainingFragment mainWorkoutTrainingFragment, int weightInKg, MainActivity mainActivity, Calculation calc) {
        this.w = workoutViewModel;
        this.m = mainWorkoutTrainingFragment;
        this.weightInKg = weightInKg;
        this.mainActivity = mainActivity;
        this.calc = calc;
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

        Log.d("MMMMEEEEETTTTT", "targetMets: " + w.targetMets.get());
        Log.d("MMMMEEEEETTTTT", "targetMets: " + weightInKg);

        //MaxIncline給最大值
        m.hideBtnSkip();

  //      w.disabledLevelUpdate.set(true);

        if (isUs) {
            m.usWorkoutStopLong();
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

        int targetPower = (int) ((w.targetMets.get() - 2) / 3.0857 * weightInKg);

        m.updateSpeedOrLevelNumWATT(calc.getLevel(targetPower, w.currentRpm.get()), true);
    //    mainActivity.getUartConsoleManager().setPwmViaPower(targetPower);
     //   Log.d("MMMMEEEEETTTTT", "runTime: " + targetPower);
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
