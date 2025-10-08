package com.dyaco.spirit_commercial.login;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.FragmentCreateAccountHeightBinding;
import com.dyaco.spirit_commercial.support.base_component.BaseBindingDialogFragment;
import com.dyaco.spirit_commercial.support.custom_view.wheelPicker.OptionsPickerView;
import com.dyaco.spirit_commercial.support.custom_view.wheelPicker.WheelView;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;

import java.util.ArrayList;
import java.util.List;

public class CreateAccountHeightFragmentBottom extends BaseBindingDialogFragment<FragmentCreateAccountHeightBinding> {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setDialogType(GENERAL.TRANSLATION_X);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        initView();


        initMetricHeightSelected();
        initImperialHeightSelected();
        initUnitSelected();
    }

    private void initUnitSelected() {
        getBinding().scUnit.setOnPositionChangedListener(position -> {

            boolean isImperial = position == 0;

            getBinding().unitCM.setVisibility(!isImperial ? View.VISIBLE : View.GONE);
            getBinding().pickCmHeight.setVisibility(!isImperial ? View.VISIBLE : View.GONE);

            getBinding().unitFt.setVisibility(isImperial ? View.VISIBLE : View.GONE);
            getBinding().pickFtHeight.setVisibility(isImperial ? View.VISIBLE : View.GONE);
            getBinding().unitIn.setVisibility(isImperial ? View.VISIBLE : View.GONE);
            getBinding().pickInHeight.setVisibility(isImperial ? View.VISIBLE : View.GONE);

        });
    }

    @SuppressWarnings("unchecked")
    private void initImperialHeightSelected() {
        List<String> list1 = new ArrayList<>(1);
        for (int i = 1; i <= 9; i++) {
            list1.add(String.valueOf(i));
        }

        List<String> list2 = new ArrayList<>(1);
        for (int i = 1; i <= 11; i++) {
            list2.add(String.valueOf(i));
        }

        OptionsPickerView<String> pickFtHeight = getBinding().pickFtHeight;
        OptionsPickerView<String>  pickInHeight = getBinding().pickInHeight;
        pickFtHeight.setData(list1);
        pickFtHeight.setVisibleItems(8);
        //   opv1.setDividerPaddingForWrap(10, true);
        pickFtHeight.setNormalItemTextColor(ContextCompat.getColor(requireActivity().getApplicationContext(), R.color.color5a7085));
        pickFtHeight.setTextSize(54,false);
        pickFtHeight.setCurved(true);
        pickFtHeight.setTextBoundaryMargin(0,true);
        pickFtHeight.setCurvedArcDirection(WheelView.CURVED_ARC_DIRECTION_CENTER);
        pickFtHeight.setCurvedArcDirectionFactor(1.0f);
        pickFtHeight.setSelectedItemTextColor(ContextCompat.getColor(requireActivity().getApplicationContext(), R.color.white));
//        opv1.setLinkageData(linkageList1, linkageList2);
        pickFtHeight.setOnOptionsSelectedListener((opt1Pos, opt1Data, opt2Pos, opt2Data, opt3Pos, opt3Data) -> {


        });

        pickInHeight.setData(list2);
        pickInHeight.setVisibleItems(8);
        //   opv1.setDividerPaddingForWrap(10, true);
        pickInHeight.setNormalItemTextColor(Color.parseColor("#5a7085"));
        pickInHeight.setTextSize(54,false);
        pickInHeight.setCurved(true);
        pickInHeight.setTextBoundaryMargin(0,true);
        pickInHeight.setCurvedArcDirection(WheelView.CURVED_ARC_DIRECTION_CENTER);
        pickInHeight.setCurvedArcDirectionFactor(1.0f);
        pickInHeight.setSelectedItemTextColor(ContextCompat.getColor(requireActivity().getApplicationContext(), R.color.color5a7085));
        pickInHeight.setOnOptionsSelectedListener((opt1Pos, opt1Data, opt2Pos, opt2Data, opt3Pos, opt3Data) -> {
            if (opt1Data == null || opt3Data == null) {
                return;
            }

        });
    }

    @SuppressWarnings("unchecked")
    private void initMetricHeightSelected() {
        List<String> list1 = new ArrayList<>(1);
        for (int i = 100; i <= 220; i++) {
            list1.add(String.valueOf(i));
        }
        OptionsPickerView<String>  pickCmHeight = getBinding().pickCmHeight;
        pickCmHeight.setData(list1);
        pickCmHeight.setVisibleItems(8);
        //   opv1.setDividerPaddingForWrap(10, true);
        pickCmHeight.setNormalItemTextColor(ContextCompat.getColor(requireActivity().getApplicationContext(), R.color.color5a7085));
        pickCmHeight.setTextSize(54,false);
        pickCmHeight.setCurved(true);
        pickCmHeight.setTextBoundaryMargin(0,true);
        pickCmHeight.setCurvedArcDirection(WheelView.CURVED_ARC_DIRECTION_CENTER);
        pickCmHeight.setCurvedArcDirectionFactor(1.0f);
        pickCmHeight.setSelectedItemTextColor(ContextCompat.getColor(requireActivity().getApplicationContext(), R.color.white));
        pickCmHeight.setCyclic(true);
//        opv1.setLinkageData(linkageList1, linkageList2);
        pickCmHeight.setOnOptionsSelectedListener((opt1Pos, opt1Data, opt2Pos, opt2Data, opt3Pos, opt3Data) -> {
            if (opt1Data == null || opt3Data == null) {
                return;
            }

        });

        pickCmHeight.setOpt1SelectedPosition(70,false);

//        Typeface typeface = mContext.getResources().getFont(R.font.inter_bold);
//        pickCmHeight.setTypeface(typeface);
    }


    private void initView() {
        getBinding().btnClose.setOnClickListener(v -> dismiss());

        getBinding().btnBack.setOnClickListener(v -> {
         //   dismiss();
            ((MainActivity) requireActivity()).navController.navigate(R.id.createAccountWeightFragment);
        });

        getBinding().btnNext.setOnClickListener(v -> {
            dismiss();

            //   ((MainActivity) requireActivity()).navController.navigate(QrcodeLoginFragmentDirections.actionQrcodeLoginFragmentToCreateAccountAgeFragment());
           ((MainActivity) requireActivity()).navController.navigate(R.id.createAccountAgeFragment);

        });
    }
}