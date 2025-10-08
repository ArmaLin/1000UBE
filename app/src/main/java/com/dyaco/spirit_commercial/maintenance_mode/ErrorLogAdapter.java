package com.dyaco.spirit_commercial.maintenance_mode;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.ItemsErrorLogListBinding;
import com.dyaco.spirit_commercial.databinding.ItemsNoDataBinding;
import com.dyaco.spirit_commercial.support.room.spirit.spirit_entity.ErrorMsgEntity;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ErrorLogAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ErrorMsgEntity> errorLogBeanList;
    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_MESSAGE = 1;
    private static final int VIEW_TYPE_FOOTER = 2;
    private final SimpleDateFormat dateFormat1 ;
    private final SimpleDateFormat dateFormat2 ;


    private Context mContext;

    ErrorLogAdapter(Context context) {
        this.mContext = context;
        dateFormat1 = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        dateFormat2 = new SimpleDateFormat("hh:mm:ss a z", Locale.ENGLISH);
    }

    @SuppressLint("NotifyDataSetChanged")
    void setData2View(List<ErrorMsgEntity> list) {
        errorLogBeanList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        if (viewType == VIEW_TYPE_MESSAGE) {
            viewHolder = new MyRecyclerViewHolder(ItemsErrorLogListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
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
            final ErrorMsgEntity errorLogBean = errorLogBeanList.get(position);
            viewHolder.binding.tvDate.setText(dateFormat1.format(errorLogBean.getErrorDate()));
            viewHolder.binding.tvTime.setText(dateFormat2.format(errorLogBean.getErrorDate()));
            viewHolder.binding.tvErrorCode.setText(String.valueOf(errorLogBean.getErrorCode()));
            viewHolder.binding.tvErrorMessage.setText(String.valueOf(errorLogBean.getErrorMessage()));

            viewHolder.binding.clBase.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    onItemClickListener.onItemClick(errorLogBean);
                    return false;
                }
            });

        } else {
            final ViewHolderEmpty viewHolder = (ViewHolderEmpty) holder;
            viewHolder.binding.tvNodata.setText(R.string.NO_DATA);
          //  viewHolder.binding.tvNodata.setTextColor();
        }
    }

    @Override
    public int getItemCount() {
        if (errorLogBeanList == null || errorLogBeanList.size() <= 0) {
            return 1;
        } else {
            return errorLogBeanList.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = VIEW_TYPE_MESSAGE;

        if (errorLogBeanList == null || errorLogBeanList.size() <= 0) {
            viewType = VIEW_TYPE_EMPTY;
        }

        return viewType;
    }

    public static class MyRecyclerViewHolder extends RecyclerView.ViewHolder {
        private final ItemsErrorLogListBinding binding;

        public MyRecyclerViewHolder(ItemsErrorLogListBinding binding) {
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
        void onItemClick(ErrorMsgEntity bean);
    }

    private OnItemClickListener onItemClickListener;

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}
