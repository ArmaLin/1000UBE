// IServiceApi.kt
package com.dyaco.spirit_commercial.model.kotlin

import com.dyaco.spirit_commercial.model.webapi.bean.EgymLoginBean
import com.dyaco.spirit_commercial.model.webapi.bean.EgymTrainingPlans
import com.dyaco.spirit_commercial.model.webapi.bean.EgymUserDetailsBean
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

/**
 * 定義 API 介面，改用 suspend function 表示網路請求。
 * 這裡以 Egym 登入為例，其它 API 可依此模式擴充。
 */
interface IServiceApi {
    @FormUrlEncoded
    @POST("oauth/token")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    suspend fun apiEgymLogin(
        @HeaderMap header: Map<String, String>,
        @FieldMap params: Map<String, String>
    ): Response<EgymLoginBean> // ✅ 改成 Response<EgymLoginBean>


    @GET("images/{imageId}")
    @Headers("Accept:image/*, application/json")
    suspend fun apiEgymImages(
        @Header("Authorization") authorization: String,
        @Path("imageId") imageId: String,
        @Query("imageType") imageType: String,
        @Query("imageSize") imageSize: String
    ): Response<ResponseBody> // ✅ 使用 Response<> 以便處理 HTTP 錯誤


    // ✅ 取得 Egym 使用者詳細資料 API
    @GET("users")
    suspend fun apiEgymUserDetails(
        @HeaderMap header: Map<String, String>
    ): Response<EgymUserDetailsBean>

    // ✅ 取得 Egym 訓練計畫 API
    @GET("cardio/training-plans")
    suspend fun apiGetTrainingPlans(
        @HeaderMap header: Map<String, String>
    ): Response<EgymTrainingPlans>

    @POST("cardio/workouts")
    @Headers("Content-Type: application/json")
    suspend fun apiCreateWorkout(
        @HeaderMap header: Map<String, String>,
        @Body params: String
    ): Response<String>



    @GET("resources/terms-and-conditions")
    suspend fun apiGetTermsAndConditions(@Query("locale") locale: String): Response<ResponseBody>



    @PUT("users/terms-and-conditions")
    suspend fun acceptTermsAndConditions(
        @Header("Authorization") bearerToken: String,
        @Query("locale") locale: String
    ): Response<ResponseBody> // 因為伺服器回傳 HTTP 204 No Content


}

