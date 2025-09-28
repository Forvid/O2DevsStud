package ru.forvid.o2devsstud.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ru.forvid.o2devsstud.ui.screens.OrdersScreen
import ru.forvid.o2devsstud.ui.theme.screens.orders.OrderDetailsScreen
import ru.forvid.o2devsstud.ui.screens.CreateOrderScreen
import ru.forvid.o2devsstud.ui.screens.DriverMapScreen
import ru.forvid.o2devsstud.ui.screens.ConfirmedScreen

@Composable
fun AppNavHost(startDestination: String = Screen.Orders.route) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = startDestination) {

        composable(Screen.Orders.route) {
            OrdersScreen(
                onOpenOrder = { orderId: Long ->
                    navController.navigate(Screen.OrderDetails.createRoute(orderId))
                },
                onCreateOrder = {
                    navController.navigate(Screen.CreateOrder.route)
                }
            )
        }

        composable(
            route = Screen.OrderDetails.route,
            arguments = listOf(navArgument("orderId") { type = NavType.LongType })
        ) { backStackEntry: NavBackStackEntry ->
            val orderId: Long = backStackEntry.arguments?.getLong("orderId") ?: 0L
            OrderDetailsScreen(
                orderId = orderId,
                onBack = { navController.popBackStack() },
                onConfirm = { id: Long -> navController.navigate(Screen.Confirmed.createRoute(id)) }
            )
        }

        composable(Screen.CreateOrder.route) {
            CreateOrderScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.DriverMap.route) {
            DriverMapScreen(onBack = { navController.popBackStack() })
        }

        composable(
            route = Screen.Confirmed.route,
            arguments = listOf(navArgument("orderId") { type = NavType.LongType })
        ) { backStackEntry: NavBackStackEntry ->
            val orderId: Long = backStackEntry.arguments?.getLong("orderId") ?: 0L
            ConfirmedScreen(orderId = orderId, onBack = { navController.popBackStack() })
        }
    }
}
