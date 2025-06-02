package com.example.alumnimanagementsystemapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.alumnimanagementsystemapp.pages.ForgotPasswordPage
import com.example.alumnimanagementsystemapp.pages.LoginPage
import com.example.alumnimanagementsystemapp.pages.NotificationPage
import com.example.alumnimanagementsystemapp.pages.Posts
import com.example.alumnimanagementsystemapp.pages.SignupPage
import com.example.alumnimanagementsystemapp.pages.Users
import com.example.alumnimanagementsystemapp.pages.VerificationPage
import com.example.alumnimanagementsystemapp.screens.LoginScreen
import com.example.alumnimanagementsystemapp.screens.ProfileScreen
import com.example.alumnimanagementsystemapp.screens.SignupScreen
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
                delay(2000) // Show welcome screen for 2 seconds
                when (authState.value) {
                    is AuthState.Authenticated -> {
                        navController.navigate("main") {
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
        }

        composable("loginScreen") {
            LoginScreen(navController = navController, authViewModel = authViewModel)
        }

        composable("signupScreen") {
            SignupScreen(navController = navController, authViewModel = authViewModel)
        }

        composable("main") {
            Screen(navController = navController, authViewModel = authViewModel) { paddingValues ->
                ScreenContent(paddingValues = paddingValues)
            }
        }

        composable("signup") {
            SignupPage(modifier, navController, authViewModel)
        }

        composable("users") {
            Users(modifier, navController, authViewModel)
        }

        composable("posts") {
            Posts(modifier, navController, authViewModel)
        }

        composable("job_post_detail/{postId}") { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId")
            if (postId != null) {
                JobPostDetail(navController = navController, postId = postId, authViewModel = authViewModel)
            }
        }

        composable("profile") {
            ProfileScreen(navController = navController, authViewModel = authViewModel)
        }

        composable("verificationPage") {
            VerificationPage(modifier, navController, authViewModel)
        }

        composable("forgotPasswordPage") {
            ForgotPasswordPage(modifier, navController, authViewModel)
        }
    }

    // Handle authentication state changes
    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Authenticated -> {
                // If we're not already on the main screen, navigate there
                if (navController.currentDestination?.route != "main") {
                    navController.navigate("main") {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }
            }
            is AuthState.Unauthenticated -> {
                // Only navigate to login if we're not already there and not on welcome screen
                if (navController.currentDestination?.route != "login" && 
                    navController.currentDestination?.route != "welcome") {
                    navController.navigate("login") {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
            else -> {
                // Handle other states if needed
            }
        }
    }
}   