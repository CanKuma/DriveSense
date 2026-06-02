package com.example.drivesense.data.GPS

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/*
 * Repository responsible for reading GPS/location updates from Android
 *
 * Exposes location updates as a Flow of GpsData
 */
class GpsRepository(
    private val context: Context
) {
    private val TAG = "GpsRepository"

    /*
     * Observes high-accuracy location updates about once per second
     *
     * Location Permission must be granted before this function is called
     */
    @SuppressLint("MissingPermission")
    fun observeGpsData(): Flow<GpsData> = callbackFlow {
        val fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(context)

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            1000L
        ).build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                Log.d(TAG, "onLocationResult called, locations=${result.locations.size}.")
                val location = result.lastLocation
                if (location == null) {
                    Log.d(TAG, "LocationResult has no lastLocation")
                    return
                }

                Log.d(
                    TAG,
                    "Location received: lat=${location.latitude}, lng=${location.longitude}, accuracy=${location.accuracy}"
                )

                val isGpsSend = trySend(
                    GpsData(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        altitude = if (location.hasAltitude()) location.altitude else null,
                        speed = if (location.hasSpeed()) location.speed else null,
                        accuracy = if (location.hasAccuracy()) location.accuracy else null
                    )
                )

                if (isGpsSend.isSuccess) Log.d(TAG, "Gps data is successfully sent. ")
                if (isGpsSend.isFailure) Log.d(TAG, "Gps data sent failed. ")
            }
        }

        Log.d(TAG, "Requesting location updates")

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            callback,
            // Run callback on the main thread.
            Looper.getMainLooper()
        ).addOnSuccessListener {
            Log.d(TAG, "Location updates started")
        }.addOnFailureListener { exception ->
            Log.e(TAG, "Failed to start location updates", exception)
        }

        awaitClose {
            Log.d(TAG, "Flow closed, removing location updates")
            fusedLocationClient.removeLocationUpdates(callback)
        }
    }
}
