package com.dyaco.spirit_commercial.egym;

import static com.dyaco.spirit_commercial.App.getApp;
import static com.dyaco.spirit_commercial.egym.EgymUtil.EGYM_MACHINE_TYPE;
import static com.dyaco.spirit_commercial.support.CommonUtils.chkDuration;
import static com.dyaco.spirit_commercial.support.CommonUtils.formatMsToM;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.EgymDetailsItemBinding;
import com.dyaco.spirit_commercial.model.webapi.bean.EgymTrainingPlans;
import com.dyaco.spirit_commercial.support.CommonUtils;
import com.bumptech.glide.Glide;
import com.dyaco.spirit_commercial.support.custom_view.banner.adapter.BannerAdapter;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingViewModel;
import com.dyaco.spirit_commercial.viewmodel.EgymDataViewModel;
import com.dyaco.spirit_commercial.viewmodel.WorkoutViewModel;

import java.util.List;


public class EgymBannerAdapter extends BannerAdapter<EgymTrainingPlans.TrainerDTO, RecyclerView.ViewHolder> {
    Context context;
    DeviceSettingViewModel deviceSettingViewModel;
    WorkoutViewModel workoutViewModel;
    EgymDataViewModel egymDataViewModel;

    public EgymBannerAdapter(Context context, List<EgymTrainingPlans.TrainerDTO> programsEnumList, WorkoutViewModel workoutViewModel, DeviceSettingViewModel deviceSettingViewModel, EgymDataViewModel egymDataViewModel) {
        super(programsEnumList);
        this.context = context;
        this.deviceSettingViewModel = deviceSettingViewModel;
        this.workoutViewModel = workoutViewModel;
        this.egymDataViewModel = egymDataViewModel;
    }


    public class ProgramsViewHolder extends RecyclerView.ViewHolder {

        private final EgymDetailsItemBinding binding;

        public ProgramsViewHolder(EgymDetailsItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.setDeviceSetting(deviceSettingViewModel);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProgramsViewHolder(EgymDetailsItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindView(@NonNull RecyclerView.ViewHolder viewHolder, EgymTrainingPlans.TrainerDTO trainerDTO, int position, int size) {
        final ProgramsViewHolder holder = (ProgramsViewHolder) viewHolder;

        holder.binding.tvPlanName.setText(String.format("%s %s", EGYM_MACHINE_TYPE, context.getString(R.string.Activity)));
        holder.binding.tvPlanFrom.setText(String.format("from %s", trainerDTO.getSessionName()));
        holder.binding.tvCoachName.setText(String.format("by %s %s", trainerDTO.getAuthor().getLastName(), trainerDTO.getAuthor().getFirstName()));

        // **設定背景圖片**
        int[] bgResources = {
                R.drawable.plan_bg_86a73a,
                R.drawable.plan_bg_222,
                R.drawable.plan_bg_333,
                R.drawable.plan_bg_444
        };
        holder.binding.vBG.setBackgroundResource(bgResources[position % bgResources.length]);

        // **計算課程時間**
        int durationTime = 0;
        for (EgymTrainingPlans.TrainerDTO.IntervalsDTO interval : trainerDTO.getIntervals()) {
            durationTime += chkDuration(interval.getDuration());
        }
        holder.binding.tvPlanTime.setText(formatMsToM(durationTime));

        // **監聽 `coachImages` 變更，更新 `ivCoach`**
        egymDataViewModel.coachImages.observe((LifecycleOwner) context, images -> {
            if (images != null && CommonUtils.hasValueAt(images, position)) {
                Glide.with(getApp())
                        .load(images.get(position))
                        .circleCrop()
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .skipMemoryCache(false)
                        .error(R.drawable.avatar_normal_1_default)
                        .into(holder.binding.ivCoach);
            }
        });

        // **按鈕點擊監聽**
        holder.binding.vBG.setOnClickListener(view -> onImageClickListener.onImageClick(trainerDTO));
        holder.binding.btnSelect.setOnClickListener(view -> onImageClickListener.onImageClick(trainerDTO));
    }




    @SuppressLint("NotifyDataSetChanged")
    public void updateCoachImage() {
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(boolean isForward);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnImageClickListener {
        void onImageClick(EgymTrainingPlans.TrainerDTO trainerDTO);
    }

    private OnImageClickListener onImageClickListener;

    public void setOnImageClickListener(OnImageClickListener onImageClickListener) {
        this.onImageClickListener = onImageClickListener;
    }


}
