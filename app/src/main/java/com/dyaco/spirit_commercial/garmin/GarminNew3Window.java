package com.dyaco.spirit_commercial.garmin;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.dyaco.spirit_commercial.databinding.WindowGarminNew3Binding;
import com.dyaco.spirit_commercial.support.MsgEvent;
import com.dyaco.spirit_commercial.support.RxTimer;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;
import com.dyaco.spirit_commercial.support.utils.CheckDoubleClick;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class GarminNew3Window extends BasePopupWindow<WindowGarminNew3Binding> {
    private Context mContext;

    public GarminNew3Window(Context context) {
        super(context, 300, 0, 0, GENERAL.FADE, false, false, false,false);
        mContext = context;

        setFocusable(true);

        TextInputLayout pingCodeLayout = getBinding().pingCodeLayout;
        TextInputEditText etPingCode = getBinding().etPingCode;

        etPingCode.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(etPingCode, InputMethodManager.SHOW_IMPLICIT);

        etPingCode.setOnFocusChangeListener((v, hasFocus) -> {
            Log.d("TAG", "onFocusChange: " + hasFocus);
            if (hasFocus) {
                setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        });

        etPingCode.requestFocus();

        pingCodeLayout.setEndIconOnClickListener(v -> {
            etPingCode.setText("");
        });

        etPingCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 6) {
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(etPingCode.getWindowToken(), 0);
                }
            }
        });

        getBinding().btnClose.setOnClickListener(v -> dismiss());

        getBinding().btnConfirm.setOnClickListener(v -> dismiss());

        getBinding().btnBack.setOnClickListener(v -> {
            if (CheckDoubleClick.isFastClick()) return;
            GarminPairWindow garminPairWindow = new GarminPairWindow(mContext);
            garminPairWindow.showAtLocation(parentView, Gravity.END | Gravity.BOTTOM, 0, 0);
            garminPairWindow.setOnCustomDismissListener(new OnCustomDismissListener() {
                @Override
                public void onStartDismiss(MsgEvent value) {

                }

                @Override
                public void onDismiss() {

                }
            });

            new RxTimer().timer(100, number -> dismiss());
        });

     //   fullScreenImmersive(getContentView());
    }


}
