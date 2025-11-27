package com.dyaco.spirit_commercial.workout.programs;


import static com.dyaco.spirit_commercial.support.intdef.WorkoutIntDef.DEFAULT_PROGRAM;
import static com.dyaco.spirit_commercial.support.intdef.WorkoutIntDef.EGYM_PROGRAM;
import static com.dyaco.spirit_commercial.support.intdef.WorkoutIntDef.FITNESS_TESTS;
import static com.dyaco.spirit_commercial.support.intdef.WorkoutIntDef.PROFILE_PROGRAM;

import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.support.intdef.WorkoutIntDef;

public enum ProgramsEnum {

    MANUAL(0, R.string.Timer, DEFAULT_PROGRAM, R.string.manual_desc,R.string.manual_desc_bike, 0, 0,0, "1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1", "0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0","10#10#10#10#10#10#10#10#10#10#10#10#10#10#10#10#10#10#10#10", "0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0",1),
    CALORIES(25, R.string.Calories, DEFAULT_PROGRAM, R.string.calories_desc,R.string.calories_desc, 0, 0,0, "1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1", "0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0","10#10#10#10#10#10#10#10#10#10#10#10#10#10#10#10#10#10#10#10", "0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0",1),
    STEPS(26, R.string.Steps, DEFAULT_PROGRAM, R.string.Steps_desc,R.string.Steps_desc, 0, 0,R.drawable.btn_banner_manual, "1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1", "0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0","10#10#10#10#10#10#10#10#10#10#10#10#10#10#10#10#10#10#10#10", "0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0",1),
    METS(28, R.string.METs, DEFAULT_PROGRAM, R.string.Mets_desc,R.string.Mets_desc, 0, 0,R.drawable.btn_banner_manual, "1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1", "0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0","10#10#10#10#10#10#10#10#10#10#10#10#10#10#10#10#10#10#10#10", "0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0",1),
    WINGATE_TEST(27, R.string.Wingate_Test, DEFAULT_PROGRAM, R.string.Wingate_Test_desc,R.string.Wingate_Test_desc, 0, 0,R.drawable.btn_banner_manual, "1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1", "0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0","10#10#10#10#10#10#10#10#10#10#10#10#10#10#10#10#10#10#10#10", "0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0",1),
    HILL(1, R.string.hill, PROFILE_PROGRAM, R.string.hill_desc,R.string.hill_desc_bike,  R.drawable.diagram_profile_hill, R.drawable.diagram_profile_hill, 0,"1#2#2#3#3#4#4#5#5#7#7#5#5#4#4#3#3#3#2#1", "2#2#6#6#6#8#8#8#8#10#10#8#8#8#8#6#6#6#2#2","20#30#40#50#60#60#70#70#70#80#80#70#80#80#100#100#70#80#80#70#70#60#50#40#30#20", "0#0#0#0#2#4#6#6#8#6#8#8#10#6#8#6#8#10#8#6#2#2#0#0#0#0",2),
    PLATEAU(24, R.string.Plateau, PROFILE_PROGRAM, R.string.plateau_desc,R.string.plateau_desc,  R.drawable.diagram_profile_plateau, R.drawable.diagram_profile_plateau, 0,"5#6#7#8#10#10#10#10#10#10#10#10#10#10#10#10#8#7#6#5", "2#2#6#6#6#8#8#8#8#10#10#8#8#8#8#6#6#6#2#2","20#30#40#50#60#60#70#70#70#80#80#70#80#80#100#100#70#80#80#70#70#60#50#40#30#20", "0#0#0#0#2#4#6#6#8#6#8#8#10#6#8#6#8#10#8#6#2#2#0#0#0#0",2),
    FATBURN(2, R.string.fatburn, PROFILE_PROGRAM, R.string.fatburn_desc,R.string.fatburn_desc_bike,  R.drawable.diagram_profile_fatburn, R.drawable.diagram_profile_bike_fatburn, 0,"1#2#3#5#5#5#5#5#5#5#5#5#5#5#5#5#5#3#2#1", "3#3#10#10#10#10#10#10#10#10#10#10#10#10#10#10#10#10#3#3","20#30#40#50#80#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#80#50#40#30#20", "0#0#0#0#2#4#6#6#8#10#6#8#8#6#4#6#8#10#12#8#4#2#0#0#0#0",3),
    CARDIO(3, R.string.cardio, PROFILE_PROGRAM, R.string.cardio_desc,R.string.cardio_desc_bike, R.drawable.diagram_profile_cardio, R.drawable.diagram_profile_bike_cardio, 0,"1#2#3#5#6#7#6#6#6#7#6#5#6#7#6#5#6#5#2#1", "2#2#8#8#10#4#4#4#10#4#4#4#10#2#2#2#8#2#2#2","20#30#40#50#60#60#70#70#70#80#70#50#80#80#60#70#80#80#70#80#60#60#50#40#30#20", "0#0#0#0#2#2#4#4#6#4#4#6#2#4#6#4#4#8#4#6#2#2#0#0#0#0",4),
    STRENGTH(4, R.string.strength, PROFILE_PROGRAM, R.string.strength_desc,R.string.strength_desc_bike,  R.drawable.diagram_profile_strength, R.drawable.diagram_profile_bike_strength, 0,"1#2#2#3#3#4#4#5#5#6#7#7#8#8#8#8#8#6#4#1", "2#2#8#8#8#8#10#10#10#10#10#10#10#10#6#6#6#6#2#2","20#30#40#50#60#80#80#80#80#80#80#80#80#80#80#80#80#80#80#80#80#80#80#40#30#20", "0#0#0#2#6#10#14#18#22#26#26#26#26#26#26#26#26#26#26#26#20#10#0#0#0#0",5),
    HIIT(5, R.string.hiit, DEFAULT_PROGRAM, R.string.hiit_desc,R.string.hiit_desc_bike,  R.drawable.diagram_profile_hiit, R.drawable.diagram_profile_bike_hiit, 0,"0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0", "0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0","0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0", "0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0",6),
    RUN_5K(6, R.string.k5_run, DEFAULT_PROGRAM, R.string.k5_run_desc,R.string.k5_run_desc, 0, 0, R.drawable.btn_banner_5k,"0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0", "0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0","1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1", "0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0",7),
    //RUN_10K(7, R.string.k10_run, DEFAULT_PROGRAM, R.string.k10_run_desc,R.string.k10_run_desc, 0, 0, R.drawable.img_10k_default,"16#12#3#4#6#7#5#7#1#1#1#6#1#1#1#1#1#1#1#1", "16#12#3#2#2#2#2#3#3#3#3#2#2#2#2#2#2#2#2#1"),
    RUN_10K(7, R.string.k10_run, DEFAULT_PROGRAM, R.string.k10_run_desc,R.string.k10_run_desc, 0, 0, R.drawable.btn_banner_10k,"0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0", "0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0","1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1", "0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0",8),
    HEART_RATE(8, R.string.heart_rate, DEFAULT_PROGRAM, R.string.heart_rate_desc,R.string.heart_rate_desc_bike, 0, 0, R.drawable.btn_banner_hr,"1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1", "0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0","1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1", "0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0",9),
    AIR_FORCE(9, R.string.air_force, FITNESS_TESTS, R.string.air_force_desc,R.string.air_force_desc, 0, 0, 0,"0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0", "0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0","1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1", "0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0",101),
    ARMY(10, R.string.army, FITNESS_TESTS, R.string.army_desc,R.string.army_desc, 0, 0, 0,"0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0", "0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0","1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1", "0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0",102),
    COAST_GUARD(11, R.string.coast_guard, FITNESS_TESTS, R.string.coast_guard_desc,R.string.coast_guard_desc, 0, 0, 0,"5#5#5#5#5#5#5#5#5#5#5#5#5#5#5#5#5#5#5#5", "0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0","1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1", "0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0",103),
    GERKIN(12, R.string.gerkin, FITNESS_TESTS, R.string.gerkin_desc,R.string.gerkin_desc, 0, 0, 0,"0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0", "0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0","1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1", "0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0",104),
    PEB(13, R.string.peb, FITNESS_TESTS, R.string.peb_desc,R.string.peb_desc, 0, 0, 0,"0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0", "0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0","1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1", "0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0",105),
    MARINE_CORPS(14, R.string.marine_corps, FITNESS_TESTS, R.string.marine_corps_desc,R.string.marine_corps_desc, 0, 0, 0,"0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0", "0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0","1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1", "0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0",106),
    NAVY(15, R.string.navy, FITNESS_TESTS, R.string.navy_desc,R.string.navy_desc, 0, 0, 0,"0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0", "0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0","1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1", "0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0",107),
    WFI(16, R.string.wfi, FITNESS_TESTS, R.string.wfi_desc,R.string.wfi_desc, 0, 0, 0,"1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1", "0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0","1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1", "0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0",108),
    CTT_PREDICTION(17, R.string.ctt_prediction, FITNESS_TESTS, R.string.ctt_prediction_desc,0, 0, 0, 0,"1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1", "0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0","1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1", "0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0",109),
    CTT_PERFORMANCE(18, R.string.ctt_performance, FITNESS_TESTS, R.string.ctt_performance_desc,0, 0, 0, 0,"1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1", "0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0","1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1", "0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0",110),
    WATTS(19, R.string.Watts, DEFAULT_PROGRAM, R.string.watt_desc, R.string.watt_desc, 0, 0, 0,"1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1", "0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0","1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1", "0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0",-1),
    FITNESS_TEST(20, R.string.fitness_test, FITNESS_TESTS, R.string.fitness_test_desc,R.string.fitness_test_desc, 0, 0, 0,"1#1#1#1", "0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0","1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1", "0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0",-1),
    INTERVAL(21, R.string.Interval, PROFILE_PROGRAM, R.string.interval_desc, R.string.interval_bike_desc, R.drawable.diagram_profile_interval, R.drawable.diagram_profile_bike_interval, 0,"1#2#2#7#7#2#2#7#7#2#2#7#7#2#2#7#7#2#2#1", "0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0","20#30#40#50#60#60#100#100#60#60#100#100#60#60#100#100#60#60#100#100#60#60#50#40#30#20", "0#0#0#0#2#4#6#10#4#6#12#4#6#14#4#6#16#4#6#10#6#2#0#0#0#0",-1),
    CUSTOM(22, R.string.Custom, PROFILE_PROGRAM, R.string.custom_desc, R.string.custom_bike_desc, 0, 0, R.drawable.btn_banner_custom_treadmill,"0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0", "0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0","0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0", "0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0",-1),
    EGYM(23, R.string.Custom, EGYM_PROGRAM, R.string.custom_desc, R.string.custom_bike_desc, 0, 0, R.drawable.btn_banner_custom_treadmill,"1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1", "0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0","10#10#10#10#10#10#10#10#10#10#10#10#10#10#10#10#10#10#10#10", "0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0",1);


