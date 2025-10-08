package com.dyaco.spirit_commercial.dashboard_training;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.ItemsNoDataBinding;
import com.dyaco.spirit_commercial.databinding.ItemsRank1ListBinding;
import com.dyaco.spirit_commercial.model.webapi.bean.GetGymMonthlyRankingFromMachineBean;
import com.dyaco.spirit_commercial.settings.AvatarEntity;

import java.util.List;

public class RankingAdapter1 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<GetGymMonthlyRankingFromMachineBean.DataMapDTO.DataDTO> rankingBeanList;
    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_MESSAGE = 1;
    private static final int VIEW_TYPE_FOOTER = 2;


    private Context mContext;

    RankingAdapter1(Context context) {
        this.mContext = context;
    }

    @SuppressLint("NotifyDataSetChanged")
    void setData2View(List<GetGymMonthlyRankingFromMachineBean.DataMapDTO.DataDTO> list) {
        rankingBeanList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        if (viewType == VIEW_TYPE_MESSAGE) {
            viewHolder = new MyRecyclerViewHolder(ItemsRank1ListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
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
            final GetGymMonthlyRankingFromMachineBean.DataMapDTO.DataDTO rankingBean = rankingBeanList.get(position);
            boolean isSelf = rankingBean.getIsSelf() != null && rankingBean.getIsSelf();
            if (isSelf) {
                viewHolder.binding.clBase.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color201396ef));
                viewHolder.binding.tvNo.setTextColor(ContextCompat.getColorStateList(mContext, R.color.color1396ef));
                viewHolder.binding.tvMemberName.setTextColor(ContextCompat.getColorStateList(mContext, R.color.color1396ef));
                viewHolder.binding.tvTotalHours.setTextColor(ContextCompat.getColorStateList(mContext, R.color.color1396ef));
                viewHolder.binding.vMeUnderline.setVisibility(View.VISIBLE);
            } else {
                viewHolder.binding.vMeUnderline.setVisibility(View.INVISIBLE);
            }

            viewHolder.binding.tvNo.setText(String.valueOf(rankingBean.getRanking()));
            viewHolder.binding.tvMemberName.setText(rankingBean.getDisplayName());
            viewHolder.binding.tvTotalHours.setText(String.valueOf(rankingBean.getPerformance()));

        } else {
            final ViewHolderEmpty viewHolder = (ViewHolderEmpty) holder;
            viewHolder.binding.tvNodata.setText("");
        }
    }

    @Override
    public int getItemCount() {
        if (rankingBeanList == null || rankingBeanList.size() <= 0) {
            return 1;
        } else {
            return rankingBeanList.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = VIEW_TYPE_MESSAGE;

        if (rankingBeanList == null || rankingBeanList.size() <= 0) {
            viewType = VIEW_TYPE_EMPTY;
        }

        return viewType;
    }

    public static class MyRecyclerViewHolder extends RecyclerView.ViewHolder {
        private final ItemsRank1ListBinding binding;

        public MyRecyclerViewHolder(ItemsRank1ListBinding binding) {
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
