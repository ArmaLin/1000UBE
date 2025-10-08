package com.dyaco.spirit_commercial.support.room.spirit.spirit_entity;


import static com.dyaco.spirit_commercial.App.getApp;
import static com.dyaco.spirit_commercial.support.CommonUtils.drawableToByteArray;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.dyaco.spirit_commercial.support.mediaapp.MediaAppEnum;

import java.util.Arrays;

//@Entity(
//        tableName = UploadWorkoutDataEntity.UPLOAD_WORKOUT_DATA,
//        indices = {@Index("uid")}
//)

@Entity(
        tableName = MediaAppsEntity.MEDIA_APPS
)

public class MediaAppsEntity {
    public static final String MEDIA_APPS = "media_apps_entity";
  //  @PrimaryKey(autoGenerate = true)
  //  private long uid;
    @PrimaryKey
    @NonNull
    private String packageName = "";
    private String appName;
    private String mediaType;
    private String version;
    private String comment;
    private String gmsNeeded;
    private String forceUpdates;
    private int versionCode;
    private int sort;
    private String md5;
    private String downloadUrl;
    private String path;
    private String webUrl;
    private long updateTime;
    private boolean isUpdate;

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    private byte[] appIconS;

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    private byte[] appIconM;

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    private byte[] appIconL;

//    public long getUid() {
//        return uid;
//    }
//
//    public void setUid(long uid) {
//        this.uid = uid;
//    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    @NonNull
    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(@NonNull String packageName) {
        this.packageName = packageName;
    }

    public byte[] getAppIconS() {
        return appIconS;
    }

    public void setAppIconS(byte[] appIconS) {
        this.appIconS = appIconS;
    }

    public byte[] getAppIconM() {
        return appIconM;
    }

    public void setAppIconM(byte[] appIconM) {
        this.appIconM = appIconM;
    }

    public byte[] getAppIconL() {
        return appIconL;
    }

    public void setAppIconL(byte[] appIconL) {
        this.appIconL = appIconL;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getGmsNeeded() {
        return gmsNeeded;
    }

    public void setGmsNeeded(String gmsNeeded) {
        this.gmsNeeded = gmsNeeded;
    }

    public String getForceUpdates() {
        return forceUpdates;
    }

    public void setForceUpdates(String forceUpdates) {
        this.forceUpdates = forceUpdates;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public boolean isUpdate() {
        return isUpdate;
    }

    public void setUpdate(boolean update) {
        isUpdate = update;
    }

    @NonNull
    @Override
    public String toString() {
        return "MediaAppsEntity{" +
                "packageName='" + packageName + '\'' +
                ", appName='" + appName + '\'' +
                ", mediaType='" + mediaType + '\'' +
                ", version='" + version + '\'' +
                ", comment='" + comment + '\'' +
                ", gmsNeeded='" + gmsNeeded + '\'' +
                ", forceUpdates='" + forceUpdates + '\'' +
                ", versionCode=" + versionCode +
                ", sort=" + sort +
                ", md5='" + md5 + '\'' +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", path='" + path + '\'' +
                ", webUrl='" + webUrl + '\'' +
                ", updateTime=" + updateTime +
                ", isUpdate=" + isUpdate +
                ", appIconS=" + Arrays.toString(appIconS) +
                ", appIconM=" + Arrays.toString(appIconM) +
                ", appIconL=" + Arrays.toString(appIconL) +
                '}';
    }

    public MediaAppsEntity() {
    }

    public MediaAppsEntity(MediaAppEnum mediaAppEnum, int i) {
        this.packageName = mediaAppEnum.getAppPackageName();
        this.appName = getApp().getString(mediaAppEnum.getAppName());
        this.mediaType = "APP";
        this.version = "";
        this.comment = getApp().getString(mediaAppEnum.getAppName());
        this.gmsNeeded = mediaAppEnum.getIsGmsNeed();
        this.forceUpdates = "NO";
        this.versionCode = 0;
        this.sort = i;
        this.md5 = "";
        this.downloadUrl = "";
        this.path = "";
        this.webUrl = mediaAppEnum.getAppUrl();
        this.updateTime = 0;
        this.appIconS = new byte[0];
        this.appIconM = drawableToByteArray(mediaAppEnum.getAppIcon());
        this.appIconL = new byte[0];
        this.isUpdate = false;
    }

    @Ignore
    public MediaAppsEntity(MediaAppEnum mediaAppEnum) {
        this.packageName = mediaAppEnum.getAppPackageName();
        this.appName = getApp().getString(mediaAppEnum.getAppName());
        this.mediaType = "APP";
        this.version = "";
        this.comment = getApp().getString(mediaAppEnum.getAppName());
        this.gmsNeeded = mediaAppEnum.getIsGmsNeed();
        this.forceUpdates = "NO";
        this.versionCode = 0;
        this.sort = 0;
        this.md5 = "";
        this.downloadUrl = "";
        this.path = "";
        this.webUrl = mediaAppEnum.getAppUrl();
        this.updateTime = 0;
        this.appIconS = new byte[0];
        this.appIconM = drawableToByteArray(mediaAppEnum.getAppIcon());
        this.appIconL = new byte[0];
        this.isUpdate = false;
    }

    public MediaAppsEntity(@NonNull String packageName, String appName, String mediaType, String version, String comment, String gmsNeeded, String forceUpdates, int versionCode, int sort, String md5, String downloadUrl, String path, String webUrl, long updateTime, byte[] appIconS, byte[] appIconM, byte[] appIconL) {
        this.packageName = packageName;
        this.appName = appName;
        this.mediaType = mediaType;
        this.version = version;
        this.comment = comment;
        this.gmsNeeded = gmsNeeded;
        this.forceUpdates = forceUpdates;
        this.versionCode = versionCode;
        this.sort = sort;
        this.md5 = md5;
        this.downloadUrl = downloadUrl;
        this.path = path;
        this.webUrl = webUrl;
        this.updateTime = updateTime;
        this.appIconS = appIconS;
        this.appIconM = appIconM;
        this.appIconL = appIconL;
        this.isUpdate = false;
    }
}