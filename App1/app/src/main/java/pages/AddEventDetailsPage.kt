
package pages

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.app1.EventViewModel
import com.example.app1.Resource
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun EventDetailsPage(navController: NavController) {
    val eventViewModel: EventViewModel = viewModel()
    //  val location: MutableState<LatLng?>


    val eventTypes = listOf("Concert", "Sports Event", "Manifestation", "Natural Disaster")
    var selectedEventType by remember { mutableStateOf("") }
    val eventName = remember { mutableStateOf(TextFieldValue("")) }
    val additionalDetails = remember { mutableStateOf(TextFieldValue("")) }
    val crowdLevel = remember { mutableStateOf(1) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val buttonIsLoading = remember { mutableStateOf(false) }
    val showedAlert = remember { mutableStateOf(false) }
    val eventFlow = eventViewModel?.eventFlow?.collectAsState(initial = null)?.value
    var galleryImages by remember { mutableStateOf<List<Uri>>(emptyList()) }


    // Photo picker launcher
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }
    val location = remember { mutableStateOf<LatLng?>(null) }
    val savedStateHandle = navController.previousBackStackEntry?.savedStateHandle

    // Preuzimanje vrednosti iz SavedStateHandle-a
    LaunchedEffect(Unit) {
        val receivedLocation = savedStateHandle?.get<LatLng>("location")
        location.value = receivedLocation
    }

    val currentUser= Firebase.auth.currentUser
    val userId=currentUser?.uid?:"Unknown"


    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Button(
            onClick = { navController.navigateUp() }, // Navigira unazad
            modifier = Modifier.padding(bottom = 16.dp).align(Alignment.Start)
        ) {
            Text(text = "Back")
        }
        Text(
            text = "Event Details",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFF75553),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Event Type Selection
        Text(text = "Select Event Type", fontWeight = FontWeight.Bold)
        eventTypes.forEach { eventType ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                RadioButton(
                    selected = (selectedEventType == eventType),
                    onClick = { selectedEventType = eventType }
                )
                Text(text = eventType)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Event Name Input
        TextField(
            value = eventName.value,
            onValueChange = { eventName.value = it },
            label = { Text("Event Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Additional Details and Crowd Level for all events
        TextField(
            value = additionalDetails.value,
            onValueChange = { additionalDetails.value = it },
            label = {
                if (selectedEventType == "Natural Disaster") {
                    Text("How did it occur?")
                } else {
                    Text("Describe the situation, crowd...")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Crowd Level (1-5)", fontWeight = FontWeight.Bold)
        Row {
            (1..5).forEach { level ->
                RadioButton(
                    selected = (crowdLevel.value == level),
                    onClick = { crowdLevel.value = level }
                )
                Text(text = level.toString())
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Add Photo
        Box(
            modifier = Modifier
                .size(150.dp)
                .clickable {
                    launcher.launch("image/*")
                },
            contentAlignment = Alignment.Center
        ) {
            if (imageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(imageUri),
                    contentDescription = "Selected Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(text = "Add Photo", color = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Submit Button
        Button(
            onClick = {
                showedAlert.value = false
                buttonIsLoading.value = true
                eventViewModel?.saveEventData(
                    userId=userId,
                    eventName = eventName.value.text,
                    eventType = selectedEventType,
                    description = additionalDetails.value.text,
                    crowd = crowdLevel.value,
                    mainImage = imageUri!!,
                    galleryImages = emptyList(),
                    location =location.value

                )
                navController.previousBackStackEntry?.savedStateHandle?.set(
                    "newMarker",
                    Pair(location.value!!, eventName.value.text)
                )
                navController.navigateUp() // Navigate back
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            if (buttonIsLoading.value) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp))
            } else {
                Text(text = "Submit")
            }

            Spacer(modifier = Modifier.height(8.dp)) // Opcionalno, ako želite dodatni razmak

        }

        eventFlow?.let {
            when (it) {
                is Resource.Failure -> {
                    Log.d("EventFlow", it.toString())
                    buttonIsLoading.value = false
                    val context = LocalContext.current
                    if (!showedAlert.value) {
                        showedAlert.value = true
                        // Display error message (e.g., using Toast)
                        eventViewModel?.getAllEvents()
                    }
                }
                is Resource.Loading -> {
                    // Handle loading state if necessary
                }
                is Resource.Success -> {
                    Log.d("EventFlow", it.toString())
                    buttonIsLoading.value = false
                    val context = LocalContext.current
                    if (!showedAlert.value) {
                        showedAlert.value = true
                        // Display success message (e.g., using Toast)
                        eventViewModel?.getAllEvents()
                    }
                }
                null -> {}
            }
        }
    }
}
