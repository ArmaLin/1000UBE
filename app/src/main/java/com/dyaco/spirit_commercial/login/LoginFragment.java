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
import com.dyaco.spirit_commercial.support.CommonUtils;
import com.dyaco.spirit_commercial.support.GlideApp;
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

public class LoginFragment extends BaseBindingFragment<FragmentLoginBinding> {

    //    private static final Logger log = LoggerFactory.getLogger(LoginFragment.class);
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

//    public EgymDiagramBarsViewBySet egymDiagramBarsView;

    private void sss() {

//        GeoLocationResolver.newRequest()
//                .add("http://www.geoplugin.net/json.gp", "geoplugin_timezone")
//                .add("https://ipapi.co/json/", "timezone")
//                .add("https://ipwho.is/", "timezone.id")//巢狀欄位
//                .add("https://api.ipbase.com/v1/json/", "time_zone")
//                .add("http://worldtimeapi.org/api/ip", "timezone")
//                .add("https://ipinfo.io/json", "timezone")
//                .execute(new GeoLocationCallback() {
//                    @Override
//                    public void onSuccess(@NonNull String value) {
//                        Log.d("GeoLocation", "✅ 成功取得Timezone: " + value);
//                        //使用取得的時區更新時間和時區
//                    }
//
//                    @Override
//                    public void onFailure(@NonNull String error) {
//                        Log.e("GeoLocation", "❌ 全部失敗: " + error);
//                        //使用當前的時區更新時間
//                    }
//                });


//        ImageView imageView =getBinding().imageView;
////        imageView.setImageResource(R.drawable.egym_avd_draw_trail);
//        imageView.setImageResource(R.drawable.egym_avd_fade_anim);
//        Drawable drawable = imageView.getDrawable();
//        if (drawable instanceof AnimatedVectorDrawable) {
//            AnimatedVectorDrawable avd = (AnimatedVectorDrawable) drawable;
//            avd.start();
//        }

        //    String json = "{ \"genius\": [], \"smart\": [], \"trainer\": [{ \"author\": { \"firstName\": \"Spiritfitness\", \"image\": { \"imageId\": \"qj5FmWYhi8SMo8oUw8dA5A\", \"imageType\": \"AVATAR\" }, \"lastName\": \"Trainer\", \"userId\": \"1ichsmhai7bjz\" }, \"endTimestamp\": 1745884800000, \"exerciseName\": \"Treadmill\", \"frequency\": 2, \"intervals\": [ { \"duration\": 60000, \"incline\": 1, \"speed\": 240.0000024 }, { \"duration\": 120000, \"incline\": 1, \"speed\": 100.0000008 }, { \"duration\": 60000, \"incline\": 1, \"speed\": 3.0000024 }, { \"duration\": 120000, \"incline\": 1, \"speed\": 1.0000008 }, { \"duration\": 60000, \"incline\": 1, \"speed\": 100.0000032 }, { \"duration\": 120000, \"incline\": 1, \"speed\": 110.0000008 }, { \"duration\": 60000, \"incline\": 1, \"speed\": 40.0000032 }, { \"duration\": 120000, \"incline\": 1, \"speed\": 1.0000008 }, { \"duration\": 60000, \"incline\": 1, \"speed\": 5.000004000000001 }, { \"duration\": 120000, \"incline\": 1, \"speed\": 1.0000008 }, { \"duration\": 60000, \"incline\": 1, \"speed\": 5.000004000000001 }, { \"duration\": 120000, \"incline\": 1, \"speed\": 120.0000008 } ], \"orderNumber\": 0, \"sessionName\": \"Session 1\", \"startTimestamp\": 1742256000000, \"timezone\": \"America/Chicago\", \"trainingPlanExerciseId\": 5979275685789696, \"trainingPlanId\": 4946801719508992 }] }";
//        String json = "{\"trainer\":[{\"author\":{\"userId\":\"1ichsmhai7bjz\",\"firstName\":\"Spiritfitness\",\"lastName\":\"Trainer\",\"image\":{\"imageType\":\"AVATAR\",\"imageId\":\"qj5FmWYhi8SMo8oUw8dA5A\"}},\"exerciseName\":\"Treadmill\",\"sessionName\":\"QQQQQ\",\"startTimestamp\":1742342400000,\"endTimestamp\":1745971200000,\"frequency\":2,\"timezone\":\"America/Chicago\",\"trainingPlanId\":4734618255491072,\"trainingPlanExerciseId\":6253907168985088,\"orderNumber\":0,\"intervals\":[{\"speed\":6.111116000000001,\"duration\":1800000,\"distance\":80000.0,\"heartRate\":90,\"incline\":1.0},{\"speed\":6.3888940000000005,\"duration\":9600000,\"distance\":80000.0,\"heartRate\":80,\"incline\":8.0},{\"speed\":8.055562,\"duration\":14000,\"distance\":8000.0,\"incline\":1.0}]}],\"smart\":[],\"genius\":[]}";
//        Gson gson = new Gson();
//        EgymTrainingPlans egymTrainingPlans = gson.fromJson(json, EgymTrainingPlans.class);
//
//        Log.d("EGYM_W", "sss: " + gson.toJson(egymTrainingPlans));
//
//        List<Integer> durationTimesList = new ArrayList<>();
//        List<Integer> setsSpeedList = new ArrayList<>();
//        for (EgymTrainingPlans.TrainerDTO trainer : egymTrainingPlans.getTrainer()) {
//            for (EgymTrainingPlans.TrainerDTO.IntervalsDTO interval : trainer.getIntervals()) {
//                durationTimesList.add(interval.getDuration() / 1000); // 轉換 duration -> 秒
//                setsSpeedList.add((int) Math.round(interval.getSpeed())); // 轉換 speed -> 整數
//            }
//        }


//        EgymIntervalViewBySet progressBar = getBinding().egymIntervalView;
//        final int[] x = {0};
//        progressBar.setDurationTimesList(durationTimesList);
//        progressBar.setProgress(0);
//        progressBar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                x[0]++;
//                progressBar.setProgress(x[0]);
//            }
//        });

//        egymDiagramBarsView = getBinding().egymDiagramBarsView;
//
//
//        egymDiagramBarsView.isTreadmill(isTreadmill);
//
//        int barMaxLevel = (int) (getFloat(getMaxSpeedValue(ProgramsEnum.EGYM)) > 0 ? getMaxSpeedValue(ProgramsEnum.EGYM) + 1 : getMaxSpeedValue(ProgramsEnum.EGYM));
//        //    int barMaxLevel = 24;
//        //bar 長度
//        egymDiagramBarsView.setBarMaxLevel(barMaxLevel);
//
//        int totalTime = durationTimesList.stream().mapToInt(Integer::intValue).sum();
//        Log.d("EGYM_W", "total 時間秒數: " + totalTime);
//        Log.d("EGYM_W", "sets所佔的秒數: " + durationTimesList);
//        Log.d("EGYM_W", "sets SPEED: " + setsSpeedList);
//        Log.d("EGYM_W", "sets數量: " + durationTimesList.size());
//
//        List<Integer> setsTimePosition = convertToPositionList(durationTimesList);
//        Log.d("EGYM_W", "sets,所佔的Index: " + setsTimePosition);
//
//
//        egymDiagramBarsView.setDurationTimesList(durationTimesList);
//
//        Log.d("EGYM_W", "Bar數量: " + egymDiagramBarsView.getBarCount());
//
//        // INCLINE 0.5 > 1階
//        // SPEED 0.1 > 1階
//        //公制 24階 > incline 最大 30, speed 最大 240
//
////        //紫色incline在前面, 蓋住 Speed, 藍色speed 在後面
//
//
//        int[] currentIndex = {0};
//        AtomicInteger targetValue = new AtomicInteger();
////        new RxTimer().interval(1000, x -> {
////            egymDiagramBarsView.setWhiteLinePosition(currentIndex[0]);
////            currentIndex[0] += 1;
//            targetValue.set(setsTimePosition.stream().filter(num -> num >= currentIndex[0]).findFirst().orElse(currentIndex[0]));
////        });
//
//        isChartHidden = false;
//        final int[] speedValue = {120};
//
//
//
//        LongClickUtil.attach(getBinding().iM).observe(getViewLifecycleOwner(), unit -> {
//            speedValue[0] -= 1;
//            for (int i = currentIndex[0]; i <= targetValue.get(); i++) {
//                egymDiagramBarsView.setBarLevel(BAR_TYPE_LEVEL_SPEED, i, speedValue[0], currentIndex[0], false);
//            }
//        });
//
//        LongClickUtil.attach(getBinding().iP).observe(getViewLifecycleOwner(), unit -> {
//            speedValue[0] += 1;
//            for (int i = currentIndex[0]; i <= targetValue.get(); i++) {
//                egymDiagramBarsView.setBarLevel(BAR_TYPE_LEVEL_SPEED, i, speedValue[0], currentIndex[0], false);
//            }
//        });
//
//        egymDiagramBarsView.setWhiteLinePosition(currentIndex[0]);
//
//        LongClickUtil.attach(getBinding().sM).observe(getViewLifecycleOwner(), unit -> {
//            currentIndex[0] += 1;
//
//            egymDiagramBarsView.setWhiteLinePosition(currentIndex[0]);
//
//
//            targetValue.set(setsTimePosition.stream().filter(num -> num >= currentIndex[0]).findFirst().orElse(currentIndex[0]));
//
//            Log.d("EGYM_W", "目標index: " + targetValue);
//        });

    }


