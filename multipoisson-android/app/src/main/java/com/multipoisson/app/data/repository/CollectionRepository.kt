package com.multipoisson.app.data.repository

import android.content.Context
import com.multipoisson.app.data.db.AppDatabase
import com.multipoisson.app.data.db.entity.UnlockedFishEntity
import com.multipoisson.app.domain.logic.CollectionChecker
import kotlinx.coroutines.flow.Flow

class CollectionRepository(context: Context) {

    private val db = AppDatabase.getInstance(context)
    private val fishDao = db.unlockedFishDao()
    private val attemptDao = db.questionAttemptDao()
    private val sessionDao = db.playSessionDao()

    fun observeUnlocked(profileId: String): Flow<List<UnlockedFishEntity>> =
        fishDao.observeByProfile(profileId)

    suspend fun getUnlockedIds(profileId: String): Set<String> =
        fishDao.getUnlockedIds(profileId).toSet()

    /**
     * Checks for newly unlocked fish after a game session ends, persists them,
     * and returns the list of newly unlocked IDs (empty if nothing new).
     */
    suspend fun checkAndUnlock(profileId: String): List<String> {
        val alreadyUnlocked = getUnlockedIds(profileId)
        val correctAttempts = attemptDao.getCorrectByProfile(profileId)
        val allSessions = sessionDao.getByProfile(profileId)

        val newUnlocks = CollectionChecker.computeNewUnlocks(
            correctAttempts = correctAttempts,
            allSessions = allSessions,
            alreadyUnlocked = alreadyUnlocked,
        )

        if (newUnlocks.isNotEmpty()) {
            val now = System.currentTimeMillis()
            fishDao.insertAll(newUnlocks.map { UnlockedFishEntity(profileId, it, now) })
        }

        return newUnlocks
    }

    suspend fun isUnlocked(profileId: String, fishId: String): Boolean =
        fishDao.isUnlocked(profileId, fishId)
}
