package com.dyaco.spirit_commercial.maintenance_mode;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.corestar.libs.tvtuner.Channel;
import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.ItemsNoDataBinding;
import com.dyaco.spirit_commercial.databinding.ItemsTvScanListBinding;

import java.util.List;

public class TvScanAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Channel> channelList;
    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_MESSAGE = 1;
    private static final int VIEW_TYPE_FOOTER = 2;


    private Context mContext;

    TvScanAdapter(Context context) {
        this.mContext = context;
    }

    @SuppressLint("NotifyDataSetChanged")
    void setData2View(List<Channel> list) {
        channelList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        if (viewType == VIEW_TYPE_MESSAGE) {
            viewHolder = new MyRecyclerViewHolder(ItemsTvScanListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
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
            final Channel channel = channelList.get(position);
            if (channel.isVisible()) {
//                viewHolder.binding.tvChannelNo.setText(String.valueOf(channel.getMainChannel()));
                viewHolder.binding.tvChannelNo.setText(String.valueOf(channel.getChannelNumber()));
                viewHolder.binding.tvChannelName.setText(channel.getStation());
                viewHolder.binding.clBase.setOnClickListener(view ->
                        onItemClickListener.onItemClick(channel, position));


                if (position == getThisPosition()) {
                    viewHolder.binding.tvChannelNo.setTextColor(ContextCompat.getColorStateList(mContext, R.color.colore24b44));
                    viewHolder.binding.tvChannelName.setTextColor(ContextCompat.getColorStateList(mContext, R.color.colore24b44));
                } else {
                    viewHolder.binding.tvChannelNo.setTextColor(ContextCompat.getColorStateList(mContext, R.color.white));
                    viewHolder.binding.tvChannelName.setTextColor(ContextCompat.getColorStateList(mContext, R.color.white));
                }

            }

        } else {
            final ViewHolderEmpty viewHolder = (ViewHolderEmpty) holder;
            viewHolder.binding.tvNodata.setText(R.string.NO_DATA);
        }
    }

    @Override
    public int getItemCount() {
        if (channelList == null || channelList.size() <= 0) {
            return 1;
        } else {
            return channelList.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = VIEW_TYPE_MESSAGE;

        if (channelList == null || channelList.size() <= 0) {
            viewType = VIEW_TYPE_EMPTY;
        }

        return viewType;
    }

    public static class MyRecyclerViewHolder extends RecyclerView.ViewHolder {
        private final ItemsTvScanListBinding binding;

        public MyRecyclerViewHolder(ItemsTvScanListBinding binding) {
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
        void onItemClick(Channel bean, int position);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    private int thisPosition = 0;

    public int getThisPosition() {
        return thisPosition;
    }

    public void setThisPosition(int thisPosition) {
        this.thisPosition = thisPosition;
    }

}
