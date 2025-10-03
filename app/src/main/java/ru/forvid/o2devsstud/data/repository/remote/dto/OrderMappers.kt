package ru.forvid.o2devsstud.data.remote.dto

import android.util.Log
import ru.forvid.o2devsstud.domain.model.Order
import ru.forvid.o2devsstud.domain.model.OrderStatus

private const val TAG = "OrderMappers"

/** Надёжный маппер OrderDto -> domain.Order */
fun OrderDto.toDomain(): Order {
    val resolvedId = this.id ?: this.idAlt ?: System.currentTimeMillis()
    val fromResolved = this.fromAddress ?: this.from ?: ""
    val toResolved = this.toAddress ?: this.to ?: ""
    val reqNum = this.requestNumber ?: this.requestNumberAlt ?: ""
    val statusResolved = try {
        OrderStatus.valueOf(this.status ?: "PLACED")
    } catch (t: Throwable) {
        Log.w(TAG, "Unknown status='${this.status}', fallback to PLACED")
        OrderStatus.PLACED
    }
    val days = this.estimatedDays ?: this.estimatedDaysAlt ?: 1

    return Order(
        id = resolvedId,
        from = fromResolved,
        to = toResolved,
        requestNumber = reqNum,
        status = statusResolved,
        estimatedDays = days
    )
}

/** TrackDto -> список точек (lat, lon) для Polyline.
 *  Фильтруем точки с пустыми координатами. */
fun TrackDto.toPolylinePoints(): List<Pair<Double, Double>> {
    val pts = this.points ?: emptyList()
    return pts.mapNotNull { p ->
        val lat = p.lat
        val lon = p.lon ?: p.lng
        if (lat != null && lon != null) {
            lat to lon
        } else null
    }
}
