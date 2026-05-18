package com.example.bricole.navigation

sealed class Screen(val route: String) {
    data object HomeScreen : Screen("home_screen")
    data object SearchScreen : Screen("search_screen")
    data object JoinScreen : Screen("join_screen")
    data object ProviderScreen : Screen("provider/{serviceId}/{serviceName}") {
        fun createRoute(serviceId: Int, serviceName: String): String {
            return "provider/$serviceId/$serviceName"
        }
    }
}