package com.multipoisson.app.domain.logic

import com.multipoisson.app.data.db.entity.QuestionAttemptEntity

object MasteryChecker {

    private val THIRTY_DAYS_MS = 30L * 24 * 60 * 60 * 1000

    /**
     * Core pairs for table N: N×2 through N×9 (commutative, stored as min/max).
     * ×0, ×1, ×10 are excluded from the mastery requirement because they are
     * often disabled by players — a child who always skips them should not be
     * blocked from earning their table fish.
     */
    private fun corePairs(tableN: Int): Set<Pair<Int, Int>> =
        (2..9).map { m ->
            val a = minOf(tableN, m)
            val b = maxOf(tableN, m)
            Pair(a, b)
        }.toSet()

    /**
     * Returns true if every core pair of [tableN] has been answered correctly
     * at least 3 times, with at least one correct answer in the last 30 days.
     *
     * [correctAttempts] must already be filtered for profileId and isCorrect=true.
     */
    fun isTableMastered(
        tableN: Int,
        correctAttempts: List<QuestionAttemptEntity>,
    ): Boolean {
        val thirtyDaysAgo = System.currentTimeMillis() - THIRTY_DAYS_MS
        val tableAttempts = correctAttempts.filter { it.tableN == tableN }

        for ((pairMin, pairMax) in corePairs(tableN)) {
            val pairCorrect = tableAttempts.filter { attempt ->
                minOf(attempt.operandA, attempt.operandB) == pairMin &&
                    maxOf(attempt.operandA, attempt.operandB) == pairMax
            }
            if (pairCorrect.size < 3) return false
            if (pairCorrect.none { it.timestamp >= thirtyDaysAgo }) return false
        }
        return true
    }

    /**
     * Returns the set of table indices (1–10) that are currently mastered.
     */
    fun masteredTables(correctAttempts: List<QuestionAttemptEntity>): Set<Int> =
        (1..10).filter { isTableMastered(it, correctAttempts) }.toSet()
}
