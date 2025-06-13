package com.example.alumnimanagementsystemapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.alumnimanagementsystemapp.pages.ApplicationsPage
import com.example.alumnimanagementsystemapp.pages.ForgotPasswordPage
import com.example.alumnimanagementsystemapp.pages.JobApplicationPage
import com.example.alumnimanagementsystemapp.pages.JobPostDetail
import com.example.alumnimanagementsystemapp.pages.LoginPage
import com.example.alumnimanagementsystemapp.pages.NotificationsPage
import com.example.alumnimanagementsystemapp.pages.Posts
import com.example.alumnimanagementsystemapp.pages.SignupPage
import com.example.alumnimanagementsystemapp.pages.Users
import com.example.alumnimanagementsystemapp.pages.VerificationPage
import com.example.alumnimanagementsystemapp.pages.ViewApplicationPage
import com.example.alumnimanagementsystemapp.screens.LoginScreen
import com.example.alumnimanagementsystemapp.screens.ProfileScreen
import com.example.alumnimanagementsystemapp.screens.SignupScreen
import com.example.alumnimanagementsystemapp.screens.WelcomeScreen
import kotlinx.coroutines.delay

@Composable
fun Navigation(modifier: Modifier = Modifier, authViewModel: AuthViewModel) {
    val navController = rememberNavController()
    val authState = authViewModel.authState.observeAsState()

    NavHost(navController = navController, startDestination = Screen.Welcome.route) {
        composable(Screen.Welcome.route) {
            WelcomeScreen(navController = navController)

            LaunchedEffect(Unit) {
                delay(2000) // Show welcome screen for 2 seconds
                when (authState.value) {
                    is AuthState.Authenticated -> {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Welcome.route) { inclusive = true }
                        }
                    }
                    is AuthState.Unauthenticated -> {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Welcome.route) { inclusive = true }
                        }
                    }
                    else -> {
                        // Default case: If state is unclear, stay on welcome
                    }
                }
            }
        }

        composable(Screen.Login.route) {
            LoginPage(modifier, navController, authViewModel)
        }

        composable("loginScreen") {
            LoginScreen(navController = navController, authViewModel = authViewModel)
        }

        composable(Screen.Register.route) {
            SignupPage(modifier, navController, authViewModel)
        }

        composable("signupScreen") {
            SignupScreen(navController = navController, authViewModel = authViewModel)
        }

        composable(
            route = "verificationPage/{email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            VerificationPage(modifier, navController, authViewModel, email)
        }

        composable(Screen.Home.route) {
            Screen(navController = navController, authViewModel = authViewModel) { paddingValues ->
                ScreenContent(paddingValues = paddingValues)
            }
        }

        composable(Screen.Users.route) {
            Users(modifier, navController, authViewModel)
        }

        composable(Screen.Posts.route) {
            Posts(modifier, navController, authViewModel)
        }

        composable(Screen.JobDetails.route) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId")
            if (postId != null) {
                JobPostDetail(navController = navController, postId = postId, authViewModel = authViewModel)
            }
        }

        composable(Screen.JobPostDetail.route) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId")
            if (postId != null) {
                JobPostDetail(navController = navController, postId = postId, authViewModel = authViewModel)
            }
        }

        composable(Screen.JobApplication.route) { backStackEntry ->
            val jobId = backStackEntry.arguments?.getString("jobId")
            if (jobId != null) {
                JobApplicationPage(navController = navController, jobId = jobId, authViewModel = authViewModel)
            }
        }

        composable(Screen.Applications.route) {
            ApplicationsPage(navController = navController, authViewModel = authViewModel)
        }

        composable(Screen.Profile.route) {
            ProfileScreen(navController = navController, authViewModel = authViewModel)
        }

        composable(Screen.Notifications.route) {
            NotificationsPage(navController = navController, authViewModel = authViewModel)
        }

        composable("forgotPasswordPage") {
            ForgotPasswordPage(modifier, navController, authViewModel)
        }

        composable(Screen.ViewApplication.route) { backStackEntry ->
            val applicationId = backStackEntry.arguments?.getString("applicationId")
            if (applicationId != null) {
                ViewApplicationPage(navController = navController, applicationId = applicationId, authViewModel = authViewModel)
            }
        }
    }

    // Handle authentication state changes
    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Authenticated -> {
                // If we're not already on the main screen, navigate there
                if (navController.currentDestination?.route != Screen.Home.route) {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
            is AuthState.Unauthenticated -> {
                // Only navigate to login if we're not already there and not on welcome screen
                // and not on verification page
                val currentRoute = navController.currentDestination?.route
                if (currentRoute != Screen.Login.route && 
                    currentRoute != "loginScreen" &&
                    currentRoute != Screen.Welcome.route &&
                    currentRoute != "verificationPage/{email}" &&
                    currentRoute != Screen.Register.route) {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
            is AuthState.VerificationEmailSent -> {
                // Don't do anything here, let the SignupPage handle the navigation
            }
            else -> {
                // Handle other states if needed
            }
        }
    }
}   