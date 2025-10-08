package com.dyaco.spirit_commercial.support.room.fitness_test.entity_fitness_test;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;

@Entity(tableName = "TableMarines", primaryKeys = { "gender", "time_le"})
public class TableMarines {
    private int     gender;
    private int     time_le;
    private int     points;

    public TableMarines(){}

    @Ignore
    public TableMarines(int gender, int time_le, int points) {
        this.gender = gender;
        this.time_le = time_le;
        this.points = points;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
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
        return "TableMarines{" +
                "gender=" + gender +
                ", time_le=" + time_le +
                ", points=" + points +
                '}';
    }
}
