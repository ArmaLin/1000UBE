package com.dyaco.spirit_commercial.login;

import static com.dyaco.spirit_commercial.App.UNIT_E;
import static com.dyaco.spirit_commercial.support.FormulaUtil.ft_in2TotalIn;
import static com.dyaco.spirit_commercial.support.FormulaUtil.in2cm;
import static com.dyaco.spirit_commercial.support.FormulaUtil.totalIn2In;
import static com.dyaco.spirit_commercial.support.FormulaUtil.totalIn2ft;

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.FragmentCreateAccountHeightBinding;
import com.dyaco.spirit_commercial.support.FormulaUtil;
import com.dyaco.spirit_commercial.support.MsgEvent;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.custom_view.wheelPicker.OptionsPickerView;
import com.dyaco.spirit_commercial.support.custom_view.wheelPicker.WheelView;
import com.dyaco.spirit_commercial.support.intdef.DeviceIntDef;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;
import com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS;
import com.dyaco.spirit_commercial.support.utils.CheckDoubleClick;

import java.util.ArrayList;
import java.util.List;

public class CreateAccountHeightWindow extends BasePopupWindow<FragmentCreateAccountHeightBinding> {

    public CreateAccountHeightWindow(Context context) {
        super(context, 0, 920, 0, GENERAL.TRANSLATION_Y,false,false,false,false);
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);

