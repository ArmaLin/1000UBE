package com.dyaco.spirit_commercial.support;

import android.content.Context;

import androidx.annotation.NonNull;

import com.dyaco.spirit_commercial.databinding.DialogBgAllBBinding;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;

public class LoadingWindowAllB extends BasePopupWindow<DialogBgAllBBinding> {


    public LoadingWindowAllB(@NonNull Context context) {
        super(context, 0, 0, 0, GENERAL.FADE,false,false,true,false);

    }

    @Override
    public void dismiss() {

        super.dismiss();
    }

}