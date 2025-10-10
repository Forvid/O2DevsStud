package ru.forvid.o2devsstud.domain.model

data class DriverProfile(
    val id: Long,
    val fullName: String,
    val column: String,
    val phoneDriver: String,
    val phoneColumn: String,
    val phoneLogist: String,
    val email: String? = null,
    val avatarUrl: String? = null
)
