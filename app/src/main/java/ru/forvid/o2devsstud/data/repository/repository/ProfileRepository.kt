package ru.forvid.o2devsstud.data.repository.repository

import ru.forvid.o2devsstud.data.remote.dto.toDomain // <-- Убедитесь, что этот импорт есть
import ru.forvid.o2devsstud.domain.model.DriverProfile
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getProfile(): DriverProfile {
        return apiService.getProfile().toDomain()
    }
}
