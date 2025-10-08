package com.dyaco.spirit_commercial.model.kotlin

import android.util.Log
import com.dyaco.spirit_commercial.model.webapi.bean.EgymLoginBean
import com.dyaco.spirit_commercial.model.webapi.bean.EgymTrainingPlans
import com.dyaco.spirit_commercial.model.webapi.bean.EgymUserDetailsBean
import kotlinx.coroutines.*
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.lang.ref.WeakReference

/**
 * EgymApiKK 物件封裝 Egym 相關 API 呼叫，
 * 採用全域 CoroutineScope 處理 API 請求，
 * 並自動處理錯誤與重試機制。
 */
object EgymApiManager {

    // 定義最大重試次數常數
    private const val MAX_RETRY_COUNT = 3

    // 全域 CoroutineScope，不受 Activity/Fragment 生命週期影響
    private val apiScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /**
     * 提取 errorBody 中的錯誤訊息
     */
    private fun extractErrorMessage(response: Response<*>): String {
        return try {
            response.errorBody()?.use { it.string() } ?: "Unknown error"
        } catch (e: IOException) {
            "Error reading errorBody: ${e.message}"
        }
    }

    /**
     * 通用的 API 執行函式
     *
     * @param apiCall 要執行的 suspend 函式 (API 呼叫邏輯)
     * @param listener 回呼介面
     * @param retryCount 目前已重試次數，預設為 0
     */
    @JvmStatic
    fun <T> executeApi(
        apiCall: suspend () -> Response<T>,
        listener: ApiResponseListener<T>,
        retryCount: Int = 0
    ) {
        // 使用弱引用避免記憶體洩漏
        val weakListener = WeakReference(listener)

        apiScope.launch {
            try {
                val response = withTimeout(30_000) { apiCall() } // 30 秒 Timeout
                withContext(Dispatchers.Main) {
                    // 若 listener 已被回收則直接返回
                    val safeListener = weakListener.get() ?: return@withContext

                    if (response.isSuccessful) {
                        @Suppress("UNCHECKED_CAST")
                        // 直接回傳 response.body()，HTTP 204 回傳 null 由呼叫端自行判斷
                        safeListener.onSuccess(response.body() as T, response.code())
                        Log.d("EGYM_API", "✅ Success [${response.code()}]: ${response.body()}")
                    } else {
                        val errorMessage = extractErrorMessage(response)
//                        val error = Exception("HTTP ${response.code()} - $errorMessage")
                        val error = Exception(errorMessage)
                        safeListener.onFailure(error, response.code())
                        Log.e("EGYM_API", "❌ Error [${response.code()}]: $errorMessage")
                    }
                }
            } catch (e: TimeoutCancellationException) {
                Log.e("EGYM_API", "❌ Timeout (30s)")
                withContext(Dispatchers.Main) {
                    weakListener.get()?.onFailure(e, null)
                }
            } catch (e: IOException) {
                Log.e("EGYM_API", "❌ Network Error: ${e.message}")
                withContext(Dispatchers.Main) {
                    weakListener.get()?.onFailure(e, null)
                }
            } catch (e: HttpException) {
                if ((e.code() == 429 || e.code() == 503) && retryCount < MAX_RETRY_COUNT) {
                    // 採用指數退避機制，根據重試次數延遲更久
                    val delayTime = 2000L * (retryCount + 1)
                    Log.w("EGYM_API", "🔁 Retry [${retryCount + 1}] due to HTTP ${e.code()}, delaying for $delayTime ms")
                    delay(delayTime)
                    executeApi(apiCall, listener, retryCount + 1)
                } else {
                    Log.e("EGYM_API", "❌ HttpException: ${e.message}")
                    withContext(Dispatchers.Main) {
                        weakListener.get()?.onFailure(e, e.code())
                    }
                }
            }
        }
    }

    /**
     * 提供一個方法取消所有進行中的 API 請求
     */
    @JvmStatic
    fun cancelAllRequests() {
        apiScope.cancel("取消所有 API 請求")
        Log.d("EGYM_API", "所有 API 請求已取消")
    }

    // 以下是各個 Egym 專用 API 的封裝呼叫

    @JvmStatic
    fun loginEgym(
        header: Map<String, String>,
        params: Map<String, String>,
        listener: ApiResponseListener<EgymLoginBean>
    ) {
        executeApi({
            RetrofitClient.createService(IServiceApi::class.java, RetrofitClient.API_TYPE_EGYM)
                .apiEgymLogin(header, params)
        }, listener)
    }

    @JvmStatic
    fun getEgymImage(
        authorization: String,
        imageId: String,
        imageType: String,
        imageSize: String,
        listener: ApiResponseListener<ResponseBody>
    ) {
        executeApi({
            RetrofitClient.createService(IServiceApi::class.java, RetrofitClient.API_TYPE_EGYM)
                .apiEgymImages(authorization, imageId, imageType, imageSize)
        }, listener)
    }

    @JvmStatic
    fun getEgymUserDetails(
        header: Map<String, String>,
        listener: ApiResponseListener<EgymUserDetailsBean>
    ) {
        executeApi({
            RetrofitClient.createService(IServiceApi::class.java, RetrofitClient.API_TYPE_EGYM)
                .apiEgymUserDetails(header)
        }, listener)
    }

    @JvmStatic
    fun getEgymTrainingPlans(
        header: Map<String, String>,
        listener: ApiResponseListener<EgymTrainingPlans>
    ) {
        executeApi({
            RetrofitClient.createService(IServiceApi::class.java, RetrofitClient.API_TYPE_EGYM)
                .apiGetTrainingPlans(header)
        }, listener)
    }

    @JvmStatic
    fun createEgymWorkout(
        header: Map<String, String>,
        params: String,
        listener: ApiResponseListener<String>
    ) {
        executeApi({
            RetrofitClient.createService(IServiceApi::class.java, RetrofitClient.API_TYPE_EGYM)
                .apiCreateWorkout(header, params)
        }, listener)
    }


    @JvmStatic
    fun getTermsAndConditions(
        locale: String = "en_US",
        listener: ApiResponseListener<ResponseBody>
    ) {
        executeApi({
            RetrofitClient.createService(IServiceApi::class.java, RetrofitClient.API_TYPE_EGYM)
                .apiGetTermsAndConditions(locale)
        }, listener)
    }


    @JvmStatic
    fun acceptTermsAndConditions(
        bearerToken: String,
        locale: String = "en_US",
        listener: ApiResponseListener<ResponseBody>
    ) {
        executeApi({
            RetrofitClient.createService(IServiceApi::class.java, RetrofitClient.API_TYPE_EGYM)
                .acceptTermsAndConditions(bearerToken, locale)
        }, listener)
    }



}
