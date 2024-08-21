package com.example.app1


import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.GeoPoint

data class Event(
    @DocumentId val id: String = "",
    val userId: String = "",
    val eventName: String,
    val eventType: String,
    val description: String? = null, // Nullable, as it won't be used for Natural Disasters
    val crowdLevel: Int? = null, // Nullable, as it won't be used for Natural Disasters
    val mainImage: String? = null, // Nullable, as the image might not be selected


val galleryImages: List<String> = emptyList(),
    val location: GeoPoint = GeoPoint(0.0, 0.0)
)