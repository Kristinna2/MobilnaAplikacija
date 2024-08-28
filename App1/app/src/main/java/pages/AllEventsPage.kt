    package pages

    import android.util.Log
    import androidx.compose.foundation.background
    import androidx.compose.foundation.layout.Arrangement
    import androidx.compose.foundation.layout.Box
    import androidx.compose.foundation.layout.Column
    import androidx.compose.foundation.layout.PaddingValues
    import androidx.compose.foundation.layout.Spacer
    import androidx.compose.foundation.layout.fillMaxSize
    import androidx.compose.foundation.layout.fillMaxWidth
    import androidx.compose.foundation.layout.height
    import androidx.compose.foundation.layout.padding
    import androidx.compose.foundation.lazy.LazyColumn
    import androidx.compose.foundation.shape.RoundedCornerShape
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
    import androidx.compose.ui.unit.dp
    import androidx.compose.ui.unit.sp
    import androidx.lifecycle.viewmodel.compose.viewModel
    import androidx.navigation.NavController
    import com.example.app1.Event
    import com.example.app1.EventViewModel
    import com.example.app1.Resource

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
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        val filteredEvents = eventList.filter { it.eventName.isNotEmpty() }
                        items(filteredEvents.size) { index ->
                            val event = filteredEvents[index]
                            Log.d("AllEventsPage", "Event: $event")
                            EventItem(event = event, navController = navController, eventViewModel = eventViewModel)
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
    fun EventItem(event: Event, navController: NavController, eventViewModel: EventViewModel) {
        var selectedEventId by remember { mutableStateOf<String?>(null) }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = event.eventName ?: "Unnamed Event",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Crowd Level: ${event.crowdLevel ?: "Unknown"}", // Adjust according to your data model
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    fontSize = 14.sp
                )

                // Dodaj dugme "Show on Map"
                Button(
                    onClick = {
                        Log.d("EventItem", "Selected Event: $event")
                        navController.currentBackStackEntry?.savedStateHandle?.set("eventId", event.id)
                        selectedEventId = event.id // Postavljamo selektovani ID
                    },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(text = "Show on Map")
                }
            }
        }

        LaunchedEffect(selectedEventId) {
            selectedEventId?.let {
                navController.navigate("home")
            }
        }
    }
