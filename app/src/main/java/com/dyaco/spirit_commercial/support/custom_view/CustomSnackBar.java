package com.dyaco.spirit_commercial.support.custom_view;

import android.app.Activity;
import android.view.View;

import com.dyaco.spirit_commercial.R;
import com.google.android.material.snackbar.Snackbar;

public class CustomSnackBar {

    public CustomSnackBar() {
    }

    public static void showMessage(Activity activity, View view, String str) {

        // create an instance of the snackbar
        final Snackbar snackbar = Snackbar.make(view, str, Snackbar.LENGTH_SHORT);

        // inflate the custom_snackbar_view created previously
        View customSnackView = activity.getLayoutInflater().inflate(R.layout.custom_toast_layout, activity.findViewById(R.id.toast_layout));

        // set the background of the default snackbar as transparent
     //   snackbar.getView().setBackgroundColor(Color.TRANSPARENT);

        // now change the layout of the snackbar
        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();

        // set padding of the all corners as 0
        snackbarLayout.setPadding(0, 0, 0, 0);

        // add the custom snack bar layout to snackbar layout
        snackbarLayout.addView(customSnackView, 0);

        snackbar.show();



//        Snackbar snackbar = Snackbar.make(view, str, length);
//        View snackbarView = snackbar.getView();
//        //设置布局居中
//        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(snackbarView.getLayoutParams().width, snackbarView.getLayoutParams().height);
//        params.gravity = Gravity.CENTER;
//        snackbarView.setLayoutParams(params);
//        //文字居中
//        TextView message = snackbarView.findViewById(R.id.snackbar_text);
//        message.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
//        message.setGravity(Gravity.CENTER);
//        message.setMaxLines(1);
//        snackbar.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
//            @Override
//            public void onDismissed(Snackbar transientBottomBar, int event) {
//                super.onDismissed(transientBottomBar, event);
//                //Snackbar关闭
//            }
//
//            @Override
//            public void onShown(Snackbar transientBottomBar) {
//                super.onShown(transientBottomBar);
//                //Snackbar显示
//            }
//        });
//        snackbar.setAction("取消", new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //显示一个默认的Snackbar。
//                Snackbar.make(view, "我先走", BaseTransientBottomBar.LENGTH_LONG).show();
//            }
//        });
//        snackbar.show();

    }
}
