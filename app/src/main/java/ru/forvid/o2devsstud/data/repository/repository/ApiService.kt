package ru.forvid.o2devsstud.data.repository.repository

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import ru.forvid.o2devsstud.data.remote.dto.LoginRequestDto
import ru.forvid.o2devsstud.data.remote.dto.TokenResponseDto
import ru.forvid.o2devsstud.data.remote.dto.ProfileDto
import ru.forvid.o2devsstud.data.remote.dto.HistoryDto
import ru.forvid.o2devsstud.data.remote.dto.ContactRequest
import ru.forvid.o2devsstud.data.repository.remote.dto.dto.OrderDto
import ru.forvid.o2devsstud.data.remote.dto.TrackDto

interface ApiService {
    // AUTH
    @POST("login") // <-- путь подтверди с бекендом (может быть "auth/login" и т.п.)
    suspend fun login(@Body request: LoginRequestDto): TokenResponseDto

    // PROFILE
    @GET("profile")
    suspend fun getProfile(): ProfileDto

    @PUT("profile")
    suspend fun updateProfile(@Body body: ProfileDto): ProfileDto

    // HISTORY
    @GET("history")
    suspend fun getHistory(): List<HistoryDto>

    // CONTACT / SUPPORT
    @POST("contact")
    suspend fun sendMessage(@Body request: ContactRequest): Unit

    // ORDERS / TRACKS
    @GET("orders")
    suspend fun getOrders(): List<OrderDto>

    @GET("tracks/{id}")
    suspend fun getTrack(@Path("id") id: Long): TrackDto?
}
