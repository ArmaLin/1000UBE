package com.dyaco.spirit_commercial.dashboard_media;

import static com.dyaco.spirit_commercial.support.intdef.GENERAL.MEDIA_PHYSICAL_KEYS;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.dyaco.spirit_commercial.alert_message.WarringNoButtonWindow;
import com.dyaco.spirit_commercial.databinding.FragmentDashboardMainMediaBinding;
import com.dyaco.spirit_commercial.support.MsgEvent;
import com.dyaco.spirit_commercial.support.base_component.BaseBindingFragment;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.intdef.AppStatusIntDef;
import com.dyaco.spirit_commercial.viewmodel.AppStatusViewModel;
import com.dyaco.spirit_commercial.viewmodel.WorkoutViewModel;

/**
 * DashboardMediaFragment
 */
public class DashboardMainMediaFragment extends BaseBindingFragment<FragmentDashboardMainMediaBinding> {

    private AppStatusViewModel appStatusViewModel;
    private WorkoutViewModel workoutViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        appStatusViewModel = new ViewModelProvider(requireActivity()).get(AppStatusViewModel.class);
        workoutViewModel = new ViewModelProvider(requireActivity()).get(WorkoutViewModel.class);

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (parent == null) return;

        if (parent.popupWindow != null) {
            parent.popupWindow.dismiss();
            parent.popupWindow = null;
        }

        //Workout時切換到Media跳出提示視窗
        if (!hidden) {
            if (appStatusViewModel.currentStatus.get() == AppStatusIntDef.STATUS_RUNNING || appStatusViewModel.currentStatus.get() == AppStatusIntDef.STATUS_PAUSE) {
                //   parent.popupWindow = new ModalPhysicalKeysWindow(requireActivity());
                parent.popupWindow = new WarringNoButtonWindow(requireActivity(), MEDIA_PHYSICAL_KEYS, workoutViewModel);
                parent.popupWindow.showAtLocation(parent.getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);
                ((WarringNoButtonWindow) parent.popupWindow).setOnCustomDismissListener(new BasePopupWindow.OnCustomDismissListener() {
                    @Override
                    public void onStartDismiss(MsgEvent value) {
                    }

                    @Override
                    public void onDismiss() {
                        parent.popupWindow = null;
                    }
                });
            }
            parent.hrNotifyWarringWindow(false);
        }
    }
}