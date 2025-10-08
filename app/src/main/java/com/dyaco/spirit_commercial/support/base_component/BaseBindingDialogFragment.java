package com.dyaco.spirit_commercial.support.base_component;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.viewbinding.ViewBinding;

import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;
import com.dylanc.viewbinding.base.ViewBindingUtil;

public abstract class BaseBindingDialogFragment<VB extends ViewBinding> extends DialogFragment {
    private VB binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.fragment_dialog);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = ViewBindingUtil.inflateWithGeneric(this, getLayoutInflater(), container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setCancelable(false);


        if (getDialog() != null) {
            Resources resources = requireActivity().getResources();
            int height = 0;
            int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                height = resources.getDimensionPixelSize(resourceId) * -1;
            }
            Window window = getDialog().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            getDialog().setOnShowListener(dialog -> {
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            });
            //   window.getDecorView().setPadding(0, 0, 0, height);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public VB getBinding() {
        return binding;
    }


    @Override
    public void onStart() {
        super.onStart();

        if (getDialog() != null) {
            Window window = getDialog().getWindow();

            //     window.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            fullScreenImmersive(window.getDecorView());
            WindowManager.LayoutParams params = window.getAttributes();

            window.setWindowAnimations(isAni ? animationStyle : 0);

            window.setGravity(gravity);
            params.width = setWidth;
            params.height = setHeight;
            window.setAttributes(params);

        }
    }

    private boolean isAni = true;

    public void setAni(boolean b) {
        isAni = b;
    }

    int animationStyle = R.style.left_animation;
    int gravity = Gravity.END;
    int setWidth = FrameLayout.LayoutParams.WRAP_CONTENT;
    int setHeight = FrameLayout.LayoutParams.MATCH_PARENT;

    protected void setDialogType(@GENERAL.animationType int type) {
        if (type == GENERAL.TRANSLATION_Y) {
            animationStyle = R.style.bottom_animation;
            gravity = Gravity.BOTTOM;
            setWidth = FrameLayout.LayoutParams.MATCH_PARENT;
            setHeight = FrameLayout.LayoutParams.WRAP_CONTENT;
        } else if (type == GENERAL.TRANSLATION_X) {
            animationStyle = R.style.left_animation;
            gravity = Gravity.END;
            setWidth = FrameLayout.LayoutParams.WRAP_CONTENT;
            setHeight = FrameLayout.LayoutParams.MATCH_PARENT;
        } else {
            animationStyle = R.style.fade_animation;
            gravity = Gravity.BOTTOM;
            setWidth = FrameLayout.LayoutParams.MATCH_PARENT;
            setHeight = FrameLayout.LayoutParams.WRAP_CONTENT;
        }
    }

    private void fullScreenImmersive(View view) {
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_FULLSCREEN;


        view.setSystemUiVisibility(uiOptions);
    }


}
