package com.dyaco.spirit_commercial.settings;

import static com.dyaco.spirit_commercial.App.getApp;
import static com.dyaco.spirit_commercial.model.webapi.CloudData.MESSAGE_CATEGORY_NEWS;
import static com.dyaco.spirit_commercial.model.webapi.CloudData.MESSAGE_CATEGORY_REGULATIONS;
import static com.dyaco.spirit_commercial.support.CommonUtils.getJson;
import static com.dyaco.spirit_commercial.support.utils.MyAnimationUtils.crossFade;

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.PopupInboxBinding;
import com.dyaco.spirit_commercial.model.webapi.BaseApi;
import com.dyaco.spirit_commercial.model.webapi.IServiceApi;
import com.dyaco.spirit_commercial.model.webapi.bean.GetMessageFromMachineBean;
import com.dyaco.spirit_commercial.model.webapi.bean.GetMessagesFromMachineBean;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.custom_view.DividerItemDecorator;
import com.dyaco.spirit_commercial.support.custom_view.RecyclerViewCornerRadius;
import com.dyaco.spirit_commercial.support.endless.Endless;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class InboxPopupWindow extends BasePopupWindow<PopupInboxBinding> {
    private DeviceSettingViewModel deviceSettingViewModel;
    InboxAdapter inboxAdapter;
    private Endless endless;
    int page = 1;
    int pageSize = 8;
    private View loadingView;

    public InboxPopupWindow(Context context, DeviceSettingViewModel deviceSettingViewModel) {
        super(context, 500, 0, 795, GENERAL.TRANSLATION_X, false, true, true, true);
        this.deviceSettingViewModel = deviceSettingViewModel;
        getBinding().setDeviceSetting(deviceSettingViewModel);
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);

        initView();
        initNotification();

        ((MainActivity) mContext).webApiGetUnreadMessageCountFromMachine();

        //   webApiGetMessagesFromMachine();
    }

    private void initView() {

        getBinding().btnClose.setOnClickListener(v -> dismiss());

        //返回訊息列表
        getBinding().btnBack.setOnClickListener(v -> {
            crossFade(getBinding().recyclerView, getBinding().scContentView, 200);
//            getBinding().scContentView.setVisibility(View.GONE);
//            getBinding().recyclerView.setVisibility(View.VISIBLE);
        });
    }


    private void initNotification() {


        RecyclerView recyclerView = getBinding().recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setHasFixedSize(true);

        RecyclerViewCornerRadius radiusItemDecoration = new RecyclerViewCornerRadius(recyclerView);
        radiusItemDecoration.setCornerRadius(0, 0, 30, 0);
        //   radiusItemDecoration.setCornerRadius(30);
        recyclerView.addItemDecoration(radiusItemDecoration);

        DividerItemDecorator dividerItemDecoration = new DividerItemDecorator(Objects.requireNonNull(ContextCompat.getDrawable(mContext, R.drawable.divider_line_323f4b)));
        recyclerView.addItemDecoration(dividerItemDecoration);

        inboxAdapter = new InboxAdapter(mContext);
        recyclerView.setAdapter(inboxAdapter);

        //    ViewGroup container = v.findViewById(R.id.fl_root);
//       // TextView t = v.findViewById(R.id.tvInboxTitle);


        inboxAdapter.setOnItemClickListener((view, inboxBean, position) -> {

            getBinding().tvInboxTitle.setText("");
            getBinding().tvInboxContent.setText("");
            getBinding().tvInboxDate.setText("");
            getBinding().tvInboxFrom.setText("");

//            MaterialContainerTransform transform = new MaterialContainerTransform();
//            transform.setStartView(view);
//            transform.setEndView(getBinding().tvInboxTitle);
//            transform.setPathMotion(new MaterialArcMotion());
//            transform.setScrimColor(Color.TRANSPARENT);
//            transform.setDuration(3000);
//            TransitionManager.beginDelayedTransition(getBinding().flRoot, transform);
//
//            getBinding().recyclerView.setVisibility(View.GONE);
//            getBinding().scContentView.setVisibility(View.VISIBLE);


            crossFade(getBinding().scContentView, getBinding().recyclerView, 200);


            webApiGetMessageFromMachine(inboxBean.getMessageId(), position);
        });


        loadingView = View.inflate(mContext, R.layout.layout_loading, null);
        loadingView.setVisibility(View.GONE);
        endless = Endless.applyTo(recyclerView, loadingView);
        endless.setLoadMoreListener(n -> webApiGetMessagesFromMachine());


//        List<GetMessagesFromMachineBean.DataMapDTO.DataDTO> dataDTOList = new ArrayList<>();
//        for (int i = 0; i < pageSize; i++) {
//            GetMessagesFromMachineBean.DataMapDTO.DataDTO dataDTO = new GetMessagesFromMachineBean.DataMapDTO.DataDTO();
//            dataDTO.setBody(ss + "XXXXXXX");
//            dataDTO.setIsRead(true);
//            dataDTO.setMessageId("efa176bb-8b36-41eb-8076-01c993dd9596");
//            dataDTO.setPublishDate("3000/01/01");
//            dataDTO.setTitle("TITLE");
//            dataDTO.setMessageCategory(1);
//            dataDTOList.add(dataDTO);
//            ss += 1;
//        }
//        inboxAdapter.setData2View(dataDTOList);
    }


    int ss = 0;
    List<GetMessagesFromMachineBean.DataMapDTO.DataDTO> allMessageList = new ArrayList<>();

    /**
     * 全部清單
     */
    public void webApiGetMessagesFromMachine() {

        Map<String, Object> map = new HashMap<>();
        map.put("page", page); // 1開始
        map.put("pageSize", pageSize);

        BaseApi.request(BaseApi.createApi(IServiceApi.class).apiGetMessagesFromMachine(getJson(map)),
                new BaseApi.IResponseListener<GetMessagesFromMachineBean>() {
                    @Override
                    public void onSuccess(GetMessagesFromMachineBean data) {

                        //分頁
                        try {

                            loadingView.setVisibility(View.GONE);
                            getBinding().progressCirculars.setVisibility(View.GONE);

                            if (data.getSuccess()) {
                                //***test data
//                        List<GetMessagesFromMachineBean.DataMapDTO.DataDTO> dataDTOList = new ArrayList<>();
//                        for (int i = 0; i < pageSize; i++) {
//                            GetMessagesFromMachineBean.DataMapDTO.DataDTO dataDTO = new GetMessagesFromMachineBean.DataMapDTO.DataDTO();
//                            dataDTO.setBody(ss + "XXXXXXX");
//                            dataDTO.setIsRead(true);
//                            dataDTO.setMessageId("efa176bb-8b36-41eb-8076-01c993dd9596");
//                            dataDTO.setPublishDate("3000/01/01");
//                            dataDTO.setTitle("TITLE");
//                            dataDTO.setMessageCategory(1);
//                            dataDTOList.add(dataDTO);
//                            ss += 1;
//                        }
                                //***

                                if (data.getDataMap() != null && data.getDataMap().getData() != null && data.getDataMap().getData().size() > 0) {
                                    List<GetMessagesFromMachineBean.DataMapDTO.DataDTO> dataDTOList = data.getDataMap().getData();


                                    allMessageList.addAll(dataDTOList);
                                    inboxAdapter.setData2View(allMessageList);

                                    Log.d("WEB_API", "GetMessagesFromMachineBean: " + allMessageList.toString());
                                    page += 1;
                                    endless.loadMoreComplete();

                                    loadingView.setVisibility(View.VISIBLE);
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFail() {
                        loadingView.setVisibility(View.GONE);
                        getBinding().progressCirculars.setVisibility(View.GONE);
                        loadingView.setVisibility(View.GONE);
                    }
                });
    }

    /**
     * 單筆
     */
    public void webApiGetMessageFromMachine(String id, int position) {
        getBinding().progressCirculars.setVisibility(View.VISIBLE);
        Map<String, Object> map = new HashMap<>();
        //    map.put("messageId", "efa176bb-8b36-41eb-8076-01c993dd9596");
        map.put("messageId", id);

        BaseApi.request(BaseApi.createApi(IServiceApi.class).apiGetMessageFromMachine(getJson(map)),
                new BaseApi.IResponseListener<GetMessageFromMachineBean>() {
                    @Override
                    public void onSuccess(GetMessageFromMachineBean data) {
                        try {
                            getBinding().progressCirculars.setVisibility(View.GONE);
                            if (data.getSuccess()) {
                                GetMessageFromMachineBean.DataMapDTO.DataDTO dataDTO = data.getDataMap().getData();
                                setMessage(dataDTO, position);
                            } else {
                                Toasty.warning(getApp(), data.getErrorMessage(), Toasty.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFail() {
                        getBinding().progressCirculars.setVisibility(View.GONE);
                    }
                });
    }


    private void setMessage(GetMessageFromMachineBean.DataMapDTO.DataDTO dataDTO, int position) {

        getBinding().tvInboxTitle.setText(dataDTO.getTitle());
        getBinding().tvInboxContent.setText(dataDTO.getBody());
        getBinding().tvInboxDate.setText(dataDTO.getPublishDate());

        String category;
        int color;
        if (dataDTO.getMessageCategory() == MESSAGE_CATEGORY_NEWS) {
            category = mContext.getString(R.string.News);
            color = R.color.color1396ef;
        } else if (dataDTO.getMessageCategory() == MESSAGE_CATEGORY_REGULATIONS) {
            category = mContext.getString(R.string.Regulations);
            color = R.color.color0DAC87;
        } else {
            category = mContext.getString(R.string.Promo);
            color = R.color.colorCd5bff;
        }

        getBinding().tvInboxFrom.setText(category);
        getBinding().tvInboxFrom.setTextColor(ContextCompat.getColorStateList(mContext, color));
        ((MainActivity) mContext).webApiGetUnreadMessageCountFromMachine();

        allMessageList.get(position).setIsRead(true);
        inboxAdapter.updateItem(position);
    }
}