package com.dyaco.spirit_commercial.settings;

import static com.dyaco.spirit_commercial.App.UNIT_E;
import static com.dyaco.spirit_commercial.MainActivity.userProfileViewModel;
import static com.dyaco.spirit_commercial.model.webapi.CloudData.UPDATE_FIELD_WEIGHT;

import android.content.Context;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.dyaco.spirit_commercial.databinding.WindowMemberProfileWeightEditorBinding;
import com.dyaco.spirit_commercial.model.webapi.CallWebApi;
import com.dyaco.spirit_commercial.support.CommonUtils;
import com.dyaco.spirit_commercial.support.FormulaUtil;
import com.dyaco.spirit_commercial.support.MsgEvent;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.intdef.DeviceIntDef;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;

import java.util.Objects;

public class MemberProfileWeightEditorWindow extends BasePopupWindow<WindowMemberProfileWeightEditorBinding> {


    public MemberProfileWeightEditorWindow(Context context) {
        super(context, 500, 848, 0, GENERAL.TRANSLATION_Y, false, true, false, true);
        initView();

        //顯示鍵盤
        //  setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setFocusable(true);
        setWindowLayoutType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
//        update();
        //   setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        //   setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        //  setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED);


        getBinding().etWeight.setTransformationMethod(HideReturnsTransformationMethod.getInstance());


        getBinding().etWeight.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.d("TAG", "onFocusChange: " + hasFocus);
                if (hasFocus) {
                    setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

        getBinding().etWeight.requestFocus();


//                InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.showSoftInput(getBinding().etWeight, InputMethodManager.SHOW_IMPLICIT);
    }

    private void initView() {

        double weight = UNIT_E == DeviceIntDef.IMPERIAL ? userProfileViewModel.getWeight_imperial() : userProfileViewModel.getWeight_metric();
        getBinding().etWeight.setText(String.valueOf((int)weight));
        getBinding().btnClose.setOnClickListener(v -> dismiss());


        getBinding().btnSave.setOnClickListener(v -> {

            if (TextUtils.isEmpty(Objects.requireNonNull(getBinding().etWeight.getText()).toString()))
                return;
            if (!CommonUtils.chkNum(getBinding().etWeight.getText().toString())) return;

            double uWeight = Double.parseDouble(getBinding().etWeight.getText().toString());

            Log.d("@@@@@@@@@", "initView: " + FormulaUtil.lb2kg2(uWeight) +","+ uWeight);
            //Web Api 更新 WEIGHT
            new CallWebApi(mContext).updateUserInfo(UPDATE_FIELD_WEIGHT,
                    String.valueOf(UNIT_E == DeviceIntDef.METRIC ? uWeight : FormulaUtil.lb2kg2(uWeight)),
                    data -> {
                        userProfileViewModel.setWeight_imperial(UNIT_E == DeviceIntDef.IMPERIAL ? uWeight : FormulaUtil.kg2lbPure(uWeight));
                        userProfileViewModel.setWeight_metric(UNIT_E == DeviceIntDef.METRIC ? uWeight : FormulaUtil.lb2kgPure(uWeight));
                        returnValue(new MsgEvent(Integer.parseInt(Objects.requireNonNull(getBinding().etWeight.getText()).toString())));
                        dismiss();
                    });

            //   SpiritDbManager.getInstance(getApp()).
//                    updateUserProfile(userProfileEntity, new DatabaseCallback<UserProfileEntity>() {
//                        @Override
//                        public void onUpdated() {
//                            super.onUpdated();
//                            returnValue(new MsgEvent(Integer.parseInt(Objects.requireNonNull(getBinding().etWeight.getText()).toString())));
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
}
