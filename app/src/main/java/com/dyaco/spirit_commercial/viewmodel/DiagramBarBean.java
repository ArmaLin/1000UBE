package com.dyaco.spirit_commercial.viewmodel;

import com.dyaco.spirit_commercial.support.intdef.GENERAL;

public class DiagramBarBean {
    private @GENERAL.barType
    int barType;
    private @GENERAL.barStatus
    int barStatus;
    private int barNum; //階數
    private int orgBarNum;

    private int barK; //0 normal, 1 sprint/warmup , 2 rest/cooldown
    private int timeK;

    public DiagramBarBean() {
    }

    public DiagramBarBean(int barType, int barStatus, int barNum, int orgBarNum, int barK, int timeK) {
        this.barType = barType;
        this.barStatus = barStatus;
        this.barNum = barNum;
        this.orgBarNum = orgBarNum;
        this.barK = barK;
        this.timeK = timeK;
    }

    public int getTimeK() {
        return timeK;
    }

    public void setTimeK(int timeK) {
        this.timeK = timeK;
    }

    public int getBarK() {
        return barK;
    }

    public void setBarK(int barK) {
        this.barK = barK;
    }

    public int getOrgBarNum() {
        return orgBarNum;
    }

    public void setOrgBarNum(int orgBarNum) {
        this.orgBarNum = orgBarNum;
    }

    public @GENERAL.barType
    int getBarType() {
        return barType;
    }

    public void setBarType(@GENERAL.barType int barType) {
        this.barType = barType;
    }

    public @GENERAL.barStatus
    int getBarStatus() {
        return barStatus;
    }

    public void setBarStatus(@GENERAL.barStatus int barStatus) {
        this.barStatus = barStatus;
    }

    public int getBarNum() {
        return barNum;
    }

    public void setBarNum(int barNum) {
        this.barNum = barNum;
    }

}
