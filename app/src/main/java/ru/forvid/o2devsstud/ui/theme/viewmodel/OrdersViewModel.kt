package ru.forvid.o2devsstud.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.forvid.o2devsstud.domain.model.Order
import ru.forvid.o2devsstud.domain.model.OrderStatus
import ru.forvid.o2devsstud.domain.repository.OrdersRepository
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val repository: OrdersRepository
) : ViewModel() {

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    init {
        load()
    }

    // Загружает список асинхронно
    fun load() {
        viewModelScope.launch {
            val list = repository.getAll()
            _orders.value = list
        }
    }

    fun changeStatus(orderId: Long, status: OrderStatus) {
        viewModelScope.launch {
            repository.updateStatus(orderId, status)
            _orders.value = repository.getAll()
        }
    }

    fun getOrder(orderId: Long, callback: (Order?) -> Unit) {
        viewModelScope.launch {
            val o = repository.getById(orderId)
            callback(o)
        }
    }

    fun createOrder(from: String, to: String, requestNumber: String, estimatedDays: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            // генерируем id (простой метод — максимум +1)
            val newId = (repository.getAll().maxOfOrNull { it.id } ?: 0L) + 1
            val newOrder = Order(
                id = newId,
                from = from,
                to = to,
                requestNumber = requestNumber,
                status = OrderStatus.PLACED,
                estimatedDays = estimatedDays
            )
            repository.insert(newOrder)
            // обновляем UI на основном потоке
            load()
        }
    }
}
