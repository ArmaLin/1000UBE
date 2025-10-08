package com.dyaco.spirit_commercial.model.repository;

import androidx.annotation.NonNull;

public class WifiXmlData {
    String SSID;
    String PreSharedKey;

    String type;

    String hiddenSSID;


    public String getSSID() {
        return SSID;
    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    public String getPreSharedKey() {
        return PreSharedKey;
    }

    public void setPreSharedKey(String preSharedKey) {
        PreSharedKey = preSharedKey;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String isHiddenSSID() {
        return hiddenSSID;
    }

    public void setHiddenSSID(String hiddenSSID) {
        this.hiddenSSID = hiddenSSID;
    }

    @NonNull
    @Override
    public String toString() {
        return "WifiXmlData{" +
                "SSID='" + SSID + '\'' +
                ", PreSharedKey='" + PreSharedKey + '\'' +
                ", type='" + type + '\'' +
                ", hiddenSSID=" + hiddenSSID +
                '}';
    }
}
