package com.dyaco.spirit_commercial.workout.programs;

import static com.dyaco.spirit_commercial.MainActivity.isUs;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.setBikeLevelDiagram;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.POWER_MAX;
import static com.dyaco.spirit_commercial.support.intdef.WorkoutIntDef.UNLIMITED;

import android.util.Log;
import android.view.View;

import com.corestar.calculation_libs.Calculation;
import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.UartVM;
import com.dyaco.spirit_commercial.viewmodel.WorkoutViewModel;
import com.dyaco.spirit_commercial.workout.MainWorkoutTrainingFragment;
import com.dyaco.spirit_commercial.workout.WorkoutChartsFragment;
import com.dyaco.spirit_commercial.workout.WorkoutStatsFragment;

public class Watt implements IPrograms {

    WorkoutViewModel w;
    MainWorkoutTrainingFragment m;
    Calculation calc;
    UartVM uartVM;

    public Watt(WorkoutViewModel workoutViewModel, MainWorkoutTrainingFragment mainWorkoutTrainingFragment, Calculation calc, UartVM uartVM) {
        this.w = workoutViewModel;
        this.m = mainWorkoutTrainingFragment;
        this.calc = calc;
        this.uartVM = uartVM;
        w.constantPowerW.set(w.selConstantPowerW.get());
        w.currentLevel.set(calc.getLevel(w.selConstantPowerW.get(), w.currentRpm.get()));
        Log.d("##########", "1111111updateSpeedOrLevelNum: " +w.selConstantPowerW.get() + "," + w.currentRpm.get() +","+ calc.getLevel(w.constantPowerW.get(),w.currentRpm.get()));

    }

    @Override
    public void initChart(WorkoutChartsFragment c) {
        setBikeLevelDiagram(w);
    }

    @Override
    public void initStats(WorkoutStatsFragment s) {
        s.showStatsGroup(s.NORMAL_STATS);
    }

    @Override
    public void init() {

      //  uartVM.constantType.set(CT_POWER);

        w.currentMaxSpeed.set(POWER_MAX);

        //30- 300

//        w.disabledInclineUpdate.set(true);
//        w.disabledSpeedUpdate.set(true);

        //其他program自己會算

        if (isUs) {
            m.usWorkoutStopLong();
            m.getBinding().viewBikeLevelTitleUs.setText(R.string.Power);

            m.getBinding().tvTopTextUs.setText(R.string.direct_watts);
            m.getBinding().tvTopTextUs.setVisibility(View.VISIBLE);
            m.getBinding().vTopUs1.setVisibility(View.VISIBLE);
            m.getBinding().vTopUs2.setVisibility(View.VISIBLE);

            m.getBinding().groupBottomNumberUs.setVisibility(View.VISIBLE);
            m.getBinding().tvBottomTextUs.setText(R.string.direct_watts);
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
            //   m.updateSpeedOrLevelNum(UNIT_E == IMPERIAL ? getSpeedLevel(0.5f) : getSpeedLevel(0.8f), true);
            //   m.updateSpeedOrLevelNum(w.selConstantPowerW.get(), true);
            //   m.updateSpeedOrLevelNum(calc.getLevel(w.selConstantPowerW.get(),w.currentRpm.get()), true);
            m.updateSpeedOrLevelNum(w.selConstantPowerW.get(), true);
        }
    }

    @Override
    public void out() {
        //下數時間小於等於0，離開Workout
        if (w.selWorkoutTime.get() != UNLIMITED && w.remainingTime.get() <= 0) {
            m.finishWorkout(true);
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
