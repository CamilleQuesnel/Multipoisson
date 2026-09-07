package com.multipoisson.app.model

import org.junit.Assert.*
import org.junit.Test

class GameConfigTest {

    @Test
    fun `GameConfig with valid questionCount creates successfully`() {
        val config = GameConfig(selectedTables = listOf(3), questionCount = 20)
        assertEquals(20, config.questionCount)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `GameConfig with questionCount 0 throws`() {
        GameConfig(selectedTables = listOf(3), questionCount = 0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `GameConfig with questionCount above MAX throws`() {
        GameConfig(selectedTables = listOf(3), questionCount = MAX_QUESTION_COUNT + 1)
    }

    @Test
    fun `GameConfig with questionCount equal to MAX is valid`() {
        val config = GameConfig(selectedTables = listOf(3), questionCount = MAX_QUESTION_COUNT)
        assertEquals(MAX_QUESTION_COUNT, config.questionCount)
    }

    @Test
    fun `GameConfig with questionCount 1 is valid`() {
        val config = GameConfig(selectedTables = listOf(3), questionCount = 1)
        assertEquals(1, config.questionCount)
    }

    @Test
    fun `GameConfig defaults are sane`() {
        val config = GameConfig(selectedTables = listOf(5), questionCount = 10)
        assertFalse(config.excludeZero)
        assertFalse(config.excludeOne)
        assertFalse(config.excludeTen)
        assertFalse(config.timerEnabled)
        assertEquals(GameMode.TABLE, config.mode)
        assertTrue(config.profileId.isEmpty())
        assertTrue(config.sessionId.isNotBlank())
    }

    @Test
    fun `MAX_QUESTION_COUNT is 100`() {
        assertEquals(100, MAX_QUESTION_COUNT)
    }
}
