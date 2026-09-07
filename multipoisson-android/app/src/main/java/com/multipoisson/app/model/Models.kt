package com.multipoisson.app.model

import java.util.UUID

/** Hard cap on any game series — never exceed 100 questions. */
const val MAX_QUESTION_COUNT = 100

enum class GameMode { TABLE, MIXTE, DEFI }

data class GameConfig(
    val selectedTables: List<Int>,
    val timerEnabled: Boolean = false,
    val questionCount: Int,
    val profileId: String = "",
    val sessionId: String = UUID.randomUUID().toString(),
    val mode: GameMode = GameMode.TABLE,
) {
    init {
        require(questionCount in 1..MAX_QUESTION_COUNT) {
            "questionCount doit être entre 1 et $MAX_QUESTION_COUNT, reçu: $questionCount"
        }
    }
}

data class Question(val a: Int, val b: Int, val answer: Int)

data class GameResult(
    val id: String,
    val sessionId: String = "",
    val date: String,
    val profileId: String = "",
    val playerName: String,
    val questionCount: Int,
    val score: Int,
    val maxScore: Int,
    val bonusPoints: Int,
    val timeSeconds: Int?,
    val newlyUnlockedFish: List<String> = emptyList(),
)
