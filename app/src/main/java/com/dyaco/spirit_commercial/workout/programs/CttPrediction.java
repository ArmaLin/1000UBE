package com.dyaco.spirit_commercial.workout.programs;

import static com.dyaco.spirit_commercial.MainActivity.isUs;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.getInclineLevel;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.getSpeedLevelNum;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.setSpeedDiagram;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.setTreadmillInclineDiagram;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.WARRING_HR_REACHED_TARGET;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.THR_CONSTANT;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.THR_PERCENTAGE_80;

import android.util.Log;
import android.view.View;

import com.dyaco.spirit_commercial.UartConsoleManagerPF;
import com.dyaco.spirit_commercial.support.RxTimer;
import com.dyaco.spirit_commercial.support.intdef.DeviceIntDef;
import com.dyaco.spirit_commercial.viewmodel.WorkoutViewModel;
import com.dyaco.spirit_commercial.workout.MainWorkoutTrainingFragment;
import com.dyaco.spirit_commercial.workout.WorkoutChartsFragment;
import com.dyaco.spirit_commercial.workout.WorkoutStatsFragment;

/**
 *
 * 不能調整SPEED AND INCLINE ，有WARM UP
 *
 * Target : 目標心跳，或完成 6 Level
 *
 *  Warmup時STOP > 直接結束
 *  Workout時STOP > cooldown
 *  Cooldown時STOP > 直接結束
 *
 *  當USER無法承受,按下STOP時,依時間查表,以取得VO2的分數
 */
public class CttPrediction implements IPrograms {
    UartConsoleManagerPF u;
    WorkoutViewModel w;
    MainWorkoutTrainingFragment m;
    private int noHrTime;
    float stage = 1;

    public CttPrediction(WorkoutViewModel workoutViewModel, MainWorkoutTrainingFragment mainWorkoutTrainingFragment, UartConsoleManagerPF u) {
        this.w = workoutViewModel;
        this.m = mainWorkoutTrainingFragment;
        this.u = u;
    }

    @Override
    public void initStats(WorkoutStatsFragment s) {
        s.showStatsGroup(s.HEART_TARGET_STATS);
    }

    @Override
    public void initChart(WorkoutChartsFragment c) {
        setSpeedDiagram(w, getSpeedLevelNum(6.2f, DeviceIntDef.METRIC)); //初始圖形
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
        m.disappearTreadmillControl(m.PANEL_ALL_DISAPPEAR);

        w.coolDownTime.set(60 * 3);//cool down 3分鐘
        w.warmUpTime.set(2 * 60);//warm up 2分鐘
    //    w.warmUpTime.set(2);//warm up 2分鐘

//        w.coolDownTime.set(2);//cool down 3分鐘
//        w.warmUpTime.set(2);//warm up 2分鐘

        w.currentStage.set(stage);

        w.disabledInclineUpdate.set(true);//不能調整Incline
        w.disabledSpeedUpdate.set(true);//不能調整Speed
        //When the warm-up time ends set the Time to 12 minutes
        w.selWorkoutTime.set(12 * 60);

        w.selTargetHrBpm.set((THR_CONSTANT - w.selYO.get()) * THR_PERCENTAGE_80 / 100);

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


        m.noHrCheck();
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
            w.currentStage.set(++stage);
        }

        //CTT_PREDICTION 每2分鐘存一次hr，12分鐘 共 6 level
        if (w.elapsedTime.get() % 120 == 0) {
            w.cttLevelHrList.add((double) w.currentHeartRate.get());
            Log.d("CttPrediction_TEST", "LEVEL:" + (hrLevel+1) + "," + w.cttLevelHrList.toString() +",STAGE:"+w.currentStage.get() +",時間："+ w.elapsedTime.get());


            //判斷 當前 HR 小於等於 上一個 HR = 測試失敗
            if (w.cttLevelHrList.size() > 1) {
                double currentHr = w.cttLevelHrList.get(hrLevel);
                double previousHr = w.cttLevelHrList.get(hrLevel - 1);

//                Log.d("CttPrediction_TEST", "當前: " + w.cttLevelHrList.get(hrLevel));
//                Log.d("CttPrediction_TEST", "上一個: " + w.cttLevelHrList.get(hrLevel - 1));

              //  if (currentHr <= previousHr) {
                if (currentHr <= previousHr) {
                    Log.d("CttPrediction_TEST", "當前HR: " + currentHr + ",小於等於 上一個HR:" + previousHr + ",測試失敗");
                    m.finishWorkout(false);
                }

            }
            hrLevel++;
        }

    }

    int hrLevel;

    @Override
    public void out() {
        //如果連續30秒未偵測到心跳, 就要顯示訊息「未偵測到心跳 , 10秒後結束」
        m.noHrCheck();
//        if (w.currentHeartRate.get() < NO_HR_HR) {
//            noHrTime++;
//            if (noHrTime > 30) {
//                m.showWarring(WARRING_NO_HR);
//            }
//        } else {
//            noHrTime = 0;
//        }

        // 若是級別結束的HR低於前級別的HR, 則提前結束訓練(測試無效)
    }


    int hr85Time;

    @Override
    public void target() {

        //CTT_PREDICTION 不能超過level6 時間到就會離開
        if (w.remainingTime.get() <= 0) {
            Log.d("CttPrediction_TEST", "時間到: ");
            w.setWorkoutDone(true);
            new RxTimer().timer(500, number -> m.initCoolDown());
        }

        if (w.cttLevelHrList.size() >= 6) {
            if (!m.isWorkoutTimerRunning) return;
            w.setWorkoutDone(true);
            new RxTimer().timer(500, number -> m.initCoolDown());
        }


        //到達目標HR80%，超過15秒
        if (!m.isWorkoutTimerRunning) return;
        if (w.currentHeartRate.get() > w.selTargetHrBpm.get()) {
            hr85Time++;
            if (hr85Time > 15) {
                m.isWorkoutTimerRunning = false;
                m.showWarring(WARRING_HR_REACHED_TARGET);
                w.setWorkoutDone(true);
            }
        } else {
            hr85Time = 0;
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



        m.noHrCheck();
    }

    @Override
    public void cancelTimer() {

    }
}
