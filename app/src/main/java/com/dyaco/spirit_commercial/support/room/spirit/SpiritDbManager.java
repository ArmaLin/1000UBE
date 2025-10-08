package com.dyaco.spirit_commercial.support.room.spirit;

import static com.dyaco.spirit_commercial.support.room.spirit.SpiritDatabase.MIGRATION_2_3;
import static com.dyaco.spirit_commercial.support.room.spirit.SpiritDatabase.MIGRATION_3_4;
import static com.dyaco.spirit_commercial.support.room.spirit.SpiritDatabase.MIGRATION_4_5;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Room;

import com.dyaco.spirit_commercial.App;
import com.dyaco.spirit_commercial.support.room.DatabaseCallback;
import com.dyaco.spirit_commercial.support.room.spirit.spirit_entity.DeviceEntity;
import com.dyaco.spirit_commercial.support.room.spirit.spirit_entity.EgymEntity;
import com.dyaco.spirit_commercial.support.room.spirit.spirit_entity.ErrorMsgEntity;
import com.dyaco.spirit_commercial.support.room.spirit.spirit_entity.MediaAppsEntity;
import com.dyaco.spirit_commercial.support.room.spirit.spirit_entity.RankEntity;
import com.dyaco.spirit_commercial.support.room.spirit.spirit_entity.UploadWorkoutDataEntity;
import com.dyaco.spirit_commercial.support.room.spirit.spirit_entity.UserProfileEntity;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableMaybeObserver;
import io.reactivex.schedulers.Schedulers;

public class SpiritDbManager {
    private static volatile SpiritDbManager instance;
    private final SpiritDatabase db;
    private static final String DB_NAME = "spirit_database";
    private Disposable mDisposable;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    public static SpiritDbManager getInstance(Context context) {
        if (instance == null) {
            synchronized (SpiritDatabase.class) {
                if (instance == null) {
                    instance = new SpiritDbManager(context);
                }
            }
        }
        return instance;
    }

    public SpiritDbManager(Context context) {
        db = Room.databaseBuilder(context, SpiritDatabase.class, DB_NAME)
                .addMigrations(MIGRATION_2_3) //升級
                .addMigrations(MIGRATION_2_3, MIGRATION_3_4) //升級
                .addMigrations(MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5) //升級
                // .fallbackToDestructiveMigration() //清空
//                .addMigrations(MIGRATION_1_2) //升級
//                .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                //  .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                .build();
    }

    public void insertUserProfile(final UserProfileEntity userProfileEntities,
                                  final DatabaseCallback<UserProfileEntity> databaseCallback) {
        //這裡使用Completable是因為數據庫處理完後不發射數據，只處理onComplete和onError事件
        AtomicLong rowId = new AtomicLong();
        Completable
                .fromAction(() -> rowId.set(db.spiritDao().insertUserProfile(userProfileEntities)))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        compositeDisposable.add(d);
                        //   mDisposable = d;
                        //別人把它清掉了
                    }

