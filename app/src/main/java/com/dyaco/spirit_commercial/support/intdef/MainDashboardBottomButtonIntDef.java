package com.dyaco.spirit_commercial.support.intdef;

import androidx.annotation.IntDef;

public class MainDashboardBottomButtonIntDef {

    public static final int DISAPPEAR = 0;
    public static final int START_THIS_PROGRAM = 1;
    public static final int START_THIS_TEST = 2;
    public static final int JUST_NEXT = 3;
    public static final int PREVIOUS_AND_NEXT = 4;
    public static final int PREVIOUS_AND_START = 5;
    public static final int WORK_OUT_STOP = 6;
    public static final int CHOOSE = 7;

    @IntDef({DISAPPEAR, START_THIS_PROGRAM})
    public @interface ButtonType {
    }

}