package com.dyaco.spirit_commercial.dashboard_training;

import static com.dyaco.spirit_commercial.App.UNIT_E;
import static com.dyaco.spirit_commercial.App.getApp;
import static com.dyaco.spirit_commercial.App.getDeviceGEM;
import static com.dyaco.spirit_commercial.App.getDeviceSpiritC;
import static com.dyaco.spirit_commercial.MainActivity.isHomeScreen;
import static com.dyaco.spirit_commercial.MainActivity.isSummary;
import static com.dyaco.spirit_commercial.MainActivity.isUs;
import static com.dyaco.spirit_commercial.MainActivity.userProfileViewModel;
import static com.dyaco.spirit_commercial.egym.EgymUtil.EGYM_MACHINE_TYPE;
import static com.dyaco.spirit_commercial.login.LoginFragment.isGuestQuickStart;
import static com.dyaco.spirit_commercial.support.CommonUtils.iExc;
import static com.dyaco.spirit_commercial.support.CommonUtils.ignoringExc;
import static com.dyaco.spirit_commercial.support.CommonUtils.showException;
import static com.dyaco.spirit_commercial.support.FormulaUtil.kg2lbPure;
import static com.dyaco.spirit_commercial.support.FormulaUtil.lb2kgPure;
import static com.dyaco.spirit_commercial.support.intdef.AppStatusIntDef.CURRENT_PAGE_MEDIA;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.CONSOLE_SYSTEM_EGYM;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.METRIC;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.EVENT_CLOSE_SUMMARY;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.EVENT_SET_UNIT;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.LOG_IN_EVENT;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.USER_TYPE_EGYM;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.USER_TYPE_GUEST;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.USER_TYPE_NORMAL;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.getMaxWeight;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.getMinWeight;

import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.corestar.libs.device.DeviceGEM;
import com.corestar.libs.ota.LwrMcuUpdateManager;
import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.FragmentDashboardTrainingBinding;
import com.dyaco.spirit_commercial.egym.EgymBannerAdapter;
import com.dyaco.spirit_commercial.egym.EgymUtil;
import com.dyaco.spirit_commercial.model.webapi.BaseApi;
import com.dyaco.spirit_commercial.model.webapi.EgymWebListener;
import com.dyaco.spirit_commercial.model.webapi.IServiceApi;
import com.dyaco.spirit_commercial.model.webapi.bean.EgymTrainingPlans;
import com.dyaco.spirit_commercial.model.webapi.bean.GetGymMonthlyRankingFromMachineBean;
import com.dyaco.spirit_commercial.model.webapi.bean.GymSort;
import com.dyaco.spirit_commercial.model.webapi.bean.JoinGymRankingBean;
import com.dyaco.spirit_commercial.support.CommonUtils;
import com.bumptech.glide.Glide;
import com.dyaco.spirit_commercial.support.RxTimer;
import com.dyaco.spirit_commercial.support.SafeClickListener;
import com.dyaco.spirit_commercial.support.base_component.BaseBindingFragment;
import com.dyaco.spirit_commercial.support.interaction.TenTapClick;
import com.dyaco.spirit_commercial.support.custom_view.DividerItemDecorator;
import com.dyaco.spirit_commercial.support.custom_view.WavyTextView;
import com.dyaco.spirit_commercial.support.custom_view.banner.Banner;
import com.dyaco.spirit_commercial.support.custom_view.banner.indicator.CircleIndicator;
import com.dyaco.spirit_commercial.support.custom_view.banner.listener.OnPageChangeListener;
import com.dyaco.spirit_commercial.support.custom_view.banner.util.BannerUtils;
import com.dyaco.spirit_commercial.support.intdef.AppStatusIntDef;
import com.dyaco.spirit_commercial.support.intdef.DeviceIntDef;
import com.dyaco.spirit_commercial.support.intdef.WorkoutIntDef;
import com.dyaco.spirit_commercial.support.utils.CheckDoubleClick;
import com.dyaco.spirit_commercial.viewmodel.AppStatusViewModel;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingViewModel;
import com.dyaco.spirit_commercial.viewmodel.EgymDataViewModel;
import com.dyaco.spirit_commercial.viewmodel.UserProfileViewModel;
import com.dyaco.spirit_commercial.viewmodel.WorkoutViewModel;
import com.dyaco.spirit_commercial.workout.programs.ProgramsEnum;
import com.google.gson.Gson;
import com.jeremyliao.liveeventbus.LiveEventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import es.dmoral.toasty.Toasty;


