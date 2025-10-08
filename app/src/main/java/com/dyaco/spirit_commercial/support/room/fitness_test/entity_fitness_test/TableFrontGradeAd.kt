package com.dyaco.spirit_commercial.support.room.fitness_test.entity_fitness_test

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "TableFrontGradeAd")
data class TableFrontGradeAd(
    @PrimaryKey
    var grade: Int = 0,
    
    var ad: Int = 0
)