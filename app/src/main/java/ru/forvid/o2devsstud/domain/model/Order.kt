package ru.forvid.o2devsstud.domain.model

data class Order(
    val id: Long,
    val from: String,
    val to: String,
    val requestNumber: String,
    val status: OrderStatus,
    val estimatedDays: Int,
    val trackId: Long? = null,
    val date: String, // Дата заказа, например "24.07.2024"
    val statusName: String, // Текстовое описание статуса, например "Заказ размещен"
    val codAmount: String? // Сумма наложенного платежа, может быть null
)