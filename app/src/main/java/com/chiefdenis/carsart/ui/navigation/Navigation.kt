package com.chiefdenis.carsart.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.chiefdenis.carsart.ui.screens.AddServiceRecordScreen
import com.chiefdenis.carsart.ui.screens.AddVehicleScreen
import com.chiefdenis.carsart.ui.screens.MaintenanceCheckScreen
import com.chiefdenis.carsart.ui.screens.SettingsScreen
import com.chiefdenis.carsart.ui.screens.VehicleDetailScreen
import com.chiefdenis.carsart.ui.screens.VehiclesScreen

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
    object MaintenanceCheck : Screen("maintenance/check/{taskId}") {
        fun createRoute(taskId: String) = "maintenance/check/$taskId"
        const val uri = "carsart://maintenance/check"
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
        composable(
            route = Screen.MaintenanceCheck.route,
            arguments = listOf(navArgument("taskId") { type = NavType.StringType }),
            deepLinks = listOf(navDeepLink { uriPattern = "${Screen.MaintenanceCheck.uri}/{taskId}" })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId")
            if (taskId != null) {
                MaintenanceCheckScreen(navController = navController, taskId = taskId)
            }
        }
    }
}
