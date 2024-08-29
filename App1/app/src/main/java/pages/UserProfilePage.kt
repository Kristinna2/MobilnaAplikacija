package pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.app1.views.AuthViewModel
import com.example.app1.Event
import com.example.app1.views.EventViewModel
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun UserProfilePage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    userId: String?
) {
    // State variables for user data
    var firstName by remember { mutableStateOf<String?>(null) }
    var lastName by remember { mutableStateOf<String?>(null) }
    var phoneNumber by remember { mutableStateOf<String?>(null) }
    var photoUrl by remember { mutableStateOf<String?>(null) }
  //  var totalRatings by remember { mutableStateOf(0) } // Total ratings sum

    var points by remember { mutableStateOf<Int?>(null) }

    val eventViewModel: EventViewModel = viewModel()
    var events by remember { mutableStateOf<List<Event>>(emptyList()) } // List of user's events

    // Fetch user data and ratings
    LaunchedEffect(userId) {
        userId?.let { id ->
            // Fetch user data
            val userDocument = FirebaseFirestore.getInstance().collection("users").document(id)
            userDocument.get().addOnSuccessListener { document ->
                if (document != null) {
                    firstName = document.getString("firstName")
                    lastName = document.getString("lastName")
                    phoneNumber = document.getString("phoneNumber")
                    photoUrl = document.getString("photoUrl")
                    points = document.getLong("points")?.toInt()

                }
            }

            // Fetch events for the user
            eventViewModel.filterEventsByUserId(id) { userEvents ->
                events = userEvents
            }


        }
    }

    // Add vertical scroll
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFbce6f6))
            .padding(16.dp)
            .verticalScroll(scrollState), // Enable scrolling
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "User Profile",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2589a0)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Display the profile picture if available
        Box(
            modifier = Modifier
                .size(150.dp)
                .background(Color(0xFFF5F5F5), shape = CircleShape)
        ) {
            if (photoUrl != null) {
                AsyncImage(
                    model = photoUrl,
                    contentDescription = "Profile Picture",
                    modifier = Modifier.size(150.dp)
                )
            } else {
                Text(
                    text = "No Image",
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Display user's name and other details
        Text(
            text = "Name: ${firstName ?: "Loading..."} ${lastName ?: ""}",
            fontSize = 24.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Phone: ${phoneNumber ?: "Loading..."}",
            fontSize = 24.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))


        Text(
            text = "Points: ${points ?: "Nije bilo interakcije"}",
            fontSize = 24.sp,
            color = Color.Red
        )

        Spacer(modifier = Modifier.height(32.dp))

        Spacer(modifier = Modifier.height(32.dp))

        // Display user's events
        Text(
            text = "User's Events:",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2589a0)
        )

        Spacer(modifier = Modifier.height(16.dp))

        events.forEach { event ->
            Column(
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(text = "Name: ${event.eventName}", fontSize = 20.sp, color = Color.Black)
                Text(text = "Description: ${event.description}", fontSize = 16.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                authViewModel.signout()
                navController.navigate("login") {
                    popUpTo("home") { inclusive = true }
                }
            },
            modifier = Modifier.size(width = 180.dp, height = 48.dp),
            shape = CircleShape,
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color(0xFFF75553))
        ) {
            Text(text = "Log Out", fontSize = 18.sp, color = Color.White)
        }

        Spacer(modifier = Modifier.height(14.dp))

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
