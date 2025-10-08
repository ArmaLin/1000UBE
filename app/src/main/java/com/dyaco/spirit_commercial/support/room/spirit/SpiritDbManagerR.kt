package com.dyaco.spirit_commercial.support.room.spirit

import android.content.Context
import androidx.room.Room
import com.dyaco.spirit_commercial.App
import com.dyaco.spirit_commercial.support.room.DatabaseCallback
import com.dyaco.spirit_commercial.support.room.spirit.spirit_entity.*
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.atomic.AtomicLong

class SpiritDbManagerR private constructor(context: Context) {

    private val db = Room.databaseBuilder(
        context.applicationContext,
        SpiritDatabase::class.java,
        DB_NAME
    )
        .addMigrations()
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


    @JvmOverloads
    fun getMediaAppList(callback: DatabaseCallback<MediaAppsEntity>? = null) {
        runMaybe(db.spiritDao().getAllMediasAppList(), { list ->
            callback?.onDataLoadedList(list)
        }, { e ->
            callback?.onError(e.message ?: "")
        })
    }


}
