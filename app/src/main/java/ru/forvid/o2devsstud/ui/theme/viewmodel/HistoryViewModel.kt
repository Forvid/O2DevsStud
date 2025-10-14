package ru.forvid.o2devsstud.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.forvid.o2devsstud.data.repository.repository.HistoryRepository // <-- ИЗМЕНЕНИЕ
import ru.forvid.o2devsstud.domain.model.HistoryItem
import javax.inject.Inject

private const val TAG = "HistoryViewModel"

data class HistoryUiState(
    val items: List<HistoryItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    // --- Внедряет HistoryRepository вместо ApiService ---
    private val repository: HistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        loadHistory()
    }

    fun loadHistory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                // --- Получает данные из репозитория ---
                val historyItems = repository.getHistoryItems()
                _uiState.update { it.copy(items = historyItems, isLoading = false) }
            } catch (e: Throwable) {
                Log.e(TAG, "loadHistory error", e)
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}