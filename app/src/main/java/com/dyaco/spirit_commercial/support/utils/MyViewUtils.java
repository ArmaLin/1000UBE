package com.dyaco.spirit_commercial.support.utils;

import android.graphics.Rect;
import android.view.TouchDelegate;
import android.view.View;

public class MyViewUtils {

    public static void expandTouchArea(View view, int size) {
        View parentView = (View) view.getParent();
        parentView.post(() -> {
            Rect rect = new Rect();
            view.getHitRect(rect);

            rect.top -= size;
            rect.bottom += size;
            rect.left -= size;
            rect.right += size;

            parentView.setTouchDelegate(new TouchDelegate(rect, view));
        });
    }

//    @SuppressLint("ResourceType")
//    public static void setBtnBackground(View view ,int imgResId) {
//        view.setBackground(MyApplication.getInstance().getDrawable(imgResId));
//    }





}
