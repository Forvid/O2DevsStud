package ru.forvid.o2devsstud.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [OrderEntity::class], version = 1, exportSchema = false)
@TypeConverters(OrderStatusConverter::class)
abstract class OrdersDatabase : RoomDatabase() {
    abstract fun orderDao(): OrderDao
}
