package com.dyaco.spirit_commercial.support.base_component;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import androidx.viewbinding.ViewBinding;

import com.dyaco.spirit_commercial.support.MsgEvent;
import com.dylanc.viewbinding.base.ViewBindingUtil;
//有反射
public class BasePopupMsgWindow<VB extends ViewBinding> extends PopupWindow {

    private VB binding;

    protected Context mContext;
    protected View parentView;

    public BasePopupMsgWindow(Context context) {

        this.mContext = context;

        binding = ViewBindingUtil.inflateWithGeneric(this, LayoutInflater.from(context));
        setContentView(binding.getRoot());

        setWidth(WRAP_CONTENT);
        setHeight(WRAP_CONTENT);

        setFocusable(false);
        setOutsideTouchable(false);


        setWindowLayoutType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);

    }


    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
    }

    private OnCustomDismissListener onCustomDismissListener;

    public void superDismiss() {
        super.dismiss();
        if (onCustomDismissListener != null) {
            onCustomDismissListener.onDismiss();
        }
    }

    @Override
    public void dismiss() {

        superDismiss();

        if (onCustomDismissListener != null) {
            onCustomDismissListener.onStartDismiss(returnValue);
        }
        binding = null;
    }

    public void setOnCustomDismissListener(OnCustomDismissListener onCustomDismissListener) {
        this.onCustomDismissListener = onCustomDismissListener;
    }

    public interface OnCustomDismissListener {

        void onStartDismiss(MsgEvent value);

        void onDismiss();
    }

    MsgEvent returnValue;
    protected void returnValue(MsgEvent value) {
        returnValue = value;
    }

    public VB getBinding() {
        return binding;
    }

}
