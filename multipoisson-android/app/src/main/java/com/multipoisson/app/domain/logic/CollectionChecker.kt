package com.multipoisson.app.domain.logic

import com.multipoisson.app.data.db.entity.PlaySessionEntity
import com.multipoisson.app.data.db.entity.QuestionAttemptEntity
import com.multipoisson.app.domain.FishId

object CollectionChecker {

    /**
     * Computes which fish IDs should be newly unlocked for a profile.
     *
     * @param correctAttempts All correct question attempts for the profile.
     * @param allSessions     All play sessions for the profile.
     * @param alreadyUnlocked Set of fish IDs already recorded as unlocked.
     * @return List of fish IDs that meet unlock conditions but are not yet unlocked.
     */
    fun computeNewUnlocks(
        correctAttempts: List<QuestionAttemptEntity>,
        allSessions: List<PlaySessionEntity>,
        alreadyUnlocked: Set<String>,
    ): List<String> {
        val pending = mutableListOf<String>()

        // ── Table fish ────────────────────────────────────────────────────
        for (tableN in 1..10) {
            val fishId = FishId.tableId(tableN)
            if (fishId !in alreadyUnlocked && MasteryChecker.isTableMastered(tableN, correctAttempts)) {
                pending += fishId
            }
        }

        // ── Bouquet final — all 10 tables mastered ────────────────────────
        if (FishId.BOUQUET !in alreadyUnlocked) {
            val mastered = MasteryChecker.masteredTables(correctAttempts)
            if ((1..10).all { it in mastered }) pending += FishId.BOUQUET
        }

        // ── Regularity — distinct play days ───────────────────────────────
        val distinctDays = allSessions.map { it.dateStr }.toSet().size
        for ((fishId, threshold) in FishId.REGULAR_THRESHOLDS) {
            if (fishId !in alreadyUnlocked && distinctDays >= threshold) {
                pending += fishId
            }
        }

        // ── Performance — timed session achievements ───────────────────────
        val timedSessions = allSessions.filter { it.timerEnabled }
        for (session in timedSessions) {
            if (session.questionCount == 0) continue
            val pct = session.correctCount * 100f / session.questionCount

            if (FishId.PERF_50_80 !in alreadyUnlocked && FishId.PERF_50_80 !in pending
                && session.questionCount >= 50 && pct >= 80f
            ) pending += FishId.PERF_50_80

            if (FishId.PERF_100_90 !in alreadyUnlocked && FishId.PERF_100_90 !in pending
                && session.questionCount >= 100 && pct >= 90f
            ) pending += FishId.PERF_100_90

            if (FishId.PERF_100_95 !in alreadyUnlocked && FishId.PERF_100_95 !in pending
                && session.questionCount == 100 && pct >= 95f
            ) pending += FishId.PERF_100_95
        }

        // ── Legendary — 100% on 100 questions, timer on ───────────────────
        if (FishId.LEGENDARY !in alreadyUnlocked && FishId.LEGENDARY !in pending) {
            if (timedSessions.any { it.questionCount == 100 && it.correctCount == 100 }) {
                pending += FishId.LEGENDARY
            }
        }

        return pending
    }
}
