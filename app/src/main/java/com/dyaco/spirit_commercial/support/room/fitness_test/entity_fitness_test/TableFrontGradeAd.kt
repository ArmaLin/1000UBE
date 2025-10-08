package com.dyaco.spirit_commercial.support.room.fitness_test.entity_fitness_test;

import androidx.room.Entity;
import androidx.room.Ignore;

@Entity(tableName = "TableFrontGradeAd", primaryKeys = { "grade" })
public class TableFrontGradeAd {

    private int grade;
    private int ad;

    public TableFrontGradeAd() {
    }

    @Ignore
    public TableFrontGradeAd(int grade, int ad) {
        this.grade = grade;
        this.ad = ad;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public int getAd() {
        return ad;
    }

    public void setAd(int ad) {
        this.ad = ad;
    }
}
