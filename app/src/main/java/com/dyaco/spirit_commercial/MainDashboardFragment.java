package com.dyaco.spirit_commercial;

import static com.dyaco.spirit_commercial.MainActivity.isSummary;
import static com.dyaco.spirit_commercial.MainActivity.isUs;
import static com.dyaco.spirit_commercial.MainActivity.lastMedia;
import static com.dyaco.spirit_commercial.support.CommonUtils.iExc;
import static com.dyaco.spirit_commercial.support.CommonUtils.showException;
import static com.dyaco.spirit_commercial.support.intdef.AppStatusIntDef.CURRENT_PAGE_MEDIA;
import static com.dyaco.spirit_commercial.support.intdef.AppStatusIntDef.CURRENT_PAGE_TRAINING;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.FITNESS_TEST_STOP_WORKOUT;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.MEDIA_GO_TO_TRAINING;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.MEDIA_MENU_GO_TO_MEDIA;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.MEDIA_MENU_GO_TO_MEDIA_2;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.NO_HR_HIDE_BUTTON;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.PAUSE_WORKOUT;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.PROGRAM_CHOOSE;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.RESUME_WORKOUT;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.START_PAUSE;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.START_WORKOUT;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.STEP1_TO_STEP_2;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.STEP2_TO_STEP_1;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.STEP2_TO_STEP_3;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.STEP3_TO_STEP_2;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.STOP_WORKOUT;
import static com.dyaco.spirit_commercial.support.intdef.MainDashboardBottomButtonIntDef.CHOOSE;
import static com.dyaco.spirit_commercial.support.intdef.MainDashboardBottomButtonIntDef.DISAPPEAR;
import static com.dyaco.spirit_commercial.support.intdef.MainDashboardBottomButtonIntDef.JUST_NEXT;
import static com.dyaco.spirit_commercial.support.intdef.MainDashboardBottomButtonIntDef.PREVIOUS_AND_NEXT;
import static com.dyaco.spirit_commercial.support.intdef.MainDashboardBottomButtonIntDef.PREVIOUS_AND_START;
import static com.dyaco.spirit_commercial.support.intdef.MainDashboardBottomButtonIntDef.START_THIS_PROGRAM;
import static com.dyaco.spirit_commercial.support.intdef.MainDashboardBottomButtonIntDef.START_THIS_TEST;
import static com.dyaco.spirit_commercial.support.intdef.MainDashboardBottomButtonIntDef.WORK_OUT_STOP;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.transition.ChangeBounds;
import androidx.transition.TransitionManager;

import com.dyaco.spirit_commercial.dashboard_media.DashboardMainMediaFragment;
import com.dyaco.spirit_commercial.dashboard_training.MainDashboardTrainingFragment;
import com.dyaco.spirit_commercial.databinding.FragmentMainDashboardBinding;
import com.dyaco.spirit_commercial.support.CoTimer;
import com.dyaco.spirit_commercial.support.FormulaUtil;
import com.dyaco.spirit_commercial.support.IdleRunner;
import com.dyaco.spirit_commercial.support.MsgEvent;
import com.dyaco.spirit_commercial.support.RxTimer;
import com.dyaco.spirit_commercial.support.base_component.BaseBindingFragment;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.intdef.AppStatusIntDef;
import com.dyaco.spirit_commercial.support.intdef.EventKey;
import com.dyaco.spirit_commercial.viewmodel.AppStatusViewModel;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingViewModel;
import com.dyaco.spirit_commercial.viewmodel.EgymDataViewModel;
import com.dyaco.spirit_commercial.viewmodel.WorkoutViewModel;
import com.dyaco.spirit_commercial.workout.MainWorkoutTrainingFragment;
import com.dyaco.spirit_commercial.workout.WorkoutPauseFragment;
import com.dyaco.spirit_commercial.workout.WorkoutSummaryWindow;
import com.dyaco.spirit_commercial.workout.programs.ProgramsEnum;
import com.google.android.material.button.MaterialButton;
import com.jeremyliao.liveeventbus.LiveEventBus;

