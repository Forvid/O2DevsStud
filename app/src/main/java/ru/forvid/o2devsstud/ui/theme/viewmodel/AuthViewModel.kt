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
        // Подписывает на AppAuth — когда token станет не-null, считаем пользователя авторизованным.
        viewModelScope.launch {
            appAuth.authState.collect { auth ->
                _authState.value = AuthUiState(
                    isAuthorized = auth.token != null,
                    isLoading = false,
                    error = null
                )
            }
        }
    }

    fun login(username: String, password: String) {
        // Защита от одновременных вызовов
        if (_authState.value.isLoading) return

        // Простая валидация
        if (username.isBlank() || password.isBlank()) {
            _authState.value = _authState.value.copy(error = "Логин и пароль не могут быть пустыми")
            return
        }

        viewModelScope.launch {
            // Показывает загрузку, сбрасываем предыдущие ошибки
            _authState.value = _authState.value.copy(isLoading = true, error = null)
            try {
                repository.login(username, password)
                // Успех — AppAuth обновит поток, init{} обработает это и выставит isAuthorized=true и isLoading=false.
                // Ничего дополнительно не делаем здесь.
            } catch (e: Throwable) {
                // Показывает читаемое сообщение об ошибке
                val message = when (e) {
                    is IllegalArgumentException -> e.message ?: "Неверные данные"
                    else -> "Ошибка входа: ${e.message ?: "неизвестная ошибка"}"
                }
                _authState.value = AuthUiState(isAuthorized = false, isLoading = false, error = message)
            }
        }
    }

    fun onSignOut() {
        repository.logout()
    }
}
