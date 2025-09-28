package ru.forvid.o2devsstud.ui.navigation

sealed class Screen(val route: String) {
    object Orders : Screen("orders")
    object CreateOrder : Screen("create_order")
    object DriverMap : Screen("driver_map")
    object OrderDetails : Screen("order_details/{orderId}") {
        fun createRoute(orderId: Long) = "order_details/$orderId"
    }
    object Confirmed : Screen("confirmed/{orderId}") {
        fun createRoute(orderId: Long) = "confirmed/$orderId"
    }
}
