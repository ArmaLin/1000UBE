package com.dyaco.spirit_commercial.dashboard_media;

import static com.dyaco.spirit_commercial.App.getApp;
import static com.dyaco.spirit_commercial.support.CommonUtils.updateTime;

import android.content.Context;
import android.util.Log;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.databinding.ActivityMainBinding;
import com.dyaco.spirit_commercial.databinding.WindowFloatingTopDashboardBinding;
import com.dyaco.spirit_commercial.support.CommonUtils;
import com.bumptech.glide.Glide;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;
import com.dyaco.spirit_commercial.viewmodel.AppStatusViewModel;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingViewModel;
import com.dyaco.spirit_commercial.viewmodel.UserProfileViewModel;
import com.dyaco.spirit_commercial.viewmodel.WorkoutViewModel;

/**
 *
 *
 *
 *
 *
 *
 *
 *
 * 沒用到
 */
public class FloatingTopDashBoardWindow extends BasePopupWindow<WindowFloatingTopDashboardBinding>{

    private final ActivityMainBinding mainBinding;

    public FloatingTopDashBoardWindow(Context context, AppStatusViewModel statusViewModel, WorkoutViewModel workoutViewModel, DeviceSettingViewModel deviceSettingViewModel, UserProfileViewModel userProfileViewModel,boolean isEnabled) {
        super(context, 10, 80, 0, GENERAL.FADE, false, false,true,true);

        mainBinding = ((MainActivity) context).getBinding();
        getBinding().setAppStatusViewModel(statusViewModel);
        getBinding().setWorkoutData(workoutViewModel);
        getBinding().setDeviceSetting(deviceSettingViewModel);
        getBinding().setUserProfileViewModel(userProfileViewModel);
        getBinding().setComm(new CommonUtils());

        getBinding().tvTime.setText(updateTime());

        if (userProfileViewModel.getAvatarId() != null) {
            int avatarRes = CommonUtils.getAvatarSelectedFromTag(userProfileViewModel.getAvatarId(), false);
            Glide.with(getApp())
                    .load(avatarRes)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(getBinding().ivMemberIcon);
        }

        if (userProfileViewModel.getPhotoFileUrl() != null) {
            Glide.with(getApp())
                    .load(userProfileViewModel.getPhotoFileUrl())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .circleCrop()
                    .into(getBinding().ivMemberIcon);
        }

        if (isEnabled) {
            initEvent();
        }
    }


    private void initEvent() {

     //   getBinding().ibAlert.setOnClickListener(v -> mainBinding.ibAlert.callOnClick());
        getBinding().ibSetting.setOnClickListener(v -> mainBinding.ibSetting.callOnClick());
       // getBinding().tvTime.setOnClickListener(v -> mainBinding.tvTime.callOnClick());

//        getBinding().ibWifi.setOnClickListener(v -> mainBinding.ibWifi.callOnClick());
        getBinding().btnBT.setOnClickListener(v -> mainBinding.btnBT.callOnClick());

     //   getBinding().btnFan.setOnClickListener(v -> mainBinding.btnFan.callOnClick());
        getBinding().btnHR.setOnClickListener(v -> mainBinding.btnHR.callOnClick());
        getBinding().btnMemberProfile.setOnClickListener(v -> mainBinding.btnMemberProfile.callOnClick());
        getBinding().btnSound.setOnClickListener(v -> mainBinding.btnSound.callOnClick());
        getBinding().btnLogout.setOnClickListener(v -> {
            mainBinding.btnLogout.callOnClick();
            dismiss();
        });
        getBinding().ibQrCode.setOnClickListener(v -> mainBinding.ibQrCode.callOnClick());
        getBinding().btnGarmin.setOnClickListener(v -> mainBinding.btnGarmin.callOnClick());

    //    LiveEventBus.get(EVENT_TIME_TICK, Boolean.class).observeForever(observer);
    }

//    //讓stop時也能收到
//    Observer<Boolean> observer = s -> {
//        if (getBinding() != null) {
//            getBinding().tvTime.setText(updateTime());
//        }
//    };

    @Override
    public void dismiss() {
        super.dismiss();
     //   LiveEventBus.get(EVENT_TIME_TICK, Boolean.class).removeObserver(observer);
        Log.d("MainDashboardFragment", "dismiss: ");
    }
}
