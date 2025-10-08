package com.dyaco.spirit_commercial.maintenance_mode;

import android.content.Context;

import com.dyaco.spirit_commercial.databinding.WindowTestingBinding;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;

public class TestingWindow extends BasePopupWindow<WindowTestingBinding> {
    private Context mContext;
    public TestingWindow(Context context) {
        super(context, 0, 0, 0, GENERAL.TRANSLATION_Y,false,true,false,true);
        mContext = context;

    }

}
