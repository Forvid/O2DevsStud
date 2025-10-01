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

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val repository: OrdersRepository
) : ViewModel() {

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _orders.value = repository.getAll()
        }
    }

    fun changeStatus(orderId: Long, status: OrderStatus) {
        viewModelScope.launch {
            repository.updateStatus(orderId, status)
            _orders.value = repository.getAll()
        }
    }

    fun getOrder(orderId: Long, onResult: (Order?) -> Unit) {
        viewModelScope.launch {
            onResult(repository.getById(orderId))
        }
    }

    fun createOrder(from: String, to: String, requestNumber: String, estimatedDays: Int) {
        viewModelScope.launch {
            // создаём объект (в тестовом варианте id генерируем временно)
            val id = System.currentTimeMillis() // простая генерация id для демо
            val order = Order(
                id = id,
                from = from,
                to = to,
                requestNumber = requestNumber,
                status = OrderStatus.PLACED,
                estimatedDays = estimatedDays
            )
            try {
                repository.insert(order)
            } catch (e: Throwable) {
                // логирование при необходимости
            }
            _orders.value = repository.getAll()
        }
    }
}
