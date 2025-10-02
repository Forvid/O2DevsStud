package ru.forvid.o2devsstud.data.remote.dto

import com.google.gson.annotations.SerializedName

data class OrderDto(
    val id: Long,
    @SerializedName("from")
    val fromAddress: String,
    @SerializedName("to")
    val toAddress: String,
    val requestNumber: String,
    val status: String,
    val estimatedDays: Int
)
