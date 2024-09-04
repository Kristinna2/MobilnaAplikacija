package com.example.app1


import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.GeoPoint

data class Event(
    @DocumentId val id: String = "",
    val userId: String = "",
    val eventName: String,
    val eventType: String,
    val description: String? = null,
    val crowdLevel: Int? = null,
    val mainImage: String? = null,
        val galleryImages: List<String> = emptyList(),
    val location: GeoPoint = GeoPoint(0.0, 0.0)
){
    constructor() : this(
        id = "",
        userId = "",
        eventName = "",
        eventType = "",
        description = null,
        crowdLevel = null,
        mainImage = null,
        galleryImages = emptyList(),
        location = GeoPoint(0.0, 0.0)
    )
}