package ru.forvid.o2devsstud.data.repository.repository

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import ru.forvid.o2devsstud.data.remote.dto.LoginRequestDto
import ru.forvid.o2devsstud.data.remote.dto.TokenResponseDto
import ru.forvid.o2devsstud.data.repository.remote.dto.dto.OrderDto
import ru.forvid.o2devsstud.data.remote.dto.TrackDto

interface ApiService {

    // Авторизация
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequestDto): TokenResponseDto

    // Список заказов
    @GET("orders")
    suspend fun getOrders(): List<OrderDto>

    // Получение трека по ID
    @GET("tracks/{id}")
    suspend fun getTrack(@Path("id") id: Long): TrackDto
}
