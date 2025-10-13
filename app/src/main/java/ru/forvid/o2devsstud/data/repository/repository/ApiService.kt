package ru.forvid.o2devsstud.data.repository.repository

import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Multipart
import retrofit2.http.Part
import ru.forvid.o2devsstud.data.remote.dto.LoginRequestDto
import ru.forvid.o2devsstud.data.remote.dto.TokenResponseDto
import ru.forvid.o2devsstud.data.remote.dto.ProfileDto
import ru.forvid.o2devsstud.data.remote.dto.HistoryDto
import ru.forvid.o2devsstud.data.remote.dto.ContactRequest
import ru.forvid.o2devsstud.data.repository.remote.dto.dto.OrderDto
import ru.forvid.o2devsstud.data.remote.dto.TrackDto
import ru.forvid.o2devsstud.data.remote.dto.AvatarUploadResponseDto

interface ApiService {

    // AUTH
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequestDto): TokenResponseDto

    // PROFILE
    @GET("api/profile")
    suspend fun getProfile(): ProfileDto

    @PUT("api/profile")
    suspend fun updateProfile(@Body body: ProfileDto): ProfileDto

    // HISTORY
    @GET("api/history")
    suspend fun getHistory(): List<HistoryDto>

    // CONTACT / SUPPORT
    @POST("api/contact")
    suspend fun sendMessage(@Body request: ContactRequest): Unit

    // ORDERS / TRACKS
    @GET("api/orders")
    suspend fun getOrders(): List<OrderDto>

    @GET("api/tracks/{id}")
    suspend fun getTrack(@Path("id") id: Long): TrackDto?

    // AVATAR UPLOAD (multipart)
    @Multipart
    @POST("api/profile/avatar")
    suspend fun uploadAvatar(@Part file: MultipartBody.Part): AvatarUploadResponseDto
}