public class MainDashboardFragment extends BaseBindingFragment<FragmentMainDashboardBinding> implements View.OnClickListener {

    private MaterialButton mbTraining;
    private MaterialButton mbMedia;
    private FragmentTransaction fragmentTransaction;
    private ConstraintSet selectedItem1;
    private ConstraintSet selectedItem2;
    private ChangeBounds transition;
    private AppStatusViewModel appStatusViewModel;
    public DeviceSettingViewModel deviceSettingViewModel;
    private WorkoutViewModel workoutViewModel;
    private WorkoutSummaryWindow workoutSummaryWindow;
    private EgymDataViewModel egymDataViewModel;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        appStatusViewModel = new ViewModelProvider(requireActivity()).get(AppStatusViewModel.class);
        egymDataViewModel = new ViewModelProvider(requireActivity()).get(EgymDataViewModel.class);

        getBinding().setAppStatusViewModel(appStatusViewModel);

        //Fatal Exception: java.lang.IllegalStateException: Fragment already added: DashboardMainMediaFragment
        getBinding().mbMedia.setEnabled(false);

        CoTimer.after(2000, () -> {
            if (getBinding() != null) {
                Log.d("MMMMMMMAAAAA", "onViewCreated:  getBinding().mbMedia.setEnabled(true)");
                getBinding().mbMedia.setEnabled(true);
            }
        });

        if (!isSummary) {
            appStatusViewModel.currentStatus.set(AppStatusIntDef.STATUS_IDLE);
        }
        appStatusViewModel.changeMainButtonType(DISAPPEAR);

        deviceSettingViewModel = new ViewModelProvider(requireActivity()).get(DeviceSettingViewModel.class);
        getBinding().setDeviceSetting(deviceSettingViewModel);

        workoutViewModel = new ViewModelProvider(requireActivity()).get(WorkoutViewModel.class);
        getBinding().setWorkoutViewModel(workoutViewModel);


//        DeviceSettingBean a = getApp().getDeviceSettingBean();
//        a.setBrand_name("NNNNNNNNNN");
//
//        getApp().setDeviceSettingBean(a);

     //   Timber.tag("XXXXXXXXXX").d("onViewCreated: " + getApp().getDeviceSettingBean().getBrand_name());



//        workoutViewModel.isAppleWatchEnabled.set(true);
//        workoutViewModel.isAppleWatchConnected.set(true);

//        workoutViewModel.isSamsungWatchEnabled.set(true);
//        workoutViewModel.isSamsungWatchConnected.set(true);


        // TODO:
        workoutViewModel.isGarminConnected.set(true);
        workoutViewModel.garminRespirationRate.set(50);
        workoutViewModel.garminBodyBatteryLevel.set(50);
        workoutViewModel.isWorkoutReadyStart.set(true);

        iExc(() -> {
            if (parent.updateRestartWindow != null) {
                parent.updateRestartWindow.dismiss();
                parent.updateRestartWindow = null;
            }
        });

        IdleRunner.run(() -> {
            initView();
            initEvent();
            initAnimation();
        });

//        Choreographer.getInstance().postFrameCallback(frameTimeNanos -> {
//            initView();
//            initEvent();
//            initAnimation();
//        });

//        new Handler(Looper.getMainLooper()).postDelayed(() -> {
//            initView();
//            initEvent();
//            initAnimation();
//        },500);

//        getBinding().getRoot().post(() -> {
//            initView();
//            initEvent();
//            initAnimation();
//        });

//        Looper.myQueue().addIdleHandler(() -> {
//            initView();
//            initEvent();
//            initAnimation();
//            return false;
//        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initAnimation() {

        mbTraining = getBinding().mbTraining;
        mbMedia = getBinding().mbMedia;

        mbTraining.setOnClickListener(this);
        mbMedia.setOnClickListener(this);

        selectedItem1 = new ConstraintSet();
        selectedItem1.clone(requireActivity(), R.layout.fragment_main_dashboard);

