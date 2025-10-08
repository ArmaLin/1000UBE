package com.dyaco.spirit_commercial.support.room.spirit.spirit_entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = UploadWorkoutDataEntity.UPLOAD_WORKOUT_DATA)
data class UploadWorkoutDataEntity(
    @PrimaryKey(autoGenerate = true)
    var uid: Long = 0,
    var dataJson: String? = null,
    var isUploaded: Boolean = false
) {
    companion object {
        const val UPLOAD_WORKOUT_DATA = "upload_workout_data"
    }
}