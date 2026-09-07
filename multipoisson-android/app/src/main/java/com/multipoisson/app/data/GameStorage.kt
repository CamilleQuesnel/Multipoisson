package com.multipoisson.app.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.multipoisson.app.model.GameResult

private const val PREFS_NAME = "multipoisson_prefs"
private const val KEY_HISTORY = "game_history"
private const val KEY_LAST_PLAYER = "last_player_name"
private const val MAX_HISTORY = 50

object GameStorage {
    private val gson = Gson()

    fun saveLastPlayerName(context: Context, name: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putString(KEY_LAST_PLAYER, name).apply()
    }

    fun getLastPlayerName(context: Context): String {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_LAST_PLAYER, "") ?: ""
    }

    fun saveGameResult(context: Context, result: GameResult) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val existing = getHistory(context).toMutableList()
        existing.add(0, result)
        val trimmed = existing.take(MAX_HISTORY)
        prefs.edit().putString(KEY_HISTORY, gson.toJson(trimmed)).apply()
    }

    fun getHistory(context: Context): List<GameResult> {
        val json = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_HISTORY, null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<GameResult>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
