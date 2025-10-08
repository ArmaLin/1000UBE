package com.dyaco.spirit_commercial.maintenance_mode;

import static com.dyaco.spirit_commercial.App.getApp;
import static com.dyaco.spirit_commercial.support.CommonUtils.getJson;
import static com.dyaco.spirit_commercial.support.CommonUtils.iExc;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.MAC_ADDRESS_PARAM;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dyaco.spirit_commercial.databinding.FragmentMaintenanceOrganizationBinding;
import com.dyaco.spirit_commercial.model.webapi.BaseApi;
import com.dyaco.spirit_commercial.model.webapi.IServiceApi;
import com.dyaco.spirit_commercial.model.webapi.bean.GetGymInfo2Bean;
import com.dyaco.spirit_commercial.support.HmacUtil;
import com.dyaco.spirit_commercial.support.base_component.BaseBindingDialogFragment;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingBean;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;


public class MaintenanceOrganizationFragment extends BaseBindingDialogFragment<FragmentMaintenanceOrganizationBinding> {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initEvent();

//        getBinding().view1.setVisibility(View.GONE);
//        getBinding().view2.setVisibility(View.VISIBLE);
        getGymInfo();
    }

    private void setGymInfo(GetGymInfo2Bean getGymInfoBean) {

        try {
            if (getBinding() == null) return;

            getBinding().view1.setVisibility(View.VISIBLE);
            //    getBinding().view2.setVisibility(View.GONE);

            if (getGymInfoBean.getDataMap() == null) return;

            GetGymInfo2Bean.DataMapDTO.DataDTO gymInfo = getGymInfoBean.getDataMap().getData();

//            if (gymInfo.getLogoUrl() != null) {
//                GlideApp.with(getApp())
//                        .load(gymInfo.getLogoUrl())
////                        .diskCacheStrategy(DiskCacheStrategy.NONE)
////                        .skipMemoryCache(true)
//                        .into(getBinding().ivLogo);
//            }



            getBinding().tvOrganizationName.setText(gymInfo.getClubFullName());
            getBinding().tvOrganizationID.setText(gymInfo.getTaxId());
            getBinding().tvAdministratorEmail.setText(gymInfo.getContactEmail());
            getBinding().tvAddress.setText(gymInfo.getAddress());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getGymInfo() {
        DeviceSettingBean d = getApp().getDeviceSettingBean();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(MAC_ADDRESS_PARAM, d.getMachine_mac() == null ? "" : d.getMachine_mac()); // 1開始
        paramMap.put("timestamp", Calendar.getInstance().getTimeInMillis());
        String paramJson = getJson(paramMap);

        Map<String, Object> header = new HashMap<>();
        header.put("signature", HmacUtil.hash(paramJson));

//        Log.d("GetGymInfoBean", "paramJson: " + paramJson);
//        Log.d("GetGymInfoBean", "signature: " + HmacUtil.hash(paramJson));

//        // 3. 呼叫 Raw API
//        Observable<String> rawCall =
//                BaseApi.createApi(IServiceApi.class)
//                        .apiGetGymInfoRaw(header, paramJson);
//
//        BaseApi.request(rawCall, new BaseApi.IResponseListener<String>() {
//            @Override
//            public void onSuccess(String rawJson) {
//                // 把完整 JSON 字串印到 log 或顯示在畫面上
//                Log.d("GetGymInfoBean", "rawJson: " + rawJson);
//
//            }
//
//            @Override
//            public void onFail() {
//                Log.e("GetGymInfoBean", "apiGetGymInfoRaw fail");
//            }
//        });

        BaseApi.request(BaseApi.createApi(IServiceApi.class).apiGetGymInfo(header, paramJson),
                new BaseApi.IResponseListener<GetGymInfo2Bean>() {
                    @Override
                    public void onSuccess(GetGymInfo2Bean data) {
                        iExc(() -> {
                            stopProgress();
                            if (data.getSuccess()) {
                                setGymInfo(data);

                                Log.d("GetGymInfoBean", "GetGymInfoBean: " + new Gson().toJson(data));
                            } else {
                                iExc(() -> Toasty.error(requireActivity(), data.getErrorMessage(), Toasty.LENGTH_LONG).show());
                                //    if (getBinding() == null) return;
                                //    getBinding().view1.setVisibility(View.GONE);
                                //   getBinding().view2.setVisibility(View.VISIBLE);
                            }
                        });
                    }

                    @Override
                    public void onFail() {
                        iExc(() -> {
                            Toasty.error(requireActivity(), "Fail", Toasty.LENGTH_LONG).show();
                            //  if (getBinding() == null) return;
                            //    getBinding().view1.setVisibility(View.GONE);
                            //    getBinding().view2.setVisibility(View.VISIBLE);
                            stopProgress();
                        });
                    }
                });
    }


    private void initEvent() {

        getBinding().btnDone.setOnClickListener(v -> dismiss());

//        getBinding().btnSignOut.setOnClickListener(new OnMultiClickListener() {
//            @Override
//            public void onMultiClick(View v) {
//                getBinding().view1.setVisibility(View.GONE);
//                getBinding().view2.setVisibility(View.VISIBLE);
//            }
//        });
    }

    private void stopProgress() {
        if (getBinding() != null) {
            getBinding().progress.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}