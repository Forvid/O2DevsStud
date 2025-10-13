package ru.forvid.o2devsstud

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.forvid.o2devsstud.ui.theme.navigation.RootNavigation
import ru.forvid.o2devsstud.ui.theme.navigation.Screen
import ru.forvid.o2devsstud.ui.theme.O2DevsStudTheme
import ru.forvid.o2devsstud.ui.theme.viewmodel.AuthViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            O2DevsStudTheme {
                val authState by authViewModel.authState.collectAsState()
                val navController = rememberNavController()
                val isAuthorized = authState.isAuthorized

                // Этот LaunchedEffect - единственный "дирижер" навигации при смене статуса авторизации.
                LaunchedEffect(isAuthorized) {
                    val newRoute = if (isAuthorized) Screen.MainFlow.route else Screen.AuthFlow.route
                    // Проверяю, чтобы не делать лишнюю навигацию, если уже на нужном экране.
                    if (navController.currentDestination?.route != newRoute) {
                        navController.navigate(newRoute) {
                            // Очищаю весь стек навигации, чтобы нельзя было вернуться
                            // на экран логина кнопкой "назад".
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }

                Surface(modifier = Modifier.fillMaxSize()) {
                    // NavHost стартует с нужного экрана в зависимости от текущего статуса авторизации.
                    // Это предотвращает "мигание" экрана логина, если пользователь уже авторизован.
                    RootNavigation(
                        navController = navController,
                        startDestination = if (isAuthorized) Screen.MainFlow.route else Screen.AuthFlow.route
                    )
                }
            }
        }
    }
}
