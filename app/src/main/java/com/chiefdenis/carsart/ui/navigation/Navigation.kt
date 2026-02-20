package com.chiefdenis.carsart.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.chiefdenis.carsart.ui.screens.SettingsScreen
import com.chiefdenis.carsart.ui.screens.VehiclesScreen

sealed class Screen(val route: String) {
    object Vehicles : Screen("vehicles")
    object Settings : Screen("settings")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.Vehicles.route) {
        composable(Screen.Vehicles.route) {
            VehiclesScreen()
        }
        composable(Screen.Settings.route) {
            SettingsScreen()
        }
    }
}
