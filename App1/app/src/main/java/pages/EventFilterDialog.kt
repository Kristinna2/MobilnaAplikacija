package pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import com.example.app1.EventViewModel
import com.example.app1.MarkerViewModel
import com.example.app1.Resource
import com.example.app1.User
import com.example.app1.UsersViewModel


@Composable
fun EventFilterDialog(
    onDismiss: () -> Unit,
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
                    eventsState.forEach { event:Event ->
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
                    val crowdLevelText = if (selectedCrowdLevel != null) {
                        selectedCrowdLevel.toString() // Prikazivanje samo odabranog nivoa gužve
                    } else {
                        "Select Crowd Level" // Prikazivanje teksta ako nivo nije odabran
                    }

                    Text(crowdLevelText)
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
            }
        },
        confirmButton = {
            TextButton(onClick = {
                markerViewModel.filterMarkers(Category,ChooseEventName,selectedCrowdLevel)
                onDismiss() // Zatvori dijalog nakon filtriranja
            }) {
                Text("Filter")
            }
        }
    )
}
