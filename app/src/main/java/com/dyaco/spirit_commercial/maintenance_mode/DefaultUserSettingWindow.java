package com.dyaco.spirit_commercial.maintenance_mode;

import android.content.Context;
import android.view.View;
import android.widget.RadioButton;

import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.PopupSettingBinding;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;

import java.util.ArrayList;

public class DefaultUserSettingWindow extends BasePopupWindow<PopupSettingBinding> {
    public DefaultUserSettingWindow(Context context) {
        super(context, 500, 0, 1303, GENERAL.TRANSLATION_X,false,true,true,true);
        initView();


        getBinding().settingTitle.setText(R.string.Default_User_Settings);


        getBinding().rgSelectUnit.setOnCheckedChangeListener((group, checkedId) -> {
            ArrayList<RadioButton> listOfRadioButtons = new ArrayList<>();
            for (int i = 0; i < getBinding().rgSelectUnit.getChildCount(); i++) {
                View o = getBinding().rgSelectUnit.getChildAt(i);
                if (o instanceof RadioButton) {
                    listOfRadioButtons.add((RadioButton) o);
                    if (listOfRadioButtons.get(i).getId() == checkedId) {

                    }
                }
            }
        });


        getBinding().rgSelectLanguage.setOnCheckedChangeListener((group, checkedId) -> {
            ArrayList<RadioButton> listOfRadioButtons = new ArrayList<>();
            for (int i = 0; i < getBinding().rgSelectLanguage.getChildCount(); i++) {
                View o = getBinding().rgSelectLanguage.getChildAt(i);
                if (o instanceof RadioButton) {
                    listOfRadioButtons.add((RadioButton) o);
                    if (listOfRadioButtons.get(i).getId() == checkedId) {

                    }
                }
            }
        });
    }

    private void initView() {

        getBinding().btnClose.setOnClickListener(v -> dismiss());
    }
}
