package com.dyaco.spirit_commercial.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.corestar.libs.audio.AudioDeviceWatcher;
import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.ItemsBtDeviceListBinding;
import com.dyaco.spirit_commercial.databinding.ItemsNoDataBinding;

import java.util.List;

public class BtAudioDeviceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<AudioDeviceWatcher.AudioDevice> channelBeanList;
    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_MESSAGE = 1;
    private static final int VIEW_TYPE_FOOTER = 2;


    private final Context mContext;

    BtAudioDeviceAdapter(Context context) {
        this.mContext = context;

    }

    void setData2View(List<AudioDeviceWatcher.AudioDevice> bluetoothDevicesList) {
        channelBeanList = bluetoothDevicesList;
        notifyItemInserted(channelBeanList.size());
//        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    void setData2ViewALL(List<AudioDeviceWatcher.AudioDevice> bluetoothDevicesList) {
        channelBeanList = bluetoothDevicesList;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        if (viewType == VIEW_TYPE_MESSAGE) {
            viewHolder = new MyRecyclerViewHolder(ItemsBtDeviceListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        } else {
            viewHolder = new ViewHolderEmpty(ItemsNoDataBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }
        return viewHolder;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {

        int viewType = getItemViewType(i);
        if (viewType == VIEW_TYPE_MESSAGE) {
            final MyRecyclerViewHolder viewHolder = (MyRecyclerViewHolder) holder;
            final int position = viewHolder.getAdapterPosition();
            final AudioDeviceWatcher.AudioDevice bluetoothDevice = channelBeanList.get(position);

            viewHolder.binding.clBase.setBackground(bluetoothDevice.isConnected() ? ContextCompat.getDrawable(mContext, R.color.color201396ef) : ContextCompat.getDrawable(mContext, R.color.btn_1c242a_252e37));
            viewHolder.binding.tvBtTitle.setTextColor(bluetoothDevice.isConnected() ? ContextCompat.getColorStateList(mContext, R.color.color1396ef) : ContextCompat.getColorStateList(mContext, R.color.white));

            viewHolder.binding.vConnectedUnderline.setVisibility(bluetoothDevice.isConnected() ? View.VISIBLE : View.INVISIBLE);

//            if (bluetoothDevice.isBonded() && !bluetoothDevice.isConnected()) {
//                viewHolder.binding.tvBtTitle.setText(String.format("%s (%s)", bluetoothDevice.getDevice().getName(), mContext.getString(R.string.paired)));
//            } else {
//                viewHolder.binding.tvBtTitle.setText(bluetoothDevice.getDevice().getName());
//            }

            viewHolder.binding.tvBtTitle.setText(bluetoothDevice.getDevice().getName());

            viewHolder.binding.tvBtStatus.setText(bluetoothDevice.isBonded() && !bluetoothDevice.isConnected() ? "(" +mContext.getString(R.string.paired) +")" : "");


            viewHolder.binding.ivBtIcon.setBackgroundResource(bluetoothDevice.isConnected() ? R.drawable.icon_bluetooth_pressed : R.drawable.icon_bluetooth_default);

            viewHolder.binding.progress.setVisibility(View.INVISIBLE);

            viewHolder.binding.clBase.setOnClickListener(v -> {
                onItemClickListener.onItemClick(bluetoothDevice, viewHolder.binding);
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

        if (channelBeanList == null || channelBeanList.size() == 0) {
            viewType = VIEW_TYPE_EMPTY;
        }

        return viewType;
    }

    public static class MyRecyclerViewHolder extends RecyclerView.ViewHolder {
        private final ItemsBtDeviceListBinding binding;

        public MyRecyclerViewHolder(ItemsBtDeviceListBinding binding) {
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
        void onItemClick(AudioDeviceWatcher.AudioDevice bean, ItemsBtDeviceListBinding binding);
    }

    private OnItemClickListener onItemClickListener;

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}
