package com.dyaco.spirit_commercial.workout;

import android.animation.ValueAnimator;
import android.content.Context;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.PopupWingateReadyBinding;
import com.dyaco.spirit_commercial.support.RxTimer;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;
import com.github.lzyzsd.circleprogress.DonutProgress;

public class WingateReadyWindow extends BasePopupWindow<PopupWingateReadyBinding> {
    private DonutProgress donutProgress;
    private TextView tvDonutProgress;

    public WingateReadyWindow(Context context) {
        super(context, 200, 0, 0, GENERAL.FADE, false, false, true, false);
        initView();

        new RxTimer().timer(200, number -> start());

    }


    private void initView() {
        donutProgress = getBinding().donutProgress;
        tvDonutProgress = getBinding().tvDonutProgress;
    }

    public void start() {

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
                new RxTimer().timer(500, number -> dismiss());

            }

       //     Timber.tag("WWINNNNNNN").d("start: " + animation.getAnimatedValue() + "," + currentTime);
        });
        valueAnimator.start();

        //  rxTimer.interval3(10, number -> xx(number));
    }
}
