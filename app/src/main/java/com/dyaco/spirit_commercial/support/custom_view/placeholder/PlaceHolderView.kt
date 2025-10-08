package com.dyaco.spirit_commercial.support.custom_view.placeholder

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.AnimationUtils
import com.dyaco.spirit_commercial.R

class PlaceHolderView(context: Context, attrs: AttributeSet?): View(context, attrs)  {

    init {
        setBackgroundResource(R.drawable.loading_placeholder)
        startAnimation(AnimationUtils.loadAnimation(context, R.anim.placeholder))
    }

}