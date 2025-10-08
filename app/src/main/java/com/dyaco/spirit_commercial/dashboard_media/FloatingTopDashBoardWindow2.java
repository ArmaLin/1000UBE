package com.dyaco.spirit_commercial.dashboard_media;

import static com.dyaco.spirit_commercial.App.getApp;

import static com.dyaco.spirit_commercial.MainActivity.isSummary;
import static com.dyaco.spirit_commercial.MainActivity.isUs;
import static com.dyaco.spirit_commercial.MainActivity.userProfileViewModel;
import static com.dyaco.spirit_commercial.support.CommonUtils.showException;
import static com.dyaco.spirit_commercial.support.CommonUtils.updateTime;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.USER_TYPE_EGYM;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;

import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.ActivityMainBinding;
import com.dyaco.spirit_commercial.databinding.WindowFloatingTopDashboardBinding;
import com.dyaco.spirit_commercial.support.CommonUtils;
import com.dyaco.spirit_commercial.support.GlideApp;
import com.dyaco.spirit_commercial.support.base_component.BaseWindow;
import com.dyaco.spirit_commercial.support.intdef.AppStatusIntDef;
import com.dyaco.spirit_commercial.viewmodel.AppStatusViewModel;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingViewModel;
import com.dyaco.spirit_commercial.viewmodel.EgymDataViewModel;
import com.dyaco.spirit_commercial.viewmodel.UserProfileViewModel;
import com.dyaco.spirit_commercial.viewmodel.WorkoutViewModel;


public class FloatingTopDashBoardWindow2 extends BaseWindow<WindowFloatingTopDashboardBinding> {

    private final ActivityMainBinding mainBinding;
    private final AppStatusViewModel appStatusViewModel;
    private final WorkoutViewModel workoutViewModel;
    private final EgymDataViewModel egymDataViewModel;

    public FloatingTopDashBoardWindow2(Context context, AppStatusViewModel statusViewModel, WorkoutViewModel workoutViewModel, DeviceSettingViewModel deviceSettingViewModel, UserProfileViewModel userProfileViewModel, EgymDataViewModel egymDataViewModel, boolean isEnabled) {
        super(context, WindowManager.LayoutParams.MATCH_PARENT, 80, Gravity.TOP, 0, 0);

        mainBinding = ((MainActivity) context).getBinding();
        getBinding().setAppStatusViewModel(statusViewModel);
        getBinding().setWorkoutData(workoutViewModel);
        getBinding().setDeviceSetting(deviceSettingViewModel);
        getBinding().setUserProfileViewModel(userProfileViewModel);
        getBinding().setComm(new CommonUtils());

        getBinding().setIsUs(isUs);

        this.appStatusViewModel = statusViewModel;
        this.workoutViewModel = workoutViewModel;
        this.egymDataViewModel = egymDataViewModel;

        getBinding().tvTime.setText(updateTime());

        setAvatar();
//        if (userProfileViewModel.getAvatarId() != null) {
//            int avatarRes = CommonUtils.getAvatarSelectedFromTag(userProfileViewModel.getAvatarId(), false);
//            GlideApp.with(getApp())
//                    .load(avatarRes)
////                    .diskCacheStrategy(DiskCacheStrategy.NONE)
////                    .skipMemoryCache(true)
//                    .into(getBinding().ivMemberIcon);
//        }
//
//        if (userProfileViewModel.getPhotoFileUrl() != null) {
//            GlideApp.with(getApp())
//                    .load(userProfileViewModel.getPhotoFileUrl())
//                 //   .diskCacheStrategy(DiskCacheStrategy.NONE)
//                  //  .skipMemoryCache(true)
//                    .circleCrop()
//                    .into(getBinding().ivMemberIcon);
//        }

        if (isEnabled) {
            initEvent();
        }


    }

