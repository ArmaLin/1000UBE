package com.dyaco.spirit_commercial.egym;

import static com.dyaco.spirit_commercial.App.MODE;
import static com.dyaco.spirit_commercial.App.UNIT_E;
import static com.dyaco.spirit_commercial.egym.EgymUtil.SYMBOL_DURATION;
import static com.dyaco.spirit_commercial.support.CommonUtils.chkDuration;
import static com.dyaco.spirit_commercial.support.CommonUtils.formatMsToM;
import static com.dyaco.spirit_commercial.support.FormulaUtil.getEgymUnitD;
import static com.dyaco.spirit_commercial.support.FormulaUtil.getEgymUnitS;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.IMPERIAL;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.METRIC;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.EgymStepsListBinding;
import com.dyaco.spirit_commercial.databinding.ItemsNoDataBinding;
import com.dyaco.spirit_commercial.model.webapi.bean.EgymTrainingPlans;
import com.dyaco.spirit_commercial.product_flavor.ModeEnum;
import com.dyaco.spirit_commercial.settings.AvatarEntity;
import com.dyaco.spirit_commercial.viewmodel.EgymDataViewModel;
import com.dyaco.spirit_commercial.viewmodel.WorkoutViewModel;

import java.util.List;
import java.util.Objects;


public class EgymStepsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<EgymTrainingPlans.TrainerDTO.IntervalsDTO> intervalsDTOList;
    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_MESSAGE = 1;
    private static final int VIEW_TYPE_FOOTER = 2;
    WorkoutViewModel workoutViewModel;
    private EgymDataViewModel egymDataViewModel;

    private Context mContext;
    int egymUnit;

    EgymStepsAdapter(Context context, WorkoutViewModel workoutViewModel, EgymTrainingPlans.TrainerDTO trainerDTO, EgymDataViewModel egymDataViewModel) {
        this.mContext = context;
        this.workoutViewModel = workoutViewModel;
        this.intervalsDTOList = trainerDTO.getIntervals();
        this.egymDataViewModel = egymDataViewModel;

        egymUnit = "METRIC".equalsIgnoreCase(Objects.requireNonNull(egymDataViewModel.egymUserDetailsModel.getValue()).getUnitSystem()) ? METRIC : IMPERIAL;

//        Optional<List<EgymTrainingPlans.TrainerDTO.IntervalsDTO>> optionalIntervals = Optional.of(egymDataViewModel)
//                .map(viewModel -> viewModel.egymTrainingPlansData.getValue())
//                .map(EgymTrainingPlans::getTrainer)
//                .map(trainers -> trainers.get(trainPosition))
//                .map(EgymTrainingPlans.TrainerDTO::getIntervals);

        //      this.intervalsDTOList = optionalIntervals.orElseGet(ArrayList::new);
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        if (viewType == VIEW_TYPE_MESSAGE) {
            viewHolder = new MyRecyclerViewHolder(EgymStepsListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
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
            final EgymTrainingPlans.TrainerDTO.IntervalsDTO intervalsDTO = intervalsDTOList.get(position);
//            viewHolder.binding.tvSetTime.setText(formatMsToM(intervalsDTO.getDuration()));

            String durationText = "00:00";
            if (intervalsDTO.getDuration() != SYMBOL_DURATION) {
                durationText = formatMsToM(chkDuration(intervalsDTO.getDuration()));
            }
//            viewHolder.binding.tvSetTime.setText(formatMsToM(chkDuration(intervalsDTO.getDuration())));
            viewHolder.binding.tvSetTime.setText(durationText);

            viewHolder.binding.tvSetNo.setText(String.valueOf(position + 1));

            //DISTANCE
            String distanceText = "---";
            viewHolder.binding.tvDistanceUnit.setText("");
            if (intervalsDTO.getDistance() != null) {
                distanceText = getEgymUnitD(egymUnit, intervalsDTO.getDistance());
                viewHolder.binding.tvDistanceText.setTextColor(ContextCompat.getColorStateList(mContext, R.color.white));
                viewHolder.binding.ivDistance.setColorFilter(ContextCompat.getColor(mContext, R.color.white_text));
                viewHolder.binding.tvDistanceUnit.setText((UNIT_E == IMPERIAL ? mContext.getString(R.string.mi) : mContext.getString(R.string.km)));
                viewHolder.binding.tvDistanceUnitText.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            } else {
                viewHolder.binding.ivDistance.setColorFilter(ContextCompat.getColor(mContext, R.color.colorDisText));
                viewHolder.binding.tvDistanceText.setTextColor(ContextCompat.getColorStateList(mContext, R.color.colorDisText));
                viewHolder.binding.tvDistanceUnitText.setTextColor(ContextCompat.getColor(mContext, R.color.colorDisText));
            }
            viewHolder.binding.tvDistanceText.setText(distanceText);


            //SPEED
            String speedText = "---";
            viewHolder.binding.tvSpeedUnit.setText("");

            if (MODE == ModeEnum.CT1000ENT) {
                if (intervalsDTO.getSpeed() != null) {
                    speedText = getEgymUnitS(egymUnit, intervalsDTO.getSpeed());
                    viewHolder.binding.tvSpeedText.setTextColor(ContextCompat.getColorStateList(mContext, R.color.white));
                    viewHolder.binding.ivSpeed.setColorFilter(ContextCompat.getColor(mContext, R.color.white_text));
                    viewHolder.binding.tvSpeedUnit.setText((UNIT_E == IMPERIAL ? mContext.getString(R.string.mph) : mContext.getString(R.string.km_h)));

                    viewHolder.binding.ivSpeed.setImageResource(R.drawable.icon_speed_32);

                    viewHolder.binding.tvSpeedUnitText.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                } else {
                    viewHolder.binding.tvSpeedUnitText.setTextColor(ContextCompat.getColor(mContext, R.color.colorDisText));
                    viewHolder.binding.tvSpeedText.setTextColor(ContextCompat.getColor(mContext, R.color.colorDisText));
                    viewHolder.binding.ivSpeed.setColorFilter(ContextCompat.getColor(mContext, R.color.colorDisText));
                }
                viewHolder.binding.tvSpeedUnitText.setText(mContext.getString(R.string.Speed));
            } else if (MODE == ModeEnum.CE1000ENT) {
                if (intervalsDTO.getStepsPerMinute() != null) {
                    speedText = String.valueOf(intervalsDTO.getStepsPerMinute());
                    viewHolder.binding.tvSpeedText.setTextColor(ContextCompat.getColorStateList(mContext, R.color.white));
                    viewHolder.binding.ivSpeed.setColorFilter(ContextCompat.getColor(mContext, R.color.white_text));
                    viewHolder.binding.tvSpeedUnitText.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                    viewHolder.binding.tvSpeedUnit.setText(mContext.getString(R.string.SPM));
                } else {
                    viewHolder.binding.tvSpeedUnitText.setTextColor(ContextCompat.getColor(mContext, R.color.colorDisText));
                    viewHolder.binding.tvSpeedText.setTextColor(ContextCompat.getColor(mContext, R.color.colorDisText));
                    viewHolder.binding.ivSpeed.setColorFilter(ContextCompat.getColor(mContext, R.color.colorDisText));
                }
                viewHolder.binding.ivSpeed.setImageResource(R.drawable.icon_egym_rpm_36);
                viewHolder.binding.tvSpeedUnitText.setText(mContext.getString(R.string.Cadence));
            } else {
                //BIKE
                if (intervalsDTO.getRotations() != null) {
                    speedText = String.valueOf(intervalsDTO.getRotations());
                    viewHolder.binding.tvSpeedText.setTextColor(ContextCompat.getColorStateList(mContext, R.color.white));
                    viewHolder.binding.ivSpeed.setColorFilter(ContextCompat.getColor(mContext, R.color.white_text));
                    viewHolder.binding.tvSpeedUnitText.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                    viewHolder.binding.tvSpeedUnit.setText(mContext.getString(R.string.RPM));
                } else {
                    viewHolder.binding.tvSpeedUnitText.setTextColor(ContextCompat.getColor(mContext, R.color.colorDisText));
                    viewHolder.binding.tvSpeedText.setTextColor(ContextCompat.getColor(mContext, R.color.colorDisText));
                    viewHolder.binding.ivSpeed.setColorFilter(ContextCompat.getColor(mContext, R.color.colorDisText));
                }
                viewHolder.binding.ivSpeed.setImageResource(R.drawable.icon_egym_rpm_36);
                viewHolder.binding.tvSpeedUnitText.setText(mContext.getString(R.string.Cadence));
            }

            viewHolder.binding.tvSpeedText.setText(speedText);


            //HR
            String hrText = "---";
            viewHolder.binding.tvHrUnit.setText("");
            if (intervalsDTO.getHeartRate() != null) {
                hrText = String.valueOf(intervalsDTO.getHeartRate());
                viewHolder.binding.tvHrText.setTextColor(ContextCompat.getColorStateList(mContext, R.color.white));
                viewHolder.binding.ivHr.setColorFilter(ContextCompat.getColor(mContext, R.color.white_text));
                viewHolder.binding.tvHrUnit.setText(mContext.getString(R.string.BPM));
                viewHolder.binding.tvHrUnitText.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            } else {
                viewHolder.binding.tvHrUnitText.setTextColor(ContextCompat.getColor(mContext, R.color.colorDisText));
                viewHolder.binding.tvHrText.setTextColor(ContextCompat.getColor(mContext, R.color.colorDisText));
                viewHolder.binding.ivHr.setColorFilter(ContextCompat.getColor(mContext, R.color.colorDisText));
            }
            viewHolder.binding.tvHrText.setText(hrText);

            //INCLINE
            String inclineText = "---";
            if (MODE == ModeEnum.CT1000ENT) {
                if (intervalsDTO.getIncline() != null) {
                    inclineText = String.valueOf(intervalsDTO.getIncline());
                    viewHolder.binding.tvInclineText.setTextColor(ContextCompat.getColorStateList(mContext, R.color.white));
                    viewHolder.binding.ivIncline.setColorFilter(ContextCompat.getColor(mContext, R.color.white_text));
                    viewHolder.binding.ivIncline.setImageResource(R.drawable.icon_incline_32);
                    viewHolder.binding.tvInclineUnitText.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                } else {
                    viewHolder.binding.tvInclineUnitText.setTextColor(ContextCompat.getColor(mContext, R.color.colorDisText));
                    viewHolder.binding.tvInclineText.setTextColor(ContextCompat.getColor(mContext, R.color.colorDisText));
                    viewHolder.binding.ivIncline.setColorFilter(ContextCompat.getColor(mContext, R.color.colorDisText));
                }
            } else {
                //LEVEL
                if (intervalsDTO.getResistance() != null) {
                    inclineText = String.valueOf(intervalsDTO.getResistance());
                    viewHolder.binding.tvInclineText.setTextColor(ContextCompat.getColorStateList(mContext, R.color.white));
                    viewHolder.binding.ivIncline.setColorFilter(ContextCompat.getColor(mContext, R.color.white_text));
                    viewHolder.binding.tvInclineUnitText.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                } else {
                    viewHolder.binding.tvInclineUnitText.setTextColor(ContextCompat.getColor(mContext, R.color.colorDisText));
                    viewHolder.binding.tvInclineText.setTextColor(ContextCompat.getColor(mContext, R.color.colorDisText));
                    viewHolder.binding.ivIncline.setColorFilter(ContextCompat.getColor(mContext, R.color.colorDisText));
                }
                viewHolder.binding.ivIncline.setImageResource(R.drawable.icon_level_32);
                viewHolder.binding.tvInclineUnitText.setText(mContext.getString(R.string.Resistance));
            }
            viewHolder.binding.tvInclineText.setText(inclineText);


        } else {
            final ViewHolderEmpty viewHolder = (ViewHolderEmpty) holder;
            viewHolder.binding.tvNodata.setText("");
        }
    }


    @Override
    public int getItemCount() {
        if (intervalsDTOList == null) {
            return 1;
        } else {
            return intervalsDTOList.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = VIEW_TYPE_MESSAGE;

        if (intervalsDTOList == null || intervalsDTOList.isEmpty()) {
            viewType = VIEW_TYPE_EMPTY;
        }

        return viewType;
    }

    public static class MyRecyclerViewHolder extends RecyclerView.ViewHolder {
        private final EgymStepsListBinding binding;

        public MyRecyclerViewHolder(EgymStepsListBinding binding) {
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
