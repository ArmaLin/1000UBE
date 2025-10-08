package com.dyaco.spirit_commercial.alert_message;

import android.content.Context;

import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.databinding.WindowSafetyKeyBinding;
import com.dyaco.spirit_commercial.support.CommonUtils;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;

public class SafetyKeyWindow extends BasePopupWindow<WindowSafetyKeyBinding> {
    private final Context mContext;
    public SafetyKeyWindow(Context context) {
        super(context, 0, 0, 0, GENERAL.TRANSLATION_Y,false,true,true,true);
        mContext = context;
    }

    public void setText(int text) {
        ((MainActivity) mContext).runOnUiThread(() ->
                CommonUtils.iExc(()->getBinding().tvErrorMsg.setText(text)));
    }

}
