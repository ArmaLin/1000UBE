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
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.dyaco.spirit_commercial.App;
import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.FragmentMaintenanceUsageRestrictionsBinding;
import com.dyaco.spirit_commercial.support.CommonUtils;
import com.dyaco.spirit_commercial.support.base_component.BaseBindingDialogFragment;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingBean;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingViewModel;
import com.dyaco.spirit_commercial.viewmodel.WorkoutViewModel;

import java.util.Objects;


public class MaintenanceUsageRestrictionsFragment extends BaseBindingDialogFragment<FragmentMaintenanceUsageRestrictionsBinding> implements View.OnClickListener {
    public WorkoutViewModel workoutViewModel;
    //    private MainActivity m;
    private DeviceSettingViewModel deviceSettingViewModel;
    String passCode;
//    private AppCompatEditText editText;
//    private final StringBuffer stringBuffer = new StringBuffer();
//    private String cNum;
//    private  final int COST_MAX_LENGTH = 12;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDialogType(GENERAL.TRANSLATION_X);
//        m = ((MainActivity) requireActivity());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        deviceSettingViewModel = new ViewModelProvider(requireActivity()).get(DeviceSettingViewModel.class);
        getBinding().setDeviceSettingViewModel(deviceSettingViewModel);

        workoutViewModel = new ViewModelProvider(requireActivity()).get(WorkoutViewModel.class);



        DeviceSettingBean deviceSettingBean = App.getApp().getDeviceSettingBean();

       // Log.d("LIMIT_SHOW", "onViewCreated: "+ deviceSettingBean.getUsageRestrictionsType());

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

            getBinding().etLimit.setText(String.valueOf(deviceSettingBean.getUsageRestrictionsTimeLimit() / 60 / 60));
        }

        getBinding().etCode.setText(deviceSettingBean.getLimitCode());


        //    initView();

        initEvent();

        //已設定過密碼，要顯示輸入密碼
        if (deviceSettingBean.getUsageRestrictionsType() != NO_LIMIT) {
            getBinding().cAccessCode.setVisibility(View.VISIBLE);

            passCode = deviceSettingBean.getLimitCode();

            getBinding().btnApply.setEnabled(false);

//            getBinding().etLimitCode.setOnFocusChangeListener((v, hasFocus) -> {
//                Log.d("GGGEEEWWW", "onFocusChange: " + hasFocus);
//                if (hasFocus) {
//                    requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
//                }
//            });


            getBinding().etLimitCode.requestFocus();

            InputMethodManager imm = (InputMethodManager)getBinding().etLimitCode.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);




            getBinding().etLimitCode.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    getBinding().etLimitCode.setTextColor(ContextCompat.getColorStateList(requireActivity(), R.color.white));
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (editable.length() == 6) {
                        if (editable.toString().equals(passCode)) {
                            hideSoftKeyboard(getBinding().etLimitCode);
                            //   Toasty.success(mContext,"SUCCESS",Toasty.LENGTH_SHORT).show();

                            getBinding().cAccessCode.setVisibility(View.GONE);
                            getBinding().btnApply.setEnabled(true);

                        } else {
                            getBinding().etLimitCode.setTextColor(ContextCompat.getColorStateList(requireActivity(), R.color.colore24b44));
                            //   Toasty.error(mContext,"ERROR",Toasty.LENGTH_SHORT).show();
                        }
                    }
                }
            });



            getBinding().etLimitCode.setOnEditorActionListener((textView, actionId, keyEvent) -> {

                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (getBinding().etLimitCode.getText() != null && getBinding().etLimitCode.getText().length() == 4) {
                        if ("2222".equals(getBinding().etLimitCode.getText().toString())) { //萬用密碼
                            hideSoftKeyboard(getBinding().etLimitCode);
                            getBinding().cAccessCode.setVisibility(View.GONE);
                            getBinding().btnApply.setEnabled(true);
                        }
                    }
                  //  Toast.makeText(HelloFormStuff.this, edittext.getText(), Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            });

        } else {
            getBinding().cAccessCode.setVisibility(View.GONE);
        }




        getBinding().etCode.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

        getBinding().etCode.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        });


        getBinding().etLimit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

        getBinding().etLimit.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
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


    }

//    private void initView() {
//        editText = getBinding().etLimit;
//        editText.setInputType(InputType.TYPE_NULL);
//        cNum = "10";
//        editText.setText(cNum);
//        stringBuffer.append(cNum);
//    }


