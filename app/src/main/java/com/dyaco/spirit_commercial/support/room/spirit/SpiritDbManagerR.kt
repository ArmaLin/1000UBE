package com.dyaco.spirit_commercial.support.room.spirit

import android.content.Context
import androidx.room.Room
import com.dyaco.spirit_commercial.App
import com.dyaco.spirit_commercial.support.room.DatabaseCallback
import com.dyaco.spirit_commercial.support.room.spirit.SpiritDatabase.MIGRATION_2_3
import com.dyaco.spirit_commercial.support.room.spirit.SpiritDatabase.MIGRATION_3_4
import com.dyaco.spirit_commercial.support.room.spirit.SpiritDatabase.MIGRATION_4_5
import com.dyaco.spirit_commercial.support.room.spirit.spirit_entity.*
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.atomic.AtomicLong

class SpiritDbManagerR private constructor(context: Context) {

    // 使用 applicationContext 避免 Context 泄漏
    private val db = Room.databaseBuilder(
        context.applicationContext,
        SpiritDatabase::class.java,
        DB_NAME
    )
        .addMigrations(MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
        .build()

    companion object {
        private const val DB_NAME = "spirit_database"
        @Volatile
        private var instance: SpiritDbManagerR? = null

        @JvmStatic
        fun getInstance(context: Context): SpiritDbManagerR {
            return instance ?: synchronized(this) {
                instance ?: SpiritDbManagerR(context).also { instance = it }
            }
        }
    }

    // 內部 helper：不需儲存 Disposable，訂閱完成後即自動釋放
    @Suppress("CheckResult")
    private fun runCompletable(
        action: () -> Unit,
        onComplete: () -> Unit = {},
        onError: (Throwable) -> Unit = {}
    ) {
        Completable.fromAction { action() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ onComplete() }, { onError(it) })
    }

