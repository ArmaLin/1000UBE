package com.dyaco.spirit_commercial.alert_message;

import android.content.Context;
import android.view.View;

import com.dyaco.spirit_commercial.databinding.WindowSystemErrorBinding;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;
import com.dyaco.spirit_commercial.viewmodel.ErrorInfo;

public class SystemErrorWindow extends BasePopupWindow<WindowSystemErrorBinding> {
    private Context mContext;
    private ErrorInfo errorInfo;
    public SystemErrorWindow(Context context, ErrorInfo errorInfo) {
        super(context, 0, 0, 0, GENERAL.TRANSLATION_Y,false,true,true,true);
        mContext = context;
        this.errorInfo = errorInfo;

        getBinding().tvErrorMsg.setText(errorInfo.getTitle());

        getBinding().btnClose.setVisibility(View.VISIBLE);
        getBinding().btnContactSupport.setVisibility(View.VISIBLE);

        getBinding().btnClose.setOnClickListener(view -> dismiss());
        getBinding().btnContactSupport.setOnClickListener(view -> dismiss());
    }

}
