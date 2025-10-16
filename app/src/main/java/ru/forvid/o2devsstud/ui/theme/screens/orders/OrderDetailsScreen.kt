package ru.forvid.o2devsstud.ui.theme.screens.orders

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.forvid.o2devsstud.domain.model.Order
import ru.forvid.o2devsstud.domain.model.OrderStatus
import ru.forvid.o2devsstud.ui.viewmodel.OrdersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailsScreen(
    orderId: Long,
    onBack: () -> Unit,
    onConfirm: (Long) -> Unit,
    onShowTrackOnMap: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OrdersViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val order = remember(uiState.orders, orderId) {
        uiState.orders.find { it.id == orderId }
    }
    val attachedDocuments = uiState.attachedDocuments[orderId] ?: emptyList()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Заявка №${order?.requestNumber ?: "..."}") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        if (order != null) {
            OrderContentView(
                order = order,
                viewModel = viewModel,
                onConfirm = onConfirm,
                onShowTrackOnMap = onShowTrackOnMap,
                attachedDocuments = attachedDocuments,
                contentPadding = innerPadding
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator()
                } else {
                    Text("Заказ с ID $orderId не найден.")
                }
            }
        }
    }
}

@Composable
private fun OrderContentView(
    order: Order,
    viewModel: OrdersViewModel,
    onConfirm: (Long) -> Unit,
    onShowTrackOnMap: (Long) -> Unit,
    attachedDocuments: List<Uri>,
    contentPadding: PaddingValues
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        InfoCard(order = order)
        Spacer(modifier = Modifier.height(24.dp))

        // --- Показывает блок документов, когда водитель прибыл или разгрузился ---
        if (order.status in listOf(OrderStatus.ARRIVED_FOR_UNLOADING, OrderStatus.UNLOADED)) {
            DocumentsCard(
                attachedDocuments = attachedDocuments,
                onAddDocument = { viewModel.addDocument(order.id, it) },
                onRemoveDocument = { viewModel.removeDocument(order.id, it) }
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        StatusActionButtons(
            order = order,
            viewModel = viewModel,
            onConfirm = onConfirm,
            onShowTrackOnMap = onShowTrackOnMap,
            documentsAttached = attachedDocuments.isNotEmpty()
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun InfoCard(order: Order) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            InfoRow(label = "Откуда:", value = order.from)
            InfoRow(label = "Куда:", value = order.to)
            InfoRow(label = "Статус:", value = order.status.displayName)
            InfoRow(label = "Расчетное время:", value = "${order.estimatedDays} дн.")
        }
    }
}

@Composable
private fun DocumentsCard(
    attachedDocuments: List<Uri>,
    onAddDocument: (Uri) -> Unit,
    onRemoveDocument: (Uri) -> Unit
) {
    val pickDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? -> uri?.let(onAddDocument) }
    )

    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            Text("Документы", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            if (attachedDocuments.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    attachedDocuments.forEach { uri ->
                        val fileName = uri.pathSegments.lastOrNull() ?: "файл"
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(fileName, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                            IconButton(onClick = { onRemoveDocument(uri) }) {
                                Icon(Icons.Default.Close, contentDescription = "Удалить документ")
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            Button(
                onClick = { pickDocumentLauncher.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.AttachFile, contentDescription = null)
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                Text("Прикрепить фото документов")
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, modifier = Modifier.width(150.dp))
        Text(value, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun StatusActionButtons(
    order: Order,
    viewModel: OrdersViewModel,
    onConfirm: (Long) -> Unit,
    onShowTrackOnMap: (Long) -> Unit,
    documentsAttached: Boolean
) {
    var showConfirmDialog by remember { mutableStateOf(false) }
    var actionToConfirm by remember { mutableStateOf<(() -> Unit)?>(null) }
    var dialogText by remember { mutableStateOf("") }

    val onActionClick = { text: String, action: () -> Unit ->
        dialogText = "Вы уверены, что хотите изменить статус на \"$text\"?"
        actionToConfirm = action
        showConfirmDialog = true
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Подтверждение действия") },
            text = { Text(dialogText) },
            confirmButton = {
                Button(onClick = { actionToConfirm?.invoke(); showConfirmDialog = false }) { Text("Да") }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) { Text("Отмена") }
            }
        )
    }

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        when (order.status) {
            OrderStatus.PLACED -> ActionButton("Взял в работу") { onActionClick("Взял в работу") { viewModel.changeStatus(order.id, OrderStatus.TAKEN) } }
            OrderStatus.TAKEN -> ActionButton("В пути к месту погрузки") { onActionClick("В пути к месту погрузки") { viewModel.changeStatus(order.id, OrderStatus.IN_TRANSIT_TO_LOAD); order.trackId?.let { onShowTrackOnMap(it) } } }
            OrderStatus.IN_TRANSIT_TO_LOAD -> ActionButton("Машина загружена") { onActionClick("Машина загружена") { viewModel.changeStatus(order.id, OrderStatus.LOADED); viewModel.setActiveTrack(null) } }
            OrderStatus.LOADED -> ActionButton("В дороге на место разгрузки") { onActionClick("В дороге на место разгрузки") { viewModel.changeStatus(order.id, OrderStatus.IN_TRANSIT_TO_UNLOAD); order.trackId?.let { onShowTrackOnMap(it) } } }

            // Логика, как на скриншоте: несколько действий
            OrderStatus.IN_TRANSIT_TO_UNLOAD, OrderStatus.PARKED -> {
                if (order.status == OrderStatus.PARKED) {
                    ActionButton("Продолжить движение") { onActionClick("Продолжить движение") { viewModel.changeStatus(order.id, OrderStatus.IN_TRANSIT_TO_UNLOAD); order.trackId?.let { onShowTrackOnMap(it) } } }
                } else {
                    ActionButton("На стоянке", isOutlined = true) { onActionClick("На стоянке") { viewModel.changeStatus(order.id, OrderStatus.PARKED); viewModel.setActiveTrack(null) } }
                }
                ActionButton("Прибыл на место разгрузки") { onActionClick("Прибыл на место разгрузки") { viewModel.changeStatus(order.id, OrderStatus.ARRIVED_FOR_UNLOADING); viewModel.setActiveTrack(null) } }
            }

            // Логика, как на скриншоте: финальный этап
            OrderStatus.ARRIVED_FOR_UNLOADING, OrderStatus.UNLOADED -> {
                if (order.status == OrderStatus.ARRIVED_FOR_UNLOADING) {
                    ActionButton("Машина разгружена") { onActionClick("Машина разгружена") { viewModel.changeStatus(order.id, OrderStatus.UNLOADED) } }
                }
                // Кнопка "Завершить" (Забрал документы)
                ActionButton(
                    text = "Завершить",
                    enabled = documentsAttached,
                    onClick = {
                        onActionClick("Завершить") {
                            viewModel.changeStatus(order.id, OrderStatus.DOCUMENTS_TAKEN)
                            onConfirm(order.id)
                        }
                    }
                )
            }

            else -> {
                Text("Для статуса \"${order.status.displayName}\" нет доступных действий.", modifier = Modifier.padding(vertical = 16.dp), style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun ActionButton(
    text: String,
    enabled: Boolean = true,
    isOutlined: Boolean = false,
    onClick: () -> Unit
) {
    val modifier = Modifier.fillMaxWidth().height(48.dp)
    if (isOutlined) {
        OutlinedButton(onClick = onClick, enabled = enabled, modifier = modifier) {
            Text(text)
        }
    } else {
        Button(onClick = onClick, enabled = enabled, modifier = modifier) {
            Text(text)
        }
    }
}