package com.dyaco.spirit_commercial.alert_message;

import android.content.Context;
import android.util.Log;

import com.dyaco.spirit_commercial.databinding.WindowSplashBinding;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;

public class SplashWindow extends BasePopupWindow<WindowSplashBinding> {
    public SplashWindow(Context context) {
        super(context, 0,1080 , 1920, GENERAL.TRANSLATION_Y, false, false,true,false);

        Log.d("AAAAAAAAA", "SplashWindow: ");
    }
}
