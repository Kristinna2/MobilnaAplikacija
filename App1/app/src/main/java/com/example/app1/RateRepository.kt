package com.example.app1


import android.util.Log
import com.example.app1.Event
import com.example.app1.Resource

interface RateRepository {
    suspend fun getEventsRates(
        eid: String
    ): Resource<List<Rate>>
    suspend fun getUserRates(): Resource<List<Rate>>
    suspend fun getUserAdForEvent(): Resource<List<Rate>>
    suspend fun addRate(
        lid: String,
        rate: Int,
        event: Event
    ): Resource<String>

    suspend fun updateRate(
        rid: String,
        rate: Int,
    ): Resource<String>
    suspend fun recalculateAverageRate(lid: String): Resource<Double>

}