    private void expandAnimation() {
        MaterialButton btn = getBinding().btnEgymLogin;
        ImageView iv = getBinding().ivEgymIcon;

        // 計算目標位置
        float targetX = btn.getX() + btn.getWidth() / 2f - iv.getWidth() / 2f; // 按鈕的水平中心
        float targetY = btn.getY() - iv.getHeight() / 2f; // 按鈕的上方邊緣 負值表示向上移動
        targetY += 24; // 不要往上移動太多 扣掉 24
        // 放大動畫
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(iv, "scaleX", 1.857f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(iv, "scaleY", 1.857f);

        // 移動動畫
        ObjectAnimator moveX = ObjectAnimator.ofFloat(iv, "x", targetX);
        ObjectAnimator moveY = ObjectAnimator.ofFloat(iv, "y", targetY);

        // 動畫組合
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY, moveX, moveY);
        animatorSet.setDuration(300);
        animatorSet.start();

        ////////////////////


        getBinding().btnClose.setAlpha(0f);
        getBinding().btnClose.setVisibility(View.VISIBLE);
        getBinding().btnClose.animate()
                .alpha(1f)
                .setDuration(300)
                .setListener(null);

//        float translationY2 = CommonUtils.dpToPx(requireActivity(), -50); // 负值表示向上移动
//        ObjectAnimator moveY2 = ObjectAnimator.ofFloat(getBinding().btnClose, "translationY", 0, translationY2);
//        AnimatorSet animatorSet2 = new AnimatorSet();
//        animatorSet2.playTogether(moveY2);
//        animatorSet2.setDuration(300);
//        animatorSet2.start();


        ///////////////////////


        //     String text = getBinding().btnQQQQQ.getText().toString();
        //   SpannableString spannable = new SpannableString(text);
//        //按鈕中間文字消失
//        getBinding().btnQQQQQ.animate()
//                .setDuration(500)
//                .setUpdateListener(animation -> {
//                    float alpha = 1f - animation.getAnimatedFraction();
//                    int color = CommonUtils.adjustAlpha(alpha);
//                    spannable.setSpan(new ForegroundColorSpan(color), 0, text.length(), 0);
//                    getBinding().btnQQQQQ.setText(spannable);
//                })
//                .start();

        getBinding().tvLoginText.animate()
                .alpha(0f)
                .setDuration(200)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        getBinding().tvLoginText.setVisibility(View.INVISIBLE);
                    }
                });


        // 高度動畫
        ValueAnimator heightAnimator = ValueAnimator.ofInt(272, 368);
        heightAnimator.addUpdateListener(animation -> {
            int animatedHeight = (int) animation.getAnimatedValue();
            ViewGroup.LayoutParams layoutParams = btn.getLayoutParams();
            layoutParams.height = animatedHeight;
            btn.setLayoutParams(layoutParams);

            // 動態調整按鈕的 translationY
//            float translation = (272 - animatedHeight) / 2f; // 平均移動
//            btn.setTranslationY(translation);
        });


        //radius 24 > 40
        // 圓角動畫
        ValueAnimator cornerAnimator = ValueAnimator.ofFloat(24, 40);
        cornerAnimator.addUpdateListener(animation -> {
            float animatedCorner = (float) animation.getAnimatedValue();
            btn.setCornerRadius((int) animatedCorner);
        });

        // 同步運行動畫
        heightAnimator.setDuration(300);
        cornerAnimator.setDuration(300);
        heightAnimator.start();
        cornerAnimator.start();


        ///
        getBinding().tvEgymDesc.setAlpha(0f);
        getBinding().tvEgymDesc.setVisibility(View.VISIBLE); // 先設置為可見
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(getBinding().tvEgymDesc, "alpha", 0f, 1f);
        fadeIn.setDuration(500); // 設置動畫時長
        fadeIn.start();

        //   getBinding().tvEgymDesc.setVisibility(View.VISIBLE);
    }

    //還原
    private void collapseAnimation() {
        View view = getBinding().ivEgymIcon;
        // 創建平移動畫
        ObjectAnimator moveX = ObjectAnimator.ofFloat(view, "translationX", view.getTranslationX(), 0);
        ObjectAnimator moveY = ObjectAnimator.ofFloat(view, "translationY", view.getTranslationY(), 0);

        // 創建縮放動畫
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", view.getScaleX(), 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", view.getScaleY(), 1f);

        // 組合動畫
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(moveX, moveY, scaleX, scaleY);
        animatorSet.setDuration(300);
        animatorSet.start();

        // 按鈕淡出
        getBinding().btnClose.animate()
                .alpha(0f)
                .setDuration(300)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        getBinding().btnClose.setVisibility(View.GONE); // 動畫結束後隱藏按鈕
                    }

                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });


        //按鈕中間文字還原
