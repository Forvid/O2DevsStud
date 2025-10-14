package ru.forvid.o2devsstud.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.forvid.o2devsstud.data.remote.dto.TrackDto
import ru.forvid.o2devsstud.data.repository.remote.dto.dto.OrderDto
import ru.forvid.o2devsstud.data.remote.dto.toDomain
import ru.forvid.o2devsstud.domain.model.Order
import ru.forvid.o2devsstud.domain.model.OrderStatus
import ru.forvid.o2devsstud.domain.repository.OrdersRepository
import ru.forvid.o2devsstud.data.repository.repository.ApiService
import javax.inject.Inject

private const val TAG = "OrdersViewModel"

data class OrdersUiState(
    val orders: List<Order> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class TrackState {
    object Idle : TrackState()
    object Loading : TrackState()
    data class Success(val track: TrackDto) : TrackState()
    data class Error(val message: String?) : TrackState()
}

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val repository: OrdersRepository,
    private val apiService: ApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrdersUiState())
    val uiState: StateFlow<OrdersUiState> = _uiState.asStateFlow()

    private val _trackState = MutableStateFlow<TrackState>(TrackState.Idle)
    val trackState: StateFlow<TrackState> = _trackState.asStateFlow()

    private val _activeOrderTrackId = MutableStateFlow<Long?>(null)
    val activeOrderTrackId: StateFlow<Long?> = _activeOrderTrackId.asStateFlow()

    init {
        viewModelScope.launch {
            loadLocal()
            syncFromServer().join() // Дожидаемся завершения синхронизации
            findAndSetInitialActiveTrack()
        }
    }

    private suspend fun loadLocal() {
        try {
            val local = repository.getAll()
            _uiState.update { it.copy(orders = local, error = null) }
        } catch (e: Throwable) {
            Log.e(TAG, "loadLocal error", e)
            _uiState.update { it.copy(error = e.message ?: "Ошибка при чтении локальной БД") }
        }
    }

    fun syncFromServer(): Job = viewModelScope.launch { // Возвращаем Job для ожидания
        _uiState.update { it.copy(isLoading = true, error = null) }
        try {
            val remote: List<OrderDto> = apiService.getOrders()
            Log.d(TAG, "Fetched ${remote.size} orders from server")
            for (dto in remote) {
                repository.insert(dto.toDomain())
            }
            val updated = repository.getAll()
            _uiState.update { it.copy(orders = updated, isLoading = false, error = null) }
        } catch (e: Throwable) {
            Log.e(TAG, "Error fetching orders from server", e)
            _uiState.update { it.copy(isLoading = false, error = e.message ?: "Ошибка сети") }
        }
    }

    private fun findAndSetInitialActiveTrack() {
        val activeOrder = _uiState.value.orders.firstOrNull {
            it.status == OrderStatus.IN_TRANSIT_TO_LOAD || it.status == OrderStatus.IN_TRANSIT_TO_UNLOAD
        }

        if (activeOrder != null && activeOrder.trackId != null) {
            _activeOrderTrackId.value = activeOrder.trackId
            Log.d(TAG, "Active track found and set: ${activeOrder.trackId}")
        } else {
            Log.d(TAG, "No active orders with trackId found.")
        }
    }

    fun changeStatus(orderId: Long, status: OrderStatus) {
        viewModelScope.launch {
            try {
                val currentOrders = _uiState.value.orders
                val updatedOrders = currentOrders.map { if (it.id == orderId) it.copy(status = status) else it }
                _uiState.update { it.copy(orders = updatedOrders) }
                repository.updateStatus(orderId, status)
            } catch (e: Throwable) {
                Log.e(TAG, "Error updating status, rolling back", e)
                loadLocal()
            }
        }
    }

    fun createOrder(from: String, to: String, requestNumber: String, estimatedDays: Int) {
        viewModelScope.launch {
            val order = Order(
                id = System.currentTimeMillis(),
                from = from,
                to = to,
                requestNumber = requestNumber,
                status = OrderStatus.PLACED,
                estimatedDays = estimatedDays,
                trackId = null // Явно указываем, что trackId пока нет
            )
            try {
                repository.insert(order)
                loadLocal()
            } catch (e: Throwable) {
                Log.e(TAG, "Error creating order", e)
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun fetchTrack(trackId: Long) {
        viewModelScope.launch {
            _trackState.value = TrackState.Loading
            try {
                val track = apiService.getTrack(trackId)
                if (track != null) {
                    _trackState.value = TrackState.Success(track)
                } else {
                    _trackState.value = TrackState.Error("Трек с ID $trackId не найден")
                }
            } catch (e: Throwable) {
                Log.e(TAG, "Error fetching track $trackId", e)
                _trackState.value = TrackState.Error(e.message)
            }
        }
    }

    fun clearTrackState() {
        _trackState.value = TrackState.Idle
    }

    fun setActiveTrack(trackId: Long?) {
        _activeOrderTrackId.value = trackId
    }
}