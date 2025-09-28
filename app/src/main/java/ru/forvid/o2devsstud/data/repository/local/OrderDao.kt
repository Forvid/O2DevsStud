package ru.forvid.o2devsstud.data.repository.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface OrderDao {

    @Query("SELECT * FROM orders")
    suspend fun getAll(): List<OrderEntity>

    @Query("SELECT * FROM orders WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): OrderEntity?

    @Query("UPDATE orders SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: OrderEntity)

    @Query("DELETE FROM orders")
    suspend fun clearAll()
}
