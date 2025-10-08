package com.dyaco.spirit_commercial.support.room.fitness_test.entity_fitness_test;

import androidx.room.Entity;
import androidx.room.Ignore;

@Entity(tableName = "TableRearGradeAd", primaryKeys = { "grade" })
public class TableRearGradeAd {

    private int grade;
    private int ad;

    public TableRearGradeAd() {
    }

    @Ignore
    public TableRearGradeAd(int grade, int ad) {
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
