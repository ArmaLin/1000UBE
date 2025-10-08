package com.dyaco.spirit_commercial.support.room.spirit.spirit_entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = RankEntity.RANKS,
    indices = [Index("uid")]
)
data class RankEntity(
    @PrimaryKey(autoGenerate = true)
    var uid: Long = 0,
    var memberName: String? = null,
    var memberNo: Int = 0,
    var memberHours: Int = 0,
    var memberSpeed: Float = 0.0f,
    var memberCal: Float = 0.0f,
    var memberDistance: Float = 0.0f,
    var memberAvatar: ByteArray? = null,
    var isMe: Boolean = false
) {
    companion object {
        const val RANKS = "ranks"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RankEntity

        if (uid != other.uid) return false
        if (memberNo != other.memberNo) return false
        if (memberHours != other.memberHours) return false
        if (memberSpeed != other.memberSpeed) return false
        if (memberCal != other.memberCal) return false
        if (memberDistance != other.memberDistance) return false
        if (isMe != other.isMe) return false
        if (memberName != other.memberName) return false
        if (!memberAvatar.contentEquals(other.memberAvatar)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = uid.hashCode()
        result = 31 * result + memberNo
        result = 31 * result + memberHours
        result = 31 * result + memberSpeed.hashCode()
        result = 31 * result + memberCal.hashCode()
        result = 31 * result + memberDistance.hashCode()
        result = 31 * result + isMe.hashCode()
        result = 31 * result + (memberName?.hashCode() ?: 0)
        result = 31 * result + (memberAvatar?.contentHashCode() ?: 0)
        return result
    }
}