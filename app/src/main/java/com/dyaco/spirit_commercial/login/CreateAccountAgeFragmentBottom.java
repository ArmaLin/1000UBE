package com.dyaco.spirit_commercial.login;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.FragmentCreateAccountAgeBinding;
import com.dyaco.spirit_commercial.support.base_component.BaseBindingDialogFragment;
import com.dyaco.spirit_commercial.support.custom_view.wheelPicker.OptionsPickerView;
import com.dyaco.spirit_commercial.support.custom_view.wheelPicker.WheelView;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;

import java.util.ArrayList;
import java.util.List;

public class CreateAccountAgeFragmentBottom extends BaseBindingDialogFragment<FragmentCreateAccountAgeBinding> {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDialogType(GENERAL.TRANSLATION_X);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView();
        initAgeSelected();
    }

    @SuppressWarnings("unchecked")
    private void initAgeSelected() {
        List<String> list1 = new ArrayList<>(1);
        for (int i = 10; i <= 99; i++) {
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
            if (opt1Data == null || opt3Data == null) {
                return;
            }

        });

        pickAge.setOpt1SelectedPosition(20, false);

//        Typeface typeface = mContext.getResources().getFont(R.font.inter_bold);
//        pickCmHeight.setTypeface(typeface);
    }

    private void initView() {
        getBinding().btnClose.setOnClickListener(v -> dismiss());

        getBinding().btnNext.setOnClickListener(v -> {
         //   dismiss();

            //   ((MainActivity) requireActivity()).navController.navigate(QrcodeLoginFragmentDirections.actionQrcodeLoginFragmentToCreateAccountAgeFragment());

            try {
                ((MainActivity) requireActivity()).navController.navigate(R.id.createAccountGenderFragment);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}