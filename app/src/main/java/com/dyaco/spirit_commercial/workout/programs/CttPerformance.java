package com.dyaco.spirit_commercial.workout.programs;

import static com.dyaco.spirit_commercial.MainActivity.isUs;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.getInclineLevel;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.getSpeedLevelNum;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.setSpeedDiagram;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.setTreadmillInclineDiagram;

import android.view.View;

import com.dyaco.spirit_commercial.UartConsoleManagerPF;
import com.dyaco.spirit_commercial.support.RxTimer;
import com.dyaco.spirit_commercial.support.intdef.DeviceIntDef;
import com.dyaco.spirit_commercial.viewmodel.WorkoutViewModel;
import com.dyaco.spirit_commercial.workout.MainWorkoutTrainingFragment;
import com.dyaco.spirit_commercial.workout.WorkoutChartsFragment;
import com.dyaco.spirit_commercial.workout.WorkoutStatsFragment;

/**
 * 不能調整SPEED AND INCLINE ，有WARM UP
 *
 * Target : 到達目標時間 12 分鐘，或主動停止
 *
 *  Warmup時STOP > 直接結束
 *  Workout時STOP > cooldown
 *  Cooldown時STOP > 直接結束
 *
 *  當USER無法承受,按下STOP時,依時間查表,以取得VO2的分數
 */
public class CttPerformance implements IPrograms {
    UartConsoleManagerPF u;
    WorkoutViewModel w;
    MainWorkoutTrainingFragment m;

    public CttPerformance(WorkoutViewModel workoutViewModel, MainWorkoutTrainingFragment mainWorkoutTrainingFragment,UartConsoleManagerPF u) {
        this.w = workoutViewModel;
        this.m = mainWorkoutTrainingFragment;
        this.u = u;
    }

    @Override
    public void initStats(WorkoutStatsFragment s) {
        s.showStatsGroup(s.NORMAL_STATS);
    }

    @Override
    public void initChart(WorkoutChartsFragment c) {

        setSpeedDiagram(w, getSpeedLevelNum(6.2f, DeviceIntDef.METRIC));

      //  setMinSpeedDiagram(w);
        setTreadmillInclineDiagram(w);

        c.diagramBarsView.setBarCount(12);
        c.timeTick = 60;
    }


    @Override
    public void init() {

        //顯示下方STOP按紐
        m.showBottomStopButton();
        m.hideBtnCoolDown();
        m.hideBtnSkip();
        m.hideBtnWorkoutStop();
        w.disabledInclineUpdate.set(true);//不能調整Incline
        w.disabledSpeedUpdate.set(true);//不能調整Speed
        m.disappearTreadmillControl(m.PANEL_ALL_DISAPPEAR);


        w.coolDownTime.set(3 * 60);//cool down 3分鐘
        w.warmUpTime.set(2 * 60);//warm up 2分鐘
     //   w.warmUpTime.set(2);//warm up 2分鐘

//        w.coolDownTime.set(2);//cool down 3分鐘
//        w.warmUpTime.set(2);//warm up 2分鐘

        //When the warm-up time ends set the Time to 12 minutes
        w.selWorkoutTime.set(12 * 60);


        //MaxIncline給最大值
        m.setMaxInclineMax();

        if (isUs) {
            m.usWorkoutStopLong();
            m.getBinding().cStatsUsNoView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void warmUp(long time) {

        //Speed ramp-up: 	Start at 4.0 km/h for first minute
        if (time == w.warmUpTime.get()) { //120秒
            //  m.updateSpeedOrLevelNum(getSpeedLevelNum(4.0f, DeviceIntDef.METRIC), true);
            m.warmUpSpeedUpdate(getSpeedLevelNum(4.0f, DeviceIntDef.METRIC));
            u.setDevSpeedAndIncline();
        }

        //Increase to 4.5 km/h for next 15 seconds
        if (time == 60) {
            m.warmUpSpeedUpdate(getSpeedLevelNum(4.5f, DeviceIntDef.METRIC));
            u.setDevSpeedAndIncline();
        }

        //Increase to 5.0 km/h for next 15 seconds
        if (time == 45) {
            m.warmUpSpeedUpdate(getSpeedLevelNum(5.0f, DeviceIntDef.METRIC));
            u.setDevSpeedAndIncline();
        }

        //Increase to 5.5 km/h for next 15 seconds
        if (time == 30) {
            m.warmUpSpeedUpdate(getSpeedLevelNum(5.5f, DeviceIntDef.METRIC));
            u.setDevSpeedAndIncline();
        }

        //Increase to 6.2 km/h for final 15 seconds of warm up
        if (time == 15) {
            m.warmUpSpeedUpdate(getSpeedLevelNum(6.2f, DeviceIntDef.METRIC));
            u.setDevSpeedAndIncline();
        }

    }

    @Override
    public void calcDistance(double distanceAccumulate) {

    }

    @Override
    public void runTime() {

        //預熱時間結束後，將時間設置為 12 分鐘並倒計時。 速度和坡度保持(6.2 km / h)3.9mph 和 0%。

        //每 2 分鐘增加 3%，最多 15%
        if (w.elapsedTime.get() % 120 == 0 && w.currentInclineLevel.get() < 30) {
            m.updateInclineNum(w.currentInclineLevel.get() + getInclineLevel(3), true);
        }

    }

    @Override
    public void out() {
    }

    @Override
    public void target() {

        if (w.remainingTime.get() <= 0) {
            new RxTimer().timer(500, number -> m.initCoolDown());
        }

    }

    @Override
    public void coolDown(long time) {

        if (time == w.coolDownTime.get()) {
//            m.warmUpSpeedUpdate(getSpeedLevelNum(4.0f, DeviceIntDef.METRIC));
//            m.warmUpInclineUpdate(0);

            m.warmUpSpeedAndIncline(getSpeedLevelNum(4.0f, DeviceIntDef.METRIC),0);

            u.setDevSpeedAndIncline();
        }
    }

    @Override
    public void cancelTimer() {

    }
}
