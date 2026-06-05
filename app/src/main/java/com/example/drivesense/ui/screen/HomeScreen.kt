package com.example.drivesense.ui.screen

import android.Manifest
import android.content.res.Configuration
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.drivesense.data.Accelerator.AccData
import com.example.drivesense.data.GPS.GpsData
import com.example.drivesense.data.Gyro.GyroData
import com.example.drivesense.ui.component.GPSCard
import com.example.drivesense.ui.component.SensorCard

/**
 * It displays sensor cards, requests location permission, and starts GPS updates through ViewModel
 * once permission is available.
 */
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel()
) {
    val configuration = LocalConfiguration.current

    val gpsData by viewModel.gpsData.collectAsState()
    val hasLocationPermission by viewModel.hasLocationPermission.collectAsState()
    val gyroData by viewModel.gyroData.collectAsState()
    val accData by viewModel.accData.collectAsState()
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.startGyroUpdates(context.applicationContext)
    }
    LaunchedEffect(Unit) {
        viewModel.startAccUpdates(context.applicationContext)
    }


    LocationPermissionHandler(viewModel = viewModel)

    if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT){
        HomePortrait(
            gpsData = gpsData,
            hasLocationPermission = hasLocationPermission,
            gyroData = gyroData,
            accData = accData,
            modifier = modifier
        )} else {
            HomeLandscape(
                gpsData = gpsData,
                hasLocationPermission = hasLocationPermission,
                gyroData = gyroData,
                accData = accData,
                modifier = modifier
            )
        }
}




/**
 * Handles location permission side effects for HomeScreen.
 *
 * This composable checks the current permission, launches Android's permission
 * dialog when needed, and starts GPS updates after permission is granted.
 */
@Composable
private fun LocationPermissionHandler(
    viewModel: HomeViewModel
) {
    val context = LocalContext.current
    val hasLocationPermission by viewModel.hasLocationPermission.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        viewModel.onLocationPermissionResult(granted)
    }

    LaunchedEffect(Unit) {
        val granted = viewModel.checkLocationPermission(context)

        if (!granted) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            viewModel.startGpsUpdates(context.applicationContext)
        }
    }
}


@Composable
fun HomePortrait(
    gpsData: GpsData,
    hasLocationPermission: Boolean,
    gyroData: GyroData,
    accData: AccData,
    modifier: Modifier
){
    Column(
        modifier = modifier
    ) {
        GPSCard(
            gpsData = gpsData,
            hasPermission = hasLocationPermission,
            modifier = Modifier
                .weight(1f)
        )
        SensorCard(
            title = "Gyro",
            info1 = "X: %.2f".format(gyroData.x),
            info2 = "Y: %.2f".format(gyroData.y),
            info3 = "Z: %.2f".format(gyroData.z),
            modifier = Modifier
                .weight(1f)
        )
        SensorCard(
            title = "Acc",
            info1 = "X: %.2f".format(accData.x),
            info2 = "Y: %.2f".format(accData.y),
            info3 = "Z: %.2f".format(accData.z),
            modifier = Modifier
                .weight(1f)
        )
    }
}

@Composable
fun HomeLandscape(
    gpsData: GpsData,
    hasLocationPermission: Boolean,
    gyroData: GyroData,
    accData: AccData,
    modifier: Modifier
){
    Row(
        modifier = modifier
    ) {
        GPSCard(
            gpsData = gpsData,
            hasPermission = hasLocationPermission,
            modifier = Modifier
                .weight(1f)
        )
        SensorCard(
            title = "Gyro",
            info1 = "X: %.2f".format(gyroData.x),
            info2 = "Y: %.2f".format(gyroData.y),
            info3 = "Z: %.2f".format(gyroData.z),
            modifier = Modifier
                .weight(1f)
        )
        SensorCard(
            title = "Acc",
            info1 = "X: %.2f".format(accData.x),
            info2 = "Y: %.2f".format(accData.y),
            info3 = "Z: %.2f".format(accData.z),
            modifier = Modifier
                .weight(1f)
        )
    }
}

@Preview()
@Composable
fun HomeScreenPreview(){
    HomeScreen()
}
