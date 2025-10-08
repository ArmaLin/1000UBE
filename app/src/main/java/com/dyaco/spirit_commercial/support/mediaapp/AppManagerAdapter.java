package com.dyaco.spirit_commercial.support.mediaapp;

import static com.dyaco.spirit_commercial.App.getApp;
import static com.dyaco.spirit_commercial.maintenance_mode.MaintenanceAppManagerFragment.UPDATE_N;
import static com.dyaco.spirit_commercial.maintenance_mode.MaintenanceAppManagerFragment.UPDATE_X;
import static com.dyaco.spirit_commercial.maintenance_mode.MaintenanceAppManagerFragment.UPDATE_Y;
import static com.dyaco.spirit_commercial.support.mediaapp.MediaAppUtils.CONSOLE_MEDIA_APP_LIST;
import static com.dyaco.spirit_commercial.support.mediaapp.MediaAppUtils.MAX_MEDIA_APP_COUNT;
import static com.dyaco.spirit_commercial.support.mediaapp.MediaAppUtils.updateMediaAppSortByPkName;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.ItemsAppManagerListBinding;
import com.dyaco.spirit_commercial.databinding.ItemsNoDataBinding;
import com.dyaco.spirit_commercial.support.GlideApp;

import java.util.Collections;
import java.util.List;

