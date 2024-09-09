package com.example.alumnimanagementsystemapp

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.alumnimanagementsystemapp.pages.HomePage
import com.example.alumnimanagementsystemapp.pages.LoginPage
import com.example.alumnimanagementsystemapp.pages.SignupPage
import com.example.alumnimanagementsystemapp.screens.WelcomeScreen

@Composable
fun Navigation(modifier: Modifier = Modifier,authViewModel: AuthViewModel){
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "welcome", builder = {

        composable("welcome") { WelcomeScreen(navController = navController)
        }

        composable("login"){
            LoginPage(modifier,navController,authViewModel)
        }

        composable("signup"){
            SignupPage(modifier,navController,authViewModel)
        }

        composable("home"){
            HomePage(modifier,navController,authViewModel)
        }
    })

}