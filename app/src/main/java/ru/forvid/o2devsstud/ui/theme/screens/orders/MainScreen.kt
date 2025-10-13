package ru.forvid.o2devsstud.ui.theme.screens.orders

import androidx.activity.ComponentActivity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import ru.forvid.o2devsstud.ui.components.AppDrawer
import ru.forvid.o2devsstud.ui.theme.navigation.MainAppNavGraph
import ru.forvid.o2devsstud.ui.theme.viewmodel.AuthViewModel
import ru.forvid.o2devsstud.ui.viewmodel.OrdersViewModel
import ru.forvid.o2devsstud.ui.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    // Теперь я снова принимаю authViewModel, как и ожидает AppNavigation.kt
    authViewModel: AuthViewModel
) {
    val mainNavController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Централизованно создаю все общие ViewModel здесь.
    val activity = LocalContext.current as ComponentActivity
    val profileViewModel: ProfileViewModel = hiltViewModel(activity)
    val ordersViewModel: OrdersViewModel = hiltViewModel(activity)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            // Передаю созданные ViewModel вниз по иерархии.
            AppDrawer(
                mainNavController = mainNavController,
                onSignOut = { authViewModel.onSignOut() },
                closeDrawer = { scope.launch { drawerState.close() } },
                profileViewModel = profileViewModel
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("O2DevsStud") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Меню")
                        }
                    }
                )
            }
        ) { paddingValues ->
            MainAppNavGraph(
                navController = mainNavController,
                paddingValues = paddingValues,
                ordersViewModel = ordersViewModel,
                profileViewModel = profileViewModel
            )
        }
    }
}