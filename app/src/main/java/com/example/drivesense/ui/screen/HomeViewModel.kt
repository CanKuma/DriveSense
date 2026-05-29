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

class HomeViewModel : ViewModel() {
    private val _gpsData = MutableStateFlow(GpsData())
    val gpsData = _gpsData.asStateFlow()

    private val _hasLocationPermission = MutableStateFlow(false)
    val hasLocationPermission = _hasLocationPermission.asStateFlow()

    private var hasStartedGpsUpdates = false

    fun checkLocationPermission(context: Context) {
        _hasLocationPermission.value =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
    }

    fun isLocationPermissionGranted(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun onLocationPermissionResult(granted: Boolean) {
        _hasLocationPermission.value = granted
    }

    fun startGpsUpdates(context: Context) {
        if (hasStartedGpsUpdates) return
        hasStartedGpsUpdates = true

        viewModelScope.launch {
            GpsRepository(context)
                .observeGpsData()
                .collect { data ->
                    _gpsData.value = data
                }
        }
    }
}
