package com.dyaco.spirit_commercial.workout.programs;

import static com.dyaco.spirit_commercial.support.WorkoutUtil.setBikeLevelDiagram;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.DEVICE_TYPE_ELLIPTICAL;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.INVALID_TEST_HR_OUT_OF_RANGE;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.MALE;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.WARRING_HR_REACHED_TARGET;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.THR_CONSTANT;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.THR_PERCENTAGE_85;

import android.util.Log;

import com.corestar.calculation_libs.Calculation;
import com.dyaco.spirit_commercial.App;
import com.dyaco.spirit_commercial.viewmodel.WorkoutViewModel;
import com.dyaco.spirit_commercial.workout.MainWorkoutTrainingFragment;
import com.dyaco.spirit_commercial.workout.WorkoutChartsFragment;
import com.dyaco.spirit_commercial.workout.WorkoutStatsFragment;

public class FitnessTest implements IPrograms {
    private final String TAG = "FITNESS_TEST_PROGRAM";
    WorkoutViewModel w;
    MainWorkoutTrainingFragment m;
    Calculation calc;
    WorkoutChartsFragment c;

    boolean isStageExtend; //stage延長
    int finalStage = 4;

    private int nextStageTime = 180;
    private static final int NORMAL_STAGE_TIME = 180;
    private static final int EXTEND_TIME = 60;

//    private int nextStageTime = 40;
//    private static final int NORMAL_STAGE_TIME = 40;
//    private static final int EXTEND_TIME = 10;

    private boolean isMale = true;
    private boolean isElliptical;

    public FitnessTest(WorkoutViewModel workoutViewModel, MainWorkoutTrainingFragment mainWorkoutTrainingFragment, Calculation calc) {
        this.w = workoutViewModel;
        this.m = mainWorkoutTrainingFragment;
        this.calc = calc;
    }

    @Override
    public void initChart(WorkoutChartsFragment c) {
        setBikeLevelDiagram(w); //根據ProgramEnum 設定 orgArraySpeedAndLevel 的個數
        this.c = c;
        c.timeTick = nextStageTime;

        isElliptical = App.MODE.getTypeCode() == DEVICE_TYPE_ELLIPTICAL;

        isMale = w.selGender.get() == MALE;

        finalStage = isElliptical ? 4 : (isMale ? 3 : 4);

        c.diagramBarsView.setBarCount(finalStage); //圖表個數
    }

    @Override
    public void initStats(WorkoutStatsFragment s) {
        s.showStatsGroup(s.NORMAL_STATS);
    }

    @Override
    public void init() {
//        w.coolDownTime.set(60 * 3);
//        w.warmUpTime.set(60 * 3);
        w.selTargetHrBpm.set((THR_CONSTANT - w.selYO.get()) * THR_PERCENTAGE_85 / 100);//目標心跳 85%
        w.selHrMax.set(THR_CONSTANT - w.selYO.get());//目標心跳 85%
        w.currentStage.set(1);
        w.disabledLevelUpdate.set(true);
        Log.d(TAG, "FITNESS_TEST_PROGRAM init ,THR:" + w.selTargetHrBpm.get() +",HR MAX:"+w.selHrMax.get());

    }

    @Override
    public void warmUp(long time) {

    }

    @Override
    public void calcDistance(double distanceAccumulate) {
    }

