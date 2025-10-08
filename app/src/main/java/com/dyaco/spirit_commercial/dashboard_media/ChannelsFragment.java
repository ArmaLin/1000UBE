package com.dyaco.spirit_commercial.dashboard_media;

import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.corestar.libs.device.DeviceCab;
import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.FragmentChannelBinding;
import com.dyaco.spirit_commercial.support.base_component.BaseBindingFragment;
import com.dyaco.spirit_commercial.support.custom_view.DividerItemDecorator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChannelsFragment extends BaseBindingFragment<FragmentChannelBinding> {

    public ChannelsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Looper.myQueue().addIdleHandler(() -> {
            initRanking();
            initEvent();
            initController();
            return false;
        });
    }

    private void initController() {

        getBinding().btnNum0.setOnClickListener(view ->
                parent.sendCabCommand(DeviceCab.FUNCTIONS.DIGIT_0));

        getBinding().btnNum1.setOnClickListener(view ->
                parent.sendCabCommand(DeviceCab.FUNCTIONS.DIGIT_1));

        getBinding().btnNum2.setOnClickListener(view ->
                parent.sendCabCommand(DeviceCab.FUNCTIONS.DIGIT_2));

        getBinding().btnNum3.setOnClickListener(view ->
                parent.sendCabCommand(DeviceCab.FUNCTIONS.DIGIT_3));

        getBinding().btnNum4.setOnClickListener(view ->
                parent.sendCabCommand(DeviceCab.FUNCTIONS.DIGIT_4));

        getBinding().btnNum5.setOnClickListener(view ->
                parent.sendCabCommand(DeviceCab.FUNCTIONS.DIGIT_5));

        getBinding().btnNum6.setOnClickListener(view ->
                parent.sendCabCommand(DeviceCab.FUNCTIONS.DIGIT_6));

        getBinding().btnNum7.setOnClickListener(view ->
                parent.sendCabCommand(DeviceCab.FUNCTIONS.DIGIT_7));

        getBinding().btnNum8.setOnClickListener(view ->
                parent.sendCabCommand(DeviceCab.FUNCTIONS.DIGIT_8));

        getBinding().btnNum9.setOnClickListener(view ->
                parent.sendCabCommand(DeviceCab.FUNCTIONS.DIGIT_9));
    }

    private void initEvent() {
        getBinding().btnBack.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());

        getBinding().rgSelectChannel.setOnCheckedChangeListener((group, checkedId) -> {
            ArrayList<RadioButton> listOfRadioButtons = new ArrayList<>();
            for (int i = 0; i < getBinding().rgSelectChannel.getChildCount(); i++) {
                View o = getBinding().rgSelectChannel.getChildAt(i);
                if (o instanceof RadioButton) {
                    listOfRadioButtons.add((RadioButton) o);
                    if (listOfRadioButtons.get(i).getId() == checkedId) {

                    }
                }
            }
        });
    }

    List<ChannelBean> channelBeanList;
    ChannelsAdapter channelsAdapter;

    private void initRanking() {

        RecyclerView recyclerView = getBinding().recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        recyclerView.setHasFixedSize(true);

        DividerItemDecorator dividerItemDecoration = new DividerItemDecorator(Objects.requireNonNull(ContextCompat.getDrawable(requireActivity(), R.drawable.divider_line_1c242a)));

        recyclerView.addItemDecoration(dividerItemDecoration);
        channelsAdapter = new ChannelsAdapter(requireActivity());
        recyclerView.setAdapter(channelsAdapter);


        channelsAdapter.setOnItemClickListener(historyEntity -> {

        });


        channelBeanList = getChannelData();
        channelsAdapter.setData2View(channelBeanList);

        getBinding().progressCirculars.setVisibility(View.GONE);

    }

    private List<ChannelBean> getChannelData() {
        List<ChannelBean> list = new ArrayList<>();

        list.add(new ChannelBean("Comedy Central",127,"6:00 pm—6:30 pm","South Park",R.drawable.icon_tv_comedy));
        list.add(new ChannelBean("Fox Sports",127,"6:00 pm—6:30 pm","Boxing—Errol Spence Jr VS Someone Else",R.drawable.icon_tv_fox));
        list.add(new ChannelBean("CNN",127,"6:00 pm—6:30 pm","CNN Newsroom with Rosemary Church",R.drawable.icon_tv_cnn));
        list.add(new ChannelBean("WGN",127,"6:00 pm—6:30 pm","South Park",R.drawable.icon_tv_wgn));
        list.add(new ChannelBean("ABC News",127,"6:00 pm—6:30 pm","South Park",R.drawable.icon_tv_wgn));
        list.add(new ChannelBean("NBC Sports",127,"6:00 pm—6:30 pm","South Park",R.drawable.icon_tv_wgn));

        return list;
    }
}