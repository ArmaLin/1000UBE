package com.dyaco.spirit_commercial.workout.programs;

import static com.dyaco.spirit_commercial.MainActivity.isUs;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.getInclineLevel;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.getSpeedLevelNum;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.setSpeedDiagram;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.setTreadmillInclineDiagram;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.WARRING_HR_REACHED_TARGET;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.BAR_COUNT_GERKIN;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.THR_CONSTANT;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.THR_PERCENTAGE_85;

import android.util.Log;
import android.view.View;

import com.dyaco.spirit_commercial.UartConsoleManagerPF;
import com.dyaco.spirit_commercial.support.RxTimer;
import com.dyaco.spirit_commercial.support.WorkoutUtil;
import com.dyaco.spirit_commercial.support.intdef.DeviceIntDef;
import com.dyaco.spirit_commercial.viewmodel.WorkoutViewModel;
import com.dyaco.spirit_commercial.workout.MainWorkoutTrainingFragment;
import com.dyaco.spirit_commercial.workout.WorkoutChartsFragment;
import com.dyaco.spirit_commercial.workout.WorkoutStatsFragment;

/**
 * Target : 到達THR 或 完成所有Stage
 */
public class Gerkin implements IPrograms {
    private final String TAG = "GERKIN_TEST";
    float gerkinSpeedUpdateNum = 4.5f;
    int gerkinInclineUpdateNum = 2;
    WorkoutViewModel w;
    MainWorkoutTrainingFragment m;
    UartConsoleManagerPF u;
    private int noHrTime;
    int hr85Time;

    public Gerkin(WorkoutViewModel workoutViewModel, MainWorkoutTrainingFragment mainWorkoutTrainingFragment, UartConsoleManagerPF u) {
        this.w = workoutViewModel;
        this.m = mainWorkoutTrainingFragment;
        this.u = u;
    }

    WorkoutChartsFragment chartsFragment;
    @Override
    public void initChart(WorkoutChartsFragment c) {
        // Segment更新的時間 Gerkin 15秒1格
        c.timeTick = 15;

      //  setMinSpeedDiagram(w);
        setSpeedDiagram(w, getSpeedLevelNum(3.0f, DeviceIntDef.IMPERIAL)); //初始圖形
        setTreadmillInclineDiagram(w);

        //44格
        c.diagramBarsView.setBarCount(BAR_COUNT_GERKIN);

        chartsFragment = c;
    }

    @Override
    public void initStats(WorkoutStatsFragment s) {
        s.showStatsGroup(s.HEART_TARGET_STATS);
    }

    @Override
    public void init() {
        //顯示下方STOP按紐
        m.showBottomStopButton();
        m.hideBtnCoolDown();
        m.hideBtnSkip();
        m.hideBtnWorkoutStop();

        w.selWorkoutTime.set(11 * 60);

        w.warmUpTime.set(60 * 3); //warm up 3分鐘
     //   w.warmUpTime.set(3); //warm up 3分鐘
      //  w.currentHeartRate.set(150);

        //HR 85%
        w.selTargetHrBpm.set((THR_CONSTANT - w.selYO.get()) * THR_PERCENTAGE_85 / 100);

      //  w.currentStage.set(1);

        //MaxIncline給最大值
        m.setMaxInclineMax();

        w.disabledInclineUpdate.set(true);//不能調整Incline
        w.disabledSpeedUpdate.set(true);//不能調整Speed

        m.disappearTreadmillControl(m.PANEL_ALL_DISAPPEAR);


        if (isUs) {
            m.usWorkoutStopLong();
            m.getBinding().cStatsUsNoView.setVisibility(View.VISIBLE);

            m.getBinding().groupTopNumberUs.setVisibility(View.GONE);
            m.getBinding().groupBottomNumberUs.setVisibility(View.GONE);
        }

    }

