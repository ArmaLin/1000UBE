package com.dyaco.spirit_commercial.support.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.dyaco.spirit_commercial.R;

public class MyAnimationUtils {
    private final Context mContent;
    private final Animation mAnimation;
    private final ImageView mImageView;



    public MyAnimationUtils(Context context , ImageView imageView){
        this.mContent = context;
        this.mAnimation = android.view.animation.AnimationUtils.loadAnimation(mContent, R.anim.loadin_rotate);
        this.mImageView = imageView;
    }

    public void startLoadingRotate(){
        if(mAnimation !=null){
            LinearInterpolator lin = new LinearInterpolator();
            mAnimation.setInterpolator(lin);
            mImageView.startAnimation(mAnimation);
        }
    }

    public void stopLoadingRotate(){
        if(mAnimation != null){
            mImageView.clearAnimation();
        }
    }


    public static RotateAnimation getRotateAnimation(){
        RotateAnimation am;
        am = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        am.setDuration(2000);
        am.setRepeatCount(Animation.INFINITE);
        LinearInterpolator lir = new LinearInterpolator();
        am.setInterpolator(lir);
        return am;
    }


    /**
     * 切換view時的動畫
     * @param showView 要顯示的view
     * @param hideView 要隱藏的view
     * @param duration 持續時間
     */
    public static void crossFade(View showView, View hideView, int duration) {
       // int shortAnimationDuration = context.getApplicationContext().getResources().getInteger(android.R.integer.config_shortAnimTime);

        showView.setAlpha(0f);
        showView.setVisibility(View.VISIBLE);
        showView.animate()
                .alpha(1f)
              //  .translationX(20)
                .setDuration(duration)
                .setListener(null);

        hideView.animate()
                .alpha(0f)
                //.translationX(-20)
                .setDuration(duration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        hideView.setVisibility(View.GONE);
                    }
                });
    }


    public static void crossFade2(View showView, View hideView, int duration) {
        // int shortAnimationDuration = context.getApplicationContext().getResources().getInteger(android.R.integer.config_shortAnimTime);

        showView.setVisibility(View.VISIBLE);
        hideView.setVisibility(View.INVISIBLE);
//        showView.setAlpha(0f);
//        showView.setVisibility(View.VISIBLE);
//        showView.animate()
//                .alpha(1f)
//                .setDuration(duration)
//                .setListener(null);
//
//        hideView.animate()
//                .alpha(0f)
//                .setDuration(duration)
//                .setListener(null);
    }

}