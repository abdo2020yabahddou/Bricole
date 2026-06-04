package com.example.bricole.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.bricole.ui.screens.home.HomeScreen
import com.example.bricole.ui.screens.provider.join.JoinScreen
import com.example.bricole.ui.screens.provider.listing.ProvidersScreen
import com.example.bricole.ui.screens.provider.search.SearchScreen

@Composable
fun Navigation() {
    val navController: NavHostController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.HomeScreen.route) {
        composable(Screen.HomeScreen.route) {
            HomeScreen(
                onServiceClick = { serviceResponse ->
                    navController.navigate(
                        route = Screen.ProvidersScreen.createRoute(
                            serviceId = serviceResponse.id,
                            serviceName = serviceResponse.name
                        )
                    )
                },
                onSearchClick = { navController.navigate(Screen.SearchScreen.route) },
                onJoinClick = { navController.navigate(Screen.JoinScreen.route) }
            )
        }
        composable(Screen.SearchScreen.route) {
            SearchScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.ProvidersScreen.route,
            arguments = listOf(
                navArgument("serviceId") {
                    type = NavType.IntType
                },
                navArgument("serviceName") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val args = backStackEntry.arguments
            val serviceId = args?.getInt("serviceId") ?: return@composable
            val serviceName = args.getString("serviceName") ?: return@composable
            ProvidersScreen(
                serviceId = serviceId,
                serviceName = serviceName,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.JoinScreen.route) {
            JoinScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}