        selectedItem2 = new ConstraintSet();
        selectedItem2.clone(requireActivity(), R.layout.fragment_main_dashboard2);

        transition = new ChangeBounds();
        transition.setInterpolator(new LinearInterpolator());
        transition.setDuration(200);


        mbMedia.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                mbMedia.setAlpha(0.7f);
                return true;
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                view.performClick();
                mbMedia.setAlpha(1f);
                return false;
            } else if (motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
                mbMedia.setAlpha(1f);
                return false;
            } else {
                return false;
            }
        });

        mbTraining.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                mbTraining.setAlpha(0.7f);
                return true;
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                view.performClick();
                mbTraining.setAlpha(1f);
                return false;
            } else if (motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
                mbTraining.setAlpha(1f);
                return false;
            } else {
                return false;
            }
        });


//        mbTraining.setOnTouchListener((view, motionEvent) -> {
//            mbTraining.setAlpha(0.7f);
//            return false;
//        });
    }

    private void initView() {
        Log.d("BBBBBBBBB", "NNNNNNMMMMMMMMMMM: ");
        mainDashboardTrainingFragment = new MainDashboardTrainingFragment();
        dashboardMainMediaFragment = new DashboardMainMediaFragment();
        changeFragment(dashboardMainMediaFragment);
        changeFragment(mainDashboardTrainingFragment);


//        fragmentTransaction = getChildFragmentManager().beginTransaction();
//        fragmentTransaction.add(R.id.dashboardFragmentContainerView, dashboardMainMediaFragment).hide(dashboardMainMediaFragment);
//        fragmentTransaction.add(R.id.dashboardFragmentContainerView, mainDashboardTrainingFragment);
//        fragmentTransaction.commit();
    }

    private void initEvent() {

//        //WorkoutPause時 ResumeIng > Disable Media按鈕
//        LiveEventBus.get(WORKOUT_RESUMING_DISABLE_MEDIA, Boolean.class).observe(getViewLifecycleOwner(), s -> {
//            mbMedia.setEnabled(s);
//        });


        //在SUMMARY狀態時，用指令關閉SUMMARY
        LiveEventBus.get(EventKey.FTMS_STOP_OR_PAUSE).observe(getViewLifecycleOwner(), s -> {
            if (appStatusViewModel.currentStatus.get() == AppStatusIntDef.STATUS_SUMMARY || isSummary) {
                if (WorkoutSummaryWindow.isUploadDone) {
                    parent.uartConsole.setBuzzer();
                    if (workoutSummaryWindow != null) {
                        workoutSummaryWindow.dismiss();
                        workoutSummaryWindow = null;
                    }
                }
            }
        });

        //在SUMMARY狀態時，用指令QUICK START
        LiveEventBus.get(EventKey.EVENT_CLOSE_SUMMARY, Boolean.class).observe(getViewLifecycleOwner(), s -> {
            if (appStatusViewModel.currentStatus.get() == AppStatusIntDef.STATUS_SUMMARY || isSummary) {

                if (workoutSummaryWindow != null) {
                    workoutSummaryWindow.dismiss();
                    workoutSummaryWindow = null;
                }
            }
        });

        LiveEventBus.get(MEDIA_MENU_GO_TO_MEDIA_2, Boolean.class).observe(getViewLifecycleOwner(), s -> {
            parent.closeMedia();
            mbMedia.callOnClick();
        });

        LiveEventBus.get(MEDIA_MENU_GO_TO_MEDIA, Boolean.class).observe(getViewLifecycleOwner(), s -> {
            mbMedia.callOnClick();
        });

        //from FloatingBottomMenuWindow
        LiveEventBus.get(MEDIA_GO_TO_TRAINING, String.class).observe(getViewLifecycleOwner(), s -> {

            Log.d("CCCXXXXXXX", "@@@@@@@@@click: ");
            //關閉Media
            parent.closeMedia();
            mbTraining.callOnClick();
        });

        //開始Workout 換頁
        LiveEventBus.get(START_WORKOUT, String.class).observe(getViewLifecycleOwner(), s -> {

            if (appStatusViewModel.currentStatus.get() == AppStatusIntDef.STATUS_RUNNING) { //在ReadyStart改變
                //Workout開始
                mainWorkoutTrainingFragment = new MainWorkoutTrainingFragment();
                workoutPauseFragment = new WorkoutPauseFragment();

                removeFragment(mainDashboardTrainingFragment);

                changeFragment(workoutPauseFragment);
                changeFragment(mainWorkoutTrainingFragment);

                mainDashboardTrainingFragment = null;
//            } else {
//                mainDashboardTrainingFragment = new MainDashboardTrainingFragment();
//                //Workout結束
//                removeFragment(mainWorkoutTrainingFragment);
//                removeFragment(workoutPauseFragment);
//                mainWorkoutTrainingFragment = null;
//                workoutPauseFragment = null;
            }

            //外部控制時，從Media頁面啟動Workout
            if (appStatusViewModel.currentPage.get() == CURRENT_PAGE_MEDIA) {
                mbMedia.callOnClick();
            } else {
                mbTraining.callOnClick();
            }

            //   mbTraining.callOnClick();
        });

        //Stop  換頁
        LiveEventBus.get(STOP_WORKOUT, String.class).observe(getViewLifecycleOwner(), s -> {

            if (workoutSummaryWindow != null) {
                workoutSummaryWindow.dismiss();
                workoutSummaryWindow = null;
            }


//            new Thread(() -> {
//                removeFragment(workoutPauseFragment);
//                workoutPauseFragment = null;
//                removeFragment(mainWorkoutTrainingFragment);
//                mainWorkoutTrainingFragment = null;
//            }).start();

            removeFragment(workoutPauseFragment);
            workoutPauseFragment = null;
            removeFragment(mainWorkoutTrainingFragment);
            mainWorkoutTrainingFragment = null;


            mainDashboardTrainingFragment = new MainDashboardTrainingFragment();

            lastMedia = null;
            if (appStatusViewModel.isMediaPlaying.get()) {
                mbMedia.callOnClick();
            } else {
                mbTraining.callOnClick();
            }

            //   ((MainActivity) requireActivity()).showYouTube(false);

            //開啟Summary
            workoutSummaryWindow = new WorkoutSummaryWindow(requireActivity(), workoutViewModel, deviceSettingViewModel, appStatusViewModel, egymDataViewModel);
            workoutSummaryWindow.showAtLocation(requireActivity().getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);
            workoutSummaryWindow.setOnCustomDismissListener(new BasePopupWindow.OnCustomDismissListener() {
                @Override
                public void onStartDismiss(MsgEvent value) {
                    isSummary = false;
                    appStatusViewModel.currentStatus.set(AppStatusIntDef.STATUS_IDLE);
                }

                @Override
                public void onDismiss() {
                    workoutSummaryWindow = null;
                    new FormulaUtil().clearWorkoutViewModel(workoutViewModel);

                    new FormulaUtil().clearEgymViewModel(egymDataViewModel);

//                    if (appStatusViewModel.isMediaPlaying.get()) {
//                        Log.d("BBBBBBVVVVV", "??????????: " + appStatusViewModel.isMediaPlaying.get());
//                        mbMedia.callOnClick();
//                    } else {
//                        mbTraining.callOnClick();
//                    }


                }
            });

            new RxTimer().timer(800, c ->
                    ((MainActivity) requireActivity()).showLoadingEx(false, 200));
        });

        //resume 換頁  MainWorkoutTrainingFragment也會收到
        LiveEventBus.get(RESUME_WORKOUT, String.class).observe(getViewLifecycleOwner(), s -> {


            //  mbMedia.setEnabled(true);

            //如果目前是Media頁面，就不換
//            if (appStatusViewModel.isMediaPauseOrResume.get()) {
//                appStatusViewModel.isMediaPauseOrResume.set(false);


            if (appStatusViewModel.currentPage.get() == CURRENT_PAGE_MEDIA) {
                return;
            }

            //若是在Training頁籤，將PauseFragment換成WorkoutTrainingFragment
            //  if (appStatusViewModel.currentPage.get() == CURRENT_PAGE_TRAINING) {
            changeFragment(mainWorkoutTrainingFragment);
            //  mbTraining.callOnClick();
            //   }

        });

        //pause 換頁  MainWorkoutTrainingFragment也會收到
        LiveEventBus.get(PAUSE_WORKOUT, String.class).observe(getViewLifecycleOwner(), s -> {

            LiveEventBus.get(START_PAUSE).post(true);


            //當前頁面為Media，就不跳到Pause頁面
            if (appStatusViewModel.currentStatus.get() == AppStatusIntDef.STATUS_PAUSE &&
                    appStatusViewModel.currentPage.get() == CURRENT_PAGE_MEDIA) return;

            changeFragment(workoutPauseFragment);

        });

        //沒hr就不能啟動的program
        LiveEventBus.get(NO_HR_HIDE_BUTTON, Boolean.class).observe(getViewLifecycleOwner(), s -> {

            if (s) {
                getBinding().btnStart123.setTextColor(ContextCompat.getColorStateList(requireActivity(), R.color.color5a7085));
                getBinding().btnStart123.setBackgroundTintList(ContextCompat.getColorStateList(requireActivity(), R.color.color1c242a));
                getBinding().btnStart123.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.color1c242a));
                getBinding().btnStart123.setIconTint(ContextCompat.getColorStateList(requireActivity(), R.color.color5a7085));
                getBinding().btnStart123.setClickable(false);
            } else {
                getBinding().btnStart123.setTextColor(ContextCompat.getColorStateList(requireActivity(), R.color.white));
                getBinding().btnStart123.setIconTint(ContextCompat.getColorStateList(requireActivity(), R.color.white));
                getBinding().btnStart123.setClickable(true);


                if (workoutViewModel.selProgram != ProgramsEnum.HIIT) {

                    if (getBinding().btnStart123.getText().toString().equalsIgnoreCase(getString(R.string.Select_This_Program))) {
                        getBinding().btnStart123.setBackgroundTintList(ContextCompat.getColorStateList(requireActivity(), R.color.btn_click_1396ef));
//                        getBinding().btnStart123.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.color1396ef));
                    } else {
                        getBinding().btnStart123.setBackgroundTintList(ContextCompat.getColorStateList(requireActivity(), R.color.btn_click_0dac87));
//                        getBinding().btnStart123.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.color0dac87));
                    }
                } else {
                    getBinding().btnStart123.setBackgroundTintList(ContextCompat.getColorStateList(requireActivity(), R.color.btn_click_1396ef));
//                    getBinding().btnStart123.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.color1396ef));
                }

            }
        });