//        String text = getBinding().btnQQQQQ.getText().toString();
//        SpannableString spannable = new SpannableString(text);
//        // 動畫過程中逐漸增加文字透明度
//        getBinding().btnQQQQQ.animate()
//                .setDuration(500)
//                .setUpdateListener(animation -> {
//                    float alpha = animation.getAnimatedFraction();
//                    int color = CommonUtils.adjustAlpha(alpha); // 動態調整文字顏色透明度
//                    spannable.setSpan(new ForegroundColorSpan(color), 0, text.length(), 0);
//                    getBinding().btnQQQQQ.setText(spannable); // 更新按鈕文字
//                })
//                .start();

        getBinding().tvLoginText.setAlpha(0f);
        getBinding().tvLoginText.setVisibility(View.VISIBLE);
        getBinding().tvLoginText.animate()
                .alpha(1f)
                .setDuration(300)
                .setListener(null);

        // 高度動畫
        ValueAnimator heightAnimator = ValueAnimator.ofInt(368, 272); // (272,原始高度)
        heightAnimator.addUpdateListener(animation -> {
            int animatedHeight = (int) animation.getAnimatedValue();
            ViewGroup.LayoutParams layoutParams = getBinding().btnEgymLogin.getLayoutParams();
            layoutParams.height = animatedHeight;
            getBinding().btnEgymLogin.setLayoutParams(layoutParams);

            // 動態調整按鈕的 translationY
//            float translation = (272 - animatedHeight) / 2f; // 平均移動
//            getBinding().btnEgymLogin.setTranslationY(translation);
        });

        float translationY2 = CommonUtils.dpToPx(requireActivity(), 0); // 负值表示向上移动
        ObjectAnimator moveY2 = ObjectAnimator.ofFloat(getBinding().btnClose, "translationY", 0, translationY2);
        AnimatorSet animatorSet2 = new AnimatorSet();
        animatorSet2.playTogether(moveY2);
        animatorSet2.setDuration(300);
        animatorSet2.start();

        // 圓角動畫
        ValueAnimator cornerAnimator = ValueAnimator.ofFloat(40, 24);
        cornerAnimator.addUpdateListener(animation -> {
            float animatedCorner = (float) animation.getAnimatedValue();
            getBinding().btnEgymLogin.setCornerRadius((int) animatedCorner);
        });

        // 同步運行動畫
        heightAnimator.setDuration(300);
        cornerAnimator.setDuration(300);
        heightAnimator.start();
        cornerAnimator.start();


        getBinding().tvEgymDesc.setVisibility(View.INVISIBLE);
