package ru.forvid.o2devsstud.data.repository.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey
    val id: Long,
    @ColumnInfo(name = "from_address")
    val fromAddress: String,
    @ColumnInfo(name = "to_address")
    val toAddress: String,
    @ColumnInfo(name = "request_number")
    val requestNumber: String,
    @ColumnInfo(name = "status")
    val status: String,
    @ColumnInfo(name = "estimated_days")
    val estimatedDays: Int
)
