package com.dyaco.spirit_commercial.viewmodel;

import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.TERRITORY_US;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.corestar.libs.device.DeviceTvTuner;
import com.dyaco.spirit_commercial.support.intdef.DeviceIntDef;

import java.util.Date;
import java.util.Locale;


public class DeviceSettingBean implements Parcelable {

    /**
     * #Treadmill
     * 0x50 Add Lubricating Oil/ (添加機油) @每 1200 hours 提示一次
     * 0x51 Please clean motor room by vacuum cleaner.  @Each quarterly
     * 0x52 Please replace running belt @Each 2 years or each 8,000hr of usage
     * 0x53 Please flip running deck @Each 1 year
     * 0x54 Please replace motor driving belt @Each 2 years
     *
     * #Bike
     * 0xBA Please check belt tension(no slip) @Each 1 year or 4,000hrs of usage
     * 0xBB Please replace driving belt @Each 2 years
     *
     * Recumbent Bike
     * 0xBC Please clean siding rails(Under Seat) @Each half year
     *
     * #Elliptical
     * 0xE0 Please check belt tension(no slip) @Each 1 year
     * 0xE1 Please replace driving belt @Each 2 year
     * 0xE2 Please clean siding rails(Under pedal arms) @Each quarterly
     */
    private String mediaApps;
    private Locale defaultLanguage;
    private long softwareUpdatedMillis;
    private long currentVersionCode;
    private boolean isFakeStart;
    private boolean isGsMode; //false = GS MODE
    private boolean isAutoUpdate;
    private double ODO_time = 0; //Console 總時間
    private double ODO_distance = 0; //Console 總距離 都存成英制
    private Date deviceFirstLaunchTime; //第一次開機時間
    private long deviceStep;
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

    private int type; //typeCode DEVICE_TYPE_TREADMILL
    private int antPlusDeviceId;

    private boolean isFittttttt;

    public boolean isFittttttt() {
        return isFittttttt;
    }

    public void setFittttttt(boolean fittttttt) {
        isFittttttt = fittttttt;
    }

    //EGYM
    private int consoleSystem;

    public int getConsoleSystem() {
        return consoleSystem;
    }

    public void setConsoleSystem(int consoleSystem) {
        this.consoleSystem = consoleSystem;
    }

    //Gymkit:0, Membership:1
    private int nfc;

    //HDMI:0, TV:1, CAST:2
    private int video;

    //DCI OSD:0, IPTV:1
    private int tv;

    //Csafe:0, CAB:1
    private int protocol;

    //TvTuner 訊號種類
    private int tvTunerSignal; //0 Digital Signal, 1 Analog Signal

    //TvTuner 類比類別
    private int tvTunerAnalogType; //0 cable, 1 air

    private int territoryCode = TERRITORY_US;

    public int getTerritoryCode() {
        return territoryCode;
    }

    public void setTerritoryCode(int territoryCode) {
        this.territoryCode = territoryCode;
    }

    public int getTvTunerAnalogType() {
        return tvTunerAnalogType;
    }

    public void setTvTunerAnalogType(int tvTunerAnalogType) {
        this.tvTunerAnalogType = tvTunerAnalogType;
    }

    public int getTvTunerSignal() {
        return tvTunerSignal;
    }

    public void setTvTunerSignal(int tvTunerSignal) {
        this.tvTunerSignal = tvTunerSignal;
    }

    private int pauseMode; //workout暫停時，是否要倒數5分鐘 , 1 on 要倒數, 0 off 不要倒數

    //Auto Pause只會在 workout開始後才會偵測，當橋接板error code2 bit4 =1時，
    // 表示跑帶在動作但無人在使用；因此，當此狀況持續時間超過上表設定時間後，上表直接結束此次workout，並logout，回到login畫面；
    private int autoPause;//是否偵測無人在機台上,   >>>> AUTO LOGOUT
    private long pauseAfter; //秒 Pause After= 3:00

    private int isUseTimeLimit; //1 on 要限制, 0 off 不要限制
    private long useTimeLimit = 0; //限制個人使用時間 秒 Use Time Limit = 2:00:00,

    private int usageRestrictionsType = 0; //0:都不限制, 1:限制 distance,2:限制 time
    private long usageRestrictionsTimeLimit = 0; //限制個人使用時間 秒
    private int usageRestrictionsDistanceLimit = 0; //設定的限制使用距離 公制

