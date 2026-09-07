package com.multipoisson.app.domain

import com.multipoisson.app.data.db.entity.PlaySessionEntity
import com.multipoisson.app.data.db.entity.QuestionAttemptEntity
import com.multipoisson.app.domain.logic.CollectionChecker
import com.multipoisson.app.domain.logic.MasteryChecker
import org.junit.Assert.*
import org.junit.Test

class CollectionCheckerTest {

    private val NOW = System.currentTimeMillis()

    // ── helpers ────────────────────────────────────────────────────────────

    private fun attempt(tableN: Int, a: Int, b: Int) = QuestionAttemptEntity(
        profileId = "p1", sessionId = "s1",
        tableN = tableN, operandA = a, operandB = b,
        isCorrect = true, responseTimeMs = 3_000L,
        timerEnabled = false, timestamp = NOW,
    )

    private fun masteredAttempts(tableN: Int) =
        (2..9).flatMap { m ->
            val a = minOf(tableN, m); val b = maxOf(tableN, m)
            (1..3).map { attempt(tableN, a, b) }
        }

    private fun session(
        dateStr: String,
        questionCount: Int = 20,
        correctCount: Int = 20,
        timerEnabled: Boolean = false,
    ) = PlaySessionEntity(
        id = "$dateStr-$questionCount",
        profileId = "p1",
        dateStr = dateStr,
        mode = "TABLE",
        tables = "[1]",
        questionCount = questionCount,
        correctCount = correctCount,
        durationSeconds = 120,
        timerEnabled = timerEnabled,
        timestamp = NOW,
    )

    // ── table fish ─────────────────────────────────────────────────────────

    @Test
    fun `computeNewUnlocks gives table fish when table mastered`() {
        val attempts = masteredAttempts(3)
        val result = CollectionChecker.computeNewUnlocks(attempts, emptyList(), emptySet())
        assertTrue(FishId.TABLE_3 in result)
    }

    @Test
    fun `computeNewUnlocks does not re-unlock already unlocked table fish`() {
        val attempts = masteredAttempts(3)
        val result = CollectionChecker.computeNewUnlocks(
            attempts, emptyList(), alreadyUnlocked = setOf(FishId.TABLE_3)
        )
        assertFalse(FishId.TABLE_3 in result)
    }

    @Test
    fun `computeNewUnlocks gives no table fish when not mastered`() {
        val result = CollectionChecker.computeNewUnlocks(emptyList(), emptyList(), emptySet())
        assertTrue(result.none { it.startsWith("table_") })
    }

    // ── bouquet ────────────────────────────────────────────────────────────

    @Test
    fun `computeNewUnlocks gives bouquet when all 10 tables mastered`() {
        val attempts = (1..10).flatMap { masteredAttempts(it) }
        val result = CollectionChecker.computeNewUnlocks(attempts, emptyList(), emptySet())
        assertTrue(FishId.BOUQUET in result)
    }

    @Test
    fun `computeNewUnlocks does not give bouquet when only 9 tables mastered`() {
        val attempts = (1..9).flatMap { masteredAttempts(it) }
        val result = CollectionChecker.computeNewUnlocks(attempts, emptyList(), emptySet())
        assertFalse(FishId.BOUQUET in result)
    }

    // ── regularity ─────────────────────────────────────────────────────────

    @Test
    fun `computeNewUnlocks gives regular_7days after 7 distinct days`() {
        val sessions = (1..7).map { session("2026-09-0$it") }
        val result = CollectionChecker.computeNewUnlocks(emptyList(), sessions, emptySet())
        assertTrue(FishId.REGULAR_7 in result)
    }

    @Test
    fun `computeNewUnlocks does not give regular_7days with only 6 days`() {
        val sessions = (1..6).map { session("2026-09-0$it") }
        val result = CollectionChecker.computeNewUnlocks(emptyList(), sessions, emptySet())
        assertFalse(FishId.REGULAR_7 in result)
    }

    @Test
    fun `computeNewUnlocks counts distinct days not total sessions`() {
        // 10 sessions all on the same day → still only 1 distinct day
        val sessions = (1..10).map { session("2026-09-01") }
        val result = CollectionChecker.computeNewUnlocks(emptyList(), sessions, emptySet())
        assertFalse(FishId.REGULAR_7 in result)
    }

    // ── performance ────────────────────────────────────────────────────────

    @Test
    fun `computeNewUnlocks gives perf_50_80 when timed session hits threshold`() {
        val sessions = listOf(session("2026-09-01", questionCount = 50, correctCount = 40, timerEnabled = true))
        val result = CollectionChecker.computeNewUnlocks(emptyList(), sessions, emptySet())
        assertTrue(FishId.PERF_50_80 in result)
    }

    @Test
    fun `computeNewUnlocks does not give perf_50_80 without timer`() {
        val sessions = listOf(session("2026-09-01", questionCount = 50, correctCount = 50, timerEnabled = false))
        val result = CollectionChecker.computeNewUnlocks(emptyList(), sessions, emptySet())
        assertFalse(FishId.PERF_50_80 in result)
    }

    @Test
    fun `computeNewUnlocks gives perf_100_90 for 100 questions at 90 percent`() {
        val sessions = listOf(session("2026-09-01", questionCount = 100, correctCount = 90, timerEnabled = true))
        val result = CollectionChecker.computeNewUnlocks(emptyList(), sessions, emptySet())
        assertTrue(FishId.PERF_100_90 in result)
    }

    @Test
    fun `computeNewUnlocks does not give perf_100_90 for less than 100 questions`() {
        val sessions = listOf(session("2026-09-01", questionCount = 99, correctCount = 99, timerEnabled = true))
        val result = CollectionChecker.computeNewUnlocks(emptyList(), sessions, emptySet())
        assertFalse(FishId.PERF_100_90 in result)
    }

    // ── legendary ──────────────────────────────────────────────────────────

    @Test
    fun `computeNewUnlocks gives legendary for 100 out of 100 timed`() {
        val sessions = listOf(session("2026-09-01", questionCount = 100, correctCount = 100, timerEnabled = true))
        val result = CollectionChecker.computeNewUnlocks(emptyList(), sessions, emptySet())
        assertTrue(FishId.LEGENDARY in result)
    }

    @Test
    fun `computeNewUnlocks does not give legendary for 99 out of 100`() {
        val sessions = listOf(session("2026-09-01", questionCount = 100, correctCount = 99, timerEnabled = true))
        val result = CollectionChecker.computeNewUnlocks(emptyList(), sessions, emptySet())
        assertFalse(FishId.LEGENDARY in result)
    }
}
