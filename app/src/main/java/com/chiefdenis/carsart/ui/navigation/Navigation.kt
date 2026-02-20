package com.chiefdenis.carsart.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.chiefdenis.carsart.ui.screens.SettingsScreen
import com.chiefdenis.carsart.ui.screens.VehiclesScreen
import androidx.navigation.NavHostController
import com.chiefdenis.carsart.ui.screens.AddVehicleScreen
import com.chiefdenis.carsart.ui.screens.VehicleDetailScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.chiefdenis.carsart.ui.screens.AddServiceRecordScreen

sealed class Screen(val route: String) {
    object Vehicles : Screen("vehicles")
    object Settings : Screen("settings")
    object AddVehicle : Screen("add_vehicle")
    object VehicleDetail : Screen("vehicle_detail/{vehicleId}") {
        fun createRoute(vehicleId: String) = "vehicle_detail/$vehicleId"
    }
    object AddServiceRecord : Screen("add_service_record/{vehicleId}") {
        fun createRoute(vehicleId: String) = "add_service_record/$vehicleId"
    }
}

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Vehicles.route) {
        composable(Screen.Vehicles.route) {
            VehiclesScreen(
                onAddVehicle = { navController.navigate(Screen.AddVehicle.route) },
                onVehicleClick = { vehicleId -> navController.navigate(Screen.VehicleDetail.createRoute(vehicleId.toString())) }
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen()
        }
        composable(Screen.AddVehicle.route) { AddVehicleScreen(onVehicleAdded = { navController.popBackStack() }) }
        composable(
            route = Screen.VehicleDetail.route,
            arguments = listOf(navArgument("vehicleId") { type = NavType.StringType })
        ) { 
            VehicleDetailScreen(
                onAddServiceRecord = { vehicleId -> navController.navigate(Screen.AddServiceRecord.createRoute(vehicleId.toString())) }
            )
        }
        composable(
            route = Screen.AddServiceRecord.route,
            arguments = listOf(navArgument("vehicleId") { type = NavType.StringType })
        ) {
            AddServiceRecordScreen(onServiceRecordAdded = { navController.popBackStack() })
        }
    }
}
