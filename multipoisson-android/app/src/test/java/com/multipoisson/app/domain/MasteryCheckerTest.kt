package com.multipoisson.app.domain

import com.multipoisson.app.data.db.entity.QuestionAttemptEntity
import com.multipoisson.app.domain.logic.MasteryChecker
import org.junit.Assert.*
import org.junit.Test

class MasteryCheckerTest {

    private val RECENT = System.currentTimeMillis()
    private val OLD = System.currentTimeMillis() - 40L * 24 * 60 * 60 * 1000 // 40 days ago

    // ── helpers ────────────────────────────────────────────────────────────

    private fun attempt(
        tableN: Int,
        a: Int,
        b: Int,
        timestamp: Long = RECENT,
    ) = QuestionAttemptEntity(
        profileId = "p1",
        sessionId = "s1",
        tableN = tableN,
        operandA = a,
        operandB = b,
        isCorrect = true,
        responseTimeMs = 3_000L,
        timerEnabled = false,
        timestamp = timestamp,
    )

    /** Builds 3 correct recent attempts for every core pair of [tableN]. */
    private fun masteredAttempts(tableN: Int, count: Int = 3, ts: Long = RECENT): List<QuestionAttemptEntity> =
        (2..9).flatMap { m ->
            val a = minOf(tableN, m)
            val b = maxOf(tableN, m)
            (1..count).map { attempt(tableN, a, b, ts) }
        }

    // ── isTableMastered ────────────────────────────────────────────────────

    @Test
    fun `isTableMastered returns false when no attempts`() {
        assertFalse(MasteryChecker.isTableMastered(3, emptyList()))
    }

    @Test
    fun `isTableMastered returns false when only 2 attempts per pair`() {
        val attempts = (2..9).flatMap { m ->
            (1..2).map { attempt(3, minOf(3, m), maxOf(3, m)) }
        }
        assertFalse(MasteryChecker.isTableMastered(3, attempts))
    }

    @Test
    fun `isTableMastered returns true when all core pairs have 3+ recent correct answers`() {
        val attempts = masteredAttempts(3)
        assertTrue(MasteryChecker.isTableMastered(3, attempts))
    }

    @Test
    fun `isTableMastered returns false when all attempts are older than 30 days`() {
        val attempts = masteredAttempts(5, ts = OLD)
        assertFalse(MasteryChecker.isTableMastered(5, attempts))
    }

    @Test
    fun `isTableMastered is true when 3 old + 1 recent per pair`() {
        val old3 = masteredAttempts(4, count = 3, ts = OLD)
        val recent1 = masteredAttempts(4, count = 1, ts = RECENT)
        assertTrue(MasteryChecker.isTableMastered(4, old3 + recent1))
    }

    @Test
    fun `isTableMastered ignores times-zero and times-one and times-ten`() {
        // Only core pairs 2..9 matter — x0/x1/x10 should not block mastery
        val attempts = masteredAttempts(7)
        // Even without any x0, x1, x10 attempts it should be mastered
        assertTrue(MasteryChecker.isTableMastered(7, attempts))
    }

    @Test
    fun `isTableMastered returns false when one pair is missing`() {
        // 3×2 through 3×8 mastered, but 3×9 missing
        val attempts = (2..8).flatMap { m ->
            (1..3).map { attempt(3, minOf(3, m), maxOf(3, m)) }
        }
        assertFalse(MasteryChecker.isTableMastered(3, attempts))
    }

    // ── masteredTables ─────────────────────────────────────────────────────

    @Test
    fun `masteredTables returns empty set when nothing is mastered`() {
        assertTrue(MasteryChecker.masteredTables(emptyList()).isEmpty())
    }

    @Test
    fun `masteredTables returns correct set of mastered tables`() {
        val attempts = masteredAttempts(2) + masteredAttempts(5)
        val mastered = MasteryChecker.masteredTables(attempts)
        assertEquals(setOf(2, 5), mastered)
    }

    @Test
    fun `masteredTables can return all 10 tables`() {
        val attempts = (1..10).flatMap { masteredAttempts(it) }
        val mastered = MasteryChecker.masteredTables(attempts)
        assertEquals((1..10).toSet(), mastered)
    }
}
