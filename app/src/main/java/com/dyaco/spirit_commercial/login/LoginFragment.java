package com.dyaco.spirit_commercial.login;

import static com.dyaco.spirit_commercial.App.SETTING_SHOW;
import static com.dyaco.spirit_commercial.App.getApp;
import static com.dyaco.spirit_commercial.App.getDeviceGEM;
import static com.dyaco.spirit_commercial.App.getDeviceSpiritC;
import static com.dyaco.spirit_commercial.MainActivity.isEmulator;
import static com.dyaco.spirit_commercial.MainActivity.isHomeScreen;
import static com.dyaco.spirit_commercial.MainActivity.isTreadmill;
import static com.dyaco.spirit_commercial.MainActivity.isUs;
import static com.dyaco.spirit_commercial.MainActivity.splashImagePathJPG;
import static com.dyaco.spirit_commercial.MainActivity.userProfileViewModel;
import static com.dyaco.spirit_commercial.egym.EgymUtil.RFID_CODE;
import static com.dyaco.spirit_commercial.support.CommonUtils.checkAppInstalled;
import static com.dyaco.spirit_commercial.support.CommonUtils.hideSoftKeyboard;
import static com.dyaco.spirit_commercial.support.CommonUtils.isNetworkAvailable;
import static com.dyaco.spirit_commercial.support.CommonUtils.isScreenOn;
import static com.dyaco.spirit_commercial.support.CommonUtils.reverseUidHex;
import static com.dyaco.spirit_commercial.support.CommonUtils.showException;
import static com.dyaco.spirit_commercial.support.CommonUtils.wakeUpScreen;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.CONSOLE_SYSTEM_EGYM;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.CONSOLE_SYSTEM_SPIRIT;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.EVENT_NFC_ENABLED;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.EVENT_NFC_READ;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.FTMS_START_OR_RESUME;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.GYM3_ON_EVENT;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.LOG_IN_EVENT;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.WIFI_WORK;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DISTANCE_LIMIT;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_EMS_IDLE_STANDBY;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_IDLE_STANDBY;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.FEMALE;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.NO_LIMIT;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.USER_TYPE_GUEST;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.AGE_DEF;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.HEIGHT_IU_DEF;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.HEIGHT_MU_DEF;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.WEIGHT_IU_DEF;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.WEIGHT_MU_DEF;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.corestar.libs.device.DeviceGEM;
import com.corestar.libs.ota.LwrMcuUpdateManager;
import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.NavigationLoginDirections;
import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.FragmentLoginBinding;
import com.dyaco.spirit_commercial.egym.EgymUtil;
import com.dyaco.spirit_commercial.model.webapi.EgymWebListener;
import com.dyaco.spirit_commercial.support.CoTimer;
import com.dyaco.spirit_commercial.support.RxTimer;
import com.dyaco.spirit_commercial.support.base_component.BaseBindingFragment;
import com.dyaco.spirit_commercial.support.custom_view.CustomToast;
import com.dyaco.spirit_commercial.support.intdef.AppStatusIntDef;
import com.dyaco.spirit_commercial.support.interaction.TenTapClick;
import com.dyaco.spirit_commercial.support.utils.CheckDoubleClick;
import com.dyaco.spirit_commercial.viewmodel.AppStatusViewModel;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingBean;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingViewModel;
import com.dyaco.spirit_commercial.viewmodel.EgymDataViewModel;
import com.google.android.material.button.MaterialButton;
import com.jeremyliao.liveeventbus.LiveEventBus;

import java.io.File;

import es.dmoral.toasty.Toasty;
import timber.log.Timber;

public class LoginFragment extends BaseBindingFragment<FragmentLoginBinding> {
    private AnimatorSet expandAnimatorSet;
    private AnimatorSet collapseAnimatorSet;
    private ValueAnimator expandHeightAnimator;
    private ValueAnimator expandCornerAnimator;
    private ValueAnimator collapseHeightAnimator;
    private ValueAnimator collapseCornerAnimator;
    private ObjectAnimator textFadeOutAnimator;
    private ObjectAnimator textFadeInAnimator;
    private ObjectAnimator descFadeInAnimator;


    AppStatusViewModel appStatusViewModel;
    DeviceSettingViewModel deviceSettingViewModel;
    EgymDataViewModel egymDataViewModel;
    MainActivity mainActivity;