//    private boolean isShow = true;
//    private void showKeyboard() {
//        isShow = !isShow;
//        int height = isShow ? 393 : 1;
//
//        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) getBinding().cKeyboard.getLayoutParams();
//
//        ValueAnimator valueAnimator = ValueAnimator.ofInt(lp.height, height);
//        valueAnimator.setInterpolator(new LinearInterpolator());
//        valueAnimator.setDuration(200);
//        valueAnimator.addUpdateListener(animation -> {
//            if (getBinding() == null) return;
//            lp.height = (int) animation.getAnimatedValue();
//            getBinding().cKeyboard.setLayoutParams(lp);
//        });
//        valueAnimator.start();
//
//    }


    private void initEvent() {

//        getBinding().tvLine1Title.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showKeyboard();
//            }
//        });

//        getBinding().etLimit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//             //   showKeyboard();
//            }
//        });

        getBinding().btnDone.setOnClickListener(view -> {
            hideSoftKeyboard(requireView());
            dismiss();
        });

        getBinding().btnApply.setOnClickListener(v -> {

            Log.d("PPPPPPPP", "initEvent: " + getBinding().scLimit.getPosition());
            if (getBinding().scLimit.getPosition() == 2) { //NO LIMIT

//                deviceSettingViewModel.useDistanceLimitKM.set(0);
//                deviceSettingViewModel.useDistanceLimitMI.set(0);
//                deviceSettingViewModel.useTimeLimit.set(0);
//                deviceSettingViewModel.isUseTimeLimit.set(0);

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
//                        deviceSettingViewModel.isUseTimeLimit.set(1);
                        //0:都不限制, 1:限制 distance, 2:限制 time

                        deviceSettingBean.setUsageRestrictionsDistanceLimit((int) Math.round(saveDistance2KM(limitData)));
                        deviceSettingBean.setUsageRestrictionsTimeLimit(0);
//                        deviceSettingViewModel.useTimeLimit.set(0);
//                        deviceSettingViewModel.useDistanceLimitKM.set(limitData);
                        break;
                    case 1:
                        deviceSettingBean.setUsageRestrictionsType(TIME_LIMIT);
//                        deviceSettingViewModel.isUseTimeLimit.set(2);
                        //0:都不限制, 1:限制 distance, 2:限制 time
//                        deviceSettingViewModel.useTimeLimit.set(limitData);
//                        deviceSettingViewModel.useDistanceLimitKM.set(0);
                        deviceSettingBean.setUsageRestrictionsDistanceLimit(0);
//                        deviceSettingBean.setUsageRestrictionsTimeLimit(limitData);
                        deviceSettingBean.setUsageRestrictionsTimeLimit(limitData * 60L * 60); //小時轉秒
                        break;
                }

                //limit種類
//                deviceSettingBean.setUsageRestrictionsType(deviceSettingViewModel.isUseTimeLimit.get());
//                deviceSettingBean.setUseDistanceLimit(deviceSettingViewModel.useDistanceLimitKM.get());
//                deviceSettingBean.setUsageRestrictionsTimeLimit(deviceSettingViewModel.useTimeLimit.get());

                //當前距離時間歸0
                deviceSettingBean.setCurrentUsageRestrictionsDistance(0);
                deviceSettingBean.setCurrentUsageRestrictionsTime(0);
                deviceSettingBean.setLimitCode(etCodeStr);
                //   deviceSettingViewModel.limitCode.set(etCodeStr);
//
//            //存進MMKV
                App.getApp().setDeviceSettingBean(deviceSettingBean);


                Log.d("PPPPPPPP", "initEvent: " + deviceSettingBean.getUsageRestrictionsDistanceLimit() + "," + deviceSettingBean.getUsageRestrictionsTimeLimit());
//
//            //MMKV 存入 ViewModel
                //       new CommonUtils().mmkvDeviceSettingToViewModel(deviceSettingViewModel, deviceSettingBean);
            }

            hideSoftKeyboard(requireView());
            dismiss();
        });


        getBinding().scLimit.setOnPositionChangedListener(position -> {

            switch (position) {
                case 0:
                    getBinding().groupNoLimit.setVisibility(View.VISIBLE);
                    getBinding().tvNoLimitText.setVisibility(View.INVISIBLE);
                    getBinding().tvLine1Title.setText(UNIT_E == IMPERIAL ? R.string.limit_mi : R.string.limit_km);
                //    getBinding().tvLine1Title.setText(R.string.limit_km);
                    //      deviceSettingViewModel.isUseTimeLimit.set(1);

                    checkSaveOn();
                    break;
                case 1:
                    getBinding().groupNoLimit.setVisibility(View.VISIBLE);
                    getBinding().tvNoLimitText.setVisibility(View.INVISIBLE);
                    getBinding().tvLine1Title.setText(R.string.limit_sec);
                    //    deviceSettingViewModel.isUseTimeLimit.set(2);

                    checkSaveOn();
                    break;
                case 2:
                    getBinding().btnApply.setEnabled(true);
                    getBinding().groupNoLimit.setVisibility(View.INVISIBLE);
                    getBinding().tvNoLimitText.setVisibility(View.VISIBLE);
                    hideSoftKeyboard(requireView());
                    //     deviceSettingViewModel.isUseTimeLimit.set(0);
//                    isShow = true;
//                    showKeyboard();
                    break;
            }

        });


