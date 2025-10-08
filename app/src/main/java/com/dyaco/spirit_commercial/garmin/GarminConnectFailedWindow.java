package com.dyaco.spirit_commercial.garmin;

import android.content.Context;

import com.dyaco.spirit_commercial.databinding.WindowGarminConnectFailedBinding;
import com.dyaco.spirit_commercial.support.MsgEvent;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;
import com.dyaco.spirit_commercial.support.utils.CheckDoubleClick;

/**
 * Garmin 連線失敗 提示視窗
 **/
public class GarminConnectFailedWindow extends BasePopupWindow<WindowGarminConnectFailedBinding> {

    public GarminConnectFailedWindow(Context context) {
        super(context, 300, 0, 0, GENERAL.FADE, false, false, false, false);

        getBinding().btnClose.setOnClickListener(v -> {
            if (CheckDoubleClick.isFastClick()) return;
            returnValue(new MsgEvent(false));
            dismiss();
        });
        getBinding().btnBack.setOnClickListener(v -> {
            if (CheckDoubleClick.isFastClick()) return;
            dismiss();
        });

        getBinding().btnTryAgain.setOnClickListener(v -> {
            if (CheckDoubleClick.isFastClick()) return;
            returnValue(new MsgEvent(1, true));

            dismiss();
        });


    //    LiveEventBus.get(FTMS_START_OR_RESUME, Boolean.class).observeForever(dismissObserver);
    }

 //   Observer<Boolean> dismissObserver = s -> dismiss();

    @Override
    public void dismiss() {
        super.dismiss();
     //   LiveEventBus.get(FTMS_START_OR_RESUME, Boolean.class).removeObserver(dismissObserver);
    }
}
