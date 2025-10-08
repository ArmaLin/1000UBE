package com.dyaco.spirit_commercial.support.room.fitness_test.entity_fitness_test;

import androidx.room.Entity;
import androidx.room.Ignore;

@Entity(tableName = "TableMets", primaryKeys = { "mets"})
public class TableMets {

    private int     mets;
    private int     speed;
    private int     grade;

    public TableMets(){}

    @Ignore
    public TableMets(int mets, int speed, int grade) {
        this.mets = mets;
        this.speed = speed;
        this.grade = grade;
    }

    public int getMets() {
        return mets;
    }

    public void setMets(int mets) {
        this.mets = mets;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }
}
