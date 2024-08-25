package com.example.app1


import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
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
    private val _filteredEvents = MutableLiveData<Resource<List<Event>>>()

 //   val filteredEvents: StateFlow<Resource<List<Event>>> get() = _filteredEvents


    init {
        getAllEvents()
    }

    fun getAllEvents() = viewModelScope.launch {
        _events.value = repository.getAllEvents()
    }


    fun setEventData(marker: Event) {
        eventData.value = marker
    }


    fun saveEventData(
        description: String,
        crowd: Int,
        mainImage: Uri,
        eventName: String,
        eventType: String,
        galleryImages: List<Uri>,
        location: LatLng?
    ) = viewModelScope.launch {
        _eventFlow.value = Resource.Loading
        repository.saveEventData(
            description = description,
            crowd = crowd,
            mainImage = mainImage,
            eventName = eventName,
            eventType = eventType,
            galleryImages = galleryImages,
            location = location!!
        )
        _eventFlow.value = Resource.Success("Uspešno dodat dogadjaj")
    }


    /* fun loadEventData(markerJson: String) {
        val marker = Gson().fromJson(markerJson, Event::class.java)
        Log.d("EventViewModel", "Loaded event: $marker")
        setEventData(marker)
    }*/
    /* private val eventRepository = EventRepositoryImplementation()
    private val _eventData = MutableStateFlow<Event?>(null)

    // Metoda za učitavanje svih događaja
    suspend fun loadAllEvents() {
        when (val resource = eventRepository.getAllEvents()) {
            is Resource.Success -> {
                _eventData.value = resource.data?.firstOrNull() // Uzimamo prvi događaj kao primer
            }
            is Resource.Failure -> {
                Log.e("EventViewModel", "Greška pri učitavanju događaja: ${resource.exception}")
            }
        }
    }
*/
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

    fun filterEventsByUserId(userId: String, onResult: (List<Event>) -> Unit) = viewModelScope.launch {
        // Dohvata sve događaje
        val allEvents = when (val result = repository.getAllEvents()) {
            is Resource.Success -> result.result ?: emptyList()
            else -> emptyList()
        }

        // Filtrira događaje koji odgovaraju `userId`
        val filteredList = allEvents.filter { event: Event ->
            Log.d("EventViewModel", "event.userId: ${event.userId}, userId: $userId") // Logovanje

            event.userId == userId
        }

        // Ažurira stanje sa filtriranim događajima
        _filteredEvents.value = Resource.Success(filteredList)

        // Vraćamo filtrirane događaje
        onResult(filteredList)
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