    @Override
    public void runTime() {

        // Log.d(TAG, "VO2Max: Vo2SM1:" + w.getVo2SM1() + ", StageHR1:" + w.getStageHR1() + ", Vo2SM2:" + w.getVo2SM2() + ", StageHR2:" + w.getStageHR2() + ", THR:" + w.selTargetHrBpm.get() +", vO2Max :"+ calc.getVo2Max(8, 131, 11, 131, 157));

        //進入下一個Stage
        Log.d(TAG, "Stage" + (int)w.currentStage.get() + ":" + w.elapsedTime.get() + "sec, "+(((int)w.currentStage.get()) != finalStage ? "NextStage" + ((int)w.currentStage.get() + 1) : "TEST結束時間") + ":" + nextStageTime +" sec, StageHr1:"+w.getStageHR1() + ", StageHr2:"+w.getStageHR2());
        if (w.elapsedTime.get() == nextStageTime) {

            Log.d(TAG, "*******進入下一個STAGE*******");
            Log.d(TAG, "Stage" + (int)w.currentStage.get() + " >>>>> Stage" + ((int)w.currentStage.get() + 1));

            isStageExtend = false;
            if (w.currentStage.get() == 1) { //Stage1結束
//                //當Stage1 結束前，HR＞THR 時，顯示字串『INVALID TEST- HR OUT OF RANGE』，60 秒後回到 Idle Mode.
//                if (w.currentHeartRate.get() > w.selTargetHrBpm.get()) {
//                    Log.d(TAG, "<當Stage1 結束前，HR＞THR, 測試失敗>目前心跳:" + w.currentHeartRate.get() + ", 目標心跳:" + w.selTargetHrBpm.get() + " ,當Stage1 結束前，HR＞THR 時，顯示字串『INVALID TEST- HR OUT OF RANGE』，60 秒後回到 Idle Mode.");
//                    m.showWarring(INVALID_TEST_HR_OUT_OF_RANGE);
//                    return;
//                }

                //3.1.7.3.2 當Stage1 結束時計算並紀錄 vO2SM,定義為V02SM2，記錄 Heart Rate，紀錄為StageHR2,進入 Stage2
                w.setVo2SM2((int) calc.getVo2Sm(w.constantPowerW.get() * 6));
                w.setStageHR2(w.currentHeartRate.get());
                Log.d(TAG, "<#1# Stage1 取得:Vo2SM2,StageHR2> 3.1.7.3.2 當 Stage 1 結束時計算並紀錄 vO2SM, power:" + w.constantPowerW.get() + ", 定義為V02SW2(" + w.getVo2SM2() + ")，記錄 Heart Rate，紀錄為StageHR2(" + w.getStageHR2() + "),進入Stage 2");
            }

            //進入下一個Stage
            nextStageTime = nextStageTime + NORMAL_STAGE_TIME;
            c.timeTick = nextStageTime;
            w.currentStage.set((w.currentStage.get() + 1));


            //3.1.7.4.4 將前一個Stage 的VO2SM2 定義為VO2SM1 , StageHR2定義為StageHR1
            w.setVo2SM1(w.getVo2SM2());
            w.setStageHR1(w.getStageHR2());
            Log.d(TAG, "<#2# @@@設定 StageHR1@@@ Stage" + (int)w.currentStage.get() + " 取得 VO2SM1, StageHR1> 3.1.7.4.4 將前一個Stage 的 V02SM2(" + w.getVo2SM2() + ") 定義為VO2SM1(" + w.getVo2SM1() + "), StageHR2(" + w.getStageHR2() + ") 定義為 StageHR1 (" + w.getStageHR2() + ")");


            Log.d(TAG, "****************************");

          //  return;
        }

        //更新Watt
        updateWatt();

        //3.4.9.1 紀錄 Stage 2~4 的第2分鐘結束時的Heart Rate, 定義為Min2HR.
        if (w.elapsedTime.get() == nextStageTime - EXTEND_TIME && w.currentStage.get() != 1 && !isStageExtend) {
            w.setMin2HR(w.currentHeartRate.get());
            Log.d(TAG, "<#3# 取得Min2HR> 紀錄 Stage" + (int)w.currentStage.get() + " 的第2分鐘結束時的Heart Rate, 定義為Min2HR(" + w.getMin2HR() + ").");
            //  return;
        }

        //3.4.9.2 紀錄 Stage2~4 的第3分鐘結束時的 Heart Rate, 定義為Min3HR.
        //第3分鐘前先判斷
        if ((w.elapsedTime.get() == nextStageTime - 1) && w.currentStage.get() != 1) {

            if (!isStageExtend) { //尚未延長過才判斷

                w.setMin3HR(w.currentHeartRate.get());
                Log.d(TAG, "<#4# 取得Min3HR> 紀錄Stage" + (int)w.currentStage.get() + " 的第3分鐘結束時的 Heart Rate, 定義為Min3HR(" + w.getMin3HR() + ")");

                //3.4.9.3 若 Min3HR > Min2HR-6時，Stage 2 延長1分鐘
                if (w.getMin3HR() > (w.getMin2HR() - 6)) {
                    Log.d(TAG, "<#5# 延長一分鐘> 3.4.9.3 若 Min3HR(" + w.getMin3HR() + ") > Min2HR-6 (" + (w.getMin2HR() - 6) + ")時，Stage" + w.currentStage.get() + " 延長1分鐘");
                    nextStageTime = nextStageTime + EXTEND_TIME;//延長一分鐘
                    c.timeTick = nextStageTime;
                    isStageExtend = true;
                } else {
                    //stage結束前
                    ruleStage2_4_end();
                }
            } else {
                //stage結束前
                ruleStage2_4_end();
            }
        }
    }

