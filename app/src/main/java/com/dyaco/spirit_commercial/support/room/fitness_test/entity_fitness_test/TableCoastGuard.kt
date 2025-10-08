package com.dyaco.spirit_commercial.support.room.fitness_test.entity_fitness_test

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "TableCoastGuard", primaryKeys = ["gender", "age_min", "age_max", "time_min", "time_max"])
data class TableCoastGuard(
    var gender: Int = 0,

    @ColumnInfo(name = "age_min")
    var ageMin: Int = 0,

    @ColumnInfo(name = "age_max")
    var ageMax: Int = 0,

    @ColumnInfo(name = "time_min")
    var timeMin: Int = 0,

    @ColumnInfo(name = "time_max")
    var timeMax: Int = 0,

    var category: Int = 0
    
)