package com.dyaco.spirit_commercial.model.webapi;

public interface WebApiListener<T> {
    void onSuccess(T data);
   // void onFail();
}
