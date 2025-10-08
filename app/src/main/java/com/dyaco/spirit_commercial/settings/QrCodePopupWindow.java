package com.dyaco.spirit_commercial.settings;

import static com.dyaco.spirit_commercial.App.getApp;

import android.content.Context;
import android.os.Looper;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dyaco.spirit_commercial.databinding.PopupQrcodeBinding;
import com.dyaco.spirit_commercial.support.CommonUtils;
import com.dyaco.spirit_commercial.support.GlideApp;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;

public class QrCodePopupWindow extends BasePopupWindow<PopupQrcodeBinding> {
    private String identity;
    public QrCodePopupWindow(Context context) {
        super(context, 500, 0, 795, GENERAL.TRANSLATION_X,false,true,true,true);

        initView();
    }

    private void initView() {
        Looper.myQueue().addIdleHandler(() -> {

           // identity = getApp().getIdentity();
            getBinding().btnClose.setOnClickListener(v -> dismiss());

//            getBinding().actionImage.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    dismiss();
//                }
//            });


            GlideApp.with(getApp())
                    .load(new CommonUtils().createQRCode(getApp().getDeviceSettingBean().getMachine_mac(), 384, 8))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(getBinding().actionImage);

//            rxTimer = new RxTimer();
//            rxTimer.interval(2000, number -> apiSyncDeviceSyncUser());

            return false;
        });

    }


}
