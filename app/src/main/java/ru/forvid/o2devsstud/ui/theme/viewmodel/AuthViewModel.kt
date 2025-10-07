package ru.forvid.o2devsstud.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

data class AuthState(
    val isAuthorized: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor() : ViewModel() {
    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState

    /**
     * Простейшая имитация логина:
     * - если username/password пустые — показываем ошибку;
     * - иначе — имитируем задержку и считаем логин успешным.
     */
    fun login(username: String, password: String) {
        // включаем индикатор загрузки
        _authState.value = _authState.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            // имитация сетевой задержки
            delay(350)

            // простая валидация — убирает предупреждение "parameter never used"
            if (username.isBlank() || password.isBlank()) {
                _authState.value = AuthState(
                    isAuthorized = false,
                    isLoading = false,
                    error = "Введите логин и пароль"
                )
                return@launch
            }

            // TODO: здесь должен быть реальный вызов репозитория/Retrofit -> auth
            // Сейчас — мок успешного результата:
            _authState.value = AuthState(isAuthorized = true, isLoading = false, error = null)
        }
    }

    fun onSignOut() {
        _authState.value = AuthState(isAuthorized = false)
    }
}
