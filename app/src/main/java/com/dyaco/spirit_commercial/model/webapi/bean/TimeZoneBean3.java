package com.dyaco.spirit_commercial.model.webapi.bean;

import androidx.annotation.NonNull;

public class TimeZoneBean3 {


    private String status;
    private String message;
    private String countryCode;
    private String countryName;
    private String zoneName;
    private String abbreviation;
    private Integer gmtOffset;
    private String dst;
    private Integer zoneStart;
    private Object zoneEnd;
    private Object nextAbbreviation;
    private Integer timestamp;
    private String formatted;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public Integer getGmtOffset() {
        return gmtOffset;
    }

    public void setGmtOffset(Integer gmtOffset) {
        this.gmtOffset = gmtOffset;
    }

    public String getDst() {
        return dst;
    }

    public void setDst(String dst) {
        this.dst = dst;
    }

    public Integer getZoneStart() {
        return zoneStart;
    }

    public void setZoneStart(Integer zoneStart) {
        this.zoneStart = zoneStart;
    }

    public Object getZoneEnd() {
        return zoneEnd;
    }

    public void setZoneEnd(Object zoneEnd) {
        this.zoneEnd = zoneEnd;
    }

    public Object getNextAbbreviation() {
        return nextAbbreviation;
    }

    public void setNextAbbreviation(Object nextAbbreviation) {
        this.nextAbbreviation = nextAbbreviation;
    }

    public Integer getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Integer timestamp) {
        this.timestamp = timestamp;
    }

    public String getFormatted() {
        return formatted;
    }

    public void setFormatted(String formatted) {
        this.formatted = formatted;
    }

    @NonNull
    @Override
    public String toString() {
        return "TimeZoneBean3{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", countryName='" + countryName + '\'' +
                ", zoneName='" + zoneName + '\'' +
                ", abbreviation='" + abbreviation + '\'' +
                ", gmtOffset=" + gmtOffset +
                ", dst='" + dst + '\'' +
                ", zoneStart=" + zoneStart +
                ", zoneEnd=" + zoneEnd +
                ", nextAbbreviation=" + nextAbbreviation +
                ", timestamp=" + timestamp +
                ", formatted='" + formatted + '\'' +
                '}';
    }
}
