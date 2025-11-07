package com.dyaco.spirit_commercial.settings;

import static com.dyaco.spirit_commercial.App.UNIT_E;
import static com.dyaco.spirit_commercial.App.getApp;
import static com.dyaco.spirit_commercial.MainActivity.isTreadmill;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.EVENT_SET_UNIT;

import android.content.Context;
import android.os.Looper;
import android.view.View;
import android.widget.RadioButton;

import com.dyaco.spirit_commercial.App;
import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.PopupSettingBinding;
import com.dyaco.spirit_commercial.support.CommonUtils;
import com.dyaco.spirit_commercial.support.RxTimer;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.intdef.DeviceIntDef;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;
import com.dyaco.spirit_commercial.support.intdef.LanguageEnum;
import com.dyaco.spirit_commercial.support.utils.CheckDoubleClick;
import com.dyaco.spirit_commercial.support.utils.LanguageUtils;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingBean;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingViewModel;
import com.dyaco.spirit_commercial.viewmodel.WorkoutViewModel;
import com.dyaco.spirit_commercial.workout.programs.ProgramsEnum;
import com.jeremyliao.liveeventbus.LiveEventBus;

import java.util.ArrayList;
import java.util.Locale;

public class SettingPopupWindow extends BasePopupWindow<PopupSettingBinding> {
    DeviceSettingBean deviceSettingBean = getApp().getDeviceSettingBean();
    private Locale locale = Locale.ENGLISH;

    public SettingPopupWindow(Context context, final DeviceSettingViewModel deviceSettingViewModel, WorkoutViewModel workoutViewModel) {
        super(context, 500, 0, 1303, GENERAL.TRANSLATION_X, false, true, true, true);

        initView();

//        String pkg = MediaAppEnum.NETFLIX.getAppPackageName();
//        boolean before = PipController.isPipAllowed(context, pkg);
//        Log.d("PIP", "切前允許嗎？ " + before);
//        boolean ok = PipController.setPipPermission(context, pkg, !before);
//        Log.d("PIP", "指令執行成功？ " + ok);
//        boolean after = PipController.isPipAllowed(context, pkg);
//        Log.d("PIP", "切後允許嗎？ " + after);

        if (workoutViewModel.selProgram == ProgramsEnum.HIIT) {

            getBinding().rgSelectUnit.setEnabled(false);
            getBinding().rgSelectUnit.setClickable(false);
            getBinding().rgSelectUnit.setAlpha(0.4f);

            getBinding().rbImperial.setClickable(false);
            getBinding().rbImperial.setEnabled(false);
            getBinding().rbMetric.setClickable(false);
            getBinding().rbMetric.setEnabled(false);

        }


        if (!workoutViewModel.isWorkoutReadyStart.get()) {

            getBinding().rgSelectUnit.setEnabled(false);
            getBinding().rgSelectUnit.setClickable(false);
            getBinding().rgSelectUnit.setAlpha(0.4f);

            getBinding().rbImperial.setClickable(false);
            getBinding().rbImperial.setEnabled(false);
            getBinding().rbMetric.setClickable(false);
            getBinding().rbMetric.setEnabled(false);

        }


        //放 setOnCheckedChangeListener 前面
        getBinding().rgSelectUnit.check(UNIT_E == DeviceIntDef.IMPERIAL ? R.id.rbImperial : R.id.rbMetric);

        getBinding().rgSelectUnit.setOnCheckedChangeListener((group, checkedId) -> {
            ArrayList<RadioButton> listOfRadioButtons = new ArrayList<>();
            for (int i = 0; i < getBinding().rgSelectUnit.getChildCount(); i++) {
                View o = getBinding().rgSelectUnit.getChildAt(i);
                if (o instanceof RadioButton) {
                    listOfRadioButtons.add((RadioButton) o);
                    if (listOfRadioButtons.get(i).getId() == checkedId) {
                        UNIT_E = checkedId == R.id.rbImperial ? DeviceIntDef.IMPERIAL : DeviceIntDef.METRIC;
                        deviceSettingBean.setUnit_code(UNIT_E);

                        getApp().setDeviceSettingBean(deviceSettingBean);
                        new CommonUtils().mmkvDeviceSettingToViewModel(deviceSettingViewModel, deviceSettingBean);

                        ((MainActivity) mContext).uartConsole.setDevUnit();
                        LiveEventBus.get(EVENT_SET_UNIT).post(true);
                    }
                }
            }
        });

        Looper.myQueue().addIdleHandler(() -> {

            initLanChecked();

            getBinding().rgSelectLanguage.setOnCheckedChangeListener((group, checkedId) -> {
                if (getBinding() == null) return;
                View selectedView = getBinding().rgSelectLanguage.findViewById(checkedId);
                if (selectedView instanceof RadioButton && selectedView.getTag() != null) {
                    int languageId = Integer.parseInt(selectedView.getTag().toString());
                    locale = LanguageEnum.getLanguage(languageId).getLocale();
                    changeLanSetting(locale);
                }
            });


//            getBinding().rgSelectLanguage.setOnCheckedChangeListener((group, checkedId) -> {
//                if (getBinding() == null) return;
//                View selectedView = getBinding().rgSelectLanguage.findViewById(checkedId);
//                if (selectedView instanceof RadioButton) {
//                    Object tag = selectedView.getTag();
//                    if (tag instanceof String) {
//                        int languageId = Integer.parseInt((String) tag);
//                        locale = LanguageEnum.getLanguage(languageId).getLocale();
//                        changeLanSetting(locale);
//                    }
//                }
//            });


//            getBinding().rgSelectLanguage.setOnCheckedChangeListener((group, checkedId) -> {
//                ArrayList<RadioButton> listOfRadioButtons = new ArrayList<>();
//                if (getBinding() == null) return;
//                for (int i = 0; i < getBinding().rgSelectLanguage.getChildCount(); i++) {
//                    View o = getBinding().rgSelectLanguage.getChildAt(i);
//                    if (o instanceof RadioButton) {
//                        listOfRadioButtons.add((RadioButton) o);
//                        if (listOfRadioButtons.get(i).getTag() == null) return;
//                        if (listOfRadioButtons.get(i).getId() == checkedId) {
//                            locale = LanguageEnum.getLanguage(Integer.parseInt((String) listOfRadioButtons.get(i).getTag())).getLocale();
//                            changeLanSetting(locale);
//                            break;
//                        }
//                    }
//                }
//            });
            return false;
        });
    }

