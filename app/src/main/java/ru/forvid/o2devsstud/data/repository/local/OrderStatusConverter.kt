package ru.forvid.o2devsstud.data.repository.local

import androidx.room.TypeConverter
import ru.forvid.o2devsstud.domain.model.OrderStatus

class OrderStatusConverter {

    @TypeConverter
    fun fromStatus(status: OrderStatus?): String? = status?.name

    @TypeConverter
    fun toStatus(value: String?): OrderStatus? = value?.let { OrderStatus.valueOf(it) }
}
