package com.dyaco.spirit_commercial.support;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;


public class RxTimer {

    private Disposable mDisposable;

    /**
     * milliseconds毫秒後執行指定動作
     *
     * @param milliSeconds milliSeconds
     * @param rxAction     rxAction
     */
    public void timer(long milliSeconds, final RxAction rxAction) {
        Observable.timer(milliSeconds, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable disposable) {
                        mDisposable = disposable;
                    }

                    @Override
                    public void onNext(@NonNull Long n) {
                        if (rxAction != null) {
                            rxAction.action(n);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        //取消訂閱
                        cancel();
                    }

                    @Override
                    public void onComplete() {
                        //取消訂閱
                        cancel();
                    }
                });
    }

    /**
     * 每隔milliseconds毫秒後執行指定動作
     * initialDelay 第一次執行的延遲時間
     * .take(5) 執行5次
     * @param milliSeconds milliSeconds
     * @param rxAction rxAction
     */
    public void interval(long milliSeconds, final RxAction rxAction) {
        Observable.interval(1000, milliSeconds, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable disposable) {
                        mDisposable = disposable;
                    }

                    @Override
                    public void onNext(@NonNull Long milliSeconds) {
                        if (rxAction != null) {
                            rxAction.action(milliSeconds * 1000);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    /**
     *
     * @param initDelay 延遲時間
     * @param period 間隔時間
     * @param times 次數
     * @param rxAction action
     */
    public void intervalComplete(long initDelay,int period, int times ,final RxActionComplete rxAction) {

        Observable.interval(initDelay, period, TimeUnit.MILLISECONDS)
                .take(times + 1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable disposable) {
                        mDisposable = disposable;
                    }

                    @Override
                    public void onNext(@NonNull Long sec) {
                            rxAction.action(sec);
                        }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        if (rxAction != null) {
                            rxAction.complete();
                        }
                    }
                });
    }

    /**
     *
     * @param initialDelay 第一次執行的延遲時間
     * @param period 延遲間隔多久執行一次
     * @param count 次數
     * @param rxAction callback
     */
    public void rxCountDown(long initialDelay, long period, int count, CdAction rxAction) {
        Observable.interval(initialDelay, period, TimeUnit.MILLISECONDS)
                .take(count + 1)
                .map(aLong -> count - aLong)
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> Log.d("CDTTT", "finally: "+Thread.currentThread().getName()))
                .doOnSubscribe(disposable -> Log.d("CDTTT", "accept: "+Thread.currentThread().getName()))
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mDisposable = d;
                        Log.d("CDTTT", "onSubscribe: "+Thread.currentThread().getName());
                    }

                    @Override
                    public void onNext(@NonNull Long aLong) {
                        //設定倒數計時文字
                        if (rxAction != null) {
                            rxAction.onTime(aLong);
                            Log.d("CDTTT", "onNext: " + aLong);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        cancel();
                        Log.d("CDTTT", "onError: ");
                    }

                    @Override
                    public void onComplete() {
                        //事件完成後恢復點選
                        try {
                            Log.d("CDTTT", "onComplete: ");
                            rxAction.onComplete();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
    }


    public void interval3(long milliSeconds, final RxAction rxAction) {
        Observable.interval(0, milliSeconds, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable disposable) {
                        mDisposable = disposable;
                    }

                    @Override
                    public void onNext(@NonNull Long milliSeconds) {
                        if (rxAction != null) {
                            rxAction.action(milliSeconds);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 取消訂閱
     */
    public void cancel() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }

    public interface RxAction {
        /**
         * 讓調用者指定指定動作
         *
         * @param number number
         */
        void action(long number);
    }

    public interface RxActionComplete {
        /**
         * 讓調用者指定指定動作
         *
         * @param number number
         */
        void action(long number);
        void complete();
    }


    public interface CdAction {
        void onTime(long second);

        void onComplete();
    }
}