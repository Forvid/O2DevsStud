package ru.forvid.o2devsstud.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.forvid.o2devsstud.data.repository.repository.ApiService
import ru.forvid.o2devsstud.data.repository.remote.dto.dto.OrderDto
import ru.forvid.o2devsstud.data.remote.dto.TrackDto
import ru.forvid.o2devsstud.data.remote.dto.toDomain
import ru.forvid.o2devsstud.domain.model.Order
import ru.forvid.o2devsstud.domain.model.OrderStatus
import ru.forvid.o2devsstud.domain.repository.OrdersRepository
import javax.inject.Inject

private const val TAG = "OrdersViewModel"

data class OrdersUiState(
    val orders: List<Order> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val repository: OrdersRepository,
    private val apiService: ApiService
) : ViewModel() {

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private val _uiState = MutableStateFlow(OrdersUiState())
    val uiState: StateFlow<OrdersUiState> = _uiState.asStateFlow()

    init {
        loadLocal()
        syncFromServer()
    }

    private fun loadLocal() {
        viewModelScope.launch {
            try {
                val local = repository.getAll()
                _orders.value = local
                _uiState.update { it.copy(orders = local, error = null) }
            } catch (e: Throwable) {
                Log.e(TAG, "loadLocal error", e)
                _uiState.update { it.copy(error = e.message ?: "Ошибка при чтении локальной БД") }
            }
        }
    }

    fun syncFromServer() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val remote: List<OrderDto> = apiService.getOrders()
                Log.d(TAG, "Fetched ${remote.size} orders from server")

                for (dto in remote) {
                    val domain = dto.toDomain()
                    try {
                        repository.insert(domain)
                    } catch (e: Throwable) {
                        Log.w(TAG, "insert error for ${domain.id}: ${e.message}")
                    }
                }

                val updated = repository.getAll()
                _orders.value = updated
                _uiState.update { it.copy(orders = updated, error = null) }
            } catch (e: Throwable) {
                Log.e(TAG, "Error fetching orders from server", e)
                _uiState.update { it.copy(error = e.message ?: "Ошибка сети") }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun load() {
        loadLocal()
    }

    /**
     * Безопасно меняем статус: сначала обновляем локально (оптимистично),
     * затем пытаемся синхронизировать с репозиторием. При ошибке откатываем.
     */
    fun changeStatus(orderId: Long, status: OrderStatus) {
        viewModelScope.launch {
            try {
                val current = _orders.value
                val idx = current.indexOfFirst { it.id == orderId }
                if (idx == -1) {
                    Log.w(TAG, "changeStatus: order $orderId not found locally")
                    return@launch
                }

                // Optimistic update: заменим локальную запись
                val mutable = current.toMutableList()
                val old = mutable[idx]
                mutable[idx] = old.copy(status = status)
                _orders.value = mutable
                _uiState.update { it.copy(orders = mutable, error = null) }

                // Попытка отправить на репозиторий
                repository.updateStatus(orderId, status)
                // после успешного обновления на бэке можно при необходимости подтянуть заново:
                // val refreshed = repository.getAll(); _orders.value = refreshed
            } catch (e: Throwable) {
                Log.e(TAG, "Error updating status, rolling back", e)
                // Попробуем восстановить из репозитория
                try {
                    val refreshed = repository.getAll()
                    _orders.value = refreshed
                    _uiState.update { it.copy(orders = refreshed, error = e.message) }
                } catch (re: Throwable) {
                    Log.e(TAG, "Failed to refresh after status update error", re)
                    _uiState.update { it.copy(error = e.message ?: "Ошибка при смене статуса") }
                }
            }
        }
    }

    /**
     * createOrder — optimistic update: сначала показываем в UI, затем сохраняем в репозиторий.
     * Если репозиторий выбросит ошибку — откатываем.
     */
    fun createOrder(from: String, to: String, requestNumber: String, estimatedDays: Int) {
        viewModelScope.launch {
            // создание временного объекта с уникальным id (можно заменить на возвращаемый с сервера)
            val id = System.currentTimeMillis()
            val order = Order(
                id = id,
                from = from,
                to = to,
                requestNumber = requestNumber,
                status = OrderStatus.PLACED,
                estimatedDays = estimatedDays
            )

            val before = _orders.value
            // optimistic add
            _orders.value = before + order
            _uiState.update { it.copy(orders = _orders.value, error = null) }

            try {
                repository.insert(order)
                // после успешной вставки — можно получить актуальный список из репозитория
                val updated = repository.getAll()
                _orders.value = updated
                _uiState.update { it.copy(orders = updated, error = null) }
            } catch (e: Throwable) {
                Log.e(TAG, "Error creating order, rollback", e)
                // откат
                _orders.value = before
                _uiState.update { it.copy(orders = before, error = e.message) }
            }
        }
    }

    fun getOrder(orderId: Long, onResult: (Order?) -> Unit) {
        viewModelScope.launch {
            try {
                onResult(repository.getById(orderId))
            } catch (e: Throwable) {
                Log.e(TAG, "getOrder error", e)
                onResult(null)
            }
        }
    }

    fun fetchTrackAndLog(trackId: Long, onResult: (TrackDto?) -> Unit) {
        viewModelScope.launch {
            try {
                val track = apiService.getTrack(trackId)
                Log.d(TAG, "Fetched track $trackId: $track")
                onResult(track)
            } catch (e: Throwable) {
                Log.e(TAG, "Error fetching track $trackId", e)
                onResult(null)
            }
        }
    }
}
