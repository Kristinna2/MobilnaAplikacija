package com.example.app1

import android.net.Uri
import com.google.android.gms.maps.model.LatLng


interface EventRepository {

    suspend fun getAllEvents(): Resource<List<Event>>
    suspend fun saveEventData(
        description: String,
        crowd: Int,
        mainImage: Uri,
        eventName: String,
        eventType: String,
        galleryImages: List<Uri>,
       location: LatLng
    ): Resource<String>

    suspend fun getUserEvent(
        uid: String
    ): Resource<List<Event>>
}