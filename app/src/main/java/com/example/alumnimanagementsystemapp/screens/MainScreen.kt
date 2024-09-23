package com.example.alumnimanagementsystemapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.alumnimanagementsystemapp.AuthViewModel
import com.example.alumnimanagementsystemapp.pages.HomePage
import com.example.alumnimanagementsystemapp.pages.NotificationPage
import com.example.alumnimanagementsystemapp.pages.ProfilePage
import com.example.alumnimanagementsystemapp.pages.TaskPage


@Composable
fun MainScreen(navController: NavHostController, authViewModel: AuthViewModel) {
    val navController = rememberNavController()

    // Single route to manage navigation
    var currentRoute by remember {
        mutableStateOf("home")
    }

    // Define the bottom navigation items
    val items = listOf(
        BottomNavigationItem(
            "Home",
            "home",
            Icons.Filled.Home
        ),
        BottomNavigationItem(
            "Task",
            "task",
            Icons.Filled.Star
        ),
        BottomNavigationItem(
            "Notification",
            "notification",
            Icons.Filled.Notifications
        ),
        BottomNavigationItem(
            "Profile",
            "profile",
            Icons.Filled.AccountCircle
        ),
    )

    Scaffold(
        bottomBar = {
            BottomNavigationBar(items = items, currentScreen = currentRoute,
                onItemClick = { route ->
                    if (currentRoute != route) {
                        currentRoute = route
                        navController.navigate(route) {
                            popUpTo(0) { inclusive = false }  // Use popUpTo(0) instead
                            launchSingleTop = true
                        }
                    }
                }
            )
        }
    ) { paddingValues ->

        // Use NavHost to manage screen transitions inside Scaffold
        NavHost(
            navController = navController,
            startDestination = "home",  // Initial screen
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("home") {
                HomePage(
                    navController = navController,
                    authViewModel = authViewModel
                )
            }
            composable("task") {
                TaskPage(
                    navController = navController,
                    authViewModel = authViewModel
                )
            }
            composable("notification") {
                NotificationPage(
                    navController = navController,
                    authViewModel = authViewModel
                )
            }
            composable("profile") {
                ProfilePage(
                    navController = navController,
                    authViewModel = authViewModel
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    items: List<BottomNavigationItem>,
    currentScreen: String,
    onItemClick: (String) -> Unit
) {
    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentScreen == item.route,
                onClick = { onItemClick(item.route) },
                label = { Text(text = item.title) },
                alwaysShowLabel = currentScreen == item.route,
                icon = { Icon(imageVector = item.icon, contentDescription = item.title) }
            )
        }
    }
}

data class BottomNavigationItem(
    val title: String,
    val route: String,
    val icon: ImageVector
)

@Composable
fun ScreenOne(paddingValues: PaddingValues, color: Color){
    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(color = color)
            .padding(paddingValues)
    ){

    }
}


//@Preview(showBackground = true)
//@Composable
//fun MainScreenPreview(){
//    MainScreen(authViewModel = AuthViewModel())
//}