    public static boolean isNfcLogin = false;
    boolean isEgymNfc = false;//避免 Apple Watch 開啟 NFC 時 被 EGYM 登入


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    private void expandAnimation() {
        MaterialButton btn = getBinding().btnEgymLogin;
        ImageView iv = getBinding().ivEgymIcon;

        // 計算目標位置
        float targetX = btn.getX() + btn.getWidth() / 2f - iv.getWidth() / 2f;
        float targetY = btn.getY() - iv.getHeight() / 2f + 24; // 調整 y 軸位置

        // 放大與移動動畫
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(iv, "scaleX", 1.857f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(iv, "scaleY", 1.857f);
        ObjectAnimator moveX = ObjectAnimator.ofFloat(iv, "x", targetX);
        ObjectAnimator moveY = ObjectAnimator.ofFloat(iv, "y", targetY);

        // 動畫組合
        expandAnimatorSet = new AnimatorSet();
        expandAnimatorSet.playTogether(scaleX, scaleY, moveX, moveY);
        expandAnimatorSet.setDuration(300);
        expandAnimatorSet.start();

        // 關閉按鈕淡入
        getBinding().btnClose.setAlpha(0f);
        getBinding().btnClose.setVisibility(View.VISIBLE);
        getBinding().btnClose.animate().alpha(1f).setDuration(300).setListener(null);

        // 登入文字淡出
        textFadeOutAnimator = ObjectAnimator.ofFloat(getBinding().tvLoginText, "alpha", 1f, 0f);
        textFadeOutAnimator.setDuration(200);
        textFadeOutAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (getBinding() != null) {
                    getBinding().tvLoginText.setVisibility(View.INVISIBLE);
                }
            }
        });
        textFadeOutAnimator.start();

        // 按鈕高度動畫
        expandHeightAnimator = ValueAnimator.ofInt(btn.getHeight(), 368);
        expandHeightAnimator.addUpdateListener(animation -> {
            if (getBinding() != null) {
                int animatedHeight = (int) animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = btn.getLayoutParams();
                layoutParams.height = animatedHeight;
                btn.setLayoutParams(layoutParams);
            }
        });

        // 按鈕圓角動畫
        expandCornerAnimator = ValueAnimator.ofFloat(24, 40);
        expandCornerAnimator.addUpdateListener(animation -> {
            if (getBinding() != null) {
                float animatedCorner = (float) animation.getAnimatedValue();
                btn.setCornerRadius((int) animatedCorner);
            }
        });

        // 同步運行動畫
        expandHeightAnimator.setDuration(300);
        expandCornerAnimator.setDuration(300);
        expandHeightAnimator.start();
        expandCornerAnimator.start();

        // 描述文字淡入
        getBinding().tvEgymDesc.setAlpha(0f);
        getBinding().tvEgymDesc.setVisibility(View.VISIBLE);
        descFadeInAnimator = ObjectAnimator.ofFloat(getBinding().tvEgymDesc, "alpha", 0f, 1f);
        descFadeInAnimator.setDuration(500);
        descFadeInAnimator.start();
    }




    //還原
    private void collapseAnimation() {
        View view = getBinding().ivEgymIcon;
        MaterialButton btn = getBinding().btnEgymLogin;

        // 創建平移與縮放動畫
        ObjectAnimator moveX = ObjectAnimator.ofFloat(view, "translationX", 0);
        ObjectAnimator moveY = ObjectAnimator.ofFloat(view, "translationY", 0);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f);

        // 組合動畫
        collapseAnimatorSet = new AnimatorSet();
        collapseAnimatorSet.playTogether(moveX, moveY, scaleX, scaleY);
        collapseAnimatorSet.setDuration(300);
        collapseAnimatorSet.start();

        // 關閉按鈕淡出
        getBinding().btnClose.animate()
                .alpha(0f)
                .setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (getBinding() != null) {
                            getBinding().btnClose.setVisibility(View.GONE);
                        }
                    }
                });

        // 登入文字淡入
        getBinding().tvLoginText.setAlpha(0f);
        getBinding().tvLoginText.setVisibility(View.VISIBLE);
        textFadeInAnimator = ObjectAnimator.ofFloat(getBinding().tvLoginText, "alpha", 0f, 1f);
        textFadeInAnimator.setDuration(300);
        textFadeInAnimator.start();

        // 按鈕高度動畫
        collapseHeightAnimator = ValueAnimator.ofInt(btn.getHeight(), 272);
        collapseHeightAnimator.addUpdateListener(animation -> {
            if (getBinding() != null) {
                int animatedHeight = (int) animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = btn.getLayoutParams();
                layoutParams.height = animatedHeight;
                btn.setLayoutParams(layoutParams);
            }
        });

        // 按鈕圓角動畫
        collapseCornerAnimator = ValueAnimator.ofFloat(40, 24);
        collapseCornerAnimator.addUpdateListener(animation -> {
            if (getBinding() != null) {
                float animatedCorner = (float) animation.getAnimatedValue();
                btn.setCornerRadius((int) animatedCorner);
            }
        });

        // 同步運行動畫
        collapseHeightAnimator.setDuration(300);
        collapseCornerAnimator.setDuration(300);
        collapseHeightAnimator.start();
        collapseCornerAnimator.start();

        // 隱藏描述文字並重置背景
        getBinding().tvEgymDesc.setVisibility(View.INVISIBLE);
        if (mainActivity != null) {
            mainActivity.getBinding().bgE.setVisibility(View.GONE);
        }
        getBinding().bEgymBG.setVisibility(View.GONE);
    }


    private boolean isExpanded = false; // 記錄當前狀態

    private void initEgymView() {

        getBinding().btnQrcodeLogin.setVisibility(View.GONE);
        getBinding().btnNfcLogin.setVisibility(View.GONE);
        getBinding().btnGotoWorkout.setVisibility(View.GONE);

        getBinding().gEgym.setVisibility(View.VISIBLE);

        getBinding().ivEgymIcon.setVisibility(View.VISIBLE);
        getBinding().tvLoginText.setVisibility(View.VISIBLE);
        getBinding().btnEgymLogin.setVisibility(View.VISIBLE);
        getBinding().tvEgymDesc.setVisibility(View.INVISIBLE);

        getBinding().btnGotoWorkoutE.setOnClickListener(v -> {

            if (!cLimit()) {
                mainActivity.checkLimit();
                return;
            }

            getBinding().vBackground.setVisibility(View.VISIBLE);
            setGuestUser();
            login();
        });

        LiveEventBus.get(EVENT_NFC_ENABLED, Boolean.class).observe(getViewLifecycleOwner(), b -> {
            if (b) {
                //    if (getBinding() != null) getBinding().actionImage.setAlpha(1f);
            }
            //開啟NFC
            //     getDeviceGEM().nfcMessageEnableNfcRadio();
//            getDeviceGEM().nfcMessageDisableNfcRadio();
            Log.d("EgymUtil", "EVENT_NFC_ENABLED: 開啟：" + b);
        });

        //⭐️onNfcEventNfcRead: 97343F1E
        LiveEventBus.get(EVENT_NFC_READ, String.class).observe(getViewLifecycleOwner(), rfidCode -> {

            //避免 Apple Watch 開啟 NFC 時 被 EGYM 登入
        //    Log.d(EgymUtil.TAG, "isEgymNfc: " + isEgymNfc);
            if (!isEgymNfc) return;
            Timber.tag(EgymUtil.TAG).d("isLogin: %s", isNfcLogin);
            if (isNfcLogin) return;

            isNfcLogin = true;

            mainActivity.uartConsole.setBuzzer();

            //      RFID_CODE = toHex(rfidCode);
                  RFID_CODE = rfidCode;

         //   RFID_CODE = reverseUidHex(rfidCode);


            Timber.tag(EgymUtil.TAG).d("uid: %s", rfidCode);
            Timber.tag(EgymUtil.TAG).d("轉HEX: %s", RFID_CODE);

         //   Log.d(EgymUtil.TAG, "NFC登入中: ");


            //⭐️egymClientId  WEB_API , apiGetGymInfo 取得
            //⭐️egymClientSecret WEB_API , apiGetGymInfo 取得
            EgymUtil.getInstance().getEgymSecret(new EgymWebListener() {
                @Override
                public void onFail() {
                    isNfcLogin = false;
                }
            });


//            EgymUtil.getInstance().loginEgymK(RFID_CODE, new EgymWebListener() {
//                @Override
//                public void onFail() {
//                    isLogin = false;
//                }
//            });

        });

        getBinding().btnEgymLogin.setOnClickListener(v -> {
            if (CheckDoubleClick.isFastClick()) return;
            //   Log.d("BBBBBB", "initEgymView: " + isExpanded);
            if (isExpanded) {
                // TODO: 點擊登入 eGym
//                RFID_CODE = "C0313F1E"; //C0 31 3F 1E
//                RFID_CODE = reverseUidHex(RFID_CODE);//1E3F31C0
//                Log.d("EgymUtil", "initEgymView: " + RFID_CODE);
//                EgymUtil.getInstance().loginEgym(reverseUidHex("SP30003"));


                //⭐️egymClientId  WEB_API , apiGetGymInfo 取得
                //⭐️egymClientSecret WEB_API , apiGetGymInfo 取得


//                EgymUtil.getInstance().getTermsAndConditions(new EgymWebListener(){
//                    @Override
//                    public void onSuccess(String result) {
//                        showTermsAndConditionsWindow(result);
//                    }
//
//                    @Override
//                    public void onFailText(String errorText) {
//                    }
//                });

//                EgymUtil.getInstance().getEgymSecret(new EgymWebListener() {
//                    @Override
//                    public void onFailText(String errorText) {
//                        mainActivity.showWebApiAlert(true,errorText);
//                    }
//                });

                //    getBinding().bEgymBG.setVisibility(View.GONE);
                //    mainActivity.getBinding().bgE.setVisibility(View.GONE);


       //         LiveEventBus.get(EVENT_NFC_READ).post("C0313F1E");

            } else {
                //避免 Apple Watch 開啟 NFC 時 被 EGYM 登入
                isEgymNfc = true;

                getBinding().bEgymBG.setVisibility(View.VISIBLE);
                mainActivity.getBinding().bgE.setVisibility(View.VISIBLE);
                expandAnimation(); // 執行動畫
                isExpanded = !isExpanded; // 切換狀態

                //開啟NFC
                //     getDeviceGEM().nfcMessageEnableNfcRadio(DeviceGEM.NFC_READ_EVENT.NFC_NDEF_READ);
                getDeviceGEM().nfcMessageEnableNfcRadio(DeviceGEM.NFC_READ_EVENT.NFC_READ);

                MainActivity.isStopLogin = true;
            }
        });

        getBinding().btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckDoubleClick.isFastClick()) return;
                collapseAnimation(); // 回到原位
                isExpanded = !isExpanded; // 切換狀態

                //關閉NFC
                getDeviceGEM().nfcMessageDisableNfcRadio();

                //避免 Apple Watch 開啟 NFC 時 被 EGYM 登入
                isEgymNfc = false;

                isNfcLogin = false;

                MainActivity.isStopLogin = false;
            }
        });
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        appStatusViewModel = new ViewModelProvider(requireActivity()).get(AppStatusViewModel.class);
        deviceSettingViewModel = new ViewModelProvider(requireActivity()).get(DeviceSettingViewModel.class);
        egymDataViewModel = new ViewModelProvider(requireActivity()).get(EgymDataViewModel.class);
        mainActivity = (MainActivity) requireActivity();


        isHomeScreen = false;

        mainActivity.startHandler();


        try {
            mainActivity.getBinding().ibSetting.setClickable(false);
        } catch (Exception e) {
            showException(e);
        }


        testGboard();


        //     Log.d("EEEEGGGGGGGG", "consoleSystem: " + isUs + "," + deviceSettingViewModel.consoleSystem.get());


        if (isUs && deviceSettingViewModel.consoleSystem.get() == CONSOLE_SYSTEM_SPIRIT) {
            getBinding().vBackground.setVisibility(View.VISIBLE);
            setGuestUser();
            CoTimer.after(200, this::login);
            return;
        } else {
            if (deviceSettingViewModel.consoleSystem.get() == CONSOLE_SYSTEM_EGYM) {
                initEgymView();
            }
        }


        getBinding().gboardddd.setOnKeyListener((view1, keyCode, keyEvent) -> {
            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                getBinding().gboardddd.clearFocus();
                hideSoftKeyboard(requireActivity(), view1);
            }
            return false;
        });


        getBinding().btnqqq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //     testGboard();
