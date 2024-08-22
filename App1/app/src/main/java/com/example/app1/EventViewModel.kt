package com.example.app1


import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EventViewModel: ViewModel() {
    val repository = EventRepositoryImplementation()
    var eventData = mutableStateOf<Event?>(null)

    fun setEventData(event: Event) {
        eventData.value = event
    }
    private val _eventFlow = MutableStateFlow<Resource<String>?>(null)
    val eventFlow: StateFlow<Resource<String>?> = _eventFlow

   // private val _newRate = MutableStateFlow<Resource<String>?>(null)
   // val newRate: StateFlow<Resource<String>?> = _newRate

    private val _events = MutableStateFlow<Resource<List<Event>>>(Resource.Success(emptyList()))
    val events: StateFlow<Resource<List<Event>>> get() = _events

   // private val _rates = MutableStateFlow<Resource<List<Rate>>>(Resource.Success(emptyList()))
  //  val rates: StateFlow<Resource<List<Rate>>> get() = _rates


    private val _userEvents = MutableStateFlow<Resource<List<Event>>>(Resource.Success(emptyList()))
    val userEvents: StateFlow<Resource<List<Event>>> get() = _userEvents

    init {
        getAllEvents()
    }

    fun getAllEvents() = viewModelScope.launch {
        _events.value = repository.getAllEvents()
    }

    fun saveEventData(
        description: String,
        crowd: Int,
        mainImage: Uri,
        eventName: String,
        eventType: String,
        galleryImages: List<Uri>,
        location: LatLng?
    ) = viewModelScope.launch{
        _eventFlow.value = Resource.Loading
        repository.saveEventData(
            description = description,
            crowd = crowd,
            mainImage = mainImage,
            eventName=eventName,
            eventType=eventType,
            galleryImages = galleryImages,
           location = location!!
        )
        _eventFlow.value = Resource.Success("Uspešno dodat dogadjaj")
    }


    /*fun getBeachAllRates(
        bid: String
    ) = viewModelScope.launch {
        _rates.value = Resource.loading
        val result = rateRepository.getBeachRates(bid)
        _rates.value = result
    }

    fun addRate(
        bid: String,
        rate: Int,
        beach: Beach
    ) = viewModelScope.launch {
        _newRate.value = rateRepository.addRate(bid, rate, beach)
    }

    fun updateRate(
        rid: String,
        rate: Int
    ) = viewModelScope.launch{
        _newRate.value = rateRepository.updateRate(rid, rate)
    }
*/
    fun getUserEvents(
        uid: String
    ) = viewModelScope.launch {
        _userEvents.value = repository.getUserEvent(uid)
    }
}

class EventViewModelFactory:ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(EventViewModel::class.java)){
            return EventViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}