package com.dyaco.spirit_commercial.login;

import static com.dyaco.spirit_commercial.App.getApp;
import static com.dyaco.spirit_commercial.support.CommonUtils.getJson;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.LOG_IN_EVENT;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.MAC_ADDRESS_PARAM;

import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dyaco.spirit_commercial.App;
import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.databinding.FragmentQrcodeLoginBinding;
import com.dyaco.spirit_commercial.model.webapi.BaseApi;
import com.dyaco.spirit_commercial.model.webapi.IServiceApi;
import com.dyaco.spirit_commercial.model.webapi.bean.MemberCheckinMachineByQRCodeBean;
import com.dyaco.spirit_commercial.support.CommonUtils;
import com.bumptech.glide.Glide;
import com.dyaco.spirit_commercial.support.RxTimer;
import com.dyaco.spirit_commercial.support.base_component.BaseBindingBottomDialogFragment;
import com.jeremyliao.liveeventbus.LiveEventBus;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Response;


public class QrcodeLoginFragmentBottom extends BaseBindingBottomDialogFragment<FragmentQrcodeLoginBinding> {
    private String identity;
    private RxTimer rxTimer;
    private MainActivity mainActivity;
//    private String MACHINE_MAC;
//public class QrcodeLoginFragmentBottom extends BaseBindingFragment<FragmentQrcodeLoginBinding> {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainActivity.isStopLogin = true;

        mainActivity = (MainActivity) requireActivity();

        //   MACHINE_MAC = getApp().getDeviceSettingBean().getMachine_mac();
        //  MACHINE_MAC = "7E:C1:46:61:62:AE";
        initDelay();

    }

    private void initDelay() {

        Looper.myQueue().addIdleHandler(() -> {

            //  identity = getApp().getIdentity();
            getBinding().btnClose.setOnClickListener(v -> {
                isLogin = true;
                dismiss();
            });

            getBinding().actionImage.setOnClickListener(v -> {
//                CreateAccountWelcomeWindow createAccountWelcomeWindow = new CreateAccountWelcomeWindow(requireActivity());
//                createAccountWelcomeWindow.showAtLocation(requireActivity().getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);
//                dismiss();

                //    Navigation.findNavController(v).navigate(R.id.createAccountWelcomeFragment);
                // Navigation.findNavController(v).navigate(QrcodeLoginFragmentBottomDirections.actionQrcodeLoginFragmentToCreateAccountWelcomeFragment());

                // ((MainActivity) requireActivity()).navController.navigate(R.id.createAccountWelcomeFragment);
                //  ((MainActivity) requireActivity()).navController.navigate(QrcodeLoginFragmentBottomDirections.actionQrcodeLoginFragmentToCreateAccountWelcomeFragment());
                //   dismiss();
                //  new RxTimer().timer(500,number -> dismiss());


            });

            if (qrTimer != null) {
                qrTimer.cancel();
                qrTimer = null;
            }

            qrTimer = new RxTimer();
            qrTimer.interval3(1000, number -> {
                if (getApp().getDeviceSettingBean().getMachine_mac() != null &&
                        !"".equals(getApp().getDeviceSettingBean().getMachine_mac())) {
                    Glide.with(getApp())
                            .load(new CommonUtils().createQRCode(getApp().getDeviceSettingBean().getMachine_mac(), 384, 8))
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .into(getBinding().actionImage);

                    if (qrTimer != null) {
                        qrTimer.cancel();
                        qrTimer = null;
                    }
                } else {
                    Log.d("SETTING_FILE", "get qrcode: ");
                }
            });


//            rxTimer = new RxTimer();
//            rxTimer.interval(2000, number -> apiSyncDeviceSyncUser());

            return false;
        });

        cancelTimer();

        rxTimer = new RxTimer();
        rxTimer.interval(3000, number -> {

            if (isLogin) return;

            login();
            loginTime++;
            if (loginTime > 30) {
                dismiss();
            }

        });
    }


    /**
     * MISSING_REQUIRED_PARAMETER (102)		必要參數沒給
     * LOGIN_FAILED (1217)		登入失敗，則console應持續輪詢server直到成功，或輪詢超過90秒仍登入失敗則console須停止輪詢
     * USER_NOT_FOUND (1003)		查無使用者資料
     */
    private RxTimer qrTimer;
    int loginTime = 0;
    boolean isLogin = false;

    private void login() {

        Map<String, Object> map = new HashMap<>();
        map.put(MAC_ADDRESS_PARAM, getApp().getDeviceSettingBean().getMachine_mac());
        Log.d("@@@@@@", "login: " + getApp().getDeviceSettingBean().getMachine_mac());
//        JSONObject param = new JSONObject();
//        try {
//            param.put("macAddress", "7E:C1:46:61:62:AE");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        BaseApi.request(BaseApi.createApi(IServiceApi.class).apiMemberCheckinMachineByQRCode(getJson(map)),
                new BaseApi.IResponseListener<Response<MemberCheckinMachineByQRCodeBean>>() {
                    @Override
                    public void onSuccess(Response<MemberCheckinMachineByQRCodeBean> response) {

                        try {
                            MemberCheckinMachineByQRCodeBean data = response.body();
                            //  Log.d("WEB_API", "LOGIN: " + data.toString());
                            if (data == null) return;

                            Log.d("WEB_API", "LOGIN: " + data.getDataMap().getData());

                            if (isLogin) return;

                            if (data.getSuccess()) {

                                isLogin = true;

                                LiveEventBus.get(LOG_IN_EVENT).post("");

                                MemberCheckinMachineByQRCodeBean.DataMapDTO.DataDTO dataDTO = data.getDataMap().getData();
                                mainActivity.setUserData(dataDTO);

                                cancelTimer();

                                App.COOKIE = response.headers().get("set-cookie");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFail() {
                        Log.d("WEB_API", "onFail: ");
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cancelTimer();

        if (qrTimer != null) {
            qrTimer.cancel();
            qrTimer = null;
        }


        MainActivity.isStopLogin = false;
    }

    private void cancelTimer() {

        if (rxTimer != null) {
            rxTimer.cancel();
            rxTimer = null;
        }
    }

}