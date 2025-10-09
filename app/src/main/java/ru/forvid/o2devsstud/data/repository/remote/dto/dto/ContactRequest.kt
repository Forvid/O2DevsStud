package ru.forvid.o2devsstud.data.remote.dto

data class ContactRequest(
    val message: String,
    val email: String? = null,
    val phone: String? = null
)
