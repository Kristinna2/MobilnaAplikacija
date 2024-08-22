package pages



import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.gson.Gson

//import data.MarkerData

@Composable
fun DetailsPage(navController: NavController) {
    // Retrieve JSON string from the previous screen
    val markerDataJson = navController.previousBackStackEntry
        ?.savedStateHandle
        ?.get<String>("markerData")

    // Parse JSON string to MarkerData object
    val markerData = Gson().fromJson(markerDataJson, MarkerData::class.java)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Button(
            onClick = { navController.navigateUp() },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text(text = "Back", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = markerData?.name ?: "Unknown event",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Red,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Latitude: ${markerData?.latitude}",
            color = Color.White,
            fontSize = 16.sp
        )
        Text(
            text = "Longitude: ${markerData?.longitude}",
            color = Color.White,
            fontSize = 16.sp
        )

        // Additional details, images, etc., can be added here
    }
}