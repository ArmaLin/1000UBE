package com.dyaco.spirit_commercial.dashboard_training;

import static com.dyaco.spirit_commercial.App.UNIT_E;
import static com.dyaco.spirit_commercial.App.getApp;
import static com.dyaco.spirit_commercial.MainActivity.isTreadmill;
import static com.dyaco.spirit_commercial.MainActivity.userProfileViewModel;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.getArmyTarget;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.getMarinesTarget;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.getNavyTarget;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.IMPERIAL;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.METRIC;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.AGE_MAX;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.AGE_MIN;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.HEIGHT_IU_MIN;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.HEIGHT_MU_MIN;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.WEIGHT_IU_MIN;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.WEIGHT_MU_MIN;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.getMaxHeight;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.getMaxWeight;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.getMinHeight;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.getMinWeight;
import static com.dyaco.spirit_commercial.support.intdef.UnitEnum.DISTANCE2;
import static com.dyaco.spirit_commercial.support.intdef.UnitEnum.getUnit;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.ProgramsDetailsItemBinding;
import com.dyaco.spirit_commercial.support.CustomTypefaceSpan;
import com.dyaco.spirit_commercial.support.FormulaUtil;
import com.dyaco.spirit_commercial.support.custom_view.banner.adapter.BannerAdapter;
import com.dyaco.spirit_commercial.support.custom_view.wheelPicker.OptionsPickerView;
import com.dyaco.spirit_commercial.support.custom_view.wheelPicker.WheelView;
import com.dyaco.spirit_commercial.support.intdef.WorkoutIntDef;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingViewModel;
import com.dyaco.spirit_commercial.viewmodel.WorkoutViewModel;
import com.dyaco.spirit_commercial.workout.programs.ProgramsEnum;

import java.util.ArrayList;
import java.util.List;


public class ProgramsBannerAdapter extends BannerAdapter<ProgramsEnum, RecyclerView.ViewHolder> {
    Context context;
    DeviceSettingViewModel deviceSettingViewModel;
    WorkoutViewModel workoutViewModel;
    Typeface typeface;

    public ProgramsBannerAdapter(Context context, List<ProgramsEnum> programsEnumList, WorkoutViewModel workoutViewModel, DeviceSettingViewModel deviceSettingViewModel) {
        super(programsEnumList);
        this.context = context;
        this.deviceSettingViewModel = deviceSettingViewModel;
        this.workoutViewModel = workoutViewModel;
        typeface = ResourcesCompat.getFont(getApp(), R.font.inter_bold);
//        initFitnessData();
    }


    public class ProgramsViewHolder extends RecyclerView.ViewHolder {

        private final ProgramsDetailsItemBinding binding;

