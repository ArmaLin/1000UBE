package com.dyaco.spirit_commercial.workout.programs;

import static com.dyaco.spirit_commercial.MainActivity.isUs;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.getArmyTarget;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.setMinSpeedDiagram;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.setTreadmillInclineDiagram;

import android.view.View;

import com.dyaco.spirit_commercial.viewmodel.WorkoutViewModel;
import com.dyaco.spirit_commercial.workout.MainWorkoutTrainingFragment;
import com.dyaco.spirit_commercial.workout.WorkoutChartsFragment;
import com.dyaco.spirit_commercial.workout.WorkoutStatsFragment;

/**
 * Target : 目標距離 2.1mi
 * <p>
 * ARMY
 * (1) 條件設定: Age(35), weight(150), gender
 * (2) 可自行調整速度
 * (3) 距離顯示從 2.00 英里開始並向下計數，時間從 0:00 開始並向上計數。 速度
 * 從 0.5 開始，並且可讓用戶更改。 距離達到 0.00 後，測試結束。 總時間用
 * 於查找結果。
 * (4) 距離下數到 0 時, 測試結束, 依據總時間來查結果, 要顯示分數及測驗結果
 * (PASS/FAIL)
 * 依據年齡 17~21 的男性及女性的時間來給分數 依據年齡 22~26 的男性及女性
 * 依據年齡 27~31 的男性及女性
 * 依據年齡 32~36 的男性及女性
 * 依據年齡 37~41 的男性及女性
 */
public class Army implements IPrograms {

    WorkoutViewModel w;
    MainWorkoutTrainingFragment m;

    public Army(WorkoutViewModel workoutViewModel, MainWorkoutTrainingFragment mainWorkoutTrainingFragment) {
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

        //顯示下方STOP按紐
        m.showBottomStopButton();
        m.hideBtnCoolDown();
        m.hideBtnSkip();
        m.disappearTreadmillControl(m.PANEL_SPEED_SHOW);
        w.disabledInclineUpdate.set(true);//不能調整Incline
      //  m.hideBtnWorkoutStop();

        //距離倒數
//        w.currentDistance.set(getArmyTarget());//當前顯示的距離
//        w.targetDistance.set(w.currentDistance.get());//設定目標距離
//        w.selTargetSpeed.set(0.5f);//初始速度

        w.targetDistance.set(getArmyTarget());//設定目標距離
        w.distanceLeft.set(getArmyTarget()); //剩餘距離

        //MaxIncline給最大值
        m.setMaxInclineMax();


//        w.currentDistance.set(0.1);//當前顯示的距離
//        w.targetDistance.set(0.1);//設定目標距離
//        w.selTargetSpeed.set(15);//初始速度
        if (isUs) {
            m.usWorkoutStopLong();
            m.getBinding().groupBottomNumberUs.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void warmUp(long time) {

    }

    @Override
    public void calcDistance(double distanceAccumulate) {
        double d = w.targetDistance.get() - distanceAccumulate;
        d = d >= 0 ? d : 0;
        w.currentDistance.set(d);
    }

    @Override
    public void runTime() {

    }

    @Override
    public void out() {

    }

    @Override
    public void target() {
        //  m.targetDistance(getArmyTarget());
        m.targetDistance(0);
    }

    @Override
    public void coolDown(long time) {

    }

    @Override
    public void cancelTimer() {

    }
}
