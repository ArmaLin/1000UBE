package com.dyaco.spirit_commercial.support.utils;

import java.util.HashMap;
import java.util.Map;

public class CheckDoubleClick {
    private static final Map<String, Long> records = new HashMap<>();

    public static boolean isFastClick() {
        if (records.size() > 1000) {
            records.clear();
        }

        StackTraceElement ste = new Throwable().getStackTrace()[1];
        String key = ste.getFileName() + ste.getLineNumber();

        Long lastClickTime = records.get(key);
        long thisClickTime = System.currentTimeMillis();
        records.put(key, thisClickTime);
        if (lastClickTime == null) {
            lastClickTime = 0L;
        }
        long timeDuration = thisClickTime - lastClickTime;
        return 0 < timeDuration && timeDuration < 500;
    }


    private static final Map<String, Long> records2 = new HashMap<>();
    public static boolean isFastClick2() {
        boolean isFast;
        if (records2.size() > 300) {
            records2.clear();
        }

        StackTraceElement ste = new Throwable().getStackTrace()[1];
        String key = ste.getFileName() + ste.getLineNumber();

        Long lastClickTime = records2.get(key);
        long thisClickTime = System.currentTimeMillis();
        records2.put(key, thisClickTime);
        if (lastClickTime == null) {
            lastClickTime = 0L;
        }
        long timeDuration = thisClickTime - lastClickTime;

        isFast = 0 < timeDuration && timeDuration < 300;

        if (isFast) records2.clear();

        return isFast;
    }


//    private static final Map<String, Long> records3 = new HashMap<>();
//    public static boolean isFastClick3() {
//        boolean isFast;
//        if (records3.size() > 300) {
//            records3.clear();
//        }
//
//        StackTraceElement ste = new Throwable().getStackTrace()[1];
//        String key = ste.getFileName() + ste.getLineNumber();
//
//        Long lastClickTime = records3.get(key);
//        long thisClickTime = System.currentTimeMillis();
//        records3.put(key, thisClickTime);
//        if (lastClickTime == null) {
//            lastClickTime = 0L;
//        }
//        long timeDuration = thisClickTime - lastClickTime;
//
//        isFast = 0 < timeDuration && timeDuration < 300;
//
//        if (isFast) records3.clear();
//
//        return isFast;
//    }
}