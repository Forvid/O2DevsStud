package ru.forvid.o2devsstud.data.auth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppAuth @Inject constructor() {
    // Приватный MutableStateFlow, чтобы изменять состояние можно было только из этого класса.
    private val _authState = MutableStateFlow(AuthState())

    // Публичный StateFlow только для чтения состояния извне.
    val authState = _authState.asStateFlow()

    /**
     * Устанавливает состояние "авторизован", сохраняя токен.
     */
    fun setAuth(token: String) {
        _authState.value = AuthState(token)
    }

    /**
     * Сбрасывает состояние, удаляя токен.
     */
    fun clearAuth() {
        _authState.value = AuthState()
    }

    /**
     * Внутренний класс, описывающий состояние.
     * Наличие токена означает, что пользователь авторизован.
     */
    data class AuthState(val token: String? = null)
}