    private void initView() {

        getBinding().btnClose.setOnClickListener(v -> dismiss());
    }


    private void initLanChecked() {

        ArrayList<RadioButton> listOfRadioButtons = new ArrayList<>();
        for (int i = 0; i < getBinding().rgSelectLanguage.getChildCount(); i++) {
            View o = getBinding().rgSelectLanguage.getChildAt(i);
            if (o instanceof RadioButton) {
                listOfRadioButtons.add((RadioButton) o);
                if (listOfRadioButtons.get(i).getTag() == null) return;
                Locale locale = LanguageEnum.getLanguage(Integer.parseInt((String) listOfRadioButtons.get(i).getTag())).getLocale();
            //    Log.d("@@@@@@@@@@", "initLanChecked: " + locale + "," + Locale.getDefault() + "," + Locale.getDefault().getCountry() + "," + Locale.getDefault().getLanguage());
                ((RadioButton) o).setChecked(locale.toString().equalsIgnoreCase(LanguageUtils.getLan()));

            }
        }
    }


    //to change the locale
    public void changeLanSetting(Locale loc) {

        if (CheckDoubleClick.isFastClick()) return;

        ((MainActivity) mContext).showVBG(true);


       //todo app語言
//        DeviceSettingBean deviceSettingBean = getApp().getDeviceSettingBean();
//        deviceSettingBean.setDefaultLanguage(loc);
//        getApp().setDeviceSettingBean(deviceSettingBean);
        ///////////////////////////

        App.isShowMediaMenuOnStop = false;
        new RxTimer().timer(200, number -> {
            dismiss();
            LanguageUtils.changeLanguageSetting(loc);
            //todo app語言
            int t = isTreadmill ? 1000 : 500;
//            new RxTimer().timer(t, x -> LanguageUtils.updateLanguage(loc));
            new RxTimer().timer(t, x -> {
                ((MainActivity) mContext).webApiLogout();
            });
        });


    }

}