public class DashboardTrainingFragment extends BaseBindingFragment<FragmentDashboardTrainingBinding> implements View.OnClickListener {

    int programType = WorkoutIntDef.DEFAULT_PROGRAM;

    private WorkoutViewModel workoutViewModel;
    AppStatusViewModel appStatusViewModel;
    private DeviceSettingViewModel deviceSettingViewModel;
    private EgymDataViewModel egymDataViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //   setRetainInstance(true);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        workoutViewModel = new ViewModelProvider(requireActivity()).get(WorkoutViewModel.class);
        appStatusViewModel = new ViewModelProvider(requireActivity()).get(AppStatusViewModel.class);
        deviceSettingViewModel = new ViewModelProvider(requireActivity()).get(DeviceSettingViewModel.class);
        if(!isSummary) appStatusViewModel.currentStatus.set(AppStatusIntDef.STATUS_IDLE);

        egymDataViewModel = new ViewModelProvider(requireActivity()).get(EgymDataViewModel.class);

        if (!isHomeScreen) {
            getBinding().tvEgymText.setWaveType(WavyTextView.WAVE_TYPE_COLOR);
            getBinding().tvEgymText.startWaveOnce(1500,500);
        }

        isHomeScreen = true;
        if (isAdded()) {
            ((MainActivity) requireActivity()).startHandler();
        }



        LiveEventBus.get(LOG_IN_EVENT).observe(getViewLifecycleOwner(), s -> {
            ((MainActivity) requireActivity()).getBinding().bgE.setVisibility(View.GONE);
            Log.d(EgymUtil.TAG, "已登入，開始上傳資料: ");
            final String requestBody = new Gson().toJson(egymDataViewModel.createWorkoutParam);

            EgymUtil.getInstance().apiCreateWorkouts(requestBody, new EgymWebListener() {
                @Override
                public void onSuccess(String result) {
                    Log.d(EgymUtil.TAG, "上傳成功: ");
                }

                @Override
                public void onFail() {
                    //上傳失敗,存到資料庫
                    Log.d(EgymUtil.TAG, "上傳失敗,存到資料庫: ");
                    EgymUtil.getInstance().insertEgym(requestBody);
                }
            });

            initView();

            LiveEventBus.get(EVENT_CLOSE_SUMMARY).post(true);
        });


        //todo APPLE WATCH
        //不管有沒有連FTMS都下這個指令，workout結束後 才能再讓apple watch 連
//        getDeviceGEM().gymConnectMessageSetMachineStatus(DeviceGEM.FITNESS_EQUIPMENT_STATE.IDLE, new HashMap<>());
//        getDeviceGEM().gymConnectMessageSetTrainingStatus(DeviceGEM.TRAINING_STATUS.IDLE);
        getDeviceGEM().gymConnectMessageSetFitnessEquipmentState(DeviceGEM.FITNESS_EQUIPMENT_STATE.IDLE, new HashMap<>());

        getBinding().setWorkoutViewModel(workoutViewModel);
        getBinding().setUserProfileViewModel(userProfileViewModel);
        getBinding().setDeviceSetting(deviceSettingViewModel);


        initEvent();
        //    try {
//            Runtime.getRuntime().exec("input tap 1200 200"); //fatburn
        //      Runtime.getRuntime().exec("input tap 1200 400"); // cardio
        //    } catch (IOException e) {
        //    }