    //Stage 2~4 結束前
    private void ruleStage2_4_end() {

//        //3.1.7.4.4 將前一個Stage 的VO2SM2 定義為VO2SM1 , StageHR2定義為StageHR1
//        w.setVo2SM1(w.getVo2SM2());
//        w.setStageHR1(w.getStageHR2());
//        Log.d(TAG, "<#5# Stage" + (int)w.currentStage.get() + " 取得 VO2SM1, StageHR1> 3.1.7.4.4 將前一個Stage 的 V02SM2(" + w.getVo2SM2() + ") 定義為VO2SM1(" + w.getVo2SM1() + "), StageHR2(" + w.getStageHR2() + ") 定義為 StageHR1 (" + w.getStageHR2() + ")");

        // 3.1.7.4.5 當Stage2~4 結束時計算並紀錄 VO2SM, 定義為VO2SM2, 記錄Heart Rate, 紀錄為 StageHR2, 計算VO2Max
        w.setVo2SM2((int) calc.getVo2Sm(w.constantPowerW.get() * 6));
        w.setStageHR2(w.currentHeartRate.get());
        Log.d(TAG, "<#6# Stage" + (int)w.currentStage.get() + " 取得 VO2Max, StageHR2> 3.1.7.4.5 當 Stage" + (int)w.currentStage.get() + " 結束時計算並紀錄 vO2SM, power:" + w.constantPowerW.get() + ", 定義為V02SW2(" + w.getVo2SM2() + ")，Heart Rate紀錄為StageHR2(" + w.getStageHR2() + "),進入Stage" + ((int)w.currentStage.get() + 1));

        w.setVo2Max(calc.getVo2Max(w.getVo2SM1(), w.getStageHR1(), w.getVo2SM2(), w.getStageHR2(), w.selHrMax.get()));
        Log.d(TAG, "<#7# Stage" + (int)w.currentStage.get() + " 取得 VO2Max> VO2Max: Vo2SM1:" + w.getVo2SM1() + ", StageHR1:" + w.getStageHR1() + ", Vo2SM2:" + w.getVo2SM2() + ", StageHR2:" + w.getStageHR2() + ", THR:" + w.selTargetHrBpm.get() + ", vO2Max:" + w.getVo2Max());

        Log.d(TAG, "$$$$$$判斷 StageHR1: "+w.getStageHR1()+" 是否大於 110，及小於THR:"+w.selTargetHrBpm.get()+"$$$$$$$");

        //3.1.7.4.6 當Stage2~4 結束時, 若 StageHR1>110bpm , StageHR1<  85%MaxHR 時, 程式結束, 顯示字串『YOUR SCORE  XXX.X』, 60秒後回到Idle Mode
        if (w.getStageHR1() > 110 && w.getStageHR1() < w.selTargetHrBpm.get()) {
            Log.d(TAG, "<目標達成算分數 >3.1.7.4.6 當Stage(" + (int)w.currentStage.get() + ")結束時，若 StageHR1(" + w.getStageHR1() + ")>110bpm, StageHR1(" + w.getStageHR1() + ")< 85%MaxHR(" + w.selTargetHrBpm.get() + ") 時，程式結束，顯示宇串『YOUR SCORE XXX.X』,60 秒後回到 Idle Mode.");
            m.showWarring(WARRING_HR_REACHED_TARGET);
            return;
        }

        //3.1.7.4.8 當Stage全部結束時, Heart Rate沒有在前兩項條件內時, 顯示字串『INVALID TEST-HR OUT OF RANGE』, 60秒後回到Idle Mode
        if (w.currentStage.get() == finalStage) {

            //3.1.7.4.4 將前一個Stage 的VO2SM2 定義為VO2SM1 , StageHR2定義為StageHR1
            w.setVo2SM1(w.getVo2SM2());
            w.setStageHR1(w.getStageHR2());

            if ((w.getStageHR1() > 110 && w.getStageHR1() < w.selTargetHrBpm.get())
                    || w.currentHeartRate.get() > w.selTargetHrBpm.get()) {
                m.showWarring(WARRING_HR_REACHED_TARGET);
                //達成兩項條件
                Log.d(TAG, "<#8# 目標達成算分數 ,StageHR1" + w.getStageHR1() + ",THR:" + w.selTargetHrBpm.get() + "> ");
            } else {
                //條件
                //StageHR1>110bpm , StageHR1<  85%MaxHR
                //Heart Rate > 85%MaxHR
                Log.d(TAG, "<測試失敗 ,StageHR1:" + w.getStageHR1() + ",THR:" + w.selTargetHrBpm.get() + "> 3.1.7.4.8 當Stage(" + finalStage + ") 結束時，Heart Rate 沒有在前兩項條件內時，顯示字串『INVALID TEST-HR OUT OF RANGE』，60 秒後回到 Idle Mode.");
                Log.d(TAG, "StageHR1(" + w.getStageHR1() + ")>110bpm, StageHR1(" + w.getStageHR1() + ")< 85%MaxHR(" + w.selTargetHrBpm.get() + ")");
                m.showWarring(INVALID_TEST_HR_OUT_OF_RANGE);
            }
        }

    }

