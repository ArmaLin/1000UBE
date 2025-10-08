package com.dyaco.spirit_commercial.support.utils;

import android.content.Context;
import android.os.Build;
import android.transition.TransitionManager;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;

public class ConstraintUtils {
    private final ConstraintLayout mContainer;
    private final ConstraintSet mConstraintSet;
    private final Context mContent;


    public ConstraintUtils(Context context, ConstraintLayout constraintLayout) {
        this.mContainer = constraintLayout;
        this.mContent = context;
        this.mConstraintSet = new ConstraintSet();
        this.mConstraintSet.clone(constraintLayout);
    }

    public ConstraintSet setImageIcon(ImageView imageIcon, int drawableId) {
        if (mConstraintSet != null) {
            imageIcon.setImageDrawable(ContextCompat.getDrawable(mContent, drawableId));
        }
        return mConstraintSet;
    }

    public void setBarHeight(int viewId, float percent) {
        if (mConstraintSet != null) {
            mConstraintSet.constrainPercentHeight(viewId, percent);
            mConstraintSet.applyTo(mContainer);
            TransitionManager.beginDelayedTransition(mContainer);
        }
    }

    public void setVerticalBias(int viewId, float percent) {
        if (mConstraintSet != null) {
            mConstraintSet.setVerticalBias(viewId, percent);
            mConstraintSet.applyTo(mContainer);
            TransitionManager.beginDelayedTransition(mContainer);
        }
    }


    public void setWidth(int viewId, int width) {
        if (mConstraintSet != null) {
            mConstraintSet.constrainWidth(viewId, width);
            mConstraintSet.applyTo(mContainer);
            TransitionManager.beginDelayedTransition(mContainer);

        }
    }


    public void setHorizontalBias(int viewId, float bias) {
        if (mConstraintSet != null) {
            mConstraintSet.setHorizontalBias(viewId, bias);
            mConstraintSet.applyTo(mContainer);
            TransitionManager.beginDelayedTransition(mContainer);
        }
    }

//    public void softKeyBoardShowVerticalBias(){
//        setBarHeight(R.id.fContainer, 0.95f);
//        setVerticalBias(R.id.fContainer, 0.1f);
//    }
//
//
//    public void softKeyBoardNoShowVerticalBias(){
//        setBarHeight(R.id.fContainer, 0.57f);
//        setVerticalBias(R.id.fContainer, 0.9f);
//    }


    public void clean(int viewId) {
        if (mConstraintSet != null) {
            mConstraintSet.clear(viewId);
            mConstraintSet.applyTo(mContainer);
            TransitionManager.beginDelayedTransition(mContainer);
        }
    }

//    ConstraintUtils constraintUtils = new ConstraintUtils(MyApplication.getInstance(), constraintLayout);
//    constraintUtils.setMargin(view1,ConstraintSet.LEFT,12);
//    constraintUtils.setMargin(view2,ConstraintSet.LEFT,12);

    public void setMargin(int viewId, int anchor, int value) {
        if (mConstraintSet != null) {
            mConstraintSet.setMargin(viewId, anchor, value);
            mConstraintSet.applyTo(mContainer);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                TransitionManager.beginDelayedTransition(mContainer);
            }
        }
    }
}