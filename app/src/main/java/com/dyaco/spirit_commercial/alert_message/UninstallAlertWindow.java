package com.dyaco.spirit_commercial.alert_message;

import android.content.Context;

import com.dyaco.spirit_commercial.databinding.WindowUninstallAlerttBinding;
import com.dyaco.spirit_commercial.support.MsgEvent;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;
import com.dyaco.spirit_commercial.support.utils.CheckDoubleClick;

public class UninstallAlertWindow extends BasePopupWindow<WindowUninstallAlerttBinding> {


    public UninstallAlertWindow(Context context) {
        super(context, 300, 0, 0, GENERAL.FADE, false, false, true, false);

        getBinding().btnRestart.setOnClickListener(view -> {
            if (CheckDoubleClick.isFastClick()) return;
            returnValue(new MsgEvent(true));
            dismiss();
        });

        getBinding().btnNotNow.setOnClickListener(view -> {
            if (CheckDoubleClick.isFastClick()) return;
            returnValue(new MsgEvent(false));
            dismiss();
        });
    }


    @Override
    public void dismiss() {
        super.dismiss();

    }



}
