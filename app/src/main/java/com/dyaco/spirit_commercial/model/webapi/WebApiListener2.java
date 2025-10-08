package com.dyaco.spirit_commercial.model.webapi;

public interface WebApiListener2<T> {
    void onSuccess(T data);
    void onFail();
}
