package pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.app1.Event
import com.example.app1.views.EventViewModel
import com.example.app1.views.MarkerViewModel
import com.example.app1.Resource
import com.example.app1.views.User
import com.example.app1.views.UsersViewModel
import com.google.firebase.firestore.GeoPoint


@Composable
fun EventFilterDialog(
    onDismiss: () -> Unit,
    centerPoint: GeoPoint, // Added center point parameter

    eventViewModel: EventViewModel = viewModel(),
    usersViewModel: UsersViewModel = viewModel() // Dodajemo ViewModel kao parametar
) {
    var isCategoryDropdownExpanded by remember { mutableStateOf(false) }
    var isUserDropdownExpanded by remember { mutableStateOf(false) }
    var isEventNameDropdownExpanded by remember { mutableStateOf(false) }
    var ChooseUser by remember { mutableStateOf<User?>(null) }
    var ChooseEventName by remember { mutableStateOf("Select Event Name") }
    val usersState by usersViewModel.users.collectAsState()
    var isCrowdLevelDropdownExpanded by remember { mutableStateOf(false) }
    var selectedCrowdLevel by remember { mutableStateOf(0) }
    var Category by remember { mutableStateOf("Select Category") }
    var radius by remember { mutableStateOf(1f) } // Initialize with a default value



    // Prikupite podatke o događajima
    val eventsResource by eventViewModel.events.collectAsState()
    val eventsState = when (eventsResource) {
        is Resource.Success -> (eventsResource as Resource.Success<List<Event>>).result // Uzimamo listu događaja
        is Resource.Failure -> emptyList() // Ili neka druga logika za greške
        is Resource.Loading -> emptyList() // U slučaju učitavanja
    }
   val markerViewModel: MarkerViewModel = viewModel()


    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                text = "Select Event Details",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                // Padajući meni za kategorije
                TextButton(onClick = { isCategoryDropdownExpanded = !isCategoryDropdownExpanded }) {
                    Text(Category)
                }

                DropdownMenu(
                    expanded = isCategoryDropdownExpanded,
                    onDismissRequest = { isCategoryDropdownExpanded = false }
                ) {
                    DropdownMenuItem(
                        onClick = {
                            Category = "Concert"
                            isCategoryDropdownExpanded = false
                        },
                        text = { Text("Concert") }
                    )
                    DropdownMenuItem(
                        onClick = {
                            Category = "Sports Event"
                            isCategoryDropdownExpanded = false
                        },
                        text = { Text("Sports Event") }
                    )
                    DropdownMenuItem(
                        onClick = {
                            Category = "Manifestation"
                            isCategoryDropdownExpanded = false
                        },
                        text = { Text("Manifestation") }
                    )
                    DropdownMenuItem(
                        onClick = {
                            Category = "Natural Disaster"
                            isCategoryDropdownExpanded = false
                        },
                        text = { Text("Natural Disaster") }
                    )
                }

                // Spacer između menija
                Spacer(modifier = Modifier.height(16.dp))

                // Padajući meni za naziv događaja
                TextButton(onClick = { isEventNameDropdownExpanded = !isEventNameDropdownExpanded }) {
                    Text(ChooseEventName)
                }

                DropdownMenu(
                    expanded = isEventNameDropdownExpanded,
                    onDismissRequest = { isEventNameDropdownExpanded = false }
                ) {
                    eventsState.filter { it.eventName.isNotEmpty() }.forEach { event: Event ->
                        DropdownMenuItem(
                            onClick = {
                                ChooseEventName = event.eventName // Pretpostavljamo da event ima naziv
                                isEventNameDropdownExpanded = false
                            },
                            text = { Text(event.eventName) }
                        )
                    }
                }
                // Spacer između menija i dugmeta za odabir korisnika
                Spacer(modifier = Modifier.height(16.dp))

                // Padajući meni za korisnike
                TextButton(onClick = { isUserDropdownExpanded = !isUserDropdownExpanded }) {
                    Text(ChooseUser?.let { "${it.firstName} ${it.lastName}" } ?: "Select User")
                }

                DropdownMenu(
                    expanded = isUserDropdownExpanded,
                    onDismissRequest = { isUserDropdownExpanded = false }
                ) {
                    usersState.forEach { user ->
                        DropdownMenuItem(
                            onClick = {
                                ChooseUser = user
                                isUserDropdownExpanded = false
                            },
                            text = { Text("${user.firstName} ${user.lastName}") }
                        )
                    }
                }

                // Spacer između menija i dugmeta za odabir crowd levela
                Spacer(modifier = Modifier.height(16.dp))

                // Padajući meni za crowd level (1 do 5)


                TextButton(onClick = { isCrowdLevelDropdownExpanded = !isCrowdLevelDropdownExpanded }) {
                    Text("Crowd Level: $selectedCrowdLevel")
                }

                DropdownMenu(
                    expanded = isCrowdLevelDropdownExpanded,
                    onDismissRequest = { isCrowdLevelDropdownExpanded = false }
                ) {
                    (1..5).forEach { level ->
                        DropdownMenuItem(
                            onClick = {
                                selectedCrowdLevel = level
                                isCrowdLevelDropdownExpanded = false
                            },
                            text = { Text("$level") }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Slider for radius
                Text(text = "Radius: ${radius.toInt()} km")
                Slider(
                    value = radius,
                    onValueChange = { newRadius -> radius = newRadius },
                    valueRange = 1f..50f, // Example range for radius
                    steps = 49 // Number of steps between min and max value
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
               // val selectedUserName = ChooseUser?.let { "${it.firstName} ${it.lastName}" } ?: "No User Selected"
              //  Log.d("EventFilterDialog", "Selected User: ${ChooseUser?.firstName!!}")

            if(ChooseUser!=null){
                markerViewModel.filterMarkersByUserName(ChooseUser!!.firstName, ChooseUser!!.lastName) { filteredMarkers ->
                }
            }
            else {
                markerViewModel.filterMarkers(Category, ChooseEventName, selectedCrowdLevel,radius, centerPoint)

            }
                onDismiss() // Zatvori dijalog nakon filtriranja
            }) {
                Text("Filter")
            }
        }
    )
}
