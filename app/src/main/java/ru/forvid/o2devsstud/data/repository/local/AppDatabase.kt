package ru.forvid.o2devsstud.data.repository.local

import androidx.room.Database
import androidx.room.RoomDatabase

// Указать здесь все Entity, которые будут в БД (здесь только OrderEntity)
@Database(entities = [OrderEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun orderDao(): OrderDao
}
