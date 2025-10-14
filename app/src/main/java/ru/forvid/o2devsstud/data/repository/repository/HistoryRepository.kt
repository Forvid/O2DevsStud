package ru.forvid.o2devsstud.data.repository.repository

import ru.forvid.o2devsstud.data.remote.dto.toUiItem
import ru.forvid.o2devsstud.domain.model.HistoryItem
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Репозиторий для работы с данными истории поставок.
 * Инкапсулирует логику получения данных из ApiService.
 */
@Singleton
class HistoryRepository @Inject constructor(
    private val apiService: ApiService
) {
    /**
     * Загружает историю поставок с сервера и преобразует ее в UI-модели.
     */
    suspend fun getHistoryItems(): List<HistoryItem> {
        // Получает DTO из сети и сразу маппим в список UI-моделей
        return apiService.getHistory().map { it.toUiItem() }
    }
}