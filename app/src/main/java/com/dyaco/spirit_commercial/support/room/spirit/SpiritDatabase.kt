package com.dyaco.spirit_commercial.support.room.spirit

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.dyaco.spirit_commercial.support.room.Converters
import com.dyaco.spirit_commercial.support.room.spirit.spirit_entity.*

@Database(
    entities = [
        UserProfileEntity::class, ErrorMsgEntity::class,
        RankEntity::class, DeviceEntity::class,
        UploadWorkoutDataEntity::class, MediaAppsEntity::class, EgymEntity::class
    ],
    version = 1
)
@TypeConverters(Converters::class)
abstract class SpiritDatabase : RoomDatabase() {
    abstract fun spiritDao(): SpiritDao
}