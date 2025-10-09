package ru.forvid.o2devsstud.data.remote.dto

data class HistoryDto(
    val id: Long,
    val from: String?,
    val to: String?,
    val timeOnRoute: String?,
    val contractorName: String?,
    val loadPlace: String?,
    val unloadPlace: String?
)
