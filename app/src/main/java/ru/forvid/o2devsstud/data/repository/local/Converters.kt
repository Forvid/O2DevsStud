package ru.forvid.o2devsstud.data.repository.local

import androidx.room.TypeConverter
import ru.forvid.o2devsstud.domain.model.OrderStatus


/**
 * Конвертеры для Room, чтобы он мог сохранять в базу данных
 * нестандартные типы, такие как Enum.
 */
class Converters {
    /**
     * Конвертирует OrderStatus в строку для сохранения в БД.
     */
    @TypeConverter
    fun fromOrderStatus(value: OrderStatus): String {
        return value.name
    }

    /**
     * Конвертирует строку из БД обратно в OrderStatus.
     */
    @TypeConverter
    fun toOrderStatus(value: String): OrderStatus {
        return OrderStatus.valueOf(value)
    }
}