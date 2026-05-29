package com.example.drivesense.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.drivesense.ui.component.SensorCard

@Composable
fun HomePage(
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier
    ) {
        SensorCard("GPS")
        SensorCard("Gyro")
        SensorCard("IMU")
    }
}