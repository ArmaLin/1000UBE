package com.dyaco.spirit_commercial.support.intdef;

import static com.dyaco.spirit_commercial.App.UNIT_E;

public class OPT_SETTINGS {

    public static final int PORT_TV_TUNER = 1;
    public static final int PORT_EZ_CAST = 2;//EZ CAST
    public static final int PORT_WIRE_CAST = 3;
    public static final int PORT_MIRA_CAST = 4;//STB

    public static final int NO_HR_HR = 40; // 40以下當作沒心跳

    public static final int ORG_FRONT_INCLINE_MIN = 2442;
    public static final int ORG_FRONT_INCLINE_MAX = 27116;
    public static final int ORG_FRONT_INCLINE_DIFF = ORG_FRONT_INCLINE_MAX - ORG_FRONT_INCLINE_MIN;

    public static final String E0x50 = "0x50";
    public static final String E0x51 = "0x51";
    public static final String E0x52 = "0x52";
    public static final String E0x53 = "0x53";
    public static final String E0x54 = "0x54";
    public static final String E0xB0 = "0xB0";
    public static final String E0xB1 = "0xB1";
    public static final String E0xB2 = "0xB2";
    public static final String E0xB3 = "0xB3";
    public static final String E0xBA = "0xBA";
    public static final String E0xBB = "0xBB";
    public static final String E0xBC = "0xBC";
    public static final String E0xE0 = "0xE0";
    public static final String E0xE1 = "0xE1";
    public static final String E0xE2 = "0xE2";

    public static final String E0x32 = "0x32"; //RS485

    public static final int PAUSE_FINISH_IN_SEC = 60 * 5;

    public static final int BAR_COUNT_NORMAL = 20;
    public static final int BAR_COUNT_GERKIN = 44;

    public static final float BAR_WIDTH_NORMAL = 61.55f;
    public static final float BAR_WIDTH_GERKIN = 24f;

    //Chart Notify的位置，扣掉外面的框
    public static int viewX; //261     //US 312
    public static int viewY;  //300    //US 254    //數字越小 離bar越遠  越高       //DiagramBarsView layoutMarginTop 有關係
    //global 150(DiagramBarsView layoutMarginTop) + 80(TopDashboard) + 24(fragment top margin) = 254
    //US 20(DiagramBarsView layoutMarginTop) + 80(TopDashboard) + 200(fragment top margin) = 300
    //US 40(DiagramBarsView layoutMarginTop) + 80(TopDashboard) + 160(fragment top margin) = 280


    // biodata
    public static final int AGE_DEF = 35;
    public static final int AGE_MIN = 10;
    public static final int AGE_MAX = 99;

    // weight unit: pound (1lb = 0.454kg, lb/lbs)
    public static final int WEIGHT_IU_DEF = 155;
    public static final int WEIGHT_IU_MIN = 40;
    public static final int WEIGHT_IU_MAX = 400;

    private int WEIGHT_DEFAULT;

    public static int getDefaultWeight() {
        return UNIT_E == DeviceIntDef.IMPERIAL ? WEIGHT_IU_DEF : WEIGHT_MU_DEF;
    }

    public static int getMaxWeight() {
        return UNIT_E == DeviceIntDef.IMPERIAL ? WEIGHT_IU_MAX : WEIGHT_MU_MAX;
    }

    public static int getMinWeight() {
        return UNIT_E == DeviceIntDef.IMPERIAL ? WEIGHT_IU_MIN : WEIGHT_MU_MIN;
    }

    public static int getMaxHeight() {
        return UNIT_E == DeviceIntDef.IMPERIAL ? HEIGHT_IU_MAX : HEIGHT_MU_MAX;
    }

    public static int getMinHeight() {
        return UNIT_E == DeviceIntDef.IMPERIAL ? HEIGHT_IU_MIN : HEIGHT_MU_MIN;
    }

    // weight unit: kg (1lb = 0.454kg, lb/lbs)
    public static final int WEIGHT_MU_DEF = 70;
    public static final int WEIGHT_MU_MIN = 30;
    public static final int WEIGHT_MU_MAX = 180;

