package ru.forvid.o2devsstud.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
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

    // --- НОВОЕ ПОЛЕ ---
    // ID трека для активного заказа, который нужно показать на главном экране (HomeScreen)
    private val _activeOrderTrackId = MutableStateFlow<Long?>(null)
    val activeOrderTrackId: StateFlow<Long?> = _activeOrderTrackId.asStateFlow()

    init {
        loadLocal()
        syncFromServer()
    }

    private fun loadLocal() {
        viewModelScope.launch {
            try {
                val local = repository.getAll()
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
                    repository.insert(dto.toDomain())
                }
                val updated = repository.getAll()
                _uiState.update { it.copy(orders = updated, isLoading = false, error = null) }
            } catch (e: Throwable) {
                Log.e(TAG, "Error fetching orders from server", e)
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Ошибка сети") }
            }
        }
    }

    fun changeStatus(orderId: Long, status: OrderStatus) {
        viewModelScope.launch {
            try {
                // Обновление UI
                val currentOrders = _uiState.value.orders
                val updatedOrders = currentOrders.map { if (it.id == orderId) it.copy(status = status) else it }
                _uiState.update { it.copy(orders = updatedOrders) }
                // Обновление в репозитории
                repository.updateStatus(orderId, status)
            } catch (e: Throwable) {
                Log.e(TAG, "Error updating status, rolling back", e)
                // В случае ошибки откатывается к данным из репозитория
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
                estimatedDays = estimatedDays
            )
            try {
                repository.insert(order)
                loadLocal() // Перезагрузжает все заказы из БД для консистентности
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
                _trackState.value = if (track != null) TrackState.Success(track) else TrackState.Error("Трек не найден")
            } catch (e: Throwable) {
                Log.e(TAG, "Error fetching track $trackId", e)
                _trackState.value = TrackState.Error(e.message)
            }
        }
    }

    fun clearTrackState() {
        _trackState.value = TrackState.Idle
    }

    // --- НОВЫЙ МЕТОД ---
    /**
     * Устанавливает ID трека, который должен быть показан на главном экране.
     * Если передать null, убирает трек с главного экрана.
     */
    fun setActiveTrack(trackId: Long?) {
        _activeOrderTrackId.value = trackId
    }
}