    private double currentUsageRestrictionsDistance = 0; //當前限制使用距離 公制
    private int currentUsageRestrictionsTime = 0; //當限制使用

    private String limitCode; //限制CODE

    private int model_code = -1;

    //Imperial:0, Metric:1
    private int unit_code;

    //	0 --> on (會休眠)
//	1 --> off (不休眠)
    private int sleep_mode;
    private long sleepAfter; //秒

    //off:0, on:1
    private int beep_sound;
    private String dsFrontInclineAd;
    private String dsRearInclineAd;
    private String dsLevelAd;
    private boolean first_launch = true;
    /**
     * 0 --> XE395ENT
     * 1 --> XBR55ENT
     * 2 --> XBU55ENT
     */
    //  private int model_code = 0;
    private String model_name;
    private int child_lock;
    private int display_brightness = 100;
    /**
     * XE395_FTMS
     * XBR55_FTMS
     * XBU55_FTMS
     */
    private String ble_device_name;

    private String machine_mac;
    private boolean isMachineEdit;
    private boolean isFtmsNameEdit;

    private int tvTunerVolume;

    private double maxSpeedMu; // 20.0KPH(Range 16.0~20.0 KPH) ,240
    private double maxSpeedIu; // 12.0MPH(Range 10.0~12.0 MPH)Step 0.1 ,150

    private double maxIncline; //10.0% (Range 10.0~15.0) Step 1.0%


    /**
     * 最低速範圍：
     * 公制: 0.5 km/h (預設)- 0.8 km/h
     * 英制: 0.3 MPH (預設)- 0.5 MPH
     */
    private int minSpeedMu = 8; //5 , 8
    private int minSpeedIu = 5; //3, 5

    private String tmpApkPath = "";
    private boolean isShowUpdateNotify;

    public int getMinSpeedMu() {
        return minSpeedMu;
    }

    public void setMinSpeedMu(int minSpeedMu) {
        this.minSpeedMu = minSpeedMu;
    }

    public int getMinSpeedIu() {
        return minSpeedIu;
    }

    public void setMinSpeedIu(int minSpeedIu) {
        this.minSpeedIu = minSpeedIu;
    }

    public String getLimitCode() {
        return limitCode;
    }

    public void setLimitCode(String limitCode) {
        this.limitCode = limitCode;
    }

    public boolean isShowUpdateNotify() {
        return isShowUpdateNotify;
    }

    public void setShowUpdateNotify(boolean showUpdateNotify) {
        isShowUpdateNotify = showUpdateNotify;
    }

    public String getTmpApkPath() {
        return tmpApkPath;
    }

    public void setTmpApkPath(String tmpApkPath) {
        this.tmpApkPath = tmpApkPath;
    }

    public long getCurrentVersionCode() {
        return currentVersionCode;
    }

    public void setCurrentVersionCode(long currentVersionCode) {
        this.currentVersionCode = currentVersionCode;
    }

    public boolean isAutoUpdate() {
        return isAutoUpdate;
    }

    public void setAutoUpdate(boolean autoUpdate) {
        isAutoUpdate = autoUpdate;
    }

    public int getIsUseTimeLimit() {
        return isUseTimeLimit;
    }

    public void setIsUseTimeLimit(int isUseTimeLimit) {
        this.isUseTimeLimit = isUseTimeLimit;
    }

    public int getAutoPause() {
        return autoPause;
    }

    public void setAutoPause(int autoPause) {
        this.autoPause = autoPause;
    }

    public long getPauseAfter() {
        return pauseAfter;
    }

    public void setPauseAfter(long pauseAfter) {
        this.pauseAfter = pauseAfter;
    }

    public double getMaxSpeedIu() {
        return maxSpeedIu;
    }

    public void setMaxSpeedIu(double maxSpeedIu) {
        this.maxSpeedIu = maxSpeedIu;
    }

    public double getMaxSpeedMu() {
        return maxSpeedMu;
    }

    public void setMaxSpeedMu(double maxSpeedMu) {
        this.maxSpeedMu = maxSpeedMu;
    }

    public double getMaxIncline() {
        return maxIncline;
    }

    public void setMaxIncline(double maxIncline) {
        this.maxIncline = maxIncline;
    }

    public int getTvTunerVolume() {
        return tvTunerVolume;
    }

    public void setTvTunerVolume(int tvTunerVolume) {
        this.tvTunerVolume = tvTunerVolume;
    }

