package com.dyaco.spirit_commercial.dashboard_training;

import static com.dyaco.spirit_commercial.App.UNIT_E;
import static com.dyaco.spirit_commercial.App.getApp;
import static com.dyaco.spirit_commercial.MainActivity.isTreadmill;
import static com.dyaco.spirit_commercial.support.CommonUtils.getAvatarSelectedFromTag;
import static com.dyaco.spirit_commercial.support.FormulaUtil.formatBigDecimal;
import static com.dyaco.spirit_commercial.support.FormulaUtil.formatBigDecimal2;
import static com.dyaco.spirit_commercial.support.FormulaUtil.km2mi;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.METRIC;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.ItemsNoDataBinding;
import com.dyaco.spirit_commercial.databinding.ItemsRankAllListBinding;
import com.dyaco.spirit_commercial.model.webapi.bean.GetGymMonthlyAllRankingBean;
import com.dyaco.spirit_commercial.settings.AvatarEntity;
import com.dyaco.spirit_commercial.support.GlideApp;

import java.util.List;

public class RankingAdapterAll extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final LayoutInflater mLayoutInflater;
    private final Typeface typefaceBold;
    private final Typeface typefaceRegular;
    private List<GetGymMonthlyAllRankingBean.DataMapDTO.DataDTO> rankingBeanList;
    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_MESSAGE = 1;
    private static final int VIEW_TYPE_FOOTER = 2;
    private int highLightTag;
    private final ColorStateList color13963f;
    private final ColorStateList colorAdb8c2;
    private final ColorStateList color5a7085;
    private Context mContext;

    RankingAdapterAll(Context context, int highLightTag) {
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.highLightTag = highLightTag;
        color13963f = ContextCompat.getColorStateList(context, R.color.color1396ef);
        colorAdb8c2 = ContextCompat.getColorStateList(context, R.color.colorADB8C2);
        color5a7085 = ContextCompat.getColorStateList(context, R.color.color5a7085);

        typefaceBold = ResourcesCompat.getFont(context, R.font.inter_bold);
        typefaceRegular = ResourcesCompat.getFont(context, R.font.inter_regular);

    }

    @SuppressLint("NotifyDataSetChanged")
    void setData2View(List<GetGymMonthlyAllRankingBean.DataMapDTO.DataDTO> list, int highLightTag) {
        rankingBeanList = list;
        this.highLightTag = highLightTag;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        if (viewType == VIEW_TYPE_MESSAGE) {
            viewHolder = new MyRecyclerViewHolder(ItemsRankAllListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
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
            final GetGymMonthlyAllRankingBean.DataMapDTO.DataDTO rankingBean = rankingBeanList.get(position);

            if (rankingBean.isSelf()) {
                viewHolder.binding.clBase.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color201396ef));
                viewHolder.binding.tvNo.setTextColor(color13963f);
                viewHolder.binding.tvMemberName.setTextColor(color13963f);
                viewHolder.binding.tvDistance.setTextColor(color13963f);
                viewHolder.binding.tvCal.setTextColor(color13963f);

            } else {
                viewHolder.binding.clBase.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color1c242a));
                viewHolder.binding.tvNo.setTextColor(colorAdb8c2);
                viewHolder.binding.tvMemberName.setTextColor(color5a7085);
                viewHolder.binding.tvDistance.setTextColor(colorAdb8c2);
                viewHolder.binding.tvCal.setTextColor(colorAdb8c2);
                viewHolder.binding.tvHours.setTypeface(typefaceRegular);
            }

            switch (highLightTag) {
                case 0:
                    viewHolder.binding.tvHours.setTextColor(color13963f);
                    viewHolder.binding.tvSpeed.setTextColor(colorAdb8c2);
                    viewHolder.binding.tvDistance.setTextColor(colorAdb8c2);
                    viewHolder.binding.tvCal.setTextColor(colorAdb8c2);

                    viewHolder.binding.tvHours.setTypeface(rankingBean.isSelf() ? typefaceBold : typefaceRegular);
                    viewHolder.binding.tvSpeed.setTypeface(typefaceRegular);
                    viewHolder.binding.tvDistance.setTypeface(typefaceRegular);
                    viewHolder.binding.tvCal.setTypeface(typefaceRegular);
                    break;
                case 1:
                    viewHolder.binding.tvHours.setTextColor(colorAdb8c2);
                    viewHolder.binding.tvSpeed.setTextColor(color13963f);
                    viewHolder.binding.tvDistance.setTextColor(colorAdb8c2);
                    viewHolder.binding.tvCal.setTextColor(colorAdb8c2);

                    viewHolder.binding.tvHours.setTypeface(typefaceRegular);
                    viewHolder.binding.tvSpeed.setTypeface(rankingBean.isSelf() ? typefaceBold : typefaceRegular);
                    viewHolder.binding.tvDistance.setTypeface(typefaceRegular);
                    viewHolder.binding.tvCal.setTypeface(typefaceRegular);
                    break;
                case 2:
                    viewHolder.binding.tvHours.setTextColor(colorAdb8c2);
                    viewHolder.binding.tvSpeed.setTextColor(colorAdb8c2);
                    viewHolder.binding.tvDistance.setTextColor(color13963f);
                    viewHolder.binding.tvCal.setTextColor(colorAdb8c2);

                    viewHolder.binding.tvHours.setTypeface(typefaceRegular);
                    viewHolder.binding.tvSpeed.setTypeface(typefaceRegular);
                    viewHolder.binding.tvDistance.setTypeface(rankingBean.isSelf() ? typefaceBold : typefaceRegular);
                    viewHolder.binding.tvCal.setTypeface(typefaceRegular);
                    break;
                case 3:
                    viewHolder.binding.tvHours.setTextColor(colorAdb8c2);
                    viewHolder.binding.tvSpeed.setTextColor(colorAdb8c2);
                    viewHolder.binding.tvDistance.setTextColor(colorAdb8c2);
                    viewHolder.binding.tvCal.setTextColor(color13963f);

                    viewHolder.binding.tvHours.setTypeface(typefaceRegular);
                    viewHolder.binding.tvSpeed.setTypeface(typefaceRegular);
                    viewHolder.binding.tvDistance.setTypeface(typefaceRegular);
                    viewHolder.binding.tvCal.setTypeface(rankingBean.isSelf() ? typefaceBold : typefaceRegular);
                    break;
            }

            viewHolder.binding.tvNo.setText(String.valueOf(rankingBean.getRanking()));
            viewHolder.binding.tvMemberName.setText(String.valueOf(rankingBean.getDisplayName()));

            //  double millSec = rankingBean.getPerformanceTime() * 60 * 60 * 1000;
            double millSec = rankingBean.getPerformanceTime() * 1000;
       //     viewHolder.binding.tvHours.setText(formatTime(millSec));
            viewHolder.binding.tvHours.setText(DateUtils.formatElapsedTime((long) (millSec / 1000)));

            double distance;
            if (isTreadmill) {
                //Distance
                distance = UNIT_E == METRIC ? formatBigDecimal(rankingBean.getPerformanceDistance()) : formatBigDecimal(km2mi(rankingBean.getPerformanceDistance()));
            } else {
                //Power
                distance = formatBigDecimal2(rankingBean.getPerformancePower());
            }

            viewHolder.binding.tvDistance.setText(String.valueOf(distance));

         //   Log.d("WWWWVBBBBBVVV", "onBindViewHolder: " + rankingBean.getPerformanceSpeed() +", "+ formatBigDecimal2(rankingBean.getPerformanceSpeed()) +",  "+formatBigDecimal2(km2mi(rankingBean.getPerformanceSpeed())));
            double speed = UNIT_E == METRIC ? formatBigDecimal2(rankingBean.getPerformanceSpeed()) : formatBigDecimal2(km2mi(rankingBean.getPerformanceSpeed()));
            viewHolder.binding.tvSpeed.setText(String.valueOf(speed));

