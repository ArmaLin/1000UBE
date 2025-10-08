package com.dyaco.spirit_commercial.model.kotlin

import com.dyaco.spirit_commercial.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

/**
 * RetrofitClient 物件負責建立 Retrofit 實例，
 * 並提供 createService 方法供 Java 端簡單取得 API 服務。
 */
object RetrofitClient {
    const val API_TYPE_DEFAULT = 0
    const val API_TYPE_EGYM = 1



//    // ✅ 設定 Retrofit 日誌攔截器，方便 Debug API
//    private val loggingInterceptor = HttpLoggingInterceptor().apply {
//        level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
//    }

    private val okHttpClient = OkHttpHelper.genericClient()

//    // ✅ 正確套用 OkHttpClient 設定
//    private val okHttpClient: OkHttpClient by lazy {
//        App.getApp().genericClient().newBuilder()
//            .addInterceptor(loggingInterceptor) // ✅ 加入 HTTP 日誌
//            .build()
//    }


    //java.lang.IllegalArgumentException: baseUrl must end in /: https://partner.api.egym.com/api/v1
    // ✅ 建立 Retrofit 實例
    private fun createRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // ✅ 只建立一次 Egym & Default Retrofit 實例
    private val defaultRetrofit: Retrofit by lazy { createRetrofit(BuildConfig.API_DOMAIN) }
    private val egymRetrofit: Retrofit by lazy { createRetrofit(BuildConfig.EGYM_URL) }

    /**
     * 取得 API Service，支援 EGYM 或預設 API
     */
    @JvmStatic
    fun <T> createService(service: Class<T>, apiType: Int = API_TYPE_DEFAULT): T {
        return when (apiType) {
            API_TYPE_EGYM -> egymRetrofit.create(service)
            else -> defaultRetrofit.create(service)
        }
    }

    /**
     * ✅ 支援動態 API，讓 Java 端也可以動態切換 API
     */
    @JvmStatic
    fun <T> createDynamicService(service: Class<T>, baseUrl: String): T {
        return createRetrofit(baseUrl).create(service)
    }
}
