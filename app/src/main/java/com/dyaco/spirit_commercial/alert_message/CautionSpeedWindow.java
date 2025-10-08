package com.dyaco.spirit_commercial.alert_message;

import static com.dyaco.spirit_commercial.support.WorkoutUtil.getSpeedValue;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.FTMS_START_OR_RESUME;
import static com.dyaco.spirit_commercial.support.intdef.UnitEnum.SPEED;
import static com.dyaco.spirit_commercial.support.intdef.UnitEnum.getUnit;

import android.content.Context;
import android.view.View;

import androidx.lifecycle.Observer;

import com.dyaco.spirit_commercial.databinding.WindowCautionSpeedBinding;
import com.dyaco.spirit_commercial.support.MsgEvent;
import com.dyaco.spirit_commercial.support.OnMultiClickListener;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;
import com.jeremyliao.liveeventbus.LiveEventBus;

public class CautionSpeedWindow extends BasePopupWindow<WindowCautionSpeedBinding> {

    public CautionSpeedWindow(Context context, int speed) {
        super(context, 300, 688, 0, GENERAL.TRANSLATION_Y, false, true, true, true);

        String value = String.format("%s %s", getSpeedValue(speed), context.getString(getUnit(SPEED)));
        getBinding().tvSpeedNumber.setText(value);

        getBinding().btnGotIt.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                returnValue(new MsgEvent(true));
                dismiss();
            }
        });


        LiveEventBus.get(FTMS_START_OR_RESUME, Boolean.class).observeForever(observer);
    }

    Observer<Boolean> observer = number -> {
        if (getBinding() != null) {
            getBinding().btnGotIt.callOnClick();
        }

    };

    @Override
    public void dismiss() {
        super.dismiss();

        LiveEventBus.get(FTMS_START_OR_RESUME, Boolean.class).removeObserver(observer);
    }
}
