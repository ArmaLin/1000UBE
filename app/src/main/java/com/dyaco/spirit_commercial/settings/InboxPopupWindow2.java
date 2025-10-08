package com.dyaco.spirit_commercial.settings;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.dyaco.spirit_commercial.model.webapi.CloudData.MESSAGE_CATEGORY_NEWS;
import static com.dyaco.spirit_commercial.model.webapi.CloudData.MESSAGE_CATEGORY_REGULATIONS;
import static com.dyaco.spirit_commercial.support.CommonUtils.getJson;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.SET_ALPHA_BACKGROUND;
import static com.dyaco.spirit_commercial.support.utils.MyAnimationUtils.crossFade;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

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
import com.dylanc.viewbinding.base.ViewBindingUtil;
import com.google.android.material.transition.platform.MaterialArcMotion;
import com.google.android.material.transition.platform.MaterialContainerTransform;
import com.jeremyliao.liveeventbus.LiveEventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class InboxPopupWindow2 extends PopupWindow {
    private DeviceSettingViewModel deviceSettingViewModel;
    InboxAdapter inboxAdapter;
    private Endless endless;
    int page = 1;
    int pageSize = 8;
    private View loadingView;
    private View v ;
    private Context mContext;

    public InboxPopupWindow2(Context context, DeviceSettingViewModel deviceSettingViewModel) {

            v = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.popup_inbox, null);

        setContentView(v);
        mContext = context;

        setWidth(795);
        setHeight(MATCH_PARENT);

        setWindowLayoutType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);

        this.deviceSettingViewModel = deviceSettingViewModel;
     //   getBinding().setDeviceSetting(deviceSettingViewModel);

        initView();
        initNotification();

        ((MainActivity) mContext).webApiGetUnreadMessageCountFromMachine();
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);


        //   webApiGetMessagesFromMachine();
    }

    private Button btnClose;
    private TextView btnBack;
    RecyclerView recyclerView;
    View scContentView;
    TextView tvInboxTitle;
    ViewGroup flRoot;
    private void initView() {
        btnClose = v.findViewById(R.id.btn_close);
        btnBack = v.findViewById(R.id.btnBack);
        btnClose.setOnClickListener(v -> dismiss());
        scContentView = v.findViewById(R.id.scContentView);
        tvInboxTitle = v.findViewById(R.id.tvInboxTitle);
        flRoot = v.findViewById(R.id.fl_root);
        recyclerView = v.findViewById(R.id.recycler_view);

        //返回訊息列表
        btnBack.setOnClickListener(v -> {
            crossFade(recyclerView, scContentView, 200);
//            scContentView.setVisibility(View.GONE);
//            recyclerView.setVisibility(View.VISIBLE);
        });
    }


    private void initNotification() {


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

//            getBinding().tvInboxTitle.setText(inboxBean.getTitle());
//            getBinding().tvInboxContent.setText("");
//            getBinding().tvInboxDate.setText("");
//            getBinding().tvInboxFrom.setText("");

            MaterialContainerTransform transform = new MaterialContainerTransform();
            transform.setStartView(view);
            transform.setEndView(tvInboxTitle);
            transform.setPathMotion(new MaterialArcMotion());
            transform.setScrimColor(Color.TRANSPARENT);
            transform.setDuration(3000);
            TransitionManager.beginDelayedTransition(flRoot, transform);

            recyclerView.setVisibility(View.GONE);
            scContentView.setVisibility(View.VISIBLE);


        //    crossFade(getBinding().scContentView, getBinding().recyclerView, 200);



        });


//        loadingView = View.inflate(mContext, R.layout.layout_loading, null);
//        loadingView.setVisibility(View.GONE);
//        endless = Endless.applyTo(recyclerView, loadingView);
//        endless.setLoadMoreListener(n -> webApiGetMessagesFromMachine());


        List<GetMessagesFromMachineBean.DataMapDTO.DataDTO> dataDTOList = new ArrayList<>();
        for (int i = 0; i < pageSize; i++) {
            GetMessagesFromMachineBean.DataMapDTO.DataDTO dataDTO = new GetMessagesFromMachineBean.DataMapDTO.DataDTO();
            dataDTO.setBody(ss + "XXXXXXX");
            dataDTO.setIsRead(true);
            dataDTO.setMessageId("efa176bb-8b36-41eb-8076-01c993dd9596");
            dataDTO.setPublishDate("3000/01/01");
            dataDTO.setTitle("TITLE");
            dataDTO.setMessageCategory(1);
            dataDTOList.add(dataDTO);
            ss += 1;
        }
        inboxAdapter.setData2View(dataDTOList);
    }

    int ss = 0;





}