    int wattNum = 0;
    boolean isStage1Done = false;
    boolean isStage2Done = false;
    boolean isStage3Done = false;
    boolean isStage4Done = false;

    int stage2Hr;

    private void updateWatt() {

        //Stage 1
        if (w.currentStage.get() == 1 && !isStage1Done) {
            wattNum = isElliptical ? 10 : (isMale ? 50 : 25);
            isStage1Done = true;
            updateLevel();
        }

        //Stage 2
        if (w.currentStage.get() == 2 && !isStage2Done) {

            //除了 BIKE MALE ，當2nd Stage 的心跳決定後，3rd / 4th Stage就以表格同一行決定阻力
            stage2Hr = w.currentHeartRate.get();

            if (isElliptical) {
                if (w.currentHeartRate.get() < 80) wattNum = 80;
                if (w.currentHeartRate.get() >= 80 && w.currentHeartRate.get() < 90)
                    wattNum = 61;
                if (w.currentHeartRate.get() >= 90 && w.currentHeartRate.get() < 100)
                    wattNum = 44;
                if (w.currentHeartRate.get() >= 100) wattNum = 28;
            } else {
                if (isMale) {
                    if (w.currentHeartRate.get() < 90) wattNum = 150;
                    if (w.currentHeartRate.get() >= 90 && w.currentHeartRate.get() < 105)
                        wattNum = 125;
                    if (w.currentHeartRate.get() >= 105) wattNum = 100;
                } else {

                    if (w.currentHeartRate.get() < 80) wattNum = 125;
                    if (w.currentHeartRate.get() >= 80 && w.currentHeartRate.get() < 90)
                        wattNum = 100;
                    if (w.currentHeartRate.get() >= 90 && w.currentHeartRate.get() < 100)
                        wattNum = 75;
                    if (w.currentHeartRate.get() >= 100) wattNum = 50;
                }
            }

            isStage2Done = true;
            updateLevel();
        }

        //Stage 3
        if (w.currentStage.get() == 3 && !isStage3Done) {
            if (isElliptical) {
                if (stage2Hr < 80) wattNum = 107;
                if (stage2Hr >= 80 && stage2Hr < 90)
                    wattNum = 80;
                if (stage2Hr >= 90 && stage2Hr < 100)
                    wattNum = 61;
                if (stage2Hr >= 100) wattNum = 44;
            } else {
                if (isMale) {
                    if (w.constantPowerW.get() == 150) {
                        if (w.currentHeartRate.get() < 120) wattNum = 225;
                        if (w.currentHeartRate.get() >= 120 && w.currentHeartRate.get() < 135)
                            wattNum = 200;
                        if (w.currentHeartRate.get() >= 135) wattNum = 175;
                    } else if (w.constantPowerW.get() == 125) {
                        if (w.currentHeartRate.get() < 120) wattNum = 200;
                        if (w.currentHeartRate.get() >= 120 && w.currentHeartRate.get() < 135)
                            wattNum = 175;
                        if (w.currentHeartRate.get() >= 135) wattNum = 150;
                    } else if (w.constantPowerW.get() == 100) {
                        if (w.currentHeartRate.get() < 120) wattNum = 175;
                        if (w.currentHeartRate.get() >= 120 && w.currentHeartRate.get() < 135)
                            wattNum = 150;
                        if (w.currentHeartRate.get() >= 135) wattNum = 125;
                    }

                } else {
                    if (stage2Hr < 80) wattNum = 150;
                    if (stage2Hr >= 80 && stage2Hr < 90) wattNum = 125;
                    if (stage2Hr >= 90 && stage2Hr < 100) wattNum = 100;
                    if (stage2Hr >= 100) wattNum = 75;
                }
            }

            isStage3Done = true;
            updateLevel();
        }

        //Stage 4
        if (w.currentStage.get() == 4 && !isStage4Done) {

            if (stage2Hr < 80) wattNum = isElliptical ? 124 : 175;
            if (stage2Hr > 80 && stage2Hr < 90) wattNum = isElliptical ? 107 : 150;
            if (stage2Hr > 90 && stage2Hr < 100) wattNum = isElliptical ? 76 : 117;
            if (stage2Hr > 100) wattNum = isElliptical ? 61 : 100;

            isStage4Done = true;
            updateLevel();
        }
    }

