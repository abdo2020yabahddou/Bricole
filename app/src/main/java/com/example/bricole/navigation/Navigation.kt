package com.example.bricole.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.bricole.ui.screens.HomeScreen
import com.example.bricole.ui.screens.JoinScreen
import com.example.bricole.ui.screens.ProviderScreen
import com.example.bricole.ui.screens.SearchScreen

@Composable
fun Navigation() {
    val navController: NavHostController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.HomeScreen.route) {
        composable(Screen.HomeScreen.route) {
            HomeScreen(
                onServiceClick = { dto ->
                    navController.navigate(
                        route = Screen.ProviderScreen.createRoute(
                            serviceId = dto.id,
                            serviceName = dto.name
                        )
                    )
                },
                onSearchClick = { navController.navigate(Screen.SearchScreen.route) },
                onJoinClick = { navController.navigate(Screen.JoinScreen.route) })
        }
        composable(Screen.SearchScreen.route) {
            SearchScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            Screen.ProviderScreen.route,
            arguments = listOf(
                navArgument("serviceId") {
                    type = NavType.IntType
                },
                navArgument("serviceName") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val serviceId = backStackEntry.arguments?.getInt("serviceId") ?: 0
            val serviceName = backStackEntry.arguments?.getString("serviceName") ?: ""
            ProviderScreen(serviceId, serviceName) {
                navController.popBackStack()
            }
        }
        composable(Screen.JoinScreen.route) {
            JoinScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}