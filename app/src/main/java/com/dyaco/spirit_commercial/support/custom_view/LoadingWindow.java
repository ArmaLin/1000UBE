package com.dyaco.spirit_commercial.support.custom_view;

import android.content.Context;
import android.view.Gravity;

import androidx.annotation.NonNull;

import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.databinding.DialogProgressBinding;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;

public class LoadingWindow extends BasePopupWindow<DialogProgressBinding> {

    public LoadingWindow(@NonNull Context context) {
        super(context, 200, 0, 0, GENERAL.FADE, false, false, true, false);
    }

    public void startDialog() {
        showAtLocation(
                ((MainActivity) mContext).getWindow().getDecorView(),
                Gravity.END | Gravity.BOTTOM,
                0,
                0
        );
    }

    @Override
    public void dismiss() {
        // 不需取消動畫，ProgressBar 自帶動畫會自動清理
        super.dismiss();
    }
}