    // height unit: inch (1 inch = 2.54cm)
    public static final int HEIGHT_IU_DEF = 67; //in
    public static final int HEIGHT_IU_MIN = 40;
    public static final int HEIGHT_IU_MAX = 87;

    public static final int IMPERIAL_MAX_HEIGHT_FT = 7;
    public static final int IMPERIAL_MIN_HEIGHT_FT = 3;
    public static final int IMPERIAL_MAX_HEIGHT_IN = 11;
    public static final int IMPERIAL_MIN_HEIGHT_IN = 0;

    // height unit: cm (1 inch = 2.54cm)
    public static final int HEIGHT_MU_DEF = HEIGHT_IU_DEF * 254 / 100;  // 170 cm
    //public static final int HEIGHT_MU_MIN = HEIGHT_IU_MIN * 254 / 100 + ((HEIGHT_IU_MIN * 254 % 100) >= 50 ? 1 : 0);
    public static final int HEIGHT_MU_MIN = 100;
    //public static final int HEIGHT_MU_MAX = HEIGHT_IU_MAX * 254 / 100; //公分
    public static final int HEIGHT_MU_MAX = 220; //公分

    // unit: minute, for Timer, Manual, Hill, Plateau, Facility, HR, METs
    public static final int TARGET_TIME_DEF = 20;
    public static final int TARGET_TIME_MIN = 5;
    public static final int TARGET_TIME_MAX = 99;
    public static final int TARGET_MAX_SECONDS = TARGET_TIME_MAX * 60;

    // for Hill, Plateau, Facility (Bikes, Stepper, UBE)
    public static final int MAX_LEVEL_DEF = 5;

    // TODO: PF
    public static final int MAX_LEVEL_MIN = 1;
    public static int MAX_LEVEL_MAX = 50;


    public static final int MAX_RPM = 120;


    // [Bike, UBE, stepper] Max Workload - Power: for Hill, Plateau, Facility
    public static final int POWER_DFT = 50;    // watts
    public static final int POWER_MIN = 25;    // watts
    public static final int POWER_MAX = 300;   // watts
    public static final int POWER_INC = 1;     // watts  5->1 2021/12/14

    // for Heart Rate (count by percent of maximum heart rate)
    public static final int THR_PERCENTAGE_85 = 85;
    public static final int THR_PERCENTAGE_80 = 80;
    public static final int THR_PERCENTAGE_65 = 65;
    public static final int THR_CONSTANT = 220;
    public static final int MHR_DEF = THR_CONSTANT - AGE_DEF;

    public static final int THR_DEF = MHR_DEF * THR_PERCENTAGE_65 / 100;
    public static final int THR_MIN = 72;
    public static final int THR_MAX = 200;

    public static final int THR_MHR_DEF = THR_DEF * 100 / MHR_DEF;
    public static final int THR_MHR_MIN = THR_MIN * 100 / MHR_DEF;
    public static final int THR_MHR_MAX = THR_MAX * 100 / MHR_DEF;

    // for Speed Changing Time (unit: sec)

    // for Steps
    public static final int TARGET_STEPS_DEF = 1200;
    public static final int TARGET_STEPS_MIN = 300;
    public static final int TARGET_STEPS_MAX = 10000;
    public static final int MT_TARGET_STEPS_INC = 50;


    public static final int MT_TARGET_METS_DFT = 35;     // 3.5
    public static final int MT_TARGET_METS_MIN = 14;     // 1.4
    public static final int MT_TARGET_METS_MAX = 135;    // 13.5
    public static final int MT_TARGET_METS_INC = 1;      // 0.1


