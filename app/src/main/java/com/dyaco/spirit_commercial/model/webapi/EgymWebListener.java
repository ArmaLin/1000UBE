package com.dyaco.spirit_commercial.model.webapi;

import org.jetbrains.annotations.Nullable;

public interface EgymWebListener {
    default void onSuccess(){};
    default void onFail(){};
    default void onFail(@Nullable Throwable error, @Nullable Integer httpCode){};
    default void onDelete() {}
    default void onFailText(String errorText) {}
    default void onSuccess(String result) {}
}
