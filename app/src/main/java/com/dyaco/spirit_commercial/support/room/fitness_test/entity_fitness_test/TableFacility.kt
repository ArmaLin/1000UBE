package com.dyaco.spirit_commercial.support.room.fitness_test.entity_fitness_test;

import androidx.room.Entity;
import androidx.room.Ignore;

@Entity(tableName = "TableFacility", primaryKeys = { "profile_id", "profile_type", "profile_index" })
public class TableFacility {

    private int profile_id;
    private int profile_type;
    private int profile_index;
    private int profile_value;

    public TableFacility() {
    }

    @Ignore
    public TableFacility(int id, int type, int index, int value) {
        this.profile_id = id;
        this.profile_type = type;
        this.profile_index = index;
        this.profile_value = value;
    }

    public int getProfile_type() {
        return profile_type;
    }

    public void setProfile_type(int profile_type) {
        this.profile_type = profile_type;
    }

    public int getProfile_index() {
        return profile_index;
    }

    public void setProfile_index(int profile_index) {
        this.profile_index = profile_index;
    }

    public int getProfile_value() {
        return profile_value;
    }

    public void setProfile_value(int profile_value) {
        this.profile_value = profile_value;
    }

    public int getProfile_id() {
        return profile_id;
    }

    public void setProfile_id(int profile_id) {
        this.profile_id = profile_id;
    }
}