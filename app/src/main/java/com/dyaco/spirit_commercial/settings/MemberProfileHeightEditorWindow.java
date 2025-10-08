package com.dyaco.spirit_commercial.settings;

import static com.dyaco.spirit_commercial.App.UNIT_E;
import static com.dyaco.spirit_commercial.MainActivity.userProfileViewModel;
import static com.dyaco.spirit_commercial.model.webapi.CloudData.UPDATE_FIELD_HEIGHT;
import static com.dyaco.spirit_commercial.support.FormulaUtil.ft_in2TotalIn;
import static com.dyaco.spirit_commercial.support.FormulaUtil.in2cm;
import static com.dyaco.spirit_commercial.support.FormulaUtil.totalIn2In;
import static com.dyaco.spirit_commercial.support.FormulaUtil.totalIn2ft;
import static com.dyaco.spirit_commercial.support.utils.MyAnimationUtils.crossFade2;

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.WindowMemberProfileHeightEditorBinding;
import com.dyaco.spirit_commercial.model.webapi.CallWebApi;
import com.dyaco.spirit_commercial.support.FormulaUtil;
import com.dyaco.spirit_commercial.support.MsgEvent;
import com.dyaco.spirit_commercial.support.OnMultiClickListener;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.custom_view.wheelPicker.OptionsPickerView;
import com.dyaco.spirit_commercial.support.custom_view.wheelPicker.WheelView;
import com.dyaco.spirit_commercial.support.intdef.DeviceIntDef;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;
import com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS;

import java.util.ArrayList;
import java.util.List;


public class MemberProfileHeightEditorWindow extends BasePopupWindow<WindowMemberProfileHeightEditorBinding> {
    int tempHeightMetric;
    int tempHeightImperial;

    public MemberProfileHeightEditorWindow(Context context) {
        super(context, 500, 920, 0, GENERAL.TRANSLATION_Y, false, true, true, true);


        //  userSettingBean = ((MainActivity) context).userSettingBean;
//        //預設
//        userSettingBean.setHeightImperialTotalIn(80);
//        userSettingBean.setHeightMetricCm(FormulaUtil.in2cm(80));
//
//        Log.d("OOOOOOO", "MemberProfileHeightEditorWindow: " + userProfileEntity.getHeight_imperial());
//        Log.d("OOOOOOO", "MemberProfileHeightEditorWindow: " + userProfileEntity.getHeight_metric());


        tempHeightMetric = (int) userProfileViewModel.getHeight_metric();
        tempHeightImperial = (int) userProfileViewModel.getHeight_imperial();

        initView();
        initMetricHeightSelected();
        initImperialHeightSelected();
        initUnitSelected();
//        View.OnClickListener rgCheckListener = view -> initUnitSelected();
//        getBinding().btnImperial.setOnClickListener(rgCheckListener);
//        getBinding().btnMetric.setOnClickListener(rgCheckListener);
    }


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
        pickFtHeight.setTextSize(54, false);
        pickFtHeight.setCurved(true);
        pickFtHeight.setTextBoundaryMargin(0, true);
        pickFtHeight.setCurvedArcDirection(WheelView.CURVED_ARC_DIRECTION_CENTER);
        pickFtHeight.setCurvedArcDirectionFactor(1.0f);
        // pickFtHeight.setSelectedItemTextColor(Color.parseColor("#ffffff"));
        pickFtHeight.setSelectedItemTextColor(ContextCompat.getColor(mContext, R.color.white));
        pickFtHeight.setOnOptionsSelectedListener((opt1Pos, opt1Data, opt2Pos, opt2Data, opt3Pos, opt3Data) -> {
            if (opt1Data == null) return;

            setList2(Integer.parseInt(opt1Data) == OPT_SETTINGS.IMPERIAL_MAX_HEIGHT_FT);

            int totalIn = ft_in2TotalIn(Integer.parseInt(opt1Data), totalIn2In(tempHeightImperial));
//
            tempHeightImperial = totalIn;
            tempHeightMetric = in2cm(totalIn);

            if (tempHeightMetric > OPT_SETTINGS.HEIGHT_MU_MAX)
                tempHeightMetric = OPT_SETTINGS.HEIGHT_MU_MAX;

        });


