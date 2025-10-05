package ru.forvid.o2devsstud.data.remote.dto

import ru.forvid.o2devsstud.data.repository.remote.dto.dto.OrderDto
import ru.forvid.o2devsstud.domain.model.Order
import ru.forvid.o2devsstud.domain.model.OrderStatus

fun OrderDto.toDomain(): Order {
    // Попробуем взять числовой orderId, иначе попытаться распарсить строковый idStr в Long,
    // иначе fallback на System.currentTimeMillis()
    val idLong: Long = this.orderId
        ?: this.idStr?.let { str ->
            // если строка содержит числа — может быть hex или просто digits
            // пробуем сначала toLongOrNull(), потом parse hex, иначе генерим
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
    val statusVal = try { OrderStatus.valueOf(this.status ?: "PLACED") } catch (_: Throwable) { OrderStatus.PLACED }
    val days = this.estimatedDays ?: this.estimatedDaysAlt ?: 1

    return Order(
        id = idLong,
        from = fromVal,
        to = toVal,
        requestNumber = reqNum,
        status = statusVal,
        estimatedDays = days
    )
}

