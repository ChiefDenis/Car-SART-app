package com.chiefdenis.carsart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.chiefdenis.carsart.data.repository.ThemeMode
import com.chiefdenis.carsart.ui.screens.AddServiceRecordScreen
import com.chiefdenis.carsart.ui.screens.AddVehicleScreen
import com.chiefdenis.carsart.ui.screens.SettingsScreen
import com.chiefdenis.carsart.ui.screens.SettingsViewModel
import com.chiefdenis.carsart.ui.screens.VehicleDetailScreen
import com.chiefdenis.carsart.ui.screens.VehiclesScreen
import com.chiefdenis.carsart.ui.theme.CarSARTTheme
import dagger.hilt.android.AndroidEntryPoint

sealed class Screen(val route: String, val label: String? = null, val icon: ImageVector? = null) {
    object Vehicles : Screen("vehicles", "Vehicles", Icons.Default.DirectionsCar)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
    object AddVehicle : Screen("add_vehicle")
    object VehicleDetail : Screen("vehicle_detail/{vehicleId}") {
        fun createRoute(vehicleId: String) = "vehicle_detail/$vehicleId"
    }
    object AddServiceRecord : Screen("add_service_record/{vehicleId}") {
        fun createRoute(vehicleId: String) = "add_service_record/$vehicleId"
    }
}

val items = listOf(
    Screen.Vehicles,
    Screen.Settings,
)

@Composable
fun FloatingNavigationBar(
    items: List<Screen>,
    currentDestination: NavDestination?,
    onNavigate: (String) -> Unit
) {
    var isVisible by remember { mutableStateOf(true) }
    
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(
            animationSpec = tween(
                durationMillis = 300,
                easing = EaseOutCubic
            )
        ) + scaleIn(
            animationSpec = tween(
                durationMillis = 300,
                easing = EaseOutCubic
            ),
            initialScale = 0.8f
        ),
        exit = fadeOut(
            animationSpec = tween(
                durationMillis = 200,
                easing = EaseOutCubic
            )
        ) + scaleOut(
            animationSpec = tween(
                durationMillis = 200,
                easing = EaseOutCubic
            ),
            targetScale = 0.8f
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .navigationBarsPadding()
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(28.dp)
                ),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                    alpha = 0.95f
                )
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { screen ->
                    screen.label?.let { label ->
                        val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                        
                        FloatingNavButton(
                            icon = screen.icon!!,
                            label = label,
                            isSelected = isSelected,
                            onClick = { onNavigate(screen.route) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FloatingNavButton(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(24.dp),
            tint = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
            ),
            color = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val settings by settingsViewModel.settings.collectAsState()
            
            CarSARTTheme(themeMode = settings.themeMode) {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                
                Scaffold(
                    bottomBar = {
                        FloatingNavigationBar(
                            items = items,
                            currentDestination = currentDestination,
                            onNavigate = { route ->
                                navController.navigate(route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    NavHost(
                        navController,
                        startDestination = Screen.Vehicles.route,
                        Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Vehicles.route) { 
                            VehiclesScreen(
                                onAddVehicle = { navController.navigate(Screen.AddVehicle.route) },
                                onVehicleClick = { vehicleId -> navController.navigate(Screen.VehicleDetail.createRoute(vehicleId.toString())) }
                            )
                        }
                        composable(Screen.Settings.route) { SettingsScreen() }
                        composable(
                            Screen.AddVehicle.route,
                            enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(500)) },
                            exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }, animationSpec = tween(500)) },
                            popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }, animationSpec = tween(500)) },
                            popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween(500)) }
                        ) { AddVehicleScreen(onVehicleAdded = { navController.popBackStack() }) }
                        composable(
                            route = Screen.VehicleDetail.route,
                            arguments = listOf(navArgument("vehicleId") { type = NavType.StringType }),
                            enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(500)) },
                            exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }, animationSpec = tween(500)) },
                            popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }, animationSpec = tween(500)) },
                            popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween(500)) }
                        ) { 
                            VehicleDetailScreen(
                                onAddServiceRecord = { vehicleId -> navController.navigate(Screen.AddServiceRecord.createRoute(vehicleId.toString())) }
                            )
                        }
                        composable(
                            route = Screen.AddServiceRecord.route,
                            arguments = listOf(navArgument("vehicleId") { type = NavType.StringType }),
                            enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(500)) },
                            exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }, animationSpec = tween(500)) },
                            popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }, animationSpec = tween(500)) },
                            popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween(500)) }
                        ) {
                            AddServiceRecordScreen(onServiceRecordAdded = { navController.popBackStack() })
                        }
                    }
                }
            }
        }
    }
}
