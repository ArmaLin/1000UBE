package com.dyaco.spirit_commercial.support;

import android.content.Context;

import androidx.annotation.NonNull;

import com.dyaco.spirit_commercial.databinding.DialogBgBinding;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;

public class LoadingWindowEx extends BasePopupWindow<DialogBgBinding> {


    public LoadingWindowEx(@NonNull Context context,int duration) {
        super(context, duration, 0, 0, GENERAL.FADE,false,false,true,false);

    }

    @Override
    public void dismiss() {

        super.dismiss();
    }

}