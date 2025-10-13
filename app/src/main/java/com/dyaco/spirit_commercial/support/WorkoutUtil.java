package com.dyaco.spirit_commercial.support;

import static com.dyaco.spirit_commercial.App.UNIT_E;
import static com.dyaco.spirit_commercial.App.getApp;
import static com.dyaco.spirit_commercial.MainActivity.isTreadmill;
import static com.dyaco.spirit_commercial.MainActivity.isUs;
import static com.dyaco.spirit_commercial.support.FormulaUtil.km2mi;
import static com.dyaco.spirit_commercial.support.FormulaUtil.mi2km;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.MAX_LEVEL_MAX;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.MAX_LEVEL_MIN;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.POWER_MIN;
import static com.dyaco.spirit_commercial.workout.programs.ProgramsEnum.FITNESS_TEST;
import static com.dyaco.spirit_commercial.workout.programs.ProgramsEnum.WATTS;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;

import androidx.core.content.ContextCompat;

import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.support.intdef.DeviceIntDef;
import com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS;
import com.dyaco.spirit_commercial.support.intdef.WorkoutIntDef;
import com.dyaco.spirit_commercial.viewmodel.WorkoutViewModel;
import com.dyaco.spirit_commercial.workout.programs.ProgramsEnum;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;

public class WorkoutUtil {

    public static int getSpecifyValue(int currentValue, int value, boolean isSpecify) {
        return isSpecify ? (BigDecimal.valueOf(currentValue - value).negate()).intValue() : value;
    }

    public static int getInclineLevel(float inclineValue) {
        return Math.round(inclineValue * 2);
    }

    public static int getSpeedLevel(float speedValue) {
        return Math.round(speedValue * 10);
    }

    /**
     * Speed階數轉實際數值
     *
     * @param speedLevel Speed階數
     * @return 實際數值
     */
    public static float getSpeedValue(int speedLevel) {
        return speedLevel / 10f;
    }

    public static float getInclineValue(int inclineLevel) {
        return inclineLevel / 2f;
    }

    /**
     * @param speedNum speedLevel
     * @param unit     speedNum的單位
     * @return n
     */
    public static int getSpeedLevelNum(float speedNum, int unit) {

        int x = Math.round(speedNum * 10);
        int value;

        if (UNIT_E == DeviceIntDef.IMPERIAL) {
            if (unit == DeviceIntDef.IMPERIAL) {
                value = x;
            } else {
                value = (int) Math.round(km2mi(x));
            }
        } else {
            if (unit == DeviceIntDef.METRIC) {
                value = x;
            } else {
                value = (int) Math.round(mi2km(x));
            }
        }

        return value;
    }

    public static Integer getCttPerformanceVO2(int sec) {
        int min = (int) (Math.floor(sec / 60f) - 1) ;

        if (min < 0 || min > 11) return 0;
        HashMap<Integer, Integer> cttPerformanceVO2 = new HashMap<>();
        cttPerformanceVO2.put(0, 14);
        cttPerformanceVO2.put(1, 14);
        cttPerformanceVO2.put(2, 17);
        cttPerformanceVO2.put(3, 19);
        cttPerformanceVO2.put(4, 22);
        cttPerformanceVO2.put(5, 25);
        cttPerformanceVO2.put(6, 28);
        cttPerformanceVO2.put(7, 31);
        cttPerformanceVO2.put(8, 34);
        cttPerformanceVO2.put(9, 36);
        cttPerformanceVO2.put(10, 38);
        cttPerformanceVO2.put(11, 42);

        return cttPerformanceVO2.get(min);
    }

