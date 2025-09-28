package ru.forvid.o2devsstud.data.repository.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [OrderEntity::class], version = 1, exportSchema = false)
@TypeConverters(OrderStatusConverter::class)
abstract class OrdersDatabase : RoomDatabase() {
    abstract fun orderDao(): OrderDao

    companion object {
        private const val DB_NAME = "orders.db"

        fun create(context: Context): OrdersDatabase {
            return Room.databaseBuilder(context.applicationContext, OrdersDatabase::class.java, DB_NAME)
                .build()
        }
    }
}
