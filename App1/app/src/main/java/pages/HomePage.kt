package pages

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.app1.AuthState
import com.example.app1.AuthViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@SuppressLint("MissingPermission")
@Composable
fun HomePage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {

    val context = LocalContext.current
    val authState = authViewModel.authState.observeAsState()

    // Manage current location state
    val currentLocation = remember { mutableStateOf<LatLng?>(null) }

    // Initialize FusedLocationProviderClient
    val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    // Request location updates
    val locationRequest = com.google.android.gms.location.LocationRequest.create().apply {
        interval = 10000 // Update interval in milliseconds
        fastestInterval = 5000 // Fastest update interval in milliseconds
        priority = com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    // Start location updates
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, object : com.google.android.gms.location.LocationCallback() {
                override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
                    super.onLocationResult(locationResult)
                    locationResult.locations.forEach { location ->
                        Log.d("HomePage", "Updated Location: ${location.latitude}, ${location.longitude}")
                        currentLocation.value = LatLng(location.latitude, location.longitude)
                    }
                }
            }, null)
        } else {
            // Handle the case where permission is not granted
            println("Location permission not granted")
        }
    }

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Unauthenticated -> navController.navigate("login")
            else -> Unit
        }
    }

    // Create a CameraPositionState to control the map's camera
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(0.0, 0.0), 15f) // Default position
    }

    // Update the camera position when the current location is available
    LaunchedEffect(currentLocation.value) {
        currentLocation.value?.let {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 15f)
        }
    }

    // Dropdown menu state
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top-right corner dropdown button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize(Alignment.TopEnd)
        ) {
            IconButton(onClick = { expanded = true }) {
                Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More options")
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("User Profile") },
                    onClick = {
                        expanded = false
                        navController.navigate("user_profile")
                    }
                )
                DropdownMenuItem(
                    text = { Text("All Users") },
                    onClick = {
                        expanded = false
                        navController.navigate("all_users")
                    }
                )
            }
        }

        Text(text = "Home Page", fontSize = 32.sp)

        // Add Google Map
        GoogleMap(
            modifier = Modifier
                .fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            currentLocation.value?.let {
                Marker(
                    state = MarkerState(position = it),
                    title = "Your Location"
                )
            }
        }

        TextButton(onClick = {
            authViewModel.signout()
        }) {
            Text(text = "Sign out")
        }
    }
}
