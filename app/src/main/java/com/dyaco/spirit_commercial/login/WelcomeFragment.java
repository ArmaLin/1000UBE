package com.dyaco.spirit_commercial.login;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dyaco.spirit_commercial.databinding.FragmentWelcomeBinding;
import com.dyaco.spirit_commercial.support.base_component.BaseBindingFragment;

public class WelcomeFragment extends BaseBindingFragment<FragmentWelcomeBinding> {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }


    @Override
    public void onResume() {
        super.onResume();

        parent.navController.navigate(WelcomeFragmentDirections.actionWelcomeFragmentToNavigationLogin());
//        parent.navController.navigate(WelcomeFragmentDirections.actionWelcomeFragmentToSignInFragment());
   //     parent.navController.navigate(WelcomeFragmentDirections.actionWelcomeFragmentToProductRegistrationFragment());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
     //   Log.d("LLLOOOOO", "onDestroy: ");
    }
}