    public static Float getGerkinVO2(float stage) {

        HashMap<Float, Float> gerkinVo2Map = new HashMap<>();
        gerkinVo2Map.put(1f, 31.15f);
        gerkinVo2Map.put(2.1f, 32.55f);
        gerkinVo2Map.put(2.2f, 33.60f);
        gerkinVo2Map.put(2.3f, 34.65f);
        gerkinVo2Map.put(2.4f, 35.35f);
        gerkinVo2Map.put(3.1f, 37.45f);
        gerkinVo2Map.put(3.2f, 39.55f);
        gerkinVo2Map.put(3.3f, 41.3f);
        gerkinVo2Map.put(3.4f, 43.4f);
        gerkinVo2Map.put(4.1f, 44.1f);
        gerkinVo2Map.put(4.2f, 45.15f);
        gerkinVo2Map.put(4.3f, 46.2f);
        gerkinVo2Map.put(4.4f, 46.5f);
        gerkinVo2Map.put(5.1f, 48.6f);
        gerkinVo2Map.put(5.2f, 50f);
        gerkinVo2Map.put(5.3f, 51.4f);
        gerkinVo2Map.put(5.4f, 52.8f);
        gerkinVo2Map.put(6.1f, 53.9f);
        gerkinVo2Map.put(6.2f, 54.9f);
        gerkinVo2Map.put(6.3f, 56f);
        gerkinVo2Map.put(6.4f, 57f);
        gerkinVo2Map.put(7.1f, 57.7f);
        gerkinVo2Map.put(7.2f, 58.8f);
        gerkinVo2Map.put(7.3f, 60.2f);
        gerkinVo2Map.put(7.4f, 61.2f);
        gerkinVo2Map.put(8.1f, 62.3f);
        gerkinVo2Map.put(8.2f, 63.3f);
        gerkinVo2Map.put(8.3f, 64f);
        gerkinVo2Map.put(8.4f, 65f);
        gerkinVo2Map.put(9.1f, 66.5f);
        gerkinVo2Map.put(9.2f, 68.2f);
        gerkinVo2Map.put(9.3f, 69f);
        gerkinVo2Map.put(9.4f, 70.7f);
        gerkinVo2Map.put(10.1f, 72.1f);
        gerkinVo2Map.put(10.2f, 73.1f);
        gerkinVo2Map.put(10.3f, 73.8f);
        gerkinVo2Map.put(10.4f, 74.9f);
        gerkinVo2Map.put(11.1f, 76.3f);
        gerkinVo2Map.put(11.2f, 77.7f);
        gerkinVo2Map.put(11.3f, 79.1f);
        gerkinVo2Map.put(11.4f, 80f);

        return gerkinVo2Map.get(stage);
    }

//    public static boolean gerkinInclineUpdate(int sec) {
//        return sec == 60 || sec == 180 || sec == 300 || sec == 420 || sec == 540;
//    }

//    static class WFICheckBean {
//        public WFICheckBean() {
//        }
//
//        private boolean isSpeedUpdate;
//        private boolean isInclineUpdate;
//        private float speedValue;
//        private int inclineValue;
//
//        public boolean isInclineUpdate() {
//            return isInclineUpdate;
//        }
//
//        public void setInclineUpdate(boolean inclineUpdate) {
//            isInclineUpdate = inclineUpdate;
//        }
//
//        public int getInclineValue() {
//            return inclineValue;
//        }
//
//        public void setInclineValue(int inclineValue) {
//            this.inclineValue = inclineValue;
//        }
//
//        public boolean isSpeedUpdate() {
//            return isSpeedUpdate;
//        }
//
//        public void setSpeedUpdate(boolean speedUpdate) {
//            isSpeedUpdate = speedUpdate;
//        }
//
//        public float getSpeedValue() {
//            return speedValue;
//        }
//
//        public void setSpeedValue(float speedValue) {
//            this.speedValue = speedValue;
//        }
//    }
//
//    public static WFICheckBean checkWfiLevelUpdate(int time) {
//        WFICheckBean wfiCheckBean = new WFICheckBean();
//        wfiCheckBean.setInclineUpdate(false);
//        wfiCheckBean.setSpeedUpdate(false);
//
//        if (time == 0) {
//            wfiCheckBean.setSpeedUpdate(true);
//            wfiCheckBean.setSpeedValue(getSpeedLevelNum(3.0f, DeviceIntDef.IMPERIAL));
//        }
//
//        if (time == 181) {
//            wfiCheckBean.setSpeedUpdate(true);
//            wfiCheckBean.setSpeedValue(getSpeedLevelNum(3.0f, DeviceIntDef.IMPERIAL));
//        }
//
//
//
//        return wfiCheckBean;
//    }

    // public static

    public static int giCheck = -1;

