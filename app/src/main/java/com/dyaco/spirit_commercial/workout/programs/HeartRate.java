package com.dyaco.spirit_commercial.workout.programs;

import static com.dyaco.spirit_commercial.App.UNIT_E;
import static com.dyaco.spirit_commercial.MainActivity.isTreadmill;
import static com.dyaco.spirit_commercial.MainActivity.isUs;
import static com.dyaco.spirit_commercial.MainActivity.userProfileViewModel;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.setMinSpeedDiagram;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.setTreadmillInclineDiagram;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.IMPERIAL;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.SELECT_HR_EXCEEDS_AGE;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.WARRING_HR_TOO_HIGH;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.WARRING_NO_RPM;
import static com.dyaco.spirit_commercial.support.intdef.HrStatus.MAINTAINING_MODE;
import static com.dyaco.spirit_commercial.support.intdef.HrStatus.REACHING_MODE;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.THR_CONSTANT;
import static com.dyaco.spirit_commercial.support.intdef.WorkoutIntDef.UNLIMITED;

import android.util.Log;

import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.support.FormulaUtil;
import com.dyaco.spirit_commercial.support.RxTimer;
import com.dyaco.spirit_commercial.viewmodel.WorkoutViewModel;
import com.dyaco.spirit_commercial.workout.MainWorkoutTrainingFragment;
import com.dyaco.spirit_commercial.workout.WorkoutChartsFragment;
import com.dyaco.spirit_commercial.workout.WorkoutStatsFragment;

public class HeartRate implements IPrograms {
    private static final String TAG = "HEART_RATE_PROGRAM";
    private final int HOLDING_HR = 0;
    private int noHrTime;
    private RxTimer noRpmTimer;
    boolean isCheckReachingModeDone;
    private RxTimer hrTimer;

    WorkoutViewModel w;
    MainWorkoutTrainingFragment m;

    int currentSpeedOrLevelNum = 0;

    int HEART_MAX_SPEED_OR_LEVEL;

