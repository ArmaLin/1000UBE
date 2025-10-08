package com.dyaco.spirit_commercial.support.room.fitness_test.entity_fitness_test

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "TablePeb", primaryKeys = ["gender", "age_min", "age_max", "time_le"])
data class TablePeb(
    var gender: Int = 0,

    @ColumnInfo(name = "age_min")
    var ageMin: Int = 0,

    @ColumnInfo(name = "age_max")
    var ageMax: Int = 0,

    @ColumnInfo(name = "time_le")
    var timeLe: Int = 0,

    var percentiles: Int = 0
)