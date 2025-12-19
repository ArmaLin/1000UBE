package com.dyaco.spirit_commercial.support;

import static com.dyaco.spirit_commercial.App.MAX_HR;
import static com.dyaco.spirit_commercial.App.UNIT_E;
import static com.dyaco.spirit_commercial.egym.EgymUtil.SYMBOL_DURATION;
import static com.dyaco.spirit_commercial.support.CommonUtils.chkDuration;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.IMPERIAL;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.METRIC;
import static com.dyaco.spirit_commercial.support.intdef.HrStatus.IDLE_MODE;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.MAX_INC_MAX;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.MAX_LEVEL_MAX;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.MAX_LEVEL_MIN;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.MAX_RPM;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.MAX_SPD_IU_MAX;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.MAX_SPD_MU_MAX;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.THR_MAX;
import static com.dyaco.spirit_commercial.support.intdef.WorkoutIntDef.UNLIMITED;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.dyaco.spirit_commercial.model.webapi.bean.EgymTrainingPlans;
import com.dyaco.spirit_commercial.support.intdef.DeviceIntDef;
import com.dyaco.spirit_commercial.support.intdef.UnitEnum;
import com.dyaco.spirit_commercial.viewmodel.EgymDataViewModel;
import com.dyaco.spirit_commercial.viewmodel.WorkoutViewModel;
import com.dyaco.spirit_commercial.workout.programs.ProgramsEnum;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

public class FormulaUtil {

    public static int incF2I(double inc) {
        return (int) (inc * 2d);
    }

    public static double incI2F(int inc) {
        return (inc) / 2d;
    }

