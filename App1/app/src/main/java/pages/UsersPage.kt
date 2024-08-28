package pages

import android.util.Log
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.app1.Event
import com.example.app1.EventViewModel
import com.example.app1.User
import com.example.app1.UsersViewModel

@Composable
fun UsersPage(
    viewModel: UsersViewModel = viewModel(),
    navController: NavController // Add the NavController as a parameter
) {
    val usersState = viewModel.users.collectAsState()
    val eventsState = remember { mutableStateMapOf<String, List<Event>>() }
   val eventViewModel: EventViewModel = viewModel() // Make sure this is the correct ViewModel

    val sortedUsers = usersState.value.sortedByDescending { it.points }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Users List",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2589a0)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // LazyColumn for displaying the list of users
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(sortedUsers) { user ->
                UserItem(
                    user = user,
                   isTopUser = sortedUsers.indexOf(user) < 3,
                    onFetchEvents = { userId ->
                        // Fetch events for the selected user
                        eventViewModel.filterEventsByUserId(userId) { events ->
                            eventsState[userId] = events
                        }
                    },
                    navController = navController
                )
                eventsState[user.id]?.let { events ->
                    EventsList(events)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Back button to navigate to HomePage
        Button(
            onClick = { navController.navigate("home") },
            shape = CircleShape,
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color(0xFFF75553)),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(180.dp)
        ) {
            Text(text = "Back", fontSize = 18.sp, color = Color.White)
        }
    }
}

@Composable
fun UserItem(user: User, onFetchEvents: (String) -> Unit,    isTopUser: Boolean,
             navController:NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Display the profile picture if available
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(Color(0xFFF5F5F5), shape = CircleShape)
        ) {
            if (user.photoUrl != null) {
                AsyncImage(
                    model = user.photoUrl,
                    contentDescription = "Profile Picture",
                    modifier = Modifier.size(60.dp)
                )
            } else {
                Text(
                    text = "No Image",
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = "${user.firstName} ${user.lastName}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Phone: ${user.phoneNumber}",
                fontSize = 14.sp
            )
            Text(
                text = "Points: ${user.points}",
                fontSize = 14.sp,
                color = if (isTopUser) Color.Green else Color.White
            )
            // Button to fetch events for the user
            Button(onClick = { onFetchEvents(user.id) }) {
                Text(text = "Show Events")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                val userId = user.id
                if (userId != null) {
                    navController.navigate("user_profile/$userId")
                } else {
                    Log.e("UserItem", "User ID is null")
                }
            }) {
                Text(text = "Show Profile")
            }
        }
    }
}

@Composable
fun EventsList(events: List<Event>) {
    Column(modifier = Modifier.padding(start = 16.dp)) {
        Text(
            text = "Events:",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        for (event in events) {
            Text(
                text = event.eventName,
                fontSize = 14.sp,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}
