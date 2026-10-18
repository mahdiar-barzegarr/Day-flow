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
    /** کلید آیکون: water / book / fitness / sleep / star */
    val iconKey: String = "star",
    /** هدف اختیاری، مثلاً «۸ لیوان» */
    val goalText: String? = null,
    /** روز ساخت عادت (LocalDate.toEpochDay) */
    val createdAtEpochDay: Long,
    val isArchived: Boolean = false
)

/** هر ردیف یعنی عادت [habitId] در روز [epochDay] انجام شده. */
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
