package com.dyaco.spirit_commercial.support.room.spirit.spirit_entity;


import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(
        tableName = DeviceEntity.DEVICE,
        indices = {@Index("deviceId")})
/**
 * Treadmill
 * 0x50 Add Lubricating Oil/ (添加機油) @每 1200 hours 提示一次
 * 0x51 Please clean motor room by vacuum cleaner.  @Each quarterly
 * 0x52 Please replace running belt @Each 2 years or each 8,000hr of usage
 * 0x53 Please flip running deck @Each 1 year
 * 0x54 Please replace motor driving belt @Each 2 years
 *
 * Bike
 * 0xBA Please check belt tension(no slip) @Each 1 year or 4,000hrs of usage
 * 0xBB Please replace driving belt @Each 2 years
 *
 * Recumbent Bike
 * 0xBC Please clean siding rails(Under Seat) @Each half year
 *
 * Elliptical
 * 0xE0 Please check belt tension(no slip) @Each 1 year
 * 0xE1 Please replace driving belt @Each 2 year
 * 0xE2 Please clean siding rails(Under pedal arms) @Each quarterly
 */
public class DeviceEntity {
    public static final String DEVICE = "device";

    @PrimaryKey
    private long deviceId;
    private long deviceRunningTime;
    private double deviceDistance; //MI
    private long deviceStep;
    private Date deviceFirstLaunchTime;

    private long e050hour;
    private long e052hour;
    private long e0BAhour;

    private Date e051time;
    private Date e052time;
    private Date e053time;
    private Date e054time;

    private Date e0BAtime;
    private Date e0BBtime;

    private Date e0BCtime;

    private Date e0E0time;
    private Date e0E1time;
    private Date e0E2time;



    private int type;
    private int model;
    private int antPlusDeviceId;

    //Imperial:0, Metric:1
    private int units;

    //Gymkit:0, Membership:1
    private int nfc;

    //HDMI:0, TV:1, CAST:2
    private int video;

    //DCI OSD:0, IPTV:1
    private int tv;

    //Csafe:0, CAB:1
    private int protocol;

    //off:0, on:1
    private int beep;
    private int pauseMode;
    private int sleepMode;
    private long sleepAfter;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getModel() {
        return model;
    }

    public void setModel(int model) {
        this.model = model;
    }

    public int getAntPlusDeviceId() {
        return antPlusDeviceId;
    }

    public void setAntPlusDeviceId(int antPlusDeviceId) {
        this.antPlusDeviceId = antPlusDeviceId;
    }

    public int getUnits() {
        return units;
    }

    public void setUnits(int units) {
        this.units = units;
    }

    public int getNfc() {
        return nfc;
    }

    public void setNfc(int nfc) {
        this.nfc = nfc;
    }

    public int getVideo() {
        return video;
    }

    public void setVideo(int video) {
        this.video = video;
    }

    public int getTv() {
        return tv;
    }

    public void setTv(int tv) {
        this.tv = tv;
    }

    public int getProtocol() {
        return protocol;
    }

    public void setProtocol(int protocol) {
        this.protocol = protocol;
    }

    public int getBeep() {
        return beep;
    }

    public void setBeep(int beep) {
        this.beep = beep;
    }

    public int getPauseMode() {
        return pauseMode;
    }

    public void setPauseMode(int pauseMode) {
        this.pauseMode = pauseMode;
    }

    public int getSleepMode() {
        return sleepMode;
    }

    public void setSleepMode(int sleepMode) {
        this.sleepMode = sleepMode;
    }

    public long getSleepAfter() {
        return sleepAfter;
    }

    public void setSleepAfter(long sleepAfter) {
        this.sleepAfter = sleepAfter;
    }

    public DeviceEntity() {
    }

    public long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
    }

    public long getDeviceRunningTime() {
        return deviceRunningTime;
    }

    public void setDeviceRunningTime(long deviceRunningTime) {
        this.deviceRunningTime = deviceRunningTime;
    }

    public Date getDeviceFirstLaunchTime() {
        return deviceFirstLaunchTime;
    }

    public void setDeviceFirstLaunchTime(Date deviceFirstLaunchTime) {
        this.deviceFirstLaunchTime = deviceFirstLaunchTime;
    }

    public long getE050hour() {
        return e050hour;
    }

    public void setE050hour(long e050hour) {
        this.e050hour = e050hour;
    }

    public long getE052hour() {
        return e052hour;
    }

    public void setE052hour(long e052hour) {
        this.e052hour = e052hour;
    }

    public long getE0BAhour() {
        return e0BAhour;
    }

    public void setE0BAhour(long e0BAhour) {
        this.e0BAhour = e0BAhour;
    }

    public Date getE051time() {
        return e051time;
    }

    public void setE051time(Date e051time) {
        this.e051time = e051time;
    }

    public Date getE052time() {
        return e052time;
    }

    public void setE052time(Date e052time) {
        this.e052time = e052time;
    }

    public Date getE053time() {
        return e053time;
    }

    public void setE053time(Date e053time) {
        this.e053time = e053time;
    }

    public Date getE054time() {
        return e054time;
    }

    public void setE054time(Date e054time) {
        this.e054time = e054time;
    }

    public Date getE0BAtime() {
        return e0BAtime;
    }

    public void setE0BAtime(Date e0BAtime) {
        this.e0BAtime = e0BAtime;
    }

    public Date getE0BBtime() {
        return e0BBtime;
    }

    public void setE0BBtime(Date e0BBtime) {
        this.e0BBtime = e0BBtime;
    }

    public Date getE0BCtime() {
        return e0BCtime;
    }

    public void setE0BCtime(Date e0BCtime) {
        this.e0BCtime = e0BCtime;
    }

    public Date getE0E0time() {
        return e0E0time;
    }

    public void setE0E0time(Date e0E0time) {
        this.e0E0time = e0E0time;
    }

    public Date getE0E1time() {
        return e0E1time;
    }

    public void setE0E1time(Date e0E1time) {
        this.e0E1time = e0E1time;
    }

    public Date getE0E2time() {
        return e0E2time;
    }

    public void setE0E2time(Date e0E2time) {
        this.e0E2time = e0E2time;
    }

    public double getDeviceDistance() {
        return deviceDistance;
    }

    public void setDeviceDistance(double deviceDistance) {
        this.deviceDistance = deviceDistance;
    }

    public long getDeviceStep() {
        return deviceStep;
    }

    public void setDeviceStep(long deviceStep) {
        this.deviceStep = deviceStep;
    }
}