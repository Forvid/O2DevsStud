package ru.forvid.o2devsstud.domain.util

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class OrderCompletionNotifier {
    private val _completionFlow = MutableSharedFlow<Unit>()
    val completionFlow = _completionFlow.asSharedFlow()

    suspend fun notifyOrderCompleted() {
        _completionFlow.emit(Unit)
    }
}
