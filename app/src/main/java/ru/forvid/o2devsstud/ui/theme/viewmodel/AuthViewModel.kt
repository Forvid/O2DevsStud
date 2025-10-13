package ru.forvid.o2devsstud.ui.theme.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.forvid.o2devsstud.data.auth.AppAuth
import ru.forvid.o2devsstud.data.repository.repository.AuthRepository
import javax.inject.Inject

// Переименовываю AuthState в AuthUiState, чтобы не путать с AuthState из AppAuth.
data class AuthUiState(
    val isAuthorized: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
    appAuth: AppAuth
) : ViewModel() {

    private val _authState = MutableStateFlow(AuthUiState())
    val authState: StateFlow<AuthUiState> = _authState.asStateFlow()

    init {
        // При создании ViewModel подписываюсь на изменения в AppAuth.
        // Это гарантирует, что UI всегда будет отражать реальное состояние авторизации.
        viewModelScope.launch {
            appAuth.authState.collect { authState ->
                _authState.value = AuthUiState(isAuthorized = authState.token != null)
            }
        }
    }

    fun login(username: String, password: String) {
        // Проверrf, не идет ли уже загрузка.
        if (_authState.value.isLoading) {
            return
        }
        // Простая валидация на клиенте.
        if (username.isBlank() || password.isBlank()) {
            _authState.value = _authState.value.copy(error = "Логин и пароль не могут быть пустыми")
            return
        }

        viewModelScope.launch {
            // 1. Устанавливаю состояние "загрузка".
            _authState.value = AuthUiState(isLoading = true)
            try {
                // 2. Вызываю реальный метод репозитория для выполнения входа.
                repository.login(username, password)
                // 3. Если вызов прошел успешно, AppAuth обновит свое состояние.
                // `init`-блок выше "увидит" это изменение и автоматически
                // установит isAuthorized = true. Больше здесь ничего делать не нужно.

            } catch (e: Exception) {
                // 4. В случае любой ошибки (сетевой, 404 и т.д.) показываю сообщение.
                // Сбрасываю isLoading и устанавливаю текст ошибки.
                _authState.value = AuthUiState(isLoading = false, error = "Неверный логин или пароль")
            }
        }
    }

    fun onSignOut() {
        repository.logout()
    }
}