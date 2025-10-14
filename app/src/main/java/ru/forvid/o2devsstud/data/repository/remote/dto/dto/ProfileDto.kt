package ru.forvid.o2devsstud.data.remote.dto

data class ProfileDto(
    val id: Long,
    val fullName: String,
    val column: String,
    val phoneDriver: String,
    val phoneColumn: String,
    val phoneLogist: String,
    val email: String?,
    val avatarUrl: String?
)
