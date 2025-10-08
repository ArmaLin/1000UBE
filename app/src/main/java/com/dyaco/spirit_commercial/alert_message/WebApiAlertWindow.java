package com.dyaco.spirit_commercial.alert_message;

import android.content.Context;
import android.view.View;

import androidx.lifecycle.Observer;

import com.dyaco.spirit_commercial.databinding.WindowWebapiAlertBinding;
import com.dyaco.spirit_commercial.support.MsgEvent;
import com.dyaco.spirit_commercial.support.OnMultiClickListener;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;

public class WebApiAlertWindow extends BasePopupWindow<WindowWebapiAlertBinding> {
    public WebApiAlertWindow(Context context,String errorText) {
        super(context, 300, 688, 0, GENERAL.FADE, false, true, true, true);

        getBinding().tvSpeedNumber.setText(errorText);

        getBinding().btnGotIt.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                returnValue(new MsgEvent(true));
                dismiss();
            }
        });


    }

    Observer<Boolean> observer = number -> {
        if (getBinding() != null) {
            getBinding().btnGotIt.callOnClick();
        }

    };

    @Override
    public void dismiss() {
        super.dismiss();
    }

}
