package ru.forvid.o2devsstud.data.remote.dto


data class TrackDto(
    val id: Long,
    val points: List<PointDto>
)

data class PointDto(
    val lat: Double,
    val lng: Double,
    // необязательно: timestamp, speed и т.п.
    val timestamp: Long? = null
)
