package com.dyaco.spirit_commercial.support.room.spirit.spirit_entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
@Entity(
    tableName = UserProfileEntity.USER_PROFILE,
    indices = [Index("uid")]
)
data class UserProfileEntity(
    @PrimaryKey(autoGenerate = true)
    var uid: Long = 0,
    var userName: String? = null,
    var userId: Int = 0,
    var userImage: Int = 0,
    var userType: Int = 0, // 0 add ,1 user, 2 guest
    var weight_metric: Int = 0, //公
    var height_metric: Int = 0,
    var weight_imperial: Int = 0, //英
    var height_imperial: Int = 0,
    var birthday: String? = null,
    var age: Int = 0,
    var gender: Int = 0, //0 女; 1男
    var unit: Int = 0, //0公 1英
    var customLevelNum: String? = null,
    var customInclineNum: String? = null,
    var totalDistance_metric: Double = 0.0,
    var totalDistance_imperial: Double = 0.0,
    var totalRun: Double = 0.0, //total workout
    var avgPaceInMonth: Double = 0.0,
    var sleepMode: Int = 0,
    
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    var levelDiagram: ByteArray? = null,
    
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    var inclineDiagram: ByteArray? = null,

    //Sole+ Data
    var soleAccountNo: String? = null,
    var soleAccount: String? = null,
    var solePassword: String? = null,
    var soleEmail: String? = null,
    var soleSyncPassword: String? = null,
    var soleRegistType: String? = null,
    var soleHeaderImgUrl: String? = null,
    var wattAccumulate: Double = 0.0,
    var wattFrequency: Int = 0,
    var workoutMonth: Int = 0,
    var avatarTag: Int = 0,
    var memberSince: Date? = null,
    var membershipExpirationDate: Date? = null,
    var isVip: Boolean = false
) : Parcelable {
    companion object {
        const val USER_PROFILE = "user_profile"
    }
}