package com.dyaco.spirit_commercial.maintenance_mode;

import static com.dyaco.spirit_commercial.support.CommonUtils.isNetworkAvailable;
import static com.dyaco.spirit_commercial.support.CommonUtils.updateTime;
import static com.dyaco.spirit_commercial.support.custom_view.wheelPicker.WheelView.MONTH_TEXT;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.TF_AM_PM;
import static com.dyaco.spirit_commercial.support.utils.MyAnimationUtils.crossFade2;

import android.app.AlarmManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.dyaco.spirit_commercial.App;
import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.FragmentMaintenanceDatetimeBinding;
import com.dyaco.spirit_commercial.support.CommonUtils;
import com.dyaco.spirit_commercial.support.GeoLocationCallback;
import com.dyaco.spirit_commercial.support.GeoLocationResolver;
import com.dyaco.spirit_commercial.support.RxTimer;
import com.dyaco.spirit_commercial.support.base_component.BaseBindingDialogFragment;
import com.dyaco.spirit_commercial.support.custom_view.wheelPicker.DatePickerView;
import com.dyaco.spirit_commercial.support.custom_view.wheelPicker.DayWheelView;
import com.dyaco.spirit_commercial.support.custom_view.wheelPicker.MonthWheelView;
import com.dyaco.spirit_commercial.support.custom_view.wheelPicker.OptionsPickerView;
import com.dyaco.spirit_commercial.support.custom_view.wheelPicker.WheelView;
import com.dyaco.spirit_commercial.support.custom_view.wheelPicker.YearWheelView;
import com.dyaco.spirit_commercial.support.intdef.DeviceIntDef;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;
import com.dyaco.spirit_commercial.support.utils.CheckDoubleClick;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingBean;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingViewModel;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import es.dmoral.toasty.Toasty;


public class MaintenanceDateTimeFragment extends BaseBindingDialogFragment<FragmentMaintenanceDatetimeBinding> {

    private boolean isUpdateTimeZone = false;
    private DeviceSettingViewModel deviceSettingViewModel;
    private Calendar calendar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDialogType(GENERAL.TRANSLATION_Y);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView();

        deviceSettingViewModel = new ViewModelProvider(requireActivity()).get(DeviceSettingViewModel.class);


        calendar = Calendar.getInstance();

        //上面的AM PM
        initTenseSelected();

        initDatePicker();

        init24hTimePicker();

        initAmPmTimePicker();

        //true 24
        //   Log.d("LLLLLLLLLL", "onViewCreated: " + DateFormat.is24HourFormat(requireActivity()));


        getBinding().btnTimeZone.setText(TimeZone.getDefault().getID());

