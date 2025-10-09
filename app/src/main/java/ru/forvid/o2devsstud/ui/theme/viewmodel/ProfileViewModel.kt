package ru.forvid.o2devsstud.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.forvid.o2devsstud.data.remote.dto.ProfileDto
import ru.forvid.o2devsstud.data.remote.dto.toDomain
import ru.forvid.o2devsstud.domain.model.DriverProfile
import ru.forvid.o2devsstud.data.repository.repository.ApiService
import javax.inject.Inject

private const val TAG = "ProfileViewModel"

data class ProfileUiState(
    val profile: DriverProfile? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val dto: ProfileDto = apiService.getProfile()
                _uiState.update { it.copy(profile = dto.toDomain(), isLoading = false) }
            } catch (e: Throwable) {
                Log.e(TAG, "loadProfile error", e)
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun updateProfile(new: ProfileDto) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val updated = apiService.updateProfile(new)
                _uiState.update { it.copy(profile = updated.toDomain(), isLoading = false) }
            } catch (e: Throwable) {
                Log.e(TAG, "updateProfile error", e)
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun logout() {
        // Сделай очистку данных, навигацию и т.д. Реализация зависит от auth слоя
        // Пример: очистить токен в SharedPreferences/Datastore — здесь только сигнал
        _uiState.update { it.copy(profile = null) }
    }
}
