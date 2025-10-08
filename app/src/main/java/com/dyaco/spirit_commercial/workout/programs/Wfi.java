package com.dyaco.spirit_commercial.workout.programs;

import static com.dyaco.spirit_commercial.MainActivity.isUs;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.getInclineLevel;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.getSpeedLevelNum;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.setMinSpeedDiagram;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.setTreadmillInclineDiagram;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.WARRING_HR_REACHED_TARGET;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.THR_CONSTANT;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.THR_PERCENTAGE_85;

import com.dyaco.spirit_commercial.listener.IUartConsole;
import com.dyaco.spirit_commercial.support.RxTimer;
import com.dyaco.spirit_commercial.support.intdef.DeviceIntDef;
import com.dyaco.spirit_commercial.viewmodel.WorkoutViewModel;
import com.dyaco.spirit_commercial.workout.MainWorkoutTrainingFragment;
import com.dyaco.spirit_commercial.workout.WorkoutChartsFragment;
import com.dyaco.spirit_commercial.workout.WorkoutStatsFragment;

/**
 * Target : 到達THR 或 到達目標時間 18分鐘
 * <p>
 * 每分鐘有對應的Speed和坡度
 * WFI遇以下任一情況, 結束訓練:
 * (1)按下STOP(沒分數, 未通過測驗), 不必經過cool down
 * (2)超過THR連續15秒, TT = 開始訓練 到 HR大於THR 15秒之前 的秒數(不含連續15秒的THR)
 * 例如在 5:35達成高於THR 15秒, 則TT = 5*60+(35-15)=320秒 (通過測驗),  要經過cool down
 * (3)未達到THR, 但測試結束, TT = 18分鐘=1080秒 (通過測驗), 要經過cool down
 * (4)如果偵測不到心跳超過30秒, 則結束訓練 (沒分數, 未通過測驗)
 * 以TT計算VO2
 */
public class Wfi implements IPrograms {
    WorkoutViewModel w;
    MainWorkoutTrainingFragment m;
    private int noHrTime;
    private int hr85Time;
    IUartConsole u;

    public Wfi(WorkoutViewModel workoutViewModel, MainWorkoutTrainingFragment mainWorkoutTrainingFragment,IUartConsole u) {
        this.w = workoutViewModel;
        this.m = mainWorkoutTrainingFragment;
        this.u = u;
    }

