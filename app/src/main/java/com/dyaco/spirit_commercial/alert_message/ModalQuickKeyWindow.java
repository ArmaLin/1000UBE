package com.dyaco.spirit_commercial.alert_message;

import android.content.Context;

import com.dyaco.spirit_commercial.databinding.WindowModalQuickKeyBinding;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;

public class ModalQuickKeyWindow extends BasePopupWindow<WindowModalQuickKeyBinding> {

    public ModalQuickKeyWindow(Context context) {
        super(context, 0, 608, 0, GENERAL.TRANSLATION_Y,false,true,true,true);

    }

}
