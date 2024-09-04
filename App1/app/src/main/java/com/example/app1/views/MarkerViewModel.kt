package com.example.app1.views

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.example.app1.Event
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

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

    private val _markers = MutableStateFlow<List<Marker>>(emptyList())
    val markers: StateFlow<List<Marker>> = _markers

    private val _filteredMarkers = MutableLiveData<List<Marker>>()
    val filteredMarkers: LiveData<List<Marker>> get() = _filteredMarkers

    private val _isFilterApplied = MutableLiveData<Boolean>(false)
    val isFilterApplied: LiveData<Boolean> get() = _isFilterApplied

    private val firestore = FirebaseFirestore.getInstance()
    private var markerListenerRegistration: ListenerRegistration? = null

    init {
        loadMarkers()
    }

    fun addMarker(latitude: Double, longitude: Double, name: String) {
        val newEvent = Marker()
        firestore.collection("events")
            .add(newEvent)
            .addOnSuccessListener {
                Log.d("HomeViewModel", "Event added successfully")
            }
            .addOnFailureListener { e ->
                Log.e("HomeViewModel", "Error adding event: ", e)
            }
    }



    private var listenerRegistration: ListenerRegistration? = null



    private fun loadMarkers() {
        listenerRegistration = firestore.collection("events")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w("MarkerViewModel", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    val markerList = snapshots.mapNotNull { doc ->
                        val marker = doc.toObject(Marker::class.java).copy(id = doc.id)

                        if (marker.eventName.isNotEmpty() && marker.location != GeoPoint(0.0, 0.0)) {
                            marker
                        } else {
                            null
                        }
                    }
                    _markers.value = markerList
                }
            }
    }

fun getUserIdByNameFromFirestore(firstName: String, lastName: String, onResult: (String?) -> Unit) {

    firestore.collection("users")
        .whereEqualTo("firstName", firstName)
        .whereEqualTo("lastName", lastName)
        .get()
        .addOnSuccessListener { documents ->
            if (documents != null && !documents.isEmpty) {
                val userId = documents.documents[0].id
                onResult(userId)
            } else {
                onResult(null)
            }
        }
        .addOnFailureListener { exception ->
            onResult(null)
        }
}

    fun filterMarkers(category: String,eventName: String, crowdLevel: Int, radius: Float, centerPoint: GeoPoint) {
        val radiusInMeters = radius * 1000

        val filtered = _markers.value?.filter { marker ->

            val markerLocation = marker.location
            val distance = calculateDistance(centerPoint.latitude, centerPoint.longitude, markerLocation.latitude, markerLocation.longitude)

            val matchesCategory = (category != "Select Category" && marker.eventType == category)
            val matchesEventName = (eventName != "Select Event Name" && marker.eventName == eventName)
            val matchesCrowdLevel = (crowdLevel != 0 && marker.crowdLevel == crowdLevel)
            Log.d("MarkerViewModel","crowd:${marker.crowdLevel}")

            val withinRadius = distance <= radiusInMeters



            matchesCategory || matchesEventName || matchesCrowdLevel|| withinRadius
        } ?: emptyList()

        _filteredMarkers.value = filtered
        _isFilterApplied.value = true

        Log.d("MarkerViewModel", "Filtered markers: ${filtered.joinToString { it.eventName }}")
    }

    fun filterMarkersByUserName(firstName: String, lastName: String, onResult: (List<Marker>) -> Unit) {
        getUserIdByNameFromFirestore(firstName, lastName) { userId ->
            Log.d("FilterMarkers", "Dobijeni userId: $userId")

            if (userId != null) {
                val filteredMarkers = _markers.value?.filter { marker ->
                    marker.userId == userId
                } ?: emptyList()

                onResult(filteredMarkers)
                _filteredMarkers.value = filteredMarkers

                _isFilterApplied.value = true
            } else {
                onResult(emptyList())
            }
        }
    }


    fun resetFilter() {
        _isFilterApplied.value = false
        _filteredMarkers.value = emptyList()
    }



    override fun onCleared() {
        super.onCleared()
        markerListenerRegistration?.remove()
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadius = 6371000.0

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadius * c
    }


}