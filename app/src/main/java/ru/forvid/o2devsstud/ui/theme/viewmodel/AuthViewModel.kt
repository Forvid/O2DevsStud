package ru.forvid.o2devsstud.ui.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

data class AuthUiState(val isLoggedIn: Boolean = false, val errorMessage: String? = null)

@HiltViewModel
class AuthViewModel @Inject constructor(
    // можно внедрить AuthRepository (интерфейс)
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(login: String, pass: String) {
        // Простейшая мок-валидация: логин = driver, pass = 1234
        CoroutineScope(Dispatchers.IO).launch {
            if (login == "driver" && pass == "1234") {
                _uiState.value = AuthUiState(isLoggedIn = true, errorMessage = null)
            } else {
                _uiState.value = AuthUiState(isLoggedIn = false, errorMessage = "Неверный логин/пароль")
            }
        }
    }

    fun logout() {
        _uiState.value = AuthUiState(isLoggedIn = false, errorMessage = null)
    }
}
