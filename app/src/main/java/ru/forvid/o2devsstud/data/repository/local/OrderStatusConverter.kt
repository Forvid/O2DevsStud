package ru.forvid.o2devsstud.data.local

import androidx.room.TypeConverter
import ru.forvid.o2devsstud.domain.model.OrderStatus

object OrderStatusConverter {
    @TypeConverter
    @JvmStatic
    fun fromStatus(status: OrderStatus): String = status.name

    @TypeConverter
    @JvmStatic
    fun toStatus(value: String): OrderStatus {
        return try {
            OrderStatus.valueOf(value)
        } catch (e: Exception) {
            // на случай несоответствия — возвращаем дефолт
            OrderStatus.PLACED
        }
    }
}
