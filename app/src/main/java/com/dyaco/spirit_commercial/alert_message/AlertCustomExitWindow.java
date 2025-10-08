package com.dyaco.spirit_commercial.alert_message;

import android.content.Context;

import com.dyaco.spirit_commercial.databinding.WindowAlertCustomExitBinding;
import com.dyaco.spirit_commercial.support.MsgEvent;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;

public class AlertCustomExitWindow extends BasePopupWindow<WindowAlertCustomExitBinding> {

    public AlertCustomExitWindow(Context context) {
        super(context, 300, 688, 0, GENERAL.TRANSLATION_Y,false,true,true,true);

        getBinding().btnIgnore.setOnClickListener(view -> {
            returnValue(new MsgEvent(false));
            dismiss();

        });

        //w.selTargetHrBpm 200 > HrTargetHrMax 185 > 選定的目標心率超過您年齡預測的最大心率。
        getBinding().btnSetMaximum.setOnClickListener(view -> {
            returnValue(new MsgEvent(true));
            dismiss();
        });
    }

}
