package com.dyaco.spirit_commercial.maintenance_mode;

import static com.dyaco.spirit_commercial.App.getApp;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.TV_TUNER_SIGNAL_DIGITAL;

import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.databinding.FragmentMaintenanceTvSettingsBinding;
import com.dyaco.spirit_commercial.model.repository.CountryRepo;
import com.dyaco.spirit_commercial.model.repository.RepoCallback;
import com.dyaco.spirit_commercial.support.MsgEvent;
import com.dyaco.spirit_commercial.support.base_component.BaseBindingDialogFragment;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.custom_view.CustomToast;
import com.dyaco.spirit_commercial.support.custom_view.GridViewSpaceItemDecoration;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingBean;

import java.util.List;


/**
 * 選擇國家
 */
public class MaintenanceTvSettingsFragment extends BaseBindingDialogFragment<FragmentMaintenanceTvSettingsBinding> {


    private SelectCountryAdapter selectCountryAdapter;

    private MainActivity mainActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDialogType(GENERAL.TRANSLATION_X);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainActivity = (MainActivity) requireActivity();
        initEvent();

        if (!MainActivity.isTvOn){
            mainActivity.initTvTuner();
            //Toasty.error(requireActivity(),"TvTuner ERROR",Toasty.LENGTH_LONG).show();
        }


     //   Looper.myQueue().addIdleHandler(() -> {
            initRecyclerView();
//            return false;
//        });
    }

    private void initEvent() {

        getBinding().btnDone.setOnClickListener(v -> dismiss());

        getBinding().btnSkip.setOnClickListener(view -> goToTvSearch());
    }


    private void initRecyclerView() {


        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireActivity(), 6);
        RecyclerView recyclerView = getBinding().recyclerView;
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new GridViewSpaceItemDecoration(2, 0, requireActivity()));
        selectCountryAdapter = new SelectCountryAdapter(requireActivity(), getApp().getDeviceSettingBean().getTvCountry());
        recyclerView.setAdapter(selectCountryAdapter);
//
        selectCountryAdapter.setOnItemClickListener(bean -> {

            if(mainActivity.deviceTvTuner == null) return;

            //#TV TUNER 設定國家
            mainActivity.deviceTvTuner.setTvCountry(bean.getTvCountry());

            DeviceSettingBean deviceSettingBean = getApp().getDeviceSettingBean();
            deviceSettingBean.setTvCountry(bean.getTvCountry());
            getApp().setDeviceSettingBean(deviceSettingBean);

            mainActivity.deviceTvTuner.getTvCountry();

            goToTvSearch();
        });


        Looper.myQueue().addIdleHandler(() -> {
            getCountryData();
            return false;
        });


        getBinding().scSignal.setPosition(getApp().getDeviceSettingBean().getTvTunerSignal(),false);

        getBinding().scSignal.setOnPositionChangedListener(position -> {

            DeviceSettingBean deviceSettingBean = getApp().getDeviceSettingBean();
            deviceSettingBean.setTvTunerSignal(position);
            getApp().setDeviceSettingBean(deviceSettingBean);

            mainActivity.setDeviceTvMode();

            if (position == TV_TUNER_SIGNAL_DIGITAL) {
                getBinding().scCableAir.setEnabled(false);
                getBinding().scCableAir.setAlpha(0.3f);
            } else {
                getBinding().scCableAir.setEnabled(true);
                getBinding().scCableAir.setAlpha(1f);
            }

        });

        if (getApp().getDeviceSettingBean().getTvTunerSignal() == TV_TUNER_SIGNAL_DIGITAL) {
            getBinding().scCableAir.setEnabled(false);
            getBinding().scCableAir.setAlpha(0.5f);
        }


        getBinding().scCableAir.setPosition(getApp().getDeviceSettingBean().getTvTunerAnalogType(),false);

        getBinding().scCableAir.setOnPositionChangedListener(position -> {

            DeviceSettingBean deviceSettingBean = getApp().getDeviceSettingBean();
            deviceSettingBean.setTvTunerAnalogType(position);
            getApp().setDeviceSettingBean(deviceSettingBean);

            mainActivity.setDeviceTvMode();
        });
    }


    private void goToTvSearch() {

        if (getApp().getDeviceSettingBean().getTvCountry() == null || "".equals(getApp().getDeviceSettingBean().getTvCountry().name())) {

            CustomToast.showToast(requireActivity(),"COUNTRY Not yet set");
            return;
        }

        MaintenanceTvSearchWindow maintenanceTvSearchWindow = new MaintenanceTvSearchWindow(requireActivity());
        maintenanceTvSearchWindow.showAtLocation(requireActivity().getWindow().getDecorView(), Gravity.START | Gravity.TOP, 50, 80);
        maintenanceTvSearchWindow.setOnCustomDismissListener(new BasePopupWindow.OnCustomDismissListener() {
            @Override
            public void onStartDismiss(MsgEvent value) {
            }

            @Override
            public void onDismiss() {
            }
        });

        dismiss();
    }

    private void getCountryData() {

        new CountryRepo().getData(0, new RepoCallback<CountryBean>() {
            @Override
            public void onSuccess(List<CountryBean> dataList) {

                    if (selectCountryAdapter != null)
                        selectCountryAdapter.setData2View(dataList);

                try {
                    getBinding().progressCirculars.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(String error) {
                if(!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    try {
                        getBinding().progressCirculars.setVisibility(View.GONE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                Log.d("ERROR", "onFail: " + error);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}