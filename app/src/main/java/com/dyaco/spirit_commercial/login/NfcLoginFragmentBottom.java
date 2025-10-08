package com.dyaco.spirit_commercial.login;

import static com.dyaco.spirit_commercial.App.getApp;
import static com.dyaco.spirit_commercial.App.getDeviceGEM;
import static com.dyaco.spirit_commercial.support.CommonUtils.getJson;
import static com.dyaco.spirit_commercial.support.CommonUtils.iExc;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.EVENT_NFC_ENABLED;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.EVENT_NFC_READ;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.LOG_IN_EVENT;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.MAC_ADDRESS_PARAM;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.corestar.libs.device.DeviceGEM;
import com.dyaco.spirit_commercial.App;
import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.FragmentNfcLoginBinding;
import com.dyaco.spirit_commercial.model.webapi.BaseApi;
import com.dyaco.spirit_commercial.model.webapi.IServiceApi;
import com.dyaco.spirit_commercial.model.webapi.bean.MemberCheckinMachineByQRCodeBean;
import com.dyaco.spirit_commercial.support.RxTimer;
import com.dyaco.spirit_commercial.support.base_component.BaseBindingBottomDialogFragment;
import com.dyaco.spirit_commercial.support.custom_view.CustomToast;
import com.jeremyliao.liveeventbus.LiveEventBus;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Response;


public class NfcLoginFragmentBottom extends BaseBindingBottomDialogFragment<FragmentNfcLoginBinding> {

//    private RxTimer rxTimer;
    private RxTimer rxTimer2;
    private MainActivity mainActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainActivity.isStopLogin = true;

        mainActivity = (MainActivity) requireActivity();


        rxTimer2 = new RxTimer();
        rxTimer2.timer(1000, number -> iExc(() -> getDeviceGEM().nfcMessageEnableNfcRadio(DeviceGEM.NFC_READ_EVENT.NFC_NDEF_READ)));

        getBinding().btnClose.setOnClickListener(v -> {
            isLogin = true;

//            Map<DeviceGEM.EQUIPMENT_CONTROL_PARAMETER, Integer> parameters = new HashMap<>();
//            getDeviceGEM().gymConnectMessageSetFitnessEquipmentState(DeviceGEM.FITNESS_EQUIPMENT_STATE.IN_USE, parameters);

            dismiss();
        });


//        Map<DeviceGEM.EQUIPMENT_CONTROL_PARAMETER, Integer> parameters = new HashMap<>();
//        getDeviceGEM().gymConnectMessageSetFitnessEquipmentState(DeviceGEM.FITNESS_EQUIPMENT_STATE.IDLE, parameters);






//        getBinding().actionImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //   Navigation.findNavController(v).navigate(NavigationLoginDirections.actionGlobalMainBlankFragment());
//                parent.navController.navigate(NavigationLoginDirections.actionGlobalMainBlankFragment());
//                //   ((MainActivity)requireActivity()).navController.setGraph(R.navigation.navigation_one);
//            }
//        });

        LiveEventBus.get(EVENT_NFC_ENABLED, Boolean.class).observe(getViewLifecycleOwner(), b -> {
            if (b) {
                if (getBinding() != null) getBinding().actionImage.setAlpha(1f);
            }
        });


