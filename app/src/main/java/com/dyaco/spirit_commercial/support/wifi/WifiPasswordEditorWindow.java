package com.dyaco.spirit_commercial.support.wifi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.WindowWifiPasswordEditorBinding;
import com.dyaco.spirit_commercial.support.MsgEvent;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;

public class WifiPasswordEditorWindow extends BasePopupWindow<WindowWifiPasswordEditorBinding> {


    @SuppressLint("ClickableViewAccessibility")
    public WifiPasswordEditorWindow(Context context) {
        super(context, 500, 0, 0, GENERAL.FADE, false, false, true, false);
        initView();

        //顯示鍵盤
        setFocusable(true);
        setWindowLayoutType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);

      //  getBinding().etWeight.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

        getBinding().etInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        });

        getBinding().etInput.requestFocus();


        ImageView endIcon = getBinding().etWeight.findViewById(com.google.android.material.R.id.text_input_end_icon);

        if (endIcon != null) {
            endIcon.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.setAlpha(0.5f); // 觸摸時變淡
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        v.animate().alpha(1.0f).setDuration(200).start(); // 放開時恢復
                        v.performClick();
                        break;
                }
                return true;
            });
        }



        getBinding().etWeight.setEndIconOnClickListener(v -> {

            if (getBinding().etInput.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                getBinding().etInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                getBinding().etWeight.setEndIconDrawable(R.drawable.ic_password_toggle_off); // 切換圖示
            } else {
                getBinding().etInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                getBinding().etWeight.setEndIconDrawable(R.drawable.ic_password_toggle_on);
            }

            if (getBinding().etInput.getText() != null) {
                getBinding().etInput.setSelection(getBinding().etInput.getText().length()); // 確保游標在最後
            }
        });

    }

    private void initView() {

        getBinding().btnClose.setOnClickListener(v -> dismiss());


        getBinding().btnSave.setOnClickListener(v -> {
            if (getBinding().etInput.getText() != null) {
                String password = getBinding().etInput.getText().toString().trim();
                if (TextUtils.isEmpty(password)) return;

                if (password.length() < 8) {
                    getBinding().tvError.setVisibility(View.VISIBLE);  // 顯示錯誤訊息
                    return;
                } else {
                    getBinding().tvError.setVisibility(View.GONE);  // 隱藏錯誤訊息
                }

                returnValue(new MsgEvent(getBinding().etInput.getText().toString()));
                dismiss();
            }
        });
    }
}
