package ru.forvid.o2devsstud.data.repository

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.forvid.o2devsstud.domain.model.Order
import ru.forvid.o2devsstud.domain.model.OrderStatus
import ru.forvid.o2devsstud.domain.repository.OrdersRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeOrdersRepository @Inject constructor() : OrdersRepository {

    private val mutex = Mutex()
    private val storage = mutableListOf(
        Order(
            id = 1L,
            from = "ул. Раскольникова, 18",
            to = "Набережные Челны",
            requestNumber = "AP-001",
            status = OrderStatus.PLACED,
            estimatedDays = 1,
            date = "24.07.2024",
            statusName = "Заказ размещен",
            // --- ИСПРАВЛЕНИЕ: Передаем число Double, а не строку ---
            codAmount = 15000.0
        ),
        Order(
            id = 2L,
            from = "ул. Ленина, 5",
            to = "Казань",
            requestNumber = "AP-002",
            status = OrderStatus.TAKEN,
            estimatedDays = 2,
            date = "23.07.2024",
            statusName = "Взят в работу",
            codAmount = null
        ),
        Order(
            id = 3L,
            from = "ул. Пушкина, 10",
            to = "Москва",
            requestNumber = "AP-003",
            status = OrderStatus.PLACED,
            estimatedDays = 3,
            date = "22.07.2024",
            statusName = "Заказ размещен",
            codAmount = null
        )
    )

    override suspend fun getAll(): List<Order> = mutex.withLock { storage.toList() }

    override suspend fun updateStatus(orderId: Long, status: OrderStatus) {
        mutex.withLock {
            val idx = storage.indexOfFirst { it.id == orderId }
            if (idx >= 0) {
                val o = storage[idx]
                storage[idx] = o.copy(status = status)
            }
        }
    }

    override suspend fun getById(orderId: Long): Order? = mutex.withLock {
        storage.find { it.id == orderId }
    }

    override suspend fun insert(order: Order) {
        mutex.withLock {
            storage.removeAll { it.id == order.id }
            storage.add(order)
        }
    }
}