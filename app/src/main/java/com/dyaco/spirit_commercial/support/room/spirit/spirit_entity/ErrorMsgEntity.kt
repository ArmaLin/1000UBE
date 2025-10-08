package com.dyaco.spirit_commercial.support.room.spirit.spirit_entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = ErrorMsgEntity.ERROR_MSG,
    indices = [Index("uid")]
)
data class ErrorMsgEntity(
    @PrimaryKey(autoGenerate = true)
    var uid: Long = 0,
    var errorCode: String? = null,
    var errorMessage: String? = null,
    var errorDate: Date? = null
) {
    companion object {
        const val ERROR_MSG = "error_msg"
    }
}