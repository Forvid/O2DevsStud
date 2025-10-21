package ru.forvid.o2devsstud.data.repository.repository

import ru.forvid.o2devsstud.data.remote.dto.toUiItem
import ru.forvid.o2devsstud.domain.model.HistoryItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryRepository @Inject constructor(
    // Hilt автоматически предоставит правильный экземпляр с AuthInterceptor
    private val apiService: ApiService
) {
    /**
     * Получает историю заказов с сервера.
     * Теперь этот вызов будет автоматически включать API-ключ,
     * так как использует правильный экземпляр ApiService.
     */
    suspend fun getHistoryItems(): List<HistoryItem> {
        return apiService.getHistory().map { it.toUiItem() }
    }
}