package com.dyaco.spirit_commercial.support.mediaapp;

import java.io.Serializable;

public class MyMediaAppBean implements Serializable {
    public MyMediaAppBean(int sort,String mediaPkName) {
        this.mediaPkName = mediaPkName;
        this.sort = sort;
    }

    String mediaPkName;
    int sort;

    public String getMediaPkName() {
        return mediaPkName;
    }

    public void setMediaPkName(String mediaPkName) {
        this.mediaPkName = mediaPkName;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    @Override
    public String toString() {
        return "MyMediaAppBean{" +
                "mediaPkName='" + mediaPkName + '\'' +
                ", sort=" + sort +
                '}';
    }
}
