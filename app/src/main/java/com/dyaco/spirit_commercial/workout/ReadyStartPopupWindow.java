package com.dyaco.spirit_commercial.workout;

import static com.dyaco.spirit_commercial.support.intdef.EventKey.START_WORKOUT;

import android.animation.ValueAnimator;
import android.content.Context;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;

import com.corestar.libs.device.DeviceSpiritC;
import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.PopupReadystartBinding;
import com.dyaco.spirit_commercial.support.RxTimer;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.intdef.AppStatusIntDef;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;
import com.dyaco.spirit_commercial.viewmodel.AppStatusViewModel;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.jeremyliao.liveeventbus.LiveEventBus;

public class ReadyStartPopupWindow extends BasePopupWindow<PopupReadystartBinding> {
    private DonutProgress donutProgress;
    private TextView tvDonutProgress;

    public ReadyStartPopupWindow(Context context) {
        super(context, 200, 0, 0, GENERAL.FADE, false, false, true, false);
        initView();

        // if (isTreadmill) {
        new RxTimer().timer(200, number -> start());
//        } else {
//            getBinding().bBike.setVisibility(View.VISIBLE);
//
//            AppStatusViewModel appStatusViewModel = new ViewModelProvider((MainActivity) mContext).get(AppStatusViewModel.class);
//            appStatusViewModel.currentStatus.set(AppStatusIntDef.STATUS_RUNNING);
//
//            LiveEventBus.get(START_WORKOUT).post("");
//
//            //  dismiss();
//
//            new RxTimer().timer(500, number -> dismiss());
//        }
    }


    private void initView() {
        donutProgress = getBinding().donutProgress;
        tvDonutProgress = getBinding().tvDonutProgress;
    }

    public void start() {

        ((MainActivity) mContext).uartConsole.setDevMainMode(DeviceSpiritC.MAIN_MODE.RUNNING);


        ValueAnimator valueAnimator = ValueAnimator.ofInt(25, 100);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setDuration(3000);
        valueAnimator.addUpdateListener(animation -> {
            String c = String.valueOf((int) animation.getAnimatedValue());
            long currentTime = animation.getCurrentPlayTime();
            donutProgress.setDonut_progress(c);

            if (currentTime < 1000) {
                tvDonutProgress.setText("3");
            } else if (currentTime < 2000) {
                tvDonutProgress.setText("2");
            } else if (currentTime < 3000) {
                tvDonutProgress.setText("1");
            } else {
                tvDonutProgress.setText(R.string.GO_);

                AppStatusViewModel appStatusViewModel = new ViewModelProvider((MainActivity) mContext).get(AppStatusViewModel.class);
                appStatusViewModel.currentStatus.set(AppStatusIntDef.STATUS_RUNNING);

                LiveEventBus.get(START_WORKOUT).post("");

                new RxTimer().timer(300, number -> dismiss());

            }
        });
        valueAnimator.start();

        //  rxTimer.interval3(10, number -> xx(number));
    }
}
