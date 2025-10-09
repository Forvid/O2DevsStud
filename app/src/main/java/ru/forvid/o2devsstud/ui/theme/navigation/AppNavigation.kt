package ru.forvid.o2devsstud.ui.navigation

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ru.forvid.o2devsstud.ui.screens.*
import ru.forvid.o2devsstud.ui.screens.orders.OrderDetailsScreen
import ru.forvid.o2devsstud.ui.screens.ConfirmedScreen
import ru.forvid.o2devsstud.ui.viewmodel.HistoryViewModel
import ru.forvid.o2devsstud.ui.viewmodel.OrdersViewModel
import ru.forvid.o2devsstud.ui.viewmodel.ProfileViewModel


sealed class Screen(val route: String) {
    object AuthFlow : Screen("auth_flow")
    object MainFlow : Screen("main_flow")
    object Orders : Screen("orders")
    object CreateOrder : Screen("create_order")
    object OrderDetails : Screen("order_details/{orderId}") {
        fun createRoute(orderId: Long) = "order_details/$orderId"
    }
    object Confirmed : Screen("confirmed/{orderId}") {
        fun createRoute(orderId: Long) = "confirmed/$orderId"
    }
    object DriverMap : Screen("driver_map")
    object Map : Screen("map")
    object History : Screen("history")
    object Profile : Screen("profile")
    object ContactDevelopers : Screen("contact_developers")
}

@Composable
fun RootNavigation(navController: NavHostController, startDestination: String) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.AuthFlow.route) {
            LoginScreen(onLoginSuccess = {
                navController.navigate(Screen.MainFlow.route) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    launchSingleTop = true
                }
            })
        }
        composable(Screen.MainFlow.route) { MainScreen() }
    }
}

/**
 * MainAppNavGraph — activity-scoped OrdersViewModel пробрасывается в экраны, работающие с заказами.
 */
@Composable
fun MainAppNavGraph(navController: NavHostController, paddingValues: PaddingValues) {
    NavHost(
        navController = navController,
        startDestination = Screen.Orders.route,
        modifier = Modifier.padding(paddingValues)
    ) {
        composable(Screen.Orders.route) {
            val activity = LocalContext.current as ComponentActivity
            val sharedOrdersVm: OrdersViewModel = hiltViewModel(activity)

            OrdersScreen(
                onOpenOrder = { orderId ->
                    navController.navigate(Screen.OrderDetails.createRoute(orderId))
                },
                onCreateOrder = { navController.navigate(Screen.CreateOrder.route) },
                onShowMap = { trackId -> navController.navigate("map/$trackId") },
                viewModel = sharedOrdersVm
            )
        }

        composable(Screen.CreateOrder.route) {
            val activity = LocalContext.current as ComponentActivity
            val sharedOrdersVm: OrdersViewModel = hiltViewModel(activity)

            CreateOrderScreen(
                onBack = { navController.popBackStack() },
                viewModel = sharedOrdersVm
            )
        }

        composable(
            route = Screen.OrderDetails.route,
            arguments = listOf(navArgument("orderId") { type = NavType.LongType })
        ) { backStackEntry ->
            val activity = LocalContext.current as ComponentActivity
            val sharedOrdersVm: OrdersViewModel = hiltViewModel(activity)

            val orderId = backStackEntry.arguments?.getLong("orderId") ?: 0L
            OrderDetailsScreen(
                orderId = orderId,
                onBack = { navController.popBackStack() },
                onConfirm = { confirmedOrderId ->
                    navController.navigate(Screen.Confirmed.createRoute(confirmedOrderId)) {
                        popUpTo(Screen.OrderDetails.route) { inclusive = true }
                    }
                },
                viewModel = sharedOrdersVm
            )
        }

        composable(
            route = Screen.Confirmed.route,
            arguments = listOf(navArgument("orderId") { type = NavType.LongType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getLong("orderId") ?: 0L
            ConfirmedScreen(orderId = orderId, onBack = {
                navController.navigate(Screen.Orders.route) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                }
            })
        }

        composable(Screen.DriverMap.route) {
            DriverMapScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Map.route) {
            val activity = LocalContext.current as ComponentActivity
            val sharedOrdersVm: OrdersViewModel = hiltViewModel(activity)
            MapScreen(
                trackIdToShow = null,
                viewModel = sharedOrdersVm,
                onBack = {
                    sharedOrdersVm.clearTrackState()
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "map/{trackId}",
            arguments = listOf(navArgument("trackId") { type = NavType.LongType })
        ) { backStackEntry ->
            val activity = LocalContext.current as ComponentActivity
            val sharedOrdersVm: OrdersViewModel = hiltViewModel(activity)
            val trackId = backStackEntry.arguments?.getLong("trackId")
            MapScreen(
                trackIdToShow = trackId,
                viewModel = sharedOrdersVm,
                onBack = { navController.popBackStack() }
            )
        }

        // Profile screen (use ProfileViewModel)
        composable(Screen.Profile.route) {
            val activity = LocalContext.current as ComponentActivity
            val profileVm: ProfileViewModel = hiltViewModel(activity)
            val state by profileVm.uiState.collectAsState()

            ProfileScreen(
                profile = state.profile, // передаём доменную модель DriverProfile (или null)
                onEdit = { /* nav to edit screen */ },
                onLogout = {
                    profileVm.logout()
                    navController.navigate(Screen.AuthFlow.route) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        // History screen (use HistoryViewModel)
        composable(Screen.History.route) {
            val activity = LocalContext.current as ComponentActivity
            val historyVm: HistoryViewModel = hiltViewModel(activity)
            val state by historyVm.uiState.collectAsState()

            HistoryScreen(
                items = state.items,
                onOpen = { id -> navController.navigate(Screen.OrderDetails.createRoute(id)) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.ContactDevelopers.route) {
            ContactDevelopersScreen(
                onSendMessage = { /* send via SupportViewModel or API */ },
                phoneToCall = "+7..." /* configure */,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
