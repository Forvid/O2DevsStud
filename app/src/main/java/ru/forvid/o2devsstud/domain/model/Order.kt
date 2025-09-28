package ru.forvid.o2devsstud.domain.model

data class Order(
    val id: Long,
    val from: String,
    val to: String,
    val requestNumber: String,
    val status: OrderStatus,
    val estimatedDays: Int
)
