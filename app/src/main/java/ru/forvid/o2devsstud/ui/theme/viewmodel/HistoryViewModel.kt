package ru.forvid.o2devsstud.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.forvid.o2devsstud.data.remote.dto.HistoryDto
import ru.forvid.o2devsstud.data.remote.dto.toUiItem
import ru.forvid.o2devsstud.ui.screens.HistoryItem
import ru.forvid.o2devsstud.data.repository.repository.ApiService
import javax.inject.Inject

private const val TAG = "HistoryViewModel"

data class HistoryUiState(
    val items: List<HistoryItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val apiService: ApiService
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
                val remote: List<HistoryDto> = apiService.getHistory()
                val mapped = remote.map { it.toUiItem() }
                _uiState.update { it.copy(items = mapped, isLoading = false) }
            } catch (e: Throwable) {
                Log.e(TAG, "loadHistory error", e)
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