        //放上去觸發一次，離開超過3秒，再放上去才會再觸發
        LiveEventBus.get(EVENT_NFC_READ, String.class).observe(getViewLifecycleOwner(), s -> {
            if (isLogin) return;
            mainActivity.showLoading(true);
            mainActivity.uartConsole.setBuzzer();
            login(s);
            Log.d("EVENT_NFC_READ", "uid: " + s);
        });
    }


    boolean isLogin = false;


    /**
     * MISSING_REQUIRED_PARAMETER (102)		必要參數沒給
     * GET_MEMBER_DATA_TIMEOUT(1300)		向健身房會員系統取得會員資料逾時
     * USER_NOT_FOUND (1003)		查無使用者資料
     */
    //tag 裡寫 Spirit+ Club APP 的註冊 email
    private void login(String nfcId) {

        if (isLogin) return;
        isLogin = true;
        //  nfcId = "arma1996@hotmail.com";
        Log.d("WEB_API", "login: " + nfcId);
        Map<String, Object> map = new HashMap<>();
        map.put(MAC_ADDRESS_PARAM, getApp().getDeviceSettingBean().getMachine_mac());
        map.put("nfcContent", nfcId);

        BaseApi.request(BaseApi.createApi(IServiceApi.class).apiMemberCheckinMachineByNFC(getJson(map)),
                new BaseApi.IResponseListener<Response<MemberCheckinMachineByQRCodeBean>>() {
                    @Override
                    public void onSuccess(Response<MemberCheckinMachineByQRCodeBean> response) {

                        try {
                            MemberCheckinMachineByQRCodeBean data = response.body();


                            if (data != null && data.getSuccess()) {

                                LiveEventBus.get(LOG_IN_EVENT).post("");

                                MemberCheckinMachineByQRCodeBean.DataMapDTO.DataDTO dataDTO = data.getDataMap().getData();
                                mainActivity.setUserData(dataDTO);

                                App.COOKIE = response.headers().get("set-cookie");
                            //    Log.d("WEB_API", "genericClient: " + App.COOKIE);

                                Log.d("WEB_API", "LOGIN: " + data.getDataMap().getData().toString());
                            } else {
                                String s = "";
                                if (data != null) {

                                    if (data.getErrorMessage() != null) {
                                        s = data.getErrorMessage();
                                    }

                                    //USER_NOT_FOUND (1003), 查無使用者資料
                                    if ("1003".equals(data.getErrorCode())) {
                                        s = getString(R.string.USER_NOT_FOUND);
                                    }

                                    //GET_MEMBER_DATA_TIMEOUT(1300), 向健身房會員系統取得會員資料逾時
                                    if ("1300".equals(data.getErrorCode())) {
                                        s = getString(R.string.GET_MEMBER_DATA_TIMEOUT);
                                    }

                                    //MISSING_REQUIRED_PARAMETER (102), 必要參數沒給
                                    //NFC_NOT_ENABLED (130),

                                    //LOGIN_FAILED (1217), 登入失敗，則console應持續輪詢server直到成功，或輪詢超過90秒仍登入失敗則console須停止輪詢
//

                                    //MACHINE_UNREGISTER (1216)
//                                    if ("1216".equals(data.getErrorCode())) {
//                                        s = "MACHINE_UNREGISTER";
//                                    }
                                }

                                //   String s = data != null ? data.getErrorMessage() : "";
                                Log.d("WEB_API", "LOGIN fail: " + s );
                                loginFailed(false, s);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.d("WEB_API", "Exception: " + e.getLocalizedMessage());
                            loginFailed(false, "");
                        }

                        mainActivity.showLoading(false);
                    }

                    @Override
                    public void onFail() {
                        loginFailed(true, "");
                        Log.d("WEB_API", "onFail: ");
                    }
                });

//        BaseApi.request(BaseApi.createApi(IServiceApi.class).apiMemberCheckinMachineByNFC(getJson(map)),
//                new BaseApi.IResponseListener<Response<MemberCheckinMachineByQRCodeBean>>() {
//                    @Override
//                    public void onSuccess(Response<MemberCheckinMachineByQRCodeBean> response) {
//
//                        try {
//                            MemberCheckinMachineByQRCodeBean data = response.body();
//
//                            Log.d("WEB_API", "LOGIN: " + data.toString());
//
//                            if (data != null && data.getSuccess()) {
//
//                                LiveEventBus.get(LOG_IN_EVENT).post("");
//
//                                MemberCheckinMachineByQRCodeBean.DataMapDTO.DataDTO dataDTO = data.getDataMap().getData();
//                                mainActivity.setUserData(dataDTO);
//
//                                App.COOKIE = response.headers().get("set-cookie");
//                                Log.d("WEB_API", "genericClient: " + App.COOKIE);
//                            } else {
//                                Log.d("WEB_API", "LOGIN: " + data.toString());
//                                loginFailed(false);
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            loginFailed(false);
//                            Log.d("WEB_API", "LOGIN: " + e.getLocalizedMessage());
//                        }
//
//                        mainActivity.showLoading(false);
//                    }
//
//                    @Override
//                    public void onFail() {
//                        loginFailed(true);
//                        Log.d("WEB_API", "onFail: ");
//                    }
//                });
    }

    private void loginFailed(boolean isNetworkError, String errorMsg) {
        isLogin = false;
        try {
            String e = "".equals(errorMsg) ? (isNetworkError ? getString(R.string.network_error) : getString(R.string.LOGIN_FAILED)) : errorMsg;
            mainActivity.showLoading(false);
            CustomToast.showToast(mainActivity, e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        if (rxTimer != null) {
//            rxTimer.cancel();
//            rxTimer = null;
//        }

        if (rxTimer2 != null) {
            rxTimer2.cancel();
            rxTimer2 = null;
        }
        getDeviceGEM().nfcMessageDisableNfcRadio();


        MainActivity.isStopLogin = false;
        //  cancelTimer();
    }

//    private void cancelTimer() {
//
//        if (rxTimer != null) {
//            rxTimer.cancel();
//            rxTimer = null;
//        }
//    }
}