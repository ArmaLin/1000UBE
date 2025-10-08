package com.dyaco.spirit_commercial.support.room.fitness_test.entity_fitness_test

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "TableFacilityIndex")
data class TableFacilityIndex(
    @PrimaryKey
    @ColumnInfo(name = "profile_id")
    var profileId: Int = 0,

    @ColumnInfo(name = "profile_alias")
    var profileAlias: String = "",

    @ColumnInfo(name = "profile_remove")
    var profileRemove: Int = 0,

    @ColumnInfo(name = "speed_unit")
    var speedUnit: Int = 0
)