//                WifiQrCodePopupWindow popupWindow = new WifiQrCodePopupWindow(requireActivity());
//                popupWindow.showAtLocation(requireActivity().getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);
            }
        });


        if (!isScreenOn()) {
            wakeUpScreen(mainActivity);
        }

        nfcLoginCheck();
        qrCodeLoginCheck();

        egymLoginCheck();

        LiveEventBus.get(GYM3_ON_EVENT, Boolean.class).observe(getViewLifecycleOwner(), s -> {
            nfcLoginCheck();
            qrCodeLoginCheck();
            egymLoginCheck();
        });

        LiveEventBus.get(WIFI_WORK, Boolean.class).observe(getViewLifecycleOwner(), s -> {
            qrCodeLoginCheck();
            nfcLoginCheck();
            egymLoginCheck();
        });

        LiveEventBus.get(LOG_IN_EVENT).observe(getViewLifecycleOwner(), s -> login());

        Looper.myQueue().addIdleHandler(() -> {

            mainActivity.getBinding().ibSetting.setClickable(true);

//            //FTMS 設備狀態 IDLE
//            Map<DeviceGEM.EQUIPMENT_CONTROL_PARAMETER, Integer> parameters = new HashMap<>();
//            parent.updateFtmsMachineStatus(DeviceGEM.FITNESS_EQUIPMENT_STATE.PAUSE_OR_STOPPED_BY_THE_USER, parameters);
            return false;
        });



        getBinding().btnQrcodeLogin.setOnClickListener(v -> {

//            performAnimation(getBinding().imageView);

            if (!cLimit()) {
                mainActivity.checkLimit();
                return;
            }

            if (CheckDoubleClick.isFastClick()) return;
            try {
                Navigation.findNavController(v).navigate(LoginFragmentDirections.actionLoginFragmentToQrcodeLoginFragment());
            } catch (Exception e) {
                Timber.e(e);
                //重複點擊
            }

        });

        getBinding().btnNfcLogin.setOnClickListener(v -> {

            if (!cLimit()) {
                mainActivity.checkLimit();
                return;
            }

            if (!appStatusViewModel.isGem3On.get()) return;

            //     ((MainActivity) requireActivity()).checkUpdate();

            if (CheckDoubleClick.isFastClick()) return;
            try {
                Navigation.findNavController(v).navigate(LoginFragmentDirections.actionLoginFragmentToNfcLoginFragment());
                //   Navigation.findNavController(v).navigate(NavigationLoginDirections.actionGlobalMainBlankFragment());
            } catch (Exception e) {
                Timber.e(e);
            }
        });
        getBinding().btnGotoWorkout.setOnClickListener(v -> {

            if (!cLimit()) {
                mainActivity.checkLimit();
                return;
            }

            getBinding().vBackground.setVisibility(View.VISIBLE);
            setGuestUser();
            login();
        });

        //點10次welcome 出現工程模式
        TenTapClick.setTenClickListener(getBinding().tvWelcome, () -> {
            mainActivity.internetNotifyWarringWindow(false);
            mainActivity.goMaintenanceMode();
        });

