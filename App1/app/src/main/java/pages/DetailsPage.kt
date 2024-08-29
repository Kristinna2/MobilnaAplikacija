package pages

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.app1.views.AuthViewModel
import com.example.app1.Event
import com.example.app1.views.EventViewModel
import com.example.app1.views.EventViewModelFactory
import com.example.app1.views.Marker
import com.example.app1.Rate
import com.example.app1.Resource
import com.example.app1.views.UsersViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import java.math.RoundingMode

@Composable
fun DetailsPage(
    navController: NavController,
    eventViewModel: EventViewModel = viewModel(factory = EventViewModelFactory()),
) {


    val viewModel: AuthViewModel = viewModel()
    val  usersViewModel: UsersViewModel = viewModel() // Inicijalizacija UsersViewModel
    val ratesResources = eventViewModel.rates.collectAsState()
    val newRateResource = eventViewModel.newRate.collectAsState()

    val markerDataJson = navController.previousBackStackEntry
        ?.savedStateHandle
        ?.get<String>("markerData")

    val markerData = Gson().fromJson(markerDataJson, Marker::class.java)



    val event: Event = markerData?.let {
        Event(
            id = it.id,
            userId = it.userId,
            eventName = it.eventName,
            eventType = it.eventType,
            description = it.description,
            crowdLevel = it.crowdLevel,
            mainImage = it.mainImage,
            galleryImages = it.galleryImages,
            location = it.location
        )
    } ?: Event()


    var userName by remember { mutableStateOf("") }

    // Uzimamo userId i dohvatamo ime korisnika
    markerData?.userId?.let { userId ->
        usersViewModel.users.collectAsState().value.find { user -> user.id == userId }?.let { user ->
            userName = "${user.firstName} ${user.lastName}"
        }
    }



    val rates = remember { mutableStateListOf<Rate>() }
    val averageRate = remember { mutableStateOf(0.0) }
    val isLoading = remember { mutableStateOf(false) }
    val showRateDialog = remember { mutableStateOf(false) }
    val myPrice = remember { mutableStateOf(0) }

    LaunchedEffect(event.id) {
        if (event.id.isNotEmpty()) {
            eventViewModel.getEventDetail(event.id)  // Poziv funkcije
        }
    }


    val userId = viewModel.getCurrentUser()?.uid

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
        Spacer(modifier = Modifier.height(16.dp))
        CustomLandmarkRate(average = averageRate.value)

        CustomRateButton(
            enabled = event.userId != viewModel.getCurrentUser()?.uid,
            onClick = {
                val rateExist = rates.firstOrNull {
                    it.eventId == event.id && it.userId == viewModel.getCurrentUser()!!.uid
                }
                if (rateExist != null) {
                    myPrice.value = rateExist.rate
                }
                showRateDialog.value = true
            }
        )

        if (showRateDialog.value) {
            RateDialog(
                showRateDialog = showRateDialog,
                rate = myPrice,
                rateEvent = {
                    val rateExist = rates.firstOrNull {
                        it.eventId == event.id && it.userId == viewModel.getCurrentUser()!!.uid
                    }
                    if (rateExist != null) {
                        isLoading.value = true
                        eventViewModel.updateRate(
                            rid = rateExist.id,
                            rate = myPrice.value
                        )
                    } else {
                        isLoading.value = true
                        eventViewModel.addRate(
                            bid = event.id,
                            rate = myPrice.value,
                            event = event
                        )
                    }
                   updateRatesAndAverage(rates, myPrice.value, rateExist == null, averageRate)
                    addPointsToUser(userId!!, 10)

                },
                isLoading = isLoading,
                onRateConfirmed = { selectedRate ->
                    myPrice.value = selectedRate // Ažuriranje ocene na stranici
                    Log.d("DetailsPage", "Selected Rating: $selectedRate") // Logovanje ocene

                }
            )
        }
    }
    ratesResources.value.let { resource ->
        when (resource) {
            is Resource.Success -> {
                Log.d("DataFetch", "Rates fetched successfully: ${resource.result}")
                rates.clear()  // Clear existing rates
                rates.addAll(resource.result)
                val sum = rates.sumOf { it.rate.toDouble() }
                if (sum != 0.0) {
                    val rawAverage = sum / rates.size
                    averageRate.value = rawAverage.toBigDecimal().setScale(1, RoundingMode.UP).toDouble()
                } else {
                    Log.e("DataError", "No rates available for calculation")
                }
            }

            is Resource.Loading -> {
                // Handle loading state if needed
            }

            is Resource.Failure -> {
                Log.e("DataError", "Failed to fetch rates: ${resource.exception}")
            }
        }
    }


    newRateResource.value.let { resource ->
        when (resource) {
            is Resource.Success -> {
                isLoading.value = false
                val existingRate = rates.firstOrNull { it.id == resource.result }
                if (existingRate != null) {
                    existingRate.rate = myPrice.value
                } else {
                    rates.add(
                        Rate(
                            id = resource.result,
                            rate = myPrice.value,
                            eventId = event.id,
                            userId = viewModel.getCurrentUser()!!.uid
                        )
                    )
                }
                // Recalculate the average rate
                val sum = rates.sumOf { it.rate.toDouble() }
                averageRate.value = sum / rates.size
                Log.d("Rates", "Rates: $rates")
                Log.d("AverageCalculation", "Sum: $sum, Size: ${rates.size}, Average: ${averageRate.value}")
                updateRatesAndAverage(rates, myPrice.value, false, averageRate)

            }

            is Resource.Loading -> {
                // Handle loading state
            }

            is Resource.Failure -> {
                val context = LocalContext.current
                Toast.makeText(context, "Error rating the landmark", Toast.LENGTH_LONG).show()
                isLoading.value = false
            }

            null -> {
                isLoading.value = false
            }
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

fun updateRatesAndAverage(
    rates: MutableList<Rate>,
    newRate: Int,
    isNewRate: Boolean,
    averageRate: MutableState<Double>
) {
    if (isNewRate) {
        rates.add(Rate(rate = newRate))
    } else {
        rates.find { it.rate == newRate }?.rate = newRate
    }

    val sum = rates.sumOf { it.rate.toDouble() }
    val average = if (sum > 0) {
        (sum / rates.size).toBigDecimal().setScale(1, RoundingMode.UP).toDouble()
    } else {
        0.0
    }
    averageRate.value = average
}
@Composable
fun CustomRateButton(
    onClick: () -> Unit,
    enabled: Boolean
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(Color(0xFF6200EA), RoundedCornerShape(30.dp)),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF6200EA),
            contentColor = Color.Black,
            disabledContainerColor = Color(0xFFD3D3D3),
            disabledContentColor = Color.White
        ),
    ) {
        Text(
            "Rate landmark",
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
fun CustomLandmarkRate(
    average: Number
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = "",
            tint = Color.Yellow
        )
        Spacer(modifier = Modifier.width(5.dp))
        inputTextIndicator(textValue = "$average / 5")
    }
}

@Composable
fun inputTextIndicator(textValue: String) {
    Text(
        style = TextStyle(
            color = Color.Red,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        ),
        text = textValue
    )
}
fun addPointsToUser(userId: String, points: Int) {
    val db = FirebaseFirestore.getInstance()
    val userRef = db.collection("users").document(userId)

    userRef.get()
        .addOnSuccessListener { document ->
            if (document != null) {
                val currentPoints = document.getLong("points") ?: 0
                val newPoints = currentPoints + points

                userRef.update("points", newPoints)
                    .addOnSuccessListener {
                        Log.d("Firebase", "Points successfully updated!")
                    }
                    .addOnFailureListener { e ->
                        Log.w("Firebase", "Error updating points", e)
                    }
            } else {
                Log.d("Firebase", "No such document")
            }
        }
        .addOnFailureListener { exception ->
            Log.d("Firebase", "get failed with ", exception)
        }
}
