package ru.forvid.o2devsstud.data.remote

import retrofit2.http.GET
import retrofit2.http.Path

// --- DTOs (простые, чтобы убрать ошибки).
data class OrderDto(
    val id: Long,
    val from: String,
    val to: String,
    val requestNumber: String,
    val status: String,
    val estimatedDays: Int
)

data class LatLngDto(
    val lat: Double,
    val lng: Double
)

data class TrackDto(
    val id: Long,
    val points: List<LatLngDto>
)

// --- ApiService: примеры эндпоинтов
interface ApiService {

    // Получение списка заявок
    @GET("orders")
    suspend fun getOrders(): List<OrderDto>

    // Получение трека (полилиния) по id
    @GET("tracks/{id}")
    suspend fun getTrack(@Path("id") id: Long): TrackDto
}
