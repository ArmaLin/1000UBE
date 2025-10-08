package com.dyaco.spirit_commercial.support.room.spirit.spirit_entity;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

//@Entity(
//        tableName = UploadWorkoutDataEntity.UPLOAD_WORKOUT_DATA,
//        indices = {@Index("uid")}
//)

@Entity(
        tableName = UploadWorkoutDataEntity.UPLOAD_WORKOUT_DATA
)

//@Entity(
//        tableName = UploadWorkoutDataEntity.UPLOAD_WORKOUT_DATA
//        indices = {@Index(value = {"dataJson", "isUploaded"},
//        unique = false)})
public class
UploadWorkoutDataEntity {
    public static final String UPLOAD_WORKOUT_DATA = "upload_workout_data";

    @PrimaryKey(autoGenerate = true)
    private long uid;
    private String dataJson;

    private boolean isUploaded;


    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getDataJson() {
        return dataJson;
    }

    public void setDataJson(String dataJson) {
        this.dataJson = dataJson;
    }

    public boolean isUploaded() {
        return isUploaded;
    }

    public void setUploaded(boolean uploaded) {
        isUploaded = uploaded;
    }
}