package com.dyaco.spirit_commercial.model.repository;

import java.util.List;

public interface RepoCallback<T>  {
        void onSuccess(List<T> dataList);
        void onFail(String error);
    }