    public static boolean checkGerkinInclineUpdate(int segment) {

        if (segment == 4 && segment != giCheck) {
            giCheck = segment;
            return true;
        }

        if (segment == 12 && segment != giCheck) {
            giCheck = segment;
            return true;
        }

        if (segment == 20 && segment != giCheck) {
            giCheck = segment;
            return true;
        }

        if (segment == 28 && segment != giCheck) {
            giCheck = segment;
            return true;
        }

        if (segment == 36 && segment != giCheck) {
            giCheck = segment;
            return true;
        }

        return false;
        // return segment == 4 || segment == 12 || segment == 20 || segment == 28 || segment == 36;
    }

    public static float gsCheck = -1;

    public static boolean checkGerkinSpeedUpdate(float stage) {

        if (stage == 1f && stage != gsCheck) {
            gsCheck = stage;
            return true;
        }

        if (stage == 3.1f && stage != gsCheck) {
            gsCheck = stage;
            return true;
        }

        if (stage == 5.1f && stage != gsCheck) {
            gsCheck = stage;
            return true;
        }

        if (stage == 7.1f && stage != gsCheck) {
            gsCheck = stage;
            return true;
        }

        if (stage == 9.1f && stage != gsCheck) {
            gsCheck = stage;
            return true;
        }

        if (stage == 11.1f && stage != gsCheck) {
            gsCheck = stage;
            return true;
        }

        return false;

        //   return segment == 0 || segment == 8 || segment == 18 || segment == 26 || segment == 34;
    }

    public static float setStage(int segment) {
        float stage = 0;
        switch (segment) {
            case 1:
            case 2:
            case 3:
                stage = 1;
                break;
            case 4:
                stage = 2.1f;
                break;
            case 5:
                stage = 2.2f;
                break;
            case 6:
                stage = 2.3f;
                break;
            case 7:
                stage = 2.4f;
                break;
            case 8:
                stage = 3.1f;
                break;
            case 9:
                stage = 3.2f;
                break;
            case 10:
                stage = 3.3f;
                break;
            case 11:
                stage = 3.4f;
                break;
            case 12:
                stage = 4.1f;
                break;
            case 13:
                stage = 4.2f;
                break;
            case 14:
                stage = 4.3f;
                break;
            case 15:
                stage = 4.4f;
                break;
            case 16:
                stage = 5.1f;
                break;
            case 17:
                stage = 5.2f;
                break;
            case 18:
                stage = 5.3f;
                break;
            case 19:
                stage = 5.4f;
                break;
            case 20:
                stage = 6.1f;
                break;
            case 21:
                stage = 6.2f;
                break;
            case 22:
                stage = 6.3f;
                break;
            case 23:
                stage = 6.4f;
                break;
            case 24:
                stage = 7.1f;
                break;
            case 25:
                stage = 7.2f;
                break;
            case 26:
                stage = 7.3f;
                break;
            case 27:
                stage = 7.4f;
                break;
            case 28:
                stage = 8.1f;
                break;
            case 29:
                stage = 8.2f;
                break;
            case 30:
                stage = 8.3f;
                break;
            case 31:
                stage = 8.4f;
                break;
            case 32:
                stage = 9.1f;
                break;
            case 33:
                stage = 9.2f;
                break;
            case 34:
                stage = 9.3f;
                break;
            case 35:
                stage = 9.4f;
                break;
            case 36:
                stage = 10.1f;
                break;
            case 37:
                stage = 10.2f;
                break;
            case 38:
                stage = 10.3f;
                break;
            case 39:
                stage = 10.4f;
                break;
            case 40:
                stage = 11.1f;
                break;
            case 41:
                stage = 11.2f;
                break;
            case 42:
                stage = 11.3f;
                break;
            case 43:
                stage = 11.4f;
                break;

        }

        return stage;
    }


    public static int getMaxSpeedLevel(ProgramsEnum currentProgram) {
        int max;
        if (isTreadmill) {
            max = (UNIT_E == DeviceIntDef.IMPERIAL ? OPT_SETTINGS.MAX_SPD_IU_MAX : OPT_SETTINGS.MAX_SPD_MU_MAX);
        } else {
          //  max = (currentProgram != WATTS && currentProgram != FITNESS_TEST) ? MAX_LEVEL_MAX : POWER_MAX;
            max = MAX_LEVEL_MAX;
        }
        return max;
    }