//        getBinding().keyboard0.setOnClickListener(this);
//        getBinding().keyboard1.setOnClickListener(this);
//        getBinding().keyboard2.setOnClickListener(this);
//        getBinding().keyboard3.setOnClickListener(this);
//        getBinding().keyboard4.setOnClickListener(this);
//        getBinding().keyboard5.setOnClickListener(this);
//        getBinding().keyboard6.setOnClickListener(this);
//        getBinding().keyboard7.setOnClickListener(this);
//        getBinding().keyboard8.setOnClickListener(this);
//        getBinding().keyboard9.setOnClickListener(this);
//        getBinding().keyboardBackspace.setOnClickListener(this);
//        getBinding().keyboardEnter.setOnClickListener(this);
//        getBinding().keyboardPeriod.setOnClickListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onClick(View v) {

//        final int id = v.getId();
//        if (id == R.id.keyboard_enter) {
//            String num = stringBuffer.toString();
//
////            if (NumberValidationUtils.isRealNumber(num) && Float.parseFloat(num) <= MAX_VALUE && Float.parseFloat(num) >= MIN_VALUE) {
////
////                if (".".equals(num.substring(num.length() - 1))) {
////                    num = num.substring(0, num.length() - 1);
////                }
////
////                returnValue(new MsgEvent(num));
////                dismiss();
////
////            } else {
////                CustomToast.showToast((MainActivity)mContext,"Please enter the correct number");
////                //  Toasty.warning(mContext, "Please enter the correct number", Toast.LENGTH_SHORT).show();
////            }
//        } else if (id == R.id.keyboard_backspace) {
//
//            if (stringBuffer.length() != 0) {
//
//                stringBuffer.delete(stringBuffer.length() - 1, stringBuffer.length());
//
//                editText.setText(stringBuffer);
//            }
//        } else if (id == R.id.keyboard_period) {
//          //  if (!isFloat) return;
//            if (stringBuffer.length() < COST_MAX_LENGTH) {
//
//                //金額中沒有輸入過小數點才可輸入","，金額中已有輸入過其他數字才可以輸入"."
//                if (stringBuffer.indexOf(".") == -1 && stringBuffer.length() != 0) {
//
//                    stringBuffer.append(".");
//                    editText.setText(stringBuffer);
//                }
//            }
//        } else if (id == R.id.btnClearAll) {
//            stringBuffer.delete(0,stringBuffer.length());
//            editText.setText("");
//        } else {
//            if (id == R.id.keyboard_comma || id == R.id.keyboard__ || id == R.id.keyboard_hyphen) return;
//            if (stringBuffer.length() > 0) {
//                //已經有兩位小數 就不可再輸入
//                if (getDecimalBits(Double.parseDouble(stringBuffer.toString() + "1")) == 3) {
//                    return;
//                }
//
//                if ("0".equals(stringBuffer.substring(0, 1))) {
//                    if (stringBuffer.length() >= 2) {
//                        //第一個字是0就不能再輸入且第二個字不是"." 就不可再輸入
//                        if (!".".equals(stringBuffer.substring(1, 2))) {
//
//                            return;
//                        }
//                    } else {
//
//                        return;
//                    }
//                }
//            }
//
//
////            //輸入的金額不超過限定長度
////            if (stringBuffer.length() < COST_MAX_LENGTH) {
////
////                String a = ((AppCompatButton) getBinding().getRoot().findViewById(id)).getText().toString();
////                stringBuffer.append(a);
////
////                editText.setText(stringBuffer);
////            }
//
//            //輸入的金額不超過限定長度
//            if (stringBuffer.length() < COST_MAX_LENGTH) {
//
//                String a = ((AppCompatButton) getBinding().getRoot().findViewById(id)).getText().toString();
//
//                stringBuffer.append(a);
//
//                if (stringBuffer.length() > 0 && Float.parseFloat(stringBuffer.toString()) <= 9999999) {
//                    editText.setText(stringBuffer);
//                } else {
//                    stringBuffer.delete(0, stringBuffer.length());
//                    editText.setText("");
//                }
//
//            }
//        }
    }

//    private int getDecimalBits(double target) {
//        if (0.0 == target % 1) {
//            return 0;
//        } else {
//            String sTarget = String.valueOf(target);
//            return sTarget.length() - sTarget.indexOf('.') - 1;
//        }
//    }


    public void hideSoftKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) Objects.requireNonNull(getActivity()).getSystemService(Context.INPUT_METHOD_SERVICE);
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