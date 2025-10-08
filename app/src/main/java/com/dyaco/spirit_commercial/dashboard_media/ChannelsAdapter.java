package com.dyaco.spirit_commercial.dashboard_media;

import static com.dyaco.spirit_commercial.App.getApp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.ItemsChannelListBinding;
import com.dyaco.spirit_commercial.databinding.ItemsNoDataBinding;
import com.dyaco.spirit_commercial.settings.AvatarEntity;

import java.util.List;

public class ChannelsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final LayoutInflater mLayoutInflater;
    private List<ChannelBean> channelBeanList;
    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_MESSAGE = 1;
    private static final int VIEW_TYPE_FOOTER = 2;


    private Context mContext;

    ChannelsAdapter(Context context) {
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);

    }

    @SuppressLint("NotifyDataSetChanged")
    void setData2View(List<ChannelBean> list) {
        channelBeanList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        if (viewType == VIEW_TYPE_MESSAGE) {
            viewHolder = new MyRecyclerViewHolder(ItemsChannelListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
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
            final ChannelBean channelBean = channelBeanList.get(position);

            viewHolder.binding.tvChannelNumber.setText(String.valueOf(channelBean.getChannelNumber()));
            viewHolder.binding.tvChannelTitle.setText(String.valueOf(channelBean.getChannelName()));
            viewHolder.binding.tvChannelTime.setText(String.valueOf(channelBean.getChannelTime()));
            viewHolder.binding.tvChannelContent.setText(String.valueOf(channelBean.getChannelContent()));
            Glide.with(getApp())
                    .load(channelBean.getChannelIcon())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    //  .placeholder(R.drawable.insert_custom_diagram)
                    .into(viewHolder.binding.ivChannelIcon);

        } else {
            final ViewHolderEmpty viewHolder = (ViewHolderEmpty) holder;
            viewHolder.binding.tvNodata.setText(R.string.NO_DATA);
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
        private final ItemsChannelListBinding binding;

        public MyRecyclerViewHolder(ItemsChannelListBinding binding) {
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
        void onItemClick(AvatarEntity bean);
    }

    private OnItemClickListener onItemClickListener;

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}
