package ru.forvid.o2devsstud.data.remote.dto

import ru.forvid.o2devsstud.data.repository.remote.dto.dto.OrderDto
import ru.forvid.o2devsstud.domain.model.Order
import ru.forvid.o2devsstud.domain.model.OrderStatus

fun OrderDto.toDomain(): Order {
    val idLong: Long = this.orderId
        ?: this.idStr?.toLongOrNull()
        ?: System.currentTimeMillis()

    val fromVal = this.fromAddress ?: this.from ?: ""
    val toVal = this.toAddress ?: this.to ?: ""
    val reqNum = this.requestNumber ?: this.requestNumberAlt ?: ""
    val days = this.estimatedDays ?: this.estimatedDaysAlt

    // Безопасный метод fromString из enum
    val statusVal = OrderStatus.fromString(this.status)

    val dateVal = this.date ?: "Нет даты"
    // Если с сервера не пришло имя статуса, берет его из enum
    val statusNameVal = this.statusName ?: statusVal.displayName

    val codAmountVal = this.codAmount?.toDoubleOrNull()

    return Order(
        id = idLong,
        from = fromVal,
        to = toVal,
        requestNumber = reqNum,
        status = statusVal,
        estimatedDays = days,
        trackId = this.trackId,
        date = dateVal,
        statusName = statusNameVal,
        codAmount = codAmountVal
    )
}
