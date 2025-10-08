package com.dyaco.spirit_commercial.support.intdef;

import static com.dyaco.spirit_commercial.App.UNIT_E;

import com.dyaco.spirit_commercial.R;

public enum UnitEnum {

    DISTANCE(0, R.string.km, R.string.mi, 0),
    DISTANCE2(0, R.string.km, R.string.mile, 0),
    SPEED(1, R.string.km_h, R.string.mph, 0),
    HEIGHT(2, R.string.cm, R.string.ft, R.string.km),
    WEIGHT(3, R.string.kg, R.string.lb, 0);

    UnitEnum(int code, int metric, int imperial1, int imperial2) {
        this.code = code;
        this.metric = metric;
        this.imperial1 = imperial1;
        this.imperial2 = imperial2;
    }

    private int code;
    private int metric;
    private int imperial1;
    private int imperial2;

    public int getMetric() {
        return metric;
    }

    public void setMetric(int metric) {
        this.metric = metric;
    }

    public int getImperial1() {
        return imperial1;
    }

    public void setImperial1(int imperial1) {
        this.imperial1 = imperial1;
    }

    public int getImperial2() {
        return imperial2;
    }

    public void setImperial2(int imperial2) {
        this.imperial2 = imperial2;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public static int getUnit(UnitEnum unitEnum) {
        int unit = 0;
        switch (unitEnum) {
            case SPEED:
                unit = UNIT_E == DeviceIntDef.METRIC ? SPEED.getMetric() : SPEED.getImperial1();
                break;
            case HEIGHT:
                unit = UNIT_E == DeviceIntDef.METRIC ? HEIGHT.getMetric() : HEIGHT.getImperial1();
                break;
            case WEIGHT:
                unit = UNIT_E == DeviceIntDef.METRIC ? WEIGHT.getMetric() : WEIGHT.getImperial1();
                break;
            case DISTANCE:
                unit = UNIT_E == DeviceIntDef.METRIC ? DISTANCE.getMetric() : DISTANCE.getImperial1();
                break;
            case DISTANCE2:
                unit = UNIT_E == DeviceIntDef.METRIC ? DISTANCE2.getMetric() : DISTANCE2.getImperial1();
                break;
        }

        return unit;
    }
}
