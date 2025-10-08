package com.dyaco.spirit_commercial.model.webapi;

import static com.dyaco.spirit_commercial.support.CommonUtils.isNetworkAvailable;

import androidx.annotation.NonNull;

import com.dyaco.spirit_commercial.App;
import com.dyaco.spirit_commercial.BuildConfig;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class BaseApi {
    private static final String LOCAL_SERVER_URL = BuildConfig.API_DOMAIN;

    public static <T> T createApi(Class<T> service) {

        //默認情況下，Gson是嚴格的，只接受RFC 4627指定的JSON。此選項使解析器在接受的內容中更加自由。
        //    Gson gson = new GsonBuilder().setLenient().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(LOCAL_SERVER_URL)
                .addConverterFactory(ScalarsConverterFactory.create()) //增加返回值為String的支持(要排第一)
                .addConverterFactory(GsonConverterFactory.create())//增加返回值為为GSON的支持(以實體類返回)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())//增加返回值為Observable<T>的支持
                .client(App.getApp().genericClient())
                .build();
        return retrofit.create(service);
    }

    public static <T> T createApi2(Class<T> service, String url) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(App.getApp().genericClient())
                .build();
        return retrofit.create(service);
    }

    public static <T> T createApiEgym(Class<T> service) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.EGYM_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(App.getApp().genericClient())
                .build();
        return retrofit.create(service);
    }

    public static <T> void request(Observable<T> observable, final IResponseListener<T> listener) {

      //  final CompositeDisposable compositeDisposable = new CompositeDisposable();

        if (!isNetworkAvailable(App.getApp()))  {
            //  ToastUtils.getInstance().showToast("無網路");
            if (listener != null) {
                listener.onFail();
            }
            return;
        }

        observable.subscribeOn(Schedulers.io())
                .retry(1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<T>() {
                               @Override
                               public void onError(@NonNull Throwable e) {
                                   e.printStackTrace();
                                //   Log.d("TIME_ZONE", Objects.requireNonNull(e.getMessage()));
                                   if (listener != null) {
                                       listener.onFail();
                                   }
                               }

                               @Override
                               public void onComplete() {
                                  // Log.d("@@@e@@", "onComplete: ");
                               }

                               @Override
                               public void onSubscribe(@NonNull Disposable disposable) {
                                //   Log.d("@@@e@@", "onSubscribe: ");
                                //   compositeDisposable.add(disposable);
                               }

                               @Override
                               public void onNext(@NonNull T data) {
                                   if (listener != null) {
                                       listener.onSuccess(data);
                                   }
                               }
                           }
                );
    }

    public interface IResponseListener<T> {
        void onSuccess(T data);

        void onFail();
    }


//    private static final CompositeDisposable compositeDisposable = new CompositeDisposable();
//
//    // 清除所有訂閱的方法，應在適當時機調用
//    public static void clearDisposables() {
//        compositeDisposable.clear(); // 清空所有訂閱
//    }

//    //Activity or fragment
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        BaseApi.clearDisposables(); // 清除訂閱
//    }


//    //ViewModel
//    @Override
//    protected void onCleared() {
//        super.onCleared();
//        BaseApi.clearDisposables(); // 清除訂閱
//    }
}
