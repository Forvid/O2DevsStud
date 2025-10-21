package ru.forvid.o2devsstud.data.repository.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val id: Long,
    val fromAddress: String,
    val toAddress: String,
    val requestNumber: String,
    val status: String,
    val trackId: Long?,
    val date: String,
    val statusName: String,
    val estimatedDays: Int?,
    val codAmount: Double?
)
