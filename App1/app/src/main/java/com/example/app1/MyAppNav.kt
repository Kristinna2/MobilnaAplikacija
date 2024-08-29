package com.example.app1


import pages.AllEventsPage
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import pages.AllEventsPage
import pages.DetailsPage
import pages.EventDetailsPage
import pages.HomePage
import pages.LocationServicePage
import pages.LoginPage
import pages.RegisterPage
import pages.UserProfilePage
import pages.UsersPage

@Composable
fun MyAppNavigation(modifier: Modifier = Modifier,authViewModel: AuthViewModel) {
    val navController = rememberNavController()
    //val location = remember { mutableStateOf<LatLng?>(null) }  // Koristimo remember za pamćenje lokacije
   // val homeViewModel: HomeViewModel = viewModel()
    NavHost(navController = navController, startDestination = "login", builder = {
        composable("login"){
            LoginPage(modifier,navController,authViewModel)
        }
        composable("signup"){
            RegisterPage(modifier,navController,authViewModel)
        }
        composable("home"){
            HomePage(modifier,navController,authViewModel)
        }
        composable("user_profile/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            UserProfilePage(modifier,navController,authViewModel, userId)
        }
        composable("all_users") {
            UsersPage(navController=navController)
        }
        composable("location_service") {
            LocationServicePage(modifier,navController) // Stranica za uslugu lokacije
        }
        composable("event_details") {
            EventDetailsPage(navController = navController)
        }
        composable("details") {
           DetailsPage(navController = navController)
        }
        composable("allevents") { // Dodaj novu rutu za `AllEventsPage`
            AllEventsPage(navController = navController)
        }



    })

}