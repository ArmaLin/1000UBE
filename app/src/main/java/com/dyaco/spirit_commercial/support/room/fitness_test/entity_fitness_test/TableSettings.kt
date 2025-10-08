package com.dyaco.spirit_commercial.support.room.fitness_test.entity_fitness_test

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "TableSettings")
data class TableSettings(
    @PrimaryKey
    var id: Int = 0,

    @ColumnInfo(name = "machine_type")
    var machineType: Int = 0,

    @ColumnInfo(name = "serial_no")
    var serialNo: String = "",

    var email: String = "",

    var language: Int = 0,

    @ColumnInfo(name = "time_format")
    var timeFormat: Int = 0,

    @ColumnInfo(name = "wifi_sw")
    var wifiSw: Int = 0,

    @ColumnInfo(name = "wifi_ssid")
    var wifiSsid: String = "",

    @ColumnInfo(name = "wifi_lock")
    var wifiLock: Int = 0,

    @ColumnInfo(name = "wifi_pwd")
    var wifiPwd: String = "",

    @ColumnInfo(name = "handrails_height")
    var handrailsHeight: Int = 0,

    @ColumnInfo(name = "deck_lift")
    var deckLift: Int = 0,

    var highlights: Int = 0,
    var standby: Int = 0,

    @ColumnInfo(name = "pause_mode")
    var pauseMode: Int = 0,

    @ColumnInfo(name = "unit_sys")
    var unitSys: Int = 0,

    @ColumnInfo(name = "bt_sw")
    var btSw: Int = 0,

    @ColumnInfo(name = "bt_name")
    var btName: String = "",

    @ColumnInfo(name = "hrm_sw")
    var hrmSw: Int = 0,

    @ColumnInfo(name = "hrm_name")
    var hrmName: String = "",

    @ColumnInfo(name = "highlights_sw")
    var highlightsSw: Int = 0,

    @ColumnInfo(name = "odo_distance")
    var odoDistance: Int = 0,

    @ColumnInfo(name = "odo_time")
    var odoTime: Int = 0,

    @ColumnInfo(name = "brake_sw")
    var brakeSw: Int = 0,

    @ColumnInfo(name = "lock_facility")
    var lockFacility: Int = 0,

    @ColumnInfo(name = "incline_return")
    var inclineReturn: Int = 0,

    @ColumnInfo(name = "machine_uuid")
    var machineUuid: String = "",

    var height: Int = 0,
    var weight: Int = 0,
    var age: Int = 0
)