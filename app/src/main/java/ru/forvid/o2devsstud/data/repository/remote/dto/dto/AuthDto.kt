package ru.forvid.o2devsstud.data.remote.dto

data class LoginRequestDto(
    val login: String,
    val password: String
)

data class TokenResponseDto(
    val token: String
)
