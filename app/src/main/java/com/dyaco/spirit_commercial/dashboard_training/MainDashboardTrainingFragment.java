package com.dyaco.spirit_commercial.dashboard_training;

import static com.dyaco.spirit_commercial.App.getApp;
import static com.dyaco.spirit_commercial.support.CommonUtils.isNetworkAvailable;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.CONSOLE_SYSTEM_EGYM;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.WIFI_WORK;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.databinding.FragmentMainDashboardTrainingBinding;
import com.dyaco.spirit_commercial.egym.EgymUtil;
import com.dyaco.spirit_commercial.support.base_component.BaseBindingFragment;
import com.jeremyliao.liveeventbus.LiveEventBus;

/**
 * DashboardTrainingFragment
 */
public class MainDashboardTrainingFragment extends BaseBindingFragment<FragmentMainDashboardTrainingBinding> {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        Log.d("###EGYMMMMM", "@@@@@@@@@@@Egym待上傳資料: ");
        // TODO EGYM
        //有開啟 EGYM 模式
        if (getApp().getDeviceSettingBean().getConsoleSystem() == CONSOLE_SYSTEM_EGYM) {
            if (isNetworkAvailable(getApp())) EgymUtil.getInstance().getEgymDataList();
        }

        try {
            ((MainActivity)requireActivity()).getBinding().bgE.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }


        LiveEventBus.get(WIFI_WORK, Boolean.class).observe(getViewLifecycleOwner(), s -> {
            if (isNetworkAvailable(getApp())) {
                Log.d(EgymUtil.TAG, "有網路,判斷egym: ");
                EgymUtil.getInstance().getEgymDataList();


                // TODO: T&C
//                if (((MainActivity)requireActivity()).isEgymNoData) {
//                    Log.d(EgymUtil.TAG, "重新取得 Plan資料: ");
//                    EgymUtil.getInstance().apiUserDetails();
//                    EgymUtil.getInstance().apiGetTrainingPlans();
//                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
//        ((MainActivity) requireActivity()).stopHandler();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)requireActivity()).webApiGetUnreadMessageCountFromMachine();
      //  ((MainActivity)requireActivity()).setFan(STOP);

        //取消 螢幕不自動關閉  > 會關閉
        requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        MainActivity.isReAssignView = false;

//        ((MainActivity) requireActivity()).startHandler();
//
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (isHidden()) {
//            if (isAdded()) {
//                ((MainActivity) requireActivity()).stopHandler();
//                ((MainActivity) requireActivity()).startHandler();
//            }
        } else{
//            if (isAdded()) {
//                ((MainActivity) requireActivity()).stopHandler();
//                ((MainActivity) requireActivity()).startHandler();
//            }
        }
    }
}