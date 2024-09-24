package com.example.alumnimanagementsystemapp.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.alumnimanagementsystemapp.AuthState
import com.example.alumnimanagementsystemapp.AuthViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun HomePage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {

    val authState = authViewModel.authState.observeAsState()


    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Unauthenticated -> navController.navigate("login")
            else -> Unit
        }
    }


    // Box for the entire screen, with a white background
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White) // Set the background color to white
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween, // Space between content and nav bar
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Content in the middle
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f) // Take up remaining space above the nav bar
            ) {
                // Home Page title
                Text(text = "Home Page", fontSize = 32.sp, color = Color.Black)

                // Log out button
                TextButton(onClick = {
                    authViewModel.signout() // Call to sign out from Firebase
                    Firebase.auth.signOut() // Logs the user out from Firebase
                    navController.navigate("login") { // Navigate back to login screen
                        popUpTo("home") { inclusive = true } // Clear home from the back stack
                    }
                }) {
                    Text(text = "Log out", color = Color.Black)
                }
            }

            // Bottom Navigation Bar with larger icons


        }
    }
}