    /**
     * Chart的高度
     * 分為
     * 1.公制 240, Range 16.0~20.0 KPH
     * 2.英制 150, Range 10.0~12.0 MPH
     * 3.WATT 300
     * 4.LEVEL 40
     * @param currentProgram p
     * @return c
     */
    public static float getMaxSpeedValue(ProgramsEnum currentProgram) {
        float max;
        if (isTreadmill) {
            max = getSpeedValue(UNIT_E == DeviceIntDef.IMPERIAL ? OPT_SETTINGS.MAX_SPD_IU_MAX : OPT_SETTINGS.MAX_SPD_MU_MAX);
        } else {
         //   max = (currentProgram != WATTS && currentProgram != FITNESS_TEST) ? MAX_LEVEL_MAX : POWER_MAX;
            max = (currentProgram != WATTS && currentProgram != FITNESS_TEST) ? MAX_LEVEL_MAX : MAX_LEVEL_MAX;
            //MAX_LEVEL_MAX = ModeEnum.getBikeLevel().length
        }
        return max;
    }

    public static float getMaxSpeedMinValue(ProgramsEnum currentProgram) {
        float min;
        if (isTreadmill) {
            min = getSpeedValue(UNIT_E == DeviceIntDef.IMPERIAL ? OPT_SETTINGS.MAX_SPD_IU_DEF : OPT_SETTINGS.MAX_SPD_MU_DEF);
            //  min = 1;
        } else {
            min = (currentProgram != WATTS && currentProgram != FITNESS_TEST) ? 1 : POWER_MIN;
        }
        return min;
    }

    public static float getMinSpeedValue(ProgramsEnum currentProgram) {
        float min;
        if (isTreadmill) {
            min = getSpeedValue(UNIT_E == DeviceIntDef.IMPERIAL ? OPT_SETTINGS.MIN_SPD_IU : OPT_SETTINGS.MIN_SPD_MU);
        } else {
            min = (currentProgram != WATTS && currentProgram != FITNESS_TEST) ? 1 : POWER_MIN;
        }
        return min;
    }

    public static int getMinSpeedLevel(ProgramsEnum currentProgram) {
        int min;
        if (isTreadmill) {
            min = (UNIT_E == DeviceIntDef.IMPERIAL ? OPT_SETTINGS.MIN_SPD_IU : OPT_SETTINGS.MIN_SPD_MU);
            //  min = 1;
        } else {
          //  min = (currentProgram != WATTS && currentProgram != FITNESS_TEST) ? 1 : POWER_MIN;
            // TODO: PF
            min = MAX_LEVEL_MIN;
        }
        return min;
    }

    public static float get5kTarget() {
        return UNIT_E == DeviceIntDef.IMPERIAL ? 3.1f : 5f;
    }

    public static float get10kTarget() {
        return UNIT_E == DeviceIntDef.IMPERIAL ? 6.2f : 10f;
    }

    public static float getArmyTarget() {
        return UNIT_E == DeviceIntDef.IMPERIAL ? 2f : 3.2f;
    }

    public static float getNavyTarget() {
        return UNIT_E == DeviceIntDef.IMPERIAL ? 1.5f : 2.4f;
    }

    public static float getMarinesTarget() {
        return UNIT_E == DeviceIntDef.IMPERIAL ? 3f : 4.8f;
    }


    public static int getInclineAd(WorkoutViewModel w) {
        int ad;
        try {
            ad = w.getFrontInclineAdList().get(getInclineLevel(w.currentInclineValue.get()));
        } catch (Exception e) {
            ad = 0;
        }
        return ad;
    }

    /**
     * 使用Profile百分比計算數值
     */
    public static int getTreadmillSpeed(WorkoutViewModel w, int num) {
        return (w.selMaxSpeedOrLevel.get() * num) / 100;
    }