    // for Distance
    public static final int TARGET_DISTANCE_IU_DEF = 15;        // 1.5 Mile
    public static final int TARGET_DISTANCE_IU_MIN = 10;        // 1 Mile
    public static final int TARGET_DISTANCE_IU_MAX = 1000;      // 100 Mile
    public static final int TARGET_DISTANCE_MU_DEF = TARGET_DISTANCE_IU_DEF * 16 / 10;      // 2.4 KM
    public static final int TARGET_DISTANCE_MU_MIN = TARGET_DISTANCE_IU_MIN * 16 / 10;      // 1.6 KM
    public static final int TARGET_DISTANCE_MU_MAX = TARGET_DISTANCE_IU_MAX * 16 / 10;      // 160 KM

    // for Fitness Tests (Navy, Air Force, PEB, Coast Guard)
    public static final float TIMED_RUN15_IU_DEF = 1.5f;     // 1.5 Mile
    public static final float TIMED_RUN15_MU_DEF = TIMED_RUN15_IU_DEF * 16 / 10;    // 2.4 KM

    // for Fitness Tests (Army)
    public static final float TIMED_RUN20_IU_DEF = 2;     // 2.0 Mile
    public static final float TIMED_RUN20_MU_DEF = TIMED_RUN20_IU_DEF * 16 / 10;    // 3.2 KM

    // for Fitness Tests (Marines)
    public static final float TIMED_RUN30_IU_DEF = 3;     // 3.0 Mile
    public static final float TIMED_RUN30_MU_DEF = TIMED_RUN30_IU_DEF * 16 / 10;    // 4.8 KM

    // for Calories
    public static final int TARGET_CALORIES_DEF = 100;
    public static final int TARGET_CALORIES_MIN = 50;
    public static final int TARGET_CALORIES_MAX = 1000;

    // for METs
    public static final int TARGET_METS_DEF = 35;       // 3.5
    public static final int TARGET_METS_MIN = 14;       // 1.4
    public static final int TARGET_METS_MAX = 134;      // 13.4
    public static final int TARGET_METS_INC = 1;      // 13.4

    // for Elevation Gain (Metric, Imperial Units)
    public static final int TARGET_VERTICAL_MU_DEF = 50;  // Meter(m)
    public static final int TARGET_VERTICAL_MU_MIN = 10;
    public static final int TARGET_VERTICAL_MU_MAX = 1000;
    public static final int TARGET_VERTICAL_IU_DEF = TARGET_VERTICAL_MU_DEF * 328 / 100; // Foot(ft)
    public static final int TARGET_VERTICAL_IU_MIN = TARGET_VERTICAL_MU_MIN * 328 / 100;
    public static final int TARGET_VERTICAL_IU_MAX = TARGET_VERTICAL_MU_MAX * 328 / 100;

    /**
     * <HIIT>
     */
    // Unit: second, for HIIT (30/45/60, interval 15)
    public static final int SPRINT_TIME_DEF = 30;
    public static final int SPRINT_TIME_MIN = 30;
    public static final int SPRINT_TIME_MAX = 60;
    public static final int SPRINT_TIME_INC = 15;

    // for HIIT
    public static final int SPRINT_LEVEL_DEF = 10;
    public static final int SPRINT_LEVEL_MIN = 10;
    public static final int SPRINT_LEVEL_MAX = 40;

    // for HIIT
    public static final int SPRINT_SPEED_IU_DEF = 60;       // MPH 6.0
    public static final int SPRINT_SPEED_IU_MIN = 30;       // 3.0
    public static final int SPRINT_SPEED_IU_MAX = 125;      // 12.5
    public static final int SPRINT_SPEED_MU_DEF = SPRINT_SPEED_IU_DEF * 16 / 10;      // KPH 9.6

    public static final int SPRINT_SPEED_MU_MIN = SPRINT_SPEED_IU_MIN * 16 / 10;      // 4.8
    public static final int SPRINT_SPEED_MU_MAX = SPRINT_SPEED_IU_MAX * 16 / 10;      // 20.0


    // Unit: second, for HIIT (30/45/60/75/90, interval 15)
    public static final int REST_TIME_DEF = 30;
    public static final int REST_TIME_MIN = 30;
    public static final int REST_TIME_MAX = 90;
    public static final int REST_TIME_INC = 15;

