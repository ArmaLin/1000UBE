package com.dyaco.spirit_commercial.dashboard_training;

import static com.dyaco.spirit_commercial.App.getApp;
import static com.dyaco.spirit_commercial.MainActivity.isTreadmill;
import static com.dyaco.spirit_commercial.support.CommonUtils.getJson;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.EVENT_SET_UNIT;

import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.FragmentAllRankBinding;
import com.dyaco.spirit_commercial.model.webapi.BaseApi;
import com.dyaco.spirit_commercial.model.webapi.IServiceApi;
import com.dyaco.spirit_commercial.model.webapi.bean.GetGymMonthlyAllRankingBean;
import com.dyaco.spirit_commercial.support.base_component.BaseBindingFragment;
import com.dyaco.spirit_commercial.support.custom_view.DividerItemDecorator;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingViewModel;
import com.jeremyliao.liveeventbus.LiveEventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class AllRankFragment extends BaseBindingFragment<FragmentAllRankBinding> {

    private RecyclerView recyclerView;

    public AllRankFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        DeviceSettingViewModel deviceSettingViewModel = new ViewModelProvider(requireActivity()).get(DeviceSettingViewModel.class);
        getBinding().setDeviceSetting(deviceSettingViewModel);

        initView();
        Looper.myQueue().addIdleHandler(() -> {
            initEvent();
            initRanking();
            webApiGetGymMonthlyAllRanking();

            return false;
        });


    }

    private void initEvent() {

        //app:popExitAnim="@android:anim/fade_out"
        getBinding().btnBack.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());

        getBinding().rgFilter.setOnCheckedChangeListener((group, checkedId) -> {
            ArrayList<RadioButton> listOfRadioButtons = new ArrayList<>();
            for (int i = 0; i < getBinding().rgFilter.getChildCount(); i++) {
                View o = getBinding().rgFilter.getChildAt(i);
                if (o instanceof RadioButton) {
                    listOfRadioButtons.add((RadioButton) o);
                    if (listOfRadioButtons.get(i).getId() == checkedId) {
                        filterChange(Integer.parseInt((String) listOfRadioButtons.get(i).getTag()));
                    }
                }
            }
        });

        getBinding().btnSpeed.setOnClickListener(v -> {

        });


        LiveEventBus.get(EVENT_SET_UNIT).observe(getViewLifecycleOwner(), s -> {

            if (list != null && list.size() > 0) {
                rankingAdapterAll.setData2View(list, highLightTag);
            }

        });


    }

    RankingAdapterAll rankingAdapterAll;
//    private Endless endless;
//    private View loadingView;

    private void initRanking() {

        //    loadingView = View.inflate(requireActivity(), R.layout.layout_loading, null);

        recyclerView = getBinding().recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);
        DividerItemDecorator dividerItemDecoration = new DividerItemDecorator(Objects.requireNonNull(ContextCompat.getDrawable(requireActivity(), R.drawable.divider_line_5a7085)));
        recyclerView.addItemDecoration(dividerItemDecoration);
        rankingAdapterAll = new RankingAdapterAll(requireActivity(), highLightTag);
        recyclerView.setAdapter(rankingAdapterAll);


//        rankingAdapterAll.setOnItemClickListener(historyEntity -> {
//
//        });


//        endless = Endless.applyTo(recyclerView, loadingView);
//        endless.setLoadMoreListener(page -> {
//
//            new RxTimer().timer(1000, number -> {
//                rankingBeanList.addAll(getRankingData(false));
//                rankingAdapterAll.setData2View(rankingBeanList, highLightTag);
//            });
//
//
//        });


//        new RankRepo().getData(1, new RepoCallback<RankEntity>() {
//            @Override
//            public void onSuccess(List<RankEntity> rankEntityList) {
//                rankingBeanList = rankEntityList;
//                rankingAdapterAll.setData2View(rankEntityList, highLightTag);
//                loadingView.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onFail(String error) {
//
//            }
//        });

    }

  //  int j = 0;

