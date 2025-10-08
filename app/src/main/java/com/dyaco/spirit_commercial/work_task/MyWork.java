package com.dyaco.spirit_commercial.work_task;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.dyaco.spirit_commercial.model.webapi.CallWebApi;
import com.dyaco.spirit_commercial.model.webapi.WebApiListener2;
import com.dyaco.spirit_commercial.model.webapi.bean.NotifyUpdateConsoleSuccessBean;

public class MyWork extends Worker {
    private final Context context;

    public MyWork(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {

        new CallWebApi(context).apiNotifyUpdateConsoleSuccess2(new WebApiListener2<NotifyUpdateConsoleSuccessBean>() {
            @Override
            public void onSuccess(NotifyUpdateConsoleSuccessBean data) {
                Log.d("WORK_MANAGER", "11111onSuccess: ");
                //  return Result.success();
            }

            @Override
            public void onFail() {
                Log.d("WORK_MANAGER", "onFail: ");
                //   return Result.failure();
            }
        });
//       String data = getInputData().getString("time");
//       Log.d("WORK_MANAGER", "this_doWork" + data);
//       Data data2 = new Data.Builder().putString("data", data+":返回数据").build();
//       return Result.success(data2);

      //  Log.d("WORK_MANAGER@@@", "@@@@@@@####################doWork:@@@@@@@@@@ ");
        return Result.success();

    }
}
