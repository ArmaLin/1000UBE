package com.dyaco.spirit_commercial.login;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.FragmentCreateAccountGenderBinding;
import com.dyaco.spirit_commercial.support.base_component.BaseBindingDialogFragment;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;

public class CreateAccountGenderFragmentBottom extends BaseBindingDialogFragment<FragmentCreateAccountGenderBinding> {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDialogType(GENERAL.TRANSLATION_X);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView();

    }

    private void initView() {
        getBinding().btnClose.setOnClickListener(v -> dismiss());

        getBinding().btnBack.setOnClickListener(v -> {
         //   dismiss();
            ((MainActivity) requireActivity()).navController.navigate(R.id.createAccountAgeFragment);
        });

        getBinding().btnNext.setOnClickListener(v -> {
          //  dismiss();
            ((MainActivity) requireActivity()).navController.navigate(R.id.createAccountWeightFragment);
        });

        getBinding().btnFemale.setOnClickListener(v -> selectGender());

        getBinding().btnMale.setOnClickListener(v -> selectGender());
    }

    private void selectGender() {
        getBinding().maleText.setTextColor(getBinding().btnMale.isChecked() ?
                ContextCompat.getColor(requireActivity(), R.color.color1396ef) : ContextCompat.getColor(requireActivity(), R.color.color5a7085));
        getBinding().femaleText.setTextColor(getBinding().btnFemale.isChecked() ?
                ContextCompat.getColor(requireActivity(), R.color.color1396ef) : ContextCompat.getColor(requireActivity(), R.color.color5a7085));
    }
}