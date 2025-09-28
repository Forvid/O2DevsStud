package ru.forvid.o2devsstud.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val fromAddress: String,
    val toAddress: String,
    val requestNumber: String,
    val status: String, // сохраняем как строку, используем TypeConverter при чтении/записи
    val estimatedDays: Int
)