    @Override
    public void warmUp(long time) {
        if (time == w.warmUpTime.get()) {
            //測試以 3 分鐘的熱身開始
            //速度將設置為 3mph，坡度為 0%。
            m.warmUpSpeedUpdate(getSpeedLevelNum(3.0f, DeviceIntDef.IMPERIAL));
            u.setDevSpeedAndIncline();
        }

        m.noHrCheck();
    }

    @Override
    public void calcDistance(double distanceAccumulate) {
    }

    @Override
    public void runTime() {
        //1Stage == 4Segment，共11Stage 44Segment

        //一開始 -> 4.5mph，每2min ->Speed +0.5mph
        if (WorkoutUtil.checkGerkinSpeedUpdate(w.currentStage.get())) {
            m.updateSpeedOrLevelNum(getSpeedLevelNum(gerkinSpeedUpdateNum, DeviceIntDef.IMPERIAL), true);
            gerkinSpeedUpdateNum += 0.5f;
        }

        //1、3、5、7、9min -> incline +2%
        if (WorkoutUtil.checkGerkinInclineUpdate(w.currentSegment.get())) {
            m.updateInclineNum(getInclineLevel(gerkinInclineUpdateNum), true);
            gerkinInclineUpdateNum += 2;
        }
    }

    @Override
    public void out() {
        //如果連續30秒未偵測到心跳, 就要顯示訊息「未偵測到心跳 , 10秒後結束」
        m.noHrCheck();
//        if (w.currentHeartRate.get() < NO_HR_HR) {
//            noHrTime++;
//            Log.d(TAG, "out:目前心跳、"+w.currentHeartRate.get()+"心跳小於"+NO_HR_HR+": 時間："+ noHrTime);
//
//            if (noHrTime > 30) {
//                m.showWarring(WARRING_NO_HR);
//            }
//        } else {
//            noHrTime = 0;
//        }
    }

    @Override
    public void target() {

        //到達目標HR 或 完成所有Stage  44

        if (w.remainingTime.get() <= 0) {
            if (!m.isWorkoutTimerRunning) return;
            Log.d(TAG, "時間到: ");
            setCoolDownTime();
            new RxTimer().timer(300, number -> m.initCoolDown());

            w.setWorkoutDone(true);

            try {
                if (chartsFragment != null && chartsFragment.chartMsgStageWindow != null) {
                    chartsFragment.chartMsgStageWindow.dismiss();
                    chartsFragment.chartMsgStageWindow = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

//        if (w.currentSegment.get() >= BAR_COUNT_GERKIN) {
//            setCoolDownTime();
//            m.initCoolDown();
//            return;
//        }


        //到達目標HR85%，超過15秒
        if (!m.isWorkoutTimerRunning) return;
        if (w.currentHeartRate.get() > w.selTargetHrBpm.get()) {
            hr85Time++;
            Log.d(TAG, "target:到達目標HR: " + hr85Time +"秒");
            if (hr85Time > 15) {
                setCoolDownTime();
                m.isWorkoutTimerRunning = false;
                m.showWarring(WARRING_HR_REACHED_TARGET);
                w.setWorkoutDone(true);
            }
        } else {
            hr85Time = 0;
        }

    }

    private void setCoolDownTime() {
        //cool-down 時間取決於完成的總時間, 如果在 5 分鐘以內, cool-down 1 分鐘 如果在 5~12 分鐘, cool-down 3 分鐘
        if (w.elapsedTime.get() < (60 * 5)) {
            //  WorkoutIntDef.COOL_DOWN_TIME = 60;
            w.coolDownTime.set(60);
        } else {
            w.coolDownTime.set(180);
            //   WorkoutIntDef.COOL_DOWN_TIME = 180;
        }
    }

    @Override
    public void coolDown(long time) {

        if (time == w.coolDownTime.get()) {
            //incline 設定為 0%, 速度設定為 3.0MPH
            m.warmUpSpeedAndIncline(getSpeedLevelNum(3.0f, DeviceIntDef.IMPERIAL),0);
            u.setDevSpeedAndIncline();
        }
    }

    @Override
    public void cancelTimer() {

    }
}
