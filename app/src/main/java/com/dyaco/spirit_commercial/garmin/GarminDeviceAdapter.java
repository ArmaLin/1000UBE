package com.dyaco.spirit_commercial.garmin;

import static com.dyaco.spirit_commercial.garmin.GarminDevicesWindow.GARMIN_TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.corestar.libs.device.DeviceGEM;
import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.ItemsGarminDeviceListBinding;
import com.dyaco.spirit_commercial.databinding.ItemsGarminDeviceListLastBinding;
import com.dyaco.spirit_commercial.databinding.ItemsNoDataBinding;
import com.garmin.health.ConnectionState;
import com.garmin.health.Device;

import java.util.List;

public class GarminDeviceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Device> garminDeviceList;
    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_MESSAGE = 1;
    private static final int VIEW_TYPE_FOOTER = 2;
    private final boolean isWorkout;


    private final Context mContext;

    GarminDeviceAdapter(Context context,boolean isWorkout) {
        this.mContext = context;
        this.isWorkout = isWorkout;
    }

    @SuppressLint("NotifyDataSetChanged")
    void setData2View(List<Device> list) {
        garminDeviceList = list;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData2View(Device device) {

        Log.d(GARMIN_TAG, "setData2View: " + garminDeviceList.size());

        if (garminDeviceList != null && garminDeviceList.size() > 0) {
            int i = 0;
            for (Device garminDevice : garminDeviceList) {

                if (garminDevice.address().equals(device.address())) {
                    garminDeviceList.set(i, device);
                }
                i++;
            }
            this.notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        if (viewType == VIEW_TYPE_MESSAGE) {
            viewHolder = new MyRecyclerViewHolder(ItemsGarminDeviceListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        } else if (viewType == VIEW_TYPE_FOOTER) {
            viewHolder = new FooterViewHolder(ItemsGarminDeviceListLastBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        } else {
            viewHolder = new ViewHolderEmpty(ItemsNoDataBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {

        int viewType = getItemViewType(i);

        switch (viewType) {
            case VIEW_TYPE_MESSAGE:
                final MyRecyclerViewHolder viewHolder = (MyRecyclerViewHolder) holder;
                final int position = viewHolder.getAdapterPosition();
                final Device garminDevice = garminDeviceList.get(position);

                if (garminDevice.connectionState() == ConnectionState.CONNECTED) {
                    viewHolder.binding.ivConnected.setVisibility(View.VISIBLE);
                    viewHolder.binding.tvConnected.setVisibility(View.VISIBLE);
                    viewHolder.binding.btnDelete.setVisibility(View.VISIBLE);
                    viewHolder.binding.cbSelectData.setEnabled(true);
                    viewHolder.binding.clBase.setClickable(true);
                    viewHolder.binding.clBase.setEnabled(true);
                //    viewHolder.binding.vConnectedUnderline.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.binding.ivConnected.setVisibility(View.INVISIBLE);
                    viewHolder.binding.tvConnected.setVisibility(View.INVISIBLE);
//                    viewHolder.binding.btnDelete.setVisibility(View.INVISIBLE);
                    viewHolder.binding.btnDelete.setVisibility(View.VISIBLE);
                    viewHolder.binding.cbSelectData.setEnabled(false);
                    viewHolder.binding.clBase.setClickable(false);
                    viewHolder.binding.clBase.setEnabled(false);
//                    viewHolder.binding.vConnectedUnderline.setVisibility(View.INVISIBLE);
                }

//                viewHolder.binding.clBase.setBackgroundColor(heartRateDevice.isConnected() ? ContextCompat.getColor(mContext, R.color.color201396ef) : ContextCompat.getColor(mContext, R.color.color1c242a));

                if (MainActivity.currentGarminAddress.equals(garminDevice.address())) {
                    viewHolder.binding.clBase.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color201396ef));
                    viewHolder.binding.tvHrTitle.setTextColor(ContextCompat.getColor(mContext, R.color.color1396ef));
                    viewHolder.binding.cbSelectData.setChecked(true);
                    viewHolder.binding.vConnectedUnderline.setVisibility(View.VISIBLE);

                } else {
                    viewHolder.binding.clBase.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color1c242a));
                    viewHolder.binding.tvHrTitle.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                    viewHolder.binding.cbSelectData.setChecked(false);
                    viewHolder.binding.vConnectedUnderline.setVisibility(View.INVISIBLE);
                }

//                viewHolder.binding.tvHrTitle.setText(String.format("%s (%s)", garminDevice.friendlyName(), garminDevice.address().substring(0, 2)));
                viewHolder.binding.tvHrTitle.setText(garminDevice.friendlyName());

                viewHolder.binding.cbSelectData.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                    if (isChecked) {
                        onRadioListener.onRadioClick(garminDeviceList.get(position));
                    }
                });

                viewHolder.binding.clBase.setOnClickListener(view ->
                        viewHolder.binding.cbSelectData.setChecked(true));

//                viewHolder.binding.btnConnect.setOnClickListener(v -> {
//                    if (heartRateDevice.isConnected()) return;
//                    onItemClickListener.onItemClick(heartRateDevice, viewHolder.binding.clBase, viewHolder.binding.ivHrIcon);
//                });

                //忘記裝置;解除配對
                viewHolder.binding.btnDelete.setOnClickListener(v -> {
                    onDeleteClickListener.onDeleteClick(garminDevice);
                   // if (!heartRateDevice.isConnected()) return;
                });


                if (isWorkout){
                    viewHolder.binding.btnDelete.setVisibility(View.INVISIBLE);
                }


                break;
            case VIEW_TYPE_FOOTER: //add device
                ((FooterViewHolder) holder).binding.clBase.setOnClickListener(v -> onAddDeviceListener.onItemClick());
                break;
            case VIEW_TYPE_EMPTY:
                ((ViewHolderEmpty) holder).binding.tvNodata.setText(R.string.NO_DATA);
        }
    }


    @Override
    public int getItemCount() {
        if (!isWorkout) {
            if (garminDeviceList == null || garminDeviceList.size() <= 0) {
                return 1;
            } else {
                return garminDeviceList.size() + 1;
            }
        } else{
            return garminDeviceList.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = VIEW_TYPE_MESSAGE;

        if (!isWorkout) {
            if (garminDeviceList == null || garminDeviceList.size() <= 0) {
                viewType = VIEW_TYPE_FOOTER;
            } else if (position == garminDeviceList.size()) {
                viewType = VIEW_TYPE_FOOTER;
            }
        }

        return viewType;
    }

    public static class MyRecyclerViewHolder extends RecyclerView.ViewHolder {
        private final ItemsGarminDeviceListBinding binding;

        public MyRecyclerViewHolder(ItemsGarminDeviceListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public static class FooterViewHolder extends RecyclerView.ViewHolder {
        private final ItemsGarminDeviceListLastBinding binding;

        public FooterViewHolder(ItemsGarminDeviceListLastBinding binding) {
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

    public interface OnDeleteClickListener {
        void onDeleteClick(Device device);
    }
    private OnDeleteClickListener onDeleteClickListener;
    public void setOnDeleteClickListener(OnDeleteClickListener onDeleteClickListener) {
        this.onDeleteClickListener = onDeleteClickListener;
    }

    /**
     * Item Interface Callback
     */
    public interface OnItemClickListener {
        void onItemClick(DeviceGEM.HeartRateDevice bean, View view1, View view2);
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

    public List<Device> getDeviceList() {
        return garminDeviceList;
    }




    public interface OnRadioListener {
        void onRadioClick(Device bean);
    }
    private OnRadioListener onRadioListener;
    //使用
    public void setOnRadioListener(OnRadioListener onRadioListener) {
        this.onRadioListener = onRadioListener;
    }

}