        getBinding().btnSave.setOnClickListener(view1 -> {
            if (CommonUtils.setSystemDateAndTime(calendar)) {
                deviceSettingViewModel.timeText.set(updateTime());
            } else {
                Toasty.warning(requireActivity(), "ERROR", Toasty.LENGTH_LONG).show();
            }

            setTimeFormat(getBinding().scTimeUnit.getPosition() == 0);

            dismiss();
        });
    }

    private void initTime() {
        //初始值

        setAutoDateTime(0);
        setAutoDateTime(1);

        new RxTimer().timer(500, number -> {

            if (getBinding() == null) return;

            calendar = Calendar.getInstance();
            pickHour12.setOpt1SelectedPosition(calendar.get(Calendar.HOUR), false);
            pickMinute12.setOpt1SelectedPosition(calendar.get(Calendar.MINUTE), false);
            pickAmPm.setOpt1SelectedPosition(calendar.get(Calendar.AM_PM), false);

            //初始值
            pickHour24.setOpt1SelectedPosition(calendar.get(Calendar.HOUR_OF_DAY), false);
            pickMinute24.setOpt1SelectedPosition(calendar.get(Calendar.MINUTE), false);


            int minYear = calendar.get(Calendar.YEAR);
            int minMonth = calendar.get(Calendar.MONTH) + 1;
            int minDay = calendar.get(Calendar.DAY_OF_MONTH);
            yearWv3.setSelectedYear(minYear, false);
            monthWv3.setSelectedMonth(minMonth, false);
            dayWv3.setSelectedDay(minDay, false);

        });
    }


    private OptionsPickerView<String> pickAmPm;
    private OptionsPickerView<String> pickHour12;
    private OptionsPickerView<String> pickMinute12;

    @SuppressWarnings("unchecked")
    /**12小時制*/
    private void initAmPmTimePicker() {

        List<String> list1 = new ArrayList<>(1);
        List<String> list2 = new ArrayList<>(1);
        List<String> list3 = new ArrayList<>(1);

        list1.add("12");
        for (int i = 1; i < 12; i++) {
            list1.add(String.valueOf(i));
        }
//        for (int i = 0; i < 12; i++) {
//            list1.add(String.valueOf(i));
//        }
        for (int i = 0; i < 60; i++) {
            list2.add(String.valueOf(i));
        }


        String amm = new DateFormatSymbols(Locale.getDefault()).getAmPmStrings()[0];
        String pmm = new DateFormatSymbols(Locale.getDefault()).getAmPmStrings()[1];
        list3.add(amm);
        list3.add(pmm);
//        list3.add(getString(R.string.am));
//        list3.add(getString(R.string.pm));

        pickAmPm = getBinding().pickAmPm;
        pickHour12 = getBinding().pick12hHour;
        pickMinute12 = getBinding().pick12hMinute;

        pickHour12.setData(list1);
        pickHour12.setVisibleItems(8);
        pickHour12.setNormalItemTextColor(Color.parseColor("#5a7085"));
        pickHour12.setTextSize(54, false);
        pickHour12.setCurved(true);
        pickHour12.setCyclic(true);
        pickHour12.setTextBoundaryMargin(0, true);
        pickHour12.setCurvedArcDirection(WheelView.CURVED_ARC_DIRECTION_CENTER);
        pickHour12.setCurvedArcDirectionFactor(1.0f);
        pickHour12.setSelectedItemTextColor(Color.parseColor("#ffffff"));
        // pickHour.setTextBoundaryMargin(1);
        pickHour12.setOnOptionsSelectedListener((opt1Pos, opt1Data, opt2Pos, opt2Data, opt3Pos, opt3Data) -> {
            if (opt1Data == null) return;
            //00:30 = AM 12︰30
            //12︰00 = PM 12︰00
            //12小時制, 12時 >> 0
            int hour12 = Integer.parseInt(opt1Data) == 12 ? 0 : Integer.parseInt(opt1Data);

            calendar.set(Calendar.HOUR, hour12);
        });
        pickMinute12.setData(list2);
        pickMinute12.setVisibleItems(8);
        pickMinute12.setNormalItemTextColor(Color.parseColor("#5a7085"));
        pickMinute12.setTextSize(54, false);
        pickMinute12.setCurved(true);
        pickMinute12.setCyclic(true);
        pickMinute12.setCurvedArcDirection(WheelView.CURVED_ARC_DIRECTION_CENTER);
        pickMinute12.setCurvedArcDirectionFactor(1.0f);
        pickMinute12.setSelectedItemTextColor(Color.parseColor("#ffffff"));
        //   pickMinute.setTextBoundaryMargin(1);
        pickMinute12.setOnOptionsSelectedListener((opt1Pos, opt1Data, opt2Pos, opt2Data, opt3Pos, opt3Data) -> {
            if (opt1Data == null) return;
            calendar.set(Calendar.MINUTE, Integer.parseInt(opt1Data));
        });


        //右邊的AM/PM
        pickAmPm.setData(list3);
        pickAmPm.setVisibleItems(8);
        pickAmPm.setNormalItemTextColor(Color.parseColor("#5a7085"));
        pickAmPm.setTextSize(54, false);
        pickAmPm.setCurved(true);
        pickAmPm.setCurvedArcDirection(WheelView.CURVED_ARC_DIRECTION_CENTER);
        pickAmPm.setCurvedArcDirectionFactor(1.0f);
        pickAmPm.setSelectedItemTextColor(Color.parseColor("#ffffff"));
        pickAmPm.setOnOptionsSelectedListener((opt1Pos, opt1Data, opt2Pos, opt2Data, opt3Pos, opt3Data) -> {
            calendar.set(Calendar.AM_PM, opt1Pos);
        });

        //初始值
        pickHour12.setOpt1SelectedPosition(calendar.get(Calendar.HOUR));
        pickMinute12.setOpt1SelectedPosition(calendar.get(Calendar.MINUTE));
        pickAmPm.setOpt1SelectedPosition(calendar.get(Calendar.AM_PM));
    }


    OptionsPickerView<String> pickHour24;
    OptionsPickerView<String> pickMinute24;

    @SuppressWarnings("unchecked")
    /**24小時制*/
    private void init24hTimePicker() {

        List<String> list1 = new ArrayList<>(1);
        List<String> list2 = new ArrayList<>(1);
        for (int i = 0; i < 24; i++) {
            list1.add(String.valueOf(i));
        }
        for (int i = 0; i < 60; i++) {
            list2.add(String.valueOf(i));
        }

        pickHour24 = getBinding().pick24hHour;
        pickMinute24 = getBinding().pick24hMinute;

        pickHour24.setData(list1);
        pickHour24.setVisibleItems(8);
        pickHour24.setNormalItemTextColor(Color.parseColor("#5a7085"));
        pickHour24.setTextSize(54, false);
        pickHour24.setCurved(true);
        pickHour24.setCyclic(true);
        pickHour24.setTextBoundaryMargin(0, true);
        pickHour24.setCurvedArcDirection(WheelView.CURVED_ARC_DIRECTION_CENTER);
        pickHour24.setCurvedArcDirectionFactor(1.0f);
        pickHour24.setSelectedItemTextColor(Color.parseColor("#ffffff"));
//        opv1.setLinkageData(linkageList1, linkageList2);
        pickHour24.setOnOptionsSelectedListener((opt1Pos, opt1Data, opt2Pos, opt2Data, opt3Pos, opt3Data) -> {
            if (opt1Data == null) return;

            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(opt1Data));
        });

        pickMinute24.setData(list2);
        pickMinute24.setVisibleItems(8);
        pickMinute24.setNormalItemTextColor(Color.parseColor("#5a7085"));
        pickMinute24.setTextSize(54, false);
        pickMinute24.setCurved(true);
        pickMinute24.setCyclic(true);
        pickMinute24.setCurvedArcDirection(WheelView.CURVED_ARC_DIRECTION_CENTER);
        pickMinute24.setCurvedArcDirectionFactor(1.0f);
        pickMinute24.setSelectedItemTextColor(Color.parseColor("#ffffff"));
        pickMinute24.setOnOptionsSelectedListener((opt1Pos, opt1Data, opt2Pos, opt2Data, opt3Pos, opt3Data) -> {
            if (opt1Data == null) return;
            calendar.set(Calendar.MINUTE, Integer.parseInt(opt1Data));
        });

        //初始值
        pickHour24.setOpt1SelectedPosition(calendar.get(Calendar.HOUR_OF_DAY));
        pickMinute24.setOpt1SelectedPosition(calendar.get(Calendar.MINUTE));

    }

    /**
     * 上面的AM PM
     */
    private void initTenseSelected() {
        if (App.TIME_UNIT_E == TF_AM_PM) {
            getBinding().group12.setVisibility(View.VISIBLE);
            getBinding().group24.setVisibility(View.INVISIBLE);
        } else {
            getBinding().group12.setVisibility(View.INVISIBLE);
            getBinding().group24.setVisibility(View.VISIBLE);
        }
        getBinding().scTimeUnit.setPosition(App.TIME_UNIT_E == TF_AM_PM ? 0 : 1, false);

        getBinding().scTimeUnit.setOnPositionChangedListener(position -> {

            boolean is12H = position == 0;

            crossFade2(is12H ? getBinding().group12 : getBinding().group24, is12H ? getBinding().group24 : getBinding().group12, 500);


            DeviceSettingBean deviceSettingBean = App.getApp().getDeviceSettingBean();
            deviceSettingBean.setTime_unit(is12H ? DeviceIntDef.TF_AM_PM : DeviceIntDef.TF_24HR);

            //存進MMKV
            App.getApp().setDeviceSettingBean(deviceSettingBean);

            //MMKV 存入 ViewModel
            new CommonUtils().mmkvDeviceSettingToViewModel(deviceSettingViewModel, deviceSettingBean);

            deviceSettingViewModel.timeText.set(updateTime());

            updateTempTime(is12H);

        });
    }

    private void updateTempTime(boolean is12H) {
        if (is12H) {
            pickAmPm.setOpt1SelectedPosition(calendar.get(Calendar.AM_PM));
            pickHour12.setOpt1SelectedPosition(calendar.get(Calendar.HOUR));
            pickMinute12.setOpt1SelectedPosition(calendar.get(Calendar.MINUTE));
        } else {
            pickHour24.setOpt1SelectedPosition(calendar.get(Calendar.HOUR_OF_DAY));
            pickMinute24.setOpt1SelectedPosition(calendar.get(Calendar.MINUTE));
        }

        setTimeFormat(is12H);
    }

    /**
     * AM PM
     *
     * @param is12H b
     */
    private void setTimeFormat(boolean is12H) {
        Log.d("LLLLLLLLLL", "setTimeFormat: @@@@@@@" + is12H);
        try {
            Settings.System.putString(requireActivity().getContentResolver(), Settings.System.TIME_12_24, is12H ? "12" : "24");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private YearWheelView yearWv3;
    private MonthWheelView monthWv3;
    private DayWheelView dayWv3;

    private void initDatePicker() {
        DatePickerView dateTimePickerView = getBinding().dateTimePickerView;
        dateTimePickerView.setTextSize(54, false);
        dateTimePickerView.setVisibleItems(8);
        dateTimePickerView.setShowLabel(false);
        dateTimePickerView.setSelectedItemTextColor(Color.parseColor("#ffffff"));
        dateTimePickerView.setNormalItemTextColor(Color.parseColor("#5a7085"));

//        YearWheelView yearWv3 = dateTimePickerView.getYearWv();
//        MonthWheelView monthWv3 = dateTimePickerView.getMonthWv();
//        DayWheelView dayWv3 = dateTimePickerView.getDayWv();

        yearWv3 = dateTimePickerView.getYearWv();
        monthWv3 = dateTimePickerView.getMonthWv();
        dayWv3 = dateTimePickerView.getDayWv();

        //   yearWv3.setIntegerNeedFormat("%d年");
        //  monthWv3.setIntegerNeedFormat("%d月");
        monthWv3.setIntegerNeedFormat(MONTH_TEXT);
        // dayWv3.setIntegerNeedFormat("%02d日");

        monthWv3.setTextBoundaryMargin(80, true);
        dayWv3.setTextBoundaryMargin(90, true);
        yearWv3.setTextBoundaryMargin(100, true);

        Typeface typeface = requireActivity().getResources().getFont(R.font.inter_bold);
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

        dateTimePickerView.setOnDateSelectedListener((datePickerView, selYear, selMonth, selDay, date) -> {
            calendar.set(Calendar.YEAR, selYear);
            calendar.set(Calendar.MONTH, selMonth - 1);
            calendar.set(Calendar.DAY_OF_MONTH, selDay);
        });
    }

    private void initView() {

        getBinding().btnClose.setOnClickListener(v -> dismiss());

        getBinding().btnTimeZone.setOnClickListener(view -> {
            if (CheckDoubleClick.isFastClick()) return;
            checkTimeZone();
        });
    }


    public void checkTimeZone() {

        SimpleDateFormat dff = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        dff.setTimeZone(TimeZone.getDefault());
        String ee = dff.format(new Date());

        Log.d("PPPPPPP", "checkTimeZone: " + ee);

        if (isUpdateTimeZone) {
            initTime();
            return;
        }

        if (!isNetworkAvailable(requireActivity().getApplication())) {
            Toasty.warning(requireActivity(), R.string.No_internet_connection, Toasty.LENGTH_LONG).show();
            return;
        }

        getBinding().progress.setVisibility(View.VISIBLE);
        getBinding().hideView.setVisibility(View.VISIBLE);
        Log.d("TIME_ZONE", "Start checkTimeZone: ");


        GeoLocationResolver.newRequest()
                .add("https://freeipapi.com/api/json/", "timeZones[0]") //陣列型態的欄位
                .add("https://ipwho.is/", "timezone.id")//巢狀型態的欄位
                .add("http://www.geoplugin.net/json.gp", "geoplugin_timezone")
                .add("https://ipapi.co/json/", "timezone")
                .add("https://api.ipbase.com/v1/json/", "time_zone")
                .add("http://worldtimeapi.org/api/ip", "timezone")
                .add("https://ipinfo.io/json", "timezone")
            //    .ordered(true) //✅ 照順序呼叫 ,不加或false > 隨機呼叫
                .execute(new GeoLocationCallback() {
                    @Override
                    public void onSuccess(@NonNull String value) {
                        Log.d("GeoLocation", "✅ 成功取得Timezone: " + value);
                        isUpdateTimeZone = true;
                        updateTimeZone(value);
                        getBinding().progress.setVisibility(View.GONE);
                        getBinding().hideView.setVisibility(View.GONE);

                    }

                    @Override
                    public void onFailure(@NonNull String error) {
                        Log.e("GeoLocation", "❌ 全部失敗: " + error);
                        updateTimeZone(TimeZone.getDefault().getID());
                        getBinding().progress.setVisibility(View.GONE);
                        getBinding().hideView.setVisibility(View.GONE);
                    }
                });


//        BaseApi.request(BaseApi.createApi2(IServiceApi.class, "http://www.geoplugin.net/").apiGetTimeZone(),
//                new BaseApi.IResponseListener<TimeZoneBean>() {
//                    @Override
//                    public void onSuccess(TimeZoneBean data) {
//                        try {
//                            Log.d("TIME_ZONE", "onSuccess: " + data.getGeoplugin_timezone());
//                            updateTimeZone(data.getGeoplugin_timezone());
//                        } catch (Exception e) {
//                            Log.d("TIME_ZONE", "try catch_onSuccess: ");
//                            checkTimeZone2();
//                            Toasty.warning(requireActivity(), "Fail 1.1", Toasty.LENGTH_SHORT).show();
//                        } finally {
//                            getBinding().progress.setVisibility(View.GONE);
//                            getBinding().hideView.setVisibility(View.GONE);
//                        }
//                        isUpdateTimeZone = true;
//                        Toasty.success(requireActivity(), "", Toasty.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onFail() {
//                        Log.d("TIME_ZONE", "@@@onFail: ");
//                        checkTimeZone2();
//                        Toasty.warning(requireActivity(), "Fail 1.2", Toasty.LENGTH_SHORT).show();
//                    }
//                });
    }

//    private void checkTimeZone2() {
//        if (getBinding() == null) return;
//        Log.d("TIME_ZONE", "Start checkTimeZone2: ");
//        BaseApi.request(BaseApi.createApi2(IServiceApi.class, "http://ip-api.com/").apiGetTimeZone2(),
//                new BaseApi.IResponseListener<TimeZoneBean2>() {
//                    @Override
//                    public void onSuccess(TimeZoneBean2 data) {
//                        try {
//                            Log.d("TIME_ZONE", "onSuccess: " + data.getTimezone());
//                            updateTimeZone(data.getTimezone());
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            Log.d("TIME_ZONE", "try catch_onSuccess: ");
//                            Toasty.warning(requireActivity(), "Fail 2.1", Toasty.LENGTH_SHORT).show();
//                            updateTimeZone(TimeZone.getDefault().getID());
//                        } finally {
//                            getBinding().progress.setVisibility(View.GONE);
//                            getBinding().hideView.setVisibility(View.GONE);
//                        }
//                        isUpdateTimeZone = true;
//
//                        Toasty.success(requireActivity(), "", Toasty.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onFail() {
//                        Log.d("TIME_ZONE", "onFail: ");
//                        // setTimeZone("Asia/Taipei");
//                        updateTimeZone(TimeZone.getDefault().getID());
//                        Toasty.warning(requireActivity(), "Fail 2.2", Toasty.LENGTH_SHORT).show();
//                    }
//                });
//    }

    private void updateTimeZone(String timeZone) {
        if (getBinding() == null) return;
        try {
            AlarmManager am = (AlarmManager) requireActivity().getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            am.setTimeZone(timeZone);
            Log.d("TIME_ZONE", "TIME_ZONE: " + TimeZone.getDefault().getID());
            new RxTimer().timer(500, number -> {
                if (getBinding() == null) return;
                getBinding().btnTimeZone.setText(TimeZone.getDefault().getID());
                initTime();
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            getBinding().progress.setVisibility(View.GONE);
            getBinding().hideView.setVisibility(View.GONE);
        }
    }

    //0關, 1開

    /**
     * 設置系統是否自動獲取時間
     *
     * @param checked If checked > 0, it will auto set date.
     */
    public void setAutoDateTime(int checked) {
        try {
            android.provider.Settings.Global.putInt(requireActivity().getContentResolver(),
                    android.provider.Settings.Global.AUTO_TIME, checked);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setAutoDateTimeZone(int checked) {
        try {
            android.provider.Settings.Global.putInt(requireActivity().getContentResolver(),
                    android.provider.Settings.Global.AUTO_TIME_ZONE, checked);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}