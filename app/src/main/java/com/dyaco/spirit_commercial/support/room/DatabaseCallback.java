package com.dyaco.spirit_commercial.support.room;

import java.util.List;

public abstract class DatabaseCallback<T> {

    public void onDataLoadedList(List<T> lists) {

     }

    public void onDataLoadedBean(T entity) {

    }

    public void onAdded(long rowId) {

    }

    public void onQueryAll() {

    }

    public void onDeleted() {

    }

    public void onUpdated() {

    }

    public void onError(String err) {

    }

    public void onCount(Integer id) {

    }

    public void onNoData() {

    }

}
