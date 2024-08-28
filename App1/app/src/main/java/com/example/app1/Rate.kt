package com.example.app1


import com.google.firebase.firestore.DocumentId

data class Rate (
    @DocumentId
    val id: String = "",
    val userId: String = "",
    val eventId: String = "",
    var rate: Int = 0
)