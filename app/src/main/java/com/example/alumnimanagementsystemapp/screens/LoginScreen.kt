package com.example.alumnimanagementsystemapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.alumnimanagementsystemapp.AuthState
import com.example.alumnimanagementsystemapp.AuthViewModel
import com.example.alumnimanagementsystemapp.R
import kotlinx.coroutines.delay

@Composable
fun LoginScreen(navController: NavController, authViewModel: AuthViewModel) {

    val authState = authViewModel.authState.observeAsState()

    LaunchedEffect(authState.value) {
        if (authState.value is AuthState.Authenticated) {
            // Delay for 3 seconds before navigating to HomePage
            delay(3000)
            navController.navigate("mainScreen") {
                popUpTo("loginScreen") { inclusive = true }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.login_img), // Replace with your image resource
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Loading Indicator and Text
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(bottom = 30.dp)
                .background(Color.Transparent),
            verticalArrangement = Arrangement.Bottom, // Aligns content to the bottom
            horizontalAlignment = Alignment.CenterHorizontally // Centers content horizontally
        ) {
            // Spacer to push the content to the bottom
            Spacer(modifier = Modifier.height(100.dp))

            Text(text = "Logging in...", fontSize = 20.sp, color = Color.Red)

            Spacer(modifier = Modifier.height(16.dp))

            CircularProgressIndicator(
                color = Color.Red,
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.height(8.dp)) // Optional: adds spacing between text and spinner
        }
    }
}