package com.dyaco.spirit_commercial.dashboard_media;

import static com.dyaco.spirit_commercial.App.getApp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.databinding.ItemsMediassListBinding;
import com.dyaco.spirit_commercial.support.ProgressPayload;
import com.dyaco.spirit_commercial.support.interaction.TouchClickUtil;
import com.dyaco.spirit_commercial.support.room.spirit.spirit_entity.MediaAppsEntity;

import java.util.List;

public class MediaListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<MediaAppsEntity> mediaAppEnumList;
    private static final int VIEW_TYPE_MESSAGE = 1;

    GridLayoutManager gridLayoutManager;

    private Context mContext;

    MediaListAdapter(Context context, GridLayoutManager gridLayoutManager) {
        this.mContext = context;
        this.gridLayoutManager = gridLayoutManager;

//        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//            @Override
//            public int getSpanSize(int position) {
//                if (mediaAppEnumList == null || mediaAppEnumList.isEmpty()) {
//                    return 4; // the item in position now takes up 4 spans
//                }
//                return 1;
//            }
//        });
    }

    @SuppressLint("NotifyDataSetChanged")
    void setData2View(List<MediaAppsEntity> list) {
        mediaAppEnumList = list;
        //  notifyItemInserted(mediaAppEnumList.size());
       //   Log.d("MMEEEEDDDIIAAA", "setData2View: " + mediaAppEnumList.size());
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyRecyclerViewHolder(ItemsMediassListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, i);
        } else {
            final MyRecyclerViewHolder viewHolder = (MyRecyclerViewHolder) holder;
            final int position = viewHolder.getAdapterPosition();
            final MediaAppsEntity appsEntity = mediaAppEnumList.get(position);
            String payload = payloads.get(0).toString();
            //更新右上紅點
            if ("NOTIFY".equals(payload)) {
                viewHolder.binding.ivBadge.setVisibility(appsEntity.isUpdate() ? View.VISIBLE : View.INVISIBLE);
            } else {
                ProgressPayload p = ((ProgressPayload) payloads.get(0));
                Log.d("MMEEEEDDDIIAAA", "XXXXXXonBindViewHolder: " + p.getPayload() +","+ p.getProgress() +","+ p.getPackageName());
                if ("PROGRESS".equals(p.getPayload())) {
                    viewHolder.binding.tvPer.setText(String.valueOf(p.getProgress()));
                    viewHolder.binding.ivBadge.setVisibility(appsEntity.isUpdate() ? View.VISIBLE : View.INVISIBLE);
                    if (p.getProgress() >= 99) {
                        viewHolder.binding.tvPer.setText(String.valueOf(100));
                    }
                }
            }
        }
    }

    boolean isFingerClick;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {
        final MyRecyclerViewHolder viewHolder = (MyRecyclerViewHolder) holder;
        final int position = viewHolder.getAdapterPosition();
        final MediaAppsEntity appsEntity = mediaAppEnumList.get(position);

        //  Log.d("MMEEEEDDDIIAAA", "@@@@@@########onBindViewHolder: ");
        Glide.with(getApp()).
                load(appsEntity.getAppIconM()).
                into(viewHolder.binding.btnMediaApp);


        viewHolder.binding.tvMediaApp.setText(appsEntity.getAppName());

//        viewHolder.binding.clBase.setOnClickListener(v -> {
//            if (onItemClickListener != null) {
//                onItemClickListener.onItemClick(appsEntity);
//            }
//        });

        viewHolder.binding.clBase.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(appsEntity);
            }
        });

        viewHolder.binding.ivBadge.setVisibility(appsEntity.isUpdate() ? View.VISIBLE : View.INVISIBLE);


        viewHolder.binding.clBase.setOnTouchListener(TouchClickUtil.createTouchListener(0.7f));


//        viewHolder.binding.clBase.setOnTouchListener((view, motionEvent) -> {
//
//            int action = motionEvent.getAction();
//            int width = view.getWidth();
//            int height = view.getHeight();
//
//            switch (action) {
//                case MotionEvent.ACTION_DOWN:
//                    isFingerClick = true;
//                    viewHolder.binding.clBase.setAlpha(0.7f);
//                    return true;
//                case MotionEvent.ACTION_MOVE:
//                    float x = motionEvent.getX();
//                    float y = motionEvent.getY();
//                    if (x < 0 || x > width || y < 0 || y > height) {
//                        viewHolder.binding.clBase.setAlpha(1f);
//                        isFingerClick = false;
//                    }
//                    return true;
//                case MotionEvent.ACTION_UP:
//                    viewHolder.binding.clBase.setAlpha(1f);
//                    if (isFingerClick) {
//                        view.performClick();
//                        isFingerClick = false;
//                    }
//                    return true;
//                default:
//                    return false;
//            }
//        });
    }

    @Override
    public int getItemCount() {
        if (mediaAppEnumList == null) {
            return 0;
        } else {
            return mediaAppEnumList.size();
        }
    }

    public static class MyRecyclerViewHolder extends RecyclerView.ViewHolder {
        private final ItemsMediassListBinding binding;

        public MyRecyclerViewHolder(ItemsMediassListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }


    /**
     * Item Interface Callback
     */
    public interface OnItemClickListener {
        void onItemClick(MediaAppsEntity bean);
    }

    private OnItemClickListener onItemClickListener;

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    //使用
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    void updateData(int position, MediaAppsEntity mediaAppsEntity) {
//        MediaAppUtils.MEDIA_APP_LIST.set(position, mediaAppsEntity);
        notifyItemChanged(position);
    }


    void updateNotify(int position) {
        notifyItemChanged(position, "NOTIFY");
    }


    void updateProgress(int progress) {
        notifyItemChanged(MainActivity.forcePkNameSort, new ProgressPayload(MainActivity.forcePkName, progress, "PROGRESS"));
    }
}
