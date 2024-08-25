package com.example.app1

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class Marker(
    @DocumentId val id: String = "",
    val userId: String = "",
    val eventName: String = "",
    val eventType: String = "",
    val description: String = "",
    val crowdLevel: Int = 0,
    val mainImage: String = "",
    val galleryImages: List<String> = emptyList(),
    val location: GeoPoint = GeoPoint(0.0, 0.0)
)

class MarkerViewModel(private val context: Context) : ViewModel() {

    private val eventViewModel: EventViewModel by lazy {
        ViewModelProvider(
            (context as ViewModelStoreOwner)
        ).get(EventViewModel::class.java)
    }

    private val _markers = MutableStateFlow<List<Marker>>(emptyList())
    val markers: StateFlow<List<Marker>> = _markers

    //dodajem ove dve
    private val _filteredMarkerss = MutableLiveData<List<Event>>() // Lista filtriranih markera

    private val _filteredMarkers = MutableLiveData<List<Marker>>() // Lista filtriranih markera
    val filteredMarkers: LiveData<List<Marker>> get() = _filteredMarkers
//dodato
    private val _isFilterApplied = MutableLiveData<Boolean>(false) // Stanje da li je filter primenjen
    val isFilterApplied: LiveData<Boolean> get() = _isFilterApplied

    private val firestore = FirebaseFirestore.getInstance()
    private var markerListenerRegistration: ListenerRegistration? = null

    init {
        loadMarkers()
    }

    fun addMarker(latitude: Double, longitude: Double, name: String) {
        val newLandmark = Marker()
        firestore.collection("events")
            .add(newLandmark)
            .addOnSuccessListener {
                Log.d("HomeViewModel", "Event added successfully")
            }
            .addOnFailureListener { e ->
                Log.e("HomeViewModel", "Error adding event: ", e)
            }
    }



    private var listenerRegistration: ListenerRegistration? = null


    //druga zimena
    private fun loadMarkers() {
        listenerRegistration = firestore.collection("events")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w("MarkerViewModel", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    val markerList = snapshots.mapNotNull { doc ->
                        // Pretvori dokument u Marker klasu
                        val marker = doc.toObject(Marker::class.java).copy(userId = doc.id)

                        // Proveri da li marker ima važne vrednosti, kao što su eventName i location
                        if (marker.eventName.isNotEmpty() && marker.location != GeoPoint(0.0, 0.0)) {
                            marker
                        } else {
                            null // Ako su vrednosti prazne, vrati null
                        }
                    }
                    _markers.value = markerList
                }
            }
    }
//i ovo
fun getUserIdByNameFromFirestore(firstName: String, lastName: String, onResult: (String?) -> Unit) {
 //   val firestore = FirebaseFirestore.getInstance()

    firestore.collection("users")
        .whereEqualTo("firstName", firstName)
        .whereEqualTo("lastName", lastName)
        .get()
        .addOnSuccessListener { documents ->
            if (documents != null && !documents.isEmpty) {
                // Pretpostavljamo da je ime i prezime jedinstveno, pa uzimamo prvi dokument
                val userId = documents.documents[0].id
                onResult(userId)
            } else {
                // Korisnik nije pronađen
                onResult(null)
            }
        }
        .addOnFailureListener { exception ->
            // U slučaju greške, vratiti null
            onResult(null)
        }
}

    fun filterMarkers(category: String,eventName: String, crowdLevel: Int) {
        val filtered = _markers.value?.filter { marker ->

            // Proveravamo da li marker odgovara kategoriji
            val matchesCategory = (category != "Select Category" && marker.eventType == category)
            // Proveravamo da li marker odgovara imenu događaja
            val matchesEventName = (eventName != "Select Event Name" && marker.eventName == eventName)
            // Proveravamo da li marker odgovara nivou gužve
            val matchesCrowdLevel = (crowdLevel != 0 && marker.crowdLevel == crowdLevel)
            Log.d("MarkerViewModel","crowd:${marker.crowdLevel}")




            // Na osnovu uslova se koristi logički "I"
            matchesCategory || matchesEventName || matchesCrowdLevel  // Svi uslovi se povezuju sa logičkim "ILI"
        } ?: emptyList()

        // Postavljamo filtrirane markere
        _filteredMarkers.value = filtered
        _isFilterApplied.value = true // Obeležavamo da je filter primenjen

        Log.d("MarkerViewModel", "Filtered markers: ${filtered.joinToString { it.eventName }}")
    }

    fun filterMarkersByUserName(firstName: String, lastName: String, onResult: (List<Marker>) -> Unit) {
        getUserIdByNameFromFirestore(firstName, lastName) { userId ->
            Log.d("MarkerViewModel", "Dobijeni userId: $userId")

            if (userId != null) {

                eventViewModel.filterEventsByUserId(userId) { filteredEvents ->
                    Log.d("MarkerViewModel", "Filtrirani događaji: $filteredEvents")
                    val markers = convertFilteredEventsToMarkers(filteredEvents)

                    _filteredMarkers.value = markers //ali filtered events su tipa marker
                    _isFilterApplied.value = true // Obeležavamo da je filter primenjen

                    // Ako trebaš da vratiš filtrirane markere, možeš to dodati ovde
                    onResult(emptyList()) // Ili neki drugi rezultat po tvom izboru
                }
            } else {
                // Ako korisnik nije pronađen, vraćamo praznu listu
                onResult(emptyList())
            }
        }
    }


    fun resetFilter() {
        _isFilterApplied.value = false // Resetovanje filtera
        _filteredMarkers.value = emptyList() // Resetovanje filtriranih markera
    }



    override fun onCleared() {
        super.onCleared()
        markerListenerRegistration?.remove()
    }

    private fun convertFilteredEventsToMarkers(events: List<Event>): List<Marker> {
        return events.map { event ->
            Marker(
                id = event.id,
                userId = event.userId,
                eventName = event.eventName,
                eventType = event.eventType,
                description = event.description!!,
                crowdLevel = event.crowdLevel!!,
                mainImage = event.mainImage!!,
                galleryImages = event.galleryImages,
                location = event.location
            )
        }
    }

}