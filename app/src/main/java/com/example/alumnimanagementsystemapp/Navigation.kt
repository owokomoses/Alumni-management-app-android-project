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
import kotlinx.coroutines.delay

@Composable
fun Navigation(modifier: Modifier = Modifier, authViewModel: AuthViewModel) {
    val navController = rememberNavController()
    val authState = authViewModel.authState.observeAsState()

    NavHost(navController = navController, startDestination = "welcome") {
        composable("welcome") {
            WelcomeScreen(navController = navController)

            LaunchedEffect(Unit) {
                delay(3000)
                when (authState.value) {
                    is AuthState.Authenticated -> {
                        navController.navigate("home") {
                            popUpTo("welcome") { inclusive = true }
                        }
                    }
                    is AuthState.Unauthenticated -> {
                        navController.navigate("login") {
                            popUpTo("welcome") { inclusive = true }
                        }
                    }
                    else -> {
                        // Default case: If state is unclear, stay on welcome
                    }
                }
            }
        }

        composable("login") {
            LoginPage(modifier, navController, authViewModel)

            LaunchedEffect(authState.value) {
                if (authState.value is AuthState.Authenticated) {
                    navController.navigate("loginScreen") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            }
        }

        composable("loginScreen") {
            LoginScreen(navController = navController, authViewModel = authViewModel)

            LaunchedEffect(authState.value) {
                if (authState.value is AuthState.Authenticated) {
                    // Delay for 3 seconds before navigating to HomePage
                    delay(3000)
                    navController.navigate("home") {
                        popUpTo("loginScreen") { inclusive = true }
                    }
                }
            }
        }

        composable("signup") {
            SignupPage(modifier, navController, authViewModel)
        }

        composable("home") {
            HomePage(modifier, navController, authViewModel)
        }
    }
}