package com.example.drivesense.data.Gyro

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class GyroRepository(
    private val context: Context
) {
    fun observeGyroData(): Flow<GyroData> = callbackFlow {
        val sensorManager =
            context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        val gyroSensor =
            sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        if (gyroSensor == null) {
            close()
            return@callbackFlow
        }

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                trySend(
                    GyroData(
                        x = event.values[0],
                        y = event.values[1],
                        z = event.values[2]
                    )
                )
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
        }

        sensorManager.registerListener(
            listener,
            gyroSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )

        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }
}