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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.forvid.o2devsstud.domain.model.Order
import ru.forvid.o2devsstud.domain.model.OrderStatus
import ru.forvid.o2devsstud.ui.viewmodel.OrdersViewModel

// --- Вспомогательные функции для OrderStatus ---
private fun OrderStatus.next(): OrderStatus? = when (this) {
    OrderStatus.PLACED -> OrderStatus.TAKEN
    OrderStatus.TAKEN -> OrderStatus.IN_TRANSIT_TO_LOAD
    OrderStatus.IN_TRANSIT_TO_LOAD -> OrderStatus.LOADED
    OrderStatus.LOADED -> OrderStatus.IN_TRANSIT_TO_UNLOAD
    OrderStatus.IN_TRANSIT_TO_UNLOAD -> OrderStatus.ARRIVED_FOR_UNLOADING
    OrderStatus.PARKED -> OrderStatus.IN_TRANSIT_TO_UNLOAD
    OrderStatus.ARRIVED_FOR_UNLOADING -> OrderStatus.UNLOADED
    OrderStatus.UNLOADED -> OrderStatus.DOCUMENTS_TAKEN
    else -> null
}

// Это свойство определяет, является ли статус кнопкой, которую нажимает пользователь
private val OrderStatus.isUserAction: Boolean
    get() = this in listOf(
        OrderStatus.TAKEN,
        OrderStatus.IN_TRANSIT_TO_LOAD,
        OrderStatus.LOADED,
        OrderStatus.IN_TRANSIT_TO_UNLOAD,
        OrderStatus.ARRIVED_FOR_UNLOADING,
        OrderStatus.UNLOADED,
        OrderStatus.DOCUMENTS_TAKEN
    )

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
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
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

        if (order.status in listOf(OrderStatus.ARRIVED_FOR_UNLOADING, OrderStatus.UNLOADED, OrderStatus.DOCUMENTS_TAKEN)) {
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
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            InfoRow(label = "Откуда:", value = order.from)
            InfoRow(label = "Куда:", value = order.to)
            InfoRow(label = "Статус:", value = order.status.displayName)
            InfoRow(label = "Дата:", value = order.date)
            order.estimatedDays?.let { InfoRow(label = "Расчетное время:", value = "$it дн.") }
            order.codAmount?.let {
                if (it > 0) {
                    InfoRow(label = "Сумма нал. платежа:", value = "$it руб.", isImportant = true)
                }
            }
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
private fun InfoRow(label: String, value: String, isImportant: Boolean = false) {
    Row(verticalAlignment = Alignment.Top) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.width(150.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isImportant) FontWeight.Bold else FontWeight.Normal,
            color = if (isImportant) MaterialTheme.colorScheme.error else LocalContentColor.current
        )
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
                TextButton(onClick = { actionToConfirm?.invoke(); showConfirmDialog = false }) { Text("Да") }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) { Text("Отмена") }
            }
        )
    }

    val nextStatus = order.status.next()

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Проходиь по всем статусам, которые являются действиями пользователя
        OrderStatus.entries.filter { it.isUserAction }.forEach { status ->
            // Определяет, является ли эта кнопка следующей активной
            val isEnabled = status == nextStatus
            // Особое условие для кнопки "Завершить"
            val isFinishButton = status == OrderStatus.DOCUMENTS_TAKEN
            val isFinishButtonEnabled = isEnabled && documentsAttached

            ActionButton(
                text = status.displayName,
                isEnabled = if (isFinishButton) isFinishButtonEnabled else isEnabled,
                onClick = {
                    onActionClick(status.displayName) {
                        viewModel.changeStatus(order.id, status)
                        if ((status == OrderStatus.IN_TRANSIT_TO_LOAD || status == OrderStatus.IN_TRANSIT_TO_UNLOAD) && order.trackId != null) {
                            onShowTrackOnMap(order.trackId)
                        }
                        if (isFinishButton) {
                            onConfirm(order.id)
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun ActionButton(
    text: String,
    isEnabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = isEnabled,
        modifier = Modifier.fillMaxWidth().height(48.dp),
        colors = ButtonDefaults.buttonColors(
            // Если кнопка активна (enabled) - она красная. Если нет - серая.
            containerColor = Color(0xFFE53935),
            contentColor = Color.White,
            disabledContainerColor = Color.LightGray,
            disabledContentColor = Color.DarkGray
        )
    ) {
        Text(text)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "Order Details Preview")
@Composable
fun OrderDetailsScreenPreview() {
    val sampleOrder = Order(
        id = 1L,
        from = "Москва, ул. Тверская, д.1",
        to = "Санкт-Петербург, Невский проспект, д. 24, кв. 5",
        requestNumber = "A-123456",
        status = OrderStatus.LOADED,
        estimatedDays = 3,
        trackId = 100L,
        date = "20.10.2025",
        statusName = OrderStatus.LOADED.displayName,
        codAmount = 15000.0
    )

    MaterialTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Заявка №${sampleOrder.requestNumber}") },
                    navigationIcon = {
                        IconButton(onClick = {}) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                        }
                    }
                )
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding).padding(16.dp)) {
                InfoCard(order = sampleOrder)
                Spacer(modifier = Modifier.height(16.dp))
                ActionButton(text = "Взял в работу", isEnabled = false, onClick = {})
                ActionButton(text = "В дороге на место разгрузки", isEnabled = true, onClick = {})
                ActionButton(text = "Прибыл на место разгрузки", isEnabled = false, onClick = {})
            }
        }
    }
}