package com.dyaco.spirit_commercial.settings;

import android.content.Context;
import android.view.View;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.WindowKeyboardBinding;
import com.dyaco.spirit_commercial.support.MsgEvent;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.custom_view.CustomToast;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;
import com.dyaco.spirit_commercial.support.utils.NumberValidationUtils;

public class KeyboardWindow extends BasePopupWindow<WindowKeyboardBinding> implements View.OnClickListener {
    AppCompatEditText editText;
    private final StringBuffer stringBuffer = new StringBuffer();
    private final boolean isFloat;
    private final String cNum;
    private static final int COST_MAX_LENGTH = 4;
    private final float MAX_VALUE;
    private final float MIN_VALUE;
    String inputStr;
    public KeyboardWindow(Context context, boolean isFloat, String cNum,float MAX_VALUE,float MIN_VALUE, String inputStr) {
        super(context, 500, 936, 0, GENERAL.TRANSLATION_Y, false, true,false,true);

        this.isFloat = isFloat;
        this.cNum = cNum;

        this.MAX_VALUE = MAX_VALUE;
        this.MIN_VALUE = MIN_VALUE;

        this.inputStr = inputStr;

        initView();

    }

    private void initView() {
        editText = getBinding().etNum;

    //   String input = mContext.getString(R.string.Input) + " ";
//        editText.setHint(input + inputStr);
        editText.setHint(inputStr);

//        editText.setText(cNum);
//        stringBuffer.append(cNum);

        getBinding().btnClose.setOnClickListener(v -> dismiss());


        getBinding().keyboard.setOnClickListener(this);
        getBinding().keyboard0.setOnClickListener(this);
        getBinding().keyboard1.setOnClickListener(this);
        getBinding().keyboard2.setOnClickListener(this);
        getBinding().keyboard3.setOnClickListener(this);
        getBinding().keyboard4.setOnClickListener(this);
        getBinding().keyboard5.setOnClickListener(this);
        getBinding().keyboard6.setOnClickListener(this);
        getBinding().keyboard7.setOnClickListener(this);
        getBinding().keyboard8.setOnClickListener(this);
        getBinding().keyboard9.setOnClickListener(this);
        getBinding().keyboardBackspace.setOnClickListener(this);
        getBinding().keyboardComma.setOnClickListener(this);
        getBinding().keyboardHyphen.setOnClickListener(this);
        getBinding().keyboardEnter.setOnClickListener(this);
        getBinding().keyboardPeriod.setOnClickListener(this);
        getBinding().btnClearAll.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if (id == R.id.keyboard_enter) {
            String num = stringBuffer.toString();

            if (NumberValidationUtils.isRealNumber(num) && Float.parseFloat(num) <= MAX_VALUE && Float.parseFloat(num) >= MIN_VALUE) {

                if (".".equals(num.substring(num.length() - 1))) {
                    num = num.substring(0, num.length() - 1);
                }

                returnValue(new MsgEvent(num));
                dismiss();

            } else {
                CustomToast.showToast((MainActivity)mContext,"Please enter the correct number");
              //  Toasty.warning(mContext, "Please enter the correct number", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.keyboard_backspace) {

            if (stringBuffer.length() != 0) {

                stringBuffer.delete(stringBuffer.length() - 1, stringBuffer.length());

                editText.setText(stringBuffer);
            }
        } else if (id == R.id.keyboard_period) {
            if (!isFloat) return;
            if (stringBuffer.length() < COST_MAX_LENGTH) {

                //金額中沒有輸入過小數點才可輸入","，金額中已有輸入過其他數字才可以輸入"."
                if (stringBuffer.indexOf(".") == -1 && stringBuffer.length() != 0) {

                    stringBuffer.append(".");
                    editText.setText(stringBuffer);
                }
            }
        } else if (id == R.id.btnClearAll) {
            stringBuffer.delete(0,stringBuffer.length());
            editText.setText("");
        } else {
            if (id == R.id.keyboard_comma || id == R.id.keyboard__ || id == R.id.keyboard_hyphen) return;
            if (stringBuffer.length() > 0) {
                //已經有兩位小數 就不可再輸入
                if (getDecimalBits(Double.parseDouble(stringBuffer.toString() + "1")) == 3) {
                    return;
                }

                if ("0".equals(stringBuffer.substring(0, 1))) {
                    if (stringBuffer.length() >= 2) {
                        //第一個字是0就不能再輸入且第二個字不是"." 就不可再輸入
                        if (!".".equals(stringBuffer.substring(1, 2))) {

                            return;
                        }
                    } else {

                        return;
                    }
                }
            }


//            //輸入的金額不超過限定長度
//            if (stringBuffer.length() < COST_MAX_LENGTH) {
//
//                String a = ((AppCompatButton) getBinding().getRoot().findViewById(id)).getText().toString();
//                stringBuffer.append(a);
//
//                editText.setText(stringBuffer);
//            }

            //輸入的金額不超過限定長度
            if (stringBuffer.length() < COST_MAX_LENGTH) {

                String a = ((AppCompatButton) getBinding().getRoot().findViewById(id)).getText().toString();

                stringBuffer.append(a);

                if (stringBuffer.length() > 0 && Float.parseFloat(stringBuffer.toString()) <= MAX_VALUE) {
                    editText.setText(stringBuffer);
                } else {
                    stringBuffer.delete(0, stringBuffer.length());
                    editText.setText("");
                }

            }
        }
    }

    private int getDecimalBits(double target) {
        if (0.0 == target % 1) {
            return 0;
        } else {
            String sTarget = String.valueOf(target);
            return sTarget.length() - sTarget.indexOf('.') - 1;
        }
    }
}