//        getBinding().tvWelcome.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                EGYM_SERIAL_NUMBER = EgymUtil.getEgymSerialNumber();
//            //    Log.d("AAASSDDDDD", "EGYM_SERIAL_NUMBER: " + EGYM_SERIAL_NUMBER);
//             //   EGYM_SERIAL_NUMBER = getWifiMacViaWifiManager(getApp());
//                Log.d("AAASSDDDDD", "EGYM_SERIAL_NUMBER: " + EGYM_SERIAL_NUMBER);
//            }
//        });

        TenTapClick.setTenClickListener(getBinding().goAdRom, () -> {
            LwrMcuUpdateManager lwrMcuUpdateManager = getDeviceSpiritC().getLwrMcuUpdateManager();
            lwrMcuUpdateManager.connect();  // 回應connect之後, 才可以下runAprom
            new RxTimer().timer(500, n -> {
                lwrMcuUpdateManager.runAprom(); // 若是在ap rom, 則會回timeout
                Toasty.success(requireActivity(), "AP ROM !!!!!!", Toasty.LENGTH_LONG).show();
            });
        });

        LiveEventBus.get(FTMS_START_OR_RESUME).observe(getViewLifecycleOwner(), s -> {
      //      Log.d("EgymUtil", "onViewCreated: " + SETTING_SHOW +","+ MainActivity.isStopLogin  +","+ (appStatusViewModel.currentStatus.get() != AppStatusIntDef.STATUS_LOGIN_PAGE));
            if (SETTING_SHOW) return;
            if (MainActivity.isStopLogin) return;

            if (appStatusViewModel.currentStatus.get() != AppStatusIntDef.STATUS_LOGIN_PAGE) return;

            if (isTreadmill) {
                if (mainActivity.uartConsole.getDevStep() != DS_IDLE_STANDBY && !isEmulator) {
                    CustomToast.showToast(requireActivity(), getString(R.string.start_waiting));
                    return;
                }
            } else {
                if (mainActivity.uartConsole.getDevStep() != DS_EMS_IDLE_STANDBY && !isEmulator) {
                    CustomToast.showToast(requireActivity(), getString(R.string.start_waiting_bike));
                    return;
                }
            }

            if (isGuestQuickStart) return;

            if (getBinding() != null) {
                isGuestQuickStart = true;
                mainActivity.uartConsole.setBuzzer();
                parent.showLoadingAllB(true);
                getBinding().btnGotoWorkout.callOnClick();

            }

        });

    }

    public static boolean isGuestQuickStart = false;

    //判斷網路 & MAC
    private void qrCodeLoginCheck() {
//        DeviceSettingBean deviceSettingBean = getApp().getDeviceSettingBean();
//        deviceSettingBean.setMachine_mac("");
//        getApp().setDeviceSettingBean(deviceSettingBean);
        if (deviceSettingViewModel.consoleSystem.get() != CONSOLE_SYSTEM_SPIRIT) return;

        if (isNetworkAvailable(requireActivity().getApplication()) && !TextUtils.isEmpty(getApp().getDeviceSettingBean().getMachine_mac())) {
            getBinding().btnQrcodeLogin.setTextColor(ContextCompat.getColorStateList(requireActivity(), R.color.white));
            getBinding().btnQrcodeLogin.setIconTint(ContextCompat.getColorStateList(requireActivity(), R.color.white));
            getBinding().btnQrcodeLogin.setClickable(true);
            getBinding().btnQrcodeLogin.setEnabled(true);
        } else {
            getBinding().btnQrcodeLogin.setTextColor(ContextCompat.getColorStateList(requireActivity(), R.color.color20ffffff));
            getBinding().btnQrcodeLogin.setIconTint(ContextCompat.getColorStateList(requireActivity(), R.color.color20ffffff));
            getBinding().btnQrcodeLogin.setClickable(false);
            getBinding().btnQrcodeLogin.setEnabled(false);
        }
    }

    //判斷網路 & GEM3
    private void nfcLoginCheck() {
        if (deviceSettingViewModel.consoleSystem.get() != CONSOLE_SYSTEM_SPIRIT) return;
        if (isNetworkAvailable(requireActivity().getApplication()) && appStatusViewModel.isGem3On.get()) {
            getBinding().btnNfcLogin.setTextColor(ContextCompat.getColorStateList(requireActivity(), R.color.white));
            getBinding().btnNfcLogin.setIconTint(ContextCompat.getColorStateList(requireActivity(), R.color.white));
            getBinding().btnNfcLogin.setClickable(true);
            getBinding().btnNfcLogin.setEnabled(true);
        } else {
            getBinding().btnNfcLogin.setTextColor(ContextCompat.getColorStateList(requireActivity(), R.color.color20ffffff));
            getBinding().btnNfcLogin.setIconTint(ContextCompat.getColorStateList(requireActivity(), R.color.color20ffffff));
            getBinding().btnNfcLogin.setClickable(false);
            getBinding().btnNfcLogin.setEnabled(false);
        }
    }

    //判斷網路 & GEM3
    private void egymLoginCheck() {
        if (deviceSettingViewModel.consoleSystem.get() != CONSOLE_SYSTEM_EGYM) return;
        if (isNetworkAvailable(getApp()) && appStatusViewModel.isGem3On.get()) {
            getBinding().tvLoginText.setAlpha(1f);
            getBinding().ivEgymIcon.setAlpha(1f);
            getBinding().btnEgymLogin.setClickable(true);
            getBinding().btnEgymLogin.setEnabled(true);
        } else {
            getBinding().tvLoginText.setAlpha(0.4f);
            getBinding().ivEgymIcon.setAlpha(0.4f);
            getBinding().btnEgymLogin.setClickable(false);
            getBinding().btnEgymLogin.setEnabled(false);
        }
    }


    private void setGuestUser() {
        userProfileViewModel.userType.set(USER_TYPE_GUEST);
        //     userProfileViewModel.userType.set(USER_TYPE_NORMAL);
        userProfileViewModel.userDisplayName.set(getString(R.string.guest));
        //    userProfileViewModel.setAvatarId("2");
        userProfileViewModel.setUserAge(AGE_DEF);
        userProfileViewModel.setUserGender(FEMALE);
        userProfileViewModel.setHeight_metric(HEIGHT_MU_DEF);
        userProfileViewModel.setHeight_imperial(HEIGHT_IU_DEF);
        userProfileViewModel.setWeight_metric(WEIGHT_MU_DEF);
        userProfileViewModel.setWeight_imperial(WEIGHT_IU_DEF);

        //EGYM GUEST 要圖片
        //   ((MainActivity) requireActivity()).setAvatar(false);
        //  userProfileViewModel.setUserUnit(IMPERIAL);
    }

    private void login() {
        // mainActivity.showLoading(false);
        if (CheckDoubleClick.isFastClick()) return;
        try {
            //關閉NFC
            getDeviceGEM().nfcMessageDisableNfcRadio();

            MainActivity.isStopLogin = true;
            Navigation.findNavController(requireView()).navigate(NavigationLoginDirections.actionGlobalMainBlankFragment());
        } catch (Exception e) {
            showException(e);
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        isGuestQuickStart = false;
        MainActivity.isStopLogin = false;
        appStatusViewModel.currentStatus.set(AppStatusIntDef.STATUS_LOGIN_PAGE);
        requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //  requireActivity().onUserInteraction();


//        //Glide 若load失敗，每次網路狀態改變，都會再重load一次
//        try {
//
//            //跳出未知來源權限設定頁面時，會被glide蓋掉
//            if (!requireActivity().getPackageManager().canRequestPackageInstalls()) return;
//
//            File file = new File(splashImagePathJPG);
////            if(file.exists() && !file.isDirectory()){
//            if (file.exists()) {
//                GlideApp.with(getApp())
//                        .load(splashImagePathJPG)
//                        .transition(DrawableTransitionOptions.withCrossFade(300))
//                        .placeholder(R.color.color1c242a)
//                        //           .placeholder(R.drawable.bg_launcher_logo)
//                        .diskCacheStrategy(DiskCacheStrategy.NONE)
//                        .skipMemoryCache(true)
//                        .error(R.drawable.bg_homescreen)
//                        .into(getBinding().cHomeBg);
//            } else {
//                GlideApp.with(getApp())
//                        .load(R.drawable.background_new)
//                        .transition(DrawableTransitionOptions.withCrossFade(300))
//                        .diskCacheStrategy(DiskCacheStrategy.NONE)
//                        .placeholder(R.color.color1c242a)
//                        //                  .placeholder(R.drawable.bg_launcher_logo)
//                        .skipMemoryCache(true)
//                        .error(R.drawable.bg_homescreen)
//                        .into(getBinding().cHomeBg);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


        //Glide 若load失敗，每次網路狀態改變，都會再重load一次
        try {
            //跳出未知來源權限設定頁面時，會被glide蓋掉
            if (!requireActivity().getPackageManager().canRequestPackageInstalls()) return;

            String imagePath = splashImagePathJPG;
            // 共用的 Transition
            DrawableTransitionOptions transition = DrawableTransitionOptions.withCrossFade(300);
            // 共用的 Options
            RequestOptions options = new RequestOptions()
                    .placeholder(R.color.color1c242a)
                    .error(R.drawable.bg_homescreen)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true);

            // 先拿到 RequestBuilder<Drawable>
            RequestBuilder<Drawable> builder;
            if (!TextUtils.isEmpty(imagePath) && new File(imagePath).exists()) {
                builder = Glide.with(requireContext())
                        .load(imagePath);
            } else {
                builder = Glide.with(requireContext())
                        .load(R.drawable.background_new);
            }

            // 再依序套用 transition／options／into
            builder
                    .transition(transition)
                    .apply(options)
                    .into(getBinding().cHomeBg);

        } catch (Exception e) {
            Timber.tag("SplashLoad").e(e, "Error loading splash image");
        }
    }


    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //   SpiritDbManager.getInstance(getApp()).clear();

    }

    @Override
    public void onDestroyView() {
        Animator[] animators = {
                expandAnimatorSet, collapseAnimatorSet,
                expandHeightAnimator, expandCornerAnimator,
                collapseHeightAnimator, collapseCornerAnimator,
                textFadeOutAnimator, textFadeInAnimator, descFadeInAnimator
        };

        for (Animator anim : animators) {
            if (anim != null) {
                // 如果動畫正在運行，先取消它
                if (anim.isRunning()) {
                    anim.cancel();
                }
                // 移除所有監聽器，避免洩漏
                anim.removeAllListeners();
            }
        }

        // 清除 ViewPropertyAnimator 可能的監聽器
        if (getBinding() != null) {
            getBinding().btnClose.animate().setListener(null);
        }


        expandAnimatorSet = null;
        collapseAnimatorSet = null;
        expandHeightAnimator = null;
        expandCornerAnimator = null;
        collapseHeightAnimator = null;
        collapseCornerAnimator = null;
        textFadeOutAnimator = null;
        textFadeInAnimator = null;
        descFadeInAnimator = null;


        mainActivity.showWebApiAlert(false, "");
        //    SpiritDbManager.getInstance(getApp()).clear(); 會讓下一頁取資料庫資料時被清掉而取不到
        mainActivity = null;
//        Timber.tag("LeakCanary").d("⚽️⚽️⚽️⚽️⚽️⚽️⚽️⚽️onDestroyView: ");
        super.onDestroyView();
    }

    private boolean cLimit() {
        boolean isShow = true;
        DeviceSettingBean deviceSettingBean = getApp().getDeviceSettingBean();
        //0:都不限制, 1:限制 distance,2:限制 time
        if (deviceSettingBean.getUsageRestrictionsType() != NO_LIMIT) {
            if (deviceSettingBean.getUsageRestrictionsType() == DISTANCE_LIMIT) {
                if (deviceSettingBean.getCurrentUsageRestrictionsDistance() >= deviceSettingBean.getUsageRestrictionsDistanceLimit()) {
                    isShow = false;
                }
            } else {
                if (deviceSettingBean.getCurrentUsageRestrictionsTime() >= (deviceSettingBean.getUsageRestrictionsTimeLimit())) {
                    isShow = false;
                }
            }
        }

        return isShow;
    }

    //Android裝置內建輸入法
    public static String defaultKeyboard = "com.android.inputmethod.latin/.LatinIME";
    public static String GboardPackage = "com.google.android.inputmethod.latin/com.android.inputmethod.latin.LatinIME";

    //設定Gboard為預設輸入法
    private void testGboard() {

        //目前的輸入法
        String oldDefaultKeyboard = Settings.Secure.getString(requireActivity().getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD);
//        Settings.Secure.putString(requireActivity().getContentResolver(), Settings.Secure.ENABLED_INPUT_METHODS, "com.google.android.inputmethod.latin/.full.path");
//        Settings.Secure.putString(requireActivity().getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD, "com.google.android.inputmethod.latin/.full.path");
        Timber.tag("GboardManager").d("目前的輸入法%s", oldDefaultKeyboard);
        if (!GboardPackage.equals(oldDefaultKeyboard)) {
            //目前輸入法不是 Gboard
            //先檢查是否安裝 Gboard
            Timber.tag("GboardManager").d("目前輸入法不是Gboard 先檢查是否安裝Gboard");
            if (checkAppInstalled(requireActivity(), "com.google.android.inputmethod.latin")) {
                Settings.Secure.putString(requireActivity().getContentResolver(), Settings.Secure.ENABLED_INPUT_METHODS, GboardPackage);
                Settings.Secure.putString(requireActivity().getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD, GboardPackage);
                Timber.tag("GboardManager").d("已安裝，開啟 Gboard");
            } else {
                Timber.tag("GboardManager").d("沒安裝 Gboard，無事");
            }

            //選擇輸入法，可不執行
            //# ime set com.google.android.inputmethod.latin/com.android.inputmethod.latin.LatinIME
//            InputMethodManager imeManager = (InputMethodManager) requireActivity().getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
//            imeManager.showInputMethodPicker();
        } else {
            Timber.tag("GboardManager").d("已安裝 Gboard，無事");
        }
    }

}