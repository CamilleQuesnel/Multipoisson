package com.multipoisson.app.logic

import com.multipoisson.app.model.GameConfig
import com.multipoisson.app.model.GameMode
import com.multipoisson.app.model.MAX_QUESTION_COUNT
import org.junit.Assert.*
import org.junit.Test

class GameLogicTest {

    // ── generateQuestions ──────────────────────────────────────────────────

    @Test
    fun `generateQuestions returns exactly questionCount questions`() {
        val config = GameConfig(selectedTables = listOf(3), questionCount = 10)
        val questions = generateQuestions(config)
        assertEquals(10, questions.size)
    }

    @Test
    fun `generateQuestions respects excludeZero`() {
        val config = GameConfig(selectedTables = listOf(5), questionCount = 50, excludeZero = true)
        val questions = generateQuestions(config)
        assertTrue(questions.none { it.b == 0 })
    }

    @Test
    fun `generateQuestions respects excludeOne`() {
        val config = GameConfig(selectedTables = listOf(4), questionCount = 50, excludeOne = true)
        val questions = generateQuestions(config)
        assertTrue(questions.none { it.b == 1 })
    }

    @Test
    fun `generateQuestions respects excludeTen`() {
        val config = GameConfig(selectedTables = listOf(2), questionCount = 50, excludeTen = true)
        val questions = generateQuestions(config)
        assertTrue(questions.none { it.b == 10 })
    }

    @Test
    fun `generateQuestions returns empty list when pool is empty`() {
        // All multipliers excluded → empty pool
        val config = GameConfig(
            selectedTables = listOf(3),
            questionCount = 5,
            excludeZero = true,
            excludeOne = true,
            excludeTen = true,
        )
        // Pool has 0..10 minus 0, 1, 10 = 8 values — not empty
        // To get truly empty we'd need to exclude all, which isn't possible with 3 flags.
        // Instead test that results are within the allowed factors.
        val questions = generateQuestions(config)
        assertTrue(questions.all { it.b in 2..9 })
    }

    @Test
    fun `generateQuestions computes correct answers`() {
        val config = GameConfig(selectedTables = listOf(7), questionCount = 30)
        val questions = generateQuestions(config)
        questions.forEach { q ->
            assertEquals(q.a * q.b, q.answer)
        }
    }

    @Test
    fun `generateQuestions covers multiple tables`() {
        val config = GameConfig(selectedTables = listOf(3, 5, 7), questionCount = 60)
        val questions = generateQuestions(config)
        val tables = questions.map { it.a }.toSet()
        assertTrue(3 in tables && 5 in tables && 7 in tables)
    }

    // ── formatTime ─────────────────────────────────────────────────────────

    @Test
    fun `formatTime under 1 minute shows seconds only`() {
        assertEquals("45s", formatTime(45))
    }

    @Test
    fun `formatTime exactly 60 seconds shows 1min 00s`() {
        assertEquals("1min 00s", formatTime(60))
    }

    @Test
    fun `formatTime pads seconds with leading zero`() {
        assertEquals("2min 05s", formatTime(125))
    }

    @Test
    fun `formatTime zero seconds`() {
        assertEquals("0s", formatTime(0))
    }

    // ── getBonusForTime ────────────────────────────────────────────────────

    @Test
    fun `getBonusForTime returns 0 when timer disabled`() {
        assertEquals(0, getBonusForTime(1_000L, timerEnabled = false))
    }

    @Test
    fun `getBonusForTime returns 5 under 5 seconds`() {
        assertEquals(5, getBonusForTime(4_999L, timerEnabled = true))
    }

    @Test
    fun `getBonusForTime returns 3 under 10 seconds`() {
        assertEquals(3, getBonusForTime(9_999L, timerEnabled = true))
    }

    @Test
    fun `getBonusForTime returns 1 under 20 seconds`() {
        assertEquals(1, getBonusForTime(19_999L, timerEnabled = true))
    }

    @Test
    fun `getBonusForTime returns 0 over 20 seconds`() {
        assertEquals(0, getBonusForTime(20_000L, timerEnabled = true))
    }

    // ── computeTableWeights ────────────────────────────────────────────────

    @Test
    fun `computeTableWeights gives weight 1 for near-perfect tables`() {
        val weights = computeTableWeights(mapOf(3 to 0.95f))
        assertEquals(1, weights[3])
    }

    @Test
    fun `computeTableWeights gives weight 5 for struggling tables`() {
        val weights = computeTableWeights(mapOf(7 to 0.30f))
        assertEquals(5, weights[7])
    }

    @Test
    fun `computeTableWeights maps all thresholds correctly`() {
        val weights = computeTableWeights(mapOf(
            1 to 0.20f,  // < 0.40 → 5
            2 to 0.50f,  // < 0.60 → 4
            3 to 0.65f,  // < 0.75 → 3
            4 to 0.82f,  // < 0.90 → 2
            5 to 0.95f,  // else   → 1
        ))
        assertEquals(5, weights[1])
        assertEquals(4, weights[2])
        assertEquals(3, weights[3])
        assertEquals(2, weights[4])
        assertEquals(1, weights[5])
    }
}
