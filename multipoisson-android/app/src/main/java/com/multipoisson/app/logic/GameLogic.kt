package com.multipoisson.app.logic

import com.multipoisson.app.model.GameConfig
import com.multipoisson.app.model.Question
import kotlin.math.max

fun generateQuestions(config: GameConfig): List<Question> {
    val pool = mutableListOf<Question>()
    for (table in config.selectedTables) {
        for (m in 0..10) {
            if (config.excludeZero && m == 0) continue
            if (config.excludeOne && m == 1) continue
            if (config.excludeTen && m == 10) continue
            pool.add(Question(a = table, b = m, answer = table * m))
        }
    }
    if (pool.isEmpty()) return emptyList()

    val result = mutableListOf<Question>()
    while (result.size < config.questionCount) {
        result.addAll(pool.shuffled())
    }
    return result.take(config.questionCount)
}

/**
 * Adaptive question generation for "Défi" mode.
 * Questions from tables where the success rate is lower are picked more often.
 *
 * @param weights Map of table index → weight (higher = appears more frequently).
 *                A table with 0 correct answers gets weight 3, a table at 100% gets weight 1.
 */
fun generateAdaptiveQuestions(
    config: GameConfig,
    weights: Map<Int, Int>,
): List<Question> {
    val pool = mutableListOf<Question>()
    for (table in config.selectedTables) {
        val weight = weights.getOrDefault(table, 2).coerceIn(1, 5)
        repeat(weight) {
            for (m in 0..10) {
                if (config.excludeZero && m == 0) continue
                if (config.excludeOne && m == 1) continue
                if (config.excludeTen && m == 10) continue
                pool.add(Question(a = table, b = m, answer = table * m))
            }
        }
    }
    if (pool.isEmpty()) return emptyList()

    val result = mutableListOf<Question>()
    while (result.size < config.questionCount) {
        result.addAll(pool.shuffled())
    }
    return result.take(config.questionCount)
}

fun formatTime(totalSeconds: Int): String {
    val mins = totalSeconds / 60
    val secs = totalSeconds % 60
    return if (mins == 0) "${secs}s" else "${mins}min ${secs.toString().padStart(2, '0')}s"
}

fun getBonusForTime(ms: Long, timerEnabled: Boolean): Int {
    if (!timerEnabled) return 0
    return when {
        ms < 5_000L  -> 5
        ms < 10_000L -> 3
        ms < 20_000L -> 1
        else         -> 0
    }
}

fun getBonusLabel(bonus: Int): String = when (bonus) {
    5    -> "⚡⚡ ULTRA ! +5 bonus"
    3    -> "⚡ RAPIDE ! +3 bonus"
    1    -> "👍 BIEN ! +1 bonus"
    else -> ""
}

/**
 * Computes per-table adaptive weights from recent session history.
 * Tables with low success rates (many errors) get higher weights (appear more often).
 */
fun computeTableWeights(
    tableSuccessRates: Map<Int, Float>,
): Map<Int, Int> = tableSuccessRates.mapValues { (_, rate) ->
    when {
        rate < 0.40f -> 5
        rate < 0.60f -> 4
        rate < 0.75f -> 3
        rate < 0.90f -> 2
        else         -> 1
    }
}
