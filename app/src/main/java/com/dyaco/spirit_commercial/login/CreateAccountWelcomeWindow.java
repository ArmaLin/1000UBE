package com.dyaco.spirit_commercial.login;

import android.content.Context;
import android.view.Gravity;
import android.view.View;

import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.FragmentCreateAccountWelcomeBinding;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;
import com.dyaco.spirit_commercial.support.utils.CheckDoubleClick;

public class CreateAccountWelcomeWindow extends BasePopupWindow<FragmentCreateAccountWelcomeBinding> {

    public CreateAccountWelcomeWindow(Context context) {
        super(context, 0, 920, 0, GENERAL.TRANSLATION_Y, false, true, false, false);

    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);

        initView();
    }

    private void initView() {
        getBinding().tvText1.setText(mContext.getString(R.string.welcome_anna, "Anna"));
        getBinding().btnClose.setOnClickListener(view -> {
            if (CheckDoubleClick.isFastClick()) return;
            isClearDark = true;
            dismiss();
        });
        getBinding().btnSkip.setOnClickListener(view -> {
            if (CheckDoubleClick.isFastClick()) return;
            isClearDark = true;
            dismiss();
        });
        getBinding().btnNext.setOnClickListener(view -> {
            if (CheckDoubleClick.isFastClick()) return;
            CreateAccountAgeWindow createAccountAgeWindow = new CreateAccountAgeWindow(mContext);
            createAccountAgeWindow.showAtLocation(((MainActivity) mContext).getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);
            dismiss();
        });
    }
}
