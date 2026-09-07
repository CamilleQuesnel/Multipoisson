package com.multipoisson.app.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.multipoisson.app.data.preferences.AppPreferences
import com.multipoisson.app.data.repository.ProfileRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.security.MessageDigest

class ParentsViewModel(application: Application) : AndroidViewModel(application) {

    private val appPrefs   = AppPreferences(application)
    private val profileRepo = ProfileRepository(application)

    // ── Parental gate ──────────────────────────────────────────────────────
    val parentalCodeHash: StateFlow<String> = appPrefs.parentalCodeHash
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "")

    /** True once the correct PIN has been entered this session. */
    var isUnlocked by mutableStateOf(false)
        private set

    /** Returns true if [pin] matches the stored hash, and unlocks the screen. */
    fun validateAndUnlock(pin: String): Boolean {
        val match = hashPin(pin) == parentalCodeHash.value
        if (match) isUnlocked = true
        return match
    }

    /** Save a new PIN and immediately unlock the screen. */
    fun createCode(pin: String) {
        viewModelScope.launch {
            appPrefs.setParentalCodeHash(hashPin(pin))
            isUnlocked = true
        }
    }

    /** Change the PIN — [oldPin] must match the current hash. Returns false on mismatch. */
    fun changeCode(oldPin: String, newPin: String): Boolean {
        if (hashPin(oldPin) != parentalCodeHash.value) return false
        viewModelScope.launch { appPrefs.setParentalCodeHash(hashPin(newPin)) }
        return true
    }

    // ── Profiles ───────────────────────────────────────────────────────────
    val profiles: StateFlow<List<ProfileRepository.Profile>> = profileRepo.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val activeProfileId: StateFlow<String?> = appPrefs.activeProfileId
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun deleteProfile(id: String) {
        viewModelScope.launch {
            if (activeProfileId.value == id) appPrefs.clearActiveProfile()
            profileRepo.delete(id)
        }
    }

    // ── Settings ───────────────────────────────────────────────────────────
    val soundEnabled: StateFlow<Boolean> = appPrefs.soundEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    fun setSoundEnabled(enabled: Boolean) {
        viewModelScope.launch { appPrefs.setSoundEnabled(enabled) }
    }

    fun signOut() {
        viewModelScope.launch { appPrefs.clearActiveProfile() }
    }

    // ── Helpers ────────────────────────────────────────────────────────────
    private fun hashPin(pin: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(pin.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }
}
