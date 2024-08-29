package com.example.app1


import android.net.Uri
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID


class EventViewModel: ViewModel() {


    val repository = EventRepositoryImplementation()
    val rateRepository = RateRepositoryImpl()

    var eventData = mutableStateOf<Event?>(null)


    private val _eventFlow = MutableStateFlow<Resource<String>?>(null)
    val eventFlow: StateFlow<Resource<String>?> = _eventFlow

    private val _newRate = MutableStateFlow<Resource<String>?>(null)
    val newRate: StateFlow<Resource<String>?> = _newRate

    private val _events = MutableStateFlow<Resource<List<Event>>>(Resource.Success(emptyList()))
    val events: StateFlow<Resource<List<Event>>> get() = _events

    private val _rates = MutableStateFlow<Resource<List<Rate>>>(Resource.Success(emptyList()))
    val rates: StateFlow<Resource<List<Rate>>> get() = _rates


    private val _userEvents = MutableStateFlow<Resource<List<Event>>>(Resource.Success(emptyList()))
    val userEvents: StateFlow<Resource<List<Event>>> get() = _userEvents
    private val _filteredEvents = MutableLiveData<Resource<List<Event>>>()

    //   val filteredEvents: StateFlow<Resource<List<Event>>> get() = _filteredEvents

    private val _eventDetail = MutableStateFlow<Resource<Event>?>(null)
    val eventDetail: StateFlow<Resource<Event>?> = _eventDetail

    private val _averageRate = MutableStateFlow<Resource<Double>?>(null)
    val averageRate: StateFlow<Resource<Double>?> = _averageRate

    fun recalculateAverageRate(eventId: String) = viewModelScope.launch {
        _averageRate.value = rateRepository.recalculateAverageRate(eventId)
    }
    init {
        getAllEvents()
    }

    fun getAllEvents() = viewModelScope.launch {
        _events.value = repository.getAllEvents()
    }
   fun getEventDetail(eventId: String) = viewModelScope.launch {
        _eventDetail.value = Resource.Loading
        _eventDetail.value = repository.getEventById(eventId)

        getEventAllRates(eventId)

    }

    // Izabrani događaj
    private val _selectedEvent = mutableStateOf<Event?>(null)
    val selectedEvent: State<Event?> get() = _selectedEvent

    // Funkcija za postavljanje izabranog događaja
    fun setSelectedEvent(event: Event) {
        _selectedEvent.value = event
    }

    fun setEventData(marker: Event) {
        eventData.value = marker
    }


   /* fun saveEventData(
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
*/
   fun saveEventData(
       userId:String,
       eventType: String,
       eventName: String,
       description: String,
       crowd: Int,
       mainImage: Uri,
       galleryImages: List<Uri>,
       location: LatLng?
   ) {
       val storageRef = Firebase.storage.reference.child("images/${UUID.randomUUID()}")
       val uploadTask = storageRef.putFile(mainImage)

       uploadTask.addOnSuccessListener {
           storageRef.downloadUrl.addOnSuccessListener { uri ->
               val eventData = hashMapOf(
                   "userId" to userId,
                   "eventType" to eventType,
                   "eventName" to eventName,
                   "description" to description,
                   "crowd" to crowd,
                   "mainImage" to uri.toString(),
                   "galleryImages" to galleryImages.map { it.toString() },
                   "location" to location?.let { GeoPoint(it.latitude, it.longitude) }
               )

               // Spremi landmarkData u bazu podataka
               Firebase.firestore.collection("events")
                   .add(eventData)
                   .addOnSuccessListener {
                       // Uspješno spremljeno
                       _eventFlow.value = Resource.Success("Event successfully added!")
                   }
                   .addOnFailureListener {
                       // Greška prilikom spremanja
                       _eventFlow.value = Resource.Failure(it)
                   }
           }
       }.addOnFailureListener {
           // Greška prilikom upload-a slike
           _eventFlow.value = Resource.Failure(it)
       }
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
    fun getEventAllRates(
        bid: String
    ) = viewModelScope.launch {
        _rates.value = Resource.Loading
        val result = rateRepository.getEventsRates(bid)
        _rates.value = result
    }

    fun addRate(
        bid: String,
        rate: Int,
        event: Event
    ) = viewModelScope.launch {
        _newRate.value = rateRepository.addRate(bid, rate, event)
    }

    fun updateRate(
        rid: String,
        rate: Int
    ) = viewModelScope.launch {
        _newRate.value = rateRepository.updateRate(rid, rate)
        //  recalculateAverageRate(rid)
    }

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