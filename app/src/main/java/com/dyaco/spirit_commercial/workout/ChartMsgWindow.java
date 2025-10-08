package com.dyaco.spirit_commercial.workout;

import static com.dyaco.spirit_commercial.MainActivity.isTreadmill;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.BAR_TYPE_BLANK;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.BAR_TYPE_INCLINE;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.WindowChartMsgBinding;
import com.dyaco.spirit_commercial.support.base_component.BasePopupMsgFWindow;

import java.util.Locale;

public class ChartMsgWindow extends BasePopupMsgFWindow<WindowChartMsgBinding> {

    public ChartMsgWindow(Context context, float value, int type) {
        super(context);

        if (type == BAR_TYPE_BLANK) {
            getBinding().msgValue.setBackgroundColor(ContextCompat.getColor(context, R.color.color11151a));
            getBinding().msgValue.setText(String.format("#%s", value));
            getBinding().msgValue.setTextSize(24);
            return;
        }

        if (isTreadmill) {
            getBinding().msgValue.setBackgroundResource(type == BAR_TYPE_INCLINE ? R.drawable.element_popup_diagram_incline : R.drawable.element_popup_diagram_speed);
            getBinding().msgValue.setText(type == BAR_TYPE_INCLINE ? String.format("%s%%", value) : value + " ");
        } else {
            getBinding().msgValue.setBackgroundResource(R.drawable.element_popup_diagram_level);
            getBinding().msgValue.setText(String.format(Locale.getDefault(), "%d ", (int) value));
        }
    }

    public void setMsgType(int type, float value) {
        if (isTreadmill) {
            getBinding().msgValue.setBackgroundResource(type == BAR_TYPE_INCLINE ? R.drawable.element_popup_diagram_incline : R.drawable.element_popup_diagram_speed);
            getBinding().msgValue.setText(type == BAR_TYPE_INCLINE ? String.format("%s%%", value) : value + " ");
        } else {
            getBinding().msgValue.setBackgroundResource(R.drawable.element_popup_diagram_level);
            getBinding().msgValue.setText(String.format(Locale.getDefault(), "%d ", (int) value));
        }

    }

    public void setMsgShow(boolean isShow) {

        try {
            getBinding().getRoot().setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected WindowChartMsgBinding onCreateViewBinding(@NonNull LayoutInflater layoutInflater) {
        return WindowChartMsgBinding.inflate(layoutInflater);
    }
}

