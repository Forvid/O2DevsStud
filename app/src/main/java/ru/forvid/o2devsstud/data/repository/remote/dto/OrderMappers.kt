package ru.forvid.o2devsstud.data.remote.dto

import ru.forvid.o2devsstud.data.repository.remote.dto.dto.OrderDto
import ru.forvid.o2devsstud.domain.model.Order
import ru.forvid.o2devsstud.domain.model.OrderStatus

fun OrderDto.toDomain(): Order {
    // Попробую взять числовой orderId, иначе попытаться распарсить строковый idStr в Long,
    // иначе fallback на System.currentTimeMillis()
    val idLong: Long = this.orderId
        ?: this.idStr?.let { str ->
            val n = str.toLongOrNull()
            if (n != null) n
            else {
                // попытка парсинга hex (например "4fca")
                try {
                    str.toLong(16)
                } catch (_: Throwable) {
                    System.currentTimeMillis()
                }
            }
        } ?: System.currentTimeMillis()

    val fromVal = this.fromAddress ?: this.from ?: ""
    val toVal = this.toAddress ?: this.to ?: ""
    val reqNum = this.requestNumber ?: this.requestNumberAlt ?: ""
    val days = this.estimatedDays ?: this.estimatedDaysAlt ?: 1

    // Безопасное преобразование статуса (если строка не совпадает — ставим PLACED)
    val statusVal = try {
        if (this.status.isNullOrBlank()) OrderStatus.PLACED
        else OrderStatus.valueOf(this.status)
    } catch (_: Throwable) {
        OrderStatus.PLACED
    }

    return Order(
        id = idLong,
        from = fromVal,
        to = toVal,
        requestNumber = reqNum,
        status = statusVal,
        estimatedDays = days,
        trackId = this.trackId
    )
}
