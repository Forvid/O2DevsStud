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

                LaunchedEffect(isAuthorized) {
                    val newRoute = if (isAuthorized) Screen.MainFlow.route else Screen.AuthFlow.route
                    if (navController.currentDestination?.route != newRoute) {
                        navController.navigate(newRoute) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }

                Surface(modifier = Modifier.fillMaxSize()) {
                    RootNavigation(
                        navController = navController,
                        startDestination = if (isAuthorized) Screen.MainFlow.route else Screen.AuthFlow.route
                    )
                }
            }
        }
    }
}