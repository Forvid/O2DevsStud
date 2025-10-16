package ru.forvid.o2devsstud.domain.model

enum class OrderStatus(val displayName: String) {
    // Основная цепочка статусов из ТЗ
    PLACED("Размещена"),
    TAKEN("Взята в работу"),
    LOADED("Загружена"),
    UNLOADED("Разгружена"),

    // Статусы, связанные с движением
    IN_TRANSIT_TO_LOAD("В пути к месту погрузки"),
    IN_TRANSIT_TO_UNLOAD("В пути к месту разгрузки"),
    PARKED("На стоянке"),
    ARRIVED_FOR_UNLOADING("Прибыла на место разгрузки"),

    // Финальный статус
    DOCUMENTS_TAKEN("Документы забраны"),

    // Статус для обработки неизвестных значений
    UNKNOWN("Неизвестный статус");

    companion object {
        // Метод для безопасного преобразования строки в статус
        fun fromString(value: String?): OrderStatus {
            // Используем entries вместо values() для лучшей производительности
            return entries.find { it.name.equals(value, ignoreCase = true) }
                ?: UNKNOWN // Возвращаем UNKNOWN, если ничего не найдено
        }
    }
}