    public static String getAgeByBirthDay(String birthdayStr) {

        if (birthdayStr == null || birthdayStr.isEmpty()) return "0";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        Date birthday = null;
        try {
            birthday = sdf.parse(birthdayStr);
        } catch (ParseException | NullPointerException e) {
            e.printStackTrace();
        }
        //Calendar：日歷
        /*從Calendar對象中或得一個Date對象*/
        Calendar cal = Calendar.getInstance();
        /*把出生日期放入Calendar類型的bir對象中，進行Calendar和Date類型進行轉換*/
        Calendar bir = Calendar.getInstance();
        if (birthday != null) {
            bir.setTime(birthday);
        } else {
            return "";
        }

        /*如果生日大於當前日期，則拋出異常：出生日期不能大於當前日期*/
        if (cal.before(birthday)) {
            throw new IllegalArgumentException("The birthday is before Now,It‘s unbelievable");
        }

        /*取出當前年月日*/
        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH);
        int dayNow = cal.get(Calendar.DAY_OF_MONTH);
        /*取出出生年月日*/
        int yearBirth = bir.get(Calendar.YEAR);
        int monthBirth = bir.get(Calendar.MONTH);
        int dayBirth = bir.get(Calendar.DAY_OF_MONTH);
        /*大概年齡是當前年減去出生年*/
        int age = yearNow - yearBirth;
        /*如果出當前月小與出生月，或者當前月等於出生月但是當前日小於出生日，那麽年齡age就減一歲*/
        if (monthNow < monthBirth || (monthNow == monthBirth && dayNow < dayBirth)) {
            age--;
        }
        return String.valueOf(age);
    }

    public static double convertUnit(UnitEnum unitEnum, int thisUnit, double num) {
        double x = 0;
        switch (unitEnum) {
            case DISTANCE:
            case SPEED:
                if (UNIT_E == thisUnit) {
                    x = num;
                } else {
                    x = thisUnit == 0 ? km2mi(num) : mi2km(num);
                }
                break;
            case WEIGHT:
                if (UNIT_E == thisUnit) {
                    x = num;
                } else {
                    x = thisUnit == 0 ? kg2lb((int) num) : lb2kg((int) num);
                }
                break;
            case HEIGHT:
                if (UNIT_E == thisUnit) {
                    x = num;
                } else {
                    x = thisUnit == 0 ? cm2in((int) num) : in2cm((int) num);
                }
                break;
        }
        return x;
    }

    public static int kg2lb(int weight) {
        return (int) Math.round(weight * 2.2046226218);
    }

    public static int lb2kg(int weight) {
        return (int) Math.round(weight * 0.45359237);
    }

    public static double kg2lb2(double n) {
        double d = n * 2.2046226218d;
        BigDecimal bd = new BigDecimal(d).setScale(1, RoundingMode.HALF_UP);
        return bd.doubleValue();
        //    return n * 2.2046226218d;
    }

    public static double kg2lbPure(double n) {
        return n * 2.2046226218d;
    }

    public static double lb2kg2(double n) {
        double d = n * 0.45359237d;
        BigDecimal bd = new BigDecimal(d).setScale(1, RoundingMode.HALF_UP);
        return bd.doubleValue();
        //    return n * 0.45359237d;
    }

    public static double lb2kgPure(double n) {
        return n * 0.45359237d;
    }

    public static int cm2in(int height) {
        return (int) Math.round(height * 0.39370079);
    }

    public static int in2cm(int height) {
        return (int) Math.round(height * 2.54);
    }


    public static String getConvert(double orgH, @DeviceIntDef.UnitType int orgUnit, int type, int keep) {
        double newH;
        if (orgUnit == UNIT_E) {
            newH = (float) orgH;
        } else {
            if (UNIT_E == DeviceIntDef.IMPERIAL) {
                if (type == 0) {
                    newH = FormulaUtil.cm2ft((float) orgH);
                } else if (type == 1) {
                    newH = FormulaUtil.kg2lb2((float) orgH);
                } else if (type == 2) {
                    newH = FormulaUtil.kph2mph((float) orgH);
                } else if (type == 4) {
                    newH = FormulaUtil.m2ft((float) orgH);
                } else {
                    newH = (float) FormulaUtil.km2mi(orgH);
                }
            } else {
                if (type == 0) {
                    newH = FormulaUtil.ft2cm((float) orgH);
                } else if (type == 1) {
                    newH = FormulaUtil.lb2kg2(orgH);
                } else if (type == 2) {
                    newH = FormulaUtil.mph2kph((float) orgH);
                } else if (type == 4) {
                    newH = FormulaUtil.ft2m((float) orgH);
                } else {
                    newH = (float) FormulaUtil.mi2km(orgH);
                }
            }
        }

        return formatDecimal(newH, keep);
    }





    public static String getConvertHalfUp(double orgH, @DeviceIntDef.UnitType int orgUnit, int type, int keep) {
        double newH;
        if (orgUnit == UNIT_E) {
            newH = (float) orgH;
        } else {
            if (UNIT_E == DeviceIntDef.IMPERIAL) {
                if (type == 0) {
                    newH = FormulaUtil.cm2ft((float) orgH);
                } else if (type == 1) {
                    newH = FormulaUtil.kg2lb2((float) orgH);
                } else if (type == 2) {
                    newH = FormulaUtil.kph2mph((float) orgH);
                } else if (type == 4) {
                    newH = FormulaUtil.m2ft((float) orgH);
                } else {
                    newH = (float) FormulaUtil.km2mi(orgH);
                }
            } else {
                if (type == 0) {
                    newH = FormulaUtil.ft2cm((float) orgH);
                } else if (type == 1) {
                    newH = FormulaUtil.lb2kg2(orgH);
                } else if (type == 2) {
                    newH = FormulaUtil.mph2kph((float) orgH);
                } else if (type == 4) {
                    newH = FormulaUtil.ft2m((float) orgH);
                } else {
                    newH = (float) FormulaUtil.mi2km(orgH);
                }
            }
        }

        return formatDecimalHalfUp(newH, keep);
    }

    public static float ft2cm(float height) {
        return height * 30.5f;
    }

    public static float cm2ft(float n) {
        return n * 0.0328f;
    }

    public static float mph2kph(float n) {
        return n * 1.609344f;
    }

    public static float kph2mph(float n) {
        return n * 0.6213712f;
    }

    public static double kph2mph(double speed) {
        return speed * 0.6213712;
    }

    public static float m2ft(float n) {
        return n * 3.28084f;
    }

    public static float ft2m(float n) {
        return n * 0.3048f;
    }

