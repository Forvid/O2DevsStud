package ru.forvid.o2devsstud.ui.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
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

    // Простейшая реализация. Можно заменить на реальную аутентификацию позже.
    fun login(username: String, password: String) {
        _authState.value = _authState.value.copy(isLoading = true)
        // имитация успешного логина (для сборки/тестов)
        _authState.value = AuthState(isAuthorized = true, isLoading = false)
    }

    fun onSignOut() {
        _authState.value = AuthState(isAuthorized = false)
    }
}
