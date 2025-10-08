package com.dyaco.spirit_commercial.maintenance_mode;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.FragmentMaintenanceTouchPanelBinding;
import com.dyaco.spirit_commercial.support.base_component.BaseBindingDialogFragment;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;

import java.util.Random;


public class MaintenanceTouchPanelFragment extends BaseBindingDialogFragment<FragmentMaintenanceTouchPanelBinding> {

    private AnimationSet animationSet;
    private FrameLayout.LayoutParams layoutParams;
    private DisplayMetrics displayMetrics;
    private int height;
    private int width;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDialogType(GENERAL.TRANSLATION_X);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initEvent();

        displayMetrics = Resources.getSystem().getDisplayMetrics();
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;

     //   getBinding().waterWaveViewWithMove.showWave();

        animationSet = (AnimationSet) AnimationUtils.loadAnimation(requireActivity().getApplicationContext(), R.anim.anim_set);
        layoutParams = new FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        layoutParams.width = 48;
        layoutParams.height = 48;
        View myButton = new View(requireActivity());
        myButton.setBackgroundResource(R.drawable.circle_1396ef);
        myButton.startAnimation(animationSet);

        showTap(myButton);

        getBinding().baseView.addView(myButton);

        myButton.setOnClickListener(v -> showTap(myButton));
    }

    private void showTap(View myButton) {
        layoutParams.leftMargin = new Random().nextInt((width - 20) + 1) + 20;
        layoutParams.topMargin  = new Random().nextInt((height - 20) + 1) + 20;
        myButton.setLayoutParams(layoutParams);
    }

    private void initEvent() {

        getBinding().btnDone.setOnClickListener(v -> dismiss());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}