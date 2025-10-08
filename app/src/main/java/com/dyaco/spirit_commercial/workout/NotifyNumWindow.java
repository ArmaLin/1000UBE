package com.dyaco.spirit_commercial.workout;

import static com.dyaco.spirit_commercial.support.intdef.GENERAL.BAR_TYPE_INCLINE;

import android.content.Context;

import com.dyaco.spirit_commercial.databinding.WindowNotifyNumBinding;
import com.dyaco.spirit_commercial.support.FormulaUtil;
import com.dyaco.spirit_commercial.support.RxTimer;
import com.dyaco.spirit_commercial.support.base_component.BasePopupMsgWindow;

public class NotifyNumWindow extends BasePopupMsgWindow<WindowNotifyNumBinding> {


    public NotifyNumWindow(Context context, float value, float maxValue, int drawableRes, boolean isFloat, boolean isHeartRate,int barType) {
        super(context);
        getBinding().msgBg.setBackgroundResource(drawableRes);
       // getBinding().msgValue.setText(isFloat ? CommonUtils.formatDecimal(value,1) : String.valueOf((int) value));
        if (!isHeartRate) {
            getBinding().msgValue.setText(isFloat ? String.valueOf(value) : String.valueOf((int) value));
        } else {
            getBinding().msgValue.setText(barType == BAR_TYPE_INCLINE ? String.valueOf((int) value) : String.valueOf(value));
        }

        if (!isHeartRate) {
            getBinding().msgValueMax.setText(isFloat ? String.format("%s Max", FormulaUtil.formatDecimal(maxValue)) : String.format("%s Max", (int) maxValue));
        } else {
            getBinding().msgValueMax.setText(barType == BAR_TYPE_INCLINE ?  String.format("%s Max",(int) maxValue) : String.format("%s Max", FormulaUtil.formatDecimal(maxValue)));
        }
        new RxTimer().timer(1000, n -> dismiss());
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}

