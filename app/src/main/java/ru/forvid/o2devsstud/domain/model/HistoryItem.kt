package ru.forvid.o2devsstud.domain.model

/**
 * Модель данных для отображения одного элемента в списке истории поставок.
 */
data class HistoryItem(
    val id: Long,
    val from: String,
    val to: String,
    val timeOnRoute: String,
    val contractorName: String
)
