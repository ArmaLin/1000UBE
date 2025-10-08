package com.dyaco.spirit_commercial.settings;

import static com.dyaco.spirit_commercial.model.webapi.CloudData.MESSAGE_CATEGORY_NEWS;
import static com.dyaco.spirit_commercial.model.webapi.CloudData.MESSAGE_CATEGORY_REGULATIONS;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.ItemsInboxListBinding;
import com.dyaco.spirit_commercial.databinding.ItemsNoDataBinding;
import com.dyaco.spirit_commercial.model.webapi.bean.GetMessagesFromMachineBean;

import java.util.List;

public class InboxAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<GetMessagesFromMachineBean.DataMapDTO.DataDTO> channelBeanList;
    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_MESSAGE = 1;
    private static final int VIEW_TYPE_FOOTER = 2;


    private final Context mContext;

    InboxAdapter(Context context) {
        this.mContext = context;
    }

    int pageSize = 8;
    int totalPage;

    void setData2View(List<GetMessagesFromMachineBean.DataMapDTO.DataDTO> list) {
        channelBeanList = list;
        // notifyDataSetChanged();
        notifyItemRangeInserted(totalPage, totalPage + pageSize);
        Log.d("EEEEFFEFE", "setData2View: " + totalPage + "," + (totalPage + pageSize));

        totalPage += pageSize;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        if (viewType == VIEW_TYPE_MESSAGE) {
            viewHolder = new MyRecyclerViewHolder(ItemsInboxListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
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
            final GetMessagesFromMachineBean.DataMapDTO.DataDTO inboxBean = channelBeanList.get(position);

//            viewHolder.binding.clBase.setBackgroundColor(inboxBean.getIsRead() ? ContextCompat.getColor(mContext, R.color.btn_252e37_323f4b) : ContextCompat.getColor(mContext, R.color.btn_1c242a_252e37));

            boolean isSelf = inboxBean.getIsRead() != null && inboxBean.getIsRead();

            viewHolder.binding.clBase.setBackground(isSelf ? ContextCompat.getDrawable(mContext, R.color.btn_1c242a_252e37) : ContextCompat.getDrawable(mContext, R.color.btn_252e37_323f4b));
            //  viewHolder.binding.clBase.setBackgroundColor(inboxBean.isRead() ? ContextCompat.getColor(mContext, R.color.color1c242a) : ContextCompat.getColor(mContext, R.color.color252e37));
            //  viewHolder.binding.clBase.setBackgroundTintList(inboxBean.isRead() ? ContextCompat.getColorStateList(mContext, R.color.color1c242a) : ContextCompat.getColorStateList(mContext, R.color.color252e37));
            //  viewHolder.binding.clBase.setForeground(inboxBean.isRead() ? ContextCompat.getDrawable(mContext, R.drawable.recycler_bg_1c242a) : ContextCompat.getDrawable(mContext,  R.drawable.recycler_bg_252e37));

            // 訊息類別 (0 = News, 1 = Regulations, 2 = Promos)
            viewHolder.binding.tvInboxTitle.setText(String.valueOf(inboxBean.getTitle()));
            viewHolder.binding.tvInboxContent.setText(String.valueOf(inboxBean.getBody()));
            viewHolder.binding.tvInboxDate.setText(String.valueOf(inboxBean.getPublishDate()));
            int icon;
            if (inboxBean.getMessageCategory() == MESSAGE_CATEGORY_NEWS) {
                icon = R.drawable.icon_inbox_new_0;
            } else if (inboxBean.getMessageCategory() == MESSAGE_CATEGORY_REGULATIONS) {
                icon = R.drawable.icon_inbox_regulations_1;
            } else {
                icon = R.drawable.icon_inbox_promo_2;
            }

            viewHolder.binding.ivInboxIcon.setBackgroundResource(icon);
            viewHolder.binding.clBase.setOnClickListener(v ->
                    onItemClickListener.onItemClick(viewHolder.binding.tvInboxTitle, inboxBean, position)
            );



        } else {
            final ViewHolderEmpty viewHolder = (ViewHolderEmpty) holder;
            viewHolder.binding.tvNodata.setText(R.string.NO_MESSAGE);
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
        private final ItemsInboxListBinding binding;

        public MyRecyclerViewHolder(ItemsInboxListBinding binding) {
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
        void onItemClick(View view, GetMessagesFromMachineBean.DataMapDTO.DataDTO bean, int position);
    }

    private OnItemClickListener onItemClickListener;

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void updateItem(int position) {
        notifyItemChanged(position);
    }

    /**
     * notifyItemChanged(int)
     * notifyItemInserted(int)
     * notifyItemRemoved(int)
     * notifyItemRangeChanged(int, int)
     * notifyItemRangeInserted(int, int)
     * notifyItemRangeRemoved(int, int)
     */

}
