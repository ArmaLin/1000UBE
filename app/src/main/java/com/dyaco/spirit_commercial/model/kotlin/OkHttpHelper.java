package com.dyaco.spirit_commercial.model.kotlin;

import static com.dyaco.spirit_commercial.App.COOKIE;
import static com.dyaco.spirit_commercial.App.getApp;
import static com.dyaco.spirit_commercial.support.CommonUtils.isNetworkAvailable;

import android.util.Log;

import com.dyaco.spirit_commercial.BuildConfig;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;

public class OkHttpHelper {

    private static OkHttpClient mOkHttpClient = null;

    /**
     * **獲取共享的 `OkHttpClient` (不再依賴 `App.genericClient()`)**
     *
     * @return `OkHttpClient`
     */
    public static synchronized OkHttpClient genericClient() {
        if (mOkHttpClient == null) {
            mOkHttpClient = createNewClient();
        }
        return mOkHttpClient;
    }

    /**
     * **允許不同 API 建立不同的 `OkHttpClient` (具體 API 需要不同的 `Timeout` 或 `Interceptor` 時使用)**
     *
     * @param connectTimeout 連線超時 (秒)
     * @param readTimeout    讀取超時 (秒)
     * @param writeTimeout   寫入超時 (秒)
     * @return `OkHttpClient`
     */
    public static OkHttpClient createCustomClient(int connectTimeout, int readTimeout, int writeTimeout) {
        return new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .connectTimeout(connectTimeout, TimeUnit.SECONDS)
                .readTimeout(readTimeout, TimeUnit.SECONDS)
                .writeTimeout(writeTimeout, TimeUnit.SECONDS)
                .dns(new SafeDns(25)) //
                .addInterceptor(getHeaderInterceptor())
                .addInterceptor(getLoggingInterceptor()) // ✅ **Debug 模式才啟用**
                .addInterceptor(getNetworkInterceptor()) // ✅ **檢查是否有網路**
                .build();
    }

    /**
     * **建立新的 `OkHttpClient` (預設超時)**
     */
    private static OkHttpClient createNewClient() {
        return new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .dns(new SafeDns(25)) // ✅ **如果 `CustomDns` 失敗，會自動回退**
                .addInterceptor(getHeaderInterceptor())
            //    .addInterceptor(getLoggingInterceptor()) // ✅ **Debug 模式才啟用**
                .addInterceptor(getNetworkInterceptor()) // ✅ **檢查是否有網路**
         //       .addInterceptor(getDelayInterceptor())   // 模擬網路延遲 (延遲 2 秒)
                .build();
    }

    /**
     * **HTTP Header 攔截器**
     */
    private static Interceptor getHeaderInterceptor() {
        return chain -> {
            Request original = chain.request();
            Request request = original.newBuilder()
                    .header("Cookie", COOKIE)
                    .method(original.method(), original.body())
                    .build();
            return chain.proceed(request);
        };
    }

    /**
     * **HTTP 日誌攔截器 (只在 Debug 模式開啟)**
     */
    private static HttpLoggingInterceptor getLoggingInterceptor() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
        return loggingInterceptor;
    }

    /**
     * **網路檢查攔截器**
     */
    private static Interceptor getNetworkInterceptor() {
        return chain -> {
            if (!isNetworkAvailable(getApp())) {
                throw new IOException("No internet connection");
            }
            return chain.proceed(chain.request());
        };
    }

    /**
     * 延遲攔截器，用來模擬網路延遲 (延遲 2000 毫秒，即 2 秒)
     */
    private static Interceptor getDelayInterceptor() {
        return chain -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Log.e("OkHttpHelper", "DelayInterceptor 被中斷", e);
            }
            return chain.proceed(chain.request());
        };
    }
}
