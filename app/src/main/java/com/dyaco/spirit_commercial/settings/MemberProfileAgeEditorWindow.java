package com.dyaco.spirit_commercial.settings;

import static com.dyaco.spirit_commercial.MainActivity.userProfileViewModel;
import static com.dyaco.spirit_commercial.model.webapi.CloudData.UPDATE_FIELD_AGE;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.EVENT_SET_AGE;

import android.content.Context;
import android.graphics.Color;

import com.dyaco.spirit_commercial.databinding.WindowMemberProfileAgeEditorBinding;
import com.dyaco.spirit_commercial.model.webapi.CallWebApi;
import com.dyaco.spirit_commercial.support.MsgEvent;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.custom_view.wheelPicker.OptionsPickerView;
import com.dyaco.spirit_commercial.support.custom_view.wheelPicker.WheelView;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;
import com.jeremyliao.liveeventbus.LiveEventBus;

import java.util.ArrayList;
import java.util.List;

public class MemberProfileAgeEditorWindow extends BasePopupWindow<WindowMemberProfileAgeEditorBinding> {


    public MemberProfileAgeEditorWindow(Context context) {
        super(context, 500, 920, 0, GENERAL.TRANSLATION_Y, false, true, true, true);
        initView();


        initAgeSelected();
    }

    int selAge;

    @SuppressWarnings("unchecked")
    private void initAgeSelected() {
        //  int age = CommonUtils.getAgeFormBirth(getInstance().getUserProfile().getBirthday());
        int age = userProfileViewModel.getUserAge();

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
            if (opt1Data == null) {
                return;
            }
            selAge = Integer.parseInt(opt1Data);
        });

        pickAge.setOpt1SelectedPosition(age - 10, false);

//        Typeface typeface = mContext.getResources().getFont(R.font.inter_bold);
//        pickCmHeight.setTypeface(typeface);
    }


    // private void webApiUpdateUserInfo() {

//        ((MainActivity)mContext).showLoading(true);
//        Map<String, Object> map = new HashMap<>();
//        map.put(UPDATE_FIELD, UPDATE_FIELD_AGE);
//        map.put(UPDATE_FIELD_AGE, selAge);
//
//        BaseApi.request(BaseApi.createApi(IServiceApi.class).apiUpdateMyUserInfoFromMachine(getJson(map)),
//                new BaseApi.IResponseListener<UpdateMyUserInfoFromMachineBean>() {
//                    @Override
//                    public void onSuccess(UpdateMyUserInfoFromMachineBean data) {
//
//                        Log.d("WEB_API", "apiUpdateMyUserInfoFromMachine: " +data.toString());
//                        if (data.getSuccess()) {
//
//                            userProfileViewModel.setUserAge(selAge);
//                            LiveEventBus.get(EVENT_SET_AGE).post(true);
//                            returnValue(new MsgEvent(selAge));
//                            dismiss();
//
//                        } else {
//                            if (data.getErrorMessage() != null)
//                                Toasty.warning(mContext, data.getErrorMessage(), Toasty.LENGTH_LONG).show();
//                        }
//
//                        ((MainActivity)mContext).showLoading(false);
//                    }
//
//                    @Override
//                    public void onFail() {
//                        Log.d("WEB_API", "失敗");
//                        ((MainActivity)mContext).showLoading(false);
//                    }
//                });
    //  }

    private void initView() {
        getBinding().btnClose.setOnClickListener(v -> dismiss());

        getBinding().btnSave.setOnClickListener(v -> {

            //Web Api 更新 AGE
            new CallWebApi(mContext).updateUserInfo(UPDATE_FIELD_AGE, String.valueOf(selAge),
                    data -> {
                        userProfileViewModel.setUserAge(selAge);
                        LiveEventBus.get(EVENT_SET_AGE).post(true);
                        returnValue(new MsgEvent(selAge));
                        dismiss();
                    });

//            SpiritDbManager.getInstance(getApp()).
//                    updateUserProfile(userProfileEntity, new DatabaseCallback<UserProfileEntity>() {
//                        @Override
//                        public void onUpdated() {
//                            super.onUpdated();
//                            LiveEventBus.get(EVENT_SET_AGE).post(true);
//                            returnValue(new MsgEvent(selAge));
//                            dismiss();
//                        }
//
//                        @Override
//                        public void onError(String err) {
//                            super.onError(err);
//
//                            Toast.makeText(getApp(), "Failure:" + err, Toast.LENGTH_LONG).show();
//                        }
//                    });
        });
    }

    @Override
    public void dismiss() {
        super.dismiss();
        // SpiritDbManager.getInstance(getApp()).clear();
    }
}
