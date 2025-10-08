package com.dyaco.spirit_commercial.support.mediaapp;

import android.graphics.Color;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.dyaco.spirit_commercial.R;

public class QuickReplyItemTouchCallback extends ItemTouchHelper.Callback {
    private AppManagerAdapter mAdapter;
    private boolean mIsLongPressDragEnabled = true;

    public QuickReplyItemTouchCallback(AppManagerAdapter adapter) { //传入适配器
        mAdapter = adapter;

    }

    public void setLongPressDragEnabled(boolean isLongPressDragEnabled) {
        mIsLongPressDragEnabled = isLongPressDragEnabled;
    }


    @Override
    public boolean isItemViewSwipeEnabled() { //是否启用左右滑动
        return false;
    }


    /**
     * 是否長按拖曳
     */
    @Override
    public boolean isLongPressDragEnabled() {
//        return mIsLongPressDragEnabled;
        return false;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        //在這個回調方法裡我們回到我們需要的使用的動作功能
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;  //拖曳 這裡設定的UP 與 DOWN 表示允許上下拖曳
        int swipeFlags = ItemTouchHelper.ACTION_STATE_IDLE;         //滑動 這裡設定的ACTION_STATE_IDLE 表示我們將滑動動作設定為空閒
        return makeMovementFlags(dragFlags, swipeFlags);
   
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        //用於上下移動Item的回呼方法，在這個方法裡我們要主動將Adapter裡的資料互相替換位置
        mAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());

        //回傳 true表示我們已經將Adapter裡的資料互相替換位置
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        //左右滑動處理

    }

    //設定滑動item的背景
    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        //判斷選取狀態
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            if (viewHolder == null) return;
            viewHolder.itemView.setAlpha(0.9f);//1-0
//            viewHolder.itemView.setBackgroundColor(Color.BLACK);
//             viewHolder.itemView.setBackgroundColor(viewHolder.itemView.getContext().getResources().getColor(R.color.color0DAC87));
             viewHolder.itemView.setBackgroundColor(ContextCompat.getColor(viewHolder.itemView.getContext(), R.color.color252e37));
        }

//        if (actionState == ItemTouchHelper.ACTION_STATE_IDLE) {
//            viewHolder.itemView.setBackgroundColor(viewHolder.itemView.getContext().getResources().getColor(R.color.color0DAC87));
//        }

        Log.d("MMMMDDDDDD", "onSelectedChanged: " + actionState);

        super.onSelectedChanged(viewHolder, actionState);
    }

    //清除滑動item的背景
    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        // 恢复
        viewHolder.itemView.setBackgroundColor(Color.TRANSPARENT);
        viewHolder.itemView.setAlpha(1);

        //滑太快會亂掉，只好重整  >>  改成在onItemMove處理
      //  mAdapter.updateDataSort();
        super.clearView(recyclerView, viewHolder);
    }
}