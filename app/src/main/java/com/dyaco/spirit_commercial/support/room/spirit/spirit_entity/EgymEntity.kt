package com.dyaco.spirit_commercial.support.room.spirit.spirit_entity;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = EgymEntity.EGYM_TABLE,
        indices = {@Index("uid")}
)
public class EgymEntity {
    public static final String EGYM_TABLE = "EGYM_TABLE";

    @PrimaryKey(autoGenerate = true)
    private long uid;
    private String workoutJson;
    private long updateTime;
    private boolean isUploaded;

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }


    public String getWorkoutJson() {
        return workoutJson;
    }

    public void setWorkoutJson(String workoutJson) {
        this.workoutJson = workoutJson;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public boolean isUploaded() {
        return isUploaded;
    }

    public void setUploaded(boolean uploaded) {
        isUploaded = uploaded;
    }

    @NonNull
    @Override
    public String toString() {
        return "EgymEntity{" +
                "uid=" + uid +
                ", workoutJson='" + workoutJson + '\'' +
                ", updateTime=" + updateTime +
                ", isUploaded=" + isUploaded +
                '}';
    }
}