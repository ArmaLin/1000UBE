package com.dyaco.spirit_commercial.viewmodel;

import static com.dyaco.spirit_commercial.support.FormulaUtil.E_BLANK;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.MAX_LEVEL_MIN;
import static com.dyaco.spirit_commercial.support.intdef.WorkoutIntDef.UNLIMITED;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableDouble;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableFloat;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.ViewModel;

import com.dyaco.spirit_commercial.App;
import com.dyaco.spirit_commercial.support.SingleLiveEvent;
import com.dyaco.spirit_commercial.support.intdef.DeviceIntDef;
import com.dyaco.spirit_commercial.support.intdef.HrStatus;
import com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS;
import com.dyaco.spirit_commercial.workout.programs.ProgramsEnum;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WorkoutViewModel extends ViewModel implements Serializable {

    public final ObservableInt totalRevolutions = new ObservableInt(0);

    public final ObservableInt egymTimePerSets = new ObservableInt(0);
    public final ObservableDouble egymIntervalDistance = new ObservableDouble();
    public int[] orgArraySpeedAndLevelE; // 從 iProgram > initChart 產生
    public int[] newArraySpeedAndLevelE; // 從 iProgram > initChart 產生
    public int[] orgArrayInclineE;
    public int[] newArrayInclineE;

    public final ObservableInt egymCurrentSet = new ObservableInt(0);

//    public WorkoutViewModel() {
//        // 監聽 egymCurrentInterval 的變化
//        egymCurrentInterval.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
//            @Override
//            public void onPropertyChanged(Observable sender, int propertyId) {
////                Log.d("KKKKEEEE", "egymIntervalDistance: 數值改變 : "  + egymCurrentInterval.get());
//                egymTimePerSets.set(0); // Interval改變, 每個 set 當前的時間歸0
//
//            }
//        });
//    }


public final ObservableDouble peakLevel = new ObservableDouble();
public final ObservableDouble peakHeartRate = new ObservableDouble();
public final ObservableDouble peakSpeed = new ObservableDouble();
public final ObservableDouble peakMets = new ObservableDouble();
public final ObservableDouble peakRpm = new ObservableDouble();


    public final ObservableInt egymTotalInterval = new ObservableInt(0);

    public final ObservableField<String> egymTargetDistance = new ObservableField<>(E_BLANK);
    public final ObservableField<String> egymTargetSpeed = new ObservableField<>(E_BLANK);
    public final ObservableField<String> egymTargetIncline = new ObservableField<>(E_BLANK);
    public final ObservableField<String> egymTargetHeartRate = new ObservableField<>(E_BLANK);

    private Long workoutStartTime;
    private Long workoutEndTime;

    public final ObservableBoolean isWarmUpIng = new ObservableBoolean(false);
    public final ObservableBoolean isCoolDowning = new ObservableBoolean(false);

    public final ObservableBoolean isWorkoutReadyStart = new ObservableBoolean(false);

    private boolean isWorkoutDone; //GS MODE workout 完成，incline歸0

    public boolean isWorkoutDone() {
        return isWorkoutDone;
    }

    public void setWorkoutDone(boolean workoutDone) {
        isWorkoutDone = workoutDone;
    }

    public final ObservableBoolean isSafeKey = new ObservableBoolean(true);

    //GARMIN
    public final ObservableBoolean isGarminConnected = new ObservableBoolean(false); //Garmin 是否連線中
    public final ObservableBoolean isWorkoutGarmin = new ObservableBoolean(false); //workout開啟時有連著Garmin，途中如果斷線可再重連
//    public final ObservableInt garminStress = new ObservableInt(0);
    public final ObservableField<String> garminStress = new ObservableField<>("--");
    public final ObservableField<String> garminSpo2 = new ObservableField<>("--");
    public final ObservableInt garminBodyBatteryLevel = new ObservableInt(0);
    public final ObservableInt garminRespirationRate = new ObservableInt(0);

    public final ObservableBoolean isAppleWatchConnected = new ObservableBoolean(false); //Garmin 是否連線中
    public final ObservableBoolean isAppleWatchEnabled = new ObservableBoolean(false); //Garmin 是否連線中

    public final ObservableBoolean isSamsungWatchConnected = new ObservableBoolean(false);
    public final ObservableBoolean isSamsungWatchEnabled = new ObservableBoolean(false);


    public final ObservableInt currentFrontInclineAd = new ObservableInt(0);

    public ProgramsEnum selProgram;
    public final ObservableInt selWorkoutTime = new ObservableInt(UNLIMITED); //Program 設定的時間 UNLIMITED:上數
    public final ObservableInt selYO = new ObservableInt(0);
    public final ObservableInt selWeight = new ObservableInt(App.UNIT_E == DeviceIntDef.IMPERIAL ? OPT_SETTINGS.WEIGHT_IU_DEF : OPT_SETTINGS.WEIGHT_MU_DEF);
    public final ObservableInt selWeightIU = new ObservableInt(0);
    public final ObservableInt selWeightMU = new ObservableInt(0);
    public final ObservableInt selHeightIU = new ObservableInt(0);
    public final ObservableInt selHeightMU = new ObservableInt(0);
    public final ObservableInt selGender = new ObservableInt(0);
    public final ObservableInt selMaxSpeedOrLevel = new ObservableInt(); //一開始選擇的max Speed or Level  的階數(選5.1 就是 51)
    public final ObservableInt orgMaxSpeedInProfile = new ObservableInt(); //預設profile中的最大值
    public final ObservableInt orgMaxInclineInProfile = new ObservableInt(); //預設profile中的最大值

    public final ObservableDouble selForce = new ObservableDouble();


    public final ObservableInt constantPowerW = new ObservableInt();
    public final ObservableInt selConstantPowerW = new ObservableInt();
    public final ObservableInt selEstimatedTime = new ObservableInt();

    //HIIT
    public final ObservableInt selFirstSprintSpeedLevel = new ObservableInt();//HIIT衝刺速度 //3.2 mph >> 32(階數)
    public final ObservableInt selSprintSpeedLevel = new ObservableInt();//HIIT衝刺速度 //3.2 mph >> 32(階數)
    public final ObservableInt selSprintTimeSec = new ObservableInt();//HIIT衝刺時間
    public final ObservableInt selRestTimeSec = new ObservableInt();//HIIT休息速度
    public final ObservableInt selRestSpeedLevel = new ObservableInt();//HIIT休息速度 階數
    public final ObservableInt selIntervals = new ObservableInt();//HIIT間隔
    public final ObservableInt currentIntervals = new ObservableInt();//HIIT目前間隔

    public final ObservableFloat selTargetSpeed = new ObservableFloat();
    public final ObservableInt selTargetHrBpm = new ObservableInt(); // 72 ~ 200
    public final ObservableInt selHrMax = new ObservableInt();
    public final ObservableFloat recommendedSpeed = new ObservableFloat();

    // public final ObservableFloat currentLevel = new ObservableFloat();
    public final ObservableFloat currentMaxInclineValue = new ObservableFloat(); //Incline 目前顯示的最大數值
    public final ObservableInt currentMaxInclineLevel = new ObservableInt(); //Incline 目前最大階數
    public final ObservableFloat currentInclineValue = new ObservableFloat(); //Incline 實際數值
    public final ObservableInt currentInclineLevel = new ObservableInt();// 階數 (currentIncline * 2)

    public final ObservableFloat currentSpeed = new ObservableFloat();//level or speed 實際數值
    public final ObservableInt currentSpeedLevel = new ObservableInt();// 階數 (currentSpeed * 10?)
    public final ObservableFloat currentMaxSpeed = new ObservableFloat();
    public final ObservableInt currentMaxSpeedLevel = new ObservableInt(); // 目前最大階數

    public final ObservableInt currentLevel = new ObservableInt(MAX_LEVEL_MIN);
    public final ObservableFloat currentMaxLevel = new ObservableFloat();

    private int hpHr;
    private int wpHr;
    private int bleHr;
    private int garminHr;

    private int appleWatchHr;
    private double appleWatchCalories;

    public double getAppleWatchCalories() {
        return appleWatchCalories;
    }

    public void setAppleWatchCalories(double appleWatchCalories) {
        this.appleWatchCalories = appleWatchCalories;
    }

    public int getAppleWatchHr() {
        return appleWatchHr;
    }

    public void setAppleWatchHr(int appleWatchHr) {
        this.appleWatchHr = appleWatchHr;
    }


    private int samsungWatchHr;

    public int getSamsungWatchHr() {
        return samsungWatchHr;
    }

    public void setSamsungWatchHr(int samsungWatchHr) {
        this.samsungWatchHr = samsungWatchHr;
    }

    public final ObservableInt pwmLevelDA = new ObservableInt();
    public final ObservableInt rpmCounter = new ObservableInt();
//    public final ObservableInt rpmL = new ObservableInt();
//    public final ObservableInt rpmR = new ObservableInt();

    public final ObservableInt currentSegment = new ObservableInt(0);// 0等於第一階
    public final ObservableFloat currentStage = new ObservableFloat(1);
    public final ObservableInt currentHeartRate = new ObservableInt(0);

    public final SingleLiveEvent<Boolean> isHrConnected = new SingleLiveEvent<>(false);
    boolean tmpHrCheck = false;
    public void setIsHrConnected(boolean isHrC) {

      //  isHrConnected.setValue(isConnected); // ui thread
//        if (!isConnected){
//            //hr斷線歸0
//            currentHeartRate.set(0);
//        }
        if (tmpHrCheck == isHrC) return;
        tmpHrCheck = isHrC;
        isHrConnected.postValue(isHrC); //thread
     //   isHrConnected.setValue(isHrC); //thread
    }

//    private boolean checkHrConnect() {
//        if (garminHr > 0 || wpHr > 0 || bleHr > 0 || hpHr > 0) {
//            isHrConnected.setValue(true);
//            return true;
//        } else {
//            isHrConnected.setValue(false);
//            currentHeartRate.set(0);
//            return false;
//        }
//    }

    public SingleLiveEvent<Boolean> getIsHrConnected() {
        return isHrConnected;
    }

    //public final ObservableFloat currentMaxLevel = new ObservableFloat();
    public final ObservableDouble avgPower = new ObservableDouble();
    public final ObservableDouble avgPace = new ObservableDouble();
    public final ObservableDouble currentDistance = new ObservableDouble(0);
    public final ObservableDouble targetDistance = new ObservableDouble(0);
    public final ObservableDouble distanceLeft = new ObservableDouble();
    public final ObservableDouble currentCalories = new ObservableDouble();
    public final ObservableDouble totalCalories = new ObservableDouble();
    public final ObservableDouble targetCalories = new ObservableDouble();
    public final ObservableDouble caloriesLeft = new ObservableDouble();


    public final ObservableInt currentStep = new ObservableInt();
    public final ObservableInt totalStep = new ObservableInt();
    public final ObservableInt stepLeft = new ObservableInt();
    public final ObservableInt targetSteps = new ObservableInt();


    public final ObservableDouble currentMets = new ObservableDouble();
    public final ObservableDouble totalMets = new ObservableDouble();
    public final ObservableDouble metsLeft = new ObservableDouble();
    public final ObservableInt targetMets = new ObservableInt();


    public final ObservableInt currentPower = new ObservableInt();

    public final ObservableDouble avgMet = new ObservableDouble();
    public final ObservableInt currentRpm = new ObservableInt();
    public final ObservableDouble avgRpm = new ObservableDouble();
    public final ObservableFloat avgIncline = new ObservableFloat();
    public final ObservableFloat avgLevel = new ObservableFloat();
    public final ObservableFloat totalLevel = new ObservableFloat(0);
    public final ObservableDouble avgSpeed = new ObservableDouble();
    public final ObservableFloat avgHeartRate = new ObservableFloat();
    public final ObservableDouble currentPace = new ObservableDouble();
    public final ObservableDouble currentElevationGain = new ObservableDouble();

    public final ObservableField<String> currentShowTimeText = new ObservableField<>("00:00");//顯示的時間
    public final ObservableInt elapsedTime = new ObservableInt(0); //Workout 經過的時間(上數) 超過99分從0開始  (過去時間，從0開始數)
    public final ObservableInt totalElapsedTime = new ObservableInt(0); //Workout 實際經過的時間(上數)
    public final ObservableInt remainingTime = new ObservableInt(0); //Workout 剩餘的時間(下數) time left 倒數計時

    public final ObservableInt elapsedTimeShow = new ObservableInt(0); //Workout 經過的時間(上數) 超過99分從0開始  (過去時間，從0開始數)
    public final ObservableInt totalElapsedTimeShow = new ObservableInt(0); //Workout 實際經過的時間(上數)
    public final ObservableInt remainingTimeShow = new ObservableInt(0); //Workout 剩餘的時間(下數) time left 倒數計時


    public final ObservableField<String> currentPauseTimeText = new ObservableField<>();// show pause time

    public final ObservableDouble summaryPower = new ObservableDouble();
    public final ObservableDouble summaryMets = new ObservableDouble();
    public final ObservableInt summaryPace = new ObservableInt();
    public final ObservableInt summaryVo2 = new ObservableInt();
    public final ObservableInt summaryMaxIncline = new ObservableInt();
    public final ObservableInt summaryMaxSpeedAndLevel = new ObservableInt();
    public final ObservableInt summaryMaxHeartRate = new ObservableInt();
    public final ObservableInt coolDownTime = new ObservableInt(0);
    public final ObservableInt warmUpTime = new ObservableInt(0);

    public final ObservableBoolean disabledInclineUpdate = new ObservableBoolean(false);
    public final ObservableBoolean disabledSpeedUpdate = new ObservableBoolean(false);
    public final ObservableBoolean disabledLevelUpdate = new ObservableBoolean(false);

    private int wfiTT; //Total Time

    public int getWfiTT() {
        return wfiTT;
    }

    public void setWfiTT(int wfiTT) {
        this.wfiTT = wfiTT;
    }
    //    private boolean disabledInclineUpdate;
//    private boolean disabledSpeedUpdate;
//
//    public boolean isDisabledInclineUpdate() {
//        return disabledInclineUpdate;
//    }
//
//    public void setDisabledInclineUpdate(boolean disabledInclineUpdate) {
//        this.disabledInclineUpdate = disabledInclineUpdate;
//    }
//
//    public boolean isDisabledSpeedUpdate() {
//        return disabledSpeedUpdate;
//    }
//
//    public void setDisabledSpeedUpdate(boolean disabledSpeedUpdate) {
//        this.disabledSpeedUpdate = disabledSpeedUpdate;
//    }

    private Date updateTime;
    public int repeatCount = 1;
    public int[] orgArraySpeedAndLevel; // 從 iProgram > initChart 產生
    public int[] orgArrayIncline;
    public List<Integer> hrList = new ArrayList<>();
    public List<Integer> rpmList = new ArrayList<>();
    public List<Double> metsList = new ArrayList<>();
    public List<Double> cttLevelHrList = new ArrayList<>();

    public List<Integer> wingateWattsList = new ArrayList<>();

    public List<DiagramBarBean> inclineDiagramBarList = new ArrayList<>();
    public List<DiagramBarBean> speedDiagramBarList = new ArrayList<>();
    public List<DiagramBarBean> blankDiagramBarList = new ArrayList<>();

    public String inclineChartNum;
    public String speedAndLevelChartNum;
    public String heartRateChartNum;

    public ProgramsEnum getSelProgram() {
        return selProgram;
    }

    public void setSelProgram(ProgramsEnum selProgram) {
        this.selProgram = selProgram;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }


    private List<Integer> frontInclineAdList = new ArrayList<>();

    private HrStatus hrStatus;
    private int hrTargetHrMax; //220 - age
    private int hrROR1;
    private int hrROR2;
    private int hrGOAL;
    private int hrHR0;
    private int hrHR60;
    private int hrHR75;
    private boolean hrROR1done;
    private boolean hrROR2done;
    private boolean hrWattDone;
    private boolean hrROR1Holding;
    private boolean hrROR2Holding;
    private boolean hrMaintaining;

    public List<Integer> getFrontInclineAdList() {
        return frontInclineAdList;
    }

    public void setFrontInclineAdList(List<Integer> frontInclineAdList) {
        this.frontInclineAdList = frontInclineAdList;
    }

    public HrStatus getHrStatus() {
        return hrStatus;
    }

    public void setHrStatus(HrStatus hrStatus) {
        this.hrStatus = hrStatus;
    }

    public int getHrTargetHrMax() {
        return hrTargetHrMax;
    }

    public void setHrTargetHrMax(int hrTargetHrMax) {
        this.hrTargetHrMax = hrTargetHrMax;
    }

    public int getHrROR1() {
        return hrROR1;
    }

    public void setHrROR1(int hrROR1) {
        this.hrROR1 = hrROR1;
    }

    public int getHrROR2() {
        return hrROR2;
    }

    public void setHrROR2(int hrROR2) {
        this.hrROR2 = hrROR2;
    }

    public int getHrGOAL() {
        return hrGOAL;
    }

    public void setHrGOAL(int hrGOAL) {
        this.hrGOAL = hrGOAL;
    }

    public int getHrHR0() {
        return hrHR0;
    }

    public void setHrHR0(int hrHR0) {
        this.hrHR0 = hrHR0;
    }

    public int getHrHR60() {
        return hrHR60;
    }

    public void setHrHR60(int hrHR60) {
        this.hrHR60 = hrHR60;
    }

    public int getHrHR75() {
        return hrHR75;
    }

    public void setHrHR75(int hrHR75) {
        this.hrHR75 = hrHR75;
    }

    public boolean isHrROR1done() {
        return hrROR1done;
    }

    public void setHrROR1done(boolean hrROR1done) {
        this.hrROR1done = hrROR1done;
    }

    public boolean isHrROR2done() {
        return hrROR2done;
    }

    public void setHrROR2done(boolean hrROR2done) {
        this.hrROR2done = hrROR2done;
    }

    public boolean isHrWattDone() {
        return hrWattDone;
    }

    public void setHrWattDone(boolean hrWattDone) {
        this.hrWattDone = hrWattDone;
    }

    public boolean isHrROR1Holding() {
        return hrROR1Holding;
    }

    public void setHrROR1Holding(boolean hrROR1Holding) {
        this.hrROR1Holding = hrROR1Holding;
    }

    public boolean isHrROR2Holding() {
        return hrROR2Holding;
    }

    public void setHrROR2Holding(boolean hrROR2Holding) {
        this.hrROR2Holding = hrROR2Holding;
    }

    public boolean isHrMaintaining() {
        return hrMaintaining;
    }

    public void setHrMaintaining(boolean hrMaintaining) {
        this.hrMaintaining = hrMaintaining;
    }


    //F-Test
    private int min3HR;
    private int min2HR;
    private int vo2SM2;
    private int vo2SM1;
    private int stageHR1;
    private int stageHR2;
    private double vo2Max;

    public double getVo2Max() {
        return vo2Max;
    }

    public void setVo2Max(double vo2Max) {
        this.vo2Max = vo2Max;
    }

    public int getMin3HR() {
        return min3HR;
    }

    public void setMin3HR(int min3HR) {
        this.min3HR = min3HR;
    }

    public int getMin2HR() {
        return min2HR;
    }

    public void setMin2HR(int min2HR) {
        this.min2HR = min2HR;
    }

    public int getVo2SM2() {
        return vo2SM2;
    }

    public void setVo2SM2(int vo2SM2) {
        this.vo2SM2 = vo2SM2;
    }

    public int getVo2SM1() {
        return vo2SM1;
    }

    public void setVo2SM1(int vo2SM1) {
        this.vo2SM1 = vo2SM1;
    }

    public int getStageHR1() {
        return stageHR1;
    }

    public void setStageHR1(int stageHR1) {
        this.stageHR1 = stageHR1;
    }

    public int getStageHR2() {
        return stageHR2;
    }

    public void setStageHR2(int stageHR2) {
        this.stageHR2 = stageHR2;
    }


    public Long getWorkoutStartTime() {
        return workoutStartTime;
    }

    public void setWorkoutStartTime(Long workoutStartTime) {
        this.workoutStartTime = workoutStartTime;
    }

    public Long getWorkoutEndTime() {
        return workoutEndTime;
    }

    public void setWorkoutEndTime(Long workoutEndTime) {
        this.workoutEndTime = workoutEndTime;
    }

    public int getHpHr() {
        return hpHr;
    }

    public void setHpHr(int hpHr) {
        this.hpHr = hpHr;
    }

    public int getWpHr() {
        return wpHr;
    }

    public void setWpHr(int wpHr) {
        this.wpHr = wpHr;
    }

    public int getBleHr() {
        return bleHr;
    }

    public void setBleHr(int bleHr) {
        this.bleHr = bleHr;
    }

    public int getGarminHr() {
        return garminHr;
    }

    public void setGarminHr(int garminHr) {
        this.garminHr = garminHr;
    }



}