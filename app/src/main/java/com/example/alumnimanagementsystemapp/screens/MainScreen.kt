package com.example.alumnimanagementsystemapp.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview




@Composable
fun MainScreen (){

    Scaffold (
        bottomBar = {
            
        }
    ){ paddingValues ->
        ScreenOne(paddingValues)

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
fun ScreenOne(paddingValues: PaddingValues){

}


@Preview(showBackground = true)
@Composable
fun MainScreenPreview(){
    MainScreen()
}