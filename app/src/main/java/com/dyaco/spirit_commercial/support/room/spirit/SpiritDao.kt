package com.dyaco.spirit_commercial.support.room.spirit

import androidx.room.*
import com.dyaco.spirit_commercial.support.room.spirit.spirit_entity.*
import io.reactivex.Maybe

@Dao
interface SpiritDao {
    @Query("select * from " + UserProfileEntity.USER_PROFILE + " where userType = 1")
    fun getUserProfiles(): Maybe<List<UserProfileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUserProfile(userProfileEntity: UserProfileEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUploadWorkoutData(uploadWorkoutDataEntity: UploadWorkoutDataEntity): Long

    @Delete
    fun deleteUploadWorkoutData(uploadWorkoutDataEntity: UploadWorkoutDataEntity)

    @Transaction
    @Query("SELECT * FROM " + UploadWorkoutDataEntity.UPLOAD_WORKOUT_DATA)
    fun getUploadWorkoutDataList(): Maybe<List<UploadWorkoutDataEntity>>

    @Update
    fun updateUserProfile(userProfileEntity: UserProfileEntity)

    @Query("select * from " + UserProfileEntity.USER_PROFILE + " where userType = 0")
    fun getUserProfilesGuest(): Maybe<List<UserProfileEntity>>

    @Delete
    fun deleteUserProfile(userProfileEntity: UserProfileEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertErrorMsg(errorMsgEntity: ErrorMsgEntity): Long

    @Transaction
    @Query("SELECT * FROM " + ErrorMsgEntity.ERROR_MSG + " ORDER BY errorDate DESC")
    fun getErrorMsgList(): Maybe<List<ErrorMsgEntity>>

    @Query("SELECT * FROM " + ErrorMsgEntity.ERROR_MSG + " where errorCode = :errorCode ORDER BY errorDate DESC")
    fun getErrorMsgByErrorCode(errorCode: String): Maybe<ErrorMsgEntity>

    @Delete
    fun deleteErrorMsg(errorMsgEntity: ErrorMsgEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRankData(rankEntity: RankEntity): Long

    @Transaction
    @Query("SELECT * FROM " + RankEntity.RANKS + " ORDER BY memberNo DESC")
    fun getRankList(): Maybe<List<RankEntity>>

    @Update
    fun updateDeviceDate(deviceEntity: DeviceEntity)

    @Query("SELECT deviceId FROM " + DeviceEntity.DEVICE + " where deviceId = 0")
    fun checkDevice(): Maybe<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDeviceDate(deviceEntity: DeviceEntity): Long

    @Query("select * from " + DeviceEntity.DEVICE + " where deviceId = 0")
    fun getDeviceData(): Maybe<DeviceEntity>

    @Delete
    fun deleteDeviceData(deviceEntity: DeviceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMediasEntity(mediaAppsEntity: MediaAppsEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMediaAppList(mediaAppsEntityList: List<MediaAppsEntity>)

    @Transaction
    @Query("SELECT * FROM " + MediaAppsEntity.MEDIA_APPS + " where packageName = :packageName ORDER BY sort DESC")
    fun getMediasAppByPackageName(packageName: String): Maybe<MediaAppsEntity>

    @Transaction
    @Query("SELECT * FROM " + MediaAppsEntity.MEDIA_APPS + " ORDER BY sort DESC")
    fun getAllMediasAppList(): Maybe<List<MediaAppsEntity>>

    @Delete
    fun deleteMediaApp(mediaAppsEntity: MediaAppsEntity)

    @Query("DELETE FROM media_apps_entity")
    fun deleteAllMediaApp()

    @Update
    fun updateMediaApp(mediaAppsEntity: MediaAppsEntity)

    @Query("UPDATE media_apps_entity set sort = :sort where packageName = :packageName")
    fun updateMediaAppSort(packageName: String, sort: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEgymData(egymEntity: EgymEntity): Long

    @Transaction
    @Query("SELECT * FROM " + EgymEntity.EGYM_TABLE + " ORDER BY updateTime DESC")
    fun getEgymDataList(): Maybe<List<EgymEntity>>

    @Query("SELECT * FROM " + EgymEntity.EGYM_TABLE + " where uid = :uid ORDER BY uid DESC")
    fun getEgymDataByUid(uid: Long): Maybe<EgymEntity>

    @Delete
    fun deleteEgymData(egymEntity: EgymEntity)
}