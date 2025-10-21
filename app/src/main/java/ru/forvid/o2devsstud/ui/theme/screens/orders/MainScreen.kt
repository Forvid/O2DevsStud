package ru.forvid.o2devsstud.ui.theme.screens.orders

import androidx.activity.ComponentActivity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
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
    authViewModel: AuthViewModel
) {
    val mainNavController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val activity = LocalContext.current as ComponentActivity
    val profileViewModel: ProfileViewModel = hiltViewModel(activity)
    val ordersViewModel: OrdersViewModel = hiltViewModel(activity)

    val currentBackStackEntry = mainNavController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry.value?.destination?.route

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    mainNavController.navigate(route) {
                        launchSingleTop = true
                        popUpTo(mainNavController.graph.startDestinationId)
                    }
                    // Закрывает меню после навигации
                    scope.launch { drawerState.close() }
                },
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
                paddingValues = paddingValues, // Передает padding от Scaffold
                ordersViewModel = ordersViewModel,
                profileViewModel = profileViewModel,
                authViewModel = authViewModel,
                onDrawerOpen = { scope.launch { drawerState.open() } }
            )
        }
    }
}
