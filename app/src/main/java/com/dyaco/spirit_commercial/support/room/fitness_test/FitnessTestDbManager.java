package com.dyaco.spirit_commercial.support.room.fitness_test;

import android.content.Context;

import androidx.room.Room;

import com.dyaco.spirit_commercial.support.room.DatabaseCallback;
import com.dyaco.spirit_commercial.support.room.fitness_test.entity_fitness_test.TableAirForce;
import com.dyaco.spirit_commercial.support.room.fitness_test.entity_fitness_test.TableArmy;
import com.dyaco.spirit_commercial.support.room.fitness_test.entity_fitness_test.TableCoastGuard;
import com.dyaco.spirit_commercial.support.room.fitness_test.entity_fitness_test.TableMarines;
import com.dyaco.spirit_commercial.support.room.fitness_test.entity_fitness_test.TableNavy;
import com.dyaco.spirit_commercial.support.room.fitness_test.entity_fitness_test.TablePeb;
import com.dyaco.spirit_commercial.support.room.spirit.SpiritDatabase;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class FitnessTestDbManager {
    private static volatile FitnessTestDbManager instance;
    private final FitnessTestDatabase db;
    private Disposable mDisposable;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    public static FitnessTestDbManager getInstance(Context context) {
        if (instance == null) {
            synchronized (SpiritDatabase.class) {
                if (instance == null) {
                    instance = new FitnessTestDbManager(context);
                }
            }
        }
        return instance;
    }

    /**
     * 引用外部資料庫
     * @param context c
     */
    public FitnessTestDbManager(Context context) {
        db = Room.databaseBuilder(context, FitnessTestDatabase.class, "DyacoMedical.db")
                .createFromAsset("databases/DyacoMedical.db")
                .build();
    }


    public void getAirForceList(final DatabaseCallback<TableAirForce> callback) {
        Disposable d = db.fitnessTestDao()
                .getAirForceAll()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(callback::onDataLoadedList);

        compositeDisposable.add(d);
    }

    public void getArmy(int gender, int age, int time, final DatabaseCallback<TableArmy> callback) {
        Disposable d = db.fitnessTestDao()
                .getArmy(gender, age, time)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(callback::onDataLoadedBean);

        compositeDisposable.add(d);
    }

    public void getAirForce(int gender, int age, int time, final DatabaseCallback<TableAirForce> callback) {
        Disposable d = db.fitnessTestDao()
                .getAirForce(gender, age, time)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(callback::onDataLoadedBean);

        compositeDisposable.add(d);
    }

    public void getNavy(int gender, int age, int time, final DatabaseCallback<TableNavy> callback) {
        Disposable d = db.fitnessTestDao()
                .getNavy(gender, age, time)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(callback::onDataLoadedBean);

        compositeDisposable.add(d);
    }

    public void getCoastGuard(int gender, int age, int time, final DatabaseCallback<TableCoastGuard> callback) {
        Disposable d = db.fitnessTestDao()
                .getCoastGuard(gender, age, time)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(callback::onDataLoadedBean);

        compositeDisposable.add(d);
    }

    public void getPeb(int gender, int age, int time, final DatabaseCallback<TablePeb> callback) {
        Disposable d = db.fitnessTestDao()
                .getPeb(gender, age, time)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(callback::onDataLoadedBean);

        compositeDisposable.add(d);
    }

    public void getMarine(int gender, int time, final DatabaseCallback<TableMarines> callback) {
        Disposable d = db.fitnessTestDao()
                .getMarines(gender, time)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(callback::onDataLoadedBean);

        compositeDisposable.add(d);
    }

    public void clearTable() {
      //  db.clearAllTables();
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


}