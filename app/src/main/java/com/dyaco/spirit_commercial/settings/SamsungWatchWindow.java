package com.dyaco.spirit_commercial.settings;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.dyaco.spirit_commercial.App.getDeviceGEM;
import static com.dyaco.spirit_commercial.support.CommonUtils.showException;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.EVENT_NFC_ENABLED;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.SAMSUNG_WATCH_CLOSE_WINDOW;

import android.content.Context;

import androidx.lifecycle.Observer;

import com.corestar.libs.device.DeviceGEM;
import com.dyaco.spirit_commercial.databinding.WindowSamsungWatchBinding;
import com.dyaco.spirit_commercial.support.RxTimer;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;
import com.jeremyliao.liveeventbus.LiveEventBus;

public class SamsungWatchWindow extends BasePopupWindow<WindowSamsungWatchBinding> {
    private String identity;

    public SamsungWatchWindow(Context context) {
        super(context, 200, 0, 0, GENERAL.FADE, false, true, true, true);

        initView();


        LiveEventBus.get(EVENT_NFC_ENABLED, Boolean.class).observeForever(observer);

        LiveEventBus.get(SAMSUNG_WATCH_CLOSE_WINDOW, Boolean.class).observeForever(observer2);


        new RxTimer().timer(500, number -> getDeviceGEM().nfcMessageEnableNfcRadio(DeviceGEM.NFC_READ_EVENT.NFC_READ));

    }

    Observer<Boolean> observer = b -> {
        if (b) {
            if (getBinding() != null) getBinding().a1.setAlpha(1f);
        }
    };

    Observer<Boolean> observer2 = b -> {
        if (b) {
            try {
                getBinding().tvOk.setVisibility(VISIBLE);
                getBinding().ivOK.setVisibility(VISIBLE);

                getBinding().a1.setVisibility(GONE);
                getBinding().inboxTitle.setVisibility(GONE);
                getBinding().bText1.setVisibility(GONE);
                getBinding().bText2.setVisibility(GONE);
            } catch (Exception e) {
                showException(e);
            }
        }
    };

    private void initView() {
        getBinding().btnClose.setOnClickListener(v -> dismiss());
    }

    @Override
    public void dismiss() {
        super.dismiss();

        LiveEventBus.get(EVENT_NFC_ENABLED, Boolean.class).removeObserver(observer);

        LiveEventBus.get(SAMSUNG_WATCH_CLOSE_WINDOW, Boolean.class).removeObserver(observer2);

        getDeviceGEM().nfcMessageDisableNfcRadio();
    }

}