    // for HIIT
    public static final int REST_LEVEL_DEF = 5;
    public static final int REST_LEVEL_MIN = 1;
    public static final int REST_LEVEL_MAX = 40;

    // for HIIT
    public static final int REST_SPEED_IU_DEF = 30;     // MPH 3.0
    public static final int REST_SPEED_IU_MIN = 10;     // 1.0
    public static final int REST_SPEED_IU_MAX = 100;    // 10.0
    public static final int REST_SPEED_MU_DEF = REST_SPEED_IU_DEF * 16 / 10; // KPH 4.8
    public static final int REST_SPEED_MU_MIN = REST_SPEED_IU_MIN * 16 / 10; // 1.6
    public static final int REST_SPEED_MU_MAX = REST_SPEED_IU_MAX * 16 / 10; // 16.0

    // for HIIT
    public static final int INTERVALS_DEF = 10;
    public static final int INTERVALS_MIN = 3;
    public static final int INTERVALS_MAX = 15;
    public static final int ESTIMATED_TIME_DEF = SPRINT_TIME_DEF * INTERVALS_DEF + REST_TIME_DEF * (INTERVALS_DEF - 1);
    /**
     * </HIIT>
     */


    public static final int T1_MIN = 5;     // 5 minutes
    public static final int T1_MAX = 99;    // 99 minutes

    /* T2
       由profile計算Target Time
       Interval numbers, sprint speed & time, reset speed & time
     */

    /* MaxSped
       <Treadmill>
       Patterns-Hill, Patterns-Plateau, Patterns-Facility

     */
//    public static final int MAX_SPD_IU_MIN = 30;                                // 3.0 mi
//    public static final int MAX_SPD_IU_MAX = 125;                               // 12.5 mi
//    public static final int MAX_SPD_IU_DEF = 30;                                // 3.0 mi
//    public static final int MAX_SPD_MU_MIN = 50;                                // 5.0 km
//    public static final int MAX_SPD_MU_MAX = MAX_SPD_IU_MAX * 16 / 10;          // 20.0 km
//    public static final int MAX_SPD_MU_DEF = 50;                                // 5.0 km
//    public static final int MAX_SPD_INC    = 1;                                 // 0.1 mi/km

    // for Speed Changing Time (unit: sec)
    public static final int SPEED_CHANGE_TIME_DFT = 30;  // 3.0
    public static final int SPEED_CHANGE_TIME_MIN = 10;  // 1.0
    public static final int SPEED_CHANGE_TIME_MAX = 100; // 10.0
    public static final int SPEED_CHANGE_TIME_INC = 10;  // 1.0

    // 20.0KPH(Range 16.0~20.0 KPH) / 12MPH(Range 10.0~12.0 MPH)Step 0.1

    //改工程模式預設速度  > DeviceSettingBean >minSpeedMu
    public static final int MAX_SPD_IU_MIN = 30;                                // 3.0 mi
    public static int MAX_SPD_IU_MAX = 150;                               // 15.0 mi
  //  public static int MAX_SPD_IU_MAX = 155;                               // 15.0 mi
    public static final int MAX_SPD_IU_DEF = 30;                                // 3.0 mi

    public static final int MAX_SPD_MU_MIN = 50;                                // 5.0 km
    public static int MAX_SPD_MU_MAX = 240;                               //
 //   public static int MAX_SPD_MU_MAX = 250;                               //
    public static final int MAX_SPD_MU_DEF = 50;                                // 5.0 km
    public static final int MAX_SPD_INC = 1;


    public static final int MAX_SPD_MU_MAX_DEF = 240;
    public static final int MAX_SPD_IU_MAX_DEF = 150;

    //可調整的最小速度
    /**
     * 最低速範圍：
     * 公制: 0.5 km/h (預設)- 0.8 km/h
     * 英制: 0.3 MPH (預設)- 0.5 MPH
     */
    public static int MIN_SPD_MU = 5;                                // 0.8km > 0.5km
    public static int MIN_SPD_IU = 3;                                 // 0.5mi > 0.3mi