//    public static float mip2kmp(float n) {
//        return n * 0.621371192f;
//    }
//
//    public static float kmp2mip(float n) {
//        return n * 1.609344f;
//    }


    /**
     * 保留小數，不四捨五入
     *
     * @param value 數值
     * @param keep  保留位數
     * @return string
     */
    public static String formatDecimal(double value, int keep) {
        final DecimalFormat format = new DecimalFormat();
        format.setMaximumFractionDigits(keep);
        format.setGroupingSize(0);
        format.setRoundingMode(RoundingMode.FLOOR);
        return format.format(value);
    }

    public static String formatDecimalHalfUp(double value, int keep) {
        final DecimalFormat format = new DecimalFormat();
        format.setMaximumFractionDigits(keep);
        format.setGroupingSize(0);
        format.setRoundingMode(RoundingMode.HALF_UP); // 四捨五入
        return format.format(value);
    }


    public static String formatDecimal(float value) {
//        final DecimalFormat format = new DecimalFormat();
//        format.setMaximumFractionDigits(keep);
//        format.setGroupingSize(0);
//        format.setRoundingMode(RoundingMode.FLOOR);
        return new DecimalFormat("0.0").format(value);
    }

    public static double formatBigDecimal(double d) {
        BigDecimal bd = new BigDecimal(d);
        return bd.setScale(1, RoundingMode.HALF_UP).doubleValue();
    }

    public static double formatBigDecimal2(double d) {
        BigDecimal bd = new BigDecimal(d);
        return bd.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public static int totalIn2ft(int in) {
        return (int) Math.floor(in / 12f);
    }

    //130 in  > 8.3ft > 4 in
    public static int totalIn2In(int in) {
        return in % 12;
    }

    public static int ft_in2TotalIn(int ft, int in) {
        return ft2In(ft) + in;
    }

    public static int ft2In(int ft) {
        return ft * 12;
    }


    public static double km2mi(double km) {

        return km / 1.609344;
    }

    public static double mi2km(double mi) {

        return mi / 0.62137;
    }


    /**
     * New MaxL / Old Max L =New L / Old L
     * New MaxL =  New L* Old MaxL / Old L
     * New L= New MaxL * Old L / Old MaxL
     */


    /**
     * @param currentNum        2 1
     * @param newNum            3 5
     * @param maxLevelInProfile profile中最大的數值
     * @return 19
     */
    public static int getNewMaxNum(float currentNum, float newNum, int maxLevelInProfile) {
        return (int) Math.floor((newNum * maxLevelInProfile) / currentNum);
    }

    /**
     * @param newMaxNum     選擇的MAX
     * @param currentNum    Program的預設值
     * @param currentMaxNum 當前值的MAX
     * @return newLevel
     */
    public static int calcCurrentNum(float newMaxNum, float currentNum, float currentMaxNum) {
        return (int) Math.floor((newMaxNum * currentNum) / currentMaxNum);
    }

    /**
     * WATT
     *
     * @param weight   weight
     * @param targetHR targetHR
     * @param rpm      rpm
     * @return WATT
     */
    public static double getWATT(int weight, int targetHR, int rpm) {
        return 0.1667 * weight * ((0.05 * targetHR) - (rpm * 0.1)) / 1.8;
    }

    /**
     * 在REACHING狀態，第60秒HR值HR60若不等於0，則做ROR1的計算若HR等於0則強制等待(HOLD在第60秒)HR不等於0時做ROR1的計算 。
     *
     * @param targetHR 目標心跳
     * @param HR0      Program開始後的第一次心跳
     * @param HR60     Program開始後第60秒的心跳
     * @return ROR1
     */
    public static int getROR1(int targetHR, int HR0, int HR60) {
        return (targetHR - HR0) - (targetHR - HR60);
    }

    /**
     * GOAL
     *
     * @param ROR      ROR1/ROR2
     * @param HrSec    Program開始後第 60/75秒的心跳
     * @param targetHR 目標心跳
     * @return GOAL
     */
    public static int getGOAL(int ROR, int HrSec, int targetHR) {
        return ROR * 3 + HrSec - targetHR;
    }


    /**
     * 在 REACHING 狀態，第75秒起含第75秒,若HR不等於0做ROR2的計算,若HR等於0則強制等待(HOLD在第75秒)HR不等於0做ROR2的計算 。
     *
     * @param HR75 Program開始後第75秒的心跳
     * @param HR60 Program開始後第60秒的心跳
     * @return ROR2
     */
    public static int getROR2(int HR75, int HR60) {
        return HR75 - HR60;
    }

    public static String convertPace(double paceSeconds) {

//        String pace = String.format(Locale.getDefault(), "%.0f", paceSeconds);
//
//        int tSec, cSec, cMin;
//        tSec = Integer.parseInt(pace);
//        cSec = tSec % 60;
//        cMin = tSec / 60;
//        String s;
//        if (cMin < 10) {
//            s = "0" + cMin;
//        } else {
//            s = "" + cMin;
//        }
//        if (cSec < 10) {
//            s = s + ":0" + cSec;
//        } else {
//            s = s + ":" + cSec;
//        }
//
//        Log.d("JJJJJJJ", "convertPace: "+ s);

        int totalSeconds = (int) Math.round(paceSeconds); // 四捨五入更準
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);


    }

    public static int kRun2Sec(ProgramsEnum programsEnum, float speed) {
        //5km= 3.106856mi
        //10km= 6.213712mi
        //1小時 3600秒
        //時間 = 距離 / 速度

        float distance;
        if (programsEnum == ProgramsEnum.RUN_5K) {
            distance = UNIT_E == DeviceIntDef.IMPERIAL ? 3.106856f : 5f;
        } else {
            distance = UNIT_E == DeviceIntDef.IMPERIAL ? 6.213712f : 10f;
        }

        return Math.round((distance / speed) * 60 * 60);
    }

    //四捨五入小數點第一位
    public static float getRoundDecimal(float v) {
        return Math.round(v * 10) / 10f;
    }

    public static float getRoundDecimal(double v) {
        return Math.round(v * 10) / 10f;
    }


