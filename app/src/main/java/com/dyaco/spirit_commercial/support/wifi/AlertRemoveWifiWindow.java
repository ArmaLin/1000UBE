package com.dyaco.spirit_commercial.support.wifi;

import android.content.Context;

import com.dyaco.spirit_commercial.databinding.WindowAlertRemoveWifiBinding;
import com.dyaco.spirit_commercial.support.MsgEvent;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;

public class AlertRemoveWifiWindow extends BasePopupWindow<WindowAlertRemoveWifiBinding> {

    public AlertRemoveWifiWindow(Context context,String name) {
        super(context, 500, 0, 0, GENERAL.FADE,false,false,true,false);


        getBinding().tvStr.setText(name);

        getBinding().btnIgnore.setOnClickListener(view -> {
            returnValue(new MsgEvent(false));
            dismiss();

        });

        getBinding().btnSetMaximum.setOnClickListener(view -> {
            returnValue(new MsgEvent(true));
            dismiss();
        });
    }

}
