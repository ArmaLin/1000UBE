package com.dyaco.spirit_commercial.support.mediaapp;

import java.util.Comparator;

public class AppStoreSort implements Comparator<AppStoreBean.AppUpdateBeansDTO> {
    public int compare(AppStoreBean.AppUpdateBeansDTO a, AppStoreBean.AppUpdateBeansDTO b) {
        return (a.getSort() - b.getSort());
//        return (b.getSort() - a.getSort());
    }
}