//    private List<RankEntity> getRankingData(boolean ii) {
//        List<RankEntity> list = new ArrayList<>();
//
//        for (int i = 0; i < 20; i++) {
//            //  list.add(new RankingBean("Member" + j, j, 10 + i, 10 + i, 10 + i,R.drawable.avatar_female_1_default));
//            list.add(new RankEntity("Member" + j, j, 10 + i, 10 + i, 10 + i, 10 + i, drawableToByteArray(R.drawable.avatar_female_1_default),
//                    ii && (i == 2)));
//            j++;
//        }
//        // endless.loadMoreComplete();
//        return list;
//    }


    private void initView() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy", Locale.getDefault());
        getBinding().tvRankTileDate.setText(sdf.format(Calendar.getInstance().getTimeInMillis()));
    }


    private int highLightTag = 0;

    private void filterChange(int tag) {
        this.highLightTag = tag;
        int color13963f = ContextCompat.getColor(requireActivity(), R.color.color1396ef);
        int colorAdb8c2 = ContextCompat.getColor(requireActivity(), R.color.colorADB8C2);
        int color1, color2, color3, color4;
        switch (tag) {
            case 0:
                color1 = color13963f;
                color2 = colorAdb8c2;
                color3 = colorAdb8c2;
                color4 = colorAdb8c2;
                break;
            case 1:
                color1 = colorAdb8c2;
                color2 = color13963f;
                color3 = colorAdb8c2;
                color4 = colorAdb8c2;
                break;
            case 2:
                color1 = colorAdb8c2;
                color2 = colorAdb8c2;
                color3 = color13963f;
                color4 = colorAdb8c2;
                break;
            case 3:
                color1 = colorAdb8c2;
                color2 = colorAdb8c2;
                color3 = colorAdb8c2;
                color4 = color13963f;
                break;
            default:
                color1 = 0;
                color2 = 0;
                color3 = 0;
                color4 = 0;

        }
        getBinding().btnHours.setTextColor(color1);
        getBinding().btnSpeed.setTextColor(color2);
        getBinding().btnDistance.setTextColor(color3);
        getBinding().btnCal.setTextColor(color4);

        webApiGetGymMonthlyAllRanking();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        SpiritDbManager.getInstance(requireActivity()).clear();
    }


    List<GetGymMonthlyAllRankingBean.DataMapDTO.DataDTO> list;

    boolean isFirst = true;
    /**
     * 取得健身房月排名 - 所有的會員排名
     */
    public void webApiGetGymMonthlyAllRanking() {

        //排名評比項目，accept values: (0:time, 1:speed, 2:distance, 3:calories, 4:power)
        getBinding().progressCirculars.setVisibility(View.VISIBLE);

        int rankingType = highLightTag;

        //BIKE 變成 POWER
        if (!isTreadmill && highLightTag == 2) {
            rankingType = 4;
        }

        Map<String, Object> map = new HashMap<>();
        map.put("rankingType", rankingType);
    //    Log.d("WEBBBB", "##################rankingType: " + rankingType);
        BaseApi.request(BaseApi.createApi(IServiceApi.class).apiGetGymMonthlyAllRanking(getJson(map)),
                new BaseApi.IResponseListener<GetGymMonthlyAllRankingBean>() {
                    @Override
                    public void onSuccess(GetGymMonthlyAllRankingBean data) {

                        try {
                            getBinding().progressCirculars.setVisibility(View.GONE);

                            if (data.getSuccess()) {
                                Log.d("WEBBBB", "onSuccess: " + data.getDataMap().getData().toString());
                                list = data.getDataMap().getData();
//                                list.add(list.get(2));
//                                list.add(list.get(3));
//                                list.add(list.get(3));
//                                list.add(list.get(3));
//                                list.add(list.get(3));
//                                list.add(list.get(3));
//                                list.add(list.get(3));
//                                list.add(list.get(3));
                                rankingAdapterAll.setData2View(list, highLightTag);

                            //    Log.d("WEB_API", "@@@@@@@onSuccess: " + list.toString());

                                if (isFirst) {
                                    recyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(requireActivity(), R.anim.layout_animation_left)) ;
                                    recyclerView.scheduleLayoutAnimation();
                                    isFirst = false;

//                                    for (GetGymMonthlyAllRankingBean.DataMapDTO.DataDTO b : list) {
//                                        if (b.isSelf()) {
//                                            if (b.getAvatarId() != null) {
//                                                userProfileViewModel.setAvatarId(b.getAvatarId());
//                                                userProfileViewModel.setPhotoFileUrl(null);
//                                            } else {
//                                                userProfileViewModel.setPhotoFileUrl(b.getPhotoFileUrl());
//                                                userProfileViewModel.setAvatarId(null);
//                                            }
//                                            ((MainActivity) requireActivity()).setAvatar(false);
//                                            return;
//                                        }
//                                    }
                                }
                            } else {
                                Toasty.warning(getApp(), data.getErrorMessage(), Toasty.LENGTH_LONG).show();
                            }
                         //   Log.d("WEB_API", data.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFail() {
                        try {
                            getBinding().progressCirculars.setVisibility(View.GONE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

    }
}