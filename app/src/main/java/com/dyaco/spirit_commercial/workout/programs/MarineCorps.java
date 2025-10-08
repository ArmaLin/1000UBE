package com.dyaco.spirit_commercial.workout.programs;

import static com.dyaco.spirit_commercial.MainActivity.isUs;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.getMarinesTarget;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.setMinSpeedDiagram;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.setTreadmillInclineDiagram;

import android.view.View;

import com.dyaco.spirit_commercial.viewmodel.WorkoutViewModel;
import com.dyaco.spirit_commercial.workout.MainWorkoutTrainingFragment;
import com.dyaco.spirit_commercial.workout.WorkoutChartsFragment;
import com.dyaco.spirit_commercial.workout.WorkoutStatsFragment;

/**
 * Marines 海軍陸戰隊
 * (1) 條件設定: Age(35), weight(150), gender
 * (2) 距離從 3.0 英里開始下數, 時間從 00:00 開始上數, 距離到達時, 測驗結束 (3) 查表取得分數, 顯示 point 及 Pass/Fail
 * *依性別及時間取得 points
 * *依年齡及總時間, 來判斷是否 pass test
 */
public class MarineCorps implements IPrograms {

    WorkoutViewModel w;
    MainWorkoutTrainingFragment m;

    public MarineCorps(WorkoutViewModel workoutViewModel, MainWorkoutTrainingFragment mainWorkoutTrainingFragment) {
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

        w.targetDistance.set(getMarinesTarget());//設定目標距離
        w.distanceLeft.set(getMarinesTarget()); //剩餘距離

        //MaxIncline給最大值
        m.setMaxInclineMax();

        if (isUs) {
            m.usWorkoutStopLong();
            //SPEED 按鈕顯示
            m.getBinding().groupBottomNumberUs.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void warmUp(long time) {

    }

    @Override
    public void calcDistance(double distanceAccumulate) {
//        double d = w.targetDistance.get() - distanceAccumulate;
//        d = d >= 0 ? d : 0;
//        w.currentDistance.set(d);
    }

    @Override
    public void runTime() {

    }

    @Override
    public void out() {

    }

    @Override
    public void target() {
      //  m.targetDistance(getMarinesTarget());
        m.targetDistance(0);
    }


    @Override
    public void coolDown(long time) {

    }

    @Override
    public void cancelTimer() {

    }
}
