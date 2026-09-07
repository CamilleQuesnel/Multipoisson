package com.multipoisson.app.data.repository

import android.content.Context
import com.multipoisson.app.data.db.AppDatabase
import com.multipoisson.app.data.db.entity.PlaySessionEntity
import com.multipoisson.app.data.db.entity.QuestionAttemptEntity
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*

class GameRepository(context: Context) {

    private val db = AppDatabase.getInstance(context)
    private val attemptDao = db.questionAttemptDao()
    private val sessionDao = db.playSessionDao()

    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)

    /** Persists a completed game session and all its individual question results. */
    suspend fun saveSession(
        profileId: String,
        sessionId: String,
        mode: String,
        tables: List<Int>,
        questionResults: List<QuestionResult>,
        durationSeconds: Int,
        timerEnabled: Boolean,
    ) {
        val now = System.currentTimeMillis()
        val dateStr = dateFormatter.format(Date(now))

        val correctCount = questionResults.count { it.isCorrect }

        val sessionEntity = PlaySessionEntity(
            id = sessionId,
            profileId = profileId,
            dateStr = dateStr,
            mode = mode,
            tables = tables.joinToString(",", "[", "]"),
            questionCount = questionResults.size,
            correctCount = correctCount,
            durationSeconds = durationSeconds,
            timerEnabled = timerEnabled,
            timestamp = now,
        )

        val attemptEntities = questionResults.map { q ->
            QuestionAttemptEntity(
                profileId = profileId,
                sessionId = sessionId,
                tableN = q.tableN,
                operandA = q.operandA,
                operandB = q.operandB,
                isCorrect = q.isCorrect,
                responseTimeMs = q.responseTimeMs,
                timerEnabled = timerEnabled,
                timestamp = q.timestamp,
            )
        }

        sessionDao.insert(sessionEntity)
        attemptDao.insertAll(attemptEntities)
    }

    suspend fun getCorrectAttempts(profileId: String): List<QuestionAttemptEntity> =
        attemptDao.getCorrectByProfile(profileId)

    suspend fun getAllSessions(profileId: String): List<PlaySessionEntity> =
        sessionDao.getByProfile(profileId)

    fun observeSessions(profileId: String): Flow<List<PlaySessionEntity>> =
        sessionDao.observeByProfile(profileId)

    suspend fun getRecentSessions(profileId: String, limit: Int = 20): List<PlaySessionEntity> =
        sessionDao.getRecentByProfile(profileId, limit)

    suspend fun countDistinctPlayDays(profileId: String): Int =
        sessionDao.countDistinctDays(profileId)

    data class QuestionResult(
        val tableN: Int,
        val operandA: Int,
        val operandB: Int,
        val isCorrect: Boolean,
        val responseTimeMs: Long,
        val timestamp: Long = System.currentTimeMillis(),
    )
}
