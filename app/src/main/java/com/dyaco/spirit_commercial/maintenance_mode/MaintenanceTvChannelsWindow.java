package com.dyaco.spirit_commercial.maintenance_mode;

import static com.dyaco.spirit_commercial.App.getApp;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.GET_TV_CHANNEL_LIST;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.TV_SENT_CHANNEL;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.TV_TUNER_SIGNAL_DIGITAL;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.corestar.libs.tvtuner.Channel;
import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.FragmentMaintenanceTvChannelsBinding;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.custom_view.DividerItemDecorator;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;
import com.dyaco.spirit_commercial.support.utils.CheckDoubleClick;
import com.jeremyliao.liveeventbus.LiveEventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

/**
 * Channel列表
 */
public class MaintenanceTvChannelsWindow extends BasePopupWindow<FragmentMaintenanceTvChannelsBinding> {
    private final Context mContext;

    public MaintenanceTvChannelsWindow(Context context) {
        super(context, 0, 0, 480, GENERAL.FADE, false, false, true, false);
        mContext = context;

        initRecyclerView();

        //   getChannelData(mChannels == null ? new Channel[30] : mChannels);

        //#TV TUNER 取得掃描後的頻道清單
        if (getApp().getDeviceSettingBean().getTvTunerSignal() == TV_TUNER_SIGNAL_DIGITAL) {
            //數位
            ((MainActivity) mContext).deviceTvTuner.getDTVChannelListExtension();
        } else {
            //類比
            ((MainActivity) mContext).deviceTvTuner.getChannelList();
        }


        // TODO: 沒頻道 取清單  >>  DeviceTvTuner bad checksum


        LiveEventBus.get(GET_TV_CHANNEL_LIST, Channel[].class).observeForever(observer);

        LiveEventBus.get(TV_SENT_CHANNEL, Integer.class).observeForever(observer2);
    }

    Observer<Channel[]> observer = this::getChannelData;

    Observer<Integer> observer2 = this::setChannel;

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
    }

    TvScanAdapter tvScanAdapter;

    private void initRecyclerView() {

        RecyclerView recyclerView = getBinding().recyclerview;
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setHasFixedSize(true);

        DividerItemDecorator dividerItemDecoration = new DividerItemDecorator(Objects.requireNonNull(ContextCompat.getDrawable(mContext, R.drawable.divider_line_323f4b)));
        recyclerView.addItemDecoration(dividerItemDecoration);
        tvScanAdapter = new TvScanAdapter(mContext);
        recyclerView.setAdapter(tvScanAdapter);

        tvScanAdapter.setOnItemClickListener(new TvScanAdapter.OnItemClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onItemClick(Channel bean, int position) {

                if (CheckDoubleClick.isFastClick()) return;

                ((MainActivity) mContext).deviceTvTuner.setChannel(position + 1);

                tvScanAdapter.setThisPosition(position);
                tvScanAdapter.notifyDataSetChanged();

            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setChannel(int i) {
        tvScanAdapter.setThisPosition(i);
        tvScanAdapter.notifyDataSetChanged();
    }


    private void getChannelData(Channel[] channels) {

//        for (int i = 0; i < channels.length; i++) {
//            channels[i] = new Channel(i, i, "CHANNEL" + i, true);
//        }

        getBinding().progressCirculars.setVisibility(View.GONE);

        tvScanAdapter.setData2View(new ArrayList<>(Arrays.asList(channels)));


//        new CountryRepo().getData(0, new RepoCallback<CountryBean>() {
//            @Override
//            public void onSuccess(List<CountryBean> dataList) {
//                if (tvScanAdapter != null)
//                    tvScanAdapter.setData2View(dataList);
//                //   getBinding().progressBar.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onFail(String error) {
//                //    getBinding().progressBar.setVisibility(View.GONE);
//                Log.d("ERROR", "onFail: " + error);
//            }
//        });
    }

    @Override
    public void dismiss() {
        super.dismiss();
        LiveEventBus.get(GET_TV_CHANNEL_LIST, Channel[].class).removeObserver(observer);
        LiveEventBus.get(TV_SENT_CHANNEL, Integer.class).removeObserver(observer2);
    }

    public void hideB(boolean isShow) {
        getBinding().getRoot().setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
    }
}