    /**
     * 最低速範圍：
     * 公制: 0.5 km/h (預設)- 0.8 km/h
     * 英制: 0.3 MPH (預設)- 0.5 MPH
     */
    public final static int MIN_SPD_IU_MAX = 5; //英制 : 最低速 最大 0.5 mph
    public final static int MIN_SPD_IU_MIN = 3; //英制 : 最低速 最小 0.3 mph
    public final static int MIN_SPD_MU_MAX = 8; //公制 : 最低速 最大 0.8 km/h
    public final static int MIN_SPD_MU_MIN = 5; //公制 : 最低速 最小 0.3 km/h

    public static final int MAX_INC_MIN = 1;       // 5.0 %
    public static final int MAX_INC_MAX = 30;      // 30階 = 15% Range 10.0~15.0
    public static final int MAX_INC_DEF = 10;
    public static final int MAX_INC_INC = 5;        // 0.5 %

    /* L2
        profile計算level
     */

    /* L3
        warm up設定的level
    */

    /* I1 (起始揚升)
       <Treadmill>
       Timer,
       Targets-Distance, Targets-Calories, Targets-Steps, Targets-Elevation, Targets-Heart Rate
       Patterns-Manual, Patterns-HIIT
       Fitness Tests
     */
    public static final int I1_INIT = 0;                         // 0.0%

    /* I2 (起始揚升)
        profile計算incline
        <Treadmill>
        Patterns-Hill, Patterns-Plateau, Patterns-Facility
     */

    /* S0 (起始速度)
       <Treadmill>
       Patterns-Manual
     */
    public static final int S0_IU_INIT = 0;                         // 0 mi
    public static final int S0_MU_INIT = S0_IU_INIT * 16 / 10;      // 0 km

    /* S1 (起始速度)
       <Treadmill>
       Timer
       Target-Distance, Target-Calories, Targets-Steps, Targets-Elevation
       Fitness Tests (查表)
     */
    public static final int S1_IU_INIT = 5;                         // 0.5 mi
    public static final int S1_MU_INIT = S1_IU_INIT * 16 / 10;      // 0.8 km

    /* S2 (起始速度)
       profile計算speed
       <Treadmill>
       Patterns-Hill, Patterns-Plateau, Patterns-Facility
     */

    /* S3 (起始速度)
       warm up設定的speed
       <Treadmill>
       Patterns-HIIT
       Fitness Tests
     */

    /* SpdR1 (速度範圍)
       〈Treadmill>
       for Patterns-Manual(init speed = 0), Patterns-Facility
     */
    public static final int SPD_R1_IU_MIN = -50;                          // -5 mi
    public static final int SPD_R1_IU_MAX = 125;                          // 12.5mi
    public static final int SPD_R1_MU_MIN = SPD_R1_IU_MIN * 16 / 10;      // -8.0 km
    public static final int SPD_R1_MU_MAX = SPD_R1_IU_MAX * 16 / 10;      // 20.0 km
    public static final int SPD_R1_INC = 1;                            // 0.1 mi/km

    /* SpdR2 (速度範圍)
       <Treadmill>
       for Timer,
           Targets-Distance, Targets-Calories, Targets-Steps, Targets-Elevation, Targets-HR, Targets-METs
           Patterns-Hill, Patterns-Plateau, Patterns-HIIT
           FitnessTests
     */
    public static final int SPD_R2_IU_MIN = 5;                            // 0.5 mi
    public static final int SPD_R2_IU_MAX = 125;                          // 12.5mi
    public static final int SPD_R2_MU_MIN = SPD_R2_IU_MIN * 16 / 10;      // 0.8 km
    public static final int SPD_R2_MU_MAX = SPD_R2_IU_MAX * 16 / 10;      // 20.0 km
    public static final int SPD_R2_INC = 1;                            // 0.1 mi/km

    /* IncR1 (揚升範圍)
       <Treadmill>
       for Timer,
           Targets-Distance, Targets-Calories, Targets-Steps, Targets-Elevation, Target-METs
           Patterns-Manual, Patterns-Hill, Patterns-Plateau, Patterns-Facility, Patterns-HIIT

     */
    public static final int INC_R1_MIN = -100;     // -10.0 %
    public static final int INC_R1_MAX = 250;      // 25.0 %

