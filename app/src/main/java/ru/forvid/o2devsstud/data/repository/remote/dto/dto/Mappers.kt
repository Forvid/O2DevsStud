package ru.forvid.o2devsstud.data.remote.dto

import ru.forvid.o2devsstud.domain.model.DriverProfile
import ru.forvid.o2devsstud.ui.screens.HistoryItem

fun ProfileDto.toDomain(): DriverProfile = DriverProfile(
    id = id ?: System.currentTimeMillis(),
    fullName = fullName ?: "",
    column = column ?: "",
    phoneDriver = phoneDriver ?: "",
    phoneColumn = phoneColumn ?: "",
    phoneLogist = phoneLogist ?: "",
    email = email
)

fun HistoryDto.toUiItem(): HistoryItem = HistoryItem(
    id = id,
    from = from ?: "",
    to = to ?: "",
    timeOnRoute = timeOnRoute ?: "",
    contractorName = contractorName ?: ""
)
