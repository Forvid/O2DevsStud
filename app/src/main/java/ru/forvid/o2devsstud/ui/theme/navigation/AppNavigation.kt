package ru.forvid.o2devsstud.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ru.forvid.o2devsstud.ui.screens.*
import ru.forvid.o2devsstud.ui.screens.orders.OrderDetailsScreen
import ru.forvid.o2devsstud.ui.screens.ConfirmedScreen
import androidx.navigation.NavBackStackEntry

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


@Composable
fun MainAppNavGraph(navController: NavHostController, paddingValues: PaddingValues) {
    NavHost(
        navController = navController,
        startDestination = Screen.Orders.route,
        modifier = Modifier.padding(paddingValues)
    ) {
        composable(Screen.Orders.route) {
            OrdersScreen(
                onOpenOrder = { orderId -> navController.navigate(Screen.OrderDetails.createRoute(orderId)) },
                onCreateOrder = { navController.navigate(Screen.CreateOrder.route) }
            )
        }
        composable(Screen.CreateOrder.route) { CreateOrderScreen(onBack = { navController.popBackStack() }) }

        composable(
            route = Screen.OrderDetails.route,
            arguments = listOf(navArgument("orderId") { type = NavType.LongType })
        ) { backStackEntry: NavBackStackEntry ->
            val orderId = backStackEntry.arguments?.getLong("orderId") ?: 0L
            OrderDetailsScreen(
                orderId = orderId,
                onBack = { navController.popBackStack() },
                onConfirm = { confirmedOrderId ->
                    navController.navigate(Screen.Confirmed.createRoute(confirmedOrderId)) {
                        popUpTo(Screen.OrderDetails.route) { inclusive = true }
                    }
                }
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

        composable(Screen.DriverMap.route) { DriverMapScreen(onBack = { navController.popBackStack() }) }
        composable(Screen.Map.route) { MapScreen() }
        composable(Screen.History.route) { StubScreen(name = "История поставок") }
        composable(Screen.Profile.route) { StubScreen(name = "Мой профиль") }
        composable(Screen.ContactDevelopers.route) { StubScreen(name = "Связаться с разработчиками") }
    }
}
