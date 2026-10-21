package com.example.dayflow.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ChallengeDao {
    @Query("SELECT * FROM challenges ORDER BY id DESC")
    fun observeChallenges(): Flow<List<ChallengeEntity>>

    @Query("SELECT * FROM challenges WHERE id = :id")
    fun observeChallenge(id: Long): Flow<ChallengeEntity?>

    @Query("SELECT * FROM challenges WHERE id = :id")
    suspend fun getById(id: Long): ChallengeEntity?

    @Query("SELECT * FROM challenge_logs")
    fun observeAllLogs(): Flow<List<ChallengeLogEntity>>

    @Query("SELECT * FROM challenge_logs WHERE challengeId = :id")
    fun observeLogs(id: Long): Flow<List<ChallengeLogEntity>>

    @Insert suspend fun insert(challenge: ChallengeEntity): Long
    @Update suspend fun update(challenge: ChallengeEntity)

    @Query("DELETE FROM challenges WHERE id = :id")
    suspend fun delete(id: Long)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLog(log: ChallengeLogEntity)

    @Query("DELETE FROM challenge_logs WHERE challengeId = :id AND epochDay = :day")
    suspend fun deleteLog(id: Long, day: Long)
}