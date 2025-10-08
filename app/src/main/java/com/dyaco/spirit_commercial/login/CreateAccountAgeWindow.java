package com.dyaco.spirit_commercial.login;

import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.AGE_DEF;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.AGE_MAX;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.AGE_MIN;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;

import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.databinding.FragmentCreateAccountAgeBinding;
import com.dyaco.spirit_commercial.support.MsgEvent;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.custom_view.wheelPicker.OptionsPickerView;
import com.dyaco.spirit_commercial.support.custom_view.wheelPicker.WheelView;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;
import com.dyaco.spirit_commercial.support.utils.CheckDoubleClick;

import java.util.ArrayList;
import java.util.List;

public class CreateAccountAgeWindow extends BasePopupWindow<FragmentCreateAccountAgeBinding> {

    public CreateAccountAgeWindow(Context context) {
        super(context, 0, 920, 0, GENERAL.TRANSLATION_Y,false,false,false,true);
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);

        initView();
        initAgeSelected();
    }

    @SuppressWarnings("unchecked")
    private void initAgeSelected() {
        List<String> list1 = new ArrayList<>(1);
        for (int i = AGE_MIN; i <= AGE_MAX; i++) {
            list1.add(String.valueOf(i));
        }
        OptionsPickerView<String> pickAge = getBinding().pickAge;
        pickAge.setData(list1);
        pickAge.setVisibleItems(8);
        //   opv1.setDividerPaddingForWrap(10, true);
        pickAge.setNormalItemTextColor(Color.parseColor("#5a7085"));
        pickAge.setTextSize(54, false);
        pickAge.setCurved(true);
        //  pickAge.setTextBoundaryMargin(10, true);
        pickAge.setCurvedArcDirection(WheelView.CURVED_ARC_DIRECTION_CENTER);
        pickAge.setCurvedArcDirectionFactor(1.0f);
        pickAge.setSelectedItemTextColor(Color.parseColor("#ffffff"));
        pickAge.setCyclic(true);
//        pickAge.setDividerHeight(2, true);
//        pickAge.setDividerPadding(80);
//        pickAge.setDividerType(WheelView.DIVIDER_TYPE_FILL);
//        pickAge.setDividerColorRes(R.color.colorCd5bff);
//        pickAge.setDividerPaddingForWrap(70, true);
//        pickAge.setShowDivider(true);
        //  pickAge.setLineSpacing(14,true);
        pickAge.setOnOptionsSelectedListener((opt1Pos, opt1Data, opt2Pos, opt2Data, opt3Pos, opt3Data) -> {
            if (opt1Data == null) return;
        });

        pickAge.setOpt1SelectedPosition(AGE_DEF - AGE_MIN, false);

//        Typeface typeface = mContext.getResources().getFont(R.font.inter_bold);
//        pickCmHeight.setTypeface(typeface);
    }

    private void initView() {
        getBinding().btnClose.setOnClickListener(v -> {
            if (CheckDoubleClick.isFastClick()) return;
            duration = 300;
            dismiss();
        });

        getBinding().btnNext.setOnClickListener(v -> {
            if (CheckDoubleClick.isFastClick()) return;
            CreateAccountGenderWindow createAccountGenderWindow = new CreateAccountGenderWindow(mContext);
            createAccountGenderWindow.showAtLocation(((MainActivity) mContext).getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);
            createAccountGenderWindow.setOnCustomDismissListener(new OnCustomDismissListener() {
                @Override
                public void onStartDismiss(MsgEvent value) {
                    if (((boolean) value.getObj())) {
                        duration = 0;
                        returnValue(value);
                        dismiss();
                    }
                }
                @Override
                public void onDismiss() {

                }
            });
        });
    }
}

