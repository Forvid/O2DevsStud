package ru.forvid.o2devsstud.data.repository.repository

import ru.forvid.o2devsstud.data.remote.dto.toUiItem
import ru.forvid.o2devsstud.domain.model.HistoryItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getHistoryItems(): List<HistoryItem> {
        return apiService.getHistory().map { it.toUiItem() }
    }
}