        if (userProfileViewModel == null) {
            userProfileViewModel = new ViewModelProvider(this).get(UserProfileViewModel.class);
        }

        initView();


        if (isUs) {

            int bg = R.drawable.bg_guest_924;

            if (deviceSettingViewModel.consoleSystem.get() == CONSOLE_SYSTEM_EGYM) {
                bg = R.drawable.pt_gym_rankings_egym;
            }

            Glide.with(getApp())
                    .load(bg)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(R.color.black)
                    .skipMemoryCache(true)
                    .into(getBinding().ivLeftBg);

            TenTapClick.setTenClickListener(getBinding().tvGuestTitle, () -> {
                ((MainActivity) requireActivity()).internetNotifyWarringWindow(false);
                ((MainActivity) requireActivity()).goMaintenanceMode();
            });

            TenTapClick.setTenClickListener(getBinding().goAdRom, () -> {
                LwrMcuUpdateManager lwrMcuUpdateManager = getDeviceSpiritC().getLwrMcuUpdateManager();
                lwrMcuUpdateManager.connect();  // 回應connect之後, 才可以下runAprom
                new RxTimer().timer(500, n -> {
                    lwrMcuUpdateManager.runAprom(); // 若是在ap rom, 則會回timeout
                    Toasty.success(requireActivity(), "AP ROM !!!!!!", Toasty.LENGTH_LONG).show();
                });
            });

        }


        //     CommonUtils.getSleepTime();

