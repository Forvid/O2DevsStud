package ru.forvid.o2devsstud.data.repository.repository

import ru.forvid.o2devsstud.data.auth.AppAuth
import ru.forvid.o2devsstud.data.remote.dto.LoginRequestDto
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

/**
 * Репозиторий для авторизации.
 * Его задача - выполнить вход (в dev-режиме — эмулировать), и сохранить результат в AppAuth.
 */
@Singleton
class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val appAuth: AppAuth
) {
    /**
     * Флаг разработки.
     * В debug/локальной сборке можно держать true, чтобы было проще тестировать.
     * Перед релизом переключите в false.
     */
    private val isDevelopmentMode: Boolean = true

    /**
     * Demo credentials — те, которые вы будете передавать преподавателю.
     * Можно расширить список при необходимости.
     */
    private val demoLogin = "demo"
    private val demoPassword = "demo123"

    /**
     * Выполняет вход пользователя.
     * В development-режиме — если учётные совпадают с demo — устанавливаем фиктивный токен в AppAuth.
     * Иначе, при isDevelopmentMode = false — делаем реальный сетевой вызов.
     */
    suspend fun login(username: String, password: String) {
        if (isDevelopmentMode) {
            // Если переданы demo-учётные — эмитирует успешный вход.
            if (username == demoLogin && password == demoPassword) {
                val fakeToken = "demo_token_123456" // фиксированный токен удобнее для отладки
                appAuth.setAuth(fakeToken)
                return
            } else {
                // В режиме разработки другие комбинации считает неверными — бросает исключение,
                // чтобы ViewModel показала сообщение "неверный логин/пароль".
                throw IllegalArgumentException("Invalid demo credentials")
            }
        }

        // --- production: реальный сетевой вызов ---
        val response = apiService.login(LoginRequestDto(login = username, password = password))
        appAuth.setAuth(response.token)
    }

    /**
     * Выполняет выход, очищая состояние в AppAuth.
     */
    fun logout() {
        appAuth.clearAuth()
    }
}
