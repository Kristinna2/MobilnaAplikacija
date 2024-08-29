package pages

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.app1.AuthState
import com.example.app1.AuthViewModel
import com.example.app1.Event
import com.example.app1.EventViewModel
import com.example.app1.MarkerViewModel
import com.example.app1.Resource
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.gson.Gson
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

// Data class for markers


@SuppressLint("MissingPermission")
@Composable
fun HomePage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {

    val context = LocalContext.current
    val authState = authViewModel.authState.observeAsState()
    val eventViewModel: EventViewModel = viewModel()
    val markerViewModel: MarkerViewModel = viewModel(
        factory = MarkerViewModelFactory(context)
    )

    var newMarker by remember { mutableStateOf<Pair<LatLng, String>?>(null) }

    val currentLocation = remember { mutableStateOf<LatLng?>(null) }
    val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    val locationRequest = com.google.android.gms.location.LocationRequest.create().apply {
        interval = 10000
        fastestInterval = 5000
        priority = com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
    }

   // val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle

    // Handle new markers
    LaunchedEffect(Unit) {
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<Pair<LatLng, String>>("newMarker")
            ?.observeForever { newMarker ->
                newMarker?.let {
                    markerViewModel.addMarker(it.first.latitude, it.first.longitude, it.second) // Add marker to ViewModel
                }
            }
    }

   // val markers by homeViewModel.markers.collectAsState(initial = emptyList())
    val markers by markerViewModel.markers.collectAsState()

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, object : com.google.android.gms.location.LocationCallback() {
                override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
                    super.onLocationResult(locationResult)
                    locationResult.locations.forEach { location ->
                        Log.d("HomePage", "Updated Location: ${location.latitude}, ${location.longitude}")
                    currentLocation.value = LatLng(location.latitude, location.longitude)                    }
                  //    currentLocation.value = LatLng(37.3500000, -122.1500000)}

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

  //  val markers = remember { mutableStateListOf<Pair<LatLng, String>>() }
    var selectedMarker by remember { mutableStateOf<LatLng?>(null) }
    val markerName = remember { mutableStateOf(TextFieldValue("")) }

    var expanded by remember { mutableStateOf(false) }

    // Search bar state
    val searchQuery = remember { mutableStateOf(TextFieldValue("")) }

    // Filter buttons state
    var selectedColor by remember { mutableStateOf<Color?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    var isFilterButtonPressed by remember { mutableStateOf(false) }
    var isMarkerButtonPressed by remember { mutableStateOf(false) }

    var showFilterDialog by remember { mutableStateOf(false) }

    if (showFilterDialog) {
        EventFilterDialog(
            onDismiss = { showFilterDialog = false }
        )
    }
    if (showDialog) {
        MarkerNameDialog(
            markerName = markerName,
            onDismiss = { showDialog = false },
            onConfirm = {
                selectedMarker?.let {
                    markerViewModel.addMarker(it.latitude, it.longitude, markerName.value.text)
                }
                showDialog = false
            },
            onOpen = {
                markerName.value = TextFieldValue("")// Resetovanje unosa pri otvaranju dijaloga
            }
        )
    }
    var selectedMapStyle by remember { mutableStateOf<MapStyleOptions?>(null) }
    //selectedMapStyle = MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style)
    // Recenter Button State
    val recenterMap = {
        currentLocation.value?.let {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 15f)
        }
    }
//DODATO
    val filteredMarkers by markerViewModel.filteredMarkers.observeAsState(emptyList())
    val isFilterApplied by markerViewModel.isFilterApplied.observeAsState(false) // Da li je filter primenjen

    val currentUser = authViewModel.getCurrentUser()

    val eventsState by eventViewModel.events.collectAsState()
    var selectedEvent by remember { mutableStateOf<Event?>(null) }

    // Preuzmite ID događaja iz savedStateHandle
    val eventId = navController.currentBackStackEntry?.savedStateHandle?.get<String>("eventId")

    Log.d("HomePage", "eventId: $eventId")
    LaunchedEffect(eventId) {
        if (eventId != null) {
            // Učitajte podatke vezane za eventId
            Log.d("HomePage", "Loading data for Event ID: $eventId")
        }
    }

    if (eventId != null) {
        when (eventsState) {
            is Resource.Success -> {
                selectedEvent = (eventsState as Resource.Success).result.find { event -> event.id == eventId }
            }
            is Resource.Loading -> {
                // Prikazivanje loading indikatora
                CircularProgressIndicator()
            }
            is Resource.Failure -> {
                // Prikazivanje poruke o grešci
                val errorMessage = (eventsState as Resource.Failure).exception.message
                Text(text = "Greška: $errorMessage", color = Color.Red)
            }
        }
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
                            currentUser?.let {
                                Log.d("HomePage", "Navigating to User Profile with userId: ${it.uid}")

                                navController.navigate("user_profile/${it.uid}")
                            }
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
                        text = { Text("All Events", color = Color.White) },
                        onClick = {
                            expanded = false
                            navController.navigate("allevents") // Nova ruta za navigaciju
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

        Button(
            onClick = {
                markerViewModel.resetFilter() // Resetuje filter
            },
            modifier = Modifier
                .fillMaxWidth() // Puni širinu
                .padding(vertical = 8.dp), // Razmak oko dugmeta
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFF75553) // Boja pozadine dugmeta
            )
        ) {
            Text(
                text = "Reset Filter",
                fontSize = 16.sp,
                color = Color.White
            )
        }
            // Search bar

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .weight(1f) // Povećaj visinu mape
            ) {
                TextField(
                    value = searchQuery.value.text,
                    onValueChange = { newValue ->
                        searchQuery.value = TextFieldValue(newValue)
                    },
                    placeholder = { Text(text = "Search...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .align(Alignment.TopCenter)
                        .background(Color.White, RoundedCornerShape(8.dp)) // Dodaj pozadinsku boju i zaobljenje
                        .border(1.dp, Color.Gray, RoundedCornerShape(8.dp)) // Dodaj border za bolju vidljivost
                        .zIndex(1f), // Postavi TextField iznad mape
                    maxLines = 1,
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        cursorColor = Color.Black,
                        focusedPlaceholderColor = Color.Gray,
                        unfocusedPlaceholderColor = Color.Gray,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
                val markersToDisplay = if (isFilterApplied) {
                    filteredMarkers // Prikazujemo filtrirane markere
                } else {
                    markers // Prikazujemo sve markere
                }

                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    onMapClick = { latLng ->
                        if (!isFilterButtonPressed) {
                            selectedMarker = latLng
                            showDialog = true
                            isMarkerButtonPressed=true
                        }
                    }
                ) {
                    currentLocation.value?.let {
                        Circle(
                            center = it,
                            radius = 50.0, // Promenite veličinu po potrebi
                            fillColor = Color.Blue.copy(alpha = 0.5f), // Boja kružića
                            strokeColor = Color.Blue,
                            strokeWidth = 2f
                        )
                        Marker(
                            state = MarkerState(position = it),
                            title = "My Location",
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                        )
                    }

                    markersToDisplay.filter { it.eventName.contains(searchQuery.value.text, ignoreCase = true) }
                        .forEach { marker ->
                            Marker(
                                state = MarkerState(
                                    position = LatLng(
                                        marker.location.latitude,
                                        marker.location.longitude
                                    )
                                ),
                                title = marker.eventName,
                                icon = if (selectedColor == null || selectedColor == Color.Red) {
                                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                                } else {
                                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                                },
                                onClick = {
                                    it.showInfoWindow()

                                    val markerJson = Gson().toJson(marker)
                                    navController.currentBackStackEntry?.savedStateHandle?.set(
                                        "markerData",
                                        markerJson
                                    )
                                      navController.navigate("details")
                                    true
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

                        isMarkerButtonPressed=false
                            isFilterButtonPressed = true
                            showDialog = true


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
                if (showDialog && !isMarkerButtonPressed) {

                        EventFilterDialog(
                            onDismiss = { showDialog = false },
                            eventViewModel,
                            usersViewModel = viewModel()
                        )

                }
                Button(
                    onClick = {
                        val location = currentLocation.value
                        if (location != null) {
                            navController.currentBackStackEntry?.savedStateHandle?.set("location", location)
                            navController.navigate("event_details")
                        }
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





class MarkerViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MarkerViewModel::class.java)) {
            return MarkerViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}