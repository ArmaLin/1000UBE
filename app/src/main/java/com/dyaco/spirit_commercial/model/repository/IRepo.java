package com.dyaco.spirit_commercial.model.repository;

interface IRepo<T> {
    void getData(int id, RepoCallback<T> callback);
}