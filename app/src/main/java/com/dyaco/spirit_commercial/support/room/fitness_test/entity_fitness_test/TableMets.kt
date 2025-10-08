package com.dyaco.spirit_commercial.support.room.fitness_test.entity_fitness_test

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "TableMets")
data class TableMets(
    @PrimaryKey
    var mets: Int = 0,
    
    var speed: Int = 0,
    
    var grade: Int = 0
)