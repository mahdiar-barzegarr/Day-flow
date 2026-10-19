package com.example.dayflow.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.dayflow.data.local.*
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChallengeRepository @Inject constructor(private val dao: ChallengeDao) {

    fun observeChallenges(): Flow<List<ChallengeEntity>> = dao.observeChallenges()
    fun observeChallenge(id: Long): Flow<ChallengeEntity?> = dao.observeChallenge(id)
    fun observeAllLogs(): Flow<List<ChallengeLogEntity>> = dao.observeAllLogs()
    fun observeLogs(id: Long): Flow<List<ChallengeLogEntity>> = dao.observeLogs(id)

    @RequiresApi(Build.VERSION_CODES.O)
    private fun today() = LocalDate.now().toEpochDay()

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun create(
        title: String, iconKey: String, isGroup: Boolean, groupName: String?,
        durationDays: Int, startEpochDay: Long?
    ) {
        val today = today()
        val status = if (startEpochDay != null && startEpochDay <= today) ChallengeStatus.ACTIVE
        else ChallengeStatus.UPCOMING
        dao.insert(
            ChallengeEntity(
                title = title.trim(),
                iconKey = iconKey,
                isGroup = isGroup,
                groupName = groupName?.trim()?.ifBlank { null },
                durationDays = durationDays,
                startEpochDay = startEpochDay,
                endEpochDay = startEpochDay?.plus(durationDays - 1),
                status = status.name,
                createdAtEpochDay = today
            )
        )
    }

    /** فعال‌سازی: شروع از امروز و محاسبه‌ی دوباره‌ی پایان */
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun activate(id: Long) {
        val c = dao.getById(id) ?: return
        val today = today()
        dao.update(
            c.copy(
                status = ChallengeStatus.ACTIVE.name,
                startEpochDay = today,
                endEpochDay = today + c.durationDays - 1,
                pausedAtEpochDay = null
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun pause(id: Long) {
        val c = dao.getById(id) ?: return
        dao.update(c.copy(status = ChallengeStatus.PAUSED.name, pausedAtEpochDay = today()))
    }

    /** ادامه: روزهای توقف به پایان چالش اضافه می‌شه */
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun resume(id: Long) {
        val c = dao.getById(id) ?: return
        val pausedDays = c.pausedAtEpochDay?.let { today() - it } ?: 0L
        dao.update(
            c.copy(
                status = ChallengeStatus.ACTIVE.name,
                endEpochDay = c.endEpochDay?.plus(pausedDays),
                pausedAtEpochDay = null
            )
        )
    }

    suspend fun delete(id: Long) = dao.delete(id)

    suspend fun setDone(id: Long, day: Long, done: Boolean) {
        if (done) dao.insertLog(ChallengeLogEntity(id, day)) else dao.deleteLog(id, day)
    }
}