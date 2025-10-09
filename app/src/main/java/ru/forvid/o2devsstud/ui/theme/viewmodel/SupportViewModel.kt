package ru.forvid.o2devsstud.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.forvid.o2devsstud.data.remote.dto.ContactRequest
import ru.forvid.o2devsstud.data.repository.repository.ApiService
import javax.inject.Inject

private const val TAG = "SupportViewModel"

data class SupportUiState(
    val isSending: Boolean = false,
    val success: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class SupportViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(SupportUiState())
    val uiState: StateFlow<SupportUiState> = _uiState.asStateFlow()

    fun sendMessage(message: String, email: String? = null, phone: String? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSending = true, success = false, error = null)
            try {
                apiService.sendMessage(ContactRequest(message = message, email = email, phone = phone))
                _uiState.value = _uiState.value.copy(isSending = false, success = true)
            } catch (e: Throwable) {
                Log.e(TAG, "sendMessage error", e)
                _uiState.value = _uiState.value.copy(isSending = false, success = false, error = e.message)
            }
        }
    }
}
