package com.dyaco.spirit_commercial.model.repository;

import com.dyaco.spirit_commercial.model.webapi.bean.TimeZoneBean;

interface ITimeZoneRepo {
    void getTimeZone(int id ,RepoCallback<TimeZoneBean> repoCallback);
}

public class TimeZoneRepo implements ITimeZoneRepo {

    @Override
    public void getTimeZone(int id, RepoCallback<TimeZoneBean> repoCallback) {

    }
}