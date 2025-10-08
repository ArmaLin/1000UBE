package com.dyaco.spirit_commercial.maintenance_mode;

import static com.dyaco.spirit_commercial.App.UNIT_E;
import static com.dyaco.spirit_commercial.support.FormulaUtil.km2mi;
import static com.dyaco.spirit_commercial.support.FormulaUtil.saveDistance2KM;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.IMPERIAL;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.METRIC;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DISTANCE_LIMIT;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.NO_LIMIT;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.TIME_LIMIT;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.lifecycle.ViewModelProvider;

import com.dyaco.spirit_commercial.App;
import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.FragmentMaintenanceUsageRestrictionsBinding;
import com.dyaco.spirit_commercial.support.CommonUtils;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingBean;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingViewModel;
import com.dyaco.spirit_commercial.viewmodel.WorkoutViewModel;


public class MaintenanceUsageRestrictionsWindow extends BasePopupWindow<FragmentMaintenanceUsageRestrictionsBinding> {
    public WorkoutViewModel workoutViewModel;
    private DeviceSettingViewModel deviceSettingViewModel;

    public MaintenanceUsageRestrictionsWindow(Context context) {
        super(context, 200, 0, 0, GENERAL.FADE, false, false, false, false);


        deviceSettingViewModel = new ViewModelProvider((MainActivity) mContext).get(DeviceSettingViewModel.class);
        getBinding().setDeviceSettingViewModel(deviceSettingViewModel);

        workoutViewModel = new ViewModelProvider((MainActivity) mContext).get(WorkoutViewModel.class);

        DeviceSettingBean deviceSettingBean = App.getApp().getDeviceSettingBean();

        if (deviceSettingBean.getUsageRestrictionsType() == NO_LIMIT) {//NO LIMIT
            getBinding().scLimit.setPosition(2, false);
            getBinding().tvNoLimitText.setVisibility(View.VISIBLE);
            getBinding().groupNoLimit.setVisibility(View.INVISIBLE);

        } else if (deviceSettingBean.getUsageRestrictionsType() == DISTANCE_LIMIT) {//DISTANCE
            getBinding().scLimit.setPosition(0, false);
            getBinding().groupNoLimit.setVisibility(View.VISIBLE);
            getBinding().tvNoLimitText.setVisibility(View.INVISIBLE);
            getBinding().tvLine1Title.setText(UNIT_E == METRIC ? R.string.limit_km : R.string.limit_mi);
            int ddd = UNIT_E == METRIC ? deviceSettingBean.getUsageRestrictionsDistanceLimit() : (int) km2mi(deviceSettingBean.getUsageRestrictionsDistanceLimit());
            getBinding().etLimit.setText(String.valueOf(Math.round(ddd)));


        } else if (deviceSettingBean.getUsageRestrictionsType() == TIME_LIMIT) { //TIME
            getBinding().scLimit.setPosition(1, false);
            getBinding().groupNoLimit.setVisibility(View.VISIBLE);
            getBinding().tvNoLimitText.setVisibility(View.INVISIBLE);
            getBinding().tvLine1Title.setText(R.string.limit_sec);

//            getBinding().etLimit.setText(String.valueOf(deviceSettingBean.getUsageRestrictionsTimeLimit()));
            getBinding().etLimit.setText(String.valueOf(deviceSettingBean.getUsageRestrictionsTimeLimit() / 60 / 60));
        }

        getBinding().etCode.setText(deviceSettingBean.getLimitCode());


        //    initView();

        initEvent();


        getBinding().etCode.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

        getBinding().etCode.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        });


        getBinding().etLimit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

        getBinding().etLimit.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        });


        getBinding().etCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                checkSaveOn();
            }
        });

        getBinding().etLimit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                checkSaveOn();
            }
        });



        //顯示鍵盤
        setFocusable(true);
        setWindowLayoutType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);

    }


    private void initEvent() {

        getBinding().btnDone.setOnClickListener(view -> {
            hideSoftKeyboard(getBinding().btnDone);
            dismiss();
        });

        getBinding().btnApply.setOnClickListener(v -> {

            if (getBinding().scLimit.getPosition() == 2) {


                DeviceSettingBean deviceSettingBean = App.getApp().getDeviceSettingBean();
                //limit種類
                deviceSettingBean.setUsageRestrictionsType(NO_LIMIT);
                deviceSettingBean.setUsageRestrictionsDistanceLimit(0);
                deviceSettingBean.setUsageRestrictionsTimeLimit(0);
                deviceSettingBean.setLimitCode("");
                //當前距離時間歸0
                deviceSettingBean.setCurrentUsageRestrictionsDistance(0);
                deviceSettingBean.setCurrentUsageRestrictionsTime(0);
                App.getApp().setDeviceSettingBean(deviceSettingBean);

            } else {


                String etLimitStr = "";
                if (getBinding().etLimit.getText() != null) {
                    if (!TextUtils.isEmpty(getBinding().etLimit.getText().toString())) {
                        etLimitStr = getBinding().etLimit.getText().toString();
                    }
                }

                if ("".equals(etLimitStr) || "0".equals(etLimitStr)) {
                    Log.d("PPPPPPPP", "空的LIMIT: ");
                    return;
                }


                int limitData = CommonUtils.chkNum(etLimitStr) ? Integer.parseInt(etLimitStr) : 0;

                Log.d("PPPPPPPP", "initEvent: " + getBinding().etLimit.getText().toString() + "," + etLimitStr + "," + limitData);


                String etCodeStr = "";
                if (getBinding().etCode.getText() != null) {
                    if (!TextUtils.isEmpty(getBinding().etCode.getText().toString())) {
                        etCodeStr = getBinding().etCode.getText().toString();
                    }
                }

                if (etCodeStr.length() != 6) {
                    Log.d("PPPPPPPP", "空的密碼 ");
                    return;
                }


                DeviceSettingBean deviceSettingBean = App.getApp().getDeviceSettingBean();
                switch (getBinding().scLimit.getPosition()) {
                    case 0:
                        deviceSettingBean.setUsageRestrictionsType(DISTANCE_LIMIT);
                        //0:都不限制, 1:限制 distance, 2:限制 time

                        //  deviceSettingBean.setUsageRestrictionsDistanceLimit(limitData);
                        deviceSettingBean.setUsageRestrictionsDistanceLimit((int) Math.round(saveDistance2KM(limitData)));
                        deviceSettingBean.setUsageRestrictionsTimeLimit(0);
                        break;
                    case 1:
                        deviceSettingBean.setUsageRestrictionsType(TIME_LIMIT);
                        //0:都不限制, 1:限制 distance, 2:限制 time
                        deviceSettingBean.setUsageRestrictionsDistanceLimit(0);
                        deviceSettingBean.setUsageRestrictionsTimeLimit(limitData * 60L * 60); //小時轉秒
                        break;
                }

                //當前距離時間歸0
                deviceSettingBean.setCurrentUsageRestrictionsDistance(0);
                deviceSettingBean.setCurrentUsageRestrictionsTime(0);
                deviceSettingBean.setLimitCode(etCodeStr);
//
//            //存進MMKV
                App.getApp().setDeviceSettingBean(deviceSettingBean);
            }

            hideSoftKeyboard(getBinding().btnApply);
            dismiss();
        });


        getBinding().scLimit.setOnPositionChangedListener(position -> {

            switch (position) {
                case 0:
                    getBinding().groupNoLimit.setVisibility(View.VISIBLE);
                    getBinding().tvNoLimitText.setVisibility(View.INVISIBLE);
                    getBinding().tvLine1Title.setText(UNIT_E == IMPERIAL ? R.string.limit_mi : R.string.limit_km);
                    checkSaveOn();
                    break;
                case 1:
                    getBinding().groupNoLimit.setVisibility(View.VISIBLE);
                    getBinding().tvNoLimitText.setVisibility(View.INVISIBLE);
                    getBinding().tvLine1Title.setText(R.string.limit_sec);

                    checkSaveOn();
                    break;
                case 2:
                    getBinding().btnApply.setEnabled(true);
                    getBinding().groupNoLimit.setVisibility(View.INVISIBLE);
                    getBinding().tvNoLimitText.setVisibility(View.VISIBLE);
                    hideSoftKeyboard(getBinding().scLimit);

                    break;
            }

        });


    }


    public void hideSoftKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }


    private void checkSaveOn() {

        String etLimitStr = "";
        if (getBinding().etLimit.getText() != null) {
            if (!TextUtils.isEmpty(getBinding().etLimit.getText().toString())) {
                etLimitStr = getBinding().etLimit.getText().toString();
            }
        }

        if ("".equals(etLimitStr) || "0".equals(etLimitStr)) {
            Log.d("PPPPPPPP", "空的LIMIT: ");
            getBinding().btnApply.setEnabled(false);
            return;
        }


        int limitData = CommonUtils.chkNum(etLimitStr) ? Integer.parseInt(etLimitStr) : 0;

        Log.d("PPPPPPPP", "initEvent: " + getBinding().etLimit.getText().toString() + "," + etLimitStr + "," + limitData);


        String etCodeStr = "";
        if (getBinding().etCode.getText() != null) {
            if (!TextUtils.isEmpty(getBinding().etCode.getText().toString())) {
                etCodeStr = getBinding().etCode.getText().toString();
            }
        }

        getBinding().btnApply.setEnabled(etCodeStr.length() == 6);
    }
}