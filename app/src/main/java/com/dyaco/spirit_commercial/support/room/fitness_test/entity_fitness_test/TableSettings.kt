package com.dyaco.spirit_commercial.support.room.fitness_test.entity_fitness_test;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "TableSettings")
public class TableSettings {

    //@PrimaryKey(autoGenerate = true)
    @PrimaryKey
    private int     id;

    private int     machine_type;
    @NonNull
    private String  serial_no;
    @NonNull
    private String  email;
    private int     language;
    private int     time_format;
    private int     wifi_sw;
    @NonNull
    private String  wifi_ssid;
    private int     wifi_lock;
    @NonNull
    private String  wifi_pwd;
    private int     handrails_height;
    private int     deck_lift;
    private int     highlights;
    private int     standby;
    private int     pause_mode;
    private int     unit_sys;
    private int     bt_sw;
    @NonNull
    private String  bt_name;
    private int     hrm_sw;
    @NonNull
    private String  hrm_name;
    @NonNull
    private int     highlights_sw;
    private int     odo_distance;
    private int     odo_time;
    private int     brake_sw;
    private int     lock_facility;
    private int     incline_return;
    @NonNull
    private String  machine_uuid;
    private int     height;
    private int     weight;
    private int     age;

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public TableSettings() {
    }

    @Ignore
    public TableSettings(int id, int machine_type, String serial_no, String email, int language, int time_format, int wifi_sw, String wifi_ssid,
                         int wifi_lock, String wifi_pwd, int handrails_height, int deck_lift, int highlights, int standby, int pause_mode,
                         int unit_sys, int bt_sw, String bt_name, int hrm_sw, String hrm_name, int highlights_sw,
                         int odo_distance, int odo_time, int brake_sw, int lock_facility, int incline_return, String machine_uuid, int height, int weight, int age) {
        this.id = id;
        this.machine_type = machine_type;
        this.serial_no = serial_no;
        this.email = email;
        this.language = language;
        this.time_format = time_format;
        this.wifi_sw = wifi_sw;
        this.wifi_ssid = wifi_ssid;
        this.wifi_lock = wifi_lock;
        this.wifi_pwd = wifi_pwd;
        this.handrails_height = handrails_height;
        this.deck_lift = deck_lift;
        this.highlights = highlights;
        this.standby = standby;
        this.pause_mode = pause_mode;
        this.unit_sys = unit_sys;
        this.bt_sw = bt_sw;
        this.bt_name = bt_name;
        this.hrm_sw = hrm_sw;
        this.hrm_name = hrm_name;
        this.highlights_sw = highlights_sw;
        this.odo_distance = odo_distance;
        this.odo_time = odo_time;
        this.brake_sw = brake_sw;
        this.lock_facility = lock_facility;
        this.incline_return = incline_return;
        this.machine_uuid = machine_uuid;
        this.height = height;
        this.weight = weight;
        this.age = age;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMachine_type() {
        return machine_type;
    }

    public void setMachine_type(int machine_type) {
        this.machine_type = machine_type;
    }

    public String getSerial_no() {
        return serial_no;
    }

    public void setSerial_no(String serial_no) {
        this.serial_no = serial_no;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getLanguage() {
        return language;
    }

    public void setLanguage(int language) {
        this.language = language;
    }

    public int getTime_format() {
        return time_format;
    }

    public void setTime_format(int time_format) {
        this.time_format = time_format;
    }

    public int getWifi_sw() {
        return wifi_sw;
    }

    public void setWifi_sw(int wifi_sw) {
        this.wifi_sw = wifi_sw;
    }

    public String getWifi_ssid() {
        return wifi_ssid;
    }

    public void setWifi_ssid(String wifi_ssid) {
        this.wifi_ssid = wifi_ssid;
    }

    public int getWifi_lock() {
        return wifi_lock;
    }

    public void setWifi_lock(int wifi_lock) {
        this.wifi_lock = wifi_lock;
    }

    public String getWifi_pwd() {
        return wifi_pwd;
    }

    public void setWifi_pwd(String wifi_pwd) {
        this.wifi_pwd = wifi_pwd;
    }

    public int getHandrails_height() {
        return handrails_height;
    }

    public void setHandrails_height(int handrails_height) {
        this.handrails_height = handrails_height;
    }

    public int getDeck_lift() {
        return deck_lift;
    }

    public void setDeck_lift(int deck_lift) {
        this.deck_lift = deck_lift;
    }

    public int getHighlights() {
        return highlights;
    }

    public void setHighlights(int highlights) {
        this.highlights = highlights;
    }

    public int getStandby() {
        return standby;
    }

    public void setStandby(int standby) {
        this.standby = standby;
    }

    public int getPause_mode() {
        return pause_mode;
    }

    public void setPause_mode(int pause_mode) {
        this.pause_mode = pause_mode;
    }

    public int getUnit_sys() {
        return unit_sys;
    }

    public void setUnit_sys(int unit_sys) {
        this.unit_sys = unit_sys;
    }

    public int getBt_sw() {
        return bt_sw;
    }

    public void setBt_sw(int bt_sw) {
        this.bt_sw = bt_sw;
    }

    public String getBt_name() {
        return bt_name;
    }

    public void setBt_name(String bt_name) {
        this.bt_name = bt_name;
    }

    public int getHrm_sw() {
        return hrm_sw;
    }

    public void setHrm_sw(int hrm_sw) {
        this.hrm_sw = hrm_sw;
    }

    public String getHrm_name() {
        return hrm_name;
    }

    public void setHrm_name(String hrm_name) {
        this.hrm_name = hrm_name;
    }

    public int getHighlights_sw() {
        return highlights_sw;
    }

    public void setHighlights_sw(int highlights_sw) {
        this.highlights_sw = highlights_sw;
    }

    public int getOdo_distance() {
        return odo_distance;
    }

    public void setOdo_distance(int odo_distance) {
        this.odo_distance = odo_distance;
    }

    public int getOdo_time() {
        return odo_time;
    }

    public void setOdo_time(int odo_time) {
        this.odo_time = odo_time;
    }

    public int getBrake_sw() {
        return brake_sw;
    }

    public void setBrake_sw(int brake_sw) {
        this.brake_sw = brake_sw;
    }

    public int getLock_facility() {
        return lock_facility;
    }

    public void setLock_facility(int lock_facility) {
        this.lock_facility = lock_facility;
    }

    public int getIncline_return() {
        return incline_return;
    }

    public void setIncline_return(int incline_return) {
        this.incline_return = incline_return;
    }

    public String getMachine_uuid() {
        return machine_uuid;
    }

    public void setMachine_uuid(String machine_uuid) {
        this.machine_uuid = machine_uuid;
    }
}