    ProgramsEnum(int code, int programName, @WorkoutIntDef.ProgramType int programType, int programDescRes, int programDescResBike, int diagramTreadmill, int diagramBike, int photo, String bikeLevelNum, String bikeInclineNum, String treadmillSpeedNum, String treadmillInclineNum, int webApiProgramId) {
        this.code = code;
        this.programName = programName;
        this.programDescRes = programDescRes;
        this.programDescResBike = programDescResBike;
        this.programType = programType;
        this.diagramTreadmill = diagramTreadmill;
        this.diagramBike = diagramBike;
        this.bikeLevelNum = bikeLevelNum;
        this.bikeInclineNum = bikeInclineNum;
        this.photo = photo;
        this.treadmillSpeedNum = treadmillSpeedNum;
        this.treadmillInclineNum = treadmillInclineNum;
        this.webApiProgramId = webApiProgramId;
    }

    private final int photo;
    private int code;
    private int programName;
    private int programType;
    private final int diagramTreadmill;
    private final int diagramBike;
    private final int programDescRes;
    private int programDescResBike;
    private final int webApiProgramId;

    public int getPhoto() {
        return photo;
    }

    public int getProgramDescResBike() {
        return programDescResBike;
    }


    public void setProgramDescResBike(int programDescResBike) {
        this.programDescResBike = programDescResBike;
    }

