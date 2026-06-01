package com.example.drivesense.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.drivesense.data.GPS.GpsData

@Composable
fun GPSCard(
    gpsData: GpsData,
    hasPermission: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "GPS",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (!hasPermission) {
                Text("Location permission is required.")
            } else {
                Text("Latitude: ${gpsData.latitude ?: "Waiting..."}")
                Text("Longitude: ${gpsData.longitude ?: "Waiting..."}")
                Text("Altitude: ${gpsData.altitude ?: "Unavailable"}")
                Text("Speed: ${gpsData.speed ?: "Unavailable"} m/s")
                Text("Accuracy: ${gpsData.accuracy ?: "Unavailable"} m")
            }
        }
    }
}

@Preview
@Composable
fun GPSCardPreview(){
    GPSCard(gpsData = GpsData(), hasPermission = true)
}
