package com.dyaco.spirit_commercial.support.mediaapp;

import com.dyaco.spirit_commercial.support.room.spirit.spirit_entity.MediaAppsEntity;

import java.util.Comparator;

public class MediaAppEntitySort implements Comparator<MediaAppsEntity> {
    public int compare(MediaAppsEntity a, MediaAppsEntity b) {
//        return (int) (b.getSort() - a.getSort());  //降序。如果升序位置调换。
        return (int) (a.getSort() - b.getSort());
    }
}