package com.dyaco.spirit_commercial.support.room

abstract class DatabaseCallback<T> {
    open fun onDataLoadedList(lists: List<T>) {}
    open fun onDataLoadedBean(entity: T) {}
    open fun onAdded(rowId: Long) {}
    open fun onQueryAll() {}
    open fun onDeleted() {}
    open fun onUpdated() {}
    open fun onError(err: String) {}
    open fun onCount(id: Int?) {}
    open fun onNoData() {}
}