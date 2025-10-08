package com.dyaco.spirit_commercial.support.room.spirit.spirit_entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = EgymEntity.EGYM_TABLE,
    indices = [Index("uid")]
)
data class EgymEntity(
    @PrimaryKey(autoGenerate = true)
    var uid: Long = 0,
    var workoutJson: String? = null,
    var updateTime: Long = 0,
    var isUploaded: Boolean = false
) {
    companion object {
        const val EGYM_TABLE = "EGYM_TABLE"
    }
}