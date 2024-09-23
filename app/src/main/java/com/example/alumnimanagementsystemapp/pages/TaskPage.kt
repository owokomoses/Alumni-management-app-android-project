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


@Composable
fun TaskPage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {






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
                Text(text = "Task Page", fontSize = 32.sp, color = Color.Black)


            }

            // Bottom Navigation Bar with larger icons


        }
    }
}


