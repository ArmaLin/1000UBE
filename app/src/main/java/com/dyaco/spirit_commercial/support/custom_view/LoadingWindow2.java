package com.dyaco.spirit_commercial.support.custom_view;

import android.content.Context;
import android.view.Gravity;

import androidx.annotation.NonNull;

import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.databinding.DialogProgress2Binding;
import com.dyaco.spirit_commercial.support.RxTimer;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;

public class LoadingWindow2 extends BasePopupWindow<DialogProgress2Binding> {


    public LoadingWindow2(@NonNull Context context) {
        super(context, 200, 0, 0, GENERAL.FADE, false, false, true, false);

        initDialog();

        new RxTimer().timer(1000 * 60 * 3, n -> dismiss());
    }

    private void initDialog() {

//        getBinding().image.setBackgroundResource(R.drawable.load_animation);
//        AnimationDrawable rocketAnimation = (AnimationDrawable) getBinding().image.getBackground();
//        rocketAnimation.start();
    }

    public void startDialog() {
        showAtLocation(((MainActivity) mContext).getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

}