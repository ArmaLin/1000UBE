package com.dyaco.spirit_commercial.support.room.spirit

import android.content.Context
import androidx.room.Room
import com.dyaco.spirit_commercial.App
import com.dyaco.spirit_commercial.support.room.DatabaseCallback
import com.dyaco.spirit_commercial.support.room.spirit.spirit_entity.*
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.atomic.AtomicLong

class SpiritDbManager private constructor(context: Context) {

    private val db: SpiritDatabase = Room.databaseBuilder(
        context.applicationContext, // 使用 applicationContext 防止內存洩漏
        SpiritDatabase::class.java,
        DB_NAME
    ).build()

    private val compositeDisposable = CompositeDisposable()

    companion object {
        private const val DB_NAME = "spirit_database"
        @Volatile
        private var instance: SpiritDbManager? = null

        @JvmStatic // 關鍵！讓 Java 可以像呼叫靜態方法一樣呼叫 getInstance
        fun getInstance(context: Context): SpiritDbManager {
            return instance ?: synchronized(this) {
                instance ?: SpiritDbManager(context).also { instance = it }
            }
        }
    }

    // --- 內部輔助方法，簡化重複的 RxJava 程式碼 ---

    @Suppress("CheckResult")
    private fun runInsertCompletable(
        action: () -> Long,
        onSuccess: (Long) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val rowId = AtomicLong()
        Completable.fromAction { rowId.set(action()) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ onSuccess(rowId.get()) }, { onError(it) })
            .also { compositeDisposable.add(it) }
    }

    @Suppress("CheckResult")
    private fun runActionCompletable(
        action: () -> Unit,
        onComplete: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        Completable.fromAction { action() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ onComplete() }, { onError(it) })
            .also { compositeDisposable.add(it) }
    }

    @Suppress("CheckResult")
    private fun <T : Any> runMaybe(
        maybe: Maybe<T>,
        onSuccess: (T) -> Unit,
        onError: (Throwable) -> Unit,
        onComplete: () -> Unit = {}
    ) {
        maybe.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ onSuccess(it) }, { onError(it) }, { onComplete() })
            .also { compositeDisposable.add(it) }
    }

    // --- UserProfile ---

    @JvmOverloads
    fun insertUserProfile(userProfile: UserProfileEntity, callback: DatabaseCallback<UserProfileEntity>? = null) {
        runInsertCompletable(
            action = { db.spiritDao().insertUserProfile(userProfile) },
            onSuccess = { rowId -> callback?.onAdded(rowId) },
            onError = { e -> callback?.onError(e.message ?: "Error") }
        )
    }

    @JvmOverloads
    fun updateUserProfile(userProfile: UserProfileEntity, callback: DatabaseCallback<UserProfileEntity>? = null) {
        runActionCompletable(
            action = {
                App.getApp().setUserProfile(userProfile)
                db.spiritDao().updateUserProfile(userProfile)
            },
            onComplete = { callback?.onUpdated() },
            onError = { e -> callback?.onError(e.message ?: "Error") }
        )
    }

    @JvmOverloads
    fun deleteUserProfile(id: Int, callback: DatabaseCallback<UserProfileEntity>? = null) {
        runActionCompletable(
            action = { db.spiritDao().deleteUserProfile(UserProfileEntity().apply { uid = id.toLong() }) },
            onComplete = { callback?.onDeleted() },
            onError = { e -> callback?.onError(e.message ?: "Error") }
        )
    }

    // --- UploadWorkoutData ---

    @JvmOverloads
    fun insertUploadWorkoutData(data: UploadWorkoutDataEntity, callback: DatabaseCallback<UploadWorkoutDataEntity>? = null) {
        runInsertCompletable(
            action = { db.spiritDao().insertUploadWorkoutData(data) },
            onSuccess = { rowId -> callback?.onAdded(rowId) },
            onError = { e -> callback?.onError(e.message ?: "Error") }
        )
    }

    @JvmOverloads
    fun getUploadWorkoutDataList(callback: DatabaseCallback<UploadWorkoutDataEntity>? = null) {
        runMaybe(
            maybe = db.spiritDao().getUploadWorkoutDataList(),
            onSuccess = { list -> callback?.onDataLoadedList(list) },
            onError = { e -> callback?.onError(e.message ?: "Error") }
        )
    }

    @JvmOverloads
    fun deleteUploadWorkoutData(id: Long, callback: DatabaseCallback<UploadWorkoutDataEntity>? = null) {
        runActionCompletable(
            action = { db.spiritDao().deleteUploadWorkoutData(UploadWorkoutDataEntity().apply { uid = id }) },
            onComplete = { callback?.onDeleted() },
            onError = { e -> callback?.onError(e.message ?: "Error") }
        )
    }

    // --- ErrorMsg ---

    @JvmOverloads
    fun insertErrorMsg(errorMsg: ErrorMsgEntity, callback: DatabaseCallback<ErrorMsgEntity>? = null) {
        runInsertCompletable(
            action = { db.spiritDao().insertErrorMsg(errorMsg) },
            onSuccess = { rowId -> callback?.onAdded(rowId) },
            onError = { e -> callback?.onError(e.message ?: "Error") }
        )
    }

    @JvmOverloads
    fun getErrorMsgList(callback: DatabaseCallback<ErrorMsgEntity>? = null) {
        runMaybe(
            maybe = db.spiritDao().getErrorMsgList(),
            onSuccess = { list -> callback?.onDataLoadedList(list) },
            onError = { e -> callback?.onError(e.message ?: "Error") }
        )
    }

    @JvmOverloads
    fun getErrorMsgByErrorCode(errorCode: String, callback: DatabaseCallback<ErrorMsgEntity>? = null) {
        runMaybe(
            maybe = db.spiritDao().getErrorMsgByErrorCode(errorCode),
            onSuccess = { bean -> callback?.onDataLoadedBean(bean) },
            onError = { e -> callback?.onError(e.message ?: "Error") }
        )
    }

    @JvmOverloads
    fun deleteErrorMsg(id: Long, callback: DatabaseCallback<ErrorMsgEntity>? = null) {
        runActionCompletable(
            action = { db.spiritDao().deleteErrorMsg(ErrorMsgEntity().apply { uid = id }) },
            onComplete = { callback?.onDeleted() },
            onError = { e -> callback?.onError(e.message ?: "Error") }
        )
    }

    // --- RankData ---

    @JvmOverloads
    fun insertRankData(rank: RankEntity, callback: DatabaseCallback<RankEntity>? = null) {
        runInsertCompletable(
            action = { db.spiritDao().insertRankData(rank) },
            onSuccess = { rowId -> callback?.onAdded(rowId) },
            onError = { e -> callback?.onError(e.message ?: "Error") }
        )
    }

    @JvmOverloads
    fun getRankList(callback: DatabaseCallback<RankEntity>? = null) {
        runMaybe(
            maybe = db.spiritDao().getRankList(),
            onSuccess = { list -> callback?.onDataLoadedList(list) },
            onError = { e -> callback?.onError(e.message ?: "Error") }
        )
    }

    // --- DeviceData ---

    @JvmOverloads
    fun insertDeviceData(device: DeviceEntity, callback: DatabaseCallback<DeviceEntity>? = null) {
        runInsertCompletable(
            action = { db.spiritDao().insertDeviceDate(device) },
            onSuccess = { rowId -> callback?.onAdded(rowId) },
            onError = { e -> callback?.onError(e.message ?: "Error") }
        )
    }

    @JvmOverloads
    fun checkDevice(callback: DatabaseCallback<Int>? = null) {
        runMaybe(
            maybe = db.spiritDao().checkDevice(),
            onSuccess = { id -> callback?.onCount(id) },
            onError = { e -> callback?.onError(e.message ?: "Error") },
            onComplete = { callback?.onNoData() }
        )
    }

    @JvmOverloads
    fun getDeviceData(callback: DatabaseCallback<DeviceEntity>? = null) {
        runMaybe(
            maybe = db.spiritDao().getDeviceData(),
            onSuccess = { bean -> callback?.onDataLoadedBean(bean) },
            onError = { e -> callback?.onError(e.message ?: "Error") }
        )
    }

    @JvmOverloads
    fun updateDeviceData(device: DeviceEntity, callback: DatabaseCallback<DeviceEntity>? = null) {
        runActionCompletable(
            action = { db.spiritDao().updateDeviceDate(device) },
            onComplete = { callback?.onUpdated() },
            onError = { e -> callback?.onError(e.message ?: "Error") }
        )
    }

    @JvmOverloads
    fun deleteDeviceData(id: Int, callback: DatabaseCallback<DeviceEntity>? = null) {
        runActionCompletable(
            action = { db.spiritDao().deleteDeviceData(DeviceEntity().apply { deviceId = id.toLong() }) },
            onComplete = { callback?.onDeleted() },
            onError = { e -> callback?.onError(e.message ?: "Error") }
        )
    }

    // --- MediaApp ---

    @JvmOverloads
    fun getMediaAppList(callback: DatabaseCallback<MediaAppsEntity>? = null) {
        runMaybe(
            maybe = db.spiritDao().getAllMediasAppList(),
            onSuccess = { list -> callback?.onDataLoadedList(list) },
            onError = { e -> callback?.onError(e.message ?: "Error") }
        )
    }

    @JvmOverloads
    fun getMediaAppByPackageName(packageName: String, callback: DatabaseCallback<MediaAppsEntity>? = null) {
        runMaybe(
            maybe = db.spiritDao().getMediasAppByPackageName(packageName),
            onSuccess = { bean -> callback?.onDataLoadedBean(bean) },
            onError = { e -> callback?.onError(e.message ?: "Error") },
            onComplete = { callback?.onNoData() }
        )
    }

    @JvmOverloads
    fun insertMediaApp(mediaApp: MediaAppsEntity, callback: DatabaseCallback<MediaAppsEntity>? = null) {
        runInsertCompletable(
            action = { db.spiritDao().insertMediasEntity(mediaApp) },
            onSuccess = { rowId -> callback?.onAdded(rowId) },
            onError = { e -> callback?.onError(e.message ?: "Error") }
        )
    }

    @JvmOverloads
    fun insertMediaAppList(mediaAppList: List<MediaAppsEntity>, callback: DatabaseCallback<List<MediaAppsEntity>>? = null) {
        runActionCompletable(
            action = { db.spiritDao().insertMediaAppList(mediaAppList) },
            onComplete = { callback?.onUpdated() },
            onError = { e -> callback?.onError(e.message ?: "Error") }
        )
    }

    @JvmOverloads
    fun deleteMediaApp(packageName: String, callback: DatabaseCallback<MediaAppsEntity>? = null) {
        runActionCompletable(
            action = { db.spiritDao().deleteMediaApp(MediaAppsEntity().apply { this.packageName = packageName }) },
            onComplete = { callback?.onDeleted() },
            onError = { e -> callback?.onError(e.message ?: "Error") }
        )
    }

    @JvmOverloads
    fun deleteAllMediaApp(callback: DatabaseCallback<MediaAppsEntity>? = null) {
        runActionCompletable(
            action = { db.spiritDao().deleteAllMediaApp() },
            onComplete = { callback?.onDeleted() },
            onError = { e -> callback?.onError(e.message ?: "Error") }
        )
    }

    @JvmOverloads
    fun updateMediaApp(mediaApp: MediaAppsEntity, callback: DatabaseCallback<MediaAppsEntity>? = null) {
        runActionCompletable(
            action = { db.spiritDao().updateMediaApp(mediaApp) },
            onComplete = { callback?.onUpdated() },
            onError = { e -> callback?.onError(e.message ?: "Error") }
        )
    }

    @JvmOverloads
    fun updateMediaAppSort(packageName: String, sort: Int, callback: DatabaseCallback<MediaAppsEntity>? = null) {
        runActionCompletable(
            action = { db.spiritDao().updateMediaAppSort(packageName, sort) },
            onComplete = { callback?.onUpdated() },
            onError = { e -> callback?.onError(e.message ?: "Error") }
        )
    }


    // --- Egym ---

    @JvmOverloads
    fun insertEgymData(egym: EgymEntity, callback: DatabaseCallback<EgymEntity>? = null) {
        runInsertCompletable(
            action = { db.spiritDao().insertEgymData(egym) },
            onSuccess = { rowId -> callback?.onAdded(rowId) },
            onError = { e -> callback?.onError(e.message ?: "Error") }
        )
    }

    @JvmOverloads
    fun getEgymList(callback: DatabaseCallback<EgymEntity>? = null) {
        runMaybe(
            maybe = db.spiritDao().getEgymDataList(),
            onSuccess = { list -> callback?.onDataLoadedList(list) },
            onError = { e -> callback?.onError(e.message ?: "Error") }
        )
    }

    @JvmOverloads
    fun getEgymDataByUid(uid: Long, callback: DatabaseCallback<EgymEntity>? = null) {
        runMaybe(
            maybe = db.spiritDao().getEgymDataByUid(uid),
            onSuccess = { bean -> callback?.onDataLoadedBean(bean) },
            onError = { e -> callback?.onError(e.message ?: "Error") }
        )
    }

    @JvmOverloads
    fun deleteEgymData(id: Long, callback: DatabaseCallback<EgymEntity>? = null) {
        runActionCompletable(
            action = { db.spiritDao().deleteEgymData(EgymEntity().apply { this.uid = id }) },
            onComplete = { callback?.onDeleted() },
            onError = { e -> callback?.onError(e.message ?: "Error") }
        )
    }

    // --- 管理方法 ---

    fun clearTable() {
        runActionCompletable(
            action = { db.clearAllTables() },
            onComplete = {},
            onError = {}
        )
    }

    fun clearDisposables() {
        compositeDisposable.clear()
    }

    fun closeDb() {
        if (db.isOpen) {
            db.close()
        }
        clearDisposables()
    }

}