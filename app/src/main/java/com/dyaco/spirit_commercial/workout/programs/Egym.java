//package com.dyaco.spirit_commercial.workout.programs;
//
//import static com.dyaco.spirit_commercial.MainActivity.isTreadmill;
//import static com.dyaco.spirit_commercial.MainActivity.isUs;
//import static com.dyaco.spirit_commercial.support.WorkoutUtil.getMinSpeedLevel;
//import static com.dyaco.spirit_commercial.support.WorkoutUtil.setTreadmillInclineDiagram;
//import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.CONSOLE_SYSTEM_SPIRIT;
//import static com.dyaco.spirit_commercial.support.intdef.EventKey.EGYM_SETS_DATA_UPDATE_REAL;
//
//import android.util.Log;
//import android.view.View;
//
//import com.corestar.calculation_libs.Calculation;
//import com.dyaco.spirit_commercial.egym.EgymUtil;
//import com.dyaco.spirit_commercial.listener.IUartConsole;
//import com.dyaco.spirit_commercial.model.webapi.bean.EgymTrainingPlans;
//import com.dyaco.spirit_commercial.viewmodel.DeviceSettingViewModel;
//import com.dyaco.spirit_commercial.viewmodel.EgymDataViewModel;
//import com.dyaco.spirit_commercial.viewmodel.WorkoutViewModel;
//import com.dyaco.spirit_commercial.workout.MainWorkoutTrainingFragment;
//import com.dyaco.spirit_commercial.workout.WorkoutChartsFragment;
//import com.dyaco.spirit_commercial.workout.WorkoutStatsFragment;
//import com.jeremyliao.liveeventbus.LiveEventBus;
//
//import java.util.Arrays;
//import java.util.IntSummaryStatistics;
//
//public class Egym implements IPrograms {
//    Calculation calc;
//    WorkoutViewModel w;
//    EgymDataViewModel e;
//    MainWorkoutTrainingFragment m;
//    WorkoutChartsFragment c;
//    IUartConsole u;
//    DeviceSettingViewModel d;
//
//    public Egym(WorkoutViewModel workoutViewModel, MainWorkoutTrainingFragment mainWorkoutTrainingFragment, WorkoutChartsFragment c, IUartConsole u, EgymDataViewModel e, Calculation calc, DeviceSettingViewModel d) {
//        this.w = workoutViewModel;
//        this.m = mainWorkoutTrainingFragment;
//        this.c = c;
//        this.u = u;
//        this.e = e;
//        this.calc = calc;
//        this.d = d;
//    }
//
//    @Override
//    public void initChart(WorkoutChartsFragment c) {
//
//
//        //Incline初始值
//        setTreadmillInclineDiagram(w); //orgArrayIncline
//
//        //profile中最大的值
//        IntSummaryStatistics sIncline = Arrays.stream(w.orgArrayIncline).summaryStatistics();
//        w.orgMaxInclineInProfile.set(sIncline.getMax());
//
//
//        w.orgArraySpeedAndLevel = new int[e.setsTimePosition.get(e.setsTimePosition.size() - 1) + 1]; // 陣列大小為最後一個時間位置
//        w.orgArrayIncline = new int[e.setsTimePosition.get(e.setsTimePosition.size() - 1) + 1]; // 陣列大小為最後一個時間位置
//
//        int startIndex = 0; // 起始索引
//
//        for (int i = 0; i < e.setsTimePosition.size(); i++) {
//            int endIndex = e.setsTimePosition.get(i); // 取得該區間的結束索引
//
//            // 檢查 selTrainer 是否為 null
//            if (e.selTrainer == null || e.selTrainer.getIntervals() == null || e.selTrainer.getIntervals().size() <= i) {
//                Log.e("CommonPrograms", "selTrainer 或 Intervals 為 null，或索引超出範圍！");
//                continue; // 直接跳過此迴圈
//            }
//
//            // 取得當前 interval
//            EgymTrainingPlans.TrainerDTO.IntervalsDTO interval = e.selTrainer.getIntervals().get(i);
//            if (interval == null) {
//                Log.e("CommonPrograms", "interval 為 null，索引：" + i);
//                continue;
//            }
//
//            // 檢查 speed 和 incline 是否為 null
//            Double speed = interval.getSpeed();
//            Double incline = interval.getIncline();
//
//            // 避免 NullPointerException，使用 0 作為預設值
////                    int speedValue = (int) ((speed != null ? speed : 0) * 10);
//            //如果是Null 就用Console最小速度
//            int speedValue = (int) ((speed != null ? speed * 10 : getMinSpeedLevel(w.selProgram)));
//            int inclineValue = (int) ((incline != null ? incline : 0) * 2);
//
//            for (int j = startIndex; j <= endIndex; j++) {
//                w.orgArraySpeedAndLevel[j] = speedValue;
//                w.orgArrayIncline[j] = inclineValue;
//            }
//
//            startIndex = endIndex + 1; // 更新下一區間的起始索引
//        }
//    }
//
//    @Override
//    public void initStats(WorkoutStatsFragment s) {
//        s.showStatsGroup(s.EGYM_STATS);
//
//        s.initEgymInterval();
//    }
//
//    @Override
//    public void init() {
//        //MaxIncline給最大值
//        m.setMaxInclineMax();
//        m.hideBtnSkip();
//        if (isUs) {
//            m.usWorkoutStopLong();
//
//            //     if (isTreadmill) {
//            m.getBinding().cStatsUsNoView.setVisibility(View.GONE);
//            //     }
////                m.getBinding().btnWorkoutStopUs.setLayoutParams(LayoutParams(232, 400));
//            if (isTreadmill) {
//                m.getBinding().groupTopNumberUs.setVisibility(View.VISIBLE);
//                m.getBinding().groupBottomNumberUs.setVisibility(View.VISIBLE);
////                } else {
////                    m.getBinding().tvTopTextUs.setText(R.string.direct_level);
////                    m.getBinding().groupTopNumberUs.setVisibility(View.VISIBLE);
////                    m.getBinding().groupBottomNumberUs.setVisibility(View.VISIBLE);
////                    m.getBinding().tvBottomTextUs.setText(R.string.direct_level);
//            }
//        }
//    }
//
//    @Override
//    public void warmUp(long time) {
//
//    }
//
//    @Override
//    public void calcDistance(double distanceAccumulate) {
//
//    }
//
//
//    EgymUtil egymApi = new EgymUtil();
//    int realInterval = 0;
//    double tempDistance = 0;
//    @Override
//    public void runTime() {
//        //存太多會不能上傳和讀取
//
//        if (d.consoleSystem.get() == CONSOLE_SYSTEM_SPIRIT) return;
//
////        //不是Egym Program, 到Summary才存
////        if (w.selProgram != EGYM) return;
//
//
//        w.egymTimePerSets.set(w.egymTimePerSets.get() + 1);
////        Log.d("KKKKEEEE", "initEgymInterval: " + egymDataViewModel.durationTimesList +","+  w.egymCurrentInterval.get());
//
//
//        //原本是換Interval時執行
//        //[4,19]  currentSegment 5 的時候執行
//        //檢查是否換了 Interval,workout結束不檢查
////        if (tmpInterval == w.egymCurrentInterval.get()) return;
////        tmpInterval = w.egymCurrentInterval.get();
//
//        w.egymIntervalDistance.set(calc.getDistanceAccumulate() - tempDistance);
//
////        Log.d("EEERRRQQQQ", "####setEgymCloudIntervalData: " + w.egymIntervalDistance.get() +", " + calc.getDistanceAccumulate() +", "+ tempDistance);
//        //set的時間到時才執行
//        //每個Set在第幾秒 [120, 480]  最後一個+2秒, 因為要到summary才算
//        if (!e.durationRealTimesList.contains(w.elapsedTime.get())) return;
//
//        //秒數到達了下一個Interval------------------------
//
//        //    w.egymIntervalDistance.set(w.egymIntervalDistance.get() - tempDistance);
//
//
//        tempDistance = calc.getDistanceAccumulate();
//
//        //  Log.d("EEERRRQQQQ", "IntervalDistance: " + egymDataViewModel.intervalDistance.get());
//
//        Log.d("KKKKEEEE", "第幾秒會存: " + e.durationRealTimesList + "," + w.elapsedTime.get() + "," + realInterval);
//        egymApi.saveInterval(w, e);
//
//        //每個 set 當前的時間歸0
//        w.egymTimePerSets.set(0);
//
//        realInterval += 1;
//        LiveEventBus.get(EGYM_SETS_DATA_UPDATE_REAL, Integer.class).postDelay(realInterval, 0);
//    }
//
//    @Override
//    public void out() {
//
//    }
//
//    @Override
//    public void target() {
//
//    }
//
//    @Override
//    public void coolDown(long time) {
//
//    }
//
//    @Override
//    public void cancelTimer() {
//
//    }
//}