        initView();
        initMetricHeightSelected();
        initImperialHeightSelected();
        initUnitSelected();
    }
    int height_imperial = OPT_SETTINGS.HEIGHT_IU_DEF;
    int height_metric = OPT_SETTINGS.HEIGHT_MU_DEF;
    OptionsPickerView<String> pickCmHeight;
    OptionsPickerView<String> pickFtHeight;
    OptionsPickerView<String> pickInHeight;
    List<String> list2;

    private void setList2(boolean b) {
        int x;
        x = b ? 4 : OPT_SETTINGS.IMPERIAL_MAX_HEIGHT_IN;
        list2 = new ArrayList<>(1);
        for (int i = OPT_SETTINGS.IMPERIAL_MIN_HEIGHT_IN; i <= x; i++) {
            list2.add(String.valueOf(i));
        }

        pickInHeight.setData(list2);
    }
    private void initUnitSelected() {
        getBinding().scUnit.setOnPositionChangedListener(position -> {
            boolean isImperial = position == DeviceIntDef.IMPERIAL;
            getBinding().unitCM.setVisibility(!isImperial ? View.VISIBLE : View.GONE);
            getBinding().pickCmHeight.setVisibility(!isImperial ? View.VISIBLE : View.GONE);
            getBinding().unitFt.setVisibility(isImperial ? View.VISIBLE : View.GONE);
            getBinding().pickFtHeight.setVisibility(isImperial ? View.VISIBLE : View.GONE);
            getBinding().unitIn.setVisibility(isImperial ? View.VISIBLE : View.GONE);
            getBinding().pickInHeight.setVisibility(isImperial ? View.VISIBLE : View.GONE);
            if (isImperial) {
                int height = height_imperial;
                int ft = totalIn2ft(height);
                int in = totalIn2In(height);
                pickFtHeight.setOpt1SelectedPosition(ft - OPT_SETTINGS.IMPERIAL_MIN_HEIGHT_FT);
                pickInHeight.setOpt1SelectedPosition(in - OPT_SETTINGS.IMPERIAL_MIN_HEIGHT_IN);
                Log.d("@@@@@", "onPositionChanged: " + ft + "," + in);
            } else {
                if (height_metric > OPT_SETTINGS.HEIGHT_MU_MAX) height_metric = OPT_SETTINGS.HEIGHT_MU_MAX;
                pickCmHeight.setOpt1SelectedPosition(height_metric - OPT_SETTINGS.HEIGHT_MU_MIN);
            }
        });

        getBinding().scUnit.setPosition(UNIT_E, false);
    }

    @SuppressWarnings("unchecked")
    private void initImperialHeightSelected() {
        //FT
        pickFtHeight = getBinding().pickFtHeight;
        pickInHeight = getBinding().pickInHeight;
        List<String> list1 = new ArrayList<>(1);
        for (int i = OPT_SETTINGS.IMPERIAL_MIN_HEIGHT_FT; i <= OPT_SETTINGS.IMPERIAL_MAX_HEIGHT_FT; i++) {
            list1.add(String.valueOf(i));
        }
        setList2(false);
        pickFtHeight.setData(list1);
        pickFtHeight.setVisibleItems(8);
        //   opv1.setDividerPaddingForWrap(10, true);
        pickFtHeight.setNormalItemTextColor(ContextCompat.getColor(mContext, R.color.color5a7085));
        pickFtHeight.setTextSize(54,false);
        pickFtHeight.setCurved(true);
        pickFtHeight.setTextBoundaryMargin(0,true);
        pickFtHeight.setCurvedArcDirection(WheelView.CURVED_ARC_DIRECTION_CENTER);
        pickFtHeight.setCurvedArcDirectionFactor(1.0f);
        pickFtHeight.setSelectedItemTextColor(ContextCompat.getColor(mContext, R.color.white));
        pickFtHeight.setOnOptionsSelectedListener((opt1Pos, opt1Data, opt2Pos, opt2Data, opt3Pos, opt3Data) -> {
            if (opt1Data == null) return;
            setList2(Integer.parseInt(opt1Data) == OPT_SETTINGS.IMPERIAL_MAX_HEIGHT_FT);
            int totalIn = ft_in2TotalIn(Integer.parseInt(opt1Data), totalIn2In(height_imperial));
            height_imperial = totalIn ;
            height_metric = in2cm(totalIn);
            if (height_metric > OPT_SETTINGS.HEIGHT_MU_MAX) height_metric = OPT_SETTINGS.HEIGHT_MU_MAX;
        });

        pickInHeight.setVisibleItems(8);
        //   opv1.setDividerPaddingForWrap(10, true);
        pickInHeight.setNormalItemTextColor(ContextCompat.getColor(mContext, R.color.color5a7085));
        pickInHeight.setTextSize(54,false);
        pickInHeight.setCurved(true);
        pickInHeight.setTextBoundaryMargin(0,true);
        pickInHeight.setCurvedArcDirection(WheelView.CURVED_ARC_DIRECTION_CENTER);
        pickInHeight.setCurvedArcDirectionFactor(1.0f);
        pickInHeight.setSelectedItemTextColor(ContextCompat.getColor(mContext, R.color.white));
        pickInHeight.setOnOptionsSelectedListener((opt1Pos, opt1Data, opt2Pos, opt2Data, opt3Pos, opt3Data) -> {
            if (opt1Data == null) return;
            int totalIn = ft_in2TotalIn(totalIn2ft(height_imperial), Integer.parseInt(opt1Data));
            height_imperial = totalIn;
            height_metric = in2cm(totalIn);
            if (height_metric > OPT_SETTINGS.HEIGHT_MU_MAX) height_metric = OPT_SETTINGS.HEIGHT_MU_MAX;
        });
    }

    @SuppressWarnings("unchecked")
    private void initMetricHeightSelected() {
        List<String> list1 = new ArrayList<>(1);
        for (int i = OPT_SETTINGS.HEIGHT_MU_MIN; i <= OPT_SETTINGS.HEIGHT_MU_MAX; i++) {
            list1.add(String.valueOf(i));
        }
        pickCmHeight = getBinding().pickCmHeight;
        pickCmHeight.setData(list1);
        pickCmHeight.setVisibleItems(8);
        //   opv1.setDividerPaddingForWrap(10, true);
        pickCmHeight.setNormalItemTextColor(ContextCompat.getColor(mContext, R.color.color5a7085));
        pickCmHeight.setTextSize(54,false);
        pickCmHeight.setCurved(true);
        pickCmHeight.setTextBoundaryMargin(0,true);
        pickCmHeight.setCurvedArcDirection(WheelView.CURVED_ARC_DIRECTION_CENTER);
        pickCmHeight.setCurvedArcDirectionFactor(1.0f);
        pickCmHeight.setSelectedItemTextColor(ContextCompat.getColor(mContext, R.color.white));
        pickCmHeight.setCyclic(true);
        pickCmHeight.setOnOptionsSelectedListener((opt1Pos, opt1Data, opt2Pos, opt2Data, opt3Pos, opt3Data) -> {
            if (opt1Data == null) return;
            height_metric = Integer.parseInt(opt1Data);
            height_imperial = FormulaUtil.cm2in(height_metric);
        });

     //   pickCmHeight.setOpt1SelectedPosition(70,false);

//        Typeface typeface = mContext.getResources().getFont(R.font.inter_bold);
//        pickCmHeight.setTypeface(typeface);
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
            duration = 300;
            returnValue(new MsgEvent(true));
            dismiss();
        });
    }
}

