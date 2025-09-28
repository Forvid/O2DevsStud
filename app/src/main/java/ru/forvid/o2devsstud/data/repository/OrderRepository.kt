package ru.forvid.o2devsstud.domain.repository

import ru.forvid.o2devsstud.domain.model.Order
import ru.forvid.o2devsstud.domain.model.OrderStatus

interface OrdersRepository {
    suspend fun getAll(): List<Order>
    suspend fun updateStatus(orderId: Long, status: OrderStatus)
    suspend fun getById(orderId: Long): Order?
    suspend fun insert(order: Order)
}
