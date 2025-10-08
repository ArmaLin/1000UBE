package com.dyaco.spirit_commercial.support.room.spirit.spirit_entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dyaco.spirit_commercial.App
import com.dyaco.spirit_commercial.support.CommonUtils.drawableToByteArray
import com.dyaco.spirit_commercial.support.mediaapp.MediaAppEnum

@Entity(tableName = MediaAppsEntity.MEDIA_APPS)
data class MediaAppsEntity(
    @PrimaryKey
    var packageName: String = "",
    var appName: String? = null,
    var mediaType: String? = null,
    var version: String? = null,
    var comment: String? = null,
    var gmsNeeded: String? = null,
    var forceUpdates: String? = null,
    var versionCode: Int = 0,
    var sort: Int = 0,
    var md5: String? = null,
    var downloadUrl: String? = null,
    var path: String? = null,
    var webUrl: String? = null,
    var updateTime: Long = 0,
    var isUpdate: Boolean = false,

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    var appIconS: ByteArray? = null,

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    var appIconM: ByteArray? = null,

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    var appIconL: ByteArray? = null
) {
    constructor(mediaAppEnum: MediaAppEnum, sortIndex: Int) : this(
        packageName = mediaAppEnum.appPackageName,
        appName = App.getApp().getString(mediaAppEnum.appName),
        mediaType = "APP",
        gmsNeeded = mediaAppEnum.isGmsNeed,
        forceUpdates = "NO",
        sort = sortIndex,
        webUrl = mediaAppEnum.appUrl,
        appIconM = drawableToByteArray(mediaAppEnum.appIcon),
    )

    constructor(
        packageName: String,
        appName: String?,
        mediaType: String?,
        version: String?,
        comment: String?,
        gmsNeeded: String?,
        forceUpdates: String?,
        versionCode: Int,
        sort: Int,
        md5: String?,
        downloadUrl: String?,
        path: String?,
        webUrl: String?,
        updateTime: Int,
        appIconS: ByteArray?,
        appIconM: ByteArray?,
        appIconL: ByteArray?
    ) : this(
        packageName = packageName,
        appName = appName,
        mediaType = mediaType,
        version = version,
        comment = comment,
        gmsNeeded = gmsNeeded,
        forceUpdates = forceUpdates,
        versionCode = versionCode,
        sort = sort,
        md5 = md5,
        downloadUrl = downloadUrl,
        path = path,
        webUrl = webUrl,
        updateTime = updateTime.toLong(),
        isUpdate = false,
        appIconS = appIconS,
        appIconM = appIconM,
        appIconL = appIconL
    )

    companion object {
        const val MEDIA_APPS = "media_apps_entity"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MediaAppsEntity

        if (versionCode != other.versionCode) return false
        if (sort != other.sort) return false
        if (updateTime != other.updateTime) return false
        if (isUpdate != other.isUpdate) return false
        if (packageName != other.packageName) return false
        if (appName != other.appName) return false
        if (mediaType != other.mediaType) return false
        if (version != other.version) return false
        if (comment != other.comment) return false
        if (gmsNeeded != other.gmsNeeded) return false
        if (forceUpdates != other.forceUpdates) return false
        if (md5 != other.md5) return false
        if (downloadUrl != other.downloadUrl) return false
        if (path != other.path) return false
        if (webUrl != other.webUrl) return false
        if (!appIconS.contentEquals(other.appIconS)) return false
        if (!appIconM.contentEquals(other.appIconM)) return false
        if (!appIconL.contentEquals(other.appIconL)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = versionCode
        result = 31 * result + sort
        result = 31 * result + updateTime.hashCode()
        result = 31 * result + isUpdate.hashCode()
        result = 31 * result + packageName.hashCode()
        result = 31 * result + (appName?.hashCode() ?: 0)
        result = 31 * result + (mediaType?.hashCode() ?: 0)
        result = 31 * result + (version?.hashCode() ?: 0)
        result = 31 * result + (comment?.hashCode() ?: 0)
        result = 31 * result + (gmsNeeded?.hashCode() ?: 0)
        result = 31 * result + (forceUpdates?.hashCode() ?: 0)
        result = 31 * result + (md5?.hashCode() ?: 0)
        result = 31 * result + (downloadUrl?.hashCode() ?: 0)
        result = 31 * result + (path?.hashCode() ?: 0)
        result = 31 * result + (webUrl?.hashCode() ?: 0)
        result = 31 * result + (appIconS?.contentHashCode() ?: 0)
        result = 31 * result + (appIconM?.contentHashCode() ?: 0)
        result = 31 * result + (appIconL?.contentHashCode() ?: 0)
        return result
    }
}