    public boolean isMachineEdit() {
        return isMachineEdit;
    }

    public void setMachineEdit(boolean machineEdit) {
        isMachineEdit = machineEdit;
    }

    public boolean isFtmsNameEdit() {
        return isFtmsNameEdit;
    }

    public void setFtmsNameEdit(boolean ftmsNameEdit) {
        isFtmsNameEdit = ftmsNameEdit;
    }

    public String getMachine_mac() {
        return machine_mac;
    }

    public void setMachine_mac(String machine_mac) {
        this.machine_mac = machine_mac;
    }

    private @DeviceIntDef.TimeFormat int time_unit;

    private DeviceTvTuner.TV_COUNTRY tvCountry;

    public DeviceTvTuner.TV_COUNTRY getTvCountry() {
        return tvCountry;
    }

    public void setTvCountry(DeviceTvTuner.TV_COUNTRY tvCountry) {
        this.tvCountry = tvCountry;
    }

    public long getUseTimeLimit() {
        return useTimeLimit;
    }

    public void setUseTimeLimit(long useTimeLimit) {
        this.useTimeLimit = useTimeLimit;
    }

    public int getModel_code() {
        return model_code;
    }

    public void setModel_code(int model_code) {
        this.model_code = model_code;
    }

    public int getUnit_code() {
        return unit_code;
    }

    public void setUnit_code(int unit_code) {
        this.unit_code = unit_code;
    }

    public boolean isFakeStart() {
        return isFakeStart;
    }

    public void setFakeStart(boolean fakeStart) {
        isFakeStart = fakeStart;
    }

    public long getSoftwareUpdatedMillis() {
        return softwareUpdatedMillis;
    }

    public void setSoftwareUpdatedMillis(long softwareUpdatedMillis) {
        this.softwareUpdatedMillis = softwareUpdatedMillis;
    }

    /**
     * category_code
     * 0=Treadmill,
     * 1=Bike,
     * 2=Elliptical
     * 3=Stepper
     * 4=Rower
     */
    private int categoryCode;

    private int maxRpm;
    private int minRpm;
    private int maxLevel;
    private int minLevel;
    private String brand_name;

    public boolean isGsMode() {
        return isGsMode;
    }

    public void setGsMode(boolean gsMode) {
        this.isGsMode = gsMode;
    }

    public String getBrand_name() {
        return brand_name;
    }

    public void setBrand_name(String brand_name) {
        this.brand_name = brand_name;
    }

    public int getMaxRpm() {
        return maxRpm;
    }

    public void setMaxRpm(int maxRpm) {
        this.maxRpm = maxRpm;
    }

    public int getMinRpm() {
        return minRpm;
    }