    /**
     * 使用Profile百分比計算數值
     *
     * (selMaxSpeedOrLevel 選5.1 就是 51)
     *
     */
    public static int getTreadmillSpeedF(WorkoutViewModel w, float num) {

     //   Log.d("####RRR@@@", "getTreadmillSpeedF: "+ w.selMaxSpeedOrLevel.get() +" * " + num +", "+ Math.round((w.selMaxSpeedOrLevel.get() * num) / 100));
        //12 > 1.2mph
        return Math.round((w.selMaxSpeedOrLevel.get() * num) / 100);
    }

    /**
     * 設定速度 profile
     * @param w w
     * @param speed speed
     */
    public static void setSpeedDiagram(WorkoutViewModel w, int speed) {
        StringBuilder num = new StringBuilder();

        int[] x = Arrays.stream(ProgramsEnum.getProgram(w.selProgram.getCode()).getTreadmillSpeedNum()
                .split("#", -1))
                .mapToInt(Integer::parseInt)
                .toArray();
        int length = x.length;

        for (int i = 0; i < length; i++) {
            num.append(speed).append("#");
        }
        num = num.deleteCharAt(num.length() - 1);

        w.orgArraySpeedAndLevel =
                Arrays.stream(num.toString()
                        .split("#", -1))
                        .mapToInt(Integer::parseInt)
                        .toArray();
    }


    /**
     * 依照最低速度產生圖
     * 0.5mi / 0.8km
     *
     * @param w w
     */
    public static void setMinSpeedDiagram(WorkoutViewModel w) {
        StringBuilder num = new StringBuilder();
        //   int n = UNIT_E == IMPERIAL ? MIN_SPD_IU : MIN_SPD_MU;
        int n = getMinSpeedLevel(w.selProgram);

        int[] x = Arrays.stream(ProgramsEnum.getProgram(w.selProgram.getCode()).getTreadmillSpeedNum()
                .split("#", -1))
                .mapToInt(Integer::parseInt)
                .toArray();
        int length = x.length;

        for (int i = 0; i < length; i++) {
            num.append(n).append("#");
        }
        num = num.deleteCharAt(num.length() - 1);

        w.orgArraySpeedAndLevel =
                Arrays.stream(num.toString()
                        .split("#", -1))
                        .mapToInt(Integer::parseInt)
                        .toArray();

    }

    /**
     * Treadmill Incline
     *
     * @param w w
     */
    public static void setTreadmillInclineDiagram(WorkoutViewModel w) {
        w.orgArrayIncline =
                Arrays.stream(w.selProgram.getTreadmillInclineNum()
                        .split("#", -1))
                        .mapToInt(Integer::parseInt)
                        .toArray();
    }

    /**
     * 依照百分比計算
     * Treadmill Speed

     * 公制 最高240
     * [48, 72, 96, 120, 192, 240, 240, 240, 240, 240, 240, 240, 240, 240, 240, 240, 240, 240, 240, 240, 240, 192, 120, 96, 72, 48]
     * @param w w
     */
    public static void setTreadmillSpeedPresentDiagram(WorkoutViewModel w) {

//        w.orgArraySpeedAndLevel =
//                Arrays.stream(w.selProgram.getTreadmillSpeedNum()
//                        .split("#", -1))
//                        .mapToInt(s -> getTreadmillSpeed(w, Integer.parseInt(s)))
//                        .toArray();

        w.orgArraySpeedAndLevel =
                Arrays.stream(w.selProgram.getTreadmillSpeedNum()
                                .split("#", -1))
                        .mapToInt(s -> getTreadmillSpeedF(w, Float.parseFloat(s)))
                        .toArray();
    }


    /**
     * Bike Incline
     *
     * @param w w
     */
    public static void setBikeInclineDiagram(WorkoutViewModel w) {
        w.orgArrayIncline =
                Arrays.stream(w.selProgram.getBikeInclineNum()
                        .split("#", -1))
                        .mapToInt(Integer::parseInt)
                        .toArray();
    }

    /**
     * Bike Level
     *
     * @param w w
     */
    public static void setBikeLevelDiagram(WorkoutViewModel w) {

    // Log.d("QQQQQQWWWWWW", "@@@@@@getTreadmillSpeedF: " + w.selProgram.getBikeLevelNum());

        w.orgArraySpeedAndLevel =
                Arrays.stream(w.selProgram.getBikeLevelNum()
                        .split("#", -1))
                        .mapToInt(Integer::parseInt)
                   //     .mapToInt(s -> getBikeLevelF(w, Float.parseFloat(s)))
                        .toArray();
    }