    public HeartRate(WorkoutViewModel workoutViewModel, MainWorkoutTrainingFragment mainWorkoutTrainingFragment) {
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
            m.getBinding().viewBikeLevelTitleUs.setText(R.string.THR);
        }
    }

    @Override
    public void init() {
        hrTimer = new RxTimer();

        //一樣
        int targetHrMax = THR_CONSTANT - w.selYO.get();
        w.setHrTargetHrMax(targetHrMax);
        w.selHrMax.set(targetHrMax);//

        //16k 10m
        HEART_MAX_SPEED_OR_LEVEL = isTreadmill ? (UNIT_E == IMPERIAL ? 10 : 16) : 31;

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
        checkIDLE_MODE();
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

        currentSpeedOrLevelNum = isTreadmill ? w.currentSpeedLevel.get() : w.currentLevel.get();

        m.setHrJustRight();

        //MAINTAINING_MODE
        if (w.getHrStatus() == MAINTAINING_MODE) {

            if (!w.isHrMaintaining()) {
                Log.d(TAG, "第一次進入 MAINTAINING 狀態");
                w.setHrMaintaining(true);
            }

            //MAINTAINING 狀態下， 每15秒執行
            if (w.elapsedTime.get() % 15 == 0) inMaintaining();


        } else {

            //HR0: PROGRAM 開始後的第一次心跳值
            if (w.getHrHR0() <= 0 && w.currentHeartRate.get() > 40) {
                Log.d(TAG, "設定第一次心跳(HR0): " + w.currentHeartRate.get());
                w.setHrHR0(w.currentHeartRate.get());
            }

            /*
              REACHING MODE 檢查 : 取得 watt
              condition：
              1.當前狀態不是 REACHING_MODE
              2.watt尚未取得
             */
            if (!w.isHrWattDone() && w.getHrStatus() != REACHING_MODE && w.getHrStatus() != MAINTAINING_MODE)
                checkREACHING_MODE();

            /*
              取ROR1
              condition：
              1.當前狀態為 REACHING_MODE
              2.已取得watt
              3.ROR1尚未取得
             */
            if (w.isHrWattDone() && !w.isHrROR1done() && w.getHrStatus() == REACHING_MODE) {
                //進入一次後就不會再進了，
                getROR1();
            }

            //在 REACHING 狀態，任何時候 HR >= THR 進入 MAINTAINING 狀態
            if (w.getHrStatus() == REACHING_MODE) {
                if (w.currentHeartRate.get() >= w.selTargetHrBpm.get()) {
                    hrTimer.cancel();

                    Log.d(TAG, "在 REACHING 狀態，任何時候 HR >= THR 進入 MAINTAINING 狀態");
                    w.setHrStatus(MAINTAINING_MODE);
                    // setHintText(R.string.hr_hint_maintaining, "");
                }
            }
        }
    }

    boolean isDone = false;

    /**
     * IDLE MODE Check
     */
    private void checkIDLE_MODE() {

        if (isDone) return;

        //PROGRAM 開始後，心跳值若超過 THR*1.2 或 THR_MAX*1.2 ，系統將強制回到 IDLE_MODE 。
        //  Log.d(TAG, "checkIDLE_MODE: THR:" + workoutViewModel.currentHeartRate.get() + ",THR*1.2:" + (workoutViewModel.selTargetHrBpm.get() * 1.2) + ",THR_MAX*1.2:" + (workoutViewModel.getHrTargetHrMax() * 1.2));
        if (w.currentHeartRate.get() > (w.selTargetHrBpm.get() * 1.2) || w.currentHeartRate.get() > (w.getHrTargetHrMax() * 1.2)) {
            //心跳太高，退出Workout
            if (noRpmTimer != null) {
                noRpmTimer.cancel();
                noRpmTimer = null;
            }
            if (hrTimer != null) {
                hrTimer.cancel();
                hrTimer = null;
            }
            Log.d(TAG, "心跳太高，退出Workout");
            m.showWarring(WARRING_HR_TOO_HIGH);
            isDone = true;
        }

        //PROGRAM 開始後若30秒內檢測不到心跳輸入，系統將強制回到IDLE_MODE
        m.noHrCheck();

//        if (w.currentHeartRate.get() < NO_HR_HR) {
//            if (noHrTime == 1) {
//                m.showWarring2(WARRING_NO_HR);
//            }
//            Log.d(TAG, "無心跳: " + w.currentHeartRate.get() + ",時間:" + noHrTime);
//            noHrTime++;
//            if (noHrTime > 30) {
//
//                if (noRpmTimer != null) {
//                    noRpmTimer.cancel();
//                    noRpmTimer = null;
//                }
//
//                if (hrTimer != null) {
//                    hrTimer.cancel();
//                    hrTimer = null;
//                    Log.d(TAG, "開始後若30秒內檢測不到心跳輸入，系統將強制回到IDLE_MODE");
//                    m.showWarring(WARRING_NO_HR);
//                }
//            }
//            return;
//        } else {
//            noHrTime = 0;
//        }

        //PROGRAM 開始後若超過5分鐘沒有RPM輸入，系統將強制回到IDLE_MODE
        if (w.currentRpm.get() == 0) {
            if (noRpmTimer == null) {
                Log.d(TAG, "沒RPM，計時五分鐘結束");
                noRpmTimer = new RxTimer();
                noRpmTimer.timer(1000 * 60 * 5, s -> {
                    Log.d(TAG, "超過5分鐘沒有RPM輸入，系統將強制回到IDLE_MODE");
                    m.showWarring(WARRING_NO_RPM);
                });
            }
        } else {
            if (noRpmTimer != null) {
                Log.d(TAG, "有RPM");
                noRpmTimer.cancel();
                noRpmTimer = null;
            }
        }
    }


    int reachingTime = 0;

    /**
     * 檢查是否進入 REACHING_MODE
     */
    private void checkREACHING_MODE() {

        Log.d(TAG, "檢查是否進入 REACHING_MODE: 持續5秒:" + reachingTime + "," + w.currentHeartRate.get() + "<=" + (w.selTargetHrBpm.get() - 5) + " >>" + (w.currentHeartRate.get() <= (w.selTargetHrBpm.get() - 5)));

        //若 HR<=THR-5，持續5秒後系統會依據下列公式 設置第一次 LEVEL 並開始 REACHING 狀態
        if (w.currentHeartRate.get() <= (w.selTargetHrBpm.get() - 5)) {

            reachingTime++;

            if (reachingTime >= 5) {
                Log.d(TAG, "若 HR<=THR-5，5秒後系統會依據下列公式 設置第一次 LEVEL 並開始 REACHING 狀態");
                calcReaching();
            }

//            if (!isCheckReachingModeDone) {
//                Log.d(TAG, "若 HR<=THR-5，5秒後系統會依據下列公式 設置第一次 LEVEL 並開始 REACHING 狀態");
//                isCheckReachingModeDone = true;
//                hrTimer.timer(5000, this::calcReaching);
//            }
        } else if (w.currentHeartRate.get() >= w.selTargetHrBpm.get()) {

            w.setHrWattDone(true);
            w.setHrROR1done(true);
            w.setHrStatus(MAINTAINING_MODE);
            reachingTime = 0;
        } else {
            reachingTime = 0;
        }
    }

    /**
     * REACHING_MODE 計算 WATT
     */
    private void calcReaching() {

//        if (!m.isWorkoutTimerRunning) {
//            isCheckReachingModeDone = false;
//            Log.d(TAG, "等待五秒時暫停: ");
//            return;
//        }

        Log.d(TAG, "進入REACHING_MODE 開始計算 WATT");
        w.setHrStatus(REACHING_MODE);

        //根據該RPM的WATT值找出適當的LEVEL RPM<15不做判斷，RPM>120 時，以RPM120代入此公式求出WATT值，並以此條件來設置LEVEL 。
        int watt = 0;
        int rpm = Math.min(w.currentRpm.get(), 120);
        if (rpm >= 15) {
            watt = (int) FormulaUtil.getWATT((int) userProfileViewModel.getWeight_metric(), w.selTargetHrBpm.get(), rpm);
        }

        Log.d(TAG, "計算 WATT: " + "mRPM:" + w.currentRpm.get() + ",rpm:" + rpm + ",體重:" + userProfileViewModel.getWeight_metric() + ",年齡:" + userProfileViewModel.getUserAge() + ",THR:" + w.selTargetHrBpm.get() + ",WATT:" + watt);
        if (watt <= 0) {
            watt = 3;
        }
        m.updateSpeedOrLevelNum(m.calc.getLevel(watt, rpm) - 1, false);

        Log.d(TAG, "取得Watt: " + watt + ", level:" + m.calc.getLevel(watt, rpm) + ",設定第一次LEVEL");
        //取得Watt
        w.setHrWattDone(true);
    }

    /**
     * 取得ROR1
     * 在REACHING狀態，第60秒HR值HR60若不等於0，則做ROR1的計算若HR等於0則強制等待(HOLD在第60秒)HR不等於0時做ROR1的計算 。
     */
    private void getROR1() {

        Log.d(TAG, "等待60秒後，計算ROR1: " + w.elapsedTime.get());
        //不到60秒，離開
        if (w.elapsedTime.get() < 60) return;

        //設定HR60
        w.setHrHR60(w.currentHeartRate.get());

        //HR60 <= 0，執行Holding，等待 HR60 > 0
        if (w.getHrHR60() <= HOLDING_HR) {
            Log.d(TAG, "getROR1 HR60 <= " + HOLDING_HR + ",HOLDING");

            w.setHrROR1Holding(true);
            return;
        }

        Log.d(TAG, "過60秒取得ROR1");
        int ROR1 = FormulaUtil.getROR1(w.selTargetHrBpm.get(), w.getHrHR0(), w.getHrHR60());
        int GOAL = FormulaUtil.getGOAL(ROR1, w.getHrHR60(), w.selTargetHrBpm.get());
        w.setHrROR1(ROR1);
        w.setHrROR1done(true);
        w.setHrGOAL(GOAL);
        w.setHrROR1Holding(false);

        checkGOAL(true);
    }

    /**
     * ROR1取得後 過15秒 執行
     * condition：
     * 1.當前狀態為 REACHING_MODE
     * 2.ROR1已取得
     * 3.ROR2尚未取得
     *
     * @param sec sec
     */
    private void getROR2(long sec) {

        hrTimer.cancel();
        //在 REACHING狀態，第75秒起含第75秒，若HR不等於0做ROR2的計算若HR等於0則強制等待HOLD在第75秒
        w.setHrHR75(w.currentHeartRate.get());
        if (w.getHrHR75() > HOLDING_HR) {
            Log.d(TAG, "getROR2  取得ROR2");
            int ROR2 = FormulaUtil.getROR2(w.getHrHR75(), w.getHrHR60());
            int GOAL = FormulaUtil.getGOAL(ROR2, w.getHrHR75(), w.selTargetHrBpm.get());
            w.setHrROR2(ROR2);
            w.setHrROR2done(true);
            w.setHrROR2Holding(false);
            w.setHrGOAL(GOAL);
            checkGOAL(false);

        } else {

            w.setHrROR2Holding(true);
            //HR 75 HOLDING，等HR75 不等於 0 做 ROR2 的計算
            hrTimer.interval(1000, number -> {
                if (m.isWorkoutTimerRunning) {
                    Log.d(TAG, "HR 75 HOLDING: Workout暫停");
                    return;
                }
                Log.d(TAG, "HR75 Holding 直到 HR75 > 0");
                if (w.currentHeartRate.get() > HOLDING_HR) {
                    hrTimer.cancel();
                    w.setHrROR2Holding(false);
                    getROR2(number);
                }
            });
        }
    }

    boolean isHigh = false;
    int GOAL;

    /**
     * GOAL 判斷
     *
     * @param isROR1 true > 計算ROR1
     */
    private void checkGOAL(boolean isROR1) {

        int updateValue = 0;
        GOAL = w.getHrGOAL();

        Log.d(TAG, "GOAL 判斷 是否可以進入 MAINTAINING_MODE(5 >= GOAL && GOAL >= -5):  GOAL >>> " + GOAL);
        if (5 >= GOAL && GOAL >= -5) {
            //進入Maintaining mode
            w.setHrStatus(MAINTAINING_MODE);
            hrTimer.cancel();
            //    setHintText(R.string.hr_hint_maintaining, "");

            Log.d(TAG, "checkGOAL: 進入 MAINTAINING_MODE:" + "5 >= " + GOAL + "&& >= -5");
        } else if (GOAL > 15) {
            updateValue = -3;
            isHigh = true;
        } else if (GOAL > 7) {
            updateValue = -2;
            isHigh = true;
        } else if (GOAL > 5) {
            updateValue = -1;
            isHigh = true;
        } else if (GOAL < -40) {
            updateValue = 3;
            isHigh = false;
        } else if (GOAL < -20) {
            updateValue = 2;
            isHigh = false;
        } else {
            updateValue = 1;
            isHigh = false;
        }

//        if (5 >= GOAL && GOAL >= -5) {
//            //進入Maintaining mode
//            workoutViewModel.setHrStatus(MAINTAINING_MODE);
//            hrTimer.cancel();
//            //    setHintText(R.string.hr_hint_maintaining, "");
//
//            Log.d(TAG, "checkGOAL: 進入 MAINTAINING_MODE:" + "5 >= " + GOAL + "&& >= -5");
//        } else if (GOAL > 15) {
//            updateValue = -3;
//            isHigh = true;
//        } else if (15 >= GOAL && GOAL > 7) {
//            updateValue = -2;
//            isHigh = true;
//        } else if (7 >= GOAL && GOAL > 5) {
//            updateValue = -1;
//            isHigh = true;
//        } else if (GOAL < -40) {
//            updateValue = 3;
//            isHigh = false;
//        } else if (-40 <= GOAL && GOAL < -20) {
//            updateValue = 2;
//            isHigh = false;
//        } else if (-20 <= GOAL && GOAL < -5) {
//            updateValue = 1;
//            isHigh = false;
//        }

        if (currentSpeedOrLevelNum + (updateValue) > HEART_MAX_SPEED_OR_LEVEL) {
            if (currentSpeedOrLevelNum == 16) updateValue = 1;
            if (currentSpeedOrLevelNum == 15) updateValue = 2;
            if (currentSpeedOrLevelNum == 17) updateValue = 0;
        }

        if (currentSpeedOrLevelNum + (updateValue) <= 0) {
            if (currentSpeedOrLevelNum == 2) updateValue = -1;
            if (currentSpeedOrLevelNum == 3) updateValue = -2;
            if (currentSpeedOrLevelNum == 1) updateValue = 0;
        }

        //速度有變更
        if (updateValue != 0) {
            m.showHrHint(isHigh, currentSpeedOrLevelNum + updateValue);
        }

        if (w.getHrStatus() == REACHING_MODE) {
            //未進入 MAINTAINING_MODE
            m.updateSpeedOrLevelNum(updateValue, false);

            Log.d(TAG, "checkGOAL 調整LEVEL = +" + (updateValue));

            //ROR1、ROR2 都取得，開始循環
            if (w.isHrROR1done() && w.isHrROR2done()) {
                Log.d(TAG, "checkGOAL: ROR1 , ROR2 都已取得");
                //處理完後，HR60 = HR75
                w.setHrHR60(w.getHrHR75());
                Log.d(TAG, "checkGOAL: 未進入MAINTAINING 狀態，每15秒重複此步驟 直到進入 MAINTAINING 狀態");
                //若未進入MAINTAINING 狀態，重複此步驟 直到進入 MAINTAINING 狀態
                //15秒後執行
                hrTimer.timer(15000, this::tryToMaintaining);
            } else {
                Log.d(TAG, "checkGOAL: ROR1已取得，ROR2未取得，過15秒執行 取ROR2");
                //ROR1取得後過15秒執行
                if (isROR1) hrTimer.timer(15000, this::getROR2);
            }
        }
    }

    /**
     * ROR1 ROR2 都取得，重複執行tryToMaintaining直到進入 MAINTAINING_MODE
     */
    private void tryToMaintaining(long sec) {

        hrTimer.cancel();

        Log.d(TAG, "tryToMaintaining: 每15秒重複checkGOAL執行 直到進入 MAINTAINING_MODE");
        if (w.getHrStatus() == REACHING_MODE) {
            //下一個 15 秒的 HR( 當下的心跳值 ))=HR75
            w.setHrHR75(w.currentHeartRate.get());
            int ROR2 = FormulaUtil.getROR2(w.getHrHR75(), w.getHrHR60());
            int GOAL = FormulaUtil.getGOAL(ROR2, w.getHrHR75(), w.selTargetHrBpm.get());
            w.setHrROR2(ROR2);
            w.setHrGOAL(GOAL);

            Log.d(TAG, "tryToMaintaining: ROR2:" + ROR2 + ",GOAL:" + GOAL + ",HR75:" + w.getHrHR75() + ",HR60:" + w.getHrHR60() + ",TargetHR:" + w.selTargetHrBpm.get());
            checkGOAL(false);
        }
    }

    /**
     * MAINTAINING 狀態 下， 每隔 15 秒 做下列判斷及調整
     */
    private void inMaintaining() {
        if (w.currentHeartRate.get() < 40) return;

        Log.d(TAG, "inMaintaining: MAINTAINING 狀態 下， 每隔 15 秒 做下列判斷及調整LEVEL");
        Log.d(TAG, "inMaintaining: " + w.currentHeartRate.get() + ">=" + (w.selTargetHrBpm.get() + 3) + "," + (w.currentHeartRate.get() >= w.selTargetHrBpm.get() + 3));
        Log.d(TAG, "inMaintaining: " + w.currentHeartRate.get() + "<" + (w.selTargetHrBpm.get() - 3) + "," + (w.currentHeartRate.get() < w.selTargetHrBpm.get() - 3));

        if (w.currentHeartRate.get() >= w.selTargetHrBpm.get() + 3) {

            if ((currentSpeedOrLevelNum - 1) <= 0) return;

            m.updateSpeedOrLevelNum(-1, false);
            currentSpeedOrLevelNum = currentSpeedOrLevelNum - 1;
            Log.d(TAG, "降低 調整為 >> " + currentSpeedOrLevelNum);

            m.showHrHint(true, currentSpeedOrLevelNum);

        }

        if (w.currentHeartRate.get() < (w.selTargetHrBpm.get() - 3)) {

            if ((currentSpeedOrLevelNum + 1) > 17) return;

            m.updateSpeedOrLevelNum(1, false);
            currentSpeedOrLevelNum = currentSpeedOrLevelNum + 1;
            Log.d(TAG, "升高 調整為 >> " + currentSpeedOrLevelNum);

            m.showHrHint(false, currentSpeedOrLevelNum);
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
