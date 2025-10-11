package ru.forvid.o2devsstud.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import ru.forvid.o2devsstud.ui.viewmodel.OrdersViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateOrderScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OrdersViewModel
) {
    var from by rememberSaveable { mutableStateOf("") }
    var to by rememberSaveable { mutableStateOf("") }
    var requestNumber by rememberSaveable { mutableStateOf("") }
    var estimatedDaysText by rememberSaveable { mutableStateOf("") }

    val isFormValid by remember {
        derivedStateOf {
            from.isNotBlank() && to.isNotBlank() && requestNumber.isNotBlank() &&
                    estimatedDaysText.toIntOrNull()?.let { it > 0 } == true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Создать заявку") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Назад") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.elevatedCardElevation(6.dp)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    OutlinedTextField(value = from, onValueChange = { from = it }, label = { Text("Откуда") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next))
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = to, onValueChange = { to = it }, label = { Text("Куда") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next))
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = requestNumber, onValueChange = { requestNumber = it }, label = { Text("Номер заявки") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next))
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = estimatedDaysText,
                        onValueChange = { estimatedDaysText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Расчетное время (дни)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Button(onClick = {
                    val days = estimatedDaysText.toIntOrNull() ?: 1
                    viewModel.createOrder(
                        from = from.trim(),
                        to = to.trim(),
                        requestNumber = requestNumber.trim(),
                        estimatedDays = days
                    )
                    onBack()
                }, enabled = isFormValid, modifier = Modifier.weight(1f)) {
                    Text("Сохранить")
                }
                OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f)) {
                    Text("Отмена")
                }
            }
        }
    }
}
