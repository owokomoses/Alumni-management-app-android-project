package com.example.alumnimanagementsystemapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.alumnimanagementsystemapp.pages.HomePage
import com.example.alumnimanagementsystemapp.pages.LoginPage
import com.example.alumnimanagementsystemapp.pages.SignupPage
import com.example.alumnimanagementsystemapp.screens.LoginScreen
import com.example.alumnimanagementsystemapp.screens.WelcomeScreen

@Composable
fun Navigation(modifier: Modifier = Modifier,authViewModel: AuthViewModel){
    val navController = rememberNavController()
    val authState = authViewModel.authState.observeAsState()

    // Check the user's authentication state at the start
    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Authenticated -> {
                // Navigate to Home if already authenticated
                navController.navigate("home") {
                    popUpTo("welcome") { inclusive = true }
                }
            }
            is AuthState.Unauthenticated -> {
                // If unauthenticated, navigate to loginScreen
                navController.navigate("loginScreen") {
                    popUpTo("welcome") { inclusive = true }
                }
            }
            else -> {
                // Show Welcome Screen by default
                navController.navigate("welcome") {
                    popUpTo("welcome") { inclusive = true }
                }
            }
        }
    }

    NavHost(navController = navController, startDestination = "welcome", builder = {

        composable("welcome") { WelcomeScreen(navController = navController)
        }

        composable("login"){
            LoginPage(modifier,navController,authViewModel)
        }

        composable("loginScreen") {
            LoginScreen(navController = navController, authViewModel = authViewModel)
        }

        composable("signup"){
            SignupPage(modifier,navController,authViewModel)
        }

        composable("home"){
            HomePage(modifier,navController,authViewModel)
        }
    })

}