        //IN
        //pickInHeight.setData(list2);
        pickInHeight.setVisibleItems(8);
        //   opv1.setDividerPaddingForWrap(10, true);
        pickInHeight.setNormalItemTextColor(ContextCompat.getColor(mContext, R.color.color5a7085));
        pickInHeight.setTextSize(54, false);
        pickInHeight.setCurved(true);
        pickInHeight.setTextBoundaryMargin(0, true);
        pickInHeight.setCurvedArcDirection(WheelView.CURVED_ARC_DIRECTION_CENTER);
        pickInHeight.setCurvedArcDirectionFactor(1.0f);
        pickInHeight.setSelectedItemTextColor(ContextCompat.getColor(mContext, R.color.white));
        pickInHeight.setOnOptionsSelectedListener((opt1Pos, opt1Data, opt2Pos, opt2Data, opt3Pos, opt3Data) -> {
            if (opt1Data == null) return;
            int totalIn = ft_in2TotalIn(totalIn2ft(tempHeightImperial), Integer.parseInt(opt1Data));
            tempHeightImperial = totalIn;
            tempHeightMetric = in2cm(totalIn);

            if (tempHeightMetric > OPT_SETTINGS.HEIGHT_MU_MAX)
                tempHeightMetric = OPT_SETTINGS.HEIGHT_MU_MAX;
        });
    }

    @SuppressWarnings("unchecked")
    private void initMetricHeightSelected() {
        pickCmHeight = getBinding().pickCmHeight;
        List<String> list1 = new ArrayList<>(1);
        for (int i = OPT_SETTINGS.HEIGHT_MU_MIN; i <= OPT_SETTINGS.HEIGHT_MU_MAX; i++) {
            list1.add(String.valueOf(i));
        }
        pickCmHeight.setData(list1);
        pickCmHeight.setVisibleItems(8);
        //   opv1.setDividerPaddingForWrap(10, true);
        pickCmHeight.setNormalItemTextColor(ContextCompat.getColor(mContext, R.color.color5a7085));
        pickCmHeight.setTextSize(54, false);
        pickCmHeight.setCurved(true);
        pickCmHeight.setTextBoundaryMargin(0, true);
        pickCmHeight.setCurvedArcDirection(WheelView.CURVED_ARC_DIRECTION_CENTER);
        pickCmHeight.setCurvedArcDirectionFactor(1.0f);
        pickCmHeight.setSelectedItemTextColor(ContextCompat.getColor(mContext, R.color.white));
        pickCmHeight.setCyclic(true);
        pickCmHeight.setOnOptionsSelectedListener((opt1Pos, opt1Data, opt2Pos, opt2Data, opt3Pos, opt3Data) -> {

            if (opt1Data == null) return;

            tempHeightMetric = Integer.parseInt(opt1Data);
            tempHeightImperial = FormulaUtil.cm2in(tempHeightMetric);
        });
    }


    /**
     * 改單位
     */
    private void initUnitSelected() {

        getBinding().scUnit.setOnPositionChangedListener(position -> {

            boolean isImperial = position == DeviceIntDef.IMPERIAL;
//
//            getBinding().groupM.setVisibility(!isImperial ? View.VISIBLE : View.GONE);
//            getBinding().groupI.setVisibility(isImperial ? View.VISIBLE : View.GONE);

            crossFade2(isImperial ? getBinding().groupI : getBinding().groupM, isImperial ? getBinding().groupM : getBinding().groupI, 500);

            if (isImperial) {
                int height = tempHeightImperial;
                int ft = totalIn2ft(height);
                int in = totalIn2In(height);
                pickFtHeight.setOpt1SelectedPosition(ft - OPT_SETTINGS.IMPERIAL_MIN_HEIGHT_FT);
                pickInHeight.setOpt1SelectedPosition(in - OPT_SETTINGS.IMPERIAL_MIN_HEIGHT_IN);
                Log.d("@@@@@", "onPositionChanged: " + ft + "," + in);
            } else {

                if (tempHeightMetric > OPT_SETTINGS.HEIGHT_MU_MAX)
                    tempHeightMetric = OPT_SETTINGS.HEIGHT_MU_MAX;

                pickCmHeight.setOpt1SelectedPosition(tempHeightMetric - OPT_SETTINGS.HEIGHT_MU_MIN);
            }


        });

        //初始單位
        getBinding().scUnit.setPosition(UNIT_E, false);

    }

    private void initView() {

        getBinding().btnClose.setOnClickListener(v -> dismiss());

        getBinding().btnSave.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                //Web Api 更新 HEIGHT
                new CallWebApi(mContext).updateUserInfo(UPDATE_FIELD_HEIGHT, String.valueOf(tempHeightMetric),
                        data -> {
                            userProfileViewModel.setHeight_metric(tempHeightMetric);
                            userProfileViewModel.setHeight_imperial(tempHeightImperial);
                            returnValue(new MsgEvent(true));
                            dismiss();
                        });
            }
        });
    }

//    private void saveData(UserProfileEntity userProfileEntity) {
//        SpiritDbManager.getInstance(getApp()).
//                updateUserProfile(userProfileEntity, new DatabaseCallback<UserProfileEntity>() {
//                    @Override
//                    public void onUpdated() {
//                        super.onUpdated();
//                        returnValue(new MsgEvent(userProfileEntity));
//                        dismiss();
//                    }
//
//                    @Override
//                    public void onError(String err) {
//                        super.onError(err);
//
//                        Toast.makeText(getApp(), "Failure:" + err, Toast.LENGTH_LONG).show();
//                    }
//                });
//    }
}
