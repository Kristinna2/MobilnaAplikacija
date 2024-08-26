package pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.app1.EventViewModel
import com.example.app1.EventViewModelFactory
import com.example.app1.Marker
import com.example.app1.UsersViewModel
import com.google.gson.Gson

@Composable
fun DetailsPage(
    navController: NavController,
    eventViewModel: EventViewModel = viewModel(factory = EventViewModelFactory()),
) {
    val markerDataJson = navController.previousBackStackEntry
        ?.savedStateHandle
        ?.get<String>("markerData")

    val markerData = Gson().fromJson(markerDataJson, Marker::class.java)

    val  usersViewModel: UsersViewModel = viewModel() // Inicijalizacija UsersViewModel

    var userName by remember { mutableStateOf("") }

    // Uzimamo userId i dohvatamo ime korisnika
    markerData?.userId?.let { userId ->
        usersViewModel.users.collectAsState().value.find { user -> user.id == userId }?.let { user ->
            userName = "${user.firstName} ${user.lastName}"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFBCE6F6))
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = { navController.navigateUp() },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF75553))
        ) {
            Text(text = "Back", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        markerData?.let { marker ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = marker.eventName,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF75553),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Prikaz imena korisnika umesto userId
                    InfoBox(label = "User", value = userName)
                    InfoBox(label = "Event Type", value = marker.eventType)
                    InfoBox(label = "Description", value = marker.description)
                    InfoBox(label = "Crowd Level", value = marker.crowdLevel.toString())

                    if (marker.mainImage.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(marker.mainImage)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Main Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .background(Color.Gray)
                        )
                    }

                    if (marker.galleryImages.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Gallery Images:",
                            color = Color.Black,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        marker.galleryImages.forEach { imageUrl ->
                            Spacer(modifier = Modifier.height(8.dp))
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(imageUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Gallery Image",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .background(Color.Gray)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    InfoBox(label = "Latitude", value = marker.location.latitude.toString())
                    InfoBox(label = "Longitude", value = marker.location.longitude.toString())
                }
            }
        } ?: run {
            Text(
                text = "No event data available",
                color = Color.Red,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun InfoBox(label: String, value: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(Color(0xFFF1F1F1))
            .padding(8.dp)
    ) {
        Text(
            text = "$label: $value",
            color = Color.Black,
            fontSize = 16.sp
        )
    }
}