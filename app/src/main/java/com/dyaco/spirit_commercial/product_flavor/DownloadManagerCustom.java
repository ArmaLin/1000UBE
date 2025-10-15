package com.dyaco.spirit_commercial.product_flavor;

import static com.dyaco.spirit_commercial.App.getApp;
import static com.dyaco.spirit_commercial.MainActivity.UPDATE_FILE_PATH;
import static com.dyaco.spirit_commercial.work_task.WorkManagerUtil.WORK_NOTIFY_UPDATE_MSG_TAG;

import android.util.Log;

import androidx.work.WorkInfo;

import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.model.webapi.bean.UpdateBean;
import com.dyaco.spirit_commercial.support.CommonUtils;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingBean;
import com.dyaco.spirit_commercial.work_task.DownloadWindow;
import com.dyaco.spirit_commercial.work_task.WorkManagerUtil;
import com.ixuea.android.downloader.DownloadService;
import com.ixuea.android.downloader.callback.DownloadListener;
import com.ixuea.android.downloader.callback.DownloadManager;
import com.ixuea.android.downloader.domain.DownloadInfo;
import com.ixuea.android.downloader.exception.DownloadException;

import java.io.File;

public class DownloadManagerCustom {
    public static final String TAG = "##UpdateProcess##";
    public DownloadManager downloadManager;
    UpdateBean updateBean;
    private String apkPath;
    DownloadInfo downloadInfo;
    private final MainActivity mainActivity;

