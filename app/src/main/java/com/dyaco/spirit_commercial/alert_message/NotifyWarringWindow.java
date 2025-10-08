package com.dyaco.spirit_commercial.alert_message;

import android.content.Context;

import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.WindowWarringMsgBinding;
import com.dyaco.spirit_commercial.support.base_component.BasePopupMsgWindow;

public class NotifyWarringWindow extends BasePopupMsgWindow<WindowWarringMsgBinding> {

    public NotifyWarringWindow(Context context, String value, int drawableRes) {
        super(context);

        getBinding().notifyMsg.setBackgroundResource(drawableRes);
    //    getBinding().notifyMsg.setBackgroundResource(drawableRes);

        getBinding().notifyMsg.setText(value);

        getBinding().notifyMsg.setOnClickListener(v -> {

            try {
                if (value.equals(context.getString(R.string.bt_notify))){
                    MainActivity.isBtNotifyClose = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            dismiss();
        });
    }
}


//import android.content.Context;
//import android.view.Gravity;
//import android.view.View;
//import android.view.WindowManager;
//
//import com.dyaco.spirit_commercial.databinding.WindowWarringMsgBinding;
//import com.dyaco.spirit_commercial.support.base_component.BaseWindow;
//
//public class NotifyWarringWindow extends BaseWindow<WindowWarringMsgBinding> {
//
//    public NotifyWarringWindow(Context context, String value, int drawableRes) {
//        super(context, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, Gravity.START, 100, -6);
//
//        getBinding().notifyMsg.setBackgroundResource(drawableRes);
//
//        getBinding().notifyMsg.setText(value);
//
//        getBinding().notifyMsg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dismiss();
//            }
//        });
//    }
//}
