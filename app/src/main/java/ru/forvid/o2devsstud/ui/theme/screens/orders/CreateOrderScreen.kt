package ru.forvid.o2devsstud.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import ru.forvid.o2devsstud.ui.viewmodel.OrdersViewModel
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateOrderScreen(
    onBack: () -> Unit,
    viewModel: OrdersViewModel,
    modifier: Modifier = Modifier
) {
    var from by remember { mutableStateOf("") }
    var to by remember { mutableStateOf("") }
    var requestNumber by remember { mutableStateOf("") }
    var estimatedDaysText by remember { mutableStateOf("") }

    val isFormValid by remember {
        derivedStateOf {
            from.isNotBlank() && to.isNotBlank() && requestNumber.isNotBlank() &&
                    estimatedDaysText.toIntOrNull()?.let { it > 0 } == true
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Создать заявку") }, navigationIcon = {
            IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Назад") }
        })

        Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Top) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    OutlinedTextField(value = from, onValueChange = { from = it }, label = { Text("Откуда") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next))
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = to, onValueChange = { to = it }, label = { Text("Куда") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next))
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = requestNumber, onValueChange = { requestNumber = it }, label = { Text("Номер заявки") }, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = estimatedDaysText, onValueChange = { estimatedDaysText = it.filter { ch -> ch.isDigit() } }, label = { Text("Расчетное время (дни)") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done))
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    viewModel.createOrder(from.trim(), to.trim(), requestNumber.trim(), estimatedDaysText.toIntOrNull() ?: 1)
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
