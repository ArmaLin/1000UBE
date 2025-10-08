package com.dyaco.spirit_commercial.workout.programs;

import static com.dyaco.spirit_commercial.App.UNIT_E;
import static com.dyaco.spirit_commercial.MainActivity.isUs;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.getSpeedLevel;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.setMinSpeedDiagram;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.setTreadmillInclineDiagram;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.IMPERIAL;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.SELECT_HR_EXCEEDS_AGE;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.MAX_INC_MAX;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.THR_CONSTANT;
import static com.dyaco.spirit_commercial.support.intdef.WorkoutIntDef.UNLIMITED;

import android.util.Log;
import android.view.View;

import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.support.RxTimer;
import com.dyaco.spirit_commercial.viewmodel.WorkoutViewModel;
import com.dyaco.spirit_commercial.workout.MainWorkoutTrainingFragment;
import com.dyaco.spirit_commercial.workout.WorkoutChartsFragment;
import com.dyaco.spirit_commercial.workout.WorkoutStatsFragment;

public class HeartRateTreadmill implements IPrograms {
    private static final String TAG = "HEART_RATE_PROGRAM";
    private final int HOLDING_HR = 0;
    private int noHrTime;
    private RxTimer noRpmTimer;
    boolean isCheckReachingModeDone;
    private RxTimer hrTimer;

    WorkoutViewModel w;
    MainWorkoutTrainingFragment m;

    public static float HEART_MU_MAX_SPEED = 16;
    public static float HEART_IU_MAX_SPEED = 10;

    int HEART_MAX_SPEED;
    int HEART_MIN_SPEED;

