package com.dyaco.spirit_commercial.support.room.fitness_test.entity_fitness_test

import androidx.room.Entity

// 我們將複合主鍵直接定義在 @Entity 註解中
@Entity(tableName = "TableAirForce", primaryKeys = ["gender", "age_min", "age_max", "time_min", "time_max"])
data class TableAirForce(
    var gender: Int = 0,
    var age_min: Int = 0,
    var age_max: Int = 0,
    var time_min: Int = 0,
    var time_max: Int = 0,
    var points: Int = 0,
    var category: Int = 0
)