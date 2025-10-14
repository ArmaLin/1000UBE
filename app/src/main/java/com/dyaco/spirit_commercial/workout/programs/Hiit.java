package com.dyaco.spirit_commercial.workout.programs;

import static com.dyaco.spirit_commercial.MainActivity.isTreadmill;
import static com.dyaco.spirit_commercial.MainActivity.isUs;

import android.view.View;

import com.dyaco.spirit_commercial.UartConsoleManagerPF;
import com.dyaco.spirit_commercial.viewmodel.WorkoutViewModel;
import com.dyaco.spirit_commercial.workout.MainWorkoutTrainingFragment;
import com.dyaco.spirit_commercial.workout.WorkoutChartsFragment;
import com.dyaco.spirit_commercial.workout.WorkoutStatsFragment;

import java.util.Arrays;

/**
 * 1個間隔 = 1 個衝刺段和 1 個休息段
 */
public class Hiit implements IPrograms {

    UartConsoleManagerPF u;
    WorkoutViewModel w;
    MainWorkoutTrainingFragment m;

    public Hiit(WorkoutViewModel workoutViewModel, MainWorkoutTrainingFragment mainWorkoutTrainingFragment, UartConsoleManagerPF u) {
        this.w = workoutViewModel;
        this.m = mainWorkoutTrainingFragment;
        this.u = u;
    }

