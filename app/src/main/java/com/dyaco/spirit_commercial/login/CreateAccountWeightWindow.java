package com.dyaco.spirit_commercial.login;

import static com.dyaco.spirit_commercial.App.UNIT_E;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.WEIGHT_IU_MAX;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.WEIGHT_IU_MIN;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.WEIGHT_MU_MAX;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.WEIGHT_MU_MIN;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.FragmentCreateAccountWeightBinding;
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

public class CreateAccountWeightWindow extends BasePopupWindow<FragmentCreateAccountWeightBinding> {
    double weight_imperial = OPT_SETTINGS.WEIGHT_IU_DEF;
    double weight_metric = OPT_SETTINGS.WEIGHT_MU_DEF;

    public CreateAccountWeightWindow(Context context) {
        super(context, 0, 920, 0, GENERAL.TRANSLATION_Y, false, false, false, false);
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);

        initView();
        initWeightSelected();

        initUnit();
    }

    private void setList1() {
        List<String> list1 = new ArrayList<>(1);

        for (int i = (isImperial ? WEIGHT_IU_MIN : WEIGHT_MU_MIN); i <= (isImperial ? WEIGHT_IU_MAX : WEIGHT_MU_MAX); i++) {
            list1.add(String.valueOf(i));
        }
        pickWeight.setData(list1);
    }

    private OptionsPickerView<String> pickWeight;

    @SuppressWarnings("unchecked")
    private void initWeightSelected() {

        pickWeight = getBinding().pickWeight;

        setList1();
        pickWeight.setVisibleItems(8);
        //   opv1.setDividerPaddingForWrap(10, true);
        pickWeight.setNormalItemTextColor(ContextCompat.getColor(mContext, R.color.color5a7085));
        pickWeight.setTextSize(54, false);
        pickWeight.setCurved(true);
        //  pickAge.setTextBoundaryMargin(10, true);
        pickWeight.setCurvedArcDirection(WheelView.CURVED_ARC_DIRECTION_CENTER);
        pickWeight.setCurvedArcDirectionFactor(1.0f);
        pickWeight.setSelectedItemTextColor(ContextCompat.getColor(mContext, R.color.white));
        pickWeight.setCyclic(true);
//        pickAge.setDividerHeight(2, true);
//        pickAge.setDividerPadding(80);
//        pickAge.setDividerType(WheelView.DIVIDER_TYPE_FILL);
//        pickAge.setDividerColorRes(R.color.colorCd5bff);
//        pickAge.setDividerPaddingForWrap(70, true);
//        pickAge.setShowDivider(true);
        //  pickAge.setLineSpacing(14,true);
        pickWeight.setOnOptionsSelectedListener((opt1Pos, opt1Data, opt2Pos, opt2Data, opt3Pos, opt3Data) -> {
            if (opt1Data == null) return;
            if (!isEvent) {
                if (isImperial) {
                    weight_imperial = Double.parseDouble(opt1Data);
                    weight_metric = FormulaUtil.lb2kgPure(Double.parseDouble(opt1Data));
                } else {
                    weight_imperial = FormulaUtil.kg2lbPure(Double.parseDouble(opt1Data));
                    weight_metric = Double.parseDouble(opt1Data);
                }

                Log.d("WWWWWWW", "initWeightSelected: " + weight_imperial +","+ weight_metric);
            }
            isEvent = false;

        });

        // pickWeight.setOpt1SelectedPosition(20, false);

    }

    boolean isImperial = UNIT_E == DeviceIntDef.IMPERIAL;

    boolean isEvent;
    private void initUnit() {
        getBinding().scUnit.setOnPositionChangedListener(position -> {
            isImperial = position == DeviceIntDef.IMPERIAL;

            isEvent = true;

            getBinding().unitWeight.setText(isImperial ? mContext.getString(R.string.lb) : mContext.getString(R.string.kg));

            setList1();

            pickWeight.setOpt1SelectedPosition((int) (isImperial ? (weight_imperial - WEIGHT_IU_MIN) : (weight_metric - WEIGHT_MU_MIN)));


        });

        getBinding().scUnit.setPosition(UNIT_E, false);
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
            CreateAccountHeightWindow createAccountHeightWindow = new CreateAccountHeightWindow(mContext);
            createAccountHeightWindow.showAtLocation(((MainActivity) mContext).getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);
            createAccountHeightWindow.setOnCustomDismissListener(new OnCustomDismissListener() {
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