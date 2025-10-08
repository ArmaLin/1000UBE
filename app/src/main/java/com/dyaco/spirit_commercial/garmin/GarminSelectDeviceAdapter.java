package com.dyaco.spirit_commercial.garmin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dyaco.spirit_commercial.databinding.ItemsGarminDeviceSelectBinding;
import com.garmin.health.ScannedDevice;

import java.util.List;

public class GarminSelectDeviceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ScannedDevice> garminScannedDeviceList;

    private Context mContext;

    GarminSelectDeviceAdapter(Context context) {
        this.mContext = context;
    }

    @SuppressLint("NotifyDataSetChanged")
    void setData2View(List<ScannedDevice> list) {
        garminScannedDeviceList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new MyRecyclerViewHolder(ItemsGarminDeviceSelectBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {

        final MyRecyclerViewHolder viewHolder = (MyRecyclerViewHolder) holder;
        final int position = viewHolder.getAdapterPosition();
        final ScannedDevice scannedDevice = garminScannedDeviceList.get(position);

        viewHolder.binding.tvHrTitle.setText(String.format("%s [%s]", scannedDevice.getFriendlyName(), scannedDevice.getAddress()));

        viewHolder.binding.btnSelect.setOnClickListener(v ->
                onItemClickListener.onItemClick(scannedDevice));

        viewHolder.binding.clBase.setOnClickListener(v ->
                onItemClickListener.onItemClick(scannedDevice));
    }


    @Override
    public int getItemCount() {
        if (garminScannedDeviceList == null) {
            return 0;
        } else {
            return garminScannedDeviceList.size();
        }
    }

    public static class MyRecyclerViewHolder extends RecyclerView.ViewHolder {
        private final ItemsGarminDeviceSelectBinding binding;

        public MyRecyclerViewHolder(ItemsGarminDeviceSelectBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    /**
     * Item Interface Callback
     */
    public interface OnItemClickListener {
        void onItemClick(ScannedDevice bean);
    }

    private OnItemClickListener onItemClickListener;

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    public interface OnAddDeviceListener {
        void onItemClick();
    }

    private OnAddDeviceListener onAddDeviceListener;


    public void setOnAddDeviceListener(OnAddDeviceListener onAddDeviceListener) {
        this.onAddDeviceListener = onAddDeviceListener;
    }

}
