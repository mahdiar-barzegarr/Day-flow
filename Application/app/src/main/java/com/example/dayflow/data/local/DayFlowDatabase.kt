package com.example.dayflow.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.dayflow.data.local.note.NoteDao
import com.example.dayflow.data.local.note.NoteEntity
import com.example.dayflow.ui.group.GroupDao
import com.example.dayflow.ui.group.GroupEntity
import com.example.dayflow.ui.group.GroupInviteEntity

@Database(
    entities = [
        HabitEntity::class,
        HabitLogEntity::class,
        ChallengeEntity::class,
        ChallengeLogEntity::class,
        GroupEntity::class,
        GroupInviteEntity::class,
        NoteEntity::class
    ],
    version = 6,
    exportSchema = false
)
abstract class DayFlowDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
    abstract fun challengeDao(): ChallengeDao

    abstract fun groupDao(): GroupDao

    abstract fun noteDao(): NoteDao
}
