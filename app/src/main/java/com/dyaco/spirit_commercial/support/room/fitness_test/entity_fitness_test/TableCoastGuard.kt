package com.dyaco.spirit_commercial.support.room.fitness_test.entity_fitness_test;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;

@Entity(tableName = "TableCoastGuard", primaryKeys = { "gender", "age_min", "age_max", "time_min", "time_max" })
public class TableCoastGuard {

    private int gender;
    private int age_min;
    private int age_max;
    private int time_min;
    private int time_max;
    private int category;

    public TableCoastGuard() {
    }

    @Ignore
    public TableCoastGuard(int gender, int age_min, int age_max, int time_min, int time_max, int points, int category) {
        this.gender = gender;
        this.age_min = age_min;
        this.age_max = age_max;
        this.time_min = time_min;
        this.time_min = time_max;
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

    public int getTime_min() {
        return time_min;
    }

    public void setTime_min(int time_min) {
        this.time_min = time_min;
    }

    public int getTime_max() {
        return time_max;
    }

    public void setTime_max(int time_max) {
        this.time_max = time_max;
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
        return "TableCoastGuard{" +
                "gender=" + gender +
                ", age_min=" + age_min +
                ", age_max=" + age_max +
                ", time_min=" + time_min +
                ", time_max=" + time_max +
                ", category=" + category +
                '}';
    }
}