    public void setMinRpm(int minRpm) {
        this.minRpm = minRpm;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public int getMinLevel() {
        return minLevel;
    }

    public void setMinLevel(int minLevel) {
        this.minLevel = minLevel;
    }

    public int getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(int categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getModel_name() {
        return model_name;
    }

    public void setModel_name(String model_name) {
        this.model_name = model_name;
    }

    public int getChild_lock() {
        return child_lock;
    }

    public void setChild_lock(int child_lock) {
        this.child_lock = child_lock;
    }

    public int getDisplay_brightness() {
        return display_brightness;
    }

    public void setDisplay_brightness(int display_brightness) {
        this.display_brightness = display_brightness;
    }

    public String getBle_device_name() {

        return ble_device_name;
    }

    public void setBle_device_name(String ble_device_name) {
        this.ble_device_name = ble_device_name;
    }

    public @DeviceIntDef.TimeFormat
    int getTime_unit() {
        return time_unit;
    }

    public void setTime_unit(@DeviceIntDef.TimeFormat int time_unit) {
        this.time_unit = time_unit;
    }

    public double getODO_time() {
        return ODO_time;
    }

    public void setODO_time(double ODO_time) {
        this.ODO_time = ODO_time;
    }

    public double getODO_distance() {
        return ODO_distance;
    }

    public void setODO_distance(double ODO_distance) {
        this.ODO_distance = ODO_distance;
    }

    public int getSleep_mode() {
        return sleep_mode;
    }

    public void setSleep_mode(int sleep_mode) {
        this.sleep_mode = sleep_mode;
    }

    public int getBeep_sound() {
        return beep_sound;
    }

    public void setBeep_sound(int beep_sound) {
        this.beep_sound = beep_sound;
    }

    public String getDsFrontInclineAd() {
        return dsFrontInclineAd;
    }

    public void setDsFrontInclineAd(String dsFrontInclineAd) {
        this.dsFrontInclineAd = dsFrontInclineAd;
    }

    public String getDsRearInclineAd() {
        return dsRearInclineAd;
    }

    public void setDsRearInclineAd(String dsRearInclineAd) {
        this.dsRearInclineAd = dsRearInclineAd;
    }

    public String getDsLevelAd() {
        return dsLevelAd;
    }

    public void setDsLevelAd(String dsLevelAd) {
        this.dsLevelAd = dsLevelAd;
    }

    public boolean isFirst_launch() {
        return first_launch;
    }

    public void setFirst_launch(boolean first_launch) {
        this.first_launch = first_launch;
    }

    public Date getDeviceFirstLaunchTime() {
        return deviceFirstLaunchTime;
    }

    public void setDeviceFirstLaunchTime(Date deviceFirstLaunchTime) {
        this.deviceFirstLaunchTime = deviceFirstLaunchTime;
    }

    public long getDeviceStep() {
        return deviceStep;
    }

    public void setDeviceStep(long deviceStep) {
        this.deviceStep = deviceStep;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    public int getAntPlusDeviceId() {
        return antPlusDeviceId;
    }

    public void setAntPlusDeviceId(int antPlusDeviceId) {
        this.antPlusDeviceId = antPlusDeviceId;
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

    public int getPauseMode() {
        return pauseMode;
    }

    public void setPauseMode(int pauseMode) {
        this.pauseMode = pauseMode;
    }

    public long getSleepAfter() {
        return sleepAfter;
    }

    public void setSleepAfter(long sleepAfter) {
        this.sleepAfter = sleepAfter;
    }

    public int getUsageRestrictionsDistanceLimit() {
        return usageRestrictionsDistanceLimit;
    }

    public void setUsageRestrictionsDistanceLimit(int usageRestrictionsDistanceLimit) {
        this.usageRestrictionsDistanceLimit = usageRestrictionsDistanceLimit;
    }

    public double getCurrentUsageRestrictionsDistance() {
        return currentUsageRestrictionsDistance;
    }

    public void setCurrentUsageRestrictionsDistance(double currentUsageRestrictionsDistance) {
        this.currentUsageRestrictionsDistance = currentUsageRestrictionsDistance;
    }

    public int getCurrentUsageRestrictionsTime() {
        return currentUsageRestrictionsTime;
    }

    public void setCurrentUsageRestrictionsTime(int currentUsageRestrictionsTime) {
        this.currentUsageRestrictionsTime = currentUsageRestrictionsTime;
    }

    public int getUsageRestrictionsType() {
        return usageRestrictionsType;
    }

    public void setUsageRestrictionsType(int usageRestrictionsType) {
        this.usageRestrictionsType = usageRestrictionsType;
    }

    public long getUsageRestrictionsTimeLimit() {
        return usageRestrictionsTimeLimit;
    }

    public void setUsageRestrictionsTimeLimit(long usageRestrictionsTimeLimit) {
        this.usageRestrictionsTimeLimit = usageRestrictionsTimeLimit;
    }

    public Locale getDefaultLanguage() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(Locale defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

    public String getMediaApps() {
        return mediaApps;
    }

    public void setMediaApps(String mediaApps) {
        this.mediaApps = mediaApps;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public DeviceSettingBean() {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(unit_code);
        dest.writeDouble(ODO_time);
        dest.writeDouble(ODO_distance);
        dest.writeInt(sleep_mode);
        dest.writeInt(beep_sound);
        dest.writeString(dsFrontInclineAd);
        dest.writeString(dsRearInclineAd);
        dest.writeString(dsLevelAd);
        dest.writeByte((byte) (first_launch ? 1 : 0));
        dest.writeInt(model_code);
        dest.writeInt(time_unit);
        dest.writeString(ble_device_name);
        dest.writeString(model_name);
        dest.writeInt(child_lock);
        dest.writeInt(display_brightness);
        dest.writeInt(categoryCode);
        dest.writeInt(maxRpm);
        dest.writeInt(minRpm);
        dest.writeInt(maxLevel);
        dest.writeInt(minLevel);
        dest.writeString(brand_name);
        dest.writeSerializable(deviceFirstLaunchTime);

        dest.writeLong(deviceStep);
        dest.writeLong(e050hour);
        dest.writeLong(e052hour);
        dest.writeLong(e0BAhour);

        dest.writeSerializable(e051time);
        dest.writeSerializable(e052time);
        dest.writeSerializable(e053time);
        dest.writeSerializable(e054time);
        dest.writeSerializable(e0BAtime);
        dest.writeSerializable(e0BBtime);
        dest.writeSerializable(e0BCtime);
        dest.writeSerializable(e0E0time);
        dest.writeSerializable(e0E1time);
        dest.writeSerializable(e0E2time);

        dest.writeInt(type);
        dest.writeInt(antPlusDeviceId);
        dest.writeInt(nfc);
        dest.writeInt(video);
        dest.writeInt(tv);
        dest.writeInt(protocol);
        dest.writeInt(pauseMode);
        dest.writeLong(sleepAfter);
        dest.writeLong(useTimeLimit);
        dest.writeBoolean(isGsMode);

        dest.writeSerializable(tvCountry);

        dest.writeString(machine_mac);

        dest.writeBoolean(isMachineEdit);
        dest.writeBoolean(isFtmsNameEdit);

        dest.writeInt(tvTunerVolume);

        dest.writeDouble(maxSpeedMu);
        dest.writeDouble(maxSpeedIu);

        dest.writeDouble(maxIncline);

        dest.writeLong(pauseAfter);

        dest.writeInt(autoPause);

        dest.writeInt(isUseTimeLimit);

        dest.writeBoolean(isAutoUpdate);

        dest.writeBoolean(isFakeStart);

        dest.writeLong(currentVersionCode);

        dest.writeLong(softwareUpdatedMillis);

        dest.writeString(tmpApkPath);

        dest.writeBoolean(isShowUpdateNotify);

        dest.writeInt(usageRestrictionsDistanceLimit);

        dest.writeString(limitCode);

        dest.writeDouble(currentUsageRestrictionsDistance);
        dest.writeInt(currentUsageRestrictionsTime);


        dest.writeInt(usageRestrictionsType);
        dest.writeLong(usageRestrictionsTimeLimit);

        dest.writeInt(tvTunerSignal);

        dest.writeInt(tvTunerAnalogType);

        dest.writeInt(minSpeedIu);
        dest.writeInt(minSpeedMu);

        dest.writeInt(territoryCode);

        dest.writeSerializable(defaultLanguage);

        dest.writeString(mediaApps);

        dest.writeInt(consoleSystem);
    }

    public DeviceSettingBean(Parcel in) {
        unit_code = in.readInt();
        ODO_time = in.readDouble();
        ODO_distance = in.readDouble();
        sleep_mode = in.readInt();
        beep_sound = in.readInt();
        dsFrontInclineAd = in.readString();
        dsRearInclineAd = in.readString();
        dsLevelAd = in.readString();
        first_launch = in.readByte() != 0;
        model_code = in.readInt();
        time_unit = in.readInt();
        ble_device_name = in.readString();
        model_name = in.readString();
        child_lock = in.readInt();
        display_brightness = in.readInt();
        categoryCode = in.readInt();
        maxRpm = in.readInt();
        minRpm = in.readInt();
        maxLevel = in.readInt();
        minLevel = in.readInt();
        brand_name = in.readString();
        deviceFirstLaunchTime = (java.util.Date) in.readSerializable();

        deviceStep = in.readLong();
        e050hour = in.readLong();
        e052hour = in.readLong();
        e0BAhour = in.readLong();

        e051time = (java.util.Date) in.readSerializable();
        e052time = (java.util.Date) in.readSerializable();
        e053time = (java.util.Date) in.readSerializable();
        e054time = (java.util.Date) in.readSerializable();
        e0BAtime = (java.util.Date) in.readSerializable();
        e0BBtime = (java.util.Date) in.readSerializable();
        e0BCtime = (java.util.Date) in.readSerializable();
        e0E0time = (java.util.Date) in.readSerializable();
        e0E1time = (java.util.Date) in.readSerializable();
        e0E2time = (java.util.Date) in.readSerializable();

        type = in.readInt();
        antPlusDeviceId = in.readInt();
        nfc = in.readInt();
        video = in.readInt();
        tv = in.readInt();
        protocol = in.readInt();
        pauseMode = in.readInt();
        sleepAfter = in.readLong();
        useTimeLimit = in.readLong();

        isGsMode = in.readBoolean();

        tvCountry = (DeviceTvTuner.TV_COUNTRY) in.readSerializable();

        machine_mac = in.readString();

        isMachineEdit = in.readBoolean();
        isFtmsNameEdit = in.readBoolean();

        tvTunerVolume = in.readInt();

        maxSpeedMu = in.readDouble();
        maxSpeedIu = in.readDouble();

        maxIncline = in.readDouble();

        pauseAfter = in.readLong();

        autoPause = in.readInt();

        isUseTimeLimit = in.readInt();

        isAutoUpdate = in.readBoolean();

        isFakeStart = in.readBoolean();

        currentVersionCode = in.readLong();

        softwareUpdatedMillis = in.readLong();

        tmpApkPath = in.readString();

        isShowUpdateNotify = in.readBoolean();

        usageRestrictionsDistanceLimit = in.readInt();

        limitCode = in.readString();

        currentUsageRestrictionsDistance = in.readDouble();
        currentUsageRestrictionsTime = in.readInt();

        usageRestrictionsType = in.readInt();
        usageRestrictionsTimeLimit = in.readLong();


        tvTunerSignal = in.readInt();

        tvTunerAnalogType = in.readInt();


        minSpeedIu = in.readInt();
        minSpeedMu = in.readInt();

        territoryCode = in.readInt();

        defaultLanguage = (Locale) in.readSerializable();

        mediaApps = in.readString();

        consoleSystem = in.readInt();
    }

    public static final Creator<DeviceSettingBean> CREATOR = new Creator<DeviceSettingBean>() {
        @Override
        public DeviceSettingBean createFromParcel(Parcel in) {
            return new DeviceSettingBean(in);
        }

        @Override
        public DeviceSettingBean[] newArray(int size) {
            return new DeviceSettingBean[size];
        }
    };

    @NonNull
    @Override
    public String toString() {
        return "DeviceSettingBean{" + "\n" +
                "ODO_time=" + ODO_time + "\n" +
                "ODO_distance=" + ODO_distance + "\n" +
                "deviceFirstLaunchTime=" + deviceFirstLaunchTime + "\n" +
                "deviceStep=" + deviceStep + "\n" +
                "e050hour=" + e050hour + "\n" +
                "e052hour=" + e052hour + "\n" +
                "e0BAhour=" + e0BAhour + "\n" +
                "e051time=" + e051time + "\n" +
                "e052time=" + e052time + "\n" +
                "e053time=" + e053time + "\n" +
                "e054time=" + e054time + "\n" +
                "e0BAtime=" + e0BAtime + "\n" +
                "e0BBtime=" + e0BBtime + "\n" +
                "e0BCtime=" + e0BCtime + "\n" +
                "e0E0time=" + e0E0time + "\n" +
                "e0E1time=" + e0E1time + "\n" +
                "e0E2time=" + e0E2time + "\n" +
                "type=" + type + "\n" +
                "antPlusDeviceId=" + antPlusDeviceId + "\n" +
                "nfc=" + nfc + "\n" +
                "video=" + video + "\n" +
                "tv=" + tv + "\n" +
                "protocol=" + protocol + "\n" +
                "pauseMode=" + pauseMode + "\n" +
                "sleepAfter=" + sleepAfter + "\n" +
                "model_code=" + model_code + "\n" +
                "unit_code=" + unit_code + "\n" +
                "sleep_mode=" + sleep_mode + "\n" +
                "beep_sound=" + beep_sound + "\n" +
                "dsFrontInclineAd=" + dsFrontInclineAd + "\n" +
                "dsRearInclineAd=" + dsRearInclineAd + "\n" +
                "dsLevelAd=" + dsLevelAd + "\n" +
                "first_launch=" + first_launch + "\n" +
                "model_name=" + model_name + "\n" +
                "child_lock=" + child_lock + "\n" +
                "display_brightness=" + display_brightness + "\n" +
                "ble_device_name=" + ble_device_name + "\n" +
                "time_unit=" + time_unit + "\n" +
                "categoryCode=" + categoryCode + "\n" +
                "maxRpm=" + maxRpm + "\n" +
                "minRpm=" + minRpm + "\n" +
                "maxLevel=" + maxLevel + "\n" +
                "minLevel=" + minLevel + "\n" +
                "brand_name=" + brand_name + "\n" +
                "useTimeLimit=" + useTimeLimit + "\n" +
                "gsMode=" + isGsMode + "\n" +
                '}';
    }
}
