package ru.forvid.o2devsstud.data.remote.dto

import ru.forvid.o2devsstud.domain.model.Order
import ru.forvid.o2devsstud.domain.model.OrderStatus

fun OrderDto.toDomain(): Order =
    Order(
        id = this.id,
        from = this.fromAddress,
        to = this.toAddress,
        requestNumber = this.requestNumber,
        status = try { OrderStatus.valueOf(this.status) } catch (t: Throwable) { OrderStatus.PLACED },
        estimatedDays = this.estimatedDays
    )

fun TrackDto.toPolylinePoints(): List<Pair<Double, Double>> =
    points.map { it.lat to it.lng }
