package com.dyaco.spirit_commercial.garmin;

import com.garmin.device.realtime.RealTimeDataType;
import com.garmin.device.realtime.RealTimeResult;

public class GarminDataBean {

    public GarminDataBean(String str, RealTimeDataType realTimeDataType, RealTimeResult realTimeResult) {
        this.str = str;
        this.realTimeDataType = realTimeDataType;
        this.realTimeResult = realTimeResult;
    }

    private String str;
    private RealTimeDataType realTimeDataType;
    private RealTimeResult realTimeResult;

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    public RealTimeDataType getRealTimeDataType() {
        return realTimeDataType;
    }

    public void setRealTimeDataType(RealTimeDataType realTimeDataType) {
        this.realTimeDataType = realTimeDataType;
    }

    public RealTimeResult getRealTimeResult() {
        return realTimeResult;
    }

    public void setRealTimeResult(RealTimeResult realTimeResult) {
        this.realTimeResult = realTimeResult;
    }
}
