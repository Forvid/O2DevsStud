package ru.forvid.o2devsstud.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ru.forvid.o2devsstud.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit = {}
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Используем authState, как в MainActivity
    val authState by viewModel.authState.collectAsState()

    val loading = authState.isLoading
    val error = authState.error
    val isAuthorized = authState.isAuthorized

    // Если уже авторизовались — перенаправляем
    LaunchedEffect(isAuthorized) {
        if (isAuthorized) onLoginSuccess()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Логин") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = { viewModel.login(username, password) },
            enabled = !loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (loading) "Вход..." else "Войти")
        }

        Spacer(Modifier.height(16.dp))

        if (!error.isNullOrBlank()) {
            Text("Ошибка: $error", color = MaterialTheme.colorScheme.error)
        }
    }
}
