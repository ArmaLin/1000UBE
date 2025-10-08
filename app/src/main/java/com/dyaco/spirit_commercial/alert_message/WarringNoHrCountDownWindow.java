package com.dyaco.spirit_commercial.alert_message;

import static com.dyaco.spirit_commercial.support.intdef.GENERAL.WARRING_NO_HR;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.NO_HR_HR;

import android.app.Activity;
import android.content.Context;
import android.widget.TextView;

import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.WindowWarringCountDownBinding;
import com.dyaco.spirit_commercial.support.MsgEvent;
import com.dyaco.spirit_commercial.support.RxTimer;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;
import com.dyaco.spirit_commercial.viewmodel.WorkoutViewModel;

public class WarringNoHrCountDownWindow extends BasePopupWindow<WindowWarringCountDownBinding> {

    private WorkoutViewModel w;
    private RxTimer countDownTimer;
    private final int countDownTime = 30;
    public WarringNoHrCountDownWindow(Context context, int type, WorkoutViewModel w) {
        super(context, 300, 0, 0, GENERAL.TRANSLATION_Y, false, false, true, false);
        this.w = w;
        TextView tvWarring = getBinding().tvWarring;

        switch (type) {
            case WARRING_NO_HR:
                tvWarring.setText(R.string.no_heart_rate_device_detected_finishing_the_program);
                break;
        }

        countDownTimer = new RxTimer();
        countDownTimer.intervalComplete(500, 1000, countDownTime, new RxTimer.RxActionComplete() {
            @Override
            public void action(long number) {
                try {
                    if (w.currentHeartRate.get() >= NO_HR_HR) {
                       dismiss();
                    }

//                    if (number == 10) {
//                        w.currentHeartRate.set(NO_HR_HR);
//                    }

                    ((Activity) mContext).runOnUiThread(() -> {
                        getBinding().tvCountDown.setText(String.valueOf(countDownTime - number));
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void complete() {
                try {
                    if (isCancel) return;
                    returnValue(new MsgEvent(true));
                    dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    boolean isCancel;
    @Override
    public void dismiss() {
        super.dismiss();
        cancelTimer();
    }

    private void cancelTimer() {
        isCancel = true;
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }
}
