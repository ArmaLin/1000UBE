package com.dyaco.spirit_commercial.login;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.FragmentCreateAccountWelcomeBinding;
import com.dyaco.spirit_commercial.support.base_component.BaseBindingDialogFragment;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;

public class CreateAccountWelcomeFragmentBottom extends BaseBindingDialogFragment<FragmentCreateAccountWelcomeBinding> {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDialogType(GENERAL.TRANSLATION_X);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView();

    }

    private void initView() {

        getBinding().tvText1.setText(getString(R.string.welcome_anna, "Anna"));

        getBinding().btnClose.setOnClickListener(v -> dismiss());

        getBinding().btnSkip.setOnClickListener(v -> dismiss());

        getBinding().btnNext.setOnClickListener(v -> {

         //   ((MainActivity) requireActivity()).navController.navigate(QrcodeLoginFragmentDirections.actionQrcodeLoginFragmentToCreateAccountAgeFragment());
            ((MainActivity) requireActivity()).navController.navigate(R.id.createAccountAgeFragment);

        //    dismiss();

        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("LLKKKKD", "onDestroy: ");
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}