                    @Override
                    public void onComplete() {
                        databaseCallback.onAdded(rowId.get());
                        cancel();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        databaseCallback.onError(e.getMessage());
                    }
                });
    }

    public void updateUserProfile(final UserProfileEntity userProfileEntity,
                                  final DatabaseCallback<UserProfileEntity> callback) {
        Completable.fromAction(() -> {
                    App.getApp().setUserProfile(userProfileEntity);
                    db.spiritDao().updateUserProfile(userProfileEntity);
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onComplete() {
                        callback.onUpdated();
                        cancel();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        callback.onError(e.getMessage());
                    }
                });
    }

    public void deleteUserProfile(final int id, final DatabaseCallback<UserProfileEntity> databaseCallback) {
        /**
         * Completable
         * 適合用在執行的內容沒有回傳值，只要知道成功或失敗就好的時候，
         * 例如更新個人資料。在callback有onComplete()和onError(Throwable e)兩個方法，
         * 其中onComplete()是沒有參數的表示執行完不會有回傳值。
         */
        //建立我們要執行的內容。
        Completable
                .fromAction(() -> {
                    UserProfileEntity userProfileEntity = new UserProfileEntity();
                    userProfileEntity.setUid(id);
                    db.spiritDao().deleteUserProfile(userProfileEntity);
                })
                .subscribeOn(Schedulers.io()) //表示這些內容要在哪個thread執行，Schedulers.io()是RxJava提供的background thread之一
                .observeOn(AndroidSchedulers.mainThread()) //表示執行後的callback要在哪個thread執行
                .subscribe(new CompletableObserver() { //真的開始執行  - DisposableCompletableObserver
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onComplete() {
                        databaseCallback.onDeleted();
                        cancel();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        databaseCallback.onError(e.getMessage());
                    }
                });
    }


    /**
     * Spirit 模式的登入者，將 Workout 資料 存到資料庫
     */

    public void insertUploadWorkoutData(final UploadWorkoutDataEntity uploadWorkoutDataEntity,
                                        final DatabaseCallback<UploadWorkoutDataEntity> databaseCallback) {
        AtomicLong rowId = new AtomicLong();
        Completable
                .fromAction(() -> rowId.set(db.spiritDao().insertUploadWorkoutData(uploadWorkoutDataEntity)))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onComplete() {
                        databaseCallback.onAdded(rowId.get());
                        cancel();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        databaseCallback.onError(e.getMessage());
                    }
                });
    }

    public void getUploadWorkoutDataList(final DatabaseCallback<UploadWorkoutDataEntity> callback) {
        Disposable d = db.spiritDao()
                .getUploadWorkoutDataList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        callback::onDataLoadedList,
                        throwable -> callback.onError(throwable.getMessage()) // ✅ 加這行
                );

        compositeDisposable.add(d);
    }

    public void deleteUploadWorkoutData(final long id, final DatabaseCallback<UploadWorkoutDataEntity> databaseCallback) {
        Completable
                .fromAction(() -> {
                    UploadWorkoutDataEntity uploadWorkoutDataEntity = new UploadWorkoutDataEntity();
                    uploadWorkoutDataEntity.setUid(id);
                    db.spiritDao().deleteUploadWorkoutData(uploadWorkoutDataEntity);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onComplete() {
                        databaseCallback.onDeleted();
                        cancel();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        databaseCallback.onError(e.getMessage());
                    }
                });
    }


    public void insertErrorMsg(final ErrorMsgEntity errorMsgEntity,
                               final DatabaseCallback<ErrorMsgEntity> databaseCallback) {
        AtomicLong rowId = new AtomicLong();
        Completable
                .fromAction(() -> rowId.set(db.spiritDao().insertErrorMsg(errorMsgEntity)))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onComplete() {
                        databaseCallback.onAdded(rowId.get());
                        cancel();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        databaseCallback.onError(e.getMessage());
                    }
                });
    }

    public void getErrorMsgLit(final DatabaseCallback<ErrorMsgEntity> callback) {
        Disposable d = db.spiritDao()
                .getErrorMsgList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        callback::onDataLoadedList,
                        throwable -> callback.onError(throwable.getMessage()) // ✅ 加這行
                );

        compositeDisposable.add(d);
    }

    public void getErrorMsgByErrorCode(String errorCode, final DatabaseCallback<ErrorMsgEntity> callback) {
        Disposable d = db.spiritDao()
                .getErrorMsgByErrorCode(errorCode)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(callback::onDataLoadedBean);

        compositeDisposable.add(d);
    }


    public void deleteErrorMsg(final long id, final DatabaseCallback<ErrorMsgEntity> databaseCallback) {
        Completable
                .fromAction(() -> {
                    ErrorMsgEntity errorMsgEntity = new ErrorMsgEntity();
                    errorMsgEntity.setUid(id);
                    db.spiritDao().deleteErrorMsg(errorMsgEntity);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onComplete() {
                        databaseCallback.onDeleted();
                        cancel();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        databaseCallback.onError(e.getMessage());
                    }
                });
    }


    public void insertRankData(final RankEntity rankEntity,
                               final DatabaseCallback<RankEntity> databaseCallback) {
        AtomicLong rowId = new AtomicLong();
        Completable
                .fromAction(() -> rowId.set(db.spiritDao().insertRankData(rankEntity)))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onComplete() {
                        databaseCallback.onAdded(rowId.get());
                        cancel();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        databaseCallback.onError(e.getMessage());
                    }
                });
    }

    public void getRankLit(final DatabaseCallback<RankEntity> callback) {
        Disposable d = db.spiritDao()
                .getRankList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        callback::onDataLoadedList,
                        throwable -> callback.onError(throwable.getMessage()) // ✅ 加這行
                );

        compositeDisposable.add(d);
    }


    public void insertDeviceData(final DeviceEntity deviceEntity,
                                 final DatabaseCallback<DeviceEntity> databaseCallback) {
        AtomicLong rowId = new AtomicLong();
        Completable
                .fromAction(() -> rowId.set(db.spiritDao().insertDeviceDate(deviceEntity)))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onComplete() {
                        databaseCallback.onAdded(rowId.get());
                        cancel();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        databaseCallback.onError(e.getMessage());
                    }
                });
    }

    public void checkDevice(final DatabaseCallback<Integer> databaseCallback) {

        db.spiritDao().checkDevice()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new DisposableMaybeObserver<Integer>() {
                    @Override
                    public void onSuccess(@NonNull Integer id) {
                        databaseCallback.onCount(id);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                        databaseCallback.onNoData();
                    }
                });
    }

    public void getDeviceData(final DatabaseCallback<DeviceEntity> databaseCallback) {
        Disposable d = db.spiritDao()
                .getDeviceData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(databaseCallback::onDataLoadedBean);

        compositeDisposable.add(d);
    }

    public void updateDeviceData(final DeviceEntity deviceData,
                                 final DatabaseCallback<DeviceEntity> callback) {
        Completable.fromAction(() -> db.spiritDao()
                        .updateDeviceDate(deviceData))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onComplete() {
                        callback.onUpdated();
                        cancel();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        callback.onError(e.getMessage());
                    }
                });
    }

    public void deleteDeviceData(final int id, final DatabaseCallback<DeviceEntity> databaseCallback) {
        Completable.fromAction(() -> {
                    DeviceEntity deviceEntity = new DeviceEntity();
                    deviceEntity.setDeviceId(id);
                    db.spiritDao().deleteDeviceData(deviceEntity);
                })
                .subscribeOn(Schedulers.io()) //表示這些內容要在哪個thread執行，Schedulers.io()是RxJava提供的background thread之一
                .observeOn(AndroidSchedulers.mainThread()) //表示執行後的callback要在哪個thread執行
                .subscribe(new CompletableObserver() { //真的開始執行  - DisposableCompletableObserver
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onComplete() {
                        databaseCallback.onDeleted();
                        cancel();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        databaseCallback.onError(e.getMessage());
                    }
                });
    }


    public void clearTable() {
        db.clearAllTables();
    }


    public void close() {
        compositeDisposable.clear();
        db.close();
    }

    public void clear() {
        compositeDisposable.clear();
    }


    public void cancel() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
            //  Log.d("UPDATEEEEE", "insertUserProfile insertUserProfile: ");
        }
    }


    public void getMediaAppList(final DatabaseCallback<MediaAppsEntity> callback) {
        Disposable d = db.spiritDao()
                .getAllMediasAppList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(callback::onDataLoadedList);

        compositeDisposable.add(d);
    }


    public void getMediaAppByPackageName(String packageName, final DatabaseCallback<MediaAppsEntity> callback) {

        db.spiritDao().getMediasAppByPackageName(packageName)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new DisposableMaybeObserver<MediaAppsEntity>() {
                    @Override
                    public void onSuccess(@NonNull MediaAppsEntity mediaAppsEntity) {
                        callback.onDataLoadedBean(mediaAppsEntity);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        callback.onError(e.toString());
                    }

                    @Override
                    public void onComplete() {
                        callback.onNoData();
                    }
                });
    }


    public void insertMediaApp(final MediaAppsEntity mediaAppsEntity,
                               final DatabaseCallback<MediaAppsEntity> databaseCallback) {
        AtomicLong rowId = new AtomicLong();
        Completable
                .fromAction(() -> rowId.set(db.spiritDao().insertMediasEntity(mediaAppsEntity)))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onComplete() {
                        databaseCallback.onAdded(rowId.get());

                        cancel();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        databaseCallback.onError(e.getMessage());
                    }
                });
    }

    public void insertMediaAppList(List<MediaAppsEntity> mediaAppsEntityList, final DatabaseCallback<List<MediaAppsEntity>> databaseCallback) {

        Completable.fromAction(() -> db.spiritDao().insertMediaAppList(mediaAppsEntityList))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onComplete() {
                        databaseCallback.onUpdated();
                        cancel();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        databaseCallback.onError(e.getMessage());
                    }
                });
    }

    public void deleteMediaApp(final String packageName,
                               final DatabaseCallback<MediaAppsEntity> databaseCallback) {
        Completable
                .fromAction(() -> {
                    MediaAppsEntity historyEntity = new MediaAppsEntity();
                    historyEntity.setPackageName(packageName);
                    db.spiritDao().deleteMediaApp(historyEntity);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        //   compositeDisposable.add(d);
                    }

                    @Override
                    public void onComplete() {
                        databaseCallback.onDeleted();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        databaseCallback.onError(e.getMessage());
                    }
                });
    }


    public void deleteAllMediaApp(final DatabaseCallback<MediaAppsEntity> databaseCallback) {
        Completable
                .fromAction(() -> {
                    db.spiritDao().deleteAllMediaApp();
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        //   compositeDisposable.add(d);
                    }

                    @Override
                    public void onComplete() {
                        databaseCallback.onDeleted();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        databaseCallback.onError(e.getMessage());
                    }
                });
    }


    public void updateMediaApp(final MediaAppsEntity mediaAppsEntity,
                               final DatabaseCallback<MediaAppsEntity> callback) {
        Completable.fromAction(() -> db.spiritDao().updateMediaApp(mediaAppsEntity)).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        //   compositeDisposable.add(d);
                    }

                    @Override
                    public void onComplete() {
                        callback.onUpdated();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        callback.onError(e.getMessage());
                    }
                });
    }


    public void updateMediaAppSort(final String packageName, int sort,
                                   final DatabaseCallback<MediaAppsEntity> callback) {
        Completable.fromAction(() -> db.spiritDao().updateMediaAppSort(packageName, sort)).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        //   compositeDisposable.add(d);
                    }

                    @Override
                    public void onComplete() {
                        callback.onUpdated();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        callback.onError(e.getMessage());
                    }
                });
    }


    public void insertEgymData(final EgymEntity egymEntity,
                               final DatabaseCallback<EgymEntity> databaseCallback) {
        AtomicLong rowId = new AtomicLong();
        Completable
                .fromAction(() -> rowId.set(db.spiritDao().insertEgymData(egymEntity)))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onComplete() {
                        databaseCallback.onAdded(rowId.get());
                        cancel();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        databaseCallback.onError(e.getMessage());
                    }
                });
    }


    //讀取出來的檔案太大，有處理錯誤，就不會Crash //SpiritDbManagerR
    public void getEgymList(final DatabaseCallback<EgymEntity> callback) {
        Disposable d = db.spiritDao()
                .getEgymDataList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        callback::onDataLoadedList,
                        throwable -> callback.onError(throwable.getMessage()),
                        callback::onNoData
                );

        compositeDisposable.add(d);
    }


    // TODO: android.database.sqlite.SQLiteBlobTooBigException: Row too big to fit into CursorWindow requiredPos=0, totalRows=1
    //讀取出來的檔案太大，沒有處理錯誤，就會Crash
    public void getEgymList2(final DatabaseCallback<EgymEntity> callback) {
        Disposable d = db.spiritDao()
                .getEgymDataList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(callback::onDataLoadedList);

        compositeDisposable.add(d);
    }

    public void getEgymDataByUid(long uid, final DatabaseCallback<EgymEntity> callback) {
        Disposable d = db.spiritDao()
                .getEgymDataByUid(uid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(callback::onDataLoadedBean);

        compositeDisposable.add(d);
    }


    public void deleteEgymData(final long id, final DatabaseCallback<EgymEntity> databaseCallback) {
        Completable
                .fromAction(() -> {
                    EgymEntity egymEntity = new EgymEntity();
                    egymEntity.setUid(id);
                    db.spiritDao().deleteEgymData(egymEntity);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onComplete() {
                        databaseCallback.onDeleted();
                        cancel();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        databaseCallback.onError(e.getMessage());
                    }
                });
    }
}