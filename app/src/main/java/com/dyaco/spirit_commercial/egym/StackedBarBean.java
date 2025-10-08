package com.dyaco.spirit_commercial.egym;

import androidx.annotation.NonNull;

public class StackedBarBean {
    private double value;
    private int secTime;

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public int getSecTime() {
        return secTime;
    }

    public void setSecTime(int secTime) {
        this.secTime = secTime;
    }

    @NonNull
    @Override
    public String toString() {
        return "StackedBarBean{" +
                "value=" + value +
                ", secTime=" + secTime +
                '}';
    }
}
