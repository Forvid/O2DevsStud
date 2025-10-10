package ru.forvid.o2devsstud.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.forvid.o2devsstud.domain.model.DriverProfile
import javax.inject.Inject

data class ProfileUiState(
    val profile: DriverProfile? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    // TODO: inject repository if you have one
) : ViewModel() {

    private val _ui = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _ui.asStateFlow()

    init {
        // загрузка локально/с сервера при создании VM
        loadProfile()
    }

    fun loadProfile(id: Long = 0L) {
        viewModelScope.launch {
            _ui.update { it.copy(isLoading = true) }
            try {
                // TODO: replace with real repository call
                val p = DriverProfile(
                    id = id,
                    fullName = "Иванов Иван",
                    column = "Колонна 1",
                    phoneDriver = "+7 900 000 00 00",
                    phoneColumn = "+7 900 000 00 01",
                    phoneLogist = "+7 900 000 00 02",
                    email = "ivanov@example.com",
                    avatarUrl = null
                )
                _ui.update { it.copy(profile = p, isLoading = false, error = null) }
            } catch (e: Throwable) {
                _ui.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun logout() {
        // TODO: implement logout flow (clear token, go to auth)
    }

    /**
     * Сохраняет uri (строку) в локальном состоянии; при наличии API - загрузить/сохранить на сервер
     */
    fun saveAvatarUri(uriString: String) {
        viewModelScope.launch {
            val current = _ui.value.profile
            if (current != null) {
                val updated = current.copy(avatarUrl = uriString)
                _ui.update { it.copy(profile = updated) }
                // TODO: persist to repository/backend if required
            }
        }
    }
}
