package com.example.app1



import com.example.aquaspot.model.service.StorageService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class EventRepositoryImplementation : EventRepository {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestoreInstance = FirebaseFirestore.getInstance()
    private val storageInstance = FirebaseStorage.getInstance()

   private val databaseService = DatabaseService(firestoreInstance)
    private val storageService = StorageService(storageInstance)


    override suspend fun getAllEvents(): Resource<List<Event>> {
        return try{
            val snapshot = firestoreInstance.collection("beaches").get().await()
            val beaches = snapshot.toObjects(Event::class.java)
            Resource.Success(beaches)
        }catch (e: Exception){
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override suspend fun saveEventData(
        description: String,
        crowd: Int,
      //  mainImage: Uri,
        eventName: String,
        eventType: String,
      //  galleryImages: List<Uri>,
      //  location: LatLng
    ): Resource<String> {
        return try{
            val currentUser = firebaseAuth.currentUser
            if(currentUser!=null){
              // val mainImageUrl = storageService.uploadEventMainImage(mainImage)
             //   val galleryImagesUrls = storageService.uploadEventGalleryImages(galleryImages)
              //  val geoLocation = GeoPoint(
               //     location.latitude,
              //      location.longitude
              //  )
                val event = Event(
                    userId = currentUser.uid,
                    description = description,
                    crowdLevel = crowd,
                    eventName = eventName,
                    eventType = eventType,
                   // mainImage = mainImageUrl,
                 //   galleryImages = galleryImagesUrls,
                  //  location = geoLocation
                )
                databaseService.saveEventData(event)
               // databaseService.addPoints(currentUser.uid, 5)
            }
            Resource.Success("Uspesno sačuvani svi podaci o plaži")
        }catch (e: Exception){
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override suspend fun getUserEvent(uid: String): Resource<List<Event>> {
        return try {
            val snapshot = firestoreInstance.collection("events")
                .whereEqualTo("userId", uid)
                .get()
                .await()
            val beaches = snapshot.toObjects(Event::class.java)
            Resource.Success(beaches)
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }
}