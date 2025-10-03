package ru.forvid.o2devsstud.data.remote.dto

import com.google.gson.annotations.SerializedName

data class TrackPointDto(
    @SerializedName("lat") val lat: Double? = null,
    // сервер может назвать поле "lon" или "lng"
    @SerializedName("lon") val lon: Double? = null,
    @SerializedName("lng") val lng: Double? = null,
    @SerializedName("time") val time: String? = null
)

data class TrackDto(
    @SerializedName("track_id") val id: Long? = null,
    @SerializedName("id") val idAlt: Long? = null,
    @SerializedName("points") val points: List<TrackPointDto>? = null
)
