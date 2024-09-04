package com.example.app1

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.app1.ui.theme.App1Theme
import com.example.app1.views.AuthViewModel

class MainActivity : ComponentActivity() {

    private lateinit var permission: AppPermissions

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                println("Location permission granted")
            } else {
                println("Location permission denied")
            }
        }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val authViewModel: AuthViewModel by viewModels()

        permission = AppPermissions()

        if (permission.isLocationOk(this)) {
            println("Location permission allowed")
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            println("Location permission denied")
        }

        setContent {
            App1Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MyAppNavigation(modifier = Modifier.padding(innerPadding), authViewModel = authViewModel)
                }
            }
        }
    }
}
