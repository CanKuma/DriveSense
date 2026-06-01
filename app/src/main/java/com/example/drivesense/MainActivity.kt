package com.example.drivesense

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.drivesense.ui.screen.HomeScreen
import com.example.drivesense.ui.theme.DriveSenseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DriveSenseTheme {
                Scaffold(
                    topBar = {
                        AppTopBar()},
                    modifier = Modifier.fillMaxSize()) { innerPadding ->
                    HomeScreen(
                        modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize())
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(){
    CenterAlignedTopAppBar(
        title = {
            Text("DriveSense")
        }
    )
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DriveSenseTheme {
        Scaffold(
            topBar = {AppTopBar()}
        ) { innerPadding ->
            HomeScreen(
                modifier = Modifier
                    .padding(innerPadding)
            )
        }
    }
}
