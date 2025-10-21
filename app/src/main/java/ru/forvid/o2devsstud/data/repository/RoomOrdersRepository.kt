package ru.forvid.o2devsstud.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.forvid.o2devsstud.data.repository.local.OrderDao
import ru.forvid.o2devsstud.data.repository.local.OrderEntity
import ru.forvid.o2devsstud.domain.model.Order
import ru.forvid.o2devsstud.domain.model.OrderStatus
import ru.forvid.o2devsstud.domain.repository.OrdersRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomOrdersRepository @Inject constructor(
    private val dao: OrderDao
) : OrdersRepository {

    // Преобразует сущность БД (Entity) в доменную модель (Order)
    private fun entityToDomain(e: OrderEntity): Order = Order(
        id = e.id,
        from = e.fromAddress,
        to = e.toAddress,
        requestNumber = e.requestNumber,
        status = OrderStatus.fromString(e.status),
        estimatedDays = e.estimatedDays,
        trackId = e.trackId,
        date = e.date,
        statusName = e.statusName,
        codAmount = e.codAmount
    )

    private fun domainToEntity(o: Order): OrderEntity = OrderEntity(
        id = o.id,
        fromAddress = o.from,
        toAddress = o.to,
        requestNumber = o.requestNumber,
        status = o.status.name,
        estimatedDays = o.estimatedDays,
        trackId = o.trackId,
        date = o.date,
        statusName = o.statusName,
        codAmount = o.codAmount
    )

    override suspend fun getAll(): List<Order> = withContext(Dispatchers.IO) {
        dao.getAll().map { entityToDomain(it) }
    }

    override suspend fun updateStatus(orderId: Long, status: OrderStatus) = withContext(Dispatchers.IO) {
        dao.updateStatus(orderId, status.name)
    }

    override suspend fun getById(orderId: Long): Order? = withContext(Dispatchers.IO) {
        dao.getById(orderId)?.let { entityToDomain(it) }
    }

    override suspend fun insert(order: Order) = withContext(Dispatchers.IO) {
        dao.insert(domainToEntity(order))
    }
}
