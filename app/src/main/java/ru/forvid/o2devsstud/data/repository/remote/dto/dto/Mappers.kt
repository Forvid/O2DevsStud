package ru.forvid.o2devsstud.data.remote.dto

import ru.forvid.o2devsstud.domain.model.DriverProfile
import ru.forvid.o2devsstud.domain.model.HistoryItem

fun ProfileDto.toDomain(): DriverProfile = DriverProfile(
    id = this.id,
    fullName = this.fullName,
    column = this.column,
    phoneDriver = this.phoneDriver,
    phoneColumn = this.phoneColumn,
    phoneLogist = this.phoneLogist,
    email = this.email,
    avatarUrl = this.avatarUrl
)

fun HistoryDto.toUiItem(): HistoryItem = HistoryItem(
    id = id,
    from = from ?: "",
    to = to ?: "",
    timeOnRoute = timeOnRoute ?: "",
    contractorName = contractorName ?: ""
)
