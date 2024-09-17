package com.example.alumnimanagementsystemapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
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




@Composable
fun MainScreen (){

    var currentRoute by remember {
        mutableStateOf("home")
    }

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

    Scaffold (
        bottomBar = {
            BottomNavigationBar(items= items,currentScreen = currentRoute ) {
                currentRoute = it
            }
        }
    ){ paddingValues ->

        when (currentRoute){
            "home" -> ScreenOne(paddingValues, Color.Red)
            "task" -> ScreenOne(paddingValues, Color.Green)
            "notification" -> ScreenOne(paddingValues, Color.Blue)
            "profile" -> ScreenOne(paddingValues, Color.Yellow)
        }
    }

}

@Composable
fun BottomNavigationBar(
    items : List<BottomNavigationItem> = listOf(),
    currentScreen : String,
    onItemClick : (String) -> Unit

){
    NavigationBar {
        items.forEach{ item ->
            NavigationBarItem(
                selected = currentScreen == item.route,
                onClick = { onItemClick(item.route) },
                label = {Text(text = item.title)},
                alwaysShowLabel = currentScreen == item.route,
                icon = { Icon(imageVector = item.icon, contentDescription = "") })
        }
    }
}

data class BottomNavigationItem(
    val title : String,
    val route : String,
    val icon : ImageVector
)

@Composable
fun ScreenOne(paddingValues: PaddingValues,
              color: Color
              ){
    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(color = color)
    ){

    }
}


@Preview(showBackground = true)
@Composable
fun MainScreenPreview(){
    MainScreen()
}