package com.example.alumnimanagementsystemapp.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.alumnimanagementsystemapp.AuthState
import com.example.alumnimanagementsystemapp.AuthViewModel
import com.example.alumnimanagementsystemapp.R
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun HomePage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {

    val authState = authViewModel.authState.observeAsState()

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Unauthenticated -> navController.navigate("login")
            else -> Unit
        }
    }

    val backgroundImage: Painter =
        painterResource(id = R.drawable.img3) // Replace with your image resource

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Background image
        Image(
            painter = backgroundImage,
            contentDescription = "Background Image",
            modifier = Modifier
                .fillMaxSize()
        )

        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Home Page", fontSize = 32.sp, color = Color.Black)

            // Log out button that logs the user out from Firebase and navigates to login
            TextButton(onClick = {
                authViewModel.signout() // Call to sign out from Firebase
                Firebase.auth.signOut() // Logs the user out from Firebase
                navController.navigate("login") { // Navigate back to login screen
                    popUpTo("home") { inclusive = true } // Clear home from the stack
                }
            }) {
                Text(text = "Log out", color = Color.Black)
            }

        }
    }
}