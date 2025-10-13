package ru.forvid.o2devsstud.data.repository.repository

import ru.forvid.o2devsstud.data.auth.AppAuth
import ru.forvid.o2devsstud.data.remote.dto.LoginRequestDto
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

/**
 * Репозиторий для авторизации.
 * Его задача - сходить в сеть и сохранить результат в AppAuth.
 */
@Singleton
class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val appAuth: AppAuth // Инжектирую хранилище состояния.
) {
    // --- ВРЕМЕННЫЙ КОД ДЛЯ ТЕСТИРОВАНИЯ ---
    private val isDevelopmentMode = true

    /**
     * Выполняет вход пользователя.
     * В случае успеха сохраняет токен в AppAuth.
     * В случае ошибки выбрасывает исключение, которое будет поймано в ViewModel.
     */
    suspend fun login(username: String, password: String) {
        // --- ВРЕМЕННЫЙ КОД ДЛЯ ТЕСТИРОВАНИЯ ---
        if (isDevelopmentMode) {
            // Если включен режим разработки, имитируем успешный вход
            // и генерирует случайный фейковый токен.
            val fakeToken = "fake_token_${Random.nextLong()}"
            appAuth.setAuth(fakeToken)
            return
        }
        // --- КОНЕЦ ВРЕМЕННОГО КОДА ---

        // Выполняю реальный сетевой запрос.
        // Этот код будет работать, только если isDevelopmentMode = false.
        val response = apiService.login(LoginRequestDto(login = username, password = password))
        // Сохраняю полученный токен.
        appAuth.setAuth(response.token)
    }

    /**
     * Выполняет выход, очищая состояние в AppAuth.
     */
    fun logout() {
        appAuth.clearAuth()
    }
}