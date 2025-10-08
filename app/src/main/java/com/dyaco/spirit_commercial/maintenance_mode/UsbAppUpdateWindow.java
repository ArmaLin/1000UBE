package com.dyaco.spirit_commercial.maintenance_mode;

import android.content.Context;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.WindowUpdateFirmwareBinding;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;

public class UsbAppUpdateWindow extends BasePopupWindow<WindowUpdateFirmwareBinding> {
    private final Context mContext;
    private final SeekBar downloadProgress;
    private TextView tvMin;
    private static final String TAG = "USB_UPDATE";

    public UsbAppUpdateWindow(Context context) {

        super(context, 0, 0, 0, GENERAL.FADE, false, false, true, false);
        mContext = context;
        tvMin = getBinding().tvMin;
        downloadProgress = getBinding().seekBar;

        getBinding().pp.setVisibility(View.INVISIBLE);

        getBinding().updateTitle.setText(R.string.software_update);
        downloadProgress.setProgress(0);
    }

    public void setProgress(int progress) {

        ((MainActivity) mContext).runOnUiThread(() -> {
            downloadProgress.setProgress(progress);
            if (progress >= 99) {
                downloadProgress.setProgress(100);
                getBinding().progress.setVisibility(View.VISIBLE);
            }

        });
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}
