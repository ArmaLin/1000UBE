package com.dyaco.spirit_commercial.support.custom_view.banner.indicator;

import android.view.View;

import androidx.annotation.NonNull;

import com.dyaco.spirit_commercial.support.custom_view.banner.config.IndicatorConfig;
import com.dyaco.spirit_commercial.support.custom_view.banner.listener.OnPageChangeListener;


public interface Indicator extends OnPageChangeListener {
    @NonNull
    View getIndicatorView();

    IndicatorConfig getIndicatorConfig();

    void onPageChanged(int count, int currentPosition);

}
