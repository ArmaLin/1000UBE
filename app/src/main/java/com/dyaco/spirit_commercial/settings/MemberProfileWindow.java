package com.dyaco.spirit_commercial.settings;

import static com.dyaco.spirit_commercial.App.UNIT_E;
import static com.dyaco.spirit_commercial.App.getApp;
import static com.dyaco.spirit_commercial.MainActivity.userProfileViewModel;
import static com.dyaco.spirit_commercial.support.FormulaUtil.kg2lb2;
import static com.dyaco.spirit_commercial.support.FormulaUtil.totalIn2In;
import static com.dyaco.spirit_commercial.support.FormulaUtil.totalIn2ft;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.METRIC;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.USER_TYPE_GUEST;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;

import com.addisonelliott.segmentedbutton.SegmentedButtonGroup;
import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.WindowMemberProfileBinding;
import com.dyaco.spirit_commercial.model.webapi.BaseApi;
import com.dyaco.spirit_commercial.model.webapi.CallWebApi;
import com.dyaco.spirit_commercial.model.webapi.IServiceApi;
import com.dyaco.spirit_commercial.model.webapi.bean.GetMyWeightTrendBean;
import com.dyaco.spirit_commercial.support.CommonUtils;
import com.bumptech.glide.Glide;
import com.dyaco.spirit_commercial.support.LineChartData;
import com.dyaco.spirit_commercial.support.MsgEvent;
import com.dyaco.spirit_commercial.support.RxTimer;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.intdef.DeviceIntDef;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;
import com.dyaco.spirit_commercial.support.utils.CheckDoubleClick;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingViewModel;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class MemberProfileWindow extends BasePopupWindow<WindowMemberProfileBinding> {
    private final Context mContext;
    private DeviceSettingViewModel deviceSettingViewModel;
    private boolean isImperial;

    public PopupWindow popupWindow;

    public MemberProfileWindow(Context context, DeviceSettingViewModel deviceSettingViewModel) {
        super(context, 300, 0, 0, GENERAL.TRANSLATION_Y, false, true, true, true);
        this.deviceSettingViewModel = deviceSettingViewModel;
        mContext = context;

        //  userProfileEntity = getApp().getUserProfile();

        getBinding().setDeviceSetting(deviceSettingViewModel);
        getBinding().setUserProfileViewModel(userProfileViewModel);

        new RxTimer().timer(100, number -> {
            try {
                initEvent();
                initChart();
                webApiGetMyUserInfoFromMachine();
                webApiGetMyWeightTrend();


            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    ArrayList<String> xData = new ArrayList<>();
    ArrayList<Entry> yData = new ArrayList<>();
    LineChartData lineChartData;
    LineChart lineChart;

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
    }

    private void initChart() {
        lineChart = getBinding().chart1;

        lineChartData = new LineChartData(lineChart, mContext);

    }

    private void setWeightData(List<GetMyWeightTrendBean.DataMapDTO.DataDTO> dataDTOList) {
        int i = 0;

        xData.clear();
        yData.clear();
        if (dataDTOList.size() <= 0) return;

        //*******TEST
//        dataDTOList.clear();
//        for (int g = 1; g <= 70; g++) {
//            GetMyWeightTrendBean.DataMapDTO.DataDTO d = new GetMyWeightTrendBean.DataMapDTO.DataDTO();
//            d.setWeight(String.valueOf(g + 10));
//            d.setDate("2011-11/" +g);
//         //   d.setDate("2011-11/01");
//            dataDTOList.add(d);
//        }
        //*********

        if (dataDTOList.size() == 1) {//只有一筆資料時
            GetMyWeightTrendBean.DataMapDTO.DataDTO d = new GetMyWeightTrendBean.DataMapDTO.DataDTO();
            d.setWeight(dataDTOList.get(0).getWeight());
            d.setDate(dataDTOList.get(0).getDate());
            dataDTOList.add(d);
        }

        Collections.reverse(dataDTOList);//翻轉

        for (GetMyWeightTrendBean.DataMapDTO.DataDTO dataDTO : dataDTOList) {
            xData.add(dataDTO.getDate().substring(5).replace("-", "/"));//日期 2000-02-19
            int weight = UNIT_E == METRIC ? (int) Double.parseDouble(dataDTO.getWeight()) : (int) kg2lb2(Double.parseDouble(dataDTO.getWeight()));
            Log.d("WEB_API", "setWeightData: " + weight + "," + dataDTO.getWeight());
            yData.add(new Entry(i, weight));//體重
            i++;
        }

//        //924,576
//        int chartWidth = 924;
//        int chartHeight = 576;
//        if (xData.size() > 31) {
//            int c = xData.size() - 31;
//            chartWidth = chartWidth + (24 * c);
//
//            getBinding().chart1.setLayoutParams(new LinearLayout.LayoutParams(chartWidth, chartHeight));
//        }
        //日期
        // Log.d("EEWDEFE", "#########setWeightData: " + xData.size());
        lineChartData.initX(xData);

        lineChartData.initY(yData);

        lineChartData.initDataSet(yData);
    }

    private void initData() {
        if (getBinding() == null) return;

        isImperial = UNIT_E == DeviceIntDef.IMPERIAL;

        //Avatar
        //   getBinding().btnAvatar.setBackgroundResource(CommonUtils.getAvatarIconFromTag(userProfileEntity.getAvatarTag()));
        //  getBinding().vMemberType.setVisibility(userProfileEntity.isVip() ? View.VISIBLE : View.INVISIBLE);

        //WEIGHT
        int weight = (int) (isImperial ? userProfileViewModel.getWeight_imperial() : userProfileViewModel.getWeight_metric());
        getBinding().tvWeight.setText(String.valueOf(weight));

        //AGE
        int age = userProfileViewModel.getUserAge();
        getBinding().tvAge.setText(String.valueOf(age));

        //Height
        int height;
        if (isImperial) {
            height = (int) userProfileViewModel.getHeight_imperial();
            getBinding().tvHeightMetricFt.setText(String.valueOf(totalIn2ft(height)));
            getBinding().tvHeightMetricIn.setText(String.valueOf(totalIn2In(height)));
        } else {
            height = (int) userProfileViewModel.getHeight_metric();
            getBinding().tvHeightMetricCm.setText(String.valueOf(height));
        }
        //Gender
        getBinding().scGender.setPosition(userProfileViewModel.getUserGender(), false);

        //Member Since & Membership Expiration Date
//        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH);
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());

        String memberSince = "N/A";
        if (userProfileViewModel.getJoinedGymDateTimeMillis() != null) { //"02/18/2022 09:22:58"
            try {
                memberSince = sdf.format(userProfileViewModel.getJoinedGymDateTimeMillis());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        getBinding().tvMemberSinceDate.setText(memberSince);

        String membershipExpirationDate = "N/A";
        if (userProfileViewModel.getMembershipExpirationDate() != null) {
            try {
                membershipExpirationDate = sdf.format(userProfileViewModel.getMembershipExpirationDate());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        getBinding().tvMemberExpirationDate.setText(membershipExpirationDate);

        setProfileAvatar();

//        if (userProfileViewModel.getPhotoFileUrl() != null && !userProfileViewModel.getPhotoFileUrl().equals("")) {
        //btnAvatarWeb 有上傳圖片
        if (userProfileViewModel.getAvatarId() == null) {
            getBinding().btnAvatar.setVisibility(View.GONE);
            getBinding().btnAvatarWeb.setVisibility(View.VISIBLE);
            Glide.with(getApp())
                    .load(userProfileViewModel.getPhotoFileUrl())
//                    .diskCacheStrategy(DiskCacheStrategy.NONE)
//                    .skipMemoryCache(true)
                    .centerInside()
                    .placeholder(R.drawable.shape_oval_grey)
                    .circleCrop()
                    .into(getBinding().btnAvatarWeb);

        }

    }

    private void editAvatar() {
        if (CheckDoubleClick.isFastClick()) return;

        clearPopupWindow();
        getBinding().vBackground.setVisibility(View.VISIBLE);
        popupWindow = new MemberProfileAvatarEditorWindow(mContext);
        popupWindow.showAtLocation(parentView, Gravity.END | Gravity.BOTTOM, 0, 0);
        ((MemberProfileAvatarEditorWindow) popupWindow).setOnCustomDismissListener(new OnCustomDismissListener() {
            @Override
            public void onStartDismiss(MsgEvent value) {
                getBinding().vBackground.setVisibility(View.GONE);

                if (value != null) {
                    setProfileAvatar();
                    ((MainActivity) mContext).setAvatar(true);
                }
            }

            @Override
            public void onDismiss() {
                popupWindow = null;
            }
        });
    }

    private void initEvent() {

        getBinding().btnClose.setOnClickListener(v -> dismiss());

        getBinding().btnAvatar.setOnClickListener(v -> editAvatar());

        getBinding().btnAvatarWeb.setOnClickListener(view -> editAvatar());


        getBinding().btnAge.setOnClickListener(v -> {

            if (CheckDoubleClick.isFastClick()) return;
            clearPopupWindow();

            getBinding().vBackground.setVisibility(View.VISIBLE);
            popupWindow = new MemberProfileAgeEditorWindow(mContext);
            popupWindow.showAtLocation(parentView, Gravity.END | Gravity.BOTTOM, 0, 0);
            ((MemberProfileAgeEditorWindow) popupWindow).setOnCustomDismissListener(new OnCustomDismissListener() {
                @Override
                public void onStartDismiss(MsgEvent value) {
                 //   Log.d("@@@@@@@@@@", "onStartDismiss: ");
                    getBinding().vBackground.setVisibility(View.GONE);
                    if (value != null)
                        getBinding().tvAge.setText(String.valueOf(value.getObj()));
                }

                @Override
                public void onDismiss() {
                    popupWindow = null;
                }
            });
        });

        getBinding().btnHeight.setOnClickListener(v -> {

            if (CheckDoubleClick.isFastClick()) return;
            clearPopupWindow();

            getBinding().vBackground.setVisibility(View.VISIBLE);
            popupWindow = new MemberProfileHeightEditorWindow(mContext);
            popupWindow.showAtLocation(parentView, Gravity.END | Gravity.BOTTOM, 0, 0);
            ((MemberProfileHeightEditorWindow) popupWindow).setOnCustomDismissListener(new OnCustomDismissListener() {
                @Override
                public void onStartDismiss(MsgEvent value) {
                    getBinding().vBackground.setVisibility(View.GONE);

                    if (value != null) {
                        int height;
                        if (isImperial) {
                            height = (int) userProfileViewModel.getHeight_imperial();
                            getBinding().tvHeightMetricFt.setText(String.valueOf(totalIn2ft(height)));
                            getBinding().tvHeightMetricIn.setText(String.valueOf(totalIn2In(height)));
                        } else {
                            height = (int) userProfileViewModel.getHeight_metric();
                            getBinding().tvHeightMetricCm.setText(String.valueOf(height));
                        }
                    }
                }

                @Override
                public void onDismiss() {
                    popupWindow = null;
                }
            });
        });

        getBinding().btnAddTodayWeight.setOnClickListener(v -> {
            if (CheckDoubleClick.isFastClick()) return;
            clearPopupWindow();

            getBinding().vBackground.setVisibility(View.VISIBLE);
            popupWindow = new MemberProfileWeightEditorWindow(mContext);
//            memberProfileWeightEditorWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
//            memberProfileWeightEditorWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            popupWindow.showAtLocation(parentView, Gravity.END | Gravity.BOTTOM, 0, 0);
            ((MemberProfileWeightEditorWindow) popupWindow).setOnCustomDismissListener(new OnCustomDismissListener() {
                @Override
                public void onStartDismiss(MsgEvent value) {
                    getBinding().vBackground.setVisibility(View.GONE);

                    if (value != null) {
                        getBinding().tvWeight.setText(String.valueOf(value.getObj()));
                        webApiGetMyWeightTrend();
                    }
                }

                @Override
                public void onDismiss() {
                    popupWindow = null;
                }
            });
        });

        getBinding().scGender.setOnPositionChangedListener(new SegmentedButtonGroup.OnPositionChangedListener() {
            @Override
            public void onPositionChanged(int position) {
                userProfileViewModel.setUserGender(position);
//                SpiritDbManager.getInstance(getApp()).updateUserProfile(userProfileEntity, new DatabaseCallback<UserProfileEntity>() {
//                    @Override
//                    public void onUpdated() {
//                        super.onUpdated();
//                    }
//
//                    @Override
//                    public void onError(String err) {
//                        super.onError(err);
//                        Toast.makeText(getApp(), "Failure:" + err, Toast.LENGTH_LONG).show();
//                    }
//                });
            }
        });

    }

    @Override
    public void dismiss() {
        super.dismiss();
        //  clearData();
        clearPopupWindow();
    }

    private void clearPopupWindow() {
        if (popupWindow != null) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }


    /**
     * 體重趨勢
     */
    public void webApiGetMyWeightTrend() {

        if (userProfileViewModel.userType.get() == USER_TYPE_GUEST) return;

        getBinding().progressCirculars2.setVisibility(View.VISIBLE);

        BaseApi.request(BaseApi.createApi(IServiceApi.class).apiGetMyWeightTrend(""),
                new BaseApi.IResponseListener<GetMyWeightTrendBean>() {
                    @Override
                    public void onSuccess(GetMyWeightTrendBean data) {

                        Log.d("WEB_API", "webApiGetMyWeightTrend: " + data.toString());

                        try {
                            if (data.getSuccess()) {
                                setWeightData(data.getDataMap().getData());
                                Log.d("WEB_API", "onSuccess: " + data.getDataMap().getData());
                            } else {
                                Toasty.warning(getApp(),data.getErrorMessage(),Toasty.LENGTH_LONG).show();
                            }
                            getBinding().progressCirculars2.setVisibility(View.GONE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFail() {
                        try {
                            getBinding().progressCirculars2.setVisibility(View.GONE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    /**
     * USER 資料
     */
    public void webApiGetMyUserInfoFromMachine() {

        new CallWebApi(mContext).getUserInfo(data -> initData());

//        BaseApi.request(BaseApi.createApi(IServiceApi.class).apiGetMyUserInfoFromMachine(""),
//                new BaseApi.IResponseListener<MemberCheckinMachineByQRCodeBean>() {
//                    @Override
//                    public void onSuccess(MemberCheckinMachineByQRCodeBean data) {
//
//
//                        Log.d("WEB_API", "webApiGetMyUserInfoFromMachine: " + data.toString());
//
//                        try {
//                            if (data.getSuccess()) {
//
//                                ((MainActivity) mContext).setUserData(data.getDataMap().getData());
//
//                                initData();
//
//                                Log.d("WEB_API", "webApiGetMyUserInfoFromMachine: " + data.getDataMap().getData().toString());
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//
//                        //     getBinding().progressCirculars2.setVisibility(View.GONE);
//                    }
//
//                    @Override
//                    public void onFail() {
//                        //    getBinding().progressCirculars2.setVisibility(View.GONE);
//                    }
//                });
    }


    private void setProfileAvatar() {

        if (userProfileViewModel.getAvatarId() != null) {
            getBinding().btnAvatarWeb.setVisibility(View.GONE);
            getBinding().btnAvatar.setVisibility(View.VISIBLE);

            int avatarRes = CommonUtils.getAvatarSelectedFromTag(userProfileViewModel.getAvatarId(), false);
            Glide.with(getApp())
                    .load(avatarRes)
                    .centerInside()
//                    .diskCacheStrategy(DiskCacheStrategy.NONE)
//                    .skipMemoryCache(true)
                    .into(getBinding().btnAvatar);
        }
    }


}
