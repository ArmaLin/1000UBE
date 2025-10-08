package com.dyaco.spirit_commercial.support.room.spirit.spirit_entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = DeviceEntity.DEVICE,
    indices = [Index("deviceId")]
)
data class DeviceEntity(
    @PrimaryKey
    var deviceId: Long = 0,
    var deviceRunningTime: Long = 0,
    var deviceDistance: Double = 0.0, //MI
    var deviceStep: Long = 0,
    var deviceFirstLaunchTime: Date? = null,
    var e050hour: Long = 0,
    var e052hour: Long = 0,
    var e0BAhour: Long = 0,
    var e051time: Date? = null,
    var e052time: Date? = null,
    var e053time: Date? = null,
    var e054time: Date? = null,
    var e0BAtime: Date? = null,
    var e0BBtime: Date? = null,
    var e0BCtime: Date? = null,
    var e0E0time: Date? = null,
    var e0E1time: Date? = null,
    var e0E2time: Date? = null,
    var type: Int = 0,
    var model: Int = 0,
    var antPlusDeviceId: Int = 0,
    var units: Int = 0, //Imperial:0, Metric:1
    var nfc: Int = 0, //Gymkit:0, Membership:1
    var video: Int = 0, //HDMI:0, TV:1, CAST:2
    var tv: Int = 0, //DCI OSD:0, IPTV:1
    var protocol: Int = 0, //Csafe:0, CAB:1
    var beep: Int = 0, //off:0, on:1
    var pauseMode: Int = 0,
    var sleepMode: Int = 0,
    var sleepAfter: Long = 0
) {
    companion object {
        const val DEVICE = "device"
    }
}