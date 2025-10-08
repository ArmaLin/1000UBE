package com.dyaco.spirit_commercial.alert_message;

import android.content.Context;

import com.dyaco.spirit_commercial.databinding.WindowResumingBinding;
import com.dyaco.spirit_commercial.support.RxTimer;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;


public class ResumingWindow extends BasePopupWindow<WindowResumingBinding> {
    private RxTimer rxTimer;

    public ResumingWindow(Context context) {
        super(context, 0, 0, 0, GENERAL.TRANSLATION_Y, false, true, true, true);
        final long[] i = {3};
        rxTimer = new RxTimer();
        rxTimer.intervalComplete(0, 1000, 3, new RxTimer.RxActionComplete() {
            @Override
            public void action(long number) {
                String n = String.valueOf(i[0] - number);
                getBinding().tvReadyNum.setText((!n.equals("0") ? n : "GO!"));
            }

            @Override
            public void complete() {

                new RxTimer().timer(500, number -> dismiss());

                if (rxTimer != null) {
                    rxTimer.cancel();
                    rxTimer = null;
                }
            }
        });
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (rxTimer != null) {
            rxTimer.cancel();
            rxTimer = null;
        }
    }
}
