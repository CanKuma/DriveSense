package com.example.drivesense.ui.screen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drivesense.data.GPS.GpsData
import com.example.drivesense.data.GPS.GpsRepository
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