public class AppManagerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<AppStoreBean.AppUpdateBeansDTO> appStoreList;
    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_MESSAGE = 1;

    private final ColorStateList colorStateList1;
    private final ColorStateList colorStateList2;

    private final ColorStateList colorStateList3;
    private final ColorStateList colorStateList4;


    private final Context mContext;
    private final AppUpdateManager appUpdateManager;

    public AppManagerAdapter(Context context, AppUpdateManager appUpdateManager) {
        this.mContext = context;
        this.appUpdateManager = appUpdateManager;
        colorStateList1 = ContextCompat.getColorStateList(mContext, R.color.color8192a2);
        colorStateList2 = ContextCompat.getColorStateList(mContext, R.color.color1396ef);
        colorStateList3 = ContextCompat.getColorStateList(mContext, R.color.white);
        colorStateList4 = ContextCompat.getColorStateList(mContext, R.color.white_20);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData2View(List<AppStoreBean.AppUpdateBeansDTO> list) {
        appStoreList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        if (viewType == VIEW_TYPE_MESSAGE) {
            viewHolder = new MyRecyclerViewHolder(ItemsAppManagerListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        } else {
            viewHolder = new ViewHolderEmpty(ItemsNoDataBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }
        return viewHolder;
    }


    //只更新指定欄位
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i, @NonNull List<Object> payloads) {

        if (payloads.isEmpty()) {
            onBindViewHolder(holder, i);
        } else {
            final MyRecyclerViewHolder viewHolder = (MyRecyclerViewHolder) holder;
            final int position = viewHolder.getAdapterPosition();
            final AppStoreBean.AppUpdateBeansDTO appStoreBean = appStoreList.get(position);
            String payload = payloads.get(0).toString();
            if ("sort".equals(payload)) {

//                if (appStoreBean.getIsUpdate() != UPDATE_X) {
//                    viewHolder.binding.tvSort.setText(String.valueOf(appStoreBean.getSort() + 1));
//                } else {
//                    viewHolder.binding.tvSort.setText("");
//                }

                ColorStateList sColor;
                if (appStoreBean.getIsUpdate() != UPDATE_X) {
                    sColor = colorStateList3;
                } else {
                    sColor = colorStateList4;
                }

                viewHolder.binding.tvSort.setTextColor(sColor);
                viewHolder.binding.tvSort.setText(String.valueOf(appStoreBean.getSort() + 1));

            } else if ("bbbbb".equals(payload)) {
            } else {

            }
        }
    }

    private int lastPosition = -1;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {

        final int pp = holder.getAdapterPosition();
        if (pp > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.fade_in_0_5s);
            holder.itemView.startAnimation(animation);
            lastPosition = pp;
        }

        int viewType = getItemViewType(i);
        if (viewType == VIEW_TYPE_MESSAGE) {
            final MyRecyclerViewHolder viewHolder = (MyRecyclerViewHolder) holder;
            final int position = viewHolder.getAdapterPosition();
            final AppStoreBean.AppUpdateBeansDTO appStoreBean = appStoreList.get(position);
            // viewHolder.binding.ivAppIcon.setBackgroundResource(appStoreBean.getAppIcon());

            //排序相關功能
            appStoreList.get(position).setSort(position);

//            if (appStoreBean.getIsUpdate() != UPDATE_X) {
//                viewHolder.binding.tvSort.setText(String.valueOf(appStoreBean.getSort() + 1));
//            } else {
//                viewHolder.binding.tvSort.setText("");
//            }

            ColorStateList sColor;
            if (appStoreBean.getIsUpdate() != UPDATE_X) {
                sColor = colorStateList3;
            } else {
                sColor = colorStateList4;
            }

            viewHolder.binding.tvSort.setTextColor(sColor);
            viewHolder.binding.tvSort.setText(String.valueOf(appStoreBean.getSort() + 1));


            viewHolder.binding.tvAppName.setText(appStoreBean.getAppName());
//            viewHolder.binding.tvAppName.setText(appStoreBean.getAppName() + ", " + appStoreBean.getSort());

            if (!appStoreBean.getIcon().isEmpty()) {
                GlideApp.with(getApp()).
                        load(appStoreBean.getIcon().get(0).getAppIconMediumUrl()).
                        placeholder(R.drawable.panel_bg_all_12_323f4b).
                        error(R.drawable.panel_bg_all_12_323f4b).
                        into(viewHolder.binding.ivAppIcon);
            }
            //appstore_update.json的VersionName
            viewHolder.binding.tvVersionName.setText(appStoreBean.getVersion());


            String consoleVersionName = appUpdateManager.getPackageInfoVersionName(appStoreBean.getPackageName());

            //已安裝的WEB
            if (appStoreBean.getGmsNeeded().equalsIgnoreCase("YES") && appStoreBean.getIsUpdate() == UPDATE_N) {
                consoleVersionName = "*WEB";
            }

//            if (appStoreBean.getGmsNeeded().equalsIgnoreCase("YES") && appStoreBean.getIsUpdate() == UPDATE_X) {
//                if (consoleVersionName.isEmpty()) {
//                    //    consoleVersionName = "WEB";
//                } else {
//                    consoleVersionName = consoleVersionName + " - " + "是網頁 但已安裝 APP ";
//                }
//                Log.d("FFFFFFFFF", "onBindViewHolder: " + appStoreBean.getIsUpdate() + "," + appStoreBean.getAppName());
//            }


            //#####console資料庫沒有此app，就不顯示刪除按鈕########
            viewHolder.binding.btnDelete.setVisibility(appStoreBean.getIsUpdate() == UPDATE_X ? View.INVISIBLE : View.VISIBLE);

            //console資料庫沒有此app，但已安裝
            if (!consoleVersionName.isEmpty()) {
                viewHolder.binding.btnDelete.setVisibility(View.VISIBLE);
            }


            ColorStateList textColor = colorStateList2;
            ColorStateList dragColor;

            //Console沒安裝此APP
            if (consoleVersionName.isEmpty()) {
                consoleVersionName = mContext.getString(R.string.not_yet_been_installed);
                textColor = colorStateList1;
                //     dragColor = colorStateList4;
            }

            //console資料庫沒有此app，三條槓變灰
            if (appStoreBean.getIsUpdate() == UPDATE_X) {
                dragColor = colorStateList4;
            } else {
                dragColor = colorStateList3;
            }

            //設定三條槓的顏色
            ImageViewCompat.setImageTintList(viewHolder.binding.dragLine, dragColor);

            viewHolder.binding.tvConsoleVersionName.setText(consoleVersionName);
            viewHolder.binding.tvConsoleVersionName.setTextColor(textColor);

            viewHolder.binding.btnUpdate.setVisibility(appStoreBean.getIsUpdate() == UPDATE_N ? View.VISIBLE : View.INVISIBLE);
            viewHolder.binding.btnUpdateOK.setVisibility(appStoreBean.getIsUpdate() == UPDATE_N ? View.INVISIBLE : View.VISIBLE);

            if (CONSOLE_MEDIA_APP_LIST.size() >= MAX_MEDIA_APP_COUNT) {
                if (appStoreBean.getIsUpdate() == UPDATE_Y) {
                    //超過可安裝數量，已安裝，可UPDATE
                    viewHolder.binding.btnUpdateOK.setEnabled(true);
                } else {
                    //超過可安裝數量，尚未安裝，不可安裝
                    viewHolder.binding.btnUpdateOK.setEnabled(false);
                }
            } else {
                //尚未超過可安裝數量
                viewHolder.binding.btnUpdateOK.setEnabled(true);
            }


            String updateOk = appStoreBean.getIsUpdate() == UPDATE_X ? mContext.getString(R.string.Install) : mContext.getString(R.string.Update);
            viewHolder.binding.btnUpdateOK.setText(updateOk);

            Drawable drawable = ContextCompat.getDrawable(mContext, appStoreBean.getIsUpdate() == UPDATE_X ? R.drawable.ic_download : R.drawable.icon_update);
            viewHolder.binding.btnUpdateOK.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);

            viewHolder.binding.btnUpdateOK.setOnClickListener(view ->
                    onItemClickListener.onItemClick(appStoreBean));


            viewHolder.binding.btnDelete.setOnClickListener(view ->
                    onDelListener.onItemClick(appStoreBean));


            viewHolder.binding.dragLine.setOnTouchListener((v, event) -> {

                //沒被選擇的就不能拉
                if (appStoreBean.getIsUpdate() == UPDATE_X) return false;

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(viewHolder);
                }
                return false;
            });


            viewHolder.binding.ivAppIcon.setOnClickListener(v -> {
                if (onItemClickListener2 != null) {
                    onItemClickListener2.onItemClick(appStoreBean);
                }
            });


//            viewHolder.binding.ivAppIcon.setOnTouchListener((view, motionEvent) -> {
//                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
//                    viewHolder.binding.ivAppIcon.setAlpha(0.5f);
//                    return true;
//                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
//                    viewHolder.binding.ivAppIcon.setAlpha(1f);
//                    view.performClick();
//                    return false;
//                } else if (motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
//                    viewHolder.binding.ivAppIcon.setAlpha(1f);
//                    return false;
//                } else {
//                    return false;
//                }
//            });
        }
    }

    public interface OnStartDragListener {
        void onStartDrag(RecyclerView.ViewHolder viewHolder);
    }

    OnStartDragListener mDragStartListener;

    public void setOnDragStartListener(OnStartDragListener mDragStartListener) {
        this.mDragStartListener = mDragStartListener;
    }

    @Override
    public int getItemCount() {
        if (appStoreList == null || appStoreList.size() <= 0) {
            return 1;
        } else {
            return appStoreList.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = VIEW_TYPE_MESSAGE;

        if (appStoreList == null || appStoreList.isEmpty()) {
            viewType = VIEW_TYPE_EMPTY;
        }

        return viewType;
    }

    public static class MyRecyclerViewHolder extends RecyclerView.ViewHolder {
        private final ItemsAppManagerListBinding binding;

        public MyRecyclerViewHolder(ItemsAppManagerListBinding binding) {
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
        void onItemClick(AppStoreBean.AppUpdateBeansDTO bean);
    }

    private OnItemClickListener onItemClickListener;

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    public interface OnItemClickListener2 {
        void onItemClick(AppStoreBean.AppUpdateBeansDTO bean);
    }

    private OnItemClickListener2 onItemClickListener2;

    public void setOnItemClickListener2(OnItemClickListener2 onItemClickListener2) {
        this.onItemClickListener2 = onItemClickListener2;
    }


    public interface OnDelListener {
        void onItemClick(AppStoreBean.AppUpdateBeansDTO bean);
    }

    private OnDelListener onDelListener;

    public void setOnDelListener(OnDelListener onDelListener) {
        this.onDelListener = onDelListener;
    }

    public void updateData(int position, AppStoreBean.AppUpdateBeansDTO mediaAppsEntity) {
        appStoreList.set(position, mediaAppsEntity);
        notifyItemChanged(position);
    }


    /**
     * 提供给QuickReplyItemTouchCallback使用的移動Item位置方法
     */
    public void onItemMove(int fromPosition, int toPosition) {

        //拖曳太快
        if (Math.abs(fromPosition - toPosition) > 1) return;
        //沒被選擇的就不能拉
        if (appStoreList.get(toPosition).getIsUpdate() == UPDATE_X) return;

        Collections.swap(appStoreList, fromPosition, toPosition);//更换數據List的位置    //from: Netflix(2), to:X(1)

        notifyItemMoved(fromPosition, toPosition); //更换Adapter Item的视图位置

        appStoreList.get(fromPosition).setSort(fromPosition);
        appStoreList.get(toPosition).setSort(toPosition);


        updateMediaAppSortByPkName(appStoreList.get(fromPosition).getPackageName(), fromPosition, false);
        updateMediaAppSortByPkName(appStoreList.get(toPosition).getPackageName(), toPosition, true);


        //即時更新sort的顯示
        notifyItemChanged(fromPosition, "sort");
        notifyItemChanged(toPosition, "sort");


//        List<MediaAppsEntity> fromEntity = CONSOLE_MEDIA_APP_LIST.stream()
//                .filter(obj -> obj.getPackageName().equalsIgnoreCase(appStoreList.get(toPosition).getPackageName()))
//                .collect(Collectors.toList());
//
//        List<MediaAppsEntity> toEntity = CONSOLE_MEDIA_APP_LIST.stream()
//                .filter(obj -> obj.getPackageName().equalsIgnoreCase(appStoreList.get(fromPosition).getPackageName()))
//                .collect(Collectors.toList());
//
//        if (!fromEntity.isEmpty() && !toEntity.isEmpty()) {
//            fromEntity.get(0).setSort(toPosition);
//            toEntity.get(0).setSort(fromPosition);
//            CONSOLE_MEDIA_APP_LIST.set(toPosition, fromEntity.get(0));
//            CONSOLE_MEDIA_APP_LIST.set(fromPosition, toEntity.get(0));
//        }
    }

    public void updateDataSort() {
        //    notifyItemRangeChanged(0, MAX_MEDIA_APP_COUNT);


//        notifyItemRangeChanged(0, MAX_MEDIA_APP_COUNT,"aaaaa");
        //    notifyItemChanged(0, "aaaaa");
    }

    public List<AppStoreBean.AppUpdateBeansDTO> getAppStoreList() {

        return appStoreList;
    }

}
