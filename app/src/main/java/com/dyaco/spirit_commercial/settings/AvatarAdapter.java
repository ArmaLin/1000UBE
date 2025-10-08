package com.dyaco.spirit_commercial.settings;

import static com.dyaco.spirit_commercial.MainActivity.userProfileViewModel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dyaco.spirit_commercial.databinding.ItemsAvatarListBinding;

import java.util.List;

public class AvatarAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<AvatarBean> favoritesPojoList;
    private static final int VIEW_TYPE_MESSAGE = 1;

    GridLayoutManager gridLayoutManager;

    private Context mContext;

    AvatarAdapter(Context context, GridLayoutManager gridLayoutManager) {
        this.mContext = context;
        this.gridLayoutManager = gridLayoutManager;

        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (favoritesPojoList == null || favoritesPojoList.size() == 0) {
                    return 4; // the item in position now takes up 4 spans
                }
                return 1;
            }
        });
    }

    void setData2View(List<AvatarBean> list) {
        favoritesPojoList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyRecyclerViewHolder(ItemsAvatarListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {

        int viewType = getItemViewType(i);
        if (viewType == VIEW_TYPE_MESSAGE) {
            final MyRecyclerViewHolder viewHolder = (MyRecyclerViewHolder) holder;
            final int position = viewHolder.getAdapterPosition();
            final AvatarBean avatarBean = favoritesPojoList.get(position);

            viewHolder.binding.avatar.setBackgroundResource(avatarBean.getAvatarId());

            viewHolder.binding.avatar.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(avatarBean);
                }
            });
            viewHolder.binding.avatar.setChecked(avatarBean.getAvatarTag().equals(userProfileViewModel.getAvatarId()));
        }
    }

    @Override
    public int getItemCount() {

        if (favoritesPojoList == null) {
            return 0;
        } else {
            return favoritesPojoList.size();
        }
    }

    @Override
    public int getItemViewType(int position) {

        return VIEW_TYPE_MESSAGE;
    }

    public static class MyRecyclerViewHolder extends RecyclerView.ViewHolder {
        private final ItemsAvatarListBinding binding;

        public MyRecyclerViewHolder(ItemsAvatarListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }


    /**
     * Item Interface Callback
     */
    public interface OnItemClickListener {
        void onItemClick(AvatarBean bean);
    }

    private OnItemClickListener onItemClickListener;

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    //使用
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}
