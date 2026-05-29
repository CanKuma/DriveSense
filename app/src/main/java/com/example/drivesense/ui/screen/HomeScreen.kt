package com.example.drivesense.ui.screen

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.drivesense.ui.component.GPSCard
import com.example.drivesense.ui.component.SensorCard

@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel()
) {
    val context = LocalContext.current
    val gpsData by viewModel.gpsData.collectAsState()
    val hasLocationPermission by viewModel.hasLocationPermission.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        viewModel.onLocationPermissionResult(granted)
    }

    LaunchedEffect(Unit) {
        viewModel.checkLocationPermission(context)

        if (!viewModel.isLocationPermissionGranted(context)) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            viewModel.startGpsUpdates(context.applicationContext)
        }
    }

    Column(modifier = modifier) {
        GPSCard(
            gpsData = gpsData,
            hasPermission = hasLocationPermission
        )

        SensorCard("Gyro")
        SensorCard("IMU")
    }
}