        //   testGboard();

    }

    EgymBannerAdapter egymBannerAdapter;

    private void initEgymView(EgymTrainingPlans data) {

        if (data == null || data.getTrainer() == null || data.getTrainer().isEmpty()) {

            getBinding().viewEgymNoPlan.setVisibility(View.VISIBLE);
            getBinding().viewEgym.setVisibility(View.GONE);

            getBinding().tvEgymNoPlanText.setText(getString(R.string.you_don_t_have_any_planned_treadmill_activities, EGYM_MACHINE_TYPE));

            getBinding().btnBrowsePrograms.setOnClickListener(new SafeClickListener() {
                @Override
                public void onSafeClick(View v) {
                    if (CheckDoubleClick.isFastClick()) return;
                    if (appStatusViewModel.currentPage.get() == CURRENT_PAGE_MEDIA) return;
                    try {
                        Navigation.findNavController(v).navigate(DashboardTrainingFragmentDirections.actionDashboardTrainingFragmentToProgramsFragment(WorkoutIntDef.DEFAULT_PROGRAM));
                    } catch (Exception e) {
                        showException(e);
                    }
                }
            });
            ((MainActivity) requireActivity()).isEgymNoData = true;

        } else {
            CircleIndicator indicator;
            List<EgymTrainingPlans.TrainerDTO> programInfoList = data.getTrainer();

            ((MainActivity) requireActivity()).isEgymNoData = false;
            getBinding().viewEgym.setVisibility(View.VISIBLE);
            getBinding().viewEgymNoPlan.setVisibility(View.GONE);

            String planCount = String.valueOf(data.getTrainer().size());

            getBinding().tvPlanText.setText(getString(R.string.you_have_3_planned_treadmill_activities, planCount, EGYM_MACHINE_TYPE));
            Banner banner = getBinding().programsBanner;
            banner.setBounceEffectEnabled(true);
            indicator = getBinding().indicator;
            //app:banner_infinite_loop="false" 無限循環

            egymBannerAdapter = new EgymBannerAdapter(requireActivity(), programInfoList, workoutViewModel, deviceSettingViewModel, egymDataViewModel);
//            banner.setStartPosition(1);
//           banner.setStartPosition((egymDataViewModel.currentPlanNum.get() + 1)); // 無限循環要 +1
            banner.setStartPosition(egymDataViewModel.currentPlanNum.get());
            banner.setAdapter(egymBannerAdapter);
            banner.isAutoLoop(false);
            banner.setBannerGalleryEffect(24, 0, 1f);

            indicator.setVisibility(View.VISIBLE);
            banner.setIndicator(indicator, false);
            banner.setIndicatorSelectedWidth((int) BannerUtils.dp2px(12));
            banner.setIndicatorWidth((int) BannerUtils.dp2px(12), (int) BannerUtils.dp2px(12));
            banner.setIndicatorHeight((int) BannerUtils.dp2px(12));
            banner.setIndicatorNormalColorRes(R.color.color323f4b);
            banner.setIndicatorSelectedColorRes(R.color.color1396ef);
            banner.setIndicatorSpace((int) BannerUtils.dp2px(24));
            banner.setIndicatorRadius(0);

            banner.addOnPageChangeListener(new OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    egymDataViewModel.currentPlanNum.set(position);
                }

                @Override
                public void onPageSelected(int position) {

                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });


            egymBannerAdapter.setOnImageClickListener((trainerDTO) -> {
                try {
                    Navigation.findNavController(DashboardTrainingFragment.this.requireView()).navigate(DashboardTrainingFragmentDirections.actionDashboardTrainingFragmentToEgymPlanFragment());
                } catch (Exception e) {
                    showException(e);
                }
            });
        }


        //  apiUserDetails();

    }


    private void callApiJoinGymRanking() {
        if (CheckDoubleClick.isFastClick()) return;
        if (appStatusViewModel.currentPage.get() == CURRENT_PAGE_MEDIA) return;

        getBinding().progressCirculars.setVisibility(View.VISIBLE);

        BaseApi.request(BaseApi.createApi(IServiceApi.class).apiJoinGymRanking(""),
                new BaseApi.IResponseListener<JoinGymRankingBean>() {
                    @Override
                    public void onSuccess(JoinGymRankingBean data) {
                        iExc(() -> {
                            getBinding().progressCirculars.setVisibility(View.GONE);
                            if (data.getSuccess()) {
                                if (data.getDataMap() == null) return;
                                userProfileViewModel.setJoinedMonthlyRanking(true);
                                getBinding().viewNormal.setVisibility(View.VISIBLE);
                                getBinding().viewNoGymRankings.setVisibility(View.GONE);
                                initRanking();
                                webApiGetGymMonthlyRankingFromMachine();

                                Toasty.success(requireActivity(), "", Toasty.LENGTH_SHORT).show();

                            } else {
                                Toasty.warning(requireActivity(), data.getErrorMessage(), Toasty.LENGTH_LONG).show();
                            }
                        });
                    }

                    @Override
                    public void onFail() {

                        iExc(() -> getBinding().progressCirculars.setVisibility(View.GONE));
                        //    BaseApi.clearDispose();
                    }
                });
    }

