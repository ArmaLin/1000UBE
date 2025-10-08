package com.dyaco.spirit_commercial.support.mediaapp;

import java.util.Comparator;

public class MediaAppSort implements Comparator<MyMediaAppBean> {
    public int compare(MyMediaAppBean a, MyMediaAppBean b) {
        return (int) (a.getSort() - b.getSort());
    }
}