package com.dyaco.spirit_commercial.support.room.fitness_test.entity_fitness_test;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;

@Entity(tableName = "TableArmy", primaryKeys = { "gender", "age_min", "age_max", "time_le"})
public class TableArmy {

    private int gender;
    private int age_min;
    private int age_max;
    private int time_le;
    private int points;

    public TableArmy() {
    }

    @Ignore
    public TableArmy(int gender, int age_min, int age_max, int time_le, int points) {
        this.gender = gender;
        this.age_min = age_min;
        this.age_max = age_max;
        this.time_le = time_le;
        this.points = points;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getAge_min() {
        return age_min;
    }

    public void setAge_min(int age_min) {
        this.age_min = age_min;
    }

    public int getAge_max() {
        return age_max;
    }

    public void setAge_max(int age_max) {
        this.age_max = age_max;
    }

    public int getTime_le() {
        return time_le;
    }

    public void setTime_le(int time_le) {
        this.time_le = time_le;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    @NonNull
    @Override
    public String toString() {
        return "TableArmy{" +
                "gender=" + gender +
                ", age_min=" + age_min +
                ", age_max=" + age_max +
                ", time_le=" + time_le +
                ", points=" + points +
                '}';
    }
}
