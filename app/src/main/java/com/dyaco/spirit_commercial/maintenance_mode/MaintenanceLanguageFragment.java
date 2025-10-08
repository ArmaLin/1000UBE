package com.dyaco.spirit_commercial.maintenance_mode;

import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dyaco.spirit_commercial.App;
import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.databinding.FragmentMaintenanceLanguageBinding;
import com.dyaco.spirit_commercial.support.RxTimer;
import com.dyaco.spirit_commercial.support.base_component.BaseBindingDialogFragment;
import com.dyaco.spirit_commercial.support.intdef.EventKey;
import com.dyaco.spirit_commercial.support.intdef.LanguageEnum;
import com.dyaco.spirit_commercial.support.utils.CheckDoubleClick;
import com.dyaco.spirit_commercial.support.utils.LanguageUtils;
import com.jeremyliao.liveeventbus.LiveEventBus;

import java.util.ArrayList;
import java.util.Locale;


public class MaintenanceLanguageFragment extends BaseBindingDialogFragment<FragmentMaintenanceLanguageBinding> {

    private Locale locale = Locale.ENGLISH;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initEvent();




        new RxTimer().timer(1500,number -> {
            if (getBinding() != null) {
                getBinding().vBackground.setVisibility(View.GONE);
            }
        });

        Looper.myQueue().addIdleHandler(() -> {

            if (getBinding() != null) {

                initLanChecked();

                getBinding().rgSelectLanguage.setOnCheckedChangeListener((group, checkedId) -> {
                    ArrayList<RadioButton> listOfRadioButtons = new ArrayList<>();
                    for (int i = 0; i < getBinding().rgSelectLanguage.getChildCount(); i++) {
                        View o = getBinding().rgSelectLanguage.getChildAt(i);
                        if (o instanceof RadioButton) {
                            listOfRadioButtons.add((RadioButton) o);
                            if (listOfRadioButtons.get(i).getTag() == null) return;
                            if (listOfRadioButtons.get(i).getId() == checkedId) {
                                locale = LanguageEnum.getLanguage(Integer.parseInt((String) listOfRadioButtons.get(i).getTag())).getLocale();
                                Log.d("QQQQQQQQ", "@@@@@SELECT: " + locale.getDisplayName());
                                changeLanSetting(locale);
                                break;
                            }
                        }
                    }

                });
            }
            return false;
        });


    }


    private void initEvent() {

        getBinding().btnDone.setOnClickListener(v -> dismiss());
    }

    public void changeLanSetting(Locale locale) {
        if (CheckDoubleClick.isFastClick()) return;

        //((MainActivity) requireActivity()).showLoading(true);

//        getBinding().vBackground.setVisibility(View.VISIBLE);

        ((MainActivity) requireActivity()).showVBG(true);

        App.isShowMediaMenuOnStop = false;

        new RxTimer().timer(200, number -> {
//            dismiss();
            LiveEventBus.get(EventKey.MAINTENANCE_UPDATE_LANGUAGE_1,Locale.class).post(locale);
//            ((MainActivity) requireActivity()).webApiLogout();
//            int t = isTreadmill ? 1000 : 500;
//            new RxTimer().timer(t, x -> LanguageUtils.updateLanguage(loc));
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private void initLanChecked() {

        ArrayList<RadioButton> listOfRadioButtons = new ArrayList<>();
        for (int i = 0; i < getBinding().rgSelectLanguage.getChildCount(); i++) {
            View o = getBinding().rgSelectLanguage.getChildAt(i);
            if (o instanceof RadioButton) {
                listOfRadioButtons.add((RadioButton) o);
                if (listOfRadioButtons.get(i).getTag() == null) return;
                Locale locale = LanguageEnum.getLanguage(Integer.parseInt((String) listOfRadioButtons.get(i).getTag())).getLocale();
                //  Log.d("@@@@@@@@@@", "initLanChecked: " + locale + "," + Locale.getDefault() +","+Locale.getDefault().getCountry()+","+ Locale.getDefault().getLanguage());
                ((RadioButton) o).setChecked(locale.toString().equalsIgnoreCase(LanguageUtils.getLan()));

            }
        }
    }
}