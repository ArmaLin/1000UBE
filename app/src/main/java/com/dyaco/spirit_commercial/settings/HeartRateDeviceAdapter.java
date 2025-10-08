package com.dyaco.spirit_commercial.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.corestar.libs.device.DeviceGEM;
import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.ItemsHrDeviceListBinding;
import com.dyaco.spirit_commercial.databinding.ItemsNoDataBinding;

import java.util.List;

public class HeartRateDeviceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final LayoutInflater mLayoutInflater;
    private List<DeviceGEM.HeartRateDevice> channelBeanList;
    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_MESSAGE = 1;
    private static final int VIEW_TYPE_FOOTER = 2;


    private Context mContext;

    HeartRateDeviceAdapter(Context context) {
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);

    }

    @SuppressLint("NotifyDataSetChanged")
    void setData2View(List<DeviceGEM.HeartRateDevice> list) {
        channelBeanList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        if (viewType == VIEW_TYPE_MESSAGE) {
            viewHolder = new MyRecyclerViewHolder(ItemsHrDeviceListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        } else {
            viewHolder = new ViewHolderEmpty(ItemsNoDataBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {

        int viewType = getItemViewType(i);
        if (viewType == VIEW_TYPE_MESSAGE) {
            final MyRecyclerViewHolder viewHolder = (MyRecyclerViewHolder) holder;
            final int position = viewHolder.getAdapterPosition();
            final DeviceGEM.HeartRateDevice heartRateDevice = channelBeanList.get(position);

//            viewHolder.binding.clBase.setBackgroundColor(heartRateDevice.isConnected() ? ContextCompat.getColor(mContext, R.color.color201396ef) : ContextCompat.getColor(mContext, R.color.color1c242a));
            viewHolder.binding.clBase.setBackground(heartRateDevice.isConnected() ? ContextCompat.getDrawable(mContext, R.color.color201396ef) : ContextCompat.getDrawable(mContext, R.color.btn_1c242a_252e37));
            viewHolder.binding.tvHrTitle.setTextColor(heartRateDevice.isConnected() ? ContextCompat.getColorStateList(mContext, R.color.color1396ef) : ContextCompat.getColorStateList(mContext, R.color.white));

            viewHolder.binding.vConnectedUnderline.setVisibility(heartRateDevice.isConnected() ? View.VISIBLE : View.INVISIBLE);

            //  viewHolder.binding.tvHrTitle.setText(heartRateDevice.getName().replace("\u000E",""));
            viewHolder.binding.tvHrTitle.setText(heartRateDevice.getName());

            viewHolder.binding.tvHrNum.setText(heartRateDevice.getType() == DeviceGEM.HEART_RATE_TYPE.BLE ? String.valueOf(heartRateDevice.getAddress()) : String.valueOf(heartRateDevice.getId()));

            viewHolder.binding.tvHrDb.setText(heartRateDevice.getType().toString());

            viewHolder.binding.ivHrIcon.setBackgroundResource(heartRateDevice.isConnected() ? R.drawable.icon_hrm_pressed : R.drawable.icon_hrm_default);

            viewHolder.binding.progress.setVisibility(View.INVISIBLE);

            viewHolder.binding.clBase.setOnClickListener(v -> {

//                viewHolder.binding.clBase.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color252e37));
//                viewHolder.binding.ivHrIcon.setBackgroundResource(R.drawable.load_animation);
//                AnimationDrawable rocketAnimation = (AnimationDrawable) viewHolder.binding.ivHrIcon.getBackground();
//                rocketAnimation.start();

                onItemClickListener.onItemClick(heartRateDevice, viewHolder.binding.clBase, viewHolder.binding.ivHrIcon, viewHolder.binding.progress);
            });

        } else {
            final ViewHolderEmpty viewHolder = (ViewHolderEmpty) holder;
            //   viewHolder.binding.tvNodata.setText(R.string.NO_DATA);
            viewHolder.binding.tvNodata.setText("");
        }
    }


    @Override
    public int getItemCount() {
        if (channelBeanList == null || channelBeanList.size() <= 0) {
            return 1;
        } else {
            return channelBeanList.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = VIEW_TYPE_MESSAGE;

        if (channelBeanList == null || channelBeanList.size() <= 0) {
            viewType = VIEW_TYPE_EMPTY;
        }

        return viewType;
    }

    public static class MyRecyclerViewHolder extends RecyclerView.ViewHolder {
        private final ItemsHrDeviceListBinding binding;

        public MyRecyclerViewHolder(ItemsHrDeviceListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public static class ViewHolderEmpty extends RecyclerView.ViewHolder {
        private final ItemsNoDataBinding binding;

        ViewHolderEmpty(ItemsNoDataBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    /**
     * Item Interface Callback
     */
    public interface OnItemClickListener {
        void onItemClick(DeviceGEM.HeartRateDevice bean, View view1, View view2, ProgressBar progress);
    }

    private OnItemClickListener onItemClickListener;

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}
