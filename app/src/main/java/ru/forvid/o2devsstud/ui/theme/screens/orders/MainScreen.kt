package ru.forvid.o2devsstud.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import ru.forvid.o2devsstud.ui.components.AppDrawer
import ru.forvid.o2devsstud.ui.navigation.MainAppNavGraph
import ru.forvid.o2devsstud.ui.navigation.Screen
import ru.forvid.o2devsstud.ui.viewmodel.AuthViewModel
import ru.forvid.o2devsstud.ui.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(authViewModel: AuthViewModel = hiltViewModel()) {
    val mainNavController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val currentBackStackEntry = mainNavController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry.value?.destination?.route

    // Activity-scoped ProfileViewModel to show avatar/name in drawer header
    val profileVm: ProfileViewModel = hiltViewModel()
    val profileState by profileVm.uiState.collectAsState()
    val profile = profileState.profile

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            // ModalDrawerSheet гарантирует корректный (непрозрачный) фон для drawer
            ModalDrawerSheet {
                AppDrawer(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        mainNavController.navigate(route) {
                            this.launchSingleTop = true
                            popUpTo(mainNavController.graph.startDestinationId)
                        }
                    },
                    onSignOut = { authViewModel.onSignOut() },
                    closeDrawer = { scope.launch { drawerState.close() } },
                    profile = profile,
                    // profile header click: navigate to Profile screen and close drawer
                    onProfileClick = {
                        scope.launch {
                            drawerState.close()
                            mainNavController.navigate(Screen.Profile.route) {
                                this.launchSingleTop = true
                                popUpTo(mainNavController.graph.startDestinationId)
                            }
                        }
                    }
                )
            }
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
            MainAppNavGraph(navController = mainNavController, paddingValues = paddingValues)
        }
    }
}
