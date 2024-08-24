package com.example.app1

import android.content.Context
import android.util.Log
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



    override fun onCleared() {
        super.onCleared()
        markerListenerRegistration?.remove()
    }
}