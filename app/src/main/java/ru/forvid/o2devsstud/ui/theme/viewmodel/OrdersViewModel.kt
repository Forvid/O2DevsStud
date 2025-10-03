package ru.forvid.o2devsstud.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.forvid.o2devsstud.data.remote.ApiService
import ru.forvid.o2devsstud.data.remote.dto.OrderDto
import ru.forvid.o2devsstud.data.remote.dto.TrackDto
import ru.forvid.o2devsstud.data.remote.dto.toDomain // <- важно: импорт extension-функции
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

    // Поток со списком заказов — совместимость со старыми экранами (viewModel.orders...).
    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    // Общий UI state — можно использовать для отображения ошибок/крутилки
    private val _uiState = MutableStateFlow(OrdersUiState())
    val uiState: StateFlow<OrdersUiState> = _uiState.asStateFlow()

    init {
        // Сначала загрузим локальные данные, затем попробуем синхронизацию с сервером
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

    /**
     * Синхронизация с сервером: получаю список заказов, логируем, мапим и сохраняем локально.
     * После — обновляем flows.
     */
    fun syncFromServer() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val remote: List<OrderDto> = apiService.getOrders()
                Log.d(TAG, "Fetched ${remote.size} orders from server: $remote")

                // Мапим и сохраняем в локальный репозиторий
                for (dto in remote) {
                    val domain = dto.toDomain()
                    try {
                        repository.insert(domain)
                    } catch (e: Throwable) {
                        Log.w(TAG, "insert error for ${domain.id}: ${e.message}")
                    }
                }

                // Обновляем локальные данные в flow
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

    /** Просто перезагрузить локальные данные (если требуется) */
    fun load() {
        loadLocal()
    }

    /** Меняем статус в репозитории и обновляем flows */
    fun changeStatus(orderId: Long, status: OrderStatus) {
        viewModelScope.launch {
            try {
                repository.updateStatus(orderId, status)
                val updated = repository.getAll()
                _orders.value = updated
                _uiState.update { it.copy(orders = updated, error = null) }
            } catch (e: Throwable) {
                Log.e(TAG, "Error updating status", e)
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    /** Асинхронно вернуть заказ по id */
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

    /**
     * Создать заказ (имя параметра "to" — чтобы вызовы из экрана совпадали)
     */
    fun createOrder(from: String, to: String, requestNumber: String, estimatedDays: Int) {
        viewModelScope.launch {
            val id = System.currentTimeMillis()
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
                val updated = repository.getAll()
                _orders.value = updated
                _uiState.update { it.copy(orders = updated, error = null) }
            } catch (e: Throwable) {
                Log.e(TAG, "Error creating order", e)
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    /**
     * Запрос трека по id и отдать результат через onResult (для отрисовки polyline).
     * Логируем ответ.
     */
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
