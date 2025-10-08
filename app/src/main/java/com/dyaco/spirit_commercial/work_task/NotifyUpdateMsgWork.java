package com.dyaco.spirit_commercial.work_task;

import static com.dyaco.spirit_commercial.support.intdef.EventKey.EVENT_WORK_NOTIFY_UPDATE;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.jeremyliao.liveeventbus.LiveEventBus;

public class NotifyUpdateMsgWork extends Worker {
    private final Context context;

    public NotifyUpdateMsgWork(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {

//       String data = getInputData().getString("time");
//       Log.d("WORK_MANAGER", "this_doWork" + data);
//       Data data2 = new Data.Builder().putString("data", data+":返回数据").build();
//       return Result.success(data2);



//        DeviceSettingBean d = getApp().getDeviceSettingBean();
//        d.setShowUpdateNotify(true);
//        getApp().setDeviceSettingBean(d);
//
//        Log.d(WorkManagerUtil.TAG, "12小時後通知更新APP: " + d.isShowUpdateNotify());


        LiveEventBus.get(EVENT_WORK_NOTIFY_UPDATE).post(true);

        return Result.success();
 //       return Result.failure();
    }
}
