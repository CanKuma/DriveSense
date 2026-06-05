package com.example.drivesense.ui.screen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drivesense.data.Accelerator.AccData
import com.example.drivesense.data.Accelerator.AccRepository
import com.example.drivesense.data.GPS.GpsData
import com.example.drivesense.data.GPS.GpsRepository
import com.example.drivesense.data.Gyro.GyroData
import com.example.drivesense.data.Gyro.GyroRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
/**
 * Hold UI state for the HomeScreen and starts GPS data collection.
 *
 * The screen owns permission launching, while this ViewModel stores the latest permission state and
 * GPS values for Compose to observe.
 */
class HomeViewModel : ViewModel() {
    private val _gpsData = MutableStateFlow(GpsData())
    val gpsData = _gpsData.asStateFlow()
    private val _gyroData = MutableStateFlow(GyroData())
    val gyroData = _gyroData.asStateFlow()
    private val _accData = MutableStateFlow(AccData())
    val accData = _accData.asStateFlow()

    private var hasStartedGyroUpdates = false
    private var hasStartedAccUpdates = false

    fun startGyroUpdates(context: Context) {
        if (hasStartedGyroUpdates) return
        hasStartedGyroUpdates = true

        viewModelScope.launch {
            GyroRepository(context)
                .observeGyroData()
                .collect { data ->
                    _gyroData.value = data
                }
        }
    }

    fun startAccUpdates(context: Context) {
        if (hasStartedAccUpdates) return
        hasStartedAccUpdates = true

        viewModelScope.launch {
            AccRepository(context)
                .observeAccData()
                .collect { data ->
                    _accData.value = data
                }
        }
    }

    private val _hasLocationPermission = MutableStateFlow(false)
    val hasLocationPermission = _hasLocationPermission.asStateFlow()
    // Prevents starting multiple collectors after recomposition or permission changes.
    private var hasStartedGpsUpdates = false
    /**
     * Reads Android's current location permission and mirrors it into UI state.
     */
    fun checkLocationPermission(context: Context): Boolean {
        val granted =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        _hasLocationPermission.value = granted
        return granted
    }


    fun onLocationPermissionResult(granted: Boolean) {
        _hasLocationPermission.value = granted
    }
    /**
     * Called only after location permission has been granted.
     */
    fun startGpsUpdates(context: Context) {
        if (hasStartedGpsUpdates) return
        hasStartedGpsUpdates = true

        viewModelScope.launch {
            GpsRepository(context)
                .observeGpsData()
                .collect { data ->
                    // Updating StateFlow triggers the screen to recompose with the latest GPS data.
                    _gpsData.value = data
                }
        }
    }
}
