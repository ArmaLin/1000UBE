package com.dyaco.spirit_commercial.support.room.fitness_test.entity_fitness_test

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "TableFacility", primaryKeys = ["profile_id", "profile_type", "profile_index"])
data class TableFacility(
    @ColumnInfo(name = "profile_id")
    var profileId: Int = 0,

    @ColumnInfo(name = "profile_type")
    var profileType: Int = 0,

    @ColumnInfo(name = "profile_index")
    var profileIndex: Int = 0,

    @ColumnInfo(name = "profile_value")
    var profileValue: Int = 0
)