//        appStatusViewModel.getMainButtonType().observe(getViewLifecycleOwner(), item -> {
//            Log.d("OEOFOEOFEO", "11111initEvent: " + item);
//        });

        appStatusViewModel.getMainButtonType.observe(getViewLifecycleOwner(), item -> {

            switch (item) {
                case DISAPPEAR:
                    new RxTimer().timer(100, number -> {
                        getBinding().btnStart123.setTextColor(ContextCompat.getColorStateList(requireActivity(), R.color.white));
                        getBinding().btnStart123.setBackgroundTintList(ContextCompat.getColorStateList(requireActivity(), R.color.btn_click_0dac87));
                        getBinding().btnStart123.setOnClickListener(null);
                        getBinding().groupMain.setVisibility(View.VISIBLE);
                        getBinding().group123.setVisibility(View.GONE);
                        getBinding().group45.setVisibility(View.GONE);
                        getBinding().btnWorkoutStop.setVisibility(View.GONE);
                    });
                    break;
                case START_THIS_PROGRAM:
                    getBinding().groupMain.setVisibility(View.GONE);
                    getBinding().btnWorkoutStop.setVisibility(View.GONE);
                    getBinding().group123.setVisibility(View.VISIBLE);
                    getBinding().group45.setVisibility(View.GONE);
                    getBinding().btnStart123.setText(R.string.Start_This_Program);
                    getBinding().btnStart123.setIconPadding(-12);
                    getBinding().btnStart123.setIconGravity(MaterialButton.ICON_GRAVITY_TEXT_START);
                    getBinding().btnStart123.setIconTint(ContextCompat.getColorStateList(requireActivity(), R.color.white));
                    getBinding().btnStart123.setTextColor(ContextCompat.getColorStateList(requireActivity(), R.color.white));
                    getBinding().btnStart123.setBackgroundTintList(ContextCompat.getColorStateList(requireActivity(), R.color.btn_click_0dac87));
                    getBinding().btnStart123.setIcon(ContextCompat.getDrawable(requireActivity(), R.drawable.icon_start));
                    getBinding().btnStart123.setOnClickListener(v -> goToWorkout());
                    break;
                case START_THIS_TEST:
                    getBinding().groupMain.setVisibility(View.GONE);
                    getBinding().btnWorkoutStop.setVisibility(View.GONE);
                    getBinding().group123.setVisibility(View.VISIBLE);
                    getBinding().group45.setVisibility(View.GONE);
                    getBinding().btnStart123.setIconPadding(-12);
                    getBinding().btnStart123.setIconGravity(MaterialButton.ICON_GRAVITY_TEXT_START);
                    getBinding().btnStart123.setText(R.string.Start_This_Test);
                    getBinding().btnStart123.setIconTint(ContextCompat.getColorStateList(requireActivity(), R.color.white));
                    getBinding().btnStart123.setTextColor(ContextCompat.getColorStateList(requireActivity(), R.color.white));
                    getBinding().btnStart123.setBackgroundTintList(ContextCompat.getColorStateList(requireActivity(), R.color.btn_click_0dac87));
                    getBinding().btnStart123.setIcon(ContextCompat.getDrawable(requireActivity(), R.drawable.icon_start));
                    getBinding().btnStart123.setOnClickListener(v -> goToWorkout());
                    break;
                case JUST_NEXT:
                    getBinding().groupMain.setVisibility(View.GONE);
                    getBinding().group123.setVisibility(View.VISIBLE);
                    getBinding().group45.setVisibility(View.INVISIBLE);
                    getBinding().btnStart123.setBackgroundTintList(ContextCompat.getColorStateList(requireActivity(), R.color.btn_click_1396ef));
                    getBinding().btnStart123.setIcon(null);
                    getBinding().btnStart123.setText(R.string.Next);
                    getBinding().btnStart123.setAlpha(1f);//HIIT
                    getBinding().btnStart123.setOnClickListener(v ->
                            appStatusViewModel.selectSetTimeFragmentNavigate(STEP1_TO_STEP_2));


                    break;

                case PREVIOUS_AND_NEXT:
                    getBinding().btnStart123.setOnClickListener(null);
                    getBinding().group45.setVisibility(View.VISIBLE);
                    getBinding().btnWorkoutStop.setVisibility(View.GONE);
                    getBinding().groupMain.setVisibility(View.GONE);
                    getBinding().group123.setVisibility(View.GONE);
                    getBinding().btn45Two.setText(R.string.Next);
                    getBinding().btn45Two.setIcon(null);
                    getBinding().btn45Two.setBackgroundTintList(ContextCompat.getColorStateList(requireActivity(), R.color.btn_click_1396ef));
                    // getBinding().btn45Two.setAlpha(1f);
                    getBinding().btn45One.setOnClickListener(v ->
                            appStatusViewModel.selectSetTimeFragmentNavigate(STEP2_TO_STEP_1));
                    getBinding().btn45Two.setOnClickListener(v ->
                            appStatusViewModel.selectSetTimeFragmentNavigate(STEP2_TO_STEP_3));
                    break;
                case PREVIOUS_AND_START:
                    getBinding().groupMain.setVisibility(View.GONE);
                    getBinding().group123.setVisibility(View.GONE);
                    getBinding().group45.setVisibility(View.VISIBLE);
                    getBinding().btn45Two.setText(R.string.Start_This_Program);
                    getBinding().btn45Two.setIconPadding(-12);
                    getBinding().btn45Two.setIcon(ContextCompat.getDrawable(requireActivity(), R.drawable.icon_start));
                    getBinding().btn45Two.setBackgroundTintList(ContextCompat.getColorStateList(requireActivity(), R.color.btn_click_1396ef));
                    getBinding().btn45One.setOnClickListener(v -> {
                        appStatusViewModel.selectSetTimeFragmentNavigate(STEP3_TO_STEP_2);
                    });
                    getBinding().btn45Two.setOnClickListener(v -> goToWorkout());
                    break;

                case WORK_OUT_STOP:
                    //    closePackage(requireActivity());

                    if (isUs) {
                        //美版 Gerkin, WFI, CttPerformance, CttPrediction 中間頁籤大小跟國際版一樣
                        getBinding().btnNoView.setVisibility(View.VISIBLE);
                    } else {
                        getBinding().btnWorkoutStop.setVisibility(View.VISIBLE);
                        getBinding().btnWorkoutStop.setOnClickListener(v ->
                                LiveEventBus.get(FITNESS_TEST_STOP_WORKOUT).post(true));
                    }

                    break;

                case CHOOSE:
                    getBinding().groupMain.setVisibility(View.GONE);
                    getBinding().btnWorkoutStop.setVisibility(View.GONE);
                    getBinding().group123.setVisibility(View.VISIBLE);
                    getBinding().group45.setVisibility(View.GONE);
                    getBinding().btnStart123.setText(R.string.Select_This_Program);
//                    getBinding().btnStart123.setIconTint(ContextCompat.getColorStateList(requireActivity(), R.color.white));
                    getBinding().btnStart123.setTextColor(ContextCompat.getColorStateList(requireActivity(), R.color.white));
                    getBinding().btnStart123.setBackgroundTintList(ContextCompat.getColorStateList(requireActivity(), R.color.btn_click_1396ef));
                    //      getBinding().btnStart123.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.color1396ef));
                    getBinding().btnStart123.setIcon(null);
                    //       getBinding().btnStart123.setIcon(ContextCompat.getDrawable(requireActivity(), R.drawable.icon_next_26));
//                    getBinding().btnStart123.setIconPadding(22);
//                    getBinding().btnStart123.setIconGravity(ICON_GRAVITY_TEXT_END);
                    getBinding().btnStart123.setOnClickListener(v -> LiveEventBus.get(PROGRAM_CHOOSE).post(true));
                    break;
            }
        });

    }

    private Fragment m_currentFragment;
    private MainDashboardTrainingFragment mainDashboardTrainingFragment;
    private DashboardMainMediaFragment dashboardMainMediaFragment;

    //WorkOut
    private MainWorkoutTrainingFragment mainWorkoutTrainingFragment;

    private WorkoutPauseFragment workoutPauseFragment;

    private void changeFragment(Fragment fragment) {

        try {
            if (m_currentFragment != fragment) {
                fragmentTransaction = getChildFragmentManager().beginTransaction();
                //動畫
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                //  fragmentTransaction.setTransition(TRANSIT_FRAGMENT_FADE);

                if (m_currentFragment != null)
                    fragmentTransaction.hide(m_currentFragment);

                if (fragment.isAdded()) {
                    fragmentTransaction.show(fragment).commitAllowingStateLoss();
                    //  fragmentTransaction.show(fragment).commit();
                } else {

                    //  fragmentTransaction.remove(fragment).commitAllowingStateLoss();

                    fragmentTransaction.add(R.id.dashboardFragmentContainerView, fragment).commitAllowingStateLoss();
                    //            fragmentTransaction.add(R.id.dashboardFragmentContainerView, fragment).commitNowAllowingStateLoss();
                    //  fragmentTransaction.add(R.id.fragmentContainerView, fragment).commit();


                    /**
                     方法	是否立即執行	支援動畫	可用時機	是否允許狀態損失
                     commitAllowingStateLoss()	❌（排入主線程）	✅ 是	多數情境	✅ 是
                     commitNowAllowingStateLoss()	✅（立即執行）	❌ 否	初始化流程中	✅ 是
                     */
                }
                m_currentFragment = fragment;
            }
        } catch (Exception e) {
            showException(e);
        }
    }


    private static final String TAG = "MainDashboardFragment";

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    public void onClick(View v) {
        TransitionManager.beginDelayedTransition(getBinding().constraintLayout, transition);
        if (v.getId() == R.id.mb_training) {

            appStatusViewModel.currentPage.set(CURRENT_PAGE_TRAINING);

            selectedItem1.applyTo(getBinding().constraintLayout);
            mbTraining.setTextColor(ContextCompat.getColor(requireActivity(), R.color.color1396ef));
            mbMedia.setTextColor(ContextCompat.getColor(requireActivity(), R.color.colorADB8C2));
            mbTraining.setIconTint(ContextCompat.getColorStateList(requireActivity(), R.color.color1396ef));
            mbMedia.setIconTint(ContextCompat.getColorStateList(requireActivity(), R.color.colorADB8C2));

            if (appStatusViewModel.currentStatus.get() == AppStatusIntDef.STATUS_PAUSE) {
                changeFragment(workoutPauseFragment);
            } else if (appStatusViewModel.currentStatus.get() == AppStatusIntDef.STATUS_RUNNING) {
                changeFragment(mainWorkoutTrainingFragment);
            } else {
                changeFragment(mainDashboardTrainingFragment);
            }

//            if (userProfileViewModel.userType.get() == USER_TYPE_GUEST) {
//                //在Media頁面 結束Workout
//                Log.d("PPPEPEPPEPPEE", "22222initGuestData: ");
//                LiveEventBus.get(INIT_GUEST_DATA).post(true);
//            }

//            //關閉Media
//            parent.showMediaMenu(false);
//            parent.showMediaFloatingDashboard(false);
//            parent.showYouTube(false);

        } else if (v.getId() == R.id.mb_media) {

            appStatusViewModel.currentPage.set(CURRENT_PAGE_MEDIA);

            selectedItem2.applyTo(getBinding().constraintLayout);
            mbMedia.setTextColor(ContextCompat.getColor(requireActivity(), R.color.color1396ef));
            mbTraining.setTextColor(ContextCompat.getColor(requireActivity(), R.color.colorADB8C2));
            mbMedia.setIconTint(ContextCompat.getColorStateList(requireActivity(), R.color.color1396ef));
            mbTraining.setIconTint(ContextCompat.getColorStateList(requireActivity(), R.color.colorADB8C2));
            changeFragment(dashboardMainMediaFragment);
        }
    }


    private void removeFragment(Fragment fragment) {
        fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.remove(fragment).commitAllowingStateLoss();
    }

    @Override
    public void onDestroyView() {


        mbTraining = null;
        mbMedia = null;
        fragmentTransaction = null;
        selectedItem1 = null;
        selectedItem2 = null;
        transition = null;
        mainDashboardTrainingFragment = null;
        dashboardMainMediaFragment = null;
        mainWorkoutTrainingFragment = null;
        workoutPauseFragment = null;
        m_currentFragment = null;



        super.onDestroyView();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fragmentTransaction = null;
    }

    private void goToWorkout() {
        parent.startWorkout(false);
    }


//    private void showDashboard(boolean s) {
//
//        getBinding().mbMedia.setVisibility(s ? View.VISIBLE : View.GONE);
//        getBinding().mbTraining.setVisibility(s ? View.VISIBLE : View.GONE);
//        getBinding().bbg.setVisibility(s ? View.VISIBLE : View.GONE);
//    }
}