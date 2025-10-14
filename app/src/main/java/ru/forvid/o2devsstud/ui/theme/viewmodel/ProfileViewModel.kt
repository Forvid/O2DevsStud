package ru.forvid.o2devsstud.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.forvid.o2devsstud.data.repository.repository.ProfileRepository
import ru.forvid.o2devsstud.domain.model.DriverProfile
import javax.inject.Inject

data class ProfileUiState(
    val profile: DriverProfile? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    // --- Внедряем репозиторий ---
    private val repository: ProfileRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _ui.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _ui.update { it.copy(isLoading = true, error = null) }
            try {
                // --- Вызывает метод из репозитория ---
                val profile = repository.getProfile()
                _ui.update { it.copy(profile = profile, isLoading = false) }
            } catch (e: Throwable) {
                _ui.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun logout() {
        // TODO: implement logout flow (clear token, go to auth)
    }

    fun saveAvatarUri(uri: Uri) {
        viewModelScope.launch {
            val currentProfile = _ui.value.profile ?: return@launch
            val updatedProfile = currentProfile.copy(avatarUrl = uri.toString())
            _ui.update { it.copy(profile = updatedProfile) }
            // TODO: Реализовать загрузку аватара на сервер через репозиторий
        }
    }
}