    public int getProgramDesc() {
        return programDescRes;
    }


    private String bikeLevelNum;
    private final String bikeInclineNum;

    private String treadmillSpeedNum;
    private String treadmillInclineNum;

    public String getTreadmillSpeedNum() {
        return treadmillSpeedNum;
    }

    public void setTreadmillSpeedNum(String treadmillSpeedNum) {
        this.treadmillSpeedNum = treadmillSpeedNum;
    }

    public void setTreadmillInclineNum(String treadmillInclineNum) {
        this.treadmillInclineNum = treadmillInclineNum;
    }

    public String getTreadmillInclineNum() {
        return treadmillInclineNum;
    }

    public String getBikeLevelNum() {
        return bikeLevelNum;
    }

    public void setBikeLevelNum(String bikeLevelNum) {
        this.bikeLevelNum = bikeLevelNum;
    }


    public String getBikeInclineNum() {
        return bikeInclineNum;
    }


    public int getDiagramTreadmill() {
        return diagramTreadmill;
    }


    public int getDiagramBike() {
        return diagramBike;
    }



    public int getProgramDescRes() {
        return programDescRes;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getProgramName() {
        return programName;
    }

    public void setProgramName(int programName) {
        this.programName = programName;
    }

    public @WorkoutIntDef.ProgramType int getProgramType() {
        return programType;
    }

    public void setProgramType(@WorkoutIntDef.ProgramType int programType) {
        this.programType = programType;
    }

    public int getWebApiProgramId() {
        return webApiProgramId;
    }

    public static ProgramsEnum getProgram(int i) {
        for (ProgramsEnum programsEnum : values()) {
            if (programsEnum.getCode() == i) {
                return programsEnum;
            }
        }
        return ProgramsEnum.getProgram(9);
    }
}