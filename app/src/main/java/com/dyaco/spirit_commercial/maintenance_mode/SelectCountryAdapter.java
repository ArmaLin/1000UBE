package com.dyaco.spirit_commercial.maintenance_mode;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.corestar.libs.device.DeviceTvTuner;
import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.ItemsNoDataBinding;
import com.dyaco.spirit_commercial.databinding.ItemsSelectCountryListBinding;

import java.util.List;

public class SelectCountryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<CountryBean> countryBeanList;
    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_MESSAGE = 1;
    private static final int VIEW_TYPE_FOOTER = 2;


    private Context mContext;

    private DeviceTvTuner.TV_COUNTRY currentCountry;

    SelectCountryAdapter(Context context, DeviceTvTuner.TV_COUNTRY currentCountry) {
        this.mContext = context;
        this.currentCountry = currentCountry;
    }

    /**
     * notifyItemChanged(int)
     * notifyItemInserted(int)
     * notifyItemRemoved(int)
     * notifyItemRangeChanged(int, int)
     * notifyItemRangeInserted(int, int)
     * notifyItemRangeRemoved(int, int)
     */
    @SuppressLint("NotifyDataSetChanged")
    void setData2View(List<CountryBean> list) {
        countryBeanList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        if (viewType == VIEW_TYPE_MESSAGE) {
            viewHolder = new MyRecyclerViewHolder(ItemsSelectCountryListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
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
            final CountryBean countryBean = countryBeanList.get(position);
            viewHolder.binding.tvCountryName.setText(countryBean.getCountryName());

            if (currentCountry == countryBean.tvCountry) {
                viewHolder.binding.tvCountryName.setTextColor(ContextCompat.getColor(mContext, R.color.color1396ef));
            }

            viewHolder.binding.tvCountryName.setOnClickListener(view -> {
                if (currentCountry == countryBean.tvCountry) return;
                onItemClickListener.onItemClick(countryBean);
            });

        } else {
            final ViewHolderEmpty viewHolder = (ViewHolderEmpty) holder;
            viewHolder.binding.tvNodata.setText("");
          //  viewHolder.binding.tvNodata.setTextColor();
        }
    }

    @Override
    public int getItemCount() {
        if (countryBeanList == null || countryBeanList.size() <= 0) {
            return 1;
        } else {
            return countryBeanList.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = VIEW_TYPE_MESSAGE;

        if (countryBeanList == null || countryBeanList.size() <= 0) {
            viewType = VIEW_TYPE_EMPTY;
        }

        return viewType;
    }

    public static class MyRecyclerViewHolder extends RecyclerView.ViewHolder {
        private final ItemsSelectCountryListBinding binding;

        public MyRecyclerViewHolder(ItemsSelectCountryListBinding binding) {
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
        void onItemClick(CountryBean bean);
    }

    private OnItemClickListener onItemClickListener;

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}
