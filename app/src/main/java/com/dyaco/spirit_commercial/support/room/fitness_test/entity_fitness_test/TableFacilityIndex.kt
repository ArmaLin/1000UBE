package com.dyaco.spirit_commercial.support.room.fitness_test.entity_fitness_test;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;

@Entity(tableName = "TableFacilityIndex", primaryKeys = { "profile_id" })

public class TableFacilityIndex {
    @NonNull
    private int profile_id;
    @NonNull
    private String profile_alias;
    private int profile_remove;
    private int speed_unit;

    public TableFacilityIndex() {

    }

    @Ignore
    public TableFacilityIndex(int profile_id, String profile_alias, int profile_remove, int speed_unit) {
        this.profile_id = profile_id;
        this.profile_alias = profile_alias;
        this.profile_remove = profile_remove;
        this.speed_unit = speed_unit;
    }

    public int getProfile_id() {
        return profile_id;
    }

    public void setProfile_id(int profile_id) {
        this.profile_id = profile_id;
    }

    public String getProfile_alias() {
        return profile_alias;
    }

    public void setProfile_alias(String profile_alias) {
        this.profile_alias = profile_alias;
    }

    public int getProfile_remove() {
        return profile_remove;
    }

    public void setProfile_remove(int profile_remove) {
        this.profile_remove = profile_remove;
    }

    public int getSpeed_unit() {
        return speed_unit;
    }

    public void setSpeed_unit(int speed_unit) {
        this.speed_unit = speed_unit;
    }
}
