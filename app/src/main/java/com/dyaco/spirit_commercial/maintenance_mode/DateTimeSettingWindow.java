package com.dyaco.spirit_commercial.maintenance_mode;

import static com.dyaco.spirit_commercial.support.custom_view.wheelPicker.WheelView.MONTH_TEXT;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.WindowDateTimeSettingsBinding;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.custom_view.wheelPicker.BaseDatePickerView;
import com.dyaco.spirit_commercial.support.custom_view.wheelPicker.DatePickerView;
import com.dyaco.spirit_commercial.support.custom_view.wheelPicker.DayWheelView;
import com.dyaco.spirit_commercial.support.custom_view.wheelPicker.MonthWheelView;
import com.dyaco.spirit_commercial.support.custom_view.wheelPicker.OnDateSelectedListener;
import com.dyaco.spirit_commercial.support.custom_view.wheelPicker.OnOptionsSelectedListener;
import com.dyaco.spirit_commercial.support.custom_view.wheelPicker.OptionsPickerView;
import com.dyaco.spirit_commercial.support.custom_view.wheelPicker.WheelView;
import com.dyaco.spirit_commercial.support.custom_view.wheelPicker.YearWheelView;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DateTimeSettingWindow extends BasePopupWindow<WindowDateTimeSettingsBinding> {

    public DateTimeSettingWindow(Context context) {
        super(context, 500, 920, 0, GENERAL.TRANSLATION_Y,false,true,true,true);
        initView();

        initDatePicker();
        init24hTimePicker();
        initAmPmTimePicker();

        initTenseSelected();

        View.OnClickListener rgCheckListener = view -> {
            initTenseSelected();
        };
        getBinding().btn12H.setOnClickListener(rgCheckListener);
        getBinding().btn24H.setOnClickListener(rgCheckListener);
    }

    @SuppressWarnings("unchecked")
    private void initAmPmTimePicker() {

        List<String> list1 = new ArrayList<>(1);
        List<String> list2 = new ArrayList<>(1);
        List<String> list3 = new ArrayList<>(1);
        for (int i = 0; i < 12; i++) {
            list1.add(String.valueOf(i));
        }
        for (int i = 0; i < 60; i++) {
            list2.add(String.valueOf(i));
        }


        String amm = new DateFormatSymbols(Locale.getDefault()).getAmPmStrings()[0];
        String pmm = new DateFormatSymbols(Locale.getDefault()).getAmPmStrings()[1];
        list3.add(amm);
        list3.add(pmm);

//        list3.add("am");
//        list3.add("pm");

        OptionsPickerView<String> pickHour = getBinding().pick12hHour;
        pickHour.setData(list1);
        pickHour.setVisibleItems(8);
        pickHour.setNormalItemTextColor(Color.parseColor("#5a7085"));
        pickHour.setTextSize(54,false);
        pickHour.setCurved(true);
        pickHour.setTextBoundaryMargin(0,true);
        pickHour.setCurvedArcDirection(WheelView.CURVED_ARC_DIRECTION_CENTER);
        pickHour.setCurvedArcDirectionFactor(1.0f);
        pickHour.setSelectedItemTextColor(Color.parseColor("#ffffff"));
       // pickHour.setTextBoundaryMargin(1);
        pickHour.setOnOptionsSelectedListener((opt1Pos, opt1Data, opt2Pos, opt2Data, opt3Pos, opt3Data) -> {
            if (opt1Data == null || opt3Data == null) {
                return;
            }

        });
        OptionsPickerView<String> pickMinute = getBinding().pick12hMinute;
        pickMinute.setData(list2);
        pickMinute.setVisibleItems(8);
        pickMinute.setNormalItemTextColor(Color.parseColor("#5a7085"));
        pickMinute.setTextSize(54,false);
        pickMinute.setCurved(true);
        pickMinute.setCurvedArcDirection(WheelView.CURVED_ARC_DIRECTION_CENTER);
        pickMinute.setCurvedArcDirectionFactor(1.0f);
        pickMinute.setSelectedItemTextColor(Color.parseColor("#ffffff"));
     //   pickMinute.setTextBoundaryMargin(1);
        pickMinute.setOnOptionsSelectedListener((opt1Pos, opt1Data, opt2Pos, opt2Data, opt3Pos, opt3Data) -> {
            if (opt1Data == null || opt2Data == null || opt3Data == null) {
                return;
            }

        });


        OptionsPickerView<String> pickAmPm = getBinding().pickAmPm;
        pickAmPm.setData(list3);
        pickAmPm.setVisibleItems(8);
        pickAmPm.setNormalItemTextColor(Color.parseColor("#5a7085"));
        pickAmPm.setTextSize(54,false);
        pickAmPm.setCurved(true);
        pickAmPm.setCurvedArcDirection(WheelView.CURVED_ARC_DIRECTION_CENTER);
        pickAmPm.setCurvedArcDirectionFactor(1.0f);
        pickAmPm.setSelectedItemTextColor(Color.parseColor("#ffffff"));
        pickAmPm.setOnOptionsSelectedListener((opt1Pos, opt1Data, opt2Pos, opt2Data, opt3Pos, opt3Data) -> {
            if (opt1Data == null || opt2Data == null || opt3Data == null) {
                return;
            }

        });
    }

    @SuppressWarnings("unchecked")
    private void init24hTimePicker() {

        List<String> list1 = new ArrayList<>(1);
        List<String> list2 = new ArrayList<>(1);
        for (int i = 0; i < 24; i++) {
            list1.add(String.valueOf(i));
        }
        for (int i = 0; i < 60; i++) {
            list2.add(String.valueOf(i));
        }

        OptionsPickerView<String> pickHour = getBinding().pick24hHour;
        pickHour.setData(list1);
        pickHour.setVisibleItems(8);
        pickHour.setNormalItemTextColor(Color.parseColor("#5a7085"));
        pickHour.setTextSize(54,false);
        pickHour.setCurved(true);
        pickHour.setTextBoundaryMargin(0,true);
        pickHour.setCurvedArcDirection(WheelView.CURVED_ARC_DIRECTION_CENTER);
        pickHour.setCurvedArcDirectionFactor(1.0f);
        pickHour.setSelectedItemTextColor(Color.parseColor("#ffffff"));
//        opv1.setLinkageData(linkageList1, linkageList2);
        pickHour.setOnOptionsSelectedListener(new OnOptionsSelectedListener<String>() {
            @Override
            public void onOptionsSelected(int opt1Pos, @Nullable String opt1Data, int opt2Pos,
                                          @Nullable String opt2Data, int opt3Pos, @Nullable String opt3Data) {
                if (opt1Data == null || opt3Data == null) {
                    return;
                }

            }
        });
        OptionsPickerView<String> pickMinute = getBinding().pick24hMinute;
        pickMinute.setData(list2);
        pickMinute.setVisibleItems(8);
        pickMinute.setNormalItemTextColor(Color.parseColor("#5a7085"));
        pickMinute.setTextSize(54,false);
        pickMinute.setCurved(true);
        pickMinute.setCurvedArcDirection(WheelView.CURVED_ARC_DIRECTION_CENTER);
        pickMinute.setCurvedArcDirectionFactor(1.0f);
        pickMinute.setSelectedItemTextColor(Color.parseColor("#ffffff"));
        pickMinute.setOnOptionsSelectedListener(new OnOptionsSelectedListener<String>() {
            @Override
            public void onOptionsSelected(int opt1Pos, @Nullable String opt1Data, int opt2Pos,
                                          @Nullable String opt2Data, int opt3Pos, @Nullable String opt3Data) {
                if (opt1Data == null || opt2Data == null || opt3Data == null) {
                    return;
                }

            }
        });
    }

    private void initTenseSelected() {
        boolean is12H = getBinding().btn12H.isChecked(); //ampm

        getBinding().btn12H.setCompoundDrawablesWithIntrinsicBounds(is12H ? ContextCompat.getDrawable(mContext, R.drawable.icon_check) : null, null, null, null);
        getBinding().btn12H.setTextColor(is12H ? ContextCompat.getColor(mContext, R.color.color1396ef) : ContextCompat.getColor(mContext, R.color.colorADB8C2));
        getBinding().btn24H.setCompoundDrawablesWithIntrinsicBounds(!is12H ? ContextCompat.getDrawable(mContext, R.drawable.icon_check) : null, null, null, null);
        getBinding().btn24H.setTextColor(!is12H? ContextCompat.getColor(mContext, R.color.color1396ef) : ContextCompat.getColor(mContext, R.color.colorADB8C2));

        getBinding().pick24hHour.setVisibility(!is12H ? View.VISIBLE : View.GONE);
        getBinding().pick24hMinute.setVisibility(!is12H ? View.VISIBLE : View.GONE);
        getBinding().center24H.setVisibility(!is12H ? View.VISIBLE : View.GONE);

        getBinding().pick12hHour.setVisibility(is12H ? View.VISIBLE : View.GONE);
        getBinding().pick12hMinute.setVisibility(is12H ? View.VISIBLE : View.GONE);
        getBinding().pickAmPm.setVisibility(is12H ? View.VISIBLE : View.GONE);
        getBinding().center12H.setVisibility(is12H ? View.VISIBLE : View.GONE);
    }

    private void initDatePicker() {
        final DatePickerView dateTimePickerView = getBinding().dateTimePickerView;
        dateTimePickerView.setTextSize(54, false);
        dateTimePickerView.setVisibleItems(8);
        dateTimePickerView.setShowLabel(false);
        dateTimePickerView.setSelectedItemTextColor(Color.parseColor("#ffffff"));
        dateTimePickerView.setNormalItemTextColor(Color.parseColor("#5a7085"));
        YearWheelView yearWv3 = dateTimePickerView.getYearWv();
        MonthWheelView monthWv3 = dateTimePickerView.getMonthWv();

        DayWheelView dayWv3 = dateTimePickerView.getDayWv();

        //   yearWv3.setIntegerNeedFormat("%d年");
        //  monthWv3.setIntegerNeedFormat("%d月");
        monthWv3.setIntegerNeedFormat(MONTH_TEXT);
        // dayWv3.setIntegerNeedFormat("%02d日");

        monthWv3.setTextBoundaryMargin(80,true);
        dayWv3.setTextBoundaryMargin(90,true);
        yearWv3.setTextBoundaryMargin(100,true);

        Typeface typeface = mContext.getResources().getFont(R.font.inter_bold);
        monthWv3.setTypeface(typeface);
        dayWv3.setTypeface(typeface);
        yearWv3.setTypeface(typeface);

        yearWv3.setCurved(true);
        yearWv3.setCurvedArcDirection(WheelView.CURVED_ARC_DIRECTION_CENTER);
        yearWv3.setCurvedArcDirectionFactor(1.0f);

        monthWv3.setCurved(true);
        monthWv3.setCurvedArcDirection(WheelView.CURVED_ARC_DIRECTION_CENTER);
        monthWv3.setCurvedArcDirectionFactor(1.0f);

        dayWv3.setCurved(true);
        dayWv3.setCurvedArcDirection(WheelView.CURVED_ARC_DIRECTION_CENTER);
        dayWv3.setCurvedArcDirectionFactor(1.0f);

        dateTimePickerView.setOnDateSelectedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(BaseDatePickerView datePickerView, int year, int month, int day, @Nullable Date date) {
                Toast.makeText(mContext, year + "-" + month + "-" + day, Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void initView() {

        getBinding().btnClose.setOnClickListener(v -> dismiss());
    }
}
