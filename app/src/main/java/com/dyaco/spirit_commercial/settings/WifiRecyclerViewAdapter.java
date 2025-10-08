package com.dyaco.spirit_commercial.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.WifiResultItemBinding;
import com.dyaco.spirit_commercial.support.wifi.WifiControlUtils.WifiEntity;

import java.util.ArrayList;
import java.util.List;

public class WifiRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context mContext;
    List<WifiEntity> list = new ArrayList<>();

    private static final String TAG = "###WIFI";
    private boolean isClickable = true;

    @SuppressLint("NotifyDataSetChanged")
    public void setData2View(List<WifiEntity> list){
        this.list = list;
        try {
            isClickable = true;
            notifyDataSetChanged();
        } catch (Exception e) {
            Log.d(TAG, "setData2View: Exception" + e.getLocalizedMessage());
        //    e.printStackTrace();
        }
    }

    public void setClickable(boolean clickable) {
        this.isClickable = clickable;
    }

    public WifiRecyclerViewAdapter(Context context) {
        this.mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        viewHolder = new MyRecyclerViewHolder(WifiResultItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

        return viewHolder;
    }

    public static class MyRecyclerViewHolder extends RecyclerView.ViewHolder {
        private final WifiResultItemBinding binding;

        public MyRecyclerViewHolder(WifiResultItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        final MyRecyclerViewHolder viewHolder = (MyRecyclerViewHolder) holder;
        WifiEntity scanResult = list.get(position);
        boolean isConnected = scanResult.isConnected();
        boolean isSaved = scanResult.isSaved();
        boolean isEncrypt = scanResult.isEncrypt();

        int wifiLevel = scanResult.getLevel();


        viewHolder.binding.clBase.setBackground(isConnected ? ContextCompat.getDrawable(mContext, R.color.color201396ef) : ContextCompat.getDrawable(mContext, R.color.btn_1c242a_252e37));
        viewHolder.binding.tvBtTitle.setTextColor(isConnected ? ContextCompat.getColorStateList(mContext, R.color.color1396ef) : ContextCompat.getColorStateList(mContext, R.color.white));
        viewHolder.binding.vConnectedUnderline.setVisibility(isConnected ? View.VISIBLE : View.INVISIBLE);
        viewHolder.binding.ivBtIcon.setBackgroundResource(isConnected ? R.drawable.icon_maintenance_wifi_linked : (isEncrypt ? R.drawable.icon_maintenance_wifi_locked : R.drawable.icon_maintenance_wifi_unlock));
        viewHolder.binding.tvBtTitle.setText(scanResult.name);

        viewHolder.binding.tvBtStatus.setText(isSaved ? "(saved)" : "");
        viewHolder.binding.ivSave.setVisibility(isSaved ? View.VISIBLE : View.INVISIBLE);

        // 根據 WiFi 強度設定對應的圖標
        int wifiSignalDrawable = getWifiSignalDrawable(wifiLevel);
        viewHolder.binding.tvWifiLevel.setImageResource(wifiSignalDrawable);


//        viewHolder.binding.progress.setVisibility(View.INVISIBLE);
        if (!scanResult.isConnecting()) {
            viewHolder.binding.progress.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.binding.clBase.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color252e37));
            viewHolder.binding.progress.setVisibility(View.VISIBLE);
            viewHolder.binding.ivBtIcon.setBackgroundResource(0);
        }

        viewHolder.binding.clBase.setOnClickListener(v -> {
            if (!isClickable) return;
            onItemClickListener.onItemClick(scanResult,viewHolder.binding.clBase, viewHolder.binding.ivBtIcon, viewHolder.binding.progress);
        });

        viewHolder.binding.clBase.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (isClickable) {
                    if (isSaved) {
                        onLongItemClickListener.onLongItemClick(scanResult);
                    }
                }
                return true; //true 不加入短按動作
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    interface OnItemClick{
        void onItemClick(ScanResult scanResult);
    }




    public interface OnItemClickListener {
        void onItemClick(WifiEntity bean, View view1, View view2, ProgressBar progress);
    }

    private OnItemClickListener onItemClickListener;

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }



    public interface OnLongItemClickListener {
        void onLongItemClick(WifiEntity bean);
    }
    private OnLongItemClickListener onLongItemClickListener;
    public void setOnLongItemClickListener(OnLongItemClickListener onLongItemClickListener) {
        this.onLongItemClickListener = onLongItemClickListener;
    }

    private int getWifiSignalDrawable(int wifiLevel) {
        if (wifiLevel >= -50) {
            return R.drawable.wifi_signal_triangle_5; // 滿格
        } else if (wifiLevel >= -60) {
            return R.drawable.wifi_signal_triangle_4;
        } else if (wifiLevel >= -70) {
            return R.drawable.wifi_signal_triangle_3;
        } else if (wifiLevel >= -80) {
            return R.drawable.wifi_signal_triangle_2;
        } else if (wifiLevel >= -90) {
            return R.drawable.wifi_signal_triangle_1;
        } else {
            return R.drawable.wifi_signal_triangle_0; // 無訊號
        }
    }
}
