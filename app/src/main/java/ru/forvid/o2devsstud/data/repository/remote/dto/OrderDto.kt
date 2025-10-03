package ru.forvid.o2devsstud.data.remote.dto

import com.google.gson.annotations.SerializedName

data class OrderDto(
    @SerializedName("order_id") val id: Long? = null,
    @SerializedName("id") val idAlt: Long? = null,

    @SerializedName("from_address") val fromAddress: String? = null,
    @SerializedName("from") val from: String? = null,

    @SerializedName("to_address") val toAddress: String? = null,
    @SerializedName("to") val to: String? = null,

    @SerializedName("request_number") val requestNumber: String? = null,
    @SerializedName("requestNumber") val requestNumberAlt: String? = null,

    @SerializedName("status") val status: String? = null,

    @SerializedName("estimated_days") val estimatedDays: Int? = null,
    @SerializedName("estimatedDays") val estimatedDaysAlt: Int? = null
)