    private void updateLevel() {
        w.constantPowerW.set(wattNum);
      //  w.currentLevel.set(calc.getLevel(wattNum, w.currentRpm.get()));
     //   w.currentLevel.set(calc.getLevel(wattNum, 50));
        //    if (w.currentLevel.get() == wattNum) return;
        Log.d(TAG, "<Stage" + (int)w.currentStage.get() + ", 調整Watt> >>>>> POWER:<" + wattNum + ">W, RPM:" + w.currentRpm.get() + ", LEVEL:" + w.currentLevel.get() + ", STAGE:" + w.currentStage.get());

        Log.d("FITNESS_TEST_PROGRAM", "s0000000etPwmLevel_POWER: " + w.currentLevel.get());
//        m.updateSpeedOrLevelNum(wattNum, true);
        //w.currentLevel 在
        m.updateSpeedOrLevelNum(w.constantPowerW.get(), true);
    }

    int rpmErrorTime = 0;

    @Override
    public void out() {

        if (!m.isWorkoutTimerRunning) return;

        //3.4.9.9 如果連續30秒未偵測到心跳結束
        m.noHrCheck();

        //3.1.5.6 若程式Stage1, Stage2期間超出 Target HR, MW顯示『INVALID TEST』字串, 60秒後返回Idle Mode。
        if (w.currentStage.get() == 1) {
            if (w.currentHeartRate.get() > w.selTargetHrBpm.get()) {
                Log.d(TAG, "<測試失敗>,若程式Stage1, Stage2期間超出 Target HR ," + w.currentStage.get() + ", 心跳:" + w.currentHeartRate.get() + ", THR:" + w.selTargetHrBpm.get());
                //  Log.d(TAG, "3.4.9.7 當Stage2~4 期間 若 Heart Rate > 85%MaxHR 時，程式結束，顯示字串 『YOUR SCORE xXX.X』 ,60秒後回到 idle Mode.");
                m.showWarring(INVALID_TEST_HR_OUT_OF_RANGE);
            }
        }

        Log.d(TAG, "out: ####################### RPM:" + w.currentRpm.get());

        //3.4.9.10 當 Running 期間，RPM 沒有持續保持在48~52之間時，顯示宇串『INVALID TEST - RPM OUT OF RANGE 60 秒後回到Idle Mode
        if (w.currentRpm.get() >= 48 && w.currentRpm.get() <= 52) {
            rpmErrorTime = 0;
            m.showRpmError(false);
            return;
        }

        rpmErrorTime++;

        if (rpmErrorTime == 1) {
            Log.d(TAG, "3.4.9.10 當 Running 期間，RPM 沒有持續保持在48~52( currentRpm:" + w.currentRpm.get() + ")之間時，顯示宇串『INVALID TEST - RPM OUT OF RANGE 30 秒後回到Idle Mode ");
            m.showRpmError(true);
        }

    }