//        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(getBinding().tvEgymDesc, "alpha", 1f, 0f);
//        fadeOut.setDuration(500); // 設置動畫時長
//        fadeOut.start();
//        fadeOut.addListener(new android.animation.AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(android.animation.Animator animation) {
//                getBinding().tvEgymDesc.setVisibility(View.INVISIBLE); // 動畫結束後設置為不可見
//            }
//        });

        mainActivity.getBinding().bgE.setVisibility(View.GONE);
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
//        getBinding().btnQQQQQ.post(() -> {
//            int textLength = getString(R.string.Login_to_Egym).length();
//            Log.d("CCCCOOOOO", "onViewCreated: " + textLength);
//            int paddingStartInDp = 40 + textLength;
//            int paddingTop = getBinding().btnQQQQQ.getPaddingTop();
//            int paddingEnd = getBinding().btnQQQQQ.getPaddingEnd();
//            int paddingBottom = getBinding().btnQQQQQ.getPaddingBottom();
//            getBinding().btnQQQQQ.setPaddingRelative(paddingStartInDp, paddingTop, paddingEnd, paddingBottom);
//        });


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
            Log.d(EgymUtil.TAG, "isLogin: " + isNfcLogin);
            if (isNfcLogin) return;

            isNfcLogin = true;

            mainActivity.uartConsole.setBuzzer();

            //      RFID_CODE = toHex(rfidCode);
            //      RFID_CODE = rfidCode;

            RFID_CODE = reverseUidHex(rfidCode);


            Log.d(EgymUtil.TAG, "uid: " + rfidCode);
            Log.d(EgymUtil.TAG, "轉HEX: " + RFID_CODE);

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


