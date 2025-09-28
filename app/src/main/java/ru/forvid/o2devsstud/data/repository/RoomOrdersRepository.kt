package ru.forvid.o2devsstud.data.repository

import ru.forvid.o2devsstud.data.local.OrderDao
import ru.forvid.o2devsstud.data.local.OrderEntity
import ru.forvid.o2devsstud.domain.model.Order
import ru.forvid.o2devsstud.domain.model.OrderStatus
import ru.forvid.o2devsstud.domain.repository.OrdersRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomOrdersRepository @Inject constructor(
    private val dao: OrderDao
) : OrdersRepository {

    private fun toEntity(o: Order): OrderEntity =
        OrderEntity(
            id = if (o.id <= 0L) 0L else o.id,
            fromAddress = o.from,
            toAddress = o.to,
            requestNumber = o.requestNumber,
            status = o.status.name,
            estimatedDays = o.estimatedDays
        )

    private fun fromEntity(e: OrderEntity): Order =
        Order(
            id = e.id,
            from = e.fromAddress,
            to = e.toAddress,
            requestNumber = e.requestNumber,
            status = try {
                OrderStatus.valueOf(e.status)
            } catch (ex: Exception) {
                OrderStatus.PLACED
            },
            estimatedDays = e.estimatedDays
        )

    override suspend fun getAll(): List<Order> {
        return dao.getAll().map { fromEntity(it) }
    }

    override suspend fun getById(orderId: Long): Order? {
        return dao.getById(orderId)?.let { fromEntity(it) }
    }

    override suspend fun updateStatus(orderId: Long, status: OrderStatus) {
        dao.updateStatus(orderId, status.name)
    }

    override suspend fun create(order: Order): Long {
        val insertedId = dao.insert(toEntity(order))
        return insertedId
    }
}
