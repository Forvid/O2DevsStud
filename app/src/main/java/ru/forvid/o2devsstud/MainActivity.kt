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
import ru.forvid.o2devsstud.ui.navigation.RootNavigation
import ru.forvid.o2devsstud.ui.navigation.Screen
import ru.forvid.o2devsstud.ui.theme.O2DevsStudTheme
import ru.forvid.o2devsstud.ui.viewmodel.AuthViewModel

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

                // Навигируем при изменении статуса авторизации
                LaunchedEffect(isAuthorized) {
                    val target = if (isAuthorized) Screen.MainFlow.route else Screen.AuthFlow.route
                    // безопасно навигируем (popUpTo корня)
                    try {
                        if (navController.currentDestination?.route != target) {
                            navController.navigate(target) {
                                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    } catch (_: Throwable) {
                        // ignore: protection against graph not ready — NavHost сам установит стартовую точку
                    }
                }

                Surface(modifier = Modifier.fillMaxSize()) {
                    // стартовая точка выбор в зависимости от авторизации — это помогает сразу нарисовать нужный экран
                    RootNavigation(
                        navController = navController,
                        startDestination = if (isAuthorized) Screen.MainFlow.route else Screen.AuthFlow.route
                    )
                }
            }
        }
    }
}
