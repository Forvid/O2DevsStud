package ru.forvid.o2devsstud.domain.model

enum class OrderStatus {
    PLACED,
    TAKEN,
    IN_TRANSIT_TO_PICKUP,
    LOADED,
    IN_TRANSIT_TO_DROPOFF,
    PARKED,
    ARRIVED_DROPOFF,
    UNLOADED,
    IN_WORK,
    ON_WAY_TO_LOAD,
    ON_WAY_TO_UNLOAD,
    ARRIVED_UNLOAD,
    DOCUMENTS_TAKEN
}
