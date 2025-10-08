package com.dyaco.spirit_commercial.support.utils;

import android.view.View;

public class ViewUtils {
    public static void forceLayoutNow(View view) {
        view.measure(
            View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        );
        view.layout(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
    }
}
