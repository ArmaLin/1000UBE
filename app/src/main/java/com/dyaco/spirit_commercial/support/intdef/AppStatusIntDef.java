package com.dyaco.spirit_commercial.support.intdef;

import androidx.annotation.IntDef;

public class AppStatusIntDef {

    public static final int STATUS_LOGIN_PAGE = 0;
    public static final int STATUS_IDLE = 1;
    public static final int STATUS_RUNNING = 2;
    public static final int STATUS_PAUSE  = 3;
    public static final int STATUS_SUMMARY  = 4;
    public static final int STATUS_MAINTENANCE  = 5;

    public static final int CURRENT_PAGE_TRAINING  = 0;
    public static final int CURRENT_PAGE_MEDIA  = 1;

    @IntDef({STATUS_LOGIN_PAGE, STATUS_IDLE, STATUS_RUNNING, STATUS_PAUSE, STATUS_SUMMARY,STATUS_MAINTENANCE})
    public @interface CurrentStatus {
    }

}