    public HeartRateTreadmill(WorkoutViewModel workoutViewModel, MainWorkoutTrainingFragment mainWorkoutTrainingFragment) {
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
        s.showStatsGroup(s.HEART_RATE_STATS);

        if (isUs) {

            m.usWorkoutStopLong();

            m.getBinding().viewInclineTitleUs.setText(R.string.THR);

            m.getBinding().groupBottomNumberUs.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void init() {
        hrTimer = new RxTimer();

        //一樣
        int targetHrMax = THR_CONSTANT - w.selYO.get();
        w.setHrTargetHrMax(targetHrMax);
        w.selHrMax.set(targetHrMax);

        //16k 10m
        HEART_MAX_SPEED = UNIT_E == IMPERIAL ? getSpeedLevel(HEART_IU_MAX_SPEED) : getSpeedLevel(HEART_MU_MAX_SPEED);
        HEART_MIN_SPEED = UNIT_E == IMPERIAL ? getSpeedLevel(0.5f) : getSpeedLevel(0.8f);

        //MaxIncline給最大值
        m.setMaxInclineMax();


        //MAX_HR 年齡預測的最大心率 185
        //w.selTargetHrBpm.get() 選定的目標心率 200


        Log.d(TAG, "選擇的目標心率: " + w.selTargetHrBpm.get() + ", 年齡預測的最大心率:" + w.getHrTargetHrMax());
        //w.selTargetHrBpm 200 > MAX_HR 185 > 選定的目標心率超過您年齡預測的最大心率。
        if (w.selTargetHrBpm.get() > w.getHrTargetHrMax()) {
            Log.d(TAG, "選定的目標心率超過您年齡預測的最大心率 ");
            new RxTimer().timer(500, number ->
                    m.showWarring(SELECT_HR_EXCEEDS_AGE));
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
        updateHrData();
    }

    @Override
    public void out() {
        m.noHrCheck();
    }

    @Override
    public void target() {
//        if (w.remainingTime.get() <= 0) {
//            new RxTimer().timer(500, number -> m.initCoolDown());
//        }

        //下數時間小於等於0，離開Workout
        if (w.selWorkoutTime.get() != UNLIMITED && w.remainingTime.get() <= 0) {
            new RxTimer().timer(500, number -> m.initCoolDown());
        }

    }

    @Override
    public void coolDown(long time) {

    }

    /**
     * HR主流程
     */
    private void updateHrData() {

        updateRunIncline();
    }

    int switchTimes = 0; //3.5.7.5 採用變動完揚升後, 再隔30秒, 變動 Speed

    int hr20Times = 0; //3.5.7.1 連續20秒，心跳值 < (Target Heart Rate – 15)，則incline上升0.5，最高到15.0。
    int hr30Times = 0; //3.5.7.2 連續30秒，心跳值 < (Target Heart Rate – 3)，則incline上升0.5，最高到15.0。
    int hr15Times = 0; //3.5.7.3 連續15秒，心跳值 > (Target Heart Rate + 3)，則incline下降0.5，最低到0.0。

    private void updateRunIncline() {

        //3.5.6 前2分鐘，不將目前心跳值跟Target Heart Rate做比較，故incline不會自動變化。
        if (w.elapsedTime.get() > (60 * 2)) {
            //3.5.7 2分鐘過後，比對目前心跳值跟Target Heart Rate (Auto-Pilot Mode直接比對目前心跳值跟Target Heart Rate)

//            Log.d(TAG, "RUN: " + switchTimes);
//            switchTimes++;
//            if (switchTimes > 30) {
//                hr20Times = 0;
//                hr30Times = 0;
//                hr15Times = 0;
//                updateRunSpeed();
//                return;
//            }


            //3.5.7.4 心跳值在 Target Heart Rate ± 3 之間，則incline 不變動。
//            if (w.currentHeartRate.get() <= (w.selTargetHrBpm.get() + 3) && w.currentHeartRate.get() >= (w.selTargetHrBpm.get() - 3)) {
//                hr20Times = 0;
//                hr30Times = 0;
//                hr15Times = 0;
//                return;
//            }

            Log.d(TAG, "updateRunIncline: hr20Times:" + hr20Times +", hr15Times:"+ hr15Times +", hr30Times:"+hr30Times);
            //3.5.7.1 連續20秒，心跳值 < (Target Heart Rate – 15)，則incline上升0.5，最高到15.0。
            if (hr20Times >= 20) {
                hr20Times = 0;
                if (w.currentHeartRate.get() < (w.selTargetHrBpm.get() - 15)) {
                    if ((w.currentInclineLevel.get() + 1) > MAX_INC_MAX) return;
                    m.updateInclineNum(1, false);
                    Log.d(TAG, "提高 INCLINE 調整為 >> " + w.currentInclineLevel.get());
                    m.showHrHint2(true, w.currentInclineLevel.get(), 0);
                    hr30Times = 0;
                    hr15Times = 0;
                    return;
                }
            }
            hr20Times++;


            //3.5.7.2 連續30秒，心跳值 < (Target Heart Rate – 3)，則incline上升0.5，最高到15.0。
            if (hr30Times >= 30) {

                hr30Times = 0;

                if (w.currentHeartRate.get() < (w.selTargetHrBpm.get() - 5)) {

                    if ((w.currentInclineLevel.get() + 1) > MAX_INC_MAX) return;

                    m.updateInclineNum(1, false);

                    Log.d(TAG, "提高 INCLINE 調整為 >> " + w.currentInclineLevel.get());

                    m.showHrHint2(true, w.currentInclineLevel.get(), 0);
                    hr20Times = 0;
                    hr15Times = 0;
                    return;
                }
            }
            hr30Times++;


            //3.5.7.3 連續15秒，心跳值 > (Target Heart Rate + 3)，則incline下降0.5，最低到0.0。
            if (hr15Times >= 15) {

                hr15Times = 0;
                if (w.currentHeartRate.get() > (w.selTargetHrBpm.get() + 5)) {

                    if ((w.currentInclineLevel.get() - 1) < 0) return;

                    m.updateInclineNum(-1, false);

                    Log.d(TAG, "降低 INCLINE 調整為 >> " + w.currentInclineLevel.get());

                    m.showHrHint2(false, w.currentInclineLevel.get(), 0);
                    hr20Times = 0;
                    hr30Times = 0;
                    return;
                }
            }
            hr15Times++;
        }
    }

    int hrSpeed30PlusTimes;
    int hrSpeed30MinusTimes;

    private void updateRunSpeed() {

        //3.5.7.6 記數30秒，心跳值 < (Target Heart Rate – 3)，則 Speed上升 0.5km(0.3Mile)，最高到 16.0Km(10.0Mile)

        Log.d(TAG, "updateRunSpeed: " + hrSpeed30PlusTimes +","+ hrSpeed30MinusTimes);
        if (hrSpeed30PlusTimes >= 30) {
            hrSpeed30PlusTimes = 0;
            if (w.currentHeartRate.get() < (w.selTargetHrBpm.get() - 5)) {

                if ((w.currentSpeedLevel.get() + 1) > HEART_MAX_SPEED) return;

                m.showHrHint(false, w.currentSpeedLevel.get());
            }
        }
        hrSpeed30PlusTimes++;


        //3.5.7.7 記數30秒，心跳值 > (Target Heart Rate + 3)或偵測不到心跳(HR=0)，則 Speed下降0.5Km(0.3Mile)，最低到 0.8Km(0.5Mile)。
        if (hrSpeed30MinusTimes >= 30) {
            hrSpeed30MinusTimes = 0;
            if (w.currentHeartRate.get() > (w.selTargetHrBpm.get() + 3)) {

                if ((w.currentSpeedLevel.get() - 1) <= HEART_MIN_SPEED) return;
                //  m.updateSpeedOrLevelNum(-1, false);
                //  Log.d(TAG, "降低 SPEED 調整為 >> " + w.currentSpeedLevel.get());
                m.showHrHint(true, w.currentSpeedLevel.get());
            }
        }

        hrSpeed30MinusTimes++;

        if (switchTimes > 60) {
            switchTimes = 0;
        }
    }


    @Override
    public void cancelTimer() {
        if (hrTimer != null) {
            hrTimer.cancel();
            hrTimer = null;
        }

        if (noRpmTimer != null) {
            noRpmTimer.cancel();
            noRpmTimer = null;
        }
    }
}
