package ru.forvid.o2devsstud.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.forvid.o2devsstud.data.repository.repository.HistoryRepository
import ru.forvid.o2devsstud.domain.model.HistoryItem
import ru.forvid.o2devsstud.domain.util.OrderCompletionNotifier
import javax.inject.Inject

private const val TAG = "HistoryViewModel"

data class HistoryUiState(
    val items: List<HistoryItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: HistoryRepository,
    private val orderCompletionNotifier: OrderCompletionNotifier
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        loadHistory()

        // --- Подписывается на события завершения заказа ---
        viewModelScope.launch {
            orderCompletionNotifier.completionFlow.collect {
                Log.d(TAG, "Received order completion event. Reloading history...")
                loadHistory()
            }
        }
    }

    fun loadHistory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val historyItems = repository.getHistoryItems()
                _uiState.update { it.copy(items = historyItems, isLoading = false) }
            } catch (e: Throwable) {
                Log.e(TAG, "loadHistory error", e)
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}