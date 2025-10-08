package com.dyaco.spirit_commercial.model.webapi;


import com.dyaco.spirit_commercial.model.webapi.bean.AppUpdateData;
import com.dyaco.spirit_commercial.model.webapi.bean.EgymLoginBean;
import com.dyaco.spirit_commercial.model.webapi.bean.EgymTrainingPlans;
import com.dyaco.spirit_commercial.model.webapi.bean.EgymUserDetailsBean;
import com.dyaco.spirit_commercial.model.webapi.bean.GetGymInfo2Bean;
import com.dyaco.spirit_commercial.model.webapi.bean.GetGymMonthlyAllRankingBean;
import com.dyaco.spirit_commercial.model.webapi.bean.GetGymMonthlyRankingFromMachineBean;
import com.dyaco.spirit_commercial.model.webapi.bean.GetMessageFromMachineBean;
import com.dyaco.spirit_commercial.model.webapi.bean.GetMessagesFromMachineBean;
import com.dyaco.spirit_commercial.model.webapi.bean.GetMyWeightTrendBean;
import com.dyaco.spirit_commercial.model.webapi.bean.GetUnreadMessageCountFromMachineBean;
import com.dyaco.spirit_commercial.model.webapi.bean.JoinGymRankingBean;
import com.dyaco.spirit_commercial.model.webapi.bean.MemberCheckinMachineByQRCodeBean;
import com.dyaco.spirit_commercial.model.webapi.bean.MemberCheckoutMachineBean;
import com.dyaco.spirit_commercial.model.webapi.bean.NotifyNewestConsoleVersionBean;
import com.dyaco.spirit_commercial.model.webapi.bean.NotifyUpdateConsoleSuccessBean;
import com.dyaco.spirit_commercial.model.webapi.bean.TimeZoneBean;
import com.dyaco.spirit_commercial.model.webapi.bean.TimeZoneBean2;
import com.dyaco.spirit_commercial.model.webapi.bean.TimeZoneBean3;
import com.dyaco.spirit_commercial.model.webapi.bean.UpdateBean;
import com.dyaco.spirit_commercial.model.webapi.bean.UpdateMyUserInfoFromMachineBean;
import com.dyaco.spirit_commercial.model.webapi.bean.UploadWorkoutFromMachineBean;
import com.dyaco.spirit_commercial.support.mediaapp.AppStoreBean;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface IServiceApi {

    //    @FormUrlEncoded
//    @POST("oauth/token")
//    @Headers({
//            "Authorization: Basic U3Bpcml0L0R5YWNvX3Rlc3Q6TFpkSmpISGFpREt1SDVNejFya1lvT3N4UzN6TENjV3A=",
//            "Content-Type: application/x-www-form-urlencoded"
//    })
//    Observable<EgymLoginBean> apiEgymLogin(@FieldMap Map<String, Object> params);
//
    @FormUrlEncoded
    @POST("oauth/token")
    @Headers("Content-Type:application/x-www-form-urlencoded")
    Observable<EgymLoginBean> apiEgymLogin(@HeaderMap Map<String, Object> header, @FieldMap Map<String, Object> params);


    @GET("users")
    Observable<EgymUserDetailsBean> apiEgymUserDetails(@HeaderMap Map<String, Object> header);

    @GET("cardio/training-plans")
    Observable<EgymTrainingPlans> apiGetTrainingPlans(@HeaderMap Map<String, Object> header);

    @POST("cardio/workouts")
    @Headers("Content-Type:application/json")
    Observable<Response<String>>  apiCreateWorkout(@HeaderMap Map<String, Object> header,@Body String params);


//    @GET("images/{imageId}")
//    Observable<ResponseBody> apiEgymImages(@HeaderMap Map<String, Object> header, @Path("imageId") String var1, @QueryMap Map<String, Object> var2);

    @GET("images/{imageId}")
    @Headers("Accept:image/*, application/json")
    Observable<ResponseBody> apiEgymImages(@Header("Authorization") String authorization, @Path("imageId") String var1,
                                           @Query("imageType") String imageType,
                                           @Query("imageSize") String imageSize);


    // 當會員在console上點選"QRCode"後，會員會使用Club APP 掃描QR Code，接著Console就每3秒call此api輪詢server判斷該會員是否登入，登入成功則api回傳該會員的個人資料
//    @FormUrlEncoded
//    @POST("MemberCheckinMachineByQRCode")
//    Observable<MemberCheckinMachineByQRCodeBean> apiMemberCheckinMachineByQRCode(@HeaderMap Map<String, Object> header, @FieldMap Map<String, Object> params);
//    Observable<MemberCheckinMachineByQRCodeBean> apiMemberCheckinMachineByQRCode(@FieldMap Map<String, Object> params);
    //  Observable<Response<MemberCheckinMachineByQRCodeBean>> apiMemberCheckinMachineByQRCode(@Field("params") String params);

    //TimeOut時間 4小時
    @POST("MemberCheckinMachineByQRCode")
    @Headers("Content-Type:application/json")
    Observable<Response<MemberCheckinMachineByQRCodeBean>> apiMemberCheckinMachineByQRCode(@Body String params);


//    @FormUrlEncoded
//    @POST("MemberCheckinMachineByNFC")
//    Observable<Response<MemberCheckinMachineByQRCodeBean>> apiMemberCheckinMachineByNFC(@Field("params") String params);

    @POST("MemberCheckinMachineByNFC")
    @Headers("Content-Type:application/json")
    Observable<Response<MemberCheckinMachineByQRCodeBean>> apiMemberCheckinMachineByNFC(@Body String params);


    // 取得會員個人資訊
    @POST("GetMyUserInfoFromMachine")
    @Headers("Content-Type:application/json")
    Observable<MemberCheckinMachineByQRCodeBean> apiGetMyUserInfoFromMachine(@Body String params);
//    @FormUrlEncoded
//    @POST("GetMyUserInfoFromMachine")
//    Observable<MemberCheckinMachineByQRCodeBean> apiGetMyUserInfoFromMachine(@Field("params") String params);
//    @FormUrlEncoded
//    @POST("GetMyUserInfoFromMachine")
//    Observable<Response<String>> apiGetMyUserInfoFromMachine2(@Field("params") String params);


    // 更新個人資訊
    // @FormUrlEncoded
    @POST("UpdateMyUserInfoFromMachine")
    @Headers("Content-Type:application/json")
    Observable<UpdateMyUserInfoFromMachineBean> apiUpdateMyUserInfoFromMachine(@Body String params);

    //取得我的體重趨勢
    @POST("GetMyWeightTrend")
    @Headers("Content-Type:application/json")
    Observable<GetMyWeightTrendBean> apiGetMyWeightTrend(@Body String params);

    //取得未讀取的會員訊息數量
    @POST("GetUnreadMessageCountFromMachine")
    @Headers("Content-Type:application/json")
    Observable<GetUnreadMessageCountFromMachineBean> apiGetUnreadMessageCountFromMachine(@Body String params);

    //會員登出機器
    @POST("MemberCheckoutMachine")
    @Headers("Content-Type:application/json")
    Observable<MemberCheckoutMachineBean> apiMemberCheckoutMachine(@Body String params);

    //取得會員訊息列表 id
    @POST("GetMessageFromMachine")
    @Headers("Content-Type:application/json")
    Observable<GetMessageFromMachineBean> apiGetMessageFromMachine(@Body String params);

    //取得會員訊息清單，需實作分頁(瀑布流)
    @POST("GetMessagesFromMachine")
    @Headers("Content-Type:application/json")
    Observable<GetMessagesFromMachineBean> apiGetMessagesFromMachine(@Body String params);

    //取得健身房月排名 - 首頁只提供五筆(包含自己)，會員自己的成績需Highlight顯示
    @POST("GetGymMonthlyRankingFromMachine")
    @Headers("Content-Type:application/json")
    Observable<GetGymMonthlyRankingFromMachineBean> apiGetGymMonthlyRankingFromMachine(@Body String params);

    //取得健身房月排名 - 所有的會員排名，會員自己的成績需置中並Highlight顯示
    @POST("GetGymMonthlyAllRanking")
    @Headers("Content-Type:application/json")
    Observable<GetGymMonthlyAllRankingBean> apiGetGymMonthlyAllRanking(@Body String params);

    //上傳運動紀錄
    @POST("UploadWorkoutFromMachine")
//    @Headers("Content-Type:application/json")
    @Headers({"Accept-Encoding: gzip, deflate", "Content-Type:application/json"})
    Observable<UploadWorkoutFromMachineBean> apiUploadWorkoutFromMachine(@Body String params);


    @POST("UploadWorkoutFromMachineSigned")
    @Headers("Content-Type:application/json")
//    @Headers({"Accept-Encoding: gzip, deflate", "Content-Type:application/json",})
    Observable<UploadWorkoutFromMachineBean> apiUploadWorkoutFromMachineSigned(@HeaderMap Map<String, Object> header, @Body String params);

    @POST("UploadMachineMaintainedLog")
    @Headers("Content-Type:application/json")
    Observable<UploadWorkoutFromMachineBean> apiUploadMachineMaintainedLog(@HeaderMap Map<String, Object> header, @Body String params);


//    @Headers({
//            "Accept-Encoding: gzip, deflate",
//            "Content-Type: application/json;charset=utf-8",
//            "Accept: application/json"
//    })

    //通知Dyaco Cloud有新的Console版本
    @POST("NotifyNewestConsoleVersion")
    @Headers("Content-Type:application/json")
    //Observable<Response<String>> apiNotifyNewestConsoleVersion(@HeaderMap Map<String, Object> header, @Field("params") String params);
    Observable<NotifyNewestConsoleVersionBean> apiNotifyNewestConsoleVersion(@HeaderMap Map<String, Object> header, @Body String params);

    //Console通知Dyaco Cloud 更新成功
    @POST("NotifyUpdateConsoleSuccess")
    @Headers("Content-Type:application/json")
    Observable<NotifyUpdateConsoleSuccessBean> apiNotifyUpdateConsoleSuccess(@HeaderMap Map<String, Object> header, @Body String params);

    //會員加入健身房排行榜
    @POST("JoinGymRanking")
    @Headers("Content-Type:application/json")
    Observable<JoinGymRankingBean> apiJoinGymRanking(@Body String params);

    //(Maintenance Mode) 取得健身房資訊
    @POST("GetGymInfo")
    @Headers("Content-Type:application/json")
    Observable<GetGymInfo2Bean> apiGetGymInfo(@HeaderMap Map<String, Object> header, @Body String params);


    // 新增這支，回傳完整 JSON 字串
    @POST("GetGymInfo")
    @Headers("Content-Type:application/json")
    Observable<String> apiGetGymInfoRaw(@HeaderMap Map<String, Object> header, @Body String params);


    @GET("resources/terms-and-conditions")
    Observable<ResponseBody> apiGetTermsAndConditions(@Query("locale") String locale);




    @GET("APPs/app_update.json")
        // @GET("app_update.json")
    Observable<AppUpdateData> apiCheckAppUpdate();

    @GET("APPStore/appstore_update.json")
    Observable<AppStoreBean> apiAppStore();

    @GET("app_update.json")
    Observable<AppUpdateData> apiCheckAppUpdate2();

    @GET("update.json")
    Observable<UpdateBean> apiCheckUpdate();

    @GET("json.gp")
    Observable<TimeZoneBean> apiGetTimeZone();

    @GET("json")
//    @GET("json/1.165.214.114")
    Observable<TimeZoneBean2> apiGetTimeZone2();

    @GET("v2.1/get-time-zone")
    Observable<TimeZoneBean3> apiGetTimeZone3(@QueryMap Map<String, Object> params);
    //  Observable<TimeZoneBean3> apiGetTimeZone3(@Query("format") String order,@Query("key") String key);

//
////    @Multipart
////    @POST("UploadWorkoutDetail")
////    Observable<Eee> apiUploadFile(@Part MultipartBody.Part multipartFile, @Part("params") RequestBody requestBodyParams);
//
////    RequestBody requestBodyParams = RequestBody.create(params, MediaType.parse("text/plain"));
////    File file = readFileFromAssets(this);
////    RequestBody requestFile = RequestBody.create(file, MediaType.parse("multipart/form-data"));
////    MultipartBody.Part multipartFile = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
}
