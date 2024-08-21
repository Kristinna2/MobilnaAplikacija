package pages

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.app1.AuthState
import com.example.app1.AuthViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory
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

    val currentLocation = remember { mutableStateOf<LatLng?>(null) }
    val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    val locationRequest = com.google.android.gms.location.LocationRequest.create().apply {
        interval = 10000
        fastestInterval = 5000
        priority = com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
    }

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
            println("Location permission not granted")
        }
    }

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Unauthenticated -> navController.navigate("login")
            else -> Unit
        }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(0.0, 0.0), 15f)
    }

    LaunchedEffect(currentLocation.value) {
        currentLocation.value?.let {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 15f)
        }
    }

    val markers = remember { mutableStateListOf<Pair<LatLng, String>>() }
    var selectedMarker by remember { mutableStateOf<LatLng?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    val markerName = remember { mutableStateOf(TextFieldValue("")) }

    var expanded by remember { mutableStateOf(false) }

    // Search bar state
    val searchQuery = remember { mutableStateOf(TextFieldValue("")) }

    // Filter buttons state
    var selectedColor by remember { mutableStateOf<Color?>(null) }

    if (showDialog) {
        MarkerNameDialog(
            markerName = markerName,
            onDismiss = { showDialog = false },
            onConfirm = {
                markers.add(Pair(selectedMarker!!, markerName.value.text))
                showDialog = false
            },
            onOpen = {
                markerName.value = TextFieldValue("")// Resetovanje unosa pri otvaranju dijaloga
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFC9E2F3)) // Pozadina aplikacije na svetlu plavu boju
            .padding(16.dp)
    ) {
        // Raspored za naslov i ikonu
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Trace the Events",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold, // Podebljani font za naglašavanje
                color = Color(0xFFF75553), // Tamno plava boja za tekst
                modifier = Modifier
                    .padding(vertical = 16.dp) // Povećana vertikalna margina za bolji razmak
            )

            Box(
                modifier = Modifier
                    .wrapContentSize(Alignment.TopEnd)
            ) {
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More options",
                        modifier = Modifier.size(24.dp),
                        tint = Color.Blue // Boja ikone
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .background(Color(0xFFF75553)) // Boja pozadine menija
                ) {
                    DropdownMenuItem(
                        text = { Text("User Profile", color = Color.White) },
                        onClick = {
                            expanded = false
                            navController.navigate("user_profile")
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("All Users", color = Color.White) },
                        onClick = {
                            expanded = false
                            navController.navigate("all_users")
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Sign out", color = Color.White) },
                        onClick = {
                            expanded = false
                            authViewModel.signout()
                        }
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .weight(1f) // Povećaj visinu mape
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                onMapClick = { latLng ->
                    selectedMarker = latLng
                    showDialog = true
                }
            ) {
                currentLocation.value?.let {
                    Marker(
                        state = MarkerState(position = it),
                        title = "My Location",
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                    )
                }
                markers.forEach { (location, name) ->
                    Marker(
                        state = MarkerState(position = location),
                        title = name,
                        onInfoWindowClick = {
                            Log.d("HomePage", "Clicked on marker: $name")
                            markers.remove(Pair(location, name))
                        }
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        // Dodaj akciju za dugme "Filter"
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp) // Razmak između dugmadi
                        .size(60.dp), // Okruglo dugme
                    shape = RoundedCornerShape(30.dp), // Okrugli oblik
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF75553) // Boja pozadine dugmeta
                    )
                ) {
                    Text(
                        text = "Filter",
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }

                Button(
                    onClick = {
                        navController.navigate("event_details")
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp, end = 8.dp) // Razmak između dugmadi
                        .size(60.dp), // Okruglo dugme
                    shape = RoundedCornerShape(30.dp), // Okrugli oblik
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF75553) // Boja pozadine dugmeta
                    )
                ) {
                    Text(
                        text = "Dodaj",
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }

                Button(
                    onClick = {
                        // Navigacija do stranice za lokaciju
                        navController.navigate("location_service")
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp) // Razmak između dugmadi
                        .size(60.dp), // Okruglo dugme
                    shape = RoundedCornerShape(30.dp), // Okrugli oblik
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF75553) // Boja pozadine dugmeta
                    )
                ) {
                    Text(
                        text = "Location",
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun MarkerNameDialog(
    markerName: MutableState<TextFieldValue>,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    onOpen: () -> Unit // Funkcija za resetovanje unosa
) {
    LaunchedEffect(Unit) {
        onOpen()
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Enter Marker Name") },
        text = {
            TextField(
                value = markerName.value,
                onValueChange = { markerName.value = it },
                label = { Text("Marker Name") }
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
