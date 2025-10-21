package ru.forvid.o2devsstud.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import ru.forvid.o2devsstud.ui.viewmodel.OrdersViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateOrderScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OrdersViewModel
) {
    var fromAddress by remember { mutableStateOf("") }
    var toAddress by remember { mutableStateOf("") }
    var contractor by remember { mutableStateOf("") }
    var cargoType by remember { mutableStateOf("") }
    var cargoWeight by remember { mutableStateOf("") }
    var comments by remember { mutableStateOf("") }


    val isFormValid by remember {
        derivedStateOf {
            fromAddress.isNotBlank() && toAddress.isNotBlank() && contractor.isNotBlank()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Создание заявки") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = fromAddress,
                onValueChange = { fromAddress = it },
                label = { Text("Откуда") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = toAddress,
                onValueChange = { toAddress = it },
                label = { Text("Куда") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = contractor,
                onValueChange = { contractor = it },
                label = { Text("Контрагент") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = cargoType,
                onValueChange = { cargoType = it },
                label = { Text("Тип груза") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = cargoWeight,
                onValueChange = { cargoWeight = it },
                label = { Text("Вес груза, т") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = comments,
                onValueChange = { comments = it },
                label = { Text("Комментарии") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            )

            Spacer(modifier = Modifier.height(16.dp))
            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    viewModel.createOrder(
                        from = fromAddress,
                        to = toAddress,
                        contractor = contractor,
                        cargoType = cargoType,
                        cargoWeight = cargoWeight,
                        comments = comments
                    )
                    onBack()
                },
                enabled = isFormValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE53935),
                    contentColor = Color.White,
                    disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                )
            ) {
                Text("ОТПРАВИТЬ ЗАЯВКУ")
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800, name = "CreateOrder Preview")
@Composable
private fun CreateOrderScreenPreview() {
    ru.forvid.o2devsstud.ui.theme.O2DevsStudTheme {
        // Копия формы для preview — использует локальное состояние, не зависит от ViewModel
        var fromAddress by remember { mutableStateOf("Москва") }
        var toAddress by remember { mutableStateOf("Казань") }
        var contractor by remember { mutableStateOf("ООО Рога") }
        var cargoType by remember { mutableStateOf("Песок") }
        var cargoWeight by remember { mutableStateOf("10") }
        var comments by remember { mutableStateOf("Комментарий") }

        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(value = fromAddress, onValueChange = { fromAddress = it }, label = { Text("Откуда") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = toAddress, onValueChange = { toAddress = it }, label = { Text("Куда") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = contractor, onValueChange = { contractor = it }, label = { Text("Контрагент") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = cargoType, onValueChange = { cargoType = it }, label = { Text("Тип груза") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = cargoWeight, onValueChange = { cargoWeight = it }, label = { Text("Вес груза, т") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = comments, onValueChange = { comments = it }, label = { Text("Комментарии") }, modifier = Modifier
                .fillMaxWidth()
                .height(120.dp))
            Spacer(Modifier.height(16.dp))
            Button(onClick = {}, modifier = Modifier.fillMaxWidth().height(50.dp)) { Text("ОТПРАВИТЬ ЗАЯВКУ") }
        }
    }
}