//    public static String formatSecToM(long sec) {
//        long millisUntilFinished = sec * 1000;
//        String mm = String.format(Locale.getDefault(), "%02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished));
//        String ss = String.format(Locale.getDefault(), "%02d", TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
//
//        return String.format("%s:%s", mm, ss);
//    }

    /**
     * 灰色: 心跳%:0.0 - 0.6,  範圍:0.0 ~ 0.2
     * 綠色:     0.6 - 0.7,      0.2 ~ 0.4
     * 黃色:     0.7 - 0.8,      0.4 ~ 0.6
     * 橙色:     0.8 - 0.9,      0.6 ~ 0.8
     * 紅色:     0.9 - 1.0,      0.8 ~ 1.0
     */
    public float setHrProgress(int hr) {
        float hrPercent = hr * (1f / MAX_HR) <= 1 ? hr * (1f / MAX_HR) : 1;

        if (hrPercent <= 0.6) {
            hrPercent = hrPercent / 3;
        } else {
            hrPercent = 0.2f + ((hrPercent - 0.6f) * 2);
        }
        return hrPercent;
    }


    public float setGarminBodyBatteryProgress(int num) {
        return num * 0.01f;
    }

    public boolean setGarminBodyBatteryThumb(int num) {
        return num > 0;
    }

    public float setGarminRespirationProgress(int num) {
        return num * 0.016666f;
    }

    public String setGarminHR(int num) {
        return num <= 0 ? "--" : String.valueOf(num);
    }

    public boolean isShowProgress(int num) {
        return num > 0;
    }

    public static double nMi2Km(double distance) {
        if (UNIT_E == DeviceIntDef.IMPERIAL) {
            return distance;
        } else {
            return mi2km(distance);
        }
    }


    public static double saveDistance2Mi(double distance) {
        if (UNIT_E == DeviceIntDef.IMPERIAL) {
            return distance;
        } else {
            return km2mi(distance);
        }
    }

    public static double saveDistance2KM(double distance) {
        if (UNIT_E == DeviceIntDef.METRIC) {
            return distance;
        } else {
            return mi2km(distance);
        }
    }

    public void clearEgymViewModel(EgymDataViewModel e) {
        if (e == null) return;
        e.woIntervalData.clear();
        e.createWorkoutParam = null;
        e.selTrainer = null;

    }

    public void clearWorkoutViewModel(WorkoutViewModel w) {

        if (w == null) return;
        w.egymIntervalDistance.set(0);
        w.egymTimePerSets.set(0);
        w.isWorkoutGarmin.set(false);
        w.setHrROR1done(false);
        w.avgLevel.set(0);
        w.avgIncline.set(0);
        w.totalLevel.set(0);
        w.setHrHR0(0);
        w.setHrHR60(0);
        w.setHrHR75(0);
        w.setHrWattDone(false);
        w.setHrROR1(0);
        w.setHrGOAL(0);
        w.setHrMaintaining(false);
        w.setHrROR2(0);
        w.setHrROR1Holding(false);
        w.setHrStatus(IDLE_MODE);
        w.setHrROR2done(false);
        w.setHrROR2Holding(false);
        w.setHrTargetHrMax(0);
        w.selWeightIU.set(0);
        w.selYO.set(0);
        w.selWeightMU.set(0);
        w.selHeightIU.set(0);
        w.selHeightMU.set(0);
        w.selGender.set(0);
        w.setMin2HR(0);
        w.setMin3HR(0);
        w.setVo2SM2(0);
        w.setVo2Max(0);
        w.setVo2SM1(0);
        w.setStageHR2(0);
        w.setStageHR1(0);

        w.isCoolDowning.set(false);
        w.isWarmUpIng.set(false);

        w.selMaxSpeedOrLevel.set(0);
        w.setAppleWatchCalories(0);
        w.setAppleWatchHr(0);
        w.currentCalories.set(0);
        w.targetCalories.set(0);
        w.caloriesLeft.set(0);
        w.currentSpeed.set(0);
        w.currentInclineValue.set(0);
        w.currentInclineLevel.set(0);
        w.currentSpeedLevel.set(0);
        w.currentLevel.set(MAX_LEVEL_MIN);

        w.inclineDiagramBarList.clear();
        w.speedDiagramBarList.clear();
        w.blankDiagramBarList.clear();
        w.hrList.clear();
        w.rpmList.clear();
        w.metsList.clear();
        w.orgArrayIncline = null;
        w.orgArraySpeedAndLevel = null;
        w.orgArrayInclineE = null;
        w.orgArraySpeedAndLevelE = null;
        w.currentSegment.set(0);
        w.currentHeartRate.set(0);
        w.setWpHr(0);
        w.setHpHr(0);
        w.setBleHr(0);
        w.setGarminHr(0);
        w.currentDistance.set(0);
        w.heartRateChartNum = "";
        w.selWorkoutTime.set(UNLIMITED);
        w.remainingTime.set(0);
        w.elapsedTime.set(0);

        w.elapsedTimeShow.set(0);
        w.totalElapsedTimeShow.set(0);
        w.remainingTimeShow.set(0);

        w.coolDownTime.set(0);
        w.warmUpTime.set(0);
        w.avgSpeed.set(0);
        w.avgRpm.set(0);
        w.summaryPace.set(0);
        w.summaryMaxIncline.set(0);
        w.summaryMaxHeartRate.set(0);
        w.summaryMets.set(0);
        w.summaryMaxSpeedAndLevel.set(0);
        w.summaryVo2.set(0);
        w.summaryPower.set(0);
        w.currentPower.set(0);
        w.pwmLevelDA.set(0);
        w.avgPower.set(0);
        //  w.garminSpo2.set("--");
        w.disabledInclineUpdate.set(false);
        w.disabledSpeedUpdate.set(false);
        w.disabledLevelUpdate.set(false);
        w.currentElevationGain.set(0);
        w.cttLevelHrList.clear();
        w.repeatCount = 1;
        w.targetDistance.set(0);
        w.currentStage.set(1);
        w.distanceLeft.set(0);

        w.wingateWattsList.clear();


//        w.totalStep.set(0);
        w.stepLeft.set(0);
        w.targetSteps.set(0);
        w.currentStep.set(0);

        w.metsLeft.set(0);
        w.targetMets.set(0);
        w.totalMets.set(0);
        w.currentMets.set(0);









        w.currentShowTimeText.set("00:00");
        w.selRestSpeedLevel.set(0);
        w.selSprintSpeedLevel.set(0);
        w.selRestTimeSec.set(0);
        w.selSprintTimeSec.set(0);
        w.setWorkoutDone(false);
        w.currentMaxInclineValue.set(0);
        w.currentMaxInclineLevel.set(0);
        w.currentMaxSpeedLevel.set(0);
        w.currentMaxSpeed.set(0);
        w.currentPauseTimeText.set("05:00");
        w.totalElapsedTime.set(0);
        w.currentIntervals.set(0);
        w.selIntervals.set(0);
        w.selTargetSpeed.set(0);
        w.selTargetHrBpm.set(0);
        w.selHrMax.set(0);
        w.recommendedSpeed.set(0);

        w.orgMaxSpeedInProfile.set(0);
        w.orgMaxInclineInProfile.set(0);

        w.selConstantPowerW.set(0);
        w.constantPowerW.set(0);

        w.egymTargetDistance.set(E_BLANK);
        w.egymTargetSpeed.set(E_BLANK);
        w.egymTargetIncline.set(E_BLANK);
        w.egymTargetHeartRate.set(E_BLANK);

        w.setWfiTT(0);

        w.setSelProgram(null);


        w.setAppleWatchHr(0);
    }


    //50~200
    private static final float EGYM_CHART_NUM = 1f;

    public static float getHrPercentInChart(int hr) {
        float p = ((float) hr / THR_MAX) * EGYM_CHART_NUM;
        return Math.min(p, EGYM_CHART_NUM);
    }

    public static float getHrPercentInChart(int hr, int max) {
        float p = ((float) hr / THR_MAX) * max;
        return Math.min(p, max);
    }

    //by value  -3 deg ~ 30 deg
    public static float getInclinePercentInChart2(double incline) {
        float p = ((float) incline / ((float) MAX_INC_MAX / 2)) * EGYM_CHART_NUM;
        return Math.min(p, EGYM_CHART_NUM);
    }

    //1km/h ~ 29 km/h
    public static float getKmPercentInChart(double speed) {
        float p = (float) ((speed / (MAX_SPD_MU_MAX / 10)) * EGYM_CHART_NUM);
        return Math.min(p, EGYM_CHART_NUM);
    }

    public static float getMiPercentInChart(double speed) {
        float p = ((float) speed / ((float) MAX_SPD_IU_MAX / 10)) * EGYM_CHART_NUM;
        return Math.min(p, EGYM_CHART_NUM);
    }

    public static float getLevelPercentInChart(double level) {
        float p = ((float) level / ((float) MAX_LEVEL_MAX)) * EGYM_CHART_NUM;
        return Math.min(p, EGYM_CHART_NUM);
    }

    public static float getRpmPercentInChart(double rpm) {
        float p = ((float) rpm / (MAX_RPM)) * EGYM_CHART_NUM;
        return Math.min(p, EGYM_CHART_NUM);
    }


    // 按比例縮放數據
    public static double[] scaleArray(double[] arr, float originalMax, float targetMax) {
        float scale = targetMax / originalMax; // 使用 240 作為最大值
        double[] result = new double[arr.length];

        for (int i = 0; i < arr.length; i++) {
            result[i] = arr[i] * scale;
        }

        return result;
    }

    public static double m2Km(double meters) {
        return meters / 1000.0;
    }

    public static String E_BLANK = "—";
    public static String getEgymUnitS(int egymUnit, Double value) {
        String text = E_BLANK;
        if (value == null) return text;
        if (egymUnit == METRIC && UNIT_E == IMPERIAL) value = (double) kph2mph(value.floatValue());
        if (egymUnit == IMPERIAL && UNIT_E == METRIC) value = (double) mph2kph(value.floatValue());
        text = formatDecimal(value, 2);
        return text;
    }

    public static String getEgymUnitD(int egymUnit, Double value) {
        String text = E_BLANK;
        if (value == null) return text;
        if (egymUnit == METRIC && UNIT_E == IMPERIAL) value = km2mi(value.floatValue());
        if (egymUnit == IMPERIAL && UNIT_E == METRIC) value = mi2km(value.floatValue());
        text = formatDecimal(value, 2);
        return text;
    }


    public static  List<Integer> getDurationTimesList(EgymTrainingPlans.TrainerDTO trainerDTO) {
        return trainerDTO.getIntervals().stream()
                .map(intervalsDTO -> chkDuration(intervalsDTO.getDuration()) / 1000)
                .collect(Collectors.toList());
    }


    public static EgymTrainingPlans.TrainerDTO getTrainerDTO(EgymDataViewModel egymDataViewModel) {
        if (egymDataViewModel == null) return null;

        return Optional.of(egymDataViewModel)
                .map(viewModel -> viewModel.egymTrainingPlansData)
                .map(LiveData::getValue)
                .map(EgymTrainingPlans::getTrainer)
                .filter(trainers -> {
                    int currentPlanNum = egymDataViewModel.currentPlanNum.get();
                    return currentPlanNum >= 0 && currentPlanNum < trainers.size();
                })
                .map(trainers -> trainers.get(egymDataViewModel.currentPlanNum.get()))
                .orElse(null);
    }




    public static void initEgymWorkoutData(EgymDataViewModel egymDataViewModel) {
        if (egymDataViewModel == null) return;

        // ✅ 1. 取得 TrainerDTO
        EgymTrainingPlans.TrainerDTO trainerDTO = getTrainerDTO(egymDataViewModel);
        if (trainerDTO == null) {
            Log.w("EgymWorkoutInit", "TrainerDTO 無法取得，請檢查訓練計劃資料。");
            return;
        }

        // ✅ 2. 保留 -99 並處理秒數 [-99, 60, 60, 120, -99]
        egymDataViewModel.durationTimesListKnowZero = trainerDTO.getIntervals().stream()
                .map(intervalsDTO -> intervalsDTO.getDuration() == SYMBOL_DURATION
                        ? SYMBOL_DURATION
                        : intervalsDTO.getDuration() / 1000)
                .collect(Collectors.toList());

        // ✅ 3. 處理每個 Set 的秒數 (-99 轉為 30 分鐘或其他規則) [1800, 60, 60, 120, 1800]
        List<Integer> durationTimesList = getDurationTimesList(trainerDTO);

        egymDataViewModel.durationTimesList = new ArrayList<>(durationTimesList);
     //   Log.d("BBBBBBB", "111initEgym: " + durationTimesList);

        // ✅ 4. 計算每個 Set 開始的時間點 (加上最後一個 +2 秒)  [1800, 1860, 1920, 2040, 3842]
        egymDataViewModel.durationRealTimesList = new ArrayList<>(transformArray(egymDataViewModel.durationTimesList));

        // ✅ 5. 計算總時數（秒）
        int totalTime = durationTimesList.stream().mapToInt(Integer::intValue).sum();
        egymDataViewModel.totalSetsTime = totalTime;

//        // ✅ 6. 設定 bar 數量並分配 Set 在 bar 上的 index 位置 //[8, 9, 10, 11, 19]
//        List<Integer> setsTimePosition = allocateBars(BAR_COUNT_NORMAL, totalTime, durationTimesList);
//        egymDataViewModel.setsTimePosition = new ArrayList<>(setsTimePosition);



        // ✅ 7. 日誌輸出
     //   Log.d("GGGGGEEEEEEE", "總 bar 數: " + totalBars + ", 總時數: " + totalTime + ", durationTimesList: " + durationTimesList);

        Log.d("EGYM_W", "total 時間秒數: " + totalTime);

        Log.d("EGYM_W", "總SETs: " + durationTimesList.size());
        Log.d("EGYM_W", "20個bar,每一個bar的秒數: " + (totalTime / 20));
        Log.d("EGYM_W", "sets所佔的秒數: " + durationTimesList);
        Log.d("EGYM_W", "sets所佔的秒數有-99(沒設定時間): " + egymDataViewModel.durationTimesListKnowZero);
        Log.d("EGYM_W", "sets的起始秒數: " + egymDataViewModel.durationRealTimesList);
//        Log.d("EGYM_W", "sets在20個BAR,所佔的Index: " + setsTimePosition);
        Log.d("EGYM_W", "=================================================");
    }

    public static List<Integer> convertToPositionList(List<Integer> durationTimesList) {
        List<Integer> setsTimePosition = new ArrayList<>();
        for (int i = 0; i < durationTimesList.size(); i++) {
            setsTimePosition.add(i);
        }
        return setsTimePosition;
    }


    public static List<Integer> allocateBars(int totalBars, int totalTime, List<Integer> durationTimesList) {
        // sets所佔的秒數: [360] >  sets在20個BAR,所佔的Index: [19]

        // **避免除以零錯誤**
        if (totalTime == 0 || durationTimesList == null || durationTimesList.isEmpty()) {
            return Collections.singletonList(1);
        }

        int n = durationTimesList.size();
        int[] bars = new int[n];          // 每個 set 分配的 bar 數
        double[] exact = new double[n];     // 精確分配值（浮點數）
        double[] fraction = new double[n];  // 小數部分

        boolean allZero = true;
        for (int value : durationTimesList) {
            if (value != 0) {
                allZero = false;
                break;
            }
        }
        if (allZero) {
            return Collections.singletonList(1); // 若所有數值皆為 0，回傳 [1]
        }

        // 以最大餘數法計算，每個 set 的初始分配為 floor(exact)，但保證至少 1 個 bar
        int sumBars = 0;
        for (int i = 0; i < n; i++) {
            exact[i] = (double) durationTimesList.get(i) / totalTime * totalBars;
            int base = (int) Math.floor(exact[i]);
            if (base < 1) {
                bars[i] = 1;
                fraction[i] = exact[i] - 1;  // 可能為負值
            } else {
                bars[i] = base;
                fraction[i] = exact[i] - base;
            }
            sumBars += bars[i];
        }

        // 若總和不等於 totalBars，依差額補足或扣除（但每個 set 至少保留 1 個 bar）
        int diff = totalBars - sumBars;
        if (diff > 0) {
            // 補足多餘的 bar，優先給小數部分較大的 set
            for (int r = 0; r < diff; r++) {
                int maxIndex = -1;
                double maxFraction = -Double.MAX_VALUE;
                for (int i = 0; i < n; i++) {
                    if (fraction[i] > maxFraction) {
                        maxFraction = fraction[i];
                        maxIndex = i;
                    }
                }
                bars[maxIndex]++;
                // 將該項的 fraction 設為極小值，避免再次被補足
                fraction[maxIndex] = -Double.MAX_VALUE;
            }
        } else if (diff < 0) {
            // 若分配過多，從小數部分最小的 set 扣除，但不低於 1 個 bar
            // 修正：當候選者 fractional remainder 相同時，選擇 index 較大的那一個，
            // 以使相同 duration 的大項能更對稱
            for (int r = 0; r < -diff; r++) {
                int minIndex = -1;
                double minFraction = Double.MAX_VALUE;
                for (int i = 0; i < n; i++) {
                    if (bars[i] > 1) {
                        // 若 fraction[i] 小於目前最小值，或相等但 i 較大，則選取該 index
                        if (fraction[i] < minFraction || (fraction[i] == minFraction && i > minIndex)) {
                            minFraction = fraction[i];
                            minIndex = i;
                        }
                    }
                }
                if (minIndex != -1) {
                    bars[minIndex]--;
                    fraction[minIndex] = Double.MAX_VALUE;
                }
            }
        }

        List<Integer> setsTimePosition = new ArrayList<>();
        int cumulative = 0;
        for (int i = 0; i < n; i++) {
            cumulative += bars[i];
            int pos = cumulative - 1;
            // 確保不超過 totalBars - 1（例如 index 0~19）
            pos = Math.min(pos, totalBars - 1);
            setsTimePosition.add(pos);
        }

        // 再次檢查確保結果嚴格遞增（理論上每個 bars[i] >= 1 時已保證）
        for (int i = 1; i < setsTimePosition.size(); i++) {
            if (setsTimePosition.get(i) <= setsTimePosition.get(i - 1)) {
                setsTimePosition.set(i, setsTimePosition.get(i - 1) + 1);
                if (setsTimePosition.get(i) > totalBars - 1) {
                    setsTimePosition.set(i, totalBars - 1);
                }
            }
        }

        return setsTimePosition;
    }

    public static List<Integer> transformArray(List<Integer> list) {
        if (list == null || list.isEmpty()) {
            return list; // 如果 List 為空，直接返回
        }

        List<Integer> result = new ArrayList<>(list);

        if (list.size() == 1) {
            // 如果 List 只有一筆數據，直接加 2
            result.set(0, result.get(0) + 2);
        } else {
            // 依照規則處理
            for (int i = 1; i < result.size(); i++) {
                result.set(i, result.get(i) + result.get(i - 1)); // 加上前一個索引的值
            }
            result.set(result.size() - 1, result.get(result.size() - 1) + 2); // 最後一個索引額外加 2
        }

        return result;
    }


    public static String toHex(String str) {
        StringBuilder hex = new StringBuilder();
        for (char ch : str.toCharArray()) {
            hex.append(String.format("%02X", (int) ch)); // 轉換為 16 進制
        }
        return hex.toString();
    }

}
