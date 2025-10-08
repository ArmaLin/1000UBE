package com.dyaco.spirit_commercial.login;

import android.content.Context;
import android.view.Gravity;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.FragmentCreateAccountGenderBinding;
import com.dyaco.spirit_commercial.support.MsgEvent;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;
import com.dyaco.spirit_commercial.support.utils.CheckDoubleClick;

public class CreateAccountGenderWindow extends BasePopupWindow<FragmentCreateAccountGenderBinding> {

    public CreateAccountGenderWindow(Context context) {
        super(context, 0, 920, 0, GENERAL.TRANSLATION_Y,false,false,false,false);
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);

        initView();
    }

    private void initView() {
        getBinding().btnClose.setOnClickListener(v -> {
            if (CheckDoubleClick.isFastClick()) return;
            duration = 300;
            returnValue(new MsgEvent(true));
            dismiss();
        });

        getBinding().btnBack.setOnClickListener(v -> {
            if (CheckDoubleClick.isFastClick()) return;
            duration = 0;
            returnValue(new MsgEvent(false));
            dismiss();
        });

        getBinding().btnNext.setOnClickListener(v -> {
            if (CheckDoubleClick.isFastClick()) return;
            CreateAccountWeightWindow createAccountWeightWindow = new CreateAccountWeightWindow(mContext);
            createAccountWeightWindow.showAtLocation(((MainActivity) mContext).getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);
            createAccountWeightWindow.setOnCustomDismissListener(new OnCustomDismissListener() {
                @Override
                public void onStartDismiss(MsgEvent value) {
                    if (value != null) {
                        if (((boolean) value.getObj())) {
                            duration = 0;
                            returnValue(value);
                            dismiss();
                        }
                    }
                }
                @Override
                public void onDismiss() {

                }
            });
        });

        getBinding().btnFemale.setOnClickListener(v -> selectGender());

        getBinding().btnMale.setOnClickListener(v -> selectGender());
    }

    private void selectGender() {
        getBinding().maleText.setTextColor(getBinding().btnMale.isChecked() ?
                ContextCompat.getColor(mContext, R.color.color1396ef) : ContextCompat.getColor(mContext, R.color.color5a7085));
        getBinding().femaleText.setTextColor(getBinding().btnFemale.isChecked() ?
                ContextCompat.getColor(mContext, R.color.color1396ef) : ContextCompat.getColor(mContext, R.color.color5a7085));
    }
}

