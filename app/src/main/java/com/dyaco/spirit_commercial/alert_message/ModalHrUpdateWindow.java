package com.dyaco.spirit_commercial.alert_message;

import static com.dyaco.spirit_commercial.MainActivity.isTreadmill;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.getSpeedValue;

import android.content.Context;
import android.view.View;

import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.WindowModalHrUpdateBinding;
import com.dyaco.spirit_commercial.support.MsgEvent;
import com.dyaco.spirit_commercial.support.OnMultiClickListener;
import com.dyaco.spirit_commercial.support.RxTimer;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;

public class ModalHrUpdateWindow extends BasePopupWindow<WindowModalHrUpdateBinding> {

    public ModalHrUpdateWindow(Context context, boolean isHigh, int num) {
        super(context, 300, 608, 0, GENERAL.TRANSLATION_Y, false, false, true, false);

       // getBinding().tvSpeedNumber.setText(String.valueOf(num / 10f));
        int text;
        String value;
        if (isTreadmill) {
            text = isHigh ? R.string.your_heart_rate_is_too_high_decreasing_speed : R.string.your_heart_rate_is_not_high_enough;
            num = isHigh ? num - 1 : num + 1;
            value = String.valueOf(getSpeedValue(num));
        } else {
            text = isHigh ? R.string.your_heart_rate_is_too_high_decreasing_level : R.string.your_heart_rate_is_not_high_enough_level;
            value = String.valueOf(num);
        }

        getBinding().tvSpeedNumber.setText(value);

        getBinding().tvText.setText(text);


        if (isTreadmill) {
            getBinding().btnGotIt.setOnClickListener(new OnMultiClickListener() {
                @Override
                public void onMultiClick(View v) {
                    returnValue(new MsgEvent(true));
                    dismiss();
                }
            });
        } else {
            new RxTimer().timer(4000, number -> {
                try {
                    dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            getBinding().btnGotIt.setVisibility(View.INVISIBLE);
        }

    }

}
