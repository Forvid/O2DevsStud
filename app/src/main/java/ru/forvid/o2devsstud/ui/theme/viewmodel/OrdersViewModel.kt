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

    private fun load() {
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

    fun createOrder(from: String, to: String, requestNumber: String, estimatedDays: Int) {
        viewModelScope.launch {
            repository.create(
                Order(
                    id = 0L,
                    from = from,
                    to = to,
                    requestNumber = requestNumber,
                    status = OrderStatus.PLACED,
                    estimatedDays = estimatedDays
                )
            )
            _orders.value = repository.getAll()
        }
    }

    // Получить заказ прямо из текущего стейта UI (быстрая помощ. функция)
    fun getOrderFromState(orderId: Long): Order? {
        return _orders.value.find { it.id == orderId }
    }
}
