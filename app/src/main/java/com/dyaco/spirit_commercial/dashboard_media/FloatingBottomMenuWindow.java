package com.dyaco.spirit_commercial.dashboard_media;


import static com.dyaco.spirit_commercial.support.intdef.EventKey.MEDIA_GO_TO_TRAINING;

import android.content.Context;
import android.util.Log;
import android.view.ViewParent;

import com.dyaco.spirit_commercial.databinding.WindowFloatingBottomMenuBinding;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingViewModel;
import com.jeremyliao.liveeventbus.LiveEventBus;

import java.lang.reflect.Field;

public class FloatingBottomMenuWindow extends BasePopupWindow<WindowFloatingBottomMenuBinding> {

    public FloatingBottomMenuWindow(Context context, DeviceSettingViewModel deviceSettingViewModel, boolean isEnabled) {
        super(context, 10, 96, 0, GENERAL.FADE, false, false, true, true);
        if (isEnabled) {
            initView();
        }

        getBinding().setDeviceSetting(deviceSettingViewModel);

//        new RxTimer().interval(1000, number -> {
//            v();
//        });

    }

    private void initView() {
        getBinding().mbTraining.setOnClickListener(v -> {
            //  dismiss();
            LiveEventBus.get(MEDIA_GO_TO_TRAINING).post("");
        });

        //  LiveEventBus.get(ALPHA_FLOATING_MEDIA_BACKGROUND, Boolean.class).observeForever(observer);
    }

//    Observer<Boolean> observer = s -> {
//        if (getBinding() != null)
//            getBinding().vBackground.setVisibility(s ? View.VISIBLE : View.INVISIBLE);
//    };

    @Override
    public void dismiss() {
        super.dismiss();
        //  LiveEventBus.get(ALPHA_FLOATING_MEDIA_BACKGROUND, Boolean.class).removeObserver(observer);
        Log.d("MainDashboardFragment", "dismiss: ");
    }


    public void v() {
        try {
//            setFocusable(true);
//            setClippingEnabled(true);
//            setSplitTouchEnabled(true);
        //    Log.d("EFEFEQQWewe", "vVVVVV: ");
            //  ViewParent viewRootImpl = getWindow().getDecorView().getParent();
            //   ViewParent viewRootImpl = ((MainActivity) mContext).getWindow().getDecorView().getParent();
            ViewParent viewRootImpl = getContentView().getParent().getParent();
            Class viewRootImplClass = viewRootImpl.getClass();


            Field mAttachInfoField = viewRootImplClass.getDeclaredField("mAttachInfo");
            mAttachInfoField.setAccessible(true);
            Object mAttachInfo = mAttachInfoField.get(viewRootImpl);
            Class mAttachInfoClass = mAttachInfo.getClass();

            Field mHasWindowFocusField = mAttachInfoClass.getDeclaredField("mHasWindowFocus");

         //   Log.d("EFEFEQQWewe", "v:11111 " + mHasWindowFocusField.get(mAttachInfo));
            mHasWindowFocusField.setAccessible(true);
        //    mHasWindowFocusField.setBoolean(mAttachInfo,true);
            mHasWindowFocusField.set(mAttachInfo, true);
          //     boolean mHasWindowFocus = (boolean) mHasWindowFocusField.get(mAttachInfo);
        //    Log.d("EFEFEQQWewe", "v:22222 " + mHasWindowFocusField.getName());
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("EFEFEQQWewe", "Exception:" + e.getLocalizedMessage());
        }
    }
}
