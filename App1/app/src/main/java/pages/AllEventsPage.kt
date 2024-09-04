package pages

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.app1.Event
import com.example.app1.views.EventViewModel
import com.example.app1.Resource

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AllEventsPage(navController: NavController) {

    val eventViewModel: EventViewModel = viewModel()
    val events by eventViewModel.events.collectAsState()

    LaunchedEffect(Unit) {
        eventViewModel.getAllEvents()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color(0xFFF5F5F5))
    ) {
        Text(
            text = "All Events",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 16.dp)
        )

        when (events) {
            is Resource.Success -> {
                val eventList = (events as Resource.Success<List<Event>>).result
                    .filter { it.eventName?.isNotBlank() == true }

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(eventList) { event ->
                        EventRow(event = event, navController = navController)
                    }
                }
            }
            is Resource.Failure -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Failed to load events.",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            else -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.Blue)
                }
            }
        }
    }
}

@Composable
fun EventRow(event: Event, navController: NavController) {
    var selectedEventId by remember { mutableStateOf<String?>(null) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = CardDefaults.outlinedShape,
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val painter = rememberAsyncImagePainter(event.mainImage)
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(Color.Gray)
            ) {
                androidx.compose.foundation.Image(
                    painter = painter,
                    contentDescription = "Event Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Text(
                text = event.eventName ?: "Unnamed Event",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = Color.Black,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .widthIn(min = 160.dp)
                    .padding(end = 8.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = event.description ?: "No description available",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray,
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .widthIn(min = 160.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "Crowd: ${event.crowdLevel ?: "Unknown"}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .widthIn(min = 160.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Button(
                onClick = {
                    Log.d("EventRow", "Selected Event: $event")
                    navController.currentBackStackEntry?.savedStateHandle?.set("eventId", event.id)
                    selectedEventId = event.id
                    navController.navigate("home")

                },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(text = "Search on Map")
            }
        }
    }

    LaunchedEffect(selectedEventId) {
        selectedEventId?.let {
            navController.navigate("home")
        }
    }
}