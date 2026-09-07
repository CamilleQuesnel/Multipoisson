package com.multipoisson.app.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_prefs")

class AppPreferences(private val context: Context) {

    companion object {
        val KEY_SOUND_ENABLED     = booleanPreferencesKey("sound_enabled")
        val KEY_ACTIVE_PROFILE_ID = stringPreferencesKey("active_profile_id")
        /** SHA-256 hex of the 4-digit parental code, empty = not yet set */
        val KEY_PARENTAL_CODE_HASH = stringPreferencesKey("parental_code_hash")
        val KEY_PARENTAL_GATE_UNLOCKED = booleanPreferencesKey("parental_gate_unlocked")
    }

    val soundEnabled: Flow<Boolean> = context.dataStore.data
        .catchIo()
        .map { it[KEY_SOUND_ENABLED] ?: false }

    val activeProfileId: Flow<String?> = context.dataStore.data
        .catchIo()
        .map { it[KEY_ACTIVE_PROFILE_ID] }

    val parentalCodeHash: Flow<String> = context.dataStore.data
        .catchIo()
        .map { it[KEY_PARENTAL_CODE_HASH] ?: "" }

    suspend fun setSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { it[KEY_SOUND_ENABLED] = enabled }
    }

    suspend fun setActiveProfileId(id: String) {
        context.dataStore.edit { it[KEY_ACTIVE_PROFILE_ID] = id }
    }

    suspend fun setParentalCodeHash(hash: String) {
        context.dataStore.edit { it[KEY_PARENTAL_CODE_HASH] = hash }
    }

    suspend fun clearActiveProfile() {
        context.dataStore.edit { it.remove(KEY_ACTIVE_PROFILE_ID) }
    }

    private fun Flow<Preferences>.catchIo(): Flow<Preferences> =
        catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
}
