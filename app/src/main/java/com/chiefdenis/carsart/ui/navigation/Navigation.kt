package com.chiefdenis.carsart.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.chiefdenis.carsart.ui.screens.SettingsScreen
import com.chiefdenis.carsart.ui.screens.VehiclesScreen
import androidx.navigation.NavHostController

sealed class Screen(val route: String) {
    object Vehicles : Screen("vehicles")
    object Settings : Screen("settings")
    object AddVehicle : Screen("add_vehicle")
}

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Vehicles.route) {
        composable(Screen.Vehicles.route) {
            VehiclesScreen(onAddVehicle = { navController.navigate(Screen.AddVehicle.route) })
        }
        composable(Screen.Settings.route) {
            SettingsScreen()
        }
    }
}
