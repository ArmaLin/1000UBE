package com.dyaco.spirit_commercial.work_task;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.dyaco.spirit_commercial.model.webapi.CallWebApi;

public class UploadErrorLogWork  extends Worker {
    private final Context context;

    public UploadErrorLogWork(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {

        Log.d("UPLOAD_ERROR_LOG", "doWork: 4小時執行 ");
        new CallWebApi(context).apiUploadErrorLog();

        return Result.success();

    }

}
