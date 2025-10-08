package com.dyaco.spirit_commercial.alert_message;

import static com.dyaco.spirit_commercial.support.WorkoutUtil.getInclineValue;

import android.content.Context;

import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.WindowModalHrUpdate2Binding;
import com.dyaco.spirit_commercial.support.RxTimer;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;

public class ModalHrUpdateWindow2 extends BasePopupWindow<WindowModalHrUpdate2Binding> {

    public ModalHrUpdateWindow2(Context context, boolean isPlus, int num) {
        super(context, 300, 400, 0, GENERAL.TRANSLATION_Y, false, false, true, false);

        // getBinding().tvSpeedNumber.setText(String.valueOf(num / 10f));
        int text;
        String value;
//        if (isTreadmill) {
            text = isPlus ? R.string.your_heart_rate_is_not_high_enough_incline : R.string.your_heart_rate_is_too_high_decreasing_incline;
            value = String.valueOf(getInclineValue(num));
//        } else {
//            text = mContext.getString(isHigh ? R.string.your_heart_rate_is_too_high_decreasing_level : R.string.your_heart_rate_is_not_high_enough_level);
//            value = String.valueOf(num);
//        }

        getBinding().tvSpeedNumber.setText(value);

        getBinding().tvText.setText(text);


        new RxTimer().timer(3000, number -> {
            try {
                dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}
