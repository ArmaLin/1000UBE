package com.dyaco.spirit_commercial.support;

import static android.content.Context.WINDOW_SERVICE;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.CLOSE_SETTINGS;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.TextViewCompat;
import androidx.lifecycle.Observer;

import com.dyaco.spirit_commercial.App;
import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.R;
import com.jeremyliao.liveeventbus.LiveEventBus;

public class FloatingWidget {
    private final Context mContext;
    //    private Button button;
    private TextView view2;

    public FloatingWidget(Context context) {
        this.mContext = context;
    }

    private WindowManager windowManager;
    boolean m_bOnClick = false;
    private long m_lStartTime;
    Observer<Boolean> observer1 = s -> {
        if (view2 != null) {
            Log.d("EEEWWWQQQAA", "11111111CLOSEEEEEEEEE ");
            view2.callOnClick();
        }
    };

    /**
     * @param type 0 wifi, 1 bt, 2 系統設定頁
     * @param from 0 工程, 1 一般
     */
    public void callSetting(int type, Class<MainActivity> backCls, int from) {
        App.isShowMediaMenuOnStop = false;
        App.SETTING_SHOW = true;


        LiveEventBus.get(CLOSE_SETTINGS, Boolean.class).observeForever(observer1);

        String action = Settings.ACTION_SETTINGS;
        if (type == 0) {
            action = Settings.ACTION_WIFI_SETTINGS;
        } else if (type == 1) {
            action = Settings.ACTION_BLUETOOTH_SETTINGS;
        } else if (type == 2) {
            action = Settings.ACTION_SETTINGS;
        }

//        action = Settings.ACTION_NFC_SETTINGS;

        Intent intent = new Intent();
        intent.setAction(action);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mContext.startActivity(intent);

        //只有設定頁面在動
        ((Activity) mContext).overridePendingTransition(R.anim.fade_in_3, 0);
        //MainActivity onResume
        //overridePendingTransition(0, R.anim.fade_out_3);

        if (from == 1) {
            showBackBtn(backCls);
        }

        //開啟系統設定頁面時，關閉上下Dashboard
//        ((MainActivity) mContext).showMediaMenu(false);
//        ((MainActivity) mContext).showMediaFloatingDashboard(false,true);
//

    }

    private void showBackBtn(Class<MainActivity> backCls) {
        windowManager = (WindowManager) mContext.getSystemService(WINDOW_SERVICE);
        //   LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
//        layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
//        //    layoutParams.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY;
//        layoutParams.gravity = Gravity.BOTTOM | Gravity.END;
//        layoutParams.format = PixelFormat.TRANSLUCENT;
//        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
//        layoutParams.width = 184;
//        layoutParams.height = 64;
//        layoutParams.x = 10;
//        layoutParams.y = 10;

        Typeface typeface = ResourcesCompat.getFont(mContext, R.font.inter_bold);
//        button = new Button(mContext);
//        button.setText(R.string.back);
//        button.setTextColor(ContextCompat.getColor(mContext, R.color.white));
//        button.setTextSize(26);
//        button.setTypeface(typeface);
//        button.setGravity(Gravity.CENTER);
//        button.setBackgroundResource(R.drawable.panel_bg_20_1c242a_btn);
//        windowManager.addView(button, layoutParams);

        WindowManager.LayoutParams layoutParams2 = new WindowManager.LayoutParams();
        layoutParams2.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        layoutParams2.gravity = Gravity.TOP | Gravity.START;
        layoutParams2.format = PixelFormat.TRANSLUCENT;
        layoutParams2.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams2.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams2.height = 64;
        view2 = new TextView(mContext);

        TypedArray ta = mContext.obtainStyledAttributes(new int[]{android.R.attr.selectableItemBackground});
        Drawable mDefaultFocusHighlightCache = ta.getDrawable(0);
        ta.recycle();
        view2.setForeground(mDefaultFocusHighlightCache);

        view2.setTextSize(32);
        view2.setTextColor(ContextCompat.getColor(mContext, R.color.white));
        view2.setText(R.string.back);
        view2.setTypeface(typeface);
        view2.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        view2.setPadding(30, 0, 0, 0);
        view2.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color1c242a));
        view2.setCompoundDrawablePadding(10);
        view2.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mContext, R.drawable.icon_back), null, null, null);
        TextViewCompat.setCompoundDrawableTintList(view2, ContextCompat.getColorStateList(mContext, R.color.white));

        windowManager.addView(view2, layoutParams2);


        new RxTimer().timer(1000, number -> {
            if (view2 != null) {
                view2.setOnClickListener(view -> goBack(backCls));
            }
        });

        //   button.setOnClickListener(v -> goBack(backCls));

        fullScreenImmersive(view2);
        //   fullScreenImmersive(button);
    }

    boolean isDone;

    private void goBack(Class<MainActivity> backCls) {
        if (isDone) return;
        try {
            App.isShowMediaMenuOnStop = true;
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setClass(mContext, backCls);
            mContext.startActivity(intent);
            //     windowManager.removeViewImmediate(button);
            //    button = null;
            ((Activity) mContext).overridePendingTransition(0, 0);

            new RxTimer().timer(200, number -> {
                if (view2 != null) {
                    windowManager.removeViewImmediate(view2);
                    view2 = null;
                }
            });


            LiveEventBus.get(CLOSE_SETTINGS, Boolean.class).removeObserver(observer1);

        } catch (Exception e) {
            e.printStackTrace();
        }

        isDone = true;
    }

    private void fullScreenImmersive(View view) {
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        view.setSystemUiVisibility(uiOptions);
    }
}
