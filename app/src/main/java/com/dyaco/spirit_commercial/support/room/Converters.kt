package com.dyaco.spirit_commercial.support.room

object Converters {
    @androidx.room.TypeConverter
    @JvmStatic
    fun fromTimestamp(value: Long?): java.util.Date? {
        return value?.let { _root_ide_package_.java.util.Date(it) }
    }

    @androidx.room.TypeConverter
    @JvmStatic
    fun dateToTimestamp(date: java.util.Date?): Long? {
        return date?.time
    }
}