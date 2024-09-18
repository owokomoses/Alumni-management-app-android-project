package com.example.alumnimanagementsystemapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.example.alumnimanagementsystemapp.R
import kotlinx.coroutines.delay

@Composable
fun WelcomeScreen(navController: NavController) {
    // Delay for 3 seconds
    LaunchedEffect(Unit) {
        delay(3000L) // Wait for 3 seconds
        // Check authentication state and navigate accordingly
        navController.navigate("login") {
            popUpTo("welcome") { inclusive = true }
        }
    }

    // Display the image in the center
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.welcome), // Replace with your image resource
            contentDescription = "Welcome Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}
