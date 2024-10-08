package com.example.app1


import android.os.Build
import androidx.annotation.RequiresApi
import pages.AllEventsPage
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.app1.views.AuthViewModel
import pages.DetailsPage
import pages.EventDetailsPage
import pages.HomePage
import pages.LocationServicePage
import pages.LoginPage
import pages.RegisterPage
import pages.UserProfilePage
import pages.UsersPage

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MyAppNavigation(modifier: Modifier = Modifier,authViewModel: AuthViewModel) {
    val navController = rememberNavController()

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
        composable("allevents") {
            AllEventsPage(navController = navController)
        }



    })

}