    @Override
    public void target() {

        //3.4.9.7 當Stage2~4 期間 若 Heart Rate > 85%MaxHR 時，程式結束，顯示字串 『YOUR SCORE xXX.X』 ,60秒後回到 idle Mode.
        if (w.currentStage.get() <= 1) return;
        if (w.currentHeartRate.get() > w.selTargetHrBpm.get()) {

            //3.1.7.4.4 將前一個Stage 的VO2SM2 定義為VO2SM1 , StageHR2定義為StageHR1
            w.setVo2SM1(w.getVo2SM2());
            w.setStageHR1(w.getStageHR2());
            //3.1.7.4.5 當Stage2~4 結束時計算並紀錄 VO2SM, 定義為VO2SM2, 記錄Heart Rate, 紀錄為 StageHR2, 計算VO2Max
            w.setVo2SM2((int) calc.getVo2Sm(w.constantPowerW.get() * 6));
            w.setStageHR2(w.currentHeartRate.get());
            w.setVo2Max(calc.getVo2Max(w.getVo2SM1(), w.getStageHR1(), w.getVo2SM2(), w.getStageHR2(), w.selHrMax.get()));

            Log.d(TAG, "<目標達成算分數,Stage2~4 期間 心跳超出 THR>Stage" + w.currentStage.get() + ", 心跳:" + w.currentHeartRate.get() + ", THR:" + w.selTargetHrBpm.get());
            //  Log.d(TAG, "3.4.9.7 當Stage2~4 期間 若 Heart Rate > 85%MaxHR 時，程式結束，顯示字串 『YOUR SCORE xXX.X』 ,60秒後回到 idle Mode.");
            m.showWarring(WARRING_HR_REACHED_TARGET);
        }
    }

    @Override
    public void coolDown(long time) {

    }

    @Override
    public void cancelTimer() {

    }
}
