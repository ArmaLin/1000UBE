package com.dyaco.spirit_commercial.alert_message;

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

public class WarringCountDownWindow extends BasePopupWindow<WindowWarringCountDownBinding> {

    private WorkoutViewModel w;
    private RxTimer countDownTimer;
    public WarringCountDownWindow(Context context, int type, WorkoutViewModel w) {
        super(context, 300, 0, 0, GENERAL.TRANSLATION_Y, false, true, true, true);
        this.w = w;
        TextView tvWarring = getBinding().tvWarring;
        countDownTimer = new RxTimer();
        countDownTimer.intervalComplete(500, 1000, 30, new RxTimer.RxActionComplete() {
            @Override
            public void action(long number) {
                try {
                    ((Activity) mContext).runOnUiThread(() -> {
                        getBinding().tvCountDown.setText(String.valueOf(30 - number));
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
        tvWarring.setText(R.string.INVALID_TEST_RPM_OUT_OF_RANGE);
    }

    boolean isCancel;
    @Override
    public void dismiss() {
        super.dismiss();
        isCancel = true;
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }
}
