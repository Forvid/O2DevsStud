package ru.forvid.o2devsstud.data.repository.repository

import ru.forvid.o2devsstud.domain.model.DriverProfile
import javax.inject.Inject
import javax.inject.Singleton
import ru.forvid.o2devsstud.data.remote.dto.toDomain

@Singleton
class ProfileRepository @Inject constructor(
    private val apiService: ApiService
) {
    // Метод для получения профиля с сервера
    suspend fun getProfile(): DriverProfile {
        // Предполагает, что в ApiService есть метод getProfile(),
        // который возвращает DTO, а мы его конвертирует в доменную модель.
        // Если его нет, добавим.
        return apiService.getProfile().toDomain()
    }

    // TODO: Добавить методы для обновления профиля, если это потребуется
}