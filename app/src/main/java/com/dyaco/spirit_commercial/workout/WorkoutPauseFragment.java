package com.dyaco.spirit_commercial.workout;

import static com.dyaco.spirit_commercial.App.getApp;
import static com.dyaco.spirit_commercial.MainActivity.isEmulator;
import static com.dyaco.spirit_commercial.MainActivity.isTreadmill;
import static com.dyaco.spirit_commercial.support.CommonUtils.formatSecToM;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.ON;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.CLOSE_SETTINGS;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.FTMS_START_OR_RESUME;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.FTMS_STOP_OR_PAUSE;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.MEDIA_MENU_CHANGE;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.MEDIA_PAUSE_WORKOUT;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.MEDIA_RESUME_WORKOUT;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.RESUME_WORKOUT;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.START_PAUSE;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.STOP_WORKOUT;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_PAUSE_STANDBY;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.corestar.libs.device.DeviceGEM;
import com.dyaco.spirit_commercial.App;
import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.alert_message.ResumingWindow;
import com.dyaco.spirit_commercial.databinding.FragmentWorkoutPauseBinding;
import com.dyaco.spirit_commercial.support.CommonUtils;
import com.dyaco.spirit_commercial.support.MsgEvent;
import com.dyaco.spirit_commercial.support.RxTimer;
import com.dyaco.spirit_commercial.support.base_component.BaseBindingFragment;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.custom_view.CustomToast;
import com.dyaco.spirit_commercial.support.intdef.AppStatusIntDef;
import com.dyaco.spirit_commercial.support.utils.CheckDoubleClick;
import com.dyaco.spirit_commercial.viewmodel.AppStatusViewModel;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingViewModel;
import com.dyaco.spirit_commercial.viewmodel.WorkoutViewModel;
import com.dyaco.spirit_commercial.workout.programs.ProgramsEnum;
import com.jeremyliao.liveeventbus.LiveEventBus;

import java.util.HashMap;
import java.util.Map;


public class WorkoutPauseFragment extends BaseBindingFragment<FragmentWorkoutPauseBinding> {
    public static boolean isResuming = false;
    private RxTimer resumeTimer; //倒數開始
    private AppStatusViewModel appStatusViewModel;
    private WorkoutViewModel workoutViewModel;
    private DeviceSettingViewModel deviceSettingViewModel;
    private ResumingWindow resumingWindow;
    private boolean isMediaResume = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getBinding().setIsTreadmill(isTreadmill);

        appStatusViewModel = new ViewModelProvider(requireActivity()).get(AppStatusViewModel.class);
        getBinding().setAppStatusViewModel(appStatusViewModel);

        workoutViewModel = new ViewModelProvider(requireActivity()).get(WorkoutViewModel.class);
        getBinding().setWorkoutData(workoutViewModel);

        deviceSettingViewModel = new ViewModelProvider(requireActivity()).get(DeviceSettingViewModel.class);
        getBinding().setDeviceSetting(deviceSettingViewModel);

        getBinding().setComm(new CommonUtils());

        initView();
        initEvent();

        initExControl();

        if (workoutViewModel.selProgram == ProgramsEnum.EGYM) {
            getBinding().btnCoolDown.setVisibility(View.GONE);
        }