    @Override
    public void initStats(WorkoutStatsFragment s) {
        s.showStatsGroup(s.NORMAL_STATS);

        if (isUs) {
            if (isTreadmill) {
                m.getBinding().cStatsUsNoView.setVisibility(View.VISIBLE);
            } else {
                m.getBinding().cStatsUsNoViewBike.setVisibility(View.VISIBLE);
            }

          //  m.getBinding().btnSkipUs.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void initChart(WorkoutChartsFragment c) {
        StringBuilder num = new StringBuilder();
        // Segment更新的時間
        c.timeTick = w.selSprintTimeSec.get();

        //最後一個rest不要
        for (int i = 0; i < w.selIntervals.get(); i++) {
            num.append(w.selSprintSpeedLevel.get()).append("#").append(w.selRestSpeedLevel.get()).append("#");
        }
        num = num.deleteCharAt(num.length() - 1);

        w.orgArraySpeedAndLevel =
                Arrays.stream(num.toString()
                        .split("#", -1))
                        .mapToInt(Integer::parseInt)
                        .toArray();

        StringBuilder num2 = new StringBuilder();
        for (int i = 0; i < w.selIntervals.get(); i++) {
            num2.append("0").append("#").append("0").append("#");
        }
        num2 = num2.deleteCharAt(num2.length() - 1);
        w.orgArrayIncline =
                Arrays.stream(num2.toString()
                        .split("#", -1))
                        .mapToInt(Integer::parseInt)
                        .toArray();

        c.diagramBarsView.setBarCount((w.selIntervals.get() * 2) - 1);
    }


    @Override
    public void init() {
     //   m.hideBtnSkip();

        w.selFirstSprintSpeedLevel.set(w.selSprintSpeedLevel.get());
        // warm-up, cool down: sprint speed / 2 ，各3分鐘
        w.coolDownTime.set(60 * 3);//cool down 3分鐘
        w.warmUpTime.set(60 * 3);//warm up 3分鐘

        //MaxIncline給最大值
        m.setMaxInclineMax();

        if (isUs) {
            m.getBinding().btnSkipUs.setVisibility(View.VISIBLE);

            //SPEED 按鈕顯示
            m.getBinding().groupBottomNumberUs.setVisibility(View.VISIBLE);
            m.getBinding().groupTopNumberUs.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void warmUp(long time) {
        //如果用戶在熱身過程中改變了速度，那麼整個熱身將改變為新的速度。冷卻也是一樣。
        if (time == w.warmUpTime.get()) {
            //warm-up, cool down: sprint speed / 2  ,各3分鐘
            //熱身速度為衝刺速度的 50%

            int halfSprintSpeed = Math.round(w.selFirstSprintSpeedLevel.get() >> 1);
            //  m.updateSpeedOrLevelNum(halfSprintSpeed, true);

            m.warmUpSpeedUpdate(halfSprintSpeed);
            u.setDevSpeedAndIncline();

        }
    }

    @Override
    public void calcDistance(double distanceAccumulate) {
        w.currentDistance.set(distanceAccumulate);
    }

    @Override
    public void runTime() {

//                Log.d("HIIT_PROGRAM",
//                        "目前次數：" + w.currentIntervals.get()
//                                + " 目前時間：" + w.elapsedTime.get()
//                                + "@:衝刺時間: " + ((w.selSprintTimeSec.get() * w.currentIntervals.get())
//                                + (w.selRestTimeSec.get() * w.currentIntervals.get()))
//                                + " @:休息時間 " +((w.selSprintTimeSec.get() * w.currentIntervals.get())
//                                + (w.selRestTimeSec.get() * w.currentIntervals.get())
//                                + w.selSprintTimeSec.get()));
//
//
//        //循環 (120 * 0) + (60 * 0) + 120 + 60 == 180  循環 > 1
//        //循環 (120 * 1) + (60 * 1) + 120 + 60 == 360  循環 > 2
//        //循環 (120 * 2) + (60 * 2) + 120 + 60 == 90  循環 > 3
//        if ((w.selSprintTimeSec.get() * w.currentIntervals.get())
//                + (w.selRestTimeSec.get() * w.currentIntervals.get())
//                + w.selSprintTimeSec.get() + w.selRestTimeSec.get()
//                == w.elapsedTime.get()) {
//
//            w.currentIntervals.set(w.currentIntervals.get() + 1);
//            Log.d("HIIT_PROGRAM", "CC循環CC " + "目前循環次數：" + w.currentIntervals.get() + "," + " 目前時間：" + w.elapsedTime.get());
//
//        }
//
//
//        //衝刺 (120 * 0) + (60 * 0) == 0  || elapsedTime == 1(一開始)
//        //衝刺 (120 * 1) + (60 * 1) == 180
//        //衝刺 (120 * 2) + (60 * 2) == 360
//        if ((w.selSprintTimeSec.get() * w.currentIntervals.get())
//                + (w.selRestTimeSec.get() * w.currentIntervals.get())
//                == w.elapsedTime.get() || w.elapsedTime.get() == 1) {
//
//            //3.2 >> 32(階數)
//            m.updateSpeedOrLevelNum(w.selSprintSpeedLevel.get(), true);
//
//            Log.d("HIIT_PROGRAM", "@@衝刺@@ " + "目前循環次數：" + w.currentIntervals.get() + "," + " 目前時間：" + w.elapsedTime.get());
//        }
//
//        //休息 (120 * 0) + (60 * 0) + 120 == 120
//        //休息 (120 * 1) + (60 * 1) + 120 == 300
//        //休息 (120 * 2) + (60 * 2) + 120 == 80
//        if ((w.selSprintTimeSec.get() * w.currentIntervals.get())
//                + (w.selRestTimeSec.get() * w.currentIntervals.get())
//                + w.selSprintTimeSec.get()
//                == w.elapsedTime.get()) {
//
//
//            //結束
//            if ((w.currentIntervals.get() + 1) == w.selIntervals.get()) {
//                Log.d("PEFPEFPE", "====結束==== " + "目前循環次數：" + w.currentIntervals.get() + "," + " 目前時間：" + w.elapsedTime.get());
//                m.initCoolDown();
//                return;
//            }
//
//            m.updateSpeedOrLevelNum(w.selRestSpeedLevel.get(), true);
//
//            Log.d("HIIT_PROGRAM", "XX休息XX " + "目前循環次數：" + w.currentIntervals.get() + "," + " 目前時間：" + w.elapsedTime.get());
//
//        }


        //1秒 循環0
        //1秒 >衝刺20秒 > 20秒
        //20秒 >休息10秒 > 30秒
        //30秒 循環+1 > 1
        //30秒 >衝刺20秒 > 50秒
        //50秒 >休息10秒 > 60秒
        //50秒 循環+1 > 2
        //60秒 >衝刺20秒 > 80秒
        //80秒 >休息10秒 > 90秒
        //80秒 循環+1 > 3
        //90秒 >衝刺20秒 > 110秒
        //110秒 >休息10秒 > 120秒
        //循環4
    }

    @Override
    public void out() {

    }

    @Override
    public void target() {
        if (w.remainingTime.get() <= 0) {
            m.initCoolDown();
        }
    }

    @Override
    public void coolDown(long time) {
        if (time == w.coolDownTime.get()) {
            //如果用戶在熱身過程中改變了速度，那麼整個熱身將改變為新的速度。冷卻也是一樣。
            int x = Math.round(w.selSprintSpeedLevel.get() >> 1);
            // m.updateSpeedOrLevelNum(x, true);
            m.warmUpSpeedUpdate(x);
            u.setDevSpeedAndIncline();
        }
    }

    @Override
    public void cancelTimer() {

    }
}
