package pages

//import com.example.App1.AuthState

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.app1.AuthState
import com.example.app1.AuthViewModel

@Composable
fun RegisterPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("+381") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        photoUri = uri
    }

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
            .background(Color(0xFFbce6f6)) // Light pastel cyan background
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Register!", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2589a0)) // Vibrant Blue title

        Spacer(modifier = Modifier.height(24.dp)) // Veći razmak između naslova i slike

        // Box za odabir slike
        Box(
            modifier = Modifier
                .size(100.dp) // Square size
                .clip(RoundedCornerShape(12.dp)) // Rounded corners
                .background(Color(0xFFF5F5F5)) // Very light gray background
                .border(2.dp, Color(0xFFF75553)) // Vibrant Blue border
        ) {
            photoUri?.let {
                AsyncImage(
                    model = it,
                    contentDescription = "Selected Photo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } ?: run {
                Text(
                    text = "Select Photo!",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(8.dp),
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp)) // Veći razmak između slike i dugmeta

        Button(
            onClick = {
                launcher.launch("image/*")
            },
            shape = RoundedCornerShape(12.dp),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color(0xFF2589a0)) // Light Blue button
        ) {
            Text(text = "Select Photo", fontSize = 18.sp, color = Color.White)
        }

        Spacer(modifier = Modifier.height(24.dp)) // Veći razmak između dugmeta i polja za unos

        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text(text = "First Name", fontSize = 18.sp) },
            leadingIcon = { androidx.compose.material3.Icon(Icons.Filled.Person, contentDescription = "First Name Icon", tint = Color(0xFF2589a0), modifier = Modifier.size(20.dp)) },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .width(265.dp)
                .height(60.dp),
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 16.sp)
        )

        Spacer(modifier = Modifier.height(16.dp)) // Veći razmak između polja

        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text(text = "Last Name", fontSize = 18.sp) },
            leadingIcon = { androidx.compose.material3.Icon(Icons.Filled.Person, contentDescription = "Last Name Icon", tint = Color(0xFF2589a0), modifier = Modifier.size(20.dp)) },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .width(265.dp)
                .height(60.dp), // Smanjena visina polja
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 16.sp)
        )

        Spacer(modifier = Modifier.height(18.dp)) // Veći razmak između polja

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(text = "Email", fontSize = 18.sp) },
            leadingIcon = { androidx.compose.material3.Icon(Icons.Filled.Email, contentDescription = "Email Icon", tint = Color(0xFF2589a0), modifier = Modifier.size(20.dp)) },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .width(265.dp)
                .height(60.dp), // Smanjena visina polja
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 16.sp)
        )

        Spacer(modifier = Modifier.height(18.dp)) // Veći razmak između polja

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(text = "Password", fontSize = 18.sp) },
            visualTransformation = PasswordVisualTransformation(),
            leadingIcon = { androidx.compose.material3.Icon(Icons.Filled.Lock, contentDescription = "Password Icon", tint = Color(0xFF2589a0), modifier = Modifier.size(20.dp)) },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .width(265.dp)
                .height(60.dp), // Smanjena visina polja
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 16.sp)
        )

        Spacer(modifier = Modifier.height(16.dp)) // Veći razmak između polja

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = {
                if (it.startsWith("+381") || it.isEmpty()) {
                    phoneNumber = it
                }
            },
            label = { Text(text = "Phone Number", fontSize = 18.sp) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            leadingIcon = { androidx.compose.material3.Icon(Icons.Filled.Phone, contentDescription = "Phone Number Icon", tint = Color(0xFF2589a0), modifier = Modifier.size(20.dp)) },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .width(265.dp)
                .height(60.dp), // Smanjena visina polja
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 16.sp),
            isError = !isValidPhoneNumber(phoneNumber)
        )

        Spacer(modifier = Modifier.height(32.dp)) // Veći razmak pre dugmeta

        Button(
            onClick = {
                if (validateForm(context, firstName, lastName, email, password, phoneNumber)) {
                    authViewModel.signup(firstName, lastName, email, password, phoneNumber, photoUri)
                }
            },
            enabled = authState.value != AuthState.Loading,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .width(180.dp)
                .height(48.dp), // Smanjena visina dugmeta
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color(0xFFF75553)) // Light Blue button
        ) {
            Text(text = "Create Account", fontSize = 18.sp, color = Color.White)
        }

     //   Spacer(modifier = Modifier.height(14.dp)) // Veći razmak pre tekst dugmeta

        TextButton(onClick = {
            navController.navigate("login")
        }) {
            Text(text = "Already have an account? Log in", fontSize = 18.sp, color = Color(0xFFF75553)) // Vibrant Blue text
        }
    }
}
fun validateForm(context: Context, firstName: String, lastName: String, email: String, password: String, phoneNumber: String): Boolean {
    return when {
        firstName.isBlank() -> {
            Toast.makeText(context, "First Name is required", Toast.LENGTH_SHORT).show()
            false
        }
        lastName.isBlank() -> {
            Toast.makeText(context, "Last Name is required", Toast.LENGTH_SHORT).show()
            false
        }
        email.isBlank() -> {
            Toast.makeText(context, "Email is required", Toast.LENGTH_SHORT).show()
            false
        }
        password.isBlank() -> {
            Toast.makeText(context, "Password is required", Toast.LENGTH_SHORT).show()
            false
        }
        phoneNumber.isBlank() -> {
            Toast.makeText(context, "Phone Number is required", Toast.LENGTH_SHORT).show()
            false
        }
        !isValidPhoneNumber(phoneNumber) -> {
            Toast.makeText(context, "Invalid Phone Number", Toast.LENGTH_SHORT).show()
            false
        }
        else -> true
    }
}

fun isValidPhoneNumber(number: String): Boolean {
    return number.matches(Regex("^\\+381\\d*$"))
}