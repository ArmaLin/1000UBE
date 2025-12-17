package com.dyaco.spirit_commercial.dashboard_training;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.dyaco.spirit_commercial.App.UNIT_E;
import static com.dyaco.spirit_commercial.App.getApp;
import static com.dyaco.spirit_commercial.MainActivity.isTreadmill;
import static com.dyaco.spirit_commercial.MainActivity.userProfileViewModel;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.getArmyTarget;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.getMarinesTarget;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.getNavyTarget;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.IMPERIAL;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.METRIC;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.MT_TARGET_STEPS_INC;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.POWER_DFT;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.POWER_MAX;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.POWER_MIN;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.TARGET_CALORIES_DEF;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.TARGET_CALORIES_MAX;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.TARGET_CALORIES_MIN;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.TARGET_METS_DEF;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.TARGET_METS_MAX;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.TARGET_METS_MIN;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.TARGET_STEPS_DEF;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.TARGET_STEPS_MAX;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.TARGET_STEPS_MIN;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.TARGET_TIME_DEF;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.WEIGHT_IU_MIN;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.WEIGHT_MU_MIN;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.getMaxWeight;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.getMinWeight;
import static com.dyaco.spirit_commercial.support.intdef.UnitEnum.DISTANCE2;
import static com.dyaco.spirit_commercial.support.intdef.UnitEnum.getUnit;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.ProgramsDetailsItemBinding;
import com.dyaco.spirit_commercial.support.CommonUtils;
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

        //  holder.binding.fitnessPickGender.setVisibility(isDefaultProgram ? View.GONE : VISIBLE);
//        holder.binding.fitnessAgeUnit.setVisibility(isDefaultProgram ? View.GONE : VISIBLE);
//        holder.binding.fitnessPickWeight.setVisibility(isDefaultProgram ? View.GONE : VISIBLE);
//        holder.binding.fitnessWeightUnit.setVisibility(isDefaultProgram ? View.GONE : VISIBLE);
//        holder.binding.fitnessPickAge.setVisibility(isDefaultProgram ? View.GONE : VISIBLE);
//        holder.binding.fitnessPickHeight.setVisibility(isDefaultProgram ? View.GONE : VISIBLE);
//        holder.binding.fitnessHeightUnit.setVisibility(isDefaultProgram ? View.GONE : VISIBLE);

//        if (!isDefaultProgram) initFitnessSelected(holder.binding, programInfo);

        holder.binding.tvProgramName.setText(programInfo.getProgramName());