        LiveEventBus.get(START_PAUSE).observe(getViewLifecycleOwner(), s -> {
//            if (appStatusViewModel.currentStatus.get() == AppStatusIntDef.STATUS_SUMMARY) {
//                getBinding().btnFinish.callOnClick();
//            } else {
//                pauseTimer();
//            }
            pauseTimer();
        });


    }

    private void initExControl() {
        LiveEventBus.get(FTMS_STOP_OR_PAUSE).observe(getViewLifecycleOwner(), s -> {
            if (CheckDoubleClick.isFastClick()) return;
            if (workoutViewModel.isWarmUpIng.get() || workoutViewModel.isCoolDowning.get()) return;
            if (appStatusViewModel.currentStatus.get() == AppStatusIntDef.STATUS_PAUSE) {
                //Pause 狀態，執行 Stop，結束 Workout
                getBinding().btnFinish.callOnClick();
                ((MainActivity) requireActivity()).uartConsole.setBuzzer();
            } else {
                //Workout Running狀態，且 Resume 倒數中，停止倒數，回到 Pause狀態
                if (isResuming) {
                    if (appStatusViewModel.isMediaPlaying.get()) return; //Media執行中，不給取消
                    getBinding().btnResume.callOnClick();
                    ((MainActivity) requireActivity()).uartConsole.setBuzzer();
                }
            }
        });

        LiveEventBus.get(FTMS_START_OR_RESUME).observe(getViewLifecycleOwner(), s -> {
            if (CheckDoubleClick.isFastClick()) return;
            if (appStatusViewModel.currentStatus.get() == AppStatusIntDef.STATUS_PAUSE) {
                //執行Resume
                ((MainActivity) requireActivity()).uartConsole.setBuzzer();
                getBinding().btnResume.callOnClick();

                //MediaMenu狀態改變
                LiveEventBus.get(MEDIA_MENU_CHANGE).post("");
            }
        });
    }

    private void initEvent() {

        if (workoutViewModel.coolDownTime.get() <= 0) {
            getBinding().btnCoolDown.setEnabled(false);
        }

        getBinding().btnCoolDown.setOnClickListener(view ->
                resumeOrCooldown(false));

        //from MediaMenuWindow
        LiveEventBus.get(MEDIA_RESUME_WORKOUT, String.class).observe(getViewLifecycleOwner(), s ->
                getBinding().btnResume.callOnClick());

        //from MediaMenuWindow
        LiveEventBus.get(MEDIA_PAUSE_WORKOUT, String.class).observe(getViewLifecycleOwner(), s -> {
            //    pauseTimer();
        });

        getBinding().btnFinish.setOnClickListener(v -> {

            if (isFinish) return;
            isFinish = true;
            if (resumeTimer != null) {
                resumeTimer.cancel();
                resumeTimer = null;
            }

            if (pauseTimer != null) {
                pauseTimer.cancel();
                pauseTimer = null;
            }

            if (App.SETTING_SHOW) {
                LiveEventBus.get(CLOSE_SETTINGS).post(true);
            }

            ((MainActivity) requireActivity()).showLoadingEx(true,0);

            new RxTimer().timer(200, c -> {
                appStatusViewModel.currentStatus.set(AppStatusIntDef.STATUS_SUMMARY);
                //   appStatusViewModel.isMediaPauseOrResume.set(false);

                //結束Pause 到Summary > uartConsole.setDevWorkoutFinish();
                LiveEventBus.get(STOP_WORKOUT).post("");

                Map<DeviceGEM.EQUIPMENT_CONTROL_PARAMETER, Integer> parameters = new HashMap<>();

           //     parent.updateFtmsMachineStatus(DeviceGEM.FITNESS_EQUIPMENT_STATE.FINISHED, parameters);

                withParent(p-> p.updateFtmsMachineStatus(DeviceGEM.FITNESS_EQUIPMENT_STATE.FINISHED, parameters));
            });

//            appStatusViewModel.currentStatus.set(AppStatusIntDef.STATUS_SUMMARY);
//            //   appStatusViewModel.isMediaPauseOrResume.set(false);
//
//            //結束Pause 到Summary > uartConsole.setDevWorkoutFinish();
//            LiveEventBus.get(STOP_WORKOUT).post("");

        });

        getBinding().btnResume.setOnClickListener(v ->
                resumeOrCooldown(true));
    }

    private boolean isFinish = false;

    RxTimer pauseTimer;
    int pauseSec;
    public int PAUSE_TIME = 60 * 5;

    private void pauseTimer() {

//        if (getApp().getDeviceSettingBean().getPauseMode() != ON) {
//            workoutViewModel.currentPauseTimeText.set("");
//            return;
//        }


        if (getApp().getDeviceSettingBean().getPauseMode() != ON && getApp().getDeviceSettingBean().getAutoPause() != ON) {
            workoutViewModel.currentPauseTimeText.set("");
            return;
        }

        //進 PAUSE 並倒數Autopause 設定的時間 (例如：3 分鐘)，時間到後進到Summary，30秒後，logout (若有第三方APP播放中，一併結束並登出)

        if (getApp().getDeviceSettingBean().getAutoPause() == ON) {
            PAUSE_TIME = (int) getApp().getDeviceSettingBean().getPauseAfter();
        }


        workoutViewModel.currentPauseTimeText.set(getString(R.string.ending_time, formatSecToM(PAUSE_TIME)));

        if (pauseTimer != null) {
            pauseTimer.cancel();
            pauseTimer = null;
        }

        pauseSec = PAUSE_TIME;
        pauseTimer = new RxTimer();
        pauseTimer.intervalComplete(1000, 1000, PAUSE_TIME, new RxTimer.RxActionComplete() {
            @Override
            public void action(long number) {
                if (pauseSec > 0) {
                    workoutViewModel.currentPauseTimeText.set(getString(R.string.ending_time, formatSecToM(--pauseSec)));
                }
            }

            @Override
            public void complete() {
                getBinding().btnFinish.callOnClick();
            }
        });

        //   workoutViewModel.currentPauseTimeText.set(getString(R.string.ending_time, formatSecToM(workoutViewModel.elapsedTime.get())));

    }


    /**
     * @param isResumeButton resume 和 cooldown 按鈕共用
     */
    private void resumeOrCooldown(boolean isResumeButton) {

        if (pauseTimer != null) {
            pauseTimer.cancel();
            pauseTimer = null;
        }

        //如果當前狀態是PAUSE，就執行Resume，反之則取消倒數
        if (appStatusViewModel.currentStatus.get() == AppStatusIntDef.STATUS_PAUSE) {
            isResuming = true;
            //Resume按鈕
        //    if (isResumeButton) {
                if (isTreadmill) {
                    //等狀態
                    if (((MainActivity) requireActivity()).uartConsole.getDevStep() != DS_PAUSE_STANDBY && !isEmulator) {
                        CustomToast.showToast(requireActivity(), getString(R.string.resume_waiting));
                        //  Toasty.warning(requireActivity(), getString(R.string.resume_waiting), Toasty.LENGTH_LONG).show();
                        return;
                    }

         //       }
            }

            //讓Media按鈕不能按
            //LiveEventBus.get(WORKOUT_RESUMING_DISABLE_MEDIA).post(false);

            final long[] i = {3};

            //改變狀態為 Workout Running
            appStatusViewModel.currentStatus.set(AppStatusIntDef.STATUS_RUNNING);

            //   if (appStatusViewModel.currentPage.get() == AppStatusIntDef.CURRENT_PAGE_MEDIA && !appStatusViewModel.isMediaPlaying.get()) {

            if (!isTreadmill) {
                //BIKE
                LiveEventBus.get(RESUME_WORKOUT).post(isResumeButton ? "" : "COOL_DOWN");
                return;
            }

            if (appStatusViewModel.currentPage.get() == AppStatusIntDef.CURRENT_PAGE_MEDIA || appStatusViewModel.isMediaPlaying.get()) {
                appStatusViewModel.currentPage.set(AppStatusIntDef.CURRENT_PAGE_MEDIA);
                isMediaResume = true;
                //當前頁面是Media時，Resume時的倒數window
                resumingWindow = new ResumingWindow(requireActivity());
                resumingWindow.showAtLocation(requireActivity().getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);
                resumingWindow.setOnCustomDismissListener(new BasePopupWindow.OnCustomDismissListener() {
                    @Override
                    public void onStartDismiss(MsgEvent value) {
                    }

                    @Override
                    public void onDismiss() {
                        if (isMediaResume) {
                            LiveEventBus.get(RESUME_WORKOUT).postDelay("", 500);
                        }
                        isMediaResume = false;
                    }
                });
            } else {
                //當前頁面不是Media時，Resume時的倒數
                resumeTimer = new RxTimer();
                resumeTimer.intervalComplete(0, 1000, 3, new RxTimer.RxActionComplete() {
                    @Override
                    public void action(long number) {
                        String n = String.valueOf(i[0] - number);
                        getBinding().tvReadyNum.setText((!n.equals("0") ? n : getString(R.string.GO_)));
                    }

                    @Override
                    public void complete() {
                        //通知 MainWorkoutTrainingFragment啟動時間 & MainDashboardFragment換頁
                        //  new RxTimer().timer(500, number ->  LiveEventBus.get(RESUME_WORKOUT).post(""));
                        if (!isResuming) return; //被取消了變 false >> return
                        isCompleteResume = true;
                        LiveEventBus.get(RESUME_WORKOUT).postDelay(isResumeButton ? "" : "COOL_DOWN", 500);

                        if (resumeTimer != null) {
                            resumeTimer.cancel();
                            resumeTimer = null;
                        }
                    }
                });
            }
        } else {
            if (isCompleteResume) return; // 倒數已經執行到Complete了 true >> return
            //取消Resume倒數
            isResuming = false;

            if (appStatusViewModel.currentPage.get() == AppStatusIntDef.CURRENT_PAGE_MEDIA) {


                isMediaResume = false;
                if (resumingWindow != null) {
                    resumingWindow.dismiss();
                    resumingWindow = null;
                }
            } else {
                if (resumeTimer != null) {
                    resumeTimer.cancel();
                    resumeTimer = null;
                }
            }

            //讓Media按鈕可以按
            //LiveEventBus.get(WORKOUT_RESUMING_DISABLE_MEDIA).post(true);

            //取消Resume倒數 回到 Pause 狀態
            appStatusViewModel.currentStatus.set(AppStatusIntDef.STATUS_PAUSE);
            getBinding().tvReadyNum.setText("3");

            pauseTimer();
        }
    }

    private boolean isCompleteResume = false;

    private void initView() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (resumeTimer != null) {
            resumeTimer.cancel();
            resumeTimer = null;
        }

        if (resumingWindow != null) {
            resumingWindow.dismiss();
            resumingWindow = null;
        }

        if (pauseTimer != null) {
            pauseTimer.cancel();
            pauseTimer = null;
        }
        isResuming = false;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        try {
            if (!isAdded()) return;
            if (((MainActivity) requireActivity()).popupWindow != null) {
                ((MainActivity) requireActivity()).popupWindow.dismiss();
                ((MainActivity) requireActivity()).popupWindow = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!hidden) {
            //    workoutViewModel.currentPauseTimeText.set(getString(R.string.ending_time, formatSecToM(workoutViewModel.elapsedTime.get())));

            //若已被設定為STATUS_SUMMARY，跳過Pause 直接到Summary


            parent.getUartConsoleManager().setDevPauseWorkout();

            if (appStatusViewModel.currentStatus.get() == AppStatusIntDef.STATUS_SUMMARY) {
                getBinding().btnFinish.callOnClick();
//            } else {
//                pauseTimer();
            }

            isCompleteResume = false;
            isResuming = false;


        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            if (resumingWindow != null) {
                resumingWindow.dismiss();
                resumingWindow = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}