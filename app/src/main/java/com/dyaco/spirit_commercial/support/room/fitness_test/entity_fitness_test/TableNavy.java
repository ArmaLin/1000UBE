package com.dyaco.spirit_commercial.support.room.fitness_test.entity_fitness_test;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;

@Entity(tableName = "TableNavy", primaryKeys = { "gender", "age_min", "age_max", "time_le"})
public class TableNavy {

    private int gender;
    private int age_min;
    private int age_max;
    private int time_le;
    private int points;
    private int category;

    public TableNavy() {
    }

    @Ignore
    public TableNavy(int gender, int age_min, int age_max, int time_le, int points, int category) {
        this.gender = gender;
        this.age_min = age_min;
        this.age_max = age_max;
        this.time_le = time_le;
        this.points = points;
        this.category = category;
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

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    @NonNull
    @Override
    public String toString() {
        return "TableNavy{" +
                "gender=" + gender +
                ", age_min=" + age_min +
                ", age_max=" + age_max +
                ", time_le=" + time_le +
                ", points=" + points +
                ", category=" + category +
                '}';
    }
}
