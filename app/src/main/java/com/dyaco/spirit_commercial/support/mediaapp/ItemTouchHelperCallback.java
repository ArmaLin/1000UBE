package com.dyaco.spirit_commercial.support.mediaapp;

import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ItemTouchHelperCallback extends ItemTouchHelper.Callback {
    List<AppStoreBean.AppUpdateBeansDTO> appManagerBeanList;

    public ItemTouchHelperCallback(List<AppStoreBean.AppUpdateBeansDTO> appManagerBeanList) {
        this.appManagerBeanList = appManagerBeanList;
    }

    // 设置支持的拖动和滑动的方向
    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN; // 支持上下拖动
        int swipeFlags = 0; // 不支持滑动
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    // 在拖动过程中不断调用，用于刷新RecyclerView的显示
    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder source, @NonNull RecyclerView.ViewHolder target) {
        int fromPosition = source.getAdapterPosition();
        int toPosition = target.getAdapterPosition();
        // 更新数据集中的位置
        Log.d("PPPOOOPP", "onMove: " + appManagerBeanList.size());
        Collections.swap(appManagerBeanList, fromPosition, toPosition);
        // 更新RecyclerView的显示
        Objects.requireNonNull(recyclerView.getAdapter()).notifyItemMoved(fromPosition, toPosition);
        Log.d("MMMMDDDDDD", "onMove: " + fromPosition + "," + toPosition);
        return true;
    }

    // 在滑动过程中调用，可以用于实现滑动删除等功能
    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        // 不做任何操作
//        Log.d("MMMMDDDDDD", "onSwiped: " + direction);

    }


    //设置滑动时item的背景透明度
    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

//        Log.d("MMMMDDDDDD", "onChildDraw: " + actionState);
//   //     float alpha = 1 - Math.abs(dX) / viewHolder.itemView.getWidth();
//        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
//
//            //透明度动画
//            viewHolder.itemView.setAlpha(0.5f);//1-0
//            viewHolder.itemView.setBackgroundColor(Color.RED);
//            //设置滑出大小
////            viewHolder.itemView.setScaleX(alpha);
////            viewHolder.itemView.setScaleY(alpha);
//        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }


    //设置滑动item的背景
    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        //判断选中状态
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
         //   viewHolder.itemView.setAlpha(0.5f);//1-0
            viewHolder.itemView.setBackgroundColor(Color.BLACK);
            // viewHolder.itemView.setBackgroundColor(viewHolder.itemView.getContext().getResources().getColor(R.color.color0DAC87));
        }

//        if (actionState == ItemTouchHelper.ACTION_STATE_IDLE) {
//            viewHolder.itemView.setBackgroundColor(viewHolder.itemView.getContext().getResources().getColor(R.color.color0DAC87));
//        }

        Log.d("MMMMDDDDDD", "onSelectedChanged: " + actionState);

        super.onSelectedChanged(viewHolder, actionState);
    }


    //清除滑动item的背景
    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        // 恢复
        viewHolder.itemView.setBackgroundColor(Color.TRANSPARENT);

        //防止出现复用问题 而导致条目不显示 方式一
    //    viewHolder.itemView.setAlpha(1);//1-0

        Log.d("MMMMDDDDDD", "clearView: ");
        super.clearView(recyclerView, viewHolder);
    }


    /**
     * 是否打开长按拖拽效果
     *
     * @return
     */
    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

}
