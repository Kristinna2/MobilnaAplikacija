package com.example.app1

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class Marker(
    val userId: String = "",
    val eventName: String = "",
    val eventType: String = "",
    val description: String = "",
    val crowd: Int = 0,
    val mainImage: String = "",
    val galleryImages: List<String> = emptyList(),
    val location: GeoPoint = GeoPoint(0.0, 0.0)
)

class MarkerViewModel(private val context: Context) : ViewModel() {
    private val _markers = MutableStateFlow<List<Marker>>(emptyList())
    val markers: StateFlow<List<Marker>> = _markers

    //dodajem ove dve
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

    fun getUserNameById(userId: String, onResult: (String?) -> Unit) {
        firestore.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val firstName = document.getString("firstName") ?: ""
                    val lastName = document.getString("lastName") ?: ""
                    onResult("$firstName $lastName") // Vraća puno ime
                } else {
                    Log.d("MarkerViewModel", "No such document")
                    onResult(null) // Nema korisnika
                }
            }
            .addOnFailureListener { e ->
                Log.e("MarkerViewModel", "Error getting user: ", e)
                onResult(null) // Greška u preuzimanju
            }
    }

    fun getUserIdByName(firstName: String, lastName: String, onResult: (String?) -> Unit) {
        firestore.collection("users")
            .whereEqualTo("firstName", firstName)
            .whereEqualTo("lastName", lastName)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val document = querySnapshot.documents[0]
                    val userId = document.id // Vraća ID dokumenta koji predstavlja userId
                    onResult(userId)
                } else {
                    Log.d("MarkerViewModel", "No such user")
                    onResult(null) // Nema korisnika sa tim imenom i prezimenom
                }
            }
            .addOnFailureListener { e ->
                Log.e("MarkerViewModel", "Error getting user: ", e)
                onResult(null) // Greška u preuzimanju
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

    fun filterMarkers(category: String,eventName: String, crowdLevel: Int) {
        val filtered = _markers.value?.filter { marker ->


            // Proveravamo da li marker odgovara kategoriji
            val matchesCategory = (category != "Select Category" && marker.eventType == category)
            // Proveravamo da li marker odgovara imenu događaja
            val matchesEventName = (eventName != "Select Event Name" && marker.eventName == eventName)
            // Proveravamo da li marker odgovara nivou gužve
            val matchesCrowdLevel = (crowdLevel != 0 && marker.crowd == crowdLevel)

            // Na osnovu uslova se koristi logički "I"
            matchesCategory || matchesEventName || matchesCrowdLevel  // Svi uslovi se povezuju sa logičkim "ILI"
        } ?: emptyList()

        // Postavljamo filtrirane markere
        _filteredMarkers.value = filtered
        _isFilterApplied.value = true // Obeležavamo da je filter primenjen

        Log.d("MarkerViewModel", "Filtered markers: ${filtered.joinToString { it.eventName }}")
    }


    fun resetFilter() {
        _isFilterApplied.value = false // Resetovanje filtera
        _filteredMarkers.value = emptyList() // Resetovanje filtriranih markera
    }



    override fun onCleared() {
        super.onCleared()
        markerListenerRegistration?.remove()
    }
}