    public DownloadManagerCustom(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    DownloadWindow downloadWindow;
    private void showProgressWindow() {
        if (downloadWindow != null) {
            downloadWindow.dismiss();
            downloadWindow = null;
        }
        downloadWindow = new DownloadWindow(mainActivity);
    }


    public void initDownload(UpdateBean updateBean) {

        this.updateBean = updateBean;

//        File targetFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "a.apk");
        //  Log.d("XXXCCXXXX", "down: " + getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS));
        ///storage/emulated/0/Android/data/com.dyaco.spirit_commercial/cache/
//        File targetFile = new File(UPDATE_FILE_PATH, "Spirit.apk");
        File targetFile = new File(UPDATE_FILE_PATH, updateBean.getPATH()); //要儲存的名字
        apkPath = targetFile.getAbsolutePath();
        Log.d(TAG, "準備將APK儲存到: " + apkPath);

        getDownloadedFile(UPDATE_FILE_PATH);
        String tempApkPath = getApp().getDeviceSettingBean().getTmpApkPath();
        boolean checkApkPath = apkPath.equals(tempApkPath);
        boolean checkMd5 = updateBean.getMD5().equalsIgnoreCase(new GetApkSign().getApkMd5(tempApkPath));
        Log.d(TAG, "MD5" + new GetApkSign().getApkMd5(tempApkPath));
        if (isExist && checkApkPath && checkMd5) {
            Log.d(TAG, "APK檔案已存在，且Console暫存的檔案路徑 與 實際要存的路徑相同(apkPath == TmpApkPath)，且APK無毀損 >> 不下載: ");

            WorkInfo.State workState = new WorkManagerUtil().checkWorkStateFromTag(WORK_NOTIFY_UPDATE_MSG_TAG);
            Log.d(TAG, "目前WorkManager狀態: " + workState + ": 如果不等於 ENQUEUED(排程中) 或 RUNNING(執行中)，需要再跳出更新視窗");
            //進入此程序 代表:1.有更新檔案 ,2.AutoUpdate ON,3.已經按過等12小時 WorkInfo.State為 ENQUEUED
            if (WorkInfo.State.ENQUEUED != workState && WorkInfo.State.RUNNING != workState) {
                //如果都不符合，需要再跳出更新視窗
                Log.d(TAG, "跳出更新視窗");

                if (mainActivity != null) {
                    mainActivity.showUpdateRestartWindow();
                }
            }

            return;
        } else {
            Log.d(TAG, "檔案是否存在：" + isExist +", Console暫存的檔案路徑與實際要存的路徑相同:" + checkApkPath +", md5是否正確："+ checkMd5);
        }


        downloadManager = DownloadService.getDownloadManager(getApp());

        downloadInfo = new DownloadInfo.Builder()
                .setUrl(updateBean.getDownloadURL())
                .setPath(targetFile.getAbsolutePath())
                .build();

        Log.d(TAG, "開始下載APK: " + updateBean.getDownloadURL());
        downloadInfo.setDownloadListener(new DownloadListener() {

            @Override
            public void onStart() {
                Log.d(TAG, "onStart: ");
            }

            @Override
            public void onWaited() {
                Log.d(TAG, "onWaited: ");
            }

            @Override
            public void onPaused() {
                Log.d(TAG, "onPaused: ");
            }

            @Override
            public void onDownloading(long progress, long size) {
                //  (int) (downloadInfo.getProgress() * 100.0 / downloadInfo.getSize())
                Log.d(TAG, "onDownloading: " + progress + "," + size);
                if (downloadWindow != null) {
                    downloadWindow.setProgress((int) (downloadInfo.getProgress() * 100.0 / downloadInfo.getSize()));
                }
            }

            @Override
            public void onRemoved() {
                Log.d(TAG, "onRemoved: ");
                downloadInfo = null;
            }

            @Override
            public void onDownloadSuccess() {
                //檢查APK
                if (updateBean.getMD5().equalsIgnoreCase(new GetApkSign().getApkMd5(apkPath))) {
                    DeviceSettingBean d = getApp().getDeviceSettingBean();
                    d.setTmpApkPath(apkPath);
                    getApp().setDeviceSettingBean(d);
                    Log.d(TAG, "onDownloadSuccess: APK檢查成功，儲存下載位置:" + d.getTmpApkPath());

                    if (mainActivity != null)
                        mainActivity.showUpdateRestartWindow();
                } else {
                    Log.d(TAG, "onDownloadSuccess:APK檢查失敗 ");
                }

                cancelDownload();
            }

            @Override
            public void onDownloadFailed(DownloadException e) {
                Log.d(TAG, "onDownloadFailed: " + e.getLocalizedMessage());
//                downloadManager.remove(downloadInfo);
//                downloadManager.destroy();

                cancelDownload();
            }
        });

        downloadManager.download(downloadInfo);

        showProgressWindow();

    }

    public void dismissDownloadProgress() {
        CommonUtils.iExc(()->{
            if (downloadWindow != null) {
                downloadWindow.dismiss();
                downloadWindow = null;
            }
        });
    }

    public void cancelDownload() {

        dismissDownloadProgress();

        try {

            if (downloadManager != null && downloadInfo != null) {
                downloadManager.remove(downloadInfo);
                downloadManager.destroy();
            }
            Log.d(TAG, "cancelDownload: ");
        } catch (Exception ignore) {
        //    ignore.printStackTrace();
        }
    }

    boolean isExist = false;

    public void getDownloadedFile(String path) {
        File directory = new File(path, "");
        File[] files = directory.listFiles();
        if (directory.canRead() && files != null) {
            Log.d(TAG, "資料夾內檔案數量: " + files.length);
            for (File file : files) {
                if (updateBean.getPATH().equals(file.getName())) {
                    if (updateBean.getMD5().equalsIgnoreCase(new GetApkSign().getApkMd5(file.getAbsolutePath()))) {
                        isExist = true;
                        Log.d(TAG, file.getName() + ":" + "檔案已存在");
                    } else {
                        isExist = false;
                        Log.d(TAG, file.getName() + ":" + "檔案已存在，但損壞" + file.getAbsolutePath());
                    }
                } else {
                    //刪除其他檔案
                    String result = (file.getName() + "," + (file.delete() ? "刪除成功" : "刪除失敗"));
                    Log.d(TAG, "getDownloadedFile: " + result);
                }
            }
        } else {
            Log.d(TAG, "it is null");
        }
    }

}