//    private void webApiGetUserData() {
//        BaseApi.request(BaseApi.createApi(IServiceApi.class).apiGetMyUserInfoFromMachine(""),
//                new BaseApi.IResponseListener<GetMyUserInfoFromMachineBean>() {
//                    @Override
//                    public void onSuccess(GetMyUserInfoFromMachineBean data) {
//                        if (data.getSuccess()) {
//                            if (data.getDataMap() == null) return;
//                            GetMyUserInfoFromMachineBean.DataMapDTO.DataDTO dataDTO = data.getDataMap().getData();
//                            int gender = dataDTO.getGender().equals("M") ? MALE : FEMALE;
//                            String name = dataDTO.getFirstName() + " " + dataDTO.getLastName();
//                            int age = dataDTO.getAge();
//                            int unit = dataDTO.getMeasurementUnit(); //公制0,英制1
//                            UNIT_E = unit == 0 ? METRIC : IMPERIAL;
//
//                            int weight = Integer.parseInt(dataDTO.getWeight());
//                            int height = Integer.parseInt(dataDTO.getHeight());
//
//                            int weightImperial = unit == DeviceIntDef.IMPERIAL ? weight : kg2lb(weight);
//                            int weightMetric = unit == METRIC ? weight : FormulaUtil.lb2kg(weight);
//                            int heightImperial = unit == DeviceIntDef.IMPERIAL ? height : FormulaUtil.in2cm(height);
//                            int heightMetric = unit == METRIC ? height : FormulaUtil.cm2in(height);
//
////                            userProfileEntity.setUnit(unit);
////                            userProfileEntity.setGender(gender);
////                            userProfileEntity.setUserName(name);
////                            userProfileEntity.setAge(age);
////                            userProfileEntity.setWeight_imperial(weightImperial);
////                            userProfileEntity.setWeight_metric(weightMetric);
////                            userProfileEntity.setHeight_imperial(heightImperial);
////                            userProfileEntity.setHeight_metric(heightMetric);
////
////                            App.getApp().setUserProfile(userProfileEntity);
//                            //     new CommonUtils().mmkvUserProfileToViewModel(userProfileViewModel, userProfileEntity);
//
//                        } else {
//                            Toasty.warning(requireActivity(), data.getErrorMessage(), Toasty.LENGTH_LONG).show();
//                        }
//                    }
//
//                    @Override
//                    public void onFail() {
//                        //    BaseApi.clearDispose();
//                    }
//                });
//    }

    private void initGuestView() {

        if (userProfileViewModel.userType.get() != USER_TYPE_GUEST) return;

        initGuestWeight();

        new CommonUtils().addAutoClick(getBinding().btnAgePlus);
        new CommonUtils().addAutoClick(getBinding().btnAgeMinus);
        new CommonUtils().addAutoClick(getBinding().btnWeightPlus);
        new CommonUtils().addAutoClick(getBinding().btnWeightMinus);

        getBinding().rulerView1.setSelectedValue(userProfileViewModel.getUserAge());

        //年紀
        getBinding().rulerView1.setOnValueChangeListener((view, value) -> {
            if (getBinding() == null) return;
            getBinding().tvAge.setText(CommonUtils.subZeroAndDot(String.valueOf(value)));
            userProfileViewModel.setUserAge((int) value);
        });

        getBinding().btnAgePlus.setOnClickListener(v ->
                getBinding().rulerView1.setSelectedValue(Float.parseFloat(getBinding().tvAge.getText().toString()) + 1));
        getBinding().btnAgeMinus.setOnClickListener(v ->
                getBinding().rulerView1.setSelectedValue(Float.parseFloat(getBinding().tvAge.getText().toString()) - 1));


        //體重
        getBinding().rulerView2.setOnValueChangeListener((view, value) -> {
            if (getBinding() == null) return;
            getBinding().tvWeight.setText(CommonUtils.subZeroAndDot(String.valueOf(value)));

            if (!isEvent) {
                userProfileViewModel.setWeight_imperial(UNIT_E == DeviceIntDef.IMPERIAL ? value : kg2lbPure(value));
                userProfileViewModel.setWeight_metric(UNIT_E == METRIC ? value : lb2kgPure(value));
            }

            isEvent = false;
        });

        getBinding().btnWeightPlus.setOnClickListener(v ->
                getBinding().rulerView2.setSelectedValue(Float.parseFloat(getBinding().tvWeight.getText().toString()) + 1));
        getBinding().btnWeightMinus.setOnClickListener(v ->
                getBinding().rulerView2.setSelectedValue(Float.parseFloat(getBinding().tvWeight.getText().toString()) - 1));


        LiveEventBus.get(EVENT_SET_UNIT).observe(getViewLifecycleOwner(), s ->
                initGuestWeight());


        //在Media頁面 結束Workout
//        initGuestData();


        initGuestData();
//        LiveEventBus.get(INIT_GUEST_DATA).observe(getViewLifecycleOwner(), s -> {
//            //在Media頁面 結束Workout
//            initGuestData();
//        });
    }

    private void initGuestData() {
        if (userProfileViewModel.userType.get() != USER_TYPE_GUEST) return;
        if (getBinding() == null) return;

        getBinding().rulerView1.setSelectedValue(userProfileViewModel.getUserAge());
        getBinding().rulerView2.setSelectedValue((int) (UNIT_E == DeviceIntDef.IMPERIAL ? userProfileViewModel.getWeight_imperial() : userProfileViewModel.getWeight_metric()));
    }

    boolean isEvent = true;

    private void initGuestWeight() {
        getBinding().rulerView2.setMaxValue(getMaxWeight());
        getBinding().rulerView2.setMinValue(getMinWeight());
//        getBinding().rulerView2.setSelectedValue((int) (UNIT_E == DeviceIntDef.IMPERIAL ? userProfileViewModel.getWeight_imperial() : userProfileViewModel.getWeight_metric()));
        getBinding().rulerView2.setSelectedValue((int) (UNIT_E == DeviceIntDef.IMPERIAL ? userProfileViewModel.getWeight_imperial() : userProfileViewModel.getWeight_metric()));

        isEvent = true;
    }

    RankingAdapter1 rankingAdapter1;

    private void initRanking() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);
        getBinding().recyclerView.setLayoutManager(linearLayoutManager);
        getBinding().recyclerView.setHasFixedSize(true);
