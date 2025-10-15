package com.dyaco.spirit_commercial.product_flavor;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static com.dyaco.spirit_commercial.App.getApp;
import static com.dyaco.spirit_commercial.App.getDeviceSpiritC;
import static com.dyaco.spirit_commercial.MainActivity.isEmulator;
import static com.dyaco.spirit_commercial.MainActivity.isTreadmill;
import static com.dyaco.spirit_commercial.work_task.WorkManagerUtil.WORK_NOTIFY_UPDATE_MSG_TAG;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.corestar.libs.device.DeviceDyacoMedical;
import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.WindowUpdateAppBinding;
import com.dyaco.spirit_commercial.model.webapi.bean.UpdateBean;
import com.dyaco.spirit_commercial.support.CommonUtils;
import com.dyaco.spirit_commercial.support.RxTimer;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.download.DownloadListener;
import com.dyaco.spirit_commercial.support.download.DownloadUtil;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingBean;
import com.dyaco.spirit_commercial.work_task.InstallCallback;
import com.dyaco.spirit_commercial.work_task.WorkManagerUtil;

import java.util.Calendar;

import es.dmoral.toasty.Toasty;

public class UpdateAppWindow extends BasePopupWindow<WindowUpdateAppBinding> {
    private final Context mContext;
    private final SeekBar downloadProgress;
    private DownloadUtil downloadUtil;
    private NetworkCapabilities nc;
    private final TextView tvMin;
    private final ProgressBar pbInstall;
    private final UpdateBean updateBean;

    public UpdateAppWindow(Context context, UpdateBean updateBean) {

        super(context, 0, 0, 0, GENERAL.FADE, false, false, true, false);
        mContext = context;
        this.updateBean = updateBean;

        tvMin = getBinding().tvMin;
        downloadProgress = getBinding().seekBar;
        pbInstall = getBinding().pbInstall;

        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(CONNECTIVITY_SERVICE);
        Network nw = connectivityManager.getActiveNetwork();
        if (nw != null) {
            nc = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            updateAPP();
        } else {
            Toasty.warning(mContext, "no internet", Toast.LENGTH_SHORT).show();
//            bt_close.setEnabled(true);
            dismiss();
        }
    }

    private void updateAPP() {

        downloadUtil = new DownloadUtil("apk");
        downloadUtil.deleteFile();
        downloadUtil.downloadFile(updateBean.getDownloadURL(), new DownloadListener() {
            @Override
            public void onStart() {
                Log.d("更新", "開始下載: " + updateBean.getDownloadURL());
            }

            @Override
            public void onProgress(final int currentLength, final long count, final long total) {

                String x = downloadUtil.getDownloadTime(nc, total, count);

                ((Activity) mContext).runOnUiThread(() -> {
                    //   Log.d("UPDATE@@@", "onProgress: " + currentLength);
                    downloadProgress.setProgress(currentLength);
                    tvMin.setText(mContext.getString(R.string.estimated_time_min, x));
                });
            }

            @Override
            public void onFinish(final String localPath) {

                try {
                    ((Activity) mContext).runOnUiThread(() -> {
                        pbInstall.setVisibility(View.VISIBLE);
                        downloadProgress.setProgress(100);
//                        tvMin.setText(R.string.Done);
                        tvMin.setText("");
                        //  bt_close.setEnabled(false);
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }


                if (updateBean.getMD5().equalsIgnoreCase(new GetApkSign().getApkMd5(localPath))) {
                    Log.d("更新", "APK驗證成功，安裝APK: ");

                    ((Activity) mContext).runOnUiThread(() -> {
//                        getBinding().updateTitle.setText(R.string.Installing);
                        getBinding().tvT.setText(R.string.Installing);
                    });

                    Log.d("XXXCCXXXX", "onFinish: " + localPath);


                    //更新時間
                    DeviceSettingBean d2 = getApp().getDeviceSettingBean();
                    d2.setSoftwareUpdatedMillis(Calendar.getInstance().getTimeInMillis());
                    getApp().setDeviceSettingBean(d2);

                    if (!isEmulator && isTreadmill) {
                      //  ((MainActivity) mContext).uartConsole.setDevMainMode(RESET);
                          getDeviceSpiritC().setMainModeTreadmill(DeviceDyacoMedical.MAIN_MODE.RESET); //停止LWR計數, 以免發time out錯誤
                    }

                    new WorkManagerUtil().cancelWorkByTag(WORK_NOTIFY_UPDATE_MSG_TAG);


                    new CommonUtils().install2(mContext, localPath, new InstallCallback() {
                        @Override
                        public void onSuccess() {
                            Log.d("更新", "安裝完成: ");
                        }

                        @Override
                        public void onFail(String s) {

                            ((Activity) mContext).runOnUiThread(() -> {
                                updateFailed();
                                Log.d("更新", "安裝失敗: " + s);
                                dismiss();
                                Toasty.error(mContext, "Installation Failed:" + s, Toasty.LENGTH_LONG).show();
                            });
                        }
                    });

//                    new RxTimer().timer(1000, number -> {
//                        // /storage/emulated/0/Android/data/com.dyaco.spirit_commercial/cache/Spirit.apk
//                        boolean isInstall = new CommonUtils().install(mContext, localPath);
//
//                        if (isInstall) {
//                            Log.d("更新", "安裝完成: ");
//                        } else {
//                            updateFailed();
//                            Log.d("更新", "安裝失敗: ");
//                            ((Activity) mContext).runOnUiThread(() -> Toasty.error(mContext, "Installation Failed", Toasty.LENGTH_LONG).show());
//
//                            dismiss();
//                        }
//                    });

                } else {
                    Log.d("更新", "APK驗證失敗");
                    ((Activity) mContext).runOnUiThread(() -> {
                        downloadUtil.deleteFile();
                        Toasty.error(mContext, "File Signature Verification Failed or Corruption", Toast.LENGTH_LONG).show();
                        new RxTimer().timer(500, number -> dismiss());
                    });

                }
            }

            @Override
            public void onFailure(final String errorInfo) {
                ((MainActivity) mContext).runOnUiThread(() -> {
                    downloadProgress.setProgress(0);
                    tvMin.setText(errorInfo);
                    //  bt_close.setEnabled(true);

                    new RxTimer().timer(500, number -> dismiss());
                    if (!"".equals(errorInfo)) {
                        Toasty.error(mContext, "Download ERROR:" + errorInfo, Toasty.LENGTH_LONG).show();
                    }
                    Log.d("更新", "onFailure: " + errorInfo);
                });
            }
        });
    }

    @Override
    public void dismiss() {
        super.dismiss();

    }


    private void updateFailed() {
        if (!isEmulator && isTreadmill) {
            ((MainActivity) mContext).uartConsole.setDevMainMode(DeviceDyacoMedical.MAIN_MODE.ENG);
        }
    }
}