//        holder.binding.vBG.setBackgroundResource(programInfo.getPhoto() != 0 ? programInfo.getPhoto() : R.drawable.panel_bg_all_20_252e37_press);

        //photo 沒分機型
        int photo = programInfo.getPhoto() != 0 ? programInfo.getPhoto() : R.drawable.panel_bg_all_20_252e37_press;
        if (programInfo == ProgramsEnum.CUSTOM && !isTreadmill) {
            photo = R.drawable.btn_banner_custom_bike;
        }
        holder.binding.vBG.setBackgroundResource(photo);
        holder.binding.ivDiagram.setBackgroundResource(isTreadmill ? programInfo.getDiagramTreadmill() : programInfo.getDiagramBike());
        holder.binding.ovalSpeed.setVisibility(programInfo.getDiagramTreadmill() == 0 || !isTreadmill ? INVISIBLE : VISIBLE);
        holder.binding.ovalIncline.setVisibility(programInfo.getDiagramTreadmill() == 0 || !isTreadmill ? INVISIBLE : VISIBLE);

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


        if (programInfo == ProgramsEnum.MANUAL ||
                programInfo == ProgramsEnum.CALORIES ||
                programInfo == ProgramsEnum.WATTS ||
                programInfo == ProgramsEnum.METS ||
                programInfo == ProgramsEnum.STEPS) {
            holder.binding.timePicker.setVisibility(VISIBLE);
            holder.binding.gWingate.setVisibility(GONE);
            initTimePicker(holder.binding, programInfo);

        } else if (programInfo == ProgramsEnum.WINGATE_TEST) {

            initWingateTestPick(holder.binding);

        } else {

            holder.binding.timePicker.setVisibility(INVISIBLE);
            holder.binding.wPicker.setVisibility(INVISIBLE);
            holder.binding.kcalUnit.setVisibility(INVISIBLE);
            holder.binding.wUnit.setVisibility(INVISIBLE);
            holder.binding.gWingate.setVisibility(GONE);
        }
    }


    List<String> listWingate;
    private void initWingateTestPick(ProgramsDetailsItemBinding binding) {
        binding.gWingate.setVisibility(VISIBLE);
        binding.timePicker.setVisibility(INVISIBLE);
        binding.wPicker.setVisibility(INVISIBLE);
        binding.kcalUnit.setVisibility(INVISIBLE);
        binding.wUnit.setVisibility(INVISIBLE);


        listWingate = new ArrayList<>();

        listWingate = new ArrayList<>(1);
        for (int i = getMinWeight(); i <= getMaxWeight(); i++) {
            listWingate.add(String.valueOf(i));
        }



        OptionsPickerView<String> weightPicker = binding.opvBodyWeight;

        weightPicker.setData(listWingate);
        weightPicker.setVisibleItems(8);
        weightPicker.setNormalItemTextColor(ContextCompat.getColor(context.getApplicationContext(), R.color.color5a7085));
        weightPicker.setTextSize(54, false);
        weightPicker.setCurved(true);
        weightPicker.setCyclic(true);
        weightPicker.setTextBoundaryMargin(0, true);
        weightPicker.setCurvedArcDirection(WheelView.CURVED_ARC_DIRECTION_CENTER);
        weightPicker.setCurvedArcDirectionFactor(1.0f);
        weightPicker.setSelectedItemTextColor(ContextCompat.getColor(context.getApplicationContext(), R.color.white));

        weightPicker.setOnOptionsSelectedListener((opt1Pos, opt1Data, opt2Pos, opt2Data, opt3Pos, opt3Data) -> {
            if (opt1Data == null) {
                return;
            }

            Log.d("initTimePicker", "Time/Cal Select: " + opt1Data);

            int weight = Integer.parseInt(opt1Data);
            workoutViewModel.selWeightIU.set(UNIT_E == IMPERIAL ? weight : FormulaUtil.kg2lb(weight));
            workoutViewModel.selWeightMU.set(UNIT_E == METRIC ? weight : FormulaUtil.lb2kg(weight));
        });

        weightPicker.setOpt1SelectedPosition((int) (UNIT_E == IMPERIAL ? userProfileViewModel.getWeight_imperial() - WEIGHT_IU_MIN : userProfileViewModel.getWeight_metric() - WEIGHT_MU_MIN), false);







    }

    private void initTimePicker(ProgramsDetailsItemBinding binding, ProgramsEnum programsEnum) {
        // 1. 設定 LayoutParams (調整 MarginEnd)
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) binding.timePicker.getLayoutParams();

        // WATTS 要調整 wheel picker 位置
        if (programsEnum == ProgramsEnum.WATTS || programsEnum == ProgramsEnum.METS) {
            layoutParams.setMarginEnd((int) CommonUtils.dp2px(239));
            binding.wPicker.setVisibility(View.VISIBLE);

            if (programsEnum == ProgramsEnum.WATTS) {
                binding.wUnit.setVisibility(View.VISIBLE);
            } else {
                binding.wUnit.setVisibility(View.INVISIBLE);
            }

            // 初始化 Watts 資料列表 (每次都 new 新的以防重複)
            listW = new ArrayList<>();


            if (programsEnum == ProgramsEnum.WATTS) {
                for (int i = POWER_MIN; i <= POWER_MAX; i++) {
                    listW.add(String.valueOf(i));
                }
            } else {
                int min = (int) (TARGET_METS_MIN);
                int max = (int) (TARGET_METS_MAX);

                for (int i = min; i <= max; i++) {
                    // 將 int 轉回 double 字串，例如 14 -> "1.4"
                    listW.add(String.format(java.util.Locale.US, "%.1f", i / 10.0));
                }
            }

        } else {
            layoutParams.setMarginEnd((int) CommonUtils.dp2px(439));
            binding.wPicker.setVisibility(View.INVISIBLE);
            binding.wUnit.setVisibility(View.INVISIBLE);
        }


        binding.timePicker.setLayoutParams(layoutParams);

        // 2. 準備 Time/Calories 資料列表
        int defaultOpt = TARGET_TIME_DEF;
        list5 = new ArrayList<>(); // 重置列表

        if (programsEnum == ProgramsEnum.CALORIES) {
            defaultOpt = TARGET_CALORIES_DEF - TARGET_CALORIES_MIN;
            binding.kcalUnit.setVisibility(View.VISIBLE);
            binding.kcalUnit.setText(R.string.kcal);
            for (int i = TARGET_CALORIES_MIN; i <= TARGET_CALORIES_MAX; i++) {
                list5.add(String.valueOf(i));
            }
        } else if (programsEnum == ProgramsEnum.STEPS) {


            // 計算預設選中的索引位置： (預設值 1200 - 最小值 300) / 間隔 50 = 索引 18
            defaultOpt = (TARGET_STEPS_DEF - TARGET_STEPS_MIN) / MT_TARGET_STEPS_INC;

            binding.kcalUnit.setVisibility(View.VISIBLE);
            binding.kcalUnit.setText(R.string.steps);

            // 迴圈：從 MIN 開始，每次增加 INC (50)，直到 MAX
            for (int i = TARGET_STEPS_MIN; i <= TARGET_STEPS_MAX; i += MT_TARGET_STEPS_INC) {
                list5.add(String.valueOf(i));
            }


//        } else if (programsEnum == ProgramsEnum.METS) {
//            defaultOpt = TARGET_METS_DEF - TARGET_METS_MIN;
//            binding.kcalUnit.setVisibility(INVISIBLE);
//            for (int i = TARGET_METS_MIN; i <= TARGET_METS_MAX; i++) {
//                list5.add(String.valueOf(i));
//            }

        } else {

            //time
            binding.kcalUnit.setVisibility(View.INVISIBLE);
            list5 = CommonUtils.generateTimeOptions(0, 99);
        }

        // 3. 設定 Time/Calories Picker (左側)
        OptionsPickerView<String> timePicker = binding.timePicker;

        timePicker.setData(list5);
        timePicker.setVisibleItems(8);
        timePicker.setNormalItemTextColor(ContextCompat.getColor(context.getApplicationContext(), R.color.color5a7085));
        timePicker.setTextSize(54, false);
        timePicker.setCurved(true);
        timePicker.setCyclic(true);
        timePicker.setTextBoundaryMargin(0, true);
        timePicker.setCurvedArcDirection(WheelView.CURVED_ARC_DIRECTION_CENTER);
        timePicker.setCurvedArcDirectionFactor(1.0f);
        timePicker.setSelectedItemTextColor(ContextCompat.getColor(context.getApplicationContext(), R.color.white));

        timePicker.setOnOptionsSelectedListener((opt1Pos, opt1Data, opt2Pos, opt2Data, opt3Pos, opt3Data) -> {
            if (opt1Data == null) {
                return;
            }

            Log.d("initTimePicker", "Time/Cal Select: " + opt1Data);

            if (programsEnum == ProgramsEnum.CALORIES) {
                workoutViewModel.targetCalories.set(Double.parseDouble(opt1Data));
            } else if (programsEnum == ProgramsEnum.STEPS) {
                workoutViewModel.targetSteps.set(Double.parseDouble(opt1Data));
//            } else if (programsEnum == ProgramsEnum.METS) {
//                workoutViewModel.targetMets.set(Double.parseDouble(opt1Data));
            } else {
                workoutViewModel.selWorkoutTime.set(opt1Pos * 60);
            }
        });

        timePicker.setOpt1SelectedPosition(defaultOpt, false);


        if (programsEnum == ProgramsEnum.WATTS || programsEnum == ProgramsEnum.METS) {
            OptionsPickerView<String> wPicker = binding.wPicker;
            wPicker.setData(listW);
            wPicker.setVisibleItems(8);
            wPicker.setNormalItemTextColor(ContextCompat.getColor(context.getApplicationContext(), R.color.color5a7085));
            wPicker.setTextSize(54, false);
            wPicker.setCurved(true);
            wPicker.setCyclic(true);
            wPicker.setTextBoundaryMargin(0, true);
            wPicker.setCurvedArcDirection(WheelView.CURVED_ARC_DIRECTION_CENTER);
            wPicker.setCurvedArcDirectionFactor(1.0f);
            wPicker.setSelectedItemTextColor(ContextCompat.getColor(context.getApplicationContext(), R.color.white));


            wPicker.setOnOptionsSelectedListener((opt1Pos, opt1Data, opt2Pos, opt2Data, opt3Pos, opt3Data) -> {

                if (opt1Data == null) {
                    return;
                }


                if (programsEnum == ProgramsEnum.WATTS) {
                    workoutViewModel.selConstantPowerW.set(Integer.parseInt(opt1Data));
                } else {
                    float metsFloat = Float.parseFloat(opt1Data);
                    int finalMets = Math.round(metsFloat * 10);
                    workoutViewModel.targetMets.set(finalMets);
                }
                Log.d("MMMMMMWWWWEEEE", "POWER: " + workoutViewModel.targetMets.get());

            });

            if (programsEnum == ProgramsEnum.WATTS) {
                // 設定 Watts 預設選中位置
                wPicker.setOpt1SelectedPosition(POWER_DFT - POWER_MIN, false);
            } else {
                wPicker.setOpt1SelectedPosition((int) (TARGET_METS_DEF - TARGET_METS_MIN), false);
            }
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

    List<String> list5;
    List<String> listW;


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
