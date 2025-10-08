package com.dyaco.spirit_commercial.dashboard_media;

import static com.dyaco.spirit_commercial.MainActivity.isUs;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.PORT_EZ_CAST;

import android.content.Context;
import android.view.View;

import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.WindowHowToMirrorBinding;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;

public class HowToMirrorWindow extends BasePopupWindow<WindowHowToMirrorBinding> {
    public static boolean isWOn;
    private Context mContext;

    public HowToMirrorWindow(Context context, int type, int animateType) {
        super(context, 500, 0, 0, animateType, false, true, true, true);
        isWOn = true;
        mContext = context;
        initView();

        if (type == PORT_EZ_CAST) {
            getBinding().view1.setVisibility(View.VISIBLE);
            getBinding().view2.setVisibility(View.GONE);
            getBinding().tvHowTo1.setText(isUs ? R.string.how_to_use_mirroring : R.string.how_to_use_miracast);
        } else {
            getBinding().view1.setVisibility(View.GONE);
            getBinding().view2.setVisibility(View.VISIBLE);
            getBinding().tvHowTo2.setText(isUs ? R.string.how_to_use_wired_mirroring : R.string.how_to_use_wirecast);
        }
    }


    private void initView() {
        getBinding().btnClose.setOnClickListener(v -> dismiss());
    }

    @Override
    public void dismiss() {
        super.dismiss();
        isWOn = false;
    }
}
