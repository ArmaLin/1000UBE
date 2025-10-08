package com.dyaco.spirit_commercial.alert_message;

import android.content.Context;

import com.dyaco.spirit_commercial.databinding.WindowBackgroundBinding;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;

public class BackgroundWindow extends BasePopupWindow<WindowBackgroundBinding> {
    public BackgroundWindow(Context context) {
        super(context, 0, 0, 0, GENERAL.TRANSLATION_Y, false, false,true,false);
    }
}
