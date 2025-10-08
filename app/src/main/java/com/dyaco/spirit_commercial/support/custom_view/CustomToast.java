package com.dyaco.spirit_commercial.support.custom_view;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dyaco.spirit_commercial.R;

public class CustomToast {

    public CustomToast() {
    }

    public static void showToast(Activity activity, String showText) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast_layout, activity.findViewById(R.id.toast_layout));

        TextView text = layout.findViewById(R.id.text);
        text.setText(showText);

        Toast toast = new Toast(activity.getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0); //顯示位置
        toast.setDuration(Toast.LENGTH_SHORT); //顯示時間長短
        toast.setView(layout);
        toast.show();
    }
}
