package com.example.dayflow.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [HabitEntity::class, HabitLogEntity::class, ChallengeEntity::class, ChallengeLogEntity::class],
    version = 3,
    exportSchema = false
)
abstract class DayFlowDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
    abstract fun challengeDao(): ChallengeDao
}
