//package com.dyaco.spirit_commercial.product_flavor;
//
//import static android.content.Context.CONNECTIVITY_SERVICE;
//import static com.dyaco.spirit_commercial.App.getApp;
//import static com.dyaco.spirit_commercial.support.CommonUtils.getFileExtension;
//import static com.dyaco.spirit_commercial.support.CommonUtils.showException;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.pm.PackageInfo;
//import android.content.pm.PackageManager;
//import android.net.ConnectivityManager;
//import android.net.Network;
//import android.net.NetworkCapabilities;
//import android.util.Log;
//import android.view.View;
//import android.widget.ProgressBar;
//import android.widget.SeekBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//
//import com.dyaco.spirit_commercial.MainActivity;
//import com.dyaco.spirit_commercial.R;
//import com.dyaco.spirit_commercial.databinding.WindowUpdateAppBinding;
//import com.dyaco.spirit_commercial.support.InstallXapk.XapkInstaller;
//import com.dyaco.spirit_commercial.support.MsgEvent;
//import com.dyaco.spirit_commercial.support.RxTimer;
//import com.dyaco.spirit_commercial.support.SilentInstaller2;
//import com.dyaco.spirit_commercial.support.SilentXapkInstaller;
//import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
//import com.dyaco.spirit_commercial.support.download.DownloadListener;
//import com.dyaco.spirit_commercial.support.download.DownloadUtil;
//import com.dyaco.spirit_commercial.support.intdef.GENERAL;
//
//import es.dmoral.toasty.Toasty;
//
//public class UpdateOtherAppWindow2 extends BasePopupWindow<WindowUpdateAppBinding> {
//    private final Context mContext;
//    private final SeekBar downloadProgress;
//    private DownloadUtil downloadUtil;
//    private NetworkCapabilities nc;
//    private final TextView tvMin;
//    private final ProgressBar pbInstall;
//
//    private final String updateUrl;
//    private final String packageName;
//
//    private XapkInstaller xapkInstaller;
//
//    public UpdateOtherAppWindow2(Context context, String packageName, String updateUrl) {
//
//        super(context, 0, 0, 0, GENERAL.FADE, false, false, true, false);
//        mContext = context;
//        this.updateUrl = updateUrl;
//        this.packageName = packageName;
//
//        tvMin = getBinding().tvMin;
//        downloadProgress = getBinding().seekBar;
//        pbInstall = getBinding().pbInstall;
//
//        getBinding().btnClose.setVisibility(View.VISIBLE);
//
//        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(CONNECTIVITY_SERVICE);
//        Network nw = connectivityManager.getActiveNetwork();
//        if (nw != null) {
//            nc = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
//            updateAPP();
//        } else {
//            Toasty.warning(mContext, "no internet", Toast.LENGTH_SHORT).show();
////            bt_close.setEnabled(true);
//            dismiss();
//        }
//
//        getBinding().btnClose.setOnClickListener(view -> {
//            downloadUtil.stop();
//            dismiss();
//        });
//
////        // TODO: XAPK
////        IntentFilter intentFilter = new IntentFilter();
////        intentFilter.addAction(Intent.ACTION_VIEW);
////        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
////        mContext.registerReceiver(onCompleteReceiver, intentFilter);
//    }
//
//    private void updateAPP() {
//
//        downloadUtil = new DownloadUtil(getFileExtension(updateUrl));
//        downloadUtil.deleteFile();
//        downloadUtil.downloadFile(updateUrl, new DownloadListener() {
//            @Override
//            public void onStart() {
//                Log.d("UPDATE_APP", "開始下載: " + updateUrl);
//            }
//
//            @Override
//            public void onProgress(final int currentLength, final long count, final long total) {
//
//                ((Activity) mContext).runOnUiThread(() -> {
//
//                    //  Log.d("UPDATE@@@", "onProgress: " + currentLength);
//                    downloadProgress.setProgress(currentLength);
//                    //   String x = downloadUtil.getDownloadTime(nc, total, count);
//                    //  tvMin.setText(mContext.getString(R.string.estimated_time_min, x));
//                });
//            }
//
//            @Override
//            public void onFinish(final String localPath) {
//
//                try {
//                    ((Activity) mContext).runOnUiThread(() -> {
//                        pbInstall.setVisibility(View.VISIBLE);
//                        downloadProgress.setProgress(100);
//                        tvMin.setText("");
//                        getBinding().btnClose.setEnabled(false);
//                        getBinding().btnClose.setVisibility(View.INVISIBLE);
//                        getBinding().tvT.setText(R.string.Installing);
//                        //   getBinding().btnClose.setVisibility(View.INVISIBLE);
//                        //  getBinding().updateTitle.setText(R.string.Installing);
//
//                    });
//                } catch (Exception e) {
//                    showException(e);
//                }
//
//                // TODO: XAPK
//                if (getFileExtension(localPath).equalsIgnoreCase("xapk")) {
//                    Log.d("UPDATE_APP", "onFinish->xapk");
//
//
//                    SilentXapkInstaller.install(getApp(), localPath, new SilentXapkInstaller.OnInstallerListener() {
//                        @Override
//                        public void onSuccess() {
//                            Log.d("UPDATE_APP", "XAPK 安裝完成");
//                            returnValue(new MsgEvent(true));
//                            dismiss();
//                        }
//
//                        @Override
//                        public void onError(@NonNull String error) {
//                            Log.d("UPDATE_APP", "Err" + error);
//                            dismiss();
//                        }
//                    });
//
////
////                    xapkInstaller = XapkManager.createXapkInstaller(mContext, localPath);
////                    ExecutorService installExecutor = Executors.newSingleThreadExecutor();
////                    installExecutor.execute(() -> {
////                        xapkInstaller.setOnXapkInstallerListener(new XapkInstaller.OnXapkInstallerListener() {
////                            @Override
////                            public void onInstaller() {
////                                Log.d("UPDATE_APP", "onInstaller安裝完成");
////                                returnValue(new MsgEvent(true));
////                                ((Activity) mContext).runOnUiThread(UpdateOtherAppWindow.this::dismiss);
////                            }
////
////                            @Override
////                            public void onErr(String err) {
////                                Log.d("UPDATE_APP", "Err" + err);
////                                dismiss();
////                            }
////                        });
////                        Log.d("UPDATE_APP", "2開始安裝");
////                        xapkInstaller.install();
////                    });
//
//
//                } else {
//                    Log.d("UPDATE_APP", "onFinish->APK");
//                    SilentInstaller2.install(getApp(), localPath, new SilentInstaller2.InstallCallback() {
//                        @Override
//                        public void onSuccess() {
//                            // TODO:   改成 ui thread, 檢查packgaename取消
//                            Log.d("UPDATE_APP", "更新完成: ");
//                            returnValue(new MsgEvent(true));
//                            dismiss();
//                        }
//
//                        @Override
//                        public void onFail(@NonNull String reason) {
//                            // TODO:   改成 ui thread
//                            Log.d("UPDATE_APP", "更新失敗: " + reason);
//                            Toasty.error(mContext, "Failure", Toasty.LENGTH_LONG).show();
//                            dismiss();
//                        }
//                    });
//
//
////                    SilentInstaller.install(getApp(), localPath, new InstallCallback() {
////                        @Override
////                        public void onSuccess() {
////                            Log.d("UPDATE_APP", "更新完成: ");
////
////                            new RxTimer().timer(12000, number -> {
////                                returnValue(new MsgEvent(true));
////                                ((Activity) mContext).runOnUiThread(UpdateOtherAppWindow.this::dismiss);
////                            });
////                        }
////
////                        @Override
////                        public void onFail(@NonNull String reason) {
////                            Log.d("UPDATE_APP", "更新失敗: " + reason);
////                            ((Activity) mContext).runOnUiThread(() -> {
////                                Toasty.error(mContext, "Failure", Toasty.LENGTH_LONG).show();
////                                dismiss();
////                            });
////                        }
////                    });
//
//
////                    if (install(mContext, localPath)) {
////                        Log.d("UPDATE_APP", "更新完成: ");
////
////                        new RxTimer().timer(12000, number -> {
////                            returnValue(new MsgEvent(true));
////                            ((Activity) mContext).runOnUiThread(UpdateOtherAppWindow.this::dismiss);
////                        });
////
////                    } else {
////                        Log.d("UPDATE_APP", "更新失敗: ");
////                        ((Activity) mContext).runOnUiThread(() -> {
////                            Toasty.error(mContext, "Failure", Toasty.LENGTH_LONG).show();
////                            dismiss();
////                        });
////                    }
//                }
//            }
//
//            @Override
//            public void onFailure(final String errorInfo) {
//                ((MainActivity) mContext).runOnUiThread(() -> {
//                    downloadProgress.setProgress(0);
//                    tvMin.setText(errorInfo);
//                    //  bt_close.setEnabled(true);
//
//                    new RxTimer().timer(500, number -> dismiss());
//                    if (!"".equals(errorInfo)) {
//                        Toasty.error(mContext, "#Download ERROR:" + errorInfo, Toasty.LENGTH_LONG).show();
//                    }
//                    Log.d("UPDATE_APP", "onFailure: " + errorInfo);
//                });
//            }
//        });
//    }
//
//
////    public boolean install(Context context, String apkPath) {
////        PackageInstaller packageInstaller = context.getPackageManager().getPackageInstaller();
////        PackageInstaller.SessionParams params = new PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL);
////        String pkgName = getApkPackageName(context, apkPath);
////        Log.d("UPDATE_APP", "Package Name:" + pkgName);
////        if (pkgName == null) {
////            return false;
////        }
////        params.setAppPackageName(pkgName);
////        try {
////            Method allowDowngrade = PackageInstaller.SessionParams.class.getMethod("setAllowDowngrade", boolean.class);
////            allowDowngrade.setAccessible(true);
////            allowDowngrade.invoke(params, true);
////        } catch (Exception e) {
////            e.printStackTrace();
////            return false;
////        }
////        OutputStream os = null;
////        InputStream is = null;
////        try {
////            int sessionId = packageInstaller.createSession(params);
////            PackageInstaller.Session session = packageInstaller.openSession(sessionId);
////            os = session.openWrite(pkgName, 0, -1);
////            is = new FileInputStream(apkPath);
////            byte[] buffer = new byte[1024];
////            int len;
////            while ((len = is.read(buffer)) != -1) {
////                os.write(buffer, 0, len);
////            }
////            session.fsync(os);
////            os.close();
////            os = null;
////            is.close();
////            is = null;
////            session.commit(PendingIntent.getBroadcast(context, sessionId, new Intent(Intent.ACTION_MAIN), PendingIntent.FLAG_IMMUTABLE).getIntentSender());
////        } catch (Exception e) {
////            return false;
////        } finally {
////            if (os != null) {
////                try {
////                    os.close();
////                } catch (IOException e) {
////                    e.printStackTrace();
////                }
////            }
////            if (is != null) {
////                try {
////                    is.close();
////                } catch (IOException e) {
////                    e.printStackTrace();
////                }
////            }
////        }
////        return true;
////    }
//
//    /**
//     * 獲取apk的包名
//     */
//    public String getApkPackageName(Context context, String apkPath) {
//        PackageManager pm = context.getPackageManager();
//        PackageInfo info = pm.getPackageArchiveInfo(apkPath, 0);
//        if (info != null) {
//            return info.packageName;
//        } else {
//            return null;
//        }
//    }
//
//    @Override
//    public void dismiss() {
//        super.dismiss();
//        // TODO: XAPK
////        if (onCompleteReceiver != null) {
////            try {
////                mContext.unregisterReceiver(onCompleteReceiver);
////            } catch (Exception e) {
////                e.printStackTrace();
////            }
////        }
//    }
//
//
////    // TODO: XAPK
////    public BroadcastReceiver onCompleteReceiver = new BroadcastReceiver() {
////        @Override
////        public void onReceive(Context context, Intent intent) {
////            Log.d(XapkInstaller.TAG, "onCompleteReceiver" + intent.getAction());
////            if (Objects.equals(intent.getAction(), Intent.ACTION_VIEW)) {
////                if (xapkInstaller != null) {
////                    Bundle extras = intent.getExtras();
////                    xapkInstaller.onReceiver(extras);
////                    //      Log.d(SettingsMediaAppsWindow.TAG, "onReceive:  安裝完成");
////                }
////            } else if (xapkInstaller != null) {
////                if (Objects.equals(intent.getAction(), XapkInstaller.getPackageInstalledAction())) {
////                    Bundle extras = intent.getExtras();
////                    xapkInstaller.onReceiver(extras);
////                }
////            }
////        }
////    };
//}
