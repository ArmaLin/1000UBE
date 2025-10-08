package com.dyaco.spirit_commercial.alert_message;

import static com.corestar.libs.device.DeviceSpiritC.MAIN_MODE.RESET;
import static com.dyaco.spirit_commercial.App.APK_MD5;
import static com.dyaco.spirit_commercial.App.getApp;
import static com.dyaco.spirit_commercial.App.getDeviceSpiritC;
import static com.dyaco.spirit_commercial.MainActivity.isEmulator;
import static com.dyaco.spirit_commercial.MainActivity.isTreadmill;
import static com.dyaco.spirit_commercial.support.CommonUtils.formatSecToM;
import static com.dyaco.spirit_commercial.work_task.WorkManagerUtil.WORK_NOTIFY_UPDATE_MSG_TAG;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.corestar.libs.device.DeviceSpiritC;
import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.databinding.WindowUpdateRestartBinding;
import com.dyaco.spirit_commercial.product_flavor.DownloadManagerCustom;
import com.dyaco.spirit_commercial.product_flavor.GetApkSign;
import com.dyaco.spirit_commercial.support.CommonUtils;
import com.dyaco.spirit_commercial.support.RxTimer;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;
import com.dyaco.spirit_commercial.support.utils.CheckDoubleClick;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingBean;
import com.dyaco.spirit_commercial.work_task.InstallCallback;
import com.dyaco.spirit_commercial.work_task.WorkManagerUtil;

import java.util.Calendar;

import es.dmoral.toasty.Toasty;

public class UpdateRestartWindow extends BasePopupWindow<WindowUpdateRestartBinding> {

    private RxTimer countDownTimer;
    private final int countDownTime = 60;

    public UpdateRestartWindow(Context context) {
        super(context, 300, 0, 0, GENERAL.TRANSLATION_Y, false, false, true, false);



        getBinding().btnRestart.setOnClickListener(view -> {
            if (CheckDoubleClick.isFastClick()) return;
            cancelTimer();
            //開始安裝
            startInstallApk();
        });

        getBinding().btnNotNow.setOnClickListener(view -> {
            if (CheckDoubleClick.isFastClick()) return;
            if (isDone) return;
            cancelTimer();
            //12小時的提醒更新
            new WorkManagerUtil().notifyUpdateMsgWorkManager();

            new WorkManagerUtil().checkWorkStateFromTag(WORK_NOTIFY_UPDATE_MSG_TAG);

            dismiss();
        });

        countDownTimer = new RxTimer();
        countDownTimer.intervalComplete(500, 1000, countDownTime, new RxTimer.RxActionComplete() {
            @Override
            public void action(long number) {

//                Thread thread1 = getMainLooper().getThread();
//                Thread thread2 = Thread.currentThread();
//                Log.d("OOEOEOEOOO", "setProgress: " + thread1.getName() + "," + thread2.getName());


                try {
                    getBinding().tvCountDown.setText(formatSecToM(countDownTime - number));

                    if (number == (countDownTime - 1)) {
                        ((MainActivity) context).showLoading(true);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void complete() {
                try {
                    if (isCancel) return;
                    isDone = true;
                    //開始安裝
                    startInstallApk();
                    // dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    boolean isCancel;
    boolean isDone;

    @Override
    public void dismiss() {
        super.dismiss();

        ((MainActivity) mContext).showLoading(false);

        cancelTimer();
    }

    private void cancelTimer() {
        isCancel = true;
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }


    public void startInstallApk() {

        ((MainActivity) mContext).showLoading(true);

        DeviceSettingBean d = getApp().getDeviceSettingBean();
        if (d.getTmpApkPath() != null && !"".equals(d.getTmpApkPath())) {
            if (APK_MD5.equalsIgnoreCase(new GetApkSign().getApkMd5(d.getTmpApkPath()))) {

                //更新時間
                DeviceSettingBean d2 = getApp().getDeviceSettingBean();
                d2.setSoftwareUpdatedMillis(Calendar.getInstance().getTimeInMillis());
                getApp().setDeviceSettingBean(d2);
                Log.d(DownloadManagerCustom.TAG, "Console更新時間為：" + getApp().getDeviceSettingBean().getSoftwareUpdatedMillis());

                if (!isEmulator && isTreadmill) {
                    //  ((MainActivity) mContext).uartConsole.setDevMainMode(RESET);
                    getDeviceSpiritC().setMainModeTreadmill(RESET); //停止LWR計數, 以免發time out錯誤
                }

                new CommonUtils().install2(mContext, d.getTmpApkPath(), new InstallCallback() {
                    @Override
                    public void onSuccess() {

//                        Intent serviceIntent = new Intent(mContext, RestartingService.class);
//                        mContext.startService(serviceIntent);

                        Log.d(DownloadManagerCustom.TAG, "安裝完成: ");
                    }

                    @Override
                    public void onFail(String s) {

                        Log.d(DownloadManagerCustom.TAG, "安裝失敗: " + s);

                        //不跑 UiThread 會 Crash
                        ((Activity) mContext).runOnUiThread(() -> {
                            updateFailed();
                            dismiss();
                            Toasty.error(mContext, "Installation Failed:" + s, Toasty.LENGTH_LONG).show();
                        });
                    }
                });

          //      new RxTimer().timer(1000, number -> {
//                    boolean isInstall = new CommonUtils().install(mContext, d.getTmpApkPath());
//                    if (isInstall) {
//                        Log.d(DownloadManagerCustom.TAG, "安裝完成: ");
//                    } else {
//                        updateFailed();
//                        Log.d(DownloadManagerCustom.TAG, "安裝失敗: ");
//                        dismiss();
//                        Toasty.error(getApp(), "INSTALL FAILED", Toasty.LENGTH_LONG).show();
//                    }
           //     });

            } else {
                ((MainActivity) mContext).showLoading(false);
                dismiss();
                Log.d(DownloadManagerCustom.TAG, "安裝失敗:MD5錯誤 ");
                Toasty.error(getApp(), "INSTALL FAILED", Toasty.LENGTH_LONG).show();
            }

            //   d.setTmpApkPath("");
            //   getApp().setDeviceSettingBean(d);

        } else {
            ((MainActivity) mContext).showLoading(false);
            Log.d(DownloadManagerCustom.TAG, "安裝失敗: temp");
            Toasty.error(getApp(), "INSTALL FAILED", Toasty.LENGTH_LONG).show();
        }
    }

    private void updateFailed() {
        if (!isEmulator && isTreadmill) {
            ((MainActivity) mContext).uartConsole.setDevMainMode(DeviceSpiritC.MAIN_MODE.IDLE);
        }
        ((MainActivity) mContext).showLoading(false);
    }

}
