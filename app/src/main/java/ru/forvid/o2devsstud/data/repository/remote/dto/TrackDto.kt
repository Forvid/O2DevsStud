package ru.forvid.o2devsstud.data.remote.dto

import com.google.gson.annotations.SerializedName

data class TrackPointDto(
    @SerializedName("lat") val lat: Double?,
    @SerializedName("lon") val lon: Double?,
    @SerializedName("lng") val lng: Double? = null,
    @SerializedName("time") val time: String? = null
)

data class TrackDto(
    // ИЗМЕНЕНИЕ: Тип изменен с Long? на String?
    // Это позволит Gson успешно парсить и "1", и "bb3b" без падения приложения.
    @SerializedName("track_id") val id: String? = null,
    @SerializedName("id") val idAlt: String? = null,

    @SerializedName("points") val points: List<TrackPointDto> = emptyList()
) {
    /**
     * ДОБАВЛЕНО: Безопасное вычисляемое свойство для получения ID в виде числа.
     * Оно пытается преобразовать строковый id (или idAlt) в Long.
     * Если это не удается (например, для строки "bb3b"), оно вернет null.
     */
    val numericId: Long?
        get() = (id ?: idAlt)?.toLongOrNull()
}

/**
 * Преобразование в список пар (lat, lon) — проверяем nullable и отбрасываем неполные точки.
 * Этот код корректен и не требует изменений.
 */
fun TrackDto.toPolylinePoints(): List<Pair<Double, Double>> {
    val pts = mutableListOf<Pair<Double, Double>>()
    for (p in points) {
        val lat = p.lat
        val lon = p.lon ?: p.lng
        if (lat != null && lon != null) {
            pts.add(lat to lon)
        }
    }
    return pts
}