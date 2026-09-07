package com.multipoisson.app.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.multipoisson.app.data.repository.ProfileRepository
import com.multipoisson.app.domain.SchoolYear
import kotlinx.coroutines.launch
import java.util.Calendar

class CreateProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val profileRepo = ProfileRepository(application)

    var name by mutableStateOf("")
    var schoolYear by mutableStateOf(SchoolYear.CE1)
    var birthDay by mutableIntStateOf(1)
    var birthMonth by mutableIntStateOf(1)
    var birthYear by mutableIntStateOf(2015)

    var isSaving by mutableStateOf(false)
        private set

    var done by mutableStateOf(false)
        private set

    fun create() {
        if (isSaving) return
        isSaving = true
        viewModelScope.launch {
            val cal = Calendar.getInstance().apply {
                set(birthYear, birthMonth - 1, birthDay, 12, 0, 0)
                set(Calendar.MILLISECOND, 0)
            }
            profileRepo.create(
                name       = name.trim(),
                birthDateMs = cal.timeInMillis,
                schoolYear = schoolYear,
            )
            isSaving = false
            done = true
        }
    }
}
