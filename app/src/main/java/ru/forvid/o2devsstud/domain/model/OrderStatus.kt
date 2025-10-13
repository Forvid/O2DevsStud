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
    PARKED("На стоянке"), // Переименовано из ON_HOLD для ясности
    ARRIVED_FOR_UNLOADING("Прибыла на место разгрузки"),

    // Финальный статус
    DOCUMENTS_TAKEN("Документы забраны");


    companion object {
        fun fromString(value: String?): OrderStatus {
            return entries.find { it.name.equals(value, ignoreCase = true) }
                ?: PLACED
        }
    }
}
