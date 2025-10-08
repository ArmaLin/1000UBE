package com.dyaco.spirit_commercial.alert_message;

import android.content.Context;

import com.dyaco.spirit_commercial.databinding.WindowSystemTestInvalidBinding;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;

public class SystemTestInvalidWindow extends BasePopupWindow<WindowSystemTestInvalidBinding> {
    private Context mContext;
    public SystemTestInvalidWindow(Context context) {
        super(context, 0, 0, 0, GENERAL.TRANSLATION_Y,false,true,true,true);
        mContext = context;

    }

}
