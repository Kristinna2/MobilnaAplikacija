package pages

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.app1.User
import com.example.app1.UsersViewModel

@Composable
fun UsersPage(
    viewModel: UsersViewModel = viewModel(),
    navController: NavController // Add the NavController as a parameter
) {
    val usersState = viewModel.users.collectAsState()

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

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(usersState.value) { user ->
                UserItem(user)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Add a Back button that navigates to the HomePage
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
fun UserItem(user: User) {
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
        }
    }
}
