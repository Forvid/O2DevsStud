package ru.forvid.o2devsstud.data.repository.repository

import ru.forvid.o2devsstud.data.remote.dto.LoginRequestDto
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun login(username: String, password: String): String {
        val response = apiService.login(LoginRequestDto(username, password))
        return response.token
    }
}
