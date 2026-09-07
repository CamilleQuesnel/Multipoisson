package com.multipoisson.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.multipoisson.app.data.preferences.AppPreferences
import com.multipoisson.app.data.repository.CollectionRepository
import com.multipoisson.app.data.repository.ProfileRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class AquariumViewModel(application: Application) : AndroidViewModel(application) {

    private val collectionRepo = CollectionRepository(application)
    private val profileRepo    = ProfileRepository(application)
    private val appPrefs       = AppPreferences(application)

    private val profileId: StateFlow<String> = appPrefs.activeProfileId
        .map { it.orEmpty() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "")

    /** IDs of all fish unlocked for the active profile. */
    val unlockedIds: StateFlow<Set<String>> = profileId
        .flatMapLatest { id ->
            if (id.isEmpty()) flowOf(emptySet())
            else collectionRepo.observeUnlocked(id)
                .map { list -> list.map { it.fishId }.toSet() }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    /** ID of the mascot currently equipped by the active profile. */
    val activeMascotId: StateFlow<String> = combine(
        profileId,
        profileRepo.observeAll(),
    ) { id, profiles ->
        profiles.find { it.id == id }?.activeMascotId.orEmpty()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "")

    /** Equip [fishId] as the active profile's mascot. No-op if the fish is locked. */
    fun selectMascot(fishId: String) {
        val id = profileId.value
        if (id.isEmpty()) return
        if (fishId !in unlockedIds.value) return
        viewModelScope.launch { profileRepo.setActiveMascot(id, fishId) }
    }
}
