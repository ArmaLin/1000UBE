package com.dyaco.spirit_commercial.support.room.fitness_test.entity_fitness_test

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "TableMarines", primaryKeys = ["gender", "time_le"])
data class TableMarines(
    var gender: Int = 0,

    @ColumnInfo(name = "time_le")
    var timeLe: Int = 0,

    var points: Int = 0
)