package com.example.app1.views
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app1.Event
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class User(
    val id:String,
    val firstName: String = "",
    val lastName: String = "",
    val phoneNumber: String = "",
    val photoUrl: String? = null,
    val points: Int

)

class UsersViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()


    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users

    private val _userEvents = MutableStateFlow<Map<String, List<Event>>>(emptyMap())
    val userEvents: StateFlow<Map<String, List<Event>>> = _userEvents



    init {
        fetchUsers()

    }

    private fun fetchUsers() {
        viewModelScope.launch {
            firestore.collection("users").get().addOnSuccessListener { result ->
                val userList = result.map { document ->
                    User(
                        id=document.id,
                        firstName = document.getString("firstName") ?: "",
                        lastName = document.getString("lastName") ?: "",
                        phoneNumber = document.getString("phoneNumber") ?: "",
                        photoUrl = document.getString("photoUrl"),
                        points= document.getLong("points")?.toInt()?:0

                    )
                }
                _users.value = userList
            }
        }
    }



}