    /* IncR2 (揚升範圍)
       <Treadmill>
       for Targets-HR
           FitnessTests
     */
    public static final int INC_R2_MIN = 0;        // 0.0 %
    public static final int INC_R2_MAX = 250;      // 25.0 %

    public static final int GRADE_DEF = 10;
    public static final int GRADE_MIN = 0;
    public static final int GRADE_MAX = 100;

    public static final int HANDRAIL_HEIGHT_DEF = 18;
    public static final int HANDRAIL_HEIGHT_MIN = 0;
    public static final int HANDRAIL_HEIGHT_MAX = 30;

    public static final int DECK_LIFT_DEF = 18;
    public static final int DECK_LIFT_MIN = 0;
    public static final int DECK_LIFT_MAX = 30;

    public static final int CRANK_DEF = 18;  // 尚未定義, 待確認
    public static final int CRANK_MIN = 0;   // 尚未定義, 待確認
    public static final int CRANK_MAX = 30;  // 尚未定義, 待確認

    public static final int BACKREST_POS_DEF = 18;  // 尚未定義, 待確認
    public static final int BACKREST_POS_MIN = 0;   // 尚未定義, 待確認
    public static final int BACKREST_POS_MAX = 30;  // 尚未定義, 待確認

    public static final int SEAT_POS_DEF = 18;  // 尚未定義, 待確認
    public static final int SEAT_POS_MIN = 0;   // 尚未定義, 待確認
    public static final int SEAT_POS_MAX = 30;  // 尚未定義, 待確認

    public static final double SPEED_I2M = 1.609;
    public static final double PACE_I2M = 0.6214;
    public static final double ALTITUDE_I2M = 0.3048;

    /* Workload-speed
       <Bike> Stepper
     */
    public static final int WL_CPS_MU_MIN = 25;   // cm per second
    public static final int WL_CPS_MU_MAX = 100;  // cm per second
    public static final int WL_CPS_MU_DEF = 30;   // cm per second
    public static final int WL_CPS_INC = 5;

    public static final int WL_CPS_IU_MIN = WL_CPS_MU_MIN * 393 / 1000;   // inch per second
    public static final int WL_CPS_IU_MAX = WL_CPS_MU_MAX * 393 / 1000;   // inch per second
    public static final int WL_CPS_IU_DEF = WL_CPS_MU_DEF * 393 / 1000;   // inch per second


    /* Workload-speed
       <Bike> Non-Stepper
     */
    public static final int WL_RPM_MIN = 25;
    public static final int WL_RPM_MAX = 100;
    public static final int WL_RPM_DEF = 30;
    public static final int WL_RPM_INC = 5;
    public static final int WL_DPS_MIN = WL_RPM_MIN * 360 / 60;  // degree per second
    public static final int WL_DPS_MAX = WL_RPM_MAX * 360 / 60;  // degree per second
    public static final int WL_DPS_DEF = WL_RPM_DEF * 360 / 60;  // degree per second
    public static final int WL_DPS_INC = 30;

    //Workload-power<Bike>
    public static final int WL_PWR_MIN = 25;
    public static final int WL_PWR_MAX = 200;
    public static final int WL_PWR_DEF = 50;
    public static final int WL_PWR_INC = 5;

    // [Bike, UBE, stepper] Max Workload - Power: for Hill, Plateau, Facility
    public static final int MW_POWER_DFT = 50;    // watts
    public static final int MW_POWER_MIN = 20;    // watts
    public static final int MW_POWER_MAX = 500;   // watts
    public static final int MW_POWER_INC = 1;     // watts  5->1 2021/12/14


    /* Workload-level
       <Bike>
     */
    public static final int WL_LEV_MIN = 5;
    public static final int WL_LEV_MAX = 50;
    public static final int WL_LEV_DEF = 10;
    public static final int WL_LEV_INC = 1;


}

