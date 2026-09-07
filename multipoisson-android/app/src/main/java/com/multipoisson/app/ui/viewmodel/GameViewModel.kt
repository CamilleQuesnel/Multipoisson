package com.multipoisson.app.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.multipoisson.app.data.preferences.AppPreferences
import com.multipoisson.app.data.repository.CollectionRepository
import com.multipoisson.app.data.repository.GameRepository
import com.multipoisson.app.model.GameConfig
import com.multipoisson.app.model.GameResult
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val gameRepo       = GameRepository(application)
    private val collectionRepo = CollectionRepository(application)
    private val appPrefs       = AppPreferences(application)

    // ── Set by upstream screens ────────────────────────────────────────────
    var selectedTables by mutableStateOf<List<Int>>(emptyList())
        private set

    var config by mutableStateOf<GameConfig?>(null)
        private set

    // ── Accumulated question-by-question during the game ──────────────────
    private val _questionResults = mutableListOf<GameRepository.QuestionResult>()

    // ── Available to ResultScreen once finishGame() completes ─────────────
    var gameResult by mutableStateOf<GameResult?>(null)
        private set

    var newlyUnlockedFish by mutableStateOf<List<String>>(emptyList())
        private set

    /** True while the session is being persisted to the database. */
    var isSaving by mutableStateOf(false)
        private set

    // ── API ────────────────────────────────────────────────────────────────

    @JvmName("updateSelectedTables")
    fun setSelectedTables(tables: List<Int>) {
        selectedTables = tables
    }

    /** Called by AdvancedConfigScreen just before navigating to Countdown. */
    fun prepareGame(config: GameConfig) {
        this.config = config
        _questionResults.clear()
        gameResult = null
        newlyUnlockedFish = emptyList()
    }

    /** Called after each answer during the game (correct or wrong). */
    fun recordQuestion(result: GameRepository.QuestionResult) {
        _questionResults.add(result)
    }

    fun recordQuestion(tableN: Int, operandA: Int, operandB: Int, isCorrect: Boolean, responseTimeMs: Long) {
        _questionResults.add(GameRepository.QuestionResult(tableN, operandA, operandB, isCorrect, responseTimeMs))
    }

    /**
     * Called when the last question is answered.
     * Persists the session + attempts, checks for unlocks, then sets [gameResult].
     */
    fun finishGame(score: Int, maxScore: Int, bonusPoints: Int, elapsedSeconds: Int?) {
        val cfg = config ?: return
        isSaving = true
        viewModelScope.launch {
            val profileId = appPrefs.activeProfileId.first().orEmpty()

            if (profileId.isNotBlank()) {
                gameRepo.saveSession(
                    profileId      = profileId,
                    sessionId      = cfg.sessionId,
                    mode           = cfg.mode.name,
                    tables         = cfg.selectedTables,
                    questionResults = _questionResults.toList(),
                    durationSeconds = elapsedSeconds ?: 0,
                    timerEnabled   = cfg.timerEnabled,
                )
                newlyUnlockedFish = collectionRepo.checkAndUnlock(profileId)
            }

            gameResult = GameResult(
                id               = cfg.sessionId,
                sessionId        = cfg.sessionId,
                date             = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).format(Date()),
                profileId        = profileId,
                playerName       = "",
                questionCount    = _questionResults.size,
                score            = score,
                maxScore         = maxScore,
                bonusPoints      = bonusPoints,
                timeSeconds      = elapsedSeconds,
                newlyUnlockedFish = newlyUnlockedFish,
            )
            isSaving = false
        }
    }

    /** Reset everything when the player starts a new game from ResultScreen. */
    fun reset() {
        selectedTables    = emptyList()
        config            = null
        gameResult        = null
        newlyUnlockedFish = emptyList()
        isSaving          = false
        _questionResults.clear()
    }
}
