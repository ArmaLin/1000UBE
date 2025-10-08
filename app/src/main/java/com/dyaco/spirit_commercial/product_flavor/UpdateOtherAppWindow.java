package com.dyaco.spirit_commercial.product_flavor;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static com.dyaco.spirit_commercial.App.getApp;
import static com.dyaco.spirit_commercial.support.CommonUtils.showException;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.WindowUpdateAppBinding;
import com.dyaco.spirit_commercial.support.DownloadUtilKt;
import com.dyaco.spirit_commercial.support.MsgEvent;
import com.dyaco.spirit_commercial.support.RxTimer;
import com.dyaco.spirit_commercial.support.SilentAppInstaller;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;

import es.dmoral.toasty.Toasty;

public class UpdateOtherAppWindow extends BasePopupWindow<WindowUpdateAppBinding> {
    private final Context mContext;
    private final SeekBar downloadProgress;
    private DownloadUtilKt downloadUtil;
    private final TextView tvMin;
    private final ProgressBar pbInstall;

    private final String updateUrl;


    public UpdateOtherAppWindow(Context context, String packageName, String updateUrl) {

        super(context, 0, 0, 0, GENERAL.FADE, false, false, true, false);
        mContext = context;
        this.updateUrl = updateUrl;

        tvMin = getBinding().tvMin;
        downloadProgress = getBinding().seekBar;
        pbInstall = getBinding().pbInstall;

        getBinding().btnClose.setVisibility(View.VISIBLE);

        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(CONNECTIVITY_SERVICE);
        Network nw = connectivityManager.getActiveNetwork();
        if (nw != null) {
            updateAPP();
        } else {
            Toasty.warning(mContext, "no internet", Toast.LENGTH_SHORT).show();
//            bt_close.setEnabled(true);
            dismiss();
        }

        getBinding().btnClose.setOnClickListener(view -> {
            if (downloadUtil != null) downloadUtil.stop();
            dismiss();
        });

    }

    private void updateAPP() {

        downloadUtil = DownloadUtilKt.downloadFile(getApp(), updateUrl, new DownloadUtilKt.DownloadListener() {
            @Override
            public void onStart() {
                Log.d("UPDATE_APP", "開始下載: " + updateUrl);
            }

            @Override
            public void onProgress(final int currentLength, final long count, final long total) {
//                double downloadedMb = count / 1024.0 / 1024.0;
//                double totalMb = total / 1024.0 / 1024.0;
//                String sizeText = String.format(Locale.getDefault(), "%.2f / %.2f MB", downloadedMb, totalMb);
//                tvMin.setText(sizeText);

                downloadProgress.setProgress(currentLength);
            }

            @Override
            public void onFinish(@NonNull final String localPath) {
                try {
                    pbInstall.setVisibility(View.VISIBLE);
                    downloadProgress.setProgress(100);
                    tvMin.setText("");
                    getBinding().btnClose.setEnabled(false);
                    getBinding().btnClose.setVisibility(View.INVISIBLE);
                    getBinding().tvT.setText(R.string.Installing);

                } catch (Exception e) {
                    showException(e);
                }

                Log.d("UPDATE_APP", "下載完成:開始安裝： 下載位置:" + localPath);

                //安裝APK or XAPK
                SilentAppInstaller.install(getApp(), localPath, new SilentAppInstaller.OnInstallerListener() {
                    @Override
                    public void onSuccess() {
                        Log.d("UPDATE_APP", "更新完成: ");
                        returnValue(new MsgEvent(true));
                        dismiss();
                    }

                    @Override
                    public void onError(@NonNull String reason) {
                        Log.d("UPDATE_APP", "更新失敗: " + reason);
                        Toasty.error(mContext, "Failure", Toasty.LENGTH_LONG).show();
                        dismiss();
                    }
                });
            }

            @Override
            public void onFailure(@NonNull final String errorInfo) {
                downloadProgress.setProgress(0);
                tvMin.setText(errorInfo);
                new RxTimer().timer(500, number -> dismiss());
                Toasty.error(mContext, "#Download ERROR:" + errorInfo, Toasty.LENGTH_LONG).show();
                Log.d("UPDATE_APP", "onFailure: " + errorInfo);
            }
        });
    }


    @Override
    public void dismiss() {
        super.dismiss();
    }


}
