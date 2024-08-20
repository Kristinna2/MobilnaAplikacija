package pages

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.app1.LocationService

@Composable
fun LocationServicePage(modifier: Modifier = Modifier, navController: NavController) {
    val context = LocalContext.current
    val locationState = remember { mutableStateOf("Location not available") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color(0xFFC9E2F3)) // Pozadina stranice
            .clip(RoundedCornerShape(16.dp)), // Okrugli uglovi
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Dugme "Nazad"
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
        }

        // Naslov
        Text(
            text = "Do you want to get updated every moment?",
            fontSize = 24.sp,
            modifier = Modifier.padding(vertical = 16.dp) // Razmak iznad i ispod naslova
        )

        // Prikaz ažurirane lokacije
        Text(
            text = locationState.value,
            fontSize = 18.sp,
            modifier = Modifier.padding(vertical = 16.dp) // Razmak iznad i ispod lokacije
        )

        // Dugmad "Yes" i "No"
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp) // Razmak između dugmadi
        ) {
            Button(
                onClick = {
                    startLocationService(context)
                },
                modifier = Modifier.padding(horizontal = 16.dp) // Razmak sa strane dugmadi
            ) {
                Text(text = "Yes", fontSize = 18.sp)
            }
            Button(
                onClick = {
                    stopLocationService(context)
                },
                modifier = Modifier.padding(horizontal = 16.dp) // Razmak sa strane dugmadi
            ) {
                Text(text = "No", fontSize = 18.sp)
            }
        }
    }
}

private fun startLocationService(context: Context) {
    Log.d("LocationService", "Starting location service")
    val serviceIntent = Intent(context, LocationService::class.java)
    ContextCompat.startForegroundService(context, serviceIntent)

    // Prikazivanje obaveštenja
    Toast.makeText(context, "Location service started", Toast.LENGTH_SHORT).show()
}

private fun stopLocationService(context: Context) {
    val serviceIntent = Intent(context, LocationService::class.java)
    context.stopService(serviceIntent)

    // Prikazivanje obaveštenja
    Toast.makeText(context, "Location service stopped", Toast.LENGTH_SHORT).show()
}