//    TermsAndConditionsWindow termsAndConditionsWindow;
//    public void showTermsAndConditionsWindow(String text) {
//
//        if (termsAndConditionsWindow != null) {
//            termsAndConditionsWindow.dismiss();
//            termsAndConditionsWindow = null;
//        }
//
//        termsAndConditionsWindow = new TermsAndConditionsWindow(requireActivity(),text);
//        termsAndConditionsWindow.showAtLocation(requireActivity().getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);
//        termsAndConditionsWindow.setOnCustomDismissListener(new BasePopupWindow.OnCustomDismissListener() {
//            @Override
//            public void onStartDismiss(MsgEvent value) {
//                if (value != null) {
//
//                }
//            }
//
//            @Override
//            public void onDismiss() {
//                //   hrUpdateWindow = null; 不能加
//            }
//        });
//    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        appStatusViewModel = new ViewModelProvider(requireActivity()).get(AppStatusViewModel.class);
        deviceSettingViewModel = new ViewModelProvider(requireActivity()).get(DeviceSettingViewModel.class);
        egymDataViewModel = new ViewModelProvider(requireActivity()).get(EgymDataViewModel.class);
        mainActivity = (MainActivity) requireActivity();

//        WorkoutViewModel workoutViewModel =  new ViewModelProvider(requireActivity()).get(WorkoutViewModel.class);
//
////        workoutViewModel.isSamsungWatchEnabled.set(true);
////        workoutViewModel.isSamsungWatchConnected.set(true);
//        workoutViewModel.isGarminConnected.set(true);
        sss();

        isHomeScreen = false;

        mainActivity.startHandler();


        try {
            mainActivity.getBinding().ibSetting.setClickable(false);
        } catch (Exception e) {
            showException(e);
        }