        public ProgramsViewHolder(ProgramsDetailsItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.setDeviceSetting(deviceSettingViewModel);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProgramsViewHolder(ProgramsDetailsItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindView(@NonNull RecyclerView.ViewHolder viewHolder, ProgramsEnum programInfo, int position, int size) {
        final ProgramsViewHolder holder = (ProgramsViewHolder) viewHolder;

        boolean isDefaultProgram = programInfo.getProgramType() != WorkoutIntDef.FITNESS_TESTS;

        holder.binding.setIsDefaultProgram(isDefaultProgram);

        holder.binding.fitnessPickGender.setVisibility(isDefaultProgram ? View.GONE : View.VISIBLE);
        holder.binding.fitnessAgeUnit.setVisibility(isDefaultProgram ? View.GONE : View.VISIBLE);
        holder.binding.fitnessPickWeight.setVisibility(isDefaultProgram ? View.GONE : View.VISIBLE);
        holder.binding.fitnessWeightUnit.setVisibility(isDefaultProgram ? View.GONE : View.VISIBLE);
        holder.binding.fitnessPickAge.setVisibility(isDefaultProgram ? View.GONE : View.VISIBLE);
        holder.binding.fitnessPickHeight.setVisibility(isDefaultProgram ? View.GONE : View.VISIBLE);
        holder.binding.fitnessHeightUnit.setVisibility(isDefaultProgram ? View.GONE : View.VISIBLE);
      //  holder.binding.btnChoose.setVisibility(isDefaultProgram ? View.VISIBLE : View.INVISIBLE);

        if (!isDefaultProgram) initFitnessSelected(holder.binding, programInfo);

        holder.binding.tvProgramName.setText(programInfo.getProgramName());
//        holder.binding.vBG.setBackgroundResource(programInfo.getPhoto() != 0 ? programInfo.getPhoto() : R.drawable.panel_bg_all_20_252e37_press);

        //photo 沒分機型
        int photo = programInfo.getPhoto() != 0 ? programInfo.getPhoto() : R.drawable.panel_bg_all_20_252e37_press;
        if (programInfo == ProgramsEnum.CUSTOM && !isTreadmill) {
            photo = R.drawable.btn_banner_custom_bike;
        }
        holder.binding.vBG.setBackgroundResource(photo);
        holder.binding.ivDiagram.setBackgroundResource(isTreadmill ? programInfo.getDiagramTreadmill() : programInfo.getDiagramBike());
        holder.binding.ovalSpeed.setVisibility(programInfo.getDiagramTreadmill() == 0 || !isTreadmill ? View.INVISIBLE : View.VISIBLE);
        holder.binding.ovalIncline.setVisibility(programInfo.getDiagramTreadmill() == 0 || !isTreadmill ? View.INVISIBLE : View.VISIBLE);

        String valueStr;
        switch (programInfo) {
            case ARMY:
            case NAVY:
            case AIR_FORCE:
            case PEB:
            case COAST_GUARD:
            case MARINE_CORPS:
                if (programInfo.getCode() == ProgramsEnum.ARMY.getCode()) {
                    valueStr = String.format("%s %s", getArmyTarget(), context.getString(getUnit(DISTANCE2)));
                } else if (programInfo.getCode() == ProgramsEnum.MARINE_CORPS.getCode()) {
                    valueStr = String.format("%s %s", getMarinesTarget(), context.getString(getUnit(DISTANCE2)));
                } else {
                    valueStr = String.format("%s %s", getNavyTarget(), context.getString(getUnit(DISTANCE2)));
                }
                String str = context.getString(programInfo.getProgramDesc(), valueStr);

                holder.binding.tvProgramDesc.setText(setSpannableStr(valueStr, str));

                break;
            case GERKIN:
            case WFI:
            case CTT_PREDICTION:

           //     if ("en_US".equalsIgnoreCase(LanguageUtils.getLan())) {

                String valueStr2;
                if (programInfo == ProgramsEnum.CTT_PREDICTION) {
                    valueStr2 = context.getString(R.string.of_max_heart_rate_80);
                } else {
                    valueStr2 = context.getString(R.string.of_max_heart_rate_85);
                }

                try {
                    String str2 = context.getString(programInfo.getProgramDesc(), valueStr2);
                    holder.binding.tvProgramDesc.setText(setSpannableStr(valueStr2, str2));
                } catch (Exception e) {
                    e.printStackTrace();
                    holder.binding.tvProgramDesc.setText(context.getString(programInfo.getProgramDesc()));
                }
//                } else {
//                    holder.binding.tvProgramDesc.setText(context.getString(programInfo.getProgramDesc()));
//                }
                break;
            case CTT_PERFORMANCE:

                try {
                    holder.binding.tvProgramDesc.setText(setSpannableStr(context.getString(R.string.for_12_minutes), context.getString(programInfo.getProgramDesc())));
                } catch (Exception e) {
                    e.printStackTrace();
                    holder.binding.tvProgramDesc.setText(context.getString(programInfo.getProgramDesc()));
                }
//                if ("en_US".equalsIgnoreCase(LanguageUtils.getLan())) {
//                    holder.binding.tvProgramDesc.setText(setSpannableStr(context.getString(R.string.for_12_minutes), context.getString(programInfo.getProgramDesc())));
//                } else {
//                    holder.binding.tvProgramDesc.setText(context.getString(programInfo.getProgramDesc()));
//
//                }
                break;
            case FITNESS_TEST:

                holder.binding.tvProgramDesc.setText(setSpannableStr(context.getString(R.string.of_max_heart_rate_85), context.getString(programInfo.getProgramDesc())));
                break;
            default:
                holder.binding.tvProgramDesc.setText(isTreadmill ? programInfo.getProgramDesc() : programInfo.getProgramDescResBike());
        }
        holder.binding.btnBannerBack.setOnClickListener(v -> onItemClickListener.onItemClick(false));
        holder.binding.btnBannerForward.setOnClickListener(v -> onItemClickListener.onItemClick(true));

        if (isDefaultProgram) {
            holder.binding.vBG.setOnClickListener(view -> onImageClickListener.onImageClick(programInfo));
        } else {
            holder.binding.vBG.setOnClickListener(null);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(boolean isForward);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnImageClickListener {
        void onImageClick(ProgramsEnum programsEnum);
    }

    private OnImageClickListener onImageClickListener;

    public void setOnImageClickListener(OnImageClickListener onImageClickListener) {
        this.onImageClickListener = onImageClickListener;
    }

    List<String> list1;
    List<String> list2;
    List<String> list3;
    List<String> list4;

    private void initFitnessData() {

        list1 = new ArrayList<>(1);
        for (int i = AGE_MIN; i <= AGE_MAX; i++) {
            list1.add(String.valueOf(i));
        }

        list2 = new ArrayList<>(1);
        for (int i = getMinWeight(); i <= getMaxWeight(); i++) {
            list2.add(String.valueOf(i));
        }

        list3 = new ArrayList<>(1);
        list3.add(context.getString(R.string.Female));
        list3.add(context.getString(R.string.Male));

        //Height
        list4 = new ArrayList<>(1);
        for (int i = getMinHeight(); i <= getMaxHeight(); i++) {
            list4.add(String.valueOf(i));
        }
    }

    @SuppressWarnings("unchecked")
    private void initFitnessSelected(ProgramsDetailsItemBinding binding, ProgramsEnum programsEnum) {

        initFitnessData();

        binding.tvProgramDesc.setTextSize(44);

        OptionsPickerView<String> fitnessPickAge = binding.fitnessPickAge;
        OptionsPickerView<String> fitnessPickWeight = binding.fitnessPickWeight;

        fitnessPickAge.setData(list1);
        fitnessPickAge.setVisibleItems(8);
        fitnessPickAge.setNormalItemTextColor(ContextCompat.getColor(context.getApplicationContext(), R.color.color5a7085));
        fitnessPickAge.setTextSize(54, false);
        fitnessPickAge.setCurved(true);
        fitnessPickAge.setCyclic(true);
        fitnessPickAge.setTextBoundaryMargin(0, true);
        fitnessPickAge.setCurvedArcDirection(WheelView.CURVED_ARC_DIRECTION_CENTER);
        fitnessPickAge.setCurvedArcDirectionFactor(1.0f);
        fitnessPickAge.setSelectedItemTextColor(ContextCompat.getColor(context.getApplicationContext(), R.color.white));
        // fitnessPickAge.setOpt1SelectedPosition(AGE_DEF - AGE_MIN, false);
        fitnessPickAge.setOnOptionsSelectedListener((opt1Pos, opt1Data, opt2Pos, opt2Data, opt3Pos, opt3Data) -> {
            if (opt1Data == null) {
                return;
            }
            workoutViewModel.selYO.set(Integer.parseInt(opt1Data));
        });

        fitnessPickWeight.setData(list2);
        fitnessPickWeight.setVisibleItems(8);
        //   opv1.setDividerPaddingForWrap(10, true);
        fitnessPickWeight.setNormalItemTextColor(ContextCompat.getColor(context.getApplicationContext(), R.color.color5a7085));
        fitnessPickWeight.setTextSize(54, false);
        fitnessPickWeight.setCurved(true);
        fitnessPickWeight.setCyclic(true);
        fitnessPickWeight.setTextBoundaryMargin(0, true);
        fitnessPickWeight.setCurvedArcDirection(WheelView.CURVED_ARC_DIRECTION_CENTER);
        fitnessPickWeight.setCurvedArcDirectionFactor(1.0f);
        fitnessPickWeight.setSelectedItemTextColor(ContextCompat.getColor(context.getApplicationContext(), R.color.white));
        fitnessPickWeight.setOnOptionsSelectedListener((opt1Pos, opt1Data, opt2Pos, opt2Data, opt3Pos, opt3Data) -> {
            if (opt1Data == null) {
                return;
            }
            int weight = Integer.parseInt(opt1Data);
            workoutViewModel.selWeightIU.set(UNIT_E == IMPERIAL ? weight : FormulaUtil.kg2lb(weight));
            workoutViewModel.selWeightMU.set(UNIT_E == METRIC ? weight : FormulaUtil.lb2kg(weight));
        });

        fitnessPickWeight.setOpt1SelectedPosition((int) (UNIT_E == IMPERIAL ? userProfileViewModel.getWeight_imperial() - WEIGHT_IU_MIN : userProfileViewModel.getWeight_metric() - WEIGHT_MU_MIN), false);
        fitnessPickAge.setOpt1SelectedPosition(userProfileViewModel.getUserAge() - AGE_MIN, false);


        if (programsEnum.getProgramType() == WorkoutIntDef.FITNESS_TESTS) {
            if (programsEnum != ProgramsEnum.GERKIN && programsEnum != ProgramsEnum.WFI) {

                showGender(binding.fitnessPickGender);
            } else {
                binding.fitnessPickGender.setVisibility(View.GONE);
            }
        } else {
            binding.fitnessPickGender.setVisibility(View.GONE);
        }

        if (programsEnum == ProgramsEnum.WFI) {
            showHeight(binding.fitnessPickHeight, binding.fitnessHeightUnit);
        } else {
            binding.fitnessPickHeight.setVisibility(View.GONE);
            binding.fitnessHeightUnit.setVisibility(View.GONE);
        }
    }

    private void showGender(OptionsPickerView<String> fitnessPickGender) {
        fitnessPickGender.setVisibility(View.VISIBLE);

        fitnessPickGender.setData(list3);
        fitnessPickGender.setVisibleItems(2);
        //   opv1.setDividerPaddingForWrap(10, true);
        fitnessPickGender.setNormalItemTextColor(ContextCompat.getColor(context.getApplicationContext(), R.color.color5a7085));
        fitnessPickGender.setTextSize(54, false);
        fitnessPickGender.setCurved(true);
        fitnessPickGender.setCyclic(false);
        fitnessPickGender.setTextBoundaryMargin(0, true);
        fitnessPickGender.setCurvedArcDirection(WheelView.CURVED_ARC_DIRECTION_CENTER);
        fitnessPickGender.setCurvedArcDirectionFactor(1.0f);
        fitnessPickGender.setSelectedItemTextColor(ContextCompat.getColor(context.getApplicationContext(), R.color.white));
        fitnessPickGender.setOnOptionsSelectedListener((opt1Pos, opt1Data, opt2Pos, opt2Data, opt3Pos, opt3Data) -> {
            if (opt1Data == null) {
                return;
            }
            workoutViewModel.selGender.set(opt1Pos);
        });

        fitnessPickGender.setOpt1SelectedPosition(userProfileViewModel.getUserGender(), false);
    }


    private void showHeight(OptionsPickerView<String> fitnessPickHeight, TextView tvHeightUnit) {
        fitnessPickHeight.setVisibility(View.VISIBLE);
        tvHeightUnit.setVisibility(View.VISIBLE);
        fitnessPickHeight.setData(list4);
        fitnessPickHeight.setVisibleItems(8);
        //   opv1.setDividerPaddingForWrap(10, true);
        fitnessPickHeight.setNormalItemTextColor(ContextCompat.getColor(context.getApplicationContext(), R.color.color5a7085));
        fitnessPickHeight.setTextSize(54, false);
        fitnessPickHeight.setCurved(true);
        fitnessPickHeight.setCyclic(false);
        fitnessPickHeight.setTextBoundaryMargin(0, true);
        fitnessPickHeight.setCurvedArcDirection(WheelView.CURVED_ARC_DIRECTION_CENTER);
        fitnessPickHeight.setCurvedArcDirectionFactor(1.0f);
        fitnessPickHeight.setSelectedItemTextColor(ContextCompat.getColor(context.getApplicationContext(), R.color.white));
        fitnessPickHeight.setOnOptionsSelectedListener((opt1Pos, opt1Data, opt2Pos, opt2Data, opt3Pos, opt3Data) -> {
            if (opt1Data == null) return;

            int height = Integer.parseInt(opt1Data);
            workoutViewModel.selHeightIU.set(UNIT_E == IMPERIAL ? height : FormulaUtil.kg2lb(height));
            workoutViewModel.selWeightMU.set(UNIT_E == METRIC ? height : FormulaUtil.lb2kg(height));
        });

        fitnessPickHeight.setOpt1SelectedPosition((int) (UNIT_E == IMPERIAL ? userProfileViewModel.getHeight_imperial() - HEIGHT_IU_MIN : userProfileViewModel.getHeight_metric() - HEIGHT_MU_MIN), false);
    }

    private SpannableString setSpannableStr(String str1, String str2) {

        SpannableString spannableString = new SpannableString(str2);

        try {
            int startIndex = str2.indexOf(str1);
            int endIndex = startIndex + str1.length();
            spannableString.setSpan(new CustomTypefaceSpan("", typeface), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getApp(), R.color.colore24b44)), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return spannableString;

    }

}
