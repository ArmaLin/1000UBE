package com.dyaco.spirit_commercial.dashboard_training;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dyaco.spirit_commercial.R;

import java.util.List;


public class WheelAdapter extends RecyclerView.Adapter<WheelAdapter.WheelViewHolder> {

    private final List<String> items;

    public WheelAdapter(List<String> items) {
        this.items = items;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @NonNull
    @Override
    public WheelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_wheel_workout_time, parent, false);
        return new WheelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WheelViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    public List<String> getItems() {
        return items;
    }


    public static class WheelViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public WheelViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_wheel_item);
        }

        public void bind(String number) {
            textView.setText(String.valueOf(number));
        }
    }
}