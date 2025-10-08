package com.dyaco.spirit_commercial.login;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dyaco.spirit_commercial.NavigationLoginDirections;
import com.dyaco.spirit_commercial.databinding.FragmentGotoworkoutLoginBinding;
import com.dyaco.spirit_commercial.support.base_component.BaseBindingBottomDialogFragment;


public class GotoWorkoutsFragmentBottom extends BaseBindingBottomDialogFragment<FragmentGotoworkoutLoginBinding> {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getBinding().btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        getBinding().actionImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //   Navigation.findNavController(v).navigate(NavigationLoginDirections.actionGlobalMainBlankFragment());
                parent.navController.navigate(NavigationLoginDirections.actionGlobalMainBlankFragment());
                //   ((MainActivity)requireActivity()).navController.setGraph(R.navigation.navigation_one);
            }
        });
    }

}