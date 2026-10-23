package com.example.dayflow.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** یک عادت روزانه. */
@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val iconKey: String = "star",
    val goalText: String? = null,
    val createdAtEpochDay: Long,
    val isArchived: Boolean = false,
    val targetDays: Int? = null,
    val reminderMinutes: Int? = null
)

@Entity(
    tableName = "habit_logs",
    primaryKeys = ["habitId", "epochDay"],
    foreignKeys = [
        ForeignKey(
            entity = HabitEntity::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("epochDay")]
)
data class HabitLogEntity(
    val habitId: Long,
    val epochDay: Long
)
