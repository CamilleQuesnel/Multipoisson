package com.multipoisson.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.multipoisson.app.data.preferences.AppPreferences
import com.multipoisson.app.data.repository.ProfileRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val profileRepo = ProfileRepository(application)
    private val appPrefs    = AppPreferences(application)

    val profiles: StateFlow<List<ProfileRepository.Profile>> =
        profileRepo.observeAll()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val activeProfileId: StateFlow<String?> =
        appPrefs.activeProfileId
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun selectProfile(id: String) {
        viewModelScope.launch { appPrefs.setActiveProfileId(id) }
    }
}
