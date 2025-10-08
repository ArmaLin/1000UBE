package com.dyaco.spirit_commercial.maintenance_mode;

import static com.dyaco.spirit_commercial.App.getApp;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import androidx.core.content.ContextCompat;

import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.WindowUsageLimitReachedBinding;
import com.dyaco.spirit_commercial.support.MsgEvent;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;

public class UsageLimitReachedWindow extends BasePopupWindow<WindowUsageLimitReachedBinding> {

    String passCode;

    public UsageLimitReachedWindow(Context context) {
        super(context, 200, 0, 0, GENERAL.FADE, false, false, false, false);
        initView();

        //顯示鍵盤
        setFocusable(true);
        setWindowLayoutType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);


        passCode = getApp().getDeviceSettingBean().getLimitCode();
      //  getBinding().etLimitCode.setTransformationMethod(HideReturnsTransformationMethod.getInstance());


        getBinding().etLimitCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.d("TAG", "onFocusChange: " + hasFocus);
                if (hasFocus) {
                    setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

        getBinding().etLimitCode.requestFocus();



        getBinding().etLimitCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                getBinding().etLimitCode.setTextColor(ContextCompat.getColorStateList(mContext, R.color.white));
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 6) {
                    if (editable.toString().equals(passCode)) {
                        hideSoftKeyboard(getBinding().etLimitCode);
                        returnValue(new MsgEvent(true));
                        dismiss();
                    } else {
                        getBinding().etLimitCode.setTextColor(ContextCompat.getColorStateList(mContext, R.color.colore24b44));
                     //   Toasty.error(mContext,"ERROR",Toasty.LENGTH_SHORT).show();
                    }
                }
            }
        });


        getBinding().etLimitCode.setOnEditorActionListener((textView, actionId, keyEvent) -> {

            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (getBinding().etLimitCode.getText() != null && getBinding().etLimitCode.getText().length() == 4) {
                    if ("2222".equals(getBinding().etLimitCode.getText().toString())) { //萬用密碼
                        hideSoftKeyboard(getBinding().etLimitCode);
                        returnValue(new MsgEvent(true));
                        dismiss();
                    }
                }
                return true;
            }
            return false;
        });

    }

    private void initView() {

        getBinding().btnClose.setOnClickListener(v -> {
            hideSoftKeyboard(getBinding().btnClose);
            dismiss();
        });

    }

    public void hideSoftKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
}
