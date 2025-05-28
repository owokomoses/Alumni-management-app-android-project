package com.example.alumnimanagementsystemapp.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.alumnimanagementsystemapp.AuthState
import com.example.alumnimanagementsystemapp.AuthViewModel
import com.example.alumnimanagementsystemapp.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun LoginScreen(navController: NavController, authViewModel: AuthViewModel) {
    var showContent by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (showContent) 1f else 0.8f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "scale"
    )
    val authState = authViewModel.authState.observeAsState()
    val auth = FirebaseAuth.getInstance()

    LaunchedEffect(Unit) {
        // Check if user is verified and automatically log in
        val currentUser = auth.currentUser
        if (currentUser?.isEmailVerified == true) {
            // Automatically log in
            authViewModel.login(currentUser.email ?: "", "")
        }
    }

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Authenticated -> {
                // Wait for 2 seconds to show the loading screen
                delay(2000)
                // Navigate to main dashboard
                navController.navigate("main") {
                    popUpTo("loginScreen") { inclusive = true }
                }
            }
            is AuthState.Error -> {
                // Handle error state if needed
            }
            else -> Unit
        }
    }

    // Background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo with animation
            Image(
                painter = painterResource(id = R.drawable.icon),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(180.dp)
                    .scale(scale)
                    .clip(RoundedCornerShape(24.dp))
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Loading content with animation
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(animationSpec = tween(1000)),
                exit = fadeOut(animationSpec = tween(1000))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Logging In",
                        fontSize = 28.sp,
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Please wait while we verify your credentials",
                        fontSize = 18.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Loading indicator
                    CircularProgressIndicator(
                        color = Color.Red,
                        modifier = Modifier.size(48.dp),
                        strokeWidth = 4.dp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "This may take a few moments...",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }

    // Launch animation
    LaunchedEffect(Unit) {
        showContent = true
    }
}