//        // 設定數據
//        getBinding().stackedBarChartView.setData(
//                new String[]{"0:00", "8:00", "16:00", "24:00", ""},
//                new String[]{"0.0", "4.0", "8.0", "12.0", "16.0", "20.0"},
//                new float[]{8, 10, 8, 4, 10, 6, 8, 19, 4},
//                new float[]{8, 10, 12, 8, 16, 10, 12, 19, 6}
//        );
//
//// 設定柱狀圖顏色 color_level_speed_bar_run
////        getBinding().stackedBarChartView.setBarColors(
////                "#E61396EF", // Outcome 顶部颜色
////                "#331396EF", // Outcome 底部颜色
////                "#1A1396EF"  // Target 颜色
////        );
//        getBinding().stackedBarChartView.setBarColors(
//                ContextCompat.getColor(requireActivity(), R.color.color_level_speed_bar_run), // Outcome 顶部颜色
//                ContextCompat.getColor(requireActivity(), R.color.color_level_speed_bar_run), // Outcome 底部颜色
//                ContextCompat.getColor(requireActivity(), R.color.color1A1396ef)  // Target 颜色
//        );

        //    InputMethodSwitcher.switchInputMethod(getApp());
        testGboard();


        //     Log.d("EEEEGGGGGGGG", "consoleSystem: " + isUs + "," + deviceSettingViewModel.consoleSystem.get());


        if (isUs && deviceSettingViewModel.consoleSystem.get() == CONSOLE_SYSTEM_SPIRIT) {
            getBinding().vBackground.setVisibility(View.VISIBLE);
            setGuestUser();
            login();
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


//        getBinding().btnQrcodeLogin.
//                setOnClickListener(
//                        new OnMultiClickListener() {
//                            @Override
//                            public void onMultiClick(View v) {
//                                Navigation.findNavController(v).navigate(LoginFragmentDirections.actionLoginFragmentToQrcodeLoginFragment());
//                              //  parent.navController.navigate(LoginFragmentDirections.actionLoginFragmentToQrcodeLoginFragment());
//                            }
//                        }
//                );


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
                e.printStackTrace();
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
                e.printStackTrace();
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


//        Map<DeviceGEM.EQUIPMENT_CONTROL_PARAMETER, Integer> parameters = new HashMap<>();
//        getDeviceGEM().gymConnectMessageSetFitnessEquipmentState(DeviceGEM.FITNESS_EQUIPMENT_STATE.IDLE, parameters);

//        getBinding().vvNFC.setVisibility(View.VISIBLE);
//        getBinding().vvIdel.setVisibility(View.VISIBLE);
//        getBinding().vvInUSE.setVisibility(View.VISIBLE);
//        getBinding().vvFinish.setVisibility(View.VISIBLE);

//        getBinding().vvNFC.setOnClickListener(view13 -> {
//
//        });
//
//
//        getBinding().vvIdel.setOnClickListener(view13 -> {
//            getDeviceGEM().gymConnectMessageSetFitnessEquipmentState(DeviceGEM.FITNESS_EQUIPMENT_STATE.IDLE, parameters);
//        });
//
//        getBinding().vvInUSE.setOnClickListener(view12 -> {
//            getDeviceGEM().gymConnectMessageSetFitnessEquipmentState(DeviceGEM.FITNESS_EQUIPMENT_STATE.IN_USE, parameters);
//        });
//
//        getBinding().vvFinish.setOnClickListener(view1 -> {
//            getDeviceGEM().gymConnectMessageSetFitnessEquipmentState(DeviceGEM.FITNESS_EQUIPMENT_STATE.FINISHED, parameters);
//        });
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
                builder = GlideApp.with(requireContext())
                        .load(imagePath);
            } else {
                builder = GlideApp.with(requireContext())
                        .load(R.drawable.background_new);
            }

            // 再依序套用 transition／options／into
            builder
                    .transition(transition)
                    .apply(options)
                    .into(getBinding().cHomeBg);

        } catch (Exception e) {
            Log.e("SplashLoad", "Error loading splash image", e);
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
        super.onDestroyView();
        mainActivity.showWebApiAlert(false, "");
        //    SpiritDbManager.getInstance(getApp()).clear(); 會讓下一頁取資料庫資料時被清掉而取不到

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
        Log.d("GboardManager", "目前的輸入法" + oldDefaultKeyboard);
        if (!GboardPackage.equals(oldDefaultKeyboard)) {
            //目前輸入法不是 Gboard
            //先檢查是否安裝 Gboard
            Log.d("GboardManager", "目前輸入法不是Gboard 先檢查是否安裝Gboard");
            if (checkAppInstalled(requireActivity(), "com.google.android.inputmethod.latin")) {
                Settings.Secure.putString(requireActivity().getContentResolver(), Settings.Secure.ENABLED_INPUT_METHODS, GboardPackage);
                Settings.Secure.putString(requireActivity().getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD, GboardPackage);
                Log.d("GboardManager", "已安裝，開啟 Gboard");
            } else {
                Log.d("GboardManager", "沒安裝 Gboard，無事");
            }

            //選擇輸入法，可不執行
            //# ime set com.google.android.inputmethod.latin/com.android.inputmethod.latin.LatinIME
//            InputMethodManager imeManager = (InputMethodManager) requireActivity().getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
//            imeManager.showInputMethodPicker();
        } else {
            Log.d("GboardManager", "已安裝 Gboard，無事");
        }
    }

}