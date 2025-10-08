package com.dyaco.spirit_commercial.support.base_component;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

public abstract class BaseBindingActivity<VB extends ViewBinding> extends AppCompatActivity {
//public abstract class BaseBindingActivity<VB extends ViewBinding> extends HealthSdkActivity {

    private VB binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     //   binding = ViewBindingUtil.inflateWithGeneric(this, getLayoutInflater());
        binding = onCreateViewBinding(getLayoutInflater());
        setContentView(binding.getRoot());
    }
    protected abstract VB onCreateViewBinding(@NonNull LayoutInflater layoutInflater);

    public VB getBinding() {
        return binding;
    }

    @Override
    protected void onResume() {
        super.onResume();
      //  setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

// View.SYSTEM_UI_FLAG_HIDE_NAVIGATION 下面
//        View.SYSTEM_UI_FLAG_LAYOUT_STABLE ACTION BAR

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

    }

}