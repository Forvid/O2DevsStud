package ru.forvid.o2devsstud.data.repository.remote.dto.dto

import com.google.gson.annotations.SerializedName

data class OrderDto(
    // id в mock может быть числом (order_id) или строкой (id)
    @SerializedName("order_id") val orderId: Long? = null,
    @SerializedName("id") val idStr: String? = null,

    @SerializedName("from_address") val fromAddress: String? = null,
    @SerializedName("from") val from: String? = null,

    @SerializedName("to_address") val toAddress: String? = null,
    @SerializedName("to") val to: String? = null,

    @SerializedName("request_number") val requestNumber: String? = null,
    @SerializedName("requestNumber") val requestNumberAlt: String? = null,

    val status: String? = null,

    @SerializedName("estimated_days") val estimatedDays: Int? = null,
    @SerializedName("estimatedDays") val estimatedDaysAlt: Int? = null,

    @SerializedName("track_id")
    val trackId: Long? = null,

    @SerializedName("date")
    val date: String?,

    @SerializedName("status_name")
    val statusName: String?,

    @SerializedName("cod_amount")
    val codAmount: String?
)
