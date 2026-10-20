package com.example.dayflow.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class ChallengeStatus { UPCOMING, ACTIVE, PAUSED, COMPLETED }

@Entity(tableName = "challenges")
data class ChallengeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val iconKey: String = "star",
    val isGroup: Boolean = false,
    val groupName: String? = null,
    val durationDays: Int,
    /** null یعنی هنوز شروع مشخص نشده (تب آینده) */
    val startEpochDay: Long? = null,
    val endEpochDay: Long? = null,
    val status: String = ChallengeStatus.UPCOMING.name,
    val pausedAtEpochDay: Long? = null,
    val createdAtEpochDay: Long
)

@Entity(
    tableName = "challenge_logs",
    primaryKeys = ["challengeId", "epochDay"],
    foreignKeys = [
        ForeignKey(
            entity = ChallengeEntity::class,
            parentColumns = ["id"],
            childColumns = ["challengeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("epochDay")]
)
data class ChallengeLogEntity(val challengeId: Long, val epochDay: Long)