//            viewHolder.binding.tvCal.setText(String.format("%sK", rankingBean.getPerformanceCalories()));
            viewHolder.binding.tvCal.setText(String.valueOf(rankingBean.getPerformanceCalories()));

//            if (rankingBean.getPhotoFileUrl() != null) {
//                viewHolder.binding.ivMemberAvatarPhoto.setVisibility(View.VISIBLE);
//                viewHolder.binding.ivMemberAvatar.setVisibility(View.INVISIBLE);
//                GlideApp.with(getApp())
//                        .load(rankingBean.getPhotoFileUrl())
//                        .circleCrop()
//                        .placeholder(R.drawable.shape_oval_grey)
//                        .into(viewHolder.binding.ivMemberAvatarPhoto);
//                return;
//            }

            if (rankingBean.getAvatarId() != null) {
                viewHolder.binding.ivMemberAvatarPhoto.setVisibility(View.INVISIBLE);
                viewHolder.binding.ivMemberAvatar.setVisibility(View.VISIBLE);
                GlideApp.with(getApp())
                        .load(getAvatarSelectedFromTag(rankingBean.getAvatarId(), false))
//                        .diskCacheStrategy(DiskCacheStrategy.NONE)
//                        .skipMemoryCache(true)
                        .fitCenter()
                        .into(viewHolder.binding.ivMemberAvatar);
                return;
            }


            if (rankingBean.getPhotoFileUrl() != null) {
                viewHolder.binding.ivMemberAvatarPhoto.setVisibility(View.VISIBLE);
                viewHolder.binding.ivMemberAvatar.setVisibility(View.INVISIBLE);
                GlideApp.with(getApp())
                        .load(rankingBean.getPhotoFileUrl())
                        .circleCrop()
                        .placeholder(R.drawable.shape_oval_grey)
                        .into(viewHolder.binding.ivMemberAvatarPhoto);
            }

        } else {
            final ViewHolderEmpty viewHolder = (ViewHolderEmpty) holder;
            viewHolder.binding.tvNodata.setText("");
        }
    }

    @Override
    public int getItemCount() {
        if (rankingBeanList == null || rankingBeanList.size() <= 0) {
            return 1;
        } else {
            return rankingBeanList.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = VIEW_TYPE_MESSAGE;

        if (rankingBeanList == null || rankingBeanList.size() <= 0) {
            viewType = VIEW_TYPE_EMPTY;
        }

        return viewType;
    }

    public static class MyRecyclerViewHolder extends RecyclerView.ViewHolder {
        private final ItemsRankAllListBinding binding;

        public MyRecyclerViewHolder(ItemsRankAllListBinding binding) {
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
