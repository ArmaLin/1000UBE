package com.dyaco.spirit_commercial.garmin;

import static com.dyaco.spirit_commercial.App.getApp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.WindowGarminPrepareDeviceMsgBinding;
import com.dyaco.spirit_commercial.support.CustomTypefaceSpan;
import com.dyaco.spirit_commercial.support.RxTimer;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;
import com.dyaco.spirit_commercial.support.utils.CheckDoubleClick;

/**
 * 2 提示視窗  準備裝置
 **/
public class GarminPrepareDeviceMsgWindow extends BasePopupWindow<WindowGarminPrepareDeviceMsgBinding> {

    @SuppressLint("StringFormatInvalid")
    public GarminPrepareDeviceMsgWindow(Context context) {
        super(context, 300, 0, 0, GENERAL.FADE, false, false, false, false);


      //  if ("en_US".equalsIgnoreCase(LanguageUtils.getLan())) {
            getBinding().tvText.setText(setSpannableStr(context.getString(R.string.Prepare_your_device__), context.getString(R.string.prepare_your_device_make_sure_that_your_garmin_device_is_not_connected_to_your_smartphone_and_is_in_pairing_mode, context.getString(R.string.Prepare_your_device__))));
     //   } else {
     //       getBinding().tvText.setText(context.getString(R.string.prepare_your_device_make_sure_that_your_garmin_device_is_not_connected_to_your_smartphone_and_is_in_pairing_mode));
    //    }

        getBinding().btnClose.setOnClickListener(v -> dismiss());

        //開始搜尋可配對裝置
        getBinding().btnNext.setOnClickListener(v -> {
            if (CheckDoubleClick.isFastClick()) return;
            GarminPairWindow garminPairWindow = new GarminPairWindow(mContext);
            garminPairWindow.showAtLocation(parentView, Gravity.END | Gravity.BOTTOM, 0, 0);
            new RxTimer().timer(100, number -> {
                try {
                    dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });


      //  LiveEventBus.get(FTMS_START_OR_RESUME, Boolean.class).observeForever(dismissObserver);
    }

  //  Observer<Boolean> dismissObserver = s -> dismiss();

    @Override
    public void dismiss() {
        super.dismiss();
     //   LiveEventBus.get(FTMS_START_OR_RESUME, Boolean.class).removeObserver(dismissObserver);
    }


    private SpannableString setSpannableStr(String str1, String str2) {
        SpannableString spannableString = new SpannableString(str2);
        try {
            int startIndex = str2.indexOf(str1);
            int endIndex = startIndex + str1.length();
            spannableString.setSpan(new CustomTypefaceSpan("", ResourcesCompat.getFont(getApp(), R.font.inter_bold)), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getApp(), R.color.colore24b44)), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return spannableString;

    }
}
