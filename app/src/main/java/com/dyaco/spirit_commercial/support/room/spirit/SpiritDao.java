package com.dyaco.spirit_commercial.support.room.spirit;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.dyaco.spirit_commercial.support.room.spirit.spirit_entity.DeviceEntity;
import com.dyaco.spirit_commercial.support.room.spirit.spirit_entity.EgymEntity;
import com.dyaco.spirit_commercial.support.room.spirit.spirit_entity.ErrorMsgEntity;
import com.dyaco.spirit_commercial.support.room.spirit.spirit_entity.MediaAppsEntity;
import com.dyaco.spirit_commercial.support.room.spirit.spirit_entity.RankEntity;
import com.dyaco.spirit_commercial.support.room.spirit.spirit_entity.UploadWorkoutDataEntity;
import com.dyaco.spirit_commercial.support.room.spirit.spirit_entity.UserProfileEntity;

import java.util.List;

import io.reactivex.Maybe;

@Dao
public interface SpiritDao {
    @Query("select * from " + UserProfileEntity.USER_PROFILE + " where userType = 1")
    Maybe<List<UserProfileEntity>> getUserProfiles();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertUserProfile(UserProfileEntity userProfileEntity);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertUploadWorkoutData(UploadWorkoutDataEntity uploadWorkoutDataEntity);

    @Delete
    void deleteUploadWorkoutData(UploadWorkoutDataEntity uploadWorkoutDataEntity);

    @Transaction
    @Query("SELECT * FROM " + UploadWorkoutDataEntity.UPLOAD_WORKOUT_DATA)
    Maybe<List<UploadWorkoutDataEntity>> getUploadWorkoutDataList();


    @Update
    void updateUserProfile(UserProfileEntity userProfileEntity);

    @Query("select * from " + UserProfileEntity.USER_PROFILE + " where userType = 0")
    Maybe<List<UserProfileEntity>> getUserProfilesGuest();

    @Delete
    void deleteUserProfile(UserProfileEntity userProfileEntity);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertErrorMsg(ErrorMsgEntity errorMsgEntity);

    @Transaction
    @Query("SELECT * FROM " + ErrorMsgEntity.ERROR_MSG + " ORDER BY errorDate DESC")
    Maybe<List<ErrorMsgEntity>> getErrorMsgList();

    @Query("SELECT * FROM " + ErrorMsgEntity.ERROR_MSG + " where errorCode = :errorCode ORDER BY errorDate DESC")
    Maybe<ErrorMsgEntity> getErrorMsgByErrorCode(String errorCode);


    @Delete
    void deleteErrorMsg(ErrorMsgEntity errorMsgEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertRankData(RankEntity rankEntity);

    @Transaction
    @Query("SELECT * FROM " + RankEntity.RANKS + " ORDER BY memberNo DESC")
    Maybe<List<RankEntity>> getRankList();


    @Update
    void updateDeviceDate(DeviceEntity deviceEntity);

    @Query("SELECT deviceId FROM " + DeviceEntity.DEVICE + " where deviceId = 0")
    Maybe<Integer> checkDevice();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertDeviceDate(DeviceEntity deviceEntity);

    @Query("select * from " + DeviceEntity.DEVICE + " where deviceId = 0")
    Maybe<DeviceEntity> getDeviceData();

    @Delete
    void deleteDeviceData(DeviceEntity deviceEntity);




    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertMediasEntity(MediaAppsEntity mediaAppsEntity);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMediaAppList(List<MediaAppsEntity> mediaAppsEntityList);

    @Transaction
    @Query("SELECT * FROM " + MediaAppsEntity.MEDIA_APPS + " where packageName = :packageName ORDER BY sort DESC")
    Maybe<MediaAppsEntity> getMediasAppByPackageName(String packageName);

    @Transaction
    @Query("SELECT * FROM " + MediaAppsEntity.MEDIA_APPS + " ORDER BY sort DESC")
    Maybe<List<MediaAppsEntity>> getAllMediasAppList();

    @Delete
    void deleteMediaApp(MediaAppsEntity mediaAppsEntity);

    @Query("DELETE FROM media_apps_entity")
    void deleteAllMediaApp();

    @Update
    void updateMediaApp(MediaAppsEntity mediaAppsEntity);


    @Query("UPDATE media_apps_entity set sort = :sort where packageName = :packageName")
    void updateMediaAppSort(String packageName,int sort);




    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertEgymData(EgymEntity egymEntity);

    @Transaction
    @Query("SELECT * FROM " + EgymEntity.EGYM_TABLE + " ORDER BY updateTime DESC")
    Maybe<List<EgymEntity>> getEgymDataList();

    @Query("SELECT * FROM " + EgymEntity.EGYM_TABLE + " where uid = :uid ORDER BY uid DESC")
    Maybe<EgymEntity> getEgymDataByUid(long uid);


    @Delete
    void deleteEgymData(EgymEntity egymEntity);
}