//        dividerItemDecoration = new DividerItemDecoration(requireActivity(), DividerItemDecoration.VERTICAL);
        // dividerItemDecoration.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(requireActivity(), R.drawable.divider_line)));
        DividerItemDecorator dividerItemDecoration = new DividerItemDecorator(Objects.requireNonNull(ContextCompat.getDrawable(requireActivity(), R.drawable.divider_line_5a7085)));
        getBinding().recyclerView.addItemDecoration(dividerItemDecoration);
        rankingAdapter1 = new RankingAdapter1(requireActivity());
        getBinding().recyclerView.setAdapter(rankingAdapter1);


        rankingAdapter1.setOnItemClickListener(historyEntity -> {

        });
    }

    private List<RankingBean> getRankingData() {
        List<RankingBean> list = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            list.add(new RankingBean("Member" + i, i, 10 + i, 10 + i, 10 + i, 10 + i, 0, i == 2));
        }

        return list;
    }


    private void initEvent() {
        getBinding().btnStart.setOnClickListener(v -> {
            if (!isAdded()) return;
            if (appStatusViewModel.currentPage.get() == CURRENT_PAGE_MEDIA) return;
            ((MainActivity) requireActivity()).quickStart(false);
        });

        getBinding().btnAllProgram.setOnClickListener(v -> {
            if (CheckDoubleClick.isFastClick()) return;
            if (appStatusViewModel.currentPage.get() == CURRENT_PAGE_MEDIA) return;
            try {
                Navigation.findNavController(v).navigate(DashboardTrainingFragmentDirections.actionDashboardTrainingFragmentToProgramsFragment(WorkoutIntDef.DEFAULT_PROGRAM));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        getBinding().btnAllRank.setOnClickListener(v -> {
            if (CheckDoubleClick.isFastClick()) return;
            if (appStatusViewModel.currentPage.get() == CURRENT_PAGE_MEDIA) return;
            try {
                Navigation.findNavController(v).navigate(DashboardTrainingFragmentDirections.actionDashboardTrainingFragmentToAllRankFragment());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        getBinding().btnSetTime.setOnClickListener(v -> {
            if (CheckDoubleClick.isFastClick()) return;
            if (appStatusViewModel.currentPage.get() == CURRENT_PAGE_MEDIA) return;
            try {
                Navigation.findNavController(v).navigate(DashboardTrainingFragmentDirections.actionDashboardTrainingFragmentToSetTimeFragment(ProgramsEnum.MANUAL));
            } catch (Exception e) {
                e.printStackTrace();
            }
//            SetTimeWindow setTimeWindow = new SetTimeWindow(requireActivity());
//            setTimeWindow.showAtLocation(requireActivity().getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);
        });

        getBinding().btnFatburn.setOnClickListener(this);
        getBinding().btnCardio.setOnClickListener(this);
        getBinding().btnHill.setOnClickListener(this);
        getBinding().btnHIIT.setOnClickListener(this);
    }


    private void initView() {
        switch (userProfileViewModel.userType.get()) {
            case USER_TYPE_NORMAL:
                isGuestQuickStart = false;
                getBinding().viewEgymNoPlan.setVisibility(View.GONE);
                getBinding().viewEgym.setVisibility(View.GONE);
                if (userProfileViewModel.isJoinedMonthlyRanking()) {
                    getBinding().viewNormal.setVisibility(View.VISIBLE);
                    getBinding().viewNoGymRankings.setVisibility(View.GONE);
                    initRanking();
                    webApiGetGymMonthlyRankingFromMachine();
                } else {
                    //false > 加入Gym Ranking
                    getBinding().viewNormal.setVisibility(View.GONE);
                    getBinding().viewNoGymRankings.setVisibility(View.VISIBLE);
                    getBinding().btnJoinGymRanking.setOnClickListener(view1 -> callApiJoinGymRanking());
                }
                break;
            case USER_TYPE_EGYM:

//                egymDataViewModel = new ViewModelProvider(this).get(EgymDataViewModel.class);

                getBinding().viewNormal.setVisibility(View.GONE);
                getBinding().viewNoGymRankings.setVisibility(View.GONE);
                getBinding().viewEgymNoPlan.setVisibility(View.GONE);
                getBinding().viewEgym.setVisibility(View.GONE);

                getBinding().egymProgress.setVisibility(View.VISIBLE);

                egymDataViewModel.egymTrainingPlansData.observe(getViewLifecycleOwner(), egymTrainingPlans -> {
                    //      LogS.printJson("EGYMMMMM", new Gson().toJson(egymTrainingPlans), "GetTrainingPlans");

             //       Log.d("EgymUtil", "⭐️⭐️⭐️⭐️initView: ");
                    initEgymView(egymTrainingPlans);

                    getBinding().egymProgress.setVisibility(View.GONE);

                });


                //放到MainDashboardTrainingFragment ,以免離開此頁就無法監聽
//                LiveEventBus.get(WIFI_WORK, Boolean.class).observe(getViewLifecycleOwner(), s -> {
//                    if (isNetworkAvailable(getApp())) {
//                        Log.d("###EGYMMMMM", "有網路,判斷egym: ");
//                        new EgymUtil().getEgymDataList();
//
//                        if (isNoData) {
//                            Log.d("###EGYMMMMM", "重新取得 Plan資料: ");
//                            new EgymUtil((MainActivity) requireActivity(), deviceSettingViewModel, egymDataViewModel).apiGetTrainingPlans();
//                        }
//                    }
//                });


                break;
            case USER_TYPE_GUEST:

           //     Log.d("EgymUtil", "⭐️⭐️⭐️⭐️GUEST: ");
                getBinding().viewEgymNoPlan.setVisibility(View.GONE);
                getBinding().viewEgym.setVisibility(View.GONE);
                getBinding().viewNormal.setVisibility(View.GONE);
                getBinding().viewNoGymRankings.setVisibility(View.GONE);

                initGuestView();

                Looper.myQueue().addIdleHandler(() -> {
                    initGuestData();

                    if (isGuestQuickStart) {
                        if (getBinding() != null) {
                            getBinding().btnStart.callOnClick();
                        }
                    }

                    ((MainActivity) requireActivity()).getBinding().ibSetting.setClickable(true);

                    return false;
                });
                break;
        }
    }


    @Override
    public void onClick(View v) {
        //cannot be found from the current destination Destination
        if (CheckDoubleClick.isFastClick()) return;

        if (appStatusViewModel.currentPage.get() == CURRENT_PAGE_MEDIA) return;
        try {
            NavDirections navDirections = DashboardTrainingFragmentDirections.actionDashboardTrainingFragmentToProgramsBannerFragment(v.getId(), programType);
            Navigation.findNavController(v).navigate(navDirections);
        } catch (Exception e) {
            e.printStackTrace();
        }


//        Resources res = getResources();
//        Configuration conf = res.getConfiguration();
//        conf.setLocale(Locale.JAPAN);
//        Context context = requireActivity().createConfigurationContext(conf);
//        Resources resources = context.getResources();
//        getBinding().btnStart.setText(resources.getString(R.string.Start));


//        Configuration config = getResources().getConfiguration();
//        config.locale = Locale.JAPAN;
//        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
//
//        deviceSettingViewModel.locale.set(LanguageEnum.JAPAN.getLocale());
    }

    @Override
    public void onResume() {
        super.onResume();

        //  new RxTimer().timer(500,number -> {
        initGuestData();
        //   });

        MainActivity.isOnStopBackToMainTraining = false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        rankingAdapter1 = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //  compositeDisposable.dispose();
    }

    /**
     * 取得健身房月排名 - 首頁
     */
    public void webApiGetGymMonthlyRankingFromMachine() {

        if (userProfileViewModel.userType.get() == USER_TYPE_GUEST) return;

        getBinding().progressCirculars.setVisibility(View.VISIBLE);

        BaseApi.request(BaseApi.createApi(IServiceApi.class).apiGetGymMonthlyRankingFromMachine(""),
                new BaseApi.IResponseListener<GetGymMonthlyRankingFromMachineBean>() {
                    @Override
                    public void onSuccess(GetGymMonthlyRankingFromMachineBean data) {


                        ignoringExc("@@WEB_API", () -> {
                            getBinding().progressCirculars.setVisibility(View.GONE);
                            //     getBinding().placeholderView.setVisibility(View.GONE);

                            if (data.getSuccess()) {

                                data.getDataMap().getData().sort(new GymSort());

                                rankingAdapter1.setData2View(data.getDataMap().getData());
                                getBinding().recyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(requireActivity(), R.anim.layout_animation_fade_in));
                                getBinding().recyclerView.scheduleLayoutAnimation();

                            } else {
                                Toasty.warning(getApp(), data.getErrorMessage(), Toasty.LENGTH_LONG).show();
                            }


                        });


                    }

                    @Override
                    public void onFail() {
                        ignoringExc("onFail:GetGymMonthlyRankingFromMachineBean", () -> {
                            getBinding().progressCirculars.setVisibility(View.GONE);
                            //     getBinding().placeholderView.setVisibility(View.GONE);
                        });
                    }
                });

    }


//    private void testGboard() {
//
//        //設定Gboard為預設輸入法
//        String oldDefaultKeyboard = Settings.Secure.getString(requireActivity().getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD);
////        Settings.Secure.putString(requireActivity().getContentResolver(), Settings.Secure.ENABLED_INPUT_METHODS, "com.google.android.inputmethod.latin/.full.path");
////        Settings.Secure.putString(requireActivity().getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD, "com.google.android.inputmethod.latin/.full.path");
//        if (!oldDefaultKeyboard.equals("com.google.android.inputmethod.latin/com.android.inputmethod.latin.LatinIME")) {
//            Settings.Secure.putString(requireActivity().getContentResolver(), Settings.Secure.ENABLED_INPUT_METHODS, "com.google.android.inputmethod.latin/com.android.inputmethod.latin.LatinIME");
//            Settings.Secure.putString(requireActivity().getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD, "com.google.android.inputmethod.latin/com.android.inputmethod.latin.LatinIME");
//            Log.d("OOOOOOOOEEEEE", "22222onViewCreated: " + oldDefaultKeyboard);
//
//            //# ime set com.google.android.inputmethod.latin/com.android.inputmethod.latin.LatinIME
////            InputMethodManager imeManager = (InputMethodManager) requireActivity().getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
////            imeManager.showInputMethodPicker();
//        }
//
//
//    //    GboardLanguageAdder.addKeyboardLanguage(requireActivity(),"zh-CN");
//
//    }
}