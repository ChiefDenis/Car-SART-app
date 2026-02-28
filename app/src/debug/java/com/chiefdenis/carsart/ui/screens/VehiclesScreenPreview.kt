package com.chiefdenis.carsart.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.chiefdenis.carsart.ui.theme.CarSartTheme

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CarSartTheme {
        VehiclesScreen(onAddVehicle = {}, onVehicleClick = {})
    }
}

@Preview(showBackground = true)
@Composable
fun EmptyVehiclesPreview() {
    CarSartTheme {
        VehiclesScreen(onAddVehicle = {}, onVehicleClick = {})
    }
}
