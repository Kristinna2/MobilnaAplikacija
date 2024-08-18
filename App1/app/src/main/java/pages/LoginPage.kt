package pages

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.app1.AuthState
import com.example.app1.AuthViewModel

@Composable
fun LoginPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Authenticated -> navController.navigate("home")
            is AuthState.Error -> Toast.makeText(
                context,
                (authState.value as AuthState.Error).message, Toast.LENGTH_SHORT
            ).show()
            else -> Unit
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFbce6f6)) // Updated background color
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Welcome to UrbanAlert",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFF75553),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 24.dp) // Add padding below the title
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(text = "Email", fontSize = 18.sp) },
            leadingIcon = { androidx.compose.material3.Icon(Icons.Filled.Email, contentDescription = "Email Icon", tint = Color(0xFFF75553), modifier = Modifier.size(20.dp)) },
            shape = RoundedCornerShape(16.dp),

            modifier = Modifier
                .width(265.dp)
                .height(60.dp) // Reduced height
                .border(2.dp, Color(0xFF2589a0))
                .background(Color(0xFF2589a0)), // Background color for text field
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 16.sp), // Reduced font size

        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(text = "Password", fontSize = 18.sp) },
            visualTransformation = PasswordVisualTransformation(),
            leadingIcon = { androidx.compose.material3.Icon(Icons.Filled.Lock, contentDescription = "Password Icon", tint = Color(0xFFF75553), modifier = Modifier.size(20.dp)) },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .width(265.dp)
                .height(60.dp) // Reduced height
                .background(Color(0xFF2589a0)), // Background color for text field
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 16.sp), // Reduced font size

        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { authViewModel.login(email, password) },
            enabled = authState.value != AuthState.Loading,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .width(160.dp)
                .height(48.dp), // Reduced height
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color(0xFFF75553))
        ) {
            Text(text = "LOG IN", fontSize = 18.sp, color = Color.White) // Reduced font size
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = { navController.navigate("signup") },
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(text = "Don't have an account? Register!", fontSize = 18.sp, color = Color(0xFF2589a0)) // Reduced font size
        }
    }
}