    public void setAvatar() {
        if (userProfileViewModel.getAvatarId() != null) {
            int avatarRes = CommonUtils.getAvatarSelectedFromTag(userProfileViewModel.getAvatarId(), false);
            GlideApp.with(getApp())
                    .load(avatarRes)
//                    .diskCacheStrategy(DiskCacheStrategy.NONE)
//                    .skipMemoryCache(true)
                    .error(R.drawable.avatar_normal_1_default)
                    .into(getBinding().ivMemberIcon);

        } else {
            try {
                GlideApp.with(getApp())
                        .load(userProfileViewModel.getPhotoFileUrl())
                        //   .diskCacheStrategy(DiskCacheStrategy.NONE)
                        //  .skipMemoryCache(true)
                        .circleCrop()
                        .error(R.drawable.avatar_normal_1_default)
                        .into(getBinding().ivMemberIcon);
            } catch (Exception e) {
               showException(e);
            }
        }


        if (userProfileViewModel.userType.get() == USER_TYPE_EGYM) {
            GlideApp.with(getApp())
                    .load(egymDataViewModel.userImg.get())
                    .circleCrop()
                    .placeholder(R.color.color252e37)
                    .error(R.drawable.avatar_normal_1_default)
                    .into(getBinding().ivMemberIcon);
        }


//        if (userProfileViewModel.getPhotoFileUrl() != null) {
//            GlideApp.with(getApp())
//                    .load(userProfileViewModel.getPhotoFileUrl())
//                    //   .diskCacheStrategy(DiskCacheStrategy.NONE)
//                    //  .skipMemoryCache(true)
//                    .circleCrop()
//                    .into(getBinding().ivMemberIcon);
//        }
    }

    public void enableView() {
        initEvent();
    }

    private void showGarmin() {
        //非Workout期間，開Media時不能按Garmin，不然Float的GarminWindow會蓋掉配對輸入Pin視窗
        if (appStatusViewModel.currentStatus.get() != AppStatusIntDef.STATUS_IDLE && !isSummary) {
            mainBinding.ivGarmin.callOnClick();
        }
    }

    private void initEvent() {

        // getBinding().ibAlert.setOnClickListener(v -> mainBinding.ibAlert.callOnClick());
        getBinding().ibSetting.setOnClickListener(v -> mainBinding.ibSetting.callOnClick());

//        if (appStatusViewModel.currentStatus.get() == AppStatusIntDef.STATUS_IDLE ||
//                appStatusViewModel.currentStatus.get() == AppStatusIntDef.STATUS_SUMMARY) {
//            getBinding().ivGarmin.setAlpha();
//        }
        //android:alpha="@{AppStatusViewModel.currentStatus == status.STATUS_IDLE || AppStatusViewModel.currentStatus == status.STATUS_SUMMARY ? 0.5f : 1f}"

        getBinding().ivGarmin.setOnClickListener(v -> showGarmin());
        getBinding().tvStress.setOnClickListener(v -> showGarmin());
        getBinding().tvStressUnit.setOnClickListener(v -> showGarmin());
        getBinding().tvSpo2.setOnClickListener(v -> showGarmin());
        getBinding().tvSpo2Unit.setOnClickListener(v -> showGarmin());

        getBinding().btnAppleWatch.setOnClickListener(view -> mainBinding.btnAppleWatch.callOnClick());
        getBinding().ivAppleWatchEnabled.setOnClickListener(view -> mainBinding.ivAppleWatchEnabled.callOnClick());

        // getBinding().tvTime.setOnClickListener(v -> mainBinding.tvTime.callOnClick());

//        getBinding().ibWifi.setOnClickListener(v -> mainBinding.ibWifi.callOnClick());
        getBinding().btnBT.setOnClickListener(v -> mainBinding.btnBT.callOnClick());

        //   getBinding().btnFan.setOnClickListener(v -> mainBinding.btnFan.callOnClick());
        getBinding().btnHR.setOnClickListener(v -> mainBinding.btnHR.callOnClick());
        getBinding().btnMemberProfile.setOnClickListener(v -> mainBinding.btnMemberProfile.callOnClick());
        getBinding().btnSound.setOnClickListener(v -> mainBinding.btnSound.callOnClick());
        getBinding().btnLogout.setOnClickListener(v -> {
            Log.d("AMITY_TRACE 144", "LOG OUT");

            if (!workoutViewModel.isWorkoutReadyStart.get()) return;
            mainBinding.btnLogout.callOnClick();
            dismiss();
        });
        getBinding().ibQrCode.setOnClickListener(v -> mainBinding.ibQrCode.callOnClick());

//        getBinding().btnGarmin.setClickable(false);
//        getBinding().btnGarmin.setAlpha(0.5f);

        //   getBinding().btnGarmin.setOnClickListener(v -> mainBinding.btnGarmin.callOnClick());
    }

    @Override
    public void dismiss() {
        super.dismiss();
        Log.d("@@@@@@@@", "dismiss: ");
    }

}
