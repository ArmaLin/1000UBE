package com.dyaco.spirit_commercial.alert_message;

import static com.dyaco.spirit_commercial.App.getApp;
import static com.dyaco.spirit_commercial.MainActivity.isTreadmill;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.INVALID_TEST_HR_OUT_OF_RANGE;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.INVALID_TEST_RPM_OUT_OF_RANGE;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.MEDIA_PHYSICAL_KEYS;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.WARRING_HR_REACHED_TARGET;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.WARRING_HR_TOO_HIGH;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.WARRING_NO_HR;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.WindowWarringNoButtonBinding;
import com.dyaco.spirit_commercial.support.CustomTypefaceSpan;
import com.dyaco.spirit_commercial.support.RxTimer;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;
import com.dyaco.spirit_commercial.support.utils.LanguageUtils;
import com.dyaco.spirit_commercial.viewmodel.WorkoutViewModel;
import com.dyaco.spirit_commercial.workout.programs.ProgramsEnum;

public class WarringNoButtonWindow extends BasePopupWindow<WindowWarringNoButtonBinding> {

    private WorkoutViewModel w;
    public WarringNoButtonWindow(Context context, int type, WorkoutViewModel w) {
        super(context, 300, 480, 0, GENERAL.TRANSLATION_Y, false, true, true, true);
        this.w = w;
        TextView tvWarring = getBinding().tvWarring;
        int dismissTime = 4000;
        // TODO: 少了 WARRING_NO_RPM
        switch (type) {
            case WARRING_NO_HR:
                tvWarring.setText(R.string.no_heart_rate_device_detected_finishing_the_program);
                break;
            case WARRING_HR_TOO_HIGH:
                tvWarring.setText(R.string.your_heart_rate_is_too_high_finishing_the_program);
                break;
            case WARRING_HR_REACHED_TARGET:
                tvWarring.setText(R.string.you_have_just_reached_target_heart_rate_starting_cool_down);
                break;
            case MEDIA_PHYSICAL_KEYS:

                String str;

                if (isTreadmill) {
                    if (w.selProgram != ProgramsEnum.HEART_RATE) {
                        str = context.getString(R.string.please_use_physical_buttons_to_control_speed_and_incline);
                    } else {
                        str = context.getString(R.string.please_use_physical_buttons_to_control_speed);
                    }
                } else {
                    str = context.getString(R.string.please_use_physical_buttons_to_control_level);
                }

                if ("en_US".equalsIgnoreCase(LanguageUtils.getLan())) {

                    Typeface typeface = ResourcesCompat.getFont(getApp(), R.font.inter_bold);
                    String valueStr = context.getString(R.string.physical_buttons);
                    SpannableString spannableString = new SpannableString(str);
                    try {
                        int startIndex = str.indexOf(valueStr);
                        int endIndex = startIndex + valueStr.length();
                        spannableString.setSpan(new CustomTypefaceSpan("", typeface), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getApp(), R.color.colore24b44)), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    tvWarring.setText(spannableString);
                } else {
                    int s;
                    if (isTreadmill) {
                        if (w.selProgram != ProgramsEnum.HEART_RATE) {
                            s = R.string.please_use_physical_buttons_to_control_speed_and_incline;
                        } else {
                            s = R.string.please_use_physical_buttons_to_control_speed;
                        }
                    } else {
                        s = R.string.please_use_physical_buttons_to_control_level;
                    }
                    tvWarring.setText(s);
                }

                dismissTime = 1500;
                break;
            case INVALID_TEST_RPM_OUT_OF_RANGE:
                tvWarring.setText(R.string.INVALID_TEST_RPM_OUT_OF_RANGE);
                break;
            case INVALID_TEST_HR_OUT_OF_RANGE:
                tvWarring.setText(R.string.INVALID_TEST_HR_OUT_OF_RANGE);
                break;
        }

        new RxTimer().timer(dismissTime, number -> {
            try {
                dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}