    @Suppress("CheckResult")
    private fun <T> runMaybe(
        maybe: Maybe<T>,
        onSuccess: (T) -> Unit,
        onError: (Throwable) -> Unit = {},
        onComplete: () -> Unit = {}
    ) {
        maybe.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ onSuccess(it) }, { onError(it) }, { onComplete() })
    }


    // 以下各方法均標記 @JvmOverloads 讓 Java 呼叫時可省略 callback 參數

    @JvmOverloads
    fun insertUserProfile(userProfile: UserProfileEntity, callback: DatabaseCallback<UserProfileEntity>? = null) {
        val rowId = AtomicLong()
        runCompletable({
            rowId.set(db.spiritDao().insertUserProfile(userProfile))
        }, {
            callback?.onAdded(rowId.get())
        }, { e ->
            callback?.onError(e.message ?: "")
        })
    }

    @JvmOverloads
    fun updateUserProfile(userProfile: UserProfileEntity, callback: DatabaseCallback<UserProfileEntity>? = null) {
        runCompletable({
            App.getApp().setUserProfile(userProfile)
            db.spiritDao().updateUserProfile(userProfile)
        }, {
            callback?.onUpdated()
        }, { e ->
            callback?.onError(e.message ?: "")
        })
    }

    @JvmOverloads
    fun deleteUserProfile(id: Int, callback: DatabaseCallback<UserProfileEntity>? = null) {
        runCompletable({
            UserProfileEntity().apply { uid = id.toLong() }
                .also { db.spiritDao().deleteUserProfile(it) }
        }, {
            callback?.onDeleted()
        }, { e ->
            callback?.onError(e.message ?: "")
        })
    }

    @JvmOverloads
    fun insertUploadWorkoutData(data: UploadWorkoutDataEntity, callback: DatabaseCallback<UploadWorkoutDataEntity>? = null) {
        val rowId = AtomicLong()
        runCompletable({
            rowId.set(db.spiritDao().insertUploadWorkoutData(data))
        }, {
            callback?.onAdded(rowId.get())
        }, { e ->
            callback?.onError(e.message ?: "")
        })
    }

    @JvmOverloads
    fun getUploadWorkoutDataList(callback: DatabaseCallback<UploadWorkoutDataEntity>? = null) {
        runMaybe(db.spiritDao().getUploadWorkoutDataList(), { list ->
            callback?.onDataLoadedList(list)
        }, { e ->
            callback?.onError(e.message ?: "")
        })
    }

    @JvmOverloads
    fun deleteUploadWorkoutData(id: Long, callback: DatabaseCallback<UploadWorkoutDataEntity>? = null) {
        runCompletable({
            UploadWorkoutDataEntity().apply { uid = id }
                .also { db.spiritDao().deleteUploadWorkoutData(it) }
        }, {
            callback?.onDeleted()
        }, { e ->
            callback?.onError(e.message ?: "")
        })
    }

    @JvmOverloads
    fun insertErrorMsg(errorMsg: ErrorMsgEntity, callback: DatabaseCallback<ErrorMsgEntity>? = null) {
        val rowId = AtomicLong()
        runCompletable({
            rowId.set(db.spiritDao().insertErrorMsg(errorMsg))
        }, {
            callback?.onAdded(rowId.get())
        }, { e ->
            callback?.onError(e.message ?: "")
        })
    }

    @JvmOverloads
    fun getErrorMsgList(callback: DatabaseCallback<ErrorMsgEntity>? = null) {
        runMaybe(db.spiritDao().getErrorMsgList(), { list ->
            callback?.onDataLoadedList(list)
        }, { e ->
            callback?.onError(e.message ?: "")
        })
    }

    @JvmOverloads
    fun getErrorMsgByErrorCode(errorCode: String, callback: DatabaseCallback<ErrorMsgEntity>? = null) {
        runMaybe(db.spiritDao().getErrorMsgByErrorCode(errorCode), { bean ->
            callback?.onDataLoadedBean(bean)
        }, { e ->
            callback?.onError(e.message ?: "")
        })
    }

    @JvmOverloads
    fun deleteErrorMsg(id: Long, callback: DatabaseCallback<ErrorMsgEntity>? = null) {
        runCompletable({
            ErrorMsgEntity().apply { uid = id }
                .also { db.spiritDao().deleteErrorMsg(it) }
        }, {
            callback?.onDeleted()
        }, { e ->
            callback?.onError(e.message ?: "")
        })
    }

    @JvmOverloads
    fun insertRankData(rank: RankEntity, callback: DatabaseCallback<RankEntity>? = null) {
        val rowId = AtomicLong()
        runCompletable({
            rowId.set(db.spiritDao().insertRankData(rank))
        }, {
            callback?.onAdded(rowId.get())
        }, { e ->
            callback?.onError(e.message ?: "")
        })
    }

    @JvmOverloads
    fun getRankList(callback: DatabaseCallback<RankEntity>? = null) {
        runMaybe(db.spiritDao().getRankList(), { list ->
            callback?.onDataLoadedList(list)
        }, { e ->
            callback?.onError(e.message ?: "")
        })
    }

    @JvmOverloads
    fun insertDeviceData(device: DeviceEntity, callback: DatabaseCallback<DeviceEntity>? = null) {
        val rowId = AtomicLong()
        runCompletable({
            rowId.set(db.spiritDao().insertDeviceDate(device))
        }, {
            callback?.onAdded(rowId.get())
        }, { e ->
            callback?.onError(e.message ?: "")
        })
    }

    @JvmOverloads
    fun checkDevice(callback: DatabaseCallback<Int>? = null) {
        runMaybe(db.spiritDao().checkDevice(), { count ->
            callback?.onCount(count)
        }, { /* 可選 onError 回傳 */ }, {
            callback?.onNoData()
        })
    }

    @JvmOverloads
    fun getDeviceData(callback: DatabaseCallback<DeviceEntity>? = null) {
        runMaybe(db.spiritDao().getDeviceData(), { bean ->
            callback?.onDataLoadedBean(bean)
        }, { e ->
            callback?.onError(e.message ?: "")
        })
    }

    @JvmOverloads
    fun updateDeviceData(device: DeviceEntity, callback: DatabaseCallback<DeviceEntity>? = null) {
        runCompletable({
            db.spiritDao().updateDeviceDate(device)
        }, {
            callback?.onUpdated()
        }, { e ->
            callback?.onError(e.message ?: "")
        })
    }

    @JvmOverloads
    fun deleteDeviceData(id: Int, callback: DatabaseCallback<DeviceEntity>? = null) {
        runCompletable({
            DeviceEntity().apply { deviceId = id.toLong() }
                .also { db.spiritDao().deleteDeviceData(it) }
        }, {
            callback?.onDeleted()
        }, { e ->
            callback?.onError(e.message ?: "")
        })
    }

    @JvmOverloads
    fun getMediaAppList(callback: DatabaseCallback<MediaAppsEntity>? = null) {
        runMaybe(db.spiritDao().getAllMediasAppList(), { list ->
            callback?.onDataLoadedList(list)
        }, { e ->
            callback?.onError(e.message ?: "")
        })
    }

    @JvmOverloads
    fun getMediaAppByPackageName(packageName: String, callback: DatabaseCallback<MediaAppsEntity>? = null) {
        runMaybe(db.spiritDao().getMediasAppByPackageName(packageName), { bean ->
            callback?.onDataLoadedBean(bean)
        }, { e ->
            callback?.onError(e.message ?: "")
        }, {
            callback?.onNoData()
        })
    }

    @JvmOverloads
    fun insertMediaApp(mediaApp: MediaAppsEntity, callback: DatabaseCallback<MediaAppsEntity>? = null) {
        val rowId = AtomicLong()
        runCompletable({
            rowId.set(db.spiritDao().insertMediasEntity(mediaApp))
        }, {
            callback?.onAdded(rowId.get())
        }, { e ->
            callback?.onError(e.message ?: "")
        })
    }

    @JvmOverloads
    fun insertMediaAppList(mediaAppList: List<MediaAppsEntity>, callback: DatabaseCallback<List<MediaAppsEntity>>? = null) {
        runCompletable({
            db.spiritDao().insertMediaAppList(mediaAppList)
        }, {
            callback?.onUpdated()
        }, { e ->
            callback?.onError(e.message ?: "")
        })
    }

    @JvmOverloads
    fun deleteMediaApp(packageName: String, callback: DatabaseCallback<MediaAppsEntity>? = null) {
        runCompletable({
            MediaAppsEntity().apply { this.packageName = packageName }
                .also { db.spiritDao().deleteMediaApp(it) }
        }, {
            callback?.onDeleted()
        }, { e ->
            callback?.onError(e.message ?: "")
        })
    }

    @JvmOverloads
    fun deleteAllMediaApp(callback: DatabaseCallback<MediaAppsEntity>? = null) {
        runCompletable({
            db.spiritDao().deleteAllMediaApp()
        }, {
            callback?.onDeleted()
        }, { e ->
            callback?.onError(e.message ?: "")
        })
    }

    @JvmOverloads
    fun updateMediaApp(mediaApp: MediaAppsEntity, callback: DatabaseCallback<MediaAppsEntity>? = null) {
        runCompletable({
            db.spiritDao().updateMediaApp(mediaApp)
        }, {
            callback?.onUpdated()
        }, { e ->
            callback?.onError(e.message ?: "")
        })
    }

    @JvmOverloads
    fun updateMediaAppSort(packageName: String, sort: Int, callback: DatabaseCallback<MediaAppsEntity>? = null) {
        runCompletable({
            db.spiritDao().updateMediaAppSort(packageName, sort)
        }, {
            callback?.onUpdated()
        }, { e ->
            callback?.onError(e.message ?: "")
        })
    }

    @JvmOverloads
    fun insertEgymData(egym: EgymEntity, callback: DatabaseCallback<EgymEntity>? = null) {
        val rowId = AtomicLong()
        runCompletable({
            rowId.set(db.spiritDao().insertEgymData(egym))
        }, {
            callback?.onAdded(rowId.get())
        }, { e ->
            callback?.onError(e.message ?: "")
        })
    }

    @JvmOverloads
    fun getEgymList(callback: DatabaseCallback<EgymEntity>? = null) {
        runMaybe(db.spiritDao().getEgymDataList(), { list ->
            callback?.onDataLoadedList(list)
        }, { e ->
            callback?.onError(e.message ?: "")
        })
    }

    @JvmOverloads
    fun getEgymDataByUid(uid: Long, callback: DatabaseCallback<EgymEntity>? = null) {
        runMaybe(db.spiritDao().getEgymDataByUid(uid), { bean ->
            callback?.onDataLoadedBean(bean)
        }, { e ->
            callback?.onError(e.message ?: "")
        })
    }

    @JvmOverloads
    fun deleteEgymData(id: Long, callback: DatabaseCallback<EgymEntity>? = null) {
        runCompletable({
            EgymEntity().apply { this.uid = id }
                .also { db.spiritDao().deleteEgymData(it) }
        }, {
            callback?.onDeleted()
        }, { e ->
            callback?.onError(e.message ?: "")
        })
    }

    fun clearTable() {
        runCompletable({ db.clearAllTables() })
    }
}
