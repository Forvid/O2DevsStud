package ru.forvid.o2devsstud.ui.theme.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ru.forvid.o2devsstud.ui.screens.*
import ru.forvid.o2devsstud.ui.theme.screens.orders.OrderDetailsScreen
import ru.forvid.o2devsstud.ui.theme.screens.orders.OrdersScreen
import ru.forvid.o2devsstud.ui.theme.viewmodel.AuthViewModel
import ru.forvid.o2devsstud.ui.viewmodel.HistoryViewModel
import ru.forvid.o2devsstud.ui.viewmodel.OrdersViewModel
import ru.forvid.o2devsstud.ui.viewmodel.ProfileViewModel
import ru.forvid.o2devsstud.ui.theme.screens.orders.MainScreen

sealed class Screen(val route: String) {
    object AuthFlow : Screen("auth_flow")
    object MainFlow : Screen("main_flow")
    object Home : Screen("home")
    object Orders : Screen("orders")
    object CreateOrder : Screen("create_order")
    object OrderDetails : Screen("order_details/{orderId}") {
        fun createRoute(orderId: Long) = "order_details/$orderId"
    }
    object Confirmed : Screen("confirmed/{orderId}") {
        fun createRoute(orderId: Long) = "confirmed/$orderId"
    }
    object Map : Screen("map")
    object MapWithTrack : Screen("map/{trackId}") {
        fun createRoute(trackId: Long) = "map/$trackId"
    }
    object History : Screen("history")
    object Profile : Screen("profile")
    object ContactDevelopers : Screen("contact_developers")
}

@Composable
fun RootNavigation(navController: NavHostController, startDestination: String) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.authState.collectAsState()

    LaunchedEffect(authState.isAuthorized) {
        val newRoute = if (authState.isAuthorized) {
            Screen.MainFlow.route
        } else {
            Screen.AuthFlow.route
        }
        if (navController.currentDestination?.route?.substringBefore("/") != newRoute) {
            navController.navigate(newRoute) {
                popUpTo(navController.graph.startDestinationId) {
                    inclusive = true
                }
            }
        }
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.AuthFlow.route) { LoginScreen(viewModel = authViewModel) }
        composable(Screen.MainFlow.route) { MainScreen(authViewModel = authViewModel) }
    }
}

@Composable
fun MainAppNavGraph(
    navController: NavHostController,
    paddingValues: PaddingValues,
    ordersViewModel: OrdersViewModel,
    profileViewModel: ProfileViewModel,
    authViewModel: AuthViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = Modifier.padding(paddingValues)
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = ordersViewModel,
                onOrderClick = { orderId -> navController.navigate(Screen.OrderDetails.createRoute(orderId)) }
            )
        }

        composable(Screen.Orders.route) {
            OrdersScreen(
                onOpenOrder = { orderId -> navController.navigate(Screen.OrderDetails.createRoute(orderId)) },
                onCreateOrder = { navController.navigate(Screen.CreateOrder.route) },
                viewModel = ordersViewModel
            )
        }
        composable(Screen.CreateOrder.route) {
            CreateOrderScreen(onBack = { navController.popBackStack() }, viewModel = ordersViewModel)
        }
        composable(
            route = Screen.OrderDetails.route,
            arguments = listOf(navArgument("orderId") { type = NavType.LongType })
        ) {
            // --- ИСПРАВЛЕНИЕ 2: Возвращена правильная логика для OrderDetailsScreen ---
            OrderDetailsScreen(
                orderId = it.arguments?.getLong("orderId") ?: 0L,
                onBack = { navController.popBackStack() },
                onConfirm = { confirmedOrderId ->
                    navController.navigate(Screen.Confirmed.createRoute(confirmedOrderId)) {
                        popUpTo(Screen.OrderDetails.route) { inclusive = true }
                    }
                },
                onShowTrackOnMap = { trackId -> navController.navigate(Screen.MapWithTrack.createRoute(trackId)) },
                viewModel = ordersViewModel
            )
        }
        composable(
            route = Screen.Confirmed.route,
            arguments = listOf(navArgument("orderId") { type = NavType.LongType })
        ) {
            ConfirmedScreen(orderId = it.arguments?.getLong("orderId") ?: 0L, onBack = {
                navController.navigate(Screen.Orders.route) { popUpTo(navController.graph.startDestinationId) { inclusive = true } }
            })
        }
        composable(Screen.Map.route) {
            MapScreen(viewModel = ordersViewModel, onBack = { navController.popBackStack() })
        }
        composable(
            route = Screen.MapWithTrack.route,
            arguments = listOf(navArgument("trackId") { type = NavType.LongType })
        ) {
            MapScreen(trackIdToShow = it.arguments?.getLong("trackId"), viewModel = ordersViewModel, onBack = { navController.popBackStack() })
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                profileViewModel = profileViewModel,
                authViewModel = authViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.History.route) {
            val historyVm: HistoryViewModel = hiltViewModel()
            HistoryScreen(
                viewModel = historyVm,
                onOpen = { id -> navController.navigate(Screen.OrderDetails.createRoute(id)) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.ContactDevelopers.route) {
            ContactDevelopersScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