    /**
     * 使用Profile百分比計算數值
     */
    public static int getBikeLevelF(WorkoutViewModel w, float num) {

     //   Log.d("QQQQQQWWWWWW", "getBikeLevelF: " +w.selMaxSpeedOrLevel.get() +", "+ Math.round((w.selMaxSpeedOrLevel.get() * num) / 100));
        return Math.round((w.selMaxSpeedOrLevel.get() * num) / 100);
    }

//    public static boolean checkWorkStopButton(WorkoutViewModel w) {
//        Log.d("IIIEIEIEI", "checkWorkStopButton: " + w.isWarmUpIng.get() +","+ w.isCoolDowning.get());
//        return w.selProgram == ProgramsEnum.GERKIN;
//    }

    public static boolean checkWorkStopButton(WorkoutViewModel w, boolean isWarmUpIng, boolean isCoolDowning) {

//        if (w.selProgram == HIIT) {
//            return true;
//        }

        if (w.selProgram == FITNESS_TEST) {
            return true;
        }

        if (w.selProgram.getProgramType() == WorkoutIntDef.FITNESS_TESTS) {

            if (isUs) {
                return true;
            }
            return false;
        }


        return !(isWarmUpIng || isCoolDowning);
    }

    public static float checkWorkStopButtonAlpha(WorkoutViewModel w, boolean isWarmUpIng, boolean isCoolDowning) {


        if (w.selProgram == FITNESS_TEST) {
            return 1f;
        }

        if (w.selProgram.getProgramType() == WorkoutIntDef.FITNESS_TESTS) {

            if (isUs) {
                return 1f;
            }
            return 0.4f;
        }

        if (isWarmUpIng || isCoolDowning) {

            return 0.4f;
        } else {
            return 1f;
        }
    }


    /**
     *是否隱藏
     */
    public static Drawable checkWorkStopButtonBackground(WorkoutViewModel w, boolean isWarmUpIng, boolean isCoolDowning) {

//        if (w.selProgram == HIIT) {
//            return ContextCompat.getDrawable(getApp(), R.drawable.panel_no_cooldown);
//        }

        if (w.selProgram == FITNESS_TEST) {
            return ContextCompat.getDrawable(getApp(), R.drawable.btn_workout_pause);
        }

        if (w.selProgram.getProgramType() == WorkoutIntDef.FITNESS_TESTS) {
            return ContextCompat.getDrawable(getApp(), R.drawable.panel_no_cooldown);
        }

        boolean b = !(isWarmUpIng || isCoolDowning);
        int d = b ? R.drawable.btn_workout_pause : R.drawable.panel_no_cooldown;
        return ContextCompat.getDrawable(getApp(), d);

    }

    //US
    public static ColorStateList checkWorkStopButtonBackgroundUs(WorkoutViewModel w, boolean isWarmUpIng, boolean isCoolDowning) {

        if (w.selProgram == FITNESS_TEST) {
            return ContextCompat.getColorStateList(getApp(), R.color.btn_e24b44_20_d);
        }

        if (w.selProgram.getProgramType() == WorkoutIntDef.FITNESS_TESTS) {
            if (isUs) {
                return ContextCompat.getColorStateList(getApp(), R.color.btn_e24b44_20_d);
            }
            return ContextCompat.getColorStateList(getApp(), R.color.color30252e37);
        }

        boolean b = !(isWarmUpIng || isCoolDowning);
        int d = b ? R.color.btn_e24b44_20_d : R.color.color30252e37;
        return ContextCompat.getColorStateList(getApp(), d);

    }


        public static ColorStateList resToColor(int res) {

        return ContextCompat.getColorStateList(getApp(), res);
    }


    public static int getConNum(Button button) {

        if (button == null) {
            return 0;
        }

        if ("".equals(button.getText().toString())){
            return 0;
        }

        return Integer.parseInt(button.getText().toString());
    }


    public static int checkLevelView(boolean disabledInclineUpdate) {
        int v = disabledInclineUpdate ? (!isUs ? View.GONE : View.VISIBLE) : View.GONE;
        return v;
    }

}
