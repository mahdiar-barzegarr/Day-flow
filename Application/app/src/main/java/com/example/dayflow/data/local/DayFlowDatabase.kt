package com.example.dayflow.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [HabitEntity::class, HabitLogEntity::class],
    version = 2,
    exportSchema = false
)
abstract class DayFlowDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
}
