package com.dyaco.spirit_commercial.work_task;

import static com.dyaco.spirit_commercial.App.getApp;

import android.util.Log;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.dyaco.spirit_commercial.product_flavor.DownloadManagerCustom;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * REPLACE	如果任務隊列裡有一樣 TAG 的任務，取消並刪除這個任務，並新增一個任務進隊列裡面
 * APPEND	加入任務隊列的末端
 * KEEP	如果任務隊列裡有一樣 TAG 的任務，就什麼都不做，不然就新增一個任務進隊列裡面
 */
public class WorkManagerUtil {
    public static final String TAG = DownloadManagerCustom.TAG;
    public static final String WORK_NOTIFY_UPDATE_MSG_TAG = "NotifyUpdateMsgTag";
    public static final String WORK_UPLOAD_ERROR_LOG_TAG = "CheckUploadErrorLogTag";

    /**
     * 12小時的提醒更新
     */
    public void notifyUpdateMsgWorkManager() {
        OneTimeWorkRequest notifyUpdateMsgWork = new OneTimeWorkRequest
                .Builder(NotifyUpdateMsgWork.class)
                .setInitialDelay(12, TimeUnit.HOURS)
             //   .setInitialDelay(30, TimeUnit.SECONDS)
                .addTag(WORK_NOTIFY_UPDATE_MSG_TAG)
                //   .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build();
        WorkManager.getInstance(getApp()).enqueueUniqueWork("NotifyUpdateMsg", ExistingWorkPolicy.KEEP, notifyUpdateMsgWork);

        Log.d(TAG, "notifyUpdateMsgWorkManager: 開始12小時後提醒更新");
    }

    public WorkInfo.State checkWorkStateFromTag(String tag) {
        ListenableFuture<List<WorkInfo>> workInfoList = WorkManager.getInstance(getApp()).getWorkInfosByTag(tag);
        WorkInfo.State state = WorkInfo.State.CANCELLED;
        try {
            for (WorkInfo workInfo : workInfoList.get()) {
                Log.d("UPLOAD_ERROR_LOG", "檢查WorkManager狀態: " + workInfo.getTags() +","+workInfo.getState());
                state = workInfo.getState();
            }
            //Log.d("WORK_MANAGER", "onCreate: " + workInfoList.get().get(0).getTags() +","+workInfoList.get().get(0).getState());
        } catch (Exception e) {
            Log.d("UPLOAD_ERROR_LOG", "檢查WorkManager狀態: " +e.getLocalizedMessage());
            e.printStackTrace();
        }

        return state;
    }

    public void checkUploadErrorLog() {

        //執行條件：網路連線時
        Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();

        PeriodicWorkRequest workRequest = new PeriodicWorkRequest
                .Builder(UploadErrorLogWork.class, 4, TimeUnit.HOURS) //最短15分
          //      .Builder(UploadErrorLogWork.class, 15, TimeUnit.MINUTES) //最短15分
          //      .setInitialDelay(1, TimeUnit.HOURS)
                .setConstraints(constraints)
                .addTag(WORK_UPLOAD_ERROR_LOG_TAG)
                .build();

        WorkManager.getInstance(getApp())
                .enqueueUniquePeriodicWork("CheckUploadErrorLog", ExistingPeriodicWorkPolicy.KEEP, workRequest);

    }

    public void cancelWorkByTag(String tag) {
        try {
            WorkManager.getInstance(getApp()).cancelAllWorkByTag(tag);
            Log.d(TAG, "取消12小時更新通知");
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "取消12小時更新通知: " + e.getLocalizedMessage());
        }
    }

//            WorkManager.getInstance(getApp()).cancelAllWorkByTag("NotifyUpdateMsgTag");

    //        workManager
//                // requestId is the WorkRequest id
//                .getWorkInfoByIdLiveData(notifyUpdateMsgWork.getId())
//                .observe(this, new Observer<WorkInfo>() {
//                    @Override
//                    public void onChanged(@Nullable WorkInfo workInfo) {
//                        if (workInfo != null) {
//                       //     Data progress = workInfo.getProgress();
//                         //   int value = progress.getInt(progress, 0);
//                            // Do something with progress
//                         //   Log.d("WORK_MANAGER", "onChanged: " + progress);
//                            Log.d("WORK_MANAGER", "onChanged: " + workInfo.getTags());
//                            Log.d("WORK_MANAGER", "onChanged: " + workInfo.getState());
//                        }
//                    }
//                });
}