    @Override
    public void initChart(WorkoutChartsFragment c) {

        c.diagramBarsView.setBarCount(18);
        c.timeTick = 60;
        setMinSpeedDiagram(w);
        setTreadmillInclineDiagram(w);
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

     //   w.currentStage.set(0);

        w.selTargetHrBpm.set((THR_CONSTANT - w.selYO.get()) * THR_PERCENTAGE_85 / 100);//目標心跳 85%
        w.selWorkoutTime.set(18 * 60);//時間18分鐘

        w.disabledInclineUpdate.set(true);//不能調整Incline
        w.disabledSpeedUpdate.set(true);//不能調整Speed

        m.disappearTreadmillControl(m.PANEL_ALL_DISAPPEAR);

        w.coolDownTime.set(3 * 60);//cool down 3分鐘

        //MaxIncline給最大值
        m.setMaxInclineMax();

        if (isUs) {
            m.usWorkoutStopLong();
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

        //第1~3根, speed:3, incline:0, 00:00 ~ 01:00
        if (w.elapsedTime.get() == 1) {
            w.currentStage.set(1);
            m.updateSpeedOrLevelNum(getSpeedLevelNum(3.0f, DeviceIntDef.IMPERIAL), true);
           // m.updateInclineNum(10, true);
            return;
        }

//        //01:01~02:00
        if (w.elapsedTime.get() == 61) {
            w.currentStage.set(2);
        }
//
//        //02:01~03:00
        if (w.elapsedTime.get() == 121) {
            w.currentStage.set(3);
        }

        //4根, speed:4.5, incline:0, 03:01~04:00
        if (w.elapsedTime.get() == 181) {
            w.currentStage.set(4);
            m.updateSpeedOrLevelNum(getSpeedLevelNum(4.5f, DeviceIntDef.IMPERIAL), true);
            return;
        }

        //5根, speed:4.5, incline:2, 04:01~05:00
        if (w.elapsedTime.get() == 241) {
            w.currentStage.set(5);
            m.updateInclineNum(getInclineLevel(2), true);
            return;
        }

        //6根, speed:5, incline:2, 05:01~06:00
        if (w.elapsedTime.get() == 301) {//6,s5,i2
            w.currentStage.set(6);
            m.updateSpeedOrLevelNum(getSpeedLevelNum(5.0f, DeviceIntDef.IMPERIAL), true);
            return;
        }

        //7, speed:4.5, incline:4, 06:01~07:00
        if (w.elapsedTime.get() == 361) {//7,s5,i4
            w.currentStage.set(7);
            m.updateInclineNum(getInclineLevel(4), true);
            return;
        }

        //8, speed:5, incline:4, 07:01~08:00
        if (w.elapsedTime.get() == 421) {//8,s5.5,i4
            w.currentStage.set(8);
            m.updateSpeedOrLevelNum(getSpeedLevelNum(5.0f, DeviceIntDef.IMPERIAL), true);
        }

        //9, speed:5, incline:4, 08:01~09:00
        if (w.elapsedTime.get() == 481) {//9,s5,i4
            w.currentStage.set(9);
            m.updateInclineNum(getInclineLevel(6), true);
        }

        //10, speed:6, incline:4, 09:01~10:00
        if (w.elapsedTime.get() == 541) {//10,s5,i4
            w.currentStage.set(10);
            m.updateSpeedOrLevelNum(getSpeedLevelNum(6.0f, DeviceIntDef.IMPERIAL), true);
        }

        //11, speed:6, incline:8, 10:01~11:00
        if (w.elapsedTime.get() == 601) {//11,s5,i4
            w.currentStage.set(11);
            m.updateInclineNum(getInclineLevel(8), true);
        }

        //12, speed:6.5, incline:8, 11:01~12:00
        if (w.elapsedTime.get() == 661) {//12,s5,i4
            w.currentStage.set(12);
            m.updateSpeedOrLevelNum(getSpeedLevelNum(6.5f, DeviceIntDef.IMPERIAL), true);
        }

        //13, speed:6.5, incline:10, 12:01~13:00
        if (w.elapsedTime.get() == 721) {//13,s5,i4
            w.currentStage.set(13);
            m.updateInclineNum(getInclineLevel(10), true);
        }

        //14, speed:7, incline:10, 13:01~14:00
        if (w.elapsedTime.get() == 781) {//14,s5,i4
            w.currentStage.set(14);
            m.updateSpeedOrLevelNum(getSpeedLevelNum(7.0f, DeviceIntDef.IMPERIAL), true);
        }

        //15, speed:7, incline:12, 14:01~15:00
        if (w.elapsedTime.get() == 841) {//15,s5,i4
            w.currentStage.set(15);
            m.updateInclineNum(getInclineLevel(12), true);
        }

        //16, speed:7.5, incline:12, 15:01~16:00
        if (w.elapsedTime.get() == 901) {//16,s5,i4
            w.currentStage.set(16);
            m.updateSpeedOrLevelNum(getSpeedLevelNum(7.5f, DeviceIntDef.IMPERIAL), true);
        }

        //17, speed:7.5, incline:14, 16:01~17:00
        if (w.elapsedTime.get() == 961) {//17,s5,i4
            w.currentStage.set(17);
            m.updateInclineNum(getInclineLevel(14), true);
        }

        //18, speed:8, incline:14, 17:01~18:00
        if (w.elapsedTime.get() == 1021) {//18,s5,i4
            w.currentStage.set(18);
            m.updateSpeedOrLevelNum(getSpeedLevelNum(8.0f, DeviceIntDef.IMPERIAL), true);
        }

    }

    @Override
    public void out() {
        //如果連續30秒未偵測到心跳結束
        m.noHrCheck();
//        if (w.currentHeartRate.get() < NO_HR_HR) {
//            noHrTime++;
//            if (noHrTime > 30) {
//                m.isWorkoutTimerRunning = false;
//                m.showWarring(WARRING_NO_HR);
//            }
//        } else {
//            noHrTime = 0;
//        }
    }

    @Override
    public void target() {

//        a. 按下 stop, 沒有分數
//        b. 心率超過最大值的 85%持續 15 秒。 這種情況下的總時間(TT)將是 15 秒計數開始的時間。
//        c. 測試完成了整個 18 分鐘(TT = 18)
//        d. 如果 30 秒未檢測到心率。


        //到達18分鐘
        if (w.remainingTime.get() <= 0) {
      //  if (w.remainingTime.get() <= 1075) {
            w.setWfiTT(18 * 60);
            m.isWorkoutTimerRunning = false;
            new RxTimer().timer(300, number -> m.initCoolDown());
            w.setWorkoutDone(true);
        }

        //到達目標HR85%，超過15秒
        if (!m.isWorkoutTimerRunning) return;
        if (w.currentHeartRate.get() > w.selTargetHrBpm.get()) {
            w.setWfiTT(w.elapsedTime.get());
            hr85Time++;
            if (hr85Time > 15) {
                m.isWorkoutTimerRunning = false;
                w.setWfiTT(w.getWfiTT() - 15);
                m.showWarring(WARRING_HR_REACHED_TARGET);
                w.setWorkoutDone(true);
            }
        } else {
            w.setWfiTT(0);
            hr85Time = 0;
        }
    }

    @Override
    public void coolDown(long time) {
        //incline 設定為 0%, 速度設定為 3.0MPH
        if (time == w.coolDownTime.get()) {
            m.warmUpSpeedAndIncline(getSpeedLevelNum(3.0f, DeviceIntDef.IMPERIAL),0);
            u.setDevSpeedAndIncline();
//            m.warmUpInclineUpdate(0);
//            m.warmUpSpeedUpdate(getSpeedLevelNum(3.0f, DeviceIntDef.IMPERIAL));
        }
    }

    @Override
    public void cancelTimer() {

    }
}
